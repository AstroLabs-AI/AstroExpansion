package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.recipes.ProcessingRecipe;
import com.astrolabs.astroexpansion.common.recipes.WashingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AstroExpansion.MODID);
    
    public static final RegistryObject<RecipeSerializer<ProcessingRecipe>> PROCESSING =
        RECIPE_SERIALIZERS.register("processing", ProcessingRecipe.Serializer::new);
    
    public static final RegistryObject<RecipeSerializer<WashingRecipe>> WASHING =
        RECIPE_SERIALIZERS.register("washing", WashingRecipe.Serializer::new);
    
    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
    }
}