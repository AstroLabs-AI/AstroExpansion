package com.astrolabs.astroexpansion.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class TeleporterFrequencyCardItem extends Item {
    public TeleporterFrequencyCardItem() {
        super(new Item.Properties().stacksTo(1));
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide && player.isShiftKeyDown()) {
            // Generate new frequency
            String newFrequency = UUID.randomUUID().toString().substring(0, 8);
            setFrequency(stack, newFrequency);
            player.displayClientMessage(
                Component.literal("Frequency set to: " + newFrequency)
                    .withStyle(ChatFormatting.GREEN), 
                true
            );
            return InteractionResultHolder.success(stack);
        }
        
        return InteractionResultHolder.pass(stack);
    }
    
    public static void setFrequency(ItemStack stack, String frequency) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("frequency", frequency);
    }
    
    public static String getFrequency(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("frequency")) {
            return stack.getTag().getString("frequency");
        }
        return "";
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        String frequency = getFrequency(stack);
        if (!frequency.isEmpty()) {
            tooltip.add(Component.literal("Frequency: " + frequency).withStyle(ChatFormatting.AQUA));
        } else {
            tooltip.add(Component.literal("Shift + Right-click to set frequency").withStyle(ChatFormatting.GRAY));
        }
        
        tooltip.add(Component.literal("Used to link teleporters").withStyle(ChatFormatting.DARK_GRAY));
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return !getFrequency(stack).isEmpty();
    }
}