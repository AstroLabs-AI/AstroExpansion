package com.astrolabs.astroexpansion.common.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class ProcessingRecipe {
    private final Ingredient input;
    private final ItemStack output;
    private final int processTime;
    private final int energyCost;
    
    public ProcessingRecipe(Ingredient input, ItemStack output, int processTime, int energyCost) {
        this.input = input;
        this.output = output;
        this.processTime = processTime;
        this.energyCost = energyCost;
    }
    
    public boolean matches(ItemStack stack) {
        return input.test(stack);
    }
    
    public Ingredient getInput() {
        return input;
    }
    
    public ItemStack getOutput() {
        return output.copy();
    }
    
    public int getProcessTime() {
        return processTime;
    }
    
    public int getEnergyCost() {
        return energyCost;
    }
}