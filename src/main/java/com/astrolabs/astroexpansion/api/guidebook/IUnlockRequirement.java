package com.astrolabs.astroexpansion.api.guidebook;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * Represents requirements to unlock a guide entry
 */
public interface IUnlockRequirement {
    /**
     * Check if the player meets this requirement
     */
    boolean test(Player player);
    
    /**
     * Get a description of this requirement
     */
    Component getDescription();
    
    /**
     * Get progress towards meeting this requirement (0.0 - 1.0)
     */
    float getProgress(Player player);
    
    /**
     * Get the type of requirement for icons/display
     */
    RequirementType getType();
    
    enum RequirementType {
        ADVANCEMENT,
        ITEM,
        RESEARCH,
        DIMENSION,
        STRUCTURE,
        ENERGY,
        CUSTOM
    }
}