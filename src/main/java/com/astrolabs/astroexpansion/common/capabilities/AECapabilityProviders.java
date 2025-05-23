package com.astrolabs.astroexpansion.common.capabilities;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.blockentities.AbstractMachineBlockEntity;
import com.astrolabs.astroexpansion.common.blockentities.EnergyConduitBlockEntity;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = AstroExpansion.MODID, bus = EventBusSubscriber.Bus.MOD)
public class AECapabilityProviders {
    
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Register energy capability for all machine block entities
        event.registerBlockEntity(
            AECapabilities.ENERGY,
            com.astrolabs.astroexpansion.common.registry.AEBlockEntities.MATERIAL_PROCESSOR.get(),
            (blockEntity, side) -> blockEntity.getEnergyStorage()
        );
        
        event.registerBlockEntity(
            AECapabilities.ENERGY,
            com.astrolabs.astroexpansion.common.registry.AEBlockEntities.BASIC_GENERATOR.get(),
            (blockEntity, side) -> blockEntity.getEnergyStorage()
        );
        
        event.registerBlockEntity(
            AECapabilities.ENERGY,
            com.astrolabs.astroexpansion.common.registry.AEBlockEntities.COMPONENT_ASSEMBLER.get(),
            (blockEntity, side) -> blockEntity.getEnergyStorage()
        );
        
        event.registerBlockEntity(
            AECapabilities.ENERGY,
            com.astrolabs.astroexpansion.common.registry.AEBlockEntities.ENERGY_CONDUIT.get(),
            (blockEntity, side) -> blockEntity.getEnergyHandler()
        );
        
        event.registerBlockEntity(
            AECapabilities.ENERGY,
            com.astrolabs.astroexpansion.common.registry.AEBlockEntities.RESEARCH_CONSOLE.get(),
            (blockEntity, side) -> blockEntity.getEnergyStorage()
        );
        
        event.registerBlockEntity(
            AECapabilities.ENERGY,
            com.astrolabs.astroexpansion.common.registry.AEBlockEntities.SOLAR_PANEL.get(),
            (blockEntity, side) -> blockEntity.getEnergyHandler()
        );
        
        event.registerBlockEntity(
            AECapabilities.ENERGY,
            com.astrolabs.astroexpansion.common.registry.AEBlockEntities.ELECTRIC_FURNACE.get(),
            (blockEntity, side) -> blockEntity.getEnergyStorage()
        );
    }
}