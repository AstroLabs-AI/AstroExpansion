package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.items.DroneItem;
import com.astrolabs.astroexpansion.common.items.LunarRoverItem;
import com.astrolabs.astroexpansion.common.items.AstroGuideItem;
import com.astrolabs.astroexpansion.common.items.SpaceSuitArmorItem;
import com.astrolabs.astroexpansion.common.items.StorageDriveItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, AstroExpansion.MODID);
    
    // Raw Ores
    public static final RegistryObject<Item> RAW_TITANIUM = ITEMS.register("raw_titanium",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_LITHIUM = ITEMS.register("raw_lithium",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_URANIUM = ITEMS.register("raw_uranium",
        () -> new Item(new Item.Properties()));
    
    // Ingots
    public static final RegistryObject<Item> TITANIUM_INGOT = ITEMS.register("titanium_ingot",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LITHIUM_INGOT = ITEMS.register("lithium_ingot",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> URANIUM_INGOT = ITEMS.register("uranium_ingot",
        () -> new Item(new Item.Properties()));
    
    // Dusts
    public static final RegistryObject<Item> TITANIUM_DUST = ITEMS.register("titanium_dust",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LITHIUM_DUST = ITEMS.register("lithium_dust",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> URANIUM_DUST = ITEMS.register("uranium_dust",
        () -> new Item(new Item.Properties()));
    
    // Components
    public static final RegistryObject<Item> CIRCUIT_BOARD = ITEMS.register("circuit_board",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROCESSOR = ITEMS.register("processor",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ENERGY_CORE = ITEMS.register("energy_core",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ADVANCED_PROCESSOR = ITEMS.register("advanced_processor",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> QUANTUM_PROCESSOR = ITEMS.register("quantum_processor",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TITANIUM_PLATE = ITEMS.register("titanium_plate",
        () -> new Item(new Item.Properties()));
    
    // Misc
    public static final RegistryObject<Item> WRENCH = ITEMS.register("wrench",
        () -> new Item(new Item.Properties().stacksTo(1)));
    
    // Storage Components
    public static final RegistryObject<Item> STORAGE_DRIVE_1K = ITEMS.register("storage_drive_1k",
        () -> new StorageDriveItem(new Item.Properties(), 1000, "1k"));
    public static final RegistryObject<Item> STORAGE_DRIVE_4K = ITEMS.register("storage_drive_4k",
        () -> new StorageDriveItem(new Item.Properties(), 4000, "4k"));
    public static final RegistryObject<Item> STORAGE_DRIVE_16K = ITEMS.register("storage_drive_16k",
        () -> new StorageDriveItem(new Item.Properties(), 16000, "16k"));
    public static final RegistryObject<Item> STORAGE_DRIVE_64K = ITEMS.register("storage_drive_64k",
        () -> new StorageDriveItem(new Item.Properties(), 64000, "64k"));
    
    public static final RegistryObject<Item> STORAGE_HOUSING = ITEMS.register("storage_housing",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STORAGE_PROCESSOR = ITEMS.register("storage_processor",
        () -> new Item(new Item.Properties()));
    
    // Drones
    public static final RegistryObject<Item> MINING_DRONE = ITEMS.register("mining_drone",
        () -> new DroneItem(new Item.Properties(), ModEntities.MINING_DRONE, "Mining"));
    public static final RegistryObject<Item> CONSTRUCTION_DRONE = ITEMS.register("construction_drone",
        () -> new DroneItem(new Item.Properties(), ModEntities.CONSTRUCTION_DRONE, "Construction"));
    public static final RegistryObject<Item> FARMING_DRONE = ITEMS.register("farming_drone",
        () -> new DroneItem(new Item.Properties(), ModEntities.FARMING_DRONE, "Farming"));
    public static final RegistryObject<Item> COMBAT_DRONE = ITEMS.register("combat_drone",
        () -> new DroneItem(new Item.Properties(), ModEntities.COMBAT_DRONE, "Combat"));
    public static final RegistryObject<Item> LOGISTICS_DRONE = ITEMS.register("logistics_drone",
        () -> new DroneItem(new Item.Properties(), ModEntities.LOGISTICS_DRONE, "Logistics"));
    public static final RegistryObject<Item> DRONE_CORE = ITEMS.register("drone_core",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DRONE_SHELL = ITEMS.register("drone_shell",
        () -> new Item(new Item.Properties()));
    
    // Fusion Reactor Components
    public static final RegistryObject<Item> FUSION_CORE = ITEMS.register("fusion_core",
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> PLASMA_INJECTOR = ITEMS.register("plasma_injector",
        () -> new Item(new Item.Properties()));
    
    // Fusion Fuels
    public static final RegistryObject<Item> DEUTERIUM_CELL = ITEMS.register("deuterium_cell",
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> TRITIUM_CELL = ITEMS.register("tritium_cell",
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> EMPTY_FUEL_CELL = ITEMS.register("empty_fuel_cell",
        () -> new Item(new Item.Properties()));
    
    // Research Items
    public static final RegistryObject<Item> RESEARCH_DATA_BASIC = ITEMS.register("research_data_basic",
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> RESEARCH_DATA_ADVANCED = ITEMS.register("research_data_advanced",
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> RESEARCH_DATA_QUANTUM = ITEMS.register("research_data_quantum",
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> RESEARCH_DATA_FUSION = ITEMS.register("research_data_fusion",
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> RESEARCH_DATA_SPACE = ITEMS.register("research_data_space",
        () -> new Item(new Item.Properties()));
    
    // Space Suit Armor
    public static final RegistryObject<Item> SPACE_HELMET = ITEMS.register("space_helmet",
        () -> new SpaceSuitArmorItem(ArmorItem.Type.HELMET, new Item.Properties()));
    
    public static final RegistryObject<Item> SPACE_CHESTPLATE = ITEMS.register("space_chestplate",
        () -> new SpaceSuitArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    
    public static final RegistryObject<Item> SPACE_LEGGINGS = ITEMS.register("space_leggings",
        () -> new SpaceSuitArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties()));
    
    public static final RegistryObject<Item> SPACE_BOOTS = ITEMS.register("space_boots",
        () -> new SpaceSuitArmorItem(ArmorItem.Type.BOOTS, new Item.Properties()));
    
    // Oxygen System
    public static final RegistryObject<Item> OXYGEN_TANK = ITEMS.register("oxygen_tank",
        () -> new Item(new Item.Properties().stacksTo(1)));
    
    public static final RegistryObject<Item> OXYGEN_CANISTER = ITEMS.register("oxygen_canister",
        () -> new Item(new Item.Properties().stacksTo(16)));
    
    // Vehicles
    public static final RegistryObject<Item> LUNAR_ROVER = ITEMS.register("lunar_rover",
        () -> new LunarRoverItem());
    
    // Lunar Resources
    public static final RegistryObject<Item> HELIUM3_CRYSTAL = ITEMS.register("helium3_crystal",
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> MOON_DUST_ITEM = ITEMS.register("moon_dust_item",
        () -> new Item(new Item.Properties()));
    
    // Guide Book
    public static final RegistryObject<Item> ASTRO_GUIDE = ITEMS.register("astro_guide",
        () -> new AstroGuideItem(new Item.Properties()));
    
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}