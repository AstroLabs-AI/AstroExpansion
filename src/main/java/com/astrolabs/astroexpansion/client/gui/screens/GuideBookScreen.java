package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.api.guidebook.*;
import com.astrolabs.astroexpansion.client.gui.widgets.GuideSearchBar;
import com.astrolabs.astroexpansion.client.gui.widgets.GuideSidePanel;
import com.astrolabs.astroexpansion.common.guidebook.AstroGuideBook;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Main GUI screen for the AstroExpansion Guide Book
 */
public class GuideBookScreen extends Screen {
    private static final ResourceLocation BOOK_TEXTURE = new ResourceLocation(AstroExpansion.MODID, "textures/gui/guide_book.png");
    private static final int BOOK_WIDTH = 512;
    private static final int BOOK_HEIGHT = 256;
    private static final int PAGE_WIDTH = 220;
    private static final int PAGE_HEIGHT = 180;
    
    private final Player player;
    private final ItemStack bookStack;
    private final IGuideBook guideBook;
    
    // Current viewing state
    private IGuideCategory currentCategory;
    private IGuideEntry currentEntry;
    private int currentPageIndex = 0;
    private List<IGuidePage> currentPages = new ArrayList<>();
    
    // GUI elements
    private GuideSearchBar searchBar;
    private GuideSidePanel sidePanel;
    private Button prevPageButton;
    private Button nextPageButton;
    private Button backButton;
    private Button homeButton;
    private Button bookmarkButton;
    
    // Animation
    private float pageFlipProgress = 0;
    private boolean flippingForward = false;
    
    public GuideBookScreen(Player player, ItemStack bookStack) {
        super(Component.translatable("gui.astroexpansion.guide_book"));
        this.player = player;
        this.bookStack = bookStack;
        this.guideBook = AstroGuideBook.getInstance();
    }
    
    @Override
    protected void init() {
        super.init();
        
        int bookX = (width - BOOK_WIDTH) / 2;
        int bookY = (height - BOOK_HEIGHT) / 2;
        
        // Search bar
        searchBar = new GuideSearchBar(font, bookX + 50, bookY - 25, 200, 20, 
            Component.literal("Search..."));
        searchBar.setResponder(this::onSearchChanged);
        addRenderableWidget(searchBar);
        
        // Side panel for categories/bookmarks
        sidePanel = new GuideSidePanel(bookX - 150, bookY, 140, BOOK_HEIGHT, this);
        addRenderableWidget(sidePanel);
        
        // Navigation buttons
        prevPageButton = addRenderableWidget(Button.builder(
            Component.literal("<"),
            btn -> turnPage(-1))
            .pos(bookX + 20, bookY + BOOK_HEIGHT - 30)
            .size(20, 20)
            .build());
        
        nextPageButton = addRenderableWidget(Button.builder(
            Component.literal(">"),
            btn -> turnPage(1))
            .pos(bookX + BOOK_WIDTH - 40, bookY + BOOK_HEIGHT - 30)
            .size(20, 20)
            .build());
        
        backButton = addRenderableWidget(Button.builder(
            Component.literal("◄"),
            btn -> goBack())
            .pos(bookX + 10, bookY + 10)
            .size(30, 20)
            .build());
        
        homeButton = addRenderableWidget(Button.builder(
            Component.literal("⌂"),
            btn -> goHome())
            .pos(bookX + 45, bookY + 10)
            .size(30, 20)
            .build());
        
        bookmarkButton = addRenderableWidget(Button.builder(
            Component.literal("★"),
            btn -> toggleBookmark())
            .pos(bookX + BOOK_WIDTH - 40, bookY + 10)
            .size(30, 20)
            .build());
        
        // Initialize with home page
        goHome();
        updateButtons();
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        
        int bookX = (width - BOOK_WIDTH) / 2;
        int bookY = (height - BOOK_HEIGHT) / 2;
        
        // Render book background
        RenderSystem.setShaderTexture(0, BOOK_TEXTURE);
        graphics.blit(BOOK_TEXTURE, bookX, bookY, 0, 0, BOOK_WIDTH, BOOK_HEIGHT, 512, 512);
        
        // Render current pages
        if (!currentPages.isEmpty()) {
            // Left page
            if (currentPageIndex < currentPages.size()) {
                IGuidePage leftPage = currentPages.get(currentPageIndex);
                renderPage(graphics, leftPage, bookX + 20, bookY + 35, mouseX, mouseY, partialTick);
            }
            
            // Right page
            if (currentPageIndex + 1 < currentPages.size()) {
                IGuidePage rightPage = currentPages.get(currentPageIndex + 1);
                renderPage(graphics, rightPage, bookX + BOOK_WIDTH / 2 + 10, bookY + 35, mouseX, mouseY, partialTick);
            }
        }
        
        // Render page numbers
        String pageInfo = String.format("%d-%d / %d", 
            currentPageIndex + 1, 
            Math.min(currentPageIndex + 2, currentPages.size()), 
            currentPages.size());
        int pageInfoWidth = font.width(pageInfo);
        graphics.drawString(font, pageInfo, bookX + BOOK_WIDTH / 2 - pageInfoWidth / 2, 
            bookY + BOOK_HEIGHT - 20, 0x666666, false);
        
        super.render(graphics, mouseX, mouseY, partialTick);
        
        // Render tooltips
        if (!currentPages.isEmpty()) {
            if (currentPageIndex < currentPages.size()) {
                Component tooltip = currentPages.get(currentPageIndex).getTooltip(mouseX, mouseY);
                if (tooltip != null) {
                    graphics.renderTooltip(font, tooltip, mouseX, mouseY);
                }
            }
            if (currentPageIndex + 1 < currentPages.size()) {
                Component tooltip = currentPages.get(currentPageIndex + 1).getTooltip(mouseX, mouseY);
                if (tooltip != null) {
                    graphics.renderTooltip(font, tooltip, mouseX, mouseY);
                }
            }
        }
    }
    
    private void renderPage(GuiGraphics graphics, IGuidePage page, int x, int y, int mouseX, int mouseY, float partialTick) {
        // Clip to page bounds
        graphics.enableScissor(x, y, x + PAGE_WIDTH, y + PAGE_HEIGHT);
        page.render(graphics, x, y, PAGE_WIDTH, PAGE_HEIGHT, mouseX, mouseY, partialTick);
        graphics.disableScissor();
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        
        // Pass to current pages
        if (!currentPages.isEmpty()) {
            if (currentPageIndex < currentPages.size()) {
                if (currentPages.get(currentPageIndex).mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
            if (currentPageIndex + 1 < currentPages.size()) {
                if (currentPages.get(currentPageIndex + 1).mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchBar.isFocused()) {
            return searchBar.keyPressed(keyCode, scanCode, modifiers);
        }
        
        // Page navigation
        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            turnPage(-1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            turnPage(1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_HOME) {
            goHome();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            goBack();
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Update page animations
        for (IGuidePage page : currentPages) {
            page.tick();
        }
        
        // Update page flip animation
        if (pageFlipProgress > 0) {
            pageFlipProgress -= 0.1f;
        }
    }
    
    private void turnPage(int direction) {
        if (direction > 0 && currentPageIndex + 2 < currentPages.size()) {
            currentPageIndex += 2;
            playPageSound();
            updateButtons();
            pageFlipProgress = 1.0f;
            flippingForward = true;
        } else if (direction < 0 && currentPageIndex > 0) {
            currentPageIndex -= 2;
            playPageSound();
            updateButtons();
            pageFlipProgress = 1.0f;
            flippingForward = false;
        }
    }
    
    private void goBack() {
        // TODO: Implement navigation history
        if (currentEntry != null) {
            showCategory(currentEntry.getCategory());
        } else if (currentCategory != null) {
            goHome();
        }
    }
    
    private void goHome() {
        currentCategory = null;
        currentEntry = null;
        currentPageIndex = 0;
        
        // Show main categories page
        currentPages.clear();
        // TODO: Generate home page with categories
        
        updateButtons();
    }
    
    private void toggleBookmark() {
        if (currentEntry != null) {
            guideBook.toggleBookmark(player, currentEntry);
            updateButtons();
        }
    }
    
    public void showCategory(IGuideCategory category) {
        currentCategory = category;
        currentEntry = null;
        currentPageIndex = 0;
        
        // Generate category pages
        currentPages.clear();
        // TODO: Generate category overview pages
        
        guideBook.getProgress(player).getViewedEntries().add(category.getId());
        updateButtons();
    }
    
    public void showEntry(IGuideEntry entry) {
        currentEntry = entry;
        currentPageIndex = 0;
        
        // Get pages from entry
        currentPages = new ArrayList<>(entry.getPages());
        
        // Mark as viewed
        guideBook.markViewed(player, entry);
        
        // Open first page
        if (!currentPages.isEmpty()) {
            currentPages.get(0).onOpened(player);
            if (currentPages.size() > 1) {
                currentPages.get(1).onOpened(player);
            }
        }
        
        updateButtons();
    }
    
    private void onSearchChanged(String query) {
        if (query.isEmpty()) {
            sidePanel.showCategories();
        } else {
            List<IGuideEntry> results = guideBook.search(query);
            sidePanel.showSearchResults(results);
        }
    }
    
    private void updateButtons() {
        prevPageButton.active = currentPageIndex > 0;
        nextPageButton.active = currentPageIndex + 2 < currentPages.size();
        backButton.active = currentEntry != null || currentCategory != null;
        
        // Update bookmark button
        if (currentEntry != null && guideBook.getBookmarks(player).contains(currentEntry)) {
            bookmarkButton.setMessage(Component.literal("★"));
        } else {
            bookmarkButton.setMessage(Component.literal("☆"));
        }
    }
    
    private void playPageSound() {
        player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0f, 1.0f);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}