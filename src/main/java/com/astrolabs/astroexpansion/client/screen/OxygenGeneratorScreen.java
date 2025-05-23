package com.astrolabs.astroexpansion.client.screen;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.OxygenGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class OxygenGeneratorScreen extends AbstractContainerScreen<OxygenGeneratorMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AstroExpansion.MODID, "textures/gui/oxygen_generator.png");
    
    public OxygenGeneratorScreen(OxygenGeneratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        
        // Render energy bar
        if (menu.getEnergyStored() > 0) {
            int energyHeight = menu.getEnergyStored() * 52 / menu.getMaxEnergyStored();
            guiGraphics.blit(TEXTURE, x + 10, y + 65 - energyHeight, 176, 52 - energyHeight, 14, energyHeight);
        }
        
        // Render progress arrow
        if (menu.isGenerating()) {
            guiGraphics.blit(TEXTURE, x + 79, y + 34, 176, 52, 24, 16);
        }
        
        // Render oxygen storage
        if (menu.getOxygenStored() > 0) {
            int oxygenHeight = menu.getOxygenStored() * 52 / menu.getMaxOxygenStored();
            guiGraphics.blit(TEXTURE, x + 152, y + 65 - oxygenHeight, 190, 52 - oxygenHeight, 14, oxygenHeight);
        }
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
        
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
        // Energy tooltip
        if (mouseX >= x + 10 && mouseX <= x + 24 && mouseY >= y + 13 && mouseY <= y + 65) {
            guiGraphics.renderTooltip(this.font, 
                Component.literal(menu.getEnergyStored() + " / " + menu.getMaxEnergyStored() + " RF"), 
                mouseX, mouseY);
        }
        
        // Oxygen tooltip
        if (mouseX >= x + 152 && mouseX <= x + 166 && mouseY >= y + 13 && mouseY <= y + 65) {
            guiGraphics.renderTooltip(this.font, 
                Component.literal(menu.getOxygenStored() + " / " + menu.getMaxOxygenStored() + " mB"), 
                mouseX, mouseY);
        }
    }
}