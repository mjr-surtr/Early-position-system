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
                        pOutput.accept(Moditems.EARLY_NIGHT_VISION_POTION.get());
                        pOutput.accept(Moditems.EARLY_HEALING_POTION.get());
                        pOutput.accept(Moditems.EARLY_WATER_BREATHING_POTION.get());
                        pOutput.accept(Moditems.EARLY_STRENGTH_POTION.get());
                        pOutput.accept(Moditems.EARLY_MINING_POTION.get());
                        pOutput.accept(Moditems.EARLY_TAB_ICON.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
