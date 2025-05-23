package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
        DeferredRegister.create(ForgeRegistries.MENU_TYPES, AstroExpansion.MODID);
    
    public static final RegistryObject<MenuType<BasicGeneratorMenu>> BASIC_GENERATOR_MENU =
        registerMenuType(BasicGeneratorMenu::new, "basic_generator_menu");
    
    public static final RegistryObject<MenuType<MaterialProcessorMenu>> MATERIAL_PROCESSOR_MENU =
        registerMenuType(MaterialProcessorMenu::new, "material_processor_menu");
    
    public static final RegistryObject<MenuType<OreWasherMenu>> ORE_WASHER_MENU =
        registerMenuType(OreWasherMenu::new, "ore_washer_menu");
    
    public static final RegistryObject<MenuType<EnergyStorageMenu>> ENERGY_STORAGE_MENU =
        registerMenuType(EnergyStorageMenu::new, "energy_storage_menu");
    
    public static final RegistryObject<MenuType<StorageCoreMenu>> STORAGE_CORE_MENU =
        registerMenuType(StorageCoreMenu::new, "storage_core_menu");
    
    public static final RegistryObject<MenuType<StorageTerminalMenu>> STORAGE_TERMINAL_MENU =
        registerMenuType(StorageTerminalMenu::new, "storage_terminal_menu");
    
    public static final RegistryObject<MenuType<DroneDockMenu>> DRONE_DOCK_MENU =
        registerMenuType(DroneDockMenu::new, "drone_dock_menu");
    
    public static final RegistryObject<MenuType<ComponentAssemblerMenu>> COMPONENT_ASSEMBLER_MENU =
        registerMenuType(ComponentAssemblerMenu::new, "component_assembler_menu");
    
    public static final RegistryObject<MenuType<IndustrialFurnaceMenu>> INDUSTRIAL_FURNACE =
        registerMenuType(IndustrialFurnaceMenu::new, "industrial_furnace_menu");
    
    public static final RegistryObject<MenuType<FusionReactorMenu>> FUSION_REACTOR =
        registerMenuType(FusionReactorMenu::new, "fusion_reactor_menu");
    
    public static final RegistryObject<MenuType<FluidTankMenu>> FLUID_TANK =
        registerMenuType(FluidTankMenu::new, "fluid_tank_menu");
    
    public static final RegistryObject<MenuType<QuantumComputerMenu>> QUANTUM_COMPUTER =
        registerMenuType(QuantumComputerMenu::new, "quantum_computer_menu");
    
    public static final RegistryObject<MenuType<ResearchTerminalMenu>> RESEARCH_TERMINAL =
        registerMenuType(ResearchTerminalMenu::new, "research_terminal_menu");
    
    public static final RegistryObject<MenuType<FuelRefineryMenu>> FUEL_REFINERY =
        registerMenuType(FuelRefineryMenu::new, "fuel_refinery_menu");
    
    public static final RegistryObject<MenuType<RocketAssemblyMenu>> ROCKET_ASSEMBLY =
        registerMenuType(RocketAssemblyMenu::new, "rocket_assembly_menu");
    
    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }
    
    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}