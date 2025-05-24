package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.TieredMaterialProcessorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TieredMaterialProcessorScreen extends AbstractContainerScreen<TieredMaterialProcessorMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstroExpansion.MODID, "textures/gui/material_processor.png");

    public TieredMaterialProcessorScreen(TieredMaterialProcessorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Render progress arrow
        if (menu.isCrafting()) {
            int progress = menu.getScaledProgress();
            graphics.blit(TEXTURE, x + 79, y + 35, 176, 14, progress + 1, 16);
        }

        // Render energy bar
        int energyBarHeight = menu.getScaledEnergy();
        if (energyBarHeight > 0) {
            graphics.blit(TEXTURE, x + 8, y + 17 + (52 - energyBarHeight), 176, 31 + (52 - energyBarHeight), 16, energyBarHeight);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);

        // Energy tooltip will be handled by block entity data

        // Render tier info
        String tierInfo = "Tier: " + menu.getTier().getDisplayName();
        graphics.drawString(font, tierInfo, leftPos + imageWidth - font.width(tierInfo) - 7, topPos + 6, 0x404040, false);
    }
}