package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class ModDimensions {
    public static final ResourceKey<Level> SPACE_KEY = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation(AstroExpansion.MODID, "space"));
    
    public static final ResourceKey<Level> MOON_KEY = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation(AstroExpansion.MODID, "moon"));
    
    public static final ResourceKey<DimensionType> SPACE_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
            new ResourceLocation(AstroExpansion.MODID, "space"));
    
    public static final ResourceKey<DimensionType> MOON_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
            new ResourceLocation(AstroExpansion.MODID, "moon"));
}