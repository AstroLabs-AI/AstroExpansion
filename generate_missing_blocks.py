#!/usr/bin/env python3
"""
Generate missing block textures, models, and blockstates for AstroExpansion
"""

import json
from pathlib import Path
from PIL import Image, ImageDraw

# Missing blocks that need generation
MISSING_BLOCKS = [
    'fusion_coil', 'fusion_core_block', 'quantum_computer_controller',
    'quantum_casing', 'quantum_processor', 'quantum_core', 'research_terminal',
    'fuel_refinery_controller', 'refinery_casing', 'distillation_column',
    'rocket_assembly_controller', 'launch_pad', 'assembly_frame',
    'rocket_engine', 'rocket_fuel_tank', 'rocket_hull', 'rocket_nose_cone',
    'station_hull', 'station_glass', 'solar_panel', 'docking_port',
    'oxygen_generator', 'life_support_system', 'moon_stone', 'moon_dust',
    'helium3_ore', 'space_teleporter', 'moon_teleporter', 'earth_teleporter'
]

# Color schemes for different block types
BLOCK_COLORS = {
    # Fusion reactor blocks
    'fusion_coil': (200, 150, 50),
    'fusion_core_block': (255, 200, 0),
    
    # Quantum computer blocks
    'quantum_computer_controller': (100, 200, 255),
    'quantum_casing': (80, 150, 200),
    'quantum_processor': (150, 200, 255),
    'quantum_core': (200, 220, 255),
    
    # Research & refinery
    'research_terminal': (150, 100, 200),
    'fuel_refinery_controller': (180, 120, 60),
    'refinery_casing': (160, 100, 40),
    'distillation_column': (140, 140, 140),
    
    # Rocket blocks
    'rocket_assembly_controller': (200, 50, 50),
    'launch_pad': (80, 80, 80),
    'assembly_frame': (150, 150, 150),
    'rocket_engine': (100, 100, 120),
    'rocket_fuel_tank': (180, 60, 60),
    'rocket_hull': (200, 200, 200),
    'rocket_nose_cone': (180, 180, 200),
    
    # Space station blocks
    'station_hull': (160, 160, 180),
    'station_glass': (200, 200, 255),
    'solar_panel': (50, 50, 150),
    'docking_port': (120, 120, 140),
    
    # Life support
    'oxygen_generator': (100, 150, 200),
    'life_support_system': (150, 200, 150),
    
    # Moon blocks
    'moon_stone': (180, 180, 180),
    'moon_dust': (200, 200, 200),
    'helium3_ore': (150, 255, 150),
    
    # Teleporters
    'space_teleporter': (100, 0, 200),
    'moon_teleporter': (200, 200, 100),
    'earth_teleporter': (50, 150, 50)
}

def generate_block_texture(name: str, color: tuple) -> Image:
    """Generate a simple block texture with the given color"""
    img = Image.new('RGB', (16, 16), color)
    draw = ImageDraw.Draw(img)
    
    # Add simple shading
    for i in range(16):
        shade = int(255 * (1 - i / 32))
        draw.line([(i, 0), (i, 0)], fill=(shade, shade, shade))
        draw.line([(0, i), (0, i)], fill=(shade, shade, shade))
    
    # Add highlight
    highlight = tuple(min(255, c + 50) for c in color)
    draw.line([(1, 1), (14, 1)], fill=highlight)
    draw.line([(1, 1), (1, 14)], fill=highlight)
    
    # Add shadow
    shadow = tuple(max(0, c - 50) for c in color)
    draw.line([(1, 15), (15, 15)], fill=shadow)
    draw.line([(15, 1), (15, 15)], fill=shadow)
    
    return img

def create_blockstate(block_name: str) -> dict:
    """Create a simple blockstate JSON"""
    return {
        "variants": {
            "": {"model": f"astroexpansion:block/{block_name}"}
        }
    }

def create_block_model(block_name: str) -> dict:
    """Create a simple block model JSON"""
    return {
        "parent": "minecraft:block/cube_all",
        "textures": {
            "all": f"astroexpansion:block/{block_name}"
        }
    }

def create_item_model(block_name: str) -> dict:
    """Create a simple item model JSON"""
    return {
        "parent": f"astroexpansion:block/{block_name}"
    }

def main():
    # Setup paths
    base_path = Path(__file__).parent
    texture_path = base_path / "src/main/resources/assets/astroexpansion/textures/block"
    blockstate_path = base_path / "src/main/resources/assets/astroexpansion/blockstates"
    block_model_path = base_path / "src/main/resources/assets/astroexpansion/models/block"
    item_model_path = base_path / "src/main/resources/assets/astroexpansion/models/item"
    
    # Create directories
    texture_path.mkdir(parents=True, exist_ok=True)
    blockstate_path.mkdir(parents=True, exist_ok=True)
    block_model_path.mkdir(parents=True, exist_ok=True)
    item_model_path.mkdir(parents=True, exist_ok=True)
    
    generated = 0
    
    for block_name in MISSING_BLOCKS:
        color = BLOCK_COLORS.get(block_name, (128, 128, 128))
        
        # Generate texture
        texture_file = texture_path / f"{block_name}.png"
        if not texture_file.exists():
            texture = generate_block_texture(block_name, color)
            texture.save(texture_file)
            print(f"Generated texture: {block_name}.png")
        
        # Generate blockstate
        blockstate_file = blockstate_path / f"{block_name}.json"
        if not blockstate_file.exists():
            with open(blockstate_file, 'w') as f:
                json.dump(create_blockstate(block_name), f, indent=2)
            print(f"Generated blockstate: {block_name}.json")
        
        # Generate block model
        block_model_file = block_model_path / f"{block_name}.json"
        if not block_model_file.exists():
            with open(block_model_file, 'w') as f:
                json.dump(create_block_model(block_name), f, indent=2)
            print(f"Generated block model: {block_name}.json")
        
        # Generate item model
        item_model_file = item_model_path / f"{block_name}.json"
        if not item_model_file.exists():
            with open(item_model_file, 'w') as f:
                json.dump(create_item_model(block_name), f, indent=2)
            print(f"Generated item model: {block_name}.json")
        
        generated += 1
    
    print(f"\n✓ Generated assets for {generated} missing blocks!")
    print("You can now run './gradlew runClient' to test the mod.")

if __name__ == "__main__":
    main()