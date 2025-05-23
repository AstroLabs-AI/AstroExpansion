package com.astrolabs.astroexpansion.common.multiblock.patterns;

import com.astrolabs.astroexpansion.api.multiblock.IMultiblockMatcher;
import com.astrolabs.astroexpansion.api.multiblock.IMultiblockPattern;
import com.astrolabs.astroexpansion.api.multiblock.MultiblockMatcher;
import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class FuelRefineryPattern implements IMultiblockPattern {
    private static final String[][][] PATTERN = {
        // Layer 0 (bottom) - 5x5
        {
            {"CCCCC"},
            {"CCCCC"},
            {"CCCCC"},
            {"CCCCC"},
            {"CCCCC"}
        },
        // Layer 1 (middle with controller)
        {
            {"CCDCC"},
            {"C   C"},
            {"D O D"},
            {"C   C"},
            {"CCDCC"}
        },
        // Layer 2 (top)
        {
            {"CCDCC"},
            {"C   C"},
            {"D   D"},
            {"C   C"},
            {"CCDCC"}
        }
    };
    
    private final Map<Character, IMultiblockMatcher> matchers = new HashMap<>();
    
    public FuelRefineryPattern() {
        matchers.put('O', MultiblockMatcher.block(ModBlocks.FUEL_REFINERY_CONTROLLER.get()));
        matchers.put('C', MultiblockMatcher.block(ModBlocks.REFINERY_CASING.get()));
        matchers.put('D', MultiblockMatcher.block(ModBlocks.DISTILLATION_COLUMN.get()));
        matchers.put(' ', MultiblockMatcher.block(Blocks.AIR));
    }
    
    @Override
    public String getPatternId() {
        return "fuel_refinery";
    }
    
    @Override
    public int getWidth() {
        return 5;
    }
    
    @Override
    public int getHeight() {
        return 3;
    }
    
    @Override
    public int getDepth() {
        return 5;
    }
    
    @Override
    public BlockPos getMasterOffset() {
        return new BlockPos(2, 1, 2); // Center of the middle layer
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