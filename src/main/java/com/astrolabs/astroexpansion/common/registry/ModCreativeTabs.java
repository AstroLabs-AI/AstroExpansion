package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AstroExpansion.MODID);
    
    public static final RegistryObject<CreativeModeTab> ASTRO_TAB = CREATIVE_MODE_TABS.register("astro_tab",
        () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.TITANIUM_INGOT.get()))
            .title(Component.translatable("creativetab.astroexpansion"))
            .displayItems((params, output) -> {
                // Ores
                output.accept(ModBlocks.TITANIUM_ORE.get());
                output.accept(ModBlocks.DEEPSLATE_TITANIUM_ORE.get());
                output.accept(ModBlocks.LITHIUM_ORE.get());
                output.accept(ModBlocks.DEEPSLATE_LITHIUM_ORE.get());
                output.accept(ModBlocks.URANIUM_ORE.get());
                output.accept(ModBlocks.DEEPSLATE_URANIUM_ORE.get());
                
                // Raw Materials
                output.accept(ModItems.RAW_TITANIUM.get());
                output.accept(ModItems.RAW_LITHIUM.get());
                output.accept(ModItems.RAW_URANIUM.get());
                
                // Ingots
                output.accept(ModItems.TITANIUM_INGOT.get());
                output.accept(ModItems.LITHIUM_INGOT.get());
                output.accept(ModItems.URANIUM_INGOT.get());
                
                // Dusts
                output.accept(ModItems.TITANIUM_DUST.get());
                output.accept(ModItems.LITHIUM_DUST.get());
                output.accept(ModItems.URANIUM_DUST.get());
                
                // Blocks
                output.accept(ModBlocks.TITANIUM_BLOCK.get());
                output.accept(ModBlocks.LITHIUM_BLOCK.get());
                output.accept(ModBlocks.URANIUM_BLOCK.get());
                
                // Components
                output.accept(ModItems.CIRCUIT_BOARD.get());
                output.accept(ModItems.PROCESSOR.get());
                output.accept(ModItems.ENERGY_CORE.get());
                output.accept(ModItems.ADVANCED_PROCESSOR.get());
                output.accept(ModItems.TITANIUM_PLATE.get());
                output.accept(ModItems.SCRAP.get());
                
                // Machines (Legacy)
                output.accept(ModBlocks.BASIC_GENERATOR.get());
                output.accept(ModBlocks.MATERIAL_PROCESSOR.get());
                output.accept(ModBlocks.ORE_WASHER.get());
                output.accept(ModBlocks.RECYCLER.get());
                output.accept(ModBlocks.ENERGY_CONDUIT.get());
                output.accept(ModBlocks.ENERGY_STORAGE.get());
                
                // Tiered Machines
                output.accept(ModBlocks.POWER_GENERATOR_BASIC.get());
                output.accept(ModBlocks.POWER_GENERATOR_ADVANCED.get());
                output.accept(ModBlocks.POWER_GENERATOR_ELITE.get());
                
                output.accept(ModBlocks.MATERIAL_PROCESSOR_BASIC.get());
                output.accept(ModBlocks.MATERIAL_PROCESSOR_ADVANCED.get());
                output.accept(ModBlocks.MATERIAL_PROCESSOR_ELITE.get());
                
                output.accept(ModBlocks.ORE_WASHER_BASIC.get());
                output.accept(ModBlocks.ORE_WASHER_ADVANCED.get());
                output.accept(ModBlocks.ORE_WASHER_ELITE.get());
                
                // Storage System
                output.accept(ModBlocks.STORAGE_CORE.get());
                output.accept(ModBlocks.STORAGE_TERMINAL.get());
                output.accept(ModItems.STORAGE_DRIVE_1K.get());
                output.accept(ModItems.STORAGE_DRIVE_4K.get());
                output.accept(ModItems.STORAGE_DRIVE_16K.get());
                output.accept(ModItems.STORAGE_DRIVE_64K.get());
                output.accept(ModItems.STORAGE_HOUSING.get());
                output.accept(ModItems.STORAGE_PROCESSOR.get());
                
                // Automation
                output.accept(ModBlocks.IMPORT_BUS.get());
                output.accept(ModBlocks.EXPORT_BUS.get());
                output.accept(ModBlocks.COMPONENT_ASSEMBLER.get());
                
                // Multiblocks
                output.accept(ModBlocks.INDUSTRIAL_FURNACE_CONTROLLER.get());
                output.accept(ModBlocks.FURNACE_CASING.get());
                output.accept(ModBlocks.FUSION_REACTOR_CONTROLLER.get());
                output.accept(ModBlocks.FUSION_REACTOR_CASING.get());
                
                // Fluid System
                output.accept(ModBlocks.FLUID_TANK.get());
                output.accept(ModBlocks.FLUID_PIPE.get());
                
                // Fusion Components
                output.accept(ModItems.FUSION_CORE.get());
                output.accept(ModItems.PLASMA_INJECTOR.get());
                
                // Drones
                output.accept(ModBlocks.DRONE_DOCK.get());
                output.accept(ModItems.MINING_DRONE.get());
                output.accept(ModItems.CONSTRUCTION_DRONE.get());
                output.accept(ModItems.FARMING_DRONE.get());
                output.accept(ModItems.COMBAT_DRONE.get());
                output.accept(ModItems.LOGISTICS_DRONE.get());
                output.accept(ModItems.DRONE_CORE.get());
                output.accept(ModItems.DRONE_SHELL.get());
                
                // Tools
                output.accept(ModItems.WRENCH.get());
                output.accept(ModItems.ASTRO_GUIDE.get());
                
                // Space Items
                output.accept(ModItems.SPACE_HELMET.get());
                output.accept(ModItems.SPACE_CHESTPLATE.get());
                output.accept(ModItems.SPACE_LEGGINGS.get());
                output.accept(ModItems.SPACE_BOOTS.get());
                output.accept(ModItems.OXYGEN_TANK.get());
                output.accept(ModItems.OXYGEN_CANISTER.get());
                
                // Research Data
                output.accept(ModItems.RESEARCH_DATA_BASIC.get());
                output.accept(ModItems.RESEARCH_DATA_ADVANCED.get());
                output.accept(ModItems.RESEARCH_DATA_QUANTUM.get());
                output.accept(ModItems.RESEARCH_DATA_FUSION.get());
                output.accept(ModItems.RESEARCH_DATA_SPACE.get());
                
                // Fuel Cells
                output.accept(ModItems.EMPTY_FUEL_CELL.get());
                output.accept(ModItems.DEUTERIUM_CELL.get());
                output.accept(ModItems.TRITIUM_CELL.get());
                
                // Rocket Parts
                output.accept(ModBlocks.ROCKET_ASSEMBLY_CONTROLLER.get());
                output.accept(ModBlocks.LAUNCH_PAD.get());
                output.accept(ModBlocks.ASSEMBLY_FRAME.get());
                output.accept(ModBlocks.ROCKET_ENGINE.get());
                output.accept(ModBlocks.ROCKET_FUEL_TANK.get());
                output.accept(ModBlocks.ROCKET_HULL.get());
                output.accept(ModBlocks.ROCKET_NOSE_CONE.get());
                
                // Space Station
                output.accept(ModBlocks.STATION_HULL.get());
                output.accept(ModBlocks.STATION_GLASS.get());
                output.accept(ModBlocks.DOCKING_PORT.get());
                output.accept(ModBlocks.LIFE_SUPPORT_SYSTEM.get());
                output.accept(ModBlocks.OXYGEN_GENERATOR.get());
                output.accept(ModBlocks.SOLAR_PANEL.get());
                
                // Teleporters
                output.accept(ModBlocks.SPACE_TELEPORTER.get());
                output.accept(ModBlocks.MOON_TELEPORTER.get());
                output.accept(ModBlocks.EARTH_TELEPORTER.get());
                
                // Quantum Computer
                output.accept(ModBlocks.QUANTUM_COMPUTER_CONTROLLER.get());
                output.accept(ModBlocks.QUANTUM_CASING.get());
                output.accept(ModBlocks.QUANTUM_CORE.get());
                output.accept(ModItems.QUANTUM_PROCESSOR.get());
                
                // Fuel Refinery
                output.accept(ModBlocks.FUEL_REFINERY_CONTROLLER.get());
                output.accept(ModBlocks.REFINERY_CASING.get());
                output.accept(ModBlocks.DISTILLATION_COLUMN.get());
                
                // Research
                output.accept(ModBlocks.RESEARCH_TERMINAL.get());
                
                // Fusion Reactor Extra
                output.accept(ModBlocks.FUSION_COIL.get());
                output.accept(ModBlocks.FUSION_CORE_BLOCK.get());
                
                // Lunar Resources
                output.accept(ModBlocks.MOON_STONE.get());
                output.accept(ModBlocks.MOON_DUST.get());
                output.accept(ModBlocks.HELIUM3_ORE.get());
                output.accept(ModItems.MOON_DUST_ITEM.get());
                output.accept(ModItems.HELIUM3_CRYSTAL.get());
                
                // Vehicles
                output.accept(ModItems.LUNAR_ROVER.get());
                
                // Machine Upgrades
                output.accept(ModItems.SPEED_UPGRADE_1.get());
                output.accept(ModItems.SPEED_UPGRADE_2.get());
                output.accept(ModItems.SPEED_UPGRADE_3.get());
                output.accept(ModItems.EFFICIENCY_UPGRADE_1.get());
                output.accept(ModItems.EFFICIENCY_UPGRADE_2.get());
                output.accept(ModItems.EFFICIENCY_UPGRADE_3.get());
                output.accept(ModItems.FORTUNE_UPGRADE_1.get());
                output.accept(ModItems.FORTUNE_UPGRADE_2.get());
                output.accept(ModItems.FORTUNE_UPGRADE_3.get());
            })
            .build());
    
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}