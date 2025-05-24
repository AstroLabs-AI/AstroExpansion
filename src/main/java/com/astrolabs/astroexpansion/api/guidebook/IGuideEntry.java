package com.astrolabs.astroexpansion.api.guidebook;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Set;

/**
 * Represents a single entry/article in the guide book
 */
public interface IGuideEntry {
    /**
     * Get the unique ID of this entry
     */
    ResourceLocation getId();
    
    /**
     * Get the display name
     */
    Component getName();
    
    /**
     * Get the icon for this entry
     */
    ItemStack getIcon();
    
    /**
     * Get all pages in this entry
     */
    List<IGuidePage> getPages();
    
    /**
     * Get the category this entry belongs to
     */
    IGuideCategory getCategory();
    
    /**
     * Get all tags associated with this entry
     */
    Set<String> getTags();
    
    /**
     * Check if a player has unlocked this entry
     */
    boolean isUnlocked(Player player);
    
    /**
     * Get the unlock requirements
     */
    IUnlockRequirement getUnlockRequirement();
    
    /**
     * Get related entries
     */
    List<IGuideEntry> getRelatedEntries();
    
    /**
     * Get the sort order within its category
     */
    int getSortOrder();
    
    /**
     * Check if this entry should be highlighted as new
     */
    boolean isNew(Player player);
    
    /**
     * Get advancement reward for reading this entry
     */
    ResourceLocation getAdvancement();
    
    /**
     * Check if this entry contains spoilers
     */
    boolean hasSpoilers();
    
    /**
     * Get items/blocks associated with this entry
     */
    List<ItemStack> getAssociatedItems();
}