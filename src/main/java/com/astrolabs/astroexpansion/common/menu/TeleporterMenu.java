package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.TeleporterBlockEntity;
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

public class TeleporterMenu extends AbstractContainerMenu {
    private final TeleporterBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    
    public TeleporterMenu(int id, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(id, playerInventory, (TeleporterBlockEntity) playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }
    
    public TeleporterMenu(int id, Inventory playerInventory, TeleporterBlockEntity blockEntity) {
        super(AEMenuTypes.TELEPORTER.get(), id);
        this.blockEntity = blockEntity;
        this.level = playerInventory.player.level();
        this.data = new SimpleContainerData(2);
        
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
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return true;
    }
    
    public TeleporterBlockEntity getBlockEntity() {
        return blockEntity;
    }
    
    public int getEnergy() {
        return blockEntity.getEnergyStored();
    }
    
    public int getMaxEnergy() {
        return blockEntity.getMaxEnergyStored();
    }
}