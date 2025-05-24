package com.astrolabs.astroexpansion.common.block.entity;

import com.astrolabs.astroexpansion.common.blocks.machines.MatterFabricatorBlock;
import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.menu.MatterFabricatorMenu;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import com.astrolabs.astroexpansion.common.registry.ModFluids;
import com.astrolabs.astroexpansion.common.registry.ModItems;
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
import net.minecraft.world.item.Items;
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

public class MatterFabricatorBlockEntity extends BlockEntity implements MenuProvider {
    private static final int ENERGY_CAPACITY = 1000000; // 1M FE
    private static final int ENERGY_PER_TICK = 1000; // 1000 FE/t
    private static final int ENERGY_PER_MB = 1000000; // 1M FE per mB of UU-Matter
    private static final int TANK_CAPACITY = 10000; // 10 buckets
    private static final float SCRAP_EFFICIENCY = 0.75f; // 25% reduction in energy cost
    
    private final ItemStackHandler itemHandler = new ItemStackHandler(5) { // 1 scrap slot + 4 upgrade slots
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 0) {
                // Scrap slot - accepts scrap items
                return stack.is(Items.PAPER) || stack.is(Items.STICK) || 
                       stack.is(Items.COBBLESTONE) || stack.is(Items.DIRT);
            } else {
                // Upgrade slots
                return stack.getItem() == ModItems.PROCESSOR.get() || 
                       stack.getItem() == ModItems.ADVANCED_PROCESSOR.get() ||
                       stack.getItem() == ModItems.QUANTUM_PROCESSOR.get();
            }
        }
    };

    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(ENERGY_CAPACITY, 
            ENERGY_CAPACITY, 0);

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
    private float productionRate = 1.0f;

    public MatterFabricatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MATTER_FABRICATOR.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> MatterFabricatorBlockEntity.this.progress;
                    case 1 -> MatterFabricatorBlockEntity.this.maxProgress;
                    case 2 -> MatterFabricatorBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> MatterFabricatorBlockEntity.this.energyStorage.getMaxEnergyStored();
                    case 4 -> MatterFabricatorBlockEntity.this.fluidTank.getFluidAmount();
                    case 5 -> MatterFabricatorBlockEntity.this.fluidTank.getCapacity();
                    case 6 -> (int)(MatterFabricatorBlockEntity.this.productionRate * 100);
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> MatterFabricatorBlockEntity.this.progress = value;
                    case 1 -> MatterFabricatorBlockEntity.this.maxProgress = value;
                    case 6 -> MatterFabricatorBlockEntity.this.productionRate = value / 100f;
                }
            }

            @Override
            public int getCount() {
                return 7;
            }
        };
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MatterFabricatorBlockEntity entity) {
        if (!level.isClientSide) {
            boolean wasLit = state.getValue(MatterFabricatorBlock.LIT);
            boolean isLit = false;

            // Calculate production rate based on upgrades
            entity.calculateProductionRate();

            // Check if we can produce UU-Matter
            if (entity.hasEnoughEnergy() && entity.hasSpaceForFluid()) {
                // Check for scrap amplification
                boolean hasScrap = entity.consumeScrap();
                int energyCost = hasScrap ? 
                    (int)(ENERGY_PER_TICK * SCRAP_EFFICIENCY) : 
                    ENERGY_PER_TICK;

                // Apply production rate
                energyCost = (int)(energyCost / entity.productionRate);

                // Consume energy
                if (entity.energyStorage.extractEnergy(energyCost, false) == energyCost) {
                    entity.progress++;
                    isLit = true;

                    // Calculate progress needed based on production rate
                    int progressNeeded = (int)(ENERGY_PER_MB / ENERGY_PER_TICK / entity.productionRate);
                    if (hasScrap) {
                        progressNeeded = (int)(progressNeeded * SCRAP_EFFICIENCY);
                    }

                    if (entity.progress >= progressNeeded) {
                        // Produce 1 mB of UU-Matter
                        entity.fluidTank.fill(new FluidStack(ModFluids.UU_MATTER.get(), 1), 
                                IFluidHandler.FluidAction.EXECUTE);
                        entity.progress = 0;
                    }
                }
            }

            // Update block state
            if (wasLit != isLit) {
                level.setBlock(pos, state.setValue(MatterFabricatorBlock.LIT, isLit), 3);
            }
        }
    }

    private void calculateProductionRate() {
        productionRate = 1.0f;
        for (int i = 1; i < 5; i++) { // Check upgrade slots
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == ModItems.PROCESSOR.get()) {
                    productionRate += 0.25f;
                } else if (stack.getItem() == ModItems.ADVANCED_PROCESSOR.get()) {
                    productionRate += 0.5f;
                } else if (stack.getItem() == ModItems.QUANTUM_PROCESSOR.get()) {
                    productionRate += 1.0f;
                }
            }
        }
    }

    private boolean hasEnoughEnergy() {
        return energyStorage.getEnergyStored() >= ENERGY_PER_TICK;
    }

    private boolean hasSpaceForFluid() {
        return fluidTank.getSpace() > 0;
    }

    private boolean consumeScrap() {
        ItemStack scrap = itemHandler.getStackInSlot(0);
        if (!scrap.isEmpty()) {
            scrap.shrink(1);
            return true;
        }
        return false;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.astroexpansion.matter_fabricator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new MatterFabricatorMenu(id, playerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.put("energy", energyStorage.serializeNBT());
        tag.put("fluid", fluidTank.writeToNBT(new CompoundTag()));
        tag.putInt("progress", progress);
        tag.putFloat("productionRate", productionRate);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        energyStorage.deserializeNBT(tag.getCompound("energy"));
        fluidTank.readFromNBT(tag.getCompound("fluid"));
        progress = tag.getInt("progress");
        productionRate = tag.getFloat("productionRate");
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