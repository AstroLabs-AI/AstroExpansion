package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.registry.AEBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;

public class BasicGeneratorBlockEntity extends AbstractMachineBlockEntity implements Container {
    
    private static final int ENERGY_PER_TICK = 40;
    private static final int ENERGY_CAPACITY = 10000;
    
    private NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY); // Fuel slot
    private int burnTime = 0;
    private int burnTimeTotal = 0;
    
    public BasicGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(AEBlockEntities.BASIC_GENERATOR.get(), pos, state, ENERGY_CAPACITY);
    }
    
    @Override
    public void tick() {
        if (level == null || level.isClientSide) return;
        
        boolean wasActive = isActive;
        isActive = false;
        
        // If burning, generate energy
        if (burnTime > 0) {
            burnTime--;
            int energyGenerated = Math.min(ENERGY_PER_TICK, energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());
            energyStorage.receiveEnergy(energyGenerated, false);
            isActive = true;
        }
        
        // Try to start burning if not burning and has space for energy
        if (burnTime == 0 && energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
            ItemStack fuel = items.get(0);
            if (!fuel.isEmpty()) {
                int fuelValue = CommonHooks.getBurnTime(fuel, RecipeType.SMELTING);
                if (fuelValue > 0) {
                    burnTime = fuelValue;
                    burnTimeTotal = fuelValue;
                    fuel.shrink(1);
                    isActive = true;
                }
            }
        }
        
        if (wasActive != isActive) {
            markDirtyAndSync();
        }
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, BasicGeneratorBlockEntity blockEntity) {
        blockEntity.tick();
    }
    
    public int getBurnTime() {
        return burnTime;
    }
    
    public int getBurnTimeTotal() {
        return burnTimeTotal;
    }
    
    public float getBurnProgress() {
        return burnTimeTotal > 0 ? (float) burnTime / burnTimeTotal : 0;
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putInt("BurnTime", burnTime);
        tag.putInt("BurnTimeTotal", burnTimeTotal);
    }
    
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(1, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
        burnTime = tag.getIntOr("BurnTime", 0);
        burnTimeTotal = tag.getIntOr("BurnTimeTotal", 0);
    }
    
    // Container implementation
    @Override
    public int getContainerSize() {
        return items.size();
    }
    
    @Override
    public boolean isEmpty() {
        return items.get(0).isEmpty();
    }
    
    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }
    
    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(items, slot, amount);
    }
    
    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }
    
    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }
    
    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }
    
    @Override
    public void clearContent() {
        items.clear();
    }
}