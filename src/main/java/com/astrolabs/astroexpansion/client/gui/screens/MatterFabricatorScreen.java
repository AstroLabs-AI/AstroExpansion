package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.MatterFabricatorMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MatterFabricatorScreen extends AbstractContainerScreen<MatterFabricatorMenu> {
    private static final ResourceLocation TEXTURE = 
        new ResourceLocation(AstroExpansion.MODID, "textures/gui/matter_fabricator.png");

    public MatterFabricatorScreen(MatterFabricatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Render progress arrow
        if (menu.isCrafting()) {
            guiGraphics.blit(TEXTURE, x + 79, y + 35, 176, 14, menu.getScaledProgress(), 17);
        }

        // Render energy bar
        int energyHeight = menu.getScaledEnergy();
        if (energyHeight > 0) {
            guiGraphics.blit(TEXTURE, x + 8, y + 13 + (60 - energyHeight), 176, 31 + (60 - energyHeight), 16, energyHeight);
        }

        // Render fluid tank (UU-Matter)
        int fluidHeight = menu.getScaledFluid();
        if (fluidHeight > 0) {
            // Render UU-Matter texture (purple/pink liquid)
            RenderSystem.setShaderColor(0.8F, 0.2F, 1.0F, 1.0F);
            guiGraphics.blit(TEXTURE, x + 115, y + 13 + (60 - fluidHeight), 192, 31, 16, fluidHeight);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        // Render production rate indicator
        int productionRate = menu.getProductionRate();
        if (productionRate > 100) {
            // Show enhanced production with animated effect
            float pulse = (float)(Math.sin(System.currentTimeMillis() / 200.0) * 0.5 + 0.5);
            RenderSystem.setShaderColor(1.0F, 1.0F - pulse * 0.3F, 1.0F - pulse * 0.5F, 1.0F);
            guiGraphics.blit(TEXTURE, x + 79, y + 55, 176, 0, 26, 13);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);

        // Energy tooltip
        if (isHovering(8, 13, 16, 60, mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, 
                Component.translatable("tooltip.astroexpansion.energy", 
                    menu.getEnergyStored(), menu.getMaxEnergyStored()), 
                mouseX, mouseY);
        }

        // Fluid tooltip
        if (isHovering(115, 13, 16, 60, mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, 
                Component.translatable("tooltip.astroexpansion.uu_matter", 
                    menu.getFluidAmount(), menu.getFluidCapacity()), 
                mouseX, mouseY);
        }

        // Production rate tooltip
        if (isHovering(79, 55, 26, 13, mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, 
                Component.translatable("tooltip.astroexpansion.production_rate", 
                    menu.getProductionRate()), 
                mouseX, mouseY);
        }
    }

    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        return mouseX >= (double)(i + x) && mouseX < (double)(i + x + width) 
            && mouseY >= (double)(j + y) && mouseY < (double)(j + y + height);
    }
}