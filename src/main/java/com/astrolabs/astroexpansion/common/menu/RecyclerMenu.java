package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.block.entity.machine.RecyclerBlockEntity;
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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class RecyclerMenu extends AbstractContainerMenu {
    private final RecyclerBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public RecyclerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(buffer.readBlockPos()), new SimpleContainerData(4));
    }

    public RecyclerMenu(int containerId, Inventory playerInventory, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.RECYCLER.get(), containerId);
        checkContainerSize(playerInventory, 7); // 1 input + 3 output + 3 upgrades
        this.blockEntity = (RecyclerBlockEntity) entity;
        this.level = playerInventory.player.level();
        this.data = data;

        addDataSlots(data);

        if (blockEntity != null) {
            // Input slot (center top)
            addSlot(new SlotItemHandler(blockEntity.getInputHandler(), 0, 80, 26));

            // Output slots (bottom row)
            addSlot(new SlotItemHandler(blockEntity.getOutputHandler(), 0, 62, 62));
            addSlot(new SlotItemHandler(blockEntity.getOutputHandler(), 1, 80, 62));
            addSlot(new SlotItemHandler(blockEntity.getOutputHandler(), 2, 98, 62));

            // Upgrade slots (right side)
            addSlot(new SlotItemHandler(blockEntity.getUpgradeHandler(), 0, 152, 17));
            addSlot(new SlotItemHandler(blockEntity.getUpgradeHandler(), 1, 152, 35));
            addSlot(new SlotItemHandler(blockEntity.getUpgradeHandler(), 2, 152, 53));
        }

        // Player inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player hotbar
        for (int col = 0; col < 9; ++col) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack returnStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();

            // If clicking on a machine slot
            if (slotIndex < 7) {
                // Try to move to player inventory
                if (!this.moveItemStackTo(slotStack, 7, 43, true)) {
                    return ItemStack.EMPTY;
                }
            } 
            // If clicking on player inventory
            else {
                // Try input slot first
                if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                    // Try upgrade slots
                    if (!this.moveItemStackTo(slotStack, 4, 7, false)) {
                        // Move between inventory and hotbar
                        if (slotIndex < 34) {
                            if (!this.moveItemStackTo(slotStack, 34, 43, false)) {
                                return ItemStack.EMPTY;
                            }
                        } else if (!this.moveItemStackTo(slotStack, 7, 34, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == returnStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return returnStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.RECYCLER.get());
    }

    public int getProcessProgress() {
        int progress = data.get(0);
        int maxProgress = data.get(1);
        
        return maxProgress != 0 && progress != 0 ? progress * 24 / maxProgress : 0;
    }

    public int getEnergyStoredScaled() {
        int energyStored = data.get(2);
        int maxEnergy = data.get(3);
        
        return maxEnergy != 0 ? energyStored * 52 / maxEnergy : 0;
    }

    public int getEnergyStored() {
        return data.get(2);
    }

    public int getMaxEnergyStored() {
        return data.get(3);
    }

    public boolean isRecycling() {
        return data.get(0) > 0;
    }
}