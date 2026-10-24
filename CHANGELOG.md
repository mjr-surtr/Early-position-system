# 更新日志（Changelog）

本文件记录 **Early Potion System（早期药水系统）** 的所有重要变更。

- 版本号遵循 [语义化版本 SemVer](https://semver.org/lang/zh-CN/)：`主版本.次版本.修订号-MC版本`。
- 变更分类参考 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)。
- 每个版本对应的 Git 提交可在仓库提交历史中检索。

---

## [1.1.4-1.20.1] - 2026-10-24

> 主题：贴图全面重制与第二套盔甲「炼金宗师」。

### 新增 Added

#### 炼金宗师套装（进阶战斗向）
- 新增 4 件装备：`master_alchemist_helmet` 炼金宗师头冠、`master_alchemist_chestplate` 炼金宗师胸甲、`master_alchemist_leggings` 炼金宗师护腿、`master_alchemist_boots` 炼金宗师战靴。
- 新增材质 `ModArmorMaterials.ALCHEMIST_MASTER`：耐久倍率 24、防御力 `{靴 3, 腿 6, 胸 8, 头 3}`、附魔值 25、韧性 1.0、击退抗性 0.1、穿脱音效 `ARMOR_EQUIP_NETHERITE`，修复材料为药水核心。
- 升级配方：对应第一套盔甲 + 药水核心 + 金锭（无序合成，4 个配方）。
- 穿戴增益：头冠防火 I、胸甲抗性提升 II、护腿速度提升 I、战靴力量 II。
- 配套 32×32 物品图标、64×32 专属盔甲层、物品模型、中英文名称与 lore。
- 创造模式标签页按「旧套装 → 新套装」顺序收录。

### 变更 Changed
- **贴图全面重绘**：14 张药水/效果图标、两套共 10 张装备图标、2 张炼药台方块贴图统一为更清晰的 32×32 像素风，强化轮廓、明暗层次、玻璃高光与宝石点缀。
- **盔甲层重制**：`alchemist_layer_1/2.png` 由纯色占位改为带金属分片、金边与宝石的分部位绘制。
- `AlchemistArmorItem` 新增 `textureName` 构造参数，按套装选择盔甲层贴图与 lore 翻译键。
- `ModEvents` 改为按槽位读取装备自身声明的效果，支持多套盔甲并存，不再硬编码槽位与效果的对应关系。
- `PotionsV2` 生成器扩展为全量贴图生成（物品图标、盔甲层、方块贴图）。

### 测试 Test
- `./gradlew build` 通过，产出 `earlypotions-1.1.4-1.20.1.jar`（129 个条目）。
- 全部资源 JSON 校验通过，新增模型、配方、贴图与语言键均已打入产物。

---

## [1.1.2-1.20.1] - 2026-10-14

> 主题：视觉资源重制。**无行为变更、无新增物品、无数值调整**，仅重绘贴图与调整模型渲染参数。

### 优化 Changed

#### 物品贴图重绘（20 张，统一 32×32 像素风）
- **14 张药瓶与效果图标**（夜视 / 生命 / 水肺 / 力量 / 挖掘 / 迅捷 / 跳跃 / 缓降 / 隐身 / 发光 / 幸运 / 净化 / 秘酿 / 标签图标）：统一玻璃瓶轮廓、瓶塞、液面高光与底部阴影，各效果以独立配色与徽记区分，在物品栏缩放后仍可辨识。
- **5 张装备图标**（护目镜 / 胸甲 / 护腿 / 战靴 / 炼金之刃）：统一紫水晶与暖金属配色，强化镜片反光、金属包边与剑刃明暗层次。
- **1 张药水核心**：补充水晶切面、金色外框与高光。
- **1 张方块贴图**（早期炼药台）：重绘为带紫晶台面与金属框架的配色方案，与物品图标风格统一。
- 全部贴图保持透明背景与原有资源路径，仅内容变更，不涉及引用改名。

#### 模型渲染参数
- 全部物品模型补充 `"gui_light": "front"`，修正物品栏与手持状态下的正面光照，避免贴图发暗。
- 药水类物品模型移除自定义 `display` 变换，回归 `minecraft:item/generated` 原版默认摆放；炼金之刃沿用 `item/handheld`。
- 早期炼药台方块模型补充 `"ambientocclusion": true` 与 `"gui_light": "side"`，并同步精简几何体、重算 UV，使其与新贴图对齐。
- 早期炼药台的**物品模型**新增完整 `display` 变换（gui / ground / fixed / 第一、第三人称），修正方块物品在物品栏与手中的摆放角度与缩放。

### 测试 Test
- `./gradlew build` 通过，产出 `earlypotions-1.1.2-1.20.1.jar`。
- 全部 21 张变更 PNG 校验：32×32、32 位 RGBA，可正常解码且保留透明通道。
- 全部物品/方块模型 JSON 语法校验通过，贴图与父模型引用有效。

---

## [1.1.1-1.20.1] - 2026-10-12

> 主题：源码清理。**无行为变更、无新增内容、无数值调整**，仅移除一处已弃用 API 调用。

### 修复 Fixed
- `EarlyPotionItem#appendHoverText` 改用 `ItemStack#getFoodProperties(LivingEntity)`（Forge 推荐的 ItemStack 敏感版本），替换已弃用的 `Item#getFoodProperties()`。
  - Forge 在源码中给出的迁移指引为 `Use IForgeItem#getFoodProperties(ItemStack, LivingEntity) in favour of this.`
  - 行为完全一致：Forge 该方法的默认实现即回落到原字段，且本项目未覆写 ItemStack 变体；原版自身也以 `stack.getFoodProperties(null)` 方式调用。

### 变更 Changed
- 全量 `-Xlint:deprecation` 编译告警由 **6 条降至 5 条**。
- 剩余 5 条位于 `EarlyBrewingStandBlock`，均为原版对**调用方**的弃用（`getShape` / `getRenderShape` / `use` / `onRemove`）。Mojang 在 Javadoc 中明确注明 `Implementing/overriding is fine.`，这是 1.20.1 方块覆写的标准写法，**保留不动**。

### 测试 Test
- `./gradlew build` 通过，产出 `earlypotions-1.1.1-1.20.1.jar`。
- 全量重编译（清空 `build/classes/java/main`）确认告警数量与预期一致，0 错误。

---

## [1.1.0-1.20.1] - 2026-10-10

> 主题：物品体系扩充与长期可玩性提升。在完全兼容原有药水系统的前提下，新增消耗品、功能道具、装备与隐藏彩蛋，并优化交互与数值平衡。

### 新增 Added

#### 消耗品：6 款新增早期药水（各 600 tick / I 级）
| 物品 ID | 名称 | 效果 | 酿造材料 |
| --- | --- | --- | --- |
| `early_swiftness_potion` | 早期迅捷药水 | 移动速度提升 | 糖 `minecraft:sugar` |
| `early_leaping_potion` | 早期跳跃药水 | 跳跃提升 | 黏液球 `minecraft:slime_ball` |
| `early_slow_falling_potion` | 早期缓降药水 | 缓降 | 羽毛 `minecraft:feather` |
| `early_invisibility_potion` | 早期隐身药水 | 隐身 | 墨囊 `minecraft:ink_sac` |
| `early_glowing_potion` | 早期发光药水 | 发光 | 发光浆果 `minecraft:glow_berries` |
| `early_luck_potion` | 早期幸运药水 | 幸运 | 兔子脚 `minecraft:rabbit_foot` |

> 选取标准：均为**早期可获得的简单材料**，延续「无需下界」的设计初衷。全部复用 `EarlyPotionItem` 基类与现有炼药台 `CUSTOM_RECIPES` 机制，零侵入。

#### 功能道具
- `potion_core` **药水核心**：炼金套装与炼金之刃的核心合成材料，携带 lore 提示文本（`LoreItem`）。
- `alchemist_cleanser` **炼金净化剂**：饮下后清除全部 `MobEffectCategory.HARMFUL`（负面）效果，继承 `EarlyPotionItem`。

#### 装备：炼金术士套装（穿戴即生效）
| 物品 ID | 名称 | 穿戴增益 |
| --- | --- | --- |
| `alchemist_helmet` | 炼金护目镜 | 夜视 |
| `alchemist_chestplate` | 炼金胸甲 | 抗性提升 |
| `alchemist_leggings` | 炼金护腿 | 移动速度提升 |
| `alchemist_boots` | 炼金战靴 | 跳跃提升 |

- 新增 `ModArmorMaterials.ALCHEMIST` 盔甲材质：耐久倍率 18，防御力 `{靴 2, 腿 5, 胸 6, 头 2}`，附魔值 22，穿脱音效 `ARMOR_EQUIP_GOLD`，韧性 0.5，击退抗性 0，修复材料为药水核心。
- 新增 `AlchemistArmorItem`，通过覆写 `IForgeItem#getArmorTexture` 指定自定义盔甲层贴图（`alchemist_layer_1/2.png`）。

- `alchemist_blade` **炼金之刃**：命中目标时附加**虚弱**（120 tick）与**发光**（200 tick），自带附魔光泽（`isFoil`）。
  - 新增 `ModTiers.ALCHEMIST` 工具材质：等级 3、耐久 1000、挖掘速度 6.0、攻击伤害加成 3.0、附魔值 22，用药水核心修复。
  - 数值等价于钻石剑的伤害/攻速（7 伤害 / 1.6 攻速），但拥有更高耐久与独特命中效果。

#### 隐藏彩蛋
- `alchemist_secret_brew` **炼金术士的秘酿**：饮用后从 10 种增益池中**随机获得 3 种 II 级增益**（各 900 tick），并额外获得幸运（1800 tick）；同时向玩家发送隐藏揭示文本。
  - 增益池：力量、速度、再生、抗性提升、防火、伤害吸收、跳跃提升、夜视、急迫、水下呼吸。
  - 自带附魔光泽，无 JEI/配方书直接指引，需玩家自行探索合成路径。

#### 配套内容
- **图标贴图**：新增 16 张 PNG（13 张 32×32 物品图标 + 2 张盔甲层 + 1 张药水核心图标等）。
- **物品模型**：新增 14 个 `item/generated`（炼金之刃为 `item/handheld`）模型 JSON。
- **合成配方**：新增 8 个配方 JSON（药水核心、炼金净化剂、四件盔甲、炼金之刃、秘酿）。
- **本地化**：`zh_cn.json` / `en_us.json` 补齐全部新增物品名称、lore 与彩蛋揭示文本。
- **创造模式标签**：所有新物品按「药水 → 功能道具 → 装备 → 图标/彩蛋」顺序并入 `early` 标签页。

#### 事件系统
- 新增双端事件处理器 `ModEvents`，通过 `TickEvent.PlayerTickEvent` 为炼金套装提供穿戴增益。

### 变更 Changed
- `Moditems` 重构注册结构，新增 `potionProperties(...)` 工厂方法统一「可饮用药水」属性构建，降低重复代码。
- `EarlyBrewingStandBlockEntity.CUSTOM_RECIPES` 由 5 条扩展至 11 条。
- `EarlyBrewingJeiPlugin` 同步新增 6 条 JEI 配方展示，保证 JEI 提示与实际酿造结果一致。
- `ModCreativeModeTabs` 重新编排标签页物品顺序。
- 版本号 `1.0.2-1.20.1` → `1.1.0-1.20.1`（`gradle.properties`）。

### 优化 / 平衡 Balance
- **穿戴增益刷新策略**：炼金套装的增益由「每刻写入」改为「每 20 tick 检查、仅当效果缺失 / 剩余不足 200 tick / 等级不足时续期」，避免效果栏刷屏与无意义的包同步，同时防止与药水效果互相覆盖导致数值溢出。
- **合成经济性**：`potion_core` 配方产出由 4 下调为 2，避免因核心材料过于廉价而破坏装备获取节奏。
- **设计中立性**：全部新增物品均只依赖原版机制与既有系统（`DeferredRegister` / 酿造台 / 创造标签 / JEI `compileOnly`），不修改任何原版行为，卸载模组不影响存档。

### 修复 Fixed
- 澄清构建日志中 `Loaded 7 recipes` 的含义：该数值为**配方类型（RecipeType）数量**而非配方条目总数，经反编译源码核对（`RecipeManager#apply` 中 `map` 以 `RecipeType` 为键），新增配方实际全部正常加载，非缺陷。

### 测试 Test
- `./gradlew build` 编译通过，产出 `earlypotions-1.1.0-1.20.1.jar`。
- `./gradlew runServer` 服务端冒烟测试通过：`Done`，模组版本识别为 `1.1.0-1.20.1`，无异常堆栈。
- jar 产物校验：115 个条目，全部新增类（`ModEvents` / `ModTiers` / `ModArmorMaterials` / `Alchemist*Item` / `LoreItem`）与资源（模型、贴图、配方、语言文件）齐全。

---

## [1.0.2-1.20.1] - 2026-06-22

### 变更 Changed
- 模组标识由 `tutorial_mod` 迁移至 `earlypotions`，包结构与资源命名空间统一（对应提交 `d84f0e3`）。
- 重命名主模组类与注册表命名空间，资源目录调整为 `assets/earlypotions` 与 `data/earlypotions`。

---

## [1.0.0-1.20.1] - 2026-06-16

### 新增 Added
- 项目初始化（对应提交 `646ae23`）。
- 新增「早期炼药台」方块、方块实体、GUI 与容器菜单（5 槽位：0–2 药水瓶、3 材料、4 燃料）。
- 新增 5 款基础早期药水（夜视 / 生命 / 水肺 / 力量 / 挖掘提升），配方材料为胡萝卜、牛肉、苹果、鲑鱼、鸡肉。
- 新增自定义酿造配方表 `CUSTOM_RECIPES` 与酿造逻辑（单次酿造 400 tick，煤炭/木炭提供 20 次燃料）。
- 新增 JEI 集成（`compileOnly`，未安装 JEI 亦可运行）。

---

## 版本与 Minecraft/Forge 对应关系

| 模组版本 | Minecraft | Forge | 说明 |
| --- | --- | --- | --- |
| 1.1.4-1.20.1 | 1.20.1 | 47.2.1 | 贴图重制、第二套盔甲「炼金宗师」 |
| 1.1.2-1.20.1 | 1.20.1 | 47.2.1 | 视觉资源重制（贴图与模型渲染参数） |
| 1.1.1-1.20.1 | 1.20.1 | 47.2.1 | 源码清理（替换已弃用 API） |
| 1.1.0-1.20.1 | 1.20.1 | 47.2.1 | 物品体系扩充、平衡优化 |
| 1.0.2-1.20.1 | 1.20.1 | 47.2.1 | 命名空间迁移 |
| 1.0.0-1.20.1 | 1.20.1 | 47.2.1 | 初始版本 |

---

## 技术选型与关键决策说明

| 领域 | 选型 | 理由 |
| --- | --- | --- |
| 模组加载器 | Minecraft Forge 47.2.1（1.20.1） | 目标版本生态成熟、文档与社区支持完善 |
| 映射 | Parchment `2023.09.03-1.20.1` | 提供可读的参数名与 Javadoc，便于维护 |
| 语言 / 工具链 | Java 17（`java.toolchain.languageVersion = 17`）+ Gradle 8（FG6） | 与 1.18+ 官方目标一致 |
| 注册方式 | `DeferredRegister` + `RegistryObject` | Forge 官方推荐的延迟注册，避免过早类加载 |
| 可饮用药水实现 | `FoodProperties` + `UseAnim.DRINK` | 无需自定义药水物品类即可复用原版饮用动画与提示 |
| 套装增益 | `TickEvent.PlayerTickEvent` 服务端周期检查 | 无需 Mixin/ASM，纯事件实现，兼容性好、可读性高 |
| JEI 集成 | `compileOnly` 依赖 | 未安装 JEI 也能正常运行，避免强依赖 |

---

## 遇到的问题及解决方案

1. **构建终端无法识别 `gradlew.bat`**
   - 原因：终端起始目录为系统目录，未切换到项目根目录。
   - 解决：执行前先 `Set-Location -LiteralPath '<项目根目录>'`。

2. **PowerShell 解析 `-D` 参数异常（`ClassNotFoundException: /awt/headless=true`）**
   - 原因：PowerShell 对 `-Dkey=value` 的解析与 Java 启动参数冲突。
   - 解决：改为直接以 `java "<源文件>"` 单文件源码模式运行贴图生成脚本。

3. **`jar` 命令在 PowerShell 中不可用**
   - 原因：`jar` 未加入 PATH。
   - 解决：改用 `[System.IO.Compression.ZipFile]::OpenRead(...)` 读取并校验 jar 条目。

4. **配方产出过高导致经济失衡**
   - 原因：初版 `potion_core` 单次产出 4 个，弱化了装备的获取难度。
   - 解决：调整为产出 2 个，重算材料投入产出比。

5. **`Loaded 7 recipes` 疑似配方缺失（已澄清）**
   - 原因：日志按 `RecipeType` 计数（7 种原版配方类型），而非配方条目总数。
   - 解决：阅读反编译源码确认计数口径后排除误报；同时确认全部 10 个配方 JSON 无解析错误。

6. **客户端启动崩溃日志（`DisplayWindow.windowTick` 空指针）**
   - 原因：Forge `fmlearlydisplay` 在特定环境下的窗口初始化问题。
   - 解决：与模组无关，确认非本次改动引入，不予处理。

---

## 兼容性说明

- 目标环境：Minecraft 1.20.1 + Forge 47.2.1（`loaderVersion` 要求 `[47,)`）。
- 依赖：Forge、Minecraft（均为强制依赖，写入 `mods.toml`）。
- 可选：JEI 15.x（`compileOnly`，不安装不影响运行）。
