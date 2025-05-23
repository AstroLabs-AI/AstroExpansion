package com.astrolabs.astroexpansion.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Set;

public interface IMultiblockController {
    boolean isFormed();
    
    void checkFormation();
    
    void onFormed();
    
    void onBroken();
    
    BlockPos getMasterPos();
    
    Set<BlockPos> getMultiblockPositions();
    
    IMultiblockPattern getPattern();
    
    Level getLevel();
    
    void invalidateMultiblock();
}