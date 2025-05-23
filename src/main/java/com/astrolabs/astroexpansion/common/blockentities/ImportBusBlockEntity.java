package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.api.storage.IStorageNetwork;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
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

public class ImportBusBlockEntity extends BlockEntity {
    private final ItemStackHandler filterSlots = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private IStorageNetwork connectedNetwork = null;
    private int tickCounter = 0;
    private static final int TICK_INTERVAL = 20; // Import every second
    
    public ImportBusBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.IMPORT_BUS.get(), pos, state);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, ImportBusBlockEntity entity) {
        entity.tickCounter++;
        if (entity.tickCounter >= TICK_INTERVAL) {
            entity.tickCounter = 0;
            entity.performImport();
        }
    }
    
    private void performImport() {
        if (connectedNetwork == null || level.isClientSide) {
            return;
        }
        
        // Find adjacent inventory
        for (Direction dir : Direction.values()) {
            BlockPos adjacentPos = worldPosition.relative(dir);
            BlockEntity adjacentBE = level.getBlockEntity(adjacentPos);
            
            if (adjacentBE != null && !(adjacentBE instanceof StorageCoreBlockEntity)) {
                LazyOptional<IItemHandler> capability = adjacentBE.getCapability(ForgeCapabilities.ITEM_HANDLER, dir.getOpposite());
                capability.ifPresent(handler -> {
                    // Import items from adjacent inventory
                    for (int slot = 0; slot < handler.getSlots(); slot++) {
                        ItemStack stack = handler.extractItem(slot, 64, true);
                        
                        if (!stack.isEmpty() && shouldImport(stack)) {
                            // Try to insert into network
                            int inserted = connectedNetwork.insertItem(stack, false);
                            if (inserted > 0) {
                                handler.extractItem(slot, inserted, false);
                            }
                        }
                    }
                });
            }
        }
    }
    
    private boolean shouldImport(ItemStack stack) {
        // If no filter is set, import everything
        boolean hasFilter = false;
        for (int i = 0; i < filterSlots.getSlots(); i++) {
            if (!filterSlots.getStackInSlot(i).isEmpty()) {
                hasFilter = true;
                break;
            }
        }
        
        if (!hasFilter) {
            return true;
        }
        
        // Check if item matches any filter
        for (int i = 0; i < filterSlots.getSlots(); i++) {
            ItemStack filter = filterSlots.getStackInSlot(i);
            if (!filter.isEmpty() && ItemStack.isSameItemSameTags(stack, filter)) {
                return true;
            }
        }
        
        return false;
    }
    
    public void setNetwork(IStorageNetwork network) {
        this.connectedNetwork = network;
    }
    
    public IStorageNetwork getNetwork() {
        return connectedNetwork;
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> filterSlots);
        
        // Try to find connected network
        for (Direction dir : Direction.values()) {
            BlockPos adjacentPos = worldPosition.relative(dir);
            BlockEntity adjacentBE = level.getBlockEntity(adjacentPos);
            
            if (adjacentBE instanceof StorageCoreBlockEntity core) {
                this.connectedNetwork = core;
                break;
            }
        }
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("filter", filterSlots.serializeNBT());
        super.saveAdditional(tag);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        filterSlots.deserializeNBT(tag.getCompound("filter"));
    }
    
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }
}