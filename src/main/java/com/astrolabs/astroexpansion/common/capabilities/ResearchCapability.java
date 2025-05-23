package com.astrolabs.astroexpansion.common.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.HashMap;
import java.util.Map;

@AutoRegisterCapability
public class ResearchCapability {
    private int totalResearchPoints = 0;
    private Map<String, Boolean> unlockedResearch = new HashMap<>();
    private Map<String, Integer> researchProgress = new HashMap<>();
    
    // Research categories
    public static final String BASIC_MACHINES = "basic_machines";
    public static final String ADVANCED_ENERGY = "advanced_energy";
    public static final String DIGITAL_STORAGE = "digital_storage";
    public static final String AUTOMATION = "automation";
    public static final String FUSION_POWER = "fusion_power";
    public static final String QUANTUM_COMPUTING = "quantum_computing";
    public static final String SPACE_TECH = "space_tech";
    public static final String LUNAR_TECH = "lunar_tech";
    public static final String ALIEN_TECH = "alien_tech";
    
    public int getTotalResearchPoints() {
        return totalResearchPoints;
    }
    
    public void addResearchPoints(int points) {
        this.totalResearchPoints += points;
    }
    
    public boolean spendResearchPoints(int points) {
        if (totalResearchPoints >= points) {
            totalResearchPoints -= points;
            return true;
        }
        return false;
    }
    
    public boolean isResearchUnlocked(String researchId) {
        return unlockedResearch.getOrDefault(researchId, false);
    }
    
    public void unlockResearch(String researchId) {
        unlockedResearch.put(researchId, true);
    }
    
    public int getResearchProgress(String researchId) {
        return researchProgress.getOrDefault(researchId, 0);
    }
    
    public void addResearchProgress(String researchId, int progress) {
        researchProgress.put(researchId, getResearchProgress(researchId) + progress);
    }
    
    public void saveNBTData(CompoundTag compound) {
        compound.putInt("totalResearchPoints", totalResearchPoints);
        
        CompoundTag unlockedTag = new CompoundTag();
        for (Map.Entry<String, Boolean> entry : unlockedResearch.entrySet()) {
            unlockedTag.putBoolean(entry.getKey(), entry.getValue());
        }
        compound.put("unlockedResearch", unlockedTag);
        
        CompoundTag progressTag = new CompoundTag();
        for (Map.Entry<String, Integer> entry : researchProgress.entrySet()) {
            progressTag.putInt(entry.getKey(), entry.getValue());
        }
        compound.put("researchProgress", progressTag);
    }
    
    public void loadNBTData(CompoundTag compound) {
        totalResearchPoints = compound.getInt("totalResearchPoints");
        
        unlockedResearch.clear();
        CompoundTag unlockedTag = compound.getCompound("unlockedResearch");
        for (String key : unlockedTag.getAllKeys()) {
            unlockedResearch.put(key, unlockedTag.getBoolean(key));
        }
        
        researchProgress.clear();
        CompoundTag progressTag = compound.getCompound("researchProgress");
        for (String key : progressTag.getAllKeys()) {
            researchProgress.put(key, progressTag.getInt(key));
        }
    }
    
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        saveNBTData(compound);
        return compound;
    }
}