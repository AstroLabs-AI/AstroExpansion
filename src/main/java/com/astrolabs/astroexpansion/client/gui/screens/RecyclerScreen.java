package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.RecyclerMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.Optional;

public class RecyclerScreen extends AbstractContainerScreen<RecyclerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstroExpansion.MODID, "textures/gui/recycler.png");
    private static final int ENERGY_BAR_X = 10;
    private static final int ENERGY_BAR_Y = 17;
    private static final int ENERGY_BAR_WIDTH = 16;
    private static final int ENERGY_BAR_HEIGHT = 52;
    
    private int animationTick = 0;

    public RecyclerScreen(RecyclerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        // Center title
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Render energy bar
        int energyStored = menu.getEnergyStoredScaled();
        if (energyStored > 0) {
            graphics.blit(TEXTURE, x + ENERGY_BAR_X, y + ENERGY_BAR_Y + ENERGY_BAR_HEIGHT - energyStored,
                    176, 52 - energyStored, ENERGY_BAR_WIDTH, energyStored);
        }

        // Render progress arrow with animation
        if (menu.isRecycling()) {
            int progress = menu.getProcessProgress();
            graphics.blit(TEXTURE, x + 79, y + 44, 176, 52, progress + 1, 16);
            
            // Render animated recycling effect
            animationTick++;
            if (animationTick >= 40) {
                animationTick = 0;
            }
            
            // Draw rotating recycling symbol
            int frame = animationTick / 10;
            graphics.blit(TEXTURE, x + 76, y + 26, 192 + frame * 16, 0, 16, 16);
        }

        // Draw decorative elements
        if (menu.isRecycling()) {
            // Pulsing effect on input slot
            float pulse = (float)Math.sin(animationTick * 0.1f) * 0.5f + 0.5f;
            int alpha = (int)(pulse * 255);
            int color = FastColor.ARGB32.color(alpha, 255, 255, 255);
            
            RenderSystem.setShaderColor(
                FastColor.ARGB32.red(color) / 255.0f,
                FastColor.ARGB32.green(color) / 255.0f,
                FastColor.ARGB32.blue(color) / 255.0f,
                FastColor.ARGB32.alpha(color) / 255.0f
            );
            
            graphics.blit(TEXTURE, x + 76, y + 22, 208, 16, 24, 24);
            
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        
        // Render energy tooltip
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
        if (isHovering(ENERGY_BAR_X, ENERGY_BAR_Y, ENERGY_BAR_WIDTH, ENERGY_BAR_HEIGHT, mouseX, mouseY)) {
            List<Component> tooltip = List.of(
                Component.literal(menu.getEnergyStored() + " / " + menu.getMaxEnergyStored() + " FE")
            );
            graphics.renderTooltip(font, tooltip, Optional.empty(), mouseX - x, mouseY - y);
        }
        
        // Render process info tooltip
        if (isHovering(76, 26, 24, 40, mouseX, mouseY)) {
            List<Component> tooltip;
            if (menu.isRecycling()) {
                int progress = (menu.getProcessProgress() * 100) / 24;
                tooltip = List.of(
                    Component.translatable("gui.astroexpansion.recycler.recycling"),
                    Component.literal("Progress: " + progress + "%")
                );
            } else {
                tooltip = List.of(
                    Component.translatable("gui.astroexpansion.recycler.idle"),
                    Component.literal("Insert items to recycle")
                );
            }
            graphics.renderTooltip(font, tooltip, Optional.empty(), mouseX - x, mouseY - y);
        }
        
        // Render output chances tooltip
        if (isHovering(62, 62, 54, 18, mouseX, mouseY)) {
            List<Component> tooltip = List.of(
                Component.translatable("gui.astroexpansion.recycler.output_info"),
                Component.literal("15% Scrap Chance"),
                Component.literal("10% Material Recovery"),
                Component.literal("Upgrades increase chances")
            );
            graphics.renderTooltip(font, tooltip, Optional.empty(), mouseX - x, mouseY - y);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }
}