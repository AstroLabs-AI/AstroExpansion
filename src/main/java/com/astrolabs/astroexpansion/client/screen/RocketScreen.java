package com.astrolabs.astroexpansion.client.screen;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.RocketMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RocketScreen extends AbstractContainerScreen<RocketMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AstroExpansion.MODID, "textures/gui/rocket.png");
    
    private Button launchButton;
    
    public RocketScreen(RocketMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }
    
    @Override
    protected void init() {
        super.init();
        
        this.launchButton = this.addRenderableWidget(Button.builder(
            Component.literal("Launch"),
            button -> {
                if (menu.canLaunch()) {
                    menu.launch();
                }
            })
            .bounds(this.leftPos + 60, this.topPos + 60, 56, 20)
            .build());
    }
    
    @Override
    protected void containerTick() {
        super.containerTick();
        this.launchButton.active = menu.canLaunch();
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        
        // Render fuel gauge
        if (menu.getFuelStored() > 0) {
            int fuelHeight = menu.getFuelStored() * 52 / menu.getMaxFuelStored();
            guiGraphics.blit(TEXTURE, x + 152, y + 65 - fuelHeight, 176, 52 - fuelHeight, 14, fuelHeight);
        }
        
        // Render status indicator
        if (menu.isReady()) {
            guiGraphics.blit(TEXTURE, x + 80, y + 35, 176, 68, 16, 16);
        }
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
        
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
        // Fuel tooltip
        if (mouseX >= x + 152 && mouseX <= x + 166 && mouseY >= y + 13 && mouseY <= y + 65) {
            guiGraphics.renderTooltip(this.font, 
                Component.literal(menu.getFuelStored() + " / " + menu.getMaxFuelStored() + " mB"), 
                mouseX, mouseY);
        }
        
        // Status tooltip
        if (!menu.isReady()) {
            guiGraphics.drawCenteredString(this.font, Component.literal("Not ready for launch"), 
                x + 88, y + 85, 0xFF0000);
        }
    }
}