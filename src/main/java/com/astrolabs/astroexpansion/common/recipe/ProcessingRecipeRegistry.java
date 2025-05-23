package com.astrolabs.astroexpansion.common.recipe;

import com.astrolabs.astroexpansion.common.registry.AEItems;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProcessingRecipeRegistry {
    private static final List<ProcessingRecipe> RECIPES = new ArrayList<>();
    
    static {
        // Initialize default recipes
        registerDefaultRecipes();
    }
    
    private static void registerDefaultRecipes() {
        // Ore to ingot recipes
        register(new ProcessingRecipe(
            Ingredient.of(AEItems.RAW_TITANIUM.get()),
            new ItemStack(AEItems.TITANIUM_INGOT.get()),
            200, 20
        ));
        
        register(new ProcessingRecipe(
            Ingredient.of(AEItems.RAW_LITHIUM.get()),
            new ItemStack(AEItems.LITHIUM_INGOT.get()),
            200, 20
        ));
        
        register(new ProcessingRecipe(
            Ingredient.of(AEItems.RAW_URANIUM.get()),
            new ItemStack(AEItems.URANIUM_INGOT.get()),
            400, 40
        ));
        
        // Dust recipes
        register(new ProcessingRecipe(
            Ingredient.of(Items.IRON_INGOT),
            new ItemStack(AEItems.IRON_DUST.get()),
            100, 10
        ));
        
        register(new ProcessingRecipe(
            Ingredient.of(AEItems.TITANIUM_INGOT.get()),
            new ItemStack(AEItems.TITANIUM_DUST.get()),
            100, 10
        ));
        
        register(new ProcessingRecipe(
            Ingredient.of(Items.COAL),
            new ItemStack(AEItems.COAL_DUST.get()),
            80, 10
        ));
        
        // Steel recipe (iron + coal dust)
        register(new ProcessingRecipe(
            Ingredient.of(Items.IRON_INGOT),
            new ItemStack(AEItems.STEEL_INGOT.get()),
            300, 30
        ));
    }
    
    public static void register(ProcessingRecipe recipe) {
        RECIPES.add(recipe);
    }
    
    public static Optional<ProcessingRecipe> findRecipe(ItemStack input) {
        return RECIPES.stream()
                .filter(recipe -> recipe.matches(input))
                .findFirst();
    }
    
    public static List<ProcessingRecipe> getAllRecipes() {
        return new ArrayList<>(RECIPES);
    }
}