#!/usr/bin/env python3
"""
Advanced Texture Generator v2.0 for AstroExpansion
Creates professional-quality 16x16 textures with Minecraft aesthetics
"""

from PIL import Image, ImageDraw, ImageFilter
import numpy as np
import random
import os
import json
from typing import Tuple, Dict, List

class TextureGenerator:
    def __init__(self):
        self.size = 16
        
        # Color palettes
        self.palettes = {
            "titanium": {
                "base": (180, 180, 190),
                "light": (220, 220, 230),
                "dark": (120, 120, 130),
                "accent": (160, 160, 255)
            },
            "lithium": {
                "base": (230, 230, 255),
                "light": (250, 250, 255),
                "dark": (180, 180, 220),
                "accent": (200, 220, 255)
            },
            "uranium": {
                "base": (100, 200, 100),
                "light": (150, 255, 150),
                "dark": (50, 150, 50),
                "accent": (200, 255, 200),
                "glow": (150, 255, 150)
            },
            "energy": {
                "base": (255, 100, 100),
                "light": (255, 150, 150),
                "dark": (200, 50, 50),
                "glow": (255, 200, 200)
            },
            "digital": {
                "base": (100, 200, 255),
                "light": (150, 220, 255),
                "dark": (50, 150, 200),
                "accent": (200, 240, 255)
            },
            "quantum": {
                "base": (200, 100, 255),
                "light": (230, 150, 255),
                "dark": (150, 50, 200),
                "glow": (240, 200, 255)
            }
        }
        
        # Material properties
        self.materials = {
            "metal": {"roughness": 0.2, "metallic": 0.9, "ao": 0.8},
            "stone": {"roughness": 0.8, "metallic": 0.0, "ao": 0.6},
            "crystal": {"roughness": 0.1, "metallic": 0.3, "ao": 0.9},
            "plastic": {"roughness": 0.4, "metallic": 0.1, "ao": 0.7},
            "energy": {"roughness": 0.0, "metallic": 0.0, "ao": 1.0, "emissive": 0.8}
        }

    def create_base(self, color: Tuple[int, int, int]) -> Image.Image:
        """Create base image with color."""
        img = Image.new('RGBA', (self.size, self.size), (*color, 255))
        return img

    def add_shading(self, img: Image.Image, intensity: float = 1.0) -> Image.Image:
        """Add Minecraft-style top-down shading."""
        pixels = img.load()
        width, height = img.size
        
        for y in range(height):
            for x in range(width):
                r, g, b, a = pixels[x, y]
                
                # Calculate shading based on position
                # Top is lighter, bottom is darker
                shade_factor = 1.0 - (y / height) * 0.3 * intensity
                
                # Add subtle left-right shading
                if x < width * 0.2:
                    shade_factor *= 0.95
                elif x > width * 0.8:
                    shade_factor *= 0.92
                
                # Apply shading
                r = int(r * shade_factor)
                g = int(g * shade_factor)
                b = int(b * shade_factor)
                
                pixels[x, y] = (r, g, b, a)
        
        return img

    def add_noise(self, img: Image.Image, amount: float = 0.1) -> Image.Image:
        """Add subtle noise for texture."""
        pixels = img.load()
        width, height = img.size
        
        for y in range(height):
            for x in range(width):
                r, g, b, a = pixels[x, y]
                
                # Add random noise
                noise = random.uniform(-amount, amount)
                r = max(0, min(255, int(r + r * noise)))
                g = max(0, min(255, int(g + g * noise)))
                b = max(0, min(255, int(b + b * noise)))
                
                pixels[x, y] = (r, g, b, a)
        
        return img

    def add_edge_highlight(self, img: Image.Image, color: Tuple[int, int, int]) -> Image.Image:
        """Add edge highlighting for depth."""
        pixels = img.load()
        width, height = img.size
        
        # Top edge highlight
        for x in range(1, width - 1):
            pixels[x, 0] = (*color, 255)
            r, g, b, a = pixels[x, 1]
            blend = 0.5
            pixels[x, 1] = (
                int(r * (1 - blend) + color[0] * blend),
                int(g * (1 - blend) + color[1] * blend),
                int(b * (1 - blend) + color[2] * blend),
                a
            )
        
        # Left edge subtle highlight
        for y in range(1, height - 1):
            r, g, b, a = pixels[0, y]
            blend = 0.3
            pixels[0, y] = (
                int(r * (1 - blend) + color[0] * blend),
                int(g * (1 - blend) + color[1] * blend),
                int(b * (1 - blend) + color[2] * blend),
                a
            )
        
        return img

    def create_ore_texture(self, base_color: str, stone_color: Tuple = (128, 128, 128)) -> Image.Image:
        """Create ore texture with stone background and ore veins."""
        img = self.create_base(stone_color)
        img = self.add_noise(img, 0.3)
        img = self.add_shading(img)
        
        draw = ImageDraw.Draw(img)
        palette = self.palettes[base_color]
        
        # Create ore veins
        ore_positions = [
            (3, 3, 5, 5), (8, 2, 10, 4), (11, 7, 13, 9),
            (2, 9, 4, 11), (6, 11, 8, 13), (7, 6, 9, 8)
        ]
        
        for x1, y1, x2, y2 in ore_positions:
            # Base ore color
            draw.rectangle([x1, y1, x2, y2], fill=palette["base"])
            # Highlight
            draw.rectangle([x1, y1, x1+1, y1+1], fill=palette["light"])
            # Shadow
            draw.rectangle([x2-1, y2-1, x2, y2], fill=palette["dark"])
        
        return img

    def create_ingot_texture(self, base_color: str) -> Image.Image:
        """Create metallic ingot texture."""
        palette = self.palettes[base_color]
        img = self.create_base(palette["base"])
        
        draw = ImageDraw.Draw(img)
        
        # Ingot shape (trapezoid)
        # Top
        draw.polygon([(3, 4), (12, 4), (13, 7), (2, 7)], fill=palette["light"])
        # Front
        draw.polygon([(2, 7), (13, 7), (12, 12), (3, 12)], fill=palette["base"])
        # Side
        draw.polygon([(13, 7), (15, 5), (14, 10), (12, 12)], fill=palette["dark"])
        
        # Metallic highlights
        draw.line([(3, 5), (11, 5)], fill=palette["light"], width=1)
        draw.line([(3, 8), (6, 8)], fill=palette["light"], width=1)
        
        img = self.add_noise(img, 0.05)
        
        return img

    def create_machine_texture(self, base_color: str, face_type: str = "front") -> Image.Image:
        """Create machine block texture."""
        palette = self.palettes.get(base_color, self.palettes["titanium"])
        
        # Base machine frame
        img = Image.new('RGBA', (16, 16), (140, 140, 150, 255))
        img = self.add_shading(img, 0.7)
        img = self.add_noise(img, 0.1)
        
        draw = ImageDraw.Draw(img)
        
        if face_type == "front":
            # Display screen
            draw.rectangle([3, 3, 12, 8], fill=(20, 20, 30))
            draw.rectangle([4, 4, 11, 7], fill=palette["base"])
            # Status lights
            draw.rectangle([4, 10, 5, 11], fill=(100, 255, 100))
            draw.rectangle([7, 10, 8, 11], fill=(255, 200, 100))
            draw.rectangle([10, 10, 11, 11], fill=(255, 100, 100))
            # Vents
            for i in range(3, 13, 2):
                draw.line([(i, 13), (i, 14)], fill=(100, 100, 100))
        
        elif face_type == "side":
            # Vents
            for i in range(2, 14, 3):
                draw.rectangle([2, i, 13, i+1], fill=(100, 100, 110))
        
        elif face_type == "top":
            # Heat exhaust
            draw.rectangle([5, 5, 10, 10], fill=(80, 80, 90))
            draw.rectangle([6, 6, 9, 9], fill=(60, 60, 70))
        
        # Frame borders
        draw.rectangle([0, 0, 15, 0], fill=(180, 180, 190))  # Top
        draw.rectangle([0, 15, 15, 15], fill=(100, 100, 110))  # Bottom
        draw.rectangle([0, 0, 0, 15], fill=(160, 160, 170))  # Left
        draw.rectangle([15, 0, 15, 15], fill=(120, 120, 130))  # Right
        
        return img

    def create_item_texture(self, item_type: str, base_color: str) -> Image.Image:
        """Create item texture based on type."""
        palette = self.palettes.get(base_color, self.palettes["titanium"])
        
        if item_type == "circuit":
            img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
            draw = ImageDraw.Draw(img)
            
            # PCB base
            draw.rectangle([2, 2, 13, 13], fill=(50, 100, 50))
            draw.rectangle([3, 3, 12, 12], fill=(60, 120, 60))
            
            # Circuit traces
            trace_color = (200, 200, 100)
            # Horizontal traces
            for y in [5, 7, 9, 11]:
                draw.line([(3, y), (12, y)], fill=trace_color)
            # Vertical traces
            for x in [5, 7, 9, 11]:
                draw.line([(x, 3), (x, 12)], fill=trace_color)
            
            # Components
            draw.rectangle([4, 4, 6, 6], fill=(40, 40, 40))  # Chip
            draw.rectangle([9, 4, 11, 6], fill=(40, 40, 40))  # Chip
            draw.rectangle([6, 9, 8, 11], fill=(100, 100, 255))  # Capacitor
            
        elif item_type == "crystal":
            img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
            draw = ImageDraw.Draw(img)
            
            # Crystal shape
            points = [
                (8, 2),   # Top
                (11, 5),  # Top right
                (12, 8),  # Right
                (10, 11), # Bottom right
                (8, 13),  # Bottom
                (6, 11),  # Bottom left
                (4, 8),   # Left
                (5, 5)    # Top left
            ]
            
            # Base crystal
            draw.polygon(points, fill=palette["base"])
            
            # Facets for depth
            draw.polygon([(8, 2), (11, 5), (8, 7)], fill=palette["light"])
            draw.polygon([(8, 7), (10, 11), (8, 13)], fill=palette["dark"])
            
            # Inner glow
            if "glow" in palette:
                draw.polygon([(8, 5), (9, 6), (9, 8), (8, 9), (7, 8), (7, 6)], 
                           fill=palette["glow"])
        
        elif item_type == "dust":
            img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
            draw = ImageDraw.Draw(img)
            
            # Pile of dust
            draw.ellipse([3, 8, 12, 13], fill=palette["base"])
            draw.ellipse([4, 6, 11, 11], fill=palette["base"])
            draw.ellipse([5, 5, 10, 9], fill=palette["light"])
            
            # Particles
            for _ in range(8):
                x = random.randint(4, 11)
                y = random.randint(5, 12)
                draw.point((x, y), fill=palette["dark"])
        
        else:  # Generic item
            img = self.create_base(palette["base"])
            img = self.add_shading(img)
            img = self.add_noise(img, 0.1)
        
        return img

    def save_texture(self, img: Image.Image, path: str):
        """Save texture with proper formatting."""
        # Ensure the directory exists
        os.makedirs(os.path.dirname(path), exist_ok=True)
        # Save as PNG
        img.save(path, 'PNG')
        print(f"Saved: {path}")

# Example usage
if __name__ == "__main__":
    gen = TextureGenerator()
    
    # Create output directory
    os.makedirs("generated_textures", exist_ok=True)
    
    # Generate sample textures
    # Ores
    for material in ["titanium", "lithium", "uranium"]:
        ore = gen.create_ore_texture(material)
        gen.save_texture(ore, f"generated_textures/{material}_ore.png")
    
    # Ingots
    for material in ["titanium", "lithium", "uranium"]:
        ingot = gen.create_ingot_texture(material)
        gen.save_texture(ingot, f"generated_textures/{material}_ingot.png")
    
    # Machine blocks
    machine = gen.create_machine_texture("energy", "front")
    gen.save_texture(machine, "generated_textures/machine_front.png")
    
    # Items
    circuit = gen.create_item_texture("circuit", "digital")
    gen.save_texture(circuit, "generated_textures/circuit_board.png")
    
    crystal = gen.create_item_texture("crystal", "quantum")
    gen.save_texture(crystal, "generated_textures/quantum_crystal.png")
    
    dust = gen.create_item_texture("dust", "titanium")
    gen.save_texture(dust, "generated_textures/titanium_dust.png")
    
    print("\nTexture generation complete!")