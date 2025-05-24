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