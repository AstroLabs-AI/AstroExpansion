package com.astrolabs.astroexpansion.common.multiblock.patterns;

import com.astrolabs.astroexpansion.api.multiblock.IMultiblockMatcher;
import com.astrolabs.astroexpansion.api.multiblock.IMultiblockPattern;
import com.astrolabs.astroexpansion.api.multiblock.MultiblockMatcher;
import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class FusionReactorPattern implements IMultiblockPattern {
    private static final String[][][] PATTERN = {
        // Layer 0 (bottom)
        {
            {"LLLLL"},
            {"L   L"},
            {"L O L"},
            {"L   L"},
            {"LLLLL"}
        },
        // Layer 1
        {
            {"L   L"},
            {"     "},
            {"     "},
            {"     "},
            {"L   L"}
        },
        // Layer 2 (middle with controller)
        {
            {"LLLLL"},
            {"L   L"},
            {"L C L"},
            {"L   L"},
            {"LLLLL"}
        },
        // Layer 3
        {
            {"L   L"},
            {"     "},
            {"     "},
            {"     "},
            {"L   L"}
        },
        // Layer 4 (top)
        {
            {"LLLLL"},
            {"L   L"},
            {"L O L"},
            {"L   L"},
            {"LLLLL"}
        }
    };
    
    private final Map<Character, IMultiblockMatcher> matchers = new HashMap<>();
    
    public FusionReactorPattern() {
        matchers.put('C', MultiblockMatcher.block(ModBlocks.FUSION_REACTOR_CONTROLLER.get()));
        matchers.put('L', MultiblockMatcher.block(ModBlocks.FUSION_COIL.get()));
        matchers.put('O', MultiblockMatcher.block(ModBlocks.FUSION_CORE_BLOCK.get()));
        matchers.put(' ', MultiblockMatcher.block(Blocks.AIR));
    }
    
    @Override
    public String getPatternId() {
        return "fusion_reactor";
    }
    
    @Override
    public int getWidth() {
        return 5;
    }
    
    @Override
    public int getHeight() {
        return 5;
    }
    
    @Override
    public int getDepth() {
        return 5;
    }
    
    @Override
    public BlockPos getMasterOffset() {
        return new BlockPos(2, 2, 2); // Center of the structure
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