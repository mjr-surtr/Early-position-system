package com.besson.tutorial;

import com.besson.tutorial.block.EarlyBrewingStandScreen;
import com.besson.tutorial.block.ModBlocks;
import com.besson.tutorial.item.ModCreativeModeTabs;
import com.besson.tutorial.item.Moditems;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TutorialMod.MOD_ID)
public class TutorialMod
{
    public static final String MOD_ID = "earlypotions";

    public TutorialMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        Moditems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModBlocks.register(modEventBus);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            MenuScreens.register(ModBlocks.EARLY_BREWING_STAND_MENU.get(), EarlyBrewingStandScreen::new);
        }
    }
}
