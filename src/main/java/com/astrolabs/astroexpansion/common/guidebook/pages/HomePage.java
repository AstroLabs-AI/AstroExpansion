package com.astrolabs.astroexpansion.common.guidebook.pages;

import com.astrolabs.astroexpansion.api.guidebook.IGuidePage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * Home page for the guide book
 */
public class HomePage implements IGuidePage {
    @Override
    public String getType() {
        return "home";
    }
    
    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        // Title
        Component title = Component.literal("AstroExpansion Guide");
        int titleWidth = Minecraft.getInstance().font.width(title);
        graphics.drawString(Minecraft.getInstance().font, title, 
            x + width / 2 - titleWidth / 2, y + 10, 0x000000, false);
        
        // Welcome message
        String[] lines = {
            "Welcome to AstroExpansion!",
            "",
            "This guide will help you through",
            "your journey to the stars.",
            "",
            "Select a category from the left",
            "to begin exploring.",
            "",
            "Tips:",
            "• Use the search bar to find items",
            "• Bookmark important pages with ★",
            "• Press arrow keys to navigate",
            "• Sneak + Right-click blocks",
            "  with this book for quick info"
        };
        
        int lineY = y + 35;
        for (String line : lines) {
            if (line.startsWith("•")) {
                graphics.drawString(Minecraft.getInstance().font, line, x + 15, lineY, 0x0088FF, false);
            } else if (line.equals("Tips:")) {
                graphics.drawString(Minecraft.getInstance().font, line, x + 10, lineY, 0x000000, false);
            } else {
                graphics.drawString(Minecraft.getInstance().font, line, x + 10, lineY, 0x333333, false);
            }
            lineY += 10;
        }
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
    public void onOpened(Player player) {}
    
    @Override
    public void onClosed(Player player) {}
    
    @Override
    public void tick() {}
    
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
        return 1;
    }
}