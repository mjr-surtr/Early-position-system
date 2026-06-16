package com.besson.tutorial.block;

import com.besson.tutorial.TutorialMod;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TutorialMod.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TutorialMod.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, TutorialMod.MOD_ID);

    public static final RegistryObject<Block> EARLY_BREWING_STAND =
            BLOCKS.register("early_brewing_stand", () -> new EarlyBrewingStandBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE)
                            .strength(0.5F)
                            .noOcclusion()
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()));

    public static final RegistryObject<BlockEntityType<EarlyBrewingStandBlockEntity>> EARLY_BREWING_STAND_BE =
            BLOCK_ENTITIES.register("early_brewing_stand", () ->
                    BlockEntityType.Builder.of(
                            EarlyBrewingStandBlockEntity::new,
                            EARLY_BREWING_STAND.get()
                    ).build(null));

    public static final RegistryObject<MenuType<EarlyBrewingStandMenu>> EARLY_BREWING_STAND_MENU =
            MENU_TYPES.register("early_brewing_stand", () ->
                    IForgeMenuType.create((windowId, inv, data) ->
                            new EarlyBrewingStandMenu(windowId, inv)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
        MENU_TYPES.register(eventBus);
    }
}
