#!/usr/bin/env python3
from PIL import Image, ImageDraw
import os

def create_block_texture(block_name, size=16):
    """Create block textures based on block type"""
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    if block_name == 'advanced_teleporter':
        # Purple/ender themed advanced version
        base_color = (150, 50, 200)
        accent_color = (255, 255, 255)
        # Base
        draw.rectangle([0, 0, 16, 16], fill=base_color)
        # Frame
        draw.rectangle([0, 0, 16, 1], fill=accent_color)
        draw.rectangle([0, 15, 16, 16], fill=accent_color)
        draw.rectangle([0, 0, 1, 16], fill=accent_color)
        draw.rectangle([15, 0, 16, 16], fill=accent_color)
        # Center portal
        draw.rectangle([4, 4, 12, 12], fill=(200, 100, 255))
        draw.rectangle([6, 6, 10, 10], fill=(255, 200, 255))
        
    elif block_name == 'landing_pad':
        # Concrete landing pad with markings
        base_color = (128, 128, 128)
        line_color = (255, 255, 0)
        draw.rectangle([0, 0, 16, 16], fill=base_color)
        # Landing markings
        draw.line([0, 8, 16, 8], fill=line_color, width=1)
        draw.line([8, 0, 8, 16], fill=line_color, width=1)
        # Corner markers
        draw.rectangle([2, 2, 4, 4], fill=line_color)
        draw.rectangle([12, 2, 14, 4], fill=line_color)
        draw.rectangle([2, 12, 4, 14], fill=line_color)
        draw.rectangle([12, 12, 14, 14], fill=line_color)
        
    elif block_name == 'launch_rail':
        # Metal rail for rocket launch
        base_color = (64, 64, 64)
        rail_color = (192, 192, 192)
        draw.rectangle([0, 0, 16, 16], fill=base_color)
        # Rails
        draw.rectangle([3, 0, 5, 16], fill=rail_color)
        draw.rectangle([11, 0, 13, 16], fill=rail_color)
        # Cross supports
        for y in range(0, 16, 4):
            draw.rectangle([5, y, 11, y+1], fill=rail_color)
            
    elif block_name == 'matter_duplicator':
        # High-tech duplicator
        base_color = (100, 100, 150)
        accent_color = (200, 50, 200)
        draw.rectangle([0, 0, 16, 16], fill=base_color)
        # Tech pattern
        draw.rectangle([2, 2, 14, 14], fill=(120, 120, 170))
        draw.rectangle([4, 4, 12, 12], fill=accent_color)
        draw.rectangle([6, 6, 10, 10], fill=(255, 100, 255))
        
    elif block_name == 'matter_fabricator':
        # UU-Matter fabricator
        base_color = (50, 50, 100)
        accent_color = (100, 200, 255)
        draw.rectangle([0, 0, 16, 16], fill=base_color)
        # Tech grid
        for x in range(2, 15, 4):
            for y in range(2, 15, 4):
                draw.rectangle([x, y, x+2, y+2], fill=accent_color)
                
    elif block_name == 'recycler':
        # Green recycling machine
        base_color = (50, 100, 50)
        accent_color = (100, 200, 100)
        draw.rectangle([0, 0, 16, 16], fill=base_color)
        # Recycling symbol area
        draw.rectangle([4, 4, 12, 12], fill=accent_color)
        # Arrow pattern
        draw.line([6, 6, 10, 6], fill=(255, 255, 255))
        draw.line([10, 6, 10, 10], fill=(255, 255, 255))
        draw.line([10, 10, 6, 10], fill=(255, 255, 255))
        
    elif block_name == 'rocket_workbench':
        # Crafting bench for rockets
        base_color = (139, 90, 43)  # Wood
        metal_color = (192, 192, 192)
        draw.rectangle([0, 0, 16, 16], fill=base_color)
        # Metal top
        draw.rectangle([0, 0, 16, 4], fill=metal_color)
        # Work surface marks
        draw.rectangle([2, 6, 6, 10], fill=(100, 50, 20))
        draw.rectangle([10, 6, 14, 10], fill=(100, 50, 20))
        
    elif block_name == 'teleporter_frame':
        # Frame block for teleporter multiblock
        base_color = (80, 80, 120)
        accent_color = (150, 150, 200)
        draw.rectangle([0, 0, 16, 16], fill=base_color)
        # Frame edges
        draw.rectangle([0, 0, 16, 2], fill=accent_color)
        draw.rectangle([0, 14, 16, 16], fill=accent_color)
        draw.rectangle([0, 0, 2, 16], fill=accent_color)
        draw.rectangle([14, 0, 16, 16], fill=accent_color)
    
    return img

def main():
    missing_blocks = [
        'advanced_teleporter',
        'landing_pad', 
        'launch_rail',
        'matter_duplicator',
        'matter_fabricator',
        'recycler',
        'rocket_workbench',
        'teleporter_frame'
    ]
    
    output_dir = 'src/main/resources/assets/astroexpansion/textures/block'
    os.makedirs(output_dir, exist_ok=True)
    
    for block_name in missing_blocks:
        texture = create_block_texture(block_name)
        output_path = os.path.join(output_dir, f'{block_name}.png')
        texture.save(output_path)
        print(f"Created {block_name} texture at: {output_path}")

if __name__ == "__main__":
    main()