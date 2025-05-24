#!/usr/bin/env python3
"""
Unified Texture Generator for AstroExpansion
Generates all textures with consistent space/astro theme and 3D-ish appearance
"""

import os
import json
import numpy as np
from PIL import Image, ImageDraw, ImageFont, ImageFilter
from dataclasses import dataclass
from typing import List, Tuple, Dict, Optional
from enum import Enum
import colorsys

# Color Palette
class Colors:
    # Primary - Deep space blues
    SPACE_DARK = (10, 25, 41)
    SPACE_MID = (30, 58, 95)
    SPACE_LIGHT = (44, 82, 130)
    
    # Secondary - Metallic
    METAL_DARK = (64, 64, 64)
    METAL_MID = (128, 128, 128)
    METAL_LIGHT = (192, 192, 192)
    METAL_SHINE = (224, 224, 224)
    
    # Energy colors
    ENERGY_BLUE = (0, 212, 255)
    ENERGY_CYAN = (0, 153, 204)
    ENERGY_ORANGE = (255, 107, 53)
    ENERGY_RED = (255, 69, 0)
    ENERGY_GREEN = (0, 255, 136)
    ENERGY_PURPLE = (157, 78, 221)
    
    # Material specific
    TITANIUM = (168, 168, 180)
    LITHIUM = (255, 192, 203)
    URANIUM = (64, 255, 64)
    
    # UI Colors
    UI_BG = (17, 24, 39)
    UI_PANEL = (30, 41, 59)
    UI_BORDER = (71, 85, 105)
    UI_ACCENT = (59, 130, 246)
    UI_TEXT = (226, 232, 240)

class TextureType(Enum):
    BLOCK = "block"
    ITEM = "item"
    GUI = "gui"
    ENTITY = "entity"

@dataclass
class TextureConfig:
    name: str
    type: TextureType
    size: Tuple[int, int]
    base_color: Tuple[int, int, int]
    accent_color: Optional[Tuple[int, int, int]] = None
    has_animation: bool = False
    animation_frames: int = 1
    tier: Optional[str] = None  # basic, advanced, elite
    category: Optional[str] = None  # machine, ore, item, etc.

class TextureGenerator:
    def __init__(self, output_dir: str):
        self.output_dir = output_dir
        self.ensure_directories()
        
    def ensure_directories(self):
        """Create necessary output directories"""
        dirs = [
            "blocks",
            "blocks/machines",
            "blocks/ores",
            "blocks/storage",
            "blocks/multiblock",
            "blocks/decorative",
            "items",
            "items/materials",
            "items/components",
            "items/tools",
            "items/armor",
            "items/upgrades",
            "gui",
            "gui/container",
            "gui/widgets",
            "entity",
            "entity/rocket",
            "entity/drone",
            "particle"
        ]
        
        for dir_name in dirs:
            os.makedirs(os.path.join(self.output_dir, dir_name), exist_ok=True)
    
    def create_3d_border(self, img: Image.Image, thickness: int = 2, 
                        highlight_color: Tuple[int, int, int] = None,
                        shadow_color: Tuple[int, int, int] = None) -> Image.Image:
        """Add 3D beveled border effect"""
        if highlight_color is None:
            highlight_color = Colors.METAL_SHINE
        if shadow_color is None:
            shadow_color = Colors.METAL_DARK
            
        draw = ImageDraw.Draw(img)
        width, height = img.size
        
        # Top and left borders (highlight)
        for i in range(thickness):
            # Top
            draw.line([(i, i), (width - i - 1, i)], fill=highlight_color)
            # Left
            draw.line([(i, i), (i, height - i - 1)], fill=highlight_color)
        
        # Bottom and right borders (shadow)
        for i in range(thickness):
            # Bottom
            draw.line([(i, height - i - 1), (width - i - 1, height - i - 1)], fill=shadow_color)
            # Right
            draw.line([(width - i - 1, i), (width - i - 1, height - i - 1)], fill=shadow_color)
        
        return img
    
    def add_noise_texture(self, img: Image.Image, intensity: float = 0.1) -> Image.Image:
        """Add subtle noise for texture"""
        pixels = np.array(img)
        noise = np.random.normal(0, intensity * 255, pixels.shape)
        pixels = np.clip(pixels + noise, 0, 255).astype(np.uint8)
        return Image.fromarray(pixels)
    
    def create_gradient(self, size: Tuple[int, int], 
                       color1: Tuple[int, int, int], 
                       color2: Tuple[int, int, int], 
                       direction: str = 'vertical') -> Image.Image:
        """Create gradient background"""
        img = Image.new('RGBA', size)
        pixels = img.load()
        
        for y in range(size[1]):
            for x in range(size[0]):
                if direction == 'vertical':
                    ratio = y / size[1]
                elif direction == 'horizontal':
                    ratio = x / size[0]
                elif direction == 'diagonal':
                    ratio = (x + y) / (size[0] + size[1])
                else:
                    ratio = 0.5
                
                r = int(color1[0] * (1 - ratio) + color2[0] * ratio)
                g = int(color1[1] * (1 - ratio) + color2[1] * ratio)
                b = int(color1[2] * (1 - ratio) + color2[2] * ratio)
                pixels[x, y] = (r, g, b, 255)
        
        return img
    
    def create_energy_glow(self, size: Tuple[int, int], 
                          color: Tuple[int, int, int], 
                          intensity: float = 0.8) -> Image.Image:
        """Create glowing energy effect"""
        # Create glow on transparent background
        img = Image.new('RGBA', size, (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        center_x, center_y = size[0] // 2, size[1] // 2
        max_radius = min(size) // 3
        
        # Draw multiple circles with decreasing opacity
        for i in range(max_radius, 0, -2):
            alpha = int(255 * intensity * (i / max_radius))
            glow_color = (*color, alpha)
            draw.ellipse([center_x - i, center_y - i, center_x + i, center_y + i], 
                        fill=glow_color)
        
        # Apply gaussian blur for glow effect
        img = img.filter(ImageFilter.GaussianBlur(radius=3))
        
        return img
    
    def create_tech_pattern(self, size: Tuple[int, int], 
                           pattern_type: str = 'circuit') -> Image.Image:
        """Create tech/circuit patterns"""
        img = Image.new('RGBA', size, (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        if pattern_type == 'circuit':
            # Draw circuit-like patterns
            line_color = (*Colors.ENERGY_CYAN, 128)
            for i in range(0, size[0], 8):
                if np.random.random() > 0.5:
                    draw.line([(i, 0), (i, size[1])], fill=line_color, width=1)
            for i in range(0, size[1], 8):
                if np.random.random() > 0.5:
                    draw.line([(0, i), (size[0], i)], fill=line_color, width=1)
            
            # Add nodes
            for i in range(0, size[0], 16):
                for j in range(0, size[1], 16):
                    if np.random.random() > 0.7:
                        draw.ellipse([i-2, j-2, i+2, j+2], fill=Colors.ENERGY_CYAN)
        
        elif pattern_type == 'hex':
            # Draw hexagonal pattern
            hex_size = 8
            for y in range(0, size[1], int(hex_size * 1.5)):
                for x in range(0, size[0], hex_size * 2):
                    offset = hex_size if (y // int(hex_size * 1.5)) % 2 else 0
                    self._draw_hexagon(draw, x + offset, y, hex_size, 
                                     (*Colors.UI_BORDER, 64))
        
        return img
    
    def _draw_hexagon(self, draw: ImageDraw.Draw, x: int, y: int, 
                      size: int, color: Tuple[int, int, int, int]):
        """Draw a hexagon"""
        angles = [i * 60 for i in range(6)]
        points = []
        for angle in angles:
            px = x + size * np.cos(np.radians(angle))
            py = y + size * np.sin(np.radians(angle))
            points.append((px, py))
        draw.polygon(points, outline=color)
    
    def generate_machine_texture(self, config: TextureConfig) -> Image.Image:
        """Generate machine block texture"""
        img = Image.new('RGBA', config.size)
        
        # Base metal texture
        base = self.create_gradient(config.size, Colors.METAL_DARK, Colors.METAL_MID)
        base = self.add_noise_texture(base, 0.05)
        img.paste(base)
        
        # Add tier-specific elements
        draw = ImageDraw.Draw(img)
        
        if config.tier == 'basic':
            # Industrial look with rivets
            for i in range(2, config.size[0] - 2, 8):
                for j in [2, config.size[1] - 3]:
                    draw.ellipse([i-1, j-1, i+1, j+1], fill=Colors.METAL_DARK)
        
        elif config.tier == 'advanced':
            # Clean panels with energy lines
            panel_img = Image.new('RGBA', (12, 12), Colors.SPACE_MID)
            panel_img = self.create_3d_border(panel_img, 1)
            img.paste(panel_img, (2, 2))
            
            # Energy indicator
            draw.rectangle([3, 3, 5, 11], fill=Colors.ENERGY_CYAN)
        
        elif config.tier == 'elite':
            # Quantum/holographic effect
            holo = self.create_tech_pattern((config.size[0]-4, config.size[1]-4), 'hex')
            img.paste(holo, (2, 2), holo)
            
            # Quantum core
            glow = self.create_energy_glow((8, 8), Colors.ENERGY_PURPLE, 0.9)
            img.paste(glow, (config.size[0]//2 - 4, config.size[1]//2 - 4), glow)
        
        # Add 3D border
        img = self.create_3d_border(img)
        
        # Machine-specific details
        if 'generator' in config.name:
            # Add power symbol
            self._draw_power_symbol(draw, img.size, config.accent_color or Colors.ENERGY_BLUE)
        elif 'processor' in config.name:
            # Add grinding teeth
            self._draw_processor_teeth(draw, img.size)
        elif 'washer' in config.name:
            # Add water tank
            self._draw_water_tank(draw, img.size)
        
        return img
    
    def _draw_power_symbol(self, draw: ImageDraw.Draw, size: Tuple[int, int], 
                          color: Tuple[int, int, int]):
        """Draw power/lightning symbol"""
        cx, cy = size[0] // 2, size[1] // 2
        points = [
            (cx - 2, cy - 4),
            (cx + 1, cy - 1),
            (cx - 1, cy + 1),
            (cx + 2, cy + 4),
            (cx, cy + 2),
            (cx + 1, cy),
            (cx - 1, cy - 2),
        ]
        draw.polygon(points, fill=color)
    
    def _draw_processor_teeth(self, draw: ImageDraw.Draw, size: Tuple[int, int]):
        """Draw processor grinding teeth"""
        teeth_y = size[1] // 2
        for x in range(4, size[0] - 4, 3):
            draw.polygon([(x, teeth_y - 2), (x + 2, teeth_y), 
                         (x, teeth_y + 2)], fill=Colors.METAL_LIGHT)
    
    def _draw_water_tank(self, draw: ImageDraw.Draw, size: Tuple[int, int]):
        """Draw water tank with glass"""
        tank_rect = [size[0] - 10, 4, size[0] - 4, size[1] - 4]
        # Glass background
        draw.rectangle(tank_rect, fill=(100, 150, 200, 128))
        # Water level
        water_rect = [tank_rect[0], tank_rect[1] + 3, tank_rect[2], tank_rect[3] - 1]
        draw.rectangle(water_rect, fill=(0, 100, 200, 200))
    
    def generate_ore_texture(self, config: TextureConfig) -> Image.Image:
        """Generate ore block texture"""
        img = Image.new('RGBA', config.size)
        
        # Stone base
        stone_color = Colors.METAL_DARK if 'deepslate' in config.name else Colors.METAL_MID
        base = self.create_gradient(config.size, stone_color, 
                                   tuple(c - 20 for c in stone_color))
        base = self.add_noise_texture(base, 0.15)
        img.paste(base)
        
        # Add ore veins
        draw = ImageDraw.Draw(img)
        ore_color = config.accent_color or Colors.TITANIUM
        
        # Create crystalline ore patches
        num_crystals = np.random.randint(3, 6)
        for _ in range(num_crystals):
            x = np.random.randint(2, config.size[0] - 6)
            y = np.random.randint(2, config.size[1] - 6)
            size = np.random.randint(3, 6)
            
            # Crystal shape
            points = [
                (x, y - size),
                (x + size, y),
                (x, y + size),
                (x - size, y)
            ]
            
            # Main crystal
            draw.polygon(points, fill=ore_color)
            
            # Highlight
            highlight = tuple(min(255, c + 50) for c in ore_color)
            draw.polygon([(x, y - size), (x + size//2, y - size//2), 
                         (x, y), (x - size//2, y - size//2)], fill=highlight)
            
            # Add glow for radioactive ores
            if 'uranium' in config.name:
                glow = self.create_energy_glow((size*2, size*2), Colors.URANIUM, 0.5)
                img.paste(glow, (x - size, y - size), glow)
        
        return img
    
    def generate_item_texture(self, config: TextureConfig) -> Image.Image:
        """Generate item texture"""
        img = Image.new('RGBA', config.size, (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        if 'ingot' in config.name:
            # Metallic ingot with 3D effect
            ingot_rect = [2, 4, 14, 12]
            
            # Base
            draw.rectangle(ingot_rect, fill=config.base_color)
            
            # Top shine
            shine_rect = [ingot_rect[0], ingot_rect[1], ingot_rect[2], ingot_rect[1] + 2]
            draw.rectangle(shine_rect, fill=tuple(min(255, c + 60) for c in config.base_color))
            
            # Side shadow
            shadow_rect = [ingot_rect[0], ingot_rect[3] - 2, ingot_rect[2], ingot_rect[3]]
            draw.rectangle(shadow_rect, fill=tuple(max(0, c - 40) for c in config.base_color))
            
        elif 'dust' in config.name:
            # Pile of dust
            cx, cy = config.size[0] // 2, config.size[1] // 2
            
            # Draw pile shape
            for y in range(cy - 3, cy + 4):
                width = 6 - abs(y - cy)
                for x in range(cx - width, cx + width + 1):
                    # Add some randomness for dust texture
                    if np.random.random() > 0.2:
                        color = config.base_color
                        if np.random.random() > 0.5:
                            color = tuple(min(255, c + 20) for c in color)
                        draw.point((x, y), fill=color)
        
        elif 'circuit' in config.name or 'processor' in config.name:
            # High-tech circuit board
            # Base PCB
            draw.rectangle([1, 1, 15, 15], fill=(20, 80, 40))
            
            # Circuit traces
            trace_color = Colors.ENERGY_CYAN
            # Horizontal traces
            for y in [4, 8, 12]:
                draw.line([(2, y), (14, y)], fill=trace_color, width=1)
            # Vertical traces
            for x in [4, 8, 12]:
                draw.line([(x, 2), (x, 14)], fill=trace_color, width=1)
            
            # Components
            if 'advanced' in config.name or 'quantum' in config.name:
                # Quantum processor core
                draw.rectangle([6, 6, 10, 10], fill=Colors.ENERGY_PURPLE)
                glow = self.create_energy_glow((8, 8), Colors.ENERGY_PURPLE, 0.7)
                img.paste(glow, (4, 4), glow)
            else:
                # Regular processor
                draw.rectangle([6, 6, 10, 10], fill=Colors.METAL_DARK)
                draw.rectangle([7, 7, 9, 9], fill=Colors.METAL_MID)
        
        elif 'upgrade' in config.name:
            # Upgrade module
            # Base card
            draw.rectangle([2, 1, 14, 15], fill=Colors.SPACE_DARK, outline=Colors.METAL_MID)
            
            # Upgrade tier indicator
            tier_colors = {
                '1': Colors.ENERGY_GREEN,
                '2': Colors.ENERGY_BLUE,
                '3': Colors.ENERGY_PURPLE
            }
            tier = config.name[-1] if config.name[-1] in tier_colors else '1'
            
            # Tier gems
            for i in range(int(tier)):
                y = 3 + i * 4
                draw.ellipse([6, y, 10, y + 3], fill=tier_colors[tier])
            
            # Circuit pattern
            pattern = self.create_tech_pattern((10, 12), 'circuit')
            img.paste(pattern, (3, 2), pattern)
        
        return img
    
    def generate_gui_texture(self, config: TextureConfig) -> Image.Image:
        """Generate GUI texture"""
        img = Image.new('RGBA', config.size)
        
        if 'container' in config.category:
            # Machine GUI background
            # Main panel
            draw = ImageDraw.Draw(img)
            draw.rectangle([0, 0, config.size[0]-1, config.size[1]-1], 
                          fill=Colors.UI_BG, outline=Colors.UI_BORDER)
            
            # Inner panel with 3D effect
            inner_rect = [4, 4, config.size[0]-5, config.size[1]-5]
            draw.rectangle(inner_rect, fill=Colors.UI_PANEL)
            
            # Inventory area
            inv_y = config.size[1] - 100
            draw.rectangle([7, inv_y, config.size[0]-8, config.size[1]-8], 
                          fill=Colors.UI_BG, outline=Colors.UI_BORDER)
            
            # Title area
            draw.rectangle([7, 7, config.size[0]-8, 20], 
                          fill=Colors.SPACE_DARK, outline=Colors.UI_ACCENT)
            
            # Machine-specific elements
            if 'generator' in config.name:
                # Energy bar area
                draw.rectangle([7, 24, 23, 76], fill=Colors.SPACE_DARK, outline=Colors.UI_BORDER)
                # Fuel slot
                self._draw_slot(draw, 80, 45)
                
            elif 'processor' in config.name:
                # Input slot
                self._draw_slot(draw, 56, 35)
                # Output slot
                self._draw_slot(draw, 116, 35)
                # Progress arrow area
                draw.rectangle([79, 35, 105, 51], fill=Colors.UI_BG)
                
        elif 'button' in config.name:
            # GUI button
            draw = ImageDraw.Draw(img)
            
            # Button states (normal, hover, pressed)
            states = config.size[1] // 20  # Assuming 20px height per state
            
            for i in range(states):
                y_offset = i * 20
                
                if i == 0:  # Normal
                    color = Colors.UI_PANEL
                    border = Colors.UI_BORDER
                elif i == 1:  # Hover
                    color = tuple(min(255, c + 20) for c in Colors.UI_PANEL)
                    border = Colors.UI_ACCENT
                else:  # Pressed
                    color = Colors.UI_BG
                    border = Colors.UI_ACCENT
                
                # Draw button
                button_rect = [0, y_offset, config.size[0]-1, y_offset + 19]
                draw.rectangle(button_rect, fill=color, outline=border)
                
                # Inner highlight for 3D effect
                if i != 2:  # Not pressed
                    highlight_rect = [1, y_offset + 1, config.size[0]-2, y_offset + 2]
                    draw.rectangle(highlight_rect, fill=Colors.METAL_SHINE)
        
        return img
    
    def _draw_slot(self, draw: ImageDraw.Draw, x: int, y: int):
        """Draw item slot for GUI"""
        slot_size = 18
        # Dark background
        draw.rectangle([x-1, y-1, x+slot_size, y+slot_size], 
                      fill=Colors.SPACE_DARK, outline=Colors.UI_BORDER)
        # Inner shadow for depth
        draw.rectangle([x, y, x+slot_size-1, y+slot_size-1], 
                      fill=Colors.UI_BG)
    
    def generate_entity_texture(self, config: TextureConfig) -> Image.Image:
        """Generate entity texture (rockets, drones)"""
        img = Image.new('RGBA', config.size, (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        if 'rocket' in config.category:
            # Rocket texture map
            if 'probe' in config.name:
                # Small probe rocket
                self._draw_rocket_body(draw, img.size, Colors.METAL_MID, Colors.ENERGY_ORANGE, 'probe')
            elif 'personal' in config.name:
                # Personal rocket
                self._draw_rocket_body(draw, img.size, Colors.METAL_LIGHT, Colors.ENERGY_BLUE, 'personal')
            elif 'cargo' in config.name:
                # Cargo shuttle
                self._draw_rocket_body(draw, img.size, Colors.TITANIUM, Colors.ENERGY_CYAN, 'cargo')
                
        elif 'drone' in config.category:
            # Drone texture map
            drone_types = {
                'mining': (Colors.ENERGY_ORANGE, 'drill'),
                'construction': (Colors.ENERGY_GREEN, 'arm'),
                'farming': (Colors.ENERGY_GREEN, 'spray'),
                'combat': (Colors.ENERGY_RED, 'weapon'),
                'logistics': (Colors.ENERGY_BLUE, 'claw')
            }
            
            for drone_type, (color, tool) in drone_types.items():
                if drone_type in config.name:
                    self._draw_drone_body(draw, img.size, Colors.METAL_MID, color, tool)
                    break
        
        return img
    
    def _draw_rocket_body(self, draw: ImageDraw.Draw, size: Tuple[int, int], 
                         body_color: Tuple[int, int, int], 
                         accent_color: Tuple[int, int, int], 
                         rocket_type: str):
        """Draw rocket body texture"""
        # This would be the UV map for the 3D model
        # Simplified representation
        
        # Body sections
        section_height = size[1] // 4
        
        # Nose cone
        nose_rect = [0, 0, size[0], section_height]
        draw.rectangle(nose_rect, fill=body_color)
        # Accent stripe
        draw.rectangle([0, section_height - 4, size[0], section_height], fill=accent_color)
        
        # Main body
        for i in range(1, 3):
            y = i * section_height
            draw.rectangle([0, y, size[0], y + section_height], fill=body_color)
            
            # Panel lines
            for x in range(0, size[0], 16):
                draw.line([(x, y), (x, y + section_height)], fill=Colors.METAL_DARK)
            
            # Windows for personal rocket
            if rocket_type == 'personal' and i == 1:
                for x in range(8, size[0] - 8, 16):
                    draw.ellipse([x-3, y+8, x+3, y+14], fill=Colors.ENERGY_CYAN)
        
        # Engine section
        engine_rect = [0, 3 * section_height, size[0], size[1]]
        draw.rectangle(engine_rect, fill=Colors.METAL_DARK)
        
        # Thrusters
        for x in range(8, size[0] - 8, 16):
            draw.ellipse([x-4, size[1]-12, x+4, size[1]-4], fill=accent_color)
    
    def _draw_drone_body(self, draw: ImageDraw.Draw, size: Tuple[int, int],
                        body_color: Tuple[int, int, int],
                        accent_color: Tuple[int, int, int],
                        tool_type: str):
        """Draw drone body texture"""
        # Main body (center square of texture)
        body_rect = [size[0]//4, size[1]//4, 3*size[0]//4, 3*size[1]//4]
        draw.rectangle(body_rect, fill=body_color)
        
        # Accent panels
        panel_size = size[0]//8
        for x in [body_rect[0] + 4, body_rect[2] - panel_size - 4]:
            for y in [body_rect[1] + 4, body_rect[3] - panel_size - 4]:
                draw.rectangle([x, y, x + panel_size, y + panel_size], fill=accent_color)
        
        # Tool attachment point
        tool_rect = [size[0]//2 - 8, body_rect[3], size[0]//2 + 8, size[1]]
        draw.rectangle(tool_rect, fill=Colors.METAL_DARK)
        
        # Propellers (corners of texture)
        prop_size = size[0]//6
        for x in [0, size[0] - prop_size]:
            for y in [0, size[1] - prop_size]:
                draw.ellipse([x, y, x + prop_size, y + prop_size], 
                           fill=Colors.METAL_LIGHT, outline=Colors.METAL_DARK)
    
    def generate_all_textures(self):
        """Generate all textures for the mod"""
        textures = []
        
        # Blocks - Tiered Machines
        for tier in ['basic', 'advanced', 'elite']:
            tier_colors = {
                'basic': Colors.METAL_MID,
                'advanced': Colors.SPACE_MID,
                'elite': Colors.SPACE_DARK
            }
            
            # Power Generators
            textures.append(TextureConfig(
                f"power_generator_{tier}",
                TextureType.BLOCK,
                (16, 16),
                tier_colors[tier],
                Colors.ENERGY_ORANGE if tier == 'basic' else Colors.ENERGY_BLUE if tier == 'advanced' else Colors.ENERGY_PURPLE,
                tier=tier,
                category='machine'
            ))
            
            # Material Processors
            textures.append(TextureConfig(
                f"material_processor_{tier}",
                TextureType.BLOCK,
                (16, 16),
                tier_colors[tier],
                Colors.ENERGY_GREEN,
                tier=tier,
                category='machine'
            ))
            
            # Ore Washers
            textures.append(TextureConfig(
                f"ore_washer_{tier}",
                TextureType.BLOCK,
                (16, 16),
                tier_colors[tier],
                Colors.ENERGY_BLUE,
                tier=tier,
                category='machine'
            ))
        
        # Ores
        ore_configs = [
            ("titanium_ore", Colors.TITANIUM),
            ("deepslate_titanium_ore", Colors.TITANIUM),
            ("lithium_ore", Colors.LITHIUM),
            ("deepslate_lithium_ore", Colors.LITHIUM),
            ("uranium_ore", Colors.URANIUM),
            ("deepslate_uranium_ore", Colors.URANIUM),
            ("helium3_ore", Colors.ENERGY_CYAN),
        ]
        
        for ore_name, ore_color in ore_configs:
            textures.append(TextureConfig(
                ore_name,
                TextureType.BLOCK,
                (16, 16),
                Colors.METAL_MID,
                ore_color,
                category='ore'
            ))
        
        # Items - Ingots
        ingot_configs = [
            ("titanium_ingot", Colors.TITANIUM),
            ("lithium_ingot", Colors.LITHIUM),
            ("uranium_ingot", Colors.URANIUM),
            ("lunar_iron_ingot", Colors.METAL_LIGHT),
        ]
        
        for ingot_name, ingot_color in ingot_configs:
            textures.append(TextureConfig(
                ingot_name,
                TextureType.ITEM,
                (16, 16),
                ingot_color,
                category='item'
            ))
        
        # Items - Dusts
        dust_configs = [
            ("titanium_dust", Colors.TITANIUM),
            ("lithium_dust", Colors.LITHIUM),
            ("uranium_dust", Colors.URANIUM),
            ("moon_dust_item", (200, 200, 200)),
            ("cosmic_dust", Colors.ENERGY_PURPLE),
        ]
        
        for dust_name, dust_color in dust_configs:
            textures.append(TextureConfig(
                dust_name,
                TextureType.ITEM,
                (16, 16),
                dust_color,
                category='item'
            ))
        
        # Components
        component_configs = [
            ("circuit_board", Colors.ENERGY_CYAN),
            ("processor", Colors.METAL_MID),
            ("advanced_processor", Colors.ENERGY_BLUE),
            ("quantum_processor", Colors.ENERGY_PURPLE),
            ("energy_core", Colors.ENERGY_BLUE),
            ("fusion_core", Colors.ENERGY_ORANGE),
            ("storage_processor", Colors.ENERGY_GREEN),
        ]
        
        for comp_name, comp_color in component_configs:
            textures.append(TextureConfig(
                comp_name,
                TextureType.ITEM,
                (16, 16),
                comp_color,
                category='item'
            ))
        
        # Upgrades
        for tier in ['1', '2', '3']:
            for upgrade_type in ['speed', 'efficiency', 'fortune']:
                textures.append(TextureConfig(
                    f"{upgrade_type}_upgrade_{tier}",
                    TextureType.ITEM,
                    (16, 16),
                    Colors.SPACE_DARK,
                    category='item'
                ))
        
        # GUI Textures
        gui_machines = [
            "basic_generator",
            "material_processor",
            "ore_washer",
            "energy_storage",
            "storage_terminal",
            "component_assembler",
            "fusion_reactor",
            "quantum_computer",
            "rocket_workbench",
        ]
        
        for machine in gui_machines:
            textures.append(TextureConfig(
                machine,
                TextureType.GUI,
                (256, 256),
                Colors.UI_BG,
                category='container'
            ))
        
        # GUI Widgets
        widget_configs = [
            ("button", (200, 60)),  # 3 states
            ("tab", (28, 32)),
            ("scrollbar", (12, 64)),
            ("slot_highlight", (18, 18)),
            ("energy_bar", (16, 52)),
            ("fluid_bar", (16, 52)),
            ("progress_arrow", (24, 16)),
        ]
        
        for widget_name, widget_size in widget_configs:
            textures.append(TextureConfig(
                widget_name,
                TextureType.GUI,
                widget_size,
                Colors.UI_ACCENT,
                category='widgets'
            ))
        
        # Entity Textures - Rockets
        rocket_configs = [
            ("probe_rocket", (64, 64)),
            ("personal_rocket", (128, 128)),
            ("cargo_shuttle", (256, 256)),
        ]
        
        for rocket_name, rocket_size in rocket_configs:
            textures.append(TextureConfig(
                rocket_name,
                TextureType.ENTITY,
                rocket_size,
                Colors.METAL_MID,
                category='rocket'
            ))
        
        # Entity Textures - Drones
        drone_types = ["mining", "construction", "farming", "combat", "logistics"]
        
        for drone_type in drone_types:
            textures.append(TextureConfig(
                f"{drone_type}_drone",
                TextureType.ENTITY,
                (64, 64),
                Colors.METAL_MID,
                category='drone'
            ))
        
        # Generate all textures
        for config in textures:
            print(f"Generating {config.name}...")
            
            if config.type == TextureType.BLOCK and config.category == 'machine':
                texture = self.generate_machine_texture(config)
            elif config.type == TextureType.BLOCK and config.category == 'ore':
                texture = self.generate_ore_texture(config)
            elif config.type == TextureType.ITEM:
                texture = self.generate_item_texture(config)
            elif config.type == TextureType.GUI:
                texture = self.generate_gui_texture(config)
            elif config.type == TextureType.ENTITY:
                texture = self.generate_entity_texture(config)
            else:
                # Default texture
                texture = Image.new('RGBA', config.size, config.base_color)
            
            # Save texture
            self.save_texture(texture, config)
            
            # Generate animation metadata if needed
            if config.has_animation:
                self.generate_animation_meta(config)
    
    def save_texture(self, texture: Image.Image, config: TextureConfig):
        """Save texture to appropriate directory"""
        if config.type == TextureType.BLOCK:
            subdir = f"blocks/{config.category}" if config.category else "blocks"
        elif config.type == TextureType.ITEM:
            subdir = f"items/{config.category}" if config.category else "items"
        elif config.type == TextureType.GUI:
            subdir = f"gui/{config.category}" if config.category else "gui"
        elif config.type == TextureType.ENTITY:
            subdir = f"entity/{config.category}" if config.category else "entity"
        else:
            subdir = ""
        
        output_path = os.path.join(self.output_dir, subdir, f"{config.name}.png")
        texture.save(output_path)
        print(f"  Saved: {output_path}")
    
    def generate_animation_meta(self, config: TextureConfig):
        """Generate .mcmeta file for animated textures"""
        meta = {
            "animation": {
                "frametime": 20,
                "frames": list(range(config.animation_frames))
            }
        }
        
        meta_path = os.path.join(self.output_dir, f"{config.name}.png.mcmeta")
        with open(meta_path, 'w') as f:
            json.dump(meta, f, indent=2)


def main():
    """Main execution"""
    output_dir = "/workspaces/AstroExpansion/src/main/resources/assets/astroexpansion/textures"
    
    print("AstroExpansion Unified Texture Generator")
    print("========================================")
    print(f"Output directory: {output_dir}")
    print()
    
    generator = TextureGenerator(output_dir)
    generator.generate_all_textures()
    
    print("\nTexture generation complete!")


if __name__ == "__main__":
    main()