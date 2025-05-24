package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.FluidTankBlockEntity;
import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import com.astrolabs.astroexpansion.common.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FluidTankMenu extends AbstractContainerMenu {
    private final FluidTankBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    
    public FluidTankMenu(int windowId, Inventory inventory, FriendlyByteBuf extraData) {
        this(windowId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(3));
    }
    
    public FluidTankMenu(int windowId, Inventory inventory, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.FLUID_TANK.get(), windowId);
        checkContainerSize(inventory, 3);
        blockEntity = (FluidTankBlockEntity) entity;
        this.level = inventory.player.level();
        this.data = data;
        
        addDataSlots(data);
        
        // Player inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        
        // Player hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }
    }
    
    public int getFluidAmount() {
        return data.get(0);
    }
    
    public int getCapacity() {
        return data.get(1);
    }
    
    public int getFluidTemperature() {
        return data.get(2);
    }
    
    public FluidTankBlockEntity getBlockEntity() {
        return blockEntity;
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.FLUID_TANK.get());
    }
}