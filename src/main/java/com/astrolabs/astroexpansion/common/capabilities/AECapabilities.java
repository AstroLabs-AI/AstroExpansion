package com.astrolabs.astroexpansion.common.capabilities;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.api.energy.IEnergyHandler;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;

public class AECapabilities {
    
    public static final BlockCapability<IEnergyHandler, Direction> ENERGY = BlockCapability.createSided(
            ResourceLocation.fromNamespaceAndPath(AstroExpansion.MODID, "energy"),
            IEnergyHandler.class
    );
    
    public static final ItemCapability<IEnergyHandler, Void> ENERGY_ITEM = ItemCapability.createVoid(
            ResourceLocation.fromNamespaceAndPath(AstroExpansion.MODID, "energy_item"),
            IEnergyHandler.class
    );
}