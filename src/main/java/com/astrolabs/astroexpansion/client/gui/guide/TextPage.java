package com.astrolabs.astroexpansion.client.gui.guide;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

/**
 * A page that displays formatted text content
 */
public class TextPage extends GuidePage {
    private final Component content;
    private List<FormattedCharSequence> wrappedText;
    private int cachedWidth = -1;
    
    public TextPage(Component title, Component content) {
        super(title, "text");
        this.content = content;
    }
    
    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        
        // Cache wrapped text if width changed
        if (cachedWidth != width) {
            wrappedText = font.split(content, width - 20); // 10px margin on each side
            cachedWidth = width;
        }
        
        // Render title
        graphics.drawString(font, title, x + 10, y + 5, 0x2D2D2D, false);
        
        // Render content
        int currentY = y + 25;
        for (FormattedCharSequence line : wrappedText) {
            if (currentY >= y + height) break; // Don't render outside bounds
            graphics.drawString(font, line, x + 10, currentY, 0x3F3F3F, false);
            currentY += font.lineHeight + 2;
        }
    }
    
    @Override
    public int getContentHeight(int width) {
        Font font = Minecraft.getInstance().font;
        if (cachedWidth != width) {
            wrappedText = font.split(content, width - 20);
            cachedWidth = width;
        }
        
        return 30 + (wrappedText.size() * (font.lineHeight + 2)); // Title + content
    }
    
    public Component getContent() {
        return content;
    }
}
