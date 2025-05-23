package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import com.astrolabs.astroexpansion.common.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class LifeSupportSystemBlockEntity extends BlockEntity {
    private static final int ENERGY_CAPACITY = 50000;
    private static final int ENERGY_PER_TICK = 20;
    private static final int OXYGEN_CAPACITY = 16000;
    private static final int OXYGEN_PER_TICK = 10;
    private static final int EFFECT_RADIUS = 16;
    private static final int EFFECT_DURATION = 100; // 5 seconds

    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(ENERGY_CAPACITY, 1000, 0);
    private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> energyStorage);

    private final FluidTank oxygenTank = new FluidTank(OXYGEN_CAPACITY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.LIQUID_OXYGEN.get();
        }

        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };
    private final LazyOptional<IFluidHandler> fluidOptional = LazyOptional.of(() -> oxygenTank);

    private int tickCounter = 0;

    public LifeSupportSystemBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LIFE_SUPPORT_SYSTEM.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, LifeSupportSystemBlockEntity entity) {
        if (!level.isClientSide) {
            entity.tickCounter++;

            // Apply life support effects every second (20 ticks)
            if (entity.tickCounter % 20 == 0) {
                entity.applyLifeSupportEffects();
            }

            // Try to pull oxygen from adjacent blocks
            if (entity.tickCounter % 10 == 0) {
                entity.pullOxygenFromNeighbors();
            }

            // Try to pull energy from adjacent blocks
            entity.pullEnergyFromNeighbors();
        }
    }

    private void applyLifeSupportEffects() {
        // Check if we have enough power and oxygen
        if (energyStorage.getEnergyStored() >= ENERGY_PER_TICK && 
            oxygenTank.getFluidAmount() >= OXYGEN_PER_TICK) {
            
            // Consume resources
            energyStorage.extractEnergy(ENERGY_PER_TICK, false);
            oxygenTank.drain(OXYGEN_PER_TICK, IFluidHandler.FluidAction.EXECUTE);

            // Apply effects to all players in range
            AABB effectArea = new AABB(worldPosition).inflate(EFFECT_RADIUS);
            List<Player> players = level.getEntitiesOfClass(Player.class, effectArea);

            for (Player player : players) {
                // Breathable atmosphere
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, EFFECT_DURATION, 0, false, false));
                
                // Temperature regulation
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, EFFECT_DURATION, 0, false, false));
                
                // Radiation protection
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, EFFECT_DURATION, 0, false, false));
                
                // Remove negative space effects
                player.removeEffect(MobEffects.WITHER);
                player.removeEffect(MobEffects.DIG_SLOWDOWN);
                player.removeEffect(MobEffects.WEAKNESS);
            }
        }
    }

    private void pullOxygenFromNeighbors() {
        for (Direction dir : Direction.values()) {
            BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(dir));
            if (neighbor != null) {
                neighbor.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite()).ifPresent(handler -> {
                    // Try to extract oxygen
                    FluidStack extracted = handler.drain(
                        new FluidStack(ModFluids.LIQUID_OXYGEN.get(), Math.min(100, OXYGEN_CAPACITY - oxygenTank.getFluidAmount())), 
                        IFluidHandler.FluidAction.SIMULATE
                    );
                    
                    if (!extracted.isEmpty()) {
                        extracted = handler.drain(extracted, IFluidHandler.FluidAction.EXECUTE);
                        oxygenTank.fill(extracted, IFluidHandler.FluidAction.EXECUTE);
                    }
                });
            }
        }
    }

    private void pullEnergyFromNeighbors() {
        int energyNeeded = energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
        if (energyNeeded > 0) {
            for (Direction dir : Direction.values()) {
                BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(dir));
                if (neighbor != null) {
                    final int energyToExtract = Math.min(energyNeeded, 1000);
                    neighbor.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).ifPresent(neighborEnergy -> {
                        int extracted = neighborEnergy.extractEnergy(energyToExtract, false);
                        if (extracted > 0) {
                            energyStorage.receiveEnergy(extracted, false);
                        }
                    });
                    
                    energyNeeded = energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
                    if (energyNeeded <= 0) break;
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Energy", energyStorage.getEnergyStored());
        tag.put("OxygenTank", oxygenTank.writeToNBT(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energyStorage.setEnergy(tag.getInt("Energy"));
        oxygenTank.readFromNBT(tag.getCompound("OxygenTank"));
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyOptional.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyOptional.invalidate();
        fluidOptional.invalidate();
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public FluidTank getOxygenTank() {
        return oxygenTank;
    }
}