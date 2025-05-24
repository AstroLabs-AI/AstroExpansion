package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.TieredPowerGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TieredPowerGeneratorScreen extends AbstractContainerScreen<TieredPowerGeneratorMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstroExpansion.MODID, "textures/gui/basic_generator.png");

    public TieredPowerGeneratorScreen(TieredPowerGeneratorMenu menu, Inventory inventory, Component title) {
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

        // Render fuel progress
        if (menu.isBurning()) {
            int burnTime = menu.getScaledBurnTime();
            graphics.blit(TEXTURE, x + 80, y + 36 + 12 - burnTime, 176, 12 - burnTime, 14, burnTime + 1);
        }

        // Render energy bar
        int energyBarHeight = menu.getScaledEnergy();
        if (energyBarHeight > 0) {
            graphics.blit(TEXTURE, x + 152, y + 17 + (52 - energyBarHeight), 176, 31 + (52 - energyBarHeight), 16, energyBarHeight);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);

        // Render generation rate
        if (menu.isBurning()) {
            String genRate = "Generating: " + menu.getEnergyGeneration() + " FE/t";
            graphics.drawString(font, genRate, leftPos + 8, topPos + 72, 0x404040, false);
        }

        // Render tier info
        String tierInfo = "Tier: " + menu.getTier().getDisplayName();
        graphics.drawString(font, tierInfo, leftPos + imageWidth - font.width(tierInfo) - 7, topPos + 6, 0x404040, false);
    }
}