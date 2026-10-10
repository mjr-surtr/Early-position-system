package com.besson.tutorial.item;

import com.besson.tutorial.TutorialMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TutorialMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> EARLY_TAB =
            CREATIVE_MODE_TABS.register("early", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(Moditems.EARLY_TAB_ICON.get()))
                    .title(Component.translatable("itemGroup.early"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(Moditems.EARLY_BREWING_STAND_ITEM.get());

                        // 早期药水
                        pOutput.accept(Moditems.EARLY_NIGHT_VISION_POTION.get());
                        pOutput.accept(Moditems.EARLY_HEALING_POTION.get());
                        pOutput.accept(Moditems.EARLY_WATER_BREATHING_POTION.get());
                        pOutput.accept(Moditems.EARLY_STRENGTH_POTION.get());
                        pOutput.accept(Moditems.EARLY_MINING_POTION.get());
                        pOutput.accept(Moditems.EARLY_SWIFTNESS_POTION.get());
                        pOutput.accept(Moditems.EARLY_LEAPING_POTION.get());
                        pOutput.accept(Moditems.EARLY_SLOW_FALLING_POTION.get());
                        pOutput.accept(Moditems.EARLY_INVISIBILITY_POTION.get());
                        pOutput.accept(Moditems.EARLY_GLOWING_POTION.get());
                        pOutput.accept(Moditems.EARLY_LUCK_POTION.get());

                        // 功能道具
                        pOutput.accept(Moditems.POTION_CORE.get());
                        pOutput.accept(Moditems.ALCHEMIST_CLEANSER.get());

                        // 炼金术士装备
                        pOutput.accept(Moditems.ALCHEMIST_HELMET.get());
                        pOutput.accept(Moditems.ALCHEMIST_CHESTPLATE.get());
                        pOutput.accept(Moditems.ALCHEMIST_LEGGINGS.get());
                        pOutput.accept(Moditems.ALCHEMIST_BOOTS.get());
                        pOutput.accept(Moditems.ALCHEMIST_BLADE.get());

                        // 图标与彩蛋
                        pOutput.accept(Moditems.EARLY_TAB_ICON.get());
                        pOutput.accept(Moditems.ALCHEMIST_SECRET_BREW.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
