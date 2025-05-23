package com.astrolabs.astroexpansion.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class FusionReactorCasingBlock extends MultiblockComponentBlock {
    public FusionReactorCasingBlock() {
        super(Block.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(10.0F, 1200.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops());
    }
}