#!/usr/bin/env python3
import numpy as np
from PIL import Image, ImageDraw

def create_space_item_texture(item_type):
    """Create 16x16 space loot item textures"""
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    if item_type == 'meteor_fragment':
        # Dark rocky fragment with metallic spots
        # Base rock shape
        draw.polygon([(3, 4), (5, 2), (11, 3), (13, 6), (12, 11), (8, 13), (4, 12), (2, 8)], 
                    fill=(64, 48, 32, 255))
        # Inner detail
        draw.polygon([(5, 6), (7, 5), (10, 7), (9, 10), (6, 11), (4, 9)], 
                    fill=(48, 36, 24, 255))
        # Metallic spots
        draw.rectangle([6, 7, 7, 8], fill=(192, 192, 192, 255))
        draw.rectangle([8, 9, 9, 10], fill=(160, 160, 160, 255))
        
    elif item_type == 'cosmic_dust':
        # Sparkly purple-blue dust pile
        # Main dust pile
        draw.ellipse([3, 8, 12, 14], fill=(128, 64, 192, 255))
        draw.ellipse([5, 6, 10, 12], fill=(160, 96, 224, 255))
        # Sparkles
        for x, y in [(4, 7), (7, 5), (9, 7), (11, 9), (6, 10)]:
            draw.point((x, y), fill=(255, 255, 255, 255))
        
    elif item_type == 'space_station_key':
        # Futuristic key card
        # Card body
        draw.rectangle([3, 2, 12, 13], fill=(64, 64, 80, 255))
        draw.rectangle([4, 3, 11, 12], fill=(96, 96, 112, 255))
        # Circuit pattern
        draw.line([(5, 4), (10, 4)], fill=(0, 255, 255, 255))
        draw.line([(5, 4), (5, 8)], fill=(0, 255, 255, 255))
        draw.line([(10, 4), (10, 8)], fill=(0, 255, 255, 255))
        draw.line([(5, 8), (10, 8)], fill=(0, 255, 255, 255))
        # Access dots
        draw.rectangle([6, 10, 7, 11], fill=(255, 0, 0, 255))
        draw.rectangle([8, 10, 9, 11], fill=(0, 255, 0, 255))
        
    elif item_type == 'alien_artifact':
        # Mysterious glowing artifact
        # Outer glow
        draw.ellipse([2, 2, 13, 13], fill=(128, 255, 128, 128))
        # Main artifact
        draw.polygon([(7, 3), (11, 7), (7, 11), (3, 7)], fill=(64, 255, 64, 255))
        # Inner core
        draw.polygon([(7, 5), (9, 7), (7, 9), (5, 7)], fill=(192, 255, 192, 255))
        # Center dot
        draw.rectangle([7, 7, 8, 8], fill=(255, 255, 255, 255))
    
    return img

def main():
    items = ['meteor_fragment', 'cosmic_dust', 'space_station_key', 'alien_artifact']
    
    for item in items:
        texture = create_space_item_texture(item)
        output_path = f'src/main/resources/assets/astroexpansion/textures/item/{item}.png'
        texture.save(output_path)
        print(f"Created {item} texture at: {output_path}")

if __name__ == "__main__":
    main()