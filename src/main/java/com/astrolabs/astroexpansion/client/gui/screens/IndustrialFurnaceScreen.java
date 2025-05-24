package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.IndustrialFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class IndustrialFurnaceScreen extends AbstractContainerScreen<IndustrialFurnaceMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstroExpansion.MODID, 
        "textures/gui/industrial_furnace.png");
    
    public IndustrialFurnaceScreen(IndustrialFurnaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }
    
    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.inventoryLabelY = 72;
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
        
        // Render progress bars for each input slot
        if (menu.isFormed()) {
            for (int i = 0; i < 9; i++) {
                int progress = menu.getProgress(i);
                if (progress > 0) {
                    int col = i % 3;
                    int row = i / 3;
                    int progressWidth = progress * 14 / 100;
                    guiGraphics.blit(TEXTURE, x + 84 + col * 18, y + 17 + row * 18, 
                        176, 14, progressWidth, 17);
                }
            }
            
            // Render energy bar
            int energyHeight = menu.getEnergyStored() * 52 / menu.getMaxEnergy();
            guiGraphics.blit(TEXTURE, x + 8, y + 17 + (52 - energyHeight), 
                176, 31, 12, energyHeight);
        }
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
        
        // Energy tooltip
        if (isHovering(8, 17, 12, 52, mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, 
                Component.literal(menu.getEnergyStored() + " / " + menu.getMaxEnergy() + " FE"), 
                mouseX, mouseY);
        }
        
        // Status tooltip - show in center of screen with smaller hover area
        if (!menu.isFormed() && isHovering(60, 40, 56, 16, mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, 
                Component.literal("Multiblock not formed!"), 
                mouseX, mouseY);
        }
    }
}