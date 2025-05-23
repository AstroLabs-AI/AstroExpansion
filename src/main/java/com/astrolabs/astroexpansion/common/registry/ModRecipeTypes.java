package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.recipes.ProcessingRecipe;
import com.astrolabs.astroexpansion.common.recipes.WashingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
        DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, AstroExpansion.MODID);
    
    public static final RegistryObject<RecipeType<ProcessingRecipe>> PROCESSING =
        RECIPE_TYPES.register("processing", () -> new RecipeType<ProcessingRecipe>() {
            @Override
            public String toString() {
                return "processing";
            }
        });
    
    public static final RegistryObject<RecipeType<WashingRecipe>> WASHING =
        RECIPE_TYPES.register("washing", () -> new RecipeType<WashingRecipe>() {
            @Override
            public String toString() {
                return "washing";
            }
        });
    
    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
    }
}