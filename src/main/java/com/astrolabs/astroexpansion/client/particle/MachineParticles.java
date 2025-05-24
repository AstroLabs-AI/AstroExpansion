package com.astrolabs.astroexpansion.client.particle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class MachineParticles {
    
    /**
     * Spawn smoke particles for active machines
     */
    public static void spawnSmokeParticles(Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.5f) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
            double y = pos.getY() + 1.1;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
            
            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0.05, 0);
        }
    }
    
    /**
     * Spawn energy particles for generators
     */
    public static void spawnEnergyParticles(Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.3f) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double y = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, x, y, z, 
                (random.nextDouble() - 0.5) * 0.1, 
                random.nextDouble() * 0.1, 
                (random.nextDouble() - 0.5) * 0.1);
        }
    }
    
    /**
     * Spawn processing particles for material processor
     */
    public static void spawnProcessingParticles(Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.4f) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.6;
            double z = pos.getZ() + 0.5;
            
            level.addParticle(ParticleTypes.CRIT, x, y, z,
                (random.nextDouble() - 0.5) * 0.2,
                random.nextDouble() * 0.1,
                (random.nextDouble() - 0.5) * 0.2);
        }
    }
    
    /**
     * Spawn water particles for ore washer
     */
    public static void spawnWashingParticles(Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.6f) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
            double y = pos.getY() + 0.7;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
            
            level.addParticle(ParticleTypes.SPLASH, x, y, z, 0, -0.1, 0);
        }
        
        // Occasional bubble
        if (random.nextFloat() < 0.2f) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
            
            level.addParticle(ParticleTypes.BUBBLE, x, y, z, 0, 0.1, 0);
        }
    }
    
    /**
     * Spawn crafting particles for component assembler
     */
    public static void spawnCraftingParticles(Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.3f) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5;
            
            level.addParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, 0, 0, 0);
        }
    }
    
    /**
     * Spawn flame particles for furnaces
     */
    public static void spawnFurnaceParticles(Level level, BlockPos pos, Direction facing, RandomSource random) {
        if (random.nextFloat() < 0.7f) {
            double x = pos.getX() + 0.5 + facing.getStepX() * 0.52;
            double y = pos.getY() + random.nextDouble() * 0.6;
            double z = pos.getZ() + 0.5 + facing.getStepZ() * 0.52;
            
            level.addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
            level.addParticle(ParticleTypes.SMOKE, x, y + 0.5, z, 0, 0.05, 0);
        }
    }
    
    /**
     * Spawn fusion particles for reactor
     */
    public static void spawnFusionParticles(Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.8f) {
            // Create circular pattern
            float angle = random.nextFloat() * (float) Math.PI * 2;
            float radius = 2.5f;
            
            double x = pos.getX() + 2.5 + Math.cos(angle) * radius;
            double y = pos.getY() + 2.5 + (random.nextDouble() - 0.5) * 4;
            double z = pos.getZ() + 2.5 + Math.sin(angle) * radius;
            
            // Spiral inward
            double vx = -Math.cos(angle) * 0.1;
            double vy = (random.nextDouble() - 0.5) * 0.05;
            double vz = -Math.sin(angle) * 0.1;
            
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, vx, vy, vz);
            
            // Energy sparks
            if (random.nextFloat() < 0.3f) {
                level.addParticle(ParticleTypes.ELECTRIC_SPARK, 
                    pos.getX() + 2.5 + (random.nextDouble() - 0.5) * 3,
                    pos.getY() + 2.5 + (random.nextDouble() - 0.5) * 3,
                    pos.getZ() + 2.5 + (random.nextDouble() - 0.5) * 3,
                    0, 0, 0);
            }
        }
    }
    
    /**
     * Spawn quantum particles for quantum computer
     */
    public static void spawnQuantumParticles(Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.5f) {
            // Ender particles in cube pattern
            double x = pos.getX() + 3.5 + (random.nextDouble() - 0.5) * 6;
            double y = pos.getY() + 3.5 + (random.nextDouble() - 0.5) * 6;
            double z = pos.getZ() + 3.5 + (random.nextDouble() - 0.5) * 6;
            
            level.addParticle(ParticleTypes.PORTAL, x, y, z,
                (random.nextDouble() - 0.5) * 0.5,
                (random.nextDouble() - 0.5) * 0.5,
                (random.nextDouble() - 0.5) * 0.5);
        }
        
        // Data streams
        if (random.nextFloat() < 0.2f) {
            double x = pos.getX() + 3.5;
            double y = pos.getY() + random.nextDouble() * 7;
            double z = pos.getZ() + 3.5;
            
            level.addParticle(ParticleTypes.ENCHANT, x, y, z, 
                (random.nextDouble() - 0.5) * 3,
                0.5,
                (random.nextDouble() - 0.5) * 3);
        }
    }
    
    /**
     * Spawn storage particles for storage core
     */
    public static void spawnStorageParticles(Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.2f) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
            
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0.05, 0);
        }
    }
    
    /**
     * Spawn drone charging particles
     */
    public static void spawnDroneChargingParticles(Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.4f) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.5;
            double z = pos.getZ() + 0.5;
            
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = 0.5;
            
            level.addParticle(ParticleTypes.ELECTRIC_SPARK,
                x + Math.cos(angle) * radius,
                y,
                z + Math.sin(angle) * radius,
                -Math.cos(angle) * 0.05,
                -0.1,
                -Math.sin(angle) * 0.05);
        }
    }
}