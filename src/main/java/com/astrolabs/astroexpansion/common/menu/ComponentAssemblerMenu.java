package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.ComponentAssemblerBlockEntity;
import com.astrolabs.astroexpansion.common.registry.AEMenuTypes;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ComponentAssemblerMenu extends BaseContainerMenu {
    
    private final ComponentAssemblerBlockEntity blockEntity;
    private final ContainerData data;
    
    public ComponentAssemblerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(data.readBlockPos()), new SimpleContainerData(4));
    }
    
    public ComponentAssemblerMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity, ContainerData data) {
        super(AEMenuTypes.COMPONENT_ASSEMBLER.get(), containerId, playerInventory, (ComponentAssemblerBlockEntity) blockEntity, 10);
        this.blockEntity = (ComponentAssemblerBlockEntity) blockEntity;
        this.data = data;
        this.addDataSlots(data);
    }
    
    @Override
    protected void addContainerSlots() {
        // 3x3 crafting grid
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(container, col + row * 3, 30 + col * 18, 17 + row * 18));
            }
        }
        
        // Output slot
        this.addSlot(new Slot(container, 9, 124, 35) {
            @Override
            public boolean mayPlace(net.minecraft.world.item.ItemStack stack) {
                return false;
            }
        });
    }
    
    @Override
    protected void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 102 + row * 18));
            }
        }
    }
    
    @Override
    protected void addPlayerHotbar(Inventory playerInventory) {
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 160));
        }
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
    
    public ComponentAssemblerBlockEntity getBlockEntity() {
        return blockEntity;
    }
}