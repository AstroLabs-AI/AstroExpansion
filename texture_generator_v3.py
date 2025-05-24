#!/usr/bin/env python3
"""
Advanced Texture Generator v3.0 for AstroExpansion
Creates highly detailed, unique 16x16 textures with modern Minecraft aesthetics
"""

from PIL import Image, ImageDraw, ImageFilter, ImageEnhance
import numpy as np
import random
import math
from typing import Tuple, List, Dict, Optional

class AdvancedTextureGenerator:
    def __init__(self):
        self.size = 16
        
        # Enhanced color palettes with more variations
        self.materials = {
            'titanium': {
                'base': (140, 145, 160),
                'light': (180, 185, 200),
                'dark': (90, 95, 110),
                'accent': (160, 170, 220),
                'shine': (220, 225, 240),
                'shadow': (60, 65, 80)
            },
            'lithium': {
                'base': (200, 210, 240),
                'light': (230, 235, 255),
                'dark': (150, 160, 200),
                'accent': (180, 200, 255),
                'shine': (245, 250, 255),
                'shadow': (100, 110, 150)
            },
            'uranium': {
                'base': (80, 160, 80),
                'light': (120, 200, 120),
                'dark': (40, 120, 40),
                'accent': (150, 255, 150),
                'shine': (200, 255, 200),
                'glow': (100, 255, 100),
                'shadow': (20, 80, 20)
            },
            'steel': {
                'base': (120, 125, 130),
                'light': (160, 165, 170),
                'dark': (80, 85, 90),
                'accent': (140, 145, 150),
                'shine': (200, 205, 210),
                'shadow': (40, 45, 50)
            },
            'energy': {
                'base': (255, 100, 100),
                'light': (255, 150, 150),
                'dark': (200, 50, 50),
                'glow': (255, 200, 200),
                'pulse': (255, 255, 200)
            },
            'quantum': {
                'base': (120, 80, 200),
                'light': (160, 120, 240),
                'dark': (80, 40, 160),
                'glow': (200, 160, 255),
                'particle': (255, 200, 255)
            },
            'fusion': {
                'base': (255, 100, 200),
                'light': (255, 150, 230),
                'dark': (200, 50, 150),
                'core': (255, 255, 255),
                'plasma': (255, 200, 255)
            }
        }
    
    def add_noise(self, img: Image.Image, intensity: float = 0.05) -> Image.Image:
        """Add subtle noise for texture"""
        pixels = img.load()
        width, height = img.size
        
        for x in range(width):
            for y in range(height):
                if pixels[x, y][3] > 0:  # Only modify non-transparent pixels
                    r, g, b, a = pixels[x, y]
                    noise = random.randint(-int(255 * intensity), int(255 * intensity))
                    r = max(0, min(255, r + noise))
                    g = max(0, min(255, g + noise))
                    b = max(0, min(255, b + noise))
                    pixels[x, y] = (r, g, b, a)
        
        return img
    
    def add_scratches(self, img: Image.Image, color: Tuple[int, int, int], count: int = 3) -> Image.Image:
        """Add scratch details to metal surfaces"""
        draw = ImageDraw.Draw(img)
        width, height = img.size
        
        for _ in range(count):
            if random.random() > 0.5:
                # Horizontal scratch
                y = random.randint(2, height - 3)
                x1 = random.randint(0, width // 3)
                x2 = random.randint(width * 2 // 3, width)
                draw.line([(x1, y), (x2, y)], fill=(*color, 80), width=1)
            else:
                # Vertical scratch
                x = random.randint(2, width - 3)
                y1 = random.randint(0, height // 3)
                y2 = random.randint(height * 2 // 3, height)
                draw.line([(x, y1), (x, y2)], fill=(*color, 80), width=1)
        
        return img
    
    def add_rivets(self, img: Image.Image, color: Tuple[int, int, int]) -> Image.Image:
        """Add rivet details to mechanical items"""
        draw = ImageDraw.Draw(img)
        positions = [(2, 2), (13, 2), (2, 13), (13, 13)]
        
        for x, y in positions:
            # Dark center
            draw.point((x, y), fill=(*[int(c * 0.6) for c in color], 255))
            # Light edge
            draw.point((x + 1, y), fill=(*[int(c * 1.3) for c in color], 180))
            draw.point((x, y + 1), fill=(*[int(c * 1.3) for c in color], 180))
        
        return img
    
    def create_circuit_pattern(self, img: Image.Image, color: Tuple[int, int, int]) -> Image.Image:
        """Create circuit board patterns"""
        draw = ImageDraw.Draw(img)
        
        # Main traces
        trace_color = (*color, 200)
        # Horizontal traces
        for y in [4, 8, 12]:
            draw.line([(1, y), (14, y)], fill=trace_color, width=1)
        
        # Vertical traces  
        for x in [4, 8, 12]:
            draw.line([(x, 1), (x, 14)], fill=trace_color, width=1)
        
        # Connection points
        for x in [4, 8, 12]:
            for y in [4, 8, 12]:
                draw.rectangle([x-1, y-1, x+1, y+1], fill=(*color, 255))
                draw.point((x, y), fill=(255, 215, 0, 255))  # Gold center
        
        return img
    
    def create_energy_core_pattern(self, img: Image.Image, base_color: Tuple[int, int, int]) -> Image.Image:
        """Create glowing energy core pattern"""
        draw = ImageDraw.Draw(img)
        center_x, center_y = 8, 8
        
        # Outer rings
        for radius in range(7, 2, -1):
            alpha = int(255 * (8 - radius) / 6)
            color = (*[min(255, c + (8 - radius) * 20) for c in base_color], alpha)
            draw.ellipse([center_x - radius, center_y - radius, 
                         center_x + radius, center_y + radius], 
                        outline=color, width=1)
        
        # Inner core
        draw.ellipse([center_x - 2, center_y - 2, center_x + 2, center_y + 2], 
                    fill=(255, 255, 255, 255))
        
        # Energy particles
        for _ in range(8):
            x = random.randint(2, 13)
            y = random.randint(2, 13)
            draw.point((x, y), fill=(*base_color, 180))
        
        return img
    
    def create_crystal_pattern(self, img: Image.Image, base_color: Tuple[int, int, int]) -> Image.Image:
        """Create crystalline structure pattern"""
        draw = ImageDraw.Draw(img)
        
        # Main crystal shape
        points = [
            (8, 1),    # Top
            (12, 5),   # Top right
            (11, 11),  # Bottom right
            (8, 14),   # Bottom
            (5, 11),   # Bottom left
            (4, 5)     # Top left
        ]
        
        # Fill with gradient effect
        for i in range(len(points)):
            p1 = points[i]
            p2 = points[(i + 1) % len(points)]
            p3 = (8, 8)  # Center
            
            # Create triangular face
            face_color = [int(c * (0.7 + 0.3 * (i / len(points)))) for c in base_color]
            draw.polygon([p1, p2, p3], fill=(*face_color, 200))
            
            # Add edge highlight
            edge_color = [min(255, int(c * 1.5)) for c in base_color]
            draw.line([p1, p2], fill=(*edge_color, 255), width=1)
        
        # Inner glow
        draw.ellipse([6, 6, 10, 10], fill=(*[min(255, c + 50) for c in base_color], 180))
        
        return img
    
    def create_gear_pattern(self, img: Image.Image, base_color: Tuple[int, int, int]) -> Image.Image:
        """Create mechanical gear pattern"""
        draw = ImageDraw.Draw(img)
        center = 8
        
        # Outer gear teeth
        for angle in range(0, 360, 45):
            rad = math.radians(angle)
            x1 = int(center + 6 * math.cos(rad))
            y1 = int(center + 6 * math.sin(rad))
            x2 = int(center + 7 * math.cos(rad))
            y2 = int(center + 7 * math.sin(rad))
            
            # Tooth
            draw.rectangle([x1-1, y1-1, x2+1, y2+1], fill=(*base_color, 255))
        
        # Main gear body
        draw.ellipse([3, 3, 12, 12], fill=(*base_color, 255))
        
        # Inner details
        draw.ellipse([5, 5, 10, 10], fill=(*[int(c * 0.8) for c in base_color], 255))
        draw.ellipse([6, 6, 9, 9], fill=(*[int(c * 0.6) for c in base_color], 255))
        
        # Center hole
        draw.ellipse([7, 7, 8, 8], fill=(40, 40, 40, 255))
        
        return img
    
    def create_fluid_container(self, img: Image.Image, fluid_color: Tuple[int, int, int]) -> Image.Image:
        """Create fluid container with liquid inside"""
        draw = ImageDraw.Draw(img)
        
        # Glass container outline
        draw.rectangle([2, 1, 13, 14], fill=(200, 200, 200, 100))
        draw.rectangle([3, 2, 12, 13], fill=(180, 180, 180, 80))
        
        # Fluid inside
        fluid_level = 10
        draw.rectangle([4, 14 - fluid_level, 11, 13], fill=(*fluid_color, 200))
        
        # Glass reflections
        draw.line([(3, 3), (3, 12)], fill=(255, 255, 255, 120), width=1)
        draw.line([(12, 3), (12, 12)], fill=(150, 150, 150, 120), width=1)
        
        # Cap
        draw.rectangle([1, 0, 14, 2], fill=(100, 100, 100, 255))
        draw.rectangle([2, 1, 13, 2], fill=(140, 140, 140, 255))
        
        return img
    
    # Specific item generators
    def generate_ingot(self, material: str) -> Image.Image:
        """Generate detailed ingot texture"""
        img = Image.new('RGBA', (self.size, self.size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        colors = self.materials.get(material, self.materials['steel'])
        
        # Main ingot shape with perspective
        # Top face
        draw.polygon([(3, 4), (12, 4), (13, 5), (4, 5)], 
                    fill=(*colors['light'], 255))
        
        # Front face
        draw.polygon([(4, 5), (13, 5), (13, 11), (4, 11)], 
                    fill=(*colors['base'], 255))
        
        # Side face
        draw.polygon([(13, 5), (12, 4), (12, 10), (13, 11)], 
                    fill=(*colors['dark'], 255))
        
        # Bottom edge
        draw.line([(4, 11), (13, 11)], fill=(*colors['shadow'], 255), width=1)
        
        # Metallic shine
        draw.line([(5, 6), (11, 6)], fill=(*colors['shine'], 180), width=1)
        draw.line([(5, 7), (9, 7)], fill=(*colors['shine'], 120), width=1)
        
        # Material-specific details
        if material == 'uranium':
            # Radioactive glow
            for y in range(5, 11):
                for x in range(4, 13):
                    if random.random() > 0.8:
                        draw.point((x, y), fill=(*colors['glow'], 100))
        
        img = self.add_noise(img, 0.02)
        return img
    
    def generate_dust(self, material: str) -> Image.Image:
        """Generate detailed dust/powder texture"""
        img = Image.new('RGBA', (self.size, self.size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        colors = self.materials.get(material, self.materials['steel'])
        
        # Create pile shape
        pile_points = []
        for x in range(16):
            if 2 <= x <= 13:
                height = int(4 + 6 * math.sin((x - 2) * math.pi / 11))
                pile_points.append((x, 15 - height))
        
        pile_points.extend([(13, 15), (2, 15)])
        draw.polygon(pile_points, fill=(*colors['base'], 255))
        
        # Add particle details
        for _ in range(50):
            x = random.randint(2, 13)
            y = random.randint(5, 14)
            if y >= 15 - int(4 + 6 * math.sin((x - 2) * math.pi / 11)):
                continue
            
            particle_color = random.choice([colors['light'], colors['dark'], colors['base']])
            draw.point((x, y), fill=(*particle_color, random.randint(180, 255)))
        
        # Highlight on top
        for x in range(3, 12):
            y = 15 - int(4 + 6 * math.sin((x - 2) * math.pi / 11))
            if random.random() > 0.3:
                draw.point((x, y), fill=(*colors['light'], 200))
        
        return img
    
    def generate_plate(self, material: str) -> Image.Image:
        """Generate detailed metal plate texture"""
        img = Image.new('RGBA', (self.size, self.size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        colors = self.materials.get(material, self.materials['steel'])
        
        # Main plate
        draw.rectangle([1, 2, 14, 13], fill=(*colors['base'], 255))
        
        # Beveled edges
        draw.line([(1, 2), (14, 2)], fill=(*colors['light'], 255), width=1)
        draw.line([(1, 2), (1, 13)], fill=(*colors['light'], 255), width=1)
        draw.line([(14, 3), (14, 13)], fill=(*colors['dark'], 255), width=1)
        draw.line([(2, 13), (14, 13)], fill=(*colors['dark'], 255), width=1)
        
        # Surface details
        img = self.add_scratches(img, colors['shadow'], 4)
        
        # Bolt holes in corners
        for x, y in [(3, 4), (12, 4), (3, 11), (12, 11)]:
            draw.ellipse([x-1, y-1, x+1, y+1], fill=(*colors['shadow'], 255))
            draw.point((x, y), fill=(20, 20, 20, 255))
        
        # Reinforcement pattern
        if material == 'reinforced':
            # Diamond pattern
            for i in range(2, 14, 3):
                draw.line([(i, 2), (i-2, 13)], fill=(*colors['dark'], 100), width=1)
                draw.line([(i, 2), (i+2, 13)], fill=(*colors['dark'], 100), width=1)
        
        img = self.add_noise(img, 0.03)
        return img
    
    def generate_processor(self, tier: str) -> Image.Image:
        """Generate detailed processor chip texture"""
        img = Image.new('RGBA', (self.size, self.size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # Tier-based colors
        tier_colors = {
            'basic': {'board': (50, 100, 50), 'traces': (180, 140, 0)},
            'advanced': {'board': (50, 50, 150), 'traces': (220, 220, 220)},
            'quantum': {'board': (80, 0, 120), 'traces': (255, 100, 255)},
            'storage': {'board': (100, 50, 0), 'traces': (255, 200, 0)}
        }
        
        colors = tier_colors.get(tier, tier_colors['basic'])
        
        # PCB base
        draw.rectangle([1, 1, 14, 14], fill=(*colors['board'], 255))
        
        # Circuit traces
        img = self.create_circuit_pattern(img, colors['traces'])
        
        # Central processor
        if tier == 'quantum':
            # Quantum core
            draw.rectangle([5, 5, 10, 10], fill=(20, 0, 40, 255))
            draw.rectangle([6, 6, 9, 9], fill=(150, 50, 200, 255))
            draw.point((7, 7), fill=(255, 200, 255, 255))
        else:
            # Standard processor
            draw.rectangle([5, 5, 10, 10], fill=(40, 40, 40, 255))
            draw.rectangle([6, 6, 9, 9], fill=(80, 80, 80, 255))
            
            # Tier marking
            if tier == 'advanced':
                draw.line([(6, 7), (9, 7)], fill=(0, 255, 0, 255), width=1)
            elif tier == 'storage':
                draw.rectangle([7, 7, 8, 8], fill=(255, 200, 0, 255))
        
        # Edge connectors
        for i in range(2, 14, 2):
            draw.line([(0, i), (1, i)], fill=(200, 170, 0, 255), width=1)
            draw.line([(14, i), (15, i)], fill=(200, 170, 0, 255), width=1)
            draw.line([(i, 0), (i, 1)], fill=(200, 170, 0, 255), width=1)
            draw.line([(i, 14), (i, 15)], fill=(200, 170, 0, 255), width=1)
        
        return img
    
    def generate_drone(self, drone_type: str) -> Image.Image:
        """Generate detailed drone item texture"""
        img = Image.new('RGBA', (self.size, self.size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # Drone type colors
        drone_colors = {
            'mining': {'body': (255, 200, 0), 'accent': (180, 140, 0)},
            'combat': {'body': (200, 0, 0), 'accent': (100, 0, 0)},
            'farming': {'body': (0, 200, 0), 'accent': (0, 100, 0)},
            'construction': {'body': (255, 140, 0), 'accent': (180, 80, 0)},
            'logistics': {'body': (0, 100, 200), 'accent': (0, 50, 150)}
        }
        
        colors = drone_colors.get(drone_type, drone_colors['mining'])
        
        # Main body
        draw.ellipse([4, 5, 11, 12], fill=(*colors['body'], 255))
        
        # Propellers
        prop_positions = [(2, 3), (13, 3), (2, 14), (13, 14)]
        for x, y in prop_positions:
            # Propeller arms
            draw.line([(7, 8), (x, y)], fill=(80, 80, 80, 255), width=1)
            # Propeller blades
            draw.line([(x-1, y), (x+1, y)], fill=(150, 150, 150, 200), width=1)
            draw.line([(x, y-1), (x, y+1)], fill=(150, 150, 150, 200), width=1)
        
        # Core details
        draw.ellipse([6, 7, 9, 10], fill=(*colors['accent'], 255))
        
        # Type-specific features
        if drone_type == 'mining':
            # Drill
            draw.polygon([(7, 11), (8, 11), (7, 13)], fill=(100, 100, 100, 255))
        elif drone_type == 'combat':
            # Weapon
            draw.rectangle([7, 11, 8, 13], fill=(150, 150, 150, 255))
        elif drone_type == 'farming':
            # Sensor
            draw.ellipse([7, 11, 8, 12], fill=(255, 255, 0, 255))
        
        # LED indicator
        draw.point((7, 6), fill=(0, 255, 0, 255))
        
        return img
    
    def generate_storage_drive(self, capacity: str) -> Image.Image:
        """Generate detailed storage drive texture"""
        img = Image.new('RGBA', (self.size, self.size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # Capacity colors
        capacity_colors = {
            '1k': {'main': (100, 100, 100), 'led': (0, 255, 0)},
            '4k': {'main': (0, 100, 200), 'led': (0, 200, 255)},
            '16k': {'main': (200, 0, 200), 'led': (255, 0, 255)},
            '64k': {'main': (200, 100, 0), 'led': (255, 200, 0)}
        }
        
        colors = capacity_colors.get(capacity, capacity_colors['1k'])
        
        # Main housing
        draw.rectangle([2, 1, 13, 14], fill=(*colors['main'], 255))
        
        # Front panel
        draw.rectangle([3, 2, 12, 10], fill=(*[int(c * 0.8) for c in colors['main']], 255))
        
        # Data window
        draw.rectangle([4, 3, 11, 7], fill=(20, 20, 30, 255))
        
        # LED strips
        for y in range(4, 7):
            for x in range(5, 11, 2):
                if random.random() > 0.3:
                    draw.point((x, y), fill=(*colors['led'], 200))
        
        # Connector
        draw.rectangle([5, 11, 10, 14], fill=(150, 150, 150, 255))
        for x in range(6, 10):
            draw.line([(x, 12), (x, 13)], fill=(200, 170, 0, 255), width=1)
        
        # Capacity indicator
        capacity_markers = {'1k': 1, '4k': 2, '16k': 3, '64k': 4}
        markers = capacity_markers.get(capacity, 1)
        for i in range(markers):
            draw.rectangle([3 + i * 3, 8, 4 + i * 3, 9], fill=(*colors['led'], 255))
        
        return img
    
    def generate_ore_block(self, ore_type: str, stone_type: str = 'normal') -> Image.Image:
        """Generate detailed ore block texture"""
        img = Image.new('RGBA', (self.size, self.size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # Stone base colors
        if stone_type == 'deepslate':
            stone_colors = [(70, 70, 80), (60, 60, 70), (80, 80, 90)]
        else:
            stone_colors = [(128, 128, 128), (110, 110, 110), (140, 140, 140)]
        
        # Fill with stone texture
        for x in range(16):
            for y in range(16):
                color = random.choice(stone_colors)
                draw.point((x, y), fill=(*color, 255))
        
        # Add stone pattern
        for _ in range(8):
            x = random.randint(0, 15)
            y = random.randint(0, 15)
            size = random.randint(2, 4)
            color = random.choice(stone_colors)
            draw.ellipse([x, y, x + size, y + size], fill=(*color, 255))
        
        # Ore veins
        ore_colors = self.materials.get(ore_type, self.materials['titanium'])
        ore_positions = []
        
        # Create connected ore clusters
        start_x = random.randint(3, 12)
        start_y = random.randint(3, 12)
        ore_positions.append((start_x, start_y))
        
        # Grow ore cluster
        for _ in range(random.randint(8, 15)):
            if ore_positions:
                base = random.choice(ore_positions)
                new_x = base[0] + random.randint(-2, 2)
                new_y = base[1] + random.randint(-2, 2)
                if 0 <= new_x < 16 and 0 <= new_y < 16:
                    ore_positions.append((new_x, new_y))
        
        # Draw ore
        for x, y in ore_positions:
            # Main ore pixel
            draw.point((x, y), fill=(*ore_colors['base'], 255))
            
            # Shading
            if x > 0 and y > 0:
                draw.point((x-1, y-1), fill=(*ore_colors['light'], 200))
            if x < 15 and y < 15:
                draw.point((x+1, y+1), fill=(*ore_colors['dark'], 200))
            
            # Sparkle effect
            if random.random() > 0.7:
                draw.point((x, y), fill=(*ore_colors['shine'], 255))
        
        # Special effects for uranium
        if ore_type == 'uranium':
            for x, y in ore_positions:
                if random.random() > 0.8:
                    for dx in [-1, 0, 1]:
                        for dy in [-1, 0, 1]:
                            if 0 <= x+dx < 16 and 0 <= y+dy < 16:
                                pixels = img.load()
                                r, g, b, a = pixels[x+dx, y+dy]
                                pixels[x+dx, y+dy] = (r, min(255, g + 20), b, a)
        
        return img
    
    def generate_machine_block(self, machine_type: str, face: str = 'front') -> Image.Image:
        """Generate detailed machine block texture"""
        img = Image.new('RGBA', (self.size, self.size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # Base machine casing
        base_color = (140, 140, 150)
        draw.rectangle([0, 0, 15, 15], fill=(*base_color, 255))
        
        # Add panel lines
        draw.line([(0, 0), (15, 0)], fill=(180, 180, 190, 255), width=1)
        draw.line([(0, 0), (0, 15)], fill=(180, 180, 190, 255), width=1)
        draw.line([(15, 1), (15, 15)], fill=(100, 100, 110, 255), width=1)
        draw.line([(1, 15), (15, 15)], fill=(100, 100, 110, 255), width=1)
        
        # Machine-specific details
        if machine_type == 'generator' and face == 'front':
            # Power indicator
            draw.rectangle([6, 2, 9, 5], fill=(40, 40, 40, 255))
            draw.rectangle([7, 3, 8, 4], fill=(255, 100, 0, 255))
            
            # Vents
            for y in range(8, 14, 2):
                draw.line([(3, y), (12, y)], fill=(80, 80, 80, 255), width=1)
            
        elif machine_type == 'processor' and face == 'front':
            # Input slot
            draw.rectangle([2, 6, 6, 10], fill=(60, 60, 60, 255))
            draw.rectangle([3, 7, 5, 9], fill=(40, 40, 40, 255))
            
            # Output slot
            draw.rectangle([9, 6, 13, 10], fill=(60, 60, 60, 255))
            draw.rectangle([10, 7, 12, 9], fill=(40, 40, 40, 255))
            
            # Progress indicator
            draw.line([(7, 8), (8, 8)], fill=(0, 255, 0, 255), width=1)
            
        elif machine_type == 'assembler' and face == 'front':
            # Display screen
            draw.rectangle([3, 2, 12, 7], fill=(20, 30, 40, 255))
            # Grid pattern
            for x in range(4, 12, 2):
                for y in range(3, 7, 2):
                    draw.point((x, y), fill=(0, 200, 255, 180))
            
            # Control panel
            for x in range(4, 12, 3):
                draw.rectangle([x, 9, x+1, 10], fill=(100, 100, 100, 255))
                
        elif machine_type == 'storage' and face == 'front':
            # Drive bays
            for row in range(2):
                for col in range(4):
                    x = 2 + col * 3
                    y = 4 + row * 5
                    draw.rectangle([x, y, x+2, y+3], fill=(60, 60, 60, 255))
                    draw.rectangle([x, y+3, x+2, y+4], fill=(0, 255, 0, 180))
        
        # Add tech details
        img = self.add_rivets(img, base_color)
        img = self.add_scratches(img, (100, 100, 110), 2)
        img = self.add_noise(img, 0.02)
        
        return img
    
    def generate_multiblock_part(self, multiblock_type: str, part_type: str) -> Image.Image:
        """Generate detailed multiblock structure parts"""
        img = Image.new('RGBA', (self.size, self.size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        if multiblock_type == 'fusion':
            base_color = (100, 100, 150)
            accent_color = (200, 100, 255)
            
            # Base structure
            draw.rectangle([0, 0, 15, 15], fill=(*base_color, 255))
            
            if part_type == 'casing':
                # Reinforced casing pattern
                for i in range(0, 16, 4):
                    draw.line([(i, 0), (i, 15)], fill=(*accent_color, 100), width=1)
                    draw.line([(0, i), (15, i)], fill=(*accent_color, 100), width=1)
                
                # Energy conduits
                draw.ellipse([2, 2, 5, 5], fill=(*accent_color, 180))
                draw.ellipse([10, 10, 13, 13], fill=(*accent_color, 180))
                
            elif part_type == 'controller':
                # Main control interface
                draw.rectangle([2, 2, 13, 9], fill=(20, 20, 40, 255))
                
                # Holographic display effect
                for y in range(3, 9):
                    for x in range(3, 13):
                        if (x + y) % 2 == 0:
                            draw.point((x, y), fill=(*accent_color, 150))
                
                # Control buttons
                for x in range(3, 12, 2):
                    draw.rectangle([x, 11, x+1, 12], fill=(255, 100, 100, 255))
                    
        elif multiblock_type == 'quantum':
            base_color = (80, 60, 100)
            accent_color = (150, 100, 255)
            
            # Quantum matrix pattern
            draw.rectangle([0, 0, 15, 15], fill=(*base_color, 255))
            
            # Quantum field lines
            for i in range(16):
                if i % 2 == 0:
                    alpha = int(100 + 155 * math.sin(i * math.pi / 16))
                    draw.line([(0, i), (15, i)], fill=(*accent_color, alpha), width=1)
                    draw.line([(i, 0), (i, 15)], fill=(*accent_color, alpha), width=1)
            
            if part_type == 'controller':
                # Quantum core
                img = self.create_energy_core_pattern(img, accent_color)
        
        # Common details
        img = self.add_noise(img, 0.02)
        
        return img
    
    def generate_special_item(self, item_type: str, variant: str = 'default') -> Image.Image:
        """Generate special/unique item textures"""
        img = Image.new('RGBA', (self.size, self.size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        if item_type == 'energy_core':
            img = self.create_energy_core_pattern(img, self.materials['energy']['base'])
            
        elif item_type == 'fusion_core':
            # Containment shell
            draw.ellipse([2, 2, 13, 13], fill=(100, 100, 150, 255))
            draw.ellipse([3, 3, 12, 12], fill=(40, 40, 60, 255))
            
            # Plasma core
            img = self.create_energy_core_pattern(img, self.materials['fusion']['base'])
            
        elif item_type == 'wrench':
            # Handle
            draw.rectangle([7, 8, 8, 14], fill=(100, 50, 0, 255))
            draw.rectangle([6, 12, 9, 14], fill=(80, 40, 0, 255))
            
            # Head
            draw.polygon([(6, 2), (9, 2), (10, 3), (10, 5), (9, 6), (6, 6), (5, 5), (5, 3)],
                        fill=(150, 150, 150, 255))
            draw.polygon([(7, 3), (8, 3), (8, 5), (7, 5)], fill=(100, 100, 100, 255))
            
            # Adjustable jaw
            draw.polygon([(4, 3), (4, 5), (5, 5), (5, 3)], fill=(120, 120, 120, 255))
            
        elif item_type == 'research_data':
            # Data chip base
            draw.rectangle([3, 2, 12, 13], fill=(40, 40, 50, 255))
            
            # Data matrix
            tier_colors = {
                'basic': (0, 255, 0),
                'advanced': (0, 200, 255),
                'space': (255, 255, 0),
                'fusion': (255, 0, 255)
            }
            data_color = tier_colors.get(variant, (255, 255, 255))
            
            for y in range(4, 12):
                for x in range(5, 11):
                    if random.random() > 0.3:
                        alpha = random.randint(100, 255)
                        draw.point((x, y), fill=(*data_color, alpha))
            
            # Label
            draw.rectangle([4, 3, 11, 4], fill=(*data_color, 255))
            
        elif item_type == 'cell':
            img = self.create_fluid_container(img, self.materials.get(variant, {}).get('base', (100, 100, 255)))
            
        elif item_type == 'rocket_part':
            if variant == 'cone':
                # Nose cone
                draw.polygon([(8, 1), (13, 10), (13, 14), (3, 14), (3, 10)],
                            fill=(200, 200, 200, 255))
                draw.polygon([(8, 1), (3, 10), (6, 10)], fill=(180, 180, 180, 255))
                draw.polygon([(8, 1), (10, 10), (13, 10)], fill=(220, 220, 220, 255))
                
            elif variant == 'fin':
                # Rocket fin
                draw.polygon([(2, 14), (2, 4), (8, 2), (12, 4), (12, 14), (7, 12)],
                            fill=(180, 0, 0, 255))
                draw.polygon([(3, 13), (3, 5), (7, 4), (11, 5), (11, 13), (7, 11)],
                            fill=(220, 50, 50, 255))
                
            elif variant == 'thruster':
                # Engine nozzle
                draw.ellipse([3, 2, 12, 11], fill=(100, 100, 100, 255))
                draw.ellipse([4, 3, 11, 10], fill=(60, 60, 60, 255))
                draw.ellipse([5, 4, 10, 9], fill=(40, 40, 40, 255))
                
                # Exhaust ports
                for angle in range(0, 360, 60):
                    rad = math.radians(angle)
                    x = int(7.5 + 2 * math.cos(rad))
                    y = int(6.5 + 2 * math.sin(rad))
                    draw.point((x, y), fill=(255, 100, 0, 255))
        
        return img

def main():
    generator = AdvancedTextureGenerator()
    
    # Define all textures to generate
    textures = {
        'items': {
            # Ingots
            'titanium_ingot': lambda: generator.generate_ingot('titanium'),
            'lithium_ingot': lambda: generator.generate_ingot('lithium'),
            'uranium_ingot': lambda: generator.generate_ingot('uranium'),
            'steel_ingot': lambda: generator.generate_ingot('steel'),
            
            # Dusts
            'titanium_dust': lambda: generator.generate_dust('titanium'),
            'lithium_dust': lambda: generator.generate_dust('lithium'),
            'uranium_dust': lambda: generator.generate_dust('uranium'),
            'moon_dust_item': lambda: generator.generate_dust('moon'),
            
            # Plates
            'titanium_plate': lambda: generator.generate_plate('titanium'),
            'reinforced_plate': lambda: generator.generate_plate('reinforced'),
            
            # Processors
            'processor': lambda: generator.generate_processor('basic'),
            'advanced_processor': lambda: generator.generate_processor('advanced'),
            'quantum_processor': lambda: generator.generate_processor('quantum'),
            'storage_processor': lambda: generator.generate_processor('storage'),
            
            # Drones
            'mining_drone': lambda: generator.generate_drone('mining'),
            'combat_drone': lambda: generator.generate_drone('combat'),
            'farming_drone': lambda: generator.generate_drone('farming'),
            'construction_drone': lambda: generator.generate_drone('construction'),
            'logistics_drone': lambda: generator.generate_drone('logistics'),
            
            # Storage
            'storage_drive_1k': lambda: generator.generate_storage_drive('1k'),
            'storage_drive_4k': lambda: generator.generate_storage_drive('4k'),
            'storage_drive_16k': lambda: generator.generate_storage_drive('16k'),
            'storage_drive_64k': lambda: generator.generate_storage_drive('64k'),
            
            # Special items
            'energy_core': lambda: generator.generate_special_item('energy_core'),
            'fusion_core': lambda: generator.generate_special_item('fusion_core'),
            'wrench': lambda: generator.generate_special_item('wrench'),
            
            # Research data
            'research_data_basic': lambda: generator.generate_special_item('research_data', 'basic'),
            'research_data_advanced': lambda: generator.generate_special_item('research_data', 'advanced'),
            'research_data_space': lambda: generator.generate_special_item('research_data', 'space'),
            'research_data_fusion': lambda: generator.generate_special_item('research_data', 'fusion'),
            
            # Cells
            'empty_fuel_cell': lambda: generator.generate_special_item('cell', 'empty'),
            'filled_fuel_cell': lambda: generator.generate_special_item('cell', 'fuel'),
            'deuterium_cell': lambda: generator.generate_special_item('cell', 'deuterium'),
            'tritium_cell': lambda: generator.generate_special_item('cell', 'tritium'),
            'helium_3_cell': lambda: generator.generate_special_item('cell', 'helium'),
            
            # Rocket parts
            'rocket_cone': lambda: generator.generate_special_item('rocket_part', 'cone'),
            'rocket_fin': lambda: generator.generate_special_item('rocket_part', 'fin'),
            'rocket_thruster': lambda: generator.generate_special_item('rocket_part', 'thruster'),
        },
        'blocks': {
            # Ores
            'titanium_ore': lambda: generator.generate_ore_block('titanium'),
            'lithium_ore': lambda: generator.generate_ore_block('lithium'),
            'uranium_ore': lambda: generator.generate_ore_block('uranium'),
            'deepslate_titanium_ore': lambda: generator.generate_ore_block('titanium', 'deepslate'),
            'deepslate_lithium_ore': lambda: generator.generate_ore_block('lithium', 'deepslate'),
            'deepslate_uranium_ore': lambda: generator.generate_ore_block('uranium', 'deepslate'),
            
            # Machines
            'basic_generator': lambda: generator.generate_machine_block('generator', 'front'),
            'material_processor': lambda: generator.generate_machine_block('processor', 'front'),
            'component_assembler': lambda: generator.generate_machine_block('assembler', 'front'),
            'energy_storage': lambda: generator.generate_machine_block('storage', 'front'),
            
            # Multiblock parts
            'fusion_reactor_casing': lambda: generator.generate_multiblock_part('fusion', 'casing'),
            'fusion_reactor_controller': lambda: generator.generate_multiblock_part('fusion', 'controller'),
            'quantum_computer_controller': lambda: generator.generate_multiblock_part('quantum', 'controller'),
            'quantum_casing': lambda: generator.generate_multiblock_part('quantum', 'casing'),
        }
    }
    
    # Generate all textures
    for category, items in textures.items():
        output_dir = f"src/main/resources/assets/astroexpansion/textures/{category.rstrip('s')}"
        os.makedirs(output_dir, exist_ok=True)
        
        print(f"\nGenerating {category}...")
        for name, generator_func in items.items():
            try:
                texture = generator_func()
                texture.save(os.path.join(output_dir, f"{name}.png"))
                print(f"  ✓ Generated {name}.png")
            except Exception as e:
                print(f"  ✗ Failed to generate {name}.png: {e}")
    
    print("\nAdvanced texture generation complete!")

if __name__ == "__main__":
    main()