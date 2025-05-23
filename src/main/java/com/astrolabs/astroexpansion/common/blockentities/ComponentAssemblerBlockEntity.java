package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.menu.ComponentAssemblerMenu;
import com.astrolabs.astroexpansion.common.recipe.AssemblyRecipe;
import com.astrolabs.astroexpansion.common.recipe.AssemblyRecipeRegistry;
import com.astrolabs.astroexpansion.common.registry.AEBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ComponentAssemblerBlockEntity extends AbstractMachineBlockEntity implements Container, MenuProvider {
    
    private static final int ENERGY_CAPACITY = 20000;
    private static final int SLOT_COUNT = 10; // 3x3 crafting grid + output
    
    private NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private AssemblyRecipe currentRecipe = null;
    
    public ComponentAssemblerBlockEntity(BlockPos pos, BlockState state) {
        super(AEBlockEntities.COMPONENT_ASSEMBLER.get(), pos, state, ENERGY_CAPACITY);
    }
    
    @Override
    public void tick() {
        if (level == null || level.isClientSide) return;
        
        boolean wasActive = isActive;
        isActive = false;
        
        // Check for recipe match
        if (currentRecipe == null || !currentRecipe.matches(this)) {
            currentRecipe = AssemblyRecipeRegistry.findRecipe(this).orElse(null);
            processTime = 0;
        }
        
        if (currentRecipe != null && canProcess() && energyStorage.getEnergyStored() >= currentRecipe.getEnergyPerTick()) {
            isActive = true;
            
            if (processTime == 0) {
                maxProcessTime = currentRecipe.getProcessTime();
            }
            
            energyStorage.extractEnergy(currentRecipe.getEnergyPerTick(), false);
            processTime++;
            
            if (processTime >= maxProcessTime) {
                processRecipe();
                processTime = 0;
                maxProcessTime = 0;
                currentRecipe = null;
            }
        } else if (processTime > 0 && (currentRecipe == null || !canProcess())) {
            processTime = 0;
            maxProcessTime = 0;
        }
        
        if (wasActive != isActive) {
            markDirtyAndSync();
        }
    }
    
    private boolean canProcess() {
        if (currentRecipe == null) return false;
        
        ItemStack output = items.get(9); // Output slot
        ItemStack recipeOutput = currentRecipe.getOutput();
        
        if (output.isEmpty()) return true;
        
        return ItemStack.isSameItemSameComponents(output, recipeOutput) && 
               output.getCount() + recipeOutput.getCount() <= output.getMaxStackSize();
    }
    
    private void processRecipe() {
        if (currentRecipe == null) return;
        
        // Consume ingredients
        for (int i = 0; i < 9; i++) {
            if (!items.get(i).isEmpty() && currentRecipe.getIngredient(i).test(items.get(i))) {
                items.get(i).shrink(1);
            }
        }
        
        // Produce output
        ItemStack output = items.get(9);
        ItemStack recipeOutput = currentRecipe.getOutput();
        
        if (output.isEmpty()) {
            items.set(9, recipeOutput.copy());
        } else {
            output.grow(recipeOutput.getCount());
        }
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, ComponentAssemblerBlockEntity blockEntity) {
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
        items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
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
        return Component.translatable("block.astroexpansion.component_assembler");
    }
    
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ComponentAssemblerMenu(containerId, playerInventory, this, getContainerData());
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