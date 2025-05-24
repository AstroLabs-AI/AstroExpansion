package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.IndustrialFurnaceControllerBlockEntity;
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

public class IndustrialFurnaceMenu extends AbstractContainerMenu {
    private final IndustrialFurnaceControllerBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    
    public IndustrialFurnaceMenu(int windowId, Inventory inventory, FriendlyByteBuf extraData) {
        this(windowId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(12));
    }
    
    public IndustrialFurnaceMenu(int windowId, Inventory inventory, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.INDUSTRIAL_FURNACE.get(), windowId);
        checkContainerSize(inventory, 4);
        blockEntity = (IndustrialFurnaceControllerBlockEntity) entity;
        this.level = inventory.player.level();
        this.data = data;
        
        addDataSlots(data);
        
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // Input slots (3x3 grid)
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    this.addSlot(new SlotItemHandler(handler, row * 3 + col, 30 + col * 18, 17 + row * 18));
                }
            }
            
            // Output slots (3x3 grid)
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    this.addSlot(new SlotItemHandler(handler, 9 + row * 3 + col, 102 + col * 18, 17 + row * 18));
                }
            }
        });
        
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
    
    public boolean isCrafting() {
        for (int i = 0; i < 9; i++) {
            if (getProgress(i) > 0) return true;
        }
        return false;
    }
    
    public int getProgress(int slot) {
        return data.get(slot);
    }
    
    public int getEnergyStored() {
        return data.get(9);
    }
    
    public int getMaxEnergy() {
        return data.get(10);
    }
    
    public boolean isFormed() {
        return data.get(11) > 0;
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            
            if (index < 18) {
                // From container to player inventory
                if (!this.moveItemStackTo(stack, 18, 54, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From player inventory to input slots only
                if (!this.moveItemStackTo(stack, 0, 9, false)) {
                    return ItemStack.EMPTY;
                }
            }
            
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            
            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            
            slot.onTake(player, stack);
        }
        
        return itemstack;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.INDUSTRIAL_FURNACE_CONTROLLER.get());
    }
}