package com.astrolabs.astroexpansion.common.entities;

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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.Random;

/**
 * Unmanned probe that launches, explores space, and returns with loot
 */
public class ProbeRocketEntity extends Entity {
    private static final EntityDataAccessor<Boolean> LAUNCHED = SynchedEntityData.defineId(ProbeRocketEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LAUNCH_TIME = SynchedEntityData.defineId(ProbeRocketEntity.class, EntityDataSerializers.INT);
    
    private static final int FLIGHT_DURATION = 1200; // 60 seconds
    private ItemStack rocketItem = ItemStack.EMPTY;
    
    public ProbeRocketEntity(EntityType<?> type, Level level) {
        super(type, level);
    }
    
    public ProbeRocketEntity(Level level, double x, double y, double z) {
        this(ModEntities.PROBE_ROCKET.get(), level);
        this.setPos(x, y, z);
    }
    
    @Override
    protected void defineSynchedData() {
        this.entityData.define(LAUNCHED, false);
        this.entityData.define(LAUNCH_TIME, 0);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (isLaunched()) {
            int time = entityData.get(LAUNCH_TIME);
            
            if (time < FLIGHT_DURATION) {
                // Ascending phase
                if (time < 100) {
                    this.setDeltaMovement(0, 0.5 + (time * 0.01), 0);
                    
                    // Particle effects
                    if (level().isClientSide) {
                        for (int i = 0; i < 10; i++) {
                            level().addParticle(ParticleTypes.FLAME,
                                getX() + (random.nextDouble() - 0.5) * 0.5,
                                getY() - 0.5,
                                getZ() + (random.nextDouble() - 0.5) * 0.5,
                                0, -0.2, 0);
                            level().addParticle(ParticleTypes.SMOKE,
                                getX() + (random.nextDouble() - 0.5) * 0.5,
                                getY() - 1,
                                getZ() + (random.nextDouble() - 0.5) * 0.5,
                                0, -0.1, 0);
                        }
                    }
                }
                
                // Increment time
                entityData.set(LAUNCH_TIME, time + 1);
                
                // Disappear after ascending
                if (time == 100) {
                    this.setInvisible(true);
                    this.setDeltaMovement(Vec3.ZERO);
                }
                
            } else {
                // Return phase - spawn loot
                if (!level().isClientSide) {
                    spawnLoot();
                    this.discard();
                }
            }
        }
    }
    
    public void launch() {
        if (!isLaunched()) {
            entityData.set(LAUNCHED, true);
            entityData.set(LAUNCH_TIME, 0);
            
            // Sound effect
            level().playSound(null, blockPosition(), SoundEvents.FIREWORK_ROCKET_LAUNCH, 
                SoundSource.NEUTRAL, 3.0F, 0.7F);
        }
    }
    
    private void spawnLoot() {
        if (level() instanceof ServerLevel serverLevel) {
            Random rand = new Random();
            BlockPos pos = blockPosition();
            
            // Always return probe location data
            ItemStack locationData = new ItemStack(Items.FILLED_MAP);
            locationData.setHoverName(net.minecraft.network.chat.Component.literal("Space Station Coordinates"));
            dropItem(locationData, pos);
            
            // 30% chance for alien artifact
            if (rand.nextFloat() < 0.3f) {
                ItemStack artifact = new ItemStack(ModItems.ALIEN_ARTIFACT.get());
                dropItem(artifact, pos);
            }
            
            // 10% chance for space station key
            if (rand.nextFloat() < 0.1f) {
                ItemStack key = new ItemStack(ModItems.SPACE_STATION_KEY.get());
                dropItem(key, pos);
            }
            
            // Random space materials
            int lootCount = rand.nextInt(3) + 2;
            for (int i = 0; i < lootCount; i++) {
                ItemStack loot = getRandomSpaceLoot(rand);
                dropItem(loot, pos);
            }
            
            // Notify nearby players
            serverLevel.players().forEach(player -> {
                if (player.blockPosition().closerThan(pos, 100)) {
                    player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("Probe has returned with discoveries!"),
                        false
                    );
                }
            });
        }
    }
    
    private ItemStack getRandomSpaceLoot(Random rand) {
        return switch (rand.nextInt(8)) {
            case 0 -> new ItemStack(ModItems.METEOR_FRAGMENT.get(), rand.nextInt(3) + 1);
            case 1 -> new ItemStack(ModItems.COSMIC_DUST.get(), rand.nextInt(5) + 3);
            case 2 -> new ItemStack(ModItems.MOON_DUST_ITEM.get(), rand.nextInt(4) + 2);
            case 3 -> new ItemStack(ModItems.HELIUM3_CRYSTAL.get(), rand.nextInt(2) + 1);
            case 4 -> new ItemStack(Items.IRON_INGOT, rand.nextInt(10) + 5);
            case 5 -> new ItemStack(Items.GOLD_INGOT, rand.nextInt(5) + 2);
            case 6 -> new ItemStack(Items.REDSTONE, rand.nextInt(16) + 8);
            default -> new ItemStack(Items.GLOWSTONE_DUST, rand.nextInt(8) + 4);
        };
    }
    
    private void dropItem(ItemStack stack, BlockPos pos) {
        ItemEntity item = new ItemEntity(level(), pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, stack);
        item.setDeltaMovement(
            (random.nextDouble() - 0.5) * 0.2,
            0.2,
            (random.nextDouble() - 0.5) * 0.2
        );
        level().addFreshEntity(item);
    }
    
    public boolean isLaunched() {
        return entityData.get(LAUNCHED);
    }
    
    public void setItem(ItemStack stack) {
        this.rocketItem = stack.copy();
    }
    
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(LAUNCHED, tag.getBoolean("Launched"));
        entityData.set(LAUNCH_TIME, tag.getInt("LaunchTime"));
        rocketItem = ItemStack.of(tag.getCompound("RocketItem"));
    }
    
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("Launched", isLaunched());
        tag.putInt("LaunchTime", entityData.get(LAUNCH_TIME));
        tag.put("RocketItem", rocketItem.save(new CompoundTag()));
    }
    
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
    
    @Override
    public boolean isPickable() {
        return !isLaunched();
    }
}