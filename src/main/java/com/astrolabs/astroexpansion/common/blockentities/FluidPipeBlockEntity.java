package com.astrolabs.astroexpansion.common.blockentities;

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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class FluidPipeBlockEntity extends BlockEntity {
    private static final int TRANSFER_RATE = 100; // mB per tick
    
    private final Set<Direction> checkedDirections = new HashSet<>();
    
    private final IFluidHandler fluidHandler = new IFluidHandler() {
        @Override
        public int getTanks() {
            return 1;
        }
        
        @Override
        public FluidStack getFluidInTank(int tank) {
            return FluidStack.EMPTY;
        }
        
        @Override
        public int getTankCapacity(int tank) {
            return TRANSFER_RATE;
        }
        
        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return true;
        }
        
        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (resource.isEmpty() || level == null) return 0;
            
            if (action.execute()) {
                distributeFluid(resource);
            }
            
            return Math.min(resource.getAmount(), TRANSFER_RATE);
        }
        
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            return FluidStack.EMPTY;
        }
        
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return FluidStack.EMPTY;
        }
    };
    
    private final LazyOptional<IFluidHandler> fluidHandlerLazyOptional = LazyOptional.of(() -> fluidHandler);
    
    public FluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUID_PIPE.get(), pos, state);
    }
    
    private void distributeFluid(FluidStack fluid) {
        if (level == null || level.isClientSide) return;
        
        checkedDirections.clear();
        int remainingFluid = fluid.getAmount();
        
        // First pass: count valid outputs
        int validOutputs = 0;
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(direction);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);
            
            if (neighborBE != null && !(neighborBE instanceof FluidPipeBlockEntity)) {
                LazyOptional<IFluidHandler> cap = neighborBE.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite());
                if (cap.isPresent()) {
                    IFluidHandler handler = cap.orElse(null);
                    if (handler != null && handler.fill(fluid, IFluidHandler.FluidAction.SIMULATE) > 0) {
                        validOutputs++;
                    }
                }
            }
        }
        
        if (validOutputs == 0) return;
        
        // Second pass: distribute fluid evenly
        int fluidPerOutput = remainingFluid / validOutputs;
        int[] remaining = {remainingFluid}; // Use array to make it effectively final
        
        for (Direction direction : Direction.values()) {
            if (remaining[0] <= 0) break;
            
            BlockPos neighborPos = worldPosition.relative(direction);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);
            
            if (neighborBE != null && !(neighborBE instanceof FluidPipeBlockEntity)) {
                LazyOptional<IFluidHandler> cap = neighborBE.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite());
                cap.ifPresent(handler -> {
                    FluidStack toFill = fluid.copy();
                    toFill.setAmount(Math.min(fluidPerOutput, remaining[0]));
                    int filled = handler.fill(toFill, IFluidHandler.FluidAction.EXECUTE);
                    remaining[0] -= filled;
                });
            }
        }
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidHandlerLazyOptional.invalidate();
    }
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandlerLazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }
}