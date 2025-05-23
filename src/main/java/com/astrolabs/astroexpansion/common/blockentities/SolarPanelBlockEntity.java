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
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SolarPanelBlockEntity extends BlockEntity {
    
    private static final int MAX_GENERATION = 100; // FE per tick at full sunlight
    private static final int ENERGY_CAPACITY = 10000;
    
    private final EnergyStorage energyStorage = new EnergyStorage(ENERGY_CAPACITY, 0, MAX_GENERATION * 2);
    
    public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(AEBlockEntities.SOLAR_PANEL.get(), pos, state);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, SolarPanelBlockEntity blockEntity) {
        blockEntity.tick();
    }
    
    private void tick() {
        if (level == null || level.isClientSide) return;
        
        // Generate energy based on sunlight
        int skyLight = level.getBrightness(LightLayer.SKY, worldPosition.above());
        float timeModifier = getTimeModifier();
        int generation = (int) (MAX_GENERATION * (skyLight / 15.0f) * timeModifier);
        
        if (generation > 0 && level.canSeeSky(worldPosition.above())) {
            energyStorage.receiveEnergy(generation, false);
        }
        
        // Distribute energy to adjacent blocks
        distributeEnergy();
    }
    
    private float getTimeModifier() {
        if (level == null) return 0;
        
        long dayTime = level.getDayTime() % 24000;
        
        // Dawn (23000-24000 and 0-1000)
        if (dayTime >= 23000 || dayTime < 1000) {
            return 0.3f;
        }
        // Morning (1000-5000)
        else if (dayTime < 5000) {
            return 0.3f + (dayTime - 1000) / 4000f * 0.7f;
        }
        // Day (5000-7000)
        else if (dayTime < 7000) {
            return 1.0f;
        }
        // Evening (7000-11000)
        else if (dayTime < 11000) {
            return 1.0f - (dayTime - 7000) / 4000f * 0.7f;
        }
        // Dusk (11000-13000)
        else if (dayTime < 13000) {
            return 0.3f;
        }
        // Night (13000-23000)
        else {
            return 0;
        }
    }
    
    private void distributeEnergy() {
        for (Direction direction : Direction.values()) {
            if (energyStorage.getEnergyStored() <= 0) break;
            
            BlockPos neighborPos = worldPosition.relative(direction);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);
            
            if (neighborBE != null) {
                IEnergyHandler neighborEnergy = neighborBE.getCapability(AECapabilities.ENERGY, direction.getOpposite());
                if (neighborEnergy != null && neighborEnergy.canReceive()) {
                    int toTransfer = Math.min(energyStorage.getEnergyStored(), MAX_GENERATION * 2);
                    int transferred = neighborEnergy.receiveEnergy(toTransfer, false);
                    energyStorage.extractEnergy(transferred, false);
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