package com.besson.tutorial;

import com.besson.tutorial.item.AlchemistArmorItem;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 通用（双端）事件处理器：为炼金术士盔甲提供穿戴增益。
 */
@Mod.EventBusSubscriber(modid = TutorialMod.MOD_ID)
public final class ModEvents {

    private ModEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Player player = event.player;
        Level level = player.level();
        if (level.isClientSide) {
            return;
        }
        // 每秒刷新一次即可，避免每刻重复写入
        if (player.tickCount % 20 != 0) {
            return;
        }

        applyArmorEffect(player, EquipmentSlot.HEAD, MobEffects.NIGHT_VISION);
        applyArmorEffect(player, EquipmentSlot.CHEST, MobEffects.DAMAGE_RESISTANCE);
        applyArmorEffect(player, EquipmentSlot.LEGS, MobEffects.MOVEMENT_SPEED);
        applyArmorEffect(player, EquipmentSlot.FEET, MobEffects.JUMP);
    }

    private static void applyArmorEffect(Player player, EquipmentSlot slot, MobEffect effect) {
        ItemStack stack = player.getItemBySlot(slot);
        if (!(stack.getItem() instanceof AlchemistArmorItem armor) || armor.getGrantedEffect() != effect) {
            return;
        }
        MobEffectInstance current = player.getEffect(effect);
        if (current == null || current.getDuration() < 200 || current.getAmplifier() < armor.getGrantedAmplifier()) {
            player.addEffect(new MobEffectInstance(effect, 300, armor.getGrantedAmplifier(), true, false, true), player);
        }
    }
}
