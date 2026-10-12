# Early Potion System（早期药水系统）

> 一个为 Minecraft 1.20.1 打造的**早期游戏药水酿造**模组。新增自定义「早期炼药台」，让玩家仅用胡萝卜、牛肉、苹果、鲑鱼、鸡肉等**无需下界**的简单材料，即可在游戏前期获得药水增益。

![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-green)
![Forge](https://img.shields.io/badge/Forge-47.2.1-orange)
![License](https://img.shields.io/badge/License-MIT-blue)
![Version](https://img.shields.io/badge/version-1.1.1--1.20.1-brightgreen)

---

## 目录

- [项目简介](#项目简介)
- [功能特性](#功能特性)
- [环境依赖](#环境依赖)
- [本地部署步骤](#本地部署步骤)
- [核心模块架构](#核心模块架构)
- [物品一览](#物品一览)
- [酿造配方](#酿造配方)
- [合成配方](#合成配方)
- [API / 扩展点说明](#api--扩展点说明)
- [使用示例](#使用示例)
- [JEI 集成](#jei-集成)
- [构建与测试](#构建与测试)
- [常见问题](#常见问题)
- [许可证](#许可证)

---

## 项目简介

| 项 | 值 |
| --- | --- |
| 模组 ID | `earlypotions` |
| 显示名称 | Early Potion System |
| 版本 | `1.1.1-1.20.1` |
| Minecraft | `1.20.1` |
| Forge | `47.2.1`（`loaderVersion` 要求 `[47,)`） |
| 许可证 | MIT |
| 作者 | mjr666rt, OtherNameHere |
| 源码包 | `com.besson.tutorial` |
| 当前物品数 | 21 个注册物品 + 1 个方块 |

核心理念：**让药水增益不再依赖下界疣与烈焰粉**。玩家在游戏早期即可通过食物材料酿造 11 种药水，并进一步合成功能道具、炼金套装与隐藏彩蛋物品，形成从「采集 → 酿造 → 装备 → 探索」的完整体验闭环。

---

## 功能特性

- **早期炼药台**：自定义方块 + 方块实体 + 图形界面，5 槽位容器（0–2 输出瓶、3 材料、4 燃料）。
- **11 种可饮用药水**：5 种基础药水 + 6 种扩展药水，全部使用原版饮用动画与效果提示。
- **功能道具**：药水核心（合成材料）、炼金净化剂（清除负面效果）。
- **炼金术士套装**：头盔 / 胸甲 / 护腿 / 战靴，**穿戴即持续生效**对应增益。
- **炼金之刃**：命中附加虚弱与发光，自带附魔光泽。
- **隐藏彩蛋**：炼金术士的秘酿，随机强力增益 + 隐藏揭示文本。
- **JEI 支持（可选）**：安装 JEI 时展示全部酿造配方；未安装也能正常游戏。
- **创造模式标签页**：所有物品集中收纳于「早期药水系统」标签页。

---

## 环境依赖

### 运行游戏（作为玩家）

| 依赖 | 版本 | 必需 |
| --- | --- | --- |
| Minecraft | 1.20.1 | 是 |
| Minecraft Forge | 47.2.1（≥ 47.0） | 是 |
| JEI（Just Enough Items） | 15.x（1.20.1 版本） | 否（可选） |

> 将构建产出的 `earlypotions-1.1.1-1.20.1.jar` 放入 `.minecraft/mods/` 即可。

### 本地开发（作为开发者）

| 依赖 | 版本 |
| --- | --- |
| JDK | 17（`java.toolchain.languageVersion = 17`） |
| Gradle | 8.x（使用随项目提供的 `gradlew` / `gradlew.bat`，无需本地安装） |
| ForgeGradle | 6.x（`[6.0,6.2)`） |
| 映射 | Parchment `2023.09.03-1.20.1` |
| IDE | IntelliJ IDEA 或 Eclipse（可选，用于调试） |

---

## 本地部署步骤

### 1. 克隆仓库

```bash
git clone https://github.com/mjr-surtr/mcmod.git
cd mcmod
```

### 2. 首次初始化工作区（下载并反编译 Minecraft，耗时较长）

```bash
# Windows
gradlew.bat build
# macOS / Linux
./gradlew build
```

### 3. 生成 IDE 运行配置（可选）

```bash
# IntelliJ IDEA
gradlew.bat genIntellijRuns
# Eclipse
gradlew.bat genEclipseRuns
```

### 4. 运行 / 调试

```bash
# 启动客户端（含模组）
gradlew.bat runClient
# 启动服务端
gradlew.bat runServer
# 运行 GameTest 服务端
gradlew.bat runGameTestServer
```

### 5. 构建发布产物

```bash
gradlew.bat build
```

产物路径：`build/libs/earlypotions-1.1.1-1.20.1.jar`

> 提示：`gradlew.bat clean` 可清理构建缓存（不影响源码）；`gradlew.bat --refresh-dependencies` 可刷新依赖。

---

## 核心模块架构

```
src/main/java/com/besson/tutorial/
├── TutorialMod.java                  # 模组入口：注册 items / tabs / blocks，客户端注册 GUI
│
├── ModEvents.java                    # 通用事件：为炼金套装提供穿戴增益（PlayerTick）
│
├── block/                            # 早期炼药台（方块侧）
│   ├── ModBlocks.java                # 方块 / 方块实体 / 菜单类型注册
│   ├── EarlyBrewingStandBlock.java   # 方块行为
│   ├── EarlyBrewingStandBlockEntity.java # 酿造核心逻辑 + CUSTOM_RECIPES 配方表
│   ├── EarlyBrewingStandMenu.java    # 容器菜单（5 槽位 + 进度数据）
│   └── EarlyBrewingStandScreen.java  # 客户端界面绘制
│
├── item/                             # 物品侧
│   ├── Moditems.java                 # 全部物品注册 + potionProperties 工厂
│   ├── ModCreativeModeTabs.java      # 创造模式标签页与物品排序
│   ├── EarlyPotionItem.java          # 可饮用药水基类（DRINK 动画 + 效果提示）
│   ├── LoreItem.java                 # 通用「仅包含说明文本」的物品
│   ├── AlchemistArmorItem.java       # 炼金盔甲（自定义盔甲层贴图 + 增益字段）
│   ├── AlchemistBladeItem.java       # 炼金之刃（命中附加效果）
│   ├── AlchemistCleanserItem.java    # 炼金净化剂（清除负面效果）
│   ├── AlchemistSecretBrewItem.java  # 彩蛋：炼金术士的秘酿
│   ├── ModArmorMaterials.java        # 炼金盔甲材质
│   └── ModTiers.java                 # 炼金之刃工具材质
│
└── compat/                           # JEI 兼容层（可选依赖）
    ├── EarlyBrewingJeiPlugin.java    # JEI 插件入口：注册类别/配方/催化物
    ├── EarlyBrewingRecipe.java       # JEI 配方数据模型
    └── EarlyBrewingRecipeCategory.java # JEI 配方类别渲染
```

### 架构要点

1. **注册集中化**：所有 `DeferredRegister` 在各自的 `Mod*` 类中声明，由 `TutorialMod` 构造器统一挂载到模组事件总线。
2. **数据驱动资源**：物品模型、贴图、配方、本地化均由 `src/main/resources` 下的 JSON/PNG 提供，与代码解耦。
3. **零侵入扩展**：新增物品仅通过复用 `EarlyPotionItem` + `CUSTOM_RECIPES` 表即可接入酿造系统。
4. **可选兼容**：JEI 相关代码位于 `compat` 包，采用 `compileOnly` 依赖，运行时不存在 JEI 也不会报错。

---

## 物品一览

### 药水（可饮用，均持续 600 tick = 30 秒）

| ID | 名称 | 效果 |
| --- | --- | --- |
| `early_night_vision_potion` | 早期夜视药水 | 夜视 |
| `early_healing_potion` | 早期生命药水 | 生命恢复 |
| `early_water_breathing_potion` | 早期水肺药水 | 水下呼吸 |
| `early_strength_potion` | 早期力量药水 | 力量 |
| `early_mining_potion` | 早期挖掘提升药水 | 急迫 |
| `early_swiftness_potion` | 早期迅捷药水 | 速度提升 |
| `early_leaping_potion` | 早期跳跃药水 | 跳跃提升 |
| `early_slow_falling_potion` | 早期缓降药水 | 缓降 |
| `early_invisibility_potion` | 早期隐身药水 | 隐身 |
| `early_glowing_potion` | 早期发光药水 | 发光 |
| `early_luck_potion` | 早期幸运药水 | 幸运 |
| `early_tab_icon` | 早期全能药水 | 同时获得 5 种基础效果（标签页图标） |

### 功能道具

| ID | 名称 | 作用 |
| --- | --- | --- |
| `potion_core` | 药水核心 | 炼金套装与炼金之刃的核心合成材料 |
| `alchemist_cleanser` | 炼金净化剂 | 饮用后清除全部负面效果 |

### 炼金术士套装（穿戴即生效）

| ID | 名称 | 穿戴增益 | 防御力 | 耐久 |
| --- | --- | --- | --- | --- |
| `alchemist_helmet` | 炼金护目镜 | 夜视 | 2 | 198 |
| `alchemist_chestplate` | 炼金胸甲 | 抗性提升 | 6 | 288 |
| `alchemist_leggings` | 炼金护腿 | 速度提升 | 5 | 270 |
| `alchemist_boots` | 炼金战靴 | 跳跃提升 | 2 | 234 |
| `alchemist_blade` | 炼金之刃 | 命中附加虚弱（120t）+ 发光（200t） | 7 伤害 / 1.6 攻速 | 1000 |

> 材质参数：耐久倍率 18、附魔值 22、韧性 0.5、击退抗性 0、修复材料为药水核心。炼金之刃工具等级为钻石级（`level 3`）。

### 隐藏彩蛋

| ID | 名称 | 效果 |
| --- | --- | --- |
| `alchemist_secret_brew` | 炼金术士的秘酿 | 随机 3 种 II 级增益（各 900t）+ 幸运（1800t）+ 隐藏揭示文本 |

---

## 酿造配方

在**早期炼药台**中，将下列材料放入第 4 号槽位（材料槽），并在 0–2 号槽位放入**玻璃瓶或水瓶**即可酿造。

| 材料 | 产出药水 |
| --- | --- |
| 胡萝卜 `minecraft:carrot` | 早期夜视药水 |
| 牛肉 `minecraft:beef` | 早期力量药水 |
| 苹果 `minecraft:apple` | 早期生命药水 |
| 鲑鱼 `minecraft:salmon` | 早期水肺药水 |
| 鸡肉 `minecraft:chicken` | 早期挖掘提升药水 |
| 糖 `minecraft:sugar` | 早期迅捷药水 |
| 黏液球 `minecraft:slime_ball` | 早期跳跃药水 |
| 羽毛 `minecraft:feather` | 早期缓降药水 |
| 墨囊 `minecraft:ink_sac` | 早期隐身药水 |
| 发光浆果 `minecraft:glow_berries` | 早期发光药水 |
| 兔子脚 `minecraft:rabbit_foot` | 早期幸运药水 |

**炼药台运行参数**

| 参数 | 值 |
| --- | --- |
| 容器槽位 | 5（0–2 药水瓶，3 材料，4 燃料） |
| 单次酿造时间 | 400 tick（20 秒，与原版一致） |
| 燃料 | 煤炭 / 木炭，每个提供 20 次酿造 |
| 自动化 | 实现 `WorldlyContainer`，支持漏斗（下/上/侧向槽位有所不同） |

---

## 合成配方

| 产物 | 配方 | 产出 |
| --- | --- | --- |
| 药水核心 | 4 紫水晶碎片 + 1 玻璃瓶（十字形） | ×2 |
| 炼金净化剂 | 药水核心 + 牛奶桶（无序） | ×2 |
| 炼金护目镜 | 药水核心 ×5（头盔形） | ×1 |
| 炼金胸甲 | 药水核心 ×8（胸甲形） | ×1 |
| 炼金护腿 | 药水核心 ×7（护腿形） | ×1 |
| 炼金战靴 | 药水核心 ×4（靴子形） | ×1 |
| 炼金之刃 | 药水核心 ×2 + 木棍 | ×1 |
| 早期全能药水 | 5 款基础药水（无序） | ×1 |
| 早期炼药台 | 8 圆石 + 玻璃瓶 + 熔炉 | ×1 |
| 炼金术士的秘酿 | 药水核心 ×4 + 早期全能药水 | ×1 |

---

## API / 扩展点说明

> 以下为面向二次开发的说明。「API」在此指模组内部可供扩展的接口与约定。

### 1. 注册新物品

在 `Moditems` 中按 Forge 规范注册。可饮用药水建议复用工厂方法：

```java
// 添加一款自定义药水（效果：抗性提升，600 tick，I 级）
public static final RegistryObject<Item> EARLY_RESISTANCE_POTION =
        ITEMS.register("early_resistance_potion", () -> new EarlyPotionItem(
                potionProperties(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 0))));
```

`potionProperties(...)` 统一设置：`stacksTo(1)`、零饱食度、`alwaysEat()`、效果触发概率 `1.0f`。

### 2. 扩展酿造配方（`EarlyBrewingStandBlockEntity`）

酿造配方以「材料 → 产出物品」的映射形式集中维护于静态表 `CUSTOM_RECIPES`：

```java
static {
    CUSTOM_RECIPES.put(Items.SUGAR, Moditems.EARLY_SWIFTNESS_POTION.get());
    // 新增：金胡萝卜 → 自定义药水
    // CUSTOM_RECIPES.put(Items.GOLDEN_CARROT, Moditems.EARLY_XXX_POTION.get());
}
```

> 说明：命中 `CUSTOM_RECIPES` 的材料会走**自定义逻辑**（消耗水瓶/玻璃瓶产出药水）；未命中的材料回落到原版 `PotionBrewing` 机制。

### 3. 装备增益（`AlchemistArmorItem` + `ModEvents`）

`AlchemistArmorItem` 通过构造参数声明「穿戴增益效果 + 等级」，由 `ModEvents#onPlayerTick` 每 20 tick 检查并续期：

```java
new AlchemistArmorItem(ModArmorMaterials.ALCHEMIST, ArmorItem.Type.HELMET,
        MobEffects.NIGHT_VISION, 0, new Item.Properties());
//        ↑ 材质            ↑ 部位             ↑ 增益效果          ↑ 等级
```

续期规则（`ModEvents#applyArmorEffect`）：当效果不存在、剩余时长 < 200 tick 或等级低于装备要求时，补充 300 tick 的增益。

### 4. 自定义盔甲 / 工具材质

- 盔甲材质实现 `ArmorMaterial`（参考 `ModArmorMaterials`）。
- 工具材质实现 `Tier`（参考 `ModTiers`）。
- 两者修复材料均指向 `Moditems.POTION_CORE`。

### 5. 带说明文本的物品

`LoreItem` 接受一个翻译键，在物品提示中追加灰色斜体说明：

```java
new LoreItem(new Item.Properties(), "item.earlypotions.potion_core.lore")
```

---

## 使用示例

### 游玩流程（玩家视角）

1. **合成炼药台**：8 圆石 + 玻璃瓶 + 熔炉。
2. **准备材料与燃料**：如胡萝卜、煤炭、玻璃瓶。
3. **放置并打开界面**：把玻璃瓶放入输出槽（0–2），材料放入材料槽（3），燃料放入燃料槽（4）。
4. **等待 20 秒**：酿造完成，取出药水，右键饮用即获得 30 秒对应增益。
5. **进阶**：用紫水晶碎片 + 玻璃瓶合成**药水核心**，进而打造炼金套装与炼金之刃；探索**炼金术士的秘酿**的合成路径。

### 命令示例（调试）

```mcfunction
# 直接获取新增物品
/give @s earlypotions:early_swiftness_potion
/give @s earlypotions:alchemist_helmet
/give @s earlypotions:alchemist_blade
/give @s earlypotions:alchemist_secret_brew

# 获取炼药台
/give @s earlypotions:early_brewing_stand

# 清除全部效果（对比炼金净化剂）
/effect clear @s
```

---

## JEI 集成

- 安装 JEI（15.x，1.20.1）后，「早期炼药台」页面会展示全部酿造配方，并标注配方催化物（炼药台方块）。
- JEI 通过 `@JeiPlugin`（`EarlyBrewingJeiPlugin`）以**软依赖**方式接入：构建时使用 `compileOnly`，未安装 JEI 时模组照常运行。
- 语言键：`jei.earlypotions.early_brewing`（类别名「早期酿造」）。

---

## 构建与测试

```bash
# 完整构建（编译 + 打包 + 重混淆）
gradlew.bat build

# 服务端冒烟测试
gradlew.bat runServer
```

**验证要点**

- 构建成功，产出 `build/libs/earlypotions-1.1.1-1.20.1.jar`（约 81 KB，115 个条目）。
- 服务端日志应出现模组版本识别与 `Done` 完成提示，且无异常堆栈。
- jar 内应包含全部新增类与资源（`ModEvents`、`ModTiers`、`ModArmorMaterials`、`Alchemist*Item`、模型 / 贴图 / 配方 / 语言文件）。

> 注：日志中的 `Loaded N recipes` 反映的是**配方类型数量**（原版为 7 种），并非配方条目总数，属正常现象。

---

## 常见问题

**Q：可以不安装 JEI 吗？**
A：可以。JEI 为可选软依赖，仅影响配方界面提示。

**Q：模组会破坏原版存档吗？**
A：不会。模组不修改任何原版行为，所有新增内容通过标准注册机制接入，卸载模组后存档仍可加载（仅丢失本模组方块/物品）。

**Q：为什么药水不需要下界疣？**
A：这是本模组的设计核心——使用食物类材料即可酿造，方便前期过渡。

**Q：炼金护甲/武器坏了怎么办？**
A：在铁砧中使用**药水核心**修复。

**Q：秘酿怎么合成？**
A：属于隐藏彩蛋，提示为「配方早已失传……唯有真正的炼金术士才能复原」。可参考上文的合成配方表。

---

## 许可证

本项目基于 **MIT License** 发布，详见 [LICENSE.txt](LICENSE.txt)。
