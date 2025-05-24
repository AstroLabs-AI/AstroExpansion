package com.astrolabs.astroexpansion.common.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class FavoriteItemsData extends SavedData {
    private static final String DATA_NAME = "astroexpansion_favorites";
    
    private final Map<UUID, Set<ItemStack>> playerFavorites = new HashMap<>();
    
    public static FavoriteItemsData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
            FavoriteItemsData::load,
            FavoriteItemsData::new,
            DATA_NAME
        );
    }
    
    public Set<ItemStack> getFavorites(UUID playerUUID) {
        return playerFavorites.computeIfAbsent(playerUUID, k -> new HashSet<>());
    }
    
    public boolean isFavorite(UUID playerUUID, ItemStack stack) {
        Set<ItemStack> favorites = getFavorites(playerUUID);
        return favorites.stream().anyMatch(fav -> ItemStack.isSameItemSameTags(fav, stack));
    }
    
    public void toggleFavorite(UUID playerUUID, ItemStack stack) {
        Set<ItemStack> favorites = getFavorites(playerUUID);
        
        // Find existing favorite
        Optional<ItemStack> existing = favorites.stream()
            .filter(fav -> ItemStack.isSameItemSameTags(fav, stack))
            .findFirst();
            
        if (existing.isPresent()) {
            favorites.remove(existing.get());
        } else {
            favorites.add(stack.copy());
        }
        
        setDirty();
    }
    
    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag playersTag = new CompoundTag();
        
        for (Map.Entry<UUID, Set<ItemStack>> entry : playerFavorites.entrySet()) {
            ListTag favoritesTag = new ListTag();
            for (ItemStack stack : entry.getValue()) {
                CompoundTag itemTag = new CompoundTag();
                stack.save(itemTag);
                favoritesTag.add(itemTag);
            }
            playersTag.put(entry.getKey().toString(), favoritesTag);
        }
        
        tag.put("PlayerFavorites", playersTag);
        return tag;
    }
    
    public static FavoriteItemsData load(CompoundTag tag) {
        FavoriteItemsData data = new FavoriteItemsData();
        
        CompoundTag playersTag = tag.getCompound("PlayerFavorites");
        for (String uuidStr : playersTag.getAllKeys()) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                Set<ItemStack> favorites = new HashSet<>();
                
                ListTag favoritesTag = playersTag.getList(uuidStr, Tag.TAG_COMPOUND);
                for (int i = 0; i < favoritesTag.size(); i++) {
                    ItemStack stack = ItemStack.of(favoritesTag.getCompound(i));
                    if (!stack.isEmpty()) {
                        favorites.add(stack);
                    }
                }
                
                data.playerFavorites.put(uuid, favorites);
            } catch (IllegalArgumentException e) {
                // Invalid UUID, skip
            }
        }
        
        return data;
    }
}