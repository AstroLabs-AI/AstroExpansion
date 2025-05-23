package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.FusionReactorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FusionReactorScreen extends AbstractContainerScreen<FusionReactorMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstroExpansion.MODID, "textures/gui/fusion_reactor.png");
    
    public FusionReactorScreen(FusionReactorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }
    
    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        
        // Render energy bar
        if (menu.getEnergyCapacity() > 0) {
            int energyScaled = menu.getEnergyStored() * 52 / menu.getEnergyCapacity();
            graphics.blit(TEXTURE, x + 152, y + 69 - energyScaled, 176, 52 - energyScaled, 16, energyScaled);
        }
        
        // Render temperature gauge
        if (menu.getMaxTemperature() > 0) {
            int tempScaled = menu.getTemperature() * 52 / menu.getMaxTemperature();
            graphics.blit(TEXTURE, x + 8, y + 69 - tempScaled, 192, 52 - tempScaled, 16, tempScaled);
        }
        
        // Render deuterium tank
        if (menu.getFluidCapacity() > 0) {
            int fluidScaled = menu.getDeuteriumAmount() * 52 / menu.getFluidCapacity();
            graphics.blit(TEXTURE, x + 44, y + 69 - fluidScaled, 208, 52 - fluidScaled, 16, fluidScaled);
        }
        
        // Render tritium tank
        if (menu.getFluidCapacity() > 0) {
            int fluidScaled = menu.getTritiumAmount() * 52 / menu.getFluidCapacity();
            graphics.blit(TEXTURE, x + 116, y + 69 - fluidScaled, 224, 52 - fluidScaled, 16, fluidScaled);
        }
        
        // Render status indicator
        if (menu.isRunning()) {
            graphics.blit(TEXTURE, x + 80, y + 35, 240, 0, 16, 16);
        }
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
        
        // Render tooltips
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
        // Energy tooltip
        if (isHovering(152, 17, 16, 52, mouseX, mouseY)) {
            graphics.renderTooltip(this.font, Component.literal(
                String.format("Energy: %,d / %,d FE", menu.getEnergyStored(), menu.getEnergyCapacity())), 
                mouseX, mouseY);
        }
        
        // Temperature tooltip
        if (isHovering(8, 17, 16, 52, mouseX, mouseY)) {
            graphics.renderTooltip(this.font, Component.literal(
                String.format("Temperature: %,d / %,d K", menu.getTemperature(), menu.getMaxTemperature())), 
                mouseX, mouseY);
        }
        
        // Deuterium tooltip
        if (isHovering(44, 17, 16, 52, mouseX, mouseY)) {
            graphics.renderTooltip(this.font, Component.literal(
                String.format("Deuterium: %,d / %,d mB", menu.getDeuteriumAmount(), menu.getFluidCapacity())), 
                mouseX, mouseY);
        }
        
        // Tritium tooltip
        if (isHovering(116, 17, 16, 52, mouseX, mouseY)) {
            graphics.renderTooltip(this.font, Component.literal(
                String.format("Tritium: %,d / %,d mB", menu.getTritiumAmount(), menu.getFluidCapacity())), 
                mouseX, mouseY);
        }
        
        // Status tooltip
        if (isHovering(80, 35, 16, 16, mouseX, mouseY)) {
            graphics.renderTooltip(this.font, Component.literal(
                menu.isRunning() ? "Status: Running" : "Status: Idle"), 
                mouseX, mouseY);
        }
    }
}