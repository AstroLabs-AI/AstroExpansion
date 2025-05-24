package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.blocks.TieredMaterialProcessorBlock;
import com.astrolabs.astroexpansion.common.blocks.machines.base.MachineTier;
import com.astrolabs.astroexpansion.common.blocks.machines.base.UpgradeableMachineBlockEntity;
import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.menu.TieredMaterialProcessorMenu;
import com.astrolabs.astroexpansion.common.recipes.ProcessingRecipe;
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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import com.astrolabs.astroexpansion.common.capabilities.CombinedItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

public class TieredMaterialProcessorBlockEntity extends UpgradeableMachineBlockEntity implements MenuProvider {
    private final MachineTier tier;
    private final ItemStackHandler itemHandler;
    private final AstroEnergyStorage energyStorage;
    
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyInputHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyOutputHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyCombinedHandler = LazyOptional.empty();
    
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;
    private static final int ENERGY_REQUIRED = 20;
    
    private static final Random random = new Random();
    
    public TieredMaterialProcessorBlockEntity(BlockPos pos, BlockState state, MachineTier tier) {
        super(getBlockEntityType(tier), pos, state, tier.getUpgradeSlots());
        this.tier = tier;
        
        // Initialize item handler with 2 base slots + upgrade slots
        this.itemHandler = new ItemStackHandler(2) {
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
        
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> TieredMaterialProcessorBlockEntity.this.progress;
                    case 1 -> TieredMaterialProcessorBlockEntity.this.maxProgress;
                    case 2 -> TieredMaterialProcessorBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> TieredMaterialProcessorBlockEntity.this.energyStorage.getMaxEnergyStored();
                    default -> 0;
                };
            }
            
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> TieredMaterialProcessorBlockEntity.this.progress = value;
                    case 1 -> TieredMaterialProcessorBlockEntity.this.maxProgress = value;
                }
            }
            
            @Override
            public int getCount() {
                return 4;
            }
        };
    }
    
    private static BlockEntityType<?> getBlockEntityType(MachineTier tier) {
        return switch (tier) {
            case BASIC -> ModBlockEntities.MATERIAL_PROCESSOR_BASIC.get();
            case ADVANCED -> ModBlockEntities.MATERIAL_PROCESSOR_ADVANCED.get();
            case ELITE -> ModBlockEntities.MATERIAL_PROCESSOR_ELITE.get();
        };
    }
    
    public MachineTier getTier() {
        return tier;
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.astroexpansion.material_processor_" + tier.getSerializedName());
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new TieredMaterialProcessorMenu(id, inventory, this, this.data, tier);
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
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
                return 1;
            }
            
            @Override
            public @NotNull ItemStack getStackInSlot(int slot) {
                return itemHandler.getStackInSlot(1);
            }
            
            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                return stack;
            }
            
            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                return itemHandler.extractItem(1, amount, simulate);
            }
            
            @Override
            public int getSlotLimit(int slot) {
                return itemHandler.getSlotLimit(1);
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
        lazyInputHandler.invalidate();
        lazyOutputHandler.invalidate();
        lazyCombinedHandler.invalidate();
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.put("energy", energyStorage.serializeNBT());
        tag.putInt("progress", progress);
        tag.putString("tier", tier.getSerializedName());
        super.saveAdditional(tag);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        energyStorage.deserializeNBT(tag.get("energy"));
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
    
    public static void tick(Level level, BlockPos pos, BlockState state, TieredMaterialProcessorBlockEntity entity) {
        if (level.isClientSide) {
            return;
        }
        
        // Apply tier bonuses
        float speedMultiplier = entity.tier.getSpeedMultiplier() * entity.getSpeedMultiplier();
        float efficiencyMultiplier = entity.tier.getEfficiencyMultiplier() * entity.getEnergyEfficiency();
        
        if (hasRecipe(entity)) {
            // Calculate energy cost with efficiency
            int energyCost = (int) (ENERGY_REQUIRED / efficiencyMultiplier);
            
            if (entity.energyStorage.getEnergyStored() >= energyCost) {
                entity.energyStorage.extractEnergy(energyCost, false);
                
                // Progress faster based on speed multiplier
                entity.progress += speedMultiplier;
                
                if (entity.progress >= entity.maxProgress) {
                    craftItem(entity);
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
        if (state.getValue(TieredMaterialProcessorBlock.LIT) != lit) {
            level.setBlock(pos, state.setValue(TieredMaterialProcessorBlock.LIT, lit), 3);
        }
    }
    
    private void resetProgress() {
        this.progress = 0;
    }
    
    private static void craftItem(TieredMaterialProcessorBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }
        
        Optional<ProcessingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.PROCESSING.get(), inventory, level);
        
        if (hasRecipe(entity)) {
            ItemStack output = recipe.get().getResultItem(level.registryAccess());
            
            // Add extra outputs for higher tier machines
            if (entity.tier == MachineTier.ADVANCED && random.nextFloat() < 0.25f) {
                output.grow(1); // 25% chance for extra output
            } else if (entity.tier == MachineTier.ELITE && random.nextFloat() < 0.5f) {
                output.grow(1); // 50% chance for extra output
            }
            
            entity.itemHandler.extractItem(0, 1, false);
            entity.itemHandler.setStackInSlot(1, new ItemStack(output.getItem(),
                    entity.itemHandler.getStackInSlot(1).getCount() + output.getCount()));
            
            entity.resetProgress();
        }
    }
    
    private static boolean hasRecipe(TieredMaterialProcessorBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }
        
        Optional<ProcessingRecipe> match = level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.PROCESSING.get(), inventory, level);
        
        return match.isPresent() && canInsertAmountIntoOutputSlot(inventory) &&
                canInsertItemIntoOutputSlot(inventory, match.get().getResultItem(level.registryAccess()));
    }
    
    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack output) {
        return inventory.getItem(1).getItem() == output.getItem() || inventory.getItem(1).isEmpty();
    }
    
    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
        return inventory.getItem(1).getMaxStackSize() > inventory.getItem(1).getCount();
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
    
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
}