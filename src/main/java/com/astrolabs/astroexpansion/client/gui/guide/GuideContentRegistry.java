package com.astrolabs.astroexpansion.client.gui.guide;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Central registry for all guide book content
 */
public class GuideContentRegistry {
    private static final List<GuideCategory> categories = new ArrayList<>();
    private static boolean initialized = false;
    
    public static void initialize() {
        if (initialized) return;
        
        registerDefaultContent();
        initialized = true;
    }
    
    private static void registerDefaultContent() {
        // Getting Started Category
        GuideCategory gettingStarted = new GuideCategory(
                new ResourceLocation(AstroExpansion.MODID, "getting_started"),
                Component.literal("Getting Started"),
                new ItemStack(Items.BOOK),
                0
        );
        
        // Welcome Entry
        GuideEntry welcome = new GuideEntry(
                new ResourceLocation(AstroExpansion.MODID, "welcome"),
                Component.literal("Welcome to AstroExpansion"),
                new ItemStack(ModItems.ASTRO_GUIDE.get()),
                0
        );
        welcome.addTextPage(
                Component.literal("Welcome!"),
                Component.literal("Welcome to AstroExpansion, a mod that adds space exploration, advanced technology, and new worlds to discover!\n\nThis guide book will help you navigate through the mod's features, from your first basic machines to complex space stations and beyond.")
        );
        welcome.addTextPage(
                Component.literal("Getting Started"),
                Component.literal("To begin your journey to the stars:\n1. Collect basic resources\n2. Build your first machines\n3. Generate power\n4. Craft a Probe Rocket\n5. Establish a Launch Rail\n6. Start exploring space!\n\nEach step is detailed in the relevant sections of this guide.")
        );
        
        gettingStarted.addEntry(welcome);
        
        // Space Travel Category
        GuideCategory spaceTravel = new GuideCategory(
                new ResourceLocation(AstroExpansion.MODID, "space_travel"),
                Component.literal("Space Travel"),
                new ItemStack(ModItems.PROBE_ROCKET.get()),
                1
        );
        
        // Rocket Types Entry
        GuideEntry rocketTypes = new GuideEntry(
                new ResourceLocation(AstroExpansion.MODID, "rocket_types"),
                Component.literal("Rocket Types"),
                new ItemStack(ModItems.PERSONAL_ROCKET.get()),
                0
        );
        rocketTypes.addTextPage(
                Component.literal("Rocket Overview"),
                Component.literal("AstroExpansion features four different types of rockets, each designed for specific purposes and stages of your space exploration journey.")
        );
        rocketTypes.addSpotlightPage(
                Component.literal("Probe Rocket"),
                new ItemStack(ModItems.PROBE_ROCKET.get()),
                Component.literal("The smallest and simplest rocket. Perfect for initial space exploration and reconnaissance missions. Launches from a Launch Rail and is unmanned.")
        );
        rocketTypes.addSpotlightPage(
                Component.literal("Personal Rocket"),
                new ItemStack(ModItems.PERSONAL_ROCKET.get()),
                Component.literal("A single-person spacecraft that allows you to travel to space personally. Requires a Landing Pad for launch and don't forget your space suit!")
        );
        
        spaceTravel.addEntry(rocketTypes);
        
        // Launch Systems Entry
        GuideEntry launchSystems = new GuideEntry(
                new ResourceLocation(AstroExpansion.MODID, "launch_systems"),
                Component.literal("Launch Systems"),
                new ItemStack(ModItems.LAUNCH_RAIL.get()),
                1
        );
        launchSystems.addTextPage(
                Component.literal("Launch Systems"),
                Component.literal("Different rockets require different launch systems. Using the correct launch platform is crucial for a successful takeoff.")
        );
        launchSystems.addSpotlightPage(
                Component.literal("Launch Rail"),
                new ItemStack(ModItems.LAUNCH_RAIL.get()),
                Component.literal("A simple launch system designed exclusively for Probe Rockets. To use: Place the Launch Rail, right-click with a Probe Rocket to place it on the rail, then right-click the rail again to launch the probe.")
        );
        launchSystems.addSpotlightPage(
                Component.literal("Landing Pad"),
                new ItemStack(ModItems.LANDING_PAD.get()),
                Component.literal("A versatile platform for Personal Rockets and Cargo Shuttles. For Personal Rockets: Requires a single block. For Cargo Shuttles: Must be arranged in a 3x3 pattern.")
        );
        
        spaceTravel.addEntry(launchSystems);
        
        registerCategory(gettingStarted);
        registerCategory(spaceTravel);
    }
    
    public static void registerCategory(GuideCategory category) {
        categories.add(category);
        categories.sort(Comparator.comparingInt(GuideCategory::getSortOrder));
    }
    
    public static List<GuideCategory> getCategories() {
        if (!initialized) {
            initialize();
        }
        return categories;
    }
    
    public static GuideCategory getCategory(ResourceLocation id) {
        return getCategories().stream()
                .filter(cat -> cat.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public static GuideEntry findEntry(ResourceLocation entryId) {
        for (GuideCategory category : getCategories()) {
            for (GuideEntry entry : category.getEntries()) {
                if (entry.getId().equals(entryId)) {
                    return entry;
                }
            }
        }
        return null;
    }
    
    public static void reset() {
        categories.clear();
        initialized = false;
    }
}
