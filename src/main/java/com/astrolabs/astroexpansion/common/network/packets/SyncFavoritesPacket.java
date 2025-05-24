package com.astrolabs.astroexpansion.common.network.packets;

import com.astrolabs.astroexpansion.common.data.FavoriteItemsData;
import com.astrolabs.astroexpansion.common.menu.StorageTerminalMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class SyncFavoritesPacket {
    private final Set<ItemStack> favorites;
    
    public SyncFavoritesPacket(Set<ItemStack> favorites) {
        this.favorites = new HashSet<>(favorites);
    }
    
    public SyncFavoritesPacket(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        this.favorites = new HashSet<>();
        for (int i = 0; i < size; i++) {
            ItemStack stack = buf.readItem();
            if (!stack.isEmpty()) {
                favorites.add(stack);
            }
        }
    }
    
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(favorites.size());
        for (ItemStack stack : favorites) {
            buf.writeItem(stack);
        }
    }
    
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Client-side handling
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.containerMenu instanceof StorageTerminalMenu menu) {
                menu.setClientFavorites(favorites);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}