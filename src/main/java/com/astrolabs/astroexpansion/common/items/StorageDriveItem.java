package com.astrolabs.astroexpansion.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StorageDriveItem extends Item {
    private final int capacity;
    private final String tier;
    
    public StorageDriveItem(Properties properties, int capacity, String tier) {
        super(properties.stacksTo(1));
        this.capacity = capacity;
        this.tier = tier;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // Storage tier header
        tooltip.add(Component.literal("Storage Drive (" + tier.toUpperCase() + ")")
            .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.empty());
        
        CompoundTag tag = stack.getOrCreateTag();
        int used = getUsedCapacity(stack);
        int types = getStoredTypes(stack);
        
        // Capacity information with formatted numbers
        tooltip.add(Component.literal("Capacity: ")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal(String.format("%,d", used) + " / " + String.format("%,d", capacity) + " items")
                .withStyle(ChatFormatting.WHITE)));
        
        // Type information
        tooltip.add(Component.literal("Item Types: ")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal(types + " / 63")
                .withStyle(types >= 63 ? ChatFormatting.RED : ChatFormatting.WHITE)));
        
        // Usage bar and percentage
        if (capacity > 0) {
            double percentage = (double) used / capacity * 100;
            ChatFormatting color = percentage > 90 ? ChatFormatting.RED : 
                                  percentage > 75 ? ChatFormatting.YELLOW : 
                                  percentage > 50 ? ChatFormatting.GOLD :
                                  ChatFormatting.GREEN;
            
            // Visual progress bar
            int barLength = 20;
            int filledBars = (int) (barLength * percentage / 100);
            StringBuilder progressBar = new StringBuilder("[");
            for (int i = 0; i < barLength; i++) {
                if (i < filledBars) {
                    progressBar.append("■");
                } else {
                    progressBar.append("□");
                }
            }
            progressBar.append("]");
            
            tooltip.add(Component.literal(progressBar.toString() + " " + String.format("%.1f%%", percentage))
                .withStyle(color));
        }
        
        // Additional info when shift is held
        if (flag.isAdvanced()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.literal("Hold SHIFT for details")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        } else if (hasShiftDown()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.literal("Details:")
                .withStyle(ChatFormatting.YELLOW));
            
            // Network status
            boolean inNetwork = tag.getBoolean("InNetwork");
            tooltip.add(Component.literal("  • Status: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(inNetwork ? "Connected" : "Not Connected")
                    .withStyle(inNetwork ? ChatFormatting.GREEN : ChatFormatting.RED)));
            
            // Power consumption when in network
            if (inNetwork) {
                tooltip.add(Component.literal("  • Power Usage: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(getPowerUsage() + " RF/t")
                        .withStyle(ChatFormatting.LIGHT_PURPLE)));
            }
            
            // Storage efficiency
            if (types > 0) {
                double avgItemsPerType = (double) used / types;
                tooltip.add(Component.literal("  • Avg Items/Type: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("%.1f", avgItemsPerType))
                        .withStyle(ChatFormatting.WHITE)));
            }
            
            // Tips
            tooltip.add(Component.empty());
            tooltip.add(Component.literal("Tips:")
                .withStyle(ChatFormatting.DARK_AQUA));
            tooltip.add(Component.literal("  • Each drive can store up to 63 different item types")
                .withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(Component.literal("  • Higher tier drives are more power efficient")
                .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
    
    private boolean hasShiftDown() {
        return net.minecraft.client.Minecraft.getInstance().options.keyShift.isDown();
    }
    
    private int getPowerUsage() {
        // Power usage scales with tier
        switch (tier) {
            case "1k": return 1;
            case "4k": return 2;
            case "16k": return 4;
            case "64k": return 8;
            default: return 1;
        }
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public String getTier() {
        return tier;
    }
    
    public static int getUsedCapacity(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("UsedCapacity")) {
            return tag.getInt("UsedCapacity");
        }
        return 0;
    }
    
    public static int getStoredTypes(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("StoredItems")) {
            return tag.getList("StoredItems", 10).size();
        }
        return 0;
    }
    
    public static ListTag getStoredItems(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("StoredItems")) {
            return tag.getList("StoredItems", 10);
        }
        return new ListTag();
    }
    
    public static void setStoredItems(ItemStack stack, ListTag items) {
        stack.getOrCreateTag().put("StoredItems", items);
    }
    
    public static void updateCapacity(ItemStack stack, int used) {
        stack.getOrCreateTag().putInt("UsedCapacity", used);
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return getUsedCapacity(stack) > 0;
    }
}