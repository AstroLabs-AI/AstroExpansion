#!/usr/bin/env python3
"""
Complete texture generation script using Advanced Texture Generator v3
Generates all unique, detailed textures for AstroExpansion
"""

import os
import sys
from texture_generator_v3 import AdvancedTextureGenerator

def main():
    generator = AdvancedTextureGenerator()
    
    # Create a mapping of remaining textures that need unique generation
    additional_items = {
        # Raw materials with unique patterns
        'raw_titanium': lambda: generator.generate_special_item('raw_ore', 'titanium'),
        'raw_lithium': lambda: generator.generate_special_item('raw_ore', 'lithium'), 
        'raw_uranium': lambda: generator.generate_special_item('raw_ore', 'uranium'),
        
        # Components with unique designs
        'circuit_board': lambda: generator.generate_special_item('circuit_board', 'advanced'),
        'drone_core': lambda: generator.generate_special_item('drone_component', 'core'),
        'drone_shell': lambda: generator.generate_special_item('drone_component', 'shell'),
        'storage_housing': lambda: generator.generate_special_item('storage_component', 'housing'),
        
        # Advanced components
        'plasma_injector': lambda: generator.generate_special_item('tech_component', 'plasma'),
        'fusion_coil': lambda: generator.generate_special_item('tech_component', 'coil'),
        'quantum_circuit': lambda: generator.generate_special_item('circuit_board', 'quantum'),
        'neutron_reflector': lambda: generator.generate_special_item('tech_component', 'nuclear'),
        'heat_exchanger': lambda: generator.generate_special_item('tech_component', 'thermal'),
        'cryo_stabilizer': lambda: generator.generate_special_item('tech_component', 'cryo'),
        'plasma_conduit': lambda: generator.generate_special_item('tech_component', 'conduit'),
        'nav_computer': lambda: generator.generate_special_item('tech_component', 'navigation'),
        
        # Fluids
        'rocket_fuel': lambda: generator.generate_special_item('fluid_container', 'fuel'),
        'liquid_oxygen': lambda: generator.generate_special_item('fluid_container', 'oxygen'),
        'oxygen_tank': lambda: generator.generate_special_item('tank', 'oxygen'),
        'oxygen_canister': lambda: generator.generate_special_item('canister', 'oxygen'),
    }
    
    additional_blocks = {
        # Metal blocks with unique patterns
        'titanium_block': lambda: generator.generate_special_block('metal_block', 'titanium'),
        'lithium_block': lambda: generator.generate_special_block('metal_block', 'lithium'),
        'uranium_block': lambda: generator.generate_special_block('metal_block', 'uranium'),
        
        # Machine variations
        'ore_washer': lambda: generator.generate_machine_block('washer', 'front'),
        'solar_panel': lambda: generator.generate_special_block('solar_panel', 'default'),
        'fluid_tank': lambda: generator.generate_special_block('tank_block', 'default'),
        
        # Machine sides/tops
        'component_assembler_front': lambda: generator.generate_machine_block('assembler', 'front'),
        'component_assembler_side': lambda: generator.generate_machine_block('assembler', 'side'),
        'component_assembler_top': lambda: generator.generate_machine_block('assembler', 'top'),
        'industrial_furnace_front': lambda: generator.generate_machine_block('furnace', 'front'),
        'industrial_furnace_side': lambda: generator.generate_machine_block('furnace', 'side'),
        'machine_top': lambda: generator.generate_machine_block('generic', 'top'),
        
        # Storage system
        'storage_core': lambda: generator.generate_special_block('storage_core', 'default'),
        'storage_terminal': lambda: generator.generate_special_block('storage_terminal', 'default'),
        'import_bus': lambda: generator.generate_special_block('storage_bus', 'import'),
        'export_bus': lambda: generator.generate_special_block('storage_bus', 'export'),
        
        # Multiblock components
        'furnace_casing': lambda: generator.generate_multiblock_part('furnace', 'casing'),
        'industrial_furnace_controller': lambda: generator.generate_multiblock_part('furnace', 'controller'),
        'fuel_refinery_controller': lambda: generator.generate_multiblock_part('refinery', 'controller'),
        'rocket_assembly_controller': lambda: generator.generate_multiblock_part('rocket', 'controller'),
        'refinery_casing': lambda: generator.generate_multiblock_part('refinery', 'casing'),
        'distillation_column': lambda: generator.generate_special_block('distillation', 'default'),
        'fusion_coil_block': lambda: generator.generate_special_block('fusion_coil', 'default'),
        'fusion_core_block': lambda: generator.generate_special_block('fusion_core', 'default'),
        
        # Conduits
        'energy_conduit': lambda: generator.generate_special_block('conduit', 'energy'),
        'fluid_pipe': lambda: generator.generate_special_block('conduit', 'fluid'),
        
        # Drone dock
        'drone_dock_top': lambda: generator.generate_special_block('drone_dock', 'top'),
        'drone_dock_side': lambda: generator.generate_special_block('drone_dock', 'side'),
        'drone_dock_bottom': lambda: generator.generate_special_block('drone_dock', 'bottom'),
        
        # Space blocks
        'station_hull': lambda: generator.generate_special_block('space_block', 'hull'),
        'station_glass': lambda: generator.generate_special_block('space_block', 'glass'),
        'docking_port': lambda: generator.generate_special_block('space_block', 'docking'),
        'life_support_system': lambda: generator.generate_special_block('space_machine', 'life_support'),
        'oxygen_generator': lambda: generator.generate_special_block('space_machine', 'oxygen'),
        
        # Special blocks
        'teleporter': lambda: generator.generate_special_block('teleporter', 'default'),
        'research_terminal': lambda: generator.generate_special_block('research_terminal', 'default'),
    }
    
    # First, extend the generator with methods for these special cases
    extend_generator(generator)
    
    # Generate items from texture_generator_v3
    print("Generating detailed item textures...")
    item_path = "src/main/resources/assets/astroexpansion/textures/item"
    os.makedirs(item_path, exist_ok=True)
    
    # Get items from original generator
    from texture_generator_v3 import main as v3_main
    original_items = {
        'titanium_ingot': lambda: generator.generate_ingot('titanium'),
        'lithium_ingot': lambda: generator.generate_ingot('lithium'),
        'uranium_ingot': lambda: generator.generate_ingot('uranium'),
        'steel_ingot': lambda: generator.generate_ingot('steel'),
        'titanium_dust': lambda: generator.generate_dust('titanium'),
        'lithium_dust': lambda: generator.generate_dust('lithium'),
        'uranium_dust': lambda: generator.generate_dust('uranium'),
        'moon_dust_item': lambda: generator.generate_dust('moon'),
        'titanium_plate': lambda: generator.generate_plate('titanium'),
        'reinforced_plate': lambda: generator.generate_plate('reinforced'),
        'processor': lambda: generator.generate_processor('basic'),
        'advanced_processor': lambda: generator.generate_processor('advanced'),
        'quantum_processor': lambda: generator.generate_processor('quantum'),
        'storage_processor': lambda: generator.generate_processor('storage'),
        'mining_drone': lambda: generator.generate_drone('mining'),
        'combat_drone': lambda: generator.generate_drone('combat'),
        'farming_drone': lambda: generator.generate_drone('farming'),
        'construction_drone': lambda: generator.generate_drone('construction'),
        'logistics_drone': lambda: generator.generate_drone('logistics'),
        'storage_drive_1k': lambda: generator.generate_storage_drive('1k'),
        'storage_drive_4k': lambda: generator.generate_storage_drive('4k'),
        'storage_drive_16k': lambda: generator.generate_storage_drive('16k'),
        'storage_drive_64k': lambda: generator.generate_storage_drive('64k'),
        'energy_core': lambda: generator.generate_special_item('energy_core'),
        'fusion_core': lambda: generator.generate_special_item('fusion_core'),
        'wrench': lambda: generator.generate_special_item('wrench'),
        'research_data_basic': lambda: generator.generate_special_item('research_data', 'basic'),
        'research_data_advanced': lambda: generator.generate_special_item('research_data', 'advanced'),
        'research_data_space': lambda: generator.generate_special_item('research_data', 'space'),
        'research_data_fusion': lambda: generator.generate_special_item('research_data', 'fusion'),
        'empty_fuel_cell': lambda: generator.generate_special_item('cell', 'empty'),
        'filled_fuel_cell': lambda: generator.generate_special_item('cell', 'fuel'),
        'deuterium_cell': lambda: generator.generate_special_item('cell', 'deuterium'),
        'tritium_cell': lambda: generator.generate_special_item('cell', 'tritium'),
        'helium_3_cell': lambda: generator.generate_special_item('cell', 'helium'),
        'rocket_cone': lambda: generator.generate_special_item('rocket_part', 'cone'),
        'rocket_fin': lambda: generator.generate_special_item('rocket_part', 'fin'),
        'rocket_thruster': lambda: generator.generate_special_item('rocket_part', 'thruster'),
    }
    
    # Combine all items
    all_items = {**original_items, **additional_items}
    
    success_count = 0
    for name, generator_func in all_items.items():
        try:
            texture = generator_func()
            texture.save(os.path.join(item_path, f"{name}.png"))
            success_count += 1
            print(f"  ✓ Generated {name}.png")
        except Exception as e:
            print(f"  ✗ Failed to generate {name}.png: {e}")
    
    print(f"\nGenerated {success_count}/{len(all_items)} item textures successfully.")
    
    # Generate blocks
    print("\nGenerating detailed block textures...")
    block_path = "src/main/resources/assets/astroexpansion/textures/block"
    os.makedirs(block_path, exist_ok=True)
    
    # Get blocks from original generator
    original_blocks = {
        'titanium_ore': lambda: generator.generate_ore_block('titanium'),
        'lithium_ore': lambda: generator.generate_ore_block('lithium'),
        'uranium_ore': lambda: generator.generate_ore_block('uranium'),
        'deepslate_titanium_ore': lambda: generator.generate_ore_block('titanium', 'deepslate'),
        'deepslate_lithium_ore': lambda: generator.generate_ore_block('lithium', 'deepslate'),
        'deepslate_uranium_ore': lambda: generator.generate_ore_block('uranium', 'deepslate'),
        'basic_generator': lambda: generator.generate_machine_block('generator', 'front'),
        'material_processor': lambda: generator.generate_machine_block('processor', 'front'),
        'component_assembler': lambda: generator.generate_machine_block('assembler', 'front'),
        'energy_storage': lambda: generator.generate_machine_block('storage', 'front'),
        'fusion_reactor_casing': lambda: generator.generate_multiblock_part('fusion', 'casing'),
        'fusion_reactor_controller': lambda: generator.generate_multiblock_part('fusion', 'controller'),
        'quantum_computer_controller': lambda: generator.generate_multiblock_part('quantum', 'controller'),
        'quantum_casing': lambda: generator.generate_multiblock_part('quantum', 'casing'),
    }
    
    # Combine all blocks
    all_blocks = {**original_blocks, **additional_blocks}
    
    success_count = 0
    for name, generator_func in all_blocks.items():
        try:
            texture = generator_func()
            texture.save(os.path.join(block_path, f"{name}.png"))
            success_count += 1
            print(f"  ✓ Generated {name}.png")
        except Exception as e:
            print(f"  ✗ Failed to generate {name}.png: {e}")
    
    print(f"\nGenerated {success_count}/{len(all_blocks)} block textures successfully.")
    print("\nAdvanced texture generation complete!")

def extend_generator(generator):
    """Extend the generator with additional methods for special textures"""
    
    # Save original methods if they exist
    if hasattr(generator, 'generate_special_item'):
        generator._original_generate_special_item = generator.generate_special_item
    if hasattr(generator, 'generate_machine_block'):
        generator._original_generate_machine_block = generator.generate_machine_block
    if hasattr(generator, 'generate_multiblock_part'):
        generator._original_generate_multiblock_part = generator.generate_multiblock_part
    
    def generate_special_item(self, item_type: str, variant: str = 'default'):
        """Extended special item generator"""
        img = Image.new('RGBA', (self.size, self.size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        if item_type == 'raw_ore':
            # Raw ore chunk with rough texture
            colors = self.materials.get(variant, self.materials['titanium'])
            
            # Irregular chunk shape
            points = [(4, 3), (8, 2), (11, 3), (13, 6), (12, 9), (11, 11), 
                     (8, 13), (5, 12), (3, 10), (2, 7), (3, 5)]
            draw.polygon(points, fill=(*colors['base'], 255))
            
            # Add depth with shading
            for i in range(len(points)):
                p1 = points[i]
                p2 = points[(i + 1) % len(points)]
                if p1[1] < p2[1]:  # Edge going down = shadow
                    draw.line([p1, p2], fill=(*colors['dark'], 200), width=1)
                else:  # Edge going up = highlight
                    draw.line([p1, p2], fill=(*colors['light'], 200), width=1)
            
            # Ore veins
            for _ in range(15):
                x = random.randint(4, 11)
                y = random.randint(3, 12)
                draw.point((x, y), fill=(*colors['accent'], random.randint(150, 255)))
            
            # Rough texture
            img = self.add_noise(img, 0.1)
            
        elif item_type == 'circuit_board':
            # Detailed PCB
            pcb_color = (0, 80, 0) if variant == 'advanced' else (80, 0, 120) if variant == 'quantum' else (100, 50, 0)
            draw.rectangle([1, 1, 14, 14], fill=(*pcb_color, 255))
            
            # Complex trace pattern
            img = self.create_circuit_pattern(img, (200, 170, 0))
            
            # Additional components
            # Capacitors
            for pos in [(3, 3), (12, 12)]:
                draw.ellipse([pos[0]-1, pos[1]-1, pos[0]+1, pos[1]+1], 
                            fill=(100, 100, 200, 255))
            
            # Chips
            for pos in [(7, 4), (4, 10), (10, 10)]:
                draw.rectangle([pos[0], pos[1], pos[0]+2, pos[1]+2], 
                              fill=(40, 40, 40, 255))
            
            # LEDs
            if variant == 'quantum':
                for pos in [(2, 13), (13, 2)]:
                    draw.point(pos, fill=(255, 0, 255, 255))
                    
        elif item_type == 'drone_component':
            if variant == 'core':
                # Drone AI core
                draw.ellipse([3, 3, 12, 12], fill=(60, 60, 80, 255))
                draw.ellipse([4, 4, 11, 11], fill=(40, 40, 60, 255))
                
                # Neural network pattern
                center = 7
                for angle in range(0, 360, 30):
                    rad = math.radians(angle)
                    x = int(center + 3 * math.cos(rad))
                    y = int(center + 3 * math.sin(rad))
                    draw.line([(center, center), (x, y)], 
                             fill=(100, 200, 255, 150), width=1)
                
                # Central processor
                draw.ellipse([6, 6, 9, 9], fill=(0, 150, 255, 255))
                draw.point((7, 7), fill=(255, 255, 255, 255))
                
            elif variant == 'shell':
                # Drone shell/frame
                # Outer frame
                draw.polygon([(7, 1), (13, 4), (13, 11), (7, 14), (2, 11), (2, 4)],
                            outline=(180, 180, 180, 255), width=2)
                
                # Inner structure
                draw.line([(2, 4), (13, 11)], fill=(150, 150, 150, 255), width=1)
                draw.line([(13, 4), (2, 11)], fill=(150, 150, 150, 255), width=1)
                
                # Mounting points
                for pos in [(7, 4), (7, 11), (4, 7), (10, 7)]:
                    draw.ellipse([pos[0]-1, pos[1]-1, pos[0]+1, pos[1]+1],
                                fill=(100, 100, 100, 255))
                                
        elif item_type == 'storage_component':
            if variant == 'housing':
                # Storage drive housing
                draw.rectangle([2, 1, 13, 14], fill=(80, 80, 90, 255))
                draw.rectangle([3, 2, 12, 13], fill=(60, 60, 70, 255))
                
                # Drive bays
                for row in range(2):
                    for col in range(2):
                        x = 4 + col * 5
                        y = 3 + row * 5
                        draw.rectangle([x, y, x+3, y+3], fill=(40, 40, 50, 255))
                        draw.rectangle([x+1, y+3, x+2, y+4], fill=(0, 255, 0, 180))
                
                # Ventilation
                for y in range(12, 14):
                    draw.line([(4, y), (11, y)], fill=(50, 50, 60, 255), width=1)
                    
        elif item_type == 'tech_component':
            if variant == 'plasma':
                # Plasma injector
                # Main body
                draw.rectangle([5, 2, 10, 12], fill=(100, 100, 120, 255))
                
                # Plasma chamber
                draw.ellipse([6, 3, 9, 8], fill=(200, 100, 200, 255))
                draw.ellipse([7, 4, 8, 7], fill=(255, 200, 255, 255))
                
                # Nozzle
                draw.polygon([(6, 9), (9, 9), (7, 13)], fill=(80, 80, 100, 255))
                
            elif variant == 'coil':
                # Electromagnetic coil
                center = 8
                for radius in range(2, 7):
                    color_intensity = 255 - (radius - 2) * 30
                    draw.ellipse([center-radius, center-radius, center+radius-1, center+radius-1],
                                outline=(color_intensity, color_intensity//2, color_intensity, 255),
                                width=1)
                
                # Core
                draw.ellipse([6, 6, 9, 9], fill=(80, 80, 100, 255))
                
            elif variant == 'thermal':
                # Heat exchanger
                # Fins
                for y in range(2, 14, 2):
                    draw.rectangle([3, y, 12, y+1], fill=(150, 150, 160, 255))
                
                # Heat pipes
                draw.rectangle([2, 1, 3, 14], fill=(200, 100, 0, 255))
                draw.rectangle([12, 1, 13, 14], fill=(200, 100, 0, 255))
                
        elif item_type == 'tank':
            # Pressure tank
            # Main cylinder
            draw.ellipse([4, 1, 11, 4], fill=(150, 150, 160, 255))
            draw.rectangle([4, 2, 11, 12], fill=(150, 150, 160, 255))
            draw.ellipse([4, 11, 11, 14], fill=(130, 130, 140, 255))
            
            # Valve
            draw.rectangle([7, 0, 8, 2], fill=(100, 100, 100, 255))
            
            # Pressure gauge
            draw.ellipse([6, 6, 9, 9], fill=(200, 200, 200, 255))
            draw.ellipse([7, 7, 8, 8], fill=(255, 0, 0, 255))
            
        # Use original method for other types
        else:
            # Call the original method if it exists
            if hasattr(self, '_original_generate_special_item'):
                return self._original_generate_special_item(item_type, variant)
            else:
                # Default fallback
                return img
        
        return img
    
    def generate_special_block(self, block_type: str, variant: str = 'default'):
        """Generate special block textures"""
        img = Image.new('RGBA', (self.size, self.size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        if block_type == 'metal_block':
            # Solid metal block with panels
            colors = self.materials.get(variant, self.materials['steel'])
            
            # Base
            draw.rectangle([0, 0, 15, 15], fill=(*colors['base'], 255))
            
            # Panel lines creating 4x4 grid
            for i in range(0, 16, 4):
                # Horizontal
                draw.line([(0, i), (15, i)], fill=(*colors['dark'], 255), width=1)
                # Vertical
                draw.line([(i, 0), (i, 15)], fill=(*colors['dark'], 255), width=1)
            
            # Rivets at intersections
            for x in range(4, 16, 4):
                for y in range(4, 16, 4):
                    draw.ellipse([x-1, y-1, x, y], fill=(*colors['light'], 255))
            
            # Material-specific effects
            if variant == 'uranium':
                # Radiation warning
                draw.polygon([(7, 3), (10, 8), (4, 8)], outline=(255, 255, 0, 200), width=1)
                
        elif block_type == 'solar_panel':
            # Solar panel texture
            # Frame
            draw.rectangle([0, 0, 15, 15], fill=(60, 60, 70, 255))
            
            # Solar cells
            for row in range(4):
                for col in range(4):
                    x = 1 + col * 3
                    y = 1 + row * 3
                    draw.rectangle([x, y, x+2, y+2], fill=(20, 20, 40, 255))
                    # Cell lines
                    draw.line([(x+1, y), (x+1, y+2)], fill=(40, 40, 80, 255))
                    draw.line([(x, y+1), (x+2, y+1)], fill=(40, 40, 80, 255))
                    # Reflection
                    if (row + col) % 2 == 0:
                        draw.point((x, y), fill=(100, 100, 200, 180))
                        
        elif block_type == 'storage_core':
            # Storage core with active pattern
            # Base
            draw.rectangle([0, 0, 15, 15], fill=(80, 80, 90, 255))
            
            # Central display
            draw.rectangle([3, 3, 12, 12], fill=(20, 20, 30, 255))
            
            # Data streams
            for i in range(4, 12, 2):
                # Vertical
                alpha = 100 + random.randint(0, 155)
                draw.line([(i, 3), (i, 12)], fill=(0, 200, 255, alpha), width=1)
                # Horizontal
                alpha = 100 + random.randint(0, 155)
                draw.line([(3, i), (12, i)], fill=(0, 200, 255, alpha), width=1)
            
            # Corner indicators
            for x, y in [(1, 1), (14, 1), (1, 14), (14, 14)]:
                draw.point((x, y), fill=(0, 255, 0, 255))
                
        elif block_type == 'conduit':
            if variant == 'energy':
                # Energy conduit
                # Outer casing
                draw.rectangle([0, 0, 15, 15], fill=(100, 100, 110, 255))
                
                # Inner conduit
                draw.rectangle([4, 4, 11, 11], fill=(60, 60, 70, 255))
                
                # Energy core
                draw.rectangle([6, 6, 9, 9], fill=(255, 100, 100, 255))
                draw.point((7, 7), fill=(255, 255, 255, 255))
                
                # Connection points
                for pos in [(7, 0), (0, 7), (7, 15), (15, 7)]:
                    draw.rectangle([pos[0]-1, pos[1]-1, pos[0]+1, pos[1]+1],
                                  fill=(150, 150, 160, 255))
                                  
            elif variant == 'fluid':
                # Fluid pipe
                # Outer pipe
                draw.rectangle([0, 0, 15, 15], fill=(120, 120, 130, 255))
                
                # Inner pipe
                draw.ellipse([3, 3, 12, 12], fill=(80, 80, 90, 255))
                draw.ellipse([4, 4, 11, 11], fill=(60, 60, 70, 255))
                
                # Fluid window
                draw.ellipse([5, 5, 10, 10], fill=(100, 150, 200, 100))
                
                # Connector rings
                for pos in [2, 13]:
                    draw.line([(pos, 0), (pos, 15)], fill=(100, 100, 110, 255), width=1)
                    draw.line([(0, pos), (15, pos)], fill=(100, 100, 110, 255), width=1)
                    
        elif block_type == 'drone_dock':
            base_color = (130, 130, 140)
            
            if variant == 'top':
                # Landing pad pattern
                draw.rectangle([0, 0, 15, 15], fill=(*base_color, 255))
                
                # Landing circle
                draw.ellipse([2, 2, 13, 13], outline=(255, 200, 0, 255), width=2)
                
                # Directional arrows
                # North
                draw.polygon([(7, 3), (5, 5), (9, 5)], fill=(255, 200, 0, 255))
                
                # Center pad
                draw.ellipse([6, 6, 9, 9], fill=(100, 100, 110, 255))
                
            elif variant == 'side':
                # Dock side with status lights
                draw.rectangle([0, 0, 15, 15], fill=(*base_color, 255))
                
                # Status panel
                draw.rectangle([2, 2, 13, 8], fill=(40, 40, 50, 255))
                
                # Status lights
                for i, color in enumerate([(255, 0, 0), (255, 255, 0), (0, 255, 0)]):
                    draw.ellipse([4 + i*3, 4, 5 + i*3, 5], fill=(*color, 255))
                    
                # Maintenance hatch
                draw.rectangle([4, 10, 11, 13], fill=(80, 80, 90, 255))
                draw.line([(7, 10), (7, 13)], fill=(60, 60, 70, 255), width=1)
                
        elif block_type == 'space_machine':
            if variant == 'life_support':
                # Life support system
                draw.rectangle([0, 0, 15, 15], fill=(140, 140, 150, 255))
                
                # Air processing unit
                draw.ellipse([3, 3, 12, 12], fill=(100, 100, 110, 255))
                draw.ellipse([4, 4, 11, 11], fill=(80, 80, 90, 255))
                
                # Fan blades
                center = 7
                for angle in range(0, 360, 60):
                    rad = math.radians(angle)
                    x = int(center + 3 * math.cos(rad))
                    y = int(center + 3 * math.sin(rad))
                    draw.line([(center, center), (x, y)], fill=(120, 120, 130, 255), width=1)
                
                # Status light
                draw.ellipse([7, 7, 8, 8], fill=(0, 255, 0, 255))
                
        elif block_type == 'teleporter':
            # Teleporter pad
            # Frame
            draw.rectangle([0, 0, 15, 15], fill=(100, 80, 120, 255))
            
            # Portal area
            draw.rectangle([2, 2, 13, 13], fill=(20, 0, 40, 255))
            
            # Energy field effect
            for radius in range(1, 6):
                alpha = 255 - radius * 40
                color = (150 - radius * 20, 100 - radius * 10, 200 - radius * 20)
                draw.ellipse([7-radius, 7-radius, 8+radius, 8+radius],
                            outline=(*color, alpha), width=1)
            
            # Corner crystals
            for x, y in [(2, 2), (13, 2), (2, 13), (13, 13)]:
                draw.point((x, y), fill=(255, 100, 255, 255))
                
        elif block_type == 'tank_block':
            # Fluid tank block
            # Tank walls
            draw.rectangle([0, 0, 15, 15], fill=(130, 130, 140, 255))
            
            # Glass window
            draw.rectangle([2, 2, 13, 13], fill=(200, 200, 210, 100))
            draw.rectangle([3, 3, 12, 12], fill=(180, 200, 220, 80))
            
            # Fluid level (empty state)
            draw.rectangle([4, 10, 11, 12], fill=(100, 150, 200, 150))
            
            # Frame details
            for i in [1, 14]:
                draw.line([(i, 0), (i, 15)], fill=(100, 100, 110, 255), width=1)
                draw.line([(0, i), (15, i)], fill=(100, 100, 110, 255), width=1)
        
        # Apply common effects
        img = self.add_noise(img, 0.02)
        
        return img
    
    def generate_machine_block(self, machine_type: str, face: str = 'front'):
        """Extended machine block generator with more variety"""
        img = Image.new('RGBA', (self.size, self.size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # Base casing varies by machine
        machine_colors = {
            'washer': (120, 130, 150),
            'furnace': (140, 100, 80),
            'assembler': (130, 140, 150),
            'generic': (140, 140, 150)
        }
        
        base_color = machine_colors.get(machine_type, machine_colors['generic'])
        draw.rectangle([0, 0, 15, 15], fill=(*base_color, 255))
        
        # Common edges
        if face != 'top':
            draw.line([(0, 0), (15, 0)], fill=tuple(int(c * 1.2) for c in base_color) + (255,), width=1)
            draw.line([(0, 0), (0, 15)], fill=tuple(int(c * 1.2) for c in base_color) + (255,), width=1)
            draw.line([(15, 1), (15, 15)], fill=tuple(int(c * 0.8) for c in base_color) + (255,), width=1)
            draw.line([(1, 15), (15, 15)], fill=tuple(int(c * 0.8) for c in base_color) + (255,), width=1)
        
        if machine_type == 'washer' and face == 'front':
            # Water input
            draw.rectangle([2, 2, 5, 5], fill=(100, 150, 200, 255))
            draw.ellipse([3, 3, 4, 4], fill=(150, 200, 255, 255))
            
            # Washing drum
            draw.ellipse([6, 4, 13, 11], fill=(80, 80, 90, 255))
            draw.ellipse([7, 5, 12, 10], fill=(60, 60, 70, 255))
            
            # Control panel
            for x in range(2, 14, 3):
                draw.rectangle([x, 13, x+1, 14], fill=(100, 255, 100, 255))
                
        elif machine_type == 'furnace' and face == 'front':
            # Furnace door
            draw.rectangle([3, 3, 12, 10], fill=(60, 40, 30, 255))
            draw.rectangle([4, 4, 11, 9], fill=(40, 20, 10, 255))
            
            # Fire visible through window
            for _ in range(10):
                x = random.randint(5, 10)
                y = random.randint(6, 8)
                color = random.choice([(255, 100, 0), (255, 200, 0), (255, 150, 50)])
                draw.point((x, y), fill=(*color, 200))
            
            # Temperature gauge
            draw.rectangle([13, 3, 14, 10], fill=(40, 40, 40, 255))
            draw.rectangle([13, 7, 14, 10], fill=(255, 100, 0, 255))
            
        elif face == 'top':
            # Generic top with ventilation
            for x in range(2, 14, 3):
                for y in range(2, 14, 3):
                    draw.rectangle([x, y, x+1, y+1], fill=(60, 60, 70, 255))
                    
        elif face == 'side':
            # Generic side with maintenance panel
            draw.rectangle([3, 4, 12, 11], fill=tuple(int(c * 0.9) for c in base_color) + (255,))
            
            # Bolts
            for x, y in [(4, 5), (11, 5), (4, 10), (11, 10)]:
                draw.ellipse([x-1, y-1, x, y], fill=(80, 80, 90, 255))
                
        # Use original method for other types
        else:
            if hasattr(self, '_original_generate_machine_block'):
                return self._original_generate_machine_block(machine_type, face)
            else:
                return img
        
        img = self.add_rivets(img, base_color)
        img = self.add_noise(img, 0.02)
        
        return img
    
    def generate_multiblock_part(self, multiblock_type: str, part_type: str):
        """Extended multiblock generator"""
        img = Image.new('RGBA', (self.size, self.size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        if multiblock_type == 'furnace':
            base_color = (140, 100, 80)
            
            # Heat-resistant casing
            draw.rectangle([0, 0, 15, 15], fill=(*base_color, 255))
            
            if part_type == 'casing':
                # Refractory brick pattern
                for y in range(0, 16, 4):
                    for x in range(0, 16, 8):
                        offset = 4 if y % 8 == 4 else 0
                        draw.rectangle([x + offset, y, x + offset + 7, y + 3],
                                      outline=(100, 60, 40, 255), width=1)
                                      
            elif part_type == 'controller':
                # Control interface
                draw.rectangle([2, 2, 13, 8], fill=(40, 30, 20, 255))
                
                # Temperature display
                draw.rectangle([3, 3, 7, 7], fill=(20, 20, 20, 255))
                draw.text((4, 4), "HEAT", fill=(255, 100, 0, 255))
                
                # Control knobs
                for x in range(9, 13, 2):
                    draw.ellipse([x, 4, x+1, 5], fill=(150, 150, 150, 255))
                    
        elif multiblock_type == 'refinery':
            base_color = (110, 120, 130)
            
            draw.rectangle([0, 0, 15, 15], fill=(*base_color, 255))
            
            if part_type == 'casing':
                # Industrial panel pattern
                for i in range(0, 16, 8):
                    for j in range(0, 16, 8):
                        draw.rectangle([i+1, j+1, i+6, j+6],
                                      outline=(90, 100, 110, 255), width=1)
                        # Center bolt
                        draw.ellipse([i+3, j+3, i+4, j+4], fill=(80, 80, 90, 255))
                        
            elif part_type == 'controller':
                # Refinery controls
                draw.rectangle([2, 2, 13, 10], fill=(30, 40, 50, 255))
                
                # Flow indicators
                for i in range(3):
                    y = 4 + i * 2
                    draw.line([(3, y), (12, y)], fill=(0, 200, 255, 150), width=1)
                    # Flow direction arrow
                    draw.polygon([(11, y-1), (12, y), (11, y+1)],
                                fill=(0, 200, 255, 255))
                                
        elif multiblock_type == 'rocket':
            base_color = (160, 160, 170)
            
            draw.rectangle([0, 0, 15, 15], fill=(*base_color, 255))
            
            if part_type == 'controller':
                # Launch control panel
                draw.rectangle([2, 2, 13, 9], fill=(20, 30, 40, 255))
                
                # Rocket schematic
                draw.polygon([(7, 3), (9, 6), (9, 8), (5, 8), (5, 6)],
                            fill=(100, 200, 100, 255))
                
                # Launch button (red)
                draw.ellipse([6, 11, 9, 14], fill=(200, 0, 0, 255))
                draw.ellipse([7, 12, 8, 13], fill=(255, 50, 50, 255))
        
        # Use original method for other types
        else:
            if hasattr(self, '_original_generate_multiblock_part'):
                return self._original_generate_multiblock_part(multiblock_type, part_type)
            else:
                return img
        
        img = self.add_noise(img, 0.02)
        return img
    
    # Bind the new methods to the generator instance
    from types import MethodType
    generator.generate_special_item = MethodType(generate_special_item, generator)
    generator.generate_special_block = MethodType(generate_special_block, generator)
    generator.generate_machine_block = MethodType(generate_machine_block, generator)
    generator.generate_multiblock_part = MethodType(generate_multiblock_part, generator)
    
    # Import required modules for the methods
    global Image, ImageDraw, random, math
    from PIL import Image, ImageDraw
    import random
    import math

if __name__ == "__main__":
    main()