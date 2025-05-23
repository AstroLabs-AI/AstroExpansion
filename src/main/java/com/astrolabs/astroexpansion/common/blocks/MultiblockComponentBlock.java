package com.astrolabs.astroexpansion.common.blocks;

import com.astrolabs.astroexpansion.common.multiblock.MultiblockValidator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MultiblockComponentBlock extends Block {
    
    public MultiblockComponentBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            MultiblockValidator.onBlockPlaced(level, pos);
        }
    }
    
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide && !state.is(newState.getBlock())) {
            MultiblockValidator.onBlockRemoved(level, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}