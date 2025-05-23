package com.astrolabs.astroexpansion.common.research;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class ResearchManager {
    private static final Map<String, Research> RESEARCH_REGISTRY = new HashMap<>();
    private static final String NBT_KEY = "astroexpansion_research";
    
    static {
        registerDefaultResearch();
    }
    
    private static void registerDefaultResearch() {
        // Basic research
        register(new Research("basic_processing", "Basic Processing", 
            "Learn to process raw materials efficiently", 100, Collections.emptyList()));
        
        register(new Research("energy_systems", "Energy Systems", 
            "Understand energy generation and storage", 200, List.of("basic_processing")));
        
        register(new Research("automation", "Automation", 
            "Master automated crafting systems", 300, List.of("energy_systems")));
        
        register(new Research("advanced_materials", "Advanced Materials", 
            "Develop stronger alloys and composites", 400, List.of("automation")));
        
        register(new Research("space_technology", "Space Technology", 
            "Begin preparations for space exploration", 500, List.of("advanced_materials")));
        
        // Specialized branches
        register(new Research("fusion_power", "Fusion Power", 
            "Harness the power of nuclear fusion", 600, List.of("energy_systems")));
        
        register(new Research("quantum_computing", "Quantum Computing", 
            "Develop quantum processors", 800, List.of("automation", "advanced_materials")));
        
        register(new Research("terraforming", "Terraforming", 
            "Transform hostile worlds", 1000, List.of("space_technology", "fusion_power")));
    }
    
    public static void register(Research research) {
        RESEARCH_REGISTRY.put(research.getId(), research);
    }
    
    public static Research getResearch(String id) {
        return RESEARCH_REGISTRY.get(id);
    }
    
    public static Collection<Research> getAllResearch() {
        return RESEARCH_REGISTRY.values();
    }
    
    public static Set<String> getCompletedResearch(Player player) {
        CompoundTag playerData = player.getPersistentData();
        if (!playerData.contains(NBT_KEY)) {
            return new HashSet<>();
        }
        
        Set<String> completed = new HashSet<>();
        ListTag list = playerData.getList(NBT_KEY, 8); // STRING type
        for (int i = 0; i < list.size(); i++) {
            completed.add(list.getString(i));
        }
        return completed;
    }
    
    public static void completeResearch(Player player, String researchId) {
        Set<String> completed = getCompletedResearch(player);
        completed.add(researchId);
        
        CompoundTag playerData = player.getPersistentData();
        ListTag list = new ListTag();
        for (String id : completed) {
            list.add(StringTag.valueOf(id));
        }
        playerData.put(NBT_KEY, list);
    }
    
    public static boolean hasResearch(Player player, String researchId) {
        return getCompletedResearch(player).contains(researchId);
    }
    
    public static boolean canResearch(Player player, String researchId) {
        Research research = getResearch(researchId);
        if (research == null || hasResearch(player, researchId)) {
            return false;
        }
        
        Set<String> completed = getCompletedResearch(player);
        for (String prerequisite : research.getPrerequisites()) {
            if (!completed.contains(prerequisite)) {
                return false;
            }
        }
        return true;
    }
    
    public static List<Research> getAvailableResearch(Player player) {
        List<Research> available = new ArrayList<>();
        for (Research research : getAllResearch()) {
            if (canResearch(player, research.getId())) {
                available.add(research);
            }
        }
        return available;
    }
}