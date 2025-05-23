package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.capabilities.EnergyStorage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractMachineBlockEntity extends BlockEntity {
    
    protected final EnergyStorage energyStorage;
    protected int processTime = 0;
    protected int maxProcessTime = 0;
    protected boolean isActive = false;
    
    public AbstractMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int energyCapacity) {
        super(type, pos, state);
        this.energyStorage = new EnergyStorage(energyCapacity);
    }
    
    public abstract void tick();
    
    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public int getProcessTime() {
        return processTime;
    }
    
    public int getMaxProcessTime() {
        return maxProcessTime;
    }
    
    public float getProcessProgress() {
        return maxProcessTime > 0 ? (float) processTime / maxProcessTime : 0;
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        energyStorage.writeToNBT(tag);
        tag.putInt("ProcessTime", processTime);
        tag.putInt("MaxProcessTime", maxProcessTime);
        tag.putBoolean("IsActive", isActive);
    }
    
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energyStorage.readFromNBT(tag);
        processTime = tag.getIntOr("ProcessTime", 0);
        maxProcessTime = tag.getIntOr("MaxProcessTime", 0);
        isActive = tag.getBooleanOr("IsActive", false);
    }
    
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }
    
    protected void markDirtyAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }
    
    @Override
    public void preRemoveSideEffects() {
        // This will be called by subclasses that implement Container
        // The default Container implementation will drop items
        super.preRemoveSideEffects();
    }
}