package com.astrolabs.astroexpansion.common.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class AssemblyRecipe {
    private final Ingredient[] ingredients = new Ingredient[9]; // 3x3 grid
    private final ItemStack output;
    private final int processTime;
    private final int energyPerTick;
    
    public AssemblyRecipe(Ingredient[] ingredients, ItemStack output, int processTime, int energyPerTick) {
        System.arraycopy(ingredients, 0, this.ingredients, 0, Math.min(ingredients.length, 9));
        this.output = output;
        this.processTime = processTime;
        this.energyPerTick = energyPerTick;
    }
    
    public boolean matches(Container container) {
        for (int i = 0; i < 9; i++) {
            ItemStack stackInSlot = container.getItem(i);
            Ingredient ingredient = ingredients[i];
            
            if (ingredient == null || ingredient == Ingredient.EMPTY) {
                if (!stackInSlot.isEmpty()) return false;
            } else {
                if (!ingredient.test(stackInSlot)) return false;
            }
        }
        return true;
    }
    
    public Ingredient getIngredient(int index) {
        return index < ingredients.length ? ingredients[index] : Ingredient.EMPTY;
    }
    
    public ItemStack getOutput() {
        return output.copy();
    }
    
    public int getProcessTime() {
        return processTime;
    }
    
    public int getEnergyPerTick() {
        return energyPerTick;
    }
}