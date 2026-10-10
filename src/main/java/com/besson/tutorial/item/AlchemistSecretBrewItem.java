package com.besson.tutorial.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 彩蛋物品：炼金术士的秘酿。
 * 饮用后随机获得三种 II 级增益，并额外获得幸运，同时弹出隐藏提示。
 */
public class AlchemistSecretBrewItem extends EarlyPotionItem {

    private static final MobEffect[] POOL = {
            MobEffects.DAMAGE_BOOST, MobEffects.MOVEMENT_SPEED, MobEffects.REGENERATION,
            MobEffects.DAMAGE_RESISTANCE, MobEffects.FIRE_RESISTANCE, MobEffects.ABSORPTION,
            MobEffects.JUMP, MobEffects.NIGHT_VISION, MobEffects.DIG_SPEED, MobEffects.WATER_BREATHING
    };

    public AlchemistSecretBrewItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);
        if (!level.isClientSide && livingEntity instanceof Player player) {
            RandomSource random = level.random;
            for (int i = 0; i < 3; i++) {
                MobEffect effect = POOL[random.nextInt(POOL.length)];
                player.addEffect(new MobEffectInstance(effect, 900, 1), player);
            }
            player.addEffect(new MobEffectInstance(MobEffects.LUCK, 1800, 0), player);
            player.displayClientMessage(
                    Component.translatable("item.earlypotions.alchemist_secret_brew.reveal"), false);
        }
        return result;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.earlypotions.alchemist_secret_brew.lore")
                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
    }
}
