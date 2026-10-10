package com.besson.tutorial.item;

import com.besson.tutorial.TutorialMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 炼金术士盔甲。装备时由 ModEvents 持续提供对应增益效果。
 */
public class AlchemistArmorItem extends ArmorItem {
    private final MobEffect grantedEffect;
    private final int grantedAmplifier;

    public AlchemistArmorItem(ArmorMaterial material, ArmorItem.Type type,
                              MobEffect grantedEffect, int grantedAmplifier, Properties properties) {
        super(material, type, properties);
        this.grantedEffect = grantedEffect;
        this.grantedAmplifier = grantedAmplifier;
    }

    public MobEffect getGrantedEffect() {
        return grantedEffect;
    }

    public int getGrantedAmplifier() {
        return grantedAmplifier;
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return TutorialMod.MOD_ID + ":textures/models/armor/alchemist_layer_"
                + (slot == EquipmentSlot.LEGS ? 2 : 1) + ".png";
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.earlypotions.alchemist_armor.lore")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
}
