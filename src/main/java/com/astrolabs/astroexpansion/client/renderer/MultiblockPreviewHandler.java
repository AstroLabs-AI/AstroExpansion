package com.astrolabs.astroexpansion.client.renderer;

import com.astrolabs.astroexpansion.api.multiblock.IMultiblockController;
import com.astrolabs.astroexpansion.api.multiblock.IMultiblockMatcher;
import com.astrolabs.astroexpansion.api.multiblock.IMultiblockPattern;
import com.astrolabs.astroexpansion.client.gui.MultiblockPreviewHUD;
import com.astrolabs.astroexpansion.common.registry.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class MultiblockPreviewHandler {
    private static final Map<BlockPos, PreviewData> activePreview = new HashMap<>();
    private static boolean previewEnabled = false;
    private static long lastToggleTime = 0;
    
    public static class PreviewData {
        public final BlockPos controllerPos;
        public final IMultiblockPattern pattern;
        public final Map<BlockPos, BlockStatus> blockStatuses;
        public final Map<Character, Integer> missingBlocks;
        public final long creationTime;
        
        public PreviewData(BlockPos controllerPos, IMultiblockPattern pattern) {
            this.controllerPos = controllerPos;
            this.pattern = pattern;
            this.blockStatuses = new HashMap<>();
            this.missingBlocks = new HashMap<>();
            this.creationTime = System.currentTimeMillis();
        }
    }
    
    public enum BlockStatus {
        CORRECT(GhostBlockRenderer.COLOR_CORRECT),
        INCORRECT(GhostBlockRenderer.COLOR_INCORRECT),
        EMPTY(GhostBlockRenderer.COLOR_EMPTY),
        PREVIEW(GhostBlockRenderer.COLOR_PREVIEW);
        
        public final Vec3 color;
        
        BlockStatus(Vec3 color) {
            this.color = color;
        }
    }
    
    public static void togglePreview() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastToggleTime > 200) { // Debounce
            previewEnabled = !previewEnabled;
            lastToggleTime = currentTime;
            if (!previewEnabled) {
                activePreview.clear();
            }
        }
    }
    
    public static boolean isPreviewEnabled() {
        return previewEnabled;
    }
    
    public static void updatePreview(Level level) {
        if (!previewEnabled) return;
        
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        
        // Check if player is holding wrench
        boolean holdingWrench = player.getMainHandItem().is(ModItems.WRENCH.get()) || 
                               player.getOffhandItem().is(ModItems.WRENCH.get());
        
        if (!holdingWrench && !player.isShiftKeyDown()) {
            activePreview.clear();
            return;
        }
        
        // Check what the player is looking at
        HitResult hitResult = mc.hitResult;
        if (hitResult instanceof BlockHitResult blockHit) {
            BlockPos pos = blockHit.getBlockPos();
            BlockEntity be = level.getBlockEntity(pos);
            
            if (be instanceof IMultiblockController controller) {
                updateControllerPreview(level, pos, controller);
            }
        }
    }
    
    private static void updateControllerPreview(Level level, BlockPos controllerPos, IMultiblockController controller) {
        IMultiblockPattern pattern = controller.getPattern();
        if (pattern == null) return;
        
        PreviewData preview = activePreview.computeIfAbsent(controllerPos, 
            pos -> new PreviewData(pos, pattern));
        
        // Clear old statuses
        preview.blockStatuses.clear();
        preview.missingBlocks.clear();
        
        // Calculate block positions and their status
        BlockPos offset = pattern.getMasterOffset();
        String[][][] patternArray = pattern.getPattern();
        Map<Character, IMultiblockMatcher> matchers = pattern.getMatchers();
        
        int correctBlocks = 0;
        int totalBlocks = 0;
        
        for (int y = 0; y < pattern.getHeight(); y++) {
            for (int z = 0; z < pattern.getDepth(); z++) {
                for (int x = 0; x < pattern.getWidth(); x++) {
                    char key = patternArray[y][z][x].charAt(0);
                    if (key == ' ') continue;
                    
                    BlockPos checkPos = controllerPos.offset(
                        x - offset.getX(), 
                        y - offset.getY(), 
                        z - offset.getZ()
                    );
                    
                    BlockState state = level.getBlockState(checkPos);
                    IMultiblockMatcher matcher = matchers.get(key);
                    
                    BlockStatus status;
                    if (matcher == null) {
                        status = BlockStatus.INCORRECT;
                    } else if (state.isAir()) {
                        status = BlockStatus.EMPTY;
                        // Track missing blocks
                        preview.missingBlocks.merge(key, 1, Integer::sum);
                    } else if (matcher.matches(level, checkPos, state)) {
                        status = BlockStatus.CORRECT;
                        correctBlocks++;
                    } else {
                        status = BlockStatus.INCORRECT;
                        // Track incorrect blocks as missing
                        preview.missingBlocks.merge(key, 1, Integer::sum);
                    }
                    
                    preview.blockStatuses.put(checkPos, status);
                    totalBlocks++;
                }
            }
        }
        
        // Calculate completion percentage
        float completion = totalBlocks > 0 ? (float)correctBlocks / totalBlocks : 0;
        
        // Update HUD with completion
        String patternName = pattern.getPatternId();
        MultiblockPreviewHUD.updateCompletion(completion, patternName);
    }
    
    public static void renderPreview(PoseStack poseStack, MultiBufferSource buffer, Vec3 camPos, float partialTick) {
        if (!previewEnabled || activePreview.isEmpty()) return;
        
        for (PreviewData preview : activePreview.values()) {
            // Fade out old previews
            long age = System.currentTimeMillis() - preview.creationTime;
            float alpha = Math.max(0.3f, 1.0f - (age / 10000.0f)); // Fade over 10 seconds
            
            for (Map.Entry<BlockPos, BlockStatus> entry : preview.blockStatuses.entrySet()) {
                BlockPos pos = entry.getKey();
                BlockStatus status = entry.getValue();
                
                // Render ghost block
                GhostBlockRenderer.renderGhostBlock(poseStack, buffer, pos, camPos, status.color, alpha * 0.4f);
                
                // Render outline for empty/incorrect blocks
                if (status != BlockStatus.CORRECT) {
                    GhostBlockRenderer.renderGhostOutline(poseStack, buffer, pos, camPos, status.color);
                }
            }
        }
    }
    
    public static void clearPreview() {
        activePreview.clear();
    }
    
    public static PreviewData getActivePreview() {
        if (activePreview.isEmpty()) return null;
        // Return the most recent preview
        return activePreview.values().stream()
            .max((a, b) -> Long.compare(a.creationTime, b.creationTime))
            .orElse(null);
    }
    
    public static Map<BlockPos, PreviewData> getAllPreviews() {
        return new HashMap<>(activePreview);
    }
}