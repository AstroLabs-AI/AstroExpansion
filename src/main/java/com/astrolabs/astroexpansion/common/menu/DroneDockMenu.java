package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.DroneDockBlockEntity;
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

public class DroneDockMenu extends AbstractContainerMenu {
    private final DroneDockBlockEntity blockEntity;
    private final Level level;
    
    public DroneDockMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }
    
    public DroneDockMenu(int id, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.DRONE_DOCK_MENU.get(), id);
        this.blockEntity = (DroneDockBlockEntity) entity;
        this.level = inv.player.level();
        
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        
        // Add drone inventory slots (3x3 grid)
        if (this.blockEntity != null) {
            this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        int index = row * 3 + col;
                        this.addSlot(new SlotItemHandler(handler, index, 62 + col * 18, 17 + row * 18));
                    }
                }
            });
        }
    }
    
    public DroneDockBlockEntity getBlockEntity() {
        return blockEntity;
    }
    
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 9;
    
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();
        
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, 
                    TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, 
                    VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
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
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
            player, ModBlocks.DRONE_DOCK.get());
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
    
    public boolean hasDrone() {
        return blockEntity.getDockedDrone() != null;
    }
    
    public int getDroneHealthScaled() {
        if (blockEntity.getDockedDrone() != null) {
            float health = blockEntity.getDockedDrone().getHealth();
            float maxHealth = blockEntity.getDockedDrone().getMaxHealth();
            return maxHealth > 0 ? (int)(health * 36 / maxHealth) : 0;
        }
        return 0;
    }
    
    public float getDroneHealth() {
        return blockEntity.getDockedDrone() != null ? blockEntity.getDockedDrone().getHealth() : 0;
    }
    
    public float getDroneMaxHealth() {
        return blockEntity.getDockedDrone() != null ? blockEntity.getDockedDrone().getMaxHealth() : 0;
    }
}