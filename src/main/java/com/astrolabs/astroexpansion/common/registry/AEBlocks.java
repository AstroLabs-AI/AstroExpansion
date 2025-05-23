package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.blocks.BasicGeneratorBlock;
import com.astrolabs.astroexpansion.common.blocks.BatteryBankBlock;
import com.astrolabs.astroexpansion.common.blocks.ComponentAssemblerBlock;
import com.astrolabs.astroexpansion.common.blocks.ElectricFurnaceBlock;
import com.astrolabs.astroexpansion.common.blocks.EnergyConduitBlock;
import com.astrolabs.astroexpansion.common.blocks.ForceFieldBlock;
import com.astrolabs.astroexpansion.common.blocks.ForceFieldGeneratorBlock;
import com.astrolabs.astroexpansion.common.blocks.MaterialProcessorBlock;
import com.astrolabs.astroexpansion.common.blocks.OxygenGeneratorBlock;
import com.astrolabs.astroexpansion.common.blocks.ResearchConsoleBlock;
import com.astrolabs.astroexpansion.common.blocks.RocketBlock;
import com.astrolabs.astroexpansion.common.blocks.SolarPanelBlock;
import com.astrolabs.astroexpansion.common.blocks.TeleporterBlock;
import com.astrolabs.astroexpansion.common.blocks.WirelessEnergyNodeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AEBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AstroExpansion.MODID);
    
    // Ores
    public static final DeferredBlock<Block> TITANIUM_ORE = BLOCKS.register("titanium_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 3.0F)
                    .sound(SoundType.STONE)));
    
    public static final DeferredBlock<Block> DEEPSLATE_TITANIUM_ORE = BLOCKS.register("deepslate_titanium_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .strength(4.5F, 3.0F)
                    .sound(SoundType.DEEPSLATE)));
    
    public static final DeferredBlock<Block> LITHIUM_ORE = BLOCKS.register("lithium_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 3.0F)
                    .sound(SoundType.STONE)));
    
    public static final DeferredBlock<Block> URANIUM_ORE = BLOCKS.register("uranium_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F, 3.0F)
                    .sound(SoundType.STONE)));
    
    // Metal Blocks
    public static final DeferredBlock<Block> TITANIUM_BLOCK = BLOCKS.register("titanium_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.METAL)));
    
    public static final DeferredBlock<Block> STEEL_BLOCK = BLOCKS.register("steel_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.METAL)));
    
    // Machines
    public static final DeferredBlock<Block> MATERIAL_PROCESSOR = BLOCKS.register("material_processor",
            () -> new MaterialProcessorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(3.5F)
                    .sound(SoundType.METAL)
                    .noOcclusion()));
    
    public static final DeferredBlock<Block> BASIC_GENERATOR = BLOCKS.register("basic_generator",
            () -> new BasicGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(3.5F)
                    .sound(SoundType.METAL)
                    .noOcclusion()));
    
    public static final DeferredBlock<Block> COMPONENT_ASSEMBLER = BLOCKS.register("component_assembler",
            () -> new ComponentAssemblerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(3.5F)
                    .sound(SoundType.METAL)
                    .noOcclusion()));
    
    public static final DeferredBlock<Block> ENERGY_CONDUIT = BLOCKS.register("energy_conduit",
            () -> new EnergyConduitBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()));
    
    public static final DeferredBlock<Block> RESEARCH_CONSOLE = BLOCKS.register("research_console",
            () -> new ResearchConsoleBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(3.5F)
                    .sound(SoundType.METAL)
                    .noOcclusion()));
    
    public static final DeferredBlock<Block> SOLAR_PANEL = BLOCKS.register("solar_panel",
            () -> new SolarPanelBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLUE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()));
    
    public static final DeferredBlock<Block> ELECTRIC_FURNACE = BLOCKS.register("electric_furnace",
            () -> new ElectricFurnaceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(3.5F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(ElectricFurnaceBlock.LIT) ? 13 : 0)));
    
    public static final DeferredBlock<Block> BATTERY_BANK = BLOCKS.register("battery_bank",
            () -> new BatteryBankBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()));
    
    public static final DeferredBlock<Block> WIRELESS_ENERGY_NODE = BLOCKS.register("wireless_energy_node",
            () -> new WirelessEnergyNodeBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()));
    
    // Advanced Tech
    public static final DeferredBlock<Block> TELEPORTER = BLOCKS.register("teleporter",
            () -> new TeleporterBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .requiresCorrectToolForDrops()
                    .strength(6.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()));
    
    public static final DeferredBlock<Block> FORCE_FIELD_GENERATOR = BLOCKS.register("force_field_generator",
            () -> new ForceFieldGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()));
    
    public static final DeferredBlock<Block> FORCE_FIELD = BLOCKS.register("force_field",
            () -> new ForceFieldBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .noCollission()
                    .strength(-1.0F, 3600000.0F)
                    .noLootTable()
                    .sound(SoundType.GLASS)
                    .noOcclusion()
                    .isViewBlocking((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> false)));
    
    // Space Age
    public static final DeferredBlock<Block> OXYGEN_GENERATOR = BLOCKS.register("oxygen_generator",
            () -> new OxygenGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()));
    
    public static final DeferredBlock<Block> ROCKET = BLOCKS.register("rocket",
            () -> new RocketBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(6.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()));
}