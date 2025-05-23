package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.api.storage.IStorageNetwork;
import com.astrolabs.astroexpansion.common.blocks.StorageCoreBlock;
import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.items.StorageDriveItem;
import com.astrolabs.astroexpansion.common.menu.StorageCoreMenu;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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

import java.util.*;

public class StorageCoreBlockEntity extends BlockEntity implements MenuProvider, IStorageNetwork {
    private final ItemStackHandler driveHandler = new ItemStackHandler(6) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            rebuildNetwork();
        }
        
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.getItem() instanceof StorageDriveItem;
        }
        
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };
    
    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(50000, 1000, 1000);
    
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    
    private boolean networkFormed = false;
    private final List<BlockPos> connectedTerminals = new ArrayList<>();
    private final Map<ItemStack, Integer> storedItems = new HashMap<>();
    private long totalCapacity = 0;
    private long usedCapacity = 0;
    
    private static final int BASE_ENERGY_COST = 10;
    private static final int ENERGY_PER_DRIVE = 5;
    
    public StorageCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STORAGE_CORE.get(), pos, state);
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.astroexpansion.storage_core");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new StorageCoreMenu(id, inventory, this);
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
        lazyItemHandler = LazyOptional.of(() -> driveHandler);
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
        nbt.put("drives", driveHandler.serializeNBT());
        nbt.put("energy", energyStorage.writeToNBT(new CompoundTag()));
        nbt.putBoolean("formed", networkFormed);
        super.saveAdditional(nbt);
    }
    
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        driveHandler.deserializeNBT(nbt.getCompound("drives"));
        energyStorage.readFromNBT(nbt.getCompound("energy"));
        networkFormed = nbt.getBoolean("formed");
    }
    
    public void onRemove() {
        // Drop all drives
        SimpleContainer inventory = new SimpleContainer(driveHandler.getSlots());
        for (int i = 0; i < driveHandler.getSlots(); i++) {
            inventory.setItem(i, driveHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, StorageCoreBlockEntity entity) {
        if (level.isClientSide) {
            return;
        }
        
        // Check network formation every second
        if (level.getGameTime() % 20 == 0) {
            entity.checkNetworkFormation();
        }
        
        // Consume energy
        if (entity.networkFormed) {
            int energyCost = entity.getEnergyConsumption();
            if (entity.energyStorage.getEnergyStored() >= energyCost) {
                entity.energyStorage.extractEnergy(energyCost, false);
            } else {
                entity.networkFormed = false;
                entity.updateBlockState();
            }
        }
    }
    
    private void checkNetworkFormation() {
        boolean wasFormed = networkFormed;
        
        // Check if we have power and at least one drive
        boolean hasPower = energyStorage.getEnergyStored() > 0;
        boolean hasDriveInstalled = hasDrives();
        networkFormed = hasPower && hasDriveInstalled;
        
        // Debug logging
        if (!networkFormed && level.getGameTime() % 100 == 0) {
            AstroExpansion.LOGGER.info("Storage Core at {} - Power: {} FE, Has Drives: {}, Network Formed: {}", 
                worldPosition, energyStorage.getEnergyStored(), hasDriveInstalled, networkFormed);
        }
        
        if (wasFormed != networkFormed) {
            updateBlockState();
            if (networkFormed) {
                rebuildNetwork();
            }
        }
    }
    
    public boolean hasDrives() {
        for (int i = 0; i < driveHandler.getSlots(); i++) {
            if (!driveHandler.getStackInSlot(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    public AstroEnergyStorage getEnergyStorage() {
        return energyStorage;
    }
    
    public ItemStackHandler getDriveHandler() {
        return driveHandler;
    }
    
    private void updateBlockState() {
        BlockState state = getBlockState();
        if (state.getValue(StorageCoreBlock.FORMED) != networkFormed) {
            level.setBlock(worldPosition, state.setValue(StorageCoreBlock.FORMED, networkFormed)
                .setValue(StorageCoreBlock.POWERED, energyStorage.getEnergyStored() > 0), 3);
        }
    }
    
    private void rebuildNetwork() {
        storedItems.clear();
        totalCapacity = 0;
        usedCapacity = 0;
        
        // Calculate total capacity from drives
        for (int i = 0; i < driveHandler.getSlots(); i++) {
            ItemStack drive = driveHandler.getStackInSlot(i);
            if (!drive.isEmpty() && drive.getItem() instanceof StorageDriveItem driveItem) {
                totalCapacity += driveItem.getCapacity();
                
                // Load items from drive
                ListTag items = StorageDriveItem.getStoredItems(drive);
                for (int j = 0; j < items.size(); j++) {
                    CompoundTag itemTag = items.getCompound(j);
                    ItemStack stored = ItemStack.of(itemTag.getCompound("Item"));
                    int count = itemTag.getInt("Count");
                    
                    storedItems.merge(stored, count, Integer::sum);
                    usedCapacity += count;
                }
            }
        }
    }
    
    // IStorageNetwork implementation
    @Override
    public int insertItem(ItemStack stack, boolean simulate) {
        if (!networkFormed || stack.isEmpty()) {
            return 0;
        }
        
        long spaceAvailable = totalCapacity - usedCapacity;
        int toInsert = (int) Math.min(stack.getCount(), spaceAvailable);
        
        if (toInsert > 0 && !simulate) {
            ItemStack key = stack.copy();
            key.setCount(1);
            storedItems.merge(key, toInsert, Integer::sum);
            usedCapacity += toInsert;
            setChanged();
            saveToDrives();
        }
        
        return toInsert;
    }
    
    @Override
    public ItemStack extractItem(ItemStack stack, boolean simulate) {
        if (!networkFormed || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        
        ItemStack key = stack.copy();
        key.setCount(1);
        
        Integer stored = storedItems.get(key);
        if (stored == null || stored == 0) {
            return ItemStack.EMPTY;
        }
        
        int toExtract = Math.min(stack.getCount(), stored);
        
        if (!simulate) {
            if (toExtract >= stored) {
                storedItems.remove(key);
            } else {
                storedItems.put(key, stored - toExtract);
            }
            usedCapacity -= toExtract;
            setChanged();
            saveToDrives();
        }
        
        ItemStack result = key.copy();
        result.setCount(toExtract);
        return result;
    }
    
    @Override
    public List<ItemStack> getStoredItems() {
        List<ItemStack> items = new ArrayList<>();
        for (Map.Entry<ItemStack, Integer> entry : storedItems.entrySet()) {
            ItemStack stack = entry.getKey().copy();
            stack.setCount(entry.getValue());
            items.add(stack);
        }
        return items;
    }
    
    @Override
    public long getTotalItemCount() {
        return usedCapacity;
    }
    
    @Override
    public long getTotalCapacity() {
        return totalCapacity;
    }
    
    @Override
    public boolean hasSpace() {
        return usedCapacity < totalCapacity;
    }
    
    @Override
    public int getEnergyConsumption() {
        int drives = 0;
        for (int i = 0; i < driveHandler.getSlots(); i++) {
            if (!driveHandler.getStackInSlot(i).isEmpty()) {
                drives++;
            }
        }
        return BASE_ENERGY_COST + (drives * ENERGY_PER_DRIVE);
    }
    
    private void saveToDrives() {
        // This is a simplified version - in a real implementation,
        // you'd distribute items across drives based on their capacity
        // For now, we'll just save the state
        setChanged();
    }
    
    public boolean isNetworkFormed() {
        return networkFormed;
    }
}