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
        
        CompoundTag tag = stack.getOrCreateTag();
        int used = getUsedCapacity(stack);
        int types = getStoredTypes(stack);
        
        tooltip.add(Component.literal("Capacity: " + used + " / " + capacity + " items")
            .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Types: " + types + " / 63")
            .withStyle(ChatFormatting.GRAY));
        
        if (used > 0) {
            double percentage = (double) used / capacity * 100;
            ChatFormatting color = percentage > 90 ? ChatFormatting.RED : 
                                  percentage > 75 ? ChatFormatting.YELLOW : 
                                  ChatFormatting.GREEN;
            tooltip.add(Component.literal(String.format("%.1f%% full", percentage))
                .withStyle(color));
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