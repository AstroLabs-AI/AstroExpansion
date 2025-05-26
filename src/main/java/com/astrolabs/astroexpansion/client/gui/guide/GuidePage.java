package com.astrolabs.astroexpansion.client.gui.guide;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * Base class for all guide book pages
 */
public abstract class GuidePage {
    protected final Component title;
    protected final String pageType;
    
    public GuidePage(Component title, String pageType) {
        this.title = title;
        this.pageType = pageType;
    }
    
    public Component getTitle() {
        return title;
    }
    
    public String getPageType() {
        return pageType;
    }
    
    /**
     * Render this page content
     * @param graphics The graphics context
     * @param x Left position of content area
     * @param y Top position of content area
     * @param width Width of content area
     * @param height Height of content area
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     */
    public abstract void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY);
    
    /**
     * Handle mouse clicks on this page
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param button Mouse button (0=left, 1=right, 2=middle)
     * @return true if click was handled
     */
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }
    
    /**
     * Get the height this page needs to render fully
     * @param width Available width
     * @return Required height
     */
    public abstract int getContentHeight(int width);
}
