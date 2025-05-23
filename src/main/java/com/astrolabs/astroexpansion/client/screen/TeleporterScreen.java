package com.astrolabs.astroexpansion.client.screen;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.blockentities.TeleporterBlockEntity;
import com.astrolabs.astroexpansion.common.menu.TeleporterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class TeleporterScreen extends AbstractContainerScreen<TeleporterMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AstroExpansion.MODID, "textures/gui/teleporter.png");
    
    private int scrollOffset = 0;
    private static final int DESTINATIONS_PER_PAGE = 5;
    
    public TeleporterScreen(TeleporterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 200;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int buttonY = this.topPos + 40;
        List<TeleporterBlockEntity.TeleporterDestination> destinations = menu.getBlockEntity().getDestinations();
        
        for (int i = 0; i < DESTINATIONS_PER_PAGE && i + scrollOffset < destinations.size(); i++) {
            final int destIndex = i + scrollOffset;
            TeleporterBlockEntity.TeleporterDestination dest = destinations.get(destIndex);
            
            this.addRenderableWidget(Button.builder(
                Component.literal(dest.name()),
                button -> {
                    
                })
                .bounds(this.leftPos + 10, buttonY + (i * 25), 156, 20)
                .build());
        }
        
        if (scrollOffset > 0) {
            this.addRenderableWidget(Button.builder(
                Component.literal("↑"),
                button -> {
                    scrollOffset--;
                    this.rebuildWidgets();
                })
                .bounds(this.leftPos + 156, this.topPos + 35, 14, 20)
                .build());
        }
        
        if (scrollOffset + DESTINATIONS_PER_PAGE < destinations.size()) {
            this.addRenderableWidget(Button.builder(
                Component.literal("↓"),
                button -> {
                    scrollOffset++;
                    this.rebuildWidgets();
                })
                .bounds(this.leftPos + 156, this.topPos + 150, 14, 20)
                .build());
        }
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        
        int energyScaled = (int) (52 * ((float) menu.getEnergy() / menu.getMaxEnergy()));
        guiGraphics.blit(TEXTURE, this.leftPos + 8, this.topPos + 8 + (52 - energyScaled), 176, 52 - energyScaled, 16, energyScaled);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
        
        if (isHovering(8, 8, 16, 52, mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, 
                Component.literal(menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE"), 
                mouseX, mouseY);
        }
        
        String name = menu.getBlockEntity().getName();
        if (!name.isEmpty()) {
            guiGraphics.drawString(this.font, name, this.leftPos + 30, this.topPos + 10, 0x404040, false);
        } else {
            guiGraphics.drawString(this.font, "Teleporter", this.leftPos + 30, this.topPos + 10, 0x404040, false);
        }
        
        guiGraphics.drawString(this.font, "Destinations:", this.leftPos + 10, this.topPos + 25, 0x404040, false);
    }
}