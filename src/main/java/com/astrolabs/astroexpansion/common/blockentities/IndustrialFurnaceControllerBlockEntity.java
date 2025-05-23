package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.api.multiblock.IMultiblockPattern;
import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.menu.IndustrialFurnaceMenu;
import com.astrolabs.astroexpansion.common.multiblock.MultiblockControllerBase;
import com.astrolabs.astroexpansion.common.multiblock.patterns.IndustrialFurnacePattern;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class IndustrialFurnaceControllerBlockEntity extends MultiblockControllerBase implements MenuProvider {
    private static final int SLOT_COUNT = 18;
    private static final int INPUT_SLOTS = 9;
    private static final int OUTPUT_SLOTS = 9;
    private static final int ENERGY_PER_TICK = 20;
    private static final int TICKS_PER_OPERATION = 100;
    
    private final ItemStackHandler itemHandler = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
        
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot < INPUT_SLOTS;
        }
    };
    
    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(10000, 100, 100);
    private final LazyOptional<IItemHandler> itemHandlerLazyOptional = LazyOptional.of(() -> itemHandler);
    private final LazyOptional<IEnergyStorage> energyStorageLazyOptional = LazyOptional.of(() -> energyStorage);
    
    private final IMultiblockPattern pattern = new IndustrialFurnacePattern();
    private int[] progress = new int[INPUT_SLOTS];
    
    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            if (index < INPUT_SLOTS) {
                return progress[index];
            } else if (index == INPUT_SLOTS) {
                return energyStorage.getEnergyStored();
            } else if (index == INPUT_SLOTS + 1) {
                return energyStorage.getMaxEnergyStored();
            } else if (index == INPUT_SLOTS + 2) {
                return formed ? 1 : 0;
            }
            return 0;
        }
        
        @Override
        public void set(int index, int value) {
            if (index < INPUT_SLOTS) {
                progress[index] = value;
            }
        }
        
        @Override
        public int getCount() {
            return INPUT_SLOTS + 3;
        }
    };
    
    public IndustrialFurnaceControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INDUSTRIAL_FURNACE_CONTROLLER.get(), pos, state);
    }
    
    @Override
    public IMultiblockPattern getPattern() {
        return pattern;
    }
    
    @Override
    public void onFormed() {
    }
    
    @Override
    public void onBroken() {
        if (level != null && !level.isClientSide) {
            SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                inventory.setItem(i, itemHandler.getStackInSlot(i));
            }
            Containers.dropContents(level, worldPosition, inventory);
        }
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, IndustrialFurnaceControllerBlockEntity blockEntity) {
        if (blockEntity.formed) {
            blockEntity.processFurnaceOperations();
        } else {
            blockEntity.checkFormation();
        }
    }
    
    private void processFurnaceOperations() {
        boolean changed = false;
        
        for (int i = 0; i < INPUT_SLOTS; i++) {
            ItemStack input = itemHandler.getStackInSlot(i);
            if (!input.isEmpty() && canProcess(i)) {
                if (energyStorage.getEnergyStored() >= ENERGY_PER_TICK) {
                    progress[i]++;
                    energyStorage.extractEnergy(ENERGY_PER_TICK, false);
                    
                    if (progress[i] >= TICKS_PER_OPERATION) {
                        processItem(i);
                        progress[i] = 0;
                    }
                    changed = true;
                }
            } else if (progress[i] > 0) {
                progress[i] = 0;
                changed = true;
            }
        }
        
        if (changed) {
            setChanged();
        }
    }
    
    private boolean canProcess(int slot) {
        ItemStack input = itemHandler.getStackInSlot(slot);
        if (input.isEmpty()) return false;
        
        Optional<SmeltingRecipe> recipe = level.getRecipeManager()
            .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(input), level);
        
        if (recipe.isEmpty()) return false;
        
        ItemStack result = recipe.get().getResultItem(level.registryAccess());
        int outputSlot = findOutputSlot(result);
        
        return outputSlot != -1;
    }
    
    private void processItem(int slot) {
        ItemStack input = itemHandler.getStackInSlot(slot);
        Optional<SmeltingRecipe> recipe = level.getRecipeManager()
            .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(input), level);
        
        if (recipe.isPresent()) {
            ItemStack result = recipe.get().getResultItem(level.registryAccess()).copy();
            int outputSlot = findOutputSlot(result);
            
            if (outputSlot != -1) {
                input.shrink(1);
                itemHandler.setStackInSlot(slot, input);
                
                ItemStack existing = itemHandler.getStackInSlot(outputSlot);
                if (existing.isEmpty()) {
                    itemHandler.setStackInSlot(outputSlot, result);
                } else {
                    existing.grow(result.getCount());
                }
            }
        }
    }
    
    private int findOutputSlot(ItemStack stack) {
        for (int i = INPUT_SLOTS; i < SLOT_COUNT; i++) {
            ItemStack existing = itemHandler.getStackInSlot(i);
            if (existing.isEmpty() || (ItemStack.isSameItemSameTags(existing, stack) && 
                existing.getCount() + stack.getCount() <= existing.getMaxStackSize())) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public Component getDisplayName() {
        return Component.literal("Industrial Furnace Array");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
        return new IndustrialFurnaceMenu(windowId, inventory, this, this.data);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        energyStorage.deserializeNBT(tag.get("energy"));
        
        if (tag.contains("progress")) {
            int[] savedProgress = tag.getIntArray("progress");
            System.arraycopy(savedProgress, 0, progress, 0, Math.min(savedProgress.length, progress.length));
        }
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", itemHandler.serializeNBT());
        tag.put("energy", energyStorage.serializeNBT());
        tag.putIntArray("progress", progress);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandlerLazyOptional.invalidate();
        energyStorageLazyOptional.invalidate();
    }
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerLazyOptional.cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return energyStorageLazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }
}