package com.astrolabs.astroexpansion.common.guidebook.pages;

import com.astrolabs.astroexpansion.api.guidebook.IGuidePage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * A simple text page with markdown-like formatting support
 */
public class TextPage implements IGuidePage {
    private final String content;
    private final int pageNumber;
    private List<FormattedCharSequence> cachedLines;
    private int cachedWidth = -1;
    
    public TextPage(String content, int pageNumber) {
        this.content = content;
        this.pageNumber = pageNumber;
    }
    
    @Override
    public String getType() {
        return "text";
    }
    
    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        
        // Cache formatted lines if width changed
        if (cachedWidth != width) {
            cachedWidth = width;
            Component formatted = parseMarkdown(content);
            cachedLines = font.split(formatted, width - 10);
        }
        
        // Render text
        int yOffset = y + 5;
        for (FormattedCharSequence line : cachedLines) {
            if (yOffset + 9 > y + height) break;
            graphics.drawString(font, line, x + 5, yOffset, 0x000000, false);
            yOffset += 10;
        }
    }
    
    private Component parseMarkdown(String text) {
        // Simple markdown parser
        Component result = Component.empty();
        String[] parts = text.split("\n");
        
        for (String part : parts) {
            if (part.startsWith("# ")) {
                // Header
                result = result.copy().append(Component.literal(part.substring(2))
                    .setStyle(Style.EMPTY.withBold(true).withUnderlined(true)));
            } else if (part.startsWith("## ")) {
                // Subheader
                result = result.copy().append(Component.literal(part.substring(3))
                    .setStyle(Style.EMPTY.withBold(true)));
            } else if (part.startsWith("**") && part.endsWith("**")) {
                // Bold
                result = result.copy().append(Component.literal(part.substring(2, part.length() - 2))
                    .setStyle(Style.EMPTY.withBold(true)));
            } else if (part.startsWith("*") && part.endsWith("*")) {
                // Italic
                result = result.copy().append(Component.literal(part.substring(1, part.length() - 1))
                    .setStyle(Style.EMPTY.withItalic(true)));
            } else {
                // Normal text
                result = result.copy().append(Component.literal(part));
            }
            result = result.copy().append(Component.literal("\n"));
        }
        
        return result;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return false;
    }
    
    @Override
    public void onOpened(Player player) {
    }
    
    @Override
    public void onClosed(Player player) {
    }
    
    @Override
    public void tick() {
    }
    
    @Override
    public Component getTooltip(int mouseX, int mouseY) {
        return null;
    }
    
    @Override
    public boolean isInteractive() {
        return false;
    }
    
    @Override
    public int getPageNumber() {
        return pageNumber;
    }
}