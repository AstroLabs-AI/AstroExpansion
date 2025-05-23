package com.astrolabs.astroexpansion.common.entities;

import com.astrolabs.astroexpansion.common.entities.drones.AbstractDroneEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class CombatDroneEntity extends AbstractDroneEntity {
    private static final int ATTACK_RANGE = 16;
    private static final int ENERGY_PER_ATTACK = 75;
    private static final float ATTACK_DAMAGE = 6.0F;
    
    private LivingEntity currentTarget = null;
    
    public CombatDroneEntity(EntityType<? extends AbstractDroneEntity> type, Level level) {
        super(type, level, 9, 25000); // 9 slots for ammo, 25k energy
    }
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new CombatGoal());
        // Add return to dock goal later if needed
        
        // Target selector - attack hostile mobs
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Monster.class, true));
    }
    
    @Override
    public void setTarget(LivingEntity target) {
        this.currentTarget = target;
    }
    
    @Override
    public LivingEntity getTarget() {
        return currentTarget;
    }
    
    class CombatGoal extends Goal {
        private int attackCooldown = 0;
        
        @Override
        public boolean canUse() {
            return CombatDroneEntity.this.energyStorage.getEnergyStored() >= ENERGY_PER_ATTACK && currentTarget != null && currentTarget.isAlive();
        }
        
        @Override
        public boolean canContinueToUse() {
            return canUse() && distanceToSqr(currentTarget) < ATTACK_RANGE * ATTACK_RANGE;
        }
        
        @Override
        public void stop() {
            currentTarget = null;
            getNavigation().stop();
        }
        
        @Override
        public void tick() {
            if (currentTarget == null || !currentTarget.isAlive()) {
                return;
            }
            
            // Look at target
            getLookControl().setLookAt(currentTarget, 30.0F, 30.0F);
            
            double distance = distanceToSqr(currentTarget);
            
            if (distance < ATTACK_RANGE * ATTACK_RANGE) {
                // Stop moving when in range
                getNavigation().stop();
                
                if (attackCooldown > 0) {
                    attackCooldown--;
                } else {
                    // Attack with laser
                    performAttack();
                    attackCooldown = 20; // 1 second cooldown
                }
            } else {
                // Move towards target
                getNavigation().moveTo(currentTarget, 1.2);
            }
        }
        
        private void performAttack() {
            if (!level().isClientSide) {
                // Create laser projectile effect
                Vec3 start = position().add(0, 0.5, 0);
                Vec3 end = currentTarget.position().add(0, currentTarget.getBbHeight() * 0.5, 0);
                Vec3 direction = end.subtract(start).normalize();
                
                // Spawn projectile
                SmallFireball fireball = new SmallFireball(level(), 
                    getX(), getY() + 0.5, getZ(), 
                    direction.x * 0.1, direction.y * 0.1, direction.z * 0.1);
                fireball.setOwner(CombatDroneEntity.this);
                level().addFreshEntity(fireball);
                
                // Direct damage as backup
                currentTarget.hurt(damageSources().mobAttack(CombatDroneEntity.this), ATTACK_DAMAGE);
                
                CombatDroneEntity.this.energyStorage.extractEnergy(ENERGY_PER_ATTACK, false);
            }
        }
    }
    
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (super.hurt(source, amount)) {
            if (source.getEntity() instanceof LivingEntity attacker) {
                // Set attacker as target
                this.currentTarget = attacker;
            }
            return true;
        }
        return false;
    }
    
    @Override
    protected ItemStack getAsItemStack() {
        ItemStack stack = new ItemStack(com.astrolabs.astroexpansion.common.registry.ModItems.COMBAT_DRONE.get());
        // Store drone data in item NBT if needed
        return stack;
    }
    
    @Override
    protected void performTask() {
        // Handled by goal system
    }
    
    @Override
    protected int getEnergyConsumptionPerSecond() {
        return 15; // Higher energy consumption for combat
    }
    
    @Override
    protected int getTaskCooldown() {
        return 10; // 0.5 seconds between attack checks
    }
}