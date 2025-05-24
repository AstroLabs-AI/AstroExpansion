package com.astrolabs.astroexpansion.common.guidebook.themes;

import com.astrolabs.astroexpansion.AstroExpansion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages available guide book themes
 */
public class GuideThemeManager {
    private static final Map<String, IGuideTheme> THEMES = new HashMap<>();
    
    static {
        // Register default themes
        registerTheme(new DefaultTheme());
        registerTheme(new TechTheme());
        registerTheme(new HologramTheme());
        registerTheme(new AncientTheme());
        registerTheme(new DarkTheme());
    }
    
    public static void registerTheme(IGuideTheme theme) {
        THEMES.put(theme.getId(), theme);
    }
    
    public static IGuideTheme getTheme(String id) {
        return THEMES.getOrDefault(id, THEMES.get("default"));
    }
    
    public static Map<String, IGuideTheme> getAllThemes() {
        return new HashMap<>(THEMES);
    }
    
    // Default theme implementation
    private static class DefaultTheme implements IGuideTheme {
        @Override
        public String getId() { return "default"; }
        
        @Override
        public String getName() { return "Classic"; }
        
        @Override
        public ResourceLocation getBackgroundTexture() {
            return new ResourceLocation(AstroExpansion.MODID, "textures/gui/guide_book.png");
        }
        
        @Override
        public int getPageColor() { return 0xFFF0E8D0; }
        
        @Override
        public int getTextColor() { return 0xFF000000; }
        
        @Override
        public int getHeaderColor() { return 0xFF1A1A1A; }
        
        @Override
        public int getAccentColor() { return 0xFF0088FF; }
        
        @Override
        public int getButtonColor() { return 0xFF888888; }
        
        @Override
        public int getButtonHoverColor() { return 0xFFAAAAAA; }
        
        @Override
        public boolean useCustomFont() { return false; }
        
        @Override
        public ResourceLocation getCustomFont() { return null; }
        
        @Override
        public ResourceLocation getPageTurnSound() {
            return SoundEvents.BOOK_PAGE_TURN.getLocation();
        }
        
        @Override
        public boolean hasParticles() { return false; }
        
        @Override
        public ParticleType getParticleType() { return ParticleType.NONE; }
    }
    
    // Tech/futuristic theme
    private static class TechTheme implements IGuideTheme {
        @Override
        public String getId() { return "tech"; }
        
        @Override
        public String getName() { return "Technological"; }
        
        @Override
        public ResourceLocation getBackgroundTexture() {
            return new ResourceLocation(AstroExpansion.MODID, "textures/gui/guide_book_tech.png");
        }
        
        @Override
        public int getPageColor() { return 0xFF1A1A2E; }
        
        @Override
        public int getTextColor() { return 0xFF00FF88; }
        
        @Override
        public int getHeaderColor() { return 0xFF00FFFF; }
        
        @Override
        public int getAccentColor() { return 0xFFFF00FF; }
        
        @Override
        public int getButtonColor() { return 0xFF0F3460; }
        
        @Override
        public int getButtonHoverColor() { return 0xFF16537E; }
        
        @Override
        public boolean useCustomFont() { return true; }
        
        @Override
        public ResourceLocation getCustomFont() {
            return new ResourceLocation(AstroExpansion.MODID, "tech");
        }
        
        @Override
        public ResourceLocation getPageTurnSound() {
            return new ResourceLocation(AstroExpansion.MODID, "tech_beep");
        }
        
        @Override
        public boolean hasParticles() { return true; }
        
        @Override
        public ParticleType getParticleType() { return ParticleType.TECH_SPARKS; }
    }
    
    // Holographic theme
    private static class HologramTheme implements IGuideTheme {
        @Override
        public String getId() { return "hologram"; }
        
        @Override
        public String getName() { return "Holographic"; }
        
        @Override
        public ResourceLocation getBackgroundTexture() {
            return new ResourceLocation(AstroExpansion.MODID, "textures/gui/guide_book_hologram.png");
        }
        
        @Override
        public int getPageColor() { return 0x88000033; }
        
        @Override
        public int getTextColor() { return 0xFF00FFFF; }
        
        @Override
        public int getHeaderColor() { return 0xFFFFFFFF; }
        
        @Override
        public int getAccentColor() { return 0xFFFF00FF; }
        
        @Override
        public int getButtonColor() { return 0x88003366; }
        
        @Override
        public int getButtonHoverColor() { return 0xAA0055AA; }
        
        @Override
        public boolean useCustomFont() { return true; }
        
        @Override
        public ResourceLocation getCustomFont() {
            return new ResourceLocation(AstroExpansion.MODID, "hologram");
        }
        
        @Override
        public ResourceLocation getPageTurnSound() {
            return new ResourceLocation(AstroExpansion.MODID, "hologram_flicker");
        }
        
        @Override
        public boolean hasParticles() { return true; }
        
        @Override
        public ParticleType getParticleType() { return ParticleType.HOLOGRAM; }
    }
    
    // Ancient/mystical theme
    private static class AncientTheme implements IGuideTheme {
        @Override
        public String getId() { return "ancient"; }
        
        @Override
        public String getName() { return "Ancient Tome"; }
        
        @Override
        public ResourceLocation getBackgroundTexture() {
            return new ResourceLocation(AstroExpansion.MODID, "textures/gui/guide_book_ancient.png");
        }
        
        @Override
        public int getPageColor() { return 0xFFD4C4A0; }
        
        @Override
        public int getTextColor() { return 0xFF3D2817; }
        
        @Override
        public int getHeaderColor() { return 0xFF8B4513; }
        
        @Override
        public int getAccentColor() { return 0xFFB8860B; }
        
        @Override
        public int getButtonColor() { return 0xFF654321; }
        
        @Override
        public int getButtonHoverColor() { return 0xFF8B6914; }
        
        @Override
        public boolean useCustomFont() { return true; }
        
        @Override
        public ResourceLocation getCustomFont() {
            return new ResourceLocation(AstroExpansion.MODID, "ancient");
        }
        
        @Override
        public ResourceLocation getPageTurnSound() {
            return new ResourceLocation(AstroExpansion.MODID, "ancient_page");
        }
        
        @Override
        public boolean hasParticles() { return true; }
        
        @Override
        public ParticleType getParticleType() { return ParticleType.MAGIC_DUST; }
    }
    
    // Dark mode theme
    private static class DarkTheme implements IGuideTheme {
        @Override
        public String getId() { return "dark"; }
        
        @Override
        public String getName() { return "Dark Mode"; }
        
        @Override
        public ResourceLocation getBackgroundTexture() {
            return new ResourceLocation(AstroExpansion.MODID, "textures/gui/guide_book_dark.png");
        }
        
        @Override
        public int getPageColor() { return 0xFF1E1E1E; }
        
        @Override
        public int getTextColor() { return 0xFFE0E0E0; }
        
        @Override
        public int getHeaderColor() { return 0xFFFFFFFF; }
        
        @Override
        public int getAccentColor() { return 0xFF00AAFF; }
        
        @Override
        public int getButtonColor() { return 0xFF2D2D2D; }
        
        @Override
        public int getButtonHoverColor() { return 0xFF3D3D3D; }
        
        @Override
        public boolean useCustomFont() { return false; }
        
        @Override
        public ResourceLocation getCustomFont() { return null; }
        
        @Override
        public ResourceLocation getPageTurnSound() {
            return SoundEvents.BOOK_PAGE_TURN.getLocation();
        }
        
        @Override
        public boolean hasParticles() { return true; }
        
        @Override
        public ParticleType getParticleType() { return ParticleType.STARS; }
    }
}