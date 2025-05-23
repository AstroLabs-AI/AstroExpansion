package com.astrolabs.astroexpansion.datagen;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.registry.AEItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class AEItemTagProvider extends ItemTagsProvider {
    
    public AEItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CompletableFuture.completedFuture(null), AstroExpansion.MODID, existingFileHelper);
    }
    
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Add ingots to forge tags
        tag(Tags.Items.INGOTS)
                .add(AEItems.TITANIUM_INGOT.get())
                .add(AEItems.STEEL_INGOT.get())
                .add(AEItems.LITHIUM_INGOT.get())
                .add(AEItems.URANIUM_INGOT.get());
        
        // Add raw materials to forge tags
        tag(Tags.Items.RAW_MATERIALS)
                .add(AEItems.RAW_TITANIUM.get())
                .add(AEItems.RAW_LITHIUM.get())
                .add(AEItems.RAW_URANIUM.get());
        
        // Add dusts to forge tags
        tag(Tags.Items.DUSTS)
                .add(AEItems.TITANIUM_DUST.get())
                .add(AEItems.IRON_DUST.get())
                .add(AEItems.COAL_DUST.get());
    }
}