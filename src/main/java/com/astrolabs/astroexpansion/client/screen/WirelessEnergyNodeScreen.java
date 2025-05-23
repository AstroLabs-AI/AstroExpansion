package com.astrolabs.astroexpansion.client.screen;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.WirelessEnergyNodeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class WirelessEnergyNodeScreen extends AbstractContainerScreen<WirelessEnergyNodeMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AstroExpansion.MODID, "textures/gui/wireless_energy_node.png");
    
    private Button modeButton;
    
    public WirelessEnergyNodeScreen(WirelessEnergyNodeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }
    
    @Override
    protected void init() {
        super.init();
        
        this.modeButton = this.addRenderableWidget(Button.builder(
            getModeText(),
            button -> {
                int nextMode = (menu.getMode() + 1) % 3;
                menu.setMode(nextMode);
                button.setMessage(getModeText());
            })
            .bounds(this.leftPos + 80, this.topPos + 20, 60, 20)
            .build());
    }
    
    private Component getModeText() {
        return switch (menu.getMode()) {
            case 0 -> Component.literal("Transmit");
            case 1 -> Component.literal("Receive");
            case 2 -> Component.literal("Both");
            default -> Component.literal("Unknown");
        };
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        
        int energyScaled = (int) (52 * ((float) menu.getEnergy() / menu.getMaxEnergy()));
        guiGraphics.blit(TEXTURE, this.leftPos + 8, this.topPos + 16 + (52 - energyScaled), 176, 52 - energyScaled, 16, energyScaled);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
        
        if (isHovering(8, 16, 16, 52, mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, 
                Component.literal(menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE"), 
                mouseX, mouseY);
        }
        
        guiGraphics.drawString(this.font, 
            Component.literal("Connections: " + menu.getConnectionCount() + " / 8"), 
            this.leftPos + 80, this.topPos + 50, 0x404040, false);
        
        guiGraphics.drawString(this.font, 
            Component.literal("Range: 64 blocks"), 
            this.leftPos + 80, this.topPos + 60, 0x404040, false);
    }
}