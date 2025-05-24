package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.entities.drones.MiningDroneEntity;
import com.astrolabs.astroexpansion.common.entities.*;
import com.astrolabs.astroexpansion.common.entities.vehicles.LunarRoverEntity;
import com.astrolabs.astroexpansion.common.entity.ProbeRocketEntity;
import com.astrolabs.astroexpansion.common.entity.PersonalRocketEntity;
import com.astrolabs.astroexpansion.common.entity.CargoShuttleEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AstroExpansion.MODID);
    
    public static final RegistryObject<EntityType<MiningDroneEntity>> MINING_DRONE =
        ENTITY_TYPES.register("mining_drone",
            () -> EntityType.Builder.<MiningDroneEntity>of(MiningDroneEntity::new, MobCategory.MISC)
                .sized(0.6F, 0.6F)
                .clientTrackingRange(8)
                .build(new ResourceLocation(AstroExpansion.MODID, "mining_drone").toString()));
    
    public static final RegistryObject<EntityType<ConstructionDroneEntity>> CONSTRUCTION_DRONE =
        ENTITY_TYPES.register("construction_drone",
            () -> EntityType.Builder.<ConstructionDroneEntity>of(ConstructionDroneEntity::new, MobCategory.MISC)
                .sized(0.6F, 0.6F)
                .clientTrackingRange(8)
                .build(new ResourceLocation(AstroExpansion.MODID, "construction_drone").toString()));
    
    public static final RegistryObject<EntityType<FarmingDroneEntity>> FARMING_DRONE =
        ENTITY_TYPES.register("farming_drone",
            () -> EntityType.Builder.<FarmingDroneEntity>of(FarmingDroneEntity::new, MobCategory.MISC)
                .sized(0.6F, 0.6F)
                .clientTrackingRange(8)
                .build(new ResourceLocation(AstroExpansion.MODID, "farming_drone").toString()));
    
    public static final RegistryObject<EntityType<CombatDroneEntity>> COMBAT_DRONE =
        ENTITY_TYPES.register("combat_drone",
            () -> EntityType.Builder.<CombatDroneEntity>of(CombatDroneEntity::new, MobCategory.MISC)
                .sized(0.6F, 0.6F)
                .clientTrackingRange(8)
                .build(new ResourceLocation(AstroExpansion.MODID, "combat_drone").toString()));
    
    public static final RegistryObject<EntityType<LogisticsDroneEntity>> LOGISTICS_DRONE =
        ENTITY_TYPES.register("logistics_drone",
            () -> EntityType.Builder.<LogisticsDroneEntity>of(LogisticsDroneEntity::new, MobCategory.MISC)
                .sized(0.6F, 0.6F)
                .clientTrackingRange(8)
                .build(new ResourceLocation(AstroExpansion.MODID, "logistics_drone").toString()));
    
    public static final RegistryObject<EntityType<LunarRoverEntity>> LUNAR_ROVER =
        ENTITY_TYPES.register("lunar_rover",
            () -> EntityType.Builder.<LunarRoverEntity>of(LunarRoverEntity::new, MobCategory.MISC)
                .sized(2.0F, 1.5F)
                .clientTrackingRange(10)
                .build(new ResourceLocation(AstroExpansion.MODID, "lunar_rover").toString()));
    
    public static final RegistryObject<EntityType<ProbeRocketEntity>> PROBE_ROCKET =
        ENTITY_TYPES.register("probe_rocket",
            () -> EntityType.Builder.<ProbeRocketEntity>of(ProbeRocketEntity::new, MobCategory.MISC)
                .sized(1.0F, 2.0F)
                .clientTrackingRange(10)
                .build(new ResourceLocation(AstroExpansion.MODID, "probe_rocket").toString()));
    
    public static final RegistryObject<EntityType<PersonalRocketEntity>> PERSONAL_ROCKET =
        ENTITY_TYPES.register("personal_rocket",
            () -> EntityType.Builder.<PersonalRocketEntity>of(PersonalRocketEntity::new, MobCategory.MISC)
                .sized(1.5F, 3.0F)
                .clientTrackingRange(10)
                .build(new ResourceLocation(AstroExpansion.MODID, "personal_rocket").toString()));
    
    public static final RegistryObject<EntityType<CargoShuttleEntity>> CARGO_SHUTTLE =
        ENTITY_TYPES.register("cargo_shuttle",
            () -> EntityType.Builder.<CargoShuttleEntity>of(CargoShuttleEntity::new, MobCategory.MISC)
                .sized(2.0F, 4.0F)
                .clientTrackingRange(10)
                .build(new ResourceLocation(AstroExpansion.MODID, "cargo_shuttle").toString()));
    
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}