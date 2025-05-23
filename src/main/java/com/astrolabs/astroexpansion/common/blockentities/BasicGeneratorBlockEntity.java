package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.blocks.BasicGeneratorBlock;
import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.menu.BasicGeneratorMenu;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BasicGeneratorBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
        
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
        }
    };
    
    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(10000, 0, 100);
    
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    
    protected final ContainerData data;
    private int burnTime = 0;
    private int maxBurnTime = 0;
    
    private static final int ENERGY_PER_TICK = 40;
    
    public BasicGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASIC_GENERATOR.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> BasicGeneratorBlockEntity.this.burnTime;
                    case 1 -> BasicGeneratorBlockEntity.this.maxBurnTime;
                    case 2 -> BasicGeneratorBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> BasicGeneratorBlockEntity.this.energyStorage.getMaxEnergyStored();
                    default -> 0;
                };
            }
            
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> BasicGeneratorBlockEntity.this.burnTime = value;
                    case 1 -> BasicGeneratorBlockEntity.this.maxBurnTime = value;
                    case 2 -> BasicGeneratorBlockEntity.this.energyStorage.setEnergy(value);
                }
            }
            
            @Override
            public int getCount() {
                return 4;
            }
        };
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.astroexpansion.basic_generator");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new BasicGeneratorMenu(id, inventory, this, this.data);
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        
        return super.getCapability(cap, side);
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
    }
    
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.put("energy", energyStorage.writeToNBT(new CompoundTag()));
        nbt.putInt("burn_time", burnTime);
        nbt.putInt("max_burn_time", maxBurnTime);
        super.saveAdditional(nbt);
    }
    
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        energyStorage.readFromNBT(nbt.getCompound("energy"));
        burnTime = nbt.getInt("burn_time");
        maxBurnTime = nbt.getInt("max_burn_time");
    }
    
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, BasicGeneratorBlockEntity entity) {
        if (level.isClientSide) {
            return;
        }
        
        boolean wasGenerating = entity.burnTime > 0;
        boolean changed = false;
        
        if (entity.burnTime > 0) {
            entity.burnTime--;
            entity.energyStorage.addEnergy(ENERGY_PER_TICK);
            changed = true;
        }
        
        if (entity.burnTime == 0 && entity.energyStorage.getEnergyStored() < entity.energyStorage.getMaxEnergyStored()) {
            ItemStack fuel = entity.itemHandler.getStackInSlot(0);
            int fuelValue = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);
            
            if (fuelValue > 0) {
                entity.burnTime = entity.maxBurnTime = fuelValue;
                
                if (!fuel.isEmpty()) {
                    fuel.shrink(1);
                    changed = true;
                }
            }
        }
        
        if (wasGenerating != (entity.burnTime > 0)) {
            state = state.setValue(BasicGeneratorBlock.LIT, entity.burnTime > 0);
            level.setBlock(pos, state, 3);
            changed = true;
        }
        
        if (changed) {
            setChanged(level, pos, state);
        }
        
        // Send energy to adjacent blocks
        entity.sendOutPower();
    }
    
    private void sendOutPower() {
        if (energyStorage.getEnergyStored() <= 0) {
            return;
        }
        
        for (Direction direction : Direction.values()) {
            BlockEntity be = level.getBlockEntity(worldPosition.relative(direction));
            if (be != null) {
                be.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(handler -> {
                    if (handler.canReceive()) {
                        int received = handler.receiveEnergy(Math.min(energyStorage.getEnergyStored(), 100), false);
                        energyStorage.extractEnergy(received, false);
                        setChanged();
                    }
                });
            }
        }
    }
    
    public int getBurnTime() {
        return burnTime;
    }
    
    public int getMaxBurnTime() {
        return maxBurnTime;
    }
}