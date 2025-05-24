package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.RocketAssemblyMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RocketAssemblyScreen extends AbstractContainerScreen<RocketAssemblyMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstroExpansion.MODID, "textures/gui/rocket_assembly.png");
    private Button launchButton;
    
    public RocketAssemblyScreen(RocketAssemblyMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
        this.inventoryLabelY = 88; // Player inventory starts at Y=99
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Add launch button
        this.launchButton = this.addRenderableWidget(Button.builder(
            Component.literal("LAUNCH"),
            button -> {
                if (this.menu.blockEntity != null && this.menu.blockEntity.canLaunch()) {
                    this.menu.blockEntity.launchRocket();
                }
            })
            .pos(this.leftPos + 60, this.topPos + 120)
            .size(56, 20)
            .build()
        );
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
        
        // Update launch button state
        if (this.menu.blockEntity != null) {
            this.launchButton.active = this.menu.blockEntity.canLaunch();
        }
        
        // Render status
        if (this.menu.blockEntity != null) {
            String status = this.menu.blockEntity.isAssembled() ? "Ready to Launch" : "Not Assembled";
            int color = this.menu.blockEntity.isAssembled() ? 0x00FF00 : 0xFF0000;
            graphics.drawString(this.font, status, this.leftPos + 8, this.topPos + 20, color, false);
            
            // Render fuel level
            int fuel = this.menu.blockEntity.getFuelAmount();
            int maxFuel = this.menu.blockEntity.getMaxFuel();
            graphics.drawString(this.font, "Fuel: " + fuel + "/" + maxFuel, this.leftPos + 8, this.topPos + 32, 4210752, false);
            
            // Render checklist
            int y = 50;
            graphics.drawString(this.font, "Launch Checklist:", this.leftPos + 8, this.topPos + y, 4210752, false);
            y += 12;
            
            // Structure check
            boolean structureOk = this.menu.blockEntity.isAssembled();
            graphics.drawString(this.font, (structureOk ? "✓" : "✗") + " Structure Complete", 
                              this.leftPos + 8, this.topPos + y, structureOk ? 0x00FF00 : 0xFF0000, false);
            y += 10;
            
            // Fuel check
            boolean fuelOk = fuel >= 1000;
            graphics.drawString(this.font, (fuelOk ? "✓" : "✗") + " Fuel (1000+ mB)", 
                              this.leftPos + 8, this.topPos + y, fuelOk ? 0x00FF00 : 0xFF0000, false);
            y += 10;
            
            // Components check
            boolean componentsOk = this.menu.blockEntity.hasRequiredComponents();
            graphics.drawString(this.font, (componentsOk ? "✓" : "✗") + " Components Loaded", 
                              this.leftPos + 8, this.topPos + y, componentsOk ? 0x00FF00 : 0xFF0000, false);
        }
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
        
        // Render fuel gauge
        if (this.menu.blockEntity != null) {
            int fuel = this.menu.blockEntity.getFuelAmount();
            int maxFuel = this.menu.blockEntity.getMaxFuel();
            if (maxFuel > 0 && fuel > 0) {
                int scaledHeight = fuel * 52 / maxFuel;
                graphics.fill(this.leftPos + 152, this.topPos + 68 - scaledHeight, 
                           this.leftPos + 168, this.topPos + 68, 0xFFFFD700);
            }
        }
    }
}