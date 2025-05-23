package com.astrolabs.astroexpansion.common.events;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.capabilities.ResearchCapabilityProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AstroExpansion.MODID)
public class CapabilityEvents {
    
    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(ResearchCapabilityProvider.RESEARCH_CAPABILITY).isPresent()) {
                event.addCapability(new ResourceLocation(AstroExpansion.MODID, "research"), new ResearchCapabilityProvider());
            }
        }
    }
    
    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // Copy research data from old player to new player after death
            event.getOriginal().getCapability(ResearchCapabilityProvider.RESEARCH_CAPABILITY).ifPresent(oldStore -> {
                event.getEntity().getCapability(ResearchCapabilityProvider.RESEARCH_CAPABILITY).ifPresent(newStore -> {
                    newStore.loadNBTData(oldStore.serializeNBT());
                });
            });
        }
    }
    
    @Mod.EventBusSubscriber(modid = AstroExpansion.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void registerCapabilities(RegisterCapabilitiesEvent event) {
            event.register(com.astrolabs.astroexpansion.common.capabilities.ResearchCapability.class);
        }
    }
}