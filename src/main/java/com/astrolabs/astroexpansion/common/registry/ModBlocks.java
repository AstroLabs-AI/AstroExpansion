package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.blocks.*;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = 
        DeferredRegister.create(ForgeRegistries.BLOCKS, AstroExpansion.MODID);
    
    // Ores
    public static final RegistryObject<Block> TITANIUM_ORE = registerBlock("titanium_ore",
        () -> new DropExperienceBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .requiresCorrectToolForDrops()
            .strength(3.0F, 3.0F)
            .sound(SoundType.STONE),
            UniformInt.of(0, 2)));
    
    public static final RegistryObject<Block> DEEPSLATE_TITANIUM_ORE = registerBlock("deepslate_titanium_ore",
        () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(TITANIUM_ORE.get())
            .mapColor(MapColor.DEEPSLATE)
            .strength(4.5F, 3.0F)
            .sound(SoundType.DEEPSLATE)));
    
    public static final RegistryObject<Block> LITHIUM_ORE = registerBlock("lithium_ore",
        () -> new DropExperienceBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .requiresCorrectToolForDrops()
            .strength(3.0F, 3.0F)
            .sound(SoundType.STONE),
            UniformInt.of(0, 2)));
    
    public static final RegistryObject<Block> DEEPSLATE_LITHIUM_ORE = registerBlock("deepslate_lithium_ore",
        () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(LITHIUM_ORE.get())
            .mapColor(MapColor.DEEPSLATE)
            .strength(4.5F, 3.0F)
            .sound(SoundType.DEEPSLATE)));
    
    public static final RegistryObject<Block> URANIUM_ORE = registerBlock("uranium_ore",
        () -> new DropExperienceBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .requiresCorrectToolForDrops()
            .strength(4.0F, 3.0F)
            .sound(SoundType.STONE),
            UniformInt.of(2, 5)));
    
    public static final RegistryObject<Block> DEEPSLATE_URANIUM_ORE = registerBlock("deepslate_uranium_ore",
        () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(URANIUM_ORE.get())
            .mapColor(MapColor.DEEPSLATE)
            .strength(5.0F, 3.0F)
            .sound(SoundType.DEEPSLATE)));
    
    // Storage Blocks
    public static final RegistryObject<Block> TITANIUM_BLOCK = registerBlock("titanium_block",
        () -> new Block(BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 6.0F)
            .sound(SoundType.METAL)));
    
    public static final RegistryObject<Block> LITHIUM_BLOCK = registerBlock("lithium_block",
        () -> new Block(BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 6.0F)
            .sound(SoundType.METAL)));
    
    public static final RegistryObject<Block> URANIUM_BLOCK = registerBlock("uranium_block",
        () -> new Block(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_GREEN)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 6.0F)
            .sound(SoundType.METAL)));
    
    // Machines
    public static final RegistryObject<Block> BASIC_GENERATOR = registerBlock("basic_generator",
        () -> new BasicGeneratorBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .requiresCorrectToolForDrops()
            .strength(3.5F)
            .sound(SoundType.METAL)
            .noOcclusion()));
    
    public static final RegistryObject<Block> MATERIAL_PROCESSOR = registerBlock("material_processor",
        () -> new MaterialProcessorBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .requiresCorrectToolForDrops()
            .strength(3.5F)
            .sound(SoundType.METAL)
            .noOcclusion()));
    
    public static final RegistryObject<Block> ORE_WASHER = registerBlock("ore_washer",
        () -> new OreWasherBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .requiresCorrectToolForDrops()
            .strength(3.5F)
            .sound(SoundType.METAL)
            .noOcclusion()));
    
    public static final RegistryObject<Block> ENERGY_CONDUIT = registerBlock("energy_conduit",
        () -> new EnergyConduitBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .strength(2.0F)
            .sound(SoundType.METAL)
            .noOcclusion()));
    
    public static final RegistryObject<Block> ENERGY_STORAGE = registerBlock("energy_storage",
        () -> new EnergyStorageBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .requiresCorrectToolForDrops()
            .strength(3.5F)
            .sound(SoundType.METAL)));
    
    // Storage System
    public static final RegistryObject<Block> STORAGE_CORE = registerBlock("storage_core",
        () -> new StorageCoreBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .requiresCorrectToolForDrops()
            .strength(3.5F)
            .sound(SoundType.METAL)
            .lightLevel(state -> state.getValue(StorageCoreBlock.FORMED) ? 7 : 0)));
    
    public static final RegistryObject<Block> STORAGE_TERMINAL = registerBlock("storage_terminal",
        () -> new StorageTerminalBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .requiresCorrectToolForDrops()
            .strength(3.5F)
            .sound(SoundType.METAL)
            .noOcclusion()));
    
    // Drones
    public static final RegistryObject<Block> DRONE_DOCK = registerBlock("drone_dock",
        () -> new DroneDockBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .requiresCorrectToolForDrops()
            .strength(3.5F)
            .sound(SoundType.METAL)
            .lightLevel(state -> state.getValue(DroneDockBlock.CHARGING) ? 7 : 0)));
    
    // Storage Automation
    public static final RegistryObject<Block> IMPORT_BUS = registerBlock("import_bus",
        () -> new ImportBusBlock());
    
    public static final RegistryObject<Block> EXPORT_BUS = registerBlock("export_bus",
        () -> new ExportBusBlock());
    
    // Advanced Machines
    public static final RegistryObject<Block> COMPONENT_ASSEMBLER = registerBlock("component_assembler",
        () -> new ComponentAssemblerBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .requiresCorrectToolForDrops()
            .strength(3.5F)
            .sound(SoundType.METAL)
            .noOcclusion()));
    
    // Multiblock Components
    public static final RegistryObject<Block> FURNACE_CASING = registerBlock("furnace_casing",
        () -> new FurnaceCasingBlock());
    
    public static final RegistryObject<Block> INDUSTRIAL_FURNACE_CONTROLLER = registerBlock("industrial_furnace_controller",
        () -> new IndustrialFurnaceControllerBlock());
    
    // Fusion Reactor
    public static final RegistryObject<Block> FUSION_REACTOR_CASING = registerBlock("fusion_reactor_casing",
        () -> new FusionReactorCasingBlock());
    
    public static final RegistryObject<Block> FUSION_REACTOR_CONTROLLER = registerBlock("fusion_reactor_controller",
        () -> new FusionReactorControllerBlock());
    
    public static final RegistryObject<Block> FUSION_COIL = registerBlock("fusion_coil",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    
    public static final RegistryObject<Block> FUSION_CORE_BLOCK = registerBlock("fusion_core_block",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).lightLevel(state -> 15)));
    
    // Quantum Computer
    public static final RegistryObject<Block> QUANTUM_COMPUTER_CONTROLLER = registerBlock("quantum_computer_controller",
        () -> new QuantumComputerControllerBlock());
    
    public static final RegistryObject<Block> QUANTUM_CASING = registerBlock("quantum_casing",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    
    public static final RegistryObject<Block> QUANTUM_CORE = registerBlock("quantum_core",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).lightLevel(state -> 12)));
    
    // Research System
    public static final RegistryObject<Block> RESEARCH_TERMINAL = registerBlock("research_terminal",
        () -> new ResearchTerminalBlock());
    
    // Fuel Refinery
    public static final RegistryObject<Block> FUEL_REFINERY_CONTROLLER = registerBlock("fuel_refinery_controller",
        () -> new FuelRefineryControllerBlock());
    
    public static final RegistryObject<Block> REFINERY_CASING = registerBlock("refinery_casing",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    
    public static final RegistryObject<Block> DISTILLATION_COLUMN = registerBlock("distillation_column",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));
    
    // Rocket Assembly
    public static final RegistryObject<Block> ROCKET_ASSEMBLY_CONTROLLER = registerBlock("rocket_assembly_controller",
        () -> new RocketAssemblyControllerBlock());
    
    public static final RegistryObject<Block> LAUNCH_PAD = registerBlock("launch_pad",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(50.0F, 1200.0F)));
    
    public static final RegistryObject<Block> ASSEMBLY_FRAME = registerBlock("assembly_frame",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));
    
    // Rocket Parts
    public static final RegistryObject<Block> ROCKET_ENGINE = registerBlock("rocket_engine",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    
    public static final RegistryObject<Block> ROCKET_FUEL_TANK = registerBlock("rocket_fuel_tank",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    
    public static final RegistryObject<Block> ROCKET_HULL = registerBlock("rocket_hull",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    
    public static final RegistryObject<Block> ROCKET_NOSE_CONE = registerBlock("rocket_nose_cone",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    
    // Space Station
    public static final RegistryObject<Block> STATION_HULL = registerBlock("station_hull",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(30.0F, 600.0F)));
    
    public static final RegistryObject<Block> STATION_GLASS = registerBlock("station_glass",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.GLASS).strength(10.0F, 300.0F).noOcclusion()));
    
    public static final RegistryObject<Block> SOLAR_PANEL = registerBlock("solar_panel",
        () -> new SolarPanelBlock());
    
    public static final RegistryObject<Block> DOCKING_PORT = registerBlock("docking_port",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(20.0F, 400.0F)));
    
    public static final RegistryObject<Block> OXYGEN_GENERATOR = registerBlock("oxygen_generator",
        () -> new OxygenGeneratorBlock());
    
    public static final RegistryObject<Block> LIFE_SUPPORT_SYSTEM = registerBlock("life_support_system",
        () -> new LifeSupportSystemBlock());
    
    // Lunar Blocks
    public static final RegistryObject<Block> MOON_STONE = registerBlock("moon_stone",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).strength(2.0F, 6.0F)));
    
    public static final RegistryObject<Block> MOON_DUST = registerBlock("moon_dust",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.SAND).strength(0.5F)));
    
    public static final RegistryObject<Block> HELIUM3_ORE = registerBlock("helium3_ore",
        () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.DIAMOND_ORE)
            .strength(4.0F, 3.0F)
            .requiresCorrectToolForDrops(),
            UniformInt.of(5, 9)));
    
    // Teleporters
    public static final RegistryObject<Block> SPACE_TELEPORTER = registerBlock("space_teleporter",
        () -> new TeleporterBlock(ModDimensions.SPACE_KEY, "Space"));
    
    public static final RegistryObject<Block> MOON_TELEPORTER = registerBlock("moon_teleporter",
        () -> new TeleporterBlock(ModDimensions.MOON_KEY, "Moon"));
    
    public static final RegistryObject<Block> EARTH_TELEPORTER = registerBlock("earth_teleporter",
        () -> new TeleporterBlock(Level.OVERWORLD, "Earth"));
    
    // Fluid System
    public static final RegistryObject<Block> FLUID_TANK = registerBlock("fluid_tank",
        () -> new FluidTankBlock());
    
    public static final RegistryObject<Block> FLUID_PIPE = registerBlock("fluid_pipe",
        () -> new FluidPipeBlock());
    
    // Register block with item
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }
    
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
    
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}