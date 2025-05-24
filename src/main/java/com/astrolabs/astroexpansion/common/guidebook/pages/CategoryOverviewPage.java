package com.astrolabs.astroexpansion.common.guidebook.pages;

import com.astrolabs.astroexpansion.api.guidebook.IGuideBook;
import com.astrolabs.astroexpansion.api.guidebook.IGuideCategory;
import com.astrolabs.astroexpansion.api.guidebook.IGuideEntry;
import com.astrolabs.astroexpansion.api.guidebook.IGuidePage;
import com.astrolabs.astroexpansion.api.guidebook.IGuideProgress;
import com.astrolabs.astroexpansion.client.gui.screens.GuideBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class CategoryOverviewPage implements IGuidePage {
    private final IGuideCategory category;
    private final IGuideBook guideBook;
    private final List<EntryDisplay> entryDisplays = new ArrayList<>();
    
    private static class EntryDisplay {
        final IGuideEntry entry;
        final int x, y, width = 180, height = 24;
        final boolean unlocked;
        
        EntryDisplay(IGuideEntry entry, int x, int y, boolean unlocked) {
            this.entry = entry;
            this.x = x;
            this.y = y;
            this.unlocked = unlocked;
        }
        
        boolean isHovered(int mouseX, int mouseY) {
            return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        }
    }
    
    public CategoryOverviewPage(IGuideCategory category, IGuideBook guideBook) {
        this.category = category;
        this.guideBook = guideBook;
    }
    
    @Override
    public String getType() {
        return "category_overview";
    }
    
    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        Player player = Minecraft.getInstance().player;
        IGuideProgress progress = guideBook.getProgress(player);
        
        // Render category title
        graphics.drawString(font, category.getName(), x + 10, y + 10, category.getColor(), false);
        
        // Render category description
        String desc = category.getDescription().getString();
        List<String> descLines = wrapText(desc, width - 20, font);
        int descY = y + 25;
        for (String line : descLines) {
            graphics.drawString(font, line, x + 10, descY, 0x333333, false);
            descY += 10;
        }
        
        // Clear and rebuild entry displays
        entryDisplays.clear();
        
        // Render entries
        int entryY = descY + 15;
        int index = 0;
        for (IGuideEntry entry : category.getEntries()) {
            if (entryY + 24 > y + height - 10) break; // Don't render off page
            
            boolean unlocked = progress.getUnlockedEntries().contains(entry.getId());
            EntryDisplay display = new EntryDisplay(entry, x + 10, entryY, unlocked);
            entryDisplays.add(display);
            
            // Background
            int bgColor = display.isHovered(mouseX, mouseY) ? 0x22FFFFFF : 0x11000000;
            graphics.fill(display.x, display.y, display.x + display.width, display.y + display.height, bgColor);
            
            // Icon
            graphics.renderItem(entry.getIcon(), display.x + 2, display.y + 4);
            
            // Title
            Component title = entry.getName();
            if (!unlocked) {
                title = Component.literal("???").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.OBFUSCATED);
            }
            graphics.drawString(font, title, display.x + 24, display.y + 8, unlocked ? 0x000000 : 0x666666, false);
            
            entryY += 26;
            index++;
        }
        
        // Show total entries at bottom
        int unlockedCount = (int) category.getEntries().stream()
            .filter(e -> progress.getUnlockedEntries().contains(e.getId()))
            .count();
        String progressText = unlockedCount + "/" + category.getEntries().size() + " Unlocked";
        graphics.drawString(font, progressText, x + 10, y + height - 20, 0x666666, false);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            for (EntryDisplay display : entryDisplays) {
                if (display.isHovered((int)mouseX, (int)mouseY) && display.unlocked) {
                    // We need access to the screen to show the entry
                    // This will be handled by the screen itself
                    return true;
                }
            }
        }
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
        // Nothing to do
    }
    
    @Override
    public void onClosed(Player player) {
        // Nothing to do
    }
    
    @Override
    public void tick() {
        // No animations
    }
    
    @Override
    public Component getTooltip(int mouseX, int mouseY) {
        for (EntryDisplay display : entryDisplays) {
            if (display.isHovered(mouseX, mouseY)) {
                if (!display.unlocked) {
                    return Component.literal("Locked - Complete prerequisites to unlock");
                }
            }
        }
        return null;
    }
    
    @Override
    public boolean isInteractive() {
        return true;
    }
    
    @Override
    public int getPageNumber() {
        return 0; // Category overview is always page 0
    }
    
    public IGuideEntry getClickedEntry(int mouseX, int mouseY) {
        for (EntryDisplay display : entryDisplays) {
            if (display.isHovered(mouseX, mouseY) && display.unlocked) {
                return display.entry;
            }
        }
        return null;
    }
    
    private List<String> wrapText(String text, int maxWidth, Font font) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            if (currentLine.length() > 0) {
                String testLine = currentLine + " " + word;
                if (font.width(testLine) > maxWidth) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    currentLine.append(" ").append(word);
                }
            } else {
                currentLine.append(word);
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines;
    }
}