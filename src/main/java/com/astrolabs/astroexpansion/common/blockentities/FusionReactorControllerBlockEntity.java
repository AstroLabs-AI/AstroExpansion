package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.api.multiblock.IMultiblockPattern;
import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.menu.FusionReactorMenu;
import com.astrolabs.astroexpansion.common.multiblock.MultiblockControllerBase;
import com.astrolabs.astroexpansion.common.multiblock.patterns.FusionReactorPattern;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import com.astrolabs.astroexpansion.common.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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

public class FusionReactorControllerBlockEntity extends MultiblockControllerBase implements MenuProvider {
    private static final int ENERGY_CAPACITY = 10000000; // 10M FE
    private static final int ENERGY_GENERATION = 50000; // 50k FE/tick when running
    private static final int FLUID_CAPACITY = 16000; // 16 buckets
    private static final int FUEL_CONSUMPTION = 10; // mB/tick
    
    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(ENERGY_CAPACITY, 0, ENERGY_GENERATION);
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);
    
    private final FluidTank deuteriumTank = new FluidTank(FLUID_CAPACITY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.DEUTERIUM.get();
        }
    };
    
    private final FluidTank tritiumTank = new FluidTank(FLUID_CAPACITY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.TRITIUM.get();
        }
    };
    
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> new IFluidHandler() {
        @Override
        public int getTanks() {
            return 2;
        }
        
        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return tank == 0 ? deuteriumTank.getFluid() : tritiumTank.getFluid();
        }
        
        @Override
        public int getTankCapacity(int tank) {
            return FLUID_CAPACITY;
        }
        
        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return tank == 0 ? deuteriumTank.isFluidValid(stack) : tritiumTank.isFluidValid(stack);
        }
        
        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (resource.getFluid() == ModFluids.DEUTERIUM.get()) {
                return deuteriumTank.fill(resource, action);
            } else if (resource.getFluid() == ModFluids.TRITIUM.get()) {
                return tritiumTank.fill(resource, action);
            }
            return 0;
        }
        
        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            return FluidStack.EMPTY;
        }
        
        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return FluidStack.EMPTY;
        }
    });
    
    private boolean running = false;
    private int temperature = 0;
    private static final int MAX_TEMPERATURE = 10000;
    private static final int OPERATING_TEMPERATURE = 8000;
    
    private final IMultiblockPattern pattern = new FusionReactorPattern();
    
    public FusionReactorControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FUSION_REACTOR_CONTROLLER.get(), pos, state);
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
        running = false;
        temperature = 0;
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, FusionReactorControllerBlockEntity blockEntity) {
        if (blockEntity.isFormed()) {
            blockEntity.updateReactor();
        }
    }
    
    private void updateReactor() {
        boolean wasRunning = running;
        
        // Check if we have fuel
        if (deuteriumTank.getFluidAmount() >= FUEL_CONSUMPTION && 
            tritiumTank.getFluidAmount() >= FUEL_CONSUMPTION &&
            energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored() - ENERGY_GENERATION) {
            
            // Heat up the reactor
            if (temperature < MAX_TEMPERATURE) {
                temperature += 100;
            }
            
            // Start running at operating temperature
            if (temperature >= OPERATING_TEMPERATURE) {
                running = true;
                
                // Consume fuel
                deuteriumTank.drain(FUEL_CONSUMPTION, IFluidHandler.FluidAction.EXECUTE);
                tritiumTank.drain(FUEL_CONSUMPTION, IFluidHandler.FluidAction.EXECUTE);
                
                // Generate energy
                energyStorage.receiveEnergy(ENERGY_GENERATION, false);
            }
        } else {
            running = false;
            // Cool down
            if (temperature > 0) {
                temperature = Math.max(0, temperature - 50);
            }
        }
        
        if (wasRunning != running) {
            setChanged();
        }
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
        deuteriumTank.readFromNBT(tag.getCompound("deuterium"));
        tritiumTank.readFromNBT(tag.getCompound("tritium"));
        running = tag.getBoolean("running");
        temperature = tag.getInt("temperature");
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("energy", energyStorage.serializeNBT());
        tag.put("deuterium", deuteriumTank.writeToNBT(new CompoundTag()));
        tag.put("tritium", tritiumTank.writeToNBT(new CompoundTag()));
        tag.putBoolean("running", running);
        tag.putInt("temperature", temperature);
    }
    
    @Override
    public Component getDisplayName() {
        return Component.literal("Fusion Reactor");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new FusionReactorMenu(id, inventory, this);
    }
    
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }
    
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }
    
    public int getTemperature() {
        return temperature;
    }
    
    public int getMaxTemperature() {
        return MAX_TEMPERATURE;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public FluidStack getDeuterium() {
        return deuteriumTank.getFluid();
    }
    
    public FluidStack getTritium() {
        return tritiumTank.getFluid();
    }
    
    public int getFluidCapacity() {
        return FLUID_CAPACITY;
    }
}