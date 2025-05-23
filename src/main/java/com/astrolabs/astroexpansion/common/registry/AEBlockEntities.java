package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.blockentities.BasicGeneratorBlockEntity;
import com.astrolabs.astroexpansion.common.blockentities.ComponentAssemblerBlockEntity;
import com.astrolabs.astroexpansion.common.blockentities.EnergyConduitBlockEntity;
import com.astrolabs.astroexpansion.common.blockentities.MaterialProcessorBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AEBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AstroExpansion.MODID);
    
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MaterialProcessorBlockEntity>> MATERIAL_PROCESSOR =
            BLOCK_ENTITIES.register("material_processor",
                    () -> BlockEntityType.Builder.of(MaterialProcessorBlockEntity::new, AEBlocks.MATERIAL_PROCESSOR.get()).build(null));
    
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BasicGeneratorBlockEntity>> BASIC_GENERATOR =
            BLOCK_ENTITIES.register("basic_generator",
                    () -> BlockEntityType.Builder.of(BasicGeneratorBlockEntity::new, AEBlocks.BASIC_GENERATOR.get()).build(null));
    
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ComponentAssemblerBlockEntity>> COMPONENT_ASSEMBLER =
            BLOCK_ENTITIES.register("component_assembler",
                    () -> BlockEntityType.Builder.of(ComponentAssemblerBlockEntity::new, AEBlocks.COMPONENT_ASSEMBLER.get()).build(null));
    
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyConduitBlockEntity>> ENERGY_CONDUIT =
            BLOCK_ENTITIES.register("energy_conduit",
                    () -> BlockEntityType.Builder.of(EnergyConduitBlockEntity::new, AEBlocks.ENERGY_CONDUIT.get()).build(null));
    
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.astrolabs.astroexpansion.common.blockentities.ResearchConsoleBlockEntity>> RESEARCH_CONSOLE =
            BLOCK_ENTITIES.register("research_console",
                    () -> BlockEntityType.Builder.of(com.astrolabs.astroexpansion.common.blockentities.ResearchConsoleBlockEntity::new, AEBlocks.RESEARCH_CONSOLE.get()).build(null));
    
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.astrolabs.astroexpansion.common.blockentities.SolarPanelBlockEntity>> SOLAR_PANEL =
            BLOCK_ENTITIES.register("solar_panel",
                    () -> BlockEntityType.Builder.of(com.astrolabs.astroexpansion.common.blockentities.SolarPanelBlockEntity::new, AEBlocks.SOLAR_PANEL.get()).build(null));
    
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.astrolabs.astroexpansion.common.blockentities.ElectricFurnaceBlockEntity>> ELECTRIC_FURNACE =
            BLOCK_ENTITIES.register("electric_furnace",
                    () -> BlockEntityType.Builder.of(com.astrolabs.astroexpansion.common.blockentities.ElectricFurnaceBlockEntity::new, AEBlocks.ELECTRIC_FURNACE.get()).build(null));
}