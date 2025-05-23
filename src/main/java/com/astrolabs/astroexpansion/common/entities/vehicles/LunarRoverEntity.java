package com.astrolabs.astroexpansion.common.entities.vehicles;

import com.astrolabs.astroexpansion.common.registry.ModEntities;
import com.astrolabs.astroexpansion.common.registry.ModItems;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LunarRoverEntity extends Entity {
    private static final EntityDataAccessor<Float> ENERGY_PERCENT = SynchedEntityData.defineId(LunarRoverEntity.class, EntityDataSerializers.FLOAT);
    private static final int MAX_ENERGY = 100000;
    private static final int ENERGY_PER_TICK = 5;
    
    private final EnergyStorage energyStorage = new EnergyStorage(MAX_ENERGY);
    private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> energyStorage);
    
    private float deltaRotation;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;
    
    public LunarRoverEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
    }
    
    public LunarRoverEntity(Level level, double x, double y, double z) {
        this(ModEntities.LUNAR_ROVER.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }
    
    @Override
    protected void defineSynchedData() {
        this.entityData.define(ENERGY_PERCENT, 0.0F);
    }
    
    @Override
    public boolean canBeCollidedWith() {
        return true;
    }
    
    @Override
    public boolean isPushable() {
        return true;
    }
    
    @Override
    protected Vec3 getRelativePortalPosition(Direction.Axis axis, BlockUtil.FoundRectangle portal) {
        return LivingEntity.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition(axis, portal));
    }
    
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.level().isClientSide && !this.isRemoved()) {
            this.markHurt();
            
            boolean creative = source.getEntity() instanceof Player player && player.getAbilities().instabuild;
            if (!creative && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                this.spawnAtLocation(this.getDropItem());
            }
            
            this.discard();
            return true;
        } else {
            return true;
        }
    }
    
    protected ItemStack getDropItem() {
        return new ItemStack(ModItems.LUNAR_ROVER.get());
    }
    
    @Override
    public void animateHurt(float p_265160_) {
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() + 10.0F);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
            double d1 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
            double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
            double d3 = Mth.wrapDegrees(this.lerpYRot - (double)this.getYRot());
            this.setYRot(this.getYRot() + (float)d3 / (float)this.lerpSteps);
            this.setXRot(this.getXRot() + (float)(this.lerpXRot - (double)this.getXRot()) / (float)this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d0, d1, d2);
            this.setRot(this.getYRot(), this.getXRot());
        }
        
        this.tickLerp();
        
        if (this.isControlledByLocalInstance()) {
            this.updateMotion();
            if (this.level().isClientSide) {
                this.controlRover();
            }
            
            this.move(MoverType.SELF, this.getDeltaMovement());
        } else {
            this.setDeltaMovement(Vec3.ZERO);
        }
        
        // Update energy display
        float energyPercent = (float)energyStorage.getEnergyStored() / (float)energyStorage.getMaxEnergyStored();
        this.entityData.set(ENERGY_PERCENT, energyPercent);
        
        this.checkInsideBlocks();
    }
    
    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }
    }
    
    private void updateMotion() {
        double gravity = -0.04D; // Reduced gravity for moon
        
        if (!this.isNoGravity()) {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x, motion.y + gravity, motion.z);
        }
        
        // Apply friction
        if (this.onGround()) {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.multiply(0.8D, 1.0D, 0.8D));
        }
    }
    
    private void controlRover() {
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player player) {
            float forward = player.zza;
            float strafe = player.xxa;
            
            if (forward != 0 || strafe != 0) {
                // Consume energy when moving
                if (energyStorage.getEnergyStored() >= ENERGY_PER_TICK) {
                    energyStorage.extractEnergy(ENERGY_PER_TICK, false);
                    
                    // Set rotation based on player
                    this.setYRot(player.getYRot());
                    this.yRotO = this.getYRot();
                    this.setXRot(player.getXRot() * 0.5F);
                    
                    // Calculate movement vector
                    float speed = 0.3F; // Rover speed
                    Vec3 movement = this.getLookAngle().scale(forward * speed);
                    
                    // Add strafe movement
                    Vec3 strafeVec = this.getLookAngle().cross(new Vec3(0, 1, 0)).normalize().scale(strafe * speed * 0.5);
                    movement = movement.add(strafeVec);
                    
                    // Apply movement if on ground
                    if (this.onGround()) {
                        this.setDeltaMovement(movement.x, this.getDeltaMovement().y, movement.z);
                        
                        // Small hop over 1-block obstacles
                        BlockPos frontPos = this.blockPosition().relative(this.getDirection());
                        if (!this.level().getBlockState(frontPos).isAir() && 
                            this.level().getBlockState(frontPos.above()).isAir()) {
                            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.5, 0));
                        }
                    }
                } else {
                    // No energy - can't move
                    this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
                }
            }
        }
    }
    
    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        } else if (!this.level().isClientSide) {
            return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            return InteractionResult.SUCCESS;
        }
    }
    
    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.isControlledByLocalInstance() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.absMoveTo(this.lerpX, this.lerpY, this.lerpZ, (float)this.lerpYRot, (float)this.lerpXRot);
        }
    }
    
    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        return entity instanceof LivingEntity living ? living : null;
    }
    
    @Override
    public void positionRider(Entity passenger, MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            float f = 0.0F;
            float f1 = (float)((this.isRemoved() ? 0.01F : this.getPassengersRidingOffset()) + passenger.getMyRidingOffset());
            
            if (this.getPassengers().size() > 1) {
                int i = this.getPassengers().indexOf(passenger);
                if (i == 0) {
                    f = 0.2F;
                } else {
                    f = -0.6F;
                }
            }
            
            Vec3 vec3 = (new Vec3((double)f, 0.0D, 0.0D)).yRot(-this.getYRot() * ((float)Math.PI / 180F) - ((float)Math.PI / 2F));
            callback.accept(passenger, this.getX() + vec3.x, this.getY() + (double)f1, this.getZ() + vec3.z);
        }
    }
    
    @Override
    public double getPassengersRidingOffset() {
        return 0.5D;
    }
    
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Energy")) {
            energyStorage.deserializeNBT(tag.get("Energy"));
        }
    }
    
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put("Energy", energyStorage.serializeNBT());
    }
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyOptional.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyOptional.invalidate();
    }
    
    public float getEnergyPercent() {
        return this.entityData.get(ENERGY_PERCENT);
    }
    
    public int getHurtTime() {
        return 0;
    }
    
    public void setHurtTime(int time) {
    }
    
    public float getDamage() {
        return 0;
    }
    
    public void setDamage(float damage) {
    }
    
    public int getHurtDir() {
        return 0;
    }
    
    public void setHurtDir(int dir) {
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 30.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.5D);
    }
    
    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }
}