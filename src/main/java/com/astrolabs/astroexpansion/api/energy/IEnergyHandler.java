package com.astrolabs.astroexpansion.api.energy;

public interface IEnergyHandler {
    
    int getEnergyStored();
    
    int getMaxEnergyStored();
    
    int receiveEnergy(int maxReceive, boolean simulate);
    
    int extractEnergy(int maxExtract, boolean simulate);
    
    boolean canReceive();
    
    boolean canExtract();
}