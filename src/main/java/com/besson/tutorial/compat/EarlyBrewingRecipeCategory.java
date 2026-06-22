package com.besson.tutorial.compat;

import com.besson.tutorial.TutorialMod;
import com.besson.tutorial.block.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class EarlyBrewingRecipeCategory implements IRecipeCategory<EarlyBrewingRecipe> {

    public static final RecipeType<EarlyBrewingRecipe> TYPE =
            RecipeType.create(TutorialMod.MOD_ID, "early_brewing", EarlyBrewingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public EarlyBrewingRecipeCategory(IGuiHelper guiHelper) {
        // 72宽 x 55高，布局模仿原版酿造台：
        //   [燃料]          [原料]
        //      ↓        ↓        ↓
        //   [瓶1]  [瓶2]  [瓶3]
        this.background = guiHelper.createBlankDrawable(72, 55);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.EARLY_BREWING_STAND.get()));
                                                                                                                                                                                                                                                                                                                                            }

    @Override
    public RecipeType<EarlyBrewingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.earlypotions.early_brewing");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(EarlyBrewingRecipe recipe, IRecipeSlotsView slotsView,
                     GuiGraphics graphics, double mouseX, double mouseY) {
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EarlyBrewingRecipe recipe, IFocusGroup focuses) {
        // 左上 - 燃料（煤炭/木炭）
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                .addIngredients(Ingredient.of(Items.COAL, Items.CHARCOAL));

        // 顶部中间 - 原料（胡萝卜/牛肉/苹果/鲑鱼/鸡肉…）
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 1)
                .addIngredients(Ingredient.of(recipe.ingredient()));

        // 底部一行 - 3个药水瓶（输入玻璃瓶 → 输出药水）
        builder.addSlot(RecipeIngredientRole.OUTPUT, 1, 40)
                .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(recipe.result()))
                .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.GLASS_BOTTLE));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 27, 40)
                .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(recipe.result()))
                .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.GLASS_BOTTLE));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 53, 40)
                .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(recipe.result()))
                .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.GLASS_BOTTLE));
    }
}
