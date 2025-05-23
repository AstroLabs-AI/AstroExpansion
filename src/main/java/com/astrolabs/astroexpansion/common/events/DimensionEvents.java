package com.astrolabs.astroexpansion.common.events;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.items.SpaceSuitArmorItem;
import com.astrolabs.astroexpansion.common.registry.ModDimensions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AstroExpansion.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DimensionEvents {
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.player.level().isClientSide) {
            return;
        }
        
        Player player = event.player;
        Level level = player.level();
        
        // Check if player is in space dimension
        if (level.dimension() == ModDimensions.SPACE_KEY) {
            handleSpaceDimension(player);
        }
        // Check if player is on the moon
        else if (level.dimension() == ModDimensions.MOON_KEY) {
            handleMoonDimension(player);
        }
    }
    
    private static void handleSpaceDimension(Player player) {
        // Apply zero gravity (slow falling + levitation)
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 2, false, false));
        
        // Allow floating movement
        if (player.getDeltaMovement().y < 0.1 && player.getDeltaMovement().y > -0.1) {
            player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20, 0, false, false));
        }
        
        // Check for space suit
        if (!SpaceSuitArmorItem.hasFullSuit(player)) {
            // Apply damage effects without suit
            player.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2, false, false));
            
            // Suffocation damage
            if (player.tickCount % 20 == 0) {
                player.hurt(player.damageSources().drown(), 2.0F);
            }
        }
    }
    
    private static void handleMoonDimension(Player player) {
        // Apply low gravity (1/6th of Earth)
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 60, 2, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, false, false));
        
        // Check for space suit
        if (!SpaceSuitArmorItem.hasFullSuit(player)) {
            // Less severe than space but still dangerous
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 1, false, false));
            
            // Slow suffocation
            if (player.tickCount % 40 == 0) {
                player.hurt(player.damageSources().drown(), 1.0F);
            }
        }
    }
}