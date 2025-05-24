package com.astrolabs.astroexpansion.api.guidebook;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * Represents a single page in a guide entry
 */
public interface IGuidePage {
    /**
     * Get the page type identifier
     */
    String getType();
    
    /**
     * Render this page
     */
    void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick);
    
    /**
     * Handle mouse click
     */
    boolean mouseClicked(double mouseX, double mouseY, int button);
    
    /**
     * Handle mouse release
     */
    boolean mouseReleased(double mouseX, double mouseY, int button);
    
    /**
     * Handle mouse drag
     */
    boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY);
    
    /**
     * Handle mouse scroll
     */
    boolean mouseScrolled(double mouseX, double mouseY, double delta);
    
    /**
     * Called when this page is opened
     */
    void onOpened(Player player);
    
    /**
     * Called when this page is closed
     */
    void onClosed(Player player);
    
    /**
     * Update animations and time-based content
     */
    void tick();
    
    /**
     * Get tooltip for current mouse position
     */
    Component getTooltip(int mouseX, int mouseY);
    
    /**
     * Check if this page has interactive elements
     */
    boolean isInteractive();
    
    /**
     * Get page number for display
     */
    int getPageNumber();
}