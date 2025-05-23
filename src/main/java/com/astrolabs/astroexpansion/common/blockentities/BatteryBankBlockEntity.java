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

public class BatteryBankBlockEntity extends BlockEntity {
    
    private static final int CAPACITY = 1000000; // 1 Million FE
    private static final int MAX_TRANSFER = 5000; // 5000 FE/tick
    
    private final EnergyStorage energyStorage = new EnergyStorage(CAPACITY, MAX_TRANSFER, MAX_TRANSFER);
    
    public BatteryBankBlockEntity(BlockPos pos, BlockState state) {
        super(AEBlockEntities.BATTERY_BANK.get(), pos, state);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, BatteryBankBlockEntity blockEntity) {
        blockEntity.tick();
    }
    
    private void tick() {
        if (level == null || level.isClientSide) return;
        
        // Try to balance energy with adjacent battery banks
        balanceWithAdjacentBatteries();
        
        // Distribute energy to non-battery neighbors if we're above 50% capacity
        if (energyStorage.getEnergyStored() > energyStorage.getMaxEnergyStored() / 2) {
            distributeEnergy();
        }
    }
    
    private void balanceWithAdjacentBatteries() {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(direction);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);
            
            if (neighborBE instanceof BatteryBankBlockEntity neighborBattery) {
                int ourEnergy = energyStorage.getEnergyStored();
                int theirEnergy = neighborBattery.energyStorage.getEnergyStored();
                
                if (ourEnergy > theirEnergy) {
                    int difference = (ourEnergy - theirEnergy) / 2;
                    int toTransfer = Math.min(difference, MAX_TRANSFER);
                    
                    if (toTransfer > 0) {
                        int extracted = energyStorage.extractEnergy(toTransfer, false);
                        neighborBattery.energyStorage.receiveEnergy(extracted, false);
                    }
                }
            }
        }
    }
    
    private void distributeEnergy() {
        for (Direction direction : Direction.values()) {
            if (energyStorage.getEnergyStored() <= 0) break;
            
            BlockPos neighborPos = worldPosition.relative(direction);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);
            
            if (neighborBE != null && !(neighborBE instanceof BatteryBankBlockEntity)) {
                IEnergyHandler neighborEnergy = neighborBE.getCapability(AECapabilities.ENERGY, direction.getOpposite());
                if (neighborEnergy != null && neighborEnergy.canReceive()) {
                    int toTransfer = Math.min(energyStorage.getEnergyStored(), MAX_TRANSFER);
                    int transferred = neighborEnergy.receiveEnergy(toTransfer, false);
                    energyStorage.extractEnergy(transferred, false);
                }
            }
        }
    }
    
    public EnergyStorage getEnergyStorage() {
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