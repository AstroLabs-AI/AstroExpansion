package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import com.astrolabs.astroexpansion.common.registry.ModFluids;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OxygenGeneratorBlockEntity extends BlockEntity {
    private static final int ENERGY_CAPACITY = 100000; // 100k FE
    private static final int ENERGY_PER_OPERATION = 500; // 500 FE per operation
    private static final int OXYGEN_PER_OPERATION = 100; // 100 mB per operation
    private static final int TANK_CAPACITY = 16000; // 16 buckets
    
    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(ENERGY_CAPACITY, 10000, 0);
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);
    
    private final FluidTank oxygenTank = new FluidTank(TANK_CAPACITY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.LIQUID_OXYGEN.get();
        }
    };
    
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> oxygenTank);
    
    private int processTime = 0;
    private static final int MAX_PROCESS_TIME = 100; // 5 seconds
    
    public OxygenGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OXYGEN_GENERATOR.get(), pos, state);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, OxygenGeneratorBlockEntity blockEntity) {
        if (blockEntity.canProduce()) {
            if (blockEntity.energyStorage.extractEnergy(ENERGY_PER_OPERATION / MAX_PROCESS_TIME, false) > 0) {
                blockEntity.processTime++;
                
                if (blockEntity.processTime >= MAX_PROCESS_TIME) {
                    blockEntity.produceOxygen();
                    blockEntity.processTime = 0;
                }
                blockEntity.setChanged();
            }
        } else {
            if (blockEntity.processTime > 0) {
                blockEntity.processTime = 0;
                blockEntity.setChanged();
            }
        }
    }
    
    private boolean canProduce() {
        return energyStorage.getEnergyStored() >= ENERGY_PER_OPERATION &&
               oxygenTank.getFluidAmount() + OXYGEN_PER_OPERATION <= oxygenTank.getCapacity();
    }
    
    private void produceOxygen() {
        energyStorage.extractEnergy(ENERGY_PER_OPERATION - (ENERGY_PER_OPERATION / MAX_PROCESS_TIME), false);
        oxygenTank.fill(new FluidStack(ModFluids.LIQUID_OXYGEN.get(), OXYGEN_PER_OPERATION), IFluidHandler.FluidAction.EXECUTE);
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyHandler.invalidate();
        fluidHandler.invalidate();
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energyStorage.deserializeNBT(tag.get("energy"));
        oxygenTank.readFromNBT(tag.getCompound("oxygen"));
        processTime = tag.getInt("processTime");
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("energy", energyStorage.serializeNBT());
        tag.put("oxygen", oxygenTank.writeToNBT(new CompoundTag()));
        tag.putInt("processTime", processTime);
    }
    
    
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }
    
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }
    
    public int getProcessTime() {
        return processTime;
    }
    
    public int getMaxProcessTime() {
        return MAX_PROCESS_TIME;
    }
    
    public FluidStack getOxygen() {
        return oxygenTank.getFluid();
    }
    
    public int getTankCapacity() {
        return oxygenTank.getCapacity();
    }
}