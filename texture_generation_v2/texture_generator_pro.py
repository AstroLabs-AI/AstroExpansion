#!/usr/bin/env python3
"""
Professional Texture Generator for AstroExpansion
Generates high-quality pixel art textures with proper techniques
"""

import os
import json
import numpy as np
from PIL import Image, ImageDraw, ImageFilter, ImageOps, ImageEnhance
from dataclasses import dataclass
from typing import List, Tuple, Dict, Optional
from enum import Enum
import random
from pathlib import Path

class MaterialType(Enum):
    METAL_ROUGH = "metal_rough"
    METAL_POLISHED = "metal_polished"
    METAL_BRUSHED = "metal_brushed"
    STONE = "stone"
    CRYSTAL = "crystal"
    TECH_PANEL = "tech_panel"
    CIRCUIT = "circuit"
    GLASS = "glass"
    ENERGY = "energy"

class TierType(Enum):
    BASIC = "basic"
    ADVANCED = "advanced"
    ELITE = "elite"
    ULTIMATE = "ultimate"

@dataclass
class ColorPalette:
    """Limited color palette for pixel art"""
    primary: List[Tuple[int, int, int]]
    shadows: List[Tuple[int, int, int]]
    highlights: List[Tuple[int, int, int]]
    accents: List[Tuple[int, int, int]]
    
    def get_ramp(self, base_color: Tuple[int, int, int], steps: int = 4) -> List[Tuple[int, int, int]]:
        """Generate a color ramp for shading"""
        ramp = []
        for i in range(steps):
            factor = 0.4 + (0.8 * i / (steps - 1))  # 40% to 120% brightness
            color = tuple(int(min(255, c * factor)) for c in base_color)
            ramp.append(color)
        return ramp

# Professional color palettes
PALETTES = {
    TierType.BASIC: ColorPalette(
        primary=[(89, 89, 89), (105, 105, 105), (121, 121, 121)],
        shadows=[(45, 45, 45), (64, 64, 64)],
        highlights=[(140, 140, 140), (160, 160, 160)],
        accents=[(255, 136, 0), (255, 170, 0)]  # Orange energy
    ),
    TierType.ADVANCED: ColorPalette(
        primary=[(55, 71, 89), (71, 92, 115), (89, 115, 142)],
        shadows=[(35, 45, 56), (45, 58, 71)],
        highlights=[(115, 142, 170), (142, 170, 198)],
        accents=[(0, 174, 255), (89, 206, 255)]  # Blue energy
    ),
    TierType.ELITE: ColorPalette(
        primary=[(35, 25, 56), (56, 45, 89), (71, 58, 105)],
        shadows=[(20, 15, 35), (28, 20, 45)],
        highlights=[(89, 71, 121), (105, 89, 140)],
        accents=[(170, 0, 255), (204, 89, 255)]  # Purple energy
    )
}

class TextureGenerator:
    def __init__(self, assets_dir: str):
        self.assets_dir = Path(assets_dir)
        self.patterns = {}
        self.materials = {}
        self.load_assets()
    
    def load_assets(self):
        """Load pattern and material assets"""
        # Create default patterns if assets don't exist
        self.patterns = {
            'rivet': self.create_rivet_pattern(),
            'vent': self.create_vent_pattern(),
            'panel_lines': self.create_panel_lines(),
            'scratches': self.create_scratch_pattern(),
            'tech_detail': self.create_tech_detail_pattern()
        }
        
        self.materials = {
            MaterialType.METAL_ROUGH: self.create_metal_texture(rough=True),
            MaterialType.METAL_POLISHED: self.create_metal_texture(rough=False),
            MaterialType.METAL_BRUSHED: self.create_brushed_metal_texture(),
            MaterialType.TECH_PANEL: self.create_tech_panel_texture(),
            MaterialType.STONE: self.create_stone_texture(),
            MaterialType.CRYSTAL: self.create_crystal_texture()
        }
    
    def create_rivet_pattern(self) -> np.ndarray:
        """Create a rivet detail pattern"""
        pattern = np.zeros((5, 5, 4), dtype=np.uint8)
        # Outer ring
        for angle in range(0, 360, 45):
            x = int(2 + 1.5 * np.cos(np.radians(angle)))
            y = int(2 + 1.5 * np.sin(np.radians(angle)))
            pattern[y, x] = (64, 64, 64, 255)
        # Inner highlight
        pattern[2, 2] = (160, 160, 160, 255)
        pattern[1, 2] = (140, 140, 140, 255)
        # Shadow
        pattern[3, 2] = (45, 45, 45, 255)
        pattern[3, 3] = (45, 45, 45, 255)
        return pattern
    
    def create_vent_pattern(self) -> np.ndarray:
        """Create a ventilation grill pattern"""
        pattern = np.zeros((16, 16, 4), dtype=np.uint8)
        # Horizontal slats
        for y in range(2, 14, 3):
            pattern[y:y+2, 2:14] = (89, 89, 89, 255)
            pattern[y, 2:14] = (105, 105, 105, 255)  # Highlight
            pattern[y+1, 2:14] = (64, 64, 64, 255)   # Shadow
        return pattern
    
    def create_panel_lines(self) -> np.ndarray:
        """Create panel separation lines"""
        pattern = np.zeros((16, 16, 4), dtype=np.uint8)
        # Vertical line
        pattern[:, 7:9] = (45, 45, 45, 128)
        pattern[:, 7] = (35, 35, 35, 255)
        pattern[:, 8] = (140, 140, 140, 128)
        # Horizontal line
        pattern[7:9, :] = (45, 45, 45, 128)
        pattern[7, :] = (35, 35, 35, 255)
        pattern[8, :] = (140, 140, 140, 128)
        return pattern
    
    def create_scratch_pattern(self) -> np.ndarray:
        """Create surface scratch details"""
        pattern = np.zeros((16, 16, 4), dtype=np.uint8)
        # Random scratches
        for _ in range(3):
            start_x = random.randint(2, 13)
            start_y = random.randint(2, 13)
            length = random.randint(3, 6)
            angle = random.uniform(0, np.pi)
            
            for i in range(length):
                x = int(start_x + i * np.cos(angle))
                y = int(start_y + i * np.sin(angle))
                if 0 <= x < 16 and 0 <= y < 16:
                    pattern[y, x] = (140, 140, 140, 64)  # Light scratch
                    if y + 1 < 16:
                        pattern[y + 1, x] = (45, 45, 45, 64)  # Shadow
        return pattern
    
    def create_tech_detail_pattern(self) -> np.ndarray:
        """Create technological detail overlay"""
        pattern = np.zeros((16, 16, 4), dtype=np.uint8)
        # Small LED indicators
        led_positions = [(3, 3), (12, 3), (3, 12)]
        for x, y in led_positions:
            pattern[y, x] = (0, 255, 0, 255)
            # Glow effect
            for dx in [-1, 0, 1]:
                for dy in [-1, 0, 1]:
                    if dx != 0 or dy != 0:
                        nx, ny = x + dx, y + dy
                        if 0 <= nx < 16 and 0 <= ny < 16:
                            pattern[ny, nx] = (0, 128, 0, 128)
        return pattern
    
    def create_metal_texture(self, rough: bool = True) -> np.ndarray:
        """Create base metal texture with proper pixel art noise"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        base_color = (105, 105, 105, 255)
        
        # Fill with base color
        texture[:, :] = base_color
        
        # Add noise pattern
        if rough:
            # Rough metal - more variation
            noise_colors = [(89, 89, 89), (105, 105, 105), (121, 121, 121)]
            for y in range(16):
                for x in range(16):
                    if random.random() < 0.3:
                        texture[y, x] = (*random.choice(noise_colors), 255)
        else:
            # Polished metal - subtle variation
            for y in range(16):
                for x in range(16):
                    if random.random() < 0.1:
                        brightness = random.randint(-10, 10)
                        color = tuple(min(255, max(0, c + brightness)) for c in base_color[:3])
                        texture[y, x] = (*color, 255)
        
        # Add subtle directional shading
        for y in range(16):
            shade_factor = 0.9 + (0.2 * y / 16)
            for x in range(16):
                current = texture[y, x]
                new_color = tuple(int(min(255, c * shade_factor)) for c in current[:3])
                texture[y, x] = (*new_color, 255)
        
        return texture
    
    def create_brushed_metal_texture(self) -> np.ndarray:
        """Create brushed metal texture with horizontal lines"""
        texture = self.create_metal_texture(rough=False)
        
        # Add horizontal brush lines
        for y in range(0, 16, 2):
            brightness = random.randint(-5, 5)
            for x in range(16):
                current = texture[y, x]
                color = tuple(int(min(255, max(0, int(c) + brightness))) for c in current[:3])
                texture[y, x] = (*color, 255)
        
        return texture
    
    def create_tech_panel_texture(self) -> np.ndarray:
        """Create high-tech panel base texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Base dark metal
        texture[:, :] = (71, 71, 71, 255)
        
        # Panel borders
        texture[0, :] = (45, 45, 45, 255)
        texture[-1, :] = (45, 45, 45, 255)
        texture[:, 0] = (45, 45, 45, 255)
        texture[:, -1] = (45, 45, 45, 255)
        
        # Inner highlight
        texture[1, 1:-1] = (89, 89, 89, 255)
        texture[1:-1, 1] = (89, 89, 89, 255)
        
        # Tech pattern
        for y in range(4, 12, 3):
            texture[y, 4:12] = (56, 56, 56, 255)
            texture[y+1, 4:12] = (64, 64, 64, 255)
        
        return texture
    
    def create_stone_texture(self) -> np.ndarray:
        """Create stone/ore base texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Stone colors
        stone_colors = [(128, 128, 128), (121, 121, 121), (135, 135, 135), (115, 115, 115)]
        
        # Create stone pattern
        for y in range(16):
            for x in range(16):
                # Use perlin-like noise
                noise = (np.sin(x * 0.5) * np.cos(y * 0.5) + 1) / 2
                color_index = int(noise * len(stone_colors))
                texture[y, x] = (*stone_colors[min(color_index, len(stone_colors)-1)], 255)
        
        # Add cracks
        crack_positions = [(5, 3, 8, 7), (10, 8, 13, 12)]
        for x1, y1, x2, y2 in crack_positions:
            # Simple line algorithm
            steps = max(abs(x2-x1), abs(y2-y1))
            for i in range(steps):
                x = int(x1 + (x2-x1) * i / steps)
                y = int(y1 + (y2-y1) * i / steps)
                if 0 <= x < 16 and 0 <= y < 16:
                    texture[y, x] = (64, 64, 64, 255)
        
        return texture
    
    def create_crystal_texture(self) -> np.ndarray:
        """Create crystal/gem texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Crystal shape (hexagonal)
        center_x, center_y = 8, 8
        crystal_color = (100, 200, 255, 255)  # Light blue
        
        # Main crystal body
        for y in range(5, 11):
            width = 6 - abs(y - 8)
            for x in range(center_x - width, center_x + width + 1):
                if 0 <= x < 16:
                    # Add facet shading
                    shade = 0.7 + 0.3 * (x - (center_x - width)) / (width * 2)
                    color = tuple(int(c * shade) for c in crystal_color[:3])
                    texture[y, x] = (*color, 255)
        
        # Highlights
        texture[6, 6] = (150, 230, 255, 255)
        texture[6, 7] = (180, 240, 255, 255)
        
        return texture
    
    def apply_lighting(self, texture: np.ndarray, light_direction: Tuple[float, float] = (-1, -1)) -> np.ndarray:
        """Apply directional lighting with proper pixel art techniques"""
        height, width = texture.shape[:2]
        lit_texture = texture.copy()
        
        # Normalize light direction
        light_length = np.sqrt(light_direction[0]**2 + light_direction[1]**2)
        light_dir = (light_direction[0]/light_length, light_direction[1]/light_length)
        
        # Create height map from brightness
        height_map = np.zeros((height, width))
        for y in range(height):
            for x in range(width):
                if texture[y, x, 3] > 0:  # If pixel is not transparent
                    brightness = int(sum(int(c) for c in texture[y, x, :3])) / 3
                    height_map[y, x] = brightness / 255.0
        
        # Apply lighting
        for y in range(1, height-1):
            for x in range(1, width-1):
                if texture[y, x, 3] == 0:
                    continue
                
                # Calculate normal from height map
                dx = height_map[y, x+1] - height_map[y, x-1]
                dy = height_map[y+1, x] - height_map[y-1, x]
                
                # Dot product with light direction
                lighting = max(0, -dx * light_dir[0] - dy * light_dir[1])
                lighting = 0.7 + 0.3 * lighting  # Adjust range
                
                # Apply to color
                color = lit_texture[y, x]
                lit_texture[y, x] = tuple(int(min(255, c * lighting)) for c in color[:3]) + (color[3],)
        
        return lit_texture
    
    def add_dithering(self, texture: np.ndarray, palette: ColorPalette) -> np.ndarray:
        """Add proper pixel art dithering"""
        dithered = texture.copy()
        height, width = texture.shape[:2]
        
        # Floyd-Steinberg dithering matrix
        for y in range(height-1):
            for x in range(1, width-1):
                if texture[y, x, 3] == 0:
                    continue
                
                old_color = dithered[y, x, :3].astype(np.float32)
                # Find nearest color in palette
                all_colors = palette.primary + palette.shadows + palette.highlights
                nearest = min(all_colors, key=lambda c: sum((float(a)-float(b))**2 for a, b in zip(old_color, c)))
                
                dithered[y, x, :3] = nearest
                
                # Calculate and distribute error
                error = old_color - np.array(nearest)
                
                # Right pixel
                if x + 1 < width and dithered[y, x+1, 3] > 0:
                    dithered[y, x+1, :3] = np.clip(dithered[y, x+1, :3] + error * 7/16, 0, 255)
                
                # Bottom-left pixel
                if y + 1 < height and x - 1 >= 0 and dithered[y+1, x-1, 3] > 0:
                    dithered[y+1, x-1, :3] = np.clip(dithered[y+1, x-1, :3] + error * 3/16, 0, 255)
                
                # Bottom pixel
                if y + 1 < height and dithered[y+1, x, 3] > 0:
                    dithered[y+1, x, :3] = np.clip(dithered[y+1, x, :3] + error * 5/16, 0, 255)
                
                # Bottom-right pixel
                if y + 1 < height and x + 1 < width and dithered[y+1, x+1, 3] > 0:
                    dithered[y+1, x+1, :3] = np.clip(dithered[y+1, x+1, :3] + error * 1/16, 0, 255)
        
        return dithered.astype(np.uint8)
    
    def add_edge_highlight(self, texture: np.ndarray, color: Tuple[int, int, int] = (255, 255, 255)) -> np.ndarray:
        """Add selective edge highlighting for depth"""
        highlighted = texture.copy()
        height, width = texture.shape[:2]
        
        # Only highlight edges where there's a significant change
        for y in range(1, height-1):
            for x in range(1, width-1):
                if texture[y, x, 3] == 0:
                    continue
                
                # Check if this is an edge
                is_edge = False
                
                # Check transparency edges
                if texture[y-1, x, 3] == 0 or texture[y, x-1, 3] == 0:
                    is_edge = True
                
                # Check color difference edges
                current_brightness = int(sum(int(c) for c in texture[y, x, :3])) / 3
                for dy, dx in [(-1, 0), (0, -1)]:
                    ny, nx = y + dy, x + dx
                    if 0 <= ny < height and 0 <= nx < width and texture[ny, nx, 3] > 0:
                        neighbor_brightness = int(sum(int(c) for c in texture[ny, nx, :3])) / 3
                        if abs(current_brightness - neighbor_brightness) > 30:
                            is_edge = True
                
                if is_edge and (y == 1 or x == 1):  # Top-left edges only
                    # Blend highlight with existing color
                    blend_factor = 0.3
                    highlighted[y, x, :3] = tuple(
                        int(c * (1 - blend_factor) + color[i] * blend_factor)
                        for i, c in enumerate(texture[y, x, :3])
                    )
        
        return highlighted
    
    def generate_machine_block(self, machine_type: str, tier: TierType) -> Image.Image:
        """Generate a complete machine block texture"""
        # Get base material
        base_material = self.materials[MaterialType.TECH_PANEL].copy()
        
        # Apply tier-specific coloring
        palette = PALETTES[tier]
        base_material = self.apply_palette(base_material, palette)
        
        # Apply lighting
        base_material = self.apply_lighting(base_material)
        
        # Add machine-specific details
        if machine_type == "generator":
            # Add vents
            vent = self.patterns['vent']
            base_material = self.composite_pattern(base_material, vent, (0, 0))
            
            # Add power indicator
            indicator = self.create_power_indicator(palette.accents[0])
            base_material = self.composite_pattern(base_material, indicator, (11, 11))
            
        elif machine_type == "processor":
            # Add grinding mechanism hint
            for x in range(4, 12, 2):
                base_material[7:9, x] = (*palette.shadows[0], 255)
                base_material[8, x] = (*palette.primary[0], 255)
            
        elif machine_type == "washer":
            # Add water tank suggestion
            tank_area = base_material[2:14, 10:14].copy()
            tank_area[:, :] = (100, 150, 200, 128)  # Bluish tint
            base_material[2:14, 10:14] = tank_area
        
        # Add tier-specific decorations
        if tier == TierType.BASIC:
            # Add rivets
            rivet_positions = [(2, 2), (13, 2), (2, 13), (13, 13)]
            rivet = self.patterns['rivet']
            for x, y in rivet_positions:
                base_material = self.composite_pattern(base_material, rivet, (x-2, y-2))
            
            # Add scratches for wear
            scratches = self.patterns['scratches']
            base_material = self.composite_pattern(base_material, scratches, (0, 0), alpha=0.3)
            
        elif tier == TierType.ADVANCED:
            # Add panel lines
            lines = self.patterns['panel_lines']
            base_material = self.composite_pattern(base_material, lines, (0, 0), alpha=0.5)
            
            # Add tech details
            tech = self.patterns['tech_detail']
            base_material = self.composite_pattern(base_material, tech, (0, 0))
            
        elif tier == TierType.ELITE:
            # Add energy glow effect
            glow_positions = [(4, 4), (11, 4), (4, 11), (11, 11)]
            for x, y in glow_positions:
                base_material[y-1:y+2, x-1:x+2] = (*palette.accents[1], 200)
        
        # Add selective edge highlighting
        base_material = self.add_edge_highlight(base_material, palette.highlights[1])
        
        # Apply dithering for better color transitions
        base_material = self.add_dithering(base_material, palette)
        
        # Convert to PIL Image
        return Image.fromarray(base_material, mode='RGBA')
    
    def apply_palette(self, texture: np.ndarray, palette: ColorPalette) -> np.ndarray:
        """Apply color palette to grayscale texture"""
        result = texture.copy()
        height, width = texture.shape[:2]
        
        for y in range(height):
            for x in range(width):
                if texture[y, x, 3] > 0:
                    brightness = int(sum(int(c) for c in texture[y, x, :3])) / 3 / 255
                    
                    if brightness < 0.3:
                        color = palette.shadows[0]
                    elif brightness < 0.5:
                        color = palette.primary[0]
                    elif brightness < 0.7:
                        color = palette.primary[1]
                    elif brightness < 0.85:
                        color = palette.primary[2]
                    else:
                        color = palette.highlights[0]
                    
                    result[y, x, :3] = color
        
        return result
    
    def composite_pattern(self, base: np.ndarray, pattern: np.ndarray, 
                         position: Tuple[int, int], alpha: float = 1.0) -> np.ndarray:
        """Composite a pattern onto base texture"""
        result = base.copy()
        px, py = position
        ph, pw = pattern.shape[:2]
        
        # Calculate bounds
        x1 = max(0, px)
        y1 = max(0, py)
        x2 = min(base.shape[1], px + pw)
        y2 = min(base.shape[0], py + ph)
        
        # Pattern bounds
        px1 = max(0, -px)
        py1 = max(0, -py)
        px2 = px1 + (x2 - x1)
        py2 = py1 + (y2 - y1)
        
        # Composite
        for y in range(y2 - y1):
            for x in range(x2 - x1):
                if pattern[py1 + y, px1 + x, 3] > 0:
                    pattern_alpha = pattern[py1 + y, px1 + x, 3] / 255 * alpha
                    result[y1 + y, x1 + x] = (
                        result[y1 + y, x1 + x] * (1 - pattern_alpha) +
                        pattern[py1 + y, px1 + x] * pattern_alpha
                    ).astype(np.uint8)
        
        return result
    
    def create_power_indicator(self, color: Tuple[int, int, int]) -> np.ndarray:
        """Create a small power indicator light"""
        indicator = np.zeros((5, 5, 4), dtype=np.uint8)
        
        # Center bright
        indicator[2, 2] = (*color, 255)
        
        # Inner glow
        for y, x in [(1, 2), (3, 2), (2, 1), (2, 3)]:
            indicator[y, x] = tuple(int(c * 0.7) for c in color) + (255,)
        
        # Outer glow
        for y in range(5):
            for x in range(5):
                if indicator[y, x, 3] == 0:
                    dist = np.sqrt((x - 2)**2 + (y - 2)**2)
                    if dist < 2.5:
                        alpha = int(255 * (1 - dist / 2.5) * 0.3)
                        indicator[y, x] = tuple(int(c * 0.3) for c in color) + (alpha,)
        
        return indicator
    
    def generate_ore_texture(self, ore_type: str, stone_type: str = "stone") -> Image.Image:
        """Generate ore texture with realistic crystal formations"""
        # Get base stone texture
        if stone_type == "deepslate":
            base = self.create_deepslate_texture()
        else:
            base = self.materials[MaterialType.STONE].copy()
        
        # Ore colors
        ore_colors = {
            "titanium": [(168, 168, 180), (184, 184, 196), (200, 200, 212)],
            "lithium": [(255, 192, 203), (255, 218, 224), (255, 230, 235)],
            "uranium": [(64, 255, 64), (96, 255, 96), (128, 255, 128)],
            "helium3": [(180, 230, 255), (200, 240, 255), (220, 250, 255)]
        }
        
        colors = ore_colors.get(ore_type, [(255, 255, 255)])
        
        # Add ore veins with better distribution
        vein_positions = self.generate_ore_vein_positions()
        
        for vx, vy in vein_positions:
            # Create crystalline formation
            crystal_size = random.randint(2, 4)
            crystal_shape = self.create_crystal_shape(crystal_size, colors)
            
            # Add to base texture
            base = self.composite_pattern(base, crystal_shape, (vx-crystal_size//2, vy-crystal_size//2))
        
        # Add subtle glow for radioactive ores
        if ore_type == "uranium":
            base = self.add_radioactive_glow(base, (64, 255, 64))
        
        return Image.fromarray(base, mode='RGBA')
    
    def create_deepslate_texture(self) -> np.ndarray:
        """Create deepslate base texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Deepslate colors (darker than stone)
        deepslate_colors = [(64, 64, 68), (56, 56, 60), (72, 72, 76), (60, 60, 64)]
        
        # Layered pattern
        for y in range(16):
            base_color = deepslate_colors[y % len(deepslate_colors)]
            for x in range(16):
                # Add slight horizontal variation
                variation = random.randint(-5, 5)
                color = tuple(min(255, max(0, c + variation)) for c in base_color)
                texture[y, x] = (*color, 255)
        
        return texture
    
    def generate_ore_vein_positions(self) -> List[Tuple[int, int]]:
        """Generate realistic ore vein positions"""
        positions = []
        
        # Main vein
        main_x = random.randint(4, 11)
        main_y = random.randint(4, 11)
        positions.append((main_x, main_y))
        
        # Connected veins
        for _ in range(random.randint(2, 4)):
            offset_x = random.randint(-3, 3)
            offset_y = random.randint(-3, 3)
            new_x = max(2, min(13, main_x + offset_x))
            new_y = max(2, min(13, main_y + offset_y))
            positions.append((new_x, new_y))
        
        return positions
    
    def create_crystal_shape(self, size: int, colors: List[Tuple[int, int, int]]) -> np.ndarray:
        """Create a crystalline ore formation"""
        crystal = np.zeros((size*2, size*2, 4), dtype=np.uint8)
        center = size
        
        # Create faceted crystal
        for angle in range(0, 360, 60):  # Hexagonal
            for r in range(size):
                x = int(center + r * np.cos(np.radians(angle)))
                y = int(center + r * np.sin(np.radians(angle)))
                
                if 0 <= x < size*2 and 0 <= y < size*2:
                    # Color based on distance from center
                    color_index = min(r, len(colors) - 1)
                    crystal[y, x] = (*colors[color_index], 255)
        
        # Fill center
        for y in range(center-size//2, center+size//2+1):
            for x in range(center-size//2, center+size//2+1):
                if crystal[y, x, 3] == 0:
                    crystal[y, x] = (*colors[0], 255)
        
        return crystal
    
    def add_radioactive_glow(self, texture: np.ndarray, glow_color: Tuple[int, int, int]) -> np.ndarray:
        """Add radioactive glow effect"""
        glowing = texture.copy()
        
        # Find ore pixels
        ore_pixels = []
        for y in range(texture.shape[0]):
            for x in range(texture.shape[1]):
                if np.array_equal(texture[y, x, :3], glow_color):
                    ore_pixels.append((x, y))
        
        # Add glow around ore pixels
        for ox, oy in ore_pixels:
            for dy in range(-2, 3):
                for dx in range(-2, 3):
                    x, y = ox + dx, oy + dy
                    if 0 <= x < 16 and 0 <= y < 16 and (dx != 0 or dy != 0):
                        dist = np.sqrt(dx**2 + dy**2)
                        if dist < 2.5:
                            glow_strength = 0.3 * (1 - dist / 2.5)
                            current = glowing[y, x, :3]
                            glowing[y, x, :3] = np.clip(
                                current + np.array(glow_color) * glow_strength,
                                0, 255
                            ).astype(np.uint8)
        
        return glowing
    
    def generate_item_texture(self, item_type: str, material: str = None) -> Image.Image:
        """Generate item texture with proper pixel art style"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        if item_type == "ingot":
            texture = self.create_ingot_texture(material)
        elif item_type == "dust":
            texture = self.create_dust_texture(material)
        elif item_type == "circuit":
            texture = self.create_circuit_texture()
        elif item_type == "processor":
            texture = self.create_processor_texture(material)
        elif item_type == "upgrade":
            texture = self.create_upgrade_texture(material)
        elif item_type == "tool":
            texture = self.create_tool_texture(material)
        elif item_type == "component":
            texture = self.create_component_texture(material)
        elif item_type == "tank" or item_type == "canister":
            texture = self.create_container_texture(item_type, material)
        elif item_type == "cell":
            texture = self.create_cell_texture(material)
        elif item_type == "fuel":
            texture = self.create_fuel_texture(material)
        elif item_type == "waste":
            texture = self.create_waste_texture(material)
        elif item_type == "data":
            texture = self.create_data_texture(material)
        elif item_type == "book":
            texture = self.create_book_texture(material)
        elif item_type == "bucket":
            texture = self.create_bucket_texture(material)
        elif item_type == "raw":
            texture = self.create_raw_ore_texture(material)
        elif item_type == "crystal":
            texture = self.create_crystal_item_texture(material)
        elif item_type == "mask":
            texture = self.create_mask_texture(material)
        elif item_type == "plate":
            texture = self.create_plate_texture(material)
        elif item_type == "artifact":
            texture = self.create_artifact_texture(material)
        elif item_type == "fragment":
            texture = self.create_fragment_texture(material)
        elif item_type == "key":
            texture = self.create_key_texture(material)
        else:
            # Generic item
            texture = self.create_generic_item_texture(material)
        
        return Image.fromarray(texture, mode='RGBA')
    
    def create_ingot_texture(self, material: str) -> np.ndarray:
        """Create realistic ingot texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Material colors
        material_colors = {
            "titanium": [(168, 168, 180), (184, 184, 196), (152, 152, 164)],
            "lithium": [(220, 220, 230), (235, 235, 245), (205, 205, 215)],
            "uranium": [(100, 200, 100), (120, 220, 120), (80, 180, 80)]
        }
        
        colors = material_colors.get(material, [(128, 128, 128), (144, 144, 144), (112, 112, 112)])
        base, highlight, shadow = colors
        
        # Ingot shape with perspective
        # Top face
        for y in range(4, 7):
            for x in range(3 + (y-4), 13 - (y-4)):
                texture[y, x] = (*highlight, 255)
        
        # Front face
        for y in range(7, 12):
            for x in range(3, 13):
                texture[y, x] = (*base, 255)
        
        # Side face (right)
        for y in range(7, 12):
            x_start = 13
            x_end = 14 - (y - 7) // 2
            for x in range(x_start, min(16, x_end + 1)):
                texture[y, x] = (*shadow, 255)
        
        # Edge highlights
        texture[7, 3:13] = (*highlight, 255)
        texture[7:12, 3] = tuple(int(min(255, c * 1.1)) for c in highlight) + (255,)
        
        # Cast shadow
        for y in range(12, 14):
            shadow_width = 14 - y
            for x in range(2, 2 + shadow_width):
                if x < 16:
                    texture[y, x] = (64, 64, 64, 128)
        
        return texture
    
    def create_dust_texture(self, material: str) -> np.ndarray:
        """Create dust pile texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        material_colors = {
            "titanium": (168, 168, 180),
            "lithium": (255, 192, 203),
            "uranium": (100, 200, 100),
            "cosmic": (100, 50, 150),  # Purple cosmic dust
            "moon": (200, 200, 200)    # Gray moon dust
        }
        
        base_color = material_colors.get(material, (128, 128, 128))
        
        # Create dust pile shape
        pile_heights = [2, 4, 5, 6, 6, 5, 4, 2]
        
        for x, height in enumerate(pile_heights):
            x_pos = x + 4
            for y in range(height):
                y_pos = 13 - y
                
                # Add variation to each particle
                variation = random.randint(-20, 20)
                color = tuple(min(255, max(0, c + variation)) for c in base_color)
                
                # Dithering pattern for texture
                if (x + y) % 2 == 0:
                    texture[y_pos, x_pos] = (*color, 255)
                else:
                    darker = tuple(int(c * 0.8) for c in color)
                    texture[y_pos, x_pos] = (*darker, 255)
                
                # Add scattered particles
                if random.random() < 0.3 and x_pos > 0 and x_pos < 15:
                    scatter_x = x_pos + random.randint(-1, 1)
                    scatter_y = y_pos + random.randint(-1, 1)
                    if 0 <= scatter_x < 16 and 0 <= scatter_y < 16:
                        texture[scatter_y, scatter_x] = (*color, 200)
        
        return texture
    
    def create_circuit_texture(self) -> np.ndarray:
        """Create detailed circuit board texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # PCB base (green)
        pcb_green = (20, 80, 40)
        texture[1:15, 1:15] = (*pcb_green, 255)
        
        # Border
        texture[0, :] = (10, 40, 20, 255)
        texture[-1, :] = (10, 40, 20, 255)
        texture[:, 0] = (10, 40, 20, 255)
        texture[:, -1] = (10, 40, 20, 255)
        
        # Circuit traces
        trace_color = (180, 140, 0)  # Gold traces
        
        # Horizontal traces
        for y in [4, 8, 12]:
            texture[y, 2:14] = (*trace_color, 255)
            # Add width variation
            if y < 15:
                texture[y+1, 3:13] = tuple(int(c * 0.8) for c in trace_color) + (255,)
        
        # Vertical traces
        for x in [4, 8, 12]:
            texture[2:14, x] = (*trace_color, 255)
        
        # Components
        # Central processor
        processor_color = (40, 40, 40)
        texture[6:10, 6:10] = (*processor_color, 255)
        texture[7:9, 7:9] = (60, 60, 60, 255)  # Center detail
        
        # Capacitors
        cap_positions = [(3, 3), (12, 3), (3, 12), (12, 12)]
        for x, y in cap_positions:
            texture[y, x] = (100, 50, 0, 255)  # Brown
            texture[y-1:y+2, x] = (120, 60, 0, 255)
        
        # Solder points
        for y in range(2, 14, 2):
            for x in range(2, 14, 2):
                if texture[y, x, 3] > 0 and sum(int(c) for c in texture[y, x, :3]) < 300:
                    texture[y, x] = (200, 200, 200, 255)
        
        return texture
    
    def create_processor_texture(self, tier: str) -> np.ndarray:
        """Create processor item texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Base chip
        if tier == "advanced":
            chip_color = (40, 40, 80)  # Blue tint
            pin_color = (200, 200, 220)
        elif tier == "quantum":
            chip_color = (60, 20, 80)  # Purple tint
            pin_color = (220, 180, 255)
        else:
            chip_color = (40, 40, 40)  # Basic gray
            pin_color = (180, 180, 180)
        
        # Main chip body
        texture[4:12, 4:12] = (*chip_color, 255)
        
        # Center detail
        texture[6:10, 6:10] = tuple(int(min(255, c * 1.2)) for c in chip_color) + (255,)
        texture[7:9, 7:9] = tuple(int(min(255, c * 1.5)) for c in chip_color) + (255,)
        
        # Pins on sides
        for i in range(5, 11, 2):
            # Left pins
            texture[i, 2:4] = (*pin_color, 255)
            texture[i, 3] = tuple(int(min(255, c * 0.8)) for c in pin_color) + (255,)
            
            # Right pins
            texture[i, 12:14] = (*pin_color, 255)
            texture[i, 12] = tuple(int(min(255, c * 0.8)) for c in pin_color) + (255,)
            
            # Top pins
            texture[2:4, i] = (*pin_color, 255)
            texture[3, i] = tuple(int(min(255, c * 0.8)) for c in pin_color) + (255,)
            
            # Bottom pins
            texture[12:14, i] = (*pin_color, 255)
            texture[12, i] = tuple(int(min(255, c * 0.8)) for c in pin_color) + (255,)
        
        # Add tier-specific effects
        if tier == "quantum":
            # Quantum glow
            texture[7, 7] = (255, 200, 255, 255)
            texture[8, 8] = (255, 200, 255, 255)
        
        return texture
    
    def create_upgrade_texture(self, upgrade_type: str) -> np.ndarray:
        """Create upgrade module texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Base card
        card_color = (60, 60, 80)
        texture[1:15, 2:14] = (*card_color, 255)
        
        # Edge connectors
        connector_color = (180, 140, 0)
        texture[14, 4:12] = (*connector_color, 255)
        texture[13, 4:12] = tuple(int(c * 0.8) for c in connector_color) + (255,)
        
        # Tier gems
        tier = upgrade_type[-1] if upgrade_type[-1].isdigit() else '1'
        gem_colors = {
            '1': (0, 200, 0),
            '2': (0, 150, 255),
            '3': (200, 0, 255)
        }
        gem_color = gem_colors.get(tier, (200, 200, 200))
        
        # Draw tier indicators
        for i in range(int(tier)):
            y = 3 + i * 4
            if y < 12:
                # Gem shape
                texture[y:y+3, 6:10] = (*gem_color, 255)
                texture[y+1, 5:11] = (*gem_color, 255)
                # Highlight
                texture[y, 7] = tuple(int(min(255, c * 1.3)) for c in gem_color) + (255,)
        
        # Circuit pattern overlay
        pattern_color = tuple(int(c * 0.7) for c in card_color)
        for y in range(2, 14, 3):
            texture[y, 3:13] = (*pattern_color, 255)
        
        return texture
    
    def create_tool_texture(self, tool_type: str) -> np.ndarray:
        """Create tool texture (wrench, multimeter, etc.)"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Wrench shape
        handle_color = (100, 60, 30)  # Brown handle
        metal_color = (160, 160, 160)  # Metal head
        
        # Handle
        for y in range(10, 15):
            for x in range(6, 10):
                texture[y, x] = (*handle_color, 255)
        
        # Wrench head
        # Top jaw
        texture[2:5, 5:11] = (*metal_color, 255)
        texture[3, 4:12] = (*metal_color, 255)
        
        # Bottom jaw
        texture[7:10, 5:11] = (*metal_color, 255)
        texture[8, 4:12] = (*metal_color, 255)
        
        # Center connection
        texture[5:7, 6:10] = (*metal_color, 255)
        
        # Add highlights
        texture[2, 6:10] = tuple(int(c * 1.2) for c in metal_color) + (255,)
        texture[3, 5] = tuple(int(c * 1.2) for c in metal_color) + (255,)
        
        return texture
    
    def create_component_texture(self, component_type: str) -> np.ndarray:
        """Create component texture (energy core, plasma injector, etc.)"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Base frame
        frame_color = (80, 80, 80)
        texture[2:14, 2:14] = (*frame_color, 255)
        
        # Core element
        core_color = (0, 200, 255)  # Cyan energy
        texture[5:11, 5:11] = (*core_color, 255)
        texture[6:10, 6:10] = tuple(int(min(255, c * 1.2)) for c in core_color) + (255,)
        texture[7:9, 7:9] = (255, 255, 255, 255)  # Bright center
        
        # Corner connectors
        connector_color = (120, 120, 120)
        for x, y in [(3, 3), (12, 3), (3, 12), (12, 12)]:
            texture[y-1:y+2, x-1:x+2] = (*connector_color, 255)
        
        # Energy glow effect
        for y in range(4, 12):
            for x in range(4, 12):
                if texture[y, x, 3] == 0:
                    dist = np.sqrt((x - 7.5)**2 + (y - 7.5)**2)
                    if dist < 5:
                        alpha = int(100 * (1 - dist / 5))
                        texture[y, x] = (*core_color, alpha)
        
        return texture
    
    def create_container_texture(self, container_type: str, content: str) -> np.ndarray:
        """Create container texture (tank, canister)"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        if container_type == "tank":
            # Cylindrical tank
            tank_color = (100, 100, 100)
            
            # Main body
            texture[2:14, 4:12] = (*tank_color, 255)
            
            # Top cap
            texture[1:3, 5:11] = (120, 120, 120, 255)
            texture[1, 6:10] = (140, 140, 140, 255)
            
            # Bottom cap
            texture[13:15, 5:11] = (80, 80, 80, 255)
            
            # Side highlights
            texture[2:14, 4] = (140, 140, 140, 255)
            texture[2:14, 11] = (60, 60, 60, 255)
            
            # Content window
            if content == "oxygen":
                window_color = (180, 220, 255)
            else:
                window_color = (100, 150, 200)
            
            texture[5:11, 6:10] = (*window_color, 128)
            
        else:  # canister
            # Smaller container
            can_color = (120, 120, 130)
            
            # Body
            texture[4:12, 5:11] = (*can_color, 255)
            
            # Top
            texture[3:5, 6:10] = (140, 140, 150, 255)
            texture[2, 7:9] = (160, 160, 170, 255)
            
            # Label area
            texture[6:10, 6:10] = (200, 200, 200, 255)
            
            # Content indicator
            if content:
                content_colors = {
                    "helium3": (180, 230, 255),
                    "oxygen": (100, 200, 255)
                }
                indicator_color = content_colors.get(content, (100, 100, 100))
                texture[7:9, 7:9] = (*indicator_color, 255)
        
        return texture
    
    def create_cell_texture(self, cell_type: str) -> np.ndarray:
        """Create fuel/energy cell texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Cell casing
        casing_color = (60, 60, 60)
        texture[3:13, 4:12] = (*casing_color, 255)
        
        # Top terminal
        texture[1:3, 7:9] = (180, 180, 180, 255)
        texture[0, 7:9] = (200, 200, 200, 255)
        
        # Bottom terminal
        texture[13:15, 7:9] = (180, 180, 180, 255)
        texture[15, 7:9] = (200, 200, 200, 255)
        
        # Content window
        window_x1, window_y1 = 5, 5
        window_x2, window_y2 = 11, 11
        
        if cell_type == "deuterium":
            content_color = (100, 150, 255)
        elif cell_type == "tritium":
            content_color = (255, 100, 150)
        elif cell_type == "filled":
            content_color = (255, 200, 0)
        elif cell_type == "empty":
            content_color = (50, 50, 50)
        else:
            content_color = (100, 100, 100)
        
        texture[window_y1:window_y2, window_x1:window_x2] = (*content_color, 200)
        
        # Glass effect on window
        texture[window_y1, window_x1:window_x2] = (255, 255, 255, 100)
        texture[window_y1:window_y2, window_x1] = (255, 255, 255, 100)
        
        return texture
    
    def create_fuel_texture(self, fuel_type: str) -> np.ndarray:
        """Create fuel item texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Fuel canister shape
        can_color = (40, 40, 40)
        
        # Main body
        texture[4:14, 5:11] = (*can_color, 255)
        
        # Nozzle
        texture[1:4, 7:9] = (80, 80, 80, 255)
        texture[0, 7:9] = (100, 100, 100, 255)
        
        # Fuel level indicator
        fuel_color = (255, 100, 0)  # Orange fuel
        texture[6:12, 6:10] = (*fuel_color, 255)
        
        # Warning stripes
        stripe_color = (255, 255, 0)
        for y in range(8, 12, 2):
            texture[y, 5:11] = (*stripe_color, 255)
        
        # Handle
        texture[4:6, 4:5] = (60, 60, 60, 255)
        texture[4:6, 11:12] = (60, 60, 60, 255)
        
        return texture
    
    def create_waste_texture(self, waste_type: str) -> np.ndarray:
        """Create waste/scrap texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Scrap pieces
        scrap_colors = [(80, 70, 60), (100, 90, 80), (70, 65, 55)]
        
        # Random scrap pieces
        scrap_positions = [
            (3, 11, 5, 3),   # x, y, w, h
            (7, 10, 4, 4),
            (10, 12, 3, 2),
            (4, 8, 6, 3),
            (8, 6, 4, 3)
        ]
        
        for i, (x, y, w, h) in enumerate(scrap_positions):
            color = scrap_colors[i % len(scrap_colors)]
            for py in range(h):
                for px in range(w):
                    if y + py < 16 and x + px < 16:
                        # Add some variation
                        var = random.randint(-10, 10)
                        final_color = tuple(min(255, max(0, c + var)) for c in color)
                        texture[y + py, x + px] = (*final_color, 255)
        
        # Add rust spots
        rust_color = (120, 60, 30)
        for _ in range(5):
            rx = random.randint(2, 13)
            ry = random.randint(2, 13)
            if texture[ry, rx, 3] > 0:
                texture[ry, rx] = (*rust_color, 200)
        
        return texture
    
    def create_data_texture(self, data_type: str) -> np.ndarray:
        """Create research data texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Data pad base
        pad_color = (40, 40, 50)
        texture[2:14, 3:13] = (*pad_color, 255)
        
        # Screen area
        screen_color = (20, 30, 40)
        texture[4:10, 5:11] = (*screen_color, 255)
        
        # Data visualization based on type
        if data_type and "fusion" in data_type:
            data_color = (255, 100, 200)  # Pink
        elif data_type and "quantum" in data_type:
            data_color = (200, 100, 255)  # Purple
        elif data_type and "space" in data_type:
            data_color = (100, 200, 255)  # Cyan
        elif data_type and "advanced" in data_type:
            data_color = (100, 255, 200)  # Green
        else:
            data_color = (100, 255, 100)  # Basic green
        
        # Data pattern on screen
        for y in range(5, 9):
            for x in range(6, 10):
                if (x + y) % 2 == 0:
                    texture[y, x] = (*data_color, 255)
        
        # Status LED
        texture[11:13, 10:12] = (*data_color, 255)
        
        # Edge highlight
        texture[2, 3:13] = (60, 60, 70, 255)
        texture[2:14, 3] = (60, 60, 70, 255)
        
        return texture
    
    def create_generic_item_texture(self, item_name: str) -> np.ndarray:
        """Create a generic item texture as fallback"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Generic item shape (rounded square)
        base_color = (100, 100, 120)
        
        # Main body
        texture[3:13, 3:13] = (*base_color, 255)
        
        # Rounded corners
        texture[2, 4:12] = (*base_color, 255)
        texture[13, 4:12] = (*base_color, 255)
        texture[4:12, 2] = (*base_color, 255)
        texture[4:12, 13] = (*base_color, 255)
        
        # Highlight
        highlight_color = tuple(int(c * 1.3) for c in base_color)
        texture[3, 4:12] = (*highlight_color, 255)
        texture[4:12, 3] = (*highlight_color, 255)
        
        # Shadow
        shadow_color = tuple(int(c * 0.7) for c in base_color)
        texture[12, 4:12] = (*shadow_color, 255)
        texture[4:12, 12] = (*shadow_color, 255)
        
        # Center detail
        texture[6:10, 6:10] = tuple(int(c * 0.8) for c in base_color) + (255,)
        texture[7:9, 7:9] = tuple(int(c * 1.1) for c in base_color) + (255,)
        
        return texture
    
    def create_book_texture(self, book_type: str) -> np.ndarray:
        """Create book texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Book cover
        cover_color = (139, 69, 19)  # Brown leather
        texture[1:15, 2:14] = (*cover_color, 255)
        
        # Spine
        spine_color = (100, 50, 10)
        texture[1:15, 1:3] = (*spine_color, 255)
        
        # Pages
        page_color = (240, 230, 200)
        texture[2:14, 3:13] = (*page_color, 255)
        
        # Title decoration
        texture[4:6, 5:11] = (200, 150, 0, 255)  # Gold text
        texture[8:10, 5:11] = (200, 150, 0, 255)
        
        return texture
    
    def create_bucket_texture(self, content: str) -> np.ndarray:
        """Create bucket texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Bucket body
        bucket_color = (140, 140, 140)
        # Side walls
        texture[4:14, 2:4] = (*bucket_color, 255)
        texture[4:14, 12:14] = (*bucket_color, 255)
        # Bottom
        texture[12:14, 2:14] = (*bucket_color, 255)
        
        # Handle
        handle_color = (100, 100, 100)
        texture[2:4, 6:10] = (*handle_color, 255)
        texture[1, 5:11] = (*handle_color, 255)
        texture[2, 5] = (*handle_color, 255)
        texture[2, 10] = (*handle_color, 255)
        
        # Content (UU matter - purple)
        if content:
            content_color = (150, 0, 255)
            texture[5:12, 4:12] = (*content_color, 200)
            # Shine
            texture[5, 5:11] = (200, 100, 255, 255)
        
        return texture
    
    def create_raw_ore_texture(self, ore_type: str) -> np.ndarray:
        """Create raw ore chunk texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Ore colors
        ore_colors = {
            "lithium": [(255, 192, 203), (240, 180, 190), (255, 210, 220)],
            "titanium": [(140, 140, 150), (160, 160, 170), (120, 120, 130)],
            "uranium": [(80, 200, 80), (100, 220, 100), (60, 180, 60)],
            "lunar_iron": [(180, 180, 190), (160, 160, 170), (200, 200, 210)]
        }
        
        colors = ore_colors.get(ore_type, [(128, 128, 128), (140, 140, 140), (116, 116, 116)])
        if len(colors) < 3:
            colors = colors + [(128, 128, 128)] * (3 - len(colors))
        base_color = colors[0]
        
        # Create chunky ore shape
        # Main chunk
        texture[4:13, 5:12] = (*base_color, 255)
        
        # Irregular edges
        texture[3:5, 6:10] = (*colors[1], 255)
        texture[12:14, 7:11] = (*colors[1], 255)
        texture[6:10, 3:5] = (*colors[2], 255)
        texture[7:11, 12:14] = (*colors[2], 255)
        
        # Rock matrix
        rock_color = (90, 90, 90)
        texture[5, 5] = (*rock_color, 255)
        texture[11, 10] = (*rock_color, 255)
        texture[8, 6] = (*rock_color, 255)
        texture[9, 11] = (*rock_color, 255)
        
        return texture
    
    def create_crystal_item_texture(self, crystal_type: str) -> np.ndarray:
        """Create crystal item texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Crystal colors
        crystal_color = (180, 230, 255)  # Light blue for helium3
        darker = tuple(int(c * 0.7) for c in crystal_color)
        
        # Crystal shape - hexagonal
        # Top point
        texture[2, 7:9] = (*crystal_color, 255)
        texture[3, 6:10] = (*crystal_color, 255)
        texture[4, 5:11] = (*crystal_color, 255)
        
        # Main body
        for y in range(5, 11):
            texture[y, 4:12] = (*crystal_color, 255)
            # Faceting
            texture[y, 4] = (*darker, 255)
            texture[y, 11] = (*darker, 255)
        
        # Bottom point
        texture[11, 5:11] = (*crystal_color, 255)
        texture[12, 6:10] = (*crystal_color, 255)
        texture[13, 7:9] = (*crystal_color, 255)
        
        # Inner glow
        texture[7:9, 7:9] = (255, 255, 255, 255)
        
        return texture
    
    def create_mask_texture(self, mask_type: str) -> np.ndarray:
        """Create mask/helmet texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Mask frame
        frame_color = (80, 80, 90)
        # Top
        texture[2:4, 4:12] = (*frame_color, 255)
        # Sides
        texture[4:12, 3:5] = (*frame_color, 255)
        texture[4:12, 11:13] = (*frame_color, 255)
        # Bottom
        texture[11:13, 5:11] = (*frame_color, 255)
        
        # Visor
        visor_color = (150, 200, 255, 180)  # Transparent blue
        texture[4:11, 5:11] = visor_color
        
        # Breathing apparatus
        texture[10:12, 7:9] = (60, 60, 60, 255)
        texture[12:14, 7:9] = (100, 100, 100, 255)
        
        # Straps
        strap_color = (40, 40, 40)
        texture[3, 1:3] = (*strap_color, 255)
        texture[3, 13:15] = (*strap_color, 255)
        
        return texture
    
    def create_plate_texture(self, material: str) -> np.ndarray:
        """Create metal plate texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Material colors
        material_colors = {
            "titanium": (160, 160, 170),
            "iron": (180, 180, 180),
            "steel": (140, 140, 150)
        }
        
        base_color = material_colors.get(material, (150, 150, 150))
        
        # Plate shape - rectangular with rivets
        texture[3:13, 2:14] = (*base_color, 255)
        
        # Edge bevel
        lighter = tuple(int(min(255, c * 1.1)) for c in base_color)
        darker = tuple(int(c * 0.8) for c in base_color)
        
        texture[3, 2:14] = (*lighter, 255)
        texture[3:13, 2] = (*lighter, 255)
        texture[12, 2:14] = (*darker, 255)
        texture[3:13, 13] = (*darker, 255)
        
        # Rivets in corners
        rivet_color = (100, 100, 100)
        for x, y in [(3, 4), (12, 4), (3, 11), (12, 11)]:
            texture[y, x] = (*rivet_color, 255)
            texture[y-1, x] = (120, 120, 120, 255)  # Highlight
        
        return texture
    
    def create_artifact_texture(self, artifact_type: str) -> np.ndarray:
        """Create alien artifact texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Alien artifact - mysterious object
        base_color = (100, 50, 150)  # Purple alien tech
        glow_color = (200, 150, 255)
        
        # Main shape - angular/crystalline
        texture[3:13, 4:12] = (*base_color, 255)
        
        # Angular cuts
        texture[3:5, 4:6] = (0, 0, 0, 0)
        texture[11:13, 10:12] = (0, 0, 0, 0)
        texture[3:5, 10:12] = (0, 0, 0, 0)
        texture[11:13, 4:6] = (0, 0, 0, 0)
        
        # Glowing patterns
        texture[6:10, 7:9] = (*glow_color, 255)
        texture[7:9, 6:10] = (*glow_color, 255)
        
        # Energy veins
        texture[5, 7:9] = tuple(int(c * 0.7) for c in glow_color) + (255,)
        texture[10, 7:9] = tuple(int(c * 0.7) for c in glow_color) + (255,)
        texture[7:9, 5] = tuple(int(c * 0.7) for c in glow_color) + (255,)
        texture[7:9, 10] = tuple(int(c * 0.7) for c in glow_color) + (255,)
        
        return texture
    
    def create_fragment_texture(self, fragment_type: str) -> np.ndarray:
        """Create meteor fragment texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Meteor rock colors
        rock_color = (60, 50, 40)
        hot_color = (200, 100, 50)
        metal_color = (140, 140, 150)
        
        # Irregular rock shape
        # Main body
        texture[4:12, 5:11] = (*rock_color, 255)
        texture[5:11, 4:12] = (*rock_color, 255)
        
        # Rough edges
        texture[3, 6:9] = (*rock_color, 255)
        texture[12, 7:10] = (*rock_color, 255)
        texture[6:9, 3] = (*rock_color, 255)
        texture[7:10, 12] = (*rock_color, 255)
        
        # Hot/melted areas
        texture[6:8, 6:8] = (*hot_color, 255)
        texture[9:11, 8:10] = (*hot_color, 255)
        
        # Metallic inclusions
        texture[5, 9] = (*metal_color, 255)
        texture[10, 5] = (*metal_color, 255)
        texture[8, 7] = (*metal_color, 255)
        
        return texture
    
    def create_key_texture(self, key_type: str) -> np.ndarray:
        """Create key texture"""
        texture = np.zeros((16, 16, 4), dtype=np.uint8)
        
        # Key colors
        key_color = (180, 180, 190)  # Silver/metal
        accent_color = (100, 150, 255)  # Blue tech accent
        
        # Key handle (circular)
        # Ring
        for angle in range(0, 360, 30):
            x = int(4 + 2.5 * np.cos(np.radians(angle)))
            y = int(4 + 2.5 * np.sin(np.radians(angle)))
            if 0 <= x < 16 and 0 <= y < 16:
                texture[y, x] = (*key_color, 255)
        
        # Fill handle
        texture[2:7, 2:7] = (*key_color, 255)
        texture[3:6, 3:6] = (0, 0, 0, 0)  # Hole in handle
        
        # Key shaft
        texture[5:7, 6:14] = (*key_color, 255)
        
        # Key teeth (high-tech pattern)
        texture[7, 10:14] = (*key_color, 255)
        texture[6, 12:14] = (*key_color, 255)
        texture[5, 13:14] = (*key_color, 255)
        
        # Tech accent on handle
        texture[4, 4] = (*accent_color, 255)
        
        return texture
    
    def generate_gui_texture(self, gui_type: str, width: int = 176, height: int = 166) -> Image.Image:
        """Generate GUI texture with proper Minecraft standards"""
        texture = np.zeros((height, width, 4), dtype=np.uint8)
        
        # Background color
        bg_color = (198, 198, 198)
        texture[:, :] = (*bg_color, 255)
        
        # Main window border
        self.draw_gui_border(texture, 0, 0, width, height)
        
        # Title area
        title_height = 20
        texture[4:title_height-4, 4:width-4] = (139, 139, 139, 255)
        
        # Inner content area
        content_y = title_height
        content_height = height - title_height - 4
        self.draw_inset_panel(texture, 4, content_y, width-8, content_height)
        
        # Machine-specific elements
        if gui_type == "generator":
            self.add_generator_gui_elements(texture)
        elif gui_type == "processor":
            self.add_processor_gui_elements(texture)
        elif gui_type == "storage":
            self.add_storage_gui_elements(texture)
        elif gui_type == "furnace":
            self.add_furnace_gui_elements(texture)
        
        # Always add player inventory slots
        self.add_player_inventory_slots(texture)
        
        return Image.fromarray(texture, mode='RGBA')
    
    def draw_gui_border(self, texture: np.ndarray, x: int, y: int, w: int, h: int):
        """Draw standard Minecraft GUI border"""
        # Colors
        dark = (85, 85, 85, 255)
        light = (255, 255, 255, 255)
        mid = (139, 139, 139, 255)
        
        # Top-left corner
        texture[y:y+2, x:x+w-2] = light  # Top edge
        texture[y:y+h-2, x:x+2] = light  # Left edge
        
        # Bottom-right corner
        texture[y+h-2:y+h, x+2:x+w] = dark  # Bottom edge
        texture[y+2:y+h, x+w-2:x+w] = dark  # Right edge
        
        # Middle borders
        texture[y+2:y+h-2, x+2:x+3] = mid  # Left inner
        texture[y+2:y+3, x+2:x+w-2] = mid  # Top inner
        texture[y+h-3:y+h-2, x+2:x+w-2] = mid  # Bottom inner
        texture[y+2:y+h-2, x+w-3:x+w-2] = mid  # Right inner
    
    def draw_inset_panel(self, texture: np.ndarray, x: int, y: int, w: int, h: int):
        """Draw inset panel (for slots, displays, etc.)"""
        # Colors reversed for inset effect
        dark = (85, 85, 85, 255)
        light = (255, 255, 255, 255)
        
        # Dark edges (top-left)
        texture[y:y+1, x:x+w] = dark
        texture[y:y+h, x:x+1] = dark
        
        # Light edges (bottom-right)
        texture[y+h-1:y+h, x:x+w] = light
        texture[y:y+h, x+w-1:x+w] = light
        
        # Fill
        texture[y+1:y+h-1, x+1:x+w-1] = (139, 139, 139, 255)
    
    def add_slot(self, texture: np.ndarray, x: int, y: int):
        """Add standard 18x18 slot"""
        self.draw_inset_panel(texture, x, y, 18, 18)
        # Darker center for slot
        texture[y+1:y+17, x+1:x+17] = (111, 111, 111, 255)
    
    def add_player_inventory_slots(self, texture: np.ndarray):
        """Add standard player inventory slots at correct position"""
        # Inventory starts at x:8, y:84
        inv_x, inv_y = 8, 84
        
        # Main inventory (3 rows of 9 slots)
        for row in range(3):
            for col in range(9):
                slot_x = inv_x + col * 18
                slot_y = inv_y + row * 18
                self.add_slot(texture, slot_x, slot_y)
        
        # Hotbar (1 row of 9 slots) - 4 pixels below main inventory
        hotbar_y = inv_y + 58
        for col in range(9):
            slot_x = inv_x + col * 18
            self.add_slot(texture, slot_x, hotbar_y)
    
    def add_generator_gui_elements(self, texture: np.ndarray):
        """Add generator-specific GUI elements"""
        # Fuel slot
        self.add_slot(texture, 80, 53)
        
        # Energy display area
        energy_x, energy_y = 8, 20
        energy_w, energy_h = 16, 54
        self.draw_inset_panel(texture, energy_x, energy_y, energy_w, energy_h)
        
        # Add energy bar background
        texture[energy_y+2:energy_y+energy_h-2, energy_x+2:energy_x+energy_w-2] = (64, 64, 64, 255)
        
        # Burn time indicator area
        self.draw_inset_panel(texture, 78, 36, 20, 14)
    
    def add_processor_gui_elements(self, texture: np.ndarray):
        """Add processor-specific GUI elements"""
        # Input slot
        self.add_slot(texture, 56, 35)
        
        # Output slot
        self.add_slot(texture, 116, 35)
        
        # Progress arrow area
        arrow_x, arrow_y = 79, 35
        arrow_w, arrow_h = 24, 16
        self.draw_inset_panel(texture, arrow_x, arrow_y, arrow_w, arrow_h)
        
        # Energy display
        energy_x, energy_y = 8, 20
        energy_w, energy_h = 16, 54
        self.draw_inset_panel(texture, energy_x, energy_y, energy_w, energy_h)
        texture[energy_y+2:energy_y+energy_h-2, energy_x+2:energy_x+energy_w-2] = (64, 64, 64, 255)
    
    def add_storage_gui_elements(self, texture: np.ndarray):
        """Add storage terminal GUI elements"""
        # Storage grid (9x4)
        storage_x, storage_y = 8, 18
        for row in range(4):
            for col in range(9):
                slot_x = storage_x + col * 18
                slot_y = storage_y + row * 18
                self.add_slot(texture, slot_x, slot_y)
        
        # Crafting grid (3x3)
        craft_x, craft_y = 107, 18
        for row in range(3):
            for col in range(3):
                slot_x = craft_x + col * 18
                slot_y = craft_y + row * 18
                self.add_slot(texture, slot_x, slot_y)
        
        # Crafting result
        self.add_slot(texture, 151, 36)
    
    def add_furnace_gui_elements(self, texture: np.ndarray):
        """Add furnace-style GUI elements"""
        # Input slot
        self.add_slot(texture, 56, 17)
        
        # Fuel slot
        self.add_slot(texture, 56, 53)
        
        # Output slot
        self.add_slot(texture, 116, 35)
        
        # Progress arrow
        arrow_x, arrow_y = 79, 34
        self.draw_inset_panel(texture, arrow_x, arrow_y, 24, 17)
        
        # Burn indicator
        burn_x, burn_y = 57, 37
        self.draw_inset_panel(texture, burn_x, burn_y, 14, 14)


def generate_batch(generator: TextureGenerator, output_dir: str, config_file: str):
    """Batch generate textures from configuration"""
    with open(config_file, 'r') as f:
        config = json.load(f)
    
    # Create output directories
    os.makedirs(os.path.join(output_dir, 'blocks'), exist_ok=True)
    os.makedirs(os.path.join(output_dir, 'items'), exist_ok=True)
    os.makedirs(os.path.join(output_dir, 'gui'), exist_ok=True)
    
    total_generated = 0
    
    # Generate machine blocks
    print("Generating machine block textures...")
    for tier in config.get('tiers', ['basic', 'advanced', 'elite']):
        tier_enum = TierType[tier.upper()]
        for machine in config.get('machines', []):
            texture = generator.generate_machine_block(machine, tier_enum)
            filename = f"{machine}_{tier}.png"
            texture.save(os.path.join(output_dir, 'blocks', filename))
            print(f"  Generated: {filename}")
            total_generated += 1
    
    # Generate ores
    print("\nGenerating ore textures...")
    for ore in config.get('ores', []):
        for stone_type in ['stone', 'deepslate']:
            texture = generator.generate_ore_texture(ore, stone_type)
            filename = f"{stone_type}_{ore}_ore.png" if stone_type == 'deepslate' else f"{ore}_ore.png"
            texture.save(os.path.join(output_dir, 'blocks', filename))
            print(f"  Generated: {filename}")
            total_generated += 1
    
    # Generate items
    print("\nGenerating item textures...")
    for item_type, materials in config.get('items', {}).items():
        for material in materials:
            texture = generator.generate_item_texture(item_type, material)
            filename = f"{material}_{item_type}.png"
            texture.save(os.path.join(output_dir, 'items', filename))
            print(f"  Generated: {filename}")
            total_generated += 1
    
    # Generate specific items
    print("\nGenerating specific item textures...")
    for item_name, item_config in config.get('specific_items', {}).items():
        item_type = item_config.get('type', 'generic')
        material = item_config.get('material', None)
        texture = generator.generate_item_texture(item_type, material)
        filename = f"{item_name}.png"
        texture.save(os.path.join(output_dir, 'items', filename))
        print(f"  Generated: {filename}")
        total_generated += 1
    
    # Generate GUI textures
    print("\nGenerating GUI textures...")
    for gui_name, gui_config in config.get('guis', {}).items():
        gui_type = gui_config.get('type', 'generic')
        width = gui_config.get('width', 176)
        height = gui_config.get('height', 166)
        texture = generator.generate_gui_texture(gui_type, width, height)
        filename = f"{gui_name}.png"
        texture.save(os.path.join(output_dir, 'gui', filename))
        print(f"  Generated: {filename}")
        total_generated += 1
    
    print(f"\nTexture generation complete! Generated {total_generated} textures.")


def main():
    """Main execution"""
    import argparse
    
    parser = argparse.ArgumentParser(description='Professional Texture Generator')
    parser.add_argument('--output', '-o', default='output/textures', help='Output directory')
    parser.add_argument('--config', '-c', default='texture_config.json', help='Configuration file')
    parser.add_argument('--assets', '-a', default='assets', help='Assets directory')
    
    args = parser.parse_args()
    
    # Create generator
    generator = TextureGenerator(args.assets)
    
    # Run batch generation
    generate_batch(generator, args.output, args.config)


if __name__ == "__main__":
    main()