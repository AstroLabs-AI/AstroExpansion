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

public class BusBlockItem extends BlockItem {
    private final boolean isImport;
    private final int itemsPerOperation;
    private final int ticksPerOperation;
    
    public BusBlockItem(Block block, Properties properties, boolean isImport) {
        super(block, properties);
        this.isImport = isImport;
        this.itemsPerOperation = 1; // Can be upgraded
        this.ticksPerOperation = 20; // 1 second base
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // Bus type header
        String busType = isImport ? "Import" : "Export";
        tooltip.add(Component.literal(busType + " Bus")
            .withStyle(ChatFormatting.AQUA));
        
        // Function
        String function = isImport ? "Pulls items from adjacent inventory" : "Pushes items to adjacent inventory";
        tooltip.add(Component.literal(function)
            .withStyle(ChatFormatting.GRAY));
        
        // Transfer rate
        tooltip.add(Component.literal("Base Rate: ")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal(itemsPerOperation + " item/sec")
                .withStyle(ChatFormatting.YELLOW)));
        
        // Power consumption
        tooltip.add(Component.literal("Power: ")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal("2 FE/item")
                .withStyle(ChatFormatting.RED)));
        
        // Additional info when shift is held
        if (!flag.isAdvanced() && !hasShiftDown()) {
            tooltip.add(Component.literal("Hold SHIFT for details")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        } else if (hasShiftDown()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.literal("Configuration:")
                .withStyle(ChatFormatting.YELLOW));
            
            // Features
            if (isImport) {
                tooltip.add(Component.literal("  • Filters: Up to 9 item types")
                    .withStyle(ChatFormatting.GREEN));
                tooltip.add(Component.literal("  • Modes: Whitelist/Blacklist")
                    .withStyle(ChatFormatting.GREEN));
                tooltip.add(Component.literal("  • Priority: Configurable (0-999)")
                    .withStyle(ChatFormatting.GREEN));
            } else {
                tooltip.add(Component.literal("  • Stock Mode: Keep X items in inventory")
                    .withStyle(ChatFormatting.GREEN));
                tooltip.add(Component.literal("  • Crafting Card: Auto-craft missing items")
                    .withStyle(ChatFormatting.GREEN));
                tooltip.add(Component.literal("  • Fuzzy Mode: Ignore NBT/damage")
                    .withStyle(ChatFormatting.GREEN));
            }
            
            // Upgrade info
            tooltip.add(Component.empty());
            tooltip.add(Component.literal("Accepts Upgrades:")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
            tooltip.add(Component.literal("  • Speed: Up to 64 items/operation")
                .withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(Component.literal("  • Stack: Transfer full stacks")
                .withStyle(ChatFormatting.DARK_GRAY));
            
            // Tips
            tooltip.add(Component.empty());
            tooltip.add(Component.literal("Tip: ")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal(isImport ? 
                    "Use filters to control what enters your storage" : 
                    "Use stock mode to keep machines supplied")
                    .withStyle(ChatFormatting.GRAY)));
        }
    }
    
    private boolean hasShiftDown() {
        return net.minecraft.client.Minecraft.getInstance().options.keyShift.isDown();
    }
}