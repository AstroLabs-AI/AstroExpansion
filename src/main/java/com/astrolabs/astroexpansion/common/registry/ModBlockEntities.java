package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.blockentities.*;
import com.astrolabs.astroexpansion.common.block.entity.machine.RecyclerBlockEntity;
import com.astrolabs.astroexpansion.common.block.entity.MatterFabricatorBlockEntity;
import com.astrolabs.astroexpansion.common.block.entity.MatterDuplicatorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AstroExpansion.MODID);
    
    public static final RegistryObject<BlockEntityType<BasicGeneratorBlockEntity>> BASIC_GENERATOR =
        BLOCK_ENTITIES.register("basic_generator",
            () -> BlockEntityType.Builder.of(BasicGeneratorBlockEntity::new,
                ModBlocks.BASIC_GENERATOR.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<MaterialProcessorBlockEntity>> MATERIAL_PROCESSOR =
        BLOCK_ENTITIES.register("material_processor",
            () -> BlockEntityType.Builder.of(MaterialProcessorBlockEntity::new,
                ModBlocks.MATERIAL_PROCESSOR.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<OreWasherBlockEntity>> ORE_WASHER =
        BLOCK_ENTITIES.register("ore_washer",
            () -> BlockEntityType.Builder.of(OreWasherBlockEntity::new,
                ModBlocks.ORE_WASHER.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<RecyclerBlockEntity>> RECYCLER =
        BLOCK_ENTITIES.register("recycler",
            () -> BlockEntityType.Builder.of(RecyclerBlockEntity::new,
                ModBlocks.RECYCLER.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<EnergyConduitBlockEntity>> ENERGY_CONDUIT =
        BLOCK_ENTITIES.register("energy_conduit",
            () -> BlockEntityType.Builder.of(EnergyConduitBlockEntity::new,
                ModBlocks.ENERGY_CONDUIT.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<EnergyStorageBlockEntity>> ENERGY_STORAGE =
        BLOCK_ENTITIES.register("energy_storage",
            () -> BlockEntityType.Builder.of(EnergyStorageBlockEntity::new,
                ModBlocks.ENERGY_STORAGE.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<StorageCoreBlockEntity>> STORAGE_CORE =
        BLOCK_ENTITIES.register("storage_core",
            () -> BlockEntityType.Builder.of(StorageCoreBlockEntity::new,
                ModBlocks.STORAGE_CORE.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<StorageTerminalBlockEntity>> STORAGE_TERMINAL =
        BLOCK_ENTITIES.register("storage_terminal",
            () -> BlockEntityType.Builder.of(StorageTerminalBlockEntity::new,
                ModBlocks.STORAGE_TERMINAL.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<DroneDockBlockEntity>> DRONE_DOCK =
        BLOCK_ENTITIES.register("drone_dock",
            () -> BlockEntityType.Builder.of(DroneDockBlockEntity::new,
                ModBlocks.DRONE_DOCK.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<ImportBusBlockEntity>> IMPORT_BUS =
        BLOCK_ENTITIES.register("import_bus",
            () -> BlockEntityType.Builder.of(ImportBusBlockEntity::new,
                ModBlocks.IMPORT_BUS.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<ExportBusBlockEntity>> EXPORT_BUS =
        BLOCK_ENTITIES.register("export_bus",
            () -> BlockEntityType.Builder.of(ExportBusBlockEntity::new,
                ModBlocks.EXPORT_BUS.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<ComponentAssemblerBlockEntity>> COMPONENT_ASSEMBLER =
        BLOCK_ENTITIES.register("component_assembler",
            () -> BlockEntityType.Builder.of(ComponentAssemblerBlockEntity::new,
                ModBlocks.COMPONENT_ASSEMBLER.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<IndustrialFurnaceControllerBlockEntity>> INDUSTRIAL_FURNACE_CONTROLLER =
        BLOCK_ENTITIES.register("industrial_furnace_controller",
            () -> BlockEntityType.Builder.of(IndustrialFurnaceControllerBlockEntity::new,
                ModBlocks.INDUSTRIAL_FURNACE_CONTROLLER.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<FusionReactorControllerBlockEntity>> FUSION_REACTOR_CONTROLLER =
        BLOCK_ENTITIES.register("fusion_reactor_controller",
            () -> BlockEntityType.Builder.of(FusionReactorControllerBlockEntity::new,
                ModBlocks.FUSION_REACTOR_CONTROLLER.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<QuantumComputerControllerBlockEntity>> QUANTUM_COMPUTER_CONTROLLER =
        BLOCK_ENTITIES.register("quantum_computer_controller",
            () -> BlockEntityType.Builder.of(QuantumComputerControllerBlockEntity::new,
                ModBlocks.QUANTUM_COMPUTER_CONTROLLER.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<ResearchTerminalBlockEntity>> RESEARCH_TERMINAL =
        BLOCK_ENTITIES.register("research_terminal",
            () -> BlockEntityType.Builder.of(ResearchTerminalBlockEntity::new,
                ModBlocks.RESEARCH_TERMINAL.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<FuelRefineryControllerBlockEntity>> FUEL_REFINERY_CONTROLLER =
        BLOCK_ENTITIES.register("fuel_refinery_controller",
            () -> BlockEntityType.Builder.of(FuelRefineryControllerBlockEntity::new,
                ModBlocks.FUEL_REFINERY_CONTROLLER.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<RocketAssemblyControllerBlockEntity>> ROCKET_ASSEMBLY_CONTROLLER =
        BLOCK_ENTITIES.register("rocket_assembly_controller",
            () -> BlockEntityType.Builder.of(RocketAssemblyControllerBlockEntity::new,
                ModBlocks.ROCKET_ASSEMBLY_CONTROLLER.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<FluidTankBlockEntity>> FLUID_TANK =
        BLOCK_ENTITIES.register("fluid_tank",
            () -> BlockEntityType.Builder.of(FluidTankBlockEntity::new,
                ModBlocks.FLUID_TANK.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<FluidPipeBlockEntity>> FLUID_PIPE =
        BLOCK_ENTITIES.register("fluid_pipe",
            () -> BlockEntityType.Builder.of(FluidPipeBlockEntity::new,
                ModBlocks.FLUID_PIPE.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL =
        BLOCK_ENTITIES.register("solar_panel",
            () -> BlockEntityType.Builder.of(SolarPanelBlockEntity::new,
                ModBlocks.SOLAR_PANEL.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<OxygenGeneratorBlockEntity>> OXYGEN_GENERATOR =
        BLOCK_ENTITIES.register("oxygen_generator",
            () -> BlockEntityType.Builder.of(OxygenGeneratorBlockEntity::new,
                ModBlocks.OXYGEN_GENERATOR.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<LifeSupportSystemBlockEntity>> LIFE_SUPPORT_SYSTEM =
        BLOCK_ENTITIES.register("life_support_system",
            () -> BlockEntityType.Builder.of(LifeSupportSystemBlockEntity::new,
                ModBlocks.LIFE_SUPPORT_SYSTEM.get()).build(null));
    
    // Tiered Machine Block Entities - Material Processor
    public static final RegistryObject<BlockEntityType<TieredMaterialProcessorBlockEntity>> MATERIAL_PROCESSOR_BASIC =
        BLOCK_ENTITIES.register("material_processor_basic",
            () -> BlockEntityType.Builder.of((pos, state) -> new TieredMaterialProcessorBlockEntity(pos, state, com.astrolabs.astroexpansion.common.blocks.machines.base.MachineTier.BASIC),
                ModBlocks.MATERIAL_PROCESSOR_BASIC.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<TieredMaterialProcessorBlockEntity>> MATERIAL_PROCESSOR_ADVANCED =
        BLOCK_ENTITIES.register("material_processor_advanced",
            () -> BlockEntityType.Builder.of((pos, state) -> new TieredMaterialProcessorBlockEntity(pos, state, com.astrolabs.astroexpansion.common.blocks.machines.base.MachineTier.ADVANCED),
                ModBlocks.MATERIAL_PROCESSOR_ADVANCED.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<TieredMaterialProcessorBlockEntity>> MATERIAL_PROCESSOR_ELITE =
        BLOCK_ENTITIES.register("material_processor_elite",
            () -> BlockEntityType.Builder.of((pos, state) -> new TieredMaterialProcessorBlockEntity(pos, state, com.astrolabs.astroexpansion.common.blocks.machines.base.MachineTier.ELITE),
                ModBlocks.MATERIAL_PROCESSOR_ELITE.get()).build(null));
    
    // Tiered Machine Block Entities - Ore Washer
    public static final RegistryObject<BlockEntityType<TieredOreWasherBlockEntity>> ORE_WASHER_BASIC =
        BLOCK_ENTITIES.register("ore_washer_basic",
            () -> BlockEntityType.Builder.of((pos, state) -> new TieredOreWasherBlockEntity(pos, state, com.astrolabs.astroexpansion.common.blocks.machines.base.MachineTier.BASIC),
                ModBlocks.ORE_WASHER_BASIC.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<TieredOreWasherBlockEntity>> ORE_WASHER_ADVANCED =
        BLOCK_ENTITIES.register("ore_washer_advanced",
            () -> BlockEntityType.Builder.of((pos, state) -> new TieredOreWasherBlockEntity(pos, state, com.astrolabs.astroexpansion.common.blocks.machines.base.MachineTier.ADVANCED),
                ModBlocks.ORE_WASHER_ADVANCED.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<TieredOreWasherBlockEntity>> ORE_WASHER_ELITE =
        BLOCK_ENTITIES.register("ore_washer_elite",
            () -> BlockEntityType.Builder.of((pos, state) -> new TieredOreWasherBlockEntity(pos, state, com.astrolabs.astroexpansion.common.blocks.machines.base.MachineTier.ELITE),
                ModBlocks.ORE_WASHER_ELITE.get()).build(null));
    
    // Tiered Machine Block Entities - Power Generator
    public static final RegistryObject<BlockEntityType<TieredPowerGeneratorBlockEntity>> POWER_GENERATOR_BASIC =
        BLOCK_ENTITIES.register("power_generator_basic",
            () -> BlockEntityType.Builder.of((pos, state) -> new TieredPowerGeneratorBlockEntity(pos, state, com.astrolabs.astroexpansion.common.blocks.machines.base.MachineTier.BASIC),
                ModBlocks.POWER_GENERATOR_BASIC.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<TieredPowerGeneratorBlockEntity>> POWER_GENERATOR_ADVANCED =
        BLOCK_ENTITIES.register("power_generator_advanced",
            () -> BlockEntityType.Builder.of((pos, state) -> new TieredPowerGeneratorBlockEntity(pos, state, com.astrolabs.astroexpansion.common.blocks.machines.base.MachineTier.ADVANCED),
                ModBlocks.POWER_GENERATOR_ADVANCED.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<TieredPowerGeneratorBlockEntity>> POWER_GENERATOR_ELITE =
        BLOCK_ENTITIES.register("power_generator_elite",
            () -> BlockEntityType.Builder.of((pos, state) -> new TieredPowerGeneratorBlockEntity(pos, state, com.astrolabs.astroexpansion.common.blocks.machines.base.MachineTier.ELITE),
                ModBlocks.POWER_GENERATOR_ELITE.get()).build(null));
    
    // Matter Fabrication
    public static final RegistryObject<BlockEntityType<MatterFabricatorBlockEntity>> MATTER_FABRICATOR =
        BLOCK_ENTITIES.register("matter_fabricator",
            () -> BlockEntityType.Builder.of(MatterFabricatorBlockEntity::new,
                ModBlocks.MATTER_FABRICATOR.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<MatterDuplicatorBlockEntity>> MATTER_DUPLICATOR =
        BLOCK_ENTITIES.register("matter_duplicator",
            () -> BlockEntityType.Builder.of(MatterDuplicatorBlockEntity::new,
                ModBlocks.MATTER_DUPLICATOR.get()).build(null));
    
    // Advanced Teleporter
    public static final RegistryObject<BlockEntityType<AdvancedTeleporterBlockEntity>> ADVANCED_TELEPORTER =
        BLOCK_ENTITIES.register("advanced_teleporter",
            () -> BlockEntityType.Builder.of(AdvancedTeleporterBlockEntity::new,
                ModBlocks.ADVANCED_TELEPORTER.get()).build(null));
    
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}