package com.astrolabs.astroexpansion.datagen;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.registry.AEBlocks;
import com.astrolabs.astroexpansion.common.registry.AEItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

public class AERecipeProvider extends RecipeProvider {
    
    public AERecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }
    
    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // Smelting recipes for ores
        oreSmelting(recipeOutput, AEItems.RAW_TITANIUM.get(), AEItems.TITANIUM_INGOT.get(), 0.7f, 200);
        oreSmelting(recipeOutput, AEItems.RAW_LITHIUM.get(), AEItems.LITHIUM_INGOT.get(), 0.7f, 200);
        oreSmelting(recipeOutput, AEItems.RAW_URANIUM.get(), AEItems.URANIUM_INGOT.get(), 1.0f, 200);
        
        // Blasting recipes (faster)
        oreBlasting(recipeOutput, AEItems.RAW_TITANIUM.get(), AEItems.TITANIUM_INGOT.get(), 0.7f, 100);
        oreBlasting(recipeOutput, AEItems.RAW_LITHIUM.get(), AEItems.LITHIUM_INGOT.get(), 0.7f, 100);
        oreBlasting(recipeOutput, AEItems.RAW_URANIUM.get(), AEItems.URANIUM_INGOT.get(), 1.0f, 100);
        
        // Block recipes
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, AEItems.TITANIUM_INGOT.get(), 
                RecipeCategory.BUILDING_BLOCKS, AEBlocks.TITANIUM_BLOCK.get());
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, AEItems.STEEL_INGOT.get(), 
                RecipeCategory.BUILDING_BLOCKS, AEBlocks.STEEL_BLOCK.get());
        
        // Machine recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AEBlocks.MATERIAL_PROCESSOR.get())
                .pattern("IRI")
                .pattern("PCP")
                .pattern("IRI")
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE)
                .define('P', AEItems.PROCESSOR.get())
                .define('C', AEItems.CIRCUIT_BOARD.get())
                .unlockedBy(getHasName(AEItems.CIRCUIT_BOARD.get()), has(AEItems.CIRCUIT_BOARD.get()))
                .save(recipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AEBlocks.BASIC_GENERATOR.get())
                .pattern("IRI")
                .pattern("RFR")
                .pattern("ICI")
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE)
                .define('F', Items.FURNACE)
                .define('C', AEItems.CIRCUIT_BOARD.get())
                .unlockedBy(getHasName(AEItems.CIRCUIT_BOARD.get()), has(AEItems.CIRCUIT_BOARD.get()))
                .save(recipeOutput);
        
        // Component recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AEItems.CIRCUIT_BOARD.get())
                .pattern("GGG")
                .pattern("RIR")
                .pattern("GGG")
                .define('G', Items.GREEN_DYE)
                .define('R', Items.REDSTONE)
                .define('I', Items.IRON_INGOT)
                .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
                .save(recipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AEItems.PROCESSOR.get())
                .pattern("RRR")
                .pattern("RDR")
                .pattern("RCR")
                .define('R', Items.REDSTONE)
                .define('D', Items.DIAMOND)
                .define('C', AEItems.CIRCUIT_BOARD.get())
                .unlockedBy(getHasName(AEItems.CIRCUIT_BOARD.get()), has(AEItems.CIRCUIT_BOARD.get()))
                .save(recipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AEItems.ENERGY_CORE.get())
                .pattern("RGR")
                .pattern("GDG")
                .pattern("RGR")
                .define('R', Items.REDSTONE_BLOCK)
                .define('G', Items.GLOWSTONE_DUST)
                .define('D', Items.DIAMOND)
                .unlockedBy(getHasName(Items.DIAMOND), has(Items.DIAMOND))
                .save(recipeOutput);
        
        // Phase 2 machine recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AEBlocks.BATTERY_BANK.get())
                .pattern("TET")
                .pattern("ECE")
                .pattern("TET")
                .define('T', AEItems.TITANIUM_INGOT.get())
                .define('E', AEItems.ENERGY_CORE.get())
                .define('C', AEItems.CIRCUIT_BOARD.get())
                .unlockedBy(getHasName(AEItems.ENERGY_CORE.get()), has(AEItems.ENERGY_CORE.get()))
                .save(recipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AEBlocks.FORCE_FIELD_GENERATOR.get())
                .pattern("DPD")
                .pattern("PEP")
                .pattern("TBT")
                .define('D', Items.DIAMOND_BLOCK)
                .define('P', AEItems.ADVANCED_PROCESSOR.get())
                .define('E', AEItems.ENERGY_CORE.get())
                .define('T', AEItems.TITANIUM_INGOT.get())
                .define('B', AEBlocks.BATTERY_BANK.get())
                .unlockedBy(getHasName(AEItems.ADVANCED_PROCESSOR.get()), has(AEItems.ADVANCED_PROCESSOR.get()))
                .save(recipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AEBlocks.OXYGEN_GENERATOR.get())
                .pattern("ISI")
                .pattern("GPG")
                .pattern("ICI")
                .define('I', Items.IRON_INGOT)
                .define('S', Items.SUGAR_CANE)
                .define('G', Items.GLASS_PANE)
                .define('P', AEItems.PROCESSOR.get())
                .define('C', AEItems.CIRCUIT_BOARD.get())
                .unlockedBy(getHasName(AEItems.PROCESSOR.get()), has(AEItems.PROCESSOR.get()))
                .save(recipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AEBlocks.TELEPORTER.get())
                .pattern("EPE")
                .pattern("DUD")
                .pattern("TBT")
                .define('E', Items.ENDER_PEARL)
                .define('P', AEItems.ADVANCED_PROCESSOR.get())
                .define('D', Items.DIAMOND_BLOCK)
                .define('U', AEItems.URANIUM_INGOT.get())
                .define('T', AEItems.TITANIUM_INGOT.get())
                .define('B', AEBlocks.BATTERY_BANK.get())
                .unlockedBy(getHasName(AEItems.URANIUM_INGOT.get()), has(AEItems.URANIUM_INGOT.get()))
                .save(recipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AEBlocks.WIRELESS_ENERGY_NODE.get())
                .pattern("TRT")
                .pattern("RER")
                .pattern("TRT")
                .define('T', AEItems.TITANIUM_INGOT.get())
                .define('R', Items.REDSTONE_BLOCK)
                .define('E', AEItems.ENERGY_CORE.get())
                .unlockedBy(getHasName(AEItems.ENERGY_CORE.get()), has(AEItems.ENERGY_CORE.get()))
                .save(recipeOutput);
        
        // Advanced Processor recipe
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AEItems.ADVANCED_PROCESSOR.get())
                .pattern("GPG")
                .pattern("PDP")
                .pattern("GPG")
                .define('G', Items.GOLD_INGOT)
                .define('P', AEItems.PROCESSOR.get())
                .define('D', Items.DIAMOND)
                .unlockedBy(getHasName(AEItems.PROCESSOR.get()), has(AEItems.PROCESSOR.get()))
                .save(recipeOutput);
        
        // Space Suit Armor recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AEItems.SPACE_SUIT_HELMET.get())
                .pattern("TTT")
                .pattern("TGT")
                .pattern("   ")
                .define('T', AEItems.TITANIUM_INGOT.get())
                .define('G', Items.GLASS)
                .unlockedBy(getHasName(AEItems.TITANIUM_INGOT.get()), has(AEItems.TITANIUM_INGOT.get()))
                .save(recipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AEItems.SPACE_SUIT_CHESTPLATE.get())
                .pattern("T T")
                .pattern("TET")
                .pattern("TCT")
                .define('T', AEItems.TITANIUM_INGOT.get())
                .define('E', AEItems.ENERGY_CORE.get())
                .define('C', AEItems.CIRCUIT_BOARD.get())
                .unlockedBy(getHasName(AEItems.TITANIUM_INGOT.get()), has(AEItems.TITANIUM_INGOT.get()))
                .save(recipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AEItems.SPACE_SUIT_LEGGINGS.get())
                .pattern("TTT")
                .pattern("T T")
                .pattern("T T")
                .define('T', AEItems.TITANIUM_INGOT.get())
                .unlockedBy(getHasName(AEItems.TITANIUM_INGOT.get()), has(AEItems.TITANIUM_INGOT.get()))
                .save(recipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AEItems.SPACE_SUIT_BOOTS.get())
                .pattern("   ")
                .pattern("T T")
                .pattern("T T")
                .define('T', AEItems.TITANIUM_INGOT.get())
                .unlockedBy(getHasName(AEItems.TITANIUM_INGOT.get()), has(AEItems.TITANIUM_INGOT.get()))
                .save(recipeOutput);
    }
    
    protected void oreSmelting(RecipeOutput recipeOutput, ItemLike input, ItemLike output, float experience, int cookingTime) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.MISC, output, experience, cookingTime)
                .unlockedBy(getHasName(input), has(input))
                .save(recipeOutput, getItemName(output) + "_from_smelting");
    }
    
    protected void oreBlasting(RecipeOutput recipeOutput, ItemLike input, ItemLike output, float experience, int cookingTime) {
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(input), RecipeCategory.MISC, output, experience, cookingTime)
                .unlockedBy(getHasName(input), has(input))
                .save(recipeOutput, getItemName(output) + "_from_blasting");
    }
}