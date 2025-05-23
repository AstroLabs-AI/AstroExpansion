package com.astrolabs.astroexpansion.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class BaseContainerMenu extends AbstractContainerMenu {
    
    protected final Container container;
    protected final int containerSlots;
    
    protected BaseContainerMenu(MenuType<?> type, int containerId, Inventory playerInventory, Container container, int containerSlots) {
        super(type, containerId);
        this.container = container;
        this.containerSlots = containerSlots;
        
        container.startOpen(playerInventory.player);
        
        addContainerSlots();
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }
    
    protected abstract void addContainerSlots();
    
    protected void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
    }
    
    protected void addPlayerHotbar(Inventory playerInventory) {
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();
            
            if (index < containerSlots) {
                // Moving from container to player inventory
                if (!this.moveItemStackTo(slotStack, containerSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player inventory to container
                if (!this.moveItemStackTo(slotStack, 0, containerSlots, false)) {
                    return ItemStack.EMPTY;
                }
            }
            
            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        
        return itemstack;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }
    
    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }
}