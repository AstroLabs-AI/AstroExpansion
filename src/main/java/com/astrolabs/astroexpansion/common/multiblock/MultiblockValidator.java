package com.astrolabs.astroexpansion.common.multiblock;

import com.astrolabs.astroexpansion.api.multiblock.IMultiblockController;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashSet;
import java.util.Set;

public class MultiblockValidator {
    
    public static void validateAroundPosition(Level level, BlockPos pos) {
        if (level.isClientSide) return;
        
        Set<BlockPos> checked = new HashSet<>();
        int searchRadius = 8;
        
        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int y = -searchRadius; y <= searchRadius; y++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    if (checked.contains(checkPos)) continue;
                    checked.add(checkPos);
                    
                    BlockEntity be = level.getBlockEntity(checkPos);
                    if (be instanceof IMultiblockController controller) {
                        controller.checkFormation();
                    }
                }
            }
        }
    }
    
    public static void onBlockPlaced(Level level, BlockPos pos) {
        validateAroundPosition(level, pos);
    }
    
    public static void onBlockRemoved(Level level, BlockPos pos) {
        validateAroundPosition(level, pos);
    }
}