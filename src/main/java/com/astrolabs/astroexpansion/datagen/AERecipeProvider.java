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