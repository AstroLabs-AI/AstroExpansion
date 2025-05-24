package com.astrolabs.astroexpansion.common.network.packets;

import com.astrolabs.astroexpansion.common.menu.StorageTerminalMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleFavoriteItemPacket {
    private final ItemStack itemStack;
    
    public ToggleFavoriteItemPacket(ItemStack itemStack) {
        this.itemStack = itemStack.copy();
    }
    
    public ToggleFavoriteItemPacket(FriendlyByteBuf buf) {
        this.itemStack = buf.readItem();
    }
    
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeItem(itemStack);
    }
    
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof StorageTerminalMenu menu) {
                menu.toggleFavorite(itemStack, player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}