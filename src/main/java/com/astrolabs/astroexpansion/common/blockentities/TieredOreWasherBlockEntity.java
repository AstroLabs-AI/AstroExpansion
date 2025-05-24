package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.blocks.TieredOreWasherBlock;
import com.astrolabs.astroexpansion.common.blocks.machines.base.MachineTier;
import com.astrolabs.astroexpansion.common.blocks.machines.base.UpgradeableMachineBlockEntity;
import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.menu.TieredOreWasherMenu;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
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
import com.astrolabs.astroexpansion.common.capabilities.CombinedItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

public class TieredOreWasherBlockEntity extends UpgradeableMachineBlockEntity implements MenuProvider {
    private final MachineTier tier;
    private final ItemStackHandler itemHandler;
    private final AstroEnergyStorage energyStorage;
    private final FluidTank fluidTank;
    
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyInputHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyOutputHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyCombinedHandler = LazyOptional.empty();
    
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 120; // Slightly longer than material processor
    private static final int ENERGY_REQUIRED = 15;
    private static final int WATER_REQUIRED = 100; // mB per operation
    
    private static final Random random = new Random();
    
    public TieredOreWasherBlockEntity(BlockPos pos, BlockState state, MachineTier tier) {
        super(getBlockEntityType(tier), pos, state, tier.getUpgradeSlots());
        this.tier = tier;
        
        // Initialize item handler with 3 slots (1 input, 2 output)
        this.itemHandler = new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
            
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return slot == 0; // Only input slot accepts items
            }
        };
        
        // Initialize energy storage based on tier
        this.energyStorage = new AstroEnergyStorage(
            tier.getEnergyCapacity(),
            tier.getEnergyCapacity() / 50,
            tier.getEnergyCapacity() / 50
        );
        
        // Initialize fluid tank for water
        this.fluidTank = new FluidTank(4000) {
            @Override
            protected void onContentsChanged() {
                setChanged();
            }
            
            @Override
            public boolean isFluidValid(FluidStack stack) {
                return stack.getFluid() == net.minecraft.world.level.material.Fluids.WATER;
            }
        };
        
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> TieredOreWasherBlockEntity.this.progress;
                    case 1 -> TieredOreWasherBlockEntity.this.maxProgress;
                    case 2 -> TieredOreWasherBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> TieredOreWasherBlockEntity.this.energyStorage.getMaxEnergyStored();
                    case 4 -> TieredOreWasherBlockEntity.this.fluidTank.getFluidAmount();
                    case 5 -> TieredOreWasherBlockEntity.this.fluidTank.getCapacity();
                    default -> 0;
                };
            }
            
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> TieredOreWasherBlockEntity.this.progress = value;
                    case 1 -> TieredOreWasherBlockEntity.this.maxProgress = value;
                }
            }
            
            @Override
            public int getCount() {
                return 6;
            }
        };
    }
    
    private static BlockEntityType<?> getBlockEntityType(MachineTier tier) {
        return switch (tier) {
            case BASIC -> ModBlockEntities.ORE_WASHER_BASIC.get();
            case ADVANCED -> ModBlockEntities.ORE_WASHER_ADVANCED.get();
            case ELITE -> ModBlockEntities.ORE_WASHER_ELITE.get();
        };
    }
    
    public MachineTier getTier() {
        return tier;
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.astroexpansion.ore_washer_" + tier.getSerializedName());
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new TieredOreWasherMenu(id, inventory, this, this.data, tier);
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidHandler.cast();
        }
        
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                return lazyCombinedHandler.cast();
            } else if (side == Direction.UP) {
                return lazyInputHandler.cast();
            } else {
                return lazyOutputHandler.cast();
            }
        }
        
        return super.getCapability(cap, side);
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
        lazyFluidHandler = LazyOptional.of(() -> fluidTank);
        
        lazyInputHandler = LazyOptional.of(() -> new IItemHandler() {
            @Override
            public int getSlots() {
                return 1;
            }
            
            @Override
            public @NotNull ItemStack getStackInSlot(int slot) {
                return itemHandler.getStackInSlot(0);
            }
            
            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                return itemHandler.insertItem(0, stack, simulate);
            }
            
            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }
            
            @Override
            public int getSlotLimit(int slot) {
                return itemHandler.getSlotLimit(0);
            }
            
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return itemHandler.isItemValid(0, stack);
            }
        });
        
        lazyOutputHandler = LazyOptional.of(() -> new IItemHandler() {
            @Override
            public int getSlots() {
                return 2;
            }
            
            @Override
            public @NotNull ItemStack getStackInSlot(int slot) {
                return itemHandler.getStackInSlot(slot + 1);
            }
            
            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                return stack;
            }
            
            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                return itemHandler.extractItem(slot + 1, amount, simulate);
            }
            
            @Override
            public int getSlotLimit(int slot) {
                return itemHandler.getSlotLimit(slot + 1);
            }
            
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return false;
            }
        });
        
        lazyCombinedHandler = LazyOptional.of(() -> new CombinedItemHandler(itemHandler, upgradeHandler));
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
        lazyFluidHandler.invalidate();
        lazyInputHandler.invalidate();
        lazyOutputHandler.invalidate();
        lazyCombinedHandler.invalidate();
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.put("energy", energyStorage.serializeNBT());
        tag.put("fluid", fluidTank.writeToNBT(new CompoundTag()));
        tag.putInt("progress", progress);
        tag.putString("tier", tier.getSerializedName());
        super.saveAdditional(tag);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        energyStorage.deserializeNBT(tag.get("energy"));
        fluidTank.readFromNBT(tag.getCompound("fluid"));
        progress = tag.getInt("progress");
    }
    
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        
        Containers.dropContents(this.level, this.worldPosition, inventory);
        
        // Drop upgrade items
        dropUpgrades();
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, TieredOreWasherBlockEntity entity) {
        if (level.isClientSide) {
            return;
        }
        
        // Apply tier bonuses
        float speedMultiplier = entity.tier.getSpeedMultiplier() * entity.getSpeedMultiplier();
        float efficiencyMultiplier = entity.tier.getEfficiencyMultiplier() * entity.getEnergyEfficiency();
        
        if (hasRecipe(entity) && entity.fluidTank.getFluidAmount() >= WATER_REQUIRED) {
            // Calculate energy cost with efficiency
            int energyCost = (int) (ENERGY_REQUIRED / efficiencyMultiplier);
            
            if (entity.energyStorage.getEnergyStored() >= energyCost) {
                entity.energyStorage.extractEnergy(energyCost, false);
                
                // Progress faster based on speed multiplier
                entity.progress += speedMultiplier;
                
                if (entity.progress >= entity.maxProgress) {
                    craftItem(entity);
                    entity.fluidTank.drain(WATER_REQUIRED, IFluidHandler.FluidAction.EXECUTE);
                    entity.resetProgress();
                }
                
                setBlockLit(level, pos, state, true);
            } else {
                setBlockLit(level, pos, state, false);
            }
        } else {
            entity.resetProgress();
            setBlockLit(level, pos, state, false);
        }
    }
    
    private static void setBlockLit(Level level, BlockPos pos, BlockState state, boolean lit) {
        if (state.getValue(TieredOreWasherBlock.LIT) != lit) {
            level.setBlock(pos, state.setValue(TieredOreWasherBlock.LIT, lit), 3);
        }
    }
    
    private void resetProgress() {
        this.progress = 0;
    }
    
    private static void craftItem(TieredOreWasherBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }
        
        Optional<WashingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.WASHING.get(), inventory, level);
        
        if (recipe.isPresent()) {
            ItemStack primaryOutput = recipe.get().getResultItem(level.registryAccess());
            ItemStack secondaryOutput = recipe.get().getSecondaryOutput();
            float secondaryChance = recipe.get().getSecondaryChance();
            
            // Increase secondary chance for higher tiers
            if (entity.tier == MachineTier.ADVANCED) {
                secondaryChance *= 1.25f;
            } else if (entity.tier == MachineTier.ELITE) {
                secondaryChance *= 1.5f;
            }
            
            // Apply fortune multiplier to secondary chance
            secondaryChance *= entity.getFortuneMultiplier();
            
            entity.itemHandler.extractItem(0, 1, false);
            
            // Primary output
            ItemStack primarySlot = entity.itemHandler.getStackInSlot(1);
            if (primarySlot.isEmpty()) {
                entity.itemHandler.setStackInSlot(1, primaryOutput.copy());
            } else if (primarySlot.getItem() == primaryOutput.getItem()) {
                primarySlot.grow(primaryOutput.getCount());
            }
            
            // Secondary output (with chance)
            if (!secondaryOutput.isEmpty() && random.nextFloat() < secondaryChance) {
                ItemStack secondarySlot = entity.itemHandler.getStackInSlot(2);
                if (secondarySlot.isEmpty()) {
                    entity.itemHandler.setStackInSlot(2, secondaryOutput.copy());
                } else if (secondarySlot.getItem() == secondaryOutput.getItem()) {
                    secondarySlot.grow(secondaryOutput.getCount());
                }
            }
            
            entity.resetProgress();
        }
    }
    
    private static boolean hasRecipe(TieredOreWasherBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }
        
        Optional<WashingRecipe> match = level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.WASHING.get(), inventory, level);
        
        if (match.isEmpty()) return false;
        
        ItemStack primaryOutput = match.get().getResultItem(level.registryAccess());
        ItemStack secondaryOutput = match.get().getSecondaryOutput();
        
        // Check primary output slot
        ItemStack primarySlot = entity.itemHandler.getStackInSlot(1);
        if (!primarySlot.isEmpty() && 
            (primarySlot.getItem() != primaryOutput.getItem() || 
             primarySlot.getCount() + primaryOutput.getCount() > primarySlot.getMaxStackSize())) {
            return false;
        }
        
        // Check secondary output slot if needed
        if (!secondaryOutput.isEmpty()) {
            ItemStack secondarySlot = entity.itemHandler.getStackInSlot(2);
            if (!secondarySlot.isEmpty() && 
                (secondarySlot.getItem() != secondaryOutput.getItem() || 
                 secondarySlot.getCount() + secondaryOutput.getCount() > secondarySlot.getMaxStackSize())) {
                return false;
            }
        }
        
        return true;
    }
    
    public int getProgress() {
        return progress;
    }
    
    public int getMaxProgress() {
        return maxProgress;
    }
    
    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }
    
    public FluidTank getFluidTank() {
        return fluidTank;
    }
    
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
}