package com.besson.tutorial.compat;

import net.minecraft.world.item.Item;

/**
 * JEI recipe wrapper – one ingredient + water bottle → one potion
 */
public record EarlyBrewingRecipe(Item ingredient, Item result) {
}
