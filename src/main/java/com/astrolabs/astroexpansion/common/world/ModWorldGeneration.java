package com.astrolabs.astroexpansion.common.world;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class ModWorldGeneration {
    public static final ResourceKey<ConfiguredFeature<?, ?>> TITANIUM_ORE_KEY = registerKey("titanium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LITHIUM_ORE_KEY = registerKey("lithium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> URANIUM_ORE_KEY = registerKey("uranium_ore");
    
    public static final ResourceKey<PlacedFeature> TITANIUM_ORE_PLACED_KEY = registerPlacedKey("titanium_ore_placed");
    public static final ResourceKey<PlacedFeature> LITHIUM_ORE_PLACED_KEY = registerPlacedKey("lithium_ore_placed");
    public static final ResourceKey<PlacedFeature> URANIUM_ORE_PLACED_KEY = registerPlacedKey("uranium_ore_placed");
    
    public static void generateModWorldGen() {
        // World generation is now handled through data generation
    }
    
    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        
        List<OreConfiguration.TargetBlockState> titaniumOres = List.of(
            OreConfiguration.target(stoneReplaceables, ModBlocks.TITANIUM_ORE.get().defaultBlockState()),
            OreConfiguration.target(deepslateReplaceables, ModBlocks.DEEPSLATE_TITANIUM_ORE.get().defaultBlockState())
        );
        
        List<OreConfiguration.TargetBlockState> lithiumOres = List.of(
            OreConfiguration.target(stoneReplaceables, ModBlocks.LITHIUM_ORE.get().defaultBlockState()),
            OreConfiguration.target(deepslateReplaceables, ModBlocks.DEEPSLATE_LITHIUM_ORE.get().defaultBlockState())
        );
        
        List<OreConfiguration.TargetBlockState> uraniumOres = List.of(
            OreConfiguration.target(stoneReplaceables, ModBlocks.URANIUM_ORE.get().defaultBlockState()),
            OreConfiguration.target(deepslateReplaceables, ModBlocks.DEEPSLATE_URANIUM_ORE.get().defaultBlockState())
        );
        
        register(context, TITANIUM_ORE_KEY, Feature.ORE, new OreConfiguration(titaniumOres, 9));
        register(context, LITHIUM_ORE_KEY, Feature.ORE, new OreConfiguration(lithiumOres, 7));
        register(context, URANIUM_ORE_KEY, Feature.ORE, new OreConfiguration(uraniumOres, 4));
    }
    
    public static void bootstrapPlacements(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        
        register(context, TITANIUM_ORE_PLACED_KEY, configuredFeatures.getOrThrow(TITANIUM_ORE_KEY),
            commonOrePlacement(12, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
        
        register(context, LITHIUM_ORE_PLACED_KEY, configuredFeatures.getOrThrow(LITHIUM_ORE_KEY),
            commonOrePlacement(10, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(0))));
        
        register(context, URANIUM_ORE_PLACED_KEY, configuredFeatures.getOrThrow(URANIUM_ORE_KEY),
            rareOrePlacement(5, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(-32))));
    }
    
    private static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(AstroExpansion.MODID, name));
    }
    
    private static ResourceKey<PlacedFeature> registerPlacedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(AstroExpansion.MODID, name));
    }
    
    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                                                                         ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
    
    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
                                Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
    
    private static List<PlacementModifier> orePlacement(PlacementModifier countModifier, PlacementModifier heightModifier) {
        return List.of(countModifier, InSquarePlacement.spread(), heightModifier, BiomeFilter.biome());
    }
    
    private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier heightModifier) {
        return orePlacement(CountPlacement.of(count), heightModifier);
    }
    
    private static List<PlacementModifier> rareOrePlacement(int count, PlacementModifier heightModifier) {
        return orePlacement(RarityFilter.onAverageOnceEvery(count), heightModifier);
    }
}