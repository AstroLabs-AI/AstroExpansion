package com.astrolabs.astroexpansion.common.world;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.registry.AEBlocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.world.level.levelgen.VerticalAnchor;

import java.util.List;

public class AEOreGeneration {
    
    public static final ResourceKey<ConfiguredFeature<?, ?>> TITANIUM_ORE_KEY = registerKey("titanium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LITHIUM_ORE_KEY = registerKey("lithium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> URANIUM_ORE_KEY = registerKey("uranium_ore");
    
    public static final ResourceKey<PlacedFeature> TITANIUM_ORE_PLACED_KEY = registerPlacedKey("titanium_ore_placed");
    public static final ResourceKey<PlacedFeature> LITHIUM_ORE_PLACED_KEY = registerPlacedKey("lithium_ore_placed");
    public static final ResourceKey<PlacedFeature> URANIUM_ORE_PLACED_KEY = registerPlacedKey("uranium_ore_placed");
    
    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        
        List<OreConfiguration.TargetBlockState> titaniumTargets = List.of(
            OreConfiguration.target(stoneReplaceables, AEBlocks.TITANIUM_ORE.get().defaultBlockState()),
            OreConfiguration.target(deepslateReplaceables, AEBlocks.DEEPSLATE_TITANIUM_ORE.get().defaultBlockState())
        );
        
        register(context, TITANIUM_ORE_KEY, Feature.ORE, new OreConfiguration(titaniumTargets, 8));
        register(context, LITHIUM_ORE_KEY, Feature.ORE, new OreConfiguration(stoneReplaceables, 
                AEBlocks.LITHIUM_ORE.get().defaultBlockState(), 6));
        register(context, URANIUM_ORE_KEY, Feature.ORE, new OreConfiguration(stoneReplaceables, 
                AEBlocks.URANIUM_ORE.get().defaultBlockState(), 4));
    }
    
    public static void placedBootstrap(BootstrapContext<PlacedFeature> context) {
        var configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        
        register(context, TITANIUM_ORE_PLACED_KEY, configuredFeatures.getOrThrow(TITANIUM_ORE_KEY),
                commonOrePlacement(10, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));
        
        register(context, LITHIUM_ORE_PLACED_KEY, configuredFeatures.getOrThrow(LITHIUM_ORE_KEY),
                commonOrePlacement(8, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(64))));
        
        register(context, URANIUM_ORE_PLACED_KEY, configuredFeatures.getOrThrow(URANIUM_ORE_KEY),
                rareOrePlacement(4, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(0))));
    }
    
    private static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(AstroExpansion.MODID, name));
    }
    
    private static ResourceKey<PlacedFeature> registerPlacedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(AstroExpansion.MODID, name));
    }
    
    private static <FC extends OreConfiguration> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                            ResourceKey<ConfiguredFeature<?, ?>> key, Feature<FC> feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
    
    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
                                ConfiguredFeature<?, ?> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
    
    private static List<PlacementModifier> orePlacement(PlacementModifier count, PlacementModifier height) {
        return List.of(count, InSquarePlacement.spread(), height, BiomeFilter.biome());
    }
    
    private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier height) {
        return orePlacement(CountPlacement.of(count), height);
    }
    
    private static List<PlacementModifier> rareOrePlacement(int count, PlacementModifier height) {
        return orePlacement(CountPlacement.of(count), height);
    }
}