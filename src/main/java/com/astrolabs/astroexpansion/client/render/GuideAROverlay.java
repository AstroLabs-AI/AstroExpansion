package com.astrolabs.astroexpansion.client.render;

import com.astrolabs.astroexpansion.api.guidebook.IGuideEntry;
import com.astrolabs.astroexpansion.common.guidebook.AstroGuideBook;
import com.astrolabs.astroexpansion.common.items.AstroGuideItem;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import org.joml.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Optional;

/**
 * Renders AR overlay information when holding the guide book
 */
public class GuideAROverlay {
    private static final Minecraft mc = Minecraft.getInstance();
    
    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        
        if (mc.player == null || mc.level == null) {
            return;
        }
        
        // Check if player is holding guide book
        boolean holdingGuide = false;
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = mc.player.getItemInHand(hand);
            if (stack.getItem() instanceof AstroGuideItem) {
                holdingGuide = true;
                break;
            }
        }
        
        if (!holdingGuide) {
            return;
        }
        
        // Get what player is looking at
        HitResult hitResult = mc.hitResult;
        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            renderBlockInfo(event, blockHit);
        }
    }
    
    private static void renderBlockInfo(RenderLevelStageEvent event, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = mc.level.getBlockState(pos);
        
        // Find guide entry for this block
        Optional<IGuideEntry> entry = AstroGuideBook.getInstance()
            .getEntryFor(new ItemStack(state.getBlock()));
        
        if (entry.isEmpty()) {
            return;
        }
        
        // Calculate render position
        Vec3 renderPos = Vec3.atCenterOf(pos).add(0, 1.5, 0);
        
        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();
        Vec3 cameraPos = camera.getPosition();
        
        poseStack.pushPose();
        poseStack.translate(
            renderPos.x - cameraPos.x,
            renderPos.y - cameraPos.y,
            renderPos.z - cameraPos.z
        );
        
        // Billboard rotation
        poseStack.mulPose(camera.rotation());
        poseStack.scale(-0.025f, -0.025f, 0.025f);
        
        // Render info panel
        renderInfoPanel(poseStack, entry.get(), state);
        
        poseStack.popPose();
    }
    
    private static void renderInfoPanel(PoseStack poseStack, IGuideEntry entry, BlockState state) {
        Font font = mc.font;
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        
        // Panel components
        Component title = entry.getName();
        Component blockName = state.getBlock().getName();
        Component category = Component.literal("Category: ").append(entry.getCategory().getName());
        Component clickHint = Component.literal("Sneak + Right-click to view in guide");
        
        // Calculate panel size
        int maxWidth = Math.max(
            Math.max(font.width(title), font.width(blockName)),
            Math.max(font.width(category), font.width(clickHint))
        );
        int panelWidth = maxWidth + 20;
        int panelHeight = 60;
        
        // Render background
        Matrix4f matrix = poseStack.last().pose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        
        // Background quad
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        
        float x1 = -panelWidth / 2f;
        float x2 = panelWidth / 2f;
        float y1 = -panelHeight / 2f;
        float y2 = panelHeight / 2f;
        
        // Semi-transparent black background
        buffer.vertex(matrix, x1, y1, 0).color(0, 0, 0, 180).endVertex();
        buffer.vertex(matrix, x1, y2, 0).color(0, 0, 0, 180).endVertex();
        buffer.vertex(matrix, x2, y2, 0).color(0, 0, 0, 180).endVertex();
        buffer.vertex(matrix, x2, y1, 0).color(0, 0, 0, 180).endVertex();
        
        tesselator.end();
        
        // Border
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        
        buffer.vertex(matrix, x1, y1, 0).color(0, 170, 255, 255).endVertex();
        buffer.vertex(matrix, x1, y2, 0).color(0, 170, 255, 255).endVertex();
        buffer.vertex(matrix, x2, y2, 0).color(0, 170, 255, 255).endVertex();
        buffer.vertex(matrix, x2, y1, 0).color(0, 170, 255, 255).endVertex();
        buffer.vertex(matrix, x1, y1, 0).color(0, 170, 255, 255).endVertex();
        
        tesselator.end();
        
        // Render text
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        
        poseStack.pushPose();
        poseStack.translate(0, 0, 1); // Render text slightly in front
        
        // Title
        int titleX = -font.width(title) / 2;
        font.drawInBatch(title, titleX, -25, 0x00FFFF, false, 
            matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
        
        // Block name
        int blockX = -font.width(blockName) / 2;
        font.drawInBatch(blockName, blockX, -15, 0xFFFFFF, false,
            matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
        
        // Category
        int catX = -font.width(category) / 2;
        font.drawInBatch(category, catX, -5, 0xAAAAAA, false,
            matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
        
        // Hint
        int hintX = -font.width(clickHint) / 2;
        font.drawInBatch(clickHint, hintX, 10, 0x00FF00, false,
            matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
        
        poseStack.popPose();
        
        bufferSource.endBatch();
        
        // Render connecting line
        renderConnectingLine(poseStack);
    }
    
    private static void renderConnectingLine(PoseStack poseStack) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        Matrix4f matrix = poseStack.last().pose();
        
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableCull();
        
        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        
        // Line from panel to block
        buffer.vertex(matrix, 0, -30, 0).color(0, 170, 255, 100).endVertex();
        buffer.vertex(matrix, 0, -60, 0).color(0, 170, 255, 200).endVertex();
        
        tesselator.end();
        
        RenderSystem.enableCull();
    }
    
    /**
     * Handle right-click to open guide to specific entry
     */
    public static boolean handleBlockClick(BlockPos pos, net.minecraft.world.entity.player.Player player) {
        if (!player.isShiftKeyDown()) {
            return false;
        }
        
        ItemStack heldItem = player.getMainHandItem();
        if (!(heldItem.getItem() instanceof AstroGuideItem)) {
            heldItem = player.getOffhandItem();
            if (!(heldItem.getItem() instanceof AstroGuideItem)) {
                return false;
            }
        }
        
        BlockState state = player.level().getBlockState(pos);
        Optional<IGuideEntry> entry = AstroGuideBook.getInstance()
            .getEntryFor(new ItemStack(state.getBlock()));
        
        if (entry.isPresent()) {
            // Open guide book to specific entry
            if (player.level().isClientSide) {
                openGuideToEntry(entry.get());
            }
            return true;
        }
        
        return false;
    }
    
    private static void openGuideToEntry(IGuideEntry entry) {
        // TODO: Open guide book screen to specific entry
        mc.player.displayClientMessage(
            Component.literal("Opening guide to: ").append(entry.getName()), 
            true
        );
    }
}