package com.astrolabs.astroexpansion.common.items;

import com.astrolabs.astroexpansion.common.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;

public class MachineBlockItem extends BlockItem {
    private final MachineTooltipData tooltipData;
    
    public MachineBlockItem(Block block, Properties properties, MachineTooltipData tooltipData) {
        super(block, properties);
        this.tooltipData = tooltipData;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // Basic description
        if (tooltipData.descriptionKey != null) {
            tooltip.add(Component.translatable(tooltipData.descriptionKey)
                .withStyle(ChatFormatting.GRAY));
        }
        
        // Show detailed info when shift is held
        if (TooltipHelper.addShiftTooltip(tooltip, Screen.hasShiftDown())) {
            // Energy info
            if (tooltipData.energyCapacity > 0 || tooltipData.energyPerOperation > 0) {
                TooltipHelper.addEnergyInfo(tooltip, tooltipData.energyCapacity, tooltipData.energyPerOperation);
            }
            
            // Processing info
            if (tooltipData.processingTime > 0) {
                TooltipHelper.addProcessingInfo(tooltip, tooltipData.processingTime, tooltipData.speedMultiplier);
            }
            
            // Tier info
            if (tooltipData.tier != null) {
                TooltipHelper.addTierInfo(tooltip, tooltipData.tier);
            }
            
            // Upgrade slots
            if (tooltipData.upgradeSlots > 0) {
                TooltipHelper.addUpgradeInfo(tooltip, tooltipData.upgradeSlots);
            }
            
            // Efficiency
            if (tooltipData.efficiency != 1.0f) {
                TooltipHelper.addEfficiencyInfo(tooltip, tooltipData.efficiency);
            }
            
            // Features
            if (tooltipData.features != null && tooltipData.features.length > 0) {
                TooltipHelper.addFeatures(tooltip, tooltipData.features);
            }
            
            // Multiblock info
            if (tooltipData.multiblockStructure != null) {
                TooltipHelper.addMultiblockInfo(tooltip, tooltipData.multiblockStructure, tooltipData.multiblockSize);
            }
            
            // Storage info
            if (tooltipData.storageSlots > 0 || tooltipData.storageCapacity > 0) {
                TooltipHelper.addStorageInfo(tooltip, tooltipData.storageSlots, tooltipData.storageCapacity);
            }
            
            // Warnings
            if (tooltipData.warnings != null) {
                for (String warning : tooltipData.warnings) {
                    TooltipHelper.addWarning(tooltip, warning);
                }
            }
            
            // Requirements
            if (tooltipData.requirements != null) {
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable("tooltip.astroexpansion.requirements")
                    .withStyle(ChatFormatting.RED));
                for (String req : tooltipData.requirements) {
                    TooltipHelper.addRequirement(tooltip, req);
                }
            }
        }
    }
    
    public static class MachineTooltipData {
        public String descriptionKey;
        public int energyCapacity;
        public int energyPerOperation;
        public int processingTime;
        public float speedMultiplier = 1.0f;
        public float efficiency = 1.0f;
        public String tier;
        public int upgradeSlots;
        public String[] features;
        public String multiblockStructure;
        public String multiblockSize;
        public int storageSlots;
        public int storageCapacity;
        public String[] warnings;
        public String[] requirements;
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private final MachineTooltipData data = new MachineTooltipData();
            
            public Builder description(String key) {
                data.descriptionKey = key;
                return this;
            }
            
            public Builder energy(int capacity, int perOperation) {
                data.energyCapacity = capacity;
                data.energyPerOperation = perOperation;
                return this;
            }
            
            public Builder processing(int time) {
                data.processingTime = time;
                return this;
            }
            
            public Builder speed(float multiplier) {
                data.speedMultiplier = multiplier;
                return this;
            }
            
            public Builder efficiency(float eff) {
                data.efficiency = eff;
                return this;
            }
            
            public Builder tier(String tier) {
                data.tier = tier;
                return this;
            }
            
            public Builder upgradeSlots(int slots) {
                data.upgradeSlots = slots;
                return this;
            }
            
            public Builder features(String... features) {
                data.features = features;
                return this;
            }
            
            public Builder multiblock(String structure, String size) {
                data.multiblockStructure = structure;
                data.multiblockSize = size;
                return this;
            }
            
            public Builder storage(int slots, int capacity) {
                data.storageSlots = slots;
                data.storageCapacity = capacity;
                return this;
            }
            
            public Builder warnings(String... warnings) {
                data.warnings = warnings;
                return this;
            }
            
            public Builder requirements(String... requirements) {
                data.requirements = requirements;
                return this;
            }
            
            public MachineTooltipData build() {
                return data;
            }
        }
    }
}