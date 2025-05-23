package com.astrolabs.astroexpansion.client.gui;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.ComponentAssemblerMenu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ComponentAssemblerScreen extends BaseContainerScreen<ComponentAssemblerMenu> {
    
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AstroExpansion.MODID, "textures/gui/component_assembler.png");
    
    public ComponentAssemblerScreen(ComponentAssemblerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.texture = TEXTURE;
        this.imageHeight = 184; // Taller GUI for 3x3 grid
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTick, mouseX, mouseY);
        
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        
        // Render energy bar
        renderEnergyBar(graphics, x + 8, y + 17, menu.getEnergyStored(), menu.getMaxEnergy());
        
        // Render progress arrow
        renderProgressBar(graphics, x + 89, y + 35, menu.getProcessProgress());
    }
    
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        
        // Render energy tooltip
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        
        if (isHovering(8, 17, 16, 52, mouseX, mouseY)) {
            graphics.renderTooltip(this.font, 
                Component.literal(menu.getEnergyStored() + " / " + menu.getMaxEnergy() + " FE"), 
                mouseX - x, mouseY - y);
        }
    }
}