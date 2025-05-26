package com.astrolabs.astroexpansion.common.blocks;

import com.astrolabs.astroexpansion.common.entity.ProbeRocketEntity;
import com.astrolabs.astroexpansion.common.entity.PersonalRocketEntity;
import com.astrolabs.astroexpansion.common.registry.ModItems;
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
import javax.annotation.Nonnull;
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
 * Simple one-block launch system for probe rockets
 */
public class LaunchRailBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 2, 16);
    
    public LaunchRailBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
      @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE;
    }
      @Override
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, 
                               @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if (!level.isClientSide) {
            // Check if there's a rocket on the rail
            // Expanded AABB to ensure proper detection of rockets at Y+1.6 position
            AABB searchBox = new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ(), pos.getX() + 1, pos.getY() + 3, pos.getZ() + 1);
            List<Entity> entities = level.getEntities(null, searchBox);
            
            System.out.println("Launch rail at " + pos + " checking for rockets in box: " + searchBox);
            System.out.println("Found " + entities.size() + " entities");
              for (Entity entity : entities) {
                System.out.println("Found entity: " + entity.getType().getDescriptionId() + " at " + entity.position());
                if (entity instanceof ProbeRocketEntity probe) {
                    // Launch probe rocket
                    probe.launch();
                    player.displayClientMessage(
                        Component.literal("Probe launched! It will return in 60 seconds.").withStyle(ChatFormatting.GREEN),
                        true
                    );
                    return InteractionResult.SUCCESS;                } else if (entity instanceof PersonalRocketEntity) {
                    // Inform player to use landing pads instead
                    player.displayClientMessage(
                        Component.literal("Personal rockets must be placed on landing pads!").withStyle(ChatFormatting.RED),
                        true
                    );
                    // Remove the entity since it's in the wrong place
                    entity.discard();
                    // Give the player back their rocket item
                    player.addItem(new ItemStack(ModItems.PERSONAL_ROCKET.get()));
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
            } else {                player.displayClientMessage(
                    Component.literal("No rocket detected on launch rail - try placing it again").withStyle(ChatFormatting.RED),
                    true
                );
            }
        }
        
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    
    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
    
    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
      @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable BlockGetter level, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        tooltip.add(Component.literal("Simple launch system for probe rockets").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Supports:").withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.literal("• Probe Rockets").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Personal Rockets require landing pads").withStyle(ChatFormatting.RED));
        tooltip.add(Component.literal("Right-click to launch").withStyle(ChatFormatting.GREEN));
    }
}