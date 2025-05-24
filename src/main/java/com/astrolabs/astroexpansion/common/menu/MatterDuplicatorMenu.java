package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.block.entity.MatterDuplicatorBlockEntity;
import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import com.astrolabs.astroexpansion.common.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class MatterDuplicatorMenu extends AbstractContainerMenu {
    private final MatterDuplicatorBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public MatterDuplicatorMenu(int id, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(id, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(7));
    }

    public MatterDuplicatorMenu(int id, Inventory playerInventory, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.MATTER_DUPLICATOR_MENU.get(), id);
        checkContainerSize(playerInventory, 3);
        blockEntity = (MatterDuplicatorBlockEntity) entity;
        this.level = playerInventory.player.level();
        this.data = data;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // Input slot
            this.addSlot(new SlotItemHandler(handler, 0, 44, 35));
            
            // Output slot
            this.addSlot(new SlotItemHandler(handler, 1, 116, 35) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
            
            // Target template slot
            this.addSlot(new SlotItemHandler(handler, 2, 80, 58));
        });

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 26; // This is the width of the progress arrow

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    public int getScaledEnergy() {
        int energy = this.data.get(2);
        int maxEnergy = this.data.get(3);
        int energyBarSize = 60; // This is the height of the energy bar

        return maxEnergy != 0 && energy != 0 ? energy * energyBarSize / maxEnergy : 0;
    }

    public int getScaledFluid() {
        int fluid = this.data.get(4);
        int maxFluid = this.data.get(5);
        int fluidBarSize = 60; // This is the height of the fluid bar

        return maxFluid != 0 && fluid != 0 ? fluid * fluidBarSize / maxFluid : 0;
    }

    public int getEnergyStored() {
        return data.get(2);
    }

    public int getMaxEnergyStored() {
        return data.get(3);
    }

    public int getFluidAmount() {
        return data.get(4);
    }

    public int getFluidCapacity() {
        return data.get(5);
    }

    public int getCurrentUUCost() {
        return data.get(6);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = slot.getItem();
        ItemStack originalStack = stackInSlot.copy();

        // Player inventory slots
        if (index < 36) {
            // Try to move to input or target slot
            if (!this.moveItemStackTo(stackInSlot, 36, 37, false)) {
                if (!this.moveItemStackTo(stackInSlot, 38, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
        } else {
            // Machine slots to player inventory
            if (!this.moveItemStackTo(stackInSlot, 0, 36, true)) {
                return ItemStack.EMPTY;
            }
        }

        if (stackInSlot.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stackInSlot.getCount() == originalStack.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stackInSlot);
        return originalStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.MATTER_DUPLICATOR.get());
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

    public MatterDuplicatorBlockEntity getBlockEntity() {
        return blockEntity;
    }
}