package com.besson.tutorial.item;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class EarlyPotionItem extends Item {
    public EarlyPotionItem(Properties properties) {
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        FoodProperties food = this.getFoodProperties();
        if (food != null) {
            for (Pair<MobEffectInstance, Float> pair : food.getEffects()) {
                MobEffectInstance effect = pair.getFirst();
                MutableComponent line = Component.translatable(effect.getDescriptionId());
                if (effect.getAmplifier() > 0) {
                    line = Component.translatable("potion.withAmplifier", line,
                            Component.translatable("potion.potency." + effect.getAmplifier()));
                }
                if (effect.getDuration() > 20) {
                    line = Component.translatable("potion.withDuration", line,
                            MobEffectUtil.formatDuration(effect, 1.0F));
                }
                tooltip.add(line.withStyle(effect.getEffect().getCategory().getTooltipFormatting()));
            }
        }
    }
}
