package com.astrolabs.astroexpansion.common.network;

import com.astrolabs.astroexpansion.AstroExpansion;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = AstroExpansion.MODID, bus = EventBusSubscriber.Bus.MOD)
public class AEPackets {
    
    public static final ResourceLocation ENERGY_SYNC_ID = ResourceLocation.fromNamespaceAndPath(AstroExpansion.MODID, "energy_sync");
    
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        
        registrar.playToClient(
            EnergySyncPacket.TYPE,
            EnergySyncPacket.CODEC,
            EnergySyncPacket::handle
        );
    }
}