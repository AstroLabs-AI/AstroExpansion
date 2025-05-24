package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.ComponentAssemblerBlockEntity;
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

public class ComponentAssemblerMenu extends AbstractContainerMenu {
    private final ComponentAssemblerBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    
    public ComponentAssemblerMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }
    
    public ComponentAssemblerMenu(int id, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.COMPONENT_ASSEMBLER_MENU.get(), id);
        blockEntity = (ComponentAssemblerBlockEntity) entity;
        this.level = inv.player.level();
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ComponentAssemblerMenu.this.blockEntity.getProgress();
                    case 1 -> ComponentAssemblerMenu.this.blockEntity.getMaxProgress();
                    default -> 0;
                };
            }
            
            @Override
            public void set(int index, int value) {
                // Client side only
            }
            
            @Override
            public int getCount() {
                return 2;
            }
        };
        
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // Input slots (4 slots in a 2x2 grid)
            this.addSlot(new SlotItemHandler(handler, 0, 37, 23));
            this.addSlot(new SlotItemHandler(handler, 1, 55, 23));
            this.addSlot(new SlotItemHandler(handler, 2, 37, 41));
            this.addSlot(new SlotItemHandler(handler, 3, 55, 41));
            
            // Output slot
            this.addSlot(new SlotItemHandler(handler, 4, 131, 41));
        });
        
        addDataSlots(data);
    }
    
    public boolean isCrafting() {
        return data.get(0) > 0;
    }
    
    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 26; // Width of the progress arrow
        
        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }
    
    public int getScaledEnergy() {
        return blockEntity.getEnergyHandler().map(energy -> {
            int current = energy.getEnergyStored();
            int max = energy.getMaxEnergyStored();
            int barHeight = 60;
            return max != 0 ? current * barHeight / max : 0;
        }).orElse(0);
    }
    
    public int getEnergyStored() {
        return blockEntity.getEnergyHandler().map(energy -> energy.getEnergyStored()).orElse(0);
    }
    
    public int getMaxEnergyStored() {
        return blockEntity.getEnergyHandler().map(energy -> energy.getMaxEnergyStored()).orElse(0);
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        
        ItemStack slotStack = slot.getItem();
        ItemStack itemstack = slotStack.copy();
        
        // If it's output slot
        if (index == 40) {
            if (!this.moveItemStackTo(slotStack, 0, 36, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(slotStack, itemstack);
        }
        // If it's in player inventory
        else if (index < 36) {
            if (!this.moveItemStackTo(slotStack, 36, 40, false)) {
                if (!this.moveItemStackTo(slotStack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }
        // If it's in input slots
        else if (index < 40) {
            if (!this.moveItemStackTo(slotStack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }
        }
        
        if (slotStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        
        if (slotStack.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
        }
        
        slot.onTake(player, slotStack);
        return itemstack;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
            player, ModBlocks.COMPONENT_ASSEMBLER.get());
    }
    
    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 15 + l * 18, 110 + i * 18));
            }
        }
    }
    
    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 15 + i * 18, 168));
        }
    }
    
    public ComponentAssemblerBlockEntity getBlockEntity() {
        return blockEntity;
    }
}