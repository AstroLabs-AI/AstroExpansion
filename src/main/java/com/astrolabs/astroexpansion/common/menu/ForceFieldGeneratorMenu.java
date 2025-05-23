package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.ForceFieldGeneratorBlockEntity;
import com.astrolabs.astroexpansion.common.registry.AEMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ForceFieldGeneratorMenu extends AbstractContainerMenu {
    private final ForceFieldGeneratorBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    
    public ForceFieldGeneratorMenu(int id, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(id, playerInventory, (ForceFieldGeneratorBlockEntity) playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }
    
    public ForceFieldGeneratorMenu(int id, Inventory playerInventory, ForceFieldGeneratorBlockEntity blockEntity) {
        super(AEMenuTypes.FORCE_FIELD_GENERATOR.get(), id);
        this.blockEntity = blockEntity;
        this.level = playerInventory.player.level();
        this.data = new SimpleContainerData(5);
        
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        
        addDataSlots(data);
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
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return true;
    }
    
    public ForceFieldGeneratorBlockEntity getBlockEntity() {
        return blockEntity;
    }
    
    public int getEnergy() {
        return blockEntity.getEnergyStored();
    }
    
    public int getMaxEnergy() {
        return blockEntity.getMaxEnergyStored();
    }
    
    public boolean isActive() {
        return blockEntity.isActive();
    }
    
    public void setActive(boolean active) {
        blockEntity.setActive(active);
    }
    
    public int getRadius() {
        return blockEntity.getRadius();
    }
    
    public void setRadius(int radius) {
        blockEntity.setRadius(radius);
    }
    
    public int getShape() {
        return blockEntity.getShape();
    }
    
    public void setShape(int shape) {
        blockEntity.setShape(shape);
    }
}