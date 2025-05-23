package com.astrolabs.astroexpansion.datagen;

import com.astrolabs.astroexpansion.AstroExpansion;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = AstroExpansion.MODID, bus = EventBusSubscriber.Bus.MOD)
public class AEDataGenerators {
    
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        
        boolean includeServer = event.includeServer();
        boolean includeClient = event.includeClient();
        
        generator.addProvider(includeServer, new AERecipeProvider(packOutput, lookupProvider));
        generator.addProvider(includeServer, new AELootTableProvider(packOutput, lookupProvider));
        generator.addProvider(includeServer, new AEBlockTagProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(includeServer, new AEItemTagProvider(packOutput, lookupProvider, existingFileHelper));
        
        generator.addProvider(includeClient, new AEBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(includeClient, new AEItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(includeClient, new AELanguageProvider(packOutput, "en_us"));
    }
}