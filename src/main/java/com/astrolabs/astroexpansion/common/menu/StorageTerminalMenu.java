package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.api.storage.IStorageNetwork;
import com.astrolabs.astroexpansion.common.blockentities.StorageTerminalBlockEntity;
import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import com.astrolabs.astroexpansion.common.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public class StorageTerminalMenu extends AbstractContainerMenu {
    private final StorageTerminalBlockEntity blockEntity;
    private final Level level;
    private final List<ItemStack> networkItems = new ArrayList<>();
    private String searchQuery = "";
    
    // Crafting grid slots (3x3)
    private final CraftingContainer craftingGrid = new TransientCraftingContainer(this, 3, 3);
    private final ResultContainer craftingResult = new ResultContainer();
    
    public StorageTerminalMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }
    
    public StorageTerminalMenu(int id, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.STORAGE_TERMINAL_MENU.get(), id);
        this.blockEntity = (StorageTerminalBlockEntity) entity;
        this.level = inv.player.level();
        
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        
        // Add crafting grid slots (3x3)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(craftingGrid, col + row * 3, 30 + col * 18, 17 + row * 18));
            }
        }
        
        // Add crafting result slot
        this.addSlot(new ResultSlot(inv.player, craftingGrid, craftingResult, 0, 124, 35));
        
        updateNetworkItems();
    }
    
    public void updateNetworkItems() {
        networkItems.clear();
        IStorageNetwork network = getNetwork();
        if (network != null) {
            networkItems.addAll(network.getStoredItems());
        }
    }
    
    public List<ItemStack> getFilteredItems() {
        if (searchQuery.isEmpty()) {
            return networkItems;
        }
        
        List<ItemStack> filtered = new ArrayList<>();
        String query = searchQuery.toLowerCase();
        
        for (ItemStack stack : networkItems) {
            String name = stack.getHoverName().getString().toLowerCase();
            if (name.contains(query)) {
                filtered.add(stack);
            }
        }
        
        return filtered;
    }
    
    public void setSearchQuery(String query) {
        this.searchQuery = query;
    }
    
    public String getSearchQuery() {
        return searchQuery;
    }
    
    public IStorageNetwork getNetwork() {
        if (blockEntity != null) {
            return blockEntity.getNetwork();
        }
        return null;
    }
    
    public ItemStack insertItemToNetwork(ItemStack stack, boolean simulate) {
        IStorageNetwork network = getNetwork();
        if (network != null) {
            int inserted = network.insertItem(stack, simulate);
            if (inserted > 0) {
                ItemStack remaining = stack.copy();
                remaining.shrink(inserted);
                return remaining;
            }
        } else {
            // Network is null - terminal not connected to core
            if (!level.isClientSide && !simulate) {
                com.astrolabs.astroexpansion.AstroExpansion.LOGGER.warn(
                    "Storage Terminal at {} has no network connection!", blockEntity.getBlockPos());
            }
        }
        return stack;
    }
    
    public ItemStack extractItemFromNetwork(ItemStack stack, boolean simulate) {
        IStorageNetwork network = getNetwork();
        if (network != null) {
            return network.extractItem(stack, simulate);
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public void slotsChanged(Container container) {
        // Update crafting result when grid changes
        if (!level.isClientSide && container == craftingGrid) {
            ItemStack result = level.getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, craftingGrid, level)
                .map(recipe -> recipe.assemble(craftingGrid, level.registryAccess()))
                .orElse(ItemStack.EMPTY);
            
            craftingResult.setItem(0, result);
            setRemoteSlot(45, result); // Sync to client
        }
        super.slotsChanged(container);
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();
            
            // Crafting result slot
            if (index == 45) {
                if (!this.moveItemStackTo(slotStack, 0, 36, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, itemstack);
            }
            // Player inventory slots
            else if (index < 36) {
                // Try to insert into network first
                ItemStack remaining = insertItemToNetwork(slotStack, false);
                if (remaining.isEmpty()) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.set(remaining);
                }
                return itemstack;
            }
            // Crafting grid slots
            else if (index >= 36 && index < 45) {
                if (!this.moveItemStackTo(slotStack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }
            
            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            
            if (slotStack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            
            slot.onTake(player, slotStack);
        }
        
        return itemstack;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
            player, ModBlocks.STORAGE_TERMINAL.get());
    }
    
    @Override
    public void removed(Player player) {
        super.removed(player);
        // Return crafting grid items to player
        if (!level.isClientSide) {
            this.clearContainer(player, this.craftingGrid);
        }
    }
    
    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 15 + l * 18, 99 + i * 18));
            }
        }
    }
    
    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 15 + i * 18, 157));
        }
    }
}