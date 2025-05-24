package com.astrolabs.astroexpansion.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConduitBlockItem extends BlockItem {
    private final int transferRate;
    private final String conduitType;
    
    public ConduitBlockItem(Block block, Properties properties, int transferRate, String conduitType) {
        super(block, properties);
        this.transferRate = transferRate;
        this.conduitType = conduitType;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // Conduit type header
        tooltip.add(Component.literal(conduitType + " Conduit")
            .withStyle(ChatFormatting.AQUA));
        
        // Transfer rate
        String rateText = conduitType.equals("Energy") ? transferRate + " FE/tick" :
                          conduitType.equals("Fluid") ? transferRate + " mB/tick" :
                          transferRate + " items/tick";
        
        tooltip.add(Component.literal("Transfer Rate: ")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal(rateText)
                .withStyle(ChatFormatting.YELLOW)));
        
        // Connection info
        tooltip.add(Component.literal("Auto-connects to compatible blocks")
            .withStyle(ChatFormatting.DARK_GRAY));
        
        // Additional info when shift is held
        if (!flag.isAdvanced() && !hasShiftDown()) {
            tooltip.add(Component.literal("Hold SHIFT for details")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        } else if (hasShiftDown()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.literal("Details:")
                .withStyle(ChatFormatting.YELLOW));
            
            // Features
            tooltip.add(Component.literal("  • Lossless transfer")
                .withStyle(ChatFormatting.GREEN));
            tooltip.add(Component.literal("  • Smart routing")
                .withStyle(ChatFormatting.GREEN));
            tooltip.add(Component.literal("  • No power required")
                .withStyle(ChatFormatting.GREEN));
            
            // Network info
            tooltip.add(Component.empty());
            tooltip.add(Component.literal("Network Behavior:")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
            tooltip.add(Component.literal("  • Forms networks automatically")
                .withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(Component.literal("  • Balances " + conduitType.toLowerCase() + " across connected blocks")
                .withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(Component.literal("  • Priority: Machines > Storage")
                .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
    
    private boolean hasShiftDown() {
        return net.minecraft.client.Minecraft.getInstance().options.keyShift.isDown();
    }
}