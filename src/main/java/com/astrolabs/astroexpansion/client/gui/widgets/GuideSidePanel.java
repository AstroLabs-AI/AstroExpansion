package com.astrolabs.astroexpansion.client.gui.widgets;

import com.astrolabs.astroexpansion.api.guidebook.IGuideCategory;
import com.astrolabs.astroexpansion.api.guidebook.IGuideEntry;
import com.astrolabs.astroexpansion.client.gui.screens.GuideBookScreen;
import com.astrolabs.astroexpansion.common.guidebook.AstroGuideBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

/**
 * Side panel showing categories, bookmarks, and search results
 */
public class GuideSidePanel extends AbstractWidget {
    private final GuideBookScreen parent;
    private final List<PanelEntry> entries = new ArrayList<>();
    private int scrollOffset = 0;
    private boolean showingSearch = false;
    
    public GuideSidePanel(int x, int y, int width, int height, GuideBookScreen parent) {
        super(x, y, width, height, Component.empty());
        this.parent = parent;
        showCategories();
    }
    
    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Background - more transparent to not obscure book
        graphics.fill(getX(), getY(), getX() + width, getY() + height, 0x88000000);
        graphics.fill(getX() + 1, getY() + 1, getX() + width - 1, getY() + height - 1, 0x88222222);
        
        // Title
        String title = showingSearch ? "Search Results" : "Categories";
        graphics.drawCenteredString(Minecraft.getInstance().font, title, 
            getX() + width / 2, getY() + 5, 0xFFFFFF);
        
        // Render entries
        int y = getY() + 20;
        int visibleEntries = (height - 25) / 20;
        
        for (int i = scrollOffset; i < Math.min(scrollOffset + visibleEntries, entries.size()); i++) {
            PanelEntry entry = entries.get(i);
            boolean hovered = isMouseOver(mouseX, mouseY) && 
                mouseY >= y && mouseY < y + 20;
            
            entry.render(graphics, getX() + 5, y, width - 10, hovered);
            y += 20;
        }
        
        // Scroll bar
        if (entries.size() > visibleEntries) {
            int scrollbarHeight = Math.max(20, (visibleEntries * height) / entries.size());
            int scrollbarY = getY() + 20 + ((height - 25 - scrollbarHeight) * scrollOffset) / (entries.size() - visibleEntries);
            graphics.fill(getX() + width - 5, scrollbarY, getX() + width - 2, scrollbarY + scrollbarHeight, 0xFFAAAAAA);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) return false;
        
        int y = getY() + 20;
        int visibleEntries = (height - 25) / 20;
        
        for (int i = scrollOffset; i < Math.min(scrollOffset + visibleEntries, entries.size()); i++) {
            if (mouseY >= y && mouseY < y + 20) {
                entries.get(i).onClick();
                return true;
            }
            y += 20;
        }
        
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isMouseOver(mouseX, mouseY)) return false;
        
        int visibleEntries = (height - 25) / 20;
        int maxScroll = Math.max(0, entries.size() - visibleEntries);
        
        scrollOffset = Mth.clamp(scrollOffset - (int)delta, 0, maxScroll);
        return true;
    }
    
    public void showCategories() {
        entries.clear();
        showingSearch = false;
        scrollOffset = 0;
        
        for (IGuideCategory category : AstroGuideBook.getInstance().getCategories()) {
            entries.add(new CategoryEntry(category));
        }
    }
    
    public void showSearchResults(List<IGuideEntry> results) {
        entries.clear();
        showingSearch = true;
        scrollOffset = 0;
        
        for (IGuideEntry entry : results) {
            entries.add(new SearchResultEntry(entry));
        }
        
        if (entries.isEmpty()) {
            entries.add(new MessageEntry("No results found"));
        }
    }
    
    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }
    
    // Inner classes for different entry types
    private abstract class PanelEntry {
        abstract void render(GuiGraphics graphics, int x, int y, int width, boolean hovered);
        abstract void onClick();
    }
    
    private class CategoryEntry extends PanelEntry {
        private final IGuideCategory category;
        
        CategoryEntry(IGuideCategory category) {
            this.category = category;
        }
        
        @Override
        void render(GuiGraphics graphics, int x, int y, int width, boolean hovered) {
            int color = hovered ? 0xFFFFFF : 0xAAAAAA;
            graphics.drawString(Minecraft.getInstance().font, category.getName(), x + 20, y + 5, color, false);
            
            // Draw icon
            graphics.renderItem(category.getIcon(), x, y);
        }
        
        @Override
        void onClick() {
            parent.showCategory(category);
        }
    }
    
    private class SearchResultEntry extends PanelEntry {
        private final IGuideEntry entry;
        
        SearchResultEntry(IGuideEntry entry) {
            this.entry = entry;
        }
        
        @Override
        void render(GuiGraphics graphics, int x, int y, int width, boolean hovered) {
            int color = hovered ? 0xFFFFFF : 0xAAAAAA;
            graphics.drawString(Minecraft.getInstance().font, entry.getName(), x + 20, y + 5, color, false);
            
            // Draw icon
            graphics.renderItem(entry.getIcon(), x, y);
        }
        
        @Override
        void onClick() {
            parent.showEntry(entry);
        }
    }
    
    private class MessageEntry extends PanelEntry {
        private final String message;
        
        MessageEntry(String message) {
            this.message = message;
        }
        
        @Override
        void render(GuiGraphics graphics, int x, int y, int width, boolean hovered) {
            graphics.drawCenteredString(Minecraft.getInstance().font, message, 
                x + width / 2, y + 5, 0x666666);
        }
        
        @Override
        void onClick() {
        }
    }
}