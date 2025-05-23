package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AECreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AstroExpansion.MODID);
    
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("main_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.astroexpansion.main"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> AEItems.TITANIUM_INGOT.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        // Ores
                        output.accept(AEItems.TITANIUM_ORE_ITEM.get());
                        output.accept(AEItems.DEEPSLATE_TITANIUM_ORE_ITEM.get());
                        output.accept(AEItems.LITHIUM_ORE_ITEM.get());
                        output.accept(AEItems.URANIUM_ORE_ITEM.get());
                        
                        // Raw Materials
                        output.accept(AEItems.RAW_TITANIUM.get());
                        output.accept(AEItems.RAW_LITHIUM.get());
                        output.accept(AEItems.RAW_URANIUM.get());
                        
                        // Ingots
                        output.accept(AEItems.TITANIUM_INGOT.get());
                        output.accept(AEItems.STEEL_INGOT.get());
                        output.accept(AEItems.LITHIUM_INGOT.get());
                        output.accept(AEItems.URANIUM_INGOT.get());
                        
                        // Dusts
                        output.accept(AEItems.TITANIUM_DUST.get());
                        output.accept(AEItems.IRON_DUST.get());
                        output.accept(AEItems.COAL_DUST.get());
                        
                        // Components
                        output.accept(AEItems.CIRCUIT_BOARD.get());
                        output.accept(AEItems.PROCESSOR.get());
                        output.accept(AEItems.ADVANCED_PROCESSOR.get());
                        output.accept(AEItems.ENERGY_CORE.get());
                        
                        // Blocks
                        output.accept(AEItems.TITANIUM_BLOCK_ITEM.get());
                        output.accept(AEItems.STEEL_BLOCK_ITEM.get());
                        
                        // Machines
                        output.accept(AEItems.MATERIAL_PROCESSOR_ITEM.get());
                        output.accept(AEItems.BASIC_GENERATOR_ITEM.get());
                        output.accept(AEItems.COMPONENT_ASSEMBLER_ITEM.get());
                        output.accept(AEItems.ELECTRIC_FURNACE_ITEM.get());
                        output.accept(AEItems.SOLAR_PANEL_ITEM.get());
                        output.accept(AEItems.ENERGY_CONDUIT_ITEM.get());
                        output.accept(AEItems.RESEARCH_CONSOLE_ITEM.get());
                        
                        // Phase 2 Machines
                        output.accept(AEItems.BATTERY_BANK_ITEM.get());
                        output.accept(AEItems.WIRELESS_ENERGY_NODE_ITEM.get());
                        output.accept(AEItems.TELEPORTER_ITEM.get());
                        output.accept(AEItems.FORCE_FIELD_GENERATOR_ITEM.get());
                        output.accept(AEItems.OXYGEN_GENERATOR_ITEM.get());
                        output.accept(AEItems.ROCKET_ITEM.get());
                        
                        // Space Suit Armor
                        output.accept(AEItems.SPACE_SUIT_HELMET.get());
                        output.accept(AEItems.SPACE_SUIT_CHESTPLATE.get());
                        output.accept(AEItems.SPACE_SUIT_LEGGINGS.get());
                        output.accept(AEItems.SPACE_SUIT_BOOTS.get());
                    })
                    .build());
}