package com.astrolabs.astroexpansion.client.gui.guide;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * A page that spotlights a specific item with description
 */
public class SpotlightPage extends GuidePage {
    private final ItemStack spotlightItem;
    private final Component description;
    private List<FormattedCharSequence> wrappedText;
    private int cachedWidth = -1;
    
    public SpotlightPage(Component title, ItemStack spotlightItem, Component description) {
        super(title, "spotlight");
        this.spotlightItem = spotlightItem;
        this.description = description;
    }
    
    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        
        // Cache wrapped text if width changed
        if (cachedWidth != width - 80) { // Account for item icon space
            wrappedText = font.split(description, width - 80);
            cachedWidth = width - 80;
        }
        
        // Render title
        graphics.drawString(font, title, x + 10, y + 5, 0x2D2D2D, false);
        
        // Render item icon (large)
        if (!spotlightItem.isEmpty()) {
            graphics.pose().pushPose();
            graphics.pose().scale(2.0f, 2.0f, 1.0f);
            graphics.renderItem(spotlightItem, (x + 10) / 2, (y + 25) / 2);
            graphics.pose().popPose();
        }
        
        // Render description text next to item
        int textX = x + 50; // Space for 32x32 item icon
        int currentY = y + 25;
        for (FormattedCharSequence line : wrappedText) {
            if (currentY >= y + height) break;
            graphics.drawString(font, line, textX, currentY, 0x3F3F3F, false);
            currentY += font.lineHeight + 2;
        }
    }
    
    @Override
    public int getContentHeight(int width) {
        Font font = Minecraft.getInstance().font;
        if (cachedWidth != width - 80) {
            wrappedText = font.split(description, width - 80);
            cachedWidth = width - 80;
        }
        
        int textHeight = wrappedText.size() * (font.lineHeight + 2);
        return Math.max(65, 30 + textHeight); // At least 65px for item icon + title
    }
    
    public ItemStack getSpotlightItem() {
        return spotlightItem;
    }
    
    public Component getDescription() {
        return description;
    }
}
