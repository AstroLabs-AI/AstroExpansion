package com.astrolabs.astroexpansion.common.blocks.machines.base;

import com.astrolabs.astroexpansion.common.items.upgrades.UpgradeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public interface IUpgradeable {
    /**
     * Get the upgrade inventory handler
     */
    IItemHandler getUpgradeInventory();
    
    /**
     * Get the maximum number of upgrade slots
     */
    int getMaxUpgradeSlots();
    
    /**
     * Check if an item is a valid upgrade for this machine
     */
    boolean isValidUpgrade(ItemStack stack);
    
    /**
     * Get all currently installed upgrades
     */
    List<UpgradeItem> getInstalledUpgrades();
    
    /**
     * Calculate the speed multiplier from installed upgrades
     */
    float getSpeedMultiplier();
    
    /**
     * Calculate the efficiency multiplier from installed upgrades
     */
    float getEfficiencyMultiplier();
    
    /**
     * Calculate the fortune multiplier from installed upgrades
     */
    float getFortuneMultiplier();
    
    /**
     * Called when upgrades are changed
     */
    void onUpgradesChanged();
}