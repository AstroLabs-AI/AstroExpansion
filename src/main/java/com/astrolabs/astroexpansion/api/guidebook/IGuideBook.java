package com.astrolabs.astroexpansion.api.guidebook;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

/**
 * Main interface for the AstroExpansion Guide Book system
 */
public interface IGuideBook {
    /**
     * Get all categories in the book
     */
    List<IGuideCategory> getCategories();
    
    /**
     * Get a specific category by ID
     */
    Optional<IGuideCategory> getCategory(ResourceLocation id);
    
    /**
     * Get all entries across all categories
     */
    List<IGuideEntry> getAllEntries();
    
    /**
     * Search entries by query
     */
    List<IGuideEntry> search(String query);
    
    /**
     * Get entries by tag
     */
    List<IGuideEntry> getEntriesByTag(String tag);
    
    /**
     * Get the player's progress data
     */
    IGuideProgress getProgress(Player player);
    
    /**
     * Check if a player can view an entry
     */
    boolean canView(Player player, IGuideEntry entry);
    
    /**
     * Get the entry for a specific item/block
     */
    Optional<IGuideEntry> getEntryFor(ItemStack stack);
    
    /**
     * Get recently viewed entries for a player
     */
    List<IGuideEntry> getRecentlyViewed(Player player, int limit);
    
    /**
     * Get bookmarked entries for a player
     */
    List<IGuideEntry> getBookmarks(Player player);
    
    /**
     * Toggle bookmark for an entry
     */
    void toggleBookmark(Player player, IGuideEntry entry);
    
    /**
     * Mark an entry as viewed
     */
    void markViewed(Player player, IGuideEntry entry);
    
    /**
     * Get the book's title
     */
    Component getTitle();
    
    /**
     * Get the book's subtitle/description
     */
    Component getSubtitle();
}