package com.astrolabs.astroexpansion.common.guidebook.pages;

import com.astrolabs.astroexpansion.api.guidebook.IGuidePage;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Page supporting animated GIFs and video-like content
 */
public class MediaPage implements IGuidePage {
    private final ResourceLocation mediaLocation;
    private final Component caption;
    private final int pageNumber;
    private final MediaType type;
    
    private final List<FrameData> frames = new ArrayList<>();
    private DynamicTexture texture;
    private int currentFrame = 0;
    private long lastFrameTime = 0;
    private boolean playing = true;
    private boolean loaded = false;
    
    public MediaPage(ResourceLocation mediaLocation, MediaType type, Component caption, int pageNumber) {
        this.mediaLocation = mediaLocation;
        this.type = type;
        this.caption = caption;
        this.pageNumber = pageNumber;
    }
    
    @Override
    public String getType() {
        return "media";
    }
    
    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        if (!loaded) {
            loadMedia();
        }
        
        // Caption
        if (caption != null) {
            int captionWidth = Minecraft.getInstance().font.width(caption);
            graphics.drawString(Minecraft.getInstance().font, caption,
                x + width / 2 - captionWidth / 2, y + 5, 0x000000, false);
        }
        
        // Media display area
        int mediaY = y + (caption != null ? 20 : 5);
        int mediaHeight = height - (caption != null ? 40 : 25);
        
        if (texture != null && !frames.isEmpty()) {
            // Update frame if playing
            if (playing && System.currentTimeMillis() - lastFrameTime > frames.get(currentFrame).duration) {
                currentFrame = (currentFrame + 1) % frames.size();
                updateTexture(frames.get(currentFrame));
                lastFrameTime = System.currentTimeMillis();
            }
            
            // Render current frame
            RenderSystem.setShaderTexture(0, texture.getId());
            
            // Scale to fit
            float scale = Math.min(width / (float)frames.get(0).width, 
                                  mediaHeight / (float)frames.get(0).height);
            int renderWidth = (int)(frames.get(0).width * scale);
            int renderHeight = (int)(frames.get(0).height * scale);
            int renderX = x + width / 2 - renderWidth / 2;
            int renderY = mediaY + mediaHeight / 2 - renderHeight / 2;
            
            // Use texture directly
            graphics.blit(new ResourceLocation("guidebook", "media_" + hashCode()), 
                renderX, renderY, 0, 0, renderWidth, renderHeight);
        } else {
            // Loading indicator
            Component loading = Component.literal("Loading media...");
            int loadingWidth = Minecraft.getInstance().font.width(loading);
            graphics.drawString(Minecraft.getInstance().font, loading,
                x + width / 2 - loadingWidth / 2, y + height / 2 - 5, 0x666666, false);
        }
        
        // Media controls
        renderControls(graphics, x, y + height - 20, width, mouseX, mouseY);
    }
    
    private void renderControls(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        // Background
        graphics.fill(x, y, x + width, y + 20, 0x88000000);
        
        // Play/Pause button
        int buttonSize = 16;
        int buttonX = x + width / 2 - buttonSize / 2;
        boolean hovered = mouseX >= buttonX && mouseX < buttonX + buttonSize &&
                         mouseY >= y + 2 && mouseY < y + buttonSize + 2;
        
        graphics.fill(buttonX, y + 2, buttonX + buttonSize, y + buttonSize + 2,
            hovered ? 0xFFAAAAAA : 0xFF888888);
        
        // Draw play or pause icon
        if (playing) {
            // Pause icon (two bars)
            graphics.fill(buttonX + 4, y + 5, buttonX + 7, y + 13, 0xFFFFFFFF);
            graphics.fill(buttonX + 9, y + 5, buttonX + 12, y + 13, 0xFFFFFFFF);
        } else {
            // Play icon (triangle)
            for (int i = 0; i < 8; i++) {
                graphics.fill(buttonX + 4 + i/2, y + 5 + i, 
                            buttonX + 5 + i/2, y + 13 - i, 0xFFFFFFFF);
            }
        }
        
        // Progress bar
        if (frames.size() > 1) {
            int barX = x + 10;
            int barWidth = width - 20;
            int barY = y + 2;
            
            graphics.fill(barX, barY, barX + barWidth, barY + 2, 0xFF444444);
            
            int progress = (currentFrame * barWidth) / frames.size();
            graphics.fill(barX, barY, barX + progress, barY + 2, 0xFFAAAAAA);
        }
    }
    
    private void loadMedia() {
        if (type == MediaType.GIF) {
            loadGif();
        } else if (type == MediaType.IMAGE_SEQUENCE) {
            loadImageSequence();
        }
        loaded = true;
    }
    
    private void loadGif() {
        try {
            InputStream stream = Minecraft.getInstance().getResourceManager()
                .getResource(mediaLocation).get().open();
            
            ImageInputStream imageStream = ImageIO.createImageInputStream(stream);
            ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
            reader.setInput(imageStream);
            
            int numFrames = reader.getNumImages(true);
            
            for (int i = 0; i < numFrames; i++) {
                var image = reader.read(i);
                var metadata = reader.getImageMetadata(i);
                
                // Convert to NativeImage
                NativeImage nativeImage = new NativeImage(image.getWidth(), image.getHeight(), false);
                for (int x = 0; x < image.getWidth(); x++) {
                    for (int y = 0; y < image.getHeight(); y++) {
                        nativeImage.setPixelRGBA(x, y, image.getRGB(x, y));
                    }
                }
                
                // Get frame duration (default 100ms)
                int duration = 100;
                // TODO: Extract actual frame duration from metadata
                
                frames.add(new FrameData(nativeImage, duration, image.getWidth(), image.getHeight()));
            }
            
            if (!frames.isEmpty()) {
                texture = new DynamicTexture(frames.get(0).image);
                Minecraft.getInstance().getTextureManager().register(
                    new ResourceLocation("guidebook", "media_" + hashCode()), texture);
            }
            
            stream.close();
        } catch (IOException e) {
            // Fallback to static image
            loadStaticImage();
        }
    }
    
    private void loadImageSequence() {
        // Load a sequence of images as frames
        for (int i = 0; i < 10; i++) {
            ResourceLocation frameLoc = new ResourceLocation(
                mediaLocation.getNamespace(), 
                mediaLocation.getPath().replace(".png", "_" + i + ".png"));
            
            try {
                var resource = Minecraft.getInstance().getResourceManager()
                    .getResource(frameLoc);
                
                if (resource.isPresent()) {
                    NativeImage image = NativeImage.read(resource.get().open());
                    frames.add(new FrameData(image, 100, image.getWidth(), image.getHeight()));
                } else {
                    break;
                }
            } catch (IOException e) {
                break;
            }
        }
        
        if (!frames.isEmpty()) {
            texture = new DynamicTexture(frames.get(0).image);
            Minecraft.getInstance().getTextureManager().register(
                new ResourceLocation("guidebook", "media_seq_" + hashCode()), texture);
        }
    }
    
    private void loadStaticImage() {
        try {
            var resource = Minecraft.getInstance().getResourceManager()
                .getResource(mediaLocation);
            
            if (resource.isPresent()) {
                NativeImage image = NativeImage.read(resource.get().open());
                frames.add(new FrameData(image, 1000, image.getWidth(), image.getHeight()));
                
                texture = new DynamicTexture(image);
                Minecraft.getInstance().getTextureManager().register(
                    new ResourceLocation("guidebook", "media_static_" + hashCode()), texture);
            }
        } catch (IOException e) {
            // Failed to load
        }
    }
    
    private void updateTexture(FrameData frame) {
        if (texture != null) {
            texture.upload();
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            // Check play/pause button
            playing = !playing;
            return true;
        }
        return false;
    }
    
    @Override
    public void tick() {
        // Animation handled in render
    }
    
    @Override
    public void onOpened(Player player) {
        currentFrame = 0;
        lastFrameTime = System.currentTimeMillis();
        playing = true;
    }
    
    @Override
    public void onClosed(Player player) {
        // Clean up texture if needed
    }
    
    @Override
    public Component getTooltip(int mouseX, int mouseY) {
        return null;
    }
    
    @Override
    public boolean isInteractive() {
        return true;
    }
    
    @Override
    public int getPageNumber() {
        return pageNumber;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return false;
    }
    
    public enum MediaType {
        GIF,
        IMAGE_SEQUENCE,
        VIDEO
    }
    
    private static class FrameData {
        final NativeImage image;
        final int duration;
        final int width, height;
        
        FrameData(NativeImage image, int duration, int width, int height) {
            this.image = image;
            this.duration = duration;
            this.width = width;
            this.height = height;
        }
    }
}