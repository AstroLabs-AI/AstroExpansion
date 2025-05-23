package com.astrolabs.astroexpansion.common.recipe;

import com.astrolabs.astroexpansion.common.registry.AEItems;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AssemblyRecipeRegistry {
    private static final List<AssemblyRecipe> RECIPES = new ArrayList<>();
    
    static {
        registerDefaultRecipes();
    }
    
    private static void registerDefaultRecipes() {
        // Advanced Processor recipe
        Ingredient[] advancedProcessorIngredients = new Ingredient[9];
        advancedProcessorIngredients[0] = Ingredient.of(AEItems.PROCESSOR.get());
        advancedProcessorIngredients[1] = Ingredient.of(Items.DIAMOND);
        advancedProcessorIngredients[2] = Ingredient.of(AEItems.PROCESSOR.get());
        advancedProcessorIngredients[3] = Ingredient.of(Items.GOLD_INGOT);
        advancedProcessorIngredients[4] = Ingredient.of(AEItems.ENERGY_CORE.get());
        advancedProcessorIngredients[5] = Ingredient.of(Items.GOLD_INGOT);
        advancedProcessorIngredients[6] = Ingredient.of(AEItems.PROCESSOR.get());
        advancedProcessorIngredients[7] = Ingredient.of(Items.DIAMOND);
        advancedProcessorIngredients[8] = Ingredient.of(AEItems.PROCESSOR.get());
        
        register(new AssemblyRecipe(
            advancedProcessorIngredients,
            new ItemStack(AEItems.ADVANCED_PROCESSOR.get()),
            400, // 20 seconds
            10   // 10 FE/tick
        ));
        
        // Steel Ingot recipe (alternative method)
        Ingredient[] steelIngredients = new Ingredient[9];
        steelIngredients[4] = Ingredient.of(Items.IRON_INGOT); // Center
        steelIngredients[1] = Ingredient.of(AEItems.COAL_DUST.get()); // Top
        steelIngredients[3] = Ingredient.of(AEItems.COAL_DUST.get()); // Left
        steelIngredients[5] = Ingredient.of(AEItems.COAL_DUST.get()); // Right
        steelIngredients[7] = Ingredient.of(AEItems.COAL_DUST.get()); // Bottom
        
        register(new AssemblyRecipe(
            steelIngredients,
            new ItemStack(AEItems.STEEL_INGOT.get(), 2),
            200, // 10 seconds
            5    // 5 FE/tick
        ));
        
        // Energy Core recipe (automated version)
        Ingredient[] energyCoreIngredients = new Ingredient[9];
        energyCoreIngredients[0] = Ingredient.of(Items.REDSTONE_BLOCK);
        energyCoreIngredients[1] = Ingredient.of(Items.GLOWSTONE_DUST);
        energyCoreIngredients[2] = Ingredient.of(Items.REDSTONE_BLOCK);
        energyCoreIngredients[3] = Ingredient.of(Items.GLOWSTONE_DUST);
        energyCoreIngredients[4] = Ingredient.of(Items.DIAMOND);
        energyCoreIngredients[5] = Ingredient.of(Items.GLOWSTONE_DUST);
        energyCoreIngredients[6] = Ingredient.of(Items.REDSTONE_BLOCK);
        energyCoreIngredients[7] = Ingredient.of(Items.GLOWSTONE_DUST);
        energyCoreIngredients[8] = Ingredient.of(Items.REDSTONE_BLOCK);
        
        register(new AssemblyRecipe(
            energyCoreIngredients,
            new ItemStack(AEItems.ENERGY_CORE.get()),
            300, // 15 seconds
            15   // 15 FE/tick
        ));
    }
    
    public static void register(AssemblyRecipe recipe) {
        RECIPES.add(recipe);
    }
    
    public static Optional<AssemblyRecipe> findRecipe(Container container) {
        return RECIPES.stream()
                .filter(recipe -> recipe.matches(container))
                .findFirst();
    }
    
    public static List<AssemblyRecipe> getAllRecipes() {
        return new ArrayList<>(RECIPES);
    }
}