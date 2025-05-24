package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.BasicGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BasicGeneratorScreen extends AbstractContainerScreen<BasicGeneratorMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstroExpansion.MODID, "textures/gui/basic_generator.png");
    
    public BasicGeneratorScreen(BasicGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
    
    @Override
    protected void init() {
        super.init();
        // Standard inventory label position (player inventory starts at Y=99)
        this.inventoryLabelY = 88;
        this.titleLabelY = 6;
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        
        // Render burn progress
        if (menu.isCrafting()) {
            graphics.blit(TEXTURE, x + 80, y + 54, 176, 14, 14, menu.getScaledProgress());
        }
        
        // Render energy bar
        int energyScaled = menu.getScaledEnergy();
        graphics.blit(TEXTURE, x + 152, y + 69 - energyScaled, 176, 31, 16, energyScaled);
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
        
        // Render energy tooltip
        if (isHovering(152, 17, 16, 52, mouseX, mouseY)) {
            graphics.renderTooltip(this.font, Component.literal(menu.getEnergyStored() + " / " + menu.getMaxEnergyStored() + " FE"), mouseX, mouseY);
        }
    }
}