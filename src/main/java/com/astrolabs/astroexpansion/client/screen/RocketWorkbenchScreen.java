package com.astrolabs.astroexpansion.client.screen;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.RocketWorkbenchMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RocketWorkbenchScreen extends AbstractContainerScreen<RocketWorkbenchMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstroExpansion.MODID, "textures/gui/rocket_workbench.png");
    
    public RocketWorkbenchScreen(RocketWorkbenchMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 166;
    }
    
    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}