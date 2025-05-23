package com.astrolabs.astroexpansion;

import com.astrolabs.astroexpansion.common.network.ModPacketHandler;
import com.astrolabs.astroexpansion.common.registry.*;
import com.astrolabs.astroexpansion.common.world.ModWorldGeneration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AstroExpansion.MODID)
public class AstroExpansion {
    public static final String MODID = "astroexpansion";
    public static final Logger LOGGER = LogManager.getLogger();
    
    public AstroExpansion() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register all deferred registers
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModFluids.register(modEventBus);
        ModChunkGenerators.register(modEventBus);
        
        // Register mod event listeners
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        
        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModPacketHandler.register();
            ModWorldGeneration.generateModWorldGen();
        });
        
        LOGGER.info("Astro Expansion common setup complete!");
    }
    
    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Astro Expansion client setup complete!");
    }
    
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Astro Expansion server starting!");
    }
}