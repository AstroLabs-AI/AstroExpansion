package com.astrolabs.astroexpansion.common.guidebook.themes;

import net.minecraft.resources.ResourceLocation;

/**
 * Interface for guide book themes
 */
public interface IGuideTheme {
    /**
     * Get the unique ID of this theme
     */
    String getId();
    
    /**
     * Get the display name
     */
    String getName();
    
    /**
     * Get the main background texture
     */
    ResourceLocation getBackgroundTexture();
    
    /**
     * Get page background color
     */
    int getPageColor();
    
    /**
     * Get primary text color
     */
    int getTextColor();
    
    /**
     * Get header text color
     */
    int getHeaderColor();
    
    /**
     * Get link/highlight color
     */
    int getAccentColor();
    
    /**
     * Get button color
     */
    int getButtonColor();
    
    /**
     * Get button hover color
     */
    int getButtonHoverColor();
    
    /**
     * Whether to use custom fonts
     */
    boolean useCustomFont();
    
    /**
     * Get the font to use (if custom)
     */
    ResourceLocation getCustomFont();
    
    /**
     * Get page turn sound
     */
    ResourceLocation getPageTurnSound();
    
    /**
     * Whether to show particle effects
     */
    boolean hasParticles();
    
    /**
     * Get particle type for this theme
     */
    ParticleType getParticleType();
    
    enum ParticleType {
        NONE,
        STARS,
        TECH_SPARKS,
        MAGIC_DUST,
        HOLOGRAM,
        PAGES
    }
}