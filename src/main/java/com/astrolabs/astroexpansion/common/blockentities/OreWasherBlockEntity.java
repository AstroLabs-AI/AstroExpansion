package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.blocks.OreWasherBlock;
import com.astrolabs.astroexpansion.common.blocks.machines.base.UpgradeableMachineBlockEntity;
import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.capabilities.CombinedItemHandler;
import com.astrolabs.astroexpansion.common.menu.OreWasherMenu;
import com.astrolabs.astroexpansion.common.recipes.WashingRecipe;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import com.astrolabs.astroexpansion.common.registry.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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

import java.util.Optional;
import java.util.Random;

public class OreWasherBlockEntity extends UpgradeableMachineBlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
        
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == 0; // Only input slot accepts items
        }
    };
    
    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(10000, 100, 100);
    
    private final FluidTank fluidTank = new FluidTank(4000) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == net.minecraft.world.level.material.Fluids.WATER;
        }
    };
    
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;
    private int baseMaxProgress = 100;
    private int energyPerTick = 10;
    private int baseEnergyPerTick = 10;
    private int waterPerOperation = 100;
    private int baseWaterPerOperation = 100;
    private final Random random = new Random();
    
    public OreWasherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ORE_WASHER.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> OreWasherBlockEntity.this.progress;
                    case 1 -> OreWasherBlockEntity.this.maxProgress;
                    case 2 -> OreWasherBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> OreWasherBlockEntity.this.energyStorage.getMaxEnergyStored();
                    case 4 -> OreWasherBlockEntity.this.fluidTank.getFluidAmount();
                    case 5 -> OreWasherBlockEntity.this.fluidTank.getCapacity();
                    default -> 0;
                };
            }
            
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> OreWasherBlockEntity.this.progress = value;
                    case 1 -> OreWasherBlockEntity.this.maxProgress = value;
                    case 2 -> OreWasherBlockEntity.this.energyStorage.setEnergy(value);
                }
            }
            
            @Override
            public int getCount() {
                return 6;
            }
        };
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.astroexpansion.ore_washer");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new OreWasherMenu(id, inventory, this, this.data);
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                // Internal access - return combined handler with upgrades
                return LazyOptional.of(() -> new CombinedItemHandler(itemHandler, upgradeHandler)).cast();
            }
            return lazyItemHandler.cast();
        }
        
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidHandler.cast();
        }
        
        return super.getCapability(cap, side);
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
        lazyFluidHandler = LazyOptional.of(() -> fluidTank);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
        lazyFluidHandler.invalidate();
    }
    
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.put("energy", energyStorage.writeToNBT(new CompoundTag()));
        nbt.putInt("progress", progress);
        nbt.put("fluid", fluidTank.writeToNBT(new CompoundTag()));
        super.saveAdditional(nbt);
    }
    
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        energyStorage.readFromNBT(nbt.getCompound("energy"));
        progress = nbt.getInt("progress");
        fluidTank.readFromNBT(nbt.getCompound("fluid"));
        onUpgradesChanged();
    }
    
    @Override
    public void onUpgradesChanged() {
        super.onUpgradesChanged();
        // Update processing values based on upgrades
        maxProgress = (int)(baseMaxProgress / getSpeedMultiplier());
        energyPerTick = (int)(baseEnergyPerTick * getEfficiencyMultiplier());
        waterPerOperation = (int)(baseWaterPerOperation / getEfficiencyMultiplier());
    }
    
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, OreWasherBlockEntity entity) {
        if (level.isClientSide) {
            return;
        }
        
        boolean hasRecipe = entity.hasRecipe();
        boolean wasProcessing = entity.progress > 0;
        
        if (hasRecipe && entity.hasEnoughEnergy() && entity.hasEnoughWater()) {
            // Apply speed upgrade
            int progressIncrement = (int)Math.ceil(entity.getSpeedMultiplier());
            entity.progress += progressIncrement;
            
            // Apply efficiency upgrade to energy consumption
            int energyToConsume = (int)(entity.energyPerTick * entity.getEfficiencyMultiplier());
            entity.energyStorage.extractEnergy(energyToConsume, false);
            
            if (entity.progress >= entity.maxProgress) {
                entity.craftItem();
                entity.progress = 0;
            }
            
            setChanged(level, pos, state);
        } else if (entity.progress > 0) {
            entity.progress = Math.max(0, entity.progress - 2);
            setChanged(level, pos, state);
        }
        
        if (wasProcessing != (entity.progress > 0)) {
            state = state.setValue(OreWasherBlock.LIT, entity.progress > 0);
            level.setBlock(pos, state, 3);
        }
    }
    
    private boolean hasRecipe() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        
        Optional<WashingRecipe> recipe = level.getRecipeManager()
            .getRecipeFor(ModRecipeTypes.WASHING.get(), inventory, level);
        
        if (recipe.isPresent()) {
            // Update max progress based on recipe time and speed upgrade
            WashingRecipe washingRecipe = recipe.get();
            baseMaxProgress = washingRecipe.getProcessTime();
            maxProgress = (int)(baseMaxProgress / getSpeedMultiplier());
            
            // Update energy cost based on recipe and efficiency upgrade
            baseEnergyPerTick = washingRecipe.getEnergyCost() / baseMaxProgress;
            energyPerTick = (int)(baseEnergyPerTick * getEfficiencyMultiplier());
            
            return canInsertIntoOutputSlots(washingRecipe);
        }
        
        return false;
    }
    
    private boolean hasEnoughEnergy() {
        return energyStorage.getEnergyStored() >= energyPerTick;
    }
    
    private boolean hasEnoughWater() {
        return fluidTank.getFluidAmount() >= waterPerOperation;
    }
    
    private void craftItem() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        
        Optional<WashingRecipe> recipe = level.getRecipeManager()
            .getRecipeFor(ModRecipeTypes.WASHING.get(), inventory, level);
        
        if (recipe.isPresent()) {
            WashingRecipe washingRecipe = recipe.get();
            
            itemHandler.extractItem(0, 1, false);
            
            // Consume water with efficiency upgrade
            fluidTank.drain(waterPerOperation, IFluidHandler.FluidAction.EXECUTE);
            
            // Primary output
            ItemStack primaryOutput = washingRecipe.getResultItem(level.registryAccess()).copy();
            
            // Apply fortune upgrade to primary output
            if (getFortuneMultiplier() > 1.0f && random.nextFloat() < (getFortuneMultiplier() - 1.0f)) {
                primaryOutput.grow(1);
            }
            
            ItemStack slot1 = itemHandler.getStackInSlot(1);
            if (slot1.isEmpty()) {
                itemHandler.setStackInSlot(1, primaryOutput);
            } else {
                slot1.grow(primaryOutput.getCount());
            }
            
            // Secondary output with fortune upgrade affecting chance
            float enhancedSecondaryChance = Math.min(1.0f, washingRecipe.getSecondaryChance() * getFortuneMultiplier());
            if (level.random.nextFloat() < enhancedSecondaryChance) {
                ItemStack secondaryOutput = washingRecipe.getSecondaryOutput().copy();
                
                // Additional fortune effect on secondary output
                if (getFortuneMultiplier() > 1.5f && random.nextFloat() < (getFortuneMultiplier() - 1.5f)) {
                    secondaryOutput.grow(1);
                }
                
                ItemStack slot2 = itemHandler.getStackInSlot(2);
                if (slot2.isEmpty()) {
                    itemHandler.setStackInSlot(2, secondaryOutput);
                } else {
                    slot2.grow(secondaryOutput.getCount());
                }
            }
        }
    }
    
    private boolean canInsertIntoOutputSlots(WashingRecipe recipe) {
        ItemStack primaryOutput = recipe.getResultItem(level.registryAccess());
        ItemStack secondaryOutput = recipe.getSecondaryOutput();
        
        ItemStack slot1 = itemHandler.getStackInSlot(1);
        ItemStack slot2 = itemHandler.getStackInSlot(2);
        
        boolean primaryFits = slot1.isEmpty() || (slot1.getItem() == primaryOutput.getItem() &&
            slot1.getCount() + primaryOutput.getCount() <= slot1.getMaxStackSize());
        
        boolean secondaryFits = secondaryOutput.isEmpty() || slot2.isEmpty() || 
            (slot2.getItem() == secondaryOutput.getItem() &&
            slot2.getCount() + secondaryOutput.getCount() <= slot2.getMaxStackSize());
        
        return primaryFits && secondaryFits;
    }
}