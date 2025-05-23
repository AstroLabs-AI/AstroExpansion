package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
        DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, AstroExpansion.MODID);
    
    public static final RegistryObject<SoundEvent> MACHINE_RUNNING = registerSoundEvent("machine_running");
    public static final RegistryObject<SoundEvent> GENERATOR_RUNNING = registerSoundEvent("generator_running");
    public static final RegistryObject<SoundEvent> WASHER_RUNNING = registerSoundEvent("washer_running");
    public static final RegistryObject<SoundEvent> ROCKET_LAUNCH = registerSoundEvent("rocket_launch");
    
    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation location = new ResourceLocation(AstroExpansion.MODID, name);
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(location));
    }
    
    public static void register(IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }
}