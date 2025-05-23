package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.blocks.ElectricFurnaceBlock;
import com.astrolabs.astroexpansion.common.menu.ElectricFurnaceMenu;
import com.astrolabs.astroexpansion.common.registry.AEBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class ElectricFurnaceBlockEntity extends AbstractMachineBlockEntity implements Container, MenuProvider {
    
    private static final int ENERGY_CAPACITY = 20000;
    private static final int ENERGY_PER_TICK = 20;
    private static final int PROCESS_TIME = 100; // 5 seconds (2x faster than furnace)
    
    private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY); // Input, Output
    
    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(AEBlockEntities.ELECTRIC_FURNACE.get(), pos, state, ENERGY_CAPACITY);
    }
    
    @Override
    public void tick() {
        if (level == null || level.isClientSide) return;
        
        boolean wasActive = isActive;
        isActive = false;
        
        Optional<RecipeHolder<SmeltingRecipe>> recipe = getCurrentRecipe();
        
        if (recipe.isPresent() && canSmelt(recipe.get()) && energyStorage.getEnergyStored() >= ENERGY_PER_TICK) {
            isActive = true;
            
            if (processTime == 0) {
                maxProcessTime = PROCESS_TIME;
            }
            
            energyStorage.extractEnergy(ENERGY_PER_TICK, false);
            processTime++;
            
            if (processTime >= maxProcessTime) {
                smeltItem(recipe.get());
                processTime = 0;
                maxProcessTime = 0;
            }
        } else if (processTime > 0) {
            processTime = 0;
            maxProcessTime = 0;
        }
        
        if (wasActive != isActive) {
            level.setBlock(worldPosition, getBlockState().setValue(ElectricFurnaceBlock.LIT, isActive), 3);
            markDirtyAndSync();
        }
    }
    
    private Optional<RecipeHolder<SmeltingRecipe>> getCurrentRecipe() {
        SimpleContainer inv = new SimpleContainer(items.get(0));
        return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, inv, level);
    }
    
    private boolean canSmelt(RecipeHolder<SmeltingRecipe> recipe) {
        if (items.get(0).isEmpty()) return false;
        
        ItemStack result = recipe.value().getResultItem(level.registryAccess());
        if (result.isEmpty()) return false;
        
        ItemStack output = items.get(1);
        if (output.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(output, result)) return false;
        
        return output.getCount() + result.getCount() <= output.getMaxStackSize();
    }
    
    private void smeltItem(RecipeHolder<SmeltingRecipe> recipe) {
        ItemStack result = recipe.value().getResultItem(level.registryAccess());
        ItemStack output = items.get(1);
        
        if (output.isEmpty()) {
            items.set(1, result.copy());
        } else if (ItemStack.isSameItemSameComponents(output, result)) {
            output.grow(result.getCount());
        }
        
        items.get(0).shrink(1);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, ElectricFurnaceBlockEntity blockEntity) {
        blockEntity.tick();
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
    }
    
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(2, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
    }
    
    // Container implementation
    @Override
    public int getContainerSize() {
        return items.size();
    }
    
    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }
    
    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }
    
    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(items, slot, amount);
    }
    
    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }
    
    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }
    
    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }
    
    @Override
    public void clearContent() {
        items.clear();
    }
    
    // MenuProvider implementation
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.astroexpansion.electric_furnace");
    }
    
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ElectricFurnaceMenu(containerId, playerInventory, this, getContainerData());
    }
    
    protected ContainerData getContainerData() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> energyStorage.getEnergyStored();
                    case 1 -> energyStorage.getMaxEnergyStored();
                    case 2 -> processTime;
                    case 3 -> maxProcessTime;
                    default -> 0;
                };
            }
            
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> energyStorage.setEnergy(value);
                    case 2 -> processTime = value;
                    case 3 -> maxProcessTime = value;
                }
            }
            
            @Override
            public int getCount() {
                return 4;
            }
        };
    }
}