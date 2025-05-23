package com.astrolabs.astroexpansion.common.world.dimension;

import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MoonChunkGenerator extends ChunkGenerator {
    public static final Codec<MoonChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource),
            Codec.LONG.fieldOf("seed").stable().forGetter(gen -> gen.seed)
        ).apply(instance, MoonChunkGenerator::new)
    );
    
    private static final BlockState MOON_STONE = ModBlocks.MOON_STONE.get().defaultBlockState();
    private static final BlockState MOON_DUST = ModBlocks.MOON_DUST.get().defaultBlockState();
    private static final BlockState HELIUM3_ORE = ModBlocks.HELIUM3_ORE.get().defaultBlockState();
    private static final BlockState BEDROCK = Blocks.BEDROCK.defaultBlockState();
    
    private final long seed;
    private SimplexNoise surfaceNoise;
    private SimplexNoise craterNoise;
    
    public MoonChunkGenerator(BiomeSource biomeSource, long seed) {
        super(biomeSource);
        this.seed = seed;
    }
    
    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }
    
    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState random, ChunkAccess chunk) {
        if (surfaceNoise == null) {
            RandomSource randomSource = RandomSource.create(seed);
            surfaceNoise = new SimplexNoise(randomSource);
            craterNoise = new SimplexNoise(randomSource);
        }
        
        ChunkPos chunkPos = chunk.getPos();
        int chunkX = chunkPos.x;
        int chunkZ = chunkPos.z;
        
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkPos.getMinBlockX() + x;
                int worldZ = chunkPos.getMinBlockZ() + z;
                
                // Generate height using noise
                double heightNoise = surfaceNoise.getValue(worldX * 0.01, worldZ * 0.01);
                double craterValue = craterNoise.getValue(worldX * 0.05, worldZ * 0.05);
                
                // Base height around y=80 with variation
                int baseHeight = 80 + (int)(heightNoise * 20);
                
                // Create craters
                if (craterValue > 0.7) {
                    baseHeight -= (int)((craterValue - 0.7) * 50);
                }
                
                // Place moon dust on surface
                for (int y = baseHeight + 3; y > baseHeight; y--) {
                    chunk.setBlockState(new BlockPos(worldX, y, worldZ), MOON_DUST, false);
                }
                
                // Fill below with moon stone
                for (int y = baseHeight; y > 0; y--) {
                    BlockState state = MOON_STONE;
                    
                    // Add helium-3 ore rarely
                    if (y < 40 && region.getRandom().nextFloat() < 0.01f) {
                        state = HELIUM3_ORE;
                    }
                    
                    chunk.setBlockState(new BlockPos(worldX, y, worldZ), state, false);
                }
                
                // Bedrock at bottom
                chunk.setBlockState(new BlockPos(worldX, region.getMinBuildHeight(), worldZ), BEDROCK, false);
            }
        }
    }
    
    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState random, StructureManager structureManager, ChunkAccess chunk) {
        return CompletableFuture.supplyAsync(() -> chunk, executor);
    }
    
    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
        return 80; // Average moon surface height
    }
    
    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor height, RandomState random) {
        BlockState[] states = new BlockState[height.getHeight()];
        
        // Simple column generation
        for (int y = 0; y < 80 && y < states.length; y++) {
            states[y] = MOON_STONE;
        }
        
        return new NoiseColumn(height.getMinBuildHeight(), states);
    }
    
    @Override
    public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {
        info.add("Moon Dimension - Low Gravity Environment");
    }
    
    @Override
    public void applyCarvers(WorldGenRegion level, long seed, RandomState random, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk, GenerationStep.Carving step) {
        // No cave carvers on the moon
    }
    
    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
        // No mobs spawn naturally on the moon
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
        return -64; // No seas on the moon
    }
}