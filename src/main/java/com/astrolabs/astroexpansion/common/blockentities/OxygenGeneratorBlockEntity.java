package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.api.energy.IEnergyHandler;
import com.astrolabs.astroexpansion.common.capabilities.AECapabilityProviders;
import com.astrolabs.astroexpansion.common.menu.OxygenGeneratorMenu;
import com.astrolabs.astroexpansion.common.registry.AEBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class OxygenGeneratorBlockEntity extends AbstractMachineBlockEntity implements MenuProvider, Container {
    private static final int ENERGY_CAPACITY = 50000;
    private static final int ENERGY_PER_OPERATION = 100;
    private static final int WATER_PER_OPERATION = 100;
    private static final int OXYGEN_PER_OPERATION = 50;
    private static final int OPERATION_TIME = 100;
    
    private final EnergyStorage energyStorage = new EnergyStorage(ENERGY_CAPACITY, 500, 0);
    private final FluidTank waterTank = new FluidTank(4000);
    private final FluidTank oxygenTank = new FluidTank(4000);
    private final SimpleContainer inventory = new SimpleContainer(2);
    
    private int processTime = 0;
    private int oxygenAmount = 0;
    private int maxOxygen = 10000;
    
    public OxygenGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(AEBlockEntities.OXYGEN_GENERATOR.get(), pos, blockState, ENERGY_CAPACITY);
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
        energyStorage.deserializeNBT(registries, tag.getCompound("Energy"));
        waterTank.readFromNBT(registries, tag.getCompound("WaterTank"));
        oxygenTank.readFromNBT(registries, tag.getCompound("OxygenTank"));
        inventory.fromTag(tag.getList("Inventory", 10), registries);
        processTime = tag.getInt("ProcessTime");
        oxygenAmount = tag.getInt("OxygenAmount");
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Energy", energyStorage.serializeNBT(registries));
        tag.put("WaterTank", waterTank.writeToNBT(registries, new CompoundTag()));
        tag.put("OxygenTank", oxygenTank.writeToNBT(registries, new CompoundTag()));
        tag.put("Inventory", inventory.createTag(registries));
        tag.putInt("ProcessTime", processTime);
        tag.putInt("OxygenAmount", oxygenAmount);
    }
    
    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
            extractEnergy();
            handleWaterInput();
            processWater();
            distributeOxygen();
        }
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, OxygenGeneratorBlockEntity blockEntity) {
        blockEntity.tick();
    }
    
    private void extractEnergy() {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(direction);
            if (level.getBlockEntity(neighborPos) instanceof IEnergyHandler energyHandler) {
                int extracted = energyHandler.extractEnergy(500, false);
                energyStorage.receiveEnergy(extracted, false);
                if (extracted > 0) {
                    setChanged();
                }
            }
        }
    }
    
    private void handleWaterInput() {
        ItemStack waterBucket = inventory.getItem(0);
        if (!waterBucket.isEmpty() && waterBucket.is(Items.WATER_BUCKET)) {
            if (waterTank.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.SIMULATE) == 1000) {
                waterTank.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
                inventory.setItem(0, new ItemStack(Items.BUCKET));
                setChanged();
            }
        }
    }
    
    private void processWater() {
        if (waterTank.getFluidAmount() >= WATER_PER_OPERATION && 
            energyStorage.getEnergyStored() >= ENERGY_PER_OPERATION &&
            oxygenAmount + OXYGEN_PER_OPERATION <= maxOxygen) {
            
            if (processTime < OPERATION_TIME) {
                processTime++;
            } else {
                waterTank.drain(WATER_PER_OPERATION, IFluidHandler.FluidAction.EXECUTE);
                energyStorage.extractEnergy(ENERGY_PER_OPERATION, false);
                oxygenAmount += OXYGEN_PER_OPERATION;
                processTime = 0;
                setChanged();
            }
        } else {
            if (processTime > 0) {
                processTime--;
            }
        }
    }
    
    private void distributeOxygen() {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(direction);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);
            
        }
    }
    
    public int getOxygenAmount() {
        return oxygenAmount;
    }
    
    public int getMaxOxygen() {
        return maxOxygen;
    }
    
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }
    
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }
    
    public int getWaterAmount() {
        return waterTank.getFluidAmount();
    }
    
    public int getMaxWater() {
        return waterTank.getCapacity();
    }
    
    public int getProcessTime() {
        return processTime;
    }
    
    public int getMaxProcessTime() {
        return OPERATION_TIME;
    }
    
    @Override
    public Component getDisplayName() {
        return Component.literal("Oxygen Generator");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new OxygenGeneratorMenu(id, playerInventory, this);
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
    public int getContainerSize() {
        return inventory.getContainerSize();
    }
    
    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }
    
    @Override
    public ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }
    
    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = inventory.removeItem(slot, amount);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }
    
    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return inventory.removeItemNoUpdate(slot);
    }
    
    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.setItem(slot, stack);
        setChanged();
    }
    
    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }
    
    @Override
    public void clearContent() {
        inventory.clearContent();
        setChanged();
    }
}