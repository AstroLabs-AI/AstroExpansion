package com.astrolabs.astroexpansion.common.world.structure;

import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import com.astrolabs.astroexpansion.common.registry.ModItems;

import java.util.Random;

public class SpaceStationStructure {
    
    public static void generateSpaceStation(ServerLevel level, BlockPos center, RandomSource random) {
        // Simple abandoned space station - a floating platform with some rooms
        generatePlatform(level, center, 15, 15);
        generateWalls(level, center, 15, 15, 5);
        generateRooms(level, center);
        placeLoot(level, center, random);
    }
    
    private static void generatePlatform(ServerLevel level, BlockPos center, int width, int depth) {
        BlockState stationHull = ModBlocks.STATION_HULL.get().defaultBlockState();
        
        for (int x = -width/2; x <= width/2; x++) {
            for (int z = -depth/2; z <= depth/2; z++) {
                BlockPos pos = center.offset(x, 0, z);
                level.setBlock(pos, stationHull, 3);
            }
        }
    }
    
    private static void generateWalls(ServerLevel level, BlockPos center, int width, int depth, int height) {
        BlockState stationHull = ModBlocks.STATION_HULL.get().defaultBlockState();
        BlockState stationGlass = ModBlocks.STATION_GLASS.get().defaultBlockState();
        
        for (int y = 1; y <= height; y++) {
            // North and South walls
            for (int x = -width/2; x <= width/2; x++) {
                BlockPos northPos = center.offset(x, y, -depth/2);
                BlockPos southPos = center.offset(x, y, depth/2);
                
                // Add windows
                if (y == 2 && x % 3 == 0) {
                    level.setBlock(northPos, stationGlass, 3);
                    level.setBlock(southPos, stationGlass, 3);
                } else {
                    level.setBlock(northPos, stationHull, 3);
                    level.setBlock(southPos, stationHull, 3);
                }
            }
            
            // East and West walls
            for (int z = -depth/2 + 1; z < depth/2; z++) {
                BlockPos eastPos = center.offset(width/2, y, z);
                BlockPos westPos = center.offset(-width/2, y, z);
                
                // Add windows
                if (y == 2 && z % 3 == 0) {
                    level.setBlock(eastPos, stationGlass, 3);
                    level.setBlock(westPos, stationGlass, 3);
                } else {
                    level.setBlock(eastPos, stationHull, 3);
                    level.setBlock(westPos, stationHull, 3);
                }
            }
        }
        
        // Ceiling
        for (int x = -width/2; x <= width/2; x++) {
            for (int z = -depth/2; z <= depth/2; z++) {
                BlockPos pos = center.offset(x, height + 1, z);
                level.setBlock(pos, stationHull, 3);
            }
        }
    }
    
    private static void generateRooms(ServerLevel level, BlockPos center) {
        // Central corridor
        for (int z = -5; z <= 5; z++) {
            for (int y = 1; y <= 3; y++) {
                level.setBlock(center.offset(0, y, z), Blocks.AIR.defaultBlockState(), 3);
            }
        }
        
        // Side rooms with doors
        // West room
        for (int x = -6; x <= -2; x++) {
            for (int z = -3; z <= 3; z++) {
                for (int y = 1; y <= 3; y++) {
                    level.setBlock(center.offset(x, y, z), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
        
        // East room
        for (int x = 2; x <= 6; x++) {
            for (int z = -3; z <= 3; z++) {
                for (int y = 1; y <= 3; y++) {
                    level.setBlock(center.offset(x, y, z), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
        
        // Add some broken blocks for atmosphere
        level.setBlock(center.offset(-3, 1, 2), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(center.offset(4, 3, -1), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(center.offset(-5, 2, 0), ModBlocks.STATION_GLASS.get().defaultBlockState(), 3);
    }
    
    private static void placeLoot(ServerLevel level, BlockPos center, RandomSource random) {
        // Place chests with loot
        BlockPos[] chestPositions = {
            center.offset(-4, 1, 0),
            center.offset(4, 1, 0),
            center.offset(0, 1, -4)
        };
        
        for (BlockPos pos : chestPositions) {
            if (random.nextFloat() < 0.7f) { // 70% chance for chest
                level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 3);
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof ChestBlockEntity chest) {
                    fillChestWithLoot(chest, random);
                }
            }
        }
        
        // Place some machinery
        if (random.nextFloat() < 0.5f) {
            level.setBlock(center.offset(-5, 1, -2), ModBlocks.POWER_GENERATOR_BASIC.get().defaultBlockState(), 3);
        }
        if (random.nextFloat() < 0.3f) {
            level.setBlock(center.offset(5, 1, 2), ModBlocks.STORAGE_TERMINAL.get().defaultBlockState(), 3);
        }
    }
    
    private static void fillChestWithLoot(ChestBlockEntity chest, RandomSource random) {
        // Add random space loot
        for (int i = 0; i < 3 + random.nextInt(5); i++) {
            ItemStack loot = getRandomLoot(random);
            chest.setItem(random.nextInt(27), loot);
        }
    }
    
    private static ItemStack getRandomLoot(RandomSource random) {
        return switch (random.nextInt(15)) {
            case 0 -> new ItemStack(ModItems.ALIEN_ARTIFACT.get());
            case 1 -> new ItemStack(ModItems.SPACE_STATION_KEY.get());
            case 2 -> new ItemStack(ModItems.METEOR_FRAGMENT.get(), 1 + random.nextInt(3));
            case 3 -> new ItemStack(ModItems.COSMIC_DUST.get(), 3 + random.nextInt(5));
            case 4 -> new ItemStack(ModItems.ADVANCED_PROCESSOR.get());
            case 5 -> new ItemStack(ModItems.QUANTUM_PROCESSOR.get());
            case 6 -> new ItemStack(ModItems.PLASMA_INJECTOR.get());
            case 7 -> new ItemStack(Items.DIAMOND, 1 + random.nextInt(3));
            case 8 -> new ItemStack(Items.EMERALD, 1 + random.nextInt(5));
            case 9 -> new ItemStack(Items.NETHERITE_SCRAP);
            case 10 -> new ItemStack(ModItems.TITANIUM_INGOT.get(), 2 + random.nextInt(8));
            case 11 -> new ItemStack(ModItems.ENERGY_CORE.get());
            case 12 -> new ItemStack(Items.ENDER_PEARL, 1 + random.nextInt(4));
            case 13 -> new ItemStack(ModItems.RESEARCH_DATA_SPACE.get());
            default -> new ItemStack(Items.IRON_INGOT, 5 + random.nextInt(10));
        };
    }
}