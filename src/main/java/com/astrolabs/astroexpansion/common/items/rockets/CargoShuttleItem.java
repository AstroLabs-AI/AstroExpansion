package com.astrolabs.astroexpansion.common.items.rockets;

import com.astrolabs.astroexpansion.common.entity.CargoShuttleEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Cargo Shuttle - Reusable multi-purpose spacecraft
 * Can carry cargo and multiple players
 */
public class CargoShuttleItem extends Item {
    
    public CargoShuttleItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        
        if (!level.isClientSide && player != null) {
            // Check if we have a 3x3 landing pad
            if (isValidLandingPad(level, pos)) {
                // Spawn cargo shuttle entity
                CargoShuttleEntity shuttle = new CargoShuttleEntity(level, pos);
                level.addFreshEntity(shuttle);
                
                // Don't consume item - it's reusable!
                context.getItemInHand().shrink(1);
                
                // Notify player
                player.displayClientMessage(
                    Component.literal("Cargo shuttle deployed! Right-click to enter.").withStyle(ChatFormatting.GREEN), 
                    true
                );
                
                return InteractionResult.SUCCESS;
            } else {
                // Inform player they need a landing pad
                player.displayClientMessage(
                    Component.literal("Requires a 3x3 Landing Pad!").withStyle(ChatFormatting.RED), 
                    true
                );
            }
        }
        
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    
    private boolean isValidLandingPad(Level level, BlockPos center) {
        // Check for 3x3 area of landing pad blocks
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos checkPos = center.offset(x, 0, z);
                if (!level.getBlockState(checkPos).getBlock().getDescriptionId().contains("landing_pad")) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Reusable space vehicle").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("27-slot cargo hold").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Supports 2 players").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Features:").withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.literal("• Reusable (needs refueling)").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.literal("• Can reach Moon").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.literal("• Auto-landing system").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Requires 3x3 Landing Pad").withStyle(ChatFormatting.AQUA));
    }
}