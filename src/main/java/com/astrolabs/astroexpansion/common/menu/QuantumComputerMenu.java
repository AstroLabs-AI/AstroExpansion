package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.QuantumComputerControllerBlockEntity;
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

public class QuantumComputerMenu extends AbstractContainerMenu {
    public final QuantumComputerControllerBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    
    public QuantumComputerMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }
    
    public QuantumComputerMenu(int id, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.QUANTUM_COMPUTER.get(), id);
        checkContainerSize(inv, 9);
        blockEntity = (QuantumComputerControllerBlockEntity) entity;
        this.level = inv.player.level();
        this.data = new SimpleContainerData(4);
        
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // Input slots (4)
            for (int i = 0; i < 4; i++) {
                this.addSlot(new SlotItemHandler(handler, i, 44 + i * 18, 35));
            }
            
            // Output slots (4)
            for (int i = 0; i < 4; i++) {
                this.addSlot(new SlotItemHandler(handler, 4 + i, 44 + i * 18, 71));
            }
            
            // Research data slot
            this.addSlot(new SlotItemHandler(handler, 8, 152, 53));
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
            // Player inventory slot -> Container
            if (!moveItemStackTo(sourceStack, 36, 36 + 9, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < 36 + 9) {
            // Container slot -> Player inventory
            if (!moveItemStackTo(sourceStack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
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
    
    public int getResearchPoints() {
        return blockEntity.getResearchPoints();
    }
}