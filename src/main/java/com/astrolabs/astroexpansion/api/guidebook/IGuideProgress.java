package com.astrolabs.astroexpansion.api.guidebook;

import net.minecraft.resources.ResourceLocation;

import java.util.Set;

/**
 * Tracks a player's progress through the guide book
 */
public interface IGuideProgress {
    /**
     * Get all viewed entries
     */
    Set<ResourceLocation> getViewedEntries();
    
    /**
     * Get all unlocked entries
     */
    Set<ResourceLocation> getUnlockedEntries();
    
    /**
     * Get bookmarked entries
     */
    Set<ResourceLocation> getBookmarkedEntries();
    
    /**
     * Get completed checklists
     */
    Set<ResourceLocation> getCompletedChecklists();
    
    /**
     * Get quiz scores
     */
    int getQuizScore(ResourceLocation quizId);
    
    /**
     * Get total completion percentage
     */
    float getTotalCompletion();
    
    /**
     * Get category completion percentage
     */
    float getCategoryCompletion(ResourceLocation categoryId);
    
    /**
     * Check if a specific achievement was earned
     */
    boolean hasAchievement(ResourceLocation achievementId);
    
    /**
     * Get custom notes for an entry
     */
    String getNotes(ResourceLocation entryId);
    
    /**
     * Set custom notes for an entry
     */
    void setNotes(ResourceLocation entryId, String notes);
    
    /**
     * Get the player's selected theme
     */
    String getTheme();
    
    /**
     * Set the player's theme
     */
    void setTheme(String theme);
    
    /**
     * Get recently viewed entries (ordered)
     */
    ResourceLocation[] getRecentHistory(int limit);
}