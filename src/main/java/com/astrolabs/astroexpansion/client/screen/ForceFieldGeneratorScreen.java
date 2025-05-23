package com.astrolabs.astroexpansion.client.screen;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.ForceFieldGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ForceFieldGeneratorScreen extends AbstractContainerScreen<ForceFieldGeneratorMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AstroExpansion.MODID, "textures/gui/force_field_generator.png");
    
    private Button activeButton;
    private Button shapeButton;
    private Button radiusUpButton;
    private Button radiusDownButton;
    
    public ForceFieldGeneratorScreen(ForceFieldGeneratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }
    
    @Override
    protected void init() {
        super.init();
        
        this.activeButton = this.addRenderableWidget(Button.builder(
            getActiveText(),
            button -> {
                menu.setActive(!menu.isActive());
                button.setMessage(getActiveText());
            })
            .bounds(this.leftPos + 80, this.topPos + 20, 60, 20)
            .build());
        
        this.shapeButton = this.addRenderableWidget(Button.builder(
            getShapeText(),
            button -> {
                menu.setShape((menu.getShape() + 1) % 3);
                button.setMessage(getShapeText());
            })
            .bounds(this.leftPos + 80, this.topPos + 45, 60, 20)
            .build());
        
        this.radiusDownButton = this.addRenderableWidget(Button.builder(
            Component.literal("-"),
            button -> {
                menu.setRadius(Math.max(1, menu.getRadius() - 1));
            })
            .bounds(this.leftPos + 60, this.topPos + 70, 20, 20)
            .build());
        
        this.radiusUpButton = this.addRenderableWidget(Button.builder(
            Component.literal("+"),
            button -> {
                menu.setRadius(Math.min(16, menu.getRadius() + 1));
            })
            .bounds(this.leftPos + 140, this.topPos + 70, 20, 20)
            .build());
    }
    
    private Component getActiveText() {
        return menu.isActive() ? Component.literal("Active") : Component.literal("Inactive");
    }
    
    private Component getShapeText() {
        return switch (menu.getShape()) {
            case 0 -> Component.literal("Sphere");
            case 1 -> Component.literal("Cube");
            case 2 -> Component.literal("Dome");
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
        
        String radiusText = "Radius: " + menu.getRadius();
        int radiusTextWidth = this.font.width(radiusText);
        guiGraphics.drawString(this.font, radiusText, 
            this.leftPos + 88 + (52 - radiusTextWidth) / 2, 
            this.topPos + 75, 0x404040, false);
    }
}