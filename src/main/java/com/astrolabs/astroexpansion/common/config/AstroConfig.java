package com.astrolabs.astroexpansion.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class AstroConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final CommonConfig COMMON;
    
    static {
        ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();
        COMMON = new CommonConfig(commonBuilder);
        COMMON_SPEC = commonBuilder.build();
    }
    
    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
    }
    
    public static class CommonConfig {
        // Energy Generation
        public final ForgeConfigSpec.IntValue basicGeneratorOutput;
        public final ForgeConfigSpec.IntValue solarPanelOutput;
        public final ForgeConfigSpec.IntValue solarPanelSpaceOutput;
        public final ForgeConfigSpec.IntValue fusionReactorOutput;
        
        // Energy Consumption - Basic Machines
        public final ForgeConfigSpec.IntValue materialProcessorUsage;
        public final ForgeConfigSpec.IntValue oreWasherUsage;
        public final ForgeConfigSpec.IntValue componentAssemblerUsage;
        public final ForgeConfigSpec.IntValue industrialFurnaceUsage;
        
        // Energy Consumption - Advanced Machines
        public final ForgeConfigSpec.IntValue fuelRefineryUsage;
        public final ForgeConfigSpec.IntValue quantumComputerUsage;
        public final ForgeConfigSpec.IntValue rocketAssemblyUsage;
        
        // Energy Consumption - Support Systems
        public final ForgeConfigSpec.IntValue storageCorePowerBase;
        public final ForgeConfigSpec.IntValue storageCorePowerPerDrive;
        public final ForgeConfigSpec.IntValue lifeSupportUsage;
        public final ForgeConfigSpec.IntValue oxygenGeneratorUsage;
        
        // Energy Consumption - Teleporters
        public final ForgeConfigSpec.IntValue teleporterUsagePerUse;
        
        // Energy Storage
        public final ForgeConfigSpec.IntValue basicEnergyStorageCapacity;
        public final ForgeConfigSpec.IntValue basicEnergyStorageTransfer;
        
        // Processing Times (in ticks)
        public final ForgeConfigSpec.IntValue materialProcessorTime;
        public final ForgeConfigSpec.IntValue oreWasherTime;
        public final ForgeConfigSpec.IntValue componentAssemblerTime;
        
        public CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Energy Configuration for AstroExpansion")
                   .push("energy");
            
            builder.comment("Energy Generation Settings").push("generation");
            
            basicGeneratorOutput = builder
                .comment("Energy output of Basic Generator (FE/tick)")
                .defineInRange("basicGeneratorOutput", 40, 1, 1000);
                
            solarPanelOutput = builder
                .comment("Energy output of Solar Panel in overworld (FE/tick)")
                .defineInRange("solarPanelOutput", 100, 1, 1000);
                
            solarPanelSpaceOutput = builder
                .comment("Energy output of Solar Panel in space (FE/tick)")
                .defineInRange("solarPanelSpaceOutput", 200, 1, 2000);
                
            fusionReactorOutput = builder
                .comment("Energy output of Fusion Reactor (FE/tick)")
                .defineInRange("fusionReactorOutput", 50000, 1000, 1000000);
                
            builder.pop();
            
            builder.comment("Energy Consumption - Basic Machines").push("basic_machines");
            
            materialProcessorUsage = builder
                .comment("Energy usage of Material Processor (FE/tick)")
                .defineInRange("materialProcessorUsage", 30, 1, 1000);
                
            oreWasherUsage = builder
                .comment("Energy usage of Ore Washer (FE/tick)")
                .defineInRange("oreWasherUsage", 20, 1, 1000);
                
            componentAssemblerUsage = builder
                .comment("Energy usage of Component Assembler (FE/tick)")
                .defineInRange("componentAssemblerUsage", 40, 1, 1000);
                
            industrialFurnaceUsage = builder
                .comment("Energy usage of Industrial Furnace per item (FE/tick)")
                .defineInRange("industrialFurnaceUsage", 20, 1, 1000);
                
            builder.pop();
            
            builder.comment("Energy Consumption - Advanced Machines").push("advanced_machines");
            
            fuelRefineryUsage = builder
                .comment("Energy usage of Fuel Refinery (FE/tick)")
                .defineInRange("fuelRefineryUsage", 500, 10, 10000);
                
            quantumComputerUsage = builder
                .comment("Energy usage of Quantum Computer (FE/tick)")
                .defineInRange("quantumComputerUsage", 2000, 100, 50000);
                
            rocketAssemblyUsage = builder
                .comment("Energy usage of Rocket Assembly during construction (FE/tick)")
                .defineInRange("rocketAssemblyUsage", 500, 10, 10000);
                
            builder.pop();
            
            builder.comment("Energy Consumption - Support Systems").push("support_systems");
            
            storageCorePowerBase = builder
                .comment("Base energy usage of Storage Core (FE/tick)")
                .defineInRange("storageCorePowerBase", 20, 1, 1000);
                
            storageCorePowerPerDrive = builder
                .comment("Additional energy per storage drive (FE/tick)")
                .defineInRange("storageCorePowerPerDrive", 5, 1, 100);
                
            lifeSupportUsage = builder
                .comment("Energy usage of Life Support System (FE/tick)")
                .defineInRange("lifeSupportUsage", 50, 1, 1000);
                
            oxygenGeneratorUsage = builder
                .comment("Energy usage of Oxygen Generator (FE/tick)")
                .defineInRange("oxygenGeneratorUsage", 20, 1, 1000);
                
            builder.pop();
            
            builder.comment("Energy Consumption - Teleporters").push("teleporters");
            
            teleporterUsagePerUse = builder
                .comment("Energy required per teleportation (FE)")
                .defineInRange("teleporterUsagePerUse", 10000, 100, 1000000);
                
            builder.pop();
            
            builder.comment("Energy Storage").push("storage");
            
            basicEnergyStorageCapacity = builder
                .comment("Capacity of basic Energy Storage block (FE)")
                .defineInRange("basicEnergyStorageCapacity", 100000, 1000, 10000000);
                
            basicEnergyStorageTransfer = builder
                .comment("Max transfer rate of Energy Storage block (FE/tick)")
                .defineInRange("basicEnergyStorageTransfer", 1000, 10, 100000);
                
            builder.pop();
            
            builder.comment("Processing Times").push("processing");
            
            materialProcessorTime = builder
                .comment("Processing time for Material Processor (ticks)")
                .defineInRange("materialProcessorTime", 200, 20, 6000);
                
            oreWasherTime = builder
                .comment("Processing time for Ore Washer (ticks)")
                .defineInRange("oreWasherTime", 200, 20, 6000);
                
            componentAssemblerTime = builder
                .comment("Base processing time for Component Assembler (ticks)")
                .defineInRange("componentAssemblerTime", 200, 20, 6000);
                
            builder.pop();
            builder.pop();
        }
    }
}