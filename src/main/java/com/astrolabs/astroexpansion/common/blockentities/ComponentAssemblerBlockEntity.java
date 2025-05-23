package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.menu.ComponentAssemblerMenu;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ComponentAssemblerBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler inputSlots = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    
    private final ItemStackHandler outputSlot = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    
    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(20000, 100, 100);
    
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    
    private int progress = 0;
    private int maxProgress = 100;
    private static final int ENERGY_PER_TICK = 50;
    
    public ComponentAssemblerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COMPONENT_ASSEMBLER.get(), pos, state);
    }
    
    @Override
    public Component getDisplayName() {
        return Component.literal("Component Assembler");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ComponentAssemblerMenu(id, inv, this);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, ComponentAssemblerBlockEntity entity) {
        if (entity.canCraft()) {
            if (entity.energyStorage.extractEnergy(ENERGY_PER_TICK, true) == ENERGY_PER_TICK) {
                entity.energyStorage.extractEnergy(ENERGY_PER_TICK, false);
                entity.progress++;
                
                if (entity.progress >= entity.maxProgress) {
                    entity.craft();
                    entity.progress = 0;
                }
                
                entity.setChanged();
            }
        } else {
            entity.progress = 0;
        }
    }
    
    private boolean canCraft() {
        // Check for specific recipes
        if (hasRecipe("circuit_board")) {
            return outputSlot.getStackInSlot(0).isEmpty() || 
                   (outputSlot.getStackInSlot(0).getItem() == com.astrolabs.astroexpansion.common.registry.ModItems.CIRCUIT_BOARD.get() &&
                    outputSlot.getStackInSlot(0).getCount() < outputSlot.getStackInSlot(0).getMaxStackSize());
        } else if (hasRecipe("processor")) {
            return outputSlot.getStackInSlot(0).isEmpty() || 
                   (outputSlot.getStackInSlot(0).getItem() == com.astrolabs.astroexpansion.common.registry.ModItems.PROCESSOR.get() &&
                    outputSlot.getStackInSlot(0).getCount() < outputSlot.getStackInSlot(0).getMaxStackSize());
        } else if (hasRecipe("energy_core")) {
            return outputSlot.getStackInSlot(0).isEmpty() || 
                   (outputSlot.getStackInSlot(0).getItem() == com.astrolabs.astroexpansion.common.registry.ModItems.ENERGY_CORE.get() &&
                    outputSlot.getStackInSlot(0).getCount() < outputSlot.getStackInSlot(0).getMaxStackSize());
        } else if (hasRecipe("storage_processor")) {
            return outputSlot.getStackInSlot(0).isEmpty() || 
                   (outputSlot.getStackInSlot(0).getItem() == com.astrolabs.astroexpansion.common.registry.ModItems.STORAGE_PROCESSOR.get() &&
                    outputSlot.getStackInSlot(0).getCount() < outputSlot.getStackInSlot(0).getMaxStackSize());
        } else if (hasRecipe("drone_core")) {
            return outputSlot.getStackInSlot(0).isEmpty() || 
                   (outputSlot.getStackInSlot(0).getItem() == com.astrolabs.astroexpansion.common.registry.ModItems.DRONE_CORE.get() &&
                    outputSlot.getStackInSlot(0).getCount() < outputSlot.getStackInSlot(0).getMaxStackSize());
        }
        return false;
    }
    
    private boolean hasRecipe(String recipeType) {
        switch (recipeType) {
            case "circuit_board":
                // Requires: 2 copper, 1 gold, 1 redstone
                return inputSlots.getStackInSlot(0).getItem() == net.minecraft.world.item.Items.COPPER_INGOT &&
                       inputSlots.getStackInSlot(0).getCount() >= 2 &&
                       inputSlots.getStackInSlot(1).getItem() == net.minecraft.world.item.Items.GOLD_INGOT &&
                       inputSlots.getStackInSlot(2).getItem() == net.minecraft.world.item.Items.REDSTONE;
                       
            case "processor":
                // Requires: 1 circuit board, 1 diamond, 1 titanium ingot
                return inputSlots.getStackInSlot(0).getItem() == com.astrolabs.astroexpansion.common.registry.ModItems.CIRCUIT_BOARD.get() &&
                       inputSlots.getStackInSlot(1).getItem() == net.minecraft.world.item.Items.DIAMOND &&
                       inputSlots.getStackInSlot(2).getItem() == com.astrolabs.astroexpansion.common.registry.ModItems.TITANIUM_INGOT.get();
                       
            case "energy_core":
                // Requires: 1 lithium ingot, 2 redstone blocks, 1 gold block
                return inputSlots.getStackInSlot(0).getItem() == com.astrolabs.astroexpansion.common.registry.ModItems.LITHIUM_INGOT.get() &&
                       inputSlots.getStackInSlot(1).getItem() == net.minecraft.world.item.Items.REDSTONE_BLOCK &&
                       inputSlots.getStackInSlot(1).getCount() >= 2 &&
                       inputSlots.getStackInSlot(2).getItem() == net.minecraft.world.item.Items.GOLD_BLOCK;
                       
            case "storage_processor":
                // Requires: 1 processor, 1 emerald, 1 lithium ingot
                return inputSlots.getStackInSlot(0).getItem() == com.astrolabs.astroexpansion.common.registry.ModItems.PROCESSOR.get() &&
                       inputSlots.getStackInSlot(1).getItem() == net.minecraft.world.item.Items.EMERALD &&
                       inputSlots.getStackInSlot(2).getItem() == com.astrolabs.astroexpansion.common.registry.ModItems.LITHIUM_INGOT.get();
                       
            case "drone_core":
                // Requires: 1 energy core, 1 processor, 1 ender pearl
                return inputSlots.getStackInSlot(0).getItem() == com.astrolabs.astroexpansion.common.registry.ModItems.ENERGY_CORE.get() &&
                       inputSlots.getStackInSlot(1).getItem() == com.astrolabs.astroexpansion.common.registry.ModItems.PROCESSOR.get() &&
                       inputSlots.getStackInSlot(2).getItem() == net.minecraft.world.item.Items.ENDER_PEARL;
        }
        return false;
    }
    
    private void craft() {
        ItemStack result = ItemStack.EMPTY;
        
        if (hasRecipe("circuit_board")) {
            inputSlots.extractItem(0, 2, false); // 2 copper
            inputSlots.extractItem(1, 1, false); // 1 gold
            inputSlots.extractItem(2, 1, false); // 1 redstone
            result = new ItemStack(com.astrolabs.astroexpansion.common.registry.ModItems.CIRCUIT_BOARD.get());
        } else if (hasRecipe("processor")) {
            inputSlots.extractItem(0, 1, false); // 1 circuit board
            inputSlots.extractItem(1, 1, false); // 1 diamond
            inputSlots.extractItem(2, 1, false); // 1 titanium
            result = new ItemStack(com.astrolabs.astroexpansion.common.registry.ModItems.PROCESSOR.get());
        } else if (hasRecipe("energy_core")) {
            inputSlots.extractItem(0, 1, false); // 1 lithium
            inputSlots.extractItem(1, 2, false); // 2 redstone blocks
            inputSlots.extractItem(2, 1, false); // 1 gold block
            result = new ItemStack(com.astrolabs.astroexpansion.common.registry.ModItems.ENERGY_CORE.get());
        } else if (hasRecipe("storage_processor")) {
            inputSlots.extractItem(0, 1, false); // 1 processor
            inputSlots.extractItem(1, 1, false); // 1 emerald
            inputSlots.extractItem(2, 1, false); // 1 lithium
            result = new ItemStack(com.astrolabs.astroexpansion.common.registry.ModItems.STORAGE_PROCESSOR.get());
        } else if (hasRecipe("drone_core")) {
            inputSlots.extractItem(0, 1, false); // 1 energy core
            inputSlots.extractItem(1, 1, false); // 1 processor
            inputSlots.extractItem(2, 1, false); // 1 ender pearl
            result = new ItemStack(com.astrolabs.astroexpansion.common.registry.ModItems.DRONE_CORE.get());
        }
        
        if (!result.isEmpty()) {
            outputSlot.insertItem(0, result, false);
        }
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> new CombinedInvWrapper(inputSlots, outputSlot));
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory_input", inputSlots.serializeNBT());
        tag.put("inventory_output", outputSlot.serializeNBT());
        tag.putInt("energy", energyStorage.getEnergyStored());
        tag.putInt("progress", progress);
        super.saveAdditional(tag);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        inputSlots.deserializeNBT(tag.getCompound("inventory_input"));
        outputSlot.deserializeNBT(tag.getCompound("inventory_output"));
        energyStorage.setEnergy(tag.getInt("energy"));
        progress = tag.getInt("progress");
    }
    
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(inputSlots.getSlots() + outputSlot.getSlots());
        for (int i = 0; i < inputSlots.getSlots(); i++) {
            inventory.setItem(i, inputSlots.getStackInSlot(i));
        }
        inventory.setItem(inputSlots.getSlots(), outputSlot.getStackInSlot(0));
        
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        return super.getCapability(cap, side);
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
    
    public LazyOptional<IEnergyStorage> getEnergyHandler() {
        return lazyEnergyHandler;
    }
}