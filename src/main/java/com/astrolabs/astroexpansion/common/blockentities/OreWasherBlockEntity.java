package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.blocks.OreWasherBlock;
import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
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
import net.minecraft.world.level.block.entity.BlockEntity;
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

public class OreWasherBlockEntity extends BlockEntity implements MenuProvider {
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
    private int energyPerTick = 10;
    
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
        
        if (hasRecipe && entity.hasEnoughEnergy()) {
            entity.progress++;
            entity.energyStorage.extractEnergy(entity.energyPerTick, false);
            
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
            return canInsertIntoOutputSlots(recipe.get());
        }
        
        return false;
    }
    
    private boolean hasEnoughEnergy() {
        return energyStorage.getEnergyStored() >= energyPerTick;
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
            
            // Primary output
            ItemStack primaryOutput = washingRecipe.getResultItem(level.registryAccess());
            itemHandler.setStackInSlot(1, new ItemStack(primaryOutput.getItem(),
                itemHandler.getStackInSlot(1).getCount() + primaryOutput.getCount()));
            
            // Secondary output (10% chance)
            if (level.random.nextFloat() < washingRecipe.getSecondaryChance()) {
                ItemStack secondaryOutput = washingRecipe.getSecondaryOutput();
                itemHandler.setStackInSlot(2, new ItemStack(secondaryOutput.getItem(),
                    itemHandler.getStackInSlot(2).getCount() + secondaryOutput.getCount()));
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