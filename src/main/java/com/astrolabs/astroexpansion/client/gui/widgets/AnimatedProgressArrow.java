package com.astrolabs.astroexpansion.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class AnimatedProgressArrow extends AnimatedWidget {
    private float progress = 0.0f;
    private float targetProgress = 0.0f;
    private float displayProgress = 0.0f;
    private int arrowU;
    private int arrowV;
    private long lastProgressUpdate;
    private boolean showParticles = false;
    
    public AnimatedProgressArrow(int x, int y, int width, int height, ResourceLocation texture, int arrowU, int arrowV) {
        super(x, y, width, height, texture, Component.literal("Progress"));
        this.arrowU = arrowU;
        this.arrowV = arrowV;
        this.lastProgressUpdate = System.currentTimeMillis();
    }
    
    public void setProgress(float progress) {
        this.targetProgress = Mth.clamp(progress, 0.0f, 1.0f);
        if (targetProgress > 0 && targetProgress < 1.0f) {
            showParticles = true;
        } else {
            showParticles = false;
        }
    }
    
    @Override
    protected void updateAnimation(float partialTick) {
        super.updateAnimation(partialTick);
        
        // Smooth transition to target progress
        float delta = targetProgress - displayProgress;
        if (Math.abs(delta) > 0.001f) {
            displayProgress += delta * 0.1f;
        } else {
            displayProgress = targetProgress;
        }
        
        progress = displayProgress;
    }
    
    @Override
    protected void renderAnimation(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Base arrow outline
        RenderSystem.setShaderTexture(0, texture);
        graphics.blit(texture, getX(), getY(), arrowU, arrowV, width, height, textureWidth, textureHeight);
        
        // Animated fill
        if (progress > 0) {
            int fillWidth = (int) (width * progress);
            
            // Main progress fill with subtle animation
            float waveOffset = animationProgress * 2.0f;
            for (int i = 0; i < fillWidth; i++) {
                float wave = (float) Math.sin((i / (float) width + waveOffset) * Math.PI * 2) * 0.1f;
                int yOffset = (int) (wave * 2);
                
                graphics.blit(texture, getX() + i, getY() + yOffset, 
                    arrowU + width + i, arrowV, 1, height, textureWidth, textureHeight);
            }
            
            // Glowing edge at progress point
            if (showParticles && progress < 1.0f) {
                float pulse = (float) Math.sin(animationProgress * Math.PI * 4) * 0.5f + 0.5f;
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, pulse * 0.7f);
                
                int edgeX = getX() + fillWidth - 1;
                graphics.blit(texture, edgeX - 1, getY() - 1, 
                    arrowU + width * 2, arrowV, 3, height + 2, textureWidth, textureHeight);
                
                // Particle effects
                if (pulse > 0.7f) {
                    for (int i = 0; i < 3; i++) {
                        float particleY = getY() + (height / 2) + (float) Math.sin(animationProgress * Math.PI * 8 + i) * 3;
                        float particleX = edgeX + (float) Math.cos(animationProgress * Math.PI * 6 + i) * 2;
                        
                        RenderSystem.setShaderColor(1.0f, 0.9f, 0.7f, pulse * 0.5f);
                        graphics.fill((int)particleX, (int)particleY, (int)particleX + 1, (int)particleY + 1, 0xFFFFDD88);
                    }
                }
                
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.disableBlend();
            }
        }
        
        // Hover effect with tooltip
        if (isHovered) {
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.15f);
            graphics.fill(getX() - 1, getY() - 1, getX() + width + 1, getY() + height + 1, 0x40FFFFFF);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.disableBlend();
        }
    }
    
    @Override
    protected long getAnimationDuration() {
        return 3000; // 3 second animation cycle
    }
}