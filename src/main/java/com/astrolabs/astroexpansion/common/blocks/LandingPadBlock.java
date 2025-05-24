package com.astrolabs.astroexpansion.common.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Landing pad for cargo shuttles
 * Must be placed in 3x3 configuration
 */
public class LandingPadBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 1, 16);
    
    public LandingPadBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Landing surface for spacecraft").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Place in 3x3 pattern for Cargo Shuttle").withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Safe landing guaranteed!").withStyle(ChatFormatting.GREEN));
    }
}