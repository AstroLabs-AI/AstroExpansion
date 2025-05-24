package com.astrolabs.astroexpansion.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class RotatingGear extends AnimatedWidget {
    private float rotationSpeed = 1.0f;
    private float currentRotation = 0.0f;
    private boolean isProcessing = false;
    private int gearU;
    private int gearV;
    private float scale = 1.0f;
    
    public RotatingGear(int x, int y, int size, ResourceLocation texture, int gearU, int gearV) {
        super(x, y, size, size, texture, Component.literal("Gear"));
        this.gearU = gearU;
        this.gearV = gearV;
    }
    
    public void setProcessing(boolean processing) {
        this.isProcessing = processing;
    }
    
    public void setRotationSpeed(float speed) {
        this.rotationSpeed = speed;
    }
    
    @Override
    protected void updateAnimation(float partialTick) {
        super.updateAnimation(partialTick);
        
        if (isProcessing) {
            // Smooth acceleration
            if (rotationSpeed < 2.0f) {
                rotationSpeed += 0.02f;
            }
        } else {
            // Smooth deceleration
            if (rotationSpeed > 0.0f) {
                rotationSpeed = Math.max(0.0f, rotationSpeed - 0.03f);
            }
        }
        
        currentRotation += rotationSpeed * partialTick * 2.0f;
        currentRotation = currentRotation % 360.0f;
        
        // Hover scale effect
        if (isHovered) {
            scale = Math.min(1.1f, scale + 0.02f);
        } else {
            scale = Math.max(1.0f, scale - 0.02f);
        }
    }
    
    @Override
    protected void renderAnimation(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        
        // Center point for rotation
        float centerX = getX() + width / 2.0f;
        float centerY = getY() + height / 2.0f;
        
        // Apply transformations
        poseStack.translate(centerX, centerY, 0);
        poseStack.scale(scale, scale, 1.0f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(currentRotation));
        poseStack.translate(-width / 2.0f, -height / 2.0f, 0);
        
        // Render gear
        RenderSystem.setShaderTexture(0, texture);
        graphics.blit(texture, 0, 0, gearU, gearV, width, height, textureWidth, textureHeight);
        
        // Add spinning highlight effect when processing
        if (isProcessing && rotationSpeed > 1.5f) {
            RenderSystem.enableBlend();
            float highlightAlpha = (float) Math.sin(animationProgress * Math.PI * 4) * 0.2f + 0.1f;
            RenderSystem.setShaderColor(1.0f, 1.0f, 0.8f, highlightAlpha);
            
            graphics.blit(texture, -1, -1, gearU + width, gearV, width + 2, height + 2, textureWidth, textureHeight);
            
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.disableBlend();
        }
        
        poseStack.popPose();
        
        // Motion blur effect for fast spinning
        if (rotationSpeed > 1.8f) {
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.3f);
            
            for (int i = 1; i <= 3; i++) {
                poseStack.pushPose();
                poseStack.translate(centerX, centerY, 0);
                poseStack.scale(scale, scale, 1.0f);
                poseStack.mulPose(Axis.ZP.rotationDegrees(currentRotation - (i * 15)));
                poseStack.translate(-width / 2.0f, -height / 2.0f, 0);
                
                graphics.blit(texture, 0, 0, gearU, gearV, width, height, textureWidth, textureHeight);
                poseStack.popPose();
            }
            
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.disableBlend();
        }
    }
    
    @Override
    protected long getAnimationDuration() {
        return 4000; // 4 second highlight cycle
    }
}