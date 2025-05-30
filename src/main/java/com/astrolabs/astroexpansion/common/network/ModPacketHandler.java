package com.astrolabs.astroexpansion.common.network;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.network.packets.*;
import com.astrolabs.astroexpansion.common.network.packet.LaunchRocketPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModPacketHandler {
    private static SimpleChannel INSTANCE;
    
    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }
    
    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(AstroExpansion.MODID, "messages"))
            .networkProtocolVersion(() -> "1.0")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();
        
        INSTANCE = net;
        
        // Teleporter packets
        net.messageBuilder(TeleportPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
            .decoder(TeleportPacket::new)
            .encoder(TeleportPacket::toBytes)
            .consumerMainThread(TeleportPacket::handle)
            .add();
            
        net.messageBuilder(UpdateTeleporterNamePacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
            .decoder(UpdateTeleporterNamePacket::new)
            .encoder(UpdateTeleporterNamePacket::toBytes)
            .consumerMainThread(UpdateTeleporterNamePacket::handle)
            .add();
            
        // Storage Terminal packets
        net.messageBuilder(UpdateStorageSearchPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
            .decoder(UpdateStorageSearchPacket::new)
            .encoder(UpdateStorageSearchPacket::toBytes)
            .consumerMainThread(UpdateStorageSearchPacket::handle)
            .add();
            
        net.messageBuilder(ToggleFavoriteItemPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
            .decoder(ToggleFavoriteItemPacket::new)
            .encoder(ToggleFavoriteItemPacket::toBytes)
            .consumerMainThread(ToggleFavoriteItemPacket::handle)
            .add();
            
        net.messageBuilder(SyncFavoritesPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
            .decoder(SyncFavoritesPacket::new)
            .encoder(SyncFavoritesPacket::toBytes)
            .consumerMainThread(SyncFavoritesPacket::handle)
            .add();
            
        // Rocket launch packet
        net.messageBuilder(LaunchRocketPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
            .decoder(LaunchRocketPacket::new)
            .encoder(LaunchRocketPacket::encode)
            .consumerMainThread(LaunchRocketPacket::handle)
            .add();
    }
    
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
    
    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
    
    public static <MSG> void sendToClients(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}