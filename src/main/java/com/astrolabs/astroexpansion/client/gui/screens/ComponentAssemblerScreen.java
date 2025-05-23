package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.ComponentAssemblerMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ComponentAssemblerScreen extends AbstractContainerScreen<ComponentAssemblerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstroExpansion.MODID, "textures/gui/component_assembler.png");
    
    public ComponentAssemblerScreen(ComponentAssemblerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }
    
    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.inventoryLabelY = this.imageHeight - 94;
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
        
        // Render progress arrow
        if (this.menu.isCrafting()) {
            int progress = this.menu.getScaledProgress();
            guiGraphics.blit(TEXTURE, x + 79, y + 34, 176, 0, progress + 1, 16);
        }
        
        // Render energy bar
        int energy = this.menu.getScaledEnergy();
        guiGraphics.blit(TEXTURE, x + 8, y + 8 + (60 - energy), 176, 17 + (60 - energy), 16, energy);
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
    }
}