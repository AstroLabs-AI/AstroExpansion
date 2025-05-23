package com.astrolabs.astroexpansion.datagen;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.registry.AEBlocks;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class AEBlockTagProvider extends BlockTagsProvider {
    
    public AEBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, AstroExpansion.MODID, existingFileHelper);
    }
    
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Add ores to mineable tags
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(AEBlocks.TITANIUM_ORE.get())
                .add(AEBlocks.DEEPSLATE_TITANIUM_ORE.get())
                .add(AEBlocks.LITHIUM_ORE.get())
                .add(AEBlocks.URANIUM_ORE.get())
                .add(AEBlocks.TITANIUM_BLOCK.get())
                .add(AEBlocks.STEEL_BLOCK.get())
                .add(AEBlocks.MATERIAL_PROCESSOR.get())
                .add(AEBlocks.BASIC_GENERATOR.get());
        
        // Add harvest level requirements
        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(AEBlocks.TITANIUM_ORE.get())
                .add(AEBlocks.DEEPSLATE_TITANIUM_ORE.get())
                .add(AEBlocks.LITHIUM_ORE.get())
                .add(AEBlocks.TITANIUM_BLOCK.get())
                .add(AEBlocks.STEEL_BLOCK.get());
        
        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(AEBlocks.URANIUM_ORE.get());
    }
}