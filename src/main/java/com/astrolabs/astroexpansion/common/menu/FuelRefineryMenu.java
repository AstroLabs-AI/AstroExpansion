package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.FuelRefineryControllerBlockEntity;
import com.astrolabs.astroexpansion.common.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;

public class FuelRefineryMenu extends AbstractContainerMenu {
    public final FuelRefineryControllerBlockEntity blockEntity;
    private final Level level;
    
    public FuelRefineryMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }
    
    public FuelRefineryMenu(int id, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.FUEL_REFINERY.get(), id);
        checkContainerSize(inv, 0);
        blockEntity = (FuelRefineryControllerBlockEntity) entity;
        this.level = inv.player.level();
        
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }
    
    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, blockEntity.getBlockState().getBlock());
    }
    
    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 15 + l * 18, 99 + i * 18));
            }
        }
    }
    
    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 15 + i * 18, 157));
        }
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
    
    public int getEnergy() {
        return blockEntity.getEnergyStored();
    }
    
    public int getMaxEnergy() {
        return blockEntity.getMaxEnergyStored();
    }
    
    public int getProgress() {
        return blockEntity.getProcessingTime();
    }
    
    public int getMaxProgress() {
        return blockEntity.getMaxProcessingTime();
    }
    
    public boolean isProcessing() {
        return blockEntity.isProcessing();
    }
    
    public FluidStack getCrudeOil() {
        return blockEntity.getCrudeOil();
    }
    
    public FluidStack getRocketFuel() {
        return blockEntity.getRocketFuel();
    }
    
    public int getFluidCapacity() {
        return blockEntity.getFluidCapacity();
    }
}