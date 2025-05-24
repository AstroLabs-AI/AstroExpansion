package com.astrolabs.astroexpansion.common.entity;

import com.astrolabs.astroexpansion.common.registry.ModDimensions;
import com.astrolabs.astroexpansion.common.registry.ModEntities;
import com.astrolabs.astroexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.ChatFormatting;

public class PersonalRocketEntity extends Entity {
    private static final EntityDataAccessor<Integer> FUEL = SynchedEntityData.defineId(PersonalRocketEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> LAUNCHED = SynchedEntityData.defineId(PersonalRocketEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_FUEL = SynchedEntityData.defineId(PersonalRocketEntity.class, EntityDataSerializers.BOOLEAN);
    
    private int launchTimer = 0;
    private static final int LAUNCH_TIME = 100; // 5 seconds
    private static final int MAX_FUEL = 1000;
    private Player pilot;
    
    public PersonalRocketEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    
    public PersonalRocketEntity(Level level, BlockPos pos) {
        this(ModEntities.PERSONAL_ROCKET.get(), level);
        this.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }
    
    @Override
    protected void defineSynchedData() {
        this.entityData.define(FUEL, 0);
        this.entityData.define(LAUNCHED, false);
        this.entityData.define(HAS_FUEL, false);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (!level().isClientSide) {
            boolean launched = entityData.get(LAUNCHED);
            
            if (launched) {
                launchTimer++;
                
                // Consume fuel
                int fuel = entityData.get(FUEL);
                if (fuel > 0) {
                    entityData.set(FUEL, fuel - 2);
                }
                
                // Apply upward motion
                if (fuel > 0 && launchTimer < LAUNCH_TIME) {
                    double launchPower = 0.5 + (launchTimer / (double)LAUNCH_TIME) * 2.0;
                    this.setDeltaMovement(0, launchPower, 0);
                } else if (fuel <= 0) {
                    // Out of fuel - start falling
                    this.setDeltaMovement(this.getDeltaMovement().add(0, -0.08, 0));
                }
                
                // Teleport to space when high enough
                if (this.getY() > 300 && fuel > 0 && pilot != null && pilot instanceof ServerPlayer serverPlayer) {
                    ServerLevel spaceDim = serverPlayer.server.getLevel(ModDimensions.SPACE_KEY);
                    if (spaceDim != null) {
                        serverPlayer.changeDimension(spaceDim, new SpaceTeleporter(new BlockPos(0, 100, 0)));
                        this.discard();
                        return;
                    }
                }
                
                // Crash if hit ground with momentum
                if (this.onGround() && this.getDeltaMovement().y < -0.5) {
                    this.explode();
                    return;
                }
            }
            
            this.move(MoverType.SELF, this.getDeltaMovement());
        } else {
            // Client-side particles
            if (entityData.get(LAUNCHED) && entityData.get(FUEL) > 0) {
                for (int i = 0; i < 5; i++) {
                    level().addParticle(ParticleTypes.FLAME,
                        getX() + (random.nextDouble() - 0.5) * 0.5,
                        getY() - 0.5,
                        getZ() + (random.nextDouble() - 0.5) * 0.5,
                        0, -0.5, 0);
                    level().addParticle(ParticleTypes.SMOKE,
                        getX() + (random.nextDouble() - 0.5) * 0.5,
                        getY() - 0.5,
                        getZ() + (random.nextDouble() - 0.5) * 0.5,
                        0, -0.2, 0);
                }
            }
        }
    }
    
    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!level().isClientSide && !entityData.get(LAUNCHED)) {
            ItemStack heldItem = player.getItemInHand(hand);
            
            // Check if player is trying to fuel the rocket
            if (heldItem.getItem() == net.minecraft.world.item.Items.WATER_BUCKET && !entityData.get(HAS_FUEL)) {
                entityData.set(HAS_FUEL, true);
                entityData.set(FUEL, MAX_FUEL);
                if (!player.isCreative()) {
                    player.setItemInHand(hand, new ItemStack(net.minecraft.world.item.Items.BUCKET));
                }
                player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("Rocket fueled with water!").withStyle(net.minecraft.ChatFormatting.GREEN), 
                    true
                );
                level().playSound(null, getX(), getY(), getZ(), 
                    SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
            
            if (pilot == null && entityData.get(HAS_FUEL)) {
                // Mount the rocket
                player.startRiding(this);
                pilot = player;
                return InteractionResult.SUCCESS;
            } else if (!entityData.get(HAS_FUEL)) {
                player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("Rocket needs water fuel!").withStyle(net.minecraft.ChatFormatting.RED), 
                    true
                );
            }
        }
        return InteractionResult.PASS;
    }
    
    public void launch() {
        if (!level().isClientSide && !entityData.get(LAUNCHED) && pilot != null) {
            entityData.set(LAUNCHED, true);
            level().playSound(null, getX(), getY(), getZ(), 
                SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.NEUTRAL, 3.0F, 0.5F);
        }
    }
    
    private void explode() {
        if (!level().isClientSide) {
            level().explode(this, getX(), getY(), getZ(), 2.0F, Level.ExplosionInteraction.BLOCK);
            if (pilot != null) {
                pilot.hurt(level().damageSources().explosion(this, this), 20.0F);
            }
            this.discard();
        }
    }
    
    @Override
    public void remove(RemovalReason reason) {
        if (pilot != null) {
            pilot.stopRiding();
        }
        super.remove(reason);
    }
    
    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        entityData.set(FUEL, compound.getInt("Fuel"));
        entityData.set(LAUNCHED, compound.getBoolean("Launched"));
        launchTimer = compound.getInt("LaunchTimer");
    }
    
    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Fuel", entityData.get(FUEL));
        compound.putBoolean("Launched", entityData.get(LAUNCHED));
        compound.putInt("LaunchTimer", launchTimer);
    }
    
    @Override
    public boolean isPickable() {
        return !entityData.get(LAUNCHED);
    }
    
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide && !entityData.get(LAUNCHED)) {
            this.spawnAtLocation(new ItemStack(ModItems.PERSONAL_ROCKET.get()));
            this.discard();
            return true;
        }
        return false;
    }
    
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
    
    public Vec3 getPassengerAttachmentPoint(Entity entity) {
        return new Vec3(0, this.getBbHeight() * 0.75, 0);
    }
    
    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() < 1 && passenger instanceof Player;
    }
}