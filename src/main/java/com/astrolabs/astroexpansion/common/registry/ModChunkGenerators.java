package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.world.dimension.MoonChunkGenerator;
import com.astrolabs.astroexpansion.common.world.dimension.SpaceChunkGenerator;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModChunkGenerators {
    public static final DeferredRegister<Codec<? extends ChunkGenerator>> CHUNK_GENERATORS =
        DeferredRegister.create(Registries.CHUNK_GENERATOR, AstroExpansion.MODID);
    
    public static final RegistryObject<Codec<SpaceChunkGenerator>> SPACE_CHUNK_GENERATOR =
        CHUNK_GENERATORS.register("space", () -> SpaceChunkGenerator.CODEC);
    
    public static final RegistryObject<Codec<MoonChunkGenerator>> MOON_CHUNK_GENERATOR =
        CHUNK_GENERATORS.register("moon", () -> MoonChunkGenerator.CODEC);
    
    public static void register(IEventBus eventBus) {
        CHUNK_GENERATORS.register(eventBus);
    }
}