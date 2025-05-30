package com.astrolabs.astroexpansion.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface IMultiblockMatcher {
    boolean matches(Level level, BlockPos pos, BlockState state);
}