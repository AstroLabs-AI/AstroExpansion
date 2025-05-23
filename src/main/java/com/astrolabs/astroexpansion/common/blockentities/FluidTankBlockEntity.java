package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.menu.FluidTankMenu;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidTankBlockEntity extends BlockEntity implements MenuProvider {
    public static final int CAPACITY = 16000; // 16 buckets
    
    private final FluidTank tank = new FluidTank(CAPACITY) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    
    private final LazyOptional<IFluidHandler> fluidHandlerLazyOptional = LazyOptional.of(() -> tank);
    
    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> tank.getFluidAmount();
                case 1 -> tank.getCapacity();
                case 2 -> tank.getFluid().getFluid().getFluidType().getTemperature();
                default -> 0;
            };
        }
        
        @Override
        public void set(int index, int value) {
        }
        
        @Override
        public int getCount() {
            return 3;
        }
    };
    
    public FluidTankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUID_TANK.get(), pos, state);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, FluidTankBlockEntity blockEntity) {
        // Auto-output to adjacent tanks
        if (blockEntity.tank.getFluidAmount() > 0) {
            for (Direction direction : Direction.values()) {
                BlockEntity adjacentBE = level.getBlockEntity(pos.relative(direction));
                if (adjacentBE != null) {
                    adjacentBE.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).ifPresent(handler -> {
                        FluidStack drain = blockEntity.tank.drain(1000, IFluidHandler.FluidAction.SIMULATE);
                        if (!drain.isEmpty()) {
                            int filled = handler.fill(drain, IFluidHandler.FluidAction.EXECUTE);
                            if (filled > 0) {
                                blockEntity.tank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                            }
                        }
                    });
                }
            }
        }
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        tank.readFromNBT(tag.getCompound("tank"));
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("tank", tank.writeToNBT(new CompoundTag()));
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.put("tank", tank.writeToNBT(new CompoundTag()));
        return tag;
    }
    
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        tank.readFromNBT(tag.getCompound("tank"));
    }
    
    @Override
    public Component getDisplayName() {
        return Component.literal("Fluid Tank");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
        return new FluidTankMenu(windowId, inventory, this, this.data);
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
    
    public FluidTank getTank() {
        return tank;
    }
}