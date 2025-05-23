package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.ResearchConsoleBlockEntity;
import com.astrolabs.astroexpansion.common.registry.AEMenuTypes;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ResearchConsoleMenu extends BaseContainerMenu {
    
    private final ResearchConsoleBlockEntity blockEntity;
    private final ContainerData data;
    private final Player player;
    
    public ResearchConsoleMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(data.readBlockPos()), 
             new SimpleContainerData(5), playerInventory.player);
    }
    
    public ResearchConsoleMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity, 
                              ContainerData data, Player player) {
        super(AEMenuTypes.RESEARCH_CONSOLE.get(), containerId, playerInventory, 
              (ResearchConsoleBlockEntity) blockEntity, 1);
        this.blockEntity = (ResearchConsoleBlockEntity) blockEntity;
        this.data = data;
        this.player = player;
        this.addDataSlots(data);
    }
    
    @Override
    protected void addContainerSlots() {
        // Research input slot (processors)
        this.addSlot(new Slot(container, 0, 80, 35));
    }
    
    public int getEnergyStored() {
        return data.get(0);
    }
    
    public int getMaxEnergy() {
        return data.get(1);
    }
    
    public int getProcessTime() {
        return data.get(2);
    }
    
    public int getMaxProcessTime() {
        return data.get(3);
    }
    
    public int getResearchProgress() {
        return data.get(4);
    }
    
    public ResearchConsoleBlockEntity getBlockEntity() {
        return blockEntity;
    }
    
    public Player getPlayer() {
        return player;
    }
}