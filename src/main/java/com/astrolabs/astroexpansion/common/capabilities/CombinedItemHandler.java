package com.astrolabs.astroexpansion.common.capabilities;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

/**
 * A combined item handler that presents multiple item handlers as a single handler.
 * Used to combine main inventory and upgrade slots.
 */
public class CombinedItemHandler implements IItemHandler {
    private final IItemHandler mainHandler;
    private final IItemHandler upgradeHandler;
    private final int mainSlots;
    
    public CombinedItemHandler(IItemHandler mainHandler, IItemHandler upgradeHandler) {
        this.mainHandler = mainHandler;
        this.upgradeHandler = upgradeHandler;
        this.mainSlots = mainHandler.getSlots();
    }
    
    @Override
    public int getSlots() {
        return mainHandler.getSlots() + upgradeHandler.getSlots();
    }
    
    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        if (slot < mainSlots) {
            return mainHandler.getStackInSlot(slot);
        } else {
            return upgradeHandler.getStackInSlot(slot - mainSlots);
        }
    }
    
    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (slot < mainSlots) {
            return mainHandler.insertItem(slot, stack, simulate);
        } else {
            return upgradeHandler.insertItem(slot - mainSlots, stack, simulate);
        }
    }
    
    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot < mainSlots) {
            return mainHandler.extractItem(slot, amount, simulate);
        } else {
            return upgradeHandler.extractItem(slot - mainSlots, amount, simulate);
        }
    }
    
    @Override
    public int getSlotLimit(int slot) {
        if (slot < mainSlots) {
            return mainHandler.getSlotLimit(slot);
        } else {
            return upgradeHandler.getSlotLimit(slot - mainSlots);
        }
    }
    
    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (slot < mainSlots) {
            return mainHandler.isItemValid(slot, stack);
        } else {
            return upgradeHandler.isItemValid(slot - mainSlots, stack);
        }
    }
}