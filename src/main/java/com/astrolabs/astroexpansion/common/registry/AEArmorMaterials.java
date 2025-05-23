package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;

public class AEArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = 
            DeferredRegister.create(Registries.ARMOR_MATERIAL, AstroExpansion.MODID);
    
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SPACE_SUIT = ARMOR_MATERIALS.register("space_suit",
            () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorType.class), map -> {
                        map.put(ArmorType.BOOTS, 3);
                        map.put(ArmorType.LEGGINGS, 6);
                        map.put(ArmorType.CHESTPLATE, 8);
                        map.put(ArmorType.HELMET, 3);
                    }),
                    15,
                    SoundEvents.ARMOR_EQUIP_IRON,
                    () -> AEItems.TITANIUM_INGOT.get(),
                    ResourceLocation.fromNamespaceAndPath(AstroExpansion.MODID, "space_suit"),
                    2.0F,
                    0.1F
            ));
}