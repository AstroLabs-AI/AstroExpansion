#!/usr/bin/env python3
"""
Fixed GUI Texture Generator for AstroExpansion
Creates properly aligned 256x256 GUI textures for Minecraft machines
"""

from PIL import Image, ImageDraw
import os
from typing import Tuple, List, Dict

class MinecraftGUIGenerator:
    def __init__(self):
        self.size = 256
        self.slot_size = 18
        
        # Minecraft standard GUI starts drawing at these coordinates in the 256x256 texture
        # The actual GUI window is typically 176x166 pixels
        # But it's positioned in the texture, not at 0,0
        self.gui_offset_x = 0  # Start at texture origin
        self.gui_offset_y = 0  # Start at texture origin
        
        # Standard colors
        self.colors = {
            'window_bg': (198, 198, 198),
            'window_border_light': (255, 255, 255),
            'window_border_dark': (85, 85, 85),
            'slot_border_dark': (55, 55, 55),
            'slot_bg': (139, 139, 139),
            'slot_border_light': (255, 255, 255),
            'text': (64, 64, 64),
        }
    
    def create_blank_gui(self) -> Image.Image:
        """Create a blank 256x256 transparent GUI texture"""
        return Image.new('RGBA', (self.size, self.size), (0, 0, 0, 0))
    
    def draw_window(self, img: Image.Image, x: int, y: int, width: int, height: int):
        """Draw the main GUI window background"""
        draw = ImageDraw.Draw(img)
        
        # Main background fill
        draw.rectangle([x + 4, y + 4, x + width - 4, y + height - 4], 
                      fill=self.colors['window_bg'])
        
        # Draw border (4 pixels wide, with proper corners)
        # Top-left corner
        for i in range(4):
            # Top edge
            draw.rectangle([x + i, y + i, x + width - i - 1, y + i], 
                          fill=self.colors['window_border_light'] if i < 2 else self.colors['window_bg'])
            # Left edge
            draw.rectangle([x + i, y + i, x + i, y + height - i - 1], 
                          fill=self.colors['window_border_light'] if i < 2 else self.colors['window_bg'])
            # Bottom edge
            draw.rectangle([x + i, y + height - i - 1, x + width - i - 1, y + height - i - 1], 
                          fill=self.colors['window_border_dark'] if i < 2 else self.colors['window_bg'])
            # Right edge
            draw.rectangle([x + width - i - 1, y + i, x + width - i - 1, y + height - i - 1], 
                          fill=self.colors['window_border_dark'] if i < 2 else self.colors['window_bg'])
    
    def draw_slot(self, img: Image.Image, x: int, y: int):
        """Draw a single inventory slot at exact position"""
        draw = ImageDraw.Draw(img)
        
        # Slot is 18x18 with 1 pixel border
        # Dark outer border
        draw.rectangle([x, y, x + 17, y + 17], fill=self.colors['slot_border_dark'])
        # Medium background
        draw.rectangle([x + 1, y + 1, x + 16, y + 16], fill=self.colors['slot_bg'])
        # Light inner border (top-left)
        draw.line([(x + 1, y + 1), (x + 16, y + 1)], fill=self.colors['slot_border_light'])
        draw.line([(x + 1, y + 1), (x + 1, y + 16)], fill=self.colors['slot_border_light'])
    
    def draw_player_inventory(self, img: Image.Image, x: int, y: int):
        """Draw standard 9x3 player inventory + 9 slot hotbar"""
        # Main inventory (3 rows of 9)
        for row in range(3):
            for col in range(9):
                self.draw_slot(img, x + col * 18, y + row * 18)
        
        # Hotbar (1 row of 9, with 4 pixel gap)
        for col in range(9):
            self.draw_slot(img, x + col * 18, y + 58)
    
    def draw_energy_bar(self, img: Image.Image, x: int, y: int, width: int = 14, height: int = 42):
        """Draw energy storage indicator"""
        draw = ImageDraw.Draw(img)
        
        # Dark border
        draw.rectangle([x, y, x + width + 1, y + height + 1], fill=self.colors['slot_border_dark'])
        # Empty background
        draw.rectangle([x + 1, y + 1, x + width, y + height], fill=(70, 70, 70))
    
    def draw_arrow(self, img: Image.Image, x: int, y: int):
        """Draw progress arrow (22x15 standard)"""
        draw = ImageDraw.Draw(img)
        
        # Arrow shape
        arrow_color = (128, 128, 128)
        # Main body
        draw.rectangle([x, y + 6, x + 16, y + 8], fill=arrow_color)
        # Arrow head
        draw.polygon([(x + 16, y + 4), (x + 21, y + 7), (x + 16, y + 10)], fill=arrow_color)
    
    def draw_flame(self, img: Image.Image, x: int, y: int):
        """Draw flame icon for fuel burning (13x13 standard)"""
        draw = ImageDraw.Draw(img)
        
        # Flame shape
        flame_color = (200, 200, 200)
        # Base
        draw.rectangle([x + 3, y + 9, x + 9, y + 12], fill=flame_color)
        # Middle
        draw.rectangle([x + 2, y + 5, x + 10, y + 9], fill=flame_color)
        # Top
        draw.polygon([(x + 6, y), (x + 3, y + 5), (x + 9, y + 5)], fill=flame_color)
    
    def generate_basic_generator(self) -> Image.Image:
        """Basic Generator GUI - standard furnace-style layout"""
        img = self.create_blank_gui()
        
        # Standard window size and position
        window_x, window_y = 7, 15
        window_width, window_height = 176, 166
        
        # Draw main window
        self.draw_window(img, window_x, window_y, window_width, window_height)
        
        # Fuel slot (left)
        self.draw_slot(img, window_x + 56, window_y + 53)
        
        # Flame icon
        self.draw_flame(img, window_x + 56, window_y + 36)
        
        # Energy bar (right)
        self.draw_energy_bar(img, window_x + 112, window_y + 16)
        
        # Player inventory at standard position
        self.draw_player_inventory(img, window_x + 8, window_y + 84)
        
        return img
    
    def generate_material_processor(self) -> Image.Image:
        """Material Processor GUI - single input/output with progress"""
        img = self.create_blank_gui()
        
        window_x, window_y = 7, 15
        window_width, window_height = 176, 166
        
        self.draw_window(img, window_x, window_y, window_width, window_height)
        
        # Input slot
        self.draw_slot(img, window_x + 56, window_y + 35)
        
        # Progress arrow
        self.draw_arrow(img, window_x + 79, window_y + 35)
        
        # Output slot
        self.draw_slot(img, window_x + 112, window_y + 35)
        
        # Energy bar
        self.draw_energy_bar(img, window_x + 8, window_y + 16)
        
        # Player inventory
        self.draw_player_inventory(img, window_x + 8, window_y + 84)
        
        return img
    
    def generate_component_assembler(self) -> Image.Image:
        """Component Assembler GUI - 3x3 crafting grid"""
        img = self.create_blank_gui()
        
        window_x, window_y = 7, 6
        window_width, window_height = 176, 186
        
        self.draw_window(img, window_x, window_y, window_width, window_height)
        
        # 3x3 input grid
        for row in range(3):
            for col in range(3):
                self.draw_slot(img, window_x + 30 + col * 18, window_y + 17 + row * 18)
        
        # Progress arrow
        self.draw_arrow(img, window_x + 91, window_y + 35)
        
        # Output slot
        self.draw_slot(img, window_x + 124, window_y + 35)
        
        # Energy bar
        self.draw_energy_bar(img, window_x + 8, window_y + 16)
        
        # Player inventory
        self.draw_player_inventory(img, window_x + 8, window_y + 104)
        
        return img
    
    def generate_storage_terminal(self) -> Image.Image:
        """Storage Terminal GUI - large item grid"""
        img = self.create_blank_gui()
        
        window_x, window_y = 0, 0
        window_width, window_height = 195, 222
        
        self.draw_window(img, window_x, window_y, window_width, window_height)
        
        # Search bar background
        draw = ImageDraw.Draw(img)
        draw.rectangle([window_x + 82, window_y + 6, window_x + 170, window_y + 18], 
                      fill=(0, 0, 0))
        
        # Storage grid (9x5)
        for row in range(5):
            for col in range(9):
                self.draw_slot(img, window_x + 8, window_y + 26 + row * 18)
        
        # Crafting grid (3x3)
        for row in range(3):
            for col in range(3):
                self.draw_slot(img, window_x + 8, window_y + 124 + row * 18)
        
        # Crafting output
        self.draw_slot(img, window_x + 80, window_y + 142)
        
        # Player inventory
        self.draw_player_inventory(img, window_x + 17, window_y + 140)
        
        return img
    
    def generate_energy_storage(self) -> Image.Image:
        """Energy Storage GUI"""
        img = self.create_blank_gui()
        
        window_x, window_y = 7, 15
        window_width, window_height = 176, 166
        
        self.draw_window(img, window_x, window_y, window_width, window_height)
        
        # Large energy display
        self.draw_energy_bar(img, window_x + 79, window_y + 20, width=18, height=50)
        
        # Input slot (charge)
        self.draw_slot(img, window_x + 56, window_y + 17)
        
        # Output slot (discharge)
        self.draw_slot(img, window_x + 104, window_y + 53)
        
        # Player inventory
        self.draw_player_inventory(img, window_x + 8, window_y + 84)
        
        return img
    
    def generate_ore_washer(self) -> Image.Image:
        """Ore Washer GUI"""
        img = self.create_blank_gui()
        
        window_x, window_y = 7, 15
        window_width, window_height = 176, 166
        
        self.draw_window(img, window_x, window_y, window_width, window_height)
        
        # Input slot
        self.draw_slot(img, window_x + 46, window_y + 35)
        
        # Water tank (fluid bar)
        draw = ImageDraw.Draw(img)
        draw.rectangle([window_x + 8, window_y + 16, window_x + 25, window_y + 69], 
                      fill=self.colors['slot_border_dark'])
        draw.rectangle([window_x + 9, window_y + 17, window_x + 24, window_y + 68], 
                      fill=(100, 150, 200))
        
        # Progress arrow
        self.draw_arrow(img, window_x + 69, window_y + 35)
        
        # Output slots (2x2)
        for row in range(2):
            for col in range(2):
                self.draw_slot(img, window_x + 102 + col * 18, window_y + 26 + row * 18)
        
        # Energy bar
        self.draw_energy_bar(img, window_x + 152, window_y + 16)
        
        # Player inventory
        self.draw_player_inventory(img, window_x + 8, window_y + 84)
        
        return img
    
    def generate_fluid_tank(self) -> Image.Image:
        """Fluid Tank GUI"""
        img = self.create_blank_gui()
        
        window_x, window_y = 7, 15
        window_width, window_height = 176, 166
        
        self.draw_window(img, window_x, window_y, window_width, window_height)
        
        # Large fluid display
        draw = ImageDraw.Draw(img)
        draw.rectangle([window_x + 71, window_y + 20, window_x + 105, window_y + 74], 
                      fill=self.colors['slot_border_dark'])
        draw.rectangle([window_x + 72, window_y + 21, window_x + 104, window_y + 73], 
                      fill=(150, 150, 150))
        
        # Input bucket slot
        self.draw_slot(img, window_x + 44, window_y + 20)
        
        # Output bucket slot
        self.draw_slot(img, window_x + 44, window_y + 56)
        
        # Empty bucket slot
        self.draw_slot(img, window_x + 112, window_y + 38)
        
        # Player inventory
        self.draw_player_inventory(img, window_x + 8, window_y + 84)
        
        return img
    
    def generate_all_guis(self):
        """Generate all GUI textures"""
        output_dir = "src/main/resources/assets/astroexpansion/textures/gui"
        os.makedirs(output_dir, exist_ok=True)
        
        guis = {
            'basic_generator': self.generate_basic_generator,
            'material_processor': self.generate_material_processor,
            'component_assembler': self.generate_component_assembler,
            'storage_terminal': self.generate_storage_terminal,
            'energy_storage': self.generate_energy_storage,
            'ore_washer': self.generate_ore_washer,
            'fluid_tank': self.generate_fluid_tank,
        }
        
        # Generate basic GUIs first
        print("Generating fixed GUI textures...")
        for name, generator_func in guis.items():
            try:
                texture = generator_func()
                texture.save(os.path.join(output_dir, f"{name}.png"))
                print(f"  ✓ Generated {name}.png")
            except Exception as e:
                print(f"  ✗ Failed to generate {name}.png: {e}")
        
        # For other GUIs, use variations of the basic ones
        # Copy and modify as needed
        additional_guis = {
            'drone_dock': 'energy_storage',  # Similar layout
            'fusion_reactor': 'material_processor',  # With modifications
            'industrial_furnace': 'component_assembler',  # Similar grid
            'storage_core': 'energy_storage',  # Similar layout
            'quantum_computer': 'component_assembler',  # Similar layout
            'fuel_refinery': 'ore_washer',  # Similar multi-output
            'research_terminal': 'material_processor',  # Similar I/O
            'rocket_assembly': 'component_assembler',  # Large crafting
        }
        
        for new_name, base_name in additional_guis.items():
            try:
                # For now, just copy the base GUI
                # In a real implementation, you'd modify these
                base_func = guis.get(base_name)
                if base_func:
                    texture = base_func()
                    texture.save(os.path.join(output_dir, f"{new_name}.png"))
                    print(f"  ✓ Generated {new_name}.png (based on {base_name})")
            except Exception as e:
                print(f"  ✗ Failed to generate {new_name}.png: {e}")

def main():
    generator = MinecraftGUIGenerator()
    generator.generate_all_guis()
    print("\nFixed GUI texture generation complete!")

if __name__ == "__main__":
    main()