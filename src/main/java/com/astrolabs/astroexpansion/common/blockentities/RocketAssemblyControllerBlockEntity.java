package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.api.multiblock.IMultiblockPattern;
import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.menu.RocketAssemblyMenu;
import com.astrolabs.astroexpansion.common.multiblock.MultiblockControllerBase;
import com.astrolabs.astroexpansion.common.multiblock.patterns.RocketAssemblyPattern;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import com.astrolabs.astroexpansion.common.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RocketAssemblyControllerBlockEntity extends MultiblockControllerBase implements MenuProvider {
    private static final int ENERGY_CAPACITY = 1000000; // 1M FE
    private static final int ENERGY_PER_BUILD = 100000; // 100k FE to build rocket
    private static final int FUEL_CAPACITY = 32000; // 32 buckets
    
    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(ENERGY_CAPACITY, 50000, 0);
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);
    
    private final FluidTank fuelTank = new FluidTank(FUEL_CAPACITY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.ROCKET_FUEL.get();
        }
    };
    
    private final FluidTank oxygenTank = new FluidTank(FUEL_CAPACITY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.LIQUID_OXYGEN.get();
        }
    };
    
    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    
    private final LazyOptional<IItemHandler> itemHandlerOpt = LazyOptional.of(() -> itemHandler);
    
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> new IFluidHandler() {
        @Override
        public int getTanks() {
            return 2;
        }
        
        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return tank == 0 ? fuelTank.getFluid() : oxygenTank.getFluid();
        }
        
        @Override
        public int getTankCapacity(int tank) {
            return FUEL_CAPACITY;
        }
        
        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return tank == 0 ? fuelTank.isFluidValid(stack) : oxygenTank.isFluidValid(stack);
        }
        
        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (resource.getFluid() == ModFluids.ROCKET_FUEL.get()) {
                return fuelTank.fill(resource, action);
            } else if (resource.getFluid() == ModFluids.LIQUID_OXYGEN.get()) {
                return oxygenTank.fill(resource, action);
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
    
    private boolean rocketBuilt = false;
    private int buildProgress = 0;
    private static final int BUILD_TIME = 200; // 10 seconds
    
    private final IMultiblockPattern pattern = new RocketAssemblyPattern();
    
    public RocketAssemblyControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROCKET_ASSEMBLY_CONTROLLER.get(), pos, state);
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
        rocketBuilt = false;
        buildProgress = 0;
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, RocketAssemblyControllerBlockEntity blockEntity) {
        if (blockEntity.isFormed()) {
            blockEntity.updateAssembly();
        }
    }
    
    private void updateAssembly() {
        if (!rocketBuilt && canBuildRocket()) {
            if (energyStorage.extractEnergy(500, false) == 500) {
                buildProgress++;
                
                if (buildProgress >= BUILD_TIME) {
                    buildRocket();
                    buildProgress = 0;
                }
                setChanged();
            }
        }
    }
    
    private boolean canBuildRocket() {
        // Check for required rocket parts in inventory
        return hasRocketParts() && energyStorage.getEnergyStored() >= ENERGY_PER_BUILD;
    }
    
    private boolean hasRocketParts() {
        // Check for engine, fuel tank, hull, and nose cone
        boolean hasEngine = false;
        boolean hasFuelTank = false;
        boolean hasHull = false;
        boolean hasNoseCone = false;
        
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.getItem() == ModBlocks.ROCKET_ENGINE.get().asItem()) hasEngine = true;
            if (stack.getItem() == ModBlocks.ROCKET_FUEL_TANK.get().asItem()) hasFuelTank = true;
            if (stack.getItem() == ModBlocks.ROCKET_HULL.get().asItem()) hasHull = true;
            if (stack.getItem() == ModBlocks.ROCKET_NOSE_CONE.get().asItem()) hasNoseCone = true;
        }
        
        return hasEngine && hasFuelTank && hasHull && hasNoseCone;
    }
    
    private void buildRocket() {
        // Consume rocket parts
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.getItem() == ModBlocks.ROCKET_ENGINE.get().asItem() ||
                stack.getItem() == ModBlocks.ROCKET_FUEL_TANK.get().asItem() ||
                stack.getItem() == ModBlocks.ROCKET_HULL.get().asItem() ||
                stack.getItem() == ModBlocks.ROCKET_NOSE_CONE.get().asItem()) {
                itemHandler.extractItem(i, 1, false);
            }
        }
        
        // Consume energy
        energyStorage.extractEnergy(ENERGY_PER_BUILD, false);
        
        rocketBuilt = true;
        setChanged();
    }
    
    public boolean canLaunch() {
        return rocketBuilt && 
               fuelTank.getFluidAmount() >= 16000 && // 16 buckets of fuel
               oxygenTank.getFluidAmount() >= 16000; // 16 buckets of oxygen
    }
    
    public void launchRocket() {
        if (canLaunch()) {
            // Consume fuel
            fuelTank.drain(16000, IFluidHandler.FluidAction.EXECUTE);
            oxygenTank.drain(16000, IFluidHandler.FluidAction.EXECUTE);
            
            // Reset rocket
            rocketBuilt = false;
            
            // TODO: Trigger rocket launch animation and teleport player to space dimension
            
            setChanged();
        }
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerOpt.cast();
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
        itemHandlerOpt.invalidate();
        fluidHandler.invalidate();
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energyStorage.deserializeNBT(tag.get("energy"));
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        fuelTank.readFromNBT(tag.getCompound("fuel"));
        oxygenTank.readFromNBT(tag.getCompound("oxygen"));
        rocketBuilt = tag.getBoolean("rocketBuilt");
        buildProgress = tag.getInt("buildProgress");
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("energy", energyStorage.serializeNBT());
        tag.put("inventory", itemHandler.serializeNBT());
        tag.put("fuel", fuelTank.writeToNBT(new CompoundTag()));
        tag.put("oxygen", oxygenTank.writeToNBT(new CompoundTag()));
        tag.putBoolean("rocketBuilt", rocketBuilt);
        tag.putInt("buildProgress", buildProgress);
    }
    
    @Override
    public Component getDisplayName() {
        return Component.literal("Rocket Assembly");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new RocketAssemblyMenu(id, inventory, this);
    }
    
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }
    
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }
    
    public boolean isRocketBuilt() {
        return rocketBuilt;
    }
    
    public int getBuildProgress() {
        return buildProgress;
    }
    
    public int getMaxBuildProgress() {
        return BUILD_TIME;
    }
    
    public FluidStack getFuel() {
        return fuelTank.getFluid();
    }
    
    public FluidStack getOxygen() {
        return oxygenTank.getFluid();
    }
    
    public int getFluidCapacity() {
        return FUEL_CAPACITY;
    }
    
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
}