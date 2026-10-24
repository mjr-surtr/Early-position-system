package com.besson.tutorial.item;

import com.besson.tutorial.TutorialMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * 炼金术士盔甲材质。防御力介于铁与钻石之间，可用药水核心修复。
 */
public class ModArmorMaterials implements ArmorMaterial {
    public static final ModArmorMaterials ALCHEMIST =
            new ModArmorMaterials("alchemist", 18, new int[]{2, 5, 6, 2}, 22,
                    SoundEvents.ARMOR_EQUIP_GOLD, 0.5F, 0.0F);

    /** 进阶套装：更深邃的紫黑金属，防御与韧性接近下界合金。 */
    public static final ModArmorMaterials ALCHEMIST_MASTER =
            new ModArmorMaterials("alchemist_master", 24, new int[]{3, 6, 8, 3}, 25,
                    SoundEvents.ARMOR_EQUIP_NETHERITE, 1.0F, 0.1F);

    private final String name;
    private final int durabilityMultiplier;
    /** 顺序：{靴子, 护腿, 胸甲, 头盔} */
    private final int[] defenseForType;
    private final int enchantmentValue;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;

    private ModArmorMaterials(String name, int durabilityMultiplier, int[] defenseForType, int enchantmentValue,
                              SoundEvent equipSound, float toughness, float knockbackResistance) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.defenseForType = defenseForType;
        this.enchantmentValue = enchantmentValue;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return switch (type) {
            case BOOTS -> this.durabilityMultiplier * 13;
            case LEGGINGS -> this.durabilityMultiplier * 15;
            case CHESTPLATE -> this.durabilityMultiplier * 16;
            case HELMET -> this.durabilityMultiplier * 11;
        };
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return switch (type) {
            case BOOTS -> this.defenseForType[0];
            case LEGGINGS -> this.defenseForType[1];
            case CHESTPLATE -> this.defenseForType[2];
            case HELMET -> this.defenseForType[3];
        };
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(Moditems.POTION_CORE.get());
    }

    @Override
    public String getName() {
        return TutorialMod.MOD_ID + ":" + this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
