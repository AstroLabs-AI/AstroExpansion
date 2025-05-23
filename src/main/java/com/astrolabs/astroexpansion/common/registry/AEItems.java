package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.astrolabs.astroexpansion.common.items.SpaceSuitArmorItem;

public class AEItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AstroExpansion.MODID);
    
    // Raw Materials
    public static final DeferredItem<Item> RAW_TITANIUM = ITEMS.register("raw_titanium",
            () -> new Item(new Item.Properties()));
    
    public static final DeferredItem<Item> RAW_LITHIUM = ITEMS.register("raw_lithium",
            () -> new Item(new Item.Properties()));
    
    public static final DeferredItem<Item> RAW_URANIUM = ITEMS.register("raw_uranium",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    
    // Ingots
    public static final DeferredItem<Item> TITANIUM_INGOT = ITEMS.register("titanium_ingot",
            () -> new Item(new Item.Properties()));
    
    public static final DeferredItem<Item> STEEL_INGOT = ITEMS.register("steel_ingot",
            () -> new Item(new Item.Properties()));
    
    public static final DeferredItem<Item> LITHIUM_INGOT = ITEMS.register("lithium_ingot",
            () -> new Item(new Item.Properties()));
    
    public static final DeferredItem<Item> URANIUM_INGOT = ITEMS.register("uranium_ingot",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    
    // Dusts
    public static final DeferredItem<Item> TITANIUM_DUST = ITEMS.register("titanium_dust",
            () -> new Item(new Item.Properties()));
    
    public static final DeferredItem<Item> IRON_DUST = ITEMS.register("iron_dust",
            () -> new Item(new Item.Properties()));
    
    public static final DeferredItem<Item> COAL_DUST = ITEMS.register("coal_dust",
            () -> new Item(new Item.Properties()));
    
    // Components
    public static final DeferredItem<Item> CIRCUIT_BOARD = ITEMS.register("circuit_board",
            () -> new Item(new Item.Properties()));
    
    public static final DeferredItem<Item> PROCESSOR = ITEMS.register("processor",
            () -> new Item(new Item.Properties()));
    
    public static final DeferredItem<Item> ADVANCED_PROCESSOR = ITEMS.register("advanced_processor",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    
    public static final DeferredItem<Item> ENERGY_CORE = ITEMS.register("energy_core",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    
    // Block Items
    public static final DeferredItem<BlockItem> TITANIUM_ORE_ITEM = ITEMS.register("titanium_ore",
            () -> new BlockItem(AEBlocks.TITANIUM_ORE.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> DEEPSLATE_TITANIUM_ORE_ITEM = ITEMS.register("deepslate_titanium_ore",
            () -> new BlockItem(AEBlocks.DEEPSLATE_TITANIUM_ORE.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> LITHIUM_ORE_ITEM = ITEMS.register("lithium_ore",
            () -> new BlockItem(AEBlocks.LITHIUM_ORE.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> URANIUM_ORE_ITEM = ITEMS.register("uranium_ore",
            () -> new BlockItem(AEBlocks.URANIUM_ORE.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> TITANIUM_BLOCK_ITEM = ITEMS.register("titanium_block",
            () -> new BlockItem(AEBlocks.TITANIUM_BLOCK.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> STEEL_BLOCK_ITEM = ITEMS.register("steel_block",
            () -> new BlockItem(AEBlocks.STEEL_BLOCK.get(), new Item.Properties()));
    
    // Machine Block Items
    public static final DeferredItem<BlockItem> MATERIAL_PROCESSOR_ITEM = ITEMS.register("material_processor",
            () -> new BlockItem(AEBlocks.MATERIAL_PROCESSOR.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> BASIC_GENERATOR_ITEM = ITEMS.register("basic_generator",
            () -> new BlockItem(AEBlocks.BASIC_GENERATOR.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> COMPONENT_ASSEMBLER_ITEM = ITEMS.register("component_assembler",
            () -> new BlockItem(AEBlocks.COMPONENT_ASSEMBLER.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> ENERGY_CONDUIT_ITEM = ITEMS.register("energy_conduit",
            () -> new BlockItem(AEBlocks.ENERGY_CONDUIT.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> RESEARCH_CONSOLE_ITEM = ITEMS.register("research_console",
            () -> new BlockItem(AEBlocks.RESEARCH_CONSOLE.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> SOLAR_PANEL_ITEM = ITEMS.register("solar_panel",
            () -> new BlockItem(AEBlocks.SOLAR_PANEL.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> ELECTRIC_FURNACE_ITEM = ITEMS.register("electric_furnace",
            () -> new BlockItem(AEBlocks.ELECTRIC_FURNACE.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> BATTERY_BANK_ITEM = ITEMS.register("battery_bank",
            () -> new BlockItem(AEBlocks.BATTERY_BANK.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> WIRELESS_ENERGY_NODE_ITEM = ITEMS.register("wireless_energy_node",
            () -> new BlockItem(AEBlocks.WIRELESS_ENERGY_NODE.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> TELEPORTER_ITEM = ITEMS.register("teleporter",
            () -> new BlockItem(AEBlocks.TELEPORTER.get(), new Item.Properties().rarity(Rarity.RARE)));
    
    public static final DeferredItem<BlockItem> FORCE_FIELD_GENERATOR_ITEM = ITEMS.register("force_field_generator",
            () -> new BlockItem(AEBlocks.FORCE_FIELD_GENERATOR.get(), new Item.Properties().rarity(Rarity.RARE)));
    
    public static final DeferredItem<BlockItem> OXYGEN_GENERATOR_ITEM = ITEMS.register("oxygen_generator",
            () -> new BlockItem(AEBlocks.OXYGEN_GENERATOR.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> ROCKET_ITEM = ITEMS.register("rocket",
            () -> new BlockItem(AEBlocks.ROCKET.get(), new Item.Properties().rarity(Rarity.EPIC)));
    
    // Space Suit Armor
    public static final DeferredItem<SpaceSuitArmorItem> SPACE_SUIT_HELMET = ITEMS.register("space_suit_helmet",
            () -> new SpaceSuitArmorItem(ArmorType.HELMET, new Item.Properties()
                    .humanoidArmor(AEArmorMaterials.SPACE_SUIT.get())
                    .component(AEDataComponentTypes.OXYGEN.get(), 0)));
    
    public static final DeferredItem<SpaceSuitArmorItem> SPACE_SUIT_CHESTPLATE = ITEMS.register("space_suit_chestplate",
            () -> new SpaceSuitArmorItem(ArmorType.CHESTPLATE, new Item.Properties()
                    .humanoidArmor(AEArmorMaterials.SPACE_SUIT.get())
                    .component(AEDataComponentTypes.OXYGEN.get(), 0)));
    
    public static final DeferredItem<SpaceSuitArmorItem> SPACE_SUIT_LEGGINGS = ITEMS.register("space_suit_leggings",
            () -> new SpaceSuitArmorItem(ArmorType.LEGGINGS, new Item.Properties()
                    .humanoidArmor(AEArmorMaterials.SPACE_SUIT.get())
                    .component(AEDataComponentTypes.OXYGEN.get(), 0)));
    
    public static final DeferredItem<SpaceSuitArmorItem> SPACE_SUIT_BOOTS = ITEMS.register("space_suit_boots",
            () -> new SpaceSuitArmorItem(ArmorType.BOOTS, new Item.Properties()
                    .humanoidArmor(AEArmorMaterials.SPACE_SUIT.get())
                    .component(AEDataComponentTypes.OXYGEN.get(), 0)));
}