package com.astrolabs.astroexpansion.common.guidebook.pages;

import com.astrolabs.astroexpansion.api.guidebook.IGuidePage;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

/**
 * Page showing an interactive 3D preview of blocks/multiblocks
 */
public class Model3DPage implements IGuidePage {
    private final BlockState blockState;
    private final Component title;
    private final int pageNumber;
    
    private float rotation = 0;
    private float targetRotation = 0;
    private boolean isDragging = false;
    private double lastMouseX = 0;
    private float zoom = 1.0f;
    
    public Model3DPage(Block block, Component title, int pageNumber) {
        this.blockState = block.defaultBlockState();
        this.title = title;
        this.pageNumber = pageNumber;
    }
    
    @Override
    public String getType() {
        return "model3d";
    }
    
    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        // Draw title
        int titleWidth = Minecraft.getInstance().font.width(title);
        graphics.drawString(Minecraft.getInstance().font, title, 
            x + width / 2 - titleWidth / 2, y + 5, 0x000000, false);
        
        // Set up 3D rendering
        int modelX = x + width / 2;
        int modelY = y + height / 2 + 10;
        int scale = (int)(50 * zoom);
        
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(modelX, modelY, 100);
        poseStack.scale(scale, -scale, scale);
        
        // Smooth rotation
        float smoothRotation = rotation + (targetRotation - rotation) * partialTick * 0.2f;
        poseStack.mulPose(Axis.YP.rotationDegrees(smoothRotation));
        poseStack.mulPose(Axis.XP.rotationDegrees(30));
        
        // Enable lighting
        Lighting.setupForEntityInInventory();
        RenderSystem.enableDepthTest();
        
        // Render the block
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        
        poseStack.pushPose();
        poseStack.translate(-0.5, -0.5, -0.5);
        dispatcher.renderSingleBlock(blockState, poseStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
        poseStack.popPose();
        
        bufferSource.endBatch();
        
        // Reset render state
        RenderSystem.disableDepthTest();
        Lighting.setupFor3DItems();
        poseStack.popPose();
        
        // Draw controls hint
        Component controls = Component.literal("Drag to rotate • Scroll to zoom");
        int controlsWidth = Minecraft.getInstance().font.width(controls);
        graphics.drawString(Minecraft.getInstance().font, controls,
            x + width / 2 - controlsWidth / 2, y + height - 15, 0x666666, false);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDragging = true;
            lastMouseX = mouseX;
            return true;
        }
        return false;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDragging = false;
            rotation = targetRotation;
            return true;
        }
        return false;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDragging) {
            targetRotation += (float)(mouseX - lastMouseX) * 2;
            lastMouseX = mouseX;
            return true;
        }
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        zoom = Math.max(0.5f, Math.min(2.0f, zoom + (float)delta * 0.1f));
        return true;
    }
    
    @Override
    public void onOpened(Player player) {
        rotation = 0;
        targetRotation = 0;
        zoom = 1.0f;
    }
    
    @Override
    public void onClosed(Player player) {
    }
    
    @Override
    public void tick() {
        // Auto-rotate if not dragging
        if (!isDragging) {
            targetRotation += 0.5f;
        }
        
        // Smooth rotation update
        rotation += (targetRotation - rotation) * 0.1f;
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
}