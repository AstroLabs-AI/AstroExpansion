package com.astrolabs.astroexpansion.client;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.client.gui.screens.*;
import com.astrolabs.astroexpansion.client.screen.RocketWorkbenchScreen;
import com.astrolabs.astroexpansion.client.renderer.DroneRenderer;
import com.astrolabs.astroexpansion.client.renderer.RocketRenderer;
import com.astrolabs.astroexpansion.common.registry.ModEntities;
import com.astrolabs.astroexpansion.common.registry.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = AstroExpansion.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Register menu screens
            MenuScreens.register(ModMenuTypes.BASIC_GENERATOR_MENU.get(), BasicGeneratorScreen::new);
            MenuScreens.register(ModMenuTypes.MATERIAL_PROCESSOR_MENU.get(), MaterialProcessorScreen::new);
            MenuScreens.register(ModMenuTypes.ORE_WASHER_MENU.get(), OreWasherScreen::new);
            MenuScreens.register(ModMenuTypes.RECYCLER.get(), RecyclerScreen::new);
            MenuScreens.register(ModMenuTypes.ENERGY_STORAGE_MENU.get(), EnergyStorageScreen::new);
            MenuScreens.register(ModMenuTypes.STORAGE_CORE_MENU.get(), StorageCoreScreen::new);
            MenuScreens.register(ModMenuTypes.STORAGE_TERMINAL_MENU.get(), StorageTerminalScreen::new);
            MenuScreens.register(ModMenuTypes.COMPONENT_ASSEMBLER_MENU.get(), ComponentAssemblerScreen::new);
            MenuScreens.register(ModMenuTypes.DRONE_DOCK_MENU.get(), DroneDockScreen::new);
            MenuScreens.register(ModMenuTypes.INDUSTRIAL_FURNACE.get(), IndustrialFurnaceScreen::new);
            MenuScreens.register(ModMenuTypes.FUSION_REACTOR.get(), FusionReactorScreen::new);
            MenuScreens.register(ModMenuTypes.FLUID_TANK.get(), FluidTankScreen::new);
            MenuScreens.register(ModMenuTypes.QUANTUM_COMPUTER.get(), QuantumComputerScreen::new);
            MenuScreens.register(ModMenuTypes.RESEARCH_TERMINAL.get(), ResearchTerminalScreen::new);
            MenuScreens.register(ModMenuTypes.FUEL_REFINERY.get(), FuelRefineryScreen::new);
            MenuScreens.register(ModMenuTypes.ROCKET_ASSEMBLY.get(), RocketAssemblyScreen::new);
            MenuScreens.register(ModMenuTypes.MATTER_FABRICATOR_MENU.get(), MatterFabricatorScreen::new);
            MenuScreens.register(ModMenuTypes.MATTER_DUPLICATOR_MENU.get(), MatterDuplicatorScreen::new);
            MenuScreens.register(ModMenuTypes.ADVANCED_TELEPORTER.get(), AdvancedTeleporterScreen::new);
            MenuScreens.register(ModMenuTypes.ROCKET_WORKBENCH.get(), RocketWorkbenchScreen::new);
        });
    }
    
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Register drone renderers
        event.registerEntityRenderer(ModEntities.MINING_DRONE.get(), 
            context -> new DroneRenderer<>(context, "mining"));
        event.registerEntityRenderer(ModEntities.CONSTRUCTION_DRONE.get(), 
            context -> new DroneRenderer<>(context, "construction"));
        event.registerEntityRenderer(ModEntities.FARMING_DRONE.get(), 
            context -> new DroneRenderer<>(context, "farming"));
        event.registerEntityRenderer(ModEntities.COMBAT_DRONE.get(), 
            context -> new DroneRenderer<>(context, "combat"));
        event.registerEntityRenderer(ModEntities.LOGISTICS_DRONE.get(), 
            context -> new DroneRenderer<>(context, "logistics"));
        
        // Register rocket renderers
        event.registerEntityRenderer(ModEntities.PROBE_ROCKET.get(), 
            context -> new RocketRenderer<>(context, "probe"));
        event.registerEntityRenderer(ModEntities.PERSONAL_ROCKET.get(), 
            context -> new RocketRenderer<>(context, "personal"));
        event.registerEntityRenderer(ModEntities.CARGO_SHUTTLE.get(), 
            context -> new RocketRenderer<>(context, "cargo"));
    }
    
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(DroneRenderer.MODEL_LAYER, DroneRenderer::createBodyLayer);
        event.registerLayerDefinition(RocketRenderer.ROCKET_LAYER, RocketRenderer::createBodyLayer);
    }
}