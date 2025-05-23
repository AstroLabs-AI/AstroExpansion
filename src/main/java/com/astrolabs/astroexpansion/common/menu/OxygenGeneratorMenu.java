package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.OxygenGeneratorBlockEntity;
import com.astrolabs.astroexpansion.common.registry.AEMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class OxygenGeneratorMenu extends AbstractContainerMenu {
    private final OxygenGeneratorBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    
    public OxygenGeneratorMenu(int id, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(id, playerInventory, (OxygenGeneratorBlockEntity) playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }
    
    public OxygenGeneratorMenu(int id, Inventory playerInventory, OxygenGeneratorBlockEntity blockEntity) {
        super(AEMenuTypes.OXYGEN_GENERATOR.get(), id);
        this.blockEntity = blockEntity;
        this.level = playerInventory.player.level();
        this.data = new SimpleContainerData(8);
        
        this.addSlot(new Slot(blockEntity, 0, 44, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.WATER_BUCKET);
            }
        });
        
        this.addSlot(new Slot(blockEntity, 1, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        
        addDataSlots(data);
    }
    
    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }
    
    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            
            if (index < 2) {
                if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (itemstack1.is(Items.WATER_BUCKET)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 2 && index < 29) {
                    if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 29 && index < 38 && !this.moveItemStackTo(itemstack1, 2, 29, false)) {
                    return ItemStack.EMPTY;
                }
            }
            
            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            
            slot.onTake(player, itemstack1);
        }
        
        return itemstack;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return blockEntity.stillValid(player);
    }
    
    public int getEnergy() {
        return blockEntity.getEnergyStored();
    }
    
    public int getMaxEnergy() {
        return blockEntity.getMaxEnergyStored();
    }
    
    public int getWater() {
        return blockEntity.getWaterAmount();
    }
    
    public int getMaxWater() {
        return blockEntity.getMaxWater();
    }
    
    public int getOxygen() {
        return blockEntity.getOxygenAmount();
    }
    
    public int getMaxOxygen() {
        return blockEntity.getMaxOxygen();
    }
    
    public int getProcessTime() {
        return blockEntity.getProcessTime();
    }
    
    public int getMaxProcessTime() {
        return blockEntity.getMaxProcessTime();
    }
    
    public int getEnergyStored() {
        return getEnergy();
    }
    
    public int getMaxEnergyStored() {
        return getMaxEnergy();
    }
    
    public boolean isGenerating() {
        return blockEntity.isGenerating();
    }
    
    public int getOxygenStored() {
        return getOxygen();
    }
    
    public int getMaxOxygenStored() {
        return getMaxOxygen();
    }
}