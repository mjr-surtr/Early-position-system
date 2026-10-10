package com.besson.tutorial.item;

import com.besson.tutorial.TutorialMod;
import com.besson.tutorial.block.ModBlocks;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Moditems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TutorialMod.MOD_ID);

    // ==================== 早期药水（可饮用，30秒效果） ====================

    public static final RegistryObject<Item> EARLY_NIGHT_VISION_POTION =
            ITEMS.register("early_night_vision_potion", () -> new EarlyPotionItem(potionProperties(
                    () -> new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0))));

    public static final RegistryObject<Item> EARLY_HEALING_POTION =
            ITEMS.register("early_healing_potion", () -> new EarlyPotionItem(potionProperties(
                    () -> new MobEffectInstance(MobEffects.REGENERATION, 600, 0))));

    public static final RegistryObject<Item> EARLY_WATER_BREATHING_POTION =
            ITEMS.register("early_water_breathing_potion", () -> new EarlyPotionItem(potionProperties(
                    () -> new MobEffectInstance(MobEffects.WATER_BREATHING, 600, 0))));

    public static final RegistryObject<Item> EARLY_STRENGTH_POTION =
            ITEMS.register("early_strength_potion", () -> new EarlyPotionItem(potionProperties(
                    () -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 0))));

    public static final RegistryObject<Item> EARLY_MINING_POTION =
            ITEMS.register("early_mining_potion", () -> new EarlyPotionItem(potionProperties(
                    () -> new MobEffectInstance(MobEffects.DIG_SPEED, 600, 0))));

    // ==================== 新增早期药水（v1.1.0） ====================

    public static final RegistryObject<Item> EARLY_SWIFTNESS_POTION =
            ITEMS.register("early_swiftness_potion", () -> new EarlyPotionItem(potionProperties(
                    () -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 0))));

    public static final RegistryObject<Item> EARLY_LEAPING_POTION =
            ITEMS.register("early_leaping_potion", () -> new EarlyPotionItem(potionProperties(
                    () -> new MobEffectInstance(MobEffects.JUMP, 600, 0))));

    public static final RegistryObject<Item> EARLY_SLOW_FALLING_POTION =
            ITEMS.register("early_slow_falling_potion", () -> new EarlyPotionItem(potionProperties(
                    () -> new MobEffectInstance(MobEffects.SLOW_FALLING, 600, 0))));

    public static final RegistryObject<Item> EARLY_INVISIBILITY_POTION =
            ITEMS.register("early_invisibility_potion", () -> new EarlyPotionItem(potionProperties(
                    () -> new MobEffectInstance(MobEffects.INVISIBILITY, 600, 0))));

    public static final RegistryObject<Item> EARLY_GLOWING_POTION =
            ITEMS.register("early_glowing_potion", () -> new EarlyPotionItem(potionProperties(
                    () -> new MobEffectInstance(MobEffects.GLOWING, 600, 0))));

    public static final RegistryObject<Item> EARLY_LUCK_POTION =
            ITEMS.register("early_luck_potion", () -> new EarlyPotionItem(potionProperties(
                    () -> new MobEffectInstance(MobEffects.LUCK, 600, 0))));

    // ==================== 功能道具 ====================

    /** 炼金净化剂：清除全部负面效果。 */
    public static final RegistryObject<Item> ALCHEMIST_CLEANSER =
            ITEMS.register("alchemist_cleanser", () -> new AlchemistCleanserItem(
                    new Item.Properties().stacksTo(1).food(new FoodProperties.Builder()
                            .nutrition(0).saturationMod(0).alwaysEat().build())));

    /** 药水核心：炼金装备的核心合成材料。 */
    public static final RegistryObject<Item> POTION_CORE =
            ITEMS.register("potion_core", () -> new LoreItem(
                    new Item.Properties(), "item.earlypotions.potion_core.lore"));

    // ==================== 炼金术士装备（穿戴生效，详见 ModEvents） ====================

    public static final RegistryObject<Item> ALCHEMIST_HELMET =
            ITEMS.register("alchemist_helmet", () -> new AlchemistArmorItem(
                    ModArmorMaterials.ALCHEMIST, ArmorItem.Type.HELMET,
                    MobEffects.NIGHT_VISION, 0, new Item.Properties()));

    public static final RegistryObject<Item> ALCHEMIST_CHESTPLATE =
            ITEMS.register("alchemist_chestplate", () -> new AlchemistArmorItem(
                    ModArmorMaterials.ALCHEMIST, ArmorItem.Type.CHESTPLATE,
                    MobEffects.DAMAGE_RESISTANCE, 0, new Item.Properties()));

    public static final RegistryObject<Item> ALCHEMIST_LEGGINGS =
            ITEMS.register("alchemist_leggings", () -> new AlchemistArmorItem(
                    ModArmorMaterials.ALCHEMIST, ArmorItem.Type.LEGGINGS,
                    MobEffects.MOVEMENT_SPEED, 0, new Item.Properties()));

    public static final RegistryObject<Item> ALCHEMIST_BOOTS =
            ITEMS.register("alchemist_boots", () -> new AlchemistArmorItem(
                    ModArmorMaterials.ALCHEMIST, ArmorItem.Type.BOOTS,
                    MobEffects.JUMP, 0, new Item.Properties()));

    /** 炼金之刃：命中附加虚弱与发光。 */
    public static final RegistryObject<Item> ALCHEMIST_BLADE =
            ITEMS.register("alchemist_blade", () -> new AlchemistBladeItem(
                    ModTiers.ALCHEMIST, 3, -2.4F, new Item.Properties()));

    // ==================== 彩蛋物品 ====================

    /** 炼金术士的秘酿：随机强力增益 + 隐藏提示。 */
    public static final RegistryObject<Item> ALCHEMIST_SECRET_BREW =
            ITEMS.register("alchemist_secret_brew", () -> new AlchemistSecretBrewItem(
                    new Item.Properties().stacksTo(1).food(new FoodProperties.Builder()
                            .nutrition(0).saturationMod(0).alwaysEat().build())));

    // ==================== 创造模式图标 / 方块 ====================

    /** 早期药水系统图标（同时获得5种效果30秒）。 */
    public static final RegistryObject<Item> EARLY_TAB_ICON =
            ITEMS.register("early_tab_icon", () -> new EarlyPotionItem(new Item.Properties()
                    .stacksTo(1)
                    .food(new FoodProperties.Builder()
                            .nutrition(0)
                            .saturationMod(0)
                            .alwaysEat()
                            .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0), 1.0f)
                            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 600, 0), 1.0f)
                            .effect(() -> new MobEffectInstance(MobEffects.WATER_BREATHING, 600, 0), 1.0f)
                            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 0), 1.0f)
                            .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 600, 0), 1.0f)
                            .build())));

    /** 早期炼药台方块物品。 */
    public static final RegistryObject<Item> EARLY_BREWING_STAND_ITEM =
            ITEMS.register("early_brewing_stand", () -> new BlockItem(ModBlocks.EARLY_BREWING_STAND.get(),
                    new Item.Properties().stacksTo(64)));

    /** 统一构建"可饮用"药水属性：无饱食度，随时可饮。 */
    private static Item.Properties potionProperties(java.util.function.Supplier<MobEffectInstance> effect) {
        return new Item.Properties().stacksTo(1).food(new FoodProperties.Builder()
                .nutrition(0)
                .saturationMod(0)
                .alwaysEat()
                .effect(effect, 1.0f)
                .build());
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
