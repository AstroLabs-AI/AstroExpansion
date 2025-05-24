package com.astrolabs.astroexpansion.common.block.entity;

import com.astrolabs.astroexpansion.common.blocks.machines.MatterDuplicatorBlock;
import com.astrolabs.astroexpansion.common.data.MatterRecipes;
import com.astrolabs.astroexpansion.common.energy.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.menu.MatterDuplicatorMenu;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import com.astrolabs.astroexpansion.common.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MatterDuplicatorBlockEntity extends BlockEntity implements MenuProvider {
    private static final int ENERGY_CAPACITY = 500000; // 500k FE
    private static final int ENERGY_PER_OPERATION = 10000; // 10k FE per operation
    private static final int TANK_CAPACITY = 10000; // 10 buckets
    
    private final ItemStackHandler itemHandler = new ItemStackHandler(3) { // 1 input, 1 output, 1 target slot
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 0) {
                // Input slot - accepts any item with a UU-Matter recipe
                return MatterRecipes.getUUMatterCost(stack.getItem()) > 0;
            } else if (slot == 2) {
                // Target slot - accepts any item to use as template
                return true;
            }
            return false; // Output slot
        }
    };

    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(ENERGY_CAPACITY, 
            ENERGY_CAPACITY, 0) {
        @Override
        public void onEnergyChanged() {
            setChanged();
        }
    };

    private final FluidTank fluidTank = new FluidTank(TANK_CAPACITY) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.UU_MATTER.get();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;
    private int currentUUCost = 0;

    public MatterDuplicatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MATTER_DUPLICATOR.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> MatterDuplicatorBlockEntity.this.progress;
                    case 1 -> MatterDuplicatorBlockEntity.this.maxProgress;
                    case 2 -> MatterDuplicatorBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> MatterDuplicatorBlockEntity.this.energyStorage.getMaxEnergyStored();
                    case 4 -> MatterDuplicatorBlockEntity.this.fluidTank.getFluidAmount();
                    case 5 -> MatterDuplicatorBlockEntity.this.fluidTank.getCapacity();
                    case 6 -> MatterDuplicatorBlockEntity.this.currentUUCost;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> MatterDuplicatorBlockEntity.this.progress = value;
                    case 1 -> MatterDuplicatorBlockEntity.this.maxProgress = value;
                    case 6 -> MatterDuplicatorBlockEntity.this.currentUUCost = value;
                }
            }

            @Override
            public int getCount() {
                return 7;
            }
        };
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MatterDuplicatorBlockEntity entity) {
        if (!level.isClientSide) {
            boolean wasLit = state.getValue(MatterDuplicatorBlock.LIT);
            boolean isLit = false;

            ItemStack input = entity.itemHandler.getStackInSlot(0);
            ItemStack output = entity.itemHandler.getStackInSlot(1);
            ItemStack target = entity.itemHandler.getStackInSlot(2);

            // Determine what to duplicate
            ItemStack toDuplicate = !target.isEmpty() ? target : input;
            
            if (!toDuplicate.isEmpty()) {
                int uuCost = MatterRecipes.getUUMatterCost(toDuplicate.getItem());
                entity.currentUUCost = uuCost;

                if (uuCost > 0 && entity.canDuplicate(toDuplicate, output, uuCost)) {
                    if (entity.energyStorage.extractEnergy(ENERGY_PER_OPERATION, false) == ENERGY_PER_OPERATION) {
                        entity.progress++;
                        isLit = true;

                        if (entity.progress >= entity.maxProgress) {
                            entity.performDuplication(toDuplicate, uuCost);
                            entity.progress = 0;
                        }
                    }
                }
            }

            // Update block state
            if (wasLit != isLit) {
                level.setBlock(pos, state.setValue(MatterDuplicatorBlock.LIT, isLit), 3);
            }
        }
    }

    private boolean canDuplicate(ItemStack toDuplicate, ItemStack output, int uuCost) {
        // Check UU-Matter
        if (fluidTank.getFluidAmount() < uuCost) {
            return false;
        }

        // Check energy
        if (energyStorage.getEnergyStored() < ENERGY_PER_OPERATION) {
            return false;
        }

        // Check output
        if (output.isEmpty()) {
            return true;
        }

        return output.getItem() == toDuplicate.getItem() && 
               output.getCount() + 1 <= output.getMaxStackSize();
    }

    private void performDuplication(ItemStack toDuplicate, int uuCost) {
        // Consume UU-Matter
        fluidTank.drain(uuCost, IFluidHandler.FluidAction.EXECUTE);

        // Create output
        ItemStack output = itemHandler.getStackInSlot(1);
        if (output.isEmpty()) {
            itemHandler.setStackInSlot(1, new ItemStack(toDuplicate.getItem(), 1));
        } else {
            output.grow(1);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.astroexpansion.matter_duplicator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new MatterDuplicatorMenu(id, playerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.put("energy", energyStorage.serializeNBT());
        tag.put("fluid", fluidTank.writeToNBT(new CompoundTag()));
        tag.putInt("progress", progress);
        tag.putInt("currentUUCost", currentUUCost);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        energyStorage.deserializeNBT(tag.getCompound("energy"));
        fluidTank.readFromNBT(tag.getCompound("fluid"));
        progress = tag.getInt("progress");
        currentUUCost = tag.getInt("currentUUCost");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
        lazyFluidHandler = LazyOptional.of(() -> fluidTank);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
        lazyFluidHandler.invalidate();
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public AstroEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }
}