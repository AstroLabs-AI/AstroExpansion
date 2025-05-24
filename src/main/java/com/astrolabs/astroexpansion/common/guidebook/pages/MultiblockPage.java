package com.astrolabs.astroexpansion.common.guidebook.pages;

import com.astrolabs.astroexpansion.api.guidebook.IGuidePage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import java.util.HashMap;
import java.util.Map;

/**
 * Page showing multiblock structures with layer-by-layer view
 */
public class MultiblockPage implements IGuidePage {
    private final Map<BlockPos, BlockState> structure;
    private final Component title;
    private final int pageNumber;
    private final int width, height, depth;
    
    private int currentLayer = 0;
    private float rotation = 0;
    private float zoom = 1.0f;
    private boolean showFullStructure = false;
    private boolean animating = true;
    
    public MultiblockPage(Map<BlockPos, BlockState> structure, Component title, int pageNumber) {
        this.structure = new HashMap<>(structure);
        this.title = title;
        this.pageNumber = pageNumber;
        
        // Calculate dimensions
        int minX = 0, maxX = 0, minY = 0, maxY = 0, minZ = 0, maxZ = 0;
        for (BlockPos pos : structure.keySet()) {
            minX = Math.min(minX, pos.getX());
            maxX = Math.max(maxX, pos.getX());
            minY = Math.min(minY, pos.getY());
            maxY = Math.max(maxY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxZ = Math.max(maxZ, pos.getZ());
        }
        
        this.width = maxX - minX + 1;
        this.height = maxY - minY + 1;
        this.depth = maxZ - minZ + 1;
    }
    
    @Override
    public String getType() {
        return "multiblock";
    }
    
    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        // Title
        int titleWidth = Minecraft.getInstance().font.width(title);
        graphics.drawString(Minecraft.getInstance().font, title, 
            x + width / 2 - titleWidth / 2, y + 5, 0x000000, false);
        
        // 3D preview area
        int modelX = x + width / 2;
        int modelY = y + height / 2;
        
        renderMultiblock(graphics, modelX, modelY, partialTick);
        
        // Layer controls
        String layerInfo = String.format("Layer %d / %d", currentLayer + 1, this.height);
        graphics.drawString(Minecraft.getInstance().font, layerInfo, 
            x + 10, y + height - 25, 0x000000, false);
        
        // Instructions
        Component instructions = showFullStructure ? 
            Component.literal("Click to view layer-by-layer") :
            Component.literal("Click to view full structure");
        int instrWidth = Minecraft.getInstance().font.width(instructions);
        graphics.drawString(Minecraft.getInstance().font, instructions,
            x + width / 2 - instrWidth / 2, y + height - 10, 0x666666, false);
    }
    
    private void renderMultiblock(GuiGraphics graphics, int centerX, int centerY, float partialTick) {
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        
        // Position and scale
        poseStack.translate(centerX, centerY, 100);
        float scale = 20 * zoom / Math.max(width, Math.max(height, depth));
        poseStack.scale(scale, -scale, scale);
        
        // Rotation
        if (animating) {
            rotation += partialTick;
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.mulPose(Axis.XP.rotationDegrees(30));
        
        // Center the structure
        poseStack.translate(-width / 2.0, -height / 2.0, -depth / 2.0);
        
        // Render blocks
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        
        for (Map.Entry<BlockPos, BlockState> entry : structure.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();
            
            // Layer filtering
            if (!showFullStructure && pos.getY() != currentLayer) {
                continue;
            }
            
            // Ghost blocks for other layers
            if (showFullStructure && pos.getY() != currentLayer) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.3f);
            } else {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            }
            
            poseStack.pushPose();
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
            dispatcher.renderSingleBlock(state, poseStack, bufferSource, 
                15728880, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
            poseStack.popPose();
        }
        
        bufferSource.endBatch();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        
        poseStack.popPose();
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            showFullStructure = !showFullStructure;
            return true;
        } else if (button == 1) {
            animating = !animating;
            return true;
        }
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (showFullStructure) {
            zoom = Math.max(0.5f, Math.min(2.0f, zoom + (float)delta * 0.1f));
        } else {
            currentLayer = Math.max(0, Math.min(height - 1, currentLayer - (int)delta));
        }
        return true;
    }
    
    @Override
    public void tick() {
        if (animating && !showFullStructure) {
            rotation += 0.5f;
        }
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
    public void onOpened(Player player) {
        currentLayer = 0;
        rotation = 0;
    }
    
    @Override
    public void onClosed(Player player) {
    }
}