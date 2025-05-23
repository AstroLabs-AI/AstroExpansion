package com.astrolabs.astroexpansion.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public interface IMultiblockPattern {
    String getPatternId();
    
    int getWidth();
    int getHeight();
    int getDepth();
    
    BlockPos getMasterOffset();
    
    Map<Character, IMultiblockMatcher> getMatchers();
    
    String[][][] getPattern();
    
    default boolean checkPattern(Level level, BlockPos masterPos) {
        BlockPos offset = getMasterOffset();
        String[][][] pattern = getPattern();
        Map<Character, IMultiblockMatcher> matchers = getMatchers();
        
        for (int y = 0; y < getHeight(); y++) {
            for (int z = 0; z < getDepth(); z++) {
                for (int x = 0; x < getWidth(); x++) {
                    char key = pattern[y][z][x].charAt(0);
                    if (key == ' ') continue;
                    
                    BlockPos checkPos = masterPos.offset(x - offset.getX(), y - offset.getY(), z - offset.getZ());
                    BlockState state = level.getBlockState(checkPos);
                    
                    IMultiblockMatcher matcher = matchers.get(key);
                    if (matcher == null || !matcher.matches(level, checkPos, state)) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
}