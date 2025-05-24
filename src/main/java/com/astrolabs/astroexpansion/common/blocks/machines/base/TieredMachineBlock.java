package com.astrolabs.astroexpansion.common.blocks.machines.base;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public abstract class TieredMachineBlock extends Block implements EntityBlock {
    protected final MachineTier tier;

    public TieredMachineBlock(Properties properties, MachineTier tier) {
        super(properties);
        this.tier = tier;
    }

    public MachineTier getTier() {
        return tier;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // Add tier information
        tooltip.add(Component.translatable("tooltip.astroexpansion.tier", tier.getDisplayName()));
        
        // Add speed information
        if (tier.getSpeedMultiplier() > 1.0f) {
            tooltip.add(Component.translatable("tooltip.astroexpansion.speed_multiplier", 
                String.format("%.1fx", tier.getSpeedMultiplier())).withStyle(ChatFormatting.GREEN));
        }
        
        // Add efficiency information
        if (tier.getEfficiencyMultiplier() > 1.0f) {
            tooltip.add(Component.translatable("tooltip.astroexpansion.efficiency_multiplier", 
                String.format("%.0f%%", tier.getEfficiencyMultiplier() * 100)).withStyle(ChatFormatting.AQUA));
        }
        
        // Add upgrade slots
        tooltip.add(Component.translatable("tooltip.astroexpansion.upgrade_slots", 
            tier.getUpgradeSlots()).withStyle(ChatFormatting.GOLD));
        
        // Add energy capacity
        tooltip.add(Component.translatable("tooltip.astroexpansion.energy_capacity", 
            tier.getEnergyCapacity()).withStyle(ChatFormatting.YELLOW));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}