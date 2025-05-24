package com.astrolabs.astroexpansion.common.guidebook.pages;

import com.astrolabs.astroexpansion.api.guidebook.IGuidePage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Interactive checklist page
 */
public class ChecklistPage implements IGuidePage {
    private final Component title;
    private final List<ChecklistItem> items;
    private final int pageNumber;
    private final ResourceLocation checklistId;
    
    public ChecklistPage(Component title, ResourceLocation checklistId, int pageNumber) {
        this.title = title;
        this.checklistId = checklistId;
        this.pageNumber = pageNumber;
        this.items = new ArrayList<>();
    }
    
    public void addItem(Component text, ItemStack icon) {
        items.add(new ChecklistItem(text, icon));
    }
    
    @Override
    public String getType() {
        return "checklist";
    }
    
    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        // Title
        int titleWidth = Minecraft.getInstance().font.width(title);
        graphics.drawString(Minecraft.getInstance().font, title, 
            x + width / 2 - titleWidth / 2, y + 5, 0x000000, false);
        
        // Checklist items
        int itemY = y + 25;
        int itemHeight = 20;
        
        for (int i = 0; i < items.size() && itemY + itemHeight < y + height; i++) {
            ChecklistItem item = items.get(i);
            boolean hovered = mouseX >= x && mouseX < x + width && 
                            mouseY >= itemY && mouseY < itemY + itemHeight;
            
            // Checkbox
            int checkX = x + 10;
            int checkSize = 12;
            graphics.fill(checkX, itemY + 4, checkX + checkSize, itemY + 4 + checkSize, 
                hovered ? 0xFF888888 : 0xFF666666);
            graphics.fill(checkX + 1, itemY + 5, checkX + checkSize - 1, itemY + 3 + checkSize, 
                0xFFFFFFFF);
            
            if (item.checked) {
                // Draw checkmark
                graphics.drawString(Minecraft.getInstance().font, "✓", 
                    checkX + 2, itemY + 3, 0xFF00AA00, false);
            }
            
            // Icon
            if (!item.icon.isEmpty()) {
                graphics.renderItem(item.icon, x + 30, itemY + 2);
            }
            
            // Text
            int textX = item.icon.isEmpty() ? x + 30 : x + 50;
            graphics.drawString(Minecraft.getInstance().font, item.text, 
                textX, itemY + 6, item.checked ? 0xFF888888 : 0xFF000000, false);
            
            itemY += itemHeight;
        }
        
        // Progress
        long checkedCount = items.stream().filter(i -> i.checked).count();
        String progress = String.format("%d / %d Complete", checkedCount, items.size());
        int progressWidth = Minecraft.getInstance().font.width(progress);
        graphics.drawString(Minecraft.getInstance().font, progress,
            x + width / 2 - progressWidth / 2, y + height - 15, 0xFF666666, false);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        
        int itemY = 25;
        int itemHeight = 20;
        
        for (ChecklistItem item : items) {
            if (mouseY >= itemY && mouseY < itemY + itemHeight) {
                item.checked = !item.checked;
                // TODO: Save progress
                return true;
            }
            itemY += itemHeight;
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
        // TODO: Load saved progress
    }
    
    @Override
    public void onClosed(Player player) {
        // TODO: Save progress
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
        return true;
    }
    
    @Override
    public int getPageNumber() {
        return pageNumber;
    }
    
    private static class ChecklistItem {
        final Component text;
        final ItemStack icon;
        boolean checked;
        
        ChecklistItem(Component text, ItemStack icon) {
            this.text = text;
            this.icon = icon;
            this.checked = false;
        }
    }
}