package com.astrolabs.astroexpansion.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class FurnaceCasingBlock extends MultiblockComponentBlock {
    
    public FurnaceCasingBlock() {
        super(Block.Properties.of()
            .mapColor(MapColor.METAL)
            .strength(3.5F)
            .sound(SoundType.METAL)
            .requiresCorrectToolForDrops());
    }
}