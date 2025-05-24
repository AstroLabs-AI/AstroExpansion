package com.astrolabs.astroexpansion.common.items.rockets;

import com.astrolabs.astroexpansion.common.entity.ProbeRocketEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Probe Rocket - Early game unmanned space exploration
 * Returns with random loot and discoveries
 */
public class ProbeRocketItem extends Item {
    
    public ProbeRocketItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        
        if (!level.isClientSide) {
            // Check if placed on a launch rail
            Block block = level.getBlockState(pos).getBlock();
            if (block.getDescriptionId().contains("launch_rail")) {
                // Spawn probe rocket entity
                ProbeRocketEntity probe = new ProbeRocketEntity(level, pos.above());
                level.addFreshEntity(probe);
                
                // Consume item
                context.getItemInHand().shrink(1);
                
                // Notify player
                if (context.getPlayer() != null) {
                    context.getPlayer().displayClientMessage(
                        Component.literal("Probe rocket ready for launch! Right-click to launch.").withStyle(ChatFormatting.GREEN), 
                        true
                    );
                }
                
                return InteractionResult.SUCCESS;
            } else {
                // Inform player they need a launch rail
                if (context.getPlayer() != null) {
                    context.getPlayer().displayClientMessage(
                        Component.literal("Place on a Launch Rail to prepare for launch").withStyle(ChatFormatting.RED), 
                        true
                    );
                }
            }
        }
        
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Unmanned exploration probe").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Returns with discoveries and loot").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Requirements:").withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.literal("• Launch Rail").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("• Coal fuel (consumed)").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Place on Launch Rail to use").withStyle(ChatFormatting.GREEN));
    }
}