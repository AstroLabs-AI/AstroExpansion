package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = 
        DeferredRegister.create(ForgeRegistries.FLUIDS, AstroExpansion.MODID);
    
    public static final DeferredRegister<FluidType> FLUID_TYPES = 
        DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, AstroExpansion.MODID);
    
    // Rocket Fuel
    public static final RegistryObject<FluidType> ROCKET_FUEL_TYPE = FLUID_TYPES.register("rocket_fuel",
        () -> new FluidType(FluidType.Properties.create()
            .density(800)
            .viscosity(1500)
            .temperature(300)));
    
    public static final RegistryObject<ForgeFlowingFluid.Source> ROCKET_FUEL = FLUIDS.register("rocket_fuel",
        () -> new ForgeFlowingFluid.Source(makeFluidProperties("rocket_fuel", ROCKET_FUEL_TYPE)));
    
    public static final RegistryObject<ForgeFlowingFluid.Flowing> ROCKET_FUEL_FLOWING = FLUIDS.register("rocket_fuel_flowing",
        () -> new ForgeFlowingFluid.Flowing(makeFluidProperties("rocket_fuel", ROCKET_FUEL_TYPE)));
    
    // Liquid Oxygen
    public static final RegistryObject<FluidType> LIQUID_OXYGEN_TYPE = FLUID_TYPES.register("liquid_oxygen",
        () -> new FluidType(FluidType.Properties.create()
            .density(1141)
            .viscosity(200)
            .temperature(90)
));
    
    public static final RegistryObject<ForgeFlowingFluid.Source> LIQUID_OXYGEN = FLUIDS.register("liquid_oxygen",
        () -> new ForgeFlowingFluid.Source(makeFluidProperties("liquid_oxygen", LIQUID_OXYGEN_TYPE)));
    
    public static final RegistryObject<ForgeFlowingFluid.Flowing> LIQUID_OXYGEN_FLOWING = FLUIDS.register("liquid_oxygen_flowing",
        () -> new ForgeFlowingFluid.Flowing(makeFluidProperties("liquid_oxygen", LIQUID_OXYGEN_TYPE)));
    
    // Deuterium
    public static final RegistryObject<FluidType> DEUTERIUM_TYPE = FLUID_TYPES.register("deuterium",
        () -> new FluidType(FluidType.Properties.create()
            .density(180)
            .viscosity(100)
            .temperature(20)
));
    
    public static final RegistryObject<ForgeFlowingFluid.Source> DEUTERIUM = FLUIDS.register("deuterium",
        () -> new ForgeFlowingFluid.Source(makeFluidProperties("deuterium", DEUTERIUM_TYPE)));
    
    public static final RegistryObject<ForgeFlowingFluid.Flowing> DEUTERIUM_FLOWING = FLUIDS.register("deuterium_flowing",
        () -> new ForgeFlowingFluid.Flowing(makeFluidProperties("deuterium", DEUTERIUM_TYPE)));
    
    // Tritium
    public static final RegistryObject<FluidType> TRITIUM_TYPE = FLUID_TYPES.register("tritium",
        () -> new FluidType(FluidType.Properties.create()
            .density(250)
            .viscosity(100)
            .temperature(20)
));
    
    public static final RegistryObject<ForgeFlowingFluid.Source> TRITIUM = FLUIDS.register("tritium",
        () -> new ForgeFlowingFluid.Source(makeFluidProperties("tritium", TRITIUM_TYPE)));
    
    public static final RegistryObject<ForgeFlowingFluid.Flowing> TRITIUM_FLOWING = FLUIDS.register("tritium_flowing",
        () -> new ForgeFlowingFluid.Flowing(makeFluidProperties("tritium", TRITIUM_TYPE)));
    
    // Coolant
    public static final RegistryObject<FluidType> COOLANT_TYPE = FLUID_TYPES.register("coolant",
        () -> new FluidType(FluidType.Properties.create()
            .density(1200)
            .viscosity(2000)
            .temperature(270)
));
    
    public static final RegistryObject<ForgeFlowingFluid.Source> COOLANT = FLUIDS.register("coolant",
        () -> new ForgeFlowingFluid.Source(makeFluidProperties("coolant", COOLANT_TYPE)));
    
    public static final RegistryObject<ForgeFlowingFluid.Flowing> COOLANT_FLOWING = FLUIDS.register("coolant_flowing",
        () -> new ForgeFlowingFluid.Flowing(makeFluidProperties("coolant", COOLANT_TYPE)));
    
    // Crude Oil
    public static final RegistryObject<FluidType> CRUDE_OIL_TYPE = FLUID_TYPES.register("crude_oil",
        () -> new FluidType(FluidType.Properties.create()
            .density(900)
            .viscosity(3000)
            .temperature(300)
    ));
    
    public static final RegistryObject<ForgeFlowingFluid.Source> CRUDE_OIL = FLUIDS.register("crude_oil",
        () -> new ForgeFlowingFluid.Source(makeFluidProperties("crude_oil", CRUDE_OIL_TYPE)));
    
    public static final RegistryObject<ForgeFlowingFluid.Flowing> CRUDE_OIL_FLOWING = FLUIDS.register("crude_oil_flowing",
        () -> new ForgeFlowingFluid.Flowing(makeFluidProperties("crude_oil", CRUDE_OIL_TYPE)));
    
    private static ForgeFlowingFluid.Properties makeFluidProperties(String name, RegistryObject<FluidType> type) {
        return new ForgeFlowingFluid.Properties(type, 
            () -> FLUIDS.getEntries().stream()
                .filter(e -> e.getId().getPath().equals(name))
                .findFirst().get().get(), 
            () -> FLUIDS.getEntries().stream()
                .filter(e -> e.getId().getPath().equals(name + "_flowing"))
                .findFirst().get().get())
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);
    }
    
    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }
}