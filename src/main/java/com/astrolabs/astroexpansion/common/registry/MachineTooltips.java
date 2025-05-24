package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.common.items.MachineBlockItem.MachineTooltipData;

public class MachineTooltips {
    
    // Basic Machines
    public static final MachineTooltipData BASIC_GENERATOR = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.basic_generator.desc")
        .energy(10000, 0)  // 10k FE capacity, generates power
        .processing(20)  // 1 second per coal
        .features("auto_output", "solid_fuel")
        .build();
    
    public static final MachineTooltipData MATERIAL_PROCESSOR = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.material_processor.desc")
        .energy(20000, 40)  // 20k FE capacity, 40 FE per operation
        .processing(200)  // 10 seconds base
        .upgradeSlots(2)
        .features("ore_doubling", "upgradeable")
        .build();
    
    public static final MachineTooltipData ORE_WASHER = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.ore_washer.desc")
        .energy(15000, 30)  // 15k FE capacity, 30 FE per operation
        .processing(160)  // 8 seconds base
        .upgradeSlots(2)
        .features("ore_tripling", "fluid_required", "upgradeable")
        .requirements("water")
        .build();
    
    public static final MachineTooltipData COMPONENT_ASSEMBLER = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.component_assembler.desc")
        .energy(50000, 100)  // 50k FE capacity, 100 FE per operation
        .processing(300)  // 15 seconds base
        .upgradeSlots(3)
        .features("auto_crafting", "pattern_storage", "upgradeable")
        .build();
    
    // Energy System
    public static final MachineTooltipData ENERGY_STORAGE = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.energy_storage.desc")
        .energy(1000000, 0)  // 1M FE capacity
        .features("bidirectional", "configurable_sides")
        .build();
    
    public static final MachineTooltipData SOLAR_PANEL = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.solar_panel.desc")
        .energy(10000, 0)  // 10k FE buffer
        .features("daylight_only", "weather_affected", "dimension_bonus")
        .warnings("no_obstructions")
        .build();
    
    // Storage System
    public static final MachineTooltipData STORAGE_CORE = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.storage_core.desc")
        .energy(50000, 10)  // 50k FE capacity, 10 FE per tick active
        .storage(8, 0)  // 8 drive slots
        .features("digital_storage", "network_controller", "wireless_access", "cross_dimensional", "channel_management")
        .multiblock("storage_network", "3x3x3")
        .warnings("power_loss_data_safe")
        .build();
    
    public static final MachineTooltipData STORAGE_TERMINAL = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.storage_terminal.desc")
        .energy(0, 5)  // No storage, 5 FE per tick when active
        .features("item_search", "auto_crafting", "sorting", "jei_integration", "pattern_storage")
        .requirements("storage_core", "network_channel")
        .build();
    
    // Advanced Machines
    public static final MachineTooltipData RECYCLER = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.recycler.desc")
        .energy(20000, 45)
        .processing(100)  // 5 seconds
        .efficiency(0.125f)  // 12.5% chance for scrap
        .upgradeSlots(2)
        .features("scrap_production", "upgradeable")
        .build();
    
    public static final MachineTooltipData MATTER_FABRICATOR = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.matter_fabricator.desc")
        .energy(10000000, 1000000)  // 10M FE capacity, 1M FE per UU-Matter
        .processing(1200)  // 60 seconds base
        .features("uu_matter_generation", "scrap_amplification")
        .warnings("high_power_usage")
        .build();
    
    public static final MachineTooltipData MATTER_DUPLICATOR = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.matter_duplicator.desc")
        .energy(5000000, 0)  // 5M FE capacity, varies by item
        .features("item_replication", "pattern_learning")
        .requirements("uu_matter")
        .build();
    
    // Multiblock Controllers
    public static final MachineTooltipData INDUSTRIAL_FURNACE = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.industrial_furnace.desc")
        .energy(100000, 80)  // 100k FE capacity, 80 FE per operation
        .processing(100)  // 5 seconds base
        .speed(2.0f)  // 2x faster than vanilla
        .efficiency(1.5f)  // 50% chance for bonus output
        .multiblock("industrial_furnace", "3x3x3")
        .features("bulk_smelting", "bonus_output", "parallel_processing")
        .build();
    
    public static final MachineTooltipData FUSION_REACTOR = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.fusion_reactor.desc")
        .energy(100000000, 0)  // 100M FE capacity, generates power
        .processing(400)  // 20 seconds per cycle
        .multiblock("fusion_reactor", "5x5x5")
        .features("deuterium_tritium_fusion", "massive_power", "plasma_heating")
        .requirements("deuterium", "tritium", "startup_power")
        .warnings("explosion_risk")
        .build();
    
    public static final MachineTooltipData QUANTUM_COMPUTER = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.quantum_computer.desc")
        .energy(5000000, 10000)  // 5M FE capacity, 10k FE per operation
        .processing(600)  // 30 seconds base
        .multiblock("quantum_computer", "3x3x3")
        .features("quantum_processing", "research_boost", "calculation_speed")
        .requirements("cooling")
        .build();
    
    public static final MachineTooltipData FUEL_REFINERY = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.fuel_refinery.desc")
        .energy(200000, 200)  // 200k FE capacity, 200 FE per tick
        .processing(2400)  // 2 minutes per batch
        .multiblock("fuel_refinery", "5x7x5")
        .features("rocket_fuel_production", "fluid_processing", "byproduct_recovery")
        .build();
    
    public static final MachineTooltipData ROCKET_ASSEMBLY = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.rocket_assembly.desc")
        .energy(1000000, 500)  // 1M FE capacity, 500 FE per tick
        .multiblock("rocket_assembly", "9x15x9")
        .features("rocket_construction", "component_validation", "launch_capability")
        .requirements("launch_pad", "assembly_frame")
        .build();
    
    // Space Equipment
    public static final MachineTooltipData OXYGEN_GENERATOR = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.oxygen_generator.desc")
        .energy(50000, 100)  // 50k FE capacity, 100 FE per operation
        .processing(200)  // 10 seconds per canister
        .features("oxygen_production", "life_support")
        .requirements("plant_matter")
        .build();
    
    public static final MachineTooltipData LIFE_SUPPORT = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.life_support.desc")
        .energy(100000, 50)  // 100k FE capacity, 50 FE per tick
        .features("area_oxygenation", "temperature_control", "radiation_shielding")
        .warnings("space_only")
        .build();
    
    // Teleportation
    public static final MachineTooltipData ADVANCED_TELEPORTER = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.advanced_teleporter.desc")
        .energy(5000000, 50000)  // 5M FE capacity, 50k FE per teleport
        .multiblock("teleporter_frame", "3x3x3")
        .features("interdimensional", "coordinate_based", "frequency_cards")
        .warnings("power_surge")
        .build();
    
    // Drones
    public static final MachineTooltipData DRONE_DOCK = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.drone_dock.desc")
        .energy(100000, 1000)  // 100k FE capacity, 1k FE per charge cycle
        .features("drone_charging", "inventory_access", "programming_interface")
        .build();
    
    // Automation
    public static final MachineTooltipData IMPORT_BUS = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.import_bus.desc")
        .energy(0, 10)  // No storage, 10 FE per item
        .features("auto_import", "filter_slots", "priority_system")
        .requirements("storage_network")
        .build();
    
    public static final MachineTooltipData EXPORT_BUS = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.export_bus.desc")
        .energy(0, 10)  // No storage, 10 FE per item
        .features("auto_export", "filter_slots", "crafting_card")
        .requirements("storage_network")
        .build();
    
    // Fluids
    public static final MachineTooltipData FLUID_TANK = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.fluid_tank.desc")
        .storage(0, 16000)  // 16 buckets
        .features("fluid_storage", "auto_output", "comparator_output")
        .build();
    
    // Research
    public static final MachineTooltipData RESEARCH_TERMINAL = MachineTooltipData.builder()
        .description("tooltip.astroexpansion.research_terminal.desc")
        .energy(50000, 100)  // 50k FE capacity, 100 FE per research tick
        .features("technology_research", "data_analysis", "blueprint_unlocking")
        .build();
}