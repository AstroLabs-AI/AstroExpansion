package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.StorageTerminalMenu;
import com.astrolabs.astroexpansion.common.network.ModPacketHandler;
import com.astrolabs.astroexpansion.common.network.packets.UpdateStorageSearchPacket;
import com.astrolabs.astroexpansion.common.network.packets.ToggleFavoriteItemPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StorageTerminalScreen extends AbstractContainerScreen<StorageTerminalMenu> {
    private static final ResourceLocation TEXTURE = 
        new ResourceLocation(AstroExpansion.MODID, "textures/gui/storage_terminal.png");
    
    private EditBox searchBox;
    private int scrollOffset = 0;
    private static final int ITEMS_PER_ROW = 9;
    private static final int VISIBLE_ROWS = 4;
    
    public StorageTerminalScreen(StorageTerminalMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 195;
        this.imageHeight = 222;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Create search box
        int searchX = this.leftPos + 8;
        int searchY = this.topPos + 6;
        this.searchBox = new EditBox(this.font, searchX, searchY, 100, 12, Component.literal(""));
        this.searchBox.setMaxLength(50);
        this.searchBox.setBordered(false);
        this.searchBox.setVisible(true);
        this.searchBox.setFocused(true);
        this.searchBox.setTextColor(0xFFFFFF);
        this.searchBox.setResponder(this::onSearchChanged);
        this.searchBox.setHint(Component.literal("Search... (@mod for mod ID)"));
        this.addRenderableWidget(this.searchBox);
        
        // Add clear button
        Button clearButton = Button.builder(Component.literal("X"), button -> {
            this.searchBox.setValue("");
            this.onSearchChanged("");
        })
        .pos(this.leftPos + 110, this.topPos + 5)
        .size(14, 14)
        .build();
        this.addRenderableWidget(clearButton);
        
        // Update terminal with current search
        this.menu.setSearchQuery("");
    }
    
    private void onSearchChanged(String query) {
        this.menu.setSearchQuery(query);
        this.scrollOffset = 0;
        // Send search update to server
        ModPacketHandler.sendToServer(new UpdateStorageSearchPacket(query));
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        
        // Render stored items
        renderStoredItems(guiGraphics, mouseX, mouseY);
        
        // Render network info
        renderNetworkInfo(guiGraphics);
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        
        int x = this.leftPos;
        int y = this.topPos;
        
        // Main GUI background
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
        
        // Crafting result arrow
        if (menu.getSlot(45).hasItem()) {
            guiGraphics.blit(TEXTURE, x + 90, y + 35, 176, 0, 22, 15);
        }
    }
    
    private void renderStoredItems(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        List<ItemStack> items = menu.getFilteredItems();
        
        int x = this.leftPos + 8;
        int y = this.topPos + 18;
        
        int startIndex = scrollOffset * ITEMS_PER_ROW;
        int endIndex = Math.min(startIndex + (VISIBLE_ROWS * ITEMS_PER_ROW), items.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            int row = (i - startIndex) / ITEMS_PER_ROW;
            int col = (i - startIndex) % ITEMS_PER_ROW;
            
            int slotX = x + col * 18;
            int slotY = y + row * 18;
            
            ItemStack stack = items.get(i);
            
            // Render slot background
            guiGraphics.fill(slotX, slotY, slotX + 16, slotY + 16, 0x8B8B8B8B);
            
            // Render item
            guiGraphics.renderItem(stack, slotX, slotY);
            guiGraphics.renderItemDecorations(font, stack, slotX, slotY);
            
            // Render favorite star
            if (menu.isFavorite(stack)) {
                // Draw a small yellow star in the top-left corner
                guiGraphics.drawString(font, "★", slotX, slotY - 2, 0xFFD700, false);
            }
            
            // Handle mouse hover
            if (isHovering(slotX - leftPos, slotY - topPos, 16, 16, mouseX, mouseY)) {
                guiGraphics.fillGradient(slotX, slotY, slotX + 16, slotY + 16, 
                    0x80FFFFFF, 0x80FFFFFF);
                
                // Show tooltip with favorite hint
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(stack.getHoverName());
                if (hasShiftDown()) {
                    tooltip.add(Component.literal(""));
                    tooltip.add(Component.literal("Shift+Click to toggle favorite").withStyle(style -> style.withColor(0xFFD700)));
                }
                guiGraphics.renderTooltip(font, tooltip, stack.getTooltipImage(), mouseX, mouseY);
            }
        }
    }
    
    private void renderNetworkInfo(GuiGraphics guiGraphics) {
        if (menu.getNetwork() != null) {
            long stored = menu.getNetwork().getTotalItemCount();
            long capacity = menu.getNetwork().getTotalCapacity();
            int types = menu.getFilteredItems().size();
            
            Component storageText = Component.literal(String.format("Storage: %d / %d", stored, capacity));
            Component typesText = Component.literal(String.format("Types: %d", types));
            
            guiGraphics.drawString(font, storageText, leftPos + 8, topPos + 200, 0x404040, false);
            guiGraphics.drawString(font, typesText, leftPos + 8, topPos + 210, 0x404040, false);
        } else {
            guiGraphics.drawString(font, "Network Offline", leftPos + 8, topPos + 200, 0xFF0000, false);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle item extraction from network
        int x = this.leftPos + 8;
        int y = this.topPos + 18;
        
        List<ItemStack> items = menu.getFilteredItems();
        int startIndex = scrollOffset * ITEMS_PER_ROW;
        int endIndex = Math.min(startIndex + (VISIBLE_ROWS * ITEMS_PER_ROW), items.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            int row = (i - startIndex) / ITEMS_PER_ROW;
            int col = (i - startIndex) % ITEMS_PER_ROW;
            
            int slotX = x + col * 18;
            int slotY = y + row * 18;
            
            if (isHovering(slotX - leftPos, slotY - topPos, 16, 16, (int)mouseX, (int)mouseY)) {
                ItemStack stack = items.get(i);
                
                // Shift+click - toggle favorite
                if (hasShiftDown()) {
                    ModPacketHandler.sendToServer(new ToggleFavoriteItemPacket(stack));
                    return true;
                }
                
                // Left click - extract single item
                // Right click - extract stack
                int amount = button == 0 ? 1 : stack.getMaxStackSize();
                
                ItemStack requested = stack.copy();
                requested.setCount(amount);
                
                ItemStack extracted = menu.extractItemFromNetwork(requested, false);
                if (!extracted.isEmpty()) {
                    // Add to player inventory
                    if (!minecraft.player.getInventory().add(extracted)) {
                        // Drop if inventory full
                        minecraft.player.drop(extracted, false);
                    }
                    menu.updateNetworkItems();
                }
                
                return true;
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        List<ItemStack> items = menu.getFilteredItems();
        int maxScroll = Math.max(0, (items.size() - 1) / ITEMS_PER_ROW - VISIBLE_ROWS + 1);
        
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int)delta));
        return true;
    }
    
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Don't render default labels
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Allow ESC to close the GUI
        if (keyCode == 256) { // ESC key
            this.minecraft.player.closeContainer();
            return true;
        }
        
        // Handle search box input
        if (this.searchBox.isFocused() && this.searchBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}