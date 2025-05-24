package com.astrolabs.astroexpansion.common.network.packets;

import com.astrolabs.astroexpansion.common.menu.StorageTerminalMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateStorageSearchPacket {
    private final String searchQuery;
    
    public UpdateStorageSearchPacket(String searchQuery) {
        this.searchQuery = searchQuery;
    }
    
    public UpdateStorageSearchPacket(FriendlyByteBuf buf) {
        this.searchQuery = buf.readUtf();
    }
    
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(searchQuery);
    }
    
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof StorageTerminalMenu menu) {
                menu.setSearchQuery(searchQuery);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}