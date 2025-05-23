package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.MaterialProcessorBlockEntity;
import com.astrolabs.astroexpansion.common.registry.AEMenuTypes;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MaterialProcessorMenu extends BaseContainerMenu {
    
    private final MaterialProcessorBlockEntity blockEntity;
    private final ContainerData data;
    
    public MaterialProcessorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(data.readBlockPos()), new SimpleContainerData(4));
    }
    
    public MaterialProcessorMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity, ContainerData data) {
        super(AEMenuTypes.MATERIAL_PROCESSOR.get(), containerId, playerInventory, (MaterialProcessorBlockEntity) blockEntity, 2);
        this.blockEntity = (MaterialProcessorBlockEntity) blockEntity;
        this.data = data;
        this.addDataSlots(data);
    }
    
    @Override
    protected void addContainerSlots() {
        // Input slot
        this.addSlot(new Slot(container, 0, 56, 35));
        // Output slot
        this.addSlot(new Slot(container, 1, 116, 35) {
            @Override
            public boolean mayPlace(net.minecraft.world.item.ItemStack stack) {
                return false;
            }
        });
    }
    
    public int getEnergyStored() {
        return data.get(0);
    }
    
    public int getMaxEnergy() {
        return data.get(1);
    }
    
    public int getProcessTime() {
        return data.get(2);
    }
    
    public int getMaxProcessTime() {
        return data.get(3);
    }
    
    public float getProcessProgress() {
        return getMaxProcessTime() > 0 ? (float) getProcessTime() / getMaxProcessTime() : 0;
    }
    
    public MaterialProcessorBlockEntity getBlockEntity() {
        return blockEntity;
    }
}