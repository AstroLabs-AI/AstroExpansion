package com.astrolabs.astroexpansion.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BubblingLiquid extends AnimatedWidget {
    private final List<Bubble> bubbles = new ArrayList<>();
    private final Random random = new Random();
    private float fillLevel = 0.0f;
    private float targetFillLevel = 0.0f;
    private int liquidColor = 0xFF3366CC;
    private boolean isActive = false;
    private long lastBubbleSpawn = 0;
    private int tankU;
    private int tankV;
    
    public BubblingLiquid(int x, int y, int width, int height, ResourceLocation texture, int tankU, int tankV) {
        super(x, y, width, height, texture, Component.literal("Liquid Tank"));
        this.tankU = tankU;
        this.tankV = tankV;
    }
    
    public void setFillLevel(float level) {
        this.targetFillLevel = Mth.clamp(level, 0.0f, 1.0f);
    }
    
    public void setLiquidColor(int color) {
        this.liquidColor = color;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    @Override
    protected void updateAnimation(float partialTick) {
        super.updateAnimation(partialTick);
        
        // Smooth fill level transition
        float delta = targetFillLevel - fillLevel;
        if (Math.abs(delta) > 0.001f) {
            fillLevel += delta * 0.05f;
        } else {
            fillLevel = targetFillLevel;
        }
        
        // Update bubbles
        Iterator<Bubble> iterator = bubbles.iterator();
        while (iterator.hasNext()) {
            Bubble bubble = iterator.next();
            bubble.update(partialTick);
            if (bubble.isDead()) {
                iterator.remove();
            }
        }
        
        // Spawn new bubbles
        long currentTime = System.currentTimeMillis();
        if (isActive && fillLevel > 0.1f && currentTime - lastBubbleSpawn > 200) {
            if (random.nextFloat() < 0.3f) {
                spawnBubble();
                lastBubbleSpawn = currentTime;
            }
        }
    }
    
    private void spawnBubble() {
        float x = getX() + 4 + random.nextFloat() * (width - 8);
        float y = getY() + height - (fillLevel * height * 0.2f);
        float size = 2 + random.nextFloat() * 4;
        float speed = 0.5f + random.nextFloat() * 1.0f;
        
        bubbles.add(new Bubble(x, y, size, speed));
    }
    
    @Override
    protected void renderAnimation(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Render tank background
        RenderSystem.setShaderTexture(0, texture);
        graphics.blit(texture, getX(), getY(), tankU, tankV, width, height, textureWidth, textureHeight);
        
        // Render liquid
        if (fillLevel > 0) {
            int fillHeight = (int) (height * fillLevel);
            int liquidY = getY() + height - fillHeight;
            
            // Animated liquid surface
            float waveHeight = isActive ? 2.0f : 0.5f;
            for (int x = 0; x < width - 4; x++) {
                float wave = (float) Math.sin((x / (float) width + animationProgress) * Math.PI * 4) * waveHeight;
                int waveY = liquidY + (int) wave;
                
                // Liquid column with transparency gradient
                for (int y = waveY; y < getY() + height - 2; y++) {
                    float depth = (y - waveY) / (float) fillHeight;
                    int alpha = (int) (200 + depth * 55);
                    int color = (alpha << 24) | (liquidColor & 0x00FFFFFF);
                    
                    graphics.fill(getX() + x + 2, y, getX() + x + 3, y + 1, color);
                }
            }
            
            // Render bubbles
            RenderSystem.enableBlend();
            for (Bubble bubble : bubbles) {
                bubble.render(graphics);
            }
            RenderSystem.disableBlend();
        }
        
        // Render tank overlay (glass effect)
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.3f);
        graphics.blit(texture, getX(), getY(), tankU + width, tankV, width, height, textureWidth, textureHeight);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        
        // Hover effect
        if (isHovered) {
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.1f);
            graphics.fill(getX(), getY(), getX() + width, getY() + height, 0x20FFFFFF);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.disableBlend();
        }
    }
    
    @Override
    protected long getAnimationDuration() {
        return 5000; // 5 second wave cycle
    }
    
    private class Bubble {
        private float x, y;
        private float size;
        private float speed;
        private float wobble;
        private float lifeTime;
        private final float maxLife;
        
        public Bubble(float x, float y, float size, float speed) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speed = speed;
            this.wobble = random.nextFloat() * (float) Math.PI * 2;
            this.lifeTime = 0;
            this.maxLife = (getY() + height - y) / speed;
        }
        
        public void update(float partialTick) {
            y -= speed * partialTick;
            x += Math.sin(wobble) * 0.5f * partialTick;
            wobble += 0.1f * partialTick;
            lifeTime += partialTick;
            
            // Grow slightly as bubble rises
            size += 0.02f * partialTick;
        }
        
        public void render(GuiGraphics graphics) {
            float alpha = 1.0f - (lifeTime / maxLife);
            alpha = Math.min(1.0f, alpha * 2); // Fade in quickly, fade out slowly
            
            int centerX = (int) x;
            int centerY = (int) y;
            int radius = (int) (size / 2);
            
            // Bubble with gradient
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    float dist = (float) Math.sqrt(dx * dx + dy * dy);
                    if (dist <= radius) {
                        float edgeDist = 1.0f - (dist / radius);
                        int bubbleAlpha = (int) (alpha * edgeDist * 100);
                        int color = (bubbleAlpha << 24) | 0x00FFFFFF;
                        
                        graphics.fill(centerX + dx, centerY + dy, centerX + dx + 1, centerY + dy + 1, color);
                    }
                }
            }
            
            // Highlight
            int highlightAlpha = (int) (alpha * 150);
            int highlightColor = (highlightAlpha << 24) | 0x00FFFFFF;
            graphics.fill(centerX - 1, centerY - radius + 1, centerX + 1, centerY - radius + 2, highlightColor);
        }
        
        public boolean isDead() {
            return y < getY() + 5 || lifeTime > maxLife;
        }
    }
}