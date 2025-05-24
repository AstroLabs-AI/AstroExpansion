package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.ResearchTerminalMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ResearchTerminalScreen extends AbstractContainerScreen<ResearchTerminalMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstroExpansion.MODID, "textures/gui/research_terminal.png");
    
    public ResearchTerminalScreen(ResearchTerminalMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
        this.inventoryLabelY = this.imageHeight - 94;
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
        
        // Render research points
        int points = this.menu.getResearchPoints();
        graphics.drawString(this.font, "Research Points: " + points, this.leftPos + 8, this.topPos + 20, 4210752, false);
        
        // Render available researches
        int y = 35;
        for (int i = 0; i < Math.min(5, this.menu.getAvailableResearches().size()); i++) {
            String research = this.menu.getAvailableResearches().get(i);
            int cost = this.menu.getResearchCost(research);
            boolean canAfford = points >= cost;
            
            int color = canAfford ? 0x00FF00 : 0xFF0000;
            graphics.drawString(this.font, research + " (" + cost + " pts)", this.leftPos + 8, this.topPos + y, color, false);
            y += 12;
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            int relX = (int)mouseX - this.leftPos;
            int relY = (int)mouseY - this.topPos;
            
            // Check if clicking on a research
            if (relX >= 8 && relX <= 160 && relY >= 35 && relY <= 95) {
                int index = (relY - 35) / 12;
                if (index < this.menu.getAvailableResearches().size()) {
                    this.menu.attemptResearch(index);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}