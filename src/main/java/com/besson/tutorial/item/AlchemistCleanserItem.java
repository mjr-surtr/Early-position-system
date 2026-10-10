package com.besson.tutorial.item;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * 炼金净化剂：饮下后清除自身所有负面（HARMFUL）效果。
 */
public class AlchemistCleanserItem extends EarlyPotionItem {

    public AlchemistCleanserItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide && livingEntity instanceof Player player) {
            List<MobEffect> harmful = new ArrayList<>();
            for (MobEffectInstance instance : player.getActiveEffects()) {
                if (instance.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                    harmful.add(instance.getEffect());
                }
            }
            for (MobEffect effect : harmful) {
                player.removeEffect(effect);
            }
        }
        return super.finishUsingItem(stack, level, livingEntity);
    }
}
