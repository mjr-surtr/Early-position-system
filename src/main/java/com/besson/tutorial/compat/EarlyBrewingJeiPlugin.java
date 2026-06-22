package com.besson.tutorial.compat;

import com.besson.tutorial.TutorialMod;
import com.besson.tutorial.block.ModBlocks;
import com.besson.tutorial.item.Moditems;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

@JeiPlugin
public class EarlyBrewingJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(TutorialMod.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new EarlyBrewingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(EarlyBrewingRecipeCategory.TYPE, List.of(
                new EarlyBrewingRecipe(Items.CARROT,  Moditems.EARLY_NIGHT_VISION_POTION.get()),
                new EarlyBrewingRecipe(Items.BEEF,    Moditems.EARLY_STRENGTH_POTION.get()),
                new EarlyBrewingRecipe(Items.APPLE,   Moditems.EARLY_HEALING_POTION.get()),
                new EarlyBrewingRecipe(Items.SALMON,  Moditems.EARLY_WATER_BREATHING_POTION.get()),
                new EarlyBrewingRecipe(Items.CHICKEN, Moditems.EARLY_MINING_POTION.get())
        ));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.EARLY_BREWING_STAND.get()),
                EarlyBrewingRecipeCategory.TYPE);
    }
}
