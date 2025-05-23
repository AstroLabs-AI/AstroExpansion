package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.ComponentAssemblerMenu;
import com.astrolabs.astroexpansion.common.menu.MaterialProcessorMenu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AEMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = 
            DeferredRegister.create(Registries.MENU, AstroExpansion.MODID);
    
    public static final DeferredHolder<MenuType<?>, MenuType<MaterialProcessorMenu>> MATERIAL_PROCESSOR =
            MENU_TYPES.register("material_processor",
                    () -> IMenuTypeExtension.create(MaterialProcessorMenu::new));
    
    public static final DeferredHolder<MenuType<?>, MenuType<ComponentAssemblerMenu>> COMPONENT_ASSEMBLER =
            MENU_TYPES.register("component_assembler",
                    () -> IMenuTypeExtension.create(ComponentAssemblerMenu::new));
    
    public static final DeferredHolder<MenuType<?>, MenuType<com.astrolabs.astroexpansion.common.menu.ResearchConsoleMenu>> RESEARCH_CONSOLE =
            MENU_TYPES.register("research_console",
                    () -> IMenuTypeExtension.create(com.astrolabs.astroexpansion.common.menu.ResearchConsoleMenu::new));
    
    public static final DeferredHolder<MenuType<?>, MenuType<com.astrolabs.astroexpansion.common.menu.ElectricFurnaceMenu>> ELECTRIC_FURNACE =
            MENU_TYPES.register("electric_furnace",
                    () -> IMenuTypeExtension.create(com.astrolabs.astroexpansion.common.menu.ElectricFurnaceMenu::new));
}