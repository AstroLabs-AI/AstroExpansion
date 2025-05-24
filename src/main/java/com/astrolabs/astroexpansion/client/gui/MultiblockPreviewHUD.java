package com.astrolabs.astroexpansion.client.gui;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.client.renderer.MultiblockPreviewHandler;
import com.astrolabs.astroexpansion.client.renderer.MultiblockPreviewHandler.PreviewData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = AstroExpansion.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MultiblockPreviewHUD {
    
    private static final Map<String, String> PATTERN_NAMES = new HashMap<>();
    static {
        PATTERN_NAMES.put("fusion_reactor", "Fusion Reactor (5x5x5)");
        PATTERN_NAMES.put("industrial_furnace", "Industrial Furnace (3x3x3)");
        PATTERN_NAMES.put("rocket_assembly", "Rocket Assembly Platform (9x15x9)");
        PATTERN_NAMES.put("fuel_refinery", "Fuel Refinery (3x5x3)");
        PATTERN_NAMES.put("quantum_computer", "Quantum Computer (3x3x3)");
    }
    
    private static final ResourceLocation HUD_TEXTURE = new ResourceLocation(AstroExpansion.MODID, "textures/gui/multiblock_preview_hud.png");
    private static float currentCompletion = 0;
    private static String structureName = "";
    private static long lastUpdateTime = 0;
    
    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) return;
        
        if (!MultiblockPreviewHandler.isPreviewEnabled()) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        GuiGraphics graphics = event.getGuiGraphics();
        PoseStack poseStack = graphics.pose();
        Font font = mc.font;
        
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        
        // Position at top center
        int x = screenWidth / 2 - 100;
        int y = 10;
        
        // Draw background (semi-transparent black box)
        graphics.fill(x - 2, y - 2, x + 202, y + 40, 0x80000000);
        
        // Draw title
        Component title = Component.literal("Multiblock Preview").withStyle(ChatFormatting.AQUA);
        graphics.drawCenteredString(font, title, screenWidth / 2, y, 0xFFFFFF);
        
        // Draw structure name if available
        if (!structureName.isEmpty()) {
            graphics.drawCenteredString(font, structureName, screenWidth / 2, y + 10, 0xAAAAAA);
        }
        
        // Draw progress bar background
        int barY = y + 22;
        graphics.fill(x, barY, x + 200, barY + 12, 0xFF000000);
        graphics.fill(x + 1, barY + 1, x + 199, barY + 11, 0xFF333333);
        
        // Draw progress bar fill
        int fillWidth = (int)(198 * currentCompletion);
        if (fillWidth > 0) {
            // Color based on completion: red -> yellow -> green
            int color;
            if (currentCompletion < 0.5f) {
                color = 0xFFFF0000; // Red
            } else if (currentCompletion < 0.9f) {
                color = 0xFFFFFF00; // Yellow
            } else if (currentCompletion < 1.0f) {
                color = 0xFF88FF00; // Yellow-green
            } else {
                color = 0xFF00FF00; // Green
            }
            graphics.fill(x + 1, barY + 1, x + 1 + fillWidth, barY + 11, color);
        }
        
        // Draw percentage text
        String percentText = String.format("%.1f%%", currentCompletion * 100);
        graphics.drawCenteredString(font, percentText, screenWidth / 2, barY + 2, 0xFFFFFF);
        
        // Draw helper text
        if (currentCompletion < 1.0f) {
            Component helperText = Component.literal("Missing blocks shown in ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal("blue").withStyle(ChatFormatting.BLUE))
                .append(Component.literal(" and ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal("red").withStyle(ChatFormatting.RED));
            graphics.drawCenteredString(font, helperText, screenWidth / 2, barY + 16, 0xFFFFFF);
        } else {
            Component readyText = Component.literal("Structure complete!").withStyle(ChatFormatting.GREEN);
            graphics.drawCenteredString(font, readyText, screenWidth / 2, barY + 16, 0xFFFFFF);
        }
        
        // Show missing blocks tooltip if hovering over HUD
        int mouseX = (int)(mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth());
        int mouseY = (int)(mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight());
        
        if (mouseX >= x - 2 && mouseX <= x + 202 && mouseY >= y - 2 && mouseY <= y + 40) {
            PreviewData preview = MultiblockPreviewHandler.getActivePreview();
            if (preview != null && !preview.missingBlocks.isEmpty()) {
                MultiblockTooltipRenderer.renderMissingBlocksTooltip(graphics, 
                    preview.missingBlocks, preview.pattern.getMatchers(), mouseX, mouseY);
            }
        }
    }
    
    public static void updateCompletion(float completion, String patternId) {
        currentCompletion = completion;
        structureName = PATTERN_NAMES.getOrDefault(patternId, patternId);
        lastUpdateTime = System.currentTimeMillis();
    }
}