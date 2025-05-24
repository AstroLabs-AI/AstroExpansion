package com.astrolabs.astroexpansion.client.event;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.client.renderer.MultiblockPreviewHandler;
import com.astrolabs.astroexpansion.common.registry.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AstroExpansion.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {
    
    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        
        // Update preview data
        MultiblockPreviewHandler.updatePreview(mc.level);
        
        // Render preview
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        
        poseStack.pushPose();
        
        // Translate to world coordinates
        Vec3 camPos = event.getCamera().getPosition();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);
        
        MultiblockPreviewHandler.renderPreview(poseStack, buffer, camPos, event.getPartialTick());
        
        poseStack.popPose();
        
        // Ensure buffers are flushed
        buffer.endBatch();
    }
    
    @SubscribeEvent
    public static void onMouseClick(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        // Check if right-clicking with wrench while sneaking
        if (event.isUseItem() && mc.player.isShiftKeyDown()) {
            boolean holdingWrench = mc.player.getItemInHand(event.getHand()).is(ModItems.WRENCH.get());
            
            if (holdingWrench) {
                MultiblockPreviewHandler.togglePreview();
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        }
    }
}