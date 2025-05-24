package com.astrolabs.astroexpansion.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PulsingEnergyBar extends AnimatedWidget {
    private final int maxEnergy;
    private int currentEnergy;
    private boolean isFlowing;
    private float pulseIntensity = 0.0f;
    
    public PulsingEnergyBar(int x, int y, int width, int height, ResourceLocation texture, int maxEnergy) {
        super(x, y, width, height, texture, Component.literal("Energy"));
        this.maxEnergy = maxEnergy;
    }
    
    public void setEnergy(int energy) {
        this.currentEnergy = energy;
    }
    
    public void setFlowing(boolean flowing) {
        this.isFlowing = flowing;
    }
    
    @Override
    protected void renderAnimation(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Update pulse intensity
        if (isFlowing) {
            pulseIntensity = Math.min(1.0f, pulseIntensity + 0.05f);
        } else {
            pulseIntensity = Math.max(0.0f, pulseIntensity - 0.02f);
        }
        
        // Calculate fill percentage
        float fillPercentage = currentEnergy / (float) maxEnergy;
        int fillHeight = (int) (height * fillPercentage);
        
        // Base energy bar
        RenderSystem.setShaderTexture(0, texture);
        graphics.blit(texture, getX(), getY(), 0, 0, width, height, textureWidth, textureHeight);
        
        // Filled portion
        if (fillHeight > 0) {
            int yOffset = height - fillHeight;
            
            // Apply pulsing glow effect when energy is flowing
            if (pulseIntensity > 0) {
                float pulse = (float) Math.sin(animationProgress * Math.PI * 2) * 0.5f + 0.5f;
                float alpha = 0.7f + (pulse * 0.3f * pulseIntensity);
                
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
                
                // Render glowing overlay
                graphics.blit(texture, getX(), getY() + yOffset, width * 2, yOffset, width, fillHeight, textureWidth, textureHeight);
                
                // Add extra bright pulse at the top of the fill
                if (isFlowing) {
                    float brightPulse = pulse * pulseIntensity;
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, brightPulse * 0.5f);
                    graphics.blit(texture, getX() - 1, getY() + yOffset - 2, width * 3, 0, width + 2, 4, textureWidth, textureHeight);
                }
                
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.disableBlend();
            }
            
            // Normal filled bar
            graphics.blit(texture, getX(), getY() + yOffset, width, yOffset, width, fillHeight, textureWidth, textureHeight);
        }
        
        // Hover effect
        if (isHovered) {
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.2f);
            graphics.fill(getX(), getY(), getX() + width, getY() + height, 0x40FFFFFF);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.disableBlend();
        }
    }
    
    @Override
    protected long getAnimationDuration() {
        return 2000; // 2 second pulse cycle
    }
}