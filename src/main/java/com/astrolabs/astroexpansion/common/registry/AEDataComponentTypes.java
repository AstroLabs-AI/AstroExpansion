package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AEDataComponentTypes {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = 
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, AstroExpansion.MODID);
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> OXYGEN = 
            DATA_COMPONENT_TYPES.register("oxygen",
                    () -> DataComponentType.<Integer>builder()
                            .persistent(ByteBufCodecs.VAR_INT)
                            .build());
}