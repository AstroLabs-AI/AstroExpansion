package com.astrolabs.astroexpansion.common.multiblock.patterns;

import com.astrolabs.astroexpansion.api.multiblock.IMultiblockMatcher;
import com.astrolabs.astroexpansion.api.multiblock.IMultiblockPattern;
import com.astrolabs.astroexpansion.api.multiblock.MultiblockMatcher;
import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class IndustrialFurnacePattern implements IMultiblockPattern {
    private static final String[][][] PATTERN = {
        {
            {"CCC"},
            {"CCC"},
            {"CCC"}
        },
        {
            {"CFC"},
            {"F F"},
            {"CFC"}
        }
    };
    
    private final Map<Character, IMultiblockMatcher> matchers = new HashMap<>();
    
    public IndustrialFurnacePattern() {
        matchers.put('C', MultiblockMatcher.block(ModBlocks.FURNACE_CASING.get()));
        matchers.put('F', MultiblockMatcher.block(Blocks.FURNACE));
        matchers.put(' ', MultiblockMatcher.block(Blocks.AIR));
    }
    
    @Override
    public String getPatternId() {
        return "industrial_furnace";
    }
    
    @Override
    public int getWidth() {
        return 3;
    }
    
    @Override
    public int getHeight() {
        return 2;
    }
    
    @Override
    public int getDepth() {
        return 3;
    }
    
    @Override
    public BlockPos getMasterOffset() {
        return new BlockPos(1, 0, 1);
    }
    
    @Override
    public Map<Character, IMultiblockMatcher> getMatchers() {
        return matchers;
    }
    
    @Override
    public String[][][] getPattern() {
        return PATTERN;
    }
}