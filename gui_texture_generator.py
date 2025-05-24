#!/usr/bin/env python3
"""
GUI Texture Generator for AstroExpansion
Creates professional 256x256 GUI textures for Minecraft machines
"""

from PIL import Image, ImageDraw, ImageFont
import os
from typing import Tuple, List, Dict

class GUITextureGenerator:
    def __init__(self):
        self.size = 256
        self.slot_size = 18
        
        # Color scheme matching the mod's aesthetic
        self.colors = {
            'background': (198, 198, 198),
            'dark_border': (85, 85, 85),
            'light_border': (255, 255, 255),
            'slot_bg': (139, 139, 139),
            'slot_dark': (55, 55, 55),
            'slot_light': (220, 220, 220),
            'text': (64, 64, 64),
            'energy_empty': (40, 40, 40),
            'energy_full': (255, 85, 85),
            'fluid_empty': (40, 40, 60),
            'fluid_full': (100, 150, 255),
            'progress_empty': (100, 100, 100),
            'progress_full': (100, 255, 100),
            'titanium': (180, 180, 190),
            'lithium': (230, 230, 255),
            'uranium': (100, 200, 100),
            'fusion': (255, 150, 255),
            'quantum': (150, 100, 255)
        }
        
    def create_base_gui(self, width: int = 176, height: int = 166) -> Image.Image:
        """Create base GUI window with borders"""
        img = Image.new('RGBA', (self.size, self.size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # Calculate offset to center the GUI
        offset_x = (self.size - width) // 2
        offset_y = (self.size - height) // 2
        
        # Main background
        draw.rectangle([offset_x, offset_y, offset_x + width, offset_y + height], 
                      fill=self.colors['background'])
        
        # Create 3D border effect
        # Top and left borders (light)
        draw.line([offset_x, offset_y, offset_x + width - 1, offset_y], 
                 fill=self.colors['light_border'], width=2)
        draw.line([offset_x, offset_y, offset_x, offset_y + height - 1], 
                 fill=self.colors['light_border'], width=2)
        
        # Bottom and right borders (dark)
        draw.line([offset_x + 1, offset_y + height - 1, offset_x + width - 1, offset_y + height - 1], 
                 fill=self.colors['dark_border'], width=2)
        draw.line([offset_x + width - 1, offset_y + 1, offset_x + width - 1, offset_y + height - 1], 
                 fill=self.colors['dark_border'], width=2)
        
        return img, offset_x, offset_y
    
    def draw_slot(self, draw: ImageDraw.Draw, x: int, y: int):
        """Draw a single inventory slot"""
        # Dark border
        draw.rectangle([x - 1, y - 1, x + self.slot_size, y + self.slot_size], 
                      fill=self.colors['slot_dark'])
        # Slot background
        draw.rectangle([x, y, x + self.slot_size - 1, y + self.slot_size - 1], 
                      fill=self.colors['slot_bg'])
        # Light inner border for depth
        draw.line([x, y, x + self.slot_size - 2, y], fill=self.colors['slot_light'])
        draw.line([x, y, x, y + self.slot_size - 2], fill=self.colors['slot_light'])
    
    def draw_player_inventory(self, draw: ImageDraw.Draw, x: int, y: int):
        """Draw standard player inventory (9x3 + hotbar)"""
        # Main inventory
        for row in range(3):
            for col in range(9):
                self.draw_slot(draw, x + col * self.slot_size, y + row * self.slot_size)
        
        # Hotbar (with gap)
        for col in range(9):
            self.draw_slot(draw, x + col * self.slot_size, y + 58)
    
    def draw_progress_arrow(self, draw: ImageDraw.Draw, x: int, y: int, width: int = 24, height: int = 16):
        """Draw a progress arrow"""
        # Background arrow
        points = [
            (x, y + height // 2),
            (x + width - 8, y + height // 2),
            (x + width - 8, y),
            (x + width, y + height // 2),
            (x + width - 8, y + height),
            (x + width - 8, y + height // 2)
        ]
        draw.polygon(points[:3] + [points[0]], fill=self.colors['progress_empty'])
        draw.polygon(points[3:], fill=self.colors['progress_empty'])
        
    def draw_energy_bar(self, draw: ImageDraw.Draw, x: int, y: int, width: int = 14, height: int = 50):
        """Draw an energy storage bar"""
        # Border
        draw.rectangle([x - 1, y - 1, x + width + 1, y + height + 1], 
                      fill=self.colors['dark_border'])
        # Background
        draw.rectangle([x, y, x + width, y + height], 
                      fill=self.colors['energy_empty'])
        # Add some visual markers
        for i in range(0, height, 10):
            draw.line([x + 2, y + i, x + width - 2, y + i], 
                     fill=self.colors['slot_dark'], width=1)
    
    def draw_fluid_tank(self, draw: ImageDraw.Draw, x: int, y: int, width: int = 18, height: int = 54):
        """Draw a fluid tank"""
        # Border
        draw.rectangle([x - 1, y - 1, x + width + 1, y + height + 1], 
                      fill=self.colors['dark_border'])
        # Background
        draw.rectangle([x, y, x + width, y + height], 
                      fill=self.colors['fluid_empty'])
        # Glass effect
        draw.rectangle([x + 2, y + 2, x + width - 2, y + height - 2], 
                      fill=self.colors['fluid_empty'], outline=self.colors['slot_light'])
    
    def draw_label(self, draw: ImageDraw.Draw, x: int, y: int, text: str):
        """Draw text label"""
        # Simple text without font (Minecraft style)
        draw.text((x, y), text, fill=self.colors['text'])
    
    def generate_basic_generator(self) -> Image.Image:
        """Generate Basic Generator GUI"""
        img, ox, oy = self.create_base_gui()
        draw = ImageDraw.Draw(img)
        
        # Title
        self.draw_label(draw, ox + 8, oy + 6, "Basic Generator")
        
        # Fuel slot
        self.draw_slot(draw, ox + 56, oy + 36)
        
        # Fire/burn indicator
        draw.rectangle([ox + 57, oy + 55, ox + 71, oy + 69], 
                      fill=self.colors['progress_empty'])
        
        # Energy bar
        self.draw_energy_bar(draw, ox + 112, oy + 17)
        
        # Output slot
        self.draw_slot(draw, ox + 112, oy + 72)
        
        # Player inventory
        self.draw_player_inventory(draw, ox + 8, oy + 84)
        
        return img
    
    def generate_material_processor(self) -> Image.Image:
        """Generate Material Processor GUI"""
        img, ox, oy = self.create_base_gui()
        draw = ImageDraw.Draw(img)
        
        # Title
        self.draw_label(draw, ox + 8, oy + 6, "Material Processor")
        
        # Input slot
        self.draw_slot(draw, ox + 56, oy + 35)
        
        # Progress arrow
        self.draw_progress_arrow(draw, ox + 79, oy + 35)
        
        # Output slot
        self.draw_slot(draw, ox + 112, oy + 35)
        
        # Energy bar
        self.draw_energy_bar(draw, ox + 8, oy + 17)
        
        # Player inventory
        self.draw_player_inventory(draw, ox + 8, oy + 84)
        
        return img
    
    def generate_ore_washer(self) -> Image.Image:
        """Generate Ore Washer GUI"""
        img, ox, oy = self.create_base_gui()
        draw = ImageDraw.Draw(img)
        
        # Title
        self.draw_label(draw, ox + 8, oy + 6, "Ore Washer")
        
        # Input slot
        self.draw_slot(draw, ox + 38, oy + 35)
        
        # Water tank
        self.draw_fluid_tank(draw, ox + 8, oy + 17)
        
        # Progress arrow
        self.draw_progress_arrow(draw, ox + 61, oy + 35)
        
        # Output slots (2x2)
        for row in range(2):
            for col in range(2):
                self.draw_slot(draw, ox + 94 + col * self.slot_size, ox + 26 + row * self.slot_size)
        
        # Energy bar
        self.draw_energy_bar(draw, ox + 152, oy + 17)
        
        # Player inventory
        self.draw_player_inventory(draw, ox + 8, oy + 84)
        
        return img
    
    def generate_component_assembler(self) -> Image.Image:
        """Generate Component Assembler GUI"""
        img, ox, oy = self.create_base_gui(176, 185)
        draw = ImageDraw.Draw(img)
        
        # Title
        self.draw_label(draw, ox + 8, oy + 6, "Component Assembler")
        
        # Input grid (3x3)
        for row in range(3):
            for col in range(3):
                self.draw_slot(draw, ox + 30 + col * self.slot_size, oy + 17 + row * self.slot_size)
        
        # Progress arrow
        self.draw_progress_arrow(draw, ox + 90, oy + 35)
        
        # Output slot
        self.draw_slot(draw, ox + 124, oy + 35)
        
        # Energy bar
        self.draw_energy_bar(draw, ox + 8, oy + 17)
        
        # Player inventory
        self.draw_player_inventory(draw, ox + 8, oy + 103)
        
        return img
    
    def generate_energy_storage(self) -> Image.Image:
        """Generate Energy Storage GUI"""
        img, ox, oy = self.create_base_gui(176, 166)
        draw = ImageDraw.Draw(img)
        
        # Title
        self.draw_label(draw, ox + 8, oy + 6, "Energy Storage")
        
        # Large energy display
        self.draw_energy_bar(draw, ox + 79, oy + 20, width=18, height=60)
        
        # Charge slot (left)
        self.draw_slot(draw, ox + 44, oy + 35)
        
        # Discharge slot (right)
        self.draw_slot(draw, ox + 112, oy + 35)
        
        # Energy info area
        draw.rectangle([ox + 40, oy + 55, ox + 136, oy + 75], 
                      outline=self.colors['dark_border'])
        
        # Player inventory
        self.draw_player_inventory(draw, ox + 8, oy + 84)
        
        return img
    
    def generate_storage_terminal(self) -> Image.Image:
        """Generate Storage Terminal GUI"""
        img, ox, oy = self.create_base_gui(195, 222)
        draw = ImageDraw.Draw(img)
        
        # Title
        self.draw_label(draw, ox + 8, oy + 6, "Storage Terminal")
        
        # Search bar area
        draw.rectangle([ox + 81, oy + 6, ox + 169, oy + 18], 
                      fill=self.colors['slot_bg'], outline=self.colors['dark_border'])
        
        # Storage grid (9x5)
        for row in range(5):
            for col in range(9):
                self.draw_slot(draw, ox + 8 + col * self.slot_size, oy + 26 + row * self.slot_size)
        
        # Crafting grid (3x3)
        for row in range(3):
            for col in range(3):
                self.draw_slot(draw, ox + 8, oy + 124 + row * self.slot_size)
        
        # Crafting output
        self.draw_slot(draw, ox + 80, oy + 142)
        
        # Player inventory
        self.draw_player_inventory(draw, ox + 8 + 9, oy + 140)
        
        return img
    
    def generate_fusion_reactor(self) -> Image.Image:
        """Generate Fusion Reactor GUI"""
        img, ox, oy = self.create_base_gui(176, 185)
        draw = ImageDraw.Draw(img)
        
        # Title with fusion color
        self.draw_label(draw, ox + 8, oy + 6, "Fusion Reactor")
        
        # Fuel slots (deuterium, tritium)
        self.draw_slot(draw, ox + 27, oy + 35)
        self.draw_slot(draw, ox + 27, oy + 53)
        
        # Reaction chamber visualization
        draw.ellipse([ox + 65, oy + 25, ox + 111, oy + 71], 
                    outline=self.colors['fusion'], width=2)
        draw.ellipse([ox + 75, oy + 35, ox + 101, oy + 61], 
                    outline=self.colors['fusion'], width=1)
        
        # Output slot (helium-3)
        self.draw_slot(draw, ox + 131, oy + 44)
        
        # Energy bar (larger)
        self.draw_energy_bar(draw, ox + 8, oy + 17, width=16, height=54)
        
        # Temperature indicator
        draw.rectangle([ox + 152, oy + 17, ox + 166, oy + 71], 
                      fill=self.colors['energy_empty'], outline=self.colors['dark_border'])
        
        # Player inventory
        self.draw_player_inventory(draw, ox + 8, oy + 103)
        
        return img
    
    def generate_quantum_computer(self) -> Image.Image:
        """Generate Quantum Computer GUI"""
        img, ox, oy = self.create_base_gui(176, 185)
        draw = ImageDraw.Draw(img)
        
        # Title with quantum color
        self.draw_label(draw, ox + 8, oy + 6, "Quantum Computer")
        
        # Processor slots (2x2)
        for row in range(2):
            for col in range(2):
                self.draw_slot(draw, ox + 62 + col * self.slot_size, oy + 26 + row * self.slot_size)
        
        # Memory slots (1x4)
        for i in range(4):
            self.draw_slot(draw, ox + 116, oy + 17 + i * self.slot_size)
        
        # Quantum matrix display
        matrix_x, matrix_y = ox + 26, oy + 26
        draw.rectangle([matrix_x, matrix_y, matrix_x + 32, matrix_y + 32], 
                      fill=self.colors['quantum'], outline=self.colors['dark_border'])
        
        # Grid pattern
        for i in range(1, 4):
            draw.line([matrix_x + i * 8, matrix_y, matrix_x + i * 8, matrix_y + 32], 
                     fill=self.colors['light_border'])
            draw.line([matrix_x, matrix_y + i * 8, matrix_x + 32, matrix_y + i * 8], 
                     fill=self.colors['light_border'])
        
        # Energy bar
        self.draw_energy_bar(draw, ox + 8, oy + 17)
        
        # Player inventory
        self.draw_player_inventory(draw, ox + 8, oy + 103)
        
        return img
    
    def generate_industrial_furnace(self) -> Image.Image:
        """Generate Industrial Furnace GUI"""
        img, ox, oy = self.create_base_gui(176, 185)
        draw = ImageDraw.Draw(img)
        
        # Title
        self.draw_label(draw, ox + 8, oy + 6, "Industrial Furnace")
        
        # Input slots (2x2)
        for row in range(2):
            for col in range(2):
                self.draw_slot(draw, ox + 33 + col * self.slot_size, oy + 26 + row * self.slot_size)
        
        # Large progress arrow
        self.draw_progress_arrow(draw, ox + 74, oy + 35, width=32)
        
        # Output slots (2x2)
        for row in range(2):
            for col in range(2):
                self.draw_slot(draw, ox + 116 + col * self.slot_size, oy + 26 + row * self.slot_size)
        
        # Heat indicator
        draw.rectangle([ox + 8, oy + 17, ox + 24, oy + 71], 
                      fill=self.colors['energy_empty'], outline=self.colors['dark_border'])
        
        # Energy bar
        self.draw_energy_bar(draw, ox + 152, oy + 17)
        
        # Player inventory
        self.draw_player_inventory(draw, ox + 8, oy + 103)
        
        return img
    
    def generate_storage_core(self) -> Image.Image:
        """Generate Storage Core GUI"""
        img, ox, oy = self.create_base_gui(176, 166)
        draw = ImageDraw.Draw(img)
        
        # Title
        self.draw_label(draw, ox + 8, oy + 6, "Storage Core")
        
        # Drive slots (2x4)
        for row in range(2):
            for col in range(4):
                self.draw_slot(draw, ox + 53 + col * self.slot_size, oy + 26 + row * self.slot_size)
        
        # Status display area
        draw.rectangle([ox + 8, oy + 64, ox + 168, oy + 78], 
                      fill=self.colors['slot_bg'], outline=self.colors['dark_border'])
        
        # Player inventory
        self.draw_player_inventory(draw, ox + 8, oy + 84)
        
        return img
    
    def generate_fluid_tank(self) -> Image.Image:
        """Generate Fluid Tank GUI"""
        img, ox, oy = self.create_base_gui()
        draw = ImageDraw.Draw(img)
        
        # Title
        self.draw_label(draw, ox + 8, oy + 6, "Fluid Tank")
        
        # Large fluid display
        self.draw_fluid_tank(draw, ox + 71, oy + 20, width=34, height=60)
        
        # Input slot (bucket)
        self.draw_slot(draw, ox + 44, oy + 20)
        
        # Output slot (bucket)
        self.draw_slot(draw, ox + 112, oy + 20)
        
        # Fluid info area
        draw.rectangle([ox + 44, oy + 44, ox + 132, oy + 58], 
                      outline=self.colors['dark_border'])
        
        # Player inventory
        self.draw_player_inventory(draw, ox + 8, oy + 84)
        
        return img
    
    def generate_drone_dock(self) -> Image.Image:
        """Generate Drone Dock GUI"""
        img, ox, oy = self.create_base_gui()
        draw = ImageDraw.Draw(img)
        
        # Title
        self.draw_label(draw, ox + 8, oy + 6, "Drone Dock")
        
        # Drone slot (larger)
        draw.rectangle([ox + 78, oy + 24, ox + 98, oy + 44], 
                      fill=self.colors['slot_dark'])
        draw.rectangle([ox + 79, oy + 25, ox + 97, oy + 43], 
                      fill=self.colors['slot_bg'])
        
        # Upgrade slots (4 slots)
        for i in range(4):
            self.draw_slot(draw, ox + 44 + i * self.slot_size, oy + 50)
        
        # Energy bar
        self.draw_energy_bar(draw, ox + 8, oy + 17)
        
        # Status area
        draw.rectangle([ox + 116, oy + 26, ox + 168, oy + 68], 
                      outline=self.colors['dark_border'])
        
        # Player inventory
        self.draw_player_inventory(draw, ox + 8, oy + 84)
        
        return img
    
    def generate_research_terminal(self) -> Image.Image:
        """Generate Research Terminal GUI"""
        img, ox, oy = self.create_base_gui(176, 185)
        draw = ImageDraw.Draw(img)
        
        # Title
        self.draw_label(draw, ox + 8, oy + 6, "Research Terminal")
        
        # Research data input slots (2x2)
        for row in range(2):
            for col in range(2):
                self.draw_slot(draw, ox + 44 + col * self.slot_size, oy + 26 + row * self.slot_size)
        
        # Research progress display
        draw.rectangle([ox + 88, oy + 26, ox + 168, oy + 62], 
                      fill=self.colors['slot_bg'], outline=self.colors['dark_border'])
        
        # Output slot
        self.draw_slot(draw, ox + 124, oy + 70)
        
        # Energy bar
        self.draw_energy_bar(draw, ox + 8, oy + 17)
        
        # Player inventory
        self.draw_player_inventory(draw, ox + 8, oy + 103)
        
        return img
    
    def generate_fuel_refinery(self) -> Image.Image:
        """Generate Fuel Refinery GUI"""
        img, ox, oy = self.create_base_gui(176, 185)
        draw = ImageDraw.Draw(img)
        
        # Title
        self.draw_label(draw, ox + 8, oy + 6, "Fuel Refinery")
        
        # Input tank
        self.draw_fluid_tank(draw, ox + 26, oy + 17)
        
        # Processing slots
        self.draw_slot(draw, ox + 53, oy + 35)
        
        # Progress arrow
        self.draw_progress_arrow(draw, ox + 76, oy + 35)
        
        # Output tanks (2)
        self.draw_fluid_tank(draw, ox + 107, oy + 17, width=16, height=40)
        self.draw_fluid_tank(draw, ox + 133, oy + 17, width=16, height=40)
        
        # Energy bar
        self.draw_energy_bar(draw, ox + 8, oy + 17)
        
        # Player inventory
        self.draw_player_inventory(draw, ox + 8, oy + 103)
        
        return img
    
    def generate_rocket_assembly(self) -> Image.Image:
        """Generate Rocket Assembly GUI"""
        img, ox, oy = self.create_base_gui(195, 222)
        draw = ImageDraw.Draw(img)
        
        # Title
        self.draw_label(draw, ox + 8, oy + 6, "Rocket Assembly")
        
        # Rocket component slots (vertical layout)
        # Nose cone
        self.draw_slot(draw, ox + 88, oy + 20)
        # Body sections (3)
        for i in range(3):
            self.draw_slot(draw, ox + 88, oy + 38 + i * self.slot_size)
        # Engine
        self.draw_slot(draw, ox + 88, oy + 92)
        
        # Side component slots (fins, etc.)
        for i in range(4):
            self.draw_slot(draw, ox + 52, oy + 38 + i * 18)
            self.draw_slot(draw, ox + 124, oy + 38 + i * 18)
        
        # Fuel gauge
        self.draw_fluid_tank(draw, ox + 8, oy + 20, width=20, height=90)
        
        # Launch button area
        draw.rectangle([ox + 142, oy + 84, ox + 187, oy + 102], 
                      fill=self.colors['energy_full'], outline=self.colors['dark_border'])
        
        # Player inventory
        self.draw_player_inventory(draw, ox + 8 + 9, oy + 140)
        
        return img

def main():
    generator = GUITextureGenerator()
    output_dir = "src/main/resources/assets/astroexpansion/textures/gui"
    
    # Ensure directory exists
    os.makedirs(output_dir, exist_ok=True)
    
    # Generate all GUIs
    guis = {
        'basic_generator': generator.generate_basic_generator,
        'material_processor': generator.generate_material_processor,
        'ore_washer': generator.generate_ore_washer,
        'component_assembler': generator.generate_component_assembler,
        'energy_storage': generator.generate_energy_storage,
        'storage_terminal': generator.generate_storage_terminal,
        'fusion_reactor': generator.generate_fusion_reactor,
        'quantum_computer': generator.generate_quantum_computer,
        'industrial_furnace': generator.generate_industrial_furnace,
        'storage_core': generator.generate_storage_core,
        'fluid_tank': generator.generate_fluid_tank,
        'drone_dock': generator.generate_drone_dock,
        'research_terminal': generator.generate_research_terminal,
        'fuel_refinery': generator.generate_fuel_refinery,
        'rocket_assembly': generator.generate_rocket_assembly,
    }
    
    print("Generating GUI textures...")
    for name, generator_func in guis.items():
        try:
            texture = generator_func()
            texture.save(os.path.join(output_dir, f"{name}.png"))
            print(f"  ✓ Generated {name}.png")
        except Exception as e:
            print(f"  ✗ Failed to generate {name}.png: {e}")
    
    print("\nGUI texture generation complete!")

if __name__ == "__main__":
    main()