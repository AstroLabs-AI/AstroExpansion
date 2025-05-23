package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.RocketBlockEntity;
import com.astrolabs.astroexpansion.common.registry.AEMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RocketMenu extends AbstractContainerMenu {
    private final RocketBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    private final Inventory playerInventory;
    
    public RocketMenu(int id, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(id, playerInventory, (RocketBlockEntity) playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }
    
    public RocketMenu(int id, Inventory playerInventory, RocketBlockEntity blockEntity) {
        super(AEMenuTypes.ROCKET.get(), id);
        this.blockEntity = blockEntity;
        this.level = playerInventory.player.level();
        this.data = new SimpleContainerData(6);
        this.playerInventory = playerInventory;
        
        // Rocket cargo slots (3x9)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(blockEntity, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }
        
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
            
            if (index < 27) {
                // From rocket to player
                if (!this.moveItemStackTo(itemstack1, 27, 63, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From player to rocket
                if (!this.moveItemStackTo(itemstack1, 0, 27, false)) {
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
    
    public RocketBlockEntity getBlockEntity() {
        return blockEntity;
    }
    
    public int getFuel() {
        return blockEntity.getFuelAmount();
    }
    
    public int getMaxFuel() {
        return blockEntity.getMaxFuel();
    }
    
    public int getOxygen() {
        return blockEntity.getOxygenAmount();
    }
    
    public int getMaxOxygen() {
        return blockEntity.getMaxOxygen();
    }
    
    public int getTier() {
        return blockEntity.getTier();
    }
    
    public boolean canLaunch() {
        return blockEntity.canLaunch();
    }
    
    public void launch(Player player) {
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            blockEntity.launch(serverPlayer);
        }
    }
    
    public void launch() {
        launch(playerInventory.player);
    }
    
    public int getFuelStored() {
        return getFuel();
    }
    
    public int getMaxFuelStored() {
        return getMaxFuel();
    }
    
    public boolean isReady() {
        return canLaunch();
    }
}