package com.astrolabs.astroexpansion;

import org.slf4j.Logger;

import com.astrolabs.astroexpansion.common.registry.AEBlockEntities;
import com.astrolabs.astroexpansion.common.registry.AEBlocks;
import com.astrolabs.astroexpansion.common.registry.AECreativeTabs;
import com.astrolabs.astroexpansion.common.registry.AEItems;
import com.astrolabs.astroexpansion.common.registry.AEMenuTypes;
import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(AstroExpansion.MODID)
public class AstroExpansion {
    public static final String MODID = "astroexpansion";
    private static final Logger LOGGER = LogUtils.getLogger();

    public AstroExpansion(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        // Register deferred registries
        AEBlocks.BLOCKS.register(modEventBus);
        AEItems.ITEMS.register(modEventBus);
        AEBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        AEMenuTypes.MENU_TYPES.register(modEventBus);
        AECreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Astro Expansion is initializing...");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Astro Expansion server starting");
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Astro Expansion client setup");
        }
        
        @SubscribeEvent
        public static void registerScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
            event.register(AEMenuTypes.MATERIAL_PROCESSOR.get(), 
                com.astrolabs.astroexpansion.client.gui.MaterialProcessorScreen::new);
            event.register(AEMenuTypes.COMPONENT_ASSEMBLER.get(), 
                com.astrolabs.astroexpansion.client.gui.ComponentAssemblerScreen::new);
        }
    }
}