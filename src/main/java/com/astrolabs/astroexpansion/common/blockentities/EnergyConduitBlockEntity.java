package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.api.energy.IEnergyHandler;
import com.astrolabs.astroexpansion.common.capabilities.AECapabilities;
import com.astrolabs.astroexpansion.common.capabilities.EnergyStorage;
import com.astrolabs.astroexpansion.common.registry.AEBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;

public class EnergyConduitBlockEntity extends BlockEntity {
    
    private static final int TRANSFER_RATE = 1000; // FE per tick
    private static final int BUFFER_SIZE = 1000; // Small buffer for conduits
    
    private final EnergyStorage energyStorage = new EnergyStorage(BUFFER_SIZE, TRANSFER_RATE, TRANSFER_RATE);
    
    public EnergyConduitBlockEntity(BlockPos pos, BlockState state) {
        super(AEBlockEntities.ENERGY_CONDUIT.get(), pos, state);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, EnergyConduitBlockEntity blockEntity) {
        blockEntity.tick();
    }
    
    private void tick() {
        if (level == null || level.isClientSide) return;
        
        // Distribute energy to all connected directions
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(direction);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);
            
            if (neighborBE != null) {
                IEnergyHandler neighborEnergy = neighborBE.getCapability(AECapabilities.ENERGY, direction.getOpposite());
                if (neighborEnergy != null && neighborEnergy.canReceive()) {
                    int energyToTransfer = Math.min(energyStorage.getEnergyStored(), TRANSFER_RATE);
                    if (energyToTransfer > 0) {
                        int transferred = neighborEnergy.receiveEnergy(energyToTransfer, false);
                        energyStorage.extractEnergy(transferred, false);
                    }
                }
            }
        }
        
        // Pull energy from connected sources
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(direction);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);
            
            if (neighborBE != null) {
                IEnergyHandler neighborEnergy = neighborBE.getCapability(AECapabilities.ENERGY, direction.getOpposite());
                if (neighborEnergy != null && neighborEnergy.canExtract()) {
                    int spaceAvailable = energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
                    if (spaceAvailable > 0) {
                        int extracted = neighborEnergy.extractEnergy(Math.min(spaceAvailable, TRANSFER_RATE), false);
                        energyStorage.receiveEnergy(extracted, false);
                    }
                }
            }
        }
    }
    
    public IEnergyHandler getEnergyHandler() {
        return energyStorage;
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        energyStorage.writeToNBT(tag);
    }
    
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energyStorage.readFromNBT(tag);
    }
}