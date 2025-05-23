package com.astrolabs.astroexpansion.datagen;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.registry.AEBlocks;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class AEBlockStateProvider extends BlockStateProvider {
    
    public AEBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, AstroExpansion.MODID, exFileHelper);
    }
    
    @Override
    protected void registerStatesAndModels() {
        // Simple blocks
        simpleBlockWithItem(AEBlocks.TITANIUM_ORE.get());
        simpleBlockWithItem(AEBlocks.DEEPSLATE_TITANIUM_ORE.get());
        simpleBlockWithItem(AEBlocks.LITHIUM_ORE.get());
        simpleBlockWithItem(AEBlocks.URANIUM_ORE.get());
        simpleBlockWithItem(AEBlocks.TITANIUM_BLOCK.get());
        simpleBlockWithItem(AEBlocks.STEEL_BLOCK.get());
        
        // Machines
        machineBlock(AEBlocks.MATERIAL_PROCESSOR);
        machineBlock(AEBlocks.BASIC_GENERATOR);
    }
    
    private void simpleBlockWithItem(Block block) {
        simpleBlockWithItem(block, cubeAll(block));
    }
    
    private void machineBlock(DeferredBlock<Block> block) {
        ModelFile model = models().cubeAll(block.getId().getPath(), 
                modLoc("block/" + block.getId().getPath()));
        simpleBlock(block.get(), model);
        simpleBlockItem(block.get(), model);
    }
}