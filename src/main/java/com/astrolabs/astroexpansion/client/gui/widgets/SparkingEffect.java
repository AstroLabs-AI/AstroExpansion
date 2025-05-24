package com.astrolabs.astroexpansion.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SparkingEffect extends AnimatedWidget {
    private final List<Spark> sparks = new ArrayList<>();
    private final Random random = new Random();
    private boolean isGenerating = false;
    private long lastSparkSpawn = 0;
    private float intensity = 1.0f;
    
    public SparkingEffect(int x, int y, int width, int height, ResourceLocation texture) {
        super(x, y, width, height, texture, Component.literal("Sparks"));
    }
    
    public void setGenerating(boolean generating) {
        this.isGenerating = generating;
    }
    
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
    
    @Override
    protected void updateAnimation(float partialTick) {
        super.updateAnimation(partialTick);
        
        // Update existing sparks
        Iterator<Spark> iterator = sparks.iterator();
        while (iterator.hasNext()) {
            Spark spark = iterator.next();
            spark.update(partialTick);
            if (spark.isDead()) {
                iterator.remove();
            }
        }
        
        // Spawn new sparks
        long currentTime = System.currentTimeMillis();
        if (isGenerating && currentTime - lastSparkSpawn > (50 / intensity)) {
            if (random.nextFloat() < 0.4f * intensity) {
                spawnSpark();
                lastSparkSpawn = currentTime;
            }
        }
    }
    
    private void spawnSpark() {
        float x = getX() + width / 2 + (random.nextFloat() - 0.5f) * width * 0.8f;
        float y = getY() + height / 2 + (random.nextFloat() - 0.5f) * height * 0.8f;
        
        float vx = (random.nextFloat() - 0.5f) * 2.0f;
        float vy = (random.nextFloat() - 0.5f) * 2.0f - 1.0f; // Bias upward
        
        int color = 0xFFFFFF00; // Yellow
        if (random.nextFloat() < 0.3f) {
            color = 0xFFFFAA00; // Orange
        } else if (random.nextFloat() < 0.1f) {
            color = 0xFF00AAFF; // Blue (electric)
        }
        
        sparks.add(new Spark(x, y, vx, vy, color));
    }
    
    @Override
    protected void renderAnimation(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Render sparks
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        
        for (Spark spark : sparks) {
            spark.render(graphics);
        }
        
        // Add glow effect when generating
        if (isGenerating && intensity > 0.5f) {
            float pulse = (float) Math.sin(animationProgress * Math.PI * 4) * 0.3f + 0.2f;
            RenderSystem.setShaderColor(1.0f, 0.9f, 0.7f, pulse * intensity);
            
            // Central glow
            int glowSize = 20;
            for (int i = 0; i < glowSize; i++) {
                float alpha = (1.0f - (i / (float) glowSize)) * pulse * intensity * 0.5f;
                int color = ((int)(alpha * 255) << 24) | 0x00FFDD88;
                
                int offset = i;
                graphics.fill(
                    getX() + width / 2 - offset,
                    getY() + height / 2 - offset,
                    getX() + width / 2 + offset,
                    getY() + height / 2 + offset,
                    color
                );
            }
            
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
    
    @Override
    protected long getAnimationDuration() {
        return 1000; // 1 second pulse cycle
    }
    
    private class Spark {
        private float x, y;
        private float vx, vy;
        private float lifeTime;
        private final float maxLife;
        private final int color;
        private final List<Trail> trail = new ArrayList<>();
        
        public Spark(float x, float y, float vx, float vy, int color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
            this.lifeTime = 0;
            this.maxLife = 20 + random.nextFloat() * 20;
        }
        
        public void update(float partialTick) {
            // Add trail point
            if (lifeTime % 2 < partialTick) {
                trail.add(new Trail(x, y, lifeTime / maxLife));
            }
            
            // Update position
            x += vx * partialTick;
            y += vy * partialTick;
            
            // Apply gravity
            vy += 0.1f * partialTick;
            
            // Apply drag
            vx *= 0.98f;
            vy *= 0.98f;
            
            lifeTime += partialTick;
            
            // Update trail
            trail.removeIf(t -> t.age > 10);
            trail.forEach(t -> t.age += partialTick);
        }
        
        public void render(GuiGraphics graphics) {
            float alpha = 1.0f - (lifeTime / maxLife);
            
            // Render trail
            for (int i = 0; i < trail.size() - 1; i++) {
                Trail t1 = trail.get(i);
                Trail t2 = trail.get(i + 1);
                
                float trailAlpha = alpha * (1.0f - t1.fade) * 0.5f;
                int trailColor = ((int)(trailAlpha * 255) << 24) | (color & 0x00FFFFFF);
                
                graphics.fill((int)t1.x, (int)t1.y, (int)t2.x + 1, (int)t2.y + 1, trailColor);
            }
            
            // Render spark core
            int sparkAlpha = (int)(alpha * 255);
            int sparkColor = (sparkAlpha << 24) | (color & 0x00FFFFFF);
            
            // Bright center
            graphics.fill((int)x - 1, (int)y - 1, (int)x + 2, (int)y + 2, sparkColor);
            
            // Dimmer outer pixels
            int dimAlpha = (int)(alpha * 128);
            int dimColor = (dimAlpha << 24) | (color & 0x00FFFFFF);
            graphics.fill((int)x - 2, (int)y, (int)x - 1, (int)y + 1, dimColor);
            graphics.fill((int)x + 2, (int)y, (int)x + 3, (int)y + 1, dimColor);
            graphics.fill((int)x, (int)y - 2, (int)x + 1, (int)y - 1, dimColor);
            graphics.fill((int)x, (int)y + 2, (int)x + 1, (int)y + 3, dimColor);
        }
        
        public boolean isDead() {
            return lifeTime > maxLife || y > getY() + height + 10;
        }
        
        private class Trail {
            float x, y;
            float age;
            float fade;
            
            Trail(float x, float y, float fade) {
                this.x = x;
                this.y = y;
                this.age = 0;
                this.fade = fade;
            }
        }
    }
}