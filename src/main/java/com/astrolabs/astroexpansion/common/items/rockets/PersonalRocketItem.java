package com.astrolabs.astroexpansion.common.items.rockets;

import com.astrolabs.astroexpansion.common.entity.PersonalRocketEntity;
import com.astrolabs.astroexpansion.common.registry.ModDimensions;
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
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Personal Rocket - Mid game one-way transport to space
 * Simple, disposable, single-player vehicle
 */
public class PersonalRocketItem extends Item {
    
    public PersonalRocketItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult useOn(@javax.annotation.Nonnull UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        
        if (!level.isClientSide && player != null) {
            // Check if placed on a landing pad
            if (isValidLandingPad(level, pos)) {
                // Check if player has space suit
                if (!hasSpaceSuit(player)) {
                    player.displayClientMessage(
                        Component.literal("You need a space suit to use this rocket!").withStyle(ChatFormatting.RED), 
                        true
                    );
                    return InteractionResult.FAIL;
                }
                
                // Spawn personal rocket entity
                PersonalRocketEntity rocket = new PersonalRocketEntity(level, pos);
                level.addFreshEntity(rocket);
                
                // Make player ride the rocket
                player.startRiding(rocket);
                
                // Consume item
                context.getItemInHand().shrink(1);
                
                // Notify player
                player.displayClientMessage(
                    Component.literal("Boarding personal rocket. Hold SPACE to launch!").withStyle(ChatFormatting.GREEN), 
                    true
                );
                
                return InteractionResult.SUCCESS;
            } else {
                // Inform player they need a landing pad
                player.displayClientMessage(
                    Component.literal("Personal rockets require a landing pad (3x3 configuration)").withStyle(ChatFormatting.RED), 
                    true
                );
            }
        }
        
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    
    private boolean hasSpaceSuit(Player player) {
        // Check if player is wearing full space suit
        return player.getInventory().armor.stream()
            .anyMatch(stack -> !stack.isEmpty() && stack.getItem().getDescriptionId().contains("space_"));
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
        tooltip.add(Component.literal("One-way transport to space").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Cheap and disposable").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Requirements:").withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.literal("• Landing Pad (3x3)").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("• Space Suit").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("• Water fuel (1 bucket)").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Warning: One-time use!").withStyle(ChatFormatting.RED));
    }
}