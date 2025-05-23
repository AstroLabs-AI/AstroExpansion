package com.astrolabs.astroexpansion.common.multiblock.patterns;

import com.astrolabs.astroexpansion.api.multiblock.IMultiblockMatcher;
import com.astrolabs.astroexpansion.api.multiblock.IMultiblockPattern;
import com.astrolabs.astroexpansion.api.multiblock.MultiblockMatcher;
import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class QuantumComputerPattern implements IMultiblockPattern {
    private static final String[][][] PATTERN = {
        // Layer 0 (bottom) - 7x7
        {
            {"QQQQQQQ"},
            {"Q     Q"},
            {"Q     Q"},
            {"Q     Q"},
            {"Q     Q"},
            {"Q     Q"},
            {"QQQQQQQ"}
        },
        // Layer 1
        {
            {"Q     Q"},
            {"       "},
            {"  PPP  "},
            {"  PCP  "},
            {"  PPP  "},
            {"       "},
            {"Q     Q"}
        },
        // Layer 2
        {
            {"Q     Q"},
            {"       "},
            {"  P P  "},
            {"   C   "},
            {"  P P  "},
            {"       "},
            {"Q     Q"}
        },
        // Layer 3 (middle with controller)
        {
            {"QQQQQQQ"},
            {"Q     Q"},
            {"Q P P Q"},
            {"Q COC Q"},
            {"Q P P Q"},
            {"Q     Q"},
            {"QQQQQQQ"}
        },
        // Layer 4
        {
            {"Q     Q"},
            {"       "},
            {"  P P  "},
            {"   C   "},
            {"  P P  "},
            {"       "},
            {"Q     Q"}
        },
        // Layer 5
        {
            {"Q     Q"},
            {"       "},
            {"  PPP  "},
            {"  PCP  "},
            {"  PPP  "},
            {"       "},
            {"Q     Q"}
        },
        // Layer 6 (top) - 7x7
        {
            {"QQQQQQQ"},
            {"Q     Q"},
            {"Q     Q"},
            {"Q     Q"},
            {"Q     Q"},
            {"Q     Q"},
            {"QQQQQQQ"}
        }
    };
    
    private final Map<Character, IMultiblockMatcher> matchers = new HashMap<>();
    
    public QuantumComputerPattern() {
        matchers.put('O', MultiblockMatcher.block(ModBlocks.QUANTUM_COMPUTER_CONTROLLER.get()));
        matchers.put('Q', MultiblockMatcher.block(ModBlocks.QUANTUM_CASING.get()));
        matchers.put('P', MultiblockMatcher.block(ModBlocks.QUANTUM_PROCESSOR.get()));
        matchers.put('C', MultiblockMatcher.block(ModBlocks.QUANTUM_CORE.get()));
        matchers.put(' ', MultiblockMatcher.block(Blocks.AIR));
    }
    
    @Override
    public String getPatternId() {
        return "quantum_computer";
    }
    
    @Override
    public int getWidth() {
        return 7;
    }
    
    @Override
    public int getHeight() {
        return 7;
    }
    
    @Override
    public int getDepth() {
        return 7;
    }
    
    @Override
    public BlockPos getMasterOffset() {
        return new BlockPos(3, 3, 3); // Center of the structure
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