package com.astrolabs.astroexpansion.api.storage;

import net.minecraft.world.item.ItemStack;
import java.util.List;

/**
 * Interface for digital storage network operations
 */
public interface IStorageNetwork {
    /**
     * Insert an item into the storage network
     * @param stack The ItemStack to insert
     * @param simulate If true, simulate the insertion without actually inserting
     * @return The number of items that were (or would be) inserted
     */
    int insertItem(ItemStack stack, boolean simulate);
    
    /**
     * Extract an item from the storage network
     * @param stack The ItemStack to match (amount determines max extraction)
     * @param simulate If true, simulate the extraction without actually extracting
     * @return The ItemStack that was extracted
     */
    ItemStack extractItem(ItemStack stack, boolean simulate);
    
    /**
     * Get a list of all stored items in the network
     * @return List of ItemStacks representing stored items
     */
    List<ItemStack> getStoredItems();
    
    /**
     * Get the total number of items stored
     * @return Total item count
     */
    long getTotalItemCount();
    
    /**
     * Get the total storage capacity
     * @return Total capacity in items
     */
    long getTotalCapacity();
    
    /**
     * Check if the network can accept more items
     * @return true if space is available
     */
    boolean hasSpace();
    
    /**
     * Get the energy consumption of the network
     * @return FE per tick consumed
     */
    int getEnergyConsumption();
}