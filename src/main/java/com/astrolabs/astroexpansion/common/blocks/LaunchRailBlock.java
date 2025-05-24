package com.astrolabs.astroexpansion.common.blocks;

import com.astrolabs.astroexpansion.common.entity.ProbeRocketEntity;
import com.astrolabs.astroexpansion.common.entity.PersonalRocketEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Simple one-block launch system for Tier 1-2 rockets
 */
public class LaunchRailBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 2, 16);
    
    public LaunchRailBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            // Check if there's a rocket on the rail
            List<Entity> entities = level.getEntities(null, new AABB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1));
            
            for (Entity entity : entities) {
                if (entity instanceof ProbeRocketEntity probe) {
                    // Launch probe rocket
                    probe.launch();
                    player.displayClientMessage(
                        Component.literal("Probe launched! It will return in 60 seconds.").withStyle(ChatFormatting.GREEN),
                        true
                    );
                    return InteractionResult.SUCCESS;
                } else if (entity instanceof PersonalRocketEntity rocket) {
                    // Check if player is riding
                    if (rocket.getPassengers().contains(player)) {
                        player.displayClientMessage(
                            Component.literal("Hold SPACE to launch!").withStyle(ChatFormatting.YELLOW),
                            true
                        );
                    } else {
                        player.displayClientMessage(
                            Component.literal("Right-click the rocket to board it first").withStyle(ChatFormatting.RED),
                            true
                        );
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            
            // No rocket found
            ItemStack heldItem = player.getItemInHand(hand);
            if (heldItem.getItem().getDescriptionId().contains("rocket")) {
                player.displayClientMessage(
                    Component.literal("Right-click with a rocket item to place it").withStyle(ChatFormatting.YELLOW),
                    true
                );
            } else {
                player.displayClientMessage(
                    Component.literal("No rocket on launch rail").withStyle(ChatFormatting.RED),
                    true
                );
            }
        }
        
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Simple rocket launcher").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Supports:").withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.literal("• Probe Rockets").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("• Personal Rockets").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Right-click to launch").withStyle(ChatFormatting.GREEN));
    }
}