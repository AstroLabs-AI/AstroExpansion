package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SolarPanelBlockEntity extends BlockEntity {
    private static final int ENERGY_CAPACITY = 50000; // 50k FE
    private static final int ENERGY_GEN_DAY = 100; // 100 FE/tick in sunlight
    private static final int ENERGY_GEN_SPACE = 200; // 200 FE/tick in space (no atmosphere)
    
    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(ENERGY_CAPACITY, 0, 1000);
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);
    
    public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLAR_PANEL.get(), pos, state);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, SolarPanelBlockEntity blockEntity) {
        if (level.isDay() && level.canSeeSky(pos.above())) {
            int energyGen = level.dimension().location().getPath().contains("space") ? ENERGY_GEN_SPACE : ENERGY_GEN_DAY;
            blockEntity.energyStorage.receiveEnergy(energyGen, false);
            blockEntity.distributeEnergy();
            blockEntity.setChanged();
        }
    }
    
    private void distributeEnergy() {
        for (Direction direction : Direction.values()) {
            BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(direction));
            if (neighbor != null) {
                neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(handler -> {
                    if (handler.canReceive()) {
                        int extracted = energyStorage.extractEnergy(1000, true);
                        if (extracted > 0) {
                            int received = handler.receiveEnergy(extracted, false);
                            energyStorage.extractEnergy(received, false);
                        }
                    }
                });
            }
        }
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyHandler.invalidate();
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energyStorage.deserializeNBT(tag.get("energy"));
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("energy", energyStorage.serializeNBT());
    }
}