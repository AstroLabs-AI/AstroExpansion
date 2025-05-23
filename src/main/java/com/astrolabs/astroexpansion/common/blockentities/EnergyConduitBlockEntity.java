package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EnergyConduitBlockEntity extends BlockEntity {
    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(1000, 1000, 1000);
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    
    private static final int TRANSFER_RATE = 1000;
    
    public EnergyConduitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_CONDUIT.get(), pos, state);
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        
        return super.getCapability(cap, side);
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyEnergyHandler.invalidate();
    }
    
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("energy", energyStorage.writeToNBT(new CompoundTag()));
        super.saveAdditional(nbt);
    }
    
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        energyStorage.readFromNBT(nbt.getCompound("energy"));
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, EnergyConduitBlockEntity entity) {
        if (level.isClientSide) {
            return;
        }
        
        // Network-based energy distribution
        entity.distributeEnergy();
    }
    
    private void distributeEnergy() {
        if (energyStorage.getEnergyStored() <= 0) {
            return;
        }
        
        // Find all connected energy receivers
        List<IEnergyStorage> receivers = new ArrayList<>();
        
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(direction);
            BlockEntity be = level.getBlockEntity(neighborPos);
            
            if (be != null && !(be instanceof EnergyConduitBlockEntity)) {
                be.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(handler -> {
                    if (handler.canReceive()) {
                        int demand = handler.receiveEnergy(TRANSFER_RATE, true);
                        if (demand > 0) {
                            receivers.add(handler);
                        }
                    }
                });
            }
        }
        
        // Distribute energy evenly among receivers
        if (!receivers.isEmpty()) {
            int availableEnergy = Math.min(energyStorage.getEnergyStored(), TRANSFER_RATE);
            
            for (IEnergyStorage receiver : receivers) {
                int energyShare = availableEnergy / receivers.size();
                int transferred = receiver.receiveEnergy(energyShare, false);
                energyStorage.extractEnergy(transferred, false);
            }
            
            setChanged();
        }
    }
}