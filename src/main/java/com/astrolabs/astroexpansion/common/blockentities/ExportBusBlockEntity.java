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

public class ExportBusBlockEntity extends BlockEntity {
    private final ItemStackHandler filterSlots = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private IStorageNetwork connectedNetwork = null;
    private int tickCounter = 0;
    private static final int TICK_INTERVAL = 20; // Export every second
    
    public ExportBusBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXPORT_BUS.get(), pos, state);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, ExportBusBlockEntity entity) {
        entity.tickCounter++;
        if (entity.tickCounter >= TICK_INTERVAL) {
            entity.tickCounter = 0;
            entity.performExport();
        }
    }
    
    private void performExport() {
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
                    // Export items to adjacent inventory
                    for (int i = 0; i < filterSlots.getSlots(); i++) {
                        ItemStack filter = filterSlots.getStackInSlot(i);
                        if (!filter.isEmpty()) {
                            // Try to extract from network
                            ItemStack toExtract = filter.copy();
                            toExtract.setCount(Math.min(64, filter.getMaxStackSize()));
                            
                            ItemStack extracted = connectedNetwork.extractItem(toExtract, false);
                            if (!extracted.isEmpty()) {
                                // Try to insert into target inventory
                                for (int slot = 0; slot < handler.getSlots(); slot++) {
                                    extracted = handler.insertItem(slot, extracted, false);
                                    if (extracted.isEmpty()) {
                                        break;
                                    }
                                }
                                
                                // Return any items that couldn't be inserted
                                if (!extracted.isEmpty()) {
                                    connectedNetwork.insertItem(extracted, false);
                                }
                            }
                        }
                    }
                });
            }
        }
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