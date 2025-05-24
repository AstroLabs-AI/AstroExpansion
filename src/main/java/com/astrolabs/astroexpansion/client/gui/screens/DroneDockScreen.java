package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.DroneDockMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DroneDockScreen extends AbstractContainerScreen<DroneDockMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstroExpansion.MODID, "textures/gui/drone_dock.png");
    
    public DroneDockScreen(DroneDockMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }
    
    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.inventoryLabelY = 88; // Player inventory starts at Y=99
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
        
        // Render energy bar
        int energy = this.menu.getScaledEnergy();
        guiGraphics.blit(TEXTURE, x + 8, y + 8 + (60 - energy), 176, 0 + (60 - energy), 16, energy);
        
        // Render drone status indicators
        if (this.menu.hasDrone()) {
            // Draw drone icon
            guiGraphics.blit(TEXTURE, x + 80, y + 20, 192, 0, 16, 16);
            
            // Draw health bar
            int health = this.menu.getDroneHealthScaled();
            guiGraphics.blit(TEXTURE, x + 70, y + 40, 176, 60, health, 4);
        }
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        
        // Render energy tooltip
        if (isHovering(8, 8, 16, 60, mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, 
                Component.literal(this.menu.getEnergyStored() + " / " + this.menu.getMaxEnergyStored() + " FE"), 
                mouseX, mouseY);
        }
        
        // Render drone status tooltip
        if (this.menu.hasDrone() && isHovering(70, 40, 36, 4, mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, 
                Component.literal("Health: " + this.menu.getDroneHealth() + " / " + this.menu.getDroneMaxHealth()), 
                mouseX, mouseY);
        }
    }
}