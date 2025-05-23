package com.astrolabs.astroexpansion.common.recipes;

import com.astrolabs.astroexpansion.common.registry.ModRecipeSerializers;
import com.astrolabs.astroexpansion.common.registry.ModRecipeTypes;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class ProcessingRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient input;
    private final ItemStack output;
    private final int processTime;
    private final int energyCost;
    
    public ProcessingRecipe(ResourceLocation id, Ingredient input, ItemStack output, int processTime, int energyCost) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.processTime = processTime;
        this.energyCost = energyCost;
    }
    
    @Override
    public boolean matches(Container container, Level level) {
        return input.test(container.getItem(0));
    }
    
    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return output.copy();
    }
    
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }
    
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }
    
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(input);
        return ingredients;
    }
    
    @Override
    public ResourceLocation getId() {
        return id;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.PROCESSING.get();
    }
    
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.PROCESSING.get();
    }
    
    public Ingredient getInput() {
        return input;
    }
    
    public int getProcessTime() {
        return processTime;
    }
    
    public int getEnergyCost() {
        return energyCost;
    }
    
    public static class Serializer implements RecipeSerializer<ProcessingRecipe> {
        @Override
        public ProcessingRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            int processTime = GsonHelper.getAsInt(json, "process_time", 200);
            int energyCost = GsonHelper.getAsInt(json, "energy_cost", 1000);
            
            return new ProcessingRecipe(id, input, output, processTime, energyCost);
        }
        
        @Override
        public ProcessingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            ItemStack output = buffer.readItem();
            int processTime = buffer.readVarInt();
            int energyCost = buffer.readVarInt();
            
            return new ProcessingRecipe(id, input, output, processTime, energyCost);
        }
        
        @Override
        public void toNetwork(FriendlyByteBuf buffer, ProcessingRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeItem(recipe.output);
            buffer.writeVarInt(recipe.processTime);
            buffer.writeVarInt(recipe.energyCost);
        }
    }
}