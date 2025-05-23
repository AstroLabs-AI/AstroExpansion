package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.api.multiblock.IMultiblockPattern;
import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.menu.QuantumComputerMenu;
import com.astrolabs.astroexpansion.common.multiblock.MultiblockControllerBase;
import com.astrolabs.astroexpansion.common.multiblock.patterns.QuantumComputerPattern;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuantumComputerControllerBlockEntity extends MultiblockControllerBase implements MenuProvider {
    private static final int ENERGY_CAPACITY = 5000000; // 5M FE
    private static final int ENERGY_USAGE = 10000; // 10k FE/tick when processing
    private static final int SLOTS = 9; // Input/output slots for research
    
    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(ENERGY_CAPACITY, 50000, 0);
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);
    
    private final ItemStackHandler itemHandler = new ItemStackHandler(SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
        
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return true; // Will restrict to research items later
        }
    };
    
    private final LazyOptional<IItemHandler> itemHandlerOpt = LazyOptional.of(() -> itemHandler);
    
    private int processingTime = 0;
    private int maxProcessingTime = 200; // 10 seconds
    private int currentResearchPoints = 0;
    private boolean processing = false;
    
    private final IMultiblockPattern pattern = new QuantumComputerPattern();
    
    public QuantumComputerControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.QUANTUM_COMPUTER_CONTROLLER.get(), pos, state);
    }
    
    @Override
    public IMultiblockPattern getPattern() {
        return pattern;
    }
    
    @Override
    public void onFormed() {
        // Multiblock successfully formed
    }
    
    @Override
    public void onBroken() {
        processing = false;
        processingTime = 0;
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, QuantumComputerControllerBlockEntity blockEntity) {
        if (blockEntity.isFormed()) {
            blockEntity.updateComputer();
        }
    }
    
    private void updateComputer() {
        if (energyStorage.getEnergyStored() >= ENERGY_USAGE && hasValidInput()) {
            if (!processing) {
                processing = true;
                processingTime = 0;
            }
            
            // Consume energy and process
            energyStorage.extractEnergy(ENERGY_USAGE, false);
            processingTime++;
            
            if (processingTime >= maxProcessingTime) {
                completeProcessing();
                processingTime = 0;
                processing = false;
            }
            setChanged();
        } else {
            if (processing) {
                processing = false;
                processingTime = 0;
                setChanged();
            }
        }
    }
    
    private boolean hasValidInput() {
        // Check for research items in input slots
        for (int i = 0; i < 4; i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    private void completeProcessing() {
        // Process research items and generate research points
        for (int i = 0; i < 4; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                // TODO: Calculate research points based on item
                currentResearchPoints += 100; // Placeholder value
                itemHandler.extractItem(i, 1, false);
            }
        }
        
        // TODO: Store research points in player capability or research system
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerOpt.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyHandler.invalidate();
        itemHandlerOpt.invalidate();
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energyStorage.deserializeNBT(tag.get("energy"));
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        processingTime = tag.getInt("processingTime");
        currentResearchPoints = tag.getInt("researchPoints");
        processing = tag.getBoolean("processing");
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("energy", energyStorage.serializeNBT());
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("processingTime", processingTime);
        tag.putInt("researchPoints", currentResearchPoints);
        tag.putBoolean("processing", processing);
    }
    
    @Override
    public Component getDisplayName() {
        return Component.literal("Quantum Computer");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new QuantumComputerMenu(id, inventory, this);
    }
    
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }
    
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }
    
    public int getProcessingTime() {
        return processingTime;
    }
    
    public int getMaxProcessingTime() {
        return maxProcessingTime;
    }
    
    public boolean isProcessing() {
        return processing;
    }
    
    public int getResearchPoints() {
        return currentResearchPoints;
    }
    
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
}