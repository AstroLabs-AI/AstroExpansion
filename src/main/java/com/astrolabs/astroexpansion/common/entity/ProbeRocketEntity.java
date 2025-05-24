package com.astrolabs.astroexpansion.common.entity;

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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.Random;

public class ProbeRocketEntity extends Entity {
    private static final EntityDataAccessor<Integer> FUEL = SynchedEntityData.defineId(ProbeRocketEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> LAUNCHED = SynchedEntityData.defineId(ProbeRocketEntity.class, EntityDataSerializers.BOOLEAN);
    
    private int launchTimer = 0;
    private static final int LAUNCH_TIME = 80; // 4 seconds
    private static final int MAX_FUEL = 500;
    private final Random random = new Random();
    
    public ProbeRocketEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    
    public ProbeRocketEntity(Level level, BlockPos pos) {
        this(ModEntities.PROBE_ROCKET.get(), level);
        this.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
    }
    
    @Override
    protected void defineSynchedData() {
        this.entityData.define(FUEL, MAX_FUEL);
        this.entityData.define(LAUNCHED, false);
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
                    entityData.set(FUEL, fuel - 5);
                }
                
                // Apply upward motion
                if (fuel > 0 && launchTimer < LAUNCH_TIME) {
                    double launchPower = 0.8 + (launchTimer / (double)LAUNCH_TIME) * 3.0;
                    this.setDeltaMovement(0, launchPower, 0);
                } else if (fuel <= 0) {
                    // Out of fuel - start falling
                    this.setDeltaMovement(this.getDeltaMovement().add(0, -0.08, 0));
                }
                
                // Return with loot when high enough
                if (this.getY() > 250 && fuel > 0) {
                    this.returnWithLoot();
                    return;
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
                for (int i = 0; i < 3; i++) {
                    level().addParticle(ParticleTypes.FLAME,
                        getX() + (random.nextDouble() - 0.5) * 0.3,
                        getY() - 0.3,
                        getZ() + (random.nextDouble() - 0.5) * 0.3,
                        0, -0.5, 0);
                    level().addParticle(ParticleTypes.SMOKE,
                        getX() + (random.nextDouble() - 0.5) * 0.3,
                        getY() - 0.3,
                        getZ() + (random.nextDouble() - 0.5) * 0.3,
                        0, -0.2, 0);
                }
            }
        }
    }
    
    public void launch() {
        if (!level().isClientSide && !entityData.get(LAUNCHED)) {
            entityData.set(LAUNCHED, true);
            level().playSound(null, getX(), getY(), getZ(), 
                SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.NEUTRAL, 2.0F, 0.7F);
        }
    }
    
    public boolean hasFuel() {
        // Probe rockets have internal fuel, no check needed
        return true;
    }
    
    private void returnWithLoot() {
        if (!level().isClientSide) {
            // Drop random space loot
            BlockPos dropPos = blockPosition();
            
            // Always drop the probe back
            ItemEntity probeItem = new ItemEntity(level(), dropPos.getX() + 0.5, dropPos.getY() + 1, dropPos.getZ() + 0.5,
                new ItemStack(ModItems.PROBE_ROCKET.get()));
            level().addFreshEntity(probeItem);
            
            // Random loot chances
            if (random.nextFloat() < 0.8) { // 80% chance for moon dust
                ItemEntity loot = new ItemEntity(level(), dropPos.getX() + 0.5, dropPos.getY() + 1, dropPos.getZ() + 0.5,
                    new ItemStack(ModItems.MOON_DUST_ITEM.get(), 4 + random.nextInt(5)));
                level().addFreshEntity(loot);
            }
            
            if (random.nextFloat() < 0.5) { // 50% chance for helium-3
                ItemEntity loot = new ItemEntity(level(), dropPos.getX() + 0.5, dropPos.getY() + 1, dropPos.getZ() + 0.5,
                    new ItemStack(ModItems.HELIUM3_CRYSTAL.get(), 1 + random.nextInt(3)));
                level().addFreshEntity(loot);
            }
            
            if (random.nextFloat() < 0.2) { // 20% chance for alien artifact
                ItemEntity loot = new ItemEntity(level(), dropPos.getX() + 0.5, dropPos.getY() + 1, dropPos.getZ() + 0.5,
                    new ItemStack(ModItems.ALIEN_ARTIFACT.get()));
                level().addFreshEntity(loot);
            }
            
            if (random.nextFloat() < 0.1) { // 10% chance for research data
                ItemEntity loot = new ItemEntity(level(), dropPos.getX() + 0.5, dropPos.getY() + 1, dropPos.getZ() + 0.5,
                    new ItemStack(ModItems.RESEARCH_DATA_SPACE.get()));
                level().addFreshEntity(loot);
            }
            
            // Effects
            ((ServerLevel)level()).sendParticles(ParticleTypes.FIREWORK, 
                dropPos.getX() + 0.5, dropPos.getY() + 1, dropPos.getZ() + 0.5, 
                50, 0.5, 0.5, 0.5, 0.1);
            
            level().playSound(null, dropPos, SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, 
                SoundSource.NEUTRAL, 1.0F, 1.0F);
            
            this.discard();
        }
    }
    
    private void explode() {
        if (!level().isClientSide) {
            level().explode(this, getX(), getY(), getZ(), 1.5F, Level.ExplosionInteraction.BLOCK);
            this.discard();
        }
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
            this.spawnAtLocation(new ItemStack(ModItems.PROBE_ROCKET.get()));
            this.discard();
            return true;
        }
        return false;
    }
    
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}