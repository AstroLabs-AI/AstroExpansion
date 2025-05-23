package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.menu.EnergyStorageMenu;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnergyStorageBlockEntity extends BlockEntity implements MenuProvider {
    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(100000, 1000, 1000);
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    
    protected final ContainerData data;
    
    public EnergyStorageBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_STORAGE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> EnergyStorageBlockEntity.this.energyStorage.getEnergyStored();
                    case 1 -> EnergyStorageBlockEntity.this.energyStorage.getMaxEnergyStored();
                    default -> 0;
                };
            }
            
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> EnergyStorageBlockEntity.this.energyStorage.setEnergy(value);
                }
            }
            
            @Override
            public int getCount() {
                return 2;
            }
        };
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.astroexpansion.energy_storage");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new EnergyStorageMenu(id, inventory, this, this.data);
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        
        return super.getCapability(cap, side);
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyEnergyHandler.invalidate();
    }
    
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("energy", energyStorage.writeToNBT(new CompoundTag()));
        super.saveAdditional(nbt);
    }
    
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        energyStorage.readFromNBT(nbt.getCompound("energy"));
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, EnergyStorageBlockEntity entity) {
        if (level.isClientSide) {
            return;
        }
        
        // Send energy to adjacent blocks if needed
        if (entity.energyStorage.getEnergyStored() > 0) {
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.relative(direction);
                BlockEntity be = level.getBlockEntity(neighborPos);
                
                if (be != null) {
                    be.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(handler -> {
                        if (handler.canReceive()) {
                            int extracted = entity.energyStorage.extractEnergy(1000, true);
                            if (extracted > 0) {
                                int received = handler.receiveEnergy(extracted, false);
                                entity.energyStorage.extractEnergy(received, false);
                                entity.setChanged();
                            }
                        }
                    });
                }
            }
        }
    }
}