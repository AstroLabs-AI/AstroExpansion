#!/usr/bin/env python3
"""
Batch texture generation script for AstroExpansion
Processes all textures defined in texture_manifest.json
"""

import os
import sys
import json
import subprocess
from pathlib import Path

# Add the unified generator to path
sys.path.append(os.path.dirname(os.path.abspath(__file__)))
from texture_generator_unified import TextureGenerator, TextureConfig, TextureType, Colors

def load_manifest(manifest_path):
    """Load texture manifest"""
    with open(manifest_path, 'r') as f:
        return json.load(f)

def get_texture_type(category):
    """Map category to TextureType"""
    if category.startswith('blocks'):
        return TextureType.BLOCK
    elif category.startswith('items'):
        return TextureType.ITEM
    elif category.startswith('gui'):
        return TextureType.GUI
    elif category.startswith('entity'):
        return TextureType.ENTITY
    else:
        return TextureType.BLOCK

def get_size_for_texture(texture_name, category):
    """Determine appropriate size for texture"""
    # GUI textures
    if 'gui' in category:
        if 'container' in category:
            return (256, 256)
        elif 'button' in texture_name:
            return (200, 60) if texture_name == 'button' else (100, 20)
        elif 'bar' in texture_name:
            return (16, 52)
        elif 'arrow' in texture_name:
            return (24, 16)
        else:
            return (32, 32)
    
    # Entity textures
    elif 'entity' in category:
        if 'cargo' in texture_name:
            return (256, 256)
        elif 'personal' in texture_name:
            return (128, 128)
        else:
            return (64, 64)
    
    # Default block/item size
    else:
        return (16, 16)

def get_colors_for_texture(texture_name, category):
    """Determine colors based on texture name and category"""
    base_color = Colors.METAL_MID
    accent_color = None
    
    # Machine tiers
    if 'basic' in texture_name:
        base_color = Colors.METAL_MID
        accent_color = Colors.ENERGY_ORANGE
    elif 'advanced' in texture_name:
        base_color = Colors.SPACE_MID
        accent_color = Colors.ENERGY_BLUE
    elif 'elite' in texture_name:
        base_color = Colors.SPACE_DARK
        accent_color = Colors.ENERGY_PURPLE
    
    # Specific materials
    elif 'titanium' in texture_name:
        base_color = Colors.TITANIUM
    elif 'lithium' in texture_name:
        base_color = Colors.LITHIUM
    elif 'uranium' in texture_name:
        base_color = Colors.URANIUM
        accent_color = Colors.ENERGY_GREEN
    
    # Machine types
    elif 'generator' in texture_name:
        accent_color = Colors.ENERGY_ORANGE
    elif 'processor' in texture_name:
        accent_color = Colors.ENERGY_GREEN
    elif 'washer' in texture_name:
        accent_color = Colors.ENERGY_BLUE
    elif 'storage' in texture_name:
        accent_color = Colors.ENERGY_CYAN
    elif 'quantum' in texture_name:
        base_color = Colors.SPACE_DARK
        accent_color = Colors.ENERGY_PURPLE
    elif 'fusion' in texture_name:
        accent_color = Colors.ENERGY_ORANGE
    
    return base_color, accent_color

def generate_texture_configs(manifest):
    """Generate TextureConfig objects from manifest"""
    configs = []
    
    for main_category, subcategories in manifest['textures'].items():
        if isinstance(subcategories, dict):
            for subcategory, textures in subcategories.items():
                for texture_name in textures:
                    category_path = f"{main_category}/{subcategory}"
                    texture_type = get_texture_type(main_category)
                    size = get_size_for_texture(texture_name, category_path)
                    base_color, accent_color = get_colors_for_texture(texture_name, category_path)
                    
                    # Determine tier if applicable
                    tier = None
                    if 'basic' in texture_name:
                        tier = 'basic'
                    elif 'advanced' in texture_name:
                        tier = 'advanced'
                    elif 'elite' in texture_name:
                        tier = 'elite'
                    
                    config = TextureConfig(
                        name=texture_name,
                        type=texture_type,
                        size=size,
                        base_color=base_color,
                        accent_color=accent_color,
                        tier=tier,
                        category=subcategory
                    )
                    configs.append((category_path, config))
        else:
            # Direct texture list
            for texture_name in subcategories:
                texture_type = get_texture_type(main_category)
                size = get_size_for_texture(texture_name, main_category)
                base_color, accent_color = get_colors_for_texture(texture_name, main_category)
                
                config = TextureConfig(
                    name=texture_name,
                    type=texture_type,
                    size=size,
                    base_color=base_color,
                    accent_color=accent_color,
                    category=main_category
                )
                configs.append((main_category, config))
    
    return configs

def generate_special_textures(generator, output_base):
    """Generate special case textures that need custom handling"""
    
    # Multi-face block textures
    multi_face_blocks = {
        'component_assembler': {
            'faces': ['front', 'side', 'top'],
            'base': Colors.METAL_MID
        },
        'industrial_furnace_controller': {
            'faces': ['front', 'side', 'top'],
            'base': Colors.METAL_DARK
        },
        'rocket_workbench': {
            'faces': ['side', 'top'],
            'base': Colors.SPACE_MID
        },
        'drone_dock': {
            'faces': ['top', 'side', 'bottom'],
            'base': Colors.METAL_MID
        }
    }
    
    print("\nGenerating multi-face block textures...")
    for block_name, info in multi_face_blocks.items():
        for face in info['faces']:
            texture_name = f"{block_name}_{face}"
            config = TextureConfig(
                name=texture_name,
                type=TextureType.BLOCK,
                size=(16, 16),
                base_color=info['base'],
                accent_color=Colors.ENERGY_CYAN,
                category='machines'
            )
            
            texture = generator.generate_machine_texture(config)
            output_path = os.path.join(output_base, 'block', f'{texture_name}.png')
            texture.save(output_path)
            print(f"  Generated: {texture_name}")
    
    # Animated textures
    animated_configs = [
        ('recycler_active', 'block', Colors.METAL_MID, Colors.ENERGY_GREEN),
        ('matter_fabricator_on', 'block', Colors.SPACE_DARK, Colors.ENERGY_PURPLE),
        ('matter_duplicator_on', 'block', Colors.SPACE_MID, Colors.ENERGY_CYAN),
        ('storage_core_active', 'block', Colors.METAL_LIGHT, Colors.ENERGY_BLUE),
        ('energy_conduit_core', 'block', Colors.METAL_DARK, Colors.ENERGY_BLUE),
        ('fluid_pipe_core', 'block', Colors.METAL_DARK, Colors.ENERGY_CYAN),
    ]
    
    print("\nGenerating animated textures...")
    for texture_name, texture_type, base_color, accent_color in animated_configs:
        config = TextureConfig(
            name=texture_name,
            type=TextureType.BLOCK if texture_type == 'block' else TextureType.ITEM,
            size=(16, 16),
            base_color=base_color,
            accent_color=accent_color,
            has_animation=True,
            animation_frames=16,
            category='machines'
        )
        
        # Generate base texture
        texture = generator.generate_machine_texture(config)
        
        # Create animation strip (vertical)
        animation_strip = Image.new('RGBA', (16, 16 * 16))
        for frame in range(16):
            # Modify texture for each frame (simple pulse effect)
            frame_texture = texture.copy()
            # Add pulsing glow effect
            intensity = (frame / 16) * 0.5 + 0.5
            glow = generator.create_energy_glow((16, 16), accent_color, intensity)
            frame_texture.paste(glow, (0, 0), glow)
            
            animation_strip.paste(frame_texture, (0, frame * 16))
        
        output_path = os.path.join(output_base, texture_type, f'{texture_name}.png')
        animation_strip.save(output_path)
        
        # Create mcmeta file
        mcmeta = {
            "animation": {
                "frametime": 2,
                "frames": list(range(16))
            }
        }
        mcmeta_path = output_path + '.mcmeta'
        with open(mcmeta_path, 'w') as f:
            json.dump(mcmeta, f, indent=2)
        
        print(f"  Generated: {texture_name} (animated)")

def main():
    """Main execution"""
    # Paths
    script_dir = Path(__file__).parent
    manifest_path = script_dir / 'texture_manifest.json'
    output_base = Path('/workspaces/AstroExpansion/src/main/resources/assets/astroexpansion/textures')
    
    print("AstroExpansion Batch Texture Generator")
    print("=====================================")
    print(f"Manifest: {manifest_path}")
    print(f"Output: {output_base}")
    print()
    
    # Load manifest
    if not manifest_path.exists():
        print(f"Error: Manifest file not found: {manifest_path}")
        return 1
    
    manifest = load_manifest(manifest_path)
    
    # Create generator
    generator = TextureGenerator(str(output_base))
    
    # Generate textures from manifest
    configs = generate_texture_configs(manifest)
    
    print(f"Generating {len(configs)} textures from manifest...")
    print()
    
    # Process by category
    current_category = None
    for category_path, config in configs:
        if category_path != current_category:
            current_category = category_path
            print(f"\n{category_path}:")
        
        print(f"  - {config.name}", end='')
        
        try:
            # Generate appropriate texture based on type
            if config.type == TextureType.BLOCK:
                if 'machine' in config.category:
                    texture = generator.generate_machine_texture(config)
                elif 'ore' in config.category:
                    texture = generator.generate_ore_texture(config)
                else:
                    # Generic block texture
                    texture = Image.new('RGBA', config.size, config.base_color)
                    texture = generator.add_noise_texture(texture, 0.05)
                    texture = generator.create_3d_border(texture)
            
            elif config.type == TextureType.ITEM:
                texture = generator.generate_item_texture(config)
            
            elif config.type == TextureType.GUI:
                texture = generator.generate_gui_texture(config)
            
            elif config.type == TextureType.ENTITY:
                texture = generator.generate_entity_texture(config)
            
            else:
                # Fallback
                texture = Image.new('RGBA', config.size, config.base_color)
            
            # Save texture
            if config.type == TextureType.BLOCK:
                subdir = 'block'
            elif config.type == TextureType.ITEM:
                subdir = 'item'
            elif config.type == TextureType.GUI:
                subdir = 'gui'
            elif config.type == TextureType.ENTITY:
                subdir = 'entity'
            else:
                subdir = ''
            
            output_dir = output_base / subdir
            output_dir.mkdir(parents=True, exist_ok=True)
            
            output_path = output_dir / f'{config.name}.png'
            texture.save(output_path)
            print(" ✓")
            
        except Exception as e:
            print(f" ✗ Error: {e}")
    
    # Generate special textures
    generate_special_textures(generator, output_base)
    
    print("\n\nTexture generation complete!")
    
    # Summary
    total_textures = sum(len(textures) if isinstance(textures, list) else 
                        sum(len(t) for t in textures.values()) 
                        for textures in manifest['textures'].values())
    
    print(f"\nSummary:")
    print(f"  Total textures: {total_textures}")
    print(f"  Output directory: {output_base}")
    
    return 0

if __name__ == "__main__":
    # Import PIL
    try:
        from PIL import Image, ImageDraw
    except ImportError:
        print("Error: PIL/Pillow not installed")
        print("Please run: pip install Pillow")
        sys.exit(1)
    
    sys.exit(main())