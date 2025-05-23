package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.FluidTankMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class FluidTankScreen extends AbstractContainerScreen<FluidTankMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstroExpansion.MODID, 
        "textures/gui/fluid_tank.png");
    
    public FluidTankScreen(FluidTankMenu menu, Inventory inventory, Component title) {
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
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
        
        // Render fluid if present
        if (menu.getBlockEntity() != null && menu.getBlockEntity().getTank() != null) {
            FluidStack fluidStack = menu.getBlockEntity().getTank().getFluid();
            if (!fluidStack.isEmpty()) {
                int fluidHeight = (int) (52 * ((float) fluidStack.getAmount() / menu.getCapacity()));
                renderFluid(guiGraphics, x + 80, y + 17 + (52 - fluidHeight), 16, fluidHeight, fluidStack);
            }
        }
        
        // Render tank overlay
        guiGraphics.blit(TEXTURE, x + 80, y + 17, 176, 0, 16, 52);
    }
    
    private void renderFluid(GuiGraphics guiGraphics, int x, int y, int width, int height, FluidStack fluidStack) {
        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture(fluidStack);
        
        TextureAtlasSprite sprite = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
        int color = fluidTypeExtensions.getTintColor(fluidStack);
        
        RenderSystem.setShaderColor(
            ((color >> 16) & 0xFF) / 255f,
            ((color >> 8) & 0xFF) / 255f,
            (color & 0xFF) / 255f,
            ((color >> 24) & 0xFF) / 255f
        );
        
        guiGraphics.blit(x, y, 0, width, height, sprite);
        
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
        
        // Fluid tooltip
        if (isHovering(80, 17, 16, 52, mouseX, mouseY)) {
            if (menu.getBlockEntity() != null && menu.getBlockEntity().getTank() != null) {
                FluidStack fluidStack = menu.getBlockEntity().getTank().getFluid();
                if (!fluidStack.isEmpty()) {
                    guiGraphics.renderTooltip(this.font, 
                        Component.literal(fluidStack.getDisplayName().getString() + ": " + 
                            fluidStack.getAmount() + " / " + menu.getCapacity() + " mB"), 
                        mouseX, mouseY);
                } else {
                    guiGraphics.renderTooltip(this.font, 
                        Component.literal("Empty: 0 / " + menu.getCapacity() + " mB"), 
                        mouseX, mouseY);
                }
            }
        }
    }
}