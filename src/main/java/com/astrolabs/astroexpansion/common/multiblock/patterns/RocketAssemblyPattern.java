package com.astrolabs.astroexpansion.common.multiblock.patterns;

import com.astrolabs.astroexpansion.api.multiblock.IMultiblockMatcher;
import com.astrolabs.astroexpansion.api.multiblock.IMultiblockPattern;
import com.astrolabs.astroexpansion.api.multiblock.MultiblockMatcher;
import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class RocketAssemblyPattern implements IMultiblockPattern {
    private static final String[][][] PATTERN = {
        // Layer 0 (bottom - launch pad) - 9x9
        {
            {"PPPPPPPPP"},
            {"PPPPPPPPP"},
            {"PPPPPPPPP"},
            {"PPPPPPPPP"},
            {"PPPPOPPPP"},
            {"PPPPPPPPP"},
            {"PPPPPPPPP"},
            {"PPPPPPPPP"},
            {"PPPPPPPPP"}
        },
        // Layers 1-4 (support structure)
        {
            {"S       S"},
            {"         "},
            {"         "},
            {"         "},
            {"    R    "},
            {"         "},
            {"         "},
            {"         "},
            {"S       S"}
        },
        {
            {"S       S"},
            {"         "},
            {"         "},
            {"         "},
            {"    R    "},
            {"         "},
            {"         "},
            {"         "},
            {"S       S"}
        },
        {
            {"S       S"},
            {"         "},
            {"         "},
            {"         "},
            {"    R    "},
            {"         "},
            {"         "},
            {"         "},
            {"S       S"}
        },
        {
            {"S       S"},
            {"         "},
            {"         "},
            {"         "},
            {"    R    "},
            {"         "},
            {"         "},
            {"         "},
            {"S       S"}
        },
        // Layers 5-9 (assembly area)
        {
            {"SSSSSSSSS"},
            {"S       S"},
            {"S       S"},
            {"S       S"},
            {"S   R   S"},
            {"S       S"},
            {"S       S"},
            {"S       S"},
            {"SSSSSSSSS"}
        },
        {
            {"S       S"},
            {"         "},
            {"         "},
            {"         "},
            {"    R    "},
            {"         "},
            {"         "},
            {"         "},
            {"S       S"}
        },
        {
            {"S       S"},
            {"         "},
            {"         "},
            {"         "},
            {"    R    "},
            {"         "},
            {"         "},
            {"         "},
            {"S       S"}
        },
        {
            {"S       S"},
            {"         "},
            {"         "},
            {"         "},
            {"    R    "},
            {"         "},
            {"         "},
            {"         "},
            {"S       S"}
        },
        {
            {"S       S"},
            {"         "},
            {"         "},
            {"         "},
            {"    R    "},
            {"         "},
            {"         "},
            {"         "},
            {"S       S"}
        },
        // Layers 10-14 (upper assembly)
        {
            {"SSSSSSSSS"},
            {"S       S"},
            {"S       S"},
            {"S       S"},
            {"S   R   S"},
            {"S       S"},
            {"S       S"},
            {"S       S"},
            {"SSSSSSSSS"}
        },
        {
            {"S       S"},
            {"         "},
            {"         "},
            {"         "},
            {"    R    "},
            {"         "},
            {"         "},
            {"         "},
            {"S       S"}
        },
        {
            {"S       S"},
            {"         "},
            {"         "},
            {"         "},
            {"    R    "},
            {"         "},
            {"         "},
            {"         "},
            {"S       S"}
        },
        {
            {"S       S"},
            {"         "},
            {"         "},
            {"         "},
            {"    R    "},
            {"         "},
            {"         "},
            {"         "},
            {"S       S"}
        },
        {
            {"S       S"},
            {"         "},
            {"         "},
            {"         "},
            {"    R    "},
            {"         "},
            {"         "},
            {"         "},
            {"S       S"}
        }
    };
    
    private final Map<Character, IMultiblockMatcher> matchers = new HashMap<>();
    
    public RocketAssemblyPattern() {
        matchers.put('O', MultiblockMatcher.block(ModBlocks.ROCKET_ASSEMBLY_CONTROLLER.get()));
        matchers.put('P', MultiblockMatcher.block(ModBlocks.LAUNCH_PAD.get()));
        matchers.put('S', MultiblockMatcher.block(ModBlocks.ASSEMBLY_FRAME.get()));
        matchers.put('R', MultiblockMatcher.block(Blocks.AIR)); // Rocket build area
        matchers.put(' ', MultiblockMatcher.block(Blocks.AIR));
    }
    
    @Override
    public String getPatternId() {
        return "rocket_assembly";
    }
    
    @Override
    public int getWidth() {
        return 9;
    }
    
    @Override
    public int getHeight() {
        return 15;
    }
    
    @Override
    public int getDepth() {
        return 9;
    }
    
    @Override
    public BlockPos getMasterOffset() {
        return new BlockPos(4, 0, 4); // Center of launch pad
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