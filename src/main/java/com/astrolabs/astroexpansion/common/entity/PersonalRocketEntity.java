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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.world.entity.Pose; // Added import

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
        // Position adjustments - centered horizontally, slightly higher vertically for better alignment
        this.setPos(pos.getX() + 0.5, pos.getY() + 1.6, pos.getZ() + 0.5);
        this.noPhysics = true; // Prevent clipping with blocks
        if (level.isClientSide) {
            // Add spawn effect particles
            for (int i = 0; i < 12; i++) {
                level.addParticle(ParticleTypes.CLOUD, 
                    pos.getX() + 0.5 + (Math.random() - 0.5) * 0.5,
                    pos.getY() + 0.2 + Math.random() * 0.3,
                    pos.getZ() + 0.5 + (Math.random() - 0.5) * 0.5,
                    0, 0.05, 0);
            }
            // Add a few spark particles for added effect
            for (int i = 0; i < 5; i++) {
                level.addParticle(ParticleTypes.CRIT, 
                    pos.getX() + 0.5 + (Math.random() - 0.5) * 0.3,
                    pos.getY() + 0.5 + Math.random() * 0.5,
                    pos.getZ() + 0.5 + (Math.random() - 0.5) * 0.3,
                    0, 0, 0);
            }
        }
        System.out.println("Personal Rocket entity spawned at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
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
        
        // Position any passengers in the rocket (normally just the player)
        // Y offset is 0.48 to align player's eyes with the window
        if (!this.getPassengers().isEmpty()) {
            Entity passenger = this.getPassengers().get(0);
            if (passenger != null) {
                // Calculate the position for the passenger relative to this rocket
                double yOffset = 0.48D; // The magic offset that positions the player eyes at window level
                passenger.setPos(
                    this.getX(),  // No X offset - centered
                    this.getY() + yOffset,  // Y offset to see through window
                    this.getZ()  // No Z offset - centered
                );
                
                // Do NOT sync rotation - prevents screen flipping
                // Set player to standing pose if it's a player
                if (passenger instanceof Player) {
                    // Set player to standing pose directly
                    ((Player) passenger).setPose(Pose.STANDING);
                }
            }
        }
        
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
    public InteractionResult interact(@javax.annotation.Nonnull Player player, @javax.annotation.Nonnull InteractionHand hand) {
        // Prevent interaction when already launched
        if (level().isClientSide || entityData.get(LAUNCHED)) {
            return InteractionResult.PASS;
        }
        
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
        
        // Try to mount the rocket if it's fueled and empty
        if (this.getPassengers().isEmpty() && entityData.get(HAS_FUEL)) {
            // Print a debug message
            System.out.println("Player attempting to mount rocket");
            
            // Mount the rocket - this calls canAddPassenger internally
            boolean mounted = player.startRiding(this);
            System.out.println("Mounting result: " + mounted);
            
            if (mounted) {
                pilot = player;
                
                // Set player's rotation immediately after mounting to prevent screen flip
                // and ensure they are looking forward relative to the rocket.
                player.setXRot(0F); // Set pitch to 0 (looking straight ahead)
                player.setYRot(this.getYRot()); // Align yaw with the rocket
                
                return InteractionResult.SUCCESS;
            }
        } else if (!entityData.get(HAS_FUEL)) {
            player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("Rocket needs water fuel!").withStyle(net.minecraft.ChatFormatting.RED), 
                true
            );
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
    public void remove(@javax.annotation.Nonnull RemovalReason reason) {
        if (pilot != null) {
            pilot.stopRiding();
        }
        super.remove(reason);
    }
    
    @Override
    protected void readAdditionalSaveData(@javax.annotation.Nonnull CompoundTag compound) {
        entityData.set(FUEL, compound.getInt("Fuel"));
        entityData.set(LAUNCHED, compound.getBoolean("Launched"));
        launchTimer = compound.getInt("LaunchTimer");
    }
    
    @Override
    protected void addAdditionalSaveData(@javax.annotation.Nonnull CompoundTag compound) {
        compound.putInt("Fuel", entityData.get(FUEL));
        compound.putBoolean("Launched", entityData.get(LAUNCHED));
        compound.putInt("LaunchTimer", launchTimer);
    }
    
    @Override
    public boolean isPickable() {
        return !entityData.get(LAUNCHED);
    }
    
    @Override
    public boolean hurt(@javax.annotation.Nonnull DamageSource source, float amount) {
        if (!entityData.get(LAUNCHED) && !level().isClientSide) {
            // Drop as item if not launched
            ItemStack drop = new ItemStack(ModItems.PERSONAL_ROCKET.get());
            Block.popResource(level(), blockPosition(), drop);
            this.discard();
            return true;
        }
        return false;
    }
    
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
    
    // We don't override any positioning methods as they may be final in different Forge versions
    
    @Override
    public boolean canAddPassenger(@javax.annotation.Nonnull Entity passenger) {
        // Debug message to verify this is being called
        System.out.println("canAddPassenger called with: " + passenger.getName().getString());
        
        // Allow only one player passenger and only if not launched
        return this.getPassengers().isEmpty() && 
               passenger instanceof Player && 
               !entityData.get(LAUNCHED);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(@javax.annotation.Nonnull net.minecraft.world.entity.LivingEntity passenger) {
        // Try to find a safe spot around the rocket, prioritizing air blocks above solid ground
        for (int i = 0; i < 8; ++i) {
            double angle = (Math.PI * 2.0 / 8.0) * i;
            double xOffset = Math.cos(angle) * 1.5; // Check 1.5 blocks away
            double zOffset = Math.sin(angle) * 1.5;
            BlockPos dismountPos = BlockPos.containing(this.getX() + xOffset, this.getY(), this.getZ() + zOffset);

            // Check if the block at head level is air and block at foot level is air, and block below foot level is solid
            BlockPos headPos = dismountPos.above(); // Approximate head position
            if (this.level().getBlockState(dismountPos).isAir() && 
                this.level().getBlockState(headPos).isAir() && 
                this.level().getBlockState(dismountPos.below()).isSolidRender(this.level(), dismountPos.below())) {
                return new Vec3(dismountPos.getX() + 0.5, dismountPos.getY(), dismountPos.getZ() + 0.5);
            }
        }
        // Fallback to default if no suitable spot found nearby, or slightly above the rocket
        BlockPos aboveRocket = this.blockPosition().above((int)Math.ceil(this.getBbHeight()));
        if (this.level().getBlockState(aboveRocket).isAir() && this.level().getBlockState(aboveRocket.above()).isAir()) {
            return new Vec3(aboveRocket.getX() + 0.5, aboveRocket.getY(), aboveRocket.getZ() + 0.5);
        }
        return super.getDismountLocationForPassenger(passenger);
    }
}