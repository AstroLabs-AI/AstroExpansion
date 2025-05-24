package com.astrolabs.astroexpansion.common.recipe;

import com.astrolabs.astroexpansion.common.registry.ModRecipeSerializers;
import com.astrolabs.astroexpansion.common.registry.ModRecipeTypes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.Nullable;

public class RocketWorkbenchRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;
    
    public RocketWorkbenchRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItems) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
    }
    
    @Override
    public boolean matches(SimpleContainer container, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        
        // Check if the pattern matches
        for (int i = 0; i < 9; i++) {
            Ingredient ingredient = i < recipeItems.size() ? recipeItems.get(i) : Ingredient.EMPTY;
            if (!ingredient.test(container.getItem(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return output.copy();
    }
    
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }
    
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }
    
    @Override
    public ResourceLocation getId() {
        return id;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.ROCKET_WORKBENCH.get();
    }
    
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.ROCKET_WORKBENCH.get();
    }
    
    public static class Serializer implements RecipeSerializer<RocketWorkbenchRecipe> {
        @Override
        public RocketWorkbenchRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            ItemStack output = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "output"), true);
            
            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(9, Ingredient.EMPTY);
            
            for (int i = 0; i < ingredients.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }
            
            return new RocketWorkbenchRecipe(recipeId, output, inputs);
        }
        
        @Override
        public @Nullable RocketWorkbenchRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(9, Ingredient.EMPTY);
            
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buffer));
            }
            
            ItemStack output = buffer.readItem();
            return new RocketWorkbenchRecipe(recipeId, output, inputs);
        }
        
        @Override
        public void toNetwork(FriendlyByteBuf buffer, RocketWorkbenchRecipe recipe) {
            for (Ingredient ingredient : recipe.recipeItems) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeItem(recipe.output);
        }
    }
}