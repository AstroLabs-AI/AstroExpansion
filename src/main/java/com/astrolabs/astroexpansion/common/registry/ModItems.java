package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.items.DroneItem;
import com.astrolabs.astroexpansion.common.items.LunarRoverItem;
import com.astrolabs.astroexpansion.common.items.AstroGuideItem;
import com.astrolabs.astroexpansion.common.items.SpaceSuitArmorItem;
import com.astrolabs.astroexpansion.common.items.StorageDriveItem;
import com.astrolabs.astroexpansion.common.items.TeleporterFrequencyCardItem;
import com.astrolabs.astroexpansion.common.items.WrenchItem;
import com.astrolabs.astroexpansion.common.items.MultimeterItem;
import com.astrolabs.astroexpansion.common.items.upgrades.SpeedUpgrade;
import com.astrolabs.astroexpansion.common.items.upgrades.EfficiencyUpgrade;
import com.astrolabs.astroexpansion.common.items.upgrades.FortuneUpgrade;
import com.astrolabs.astroexpansion.common.items.rockets.ProbeRocketItem;
import com.astrolabs.astroexpansion.common.items.rockets.PersonalRocketItem;
import com.astrolabs.astroexpansion.common.items.rockets.CargoShuttleItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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
        () -> new WrenchItem(new Item.Properties()));
    public static final RegistryObject<Item> SCRAP = ITEMS.register("scrap",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MULTIMETER = ITEMS.register("multimeter",
        () -> new MultimeterItem(new Item.Properties()));
    
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
    
    // Rocket Components
    public static final RegistryObject<Item> ROCKET_COMPUTER = ITEMS.register("rocket_computer",
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> ROCKET_FUEL = ITEMS.register("rocket_fuel",
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> FILLED_FUEL_CELL = ITEMS.register("filled_fuel_cell",
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
    
    public static final RegistryObject<Item> OXYGEN_MASK = ITEMS.register("oxygen_mask",
        () -> new Item(new Item.Properties().stacksTo(1)));
    
    // Thermal Padding
    public static final RegistryObject<Item> THERMAL_PADDING_HELMET = ITEMS.register("thermal_padding_helmet",
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> THERMAL_PADDING_CHESTPLATE = ITEMS.register("thermal_padding_chestplate",
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> THERMAL_PADDING_LEGGINGS = ITEMS.register("thermal_padding_leggings",
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> THERMAL_PADDING_BOOTS = ITEMS.register("thermal_padding_boots",
        () -> new Item(new Item.Properties()));
    
    // Vehicles
    public static final RegistryObject<Item> LUNAR_ROVER = ITEMS.register("lunar_rover",
        () -> new LunarRoverItem());
    
    // New Simplified Rockets
    public static final RegistryObject<Item> PROBE_ROCKET = ITEMS.register("probe_rocket",
        () -> new ProbeRocketItem(new Item.Properties()));
    
    public static final RegistryObject<Item> PERSONAL_ROCKET = ITEMS.register("personal_rocket",
        () -> new PersonalRocketItem(new Item.Properties()));
    
    public static final RegistryObject<Item> CARGO_SHUTTLE = ITEMS.register("cargo_shuttle",
        () -> new CargoShuttleItem(new Item.Properties()));
    
    // Space Loot Items
    public static final RegistryObject<Item> ALIEN_ARTIFACT = ITEMS.register("alien_artifact",
        () -> new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));
    
    public static final RegistryObject<Item> METEOR_FRAGMENT = ITEMS.register("meteor_fragment",
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> COSMIC_DUST = ITEMS.register("cosmic_dust",
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> SPACE_STATION_KEY = ITEMS.register("space_station_key",
        () -> new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
    
    // Lunar Resources
    public static final RegistryObject<Item> HELIUM3_CRYSTAL = ITEMS.register("helium3_crystal",
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> MOON_DUST_ITEM = ITEMS.register("moon_dust_item",
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> HELIUM3_CANISTER = ITEMS.register("helium3_canister",
        () -> new Item(new Item.Properties()));
    
    // Rare Resources
    public static final RegistryObject<Item> RARE_EARTH_DUST = ITEMS.register("rare_earth_dust",
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> RARE_EARTH_INGOT = ITEMS.register("rare_earth_ingot",
        () -> new Item(new Item.Properties()));
    
    // Lunar Resources
    public static final RegistryObject<Item> RAW_LUNAR_IRON = ITEMS.register("raw_lunar_iron",
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> LUNAR_IRON_INGOT = ITEMS.register("lunar_iron_ingot",
        () -> new Item(new Item.Properties()));
    
    // Guide Book
    public static final RegistryObject<Item> ASTRO_GUIDE = ITEMS.register("astro_guide",
        () -> new AstroGuideItem(new Item.Properties()));
    
    // Machine Upgrades
    public static final RegistryObject<Item> SPEED_UPGRADE_1 = ITEMS.register("speed_upgrade_1",
        () -> new SpeedUpgrade(new Item.Properties(), 1));
    public static final RegistryObject<Item> SPEED_UPGRADE_2 = ITEMS.register("speed_upgrade_2",
        () -> new SpeedUpgrade(new Item.Properties(), 2));
    public static final RegistryObject<Item> SPEED_UPGRADE_3 = ITEMS.register("speed_upgrade_3",
        () -> new SpeedUpgrade(new Item.Properties(), 3));
    
    public static final RegistryObject<Item> EFFICIENCY_UPGRADE_1 = ITEMS.register("efficiency_upgrade_1",
        () -> new EfficiencyUpgrade(new Item.Properties(), 1));
    public static final RegistryObject<Item> EFFICIENCY_UPGRADE_2 = ITEMS.register("efficiency_upgrade_2",
        () -> new EfficiencyUpgrade(new Item.Properties(), 2));
    public static final RegistryObject<Item> EFFICIENCY_UPGRADE_3 = ITEMS.register("efficiency_upgrade_3",
        () -> new EfficiencyUpgrade(new Item.Properties(), 3));
    
    public static final RegistryObject<Item> FORTUNE_UPGRADE_1 = ITEMS.register("fortune_upgrade_1",
        () -> new FortuneUpgrade(new Item.Properties(), 1));
    public static final RegistryObject<Item> FORTUNE_UPGRADE_2 = ITEMS.register("fortune_upgrade_2",
        () -> new FortuneUpgrade(new Item.Properties(), 2));
    public static final RegistryObject<Item> FORTUNE_UPGRADE_3 = ITEMS.register("fortune_upgrade_3",
        () -> new FortuneUpgrade(new Item.Properties(), 3));
    
    // UU-Matter
    public static final RegistryObject<Item> UU_MATTER_BUCKET = ITEMS.register("uu_matter_bucket",
        () -> new BucketItem(ModFluids.UU_MATTER, new Item.Properties()
            .craftRemainder(Items.BUCKET)
            .stacksTo(1)));
    
    // Advanced Teleporter
    public static final RegistryObject<Item> TELEPORTER_FREQUENCY_CARD = ITEMS.register("teleporter_frequency_card",
        () -> new TeleporterFrequencyCardItem());
    
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}