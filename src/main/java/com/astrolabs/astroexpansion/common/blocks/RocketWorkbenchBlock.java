package com.astrolabs.astroexpansion.common.blocks;

import com.astrolabs.astroexpansion.common.blockentities.RocketWorkbenchBlockEntity;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Single block crafting station for all rocket types
 * Simplifies rocket construction with visual interface
 */
public class RocketWorkbenchBlock extends BaseEntityBlock {
    
    public RocketWorkbenchBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RocketWorkbenchBlockEntity workbench && player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, workbench, pos);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RocketWorkbenchBlockEntity(pos, state);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.ROCKET_WORKBENCH.get(), RocketWorkbenchBlockEntity::tick);
    }
    
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof RocketWorkbenchBlockEntity workbench) {
                workbench.dropContents();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("All-in-one rocket crafting").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("Craft all rocket types here").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Available Rockets:").withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.literal("• Probe Rocket (Early)").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.literal("• Personal Rocket (Mid)").withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.literal("• Cargo Shuttle (Late)").withStyle(ChatFormatting.RED));
    }
}