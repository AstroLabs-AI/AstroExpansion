package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.AdvancedTeleporterBlockEntity;
import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import com.astrolabs.astroexpansion.common.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class AdvancedTeleporterMenu extends AbstractContainerMenu {
    private final AdvancedTeleporterBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    
    public AdvancedTeleporterMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }
    
    public AdvancedTeleporterMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.ADVANCED_TELEPORTER.get(), id);
        checkContainerSize(inv, 1);
        blockEntity = (AdvancedTeleporterBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;
        
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // Frequency card slot
            this.addSlot(new SlotItemHandler(handler, 0, 80, 35));
        });
        
        addDataSlots(data);
    }
    
    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.ADVANCED_TELEPORTER.get());
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        
        ItemStack stackInSlot = slot.getItem();
        ItemStack stack = stackInSlot.copy();
        
        if (index < 36) {
            // From player inventory to machine
            if (!this.moveItemStackTo(stackInSlot, 36, 37, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            // From machine to player inventory
            if (!this.moveItemStackTo(stackInSlot, 0, 36, false)) {
                return ItemStack.EMPTY;
            }
        }
        
        if (stackInSlot.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        
        if (stackInSlot.getCount() == stack.getCount()) {
            return ItemStack.EMPTY;
        }
        
        slot.onTake(player, stackInSlot);
        return stack;
    }
    
    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }
    
    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
    
    public AdvancedTeleporterBlockEntity getBlockEntity() {
        return blockEntity;
    }
    
    public BlockPos getBlockPos() {
        return blockEntity.getBlockPos();
    }
    
    public int getEnergy() {
        return data.get(0);
    }
    
    public int getMaxEnergy() {
        return data.get(1);
    }
    
    public int getCooldown() {
        return data.get(2);
    }
    
    public boolean isFormed() {
        return data.get(3) > 0;
    }
}