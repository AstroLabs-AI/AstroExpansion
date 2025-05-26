package com.astrolabs.astroexpansion.client.gui.guide;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import net.minecraft.client.gui.components.Button;

/**
 * Main GUI screen for the AstroExpansion guide book
 */
public class AstroGuideScreen extends Screen {
    
    private static final int GUI_WIDTH = 256;
    private static final int GUI_HEIGHT = 192;
    private static final int CATEGORY_PANEL_WIDTH = 80;
    private static final int ENTRY_PANEL_WIDTH = 70;
    private static final int CONTENT_PANEL_WIDTH = GUI_WIDTH - CATEGORY_PANEL_WIDTH - ENTRY_PANEL_WIDTH - 6;
    
    private int guiLeft;
    private int guiTop;
    
    // Navigation state
    private GuideCategory selectedCategory;
    private GuideEntry selectedEntry;
    private int currentPage = 0;
    
    // UI Components
    private Button homeButton;
    private Button backButton;
    private Button forwardButton;
    private Button prevPageButton;
    private Button nextPageButton;
    
    // Scroll positions
    private int categoryScrollOffset = 0;
    private int entryScrollOffset = 0;
    private int contentScrollOffset = 0;
    
    public AstroGuideScreen() {
        super(Component.literal("Astro Guide"));
        GuideContentRegistry.initialize();
        
        // Select first category by default
        List<GuideCategory> categories = GuideContentRegistry.getCategories();
        if (!categories.isEmpty()) {
            selectedCategory = categories.get(0);
            if (!selectedCategory.getEntries().isEmpty()) {
                selectedEntry = selectedCategory.getEntries().get(0);
            }
        }
    }
    
    @Override
    protected void init() {
        super.init();
        
        this.guiLeft = (this.width - GUI_WIDTH) / 2;
        this.guiTop = (this.height - GUI_HEIGHT) / 2;
        
        // Navigation buttons
        this.homeButton = Button.builder(Component.literal("Home"), button -> goHome())
                .bounds(guiLeft + 5, guiTop + GUI_HEIGHT - 25, 40, 20)
                .build();
        this.addRenderableWidget(homeButton);
        
        this.backButton = Button.builder(Component.literal("<"), button -> goBack())
                .bounds(guiLeft + 50, guiTop + GUI_HEIGHT - 25, 20, 20)
                .build();
        this.addRenderableWidget(backButton);
        
        this.forwardButton = Button.builder(Component.literal(">"), button -> goForward())
                .bounds(guiLeft + 75, guiTop + GUI_HEIGHT - 25, 20, 20)
                .build();
        this.addRenderableWidget(forwardButton);
        
        // Page navigation buttons
        this.prevPageButton = Button.builder(Component.literal("Prev"), button -> previousPage())
                .bounds(guiLeft + GUI_WIDTH - 90, guiTop + GUI_HEIGHT - 25, 40, 20)
                .build();
        this.addRenderableWidget(prevPageButton);
        
        this.nextPageButton = Button.builder(Component.literal("Next"), button -> nextPage())
                .bounds(guiLeft + GUI_WIDTH - 45, guiTop + GUI_HEIGHT - 25, 40, 20)
                .build();
        this.addRenderableWidget(nextPageButton);
        
        updateButtonStates();
    }
      @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        
        // Draw main background (solid color for now)
        graphics.fill(guiLeft, guiTop, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT, 0xFF2D2D2D);
        graphics.fill(guiLeft + 1, guiTop + 1, guiLeft + GUI_WIDTH - 1, guiTop + GUI_HEIGHT - 1, 0xFF3D3D3D);
        
        // Draw title
        Component title = Component.literal("AstroExpansion Guide");
        int titleWidth = font.width(title);
        graphics.drawString(font, title, guiLeft + (GUI_WIDTH - titleWidth) / 2, guiTop + 5, 0xFFFFFF, false);
        
        // Draw panels
        renderCategoryPanel(graphics, mouseX, mouseY);
        renderEntryPanel(graphics, mouseX, mouseY);
        renderContentPanel(graphics, mouseX, mouseY);
        
        super.render(graphics, mouseX, mouseY, partialTick);
        
        // Draw tooltips
        renderTooltips(graphics, mouseX, mouseY);
    }
    
    private void renderCategoryPanel(GuiGraphics graphics, int mouseX, int mouseY) {
        int panelX = guiLeft + 2;
        int panelY = guiTop + 15;
        int panelHeight = GUI_HEIGHT - 45; // Leave space for navigation buttons
        
        // Panel background
        graphics.fill(panelX, panelY, panelX + CATEGORY_PANEL_WIDTH, panelY + panelHeight, 0xFF404040);
        graphics.fill(panelX + 1, panelY + 1, panelX + CATEGORY_PANEL_WIDTH - 1, panelY + panelHeight - 1, 0xFF606060);
        
        // Title
        graphics.drawString(font, "Categories", panelX + 5, panelY + 5, 0xFFFFFF, false);
        
        // Category list
        List<GuideCategory> categories = GuideContentRegistry.getCategories();
        int currentY = panelY + 20 - categoryScrollOffset;
        
        for (int i = 0; i < categories.size(); i++) {
            GuideCategory category = categories.get(i);
            
            if (currentY >= panelY + panelHeight) break;
            if (currentY + 15 > panelY + 20) { // Only render visible items
                
                boolean isSelected = category == selectedCategory;
                boolean isHovered = mouseX >= panelX + 2 && mouseX <= panelX + CATEGORY_PANEL_WIDTH - 2 &&
                                  mouseY >= currentY && mouseY <= currentY + 15;
                
                // Button background
                int bgColor = isSelected ? 0xFF4A90E2 : (isHovered ? 0xFF5A5A5A : 0xFF505050);
                graphics.fill(panelX + 2, currentY, panelX + CATEGORY_PANEL_WIDTH - 2, currentY + 15, bgColor);
                
                // Category name
                String name = category.getName().getString();
                if (name.length() > 10) {
                    name = name.substring(0, 8) + "...";
                }
                graphics.drawString(font, name, panelX + 5, currentY + 4, 0xFFFFFF, false);
            }
            
            currentY += 16;
        }
    }
    
    private void renderEntryPanel(GuiGraphics graphics, int mouseX, int mouseY) {
        int panelX = guiLeft + CATEGORY_PANEL_WIDTH + 3;
        int panelY = guiTop + 15;
        int panelHeight = GUI_HEIGHT - 45;
        
        // Panel background
        graphics.fill(panelX, panelY, panelX + ENTRY_PANEL_WIDTH, panelY + panelHeight, 0xFF404040);
        graphics.fill(panelX + 1, panelY + 1, panelX + ENTRY_PANEL_WIDTH - 1, panelY + panelHeight - 1, 0xFF606060);
        
        if (selectedCategory == null) {
            graphics.drawString(font, "No Category", panelX + 5, panelY + 5, 0xFFFFFF, false);
            return;
        }
        
        // Title
        graphics.drawString(font, "Entries", panelX + 5, panelY + 5, 0xFFFFFF, false);
        
        // Entry list
        List<GuideEntry> entries = selectedCategory.getEntries();
        int currentY = panelY + 20 - entryScrollOffset;
        
        for (GuideEntry entry : entries) {
            if (currentY >= panelY + panelHeight) break;
            if (currentY + 15 > panelY + 20) {
                
                boolean isSelected = entry == selectedEntry;
                boolean isHovered = mouseX >= panelX + 2 && mouseX <= panelX + ENTRY_PANEL_WIDTH - 2 &&
                                  mouseY >= currentY && mouseY <= currentY + 15;
                
                // Button background
                int bgColor = isSelected ? 0xFF4A90E2 : (isHovered ? 0xFF5A5A5A : 0xFF505050);
                graphics.fill(panelX + 2, currentY, panelX + ENTRY_PANEL_WIDTH - 2, currentY + 15, bgColor);
                
                // Entry name
                String name = entry.getName().getString();
                if (name.length() > 8) {
                    name = name.substring(0, 6) + "...";
                }
                graphics.drawString(font, name, panelX + 5, currentY + 4, 0xFFFFFF, false);
            }
            
            currentY += 16;
        }
    }
    
    private void renderContentPanel(GuiGraphics graphics, int mouseX, int mouseY) {
        int panelX = guiLeft + CATEGORY_PANEL_WIDTH + ENTRY_PANEL_WIDTH + 5;
        int panelY = guiTop + 15;
        int panelHeight = GUI_HEIGHT - 45;
        
        // Panel background
        graphics.fill(panelX, panelY, panelX + CONTENT_PANEL_WIDTH, panelY + panelHeight, 0xFFF0F0F0);
        graphics.fill(panelX + 1, panelY + 1, panelX + CONTENT_PANEL_WIDTH - 1, panelY + panelHeight - 1, 0xFFFFFFFF);
        
        if (selectedEntry == null) {
            graphics.drawString(font, "No Entry Selected", panelX + 10, panelY + 10, 0x333333, false);
            graphics.drawString(font, "Select a category and", panelX + 10, panelY + 30, 0x666666, false);
            graphics.drawString(font, "entry to view content.", panelX + 10, panelY + 42, 0x666666, false);
            return;
        }
        
        GuidePage page = selectedEntry.getPage(currentPage);
        if (page == null) {
            graphics.drawString(font, "No Content Available", panelX + 10, panelY + 10, 0x333333, false);
            return;
        }
        
        // Render page content
        graphics.enableScissor(panelX + 2, panelY + 2, panelX + CONTENT_PANEL_WIDTH - 2, panelY + panelHeight - 2);
        page.render(graphics, panelX + 2, panelY + 2 - contentScrollOffset, CONTENT_PANEL_WIDTH - 4, panelHeight - 4, mouseX, mouseY);
        graphics.disableScissor();
        
        // Page indicator
        if (selectedEntry.getPageCount() > 1) {
            String pageInfo = (currentPage + 1) + " / " + selectedEntry.getPageCount();
            int textWidth = font.width(pageInfo);
            graphics.drawString(font, pageInfo, panelX + CONTENT_PANEL_WIDTH - textWidth - 5, panelY + 5, 0x666666, false);
        }
    }
    
    private void renderTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        // Implementation for tooltips if needed
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle category selection
        int categoryPanelX = guiLeft + 2;
        int categoryPanelY = guiTop + 15;
        int categoryPanelHeight = GUI_HEIGHT - 45;
        
        if (mouseX >= categoryPanelX + 2 && mouseX <= categoryPanelX + CATEGORY_PANEL_WIDTH - 2 &&
            mouseY >= categoryPanelY + 20 && mouseY <= categoryPanelY + categoryPanelHeight) {
            
            List<GuideCategory> categories = GuideContentRegistry.getCategories();
            int clickedIndex = ((int) mouseY - categoryPanelY - 20 + categoryScrollOffset) / 16;
            
            if (clickedIndex >= 0 && clickedIndex < categories.size()) {
                selectCategory(categories.get(clickedIndex));
                return true;
            }
        }
        
        // Handle entry selection
        int entryPanelX = guiLeft + CATEGORY_PANEL_WIDTH + 3;
        int entryPanelY = guiTop + 15;
        int entryPanelHeight = GUI_HEIGHT - 45;
        
        if (selectedCategory != null &&
            mouseX >= entryPanelX + 2 && mouseX <= entryPanelX + ENTRY_PANEL_WIDTH - 2 &&
            mouseY >= entryPanelY + 20 && mouseY <= entryPanelY + entryPanelHeight) {
            
            List<GuideEntry> entries = selectedCategory.getEntries();
            int clickedIndex = ((int) mouseY - entryPanelY - 20 + entryScrollOffset) / 16;
            
            if (clickedIndex >= 0 && clickedIndex < entries.size()) {
                selectEntry(entries.get(clickedIndex));
                return true;
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // Handle scrolling for different panels
        int categoryPanelX = guiLeft + 2;
        int entryPanelX = guiLeft + CATEGORY_PANEL_WIDTH + 3;
        int contentPanelX = guiLeft + CATEGORY_PANEL_WIDTH + ENTRY_PANEL_WIDTH + 5;
        
        if (mouseX >= categoryPanelX && mouseX <= categoryPanelX + CATEGORY_PANEL_WIDTH) {
            categoryScrollOffset = Math.max(0, categoryScrollOffset - (int) (delta * 16));
            return true;
        } else if (mouseX >= entryPanelX && mouseX <= entryPanelX + ENTRY_PANEL_WIDTH) {
            entryScrollOffset = Math.max(0, entryScrollOffset - (int) (delta * 16));
            return true;
        } else if (mouseX >= contentPanelX && mouseX <= contentPanelX + CONTENT_PANEL_WIDTH) {
            contentScrollOffset = Math.max(0, contentScrollOffset - (int) (delta * 16));
            return true;
        }
        
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
    
    private void selectCategory(GuideCategory category) {
        this.selectedCategory = category;
        this.entryScrollOffset = 0;
        
        // Select first entry in category
        if (!category.getEntries().isEmpty()) {
            selectEntry(category.getEntries().get(0));
        } else {
            this.selectedEntry = null;
            this.currentPage = 0;
        }
        
        updateButtonStates();
    }
    
    private void selectEntry(GuideEntry entry) {
        this.selectedEntry = entry;
        this.currentPage = 0;
        this.contentScrollOffset = 0;
        
        if (entry != null) {
            entry.markAsRead();
        }
        
        updateButtonStates();
    }
    
    private void goHome() {
        List<GuideCategory> categories = GuideContentRegistry.getCategories();
        if (!categories.isEmpty()) {
            selectCategory(categories.get(0));
        }
    }
    
    private void goBack() {
        // Implementation for navigation history
    }
    
    private void goForward() {
        // Implementation for navigation history
    }
    
    private void previousPage() {
        if (selectedEntry != null && currentPage > 0) {
            currentPage--;
            contentScrollOffset = 0;
            updateButtonStates();
        }
    }
    
    private void nextPage() {
        if (selectedEntry != null && currentPage < selectedEntry.getPageCount() - 1) {
            currentPage++;
            contentScrollOffset = 0;
            updateButtonStates();
        }
    }
    
    private void updateButtonStates() {
        if (prevPageButton != null) {
            prevPageButton.active = selectedEntry != null && currentPage > 0;
        }
        if (nextPageButton != null) {
            nextPageButton.active = selectedEntry != null && currentPage < selectedEntry.getPageCount() - 1;
        }
    }
    
    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause the game
    }
}
