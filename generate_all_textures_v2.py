#!/usr/bin/env python3
"""
Generate all textures for AstroExpansion mod using the advanced texture generator v2.0
"""

import os
import sys
from texture_generator_v2 import TextureGenerator

def main():
    generator = TextureGenerator()
    
    # Define texture output paths
    block_path = "src/main/resources/assets/astroexpansion/textures/block"
    item_path = "src/main/resources/assets/astroexpansion/textures/item"
    
    # Ensure directories exist
    os.makedirs(block_path, exist_ok=True)
    os.makedirs(item_path, exist_ok=True)
    
    # Map item types to texture creation methods
    def generate_item_texture(item_type, material):
        if item_type == 'ore':
            # Raw ore chunks
            return generator.create_item_texture('raw_ore', material)
        elif item_type == 'ingot':
            return generator.create_ingot_texture(material)
        elif item_type == 'dust':
            return generator.create_item_texture('dust', material)
        elif item_type == 'plate':
            return generator.create_item_texture('plate', material)
        elif item_type in ['circuit', 'processor']:
            return generator.create_item_texture('circuit', material)
        elif item_type == 'drone':
            return generator.create_item_texture('drone', material)
        elif item_type == 'storage_drive':
            return generator.create_item_texture('storage_drive', material)
        elif item_type == 'tool':
            return generator.create_item_texture('tool', material)
        elif item_type in ['tank', 'canister', 'cell']:
            return generator.create_item_texture('container', material)
        elif item_type == 'data':
            return generator.create_item_texture('data_chip', material)
        elif item_type in ['component', 'coil', 'conduit']:
            return generator.create_item_texture('component', material)
        elif item_type == 'fluid':
            return generator.create_item_texture('fluid_container', material)
        elif item_type == 'rocket_part':
            return generator.create_item_texture('mechanical', material)
        else:
            return generator.create_item_texture('generic', material)
    
    # Map block types to texture creation methods
    def generate_block_texture(block_type, material):
        if block_type == 'ore_block':
            return generator.create_ore_texture(material)
        elif block_type == 'deepslate_ore':
            return generator.create_ore_texture(material, stone_color=(70, 70, 80))
        elif block_type == 'metal_block':
            return generator.create_machine_texture(material, 'block')
        elif block_type == 'machine':
            return generator.create_machine_texture(material, 'front')
        elif block_type in ['machine_face', 'machine_side', 'machine_top']:
            face_type = block_type.split('_')[-1]
            return generator.create_machine_texture(material, face_type)
        elif block_type == 'storage':
            return generator.create_machine_texture(material, 'storage')
        elif block_type in ['multiblock', 'multiblock_controller']:
            return generator.create_machine_texture(material, 'multiblock')
        elif block_type == 'conduit':
            return generator.create_machine_texture(material, 'conduit')
        elif block_type == 'dock':
            return generator.create_machine_texture(material, 'dock')
        elif block_type == 'space_block':
            return generator.create_machine_texture(material, 'space')
        elif block_type == 'space_machine':
            return generator.create_machine_texture(material, 'space_tech')
        elif block_type == 'special':
            return generator.create_machine_texture(material, 'special')
        else:
            return generator.create_machine_texture(material, 'basic')
    
    # Item textures with their types and materials
    items = {
        # Raw Materials
        'raw_titanium': ('ore', 'titanium'),
        'raw_lithium': ('ore', 'lithium'),
        'raw_uranium': ('ore', 'uranium'),
        
        # Ingots
        'titanium_ingot': ('ingot', 'titanium'),
        'lithium_ingot': ('ingot', 'lithium'),
        'uranium_ingot': ('ingot', 'uranium'),
        
        # Dusts
        'titanium_dust': ('dust', 'titanium'),
        'lithium_dust': ('dust', 'lithium'),
        'uranium_dust': ('dust', 'uranium'),
        
        # Crafted Materials
        'titanium_plate': ('plate', 'titanium'),
        'circuit_board': ('circuit', 'advanced'),
        'processor': ('processor', 'standard'),
        'advanced_processor': ('processor', 'advanced'),
        'storage_processor': ('processor', 'storage'),
        'energy_core': ('component', 'energy'),
        'fusion_core': ('component', 'fusion'),
        'plasma_injector': ('component', 'plasma'),
        
        # Drones
        'mining_drone': ('drone', 'mining'),
        'combat_drone': ('drone', 'combat'),
        'farming_drone': ('drone', 'farming'),
        'construction_drone': ('drone', 'construction'),
        'logistics_drone': ('drone', 'logistics'),
        'drone_core': ('component', 'drone'),
        'drone_shell': ('component', 'drone'),
        
        # Storage Drives
        'storage_drive_1k': ('storage_drive', '1k'),
        'storage_drive_4k': ('storage_drive', '4k'),
        'storage_drive_16k': ('storage_drive', '16k'),
        'storage_drive_64k': ('storage_drive', '64k'),
        'storage_housing': ('component', 'storage'),
        
        # Tools
        'wrench': ('tool', 'wrench'),
        
        # Space Items
        'oxygen_tank': ('tank', 'oxygen'),
        'oxygen_canister': ('canister', 'oxygen'),
        'moon_dust_item': ('dust', 'moon'),
        'empty_fuel_cell': ('cell', 'empty'),
        'filled_fuel_cell': ('cell', 'fuel'),
        
        # Research Data
        'research_data_basic': ('data', 'basic'),
        'research_data_advanced': ('data', 'advanced'),
        'research_data_space': ('data', 'space'),
        'research_data_fusion': ('data', 'fusion'),
        
        # Components
        'steel_ingot': ('ingot', 'steel'),
        'reinforced_plate': ('plate', 'reinforced'),
        'quantum_processor': ('processor', 'quantum'),
        'fusion_coil': ('coil', 'fusion'),
        'quantum_circuit': ('circuit', 'quantum'),
        'rocket_fuel': ('fluid', 'fuel'),
        'liquid_oxygen': ('fluid', 'oxygen'),
        'neutron_reflector': ('component', 'nuclear'),
        'heat_exchanger': ('component', 'thermal'),
        'cryo_stabilizer': ('component', 'cryo'),
        'plasma_conduit': ('conduit', 'plasma'),
        'nav_computer': ('component', 'navigation'),
        'rocket_cone': ('rocket_part', 'cone'),
        'rocket_fin': ('rocket_part', 'fin'),
        'rocket_thruster': ('rocket_part', 'thruster'),
        'helium_3_cell': ('cell', 'helium3'),
        'deuterium_cell': ('cell', 'deuterium'),
        'tritium_cell': ('cell', 'tritium')
    }
    
    # Block textures with their types
    blocks = {
        # Ores
        'titanium_ore': ('ore_block', 'titanium'),
        'lithium_ore': ('ore_block', 'lithium'),
        'uranium_ore': ('ore_block', 'uranium'),
        'deepslate_titanium_ore': ('deepslate_ore', 'titanium'),
        'deepslate_lithium_ore': ('deepslate_ore', 'lithium'),
        'deepslate_uranium_ore': ('deepslate_ore', 'uranium'),
        
        # Metal Blocks
        'titanium_block': ('metal_block', 'titanium'),
        'lithium_block': ('metal_block', 'lithium'),
        'uranium_block': ('metal_block', 'uranium'),
        
        # Machines
        'basic_generator': ('machine', 'generator'),
        'material_processor': ('machine', 'processor'),
        'ore_washer': ('machine', 'washer'),
        'component_assembler': ('machine', 'assembler'),
        'energy_storage': ('machine', 'battery'),
        'solar_panel': ('machine', 'solar'),
        'fluid_tank': ('machine', 'tank'),
        
        # Machine Parts (for multi-texture blocks)
        'component_assembler_front': ('machine_face', 'assembler'),
        'component_assembler_side': ('machine_side', 'assembler'),
        'component_assembler_top': ('machine_top', 'assembler'),
        'industrial_furnace_front': ('machine_face', 'furnace'),
        'industrial_furnace_side': ('machine_side', 'furnace'),
        'machine_top': ('machine_top', 'generic'),
        
        # Storage System
        'storage_core': ('storage', 'core'),
        'storage_terminal': ('storage', 'terminal'),
        'import_bus': ('storage', 'import'),
        'export_bus': ('storage', 'export'),
        
        # Multiblock Components
        'fusion_reactor_casing': ('multiblock', 'fusion'),
        'fusion_reactor_controller': ('multiblock_controller', 'fusion'),
        'furnace_casing': ('multiblock', 'furnace'),
        'industrial_furnace_controller': ('multiblock_controller', 'furnace'),
        'quantum_computer_controller': ('multiblock_controller', 'quantum'),
        
        # Docking
        'drone_dock_top': ('dock', 'top'),
        'drone_dock_side': ('dock', 'side'),
        'drone_dock_bottom': ('dock', 'bottom'),
        
        # Conduits
        'energy_conduit': ('conduit', 'energy'),
        'fluid_pipe': ('conduit', 'fluid'),
        
        # Space Station
        'station_hull': ('space_block', 'hull'),
        'station_glass': ('space_block', 'glass'),
        'docking_port': ('space_block', 'dock'),
        'life_support_system': ('space_machine', 'life_support'),
        'oxygen_generator': ('space_machine', 'oxygen'),
        
        # Special
        'teleporter': ('special', 'teleporter'),
        'research_terminal': ('special', 'research'),
        'fuel_refinery_controller': ('multiblock_controller', 'refinery'),
        'rocket_assembly_controller': ('multiblock_controller', 'rocket'),
        'refinery_casing': ('multiblock', 'refinery'),
        'distillation_column': ('multiblock', 'distillation'),
        'quantum_casing': ('multiblock', 'quantum'),
        'fusion_coil_block': ('multiblock', 'coil'),
        'fusion_core_block': ('multiblock', 'core')
    }
    
    print("Generating item textures...")
    success_count = 0
    for name, (item_type, material) in items.items():
        try:
            texture = generate_item_texture(item_type, material)
            texture.save(os.path.join(item_path, f"{name}.png"))
            success_count += 1
            print(f"  ✓ Generated {name}.png")
        except Exception as e:
            print(f"  ✗ Failed to generate {name}.png: {e}")
    
    print(f"\nGenerated {success_count}/{len(items)} item textures successfully.")
    
    print("\nGenerating block textures...")
    success_count = 0
    for name, (block_type, material) in blocks.items():
        try:
            texture = generate_block_texture(block_type, material)
            texture.save(os.path.join(block_path, f"{name}.png"))
            success_count += 1
            print(f"  ✓ Generated {name}.png")
        except Exception as e:
            print(f"  ✗ Failed to generate {name}.png: {e}")
    
    print(f"\nGenerated {success_count}/{len(blocks)} block textures successfully.")
    
    # GUI textures need to be handled differently as they're 256x256
    print("\nGUI textures require manual creation or a different approach (256x256 px)")
    
    print("\nTexture generation complete!")

if __name__ == "__main__":
    main()