package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.api.energy.IEnergyHandler;
import com.astrolabs.astroexpansion.common.capabilities.AECapabilityProviders;
import com.astrolabs.astroexpansion.common.menu.ForceFieldGeneratorMenu;
import com.astrolabs.astroexpansion.common.registry.AEBlockEntities;
import com.astrolabs.astroexpansion.common.registry.AEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.energy.EnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class ForceFieldGeneratorBlockEntity extends AbstractMachineBlockEntity implements MenuProvider {
    private static final int ENERGY_CAPACITY = 500000;
    private static final int ENERGY_PER_BLOCK = 10;
    private static final int ENERGY_PER_TICK_BASE = 100;
    private static final int MAX_RADIUS = 16;
    
    private final EnergyStorage energyStorage = new EnergyStorage(ENERGY_CAPACITY, 5000, 0);
    private final Set<BlockPos> forceFieldBlocks = new HashSet<>();
    
    private int radius = 5;
    private boolean isActive = false;
    private int shape = 0; // 0 = sphere, 1 = cube, 2 = dome
    
    public ForceFieldGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(AEBlockEntities.FORCE_FIELD_GENERATOR.get(), pos, blockState, ENERGY_CAPACITY);
    }
    
    @Override
    protected void applyImplicitComponents(DataComponentMap.Builder builder) {
        super.applyImplicitComponents(builder);
    }
    
    @Override
    protected void collectImplicitComponents(DataComponentMap components) {
        super.collectImplicitComponents(components);
    }
    
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        tag.readCompound("Energy").ifPresent(energyTag -> energyStorage.deserializeNBT(registries, energyTag));
        radius = tag.getIntOr("Radius", 5);
        isActive = tag.getBooleanOr("Active", false);
        shape = tag.getIntOr("Shape", 0);
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Energy", energyStorage.serializeNBT(registries));
        tag.putInt("Radius", radius);
        tag.putBoolean("Active", isActive);
        tag.putInt("Shape", shape);
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, ForceFieldGeneratorBlockEntity blockEntity) {
        if (!level.isClientSide) {
            blockEntity.extractEnergy();
            
            if (blockEntity.isActive) {
                int energyCost = ENERGY_PER_TICK_BASE + (blockEntity.forceFieldBlocks.size() * ENERGY_PER_BLOCK);
                if (blockEntity.energyStorage.getEnergyStored() >= energyCost) {
                    blockEntity.energyStorage.extractEnergy(energyCost, false);
                    blockEntity.updateForceField();
                } else {
                    blockEntity.setActive(false);
                }
            }
        }
    }
    
    private void extractEnergy() {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(direction);
            if (level.getBlockEntity(neighborPos) instanceof IEnergyHandler energyHandler) {
                int extracted = energyHandler.extractEnergy(5000, false);
                energyStorage.receiveEnergy(extracted, false);
                if (extracted > 0) {
                    setChanged();
                }
            }
        }
    }
    
    private void updateForceField() {
        Set<BlockPos> newPositions = calculateForceFieldPositions();
        
        Set<BlockPos> toRemove = new HashSet<>(forceFieldBlocks);
        toRemove.removeAll(newPositions);
        for (BlockPos pos : toRemove) {
            if (level.getBlockState(pos).is(AEBlocks.FORCE_FIELD.get())) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
        
        Set<BlockPos> toAdd = new HashSet<>(newPositions);
        toAdd.removeAll(forceFieldBlocks);
        for (BlockPos pos : toAdd) {
            if (level.getBlockState(pos).canBeReplaced()) {
                level.setBlock(pos, AEBlocks.FORCE_FIELD.get().defaultBlockState(), 3);
            }
        }
        
        forceFieldBlocks.clear();
        forceFieldBlocks.addAll(newPositions);
    }
    
    private Set<BlockPos> calculateForceFieldPositions() {
        Set<BlockPos> positions = new HashSet<>();
        
        switch (shape) {
            case 0 -> calculateSphere(positions);
            case 1 -> calculateCube(positions);
            case 2 -> calculateDome(positions);
        }
        
        return positions;
    }
    
    private void calculateSphere(Set<BlockPos> positions) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance >= radius - 0.5 && distance <= radius + 0.5) {
                        positions.add(worldPosition.offset(x, y, z));
                    }
                }
            }
        }
    }
    
    private void calculateCube(Set<BlockPos> positions) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) == radius || Math.abs(y) == radius || Math.abs(z) == radius) {
                        positions.add(worldPosition.offset(x, y, z));
                    }
                }
            }
        }
    }
    
    private void calculateDome(Set<BlockPos> positions) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = 0; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance >= radius - 0.5 && distance <= radius + 0.5) {
                        positions.add(worldPosition.offset(x, y, z));
                    }
                }
            }
        }
    }
    
    public void removeForceField() {
        for (BlockPos pos : forceFieldBlocks) {
            if (level.getBlockState(pos).is(AEBlocks.FORCE_FIELD.get())) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
        forceFieldBlocks.clear();
    }
    
    public void setActive(boolean active) {
        if (this.isActive != active) {
            this.isActive = active;
            if (!active) {
                removeForceField();
            }
            setChanged();
        }
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public int getRadius() {
        return radius;
    }
    
    public void setRadius(int radius) {
        this.radius = Math.min(radius, MAX_RADIUS);
        if (isActive) {
            updateForceField();
        }
        setChanged();
    }
    
    public int getShape() {
        return shape;
    }
    
    public void setShape(int shape) {
        this.shape = shape;
        if (isActive) {
            updateForceField();
        }
        setChanged();
    }
    
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }
    
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }
    
    @Override
    public Component getDisplayName() {
        return Component.literal("Force Field Generator");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new ForceFieldGeneratorMenu(id, playerInventory, this);
    }
    
    public void openMenu(ServerPlayer player) {
        player.openMenu(this, pos -> {
            pos.writeBlockPos(worldPosition);
        });
    }
    
    @Override
    public <T> T getCapability(BlockCapability<T, Direction> capability, Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction side) {
        if (capability == AECapabilityProviders.ENERGY_HANDLER) {
            return (T) energyStorage;
        }
        return null;
    }
    
    @Override
    public void preRemoveSideEffects() {
        // Remove force field blocks before the block entity is removed
        removeForceField();
        super.preRemoveSideEffects();
    }
}