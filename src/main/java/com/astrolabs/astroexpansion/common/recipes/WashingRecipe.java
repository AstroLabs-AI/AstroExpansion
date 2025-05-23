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

public class WashingRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient input;
    private final ItemStack output;
    private final ItemStack secondaryOutput;
    private final float secondaryChance;
    private final int processTime;
    private final int energyCost;
    
    public WashingRecipe(ResourceLocation id, Ingredient input, ItemStack output, ItemStack secondaryOutput, float secondaryChance, int processTime, int energyCost) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.secondaryOutput = secondaryOutput;
        this.secondaryChance = secondaryChance;
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
        return ModRecipeSerializers.WASHING.get();
    }
    
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.WASHING.get();
    }
    
    public Ingredient getInput() {
        return input;
    }
    
    public ItemStack getSecondaryOutput() {
        return secondaryOutput;
    }
    
    public float getSecondaryChance() {
        return secondaryChance;
    }
    
    public int getProcessTime() {
        return processTime;
    }
    
    public int getEnergyCost() {
        return energyCost;
    }
    
    public static class Serializer implements RecipeSerializer<WashingRecipe> {
        @Override
        public WashingRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            
            ItemStack secondaryOutput = ItemStack.EMPTY;
            float secondaryChance = 0.0f;
            
            if (json.has("secondary_output")) {
                JsonObject secondary = GsonHelper.getAsJsonObject(json, "secondary_output");
                secondaryOutput = ShapedRecipe.itemStackFromJson(secondary);
                secondaryChance = GsonHelper.getAsFloat(secondary, "chance", 0.1f);
            }
            
            int processTime = GsonHelper.getAsInt(json, "process_time", 100);
            int energyCost = GsonHelper.getAsInt(json, "energy_cost", 500);
            
            return new WashingRecipe(id, input, output, secondaryOutput, secondaryChance, processTime, energyCost);
        }
        
        @Override
        public WashingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            ItemStack output = buffer.readItem();
            ItemStack secondaryOutput = buffer.readItem();
            float secondaryChance = buffer.readFloat();
            int processTime = buffer.readVarInt();
            int energyCost = buffer.readVarInt();
            
            return new WashingRecipe(id, input, output, secondaryOutput, secondaryChance, processTime, energyCost);
        }
        
        @Override
        public void toNetwork(FriendlyByteBuf buffer, WashingRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeItem(recipe.output);
            buffer.writeItem(recipe.secondaryOutput);
            buffer.writeFloat(recipe.secondaryChance);
            buffer.writeVarInt(recipe.processTime);
            buffer.writeVarInt(recipe.energyCost);
        }
    }
}