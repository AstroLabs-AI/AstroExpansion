package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.blocks.TieredPowerGeneratorBlock;
import com.astrolabs.astroexpansion.common.blocks.machines.base.MachineTier;
import com.astrolabs.astroexpansion.common.blocks.machines.base.UpgradeableMachineBlockEntity;
import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.menu.TieredPowerGeneratorMenu;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import com.astrolabs.astroexpansion.common.capabilities.CombinedItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TieredPowerGeneratorBlockEntity extends UpgradeableMachineBlockEntity implements MenuProvider {
    private final MachineTier tier;
    private final ItemStackHandler itemHandler;
    private final AstroEnergyStorage energyStorage;
    
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyCombinedHandler = LazyOptional.empty();
    
    protected final ContainerData data;
    private int burnTime = 0;
    private int maxBurnTime = 0;
    
    // Power generation rates based on tier
    private final int baseEnergyPerTick;
    
    public TieredPowerGeneratorBlockEntity(BlockPos pos, BlockState state, MachineTier tier) {
        super(getBlockEntityType(tier), pos, state, tier.getUpgradeSlots());
        this.tier = tier;
        
        // Set base energy generation based on tier
        this.baseEnergyPerTick = switch (tier) {
            case BASIC -> 40;      // 40 RF/t
            case ADVANCED -> 120;  // 120 RF/t
            case ELITE -> 320;     // 320 RF/t
        };
        
        // Initialize item handler with 1 fuel slot
        this.itemHandler = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
            
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
            }
        };
        
        // Initialize energy storage based on tier
        this.energyStorage = new AstroEnergyStorage(
            tier.getEnergyCapacity(),
            0, // No input
            tier.getEnergyCapacity() / 20 // Output rate
        );
        
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> TieredPowerGeneratorBlockEntity.this.burnTime;
                    case 1 -> TieredPowerGeneratorBlockEntity.this.maxBurnTime;
                    case 2 -> TieredPowerGeneratorBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> TieredPowerGeneratorBlockEntity.this.energyStorage.getMaxEnergyStored();
                    case 4 -> TieredPowerGeneratorBlockEntity.this.getEnergyGeneration();
                    default -> 0;
                };
            }
            
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> TieredPowerGeneratorBlockEntity.this.burnTime = value;
                    case 1 -> TieredPowerGeneratorBlockEntity.this.maxBurnTime = value;
                }
            }
            
            @Override
            public int getCount() {
                return 5;
            }
        };
    }
    
    private static BlockEntityType<?> getBlockEntityType(MachineTier tier) {
        return switch (tier) {
            case BASIC -> ModBlockEntities.POWER_GENERATOR_BASIC.get();
            case ADVANCED -> ModBlockEntities.POWER_GENERATOR_ADVANCED.get();
            case ELITE -> ModBlockEntities.POWER_GENERATOR_ELITE.get();
        };
    }
    
    public MachineTier getTier() {
        return tier;
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.astroexpansion.power_generator_" + tier.getSerializedName());
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new TieredPowerGeneratorMenu(id, inventory, this, this.data, tier);
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                return lazyCombinedHandler.cast();
            } else {
                return lazyItemHandler.cast();
            }
        }
        
        return super.getCapability(cap, side);
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
        lazyCombinedHandler = LazyOptional.of(() -> new CombinedItemHandler(itemHandler, upgradeHandler));
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
        lazyCombinedHandler.invalidate();
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.put("energy", energyStorage.serializeNBT());
        tag.putInt("burnTime", burnTime);
        tag.putInt("maxBurnTime", maxBurnTime);
        tag.putString("tier", tier.getSerializedName());
        super.saveAdditional(tag);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        energyStorage.deserializeNBT(tag.get("energy"));
        burnTime = tag.getInt("burnTime");
        maxBurnTime = tag.getInt("maxBurnTime");
    }
    
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        
        Containers.dropContents(this.level, this.worldPosition, inventory);
        
        // Drop upgrade items
        dropUpgrades();
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, TieredPowerGeneratorBlockEntity entity) {
        if (level.isClientSide) {
            return;
        }
        
        boolean wasLit = entity.isBurning();
        boolean dirty = false;
        
        // If burning, generate energy
        if (entity.isBurning()) {
            entity.burnTime--;
            
            int energyGeneration = entity.getEnergyGeneration();
            if (entity.energyStorage.canReceive()) {
                entity.energyStorage.receiveEnergy(energyGeneration, false);
                dirty = true;
            }
        }
        
        // Try to start burning if not already burning
        if (!entity.isBurning() && entity.canBurn()) {
            ItemStack fuel = entity.itemHandler.getStackInSlot(0);
            if (!fuel.isEmpty()) {
                int burnTime = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);
                if (burnTime > 0) {
                    // Apply efficiency multiplier to burn time
                    entity.maxBurnTime = (int) (burnTime * entity.getEfficiencyMultiplier());
                    entity.burnTime = entity.maxBurnTime;
                    
                    if (fuel.hasCraftingRemainingItem()) {
                        entity.itemHandler.setStackInSlot(0, fuel.getCraftingRemainingItem());
                    } else {
                        fuel.shrink(1);
                    }
                    dirty = true;
                }
            }
        }
        
        // Update block state
        if (wasLit != entity.isBurning()) {
            level.setBlock(pos, state.setValue(TieredPowerGeneratorBlock.LIT, entity.isBurning()), 3);
            dirty = true;
        }
        
        if (dirty) {
            entity.setChanged();
        }
        
        // Push energy to adjacent blocks
        entity.pushEnergy();
    }
    
    private void pushEnergy() {
        if (energyStorage.getEnergyStored() <= 0) {
            return;
        }
        
        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = worldPosition.relative(direction);
            net.minecraft.world.level.block.entity.BlockEntity adjacent = level.getBlockEntity(adjacentPos);
            
            if (adjacent != null) {
                adjacent.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(handler -> {
                    if (handler.canReceive()) {
                        int toSend = Math.min(energyStorage.getEnergyStored(), 100);
                        int sent = handler.receiveEnergy(toSend, false);
                        energyStorage.extractEnergy(sent, false);
                    }
                });
            }
        }
    }
    
    private boolean isBurning() {
        return burnTime > 0;
    }
    
    private boolean canBurn() {
        return energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored();
    }
    
    public int getEnergyGeneration() {
        // Calculate actual energy generation with tier and upgrade bonuses
        float efficiencyMultiplier = tier.getEfficiencyMultiplier() * getEfficiencyMultiplier();
        return (int) (baseEnergyPerTick * efficiencyMultiplier);
    }
    
    public int getBurnTime() {
        return burnTime;
    }
    
    public int getMaxBurnTime() {
        return maxBurnTime;
    }
    
    public int getScaledBurnTime(int scale) {
        if (maxBurnTime == 0) {
            return 0;
        }
        return burnTime * scale / maxBurnTime;
    }
    
    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }
    
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
}