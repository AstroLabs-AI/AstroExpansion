package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.api.multiblock.IMultiblockPattern;
import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.menu.FuelRefineryMenu;
import com.astrolabs.astroexpansion.common.multiblock.MultiblockControllerBase;
import com.astrolabs.astroexpansion.common.multiblock.patterns.FuelRefineryPattern;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import com.astrolabs.astroexpansion.common.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
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

public class FuelRefineryControllerBlockEntity extends MultiblockControllerBase implements MenuProvider {
    private static final int ENERGY_CAPACITY = 500000; // 500k FE
    private static final int ENERGY_USAGE = 1000; // 1k FE/tick when processing
    private static final int FLUID_CAPACITY = 16000; // 16 buckets
    private static final int PROCESS_TIME = 200; // 10 seconds
    private static final int OIL_PER_OPERATION = 1000; // 1 bucket
    private static final int FUEL_PER_OPERATION = 500; // 0.5 bucket
    
    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(ENERGY_CAPACITY, 10000, 0);
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);
    
    private final FluidTank crudeOilTank = new FluidTank(FLUID_CAPACITY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.CRUDE_OIL.get();
        }
    };
    
    private final FluidTank rocketFuelTank = new FluidTank(FLUID_CAPACITY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.ROCKET_FUEL.get();
        }
    };
    
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> new IFluidHandler() {
        @Override
        public int getTanks() {
            return 2;
        }
        
        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return tank == 0 ? crudeOilTank.getFluid() : rocketFuelTank.getFluid();
        }
        
        @Override
        public int getTankCapacity(int tank) {
            return FLUID_CAPACITY;
        }
        
        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return tank == 0 ? crudeOilTank.isFluidValid(stack) : rocketFuelTank.isFluidValid(stack);
        }
        
        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (resource.getFluid() == ModFluids.CRUDE_OIL.get()) {
                return crudeOilTank.fill(resource, action);
            }
            return 0;
        }
        
        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            if (resource.getFluid() == ModFluids.ROCKET_FUEL.get()) {
                return rocketFuelTank.drain(resource, action);
            }
            return FluidStack.EMPTY;
        }
        
        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return rocketFuelTank.drain(maxDrain, action);
        }
    });
    
    private int processingTime = 0;
    private boolean processing = false;
    
    private final IMultiblockPattern pattern = new FuelRefineryPattern();
    
    public FuelRefineryControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FUEL_REFINERY_CONTROLLER.get(), pos, state);
    }
    
    @Override
    public IMultiblockPattern getPattern() {
        return pattern;
    }
    
    @Override
    public void onFormed() {
        // Multiblock successfully formed
    }
    
    @Override
    public void onBroken() {
        processing = false;
        processingTime = 0;
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, FuelRefineryControllerBlockEntity blockEntity) {
        if (blockEntity.isFormed()) {
            blockEntity.updateRefinery();
        }
    }
    
    private void updateRefinery() {
        boolean wasProcessing = processing;
        
        if (canProcess()) {
            if (!processing) {
                processing = true;
                processingTime = 0;
            }
            
            if (energyStorage.extractEnergy(ENERGY_USAGE, false) == ENERGY_USAGE) {
                processingTime++;
                
                if (processingTime >= PROCESS_TIME) {
                    processOil();
                    processingTime = 0;
                }
            }
        } else {
            processing = false;
            processingTime = 0;
        }
        
        if (wasProcessing != processing) {
            setChanged();
        }
    }
    
    private boolean canProcess() {
        return crudeOilTank.getFluidAmount() >= OIL_PER_OPERATION &&
               rocketFuelTank.getFluidAmount() + FUEL_PER_OPERATION <= rocketFuelTank.getCapacity() &&
               energyStorage.getEnergyStored() >= ENERGY_USAGE;
    }
    
    private void processOil() {
        crudeOilTank.drain(OIL_PER_OPERATION, IFluidHandler.FluidAction.EXECUTE);
        rocketFuelTank.fill(new FluidStack(ModFluids.ROCKET_FUEL.get(), FUEL_PER_OPERATION), IFluidHandler.FluidAction.EXECUTE);
        setChanged();
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
        crudeOilTank.readFromNBT(tag.getCompound("crudeOil"));
        rocketFuelTank.readFromNBT(tag.getCompound("rocketFuel"));
        processingTime = tag.getInt("processingTime");
        processing = tag.getBoolean("processing");
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("energy", energyStorage.serializeNBT());
        tag.put("crudeOil", crudeOilTank.writeToNBT(new CompoundTag()));
        tag.put("rocketFuel", rocketFuelTank.writeToNBT(new CompoundTag()));
        tag.putInt("processingTime", processingTime);
        tag.putBoolean("processing", processing);
    }
    
    @Override
    public Component getDisplayName() {
        return Component.literal("Fuel Refinery");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new FuelRefineryMenu(id, inventory, this);
    }
    
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }
    
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }
    
    public int getProcessingTime() {
        return processingTime;
    }
    
    public int getMaxProcessingTime() {
        return PROCESS_TIME;
    }
    
    public boolean isProcessing() {
        return processing;
    }
    
    public FluidStack getCrudeOil() {
        return crudeOilTank.getFluid();
    }
    
    public FluidStack getRocketFuel() {
        return rocketFuelTank.getFluid();
    }
    
    public int getFluidCapacity() {
        return FLUID_CAPACITY;
    }
    
    public int getOilAmount() {
        return crudeOilTank.getFluidAmount();
    }
    
    public int getOilCapacity() {
        return crudeOilTank.getCapacity();
    }
    
    public int getFuelAmount() {
        return rocketFuelTank.getFluidAmount();
    }
    
    public int getFuelCapacity() {
        return rocketFuelTank.getCapacity();
    }
    
    public int getProgress() {
        return processingTime;
    }
    
    public int getMaxProgress() {
        return PROCESS_TIME;
    }
}