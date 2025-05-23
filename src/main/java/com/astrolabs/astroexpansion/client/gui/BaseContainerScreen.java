package com.astrolabs.astroexpansion.client.gui;

import com.astrolabs.astroexpansion.AstroExpansion;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class BaseContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    
    protected ResourceLocation texture;
    
    public BaseContainerScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
    
    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(texture, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }
    
    protected void renderEnergyBar(GuiGraphics graphics, int x, int y, int energy, int maxEnergy) {
        if (maxEnergy > 0) {
            int scaledEnergy = (int) ((float) energy / maxEnergy * 52);
            graphics.fill(x, y + 52 - scaledEnergy, x + 16, y + 52, 0xFFCC0000);
        }
    }
    
    protected void renderProgressBar(GuiGraphics graphics, int x, int y, float progress) {
        if (progress > 0) {
            int scaledProgress = (int) (progress * 24);
            graphics.blit(texture, x, y, 176, 0, scaledProgress, 16);
        }
    }
}