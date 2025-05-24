package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.FuelRefineryMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FuelRefineryScreen extends AbstractContainerScreen<FuelRefineryMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstroExpansion.MODID, "textures/gui/fuel_refinery.png");
    
    public FuelRefineryScreen(FuelRefineryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
        
        // Render fluid levels
        if (this.menu.blockEntity != null) {
            int oilAmount = this.menu.blockEntity.getOilAmount();
            int oilCapacity = this.menu.blockEntity.getOilCapacity();
            int fuelAmount = this.menu.blockEntity.getFuelAmount();
            int fuelCapacity = this.menu.blockEntity.getFuelCapacity();
            
            // Oil tank (left side)
            if (oilCapacity > 0 && oilAmount > 0) {
                int scaledHeight = oilAmount * 52 / oilCapacity;
                graphics.fill(this.leftPos + 26, this.topPos + 68 - scaledHeight, 
                           this.leftPos + 42, this.topPos + 68, 0xFF8B4513);
            }
            
            // Fuel tank (right side)
            if (fuelCapacity > 0 && fuelAmount > 0) {
                int scaledHeight = fuelAmount * 52 / fuelCapacity;
                graphics.fill(this.leftPos + 134, this.topPos + 68 - scaledHeight, 
                           this.leftPos + 150, this.topPos + 68, 0xFFFFD700);
            }
            
            // Progress arrow
            int progress = this.menu.blockEntity.getProgress();
            int maxProgress = this.menu.blockEntity.getMaxProgress();
            if (maxProgress > 0 && progress > 0) {
                int scaledProgress = progress * 24 / maxProgress;
                graphics.blit(TEXTURE, this.leftPos + 76, this.topPos + 34, 176, 0, scaledProgress, 17);
            }
        }
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }
    
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        
        // Render fluid amounts as text
        if (this.menu.blockEntity != null) {
            graphics.drawString(this.font, "Oil: " + this.menu.blockEntity.getOilAmount() + " mB", 8, 72, 4210752, false);
            graphics.drawString(this.font, "Fuel: " + this.menu.blockEntity.getFuelAmount() + " mB", 116, 72, 4210752, false);
        }
    }
}