package com.astrolabs.astroexpansion.common.guidebook;

import com.astrolabs.astroexpansion.api.guidebook.IGuideProgress;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

/**
 * Implementation of player guide book progress tracking
 */
public class GuideProgress implements IGuideProgress {
    private final Set<ResourceLocation> viewedEntries = new HashSet<>();
    private final Set<ResourceLocation> unlockedEntries = new HashSet<>();
    private final Set<ResourceLocation> bookmarkedEntries = new HashSet<>();
    private final Set<ResourceLocation> completedChecklists = new HashSet<>();
    private final Map<ResourceLocation, Integer> quizScores = new HashMap<>();
    private final Map<ResourceLocation, String> notes = new HashMap<>();
    private final LinkedList<ResourceLocation> recentHistory = new LinkedList<>();
    private String theme = "default";
    
    @Override
    public Set<ResourceLocation> getViewedEntries() {
        return viewedEntries;
    }
    
    @Override
    public Set<ResourceLocation> getUnlockedEntries() {
        return unlockedEntries;
    }
    
    @Override
    public Set<ResourceLocation> getBookmarkedEntries() {
        return bookmarkedEntries;
    }
    
    @Override
    public Set<ResourceLocation> getCompletedChecklists() {
        return completedChecklists;
    }
    
    @Override
    public int getQuizScore(ResourceLocation quizId) {
        return quizScores.getOrDefault(quizId, 0);
    }
    
    public Map<ResourceLocation, Integer> getQuizScores() {
        return quizScores;
    }
    
    @Override
    public float getTotalCompletion() {
        // TODO: Calculate based on total available entries
        return 0.0f;
    }
    
    @Override
    public float getCategoryCompletion(ResourceLocation categoryId) {
        // TODO: Calculate based on category entries
        return 0.0f;
    }
    
    @Override
    public boolean hasAchievement(ResourceLocation achievementId) {
        // TODO: Track achievements
        return false;
    }
    
    @Override
    public String getNotes(ResourceLocation entryId) {
        return notes.getOrDefault(entryId, "");
    }
    
    @Override
    public void setNotes(ResourceLocation entryId, String noteText) {
        if (noteText == null || noteText.isEmpty()) {
            notes.remove(entryId);
        } else {
            notes.put(entryId, noteText);
        }
    }
    
    @Override
    public String getTheme() {
        return theme;
    }
    
    @Override
    public void setTheme(String theme) {
        this.theme = theme;
    }
    
    @Override
    public ResourceLocation[] getRecentHistory(int limit) {
        return recentHistory.stream()
            .limit(limit)
            .toArray(ResourceLocation[]::new);
    }
    
    public void markViewed(ResourceLocation entryId) {
        viewedEntries.add(entryId);
        
        // Update recent history
        recentHistory.remove(entryId); // Remove if already present
        recentHistory.addFirst(entryId); // Add to front
        
        // Keep history size reasonable
        while (recentHistory.size() > 50) {
            recentHistory.removeLast();
        }
    }
}