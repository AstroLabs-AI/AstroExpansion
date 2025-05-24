package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.RocketAssemblyControllerBlockEntity;
import com.astrolabs.astroexpansion.common.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.SlotItemHandler;

public class RocketAssemblyMenu extends AbstractContainerMenu {
    public final RocketAssemblyControllerBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    
    public RocketAssemblyMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }
    
    public RocketAssemblyMenu(int id, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.ROCKET_ASSEMBLY.get(), id);
        checkContainerSize(inv, 9);
        blockEntity = (RocketAssemblyControllerBlockEntity) entity;
        this.level = inv.player.level();
        this.data = new SimpleContainerData(2);
        
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        
        // Add rocket part slots
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    this.addSlot(new SlotItemHandler(handler, i * 3 + j, 62 + j * 18, 17 + i * 18));
                }
            }
        });
        
        addDataSlots(data);
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
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();
        
        if (index < 36) {
            // Player inventory -> Container
            if (!moveItemStackTo(sourceStack, 36, 36 + 9, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < 36 + 9) {
            // Container -> Player inventory
            if (!moveItemStackTo(sourceStack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
        
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }
    
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0 && blockEntity.canLaunch()) {
            blockEntity.launchRocket();
            return true;
        }
        return false;
    }
    
    public int getEnergy() {
        return blockEntity.getEnergyStored();
    }
    
    public int getMaxEnergy() {
        return blockEntity.getMaxEnergyStored();
    }
    
    public int getBuildProgress() {
        return blockEntity.getBuildProgress();
    }
    
    public int getMaxBuildProgress() {
        return blockEntity.getMaxBuildProgress();
    }
    
    public boolean isRocketBuilt() {
        return blockEntity.isRocketBuilt();
    }
    
    public boolean canLaunch() {
        return blockEntity.canLaunch();
    }
    
    public FluidStack getFuel() {
        return blockEntity.getFuel();
    }
    
    public FluidStack getOxygen() {
        return blockEntity.getOxygen();
    }
    
    public int getFluidCapacity() {
        return blockEntity.getFluidCapacity();
    }
}