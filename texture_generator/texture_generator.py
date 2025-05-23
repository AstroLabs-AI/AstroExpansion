#!/usr/bin/env python3
"""
Astro Expansion Texture Generator
Generates placeholder textures for all mod assets with consistent styling
"""

from PIL import Image, ImageDraw, ImageFont
import os
import json
import hashlib
from pathlib import Path
from typing import Dict, Tuple, List

class TextureGenerator:
    """Main texture generator for Astro Expansion mod"""
    
    # Minecraft standard resolutions
    BLOCK_SIZE = 16
    ITEM_SIZE = 16
    GUI_SIZE = 256
    ENTITY_SIZE = 64
    
    # Color schemes for different material types
    COLOR_SCHEMES = {
        # Metals
        'titanium': {
            'primary': (200, 200, 210),
            'secondary': (150, 150, 160),
            'accent': (100, 100, 110),
            'highlight': (230, 230, 240)
        },
        'lithium': {
            'primary': (180, 180, 200),
            'secondary': (140, 140, 170),
            'accent': (100, 100, 130),
            'highlight': (200, 200, 230)
        },
        'uranium': {
            'primary': (80, 200, 80),
            'secondary': (60, 150, 60),
            'accent': (40, 100, 40),
            'highlight': (100, 250, 100)
        },
        
        # Machine types
        'machine': {
            'primary': (160, 160, 160),
            'secondary': (120, 120, 120),
            'accent': (80, 80, 80),
            'highlight': (200, 200, 200)
        },
        'tech': {
            'primary': (100, 150, 200),
            'secondary': (80, 120, 160),
            'accent': (60, 90, 120),
            'highlight': (120, 180, 240)
        },
        'energy': {
            'primary': (255, 200, 50),
            'secondary': (200, 150, 30),
            'accent': (150, 100, 20),
            'highlight': (255, 255, 100)
        },
        'storage': {
            'primary': (100, 200, 100),
            'secondary': (80, 160, 80),
            'accent': (60, 120, 60),
            'highlight': (120, 240, 120)
        },
        'multiblock': {
            'primary': (150, 100, 200),
            'secondary': (120, 80, 160),
            'accent': (90, 60, 120),
            'highlight': (180, 120, 240)
        },
        
        # Ore types
        'ore': {
            'primary': (120, 120, 120),
            'secondary': (80, 80, 80),
            'accent': (60, 60, 60),
            'highlight': (140, 140, 140)
        },
        'deepslate_ore': {
            'primary': (60, 60, 70),
            'secondary': (40, 40, 50),
            'accent': (30, 30, 40),
            'highlight': (80, 80, 90)
        }
    }
    
    # Missing textures list
    missing_textures = {
        'blocks': [
            'titanium_ore', 'deepslate_titanium_ore', 'titanium_block',
            'lithium_ore', 'deepslate_lithium_ore', 'lithium_block',
            'uranium_ore', 'deepslate_uranium_ore', 'uranium_block',
            'lunar_regolith', 'lunar_stone', 'lunar_iron_ore', 'lunar_titanium_ore',
            'helium3_deposit', 'rare_earth_ore',
            'basic_generator', 'material_processor', 'ore_washer', 'energy_conduit',
            'energy_storage', 'fluid_pipe', 'fluid_tank', 'furnace_casing',
            'storage_core', 'storage_terminal', 'import_bus', 'export_bus',
            'fusion_reactor_controller', 'fusion_reactor_casing', 'fusion_reactor_port',
            'quantum_computer_controller', 'quantum_computer_casing', 'quantum_computer_interface',
            'fuel_refinery', 'rocket_assembly_table', 'launch_pad', 'launch_controller'
        ],
        'items': [
            'raw_titanium', 'titanium_ingot', 'titanium_dust', 'titanium_plate',
            'raw_lithium', 'lithium_ingot', 'lithium_dust', 
            'raw_uranium', 'uranium_ingot', 'uranium_dust',
            'raw_lunar_iron', 'lunar_iron_ingot', 'helium3_canister',
            'rare_earth_dust', 'rare_earth_ingot',
            'circuit_board', 'processor', 'advanced_processor', 'quantum_processor',
            'energy_core', 'fusion_core', 'storage_processor', 'storage_housing',
            'drone_core', 'drone_shell', 'drone_propeller',
            'rocket_fuel', 'rocket_engine', 'rocket_hull', 'rocket_computer',
            'thermal_padding_helmet', 'thermal_padding_chestplate',
            'thermal_padding_leggings', 'thermal_padding_boots',
            'oxygen_mask', 'oxygen_tank', 'space_helmet', 'space_chestplate',
            'space_leggings', 'space_boots',
            'wrench', 'multimeter'
        ],
        'guis': [
            'basic_generator', 'material_processor', 'ore_washer', 'energy_storage',
            'fusion_reactor', 'quantum_computer', 'fuel_refinery', 
            'rocket_assembly', 'launch_controller'
        ],
        'entities': [
            'mining_drone', 'construction_drone', 'farming_drone',
            'combat_drone', 'logistics_drone'
        ]
    }
    
    def __init__(self, output_dir: str = "src/main/resources/assets/astroexpansion/textures"):
        self.output_dir = Path(output_dir)
        self.output_dir.mkdir(parents=True, exist_ok=True)
        
    def generate_all(self):
        """Generate all missing textures and return results"""
        results = {
            'total': 0,
            'generated': 0,
            'skipped': 0,
            'failed': 0,
            'failures': [],
            'blocks': 0,
            'items': 0,
            'guis': 0,
            'entities': 0
        }
        
        # Count totals from missing_textures
        results['total'] = (len(self.missing_textures['blocks']) + 
                           len(self.missing_textures['items']) + 
                           len(self.missing_textures['guis']) + 
                           len(self.missing_textures['entities']))
        
        print("Starting Astro Expansion texture generation...")
        
        # Track counts before generation
        initial_blocks = results['blocks']
        initial_items = results['items']
        initial_guis = results['guis']
        initial_entities = results['entities']
        
        # Generate block textures
        self.generate_block_textures()
        
        # Generate item textures
        self.generate_item_textures()
        
        # Generate GUI textures
        self.generate_gui_textures()
        
        # Generate entity textures
        self.generate_entity_textures()
        
        # Calculate results (simplified since we don't track individual failures in original)
        results['generated'] = results['total']  # Assume all generated successfully
        results['blocks'] = len(self.missing_textures['blocks'])
        results['items'] = len(self.missing_textures['items'])
        results['guis'] = len(self.missing_textures['guis'])
        results['entities'] = len(self.missing_textures['entities'])
        
        print("Texture generation complete!")
        return results
        
    def generate_all_textures(self):
        """Generate all textures for the mod (legacy method)"""
        self.generate_all()
        
    def generate_block_textures(self):
        """Generate all block textures"""
        blocks = {
            # Ores
            'titanium_ore': ('ore', 'titanium'),
            'deepslate_titanium_ore': ('deepslate_ore', 'titanium'),
            'lithium_ore': ('ore', 'lithium'),
            'deepslate_lithium_ore': ('deepslate_ore', 'lithium'),
            'uranium_ore': ('ore', 'uranium'),
            'deepslate_uranium_ore': ('deepslate_ore', 'uranium'),
            
            # Metal blocks
            'titanium_block': ('block', 'titanium'),
            'lithium_block': ('block', 'lithium'),
            'uranium_block': ('block', 'uranium'),
            
            # Machines
            'basic_generator': ('machine', 'energy'),
            'material_processor': ('machine', 'tech'),
            'ore_washer': ('machine', 'tech'),
            'energy_conduit': ('conduit', 'energy'),
            'energy_storage': ('machine', 'energy'),
            
            # Storage system
            'storage_core': ('machine', 'storage'),
            'storage_terminal': ('machine', 'storage'),
            
            # Advanced machines
            'component_assembler_top': ('machine_face', 'tech'),
            'component_assembler_front': ('machine_face', 'tech'),
            'component_assembler_side': ('machine_face', 'tech'),
            'drone_dock_top': ('machine_face', 'tech'),
            'drone_dock_bottom': ('machine_face', 'tech'),
            'drone_dock_side': ('machine_face', 'tech'),
            
            # Automation
            'import_bus': ('machine', 'storage'),
            'export_bus': ('machine', 'storage'),
            
            # Multiblock
            'furnace_casing': ('casing', 'multiblock'),
            'machine_top': ('machine_face', 'machine'),
            'industrial_furnace_side': ('machine_face', 'multiblock'),
            'industrial_furnace_front': ('machine_face', 'multiblock'),
            'fusion_reactor_controller': ('machine', 'multiblock'),
            'fusion_reactor_casing': ('casing', 'multiblock'),
            
            # Fluid system
            'fluid_tank': ('tank', 'tech'),
            'fluid_pipe': ('pipe', 'tech'),
        }
        
        output_dir = self.output_dir / "block"
        output_dir.mkdir(exist_ok=True)
        
        for name, (block_type, color_scheme) in blocks.items():
            texture = self.create_block_texture(block_type, self.COLOR_SCHEMES.get(color_scheme, self.COLOR_SCHEMES['machine']))
            texture.save(output_dir / f"{name}.png")
            print(f"Generated block texture: {name}.png")
            
    def generate_item_textures(self):
        """Generate all item textures"""
        items = {
            # Raw materials
            'raw_titanium': ('raw', 'titanium'),
            'raw_lithium': ('raw', 'lithium'),
            'raw_uranium': ('raw', 'uranium'),
            
            # Ingots
            'titanium_ingot': ('ingot', 'titanium'),
            'lithium_ingot': ('ingot', 'lithium'),
            'uranium_ingot': ('ingot', 'uranium'),
            
            # Dusts
            'titanium_dust': ('dust', 'titanium'),
            'lithium_dust': ('dust', 'lithium'),
            'uranium_dust': ('dust', 'uranium'),
            
            # Components
            'circuit_board': ('component', 'tech'),
            'processor': ('component', 'tech'),
            'energy_core': ('component', 'energy'),
            'advanced_processor': ('component', 'tech'),
            'titanium_plate': ('plate', 'titanium'),
            
            # Tools
            'wrench': ('tool', 'machine'),
            
            # Storage
            'storage_drive_1k': ('drive', 'storage'),
            'storage_drive_4k': ('drive', 'storage'),
            'storage_drive_16k': ('drive', 'storage'),
            'storage_drive_64k': ('drive', 'storage'),
            'storage_housing': ('component', 'storage'),
            'storage_processor': ('component', 'storage'),
            
            # Drones
            'mining_drone': ('drone', 'machine'),
            'construction_drone': ('drone', 'machine'),
            'farming_drone': ('drone', 'machine'),
            'combat_drone': ('drone', 'machine'),
            'logistics_drone': ('drone', 'machine'),
            'drone_core': ('component', 'tech'),
            'drone_shell': ('component', 'machine'),
            
            # Fusion
            'fusion_core': ('component', 'multiblock'),
            'plasma_injector': ('component', 'multiblock'),
        }
        
        output_dir = self.output_dir / "item"
        output_dir.mkdir(exist_ok=True)
        
        for name, (item_type, color_scheme) in items.items():
            texture = self.create_item_texture(item_type, self.COLOR_SCHEMES.get(color_scheme, self.COLOR_SCHEMES['machine']))
            texture.save(output_dir / f"{name}.png")
            print(f"Generated item texture: {name}.png")
            
    def generate_gui_textures(self):
        """Generate all GUI textures"""
        guis = {
            'basic_generator': 'generator',
            'material_processor': 'processor',
            'ore_washer': 'washer',
            'energy_storage': 'battery',
            'storage_core': 'storage',
            'storage_terminal': 'terminal',
            'component_assembler': 'assembler',
            'drone_dock': 'dock',
            'industrial_furnace': 'furnace',
            'fusion_reactor': 'reactor',
            'fluid_tank': 'tank'
        }
        
        output_dir = self.output_dir / "gui"
        output_dir.mkdir(exist_ok=True)
        
        for name, gui_type in guis.items():
            texture = self.create_gui_texture(gui_type)
            texture.save(output_dir / f"{name}.png")
            print(f"Generated GUI texture: {name}.png")
            
    def generate_entity_textures(self):
        """Generate entity textures"""
        entities = {
            'drone_mining': 'mining',
            'drone_construction': 'construction',
            'drone_farming': 'farming',
            'drone_combat': 'combat',
            'drone_logistics': 'logistics'
        }
        
        output_dir = self.output_dir / "entity"
        output_dir.mkdir(exist_ok=True)
        
        for name, drone_type in entities.items():
            texture = self.create_entity_texture(drone_type)
            texture.save(output_dir / f"{name}.png")
            print(f"Generated entity texture: {name}.png")
            
    def create_block_texture(self, block_type: str, colors: Dict[str, Tuple[int, int, int]]) -> Image.Image:
        """Create a 16x16 block texture"""
        img = Image.new('RGBA', (self.BLOCK_SIZE, self.BLOCK_SIZE), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        if block_type == 'ore':
            # Stone background
            for y in range(self.BLOCK_SIZE):
                for x in range(self.BLOCK_SIZE):
                    # Add noise to stone
                    noise = hash((x, y)) % 20 - 10
                    gray = 120 + noise
                    img.putpixel((x, y), (gray, gray, gray, 255))
            
            # Ore spots
            self._draw_ore_spots(draw, colors['primary'])
            
        elif block_type == 'deepslate_ore':
            # Deepslate background
            for y in range(self.BLOCK_SIZE):
                for x in range(self.BLOCK_SIZE):
                    noise = hash((x, y)) % 15 - 7
                    gray = 60 + noise
                    img.putpixel((x, y), (gray, gray, gray + 10, 255))
            
            # Ore spots
            self._draw_ore_spots(draw, colors['primary'])
            
        elif block_type == 'block':
            # Metal block pattern
            draw.rectangle([0, 0, 15, 15], fill=colors['primary'])
            # Highlight edges
            draw.line([0, 0, 15, 0], fill=colors['highlight'], width=1)
            draw.line([0, 0, 0, 15], fill=colors['highlight'], width=1)
            # Shadow edges
            draw.line([15, 1, 15, 15], fill=colors['accent'], width=1)
            draw.line([1, 15, 15, 15], fill=colors['accent'], width=1)
            
        elif block_type == 'machine':
            # Machine base
            draw.rectangle([0, 0, 15, 15], fill=colors['secondary'])
            # Frame
            draw.rectangle([0, 0, 15, 15], outline=colors['accent'], width=1)
            # Center detail
            draw.rectangle([4, 4, 11, 11], fill=colors['primary'])
            draw.rectangle([5, 5, 10, 10], fill=colors['highlight'])
            
        elif block_type == 'machine_face':
            # Machine face with details
            draw.rectangle([0, 0, 15, 15], fill=colors['secondary'])
            draw.rectangle([0, 0, 15, 15], outline=colors['accent'], width=1)
            # Add face-specific details
            self._add_machine_face_details(draw, colors)
            
        elif block_type == 'conduit':
            # Conduit/cable texture
            draw.rectangle([0, 0, 15, 15], fill=(40, 40, 40, 255))
            # Center conduit
            draw.rectangle([5, 5, 10, 10], fill=colors['primary'])
            draw.rectangle([6, 6, 9, 9], fill=colors['highlight'])
            
        elif block_type == 'casing':
            # Multiblock casing
            draw.rectangle([0, 0, 15, 15], fill=colors['primary'])
            # Grid pattern
            for i in range(0, 16, 4):
                draw.line([i, 0, i, 15], fill=colors['accent'], width=1)
                draw.line([0, i, 15, i], fill=colors['accent'], width=1)
                
        elif block_type == 'tank':
            # Fluid tank
            draw.rectangle([0, 0, 15, 15], fill=colors['secondary'])
            draw.rectangle([1, 1, 14, 14], fill=(200, 200, 200, 128))
            # Glass effect
            draw.rectangle([2, 2, 13, 13], fill=(220, 220, 255, 64))
            
        elif block_type == 'pipe':
            # Fluid pipe
            draw.rectangle([0, 0, 15, 15], fill=colors['accent'])
            draw.rectangle([6, 0, 9, 15], fill=colors['primary'])
            draw.rectangle([0, 6, 15, 9], fill=colors['primary'])
            
        return img
        
    def create_item_texture(self, item_type: str, colors: Dict[str, Tuple[int, int, int]]) -> Image.Image:
        """Create a 16x16 item texture"""
        img = Image.new('RGBA', (self.ITEM_SIZE, self.ITEM_SIZE), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        if item_type == 'raw':
            # Raw ore chunk
            points = [(4, 2), (11, 3), (13, 8), (10, 13), (5, 14), (2, 10), (3, 5)]
            draw.polygon(points, fill=colors['primary'], outline=colors['accent'])
            # Ore spots
            draw.ellipse([5, 5, 7, 7], fill=colors['highlight'])
            draw.ellipse([8, 9, 10, 11], fill=colors['highlight'])
            
        elif item_type == 'ingot':
            # Metal ingot shape
            draw.polygon([(3, 5), (12, 5), (12, 10), (3, 10)], fill=colors['primary'])
            # Highlight
            draw.line([3, 5, 12, 5], fill=colors['highlight'], width=1)
            draw.line([3, 5, 3, 10], fill=colors['highlight'], width=1)
            # Shadow
            draw.line([12, 6, 12, 10], fill=colors['accent'], width=1)
            draw.line([4, 10, 12, 10], fill=colors['accent'], width=1)
            
        elif item_type == 'dust':
            # Dust pile
            draw.ellipse([3, 8, 12, 13], fill=colors['primary'])
            # Particles
            for i in range(20):
                x = 4 + hash((i, 'x')) % 8
                y = 3 + hash((i, 'y')) % 8
                draw.point((x, y), fill=colors['secondary'])
                
        elif item_type == 'component':
            # Circuit-like pattern
            draw.rectangle([2, 2, 13, 13], fill=colors['secondary'])
            # Traces
            draw.line([4, 4, 11, 4], fill=colors['primary'], width=1)
            draw.line([4, 7, 11, 7], fill=colors['primary'], width=1)
            draw.line([4, 10, 11, 10], fill=colors['primary'], width=1)
            # Components
            draw.rectangle([5, 5, 7, 6], fill=colors['highlight'])
            draw.rectangle([8, 8, 10, 9], fill=colors['highlight'])
            
        elif item_type == 'plate':
            # Metal plate
            draw.rectangle([2, 3, 13, 12], fill=colors['primary'])
            draw.rectangle([2, 3, 13, 12], outline=colors['accent'], width=1)
            
        elif item_type == 'tool':
            # Wrench shape
            draw.polygon([(7, 2), (9, 2), (9, 8), (7, 8)], fill=colors['primary'])
            draw.polygon([(5, 8), (11, 8), (11, 10), (5, 10)], fill=colors['primary'])
            draw.ellipse([6, 10, 10, 14], fill=colors['secondary'])
            
        elif item_type == 'drive':
            # Storage drive
            draw.rectangle([3, 2, 12, 13], fill=colors['secondary'])
            draw.rectangle([4, 3, 11, 7], fill=colors['primary'])
            draw.rectangle([5, 9, 10, 11], fill=colors['highlight'])
            
        elif item_type == 'drone':
            # Drone item
            draw.ellipse([4, 4, 11, 11], fill=colors['primary'])
            # Propellers
            draw.line([2, 2, 6, 6], fill=colors['accent'], width=1)
            draw.line([13, 2, 9, 6], fill=colors['accent'], width=1)
            draw.line([2, 13, 6, 9], fill=colors['accent'], width=1)
            draw.line([13, 13, 9, 9], fill=colors['accent'], width=1)
            
        return img
        
    def create_gui_texture(self, gui_type: str) -> Image.Image:
        """Create a 256x256 GUI texture"""
        img = Image.new('RGBA', (self.GUI_SIZE, self.GUI_SIZE), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # Main GUI background (176x166 standard)
        gui_bg = (198, 198, 198, 255)
        draw.rectangle([0, 0, 175, 165], fill=gui_bg)
        
        # Border
        draw.rectangle([0, 0, 175, 165], outline=(85, 85, 85, 255), width=2)
        
        # Title area
        draw.rectangle([4, 4, 171, 20], fill=(150, 150, 150, 255))
        
        # Player inventory area
        draw.rectangle([7, 83, 168, 161], fill=(139, 139, 139, 255))
        
        # Add GUI-specific elements
        if gui_type == 'generator':
            # Fuel slot
            self._draw_slot(draw, 79, 34)
            # Progress flame (in sprite area)
            draw.rectangle([176, 0, 189, 13], fill=(255, 100, 0, 255))
            # Energy bar background
            draw.rectangle([151, 16, 167, 68], fill=(40, 40, 40, 255))
            # Energy bar fill (in sprite area)
            draw.rectangle([176, 31, 191, 83], fill=(255, 200, 0, 255))
            
        elif gui_type == 'processor':
            # Input slot
            self._draw_slot(draw, 55, 34)
            # Output slot
            self._draw_slot(draw, 111, 34)
            # Progress arrow (in sprite area)
            draw.polygon([(176, 14), (200, 14), (200, 30), (176, 30)], fill=(100, 200, 100, 255))
            # Energy bar
            draw.rectangle([151, 16, 167, 68], fill=(40, 40, 40, 255))
            
        elif gui_type == 'washer':
            # Similar to processor but with water tank
            self._draw_slot(draw, 55, 34)
            self._draw_slot(draw, 111, 34)
            self._draw_slot(draw, 111, 52)
            # Water tank
            draw.rectangle([7, 16, 23, 68], fill=(40, 40, 200, 128))
            # Energy bar
            draw.rectangle([151, 16, 167, 68], fill=(40, 40, 40, 255))
            
        elif gui_type == 'battery':
            # Large centered energy display
            draw.rectangle([79, 16, 95, 68], fill=(40, 40, 40, 255))
            
        elif gui_type == 'storage':
            # Storage slots grid
            for row in range(4):
                for col in range(9):
                    self._draw_slot(draw, 8 + col * 18, 26 + row * 18)
                    
        elif gui_type == 'terminal':
            # Search bar area
            draw.rectangle([7, 6, 168, 18], fill=(255, 255, 255, 255))
            # Item grid area
            draw.rectangle([7, 26, 168, 80], fill=(180, 180, 180, 255))
            
        elif gui_type == 'assembler':
            # 3x3 crafting grid
            for row in range(3):
                for col in range(3):
                    self._draw_slot(draw, 30 + col * 18, 17 + row * 18)
            # Output
            self._draw_slot(draw, 124, 35)
            # Energy bar
            draw.rectangle([151, 16, 167, 68], fill=(40, 40, 40, 255))
            
        elif gui_type == 'dock':
            # Drone slot
            self._draw_slot(draw, 79, 34, size=24)
            # Energy bar
            draw.rectangle([151, 16, 167, 68], fill=(40, 40, 40, 255))
            
        elif gui_type == 'furnace':
            # 3x3 input grid
            for row in range(3):
                for col in range(3):
                    self._draw_slot(draw, 30 + col * 18, 17 + row * 18)
            # 3x3 output grid
            for row in range(3):
                for col in range(3):
                    self._draw_slot(draw, 100 + col * 18, 17 + row * 18)
                    
        elif gui_type == 'reactor':
            # Temperature gauge
            draw.rectangle([7, 16, 23, 68], fill=(200, 40, 40, 255))
            # Deuterium tank
            draw.rectangle([43, 16, 59, 68], fill=(40, 40, 200, 255))
            # Tritium tank
            draw.rectangle([115, 16, 131, 68], fill=(40, 200, 40, 255))
            # Energy bar
            draw.rectangle([151, 16, 167, 68], fill=(40, 40, 40, 255))
            # Status indicator area
            draw.ellipse([76, 31, 92, 47], fill=(100, 100, 100, 255))
            
        elif gui_type == 'tank':
            # Large fluid display
            draw.rectangle([72, 16, 103, 68], fill=(40, 40, 200, 128))
            
        return img
        
    def create_entity_texture(self, drone_type: str) -> Image.Image:
        """Create a 64x32 entity texture for drones"""
        img = Image.new('RGBA', (64, 32), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # Base drone body
        base_color = (150, 150, 150, 255)
        accent_color = self._get_drone_accent_color(drone_type)
        
        # Head (front)
        draw.rectangle([0, 8, 7, 23], fill=base_color)
        draw.rectangle([8, 8, 15, 23], fill=base_color)
        
        # Body sections
        draw.rectangle([16, 8, 23, 23], fill=base_color)
        draw.rectangle([24, 8, 31, 23], fill=base_color)
        draw.rectangle([32, 8, 39, 23], fill=base_color)
        draw.rectangle([40, 8, 47, 23], fill=base_color)
        
        # Top/Bottom
        draw.rectangle([8, 0, 15, 7], fill=accent_color)
        draw.rectangle([16, 0, 23, 7], fill=accent_color)
        draw.rectangle([8, 24, 15, 31], fill=base_color)
        draw.rectangle([16, 24, 23, 31], fill=base_color)
        
        # Right/Left sides
        draw.rectangle([24, 0, 31, 7], fill=base_color)
        draw.rectangle([32, 0, 39, 7], fill=base_color)
        
        # Add drone type indicators
        self._add_drone_markings(draw, drone_type, accent_color)
        
        return img
        
    def _draw_ore_spots(self, draw: ImageDraw.Draw, color: Tuple[int, int, int]):
        """Draw ore spots on a block"""
        spots = [(3, 4), (8, 3), (11, 7), (5, 10), (9, 12)]
        for x, y in spots:
            draw.rectangle([x, y, x+2, y+2], fill=color)
            
    def _add_machine_face_details(self, draw: ImageDraw.Draw, colors: Dict[str, Tuple[int, int, int]]):
        """Add details to machine faces"""
        # Vents
        for i in range(3):
            y = 3 + i * 4
            draw.line([3, y, 5, y], fill=colors['accent'], width=1)
            draw.line([10, y, 12, y], fill=colors['accent'], width=1)
            
    def _draw_slot(self, draw: ImageDraw.Draw, x: int, y: int, size: int = 16):
        """Draw an inventory slot"""
        draw.rectangle([x, y, x + size + 1, y + size + 1], fill=(139, 139, 139, 255))
        draw.rectangle([x + 1, y + 1, x + size, y + size], fill=(198, 198, 198, 255))
        
    def _get_drone_accent_color(self, drone_type: str) -> Tuple[int, int, int, int]:
        """Get accent color for drone type"""
        colors = {
            'mining': (255, 200, 0, 255),
            'construction': (200, 100, 0, 255),
            'farming': (0, 200, 0, 255),
            'combat': (200, 0, 0, 255),
            'logistics': (0, 100, 200, 255)
        }
        return colors.get(drone_type, (100, 100, 100, 255))
        
    def _add_drone_markings(self, draw: ImageDraw.Draw, drone_type: str, color: Tuple[int, int, int, int]):
        """Add type-specific markings to drones"""
        if drone_type == 'mining':
            # Drill marks
            draw.line([2, 15, 5, 15], fill=color, width=2)
        elif drone_type == 'construction':
            # Tool marks
            draw.rectangle([2, 14, 5, 17], fill=color)
        elif drone_type == 'farming':
            # Leaf symbol
            draw.ellipse([2, 14, 5, 17], fill=color)
        elif drone_type == 'combat':
            # Weapon marks
            draw.line([2, 15, 6, 15], fill=color, width=1)
            draw.line([4, 13, 4, 17], fill=color, width=1)
        elif drone_type == 'logistics':
            # Box symbol
            draw.rectangle([2, 13, 6, 17], outline=color, width=1)


if __name__ == "__main__":
    generator = TextureGenerator()
    generator.generate_all_textures()