package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

/**
 * AstroGuide Book Screen
 * Displays information about mod features and progression
 */
public class AstroGuideScreen extends Screen {
    private static final ResourceLocation TEXTURE = 
        new ResourceLocation(AstroExpansion.MODID, "textures/gui/astro_guide.png");
    private static final ResourceLocation FALLBACK_TEXTURE = 
        new ResourceLocation("minecraft", "textures/gui/book.png");
    
    private static final int GUI_WIDTH = 256;
    private static final int GUI_HEIGHT = 180;
    
    private int leftPos;
    private int topPos;
    
    private int currentPage = 0;
    private final List<GuidePage> pages = new ArrayList<>();
    private final ItemStack bookStack;
    
    public AstroGuideScreen(ItemStack stack) {
        super(Component.translatable("item.astroexpansion.astro_guide"));
        this.bookStack = stack;
        initPages();
    }
    
    private void initPages() {
        // Welcome page
        pages.add(new GuidePage(
            Component.literal("Welcome to Astro Expansion"),
            Component.literal("Your guide to the stars"),
            List.of(
                Component.literal("This guide will help you explore"),
                Component.literal("the various features of the"),
                Component.literal("Astro Expansion mod."),
                Component.empty(),
                Component.literal("Navigate using the arrows"),
                Component.literal("at the bottom of the pages.")
            )
        ));
        
        // Getting Started
        pages.add(new GuidePage(
            Component.literal("Getting Started"),
            Component.literal("First steps"),
            List.of(
                Component.literal("1. Build a Basic Generator"),
                Component.literal("2. Create a Material Processor"),
                Component.literal("3. Process raw ores into dust"),
                Component.literal("4. Smelt dusts into ingots"),
                Component.literal("5. Craft your first machines")
            )
        ));
        
        // Rockets
        pages.add(new GuidePage(
            Component.literal("Rockets"),
            Component.literal("Types of spacecraft"),
            List.of(
                Component.literal("Probe Rocket:"),
                Component.literal("• Unmanned exploration"),
                Component.literal("• Uses Launch Rails"),
                Component.literal("• Returns in 60 seconds"),
                Component.empty(),
                Component.literal("Personal Rocket:"),
                Component.literal("• For player transport"),
                Component.literal("• Uses Landing Pads"),
                Component.literal("• One-time use")
            )
        ));
        
        // More pages would be added in a full implementation
    }
    
    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - GUI_WIDTH) / 2;
        this.topPos = (this.height - GUI_HEIGHT) / 2;
          // Previous Page Button
        this.addRenderableWidget(Button.builder(
            Component.literal("<"), 
            button -> previousPage()
        )
        .pos(leftPos + 20, topPos + 150)
        .size(20, 20)
        .build());
        
        // Next Page Button
        this.addRenderableWidget(Button.builder(
            Component.literal(">"), 
            button -> nextPage()
        )
        .pos(leftPos + GUI_WIDTH - 40, topPos + 150)
        .size(20, 20)
        .build());
    }
    
    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
        }
    }
    
    private void nextPage() {
        if (currentPage < pages.size() - 1) {
            currentPage++;
        }
    }
    
    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        
        // Render book background
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        
        // Try to use our texture, fallback to vanilla book if it fails
        ResourceLocation textureToUse;
        try {
            // Try setting our texture
            RenderSystem.setShaderTexture(0, TEXTURE);
            textureToUse = TEXTURE;
            AstroExpansion.LOGGER.debug("Using AstroGuide custom texture");
        } catch (Exception e) {
            // If it fails, use the fallback
            RenderSystem.setShaderTexture(0, FALLBACK_TEXTURE);
            textureToUse = FALLBACK_TEXTURE;
            AstroExpansion.LOGGER.debug("Using fallback texture for AstroGuide: " + e.getMessage());
        }
        
        // Draw background texture
        guiGraphics.blit(textureToUse, leftPos, topPos, 0, 0, GUI_WIDTH, GUI_HEIGHT);
        
        // Get and render current page
        if (!pages.isEmpty() && currentPage < pages.size()) {
            GuidePage page = pages.get(currentPage);
            page.render(guiGraphics, leftPos, topPos, font);
        }
        
        // Page number
        String pageNumber = (currentPage + 1) + " / " + pages.size();
        int pageNumberWidth = font.width(pageNumber);
        guiGraphics.drawString(font, pageNumber, leftPos + (GUI_WIDTH / 2) - (pageNumberWidth / 2), topPos + 155, 0x333333);
        
        // Call parent render for widgets
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    /**
     * A page in the guide book
     */
    private static class GuidePage {
        private final Component title;
        private final Component subtitle;
        private final List<Component> content;
        
        public GuidePage(Component title, Component subtitle, List<Component> content) {
            this.title = title;
            this.subtitle = subtitle;
            this.content = content;
        }
        
        public void render(GuiGraphics guiGraphics, int leftPos, int topPos, net.minecraft.client.gui.Font font) {
            // Title
            guiGraphics.drawString(font, title, leftPos + 20, topPos + 20, 0x440066);
            
            // Subtitle
            guiGraphics.drawString(font, subtitle, leftPos + 20, topPos + 35, 0x666666);
            
            // Content - render each line with appropriate spacing
            int y = topPos + 60;
            for (Component line : content) {
                guiGraphics.drawString(font, line, leftPos + 20, y, 0x333333);
                y += 12; // Line spacing
            }
        }
    }
  }
