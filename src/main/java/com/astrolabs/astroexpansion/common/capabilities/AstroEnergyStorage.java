package com.astrolabs.astroexpansion.common.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.energy.EnergyStorage;

public class AstroEnergyStorage extends EnergyStorage {
    
    public AstroEnergyStorage(int capacity) {
        super(capacity);
    }
    
    public AstroEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }
    
    public AstroEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }
    
    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(energy, capacity));
    }
    
    public void addEnergy(int energy) {
        this.energy = Math.min(this.energy + energy, capacity);
    }
    
    public void consumeEnergy(int energy) {
        this.energy = Math.max(0, this.energy - energy);
    }
    
    public boolean hasEnoughEnergy(int amount) {
        return this.energy >= amount;
    }
    
    public void readFromNBT(CompoundTag nbt) {
        this.energy = nbt.getInt("Energy");
        this.capacity = nbt.getInt("Capacity");
        this.maxReceive = nbt.getInt("MaxReceive");
        this.maxExtract = nbt.getInt("MaxExtract");
    }
    
    public CompoundTag writeToNBT(CompoundTag nbt) {
        nbt.putInt("Energy", this.energy);
        nbt.putInt("Capacity", this.capacity);
        nbt.putInt("MaxReceive", this.maxReceive);
        nbt.putInt("MaxExtract", this.maxExtract);
        return nbt;
    }
    
    @Override
    public String toString() {
        return energy + "/" + capacity + " FE";
    }
}