package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.api.storage.IStorageNetwork;
import com.astrolabs.astroexpansion.common.menu.StorageTerminalMenu;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class StorageTerminalBlockEntity extends BlockEntity implements MenuProvider {
    private StorageCoreBlockEntity connectedCore = null;
    private BlockPos corePos = null;
    
    public StorageTerminalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STORAGE_TERMINAL.get(), pos, state);
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.astroexpansion.storage_terminal");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new StorageTerminalMenu(id, inventory, this);
    }
    
    public void findNetwork() {
        // Search for storage core within 16 blocks
        int searchRadius = 16;
        
        for (BlockPos pos : BlockPos.betweenClosed(
            worldPosition.offset(-searchRadius, -searchRadius, -searchRadius),
            worldPosition.offset(searchRadius, searchRadius, searchRadius))) {
            
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof StorageCoreBlockEntity core && core.isNetworkFormed()) {
                connectedCore = core;
                corePos = pos.immutable();
                return;
            }
        }
        
        connectedCore = null;
        corePos = null;
    }
    
    public boolean isConnectedToNetwork() {
        if (connectedCore == null || corePos == null) {
            findNetwork();
        }
        
        // Verify the core still exists and is formed
        if (corePos != null && level != null) {
            BlockEntity be = level.getBlockEntity(corePos);
            if (be instanceof StorageCoreBlockEntity core && core.isNetworkFormed()) {
                connectedCore = core;
                return true;
            }
        }
        
        connectedCore = null;
        corePos = null;
        return false;
    }
    
    @Nullable
    public IStorageNetwork getNetwork() {
        if (isConnectedToNetwork()) {
            return connectedCore;
        }
        return null;
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, StorageTerminalBlockEntity entity) {
        if (level.isClientSide) {
            return;
        }
        
        // Periodically check network connection
        if (level.getGameTime() % 100 == 0) {
            entity.findNetwork();
        }
    }
}