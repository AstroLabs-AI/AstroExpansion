package com.astrolabs.astroexpansion.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class AnimatedWidget extends AbstractWidget {
    protected final ResourceLocation texture;
    protected final int textureWidth;
    protected final int textureHeight;
    protected long animationStartTime;
    protected float animationProgress;
    protected boolean isAnimating = true;
    
    public AnimatedWidget(int x, int y, int width, int height, ResourceLocation texture, Component message) {
        super(x, y, width, height, message);
        this.texture = texture;
        this.textureWidth = 256;
        this.textureHeight = 256;
        this.animationStartTime = System.currentTimeMillis();
    }
    
    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        updateAnimation(partialTick);
        renderAnimation(graphics, mouseX, mouseY, partialTick);
    }
    
    protected void updateAnimation(float partialTick) {
        if (isAnimating) {
            long currentTime = System.currentTimeMillis();
            animationProgress = ((currentTime - animationStartTime) % getAnimationDuration()) / (float) getAnimationDuration();
        }
    }
    
    protected abstract void renderAnimation(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);
    
    protected abstract long getAnimationDuration();
    
    public void startAnimation() {
        isAnimating = true;
        animationStartTime = System.currentTimeMillis();
    }
    
    public void stopAnimation() {
        isAnimating = false;
    }
    
    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}