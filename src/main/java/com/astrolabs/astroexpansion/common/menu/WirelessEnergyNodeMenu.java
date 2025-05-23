package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.WirelessEnergyNodeBlockEntity;
import com.astrolabs.astroexpansion.common.registry.AEMenuTypes;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;

public class WirelessEnergyNodeMenu extends BaseContainerMenu {
    
    private final WirelessEnergyNodeBlockEntity blockEntity;
    private final ContainerData data;
    
    public WirelessEnergyNodeMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(data.readBlockPos()), new SimpleContainerData(4));
    }
    
    public WirelessEnergyNodeMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity, ContainerData data) {
        super(AEMenuTypes.WIRELESS_ENERGY_NODE.get(), containerId, playerInventory, (WirelessEnergyNodeBlockEntity) blockEntity, 1);
        this.blockEntity = (WirelessEnergyNodeBlockEntity) blockEntity;
        this.data = data;
        this.addDataSlots(data);
    }
    
    @Override
    protected void addContainerSlots() {
        // Linking card slot
        this.addSlot(new Slot(container, 0, 80, 35));
    }
    
    public int getEnergyStored() {
        return data.get(0);
    }
    
    public int getMaxEnergy() {
        return data.get(1);
    }
    
    public int getMode() {
        return data.get(2);
    }
    
    public int getLinkedCount() {
        return data.get(3);
    }
    
    public WirelessEnergyNodeBlockEntity getBlockEntity() {
        return blockEntity;
    }
    
    public void setMode(int mode) {
        blockEntity.setMode(mode);
    }
}