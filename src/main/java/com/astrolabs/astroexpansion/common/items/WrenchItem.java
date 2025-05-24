package com.astrolabs.astroexpansion.common.items;

import com.astrolabs.astroexpansion.api.multiblock.IMultiblockController;
// import com.astrolabs.astroexpansion.common.util.MultiblockBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.ChatFormatting;
import javax.annotation.Nullable;
import java.util.List;

public class WrenchItem extends Item {
    
    public WrenchItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        
        if (player == null) return InteractionResult.PASS;
        
        BlockState state = level.getBlockState(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        
        // Check for multiblock controller with shift-click
        if (player.isShiftKeyDown() && blockEntity instanceof IMultiblockController controller) {
            if (!level.isClientSide) {
                // Force structure check
                controller.checkFormation();
                
                if (controller.isFormed()) {
                    player.displayClientMessage(
                        Component.literal("Multiblock is already formed!"), true
                    );
                } else {
                    player.displayClientMessage(
                        Component.literal("Multiblock structure incomplete. Check pattern!"), true
                    );
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        
        // Normal wrench behavior - rotate blocks
        if (!player.isShiftKeyDown() && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            if (!level.isClientSide) {
                // Rotate block
                BlockState rotated = state.rotate(net.minecraft.world.level.block.Rotation.CLOCKWISE_90);
                level.setBlock(pos, rotated, 3);
                
                // Play sound
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, 
                    SoundSource.BLOCKS, 1.0F, 1.0F);
                
                // Damage wrench slightly
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        
        // Instant break with shift-click
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                // Check if it's a machine
                if (blockEntity != null) {
                    // Drop contents and block
                    Block.dropResources(state, level, pos, blockEntity);
                    level.removeBlock(pos, false);
                    
                    // Play sound
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, 
                        SoundSource.BLOCKS, 1.0F, 1.0F);
                    
                    // Damage wrench
                    stack.hurtAndBreak(2, player, p -> p.broadcastBreakEvent(context.getHand()));
                    
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        
        return InteractionResult.PASS;
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        // Make wrench glow when it has special features
        return true;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Universal Tool").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Right-click: ").withStyle(ChatFormatting.YELLOW)
            .append(Component.literal("Rotate blocks").withStyle(ChatFormatting.WHITE)));
        tooltip.add(Component.literal("Shift + Right-click: ").withStyle(ChatFormatting.YELLOW)
            .append(Component.literal("Instant break / Toggle preview").withStyle(ChatFormatting.WHITE)));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Multiblock Preview:").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal("  Hold to see structure ghost blocks").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("  ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal("Green").withStyle(ChatFormatting.GREEN))
            .append(Component.literal(" = Correct, ").withStyle(ChatFormatting.GRAY))
            .append(Component.literal("Red").withStyle(ChatFormatting.RED))
            .append(Component.literal(" = Wrong, ").withStyle(ChatFormatting.GRAY))
            .append(Component.literal("Blue").withStyle(ChatFormatting.BLUE))
            .append(Component.literal(" = Missing").withStyle(ChatFormatting.GRAY)));
    }
}