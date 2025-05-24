package com.astrolabs.astroexpansion.api.guidebook;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Represents a category/chapter in the guide book
 */
public interface IGuideCategory {
    /**
     * Get the unique ID of this category
     */
    ResourceLocation getId();
    
    /**
     * Get the display name
     */
    Component getName();
    
    /**
     * Get the description
     */
    Component getDescription();
    
    /**
     * Get the icon item for this category
     */
    ItemStack getIcon();
    
    /**
     * Get all entries in this category
     */
    List<IGuideEntry> getEntries();
    
    /**
     * Get the sort order (lower = earlier in book)
     */
    int getSortOrder();
    
    /**
     * Check if this category requires specific conditions to view
     */
    boolean isLocked();
    
    /**
     * Get parent category if this is a subcategory
     */
    IGuideCategory getParent();
    
    /**
     * Get all subcategories
     */
    List<IGuideCategory> getSubcategories();
    
    /**
     * Get the background texture for this category's pages
     */
    ResourceLocation getBackgroundTexture();
    
    /**
     * Get the color theme for this category
     */
    int getColor();
}