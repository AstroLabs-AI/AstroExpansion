package com.astrolabs.astroexpansion.datagen;

import com.astrolabs.astroexpansion.common.registry.AEBlocks;
import com.astrolabs.astroexpansion.common.registry.AEItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class AELootTableProvider extends LootTableProvider {
    
    public AELootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(AEBlockLoot::new, LootContextParamSets.BLOCK)
        ), lookupProvider);
    }
    
    private static class AEBlockLoot extends BlockLootSubProvider {
        protected AEBlockLoot(HolderLookup.Provider registries) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
        }
        
        @Override
        protected void generate() {
            // Ore drops
            add(AEBlocks.TITANIUM_ORE.get(), 
                    createOreDrop(AEBlocks.TITANIUM_ORE.get(), AEItems.RAW_TITANIUM.get()));
            add(AEBlocks.DEEPSLATE_TITANIUM_ORE.get(), 
                    createOreDrop(AEBlocks.DEEPSLATE_TITANIUM_ORE.get(), AEItems.RAW_TITANIUM.get()));
            add(AEBlocks.LITHIUM_ORE.get(), 
                    createOreDrop(AEBlocks.LITHIUM_ORE.get(), AEItems.RAW_LITHIUM.get()));
            add(AEBlocks.URANIUM_ORE.get(), 
                    createOreDrop(AEBlocks.URANIUM_ORE.get(), AEItems.RAW_URANIUM.get()));
            
            // Simple block drops
            dropSelf(AEBlocks.TITANIUM_BLOCK.get());
            dropSelf(AEBlocks.STEEL_BLOCK.get());
            dropSelf(AEBlocks.MATERIAL_PROCESSOR.get());
            dropSelf(AEBlocks.BASIC_GENERATOR.get());
        }
        
        @Override
        protected Iterable<Block> getKnownBlocks() {
            return AEBlocks.BLOCKS.getEntries().stream().map(e -> e.get()).toList();
        }
    }
}