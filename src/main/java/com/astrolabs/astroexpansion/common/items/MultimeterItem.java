package com.astrolabs.astroexpansion.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class MultimeterItem extends Item {
    
    public MultimeterItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        tooltip.add(Component.translatable("tooltip.astroexpansion.multimeter.desc")
            .withStyle(ChatFormatting.GRAY));
        
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.astroexpansion.multimeter.usage")
            .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.literal("  ")
            .append(Component.translatable("tooltip.astroexpansion.multimeter.measure")
                .withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal("  ")
            .append(Component.translatable("tooltip.astroexpansion.multimeter.network")
                .withStyle(ChatFormatting.GRAY)));
    }
}