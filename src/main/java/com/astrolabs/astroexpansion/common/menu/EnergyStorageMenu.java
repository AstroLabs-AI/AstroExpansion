package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.EnergyStorageBlockEntity;
import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import com.astrolabs.astroexpansion.common.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class EnergyStorageMenu extends AbstractContainerMenu {
    private final EnergyStorageBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    
    public EnergyStorageMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }
    
    public EnergyStorageMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.ENERGY_STORAGE_MENU.get(), id);
        this.blockEntity = (EnergyStorageBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;
        
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        
        addDataSlots(data);
    }
    
    public int getScaledEnergy() {
        int energy = this.data.get(0);
        int maxEnergy = this.data.get(1);
        int energyBarSize = 60;
        
        return maxEnergy != 0 ? (int)(((float)energy / maxEnergy) * energyBarSize) : 0;
    }
    
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        return ItemStack.EMPTY;
    }
    
    public int getEnergyStored() {
        return data.get(0);
    }
    
    public int getMaxEnergyStored() {
        return data.get(1);
    }
    
    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
            player, ModBlocks.ENERGY_STORAGE.get());
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
}