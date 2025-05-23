package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.menu.ResearchConsoleMenu;
import com.astrolabs.astroexpansion.common.research.Research;
import com.astrolabs.astroexpansion.common.research.ResearchManager;
import com.astrolabs.astroexpansion.common.registry.AEBlockEntities;
import com.astrolabs.astroexpansion.common.registry.AEItems;

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

public class ResearchConsoleBlockEntity extends AbstractMachineBlockEntity implements Container, MenuProvider {
    
    private static final int ENERGY_CAPACITY = 50000;
    private static final int RESEARCH_ENERGY_COST = 100; // Per research point
    
    private NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY); // Research input slot
    private String currentResearch = "";
    private int researchProgress = 0;
    
    public ResearchConsoleBlockEntity(BlockPos pos, BlockState state) {
        super(AEBlockEntities.RESEARCH_CONSOLE.get(), pos, state, ENERGY_CAPACITY);
    }
    
    @Override
    public void tick() {
        if (level == null || level.isClientSide) return;
        
        boolean wasActive = isActive;
        isActive = false;
        
        if (!currentResearch.isEmpty()) {
            Research research = ResearchManager.getResearch(currentResearch);
            if (research != null && hasResearchItems() && energyStorage.getEnergyStored() >= RESEARCH_ENERGY_COST) {
                isActive = true;
                
                if (processTime == 0) {
                    maxProcessTime = research.getResearchPoints();
                }
                
                energyStorage.extractEnergy(RESEARCH_ENERGY_COST, false);
                processTime++;
                researchProgress++;
                
                if (processTime >= maxProcessTime) {
                    completeResearch();
                    processTime = 0;
                    maxProcessTime = 0;
                    researchProgress = 0;
                    currentResearch = "";
                }
            }
        }
        
        if (wasActive != isActive) {
            markDirtyAndSync();
        }
    }
    
    private boolean hasResearchItems() {
        ItemStack stack = items.get(0);
        return !stack.isEmpty() && (stack.is(AEItems.PROCESSOR.get()) || stack.is(AEItems.ADVANCED_PROCESSOR.get()));
    }
    
    private void completeResearch() {
        // This would need to be called when a player interacts
        // For now, we'll store the research completion until a player accesses the console
    }
    
    public void startResearch(String researchId, Player player) {
        if (ResearchManager.canResearch(player, researchId)) {
            currentResearch = researchId;
            processTime = 0;
            researchProgress = 0;
            Research research = ResearchManager.getResearch(researchId);
            if (research != null) {
                maxProcessTime = research.getResearchPoints();
            }
        }
    }
    
    public void completeResearchForPlayer(Player player) {
        if (!currentResearch.isEmpty()) {
            ResearchManager.completeResearch(player, currentResearch);
            currentResearch = "";
            processTime = 0;
            maxProcessTime = 0;
            researchProgress = 0;
        }
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, ResearchConsoleBlockEntity blockEntity) {
        blockEntity.tick();
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putString("CurrentResearch", currentResearch);
        tag.putInt("ResearchProgress", researchProgress);
    }
    
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(1, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
        currentResearch = tag.getString("CurrentResearch");
        researchProgress = tag.getInt("ResearchProgress");
    }
    
    // Container implementation
    @Override
    public int getContainerSize() {
        return items.size();
    }
    
    @Override
    public boolean isEmpty() {
        return items.get(0).isEmpty();
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
        return Component.translatable("block.astroexpansion.research_console");
    }
    
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ResearchConsoleMenu(containerId, playerInventory, this, getContainerData(), player);
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
                    case 4 -> researchProgress;
                    default -> 0;
                };
            }
            
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> energyStorage.setEnergy(value);
                    case 2 -> processTime = value;
                    case 3 -> maxProcessTime = value;
                    case 4 -> researchProgress = value;
                }
            }
            
            @Override
            public int getCount() {
                return 5;
            }
        };
    }
}