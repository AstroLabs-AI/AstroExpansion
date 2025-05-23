package com.astrolabs.astroexpansion.common.entities.drones;

import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class AbstractDroneEntity extends Mob {
    private static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(AbstractDroneEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ENERGY = SynchedEntityData.defineId(AbstractDroneEntity.class, EntityDataSerializers.INT);
    
    protected final AstroEnergyStorage energyStorage;
    protected final ItemStackHandler inventory;
    protected final int inventorySize;
    
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    
    @Nullable
    protected UUID ownerUUID;
    @Nullable
    protected BlockPos homePos;
    protected int taskCooldown = 0;
    
    public AbstractDroneEntity(EntityType<? extends AbstractDroneEntity> type, Level level, int inventorySize, int energyCapacity) {
        super(type, level);
        this.inventorySize = inventorySize;
        this.inventory = new ItemStackHandler(inventorySize);
        this.energyStorage = new AstroEnergyStorage(energyCapacity, 100, 100);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.setNoGravity(true);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ACTIVE, false);
        this.entityData.define(ENERGY, 0);
    }
    
    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.FLYING_SPEED, 0.4D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D)
            .add(Attributes.FOLLOW_RANGE, 48.0D);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (!level().isClientSide) {
            // Update energy display
            entityData.set(ENERGY, energyStorage.getEnergyStored());
            
            // Consume energy when active
            if (isActive() && tickCount % 20 == 0) {
                int consumed = getEnergyConsumptionPerSecond();
                if (energyStorage.getEnergyStored() >= consumed) {
                    energyStorage.extractEnergy(consumed, false);
                } else {
                    setActive(false);
                }
            }
            
            // Execute drone-specific tasks
            if (isActive() && taskCooldown <= 0) {
                performTask();
                taskCooldown = getTaskCooldown();
            } else if (taskCooldown > 0) {
                taskCooldown--;
            }
            
            // Return home if inactive or low energy
            if (!isActive() || energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored() * 0.1) {
                returnToHome();
            }
        }
    }
    
    protected abstract void performTask();
    protected abstract int getTaskCooldown();
    protected abstract int getEnergyConsumptionPerSecond();
    
    public boolean isActive() {
        return entityData.get(ACTIVE);
    }
    
    public void setActive(boolean active) {
        entityData.set(ACTIVE, active);
    }
    
    public void setOwner(@Nullable UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }
    
    @Nullable
    public UUID getOwnerUUID() {
        return ownerUUID;
    }
    
    @Nullable
    public Player getOwner() {
        return ownerUUID != null ? level().getPlayerByUUID(ownerUUID) : null;
    }
    
    public void setHomePos(@Nullable BlockPos pos) {
        this.homePos = pos;
    }
    
    @Nullable
    public BlockPos getHomePos() {
        return homePos;
    }
    
    protected void returnToHome() {
        if (homePos != null && distanceToSqr(Vec3.atCenterOf(homePos)) > 4.0) {
            getNavigation().moveTo(homePos.getX() + 0.5, homePos.getY() + 1, homePos.getZ() + 0.5, 1.0);
        }
    }
    
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player && player.getUUID().equals(ownerUUID)) {
            // Owner can break drone by attacking it
            if (!level().isClientSide) {
                dropAsItem();
            }
            return false;
        }
        return super.hurt(source, amount);
    }
    
    protected void dropAsItem() {
        ItemStack droneItem = getAsItemStack();
        spawnAtLocation(droneItem);
        
        // Drop inventory contents
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                spawnAtLocation(stack);
            }
        }
        
        discard();
    }
    
    protected abstract ItemStack getAsItemStack();
    
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Inventory", inventory.serializeNBT());
        tag.put("Energy", energyStorage.writeToNBT(new CompoundTag()));
        tag.putBoolean("Active", isActive());
        
        if (ownerUUID != null) {
            tag.putUUID("Owner", ownerUUID);
        }
        
        if (homePos != null) {
            tag.putInt("HomeX", homePos.getX());
            tag.putInt("HomeY", homePos.getY());
            tag.putInt("HomeZ", homePos.getZ());
        }
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        inventory.deserializeNBT(tag.getCompound("Inventory"));
        energyStorage.readFromNBT(tag.getCompound("Energy"));
        setActive(tag.getBoolean("Active"));
        
        if (tag.hasUUID("Owner")) {
            ownerUUID = tag.getUUID("Owner");
        }
        
        if (tag.contains("HomeX")) {
            homePos = new BlockPos(tag.getInt("HomeX"), tag.getInt("HomeY"), tag.getInt("HomeZ"));
        }
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyEnergyHandler.invalidate();
    }
    
    @Override
    public void reviveCaps() {
        super.reviveCaps();
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
    }
    
    @Override
    public boolean canBeLeashed(Player player) {
        return false;
    }
    
    @Override
    public boolean isPushable() {
        return false;
    }
    
    @Override
    protected void pushEntities() {
        // Drones don't push other entities
    }
    
    public ItemStackHandler getInventory() {
        return inventory;
    }
    
    public AstroEnergyStorage getEnergyStorage() {
        return energyStorage;
    }
}