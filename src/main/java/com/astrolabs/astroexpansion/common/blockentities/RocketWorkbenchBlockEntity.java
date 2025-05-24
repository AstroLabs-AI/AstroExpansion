package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.menu.RocketWorkbenchMenu;
import com.astrolabs.astroexpansion.common.recipe.RocketWorkbenchRecipe;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RocketWorkbenchBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(10) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    
    // Slots:
    // 0-8: Crafting grid (3x3)
    // 9: Output slot
    
    public RocketWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROCKET_WORKBENCH.get(), pos, state);
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.astroexpansion.rocket_workbench");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new RocketWorkbenchMenu(containerId, playerInventory, this);
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(tag);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
    }
    
    public void dropContents() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, RocketWorkbenchBlockEntity entity) {
        if (level.isClientSide) {
            return;
        }
        
        entity.updateCrafting();
    }
    
    private void updateCrafting() {
        // Check if we have a valid recipe
        SimpleContainer craftingInventory = new SimpleContainer(9);
        for (int i = 0; i < 9; i++) {
            craftingInventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        
        RecipeManager recipeManager = level.getRecipeManager();
        Optional<RocketWorkbenchRecipe> recipe = recipeManager.getRecipeFor(ModRecipeTypes.ROCKET_WORKBENCH.get(), 
            craftingInventory, level);
        
        if (recipe.isPresent() && canInsertResult(recipe.get().getResultItem(level.registryAccess()))) {
            // Set the output
            itemHandler.setStackInSlot(9, recipe.get().getResultItem(level.registryAccess()).copy());
        } else {
            // Clear the output
            itemHandler.setStackInSlot(9, ItemStack.EMPTY);
        }
    }
    
    private boolean canInsertResult(ItemStack result) {
        ItemStack outputSlot = itemHandler.getStackInSlot(9);
        if (outputSlot.isEmpty()) {
            return true;
        }
        if (!outputSlot.is(result.getItem())) {
            return false;
        }
        return outputSlot.getCount() + result.getCount() <= outputSlot.getMaxStackSize();
    }
    
    public void craftItem() {
        SimpleContainer craftingInventory = new SimpleContainer(9);
        for (int i = 0; i < 9; i++) {
            craftingInventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        
        RecipeManager recipeManager = level.getRecipeManager();
        Optional<RocketWorkbenchRecipe> recipe = recipeManager.getRecipeFor(ModRecipeTypes.ROCKET_WORKBENCH.get(), 
            craftingInventory, level);
        
        if (recipe.isPresent()) {
            ItemStack result = recipe.get().getResultItem(level.registryAccess());
            
            // Consume ingredients
            for (int i = 0; i < 9; i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    stack.shrink(1);
                }
            }
            
            // Take the result
            itemHandler.setStackInSlot(9, ItemStack.EMPTY);
        }
    }
    
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
}