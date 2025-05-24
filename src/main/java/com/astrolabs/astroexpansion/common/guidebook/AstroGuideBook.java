package com.astrolabs.astroexpansion.common.guidebook;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.api.guidebook.*;
import com.astrolabs.astroexpansion.common.guidebook.categories.BasicCategory;
import com.astrolabs.astroexpansion.common.guidebook.entries.BasicEntry;
import com.astrolabs.astroexpansion.common.guidebook.pages.*;
import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import com.astrolabs.astroexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Main implementation of the AstroExpansion guide book
 */
public class AstroGuideBook implements IGuideBook {
    private static AstroGuideBook INSTANCE;
    
    private final Map<ResourceLocation, IGuideCategory> categories = new HashMap<>();
    private final Map<ResourceLocation, IGuideEntry> allEntries = new HashMap<>();
    private final Map<Player, GuideProgress> playerProgress = new WeakHashMap<>();
    
    private AstroGuideBook() {
        initializeContent();
    }
    
    public static AstroGuideBook getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AstroGuideBook();
        }
        return INSTANCE;
    }
    
    private void initializeContent() {
        // Create main categories
        IGuideCategory gettingStarted = new BasicCategory(
            new ResourceLocation(AstroExpansion.MODID, "getting_started"),
            Component.translatable("guide.astroexpansion.category.getting_started"),
            Component.translatable("guide.astroexpansion.category.getting_started.desc"),
            new ItemStack(Items.CRAFTING_TABLE),
            0,
            0x00AA00
        );
        registerCategory(gettingStarted);
        
        IGuideCategory machines = new BasicCategory(
            new ResourceLocation(AstroExpansion.MODID, "machines"),
            Component.translatable("guide.astroexpansion.category.machines"),
            Component.translatable("guide.astroexpansion.category.machines.desc"),
            new ItemStack(ModBlocks.BASIC_GENERATOR.get()),
            1,
            0x0088FF
        );
        registerCategory(machines);
        
        IGuideCategory energy = new BasicCategory(
            new ResourceLocation(AstroExpansion.MODID, "energy"),
            Component.translatable("guide.astroexpansion.category.energy"),
            Component.translatable("guide.astroexpansion.category.energy.desc"),
            new ItemStack(ModBlocks.ENERGY_STORAGE.get()),
            2,
            0xFFAA00
        );
        registerCategory(energy);
        
        IGuideCategory space = new BasicCategory(
            new ResourceLocation(AstroExpansion.MODID, "space"),
            Component.translatable("guide.astroexpansion.category.space"),
            Component.translatable("guide.astroexpansion.category.space.desc"),
            new ItemStack(ModItems.SPACE_HELMET.get()),
            3,
            0x000088
        );
        registerCategory(space);
        
        IGuideCategory automation = new BasicCategory(
            new ResourceLocation(AstroExpansion.MODID, "automation"),
            Component.translatable("guide.astroexpansion.category.automation"),
            Component.translatable("guide.astroexpansion.category.automation.desc"),
            new ItemStack(ModBlocks.STORAGE_TERMINAL.get()),
            4,
            0xFF00FF
        );
        registerCategory(automation);
        
        // Create entries
        createGettingStartedEntries(gettingStarted);
        createMachineEntries(machines);
        createEnergyEntries(energy);
        createSpaceEntries(space);
        createAutomationEntries(automation);
    }
    
    private void createGettingStartedEntries(IGuideCategory category) {
        // Welcome entry
        BasicEntry welcome = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "welcome"),
            Component.translatable("guide.astroexpansion.entry.welcome"),
            new ItemStack(ModItems.ASTRO_GUIDE.get()),
            category,
            0
        );
        
        welcome.addPage(new TextPage(
            "# Welcome to AstroExpansion!\n\n" +
            "This guide will help you through your journey to the stars. " +
            "AstroExpansion adds advanced technology, space exploration, and automation " +
            "to help you build the ultimate space program.\n\n" +
            "## Getting Started\n" +
            "Begin by gathering basic resources and crafting your first machines. " +
            "The **Basic Generator** will provide power for your early operations.\n\n" +
            "*Click entries in the side panel to learn more!*",
            1
        ));
        
        welcome.addTag("basics");
        welcome.addTag("tutorial");
        registerEntry(welcome);
        
        // First steps entry
        BasicEntry firstSteps = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "first_steps"),
            Component.translatable("guide.astroexpansion.entry.first_steps"),
            new ItemStack(ModItems.TITANIUM_INGOT.get()),
            category,
            1
        );
        
        firstSteps.addPage(new TextPage(
            "## Your First Steps\n\n" +
            "1. **Mine Titanium Ore** - Found below Y=20\n" +
            "2. **Smelt into Ingots** - Use a furnace\n" +
            "3. **Craft Basic Machines** - Start with generators\n" +
            "4. **Generate Power** - Fuel with coal or wood\n" +
            "5. **Expand Your Base** - Add more machines\n\n" +
            "Titanium is the foundation of all advanced technology!",
            1
        ));
        
        firstSteps.addPage(new RecipePage(
            new ResourceLocation(AstroExpansion.MODID, "titanium_ingot_smelting"),
            2
        ));
        
        firstSteps.addTag("basics");
        firstSteps.addTag("materials");
        registerEntry(firstSteps);
        
        // Titanium Ore entry
        BasicEntry titaniumOre = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "titanium_ore"),
            Component.translatable("guide.astroexpansion.entry.titanium_ore"),
            new ItemStack(ModBlocks.TITANIUM_ORE.get()),
            category,
            2
        );
        
        titaniumOre.addPage(new TextPage(
            "## Titanium Ore\n\n" +
            "**Titanium Ore** is the foundation of all advanced technology in AstroExpansion. " +
            "This valuable resource spawns in the Overworld.\n\n" +
            "**Generation Info:**\n" +
            "- Height Range: Y=-64 to Y=20\n" +
            "- Vein Size: 3-8 blocks\n" +
            "- Rarity: Common below Y=0\n\n" +
            "**Variants:**\n" +
            "- Stone Titanium Ore (Y=0 to Y=20)\n" +
            "- Deepslate Titanium Ore (Y=-64 to Y=0)\n\n" +
            "Mine with an Iron Pickaxe or better!",
            1
        ));
        
        titaniumOre.addPage(new RecipePage(
            new ResourceLocation(AstroExpansion.MODID, "titanium_ingot_smelting"),
            2
        ));
        
        titaniumOre.addTag("materials");
        titaniumOre.addTag("ores");
        registerEntry(titaniumOre);
        
        // Lithium Ore entry
        BasicEntry lithiumOre = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "lithium_ore"),
            Component.translatable("guide.astroexpansion.entry.lithium_ore"),
            new ItemStack(ModBlocks.LITHIUM_ORE.get()),
            category,
            3
        );
        
        lithiumOre.addPage(new TextPage(
            "## Lithium Ore\n\n" +
            "**Lithium Ore** is essential for energy storage and advanced batteries. " +
            "This light metal is crucial for space technology.\n\n" +
            "**Generation Info:**\n" +
            "- Height Range: Y=-32 to Y=32\n" +
            "- Vein Size: 4-9 blocks\n" +
            "- Rarity: Uncommon\n\n" +
            "**Processing:**\n" +
            "- Direct smelting yields Lithium Ingots\n" +
            "- Ore washing provides bonus output\n" +
            "- Can be processed into dust for crafting\n\n" +
            "Used in batteries and energy components!",
            1
        ));
        
        lithiumOre.addTag("materials");
        lithiumOre.addTag("ores");
        lithiumOre.addTag("energy");
        registerEntry(lithiumOre);
        
        // Uranium Ore entry
        BasicEntry uraniumOre = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "uranium_ore"),
            Component.translatable("guide.astroexpansion.entry.uranium_ore"),
            new ItemStack(ModBlocks.URANIUM_ORE.get()),
            category,
            4
        );
        
        uraniumOre.addPage(new TextPage(
            "## Uranium Ore\n\n" +
            "**Uranium Ore** is a radioactive material used in advanced power generation. " +
            "Handle with care!\n\n" +
            "**Generation Info:**\n" +
            "- Height Range: Y=-64 to Y=-16\n" +
            "- Vein Size: 1-4 blocks\n" +
            "- Rarity: Rare\n\n" +
            "**Safety Warning:**\n" +
            "⚠️ Radioactive! Long exposure may cause negative effects.\n\n" +
            "**Uses:**\n" +
            "- Nuclear fuel production\n" +
            "- Advanced energy systems\n" +
            "- Quantum technology",
            1
        ));
        
        uraniumOre.addTag("materials");
        uraniumOre.addTag("ores");
        uraniumOre.addTag("dangerous");
        registerEntry(uraniumOre);
        
        // Wrench entry
        BasicEntry wrench = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "wrench_tool"),
            Component.translatable("guide.astroexpansion.entry.wrench_tool"),
            new ItemStack(ModItems.WRENCH.get()),
            category,
            5
        );
        
        wrench.addPage(new TextPage(
            "## Wrench\n\n" +
            "The **Wrench** is an essential tool for working with machines.\n\n" +
            "**Functions:**\n" +
            "- **Right-click**: Rotate machines\n" +
            "- **Shift + Right-click**: Instantly break machines\n" +
            "- Preserves machine contents and energy\n" +
            "- Works on all AstroExpansion machines\n\n" +
            "**Tip:** Always use a wrench to move machines - breaking " +
            "them with other tools may cause loss of contents!",
            1
        ));
        
        wrench.addPage(new RecipePage(
            new ResourceLocation(AstroExpansion.MODID, "wrench"),
            2
        ));
        
        wrench.addTag("tools");
        wrench.addTag("basics");
        registerEntry(wrench);
        
        // Circuit Board entry
        BasicEntry circuitBoard = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "circuit_board"),
            Component.translatable("guide.astroexpansion.entry.circuit_board"),
            new ItemStack(ModItems.CIRCUIT_BOARD.get()),
            category,
            6
        );
        
        circuitBoard.addPage(new TextPage(
            "## Circuit Board\n\n" +
            "**Circuit Boards** are fundamental components for all electronic devices.\n\n" +
            "**Crafting Tips:**\n" +
            "- Requires copper, gold, and redstone\n" +
            "- Base component for processors\n" +
            "- Used in most machine recipes\n\n" +
            "**Advanced Uses:**\n" +
            "- Combine with other materials for specialized circuits\n" +
            "- Required for control systems\n" +
            "- Essential for automation",
            1
        ));
        
        circuitBoard.addTag("components");
        circuitBoard.addTag("crafting");
        registerEntry(circuitBoard);
        
        // Processor entry
        BasicEntry processor = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "processor_component"),
            Component.translatable("guide.astroexpansion.entry.processor_component"),
            new ItemStack(ModItems.PROCESSOR.get()),
            category,
            7
        );
        
        processor.addPage(new TextPage(
            "## Processor\n\n" +
            "**Processors** are advanced computing components that control machine operations.\n\n" +
            "**Creation:**\n" +
            "- Craft in Component Assembler\n" +
            "- Requires Circuit Board + materials\n" +
            "- Takes time and energy to produce\n\n" +
            "**Uses:**\n" +
            "- Machine upgrades\n" +
            "- Computer systems\n" +
            "- Advanced automation\n\n" +
            "Higher tier processors enable more complex operations!",
            1
        ));
        
        processor.addTag("components");
        processor.addTag("advanced");
        registerEntry(processor);
        
        // Energy Core entry
        BasicEntry energyCore = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "energy_core"),
            Component.translatable("guide.astroexpansion.entry.energy_core"),
            new ItemStack(ModItems.ENERGY_CORE.get()),
            category,
            8
        );
        
        energyCore.addPage(new TextPage(
            "## Energy Core\n\n" +
            "**Energy Cores** are high-capacity energy storage components.\n\n" +
            "**Properties:**\n" +
            "- Stores large amounts of FE\n" +
            "- Used in battery construction\n" +
            "- Required for energy-intensive machines\n\n" +
            "**Crafting:**\n" +
            "- Combines lithium with redstone\n" +
            "- Enhanced with gold for conductivity\n" +
            "- Stabilized with titanium casing\n\n" +
            "Essential for any serious power infrastructure!",
            1
        ));
        
        energyCore.addTag("components");
        energyCore.addTag("energy");
        registerEntry(energyCore);
    }
    
    private void createMachineEntries(IGuideCategory category) {
        // Basic Generator
        BasicEntry basicGen = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "basic_generator"),
            Component.translatable("guide.astroexpansion.entry.basic_generator"),
            new ItemStack(ModBlocks.BASIC_GENERATOR.get()),
            category,
            0
        );
        
        basicGen.addPage(new TextPage(
            "## Basic Generator\n\n" +
            "The **Basic Generator** is your first power source. It burns solid fuels " +
            "like coal, wood, and charcoal to produce Forge Energy (FE).\n\n" +
            "**Stats:**\n" +
            "- Power Generation: 40 FE/tick\n" +
            "- Internal Buffer: 10,000 FE\n" +
            "- Fuel Efficiency: Standard\n\n" +
            "Place fuel in the slot and it will automatically start generating power!",
            1
        ));
        
        basicGen.addPage(new RecipePage(
            new ResourceLocation(AstroExpansion.MODID, "basic_generator"),
            2
        ));
        
        basicGen.addPage(new Model3DPage(
            ModBlocks.BASIC_GENERATOR.get(),
            Component.literal("Basic Generator Model"),
            3
        ));
        
        basicGen.addTag("machines");
        basicGen.addTag("energy");
        basicGen.addTag("generators");
        basicGen.addAssociatedItem(new ItemStack(ModBlocks.BASIC_GENERATOR.get()));
        registerEntry(basicGen);
        
        // Material Processor
        BasicEntry processor = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "material_processor"),
            Component.translatable("guide.astroexpansion.entry.material_processor"),
            new ItemStack(ModBlocks.MATERIAL_PROCESSOR.get()),
            category,
            1
        );
        
        processor.addPage(new TextPage(
            "## Material Processor\n\n" +
            "The **Material Processor** doubles your ore output by crushing ores into dust.\n\n" +
            "**Features:**\n" +
            "- 2x ore multiplication\n" +
            "- Processes any ore\n" +
            "- Energy: 20 FE/tick\n" +
            "- Processing time: 200 ticks\n\n" +
            "Essential for efficient resource processing!",
            1
        ));
        
        processor.addTag("machines");
        processor.addTag("processing");
        registerEntry(processor);
        
        // Ore Washer entry
        BasicEntry oreWasher = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "ore_washer"),
            Component.translatable("guide.astroexpansion.entry.ore_washer"),
            new ItemStack(ModBlocks.ORE_WASHER.get()),
            category,
            2
        );
        
        oreWasher.addPage(new TextPage(
            "## Ore Washer\n\n" +
            "The **Ore Washer** uses water to clean raw ores, increasing yield.\n\n" +
            "**Stats:**\n" +
            "- Power Usage: 20 FE/tick\n" +
            "- Processing Time: 200 ticks\n" +
            "- Water Usage: 100 mB per operation\n" +
            "- Bonus Output: 10-50% chance\n\n" +
            "**Supported Ores:**\n" +
            "- Raw Titanium → 2-3 Titanium Dust\n" +
            "- Raw Lithium → 2-3 Lithium Dust\n" +
            "- Raw Uranium → 2-3 Uranium Dust\n\n" +
            "Requires water input via pipes or buckets!",
            1
        ));
        
        oreWasher.addPage(new RecipePage(
            new ResourceLocation(AstroExpansion.MODID, "ore_washer"),
            2
        ));
        
        oreWasher.addTag("machines");
        oreWasher.addTag("processing");
        oreWasher.addTag("fluids");
        registerEntry(oreWasher);
        
        // Energy Storage entry
        BasicEntry energyStorage = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "energy_storage"),
            Component.translatable("guide.astroexpansion.entry.energy_storage"),
            new ItemStack(ModBlocks.ENERGY_STORAGE.get()),
            category,
            3
        );
        
        energyStorage.addPage(new TextPage(
            "## Energy Storage\n\n" +
            "The **Energy Storage** block is your first battery for storing excess power.\n\n" +
            "**Specifications:**\n" +
            "- Capacity: 100,000 FE\n" +
            "- Max Input: 1,000 FE/tick\n" +
            "- Max Output: 1,000 FE/tick\n" +
            "- Retains energy when broken\n\n" +
            "**Features:**\n" +
            "- Visual charge indicator\n" +
            "- Configurable input/output sides\n" +
            "- Redstone comparator support\n\n" +
            "Essential for managing power fluctuations!",
            1
        ));
        
        energyStorage.addPage(new RecipePage(
            new ResourceLocation(AstroExpansion.MODID, "energy_storage"),
            2
        ));
        
        energyStorage.addTag("machines");
        energyStorage.addTag("energy");
        energyStorage.addTag("storage");
        registerEntry(energyStorage);
        
        // Component Assembler entry (already exists, adding more detail)
        BasicEntry componentAssembler = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "component_assembler"),
            Component.translatable("guide.astroexpansion.entry.component_assembler"),
            new ItemStack(ModBlocks.COMPONENT_ASSEMBLER.get()),
            category,
            4
        );
        
        componentAssembler.addPage(new TextPage(
            "## Component Assembler\n\n" +
            "The **Component Assembler** crafts advanced components automatically.\n\n" +
            "**Performance:**\n" +
            "- Power Usage: 40 FE/tick\n" +
            "- Base Speed: 200 ticks per craft\n" +
            "- Can process multiple recipes\n" +
            "- Upgradeable with speed modules\n\n" +
            "**Key Recipes:**\n" +
            "- Processors (all tiers)\n" +
            "- Advanced circuits\n" +
            "- Energy components\n" +
            "- Storage processors\n\n" +
            "Place ingredients in the grid to see available recipes!",
            1
        ));
        
        componentAssembler.addPage(new RecipePage(
            new ResourceLocation(AstroExpansion.MODID, "component_assembler"),
            2
        ));
        
        componentAssembler.addTag("machines");
        componentAssembler.addTag("crafting");
        componentAssembler.addTag("automation");
        registerEntry(componentAssembler);
        
        // Industrial Furnace entry
        BasicEntry industrialFurnace = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "industrial_furnace"),
            Component.translatable("guide.astroexpansion.entry.industrial_furnace"),
            new ItemStack(ModBlocks.INDUSTRIAL_FURNACE_CONTROLLER.get()),
            category,
            5
        );
        
        industrialFurnace.addPage(new TextPage(
            "## Industrial Furnace\n\n" +
            "The **Industrial Furnace** is a 3x3x2 multiblock that smelts multiple items simultaneously.\n\n" +
            "**Performance:**\n" +
            "- Processes 6 items at once\n" +
            "- 200% speed of vanilla furnace\n" +
            "- Power Usage: 80 FE/tick\n" +
            "- Shared fuel efficiency\n\n" +
            "**Structure Requirements:**\n" +
            "- 1x Industrial Furnace Controller\n" +
            "- 11x Furnace Casing\n" +
            "- 6x Vanilla Furnace\n\n" +
            "Place controller in center of 3x3x2 structure!",
            1
        ));
        
        // Create multiblock structure map
        Map<BlockPos, BlockState> furnaceStructure = new HashMap<>();
        BlockState casing = ModBlocks.FURNACE_CASING.get().defaultBlockState();
        BlockState furnace = net.minecraft.world.level.block.Blocks.FURNACE.defaultBlockState();
        BlockState controller = ModBlocks.INDUSTRIAL_FURNACE_CONTROLLER.get().defaultBlockState();
        
        // Layer 1 (bottom)
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) {
                    furnaceStructure.put(new BlockPos(x, 0, z), controller);
                } else {
                    furnaceStructure.put(new BlockPos(x, 0, z), casing);
                }
            }
        }
        
        // Layer 2 (top)
        furnaceStructure.put(new BlockPos(-1, 1, -1), furnace);
        furnaceStructure.put(new BlockPos(0, 1, -1), furnace);
        furnaceStructure.put(new BlockPos(1, 1, -1), furnace);
        furnaceStructure.put(new BlockPos(-1, 1, 0), furnace);
        furnaceStructure.put(new BlockPos(0, 1, 0), casing);
        furnaceStructure.put(new BlockPos(1, 1, 0), furnace);
        furnaceStructure.put(new BlockPos(-1, 1, 1), furnace);
        furnaceStructure.put(new BlockPos(0, 1, 1), casing);
        furnaceStructure.put(new BlockPos(1, 1, 1), casing);
        
        industrialFurnace.addPage(new MultiblockPage(
            furnaceStructure,
            Component.literal("Industrial Furnace Structure"),
            2
        ));
        
        industrialFurnace.addPage(new RecipePage(
            new ResourceLocation(AstroExpansion.MODID, "industrial_furnace_controller"),
            3
        ));
        
        industrialFurnace.addTag("machines");
        industrialFurnace.addTag("multiblock");
        industrialFurnace.addTag("processing");
        registerEntry(industrialFurnace);
        
        // Fuel Refinery entry
        BasicEntry fuelRefinery = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "fuel_refinery"),
            Component.translatable("guide.astroexpansion.entry.fuel_refinery"),
            new ItemStack(ModBlocks.FUEL_REFINERY_CONTROLLER.get()),
            category,
            6
        );
        
        fuelRefinery.addPage(new TextPage(
            "## Fuel Refinery\n\n" +
            "The **Fuel Refinery** is a 5x5x3 multiblock that processes fluids into rocket fuel.\n\n" +
            "**Processing Capabilities:**\n" +
            "- Crude Oil → Rocket Fuel\n" +
            "- Water → Hydrogen + Oxygen\n" +
            "- Power Usage: 200 FE/tick\n" +
            "- Processing Speed: Variable\n\n" +
            "**Structure Components:**\n" +
            "- 1x Fuel Refinery Controller\n" +
            "- 8x Distillation Columns\n" +
            "- 66x Refinery Casing\n\n" +
            "Connect fluid pipes for input/output!",
            1
        ));
        
        // Create fuel refinery structure
        Map<BlockPos, BlockState> refineryStructure = new HashMap<>();
        BlockState refineryCasing = ModBlocks.REFINERY_CASING.get().defaultBlockState();
        BlockState column = ModBlocks.DISTILLATION_COLUMN.get().defaultBlockState();
        BlockState refineryController = ModBlocks.FUEL_REFINERY_CONTROLLER.get().defaultBlockState();
        
        // Build 5x5x3 structure
        for (int y = 0; y < 3; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    boolean isEdge = (x == -2 || x == 2) || (z == -2 || z == 2);
                    boolean isCorner = ((x == -2 || x == 2) && (z == -2 || z == 2));
                    
                    if (y == 0 || y == 2) {
                        // Top and bottom layers - all casing
                        refineryStructure.put(new BlockPos(x, y, z), refineryCasing);
                    } else {
                        // Middle layer
                        if (x == 0 && z == -2 && y == 1) {
                            refineryStructure.put(new BlockPos(x, y, z), refineryController);
                        } else if (isCorner) {
                            refineryStructure.put(new BlockPos(x, y, z), column);
                        } else if (isEdge) {
                            refineryStructure.put(new BlockPos(x, y, z), refineryCasing);
                        }
                        // Center is hollow
                    }
                }
            }
        }
        
        fuelRefinery.addPage(new MultiblockPage(
            refineryStructure,
            Component.literal("Fuel Refinery Structure"),
            2
        ));
        
        fuelRefinery.addPage(new RecipePage(
            new ResourceLocation(AstroExpansion.MODID, "fuel_refinery_controller"),
            3
        ));
        
        fuelRefinery.addTag("machines");
        fuelRefinery.addTag("multiblock");
        fuelRefinery.addTag("fluids");
        fuelRefinery.addTag("processing");
        registerEntry(fuelRefinery);
    }
    
    private void createEnergyEntries(IGuideCategory category) {
        // Energy basics
        BasicEntry energyBasics = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "energy_basics"),
            Component.translatable("guide.astroexpansion.entry.energy_basics"),
            new ItemStack(ModBlocks.ENERGY_CONDUIT.get()),
            category,
            0
        );
        
        energyBasics.addPage(new TextPage(
            "## Understanding Energy\n\n" +
            "AstroExpansion uses **Forge Energy (FE)** for all machines. " +
            "Energy flows from generators through conduits to machines.\n\n" +
            "**Key Concepts:**\n" +
            "- **Generation** - Produced by generators\n" +
            "- **Storage** - Stored in batteries\n" +
            "- **Transfer** - Moved via conduits\n" +
            "- **Usage** - Consumed by machines\n\n" +
            "Build efficient networks to power your base!",
            1
        ));
        
        energyBasics.addTag("energy");
        energyBasics.addTag("basics");
        registerEntry(energyBasics);
        
        // Energy Conduit entry
        BasicEntry energyConduit = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "energy_conduit"),
            Component.translatable("guide.astroexpansion.entry.energy_conduit"),
            new ItemStack(ModBlocks.ENERGY_CONDUIT.get()),
            category,
            1
        );
        
        energyConduit.addPage(new TextPage(
            "## Energy Conduit\n\n" +
            "**Energy Conduits** transfer power between machines and generators.\n\n" +
            "**Specifications:**\n" +
            "- Transfer Rate: 5,000 FE/tick\n" +
            "- No internal storage\n" +
            "- Instant energy transfer\n" +
            "- Auto-connects to adjacent blocks\n\n" +
            "**Network Features:**\n" +
            "- Forms networks automatically\n" +
            "- Smart energy distribution\n" +
            "- Minimal energy loss\n" +
            "- Supports branching paths\n\n" +
            "Right-click with empty hand to see connections!",
            1
        ));
        
        energyConduit.addPage(new RecipePage(
            new ResourceLocation(AstroExpansion.MODID, "energy_conduit"),
            2
        ));
        
        energyConduit.addTag("energy");
        energyConduit.addTag("transfer");
        energyConduit.addTag("networks");
        registerEntry(energyConduit);
        
        // Fusion Reactor entry
        BasicEntry fusionReactor = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "fusion_reactor"),
            Component.translatable("guide.astroexpansion.entry.fusion_reactor"),
            new ItemStack(ModBlocks.FUSION_REACTOR_CONTROLLER.get()),
            category,
            2
        );
        
        fusionReactor.addPage(new TextPage(
            "## Fusion Reactor\n\n" +
            "The **Fusion Reactor** is a 5x5x5 multiblock that generates massive amounts of power from fusion.\n\n" +
            "**Specifications:**\n" +
            "- Power Generation: 100,000 FE/tick\n" +
            "- Fuel: Deuterium + Tritium\n" +
            "- Requires startup power: 1M FE\n" +
            "- Self-sustaining once started\n\n" +
            "**Structure Components:**\n" +
            "- 1x Fusion Reactor Controller\n" +
            "- 12x Fusion Coils (edges)\n" +
            "- 98x Fusion Reactor Casing\n" +
            "- 2x Fusion Core Blocks (top/bottom center)\n\n" +
            "⚠️ Ensure proper containment before activation!",
            1
        ));
        
        // Create fusion reactor structure
        Map<BlockPos, BlockState> fusionStructure = new HashMap<>();
        BlockState casing = ModBlocks.FUSION_REACTOR_CASING.get().defaultBlockState();
        BlockState coil = ModBlocks.FUSION_COIL.get().defaultBlockState();
        BlockState core = ModBlocks.FUSION_CORE_BLOCK.get().defaultBlockState();
        BlockState controller = ModBlocks.FUSION_REACTOR_CONTROLLER.get().defaultBlockState();
        
        // Build 5x5x5 hollow cube
        for (int y = 0; y < 5; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    boolean isEdge = (x == -2 || x == 2) || (z == -2 || z == 2) || (y == 0 || y == 4);
                    boolean isCorner = ((x == -2 || x == 2) && (z == -2 || z == 2));
                    boolean isCenter = (x == 0 && z == 0);
                    
                    if (isEdge) {
                        if (isCorner && y != 0 && y != 4) {
                            fusionStructure.put(new BlockPos(x, y, z), coil);
                        } else if (x == 0 && y == 2 && z == -2) {
                            fusionStructure.put(new BlockPos(x, y, z), controller);
                        } else if (isCenter && (y == 0 || y == 4)) {
                            fusionStructure.put(new BlockPos(x, y, z), core);
                        } else {
                            fusionStructure.put(new BlockPos(x, y, z), casing);
                        }
                    }
                }
            }
        }
        
        fusionReactor.addPage(new MultiblockPage(
            fusionStructure,
            Component.literal("Fusion Reactor Structure"),
            2
        ));
        
        fusionReactor.addPage(new RecipePage(
            new ResourceLocation(AstroExpansion.MODID, "fusion_reactor_controller"),
            3
        ));
        
        fusionReactor.addTag("energy");
        fusionReactor.addTag("multiblock");
        fusionReactor.addTag("advanced");
        fusionReactor.addTag("fusion");
        registerEntry(fusionReactor);
    }
    
    private void createSpaceEntries(IGuideCategory category) {
        // Space suit
        BasicEntry spaceSuit = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "space_suit"),
            Component.translatable("guide.astroexpansion.entry.space_suit"),
            new ItemStack(ModItems.SPACE_HELMET.get()),
            category,
            0
        );
        
        spaceSuit.addPage(new TextPage(
            "## Space Suit\n\n" +
            "The **Space Suit** is required for survival in space and on the moon. " +
            "It provides oxygen and protection from the vacuum.\n\n" +
            "**Full Set Includes:**\n" +
            "- Space Helmet\n" +
            "- Space Chestplate\n" +
            "- Space Leggings\n" +
            "- Space Boots\n\n" +
            "Don't forget oxygen tanks!",
            1
        ));
        
        spaceSuit.addTag("space");
        spaceSuit.addTag("equipment");
        registerEntry(spaceSuit);
        
        // Rocket Assembly Platform entry
        BasicEntry rocketAssembly = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "rocket_assembly"),
            Component.translatable("guide.astroexpansion.entry.rocket_assembly"),
            new ItemStack(ModBlocks.ROCKET_ASSEMBLY_CONTROLLER.get()),
            category,
            1
        );
        
        rocketAssembly.addPage(new TextPage(
            "## Rocket Assembly Platform\n\n" +
            "The **Rocket Assembly Platform** is a massive 9x9x15 multiblock for building rockets.\n\n" +
            "**Structure Components:**\n" +
            "- 1x Rocket Assembly Controller\n" +
            "- 81x Launch Pad blocks (base)\n" +
            "- Assembly Frames (tower structure)\n\n" +
            "**Assembly Process:**\n" +
            "1. Build the platform structure\n" +
            "2. Place rocket components in order:\n" +
            "   - Engine → Fuel Tank → Hull → Nose Cone\n" +
            "3. Supply power and materials\n" +
            "4. Activate assembly sequence\n\n" +
            "Once complete, your rocket is ready for launch!",
            1
        ));
        
        // Create simplified rocket assembly structure (showing key layers)
        Map<BlockPos, BlockState> rocketStructure = new HashMap<>();
        BlockState launchPad = ModBlocks.LAUNCH_PAD.get().defaultBlockState();
        BlockState frame = ModBlocks.ASSEMBLY_FRAME.get().defaultBlockState();
        BlockState controller = ModBlocks.ROCKET_ASSEMBLY_CONTROLLER.get().defaultBlockState();
        
        // Base layer (9x9 launch pad)
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                rocketStructure.put(new BlockPos(x, 0, z), launchPad);
            }
        }
        
        // Controller at center front
        rocketStructure.put(new BlockPos(0, 1, -4), controller);
        
        // Sample frame structure (corners and edges at different heights)
        int[] heights = {2, 5, 8, 11, 14};
        for (int h : heights) {
            // Corner frames
            rocketStructure.put(new BlockPos(-4, h, -4), frame);
            rocketStructure.put(new BlockPos(4, h, -4), frame);
            rocketStructure.put(new BlockPos(-4, h, 4), frame);
            rocketStructure.put(new BlockPos(4, h, 4), frame);
            
            // Some edge frames
            rocketStructure.put(new BlockPos(0, h, -4), frame);
            rocketStructure.put(new BlockPos(0, h, 4), frame);
            rocketStructure.put(new BlockPos(-4, h, 0), frame);
            rocketStructure.put(new BlockPos(4, h, 0), frame);
        }
        
        rocketAssembly.addPage(new MultiblockPage(
            rocketStructure,
            Component.literal("Rocket Assembly Platform"),
            2
        ));
        
        rocketAssembly.addPage(new RecipePage(
            new ResourceLocation(AstroExpansion.MODID, "rocket_assembly_controller"),
            3
        ));
        
        rocketAssembly.addTag("space");
        rocketAssembly.addTag("multiblock");
        rocketAssembly.addTag("rockets");
        registerEntry(rocketAssembly);
    }
    
    private void createAutomationEntries(IGuideCategory category) {
        // Storage system
        BasicEntry storage = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "storage_system"),
            Component.translatable("guide.astroexpansion.entry.storage_system"),
            new ItemStack(ModBlocks.STORAGE_CORE.get()),
            category,
            0
        );
        
        storage.addPage(new TextPage(
            "## Digital Storage System\n\n" +
            "The **Storage System** provides digital item storage and management.\n\n" +
            "**Components:**\n" +
            "- **Storage Core** - Network controller\n" +
            "- **Storage Drives** - Hold items (1k-64k)\n" +
            "- **Storage Terminal** - Access interface\n" +
            "- **Import/Export Bus** - Automation\n\n" +
            "Connect with cables for a complete system!",
            1
        ));
        
        storage.addTag("automation");
        storage.addTag("storage");
        registerEntry(storage);
        
        // Quantum Computer entry
        BasicEntry quantumComputer = new BasicEntry(
            new ResourceLocation(AstroExpansion.MODID, "quantum_computer"),
            Component.translatable("guide.astroexpansion.entry.quantum_computer"),
            new ItemStack(ModBlocks.QUANTUM_COMPUTER_CONTROLLER.get()),
            category,
            1
        );
        
        quantumComputer.addPage(new TextPage(
            "## Quantum Computer\n\n" +
            "The **Quantum Computer** is a 7x7x7 multiblock for advanced research and computation.\n\n" +
            "**Capabilities:**\n" +
            "- Research data generation\n" +
            "- Recipe optimization\n" +
            "- Quantum calculations\n" +
            "- Power Usage: 1,000 FE/tick\n\n" +
            "**Structure Components:**\n" +
            "- 1x Quantum Computer Controller\n" +
            "- 8x Quantum Cores (center cube)\n" +
            "- 334x Quantum Casing\n\n" +
            "**Research Types:**\n" +
            "- Basic → Advanced → Quantum\n" +
            "- Space & Fusion specializations\n\n" +
            "The pinnacle of computational technology!",
            1
        ));
        
        // Create quantum computer structure (simplified - showing key components)
        Map<BlockPos, BlockState> quantumStructure = new HashMap<>();
        BlockState casing = ModBlocks.QUANTUM_CASING.get().defaultBlockState();
        BlockState core = ModBlocks.QUANTUM_CORE.get().defaultBlockState();
        BlockState controller = ModBlocks.QUANTUM_COMPUTER_CONTROLLER.get().defaultBlockState();
        
        // Build 7x7x7 structure
        for (int y = 0; y < 7; y++) {
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    boolean isEdge = (x == -3 || x == 3) || (z == -3 || z == 3) || (y == 0 || y == 6);
                    boolean isCenterCube = (Math.abs(x) <= 1 && Math.abs(z) <= 1 && y >= 2 && y <= 4);
                    
                    if (x == 0 && y == 3 && z == -3) {
                        quantumStructure.put(new BlockPos(x, y, z), controller);
                    } else if (isCenterCube && !isEdge) {
                        quantumStructure.put(new BlockPos(x, y, z), core);
                    } else if (isEdge) {
                        quantumStructure.put(new BlockPos(x, y, z), casing);
                    }
                }
            }
        }
        
        quantumComputer.addPage(new MultiblockPage(
            quantumStructure,
            Component.literal("Quantum Computer Structure"),
            2
        ));
        
        quantumComputer.addPage(new RecipePage(
            new ResourceLocation(AstroExpansion.MODID, "quantum_computer_controller"),
            3
        ));
        
        quantumComputer.addTag("automation");
        quantumComputer.addTag("multiblock");
        quantumComputer.addTag("research");
        quantumComputer.addTag("quantum");
        registerEntry(quantumComputer);
    }
    
    private void registerCategory(IGuideCategory category) {
        categories.put(category.getId(), category);
    }
    
    private void registerEntry(IGuideEntry entry) {
        allEntries.put(entry.getId(), entry);
        ((BasicCategory)entry.getCategory()).addEntry(entry);
    }
    
    @Override
    public List<IGuideCategory> getCategories() {
        return categories.values().stream()
            .sorted(Comparator.comparingInt(IGuideCategory::getSortOrder))
            .collect(Collectors.toList());
    }
    
    @Override
    public Optional<IGuideCategory> getCategory(ResourceLocation id) {
        return Optional.ofNullable(categories.get(id));
    }
    
    @Override
    public List<IGuideEntry> getAllEntries() {
        return new ArrayList<>(allEntries.values());
    }
    
    @Override
    public List<IGuideEntry> search(String query) {
        String lowerQuery = query.toLowerCase();
        return allEntries.values().stream()
            .filter(entry -> {
                // Search in name
                if (entry.getName().getString().toLowerCase().contains(lowerQuery)) {
                    return true;
                }
                // Search in tags
                for (String tag : entry.getTags()) {
                    if (tag.toLowerCase().contains(lowerQuery)) {
                        return true;
                    }
                }
                return false;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public List<IGuideEntry> getEntriesByTag(String tag) {
        return allEntries.values().stream()
            .filter(entry -> entry.getTags().contains(tag))
            .collect(Collectors.toList());
    }
    
    @Override
    public IGuideProgress getProgress(Player player) {
        return playerProgress.computeIfAbsent(player, p -> new GuideProgress());
    }
    
    @Override
    public boolean canView(Player player, IGuideEntry entry) {
        return entry.isUnlocked(player);
    }
    
    @Override
    public Optional<IGuideEntry> getEntryFor(ItemStack stack) {
        return allEntries.values().stream()
            .filter(entry -> entry.getAssociatedItems().stream()
                .anyMatch(item -> ItemStack.isSameItemSameTags(item, stack)))
            .findFirst();
    }
    
    @Override
    public List<IGuideEntry> getRecentlyViewed(Player player, int limit) {
        IGuideProgress progress = getProgress(player);
        List<IGuideEntry> recent = new ArrayList<>();
        
        for (ResourceLocation id : progress.getRecentHistory(limit)) {
            getEntry(id).ifPresent(recent::add);
        }
        
        return recent;
    }
    
    @Override
    public List<IGuideEntry> getBookmarks(Player player) {
        IGuideProgress progress = getProgress(player);
        return progress.getBookmarkedEntries().stream()
            .map(this::getEntry)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }
    
    @Override
    public void toggleBookmark(Player player, IGuideEntry entry) {
        IGuideProgress progress = getProgress(player);
        ResourceLocation id = entry.getId();
        
        if (progress.getBookmarkedEntries().contains(id)) {
            progress.getBookmarkedEntries().remove(id);
        } else {
            progress.getBookmarkedEntries().add(id);
        }
    }
    
    @Override
    public void markViewed(Player player, IGuideEntry entry) {
        GuideProgress progress = (GuideProgress) getProgress(player);
        progress.markViewed(entry.getId());
    }
    
    @Override
    public Component getTitle() {
        return Component.translatable("item.astroexpansion.astro_guide");
    }
    
    @Override
    public Component getSubtitle() {
        return Component.translatable("item.astroexpansion.astro_guide.subtitle");
    }
    
    private Optional<IGuideEntry> getEntry(ResourceLocation id) {
        return Optional.ofNullable(allEntries.get(id));
    }
}