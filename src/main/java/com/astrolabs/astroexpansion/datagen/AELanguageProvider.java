package com.astrolabs.astroexpansion.datagen;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.registry.AEBlocks;
import com.astrolabs.astroexpansion.common.registry.AEItems;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class AELanguageProvider extends LanguageProvider {
    
    public AELanguageProvider(PackOutput output, String locale) {
        super(output, AstroExpansion.MODID, locale);
    }
    
    @Override
    protected void addTranslations() {
        // Creative tab
        add("itemGroup.astroexpansion.main", "Astro Expansion");
        
        // Blocks
        addBlock(AEBlocks.TITANIUM_ORE, "Titanium Ore");
        addBlock(AEBlocks.DEEPSLATE_TITANIUM_ORE, "Deepslate Titanium Ore");
        addBlock(AEBlocks.LITHIUM_ORE, "Lithium Ore");
        addBlock(AEBlocks.URANIUM_ORE, "Uranium Ore");
        addBlock(AEBlocks.TITANIUM_BLOCK, "Block of Titanium");
        addBlock(AEBlocks.STEEL_BLOCK, "Block of Steel");
        addBlock(AEBlocks.MATERIAL_PROCESSOR, "Material Processor");
        addBlock(AEBlocks.BASIC_GENERATOR, "Basic Generator");
        
        // Items
        addItem(AEItems.RAW_TITANIUM, "Raw Titanium");
        addItem(AEItems.RAW_LITHIUM, "Raw Lithium");
        addItem(AEItems.RAW_URANIUM, "Raw Uranium");
        addItem(AEItems.TITANIUM_INGOT, "Titanium Ingot");
        addItem(AEItems.STEEL_INGOT, "Steel Ingot");
        addItem(AEItems.LITHIUM_INGOT, "Lithium Ingot");
        addItem(AEItems.URANIUM_INGOT, "Uranium Ingot");
        addItem(AEItems.TITANIUM_DUST, "Titanium Dust");
        addItem(AEItems.IRON_DUST, "Iron Dust");
        addItem(AEItems.COAL_DUST, "Coal Dust");
        addItem(AEItems.CIRCUIT_BOARD, "Circuit Board");
        addItem(AEItems.PROCESSOR, "Processor");
        addItem(AEItems.ADVANCED_PROCESSOR, "Advanced Processor");
        addItem(AEItems.ENERGY_CORE, "Energy Core");
        
        // GUI
        add("gui.astroexpansion.energy", "Energy: %s FE");
        add("gui.astroexpansion.progress", "Progress: %s%%");
    }
}