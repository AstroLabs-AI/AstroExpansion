package com.astrolabs.astroexpansion.datagen;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.registry.AEItems;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

public class AEItemModelProvider extends ItemModelProvider {
    
    public AEItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, AstroExpansion.MODID, existingFileHelper);
    }
    
    @Override
    protected void registerModels() {
        // Simple items
        simpleItem(AEItems.RAW_TITANIUM);
        simpleItem(AEItems.RAW_LITHIUM);
        simpleItem(AEItems.RAW_URANIUM);
        simpleItem(AEItems.TITANIUM_INGOT);
        simpleItem(AEItems.STEEL_INGOT);
        simpleItem(AEItems.LITHIUM_INGOT);
        simpleItem(AEItems.URANIUM_INGOT);
        simpleItem(AEItems.TITANIUM_DUST);
        simpleItem(AEItems.IRON_DUST);
        simpleItem(AEItems.COAL_DUST);
        simpleItem(AEItems.CIRCUIT_BOARD);
        simpleItem(AEItems.PROCESSOR);
        simpleItem(AEItems.ADVANCED_PROCESSOR);
        simpleItem(AEItems.ENERGY_CORE);
    }
    
    private void simpleItem(DeferredItem<Item> item) {
        withExistingParent(item.getId().getPath(), "item/generated")
                .texture("layer0", modLoc("item/" + item.getId().getPath()));
    }
}