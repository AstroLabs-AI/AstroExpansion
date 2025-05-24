package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.QuantumComputerMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class QuantumComputerScreen extends AbstractContainerScreen<QuantumComputerMenu> {
    private static final ResourceLocation TEXTURE =
        new ResourceLocation(AstroExpansion.MODID, "textures/gui/quantum_computer.png");
    
    public QuantumComputerScreen(QuantumComputerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageHeight = 184;
        this.inventoryLabelY = 72;
    }
    
    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        
        // Render energy bar
        int energy = menu.getEnergy();
        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0) {
            int energyHeight = (int) (52 * ((float) energy / maxEnergy));
            guiGraphics.blit(TEXTURE, x + 10, y + 17 + (52 - energyHeight), 176, 52 - energyHeight, 8, energyHeight);
        }
        
        // Render progress bar
        if (menu.isProcessing()) {
            int progress = menu.getProgress();
            int maxProgress = menu.getMaxProgress();
            int progressWidth = (int) (24 * ((float) progress / maxProgress));
            guiGraphics.blit(TEXTURE, x + 117, y + 53, 176, 52, progressWidth, 17);
        }
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
        
        // Render energy tooltip
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (isHovering(10, 17, 8, 52, mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, 
                Component.literal(String.format("%,d / %,d FE", menu.getEnergy(), menu.getMaxEnergy())), 
                mouseX, mouseY);
        }
        
        // Render research points
        String researchText = "Research Points: " + menu.getResearchPoints();
        guiGraphics.drawString(this.font, researchText, x + 44, y + 90, 0x404040, false);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC key
            this.minecraft.player.closeContainer();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}