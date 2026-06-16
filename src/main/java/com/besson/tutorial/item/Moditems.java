package com.besson.tutorial.item;

import com.besson.tutorial.TutorialMod;
import com.besson.tutorial.block.ModBlocks;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Moditems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TutorialMod.MOD_ID);

    // 早期药水（可饮用，30秒效果）
    public static final RegistryObject<Item> EARLY_NIGHT_VISION_POTION =
            ITEMS.register("early_night_vision_potion", () -> new EarlyPotionItem(new Item.Properties()
                    .stacksTo(1)
                    .food(new FoodProperties.Builder()
                            .nutrition(0)
                            .saturationMod(0)
                            .alwaysEat()
                            .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0), 1.0f)
                            .build())));

    public static final RegistryObject<Item> EARLY_HEALING_POTION =
            ITEMS.register("early_healing_potion", () -> new EarlyPotionItem(new Item.Properties()
                    .stacksTo(1)
                    .food(new FoodProperties.Builder()
                            .nutrition(0)
                            .saturationMod(0)
                            .alwaysEat()
                            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 600, 0), 1.0f)
                            .build())));

    public static final RegistryObject<Item> EARLY_WATER_BREATHING_POTION =
            ITEMS.register("early_water_breathing_potion", () -> new EarlyPotionItem(new Item.Properties()
                    .stacksTo(1)
                    .food(new FoodProperties.Builder()
                            .nutrition(0)
                            .saturationMod(0)
                            .alwaysEat()
                            .effect(() -> new MobEffectInstance(MobEffects.WATER_BREATHING, 600, 0), 1.0f)
                            .build())));

    public static final RegistryObject<Item> EARLY_STRENGTH_POTION =
            ITEMS.register("early_strength_potion", () -> new EarlyPotionItem(new Item.Properties()
                    .stacksTo(1)
                    .food(new FoodProperties.Builder()
                            .nutrition(0)
                            .saturationMod(0)
                            .alwaysEat()
                            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 0), 1.0f)
                            .build())));

    public static final RegistryObject<Item> EARLY_MINING_POTION =
            ITEMS.register("early_mining_potion", () -> new EarlyPotionItem(new Item.Properties()
                    .stacksTo(1)
                    .food(new FoodProperties.Builder()
                            .nutrition(0)
                            .saturationMod(0)
                            .alwaysEat()
                            .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 600, 0), 1.0f)
                            .build())));

    // 早期药水系统图标（同时获得5种效果30秒）
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

    // 早期炼药台方块物品
    public static final RegistryObject<Item> EARLY_BREWING_STAND_ITEM =
            ITEMS.register("early_brewing_stand", () -> new BlockItem(ModBlocks.EARLY_BREWING_STAND.get(),
                    new Item.Properties().stacksTo(64)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
