#!/usr/bin/env python3
"""
Generate missing item textures and models for AstroExpansion
"""

import json
from pathlib import Path
from PIL import Image, ImageDraw

# Missing items that need generation
MISSING_ITEMS = [
    # Space suit items
    'space_helmet', 'space_chestplate', 'space_leggings', 'space_boots',
    'oxygen_mask', 'oxygen_tank', 'thermal_padding_helmet', 'thermal_padding_chestplate',
    'thermal_padding_leggings', 'thermal_padding_boots',
    
    # Advanced items
    'quantum_processor', 'rocket_fuel', 'rocket_computer', 'plasma_injector',
    'helium3_canister', 'rare_earth_dust', 'rare_earth_ingot',
    'lunar_iron_ingot', 'raw_lunar_iron', 'moon_dust_item',
    
    # Fuel items
    'empty_fuel_cell', 'filled_fuel_cell',
    
    # Tools
    'multimeter',
    
    # Vehicle items
    'lunar_rover'
]

# Color schemes for different item types
ITEM_COLORS = {
    # Space suit
    'space_helmet': (200, 200, 220),
    'space_chestplate': (200, 200, 220),
    'space_leggings': (200, 200, 220),
    'space_boots': (200, 200, 220),
    'oxygen_mask': (100, 100, 120),
    'oxygen_tank': (150, 150, 170),
    
    # Thermal padding
    'thermal_padding_helmet': (255, 150, 50),
    'thermal_padding_chestplate': (255, 150, 50),
    'thermal_padding_leggings': (255, 150, 50),
    'thermal_padding_boots': (255, 150, 50),
    
    # Advanced items
    'quantum_processor': (150, 200, 255),
    'rocket_fuel': (200, 50, 50),
    'rocket_computer': (100, 200, 100),
    'plasma_injector': (255, 100, 255),
    'helium3_canister': (150, 255, 150),
    'rare_earth_dust': (180, 140, 100),
    'rare_earth_ingot': (200, 160, 120),
    'lunar_iron_ingot': (220, 220, 240),
    'raw_lunar_iron': (180, 180, 200),
    'moon_dust_item': (200, 200, 200),
    
    # Fuel cells
    'empty_fuel_cell': (150, 150, 150),
    'filled_fuel_cell': (200, 50, 50),
    
    # Tools
    'multimeter': (100, 150, 200),
    
    # Vehicles
    'lunar_rover': (180, 180, 200)
}

def generate_item_texture(name: str, color: tuple) -> Image:
    """Generate a simple item texture with the given color"""
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Different patterns for different item types
    if 'helmet' in name:
        # Helmet shape
        draw.ellipse([3, 2, 12, 11], fill=color)
        draw.rectangle([4, 10, 11, 13], fill=color)
    elif 'chestplate' in name:
        # Chestplate shape
        draw.rectangle([4, 3, 11, 12], fill=color)
        draw.rectangle([2, 5, 13, 8], fill=color)
    elif 'leggings' in name:
        # Leggings shape
        draw.rectangle([5, 2, 10, 8], fill=color)
        draw.rectangle([4, 8, 6, 13], fill=color)
        draw.rectangle([9, 8, 11, 13], fill=color)
    elif 'boots' in name:
        # Boots shape
        draw.rectangle([4, 10, 7, 14], fill=color)
        draw.rectangle([8, 10, 11, 14], fill=color)
    elif 'processor' in name or 'computer' in name:
        # Circuit pattern
        draw.rectangle([2, 2, 13, 13], fill=color)
        darker = tuple(max(0, c - 50) for c in color)
        for i in range(4, 12, 3):
            draw.line([(i, 2), (i, 13)], fill=darker)
            draw.line([(2, i), (13, i)], fill=darker)
    elif 'canister' in name or 'tank' in name or 'cell' in name:
        # Cylinder shape
        draw.ellipse([4, 2, 11, 5], fill=color)
        draw.rectangle([4, 3, 11, 12], fill=color)
        draw.ellipse([4, 11, 11, 14], fill=color)
    elif 'dust' in name:
        # Powder texture
        for x in range(3, 13):
            for y in range(3, 13):
                if (x + y) % 2 == 0:
                    draw.point((x, y), fill=color)
    elif 'ingot' in name:
        # Ingot shape
        draw.polygon([(4, 6), (11, 6), (12, 9), (3, 9)], fill=color)
        lighter = tuple(min(255, c + 30) for c in color)
        draw.polygon([(4, 6), (5, 3), (12, 3), (11, 6)], fill=lighter)
    elif 'rover' in name:
        # Simple vehicle shape
        draw.rectangle([2, 8, 13, 12], fill=color)
        draw.ellipse([1, 10, 5, 14], fill=(80, 80, 80))
        draw.ellipse([10, 10, 14, 14], fill=(80, 80, 80))
        draw.rectangle([5, 5, 10, 8], fill=tuple(min(255, c + 30) for c in color))
    else:
        # Generic item
        draw.rectangle([3, 3, 12, 12], fill=color)
    
    # Add simple shading
    pixels = img.load()
    for x in range(16):
        for y in range(16):
            if pixels[x, y][3] > 0:  # If pixel is not transparent
                # Add highlight on top-left
                if x < 8 and y < 8:
                    r, g, b, a = pixels[x, y]
                    pixels[x, y] = (min(255, r + 20), min(255, g + 20), min(255, b + 20), a)
                # Add shadow on bottom-right
                elif x > 8 and y > 8:
                    r, g, b, a = pixels[x, y]
                    pixels[x, y] = (max(0, r - 20), max(0, g - 20), max(0, b - 20), a)
    
    return img

def create_item_model(item_name: str) -> dict:
    """Create a simple item model JSON"""
    return {
        "parent": "minecraft:item/generated",
        "textures": {
            "layer0": f"astroexpansion:item/{item_name}"
        }
    }

def main():
    # Setup paths
    base_path = Path(__file__).parent
    texture_path = base_path / "src/main/resources/assets/astroexpansion/textures/item"
    item_model_path = base_path / "src/main/resources/assets/astroexpansion/models/item"
    
    # Create directories
    texture_path.mkdir(parents=True, exist_ok=True)
    item_model_path.mkdir(parents=True, exist_ok=True)
    
    generated = 0
    
    for item_name in MISSING_ITEMS:
        color = ITEM_COLORS.get(item_name, (128, 128, 128))
        
        # Generate texture
        texture_file = texture_path / f"{item_name}.png"
        if not texture_file.exists():
            texture = generate_item_texture(item_name, color)
            texture.save(texture_file)
            print(f"Generated texture: {item_name}.png")
        
        # Generate item model
        item_model_file = item_model_path / f"{item_name}.json"
        if not item_model_file.exists():
            with open(item_model_file, 'w') as f:
                json.dump(create_item_model(item_name), f, indent=2)
            print(f"Generated item model: {item_name}.json")
        
        generated += 1
    
    print(f"\n✓ Generated assets for {generated} missing items!")

if __name__ == "__main__":
    main()