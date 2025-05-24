package com.astrolabs.astroexpansion.common.data;

import com.astrolabs.astroexpansion.common.registry.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;

public class MatterRecipes {
    private static final Map<Item, Integer> UU_MATTER_COSTS = new HashMap<>();
    
    static {
        // Basic materials
        UU_MATTER_COSTS.put(Items.COBBLESTONE, 1);
        UU_MATTER_COSTS.put(Items.DIRT, 1);
        UU_MATTER_COSTS.put(Items.SAND, 1);
        UU_MATTER_COSTS.put(Items.GRAVEL, 1);
        
        // Common resources
        UU_MATTER_COSTS.put(Items.COAL, 5);
        UU_MATTER_COSTS.put(Items.CHARCOAL, 5);
        UU_MATTER_COSTS.put(Items.REDSTONE, 10);
        UU_MATTER_COSTS.put(Items.GLOWSTONE_DUST, 15);
        
        // Metals
        UU_MATTER_COSTS.put(Items.IRON_INGOT, 25);
        UU_MATTER_COSTS.put(Items.COPPER_INGOT, 20);
        UU_MATTER_COSTS.put(Items.GOLD_INGOT, 50);
        UU_MATTER_COSTS.put(Items.NETHERITE_SCRAP, 500);
        
        // Mod materials
        UU_MATTER_COSTS.put(ModItems.TITANIUM_INGOT.get(), 100);
        UU_MATTER_COSTS.put(ModItems.LITHIUM_INGOT.get(), 75);
        UU_MATTER_COSTS.put(ModItems.URANIUM_INGOT.get(), 200);
        
        // Gems
        UU_MATTER_COSTS.put(Items.DIAMOND, 250);
        UU_MATTER_COSTS.put(Items.EMERALD, 300);
        UU_MATTER_COSTS.put(Items.LAPIS_LAZULI, 20);
        UU_MATTER_COSTS.put(Items.QUARTZ, 15);
        
        // Special items
        UU_MATTER_COSTS.put(Items.ENDER_PEARL, 100);
        UU_MATTER_COSTS.put(Items.BLAZE_ROD, 150);
        UU_MATTER_COSTS.put(Items.NETHER_STAR, 5000);
        UU_MATTER_COSTS.put(Items.DRAGON_EGG, 10000);
        
        // Mod components
        UU_MATTER_COSTS.put(ModItems.CIRCUIT_BOARD.get(), 50);
        UU_MATTER_COSTS.put(ModItems.PROCESSOR.get(), 150);
        UU_MATTER_COSTS.put(ModItems.ADVANCED_PROCESSOR.get(), 500);
        UU_MATTER_COSTS.put(ModItems.QUANTUM_PROCESSOR.get(), 2000);
        
        // Rare materials
        UU_MATTER_COSTS.put(ModItems.FUSION_CORE.get(), 1000);
        UU_MATTER_COSTS.put(ModItems.HELIUM3_CRYSTAL.get(), 750);
        UU_MATTER_COSTS.put(ModItems.RARE_EARTH_INGOT.get(), 400);
        
        // Building blocks
        UU_MATTER_COSTS.put(Items.STONE, 2);
        UU_MATTER_COSTS.put(Items.GLASS, 3);
        UU_MATTER_COSTS.put(Items.OBSIDIAN, 50);
        UU_MATTER_COSTS.put(Items.CRYING_OBSIDIAN, 75);
        
        // Wood
        UU_MATTER_COSTS.put(Items.OAK_LOG, 8);
        UU_MATTER_COSTS.put(Items.SPRUCE_LOG, 8);
        UU_MATTER_COSTS.put(Items.BIRCH_LOG, 8);
        UU_MATTER_COSTS.put(Items.JUNGLE_LOG, 8);
        
        // Food
        UU_MATTER_COSTS.put(Items.BREAD, 10);
        UU_MATTER_COSTS.put(Items.GOLDEN_APPLE, 250);
        UU_MATTER_COSTS.put(Items.ENCHANTED_GOLDEN_APPLE, 2500);
    }
    
    public static int getUUMatterCost(Item item) {
        return UU_MATTER_COSTS.getOrDefault(item, 0);
    }
    
    public static boolean hasRecipe(Item item) {
        return UU_MATTER_COSTS.containsKey(item);
    }
    
    public static Map<Item, Integer> getAllRecipes() {
        return new HashMap<>(UU_MATTER_COSTS);
    }
}