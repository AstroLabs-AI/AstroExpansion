package com.astrolabs.astroexpansion.common.guidebook;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.api.guidebook.*;
import com.astrolabs.astroexpansion.common.guidebook.categories.BasicCategory;
import com.astrolabs.astroexpansion.common.guidebook.entries.BasicEntry;
import com.astrolabs.astroexpansion.common.guidebook.pages.*;
import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import com.astrolabs.astroexpansion.common.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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