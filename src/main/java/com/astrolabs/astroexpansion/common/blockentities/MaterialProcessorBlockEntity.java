package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.blocks.MaterialProcessorBlock;
import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.menu.MaterialProcessorMenu;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MaterialProcessorBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
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
    
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 200;
    private int energyPerTick = 20;
    
    public MaterialProcessorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MATERIAL_PROCESSOR.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> MaterialProcessorBlockEntity.this.progress;
                    case 1 -> MaterialProcessorBlockEntity.this.maxProgress;
                    case 2 -> MaterialProcessorBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> MaterialProcessorBlockEntity.this.energyStorage.getMaxEnergyStored();
                    default -> 0;
                };
            }
            
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> MaterialProcessorBlockEntity.this.progress = value;
                    case 1 -> MaterialProcessorBlockEntity.this.maxProgress = value;
                    case 2 -> MaterialProcessorBlockEntity.this.energyStorage.setEnergy(value);
                }
            }
            
            @Override
            public int getCount() {
                return 4;
            }
        };
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.astroexpansion.material_processor");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new MaterialProcessorMenu(id, inventory, this, this.data);
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        
        return super.getCapability(cap, side);
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
    }
    
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.put("energy", energyStorage.writeToNBT(new CompoundTag()));
        nbt.putInt("progress", progress);
        super.saveAdditional(nbt);
    }
    
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        energyStorage.readFromNBT(nbt.getCompound("energy"));
        progress = nbt.getInt("progress");
    }
    
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, MaterialProcessorBlockEntity entity) {
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
            state = state.setValue(MaterialProcessorBlock.LIT, entity.progress > 0);
            level.setBlock(pos, state, 3);
        }
    }
    
    private boolean hasRecipe() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        
        Optional<ProcessingRecipe> recipe = level.getRecipeManager()
            .getRecipeFor(ModRecipeTypes.PROCESSING.get(), inventory, level);
        
        if (recipe.isPresent()) {
            ItemStack result = recipe.get().getResultItem(level.registryAccess());
            return canInsertIntoOutputSlot(result);
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
        
        Optional<ProcessingRecipe> recipe = level.getRecipeManager()
            .getRecipeFor(ModRecipeTypes.PROCESSING.get(), inventory, level);
        
        if (recipe.isPresent()) {
            ItemStack result = recipe.get().getResultItem(level.registryAccess());
            
            itemHandler.extractItem(0, 1, false);
            itemHandler.setStackInSlot(1, new ItemStack(result.getItem(),
                itemHandler.getStackInSlot(1).getCount() + result.getCount()));
        }
    }
    
    private boolean canInsertIntoOutputSlot(ItemStack result) {
        ItemStack outputStack = itemHandler.getStackInSlot(1);
        return outputStack.isEmpty() || (outputStack.getItem() == result.getItem() &&
            outputStack.getCount() + result.getCount() <= outputStack.getMaxStackSize());
    }
}