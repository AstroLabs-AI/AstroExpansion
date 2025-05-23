package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.blocks.DroneDockBlock;
import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.entities.drones.AbstractDroneEntity;
import com.astrolabs.astroexpansion.common.menu.DroneDockMenu;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DroneDockBlockEntity extends BlockEntity implements MenuProvider {
    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(50000, 1000, 1000);
    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    
    private static final int CHARGE_RATE = 100; // FE per tick
    private static final int SCAN_RADIUS = 5;
    
    public DroneDockBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRONE_DOCK.get(), pos, state);
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.astroexpansion.drone_dock");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new DroneDockMenu(id, inventory, this);
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
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyEnergyHandler.invalidate();
        lazyItemHandler.invalidate();
    }
    
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("energy", energyStorage.writeToNBT(new CompoundTag()));
        nbt.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(nbt);
    }
    
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        energyStorage.readFromNBT(nbt.getCompound("energy"));
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
    }
    
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, DroneDockBlockEntity entity) {
        if (level.isClientSide) {
            return;
        }
        
        boolean isCharging = false;
        
        // Find nearby drones to charge
        AABB searchBox = new AABB(pos).inflate(SCAN_RADIUS);
        List<Entity> entities = level.getEntities(null, searchBox);
        
        for (Entity e : entities) {
            if (e instanceof AbstractDroneEntity drone) {
                double distance = drone.distanceToSqr(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                
                if (distance < 2.0) {
                    // Charge the drone
                    if (entity.energyStorage.getEnergyStored() >= CHARGE_RATE) {
                        IEnergyStorage droneEnergy = drone.getCapability(ForgeCapabilities.ENERGY)
                            .orElse(null);
                        
                        if (droneEnergy != null && droneEnergy.canReceive()) {
                            int received = droneEnergy.receiveEnergy(CHARGE_RATE, false);
                            entity.energyStorage.extractEnergy(received, false);
                            isCharging = true;
                            
                            // Try to deposit items from drone
                            entity.depositDroneItems(drone);
                        }
                    }
                }
            }
        }
        
        // Update block state
        if (state.getValue(DroneDockBlock.CHARGING) != isCharging) {
            level.setBlock(pos, state.setValue(DroneDockBlock.CHARGING, isCharging), 3);
        }
    }
    
    private void depositDroneItems(AbstractDroneEntity drone) {
        ItemStackHandler droneInventory = drone.getInventory();
        
        for (int i = 0; i < droneInventory.getSlots(); i++) {
            net.minecraft.world.item.ItemStack stack = droneInventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                // Try to insert into dock inventory
                for (int j = 0; j < itemHandler.getSlots(); j++) {
                    stack = itemHandler.insertItem(j, stack, false);
                    if (stack.isEmpty()) {
                        break;
                    }
                }
                
                // Update drone inventory
                droneInventory.setStackInSlot(i, stack);
            }
        }
    }
    
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
    
    public LazyOptional<IEnergyStorage> getEnergyHandler() {
        return lazyEnergyHandler;
    }
    
    public AbstractDroneEntity getDockedDrone() {
        // Find the closest drone within docking range
        if (level != null && !level.isClientSide) {
            AABB searchBox = new AABB(worldPosition).inflate(2.0);
            List<Entity> entities = level.getEntities(null, searchBox);
            
            for (Entity e : entities) {
                if (e instanceof AbstractDroneEntity drone) {
                    double distance = drone.distanceToSqr(worldPosition.getX() + 0.5, 
                                                          worldPosition.getY() + 1, 
                                                          worldPosition.getZ() + 0.5);
                    if (distance < 2.0) {
                        return drone;
                    }
                }
            }
        }
        return null;
    }
}