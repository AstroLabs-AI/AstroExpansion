package com.astrolabs.astroexpansion.common.world.dimension;

import com.astrolabs.astroexpansion.common.world.structure.SpaceStationStructure;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SpaceChunkGenerator extends ChunkGenerator {
    public static final Codec<SpaceChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource)
        ).apply(instance, SpaceChunkGenerator::new)
    );
    
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    
    public SpaceChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }
    
    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }
    
    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState random, ChunkAccess chunk) {
        // Space is empty - no surface to build
    }
    
    public void applyBiomeDecoration(WorldGenRegion level, ChunkAccess chunk, StructureManager structureManager) {
        // Generate space stations rarely
        ChunkPos chunkPos = chunk.getPos();
        
        // Only generate at specific coordinates for predictability
        if (chunkPos.x % 32 == 0 && chunkPos.z % 32 == 0) {
            // 10% chance for a space station at valid coordinates
            if (level.getRandom().nextFloat() < 0.1f) {
                BlockPos stationPos = new BlockPos(chunkPos.getMiddleBlockX(), 100, chunkPos.getMiddleBlockZ());
                // Space stations will be generated separately, not during chunk generation
                // to avoid the WorldGenRegion vs ServerLevel issue
            }
        }
    }
    
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState random, StructureManager structureManager, ChunkAccess chunk) {
        // Fill with air - space is empty
        return CompletableFuture.supplyAsync(() -> {
            ChunkPos chunkPos = chunk.getPos();
            int minY = chunk.getMinBuildHeight();
            int maxY = chunk.getMaxBuildHeight();
            
            // Clear the entire chunk
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = minY; y < maxY; y++) {
                        chunk.setBlockState(new BlockPos(chunkPos.getMinBlockX() + x, y, chunkPos.getMinBlockZ() + z), AIR, false);
                    }
                }
            }
            
            return chunk;
        }, executor);
    }
    
    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
        // No ground in space
        return level.getMinBuildHeight();
    }
    
    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor height, RandomState random) {
        // Return empty column
        return new NoiseColumn(height.getMinBuildHeight(), new BlockState[0]);
    }
    
    @Override
    public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {
        info.add("Space Dimension - The Final Frontier");
    }
    
    @Override
    public void applyCarvers(WorldGenRegion level, long seed, RandomState random, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk, GenerationStep.Carving step) {
        // No carvers in space
    }
    
    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
        // No mobs spawn naturally in space
    }
    
    @Override
    public int getMinY() {
        return -64;
    }
    
    @Override
    public int getGenDepth() {
        return 384;
    }
    
    @Override
    public int getSeaLevel() {
        return -64; // No sea in space
    }
}