#!/usr/bin/env python3
import numpy as np
from PIL import Image, ImageDraw

def create_rocket_texture(rocket_type, size=64):
    """Create a simple rocket texture"""
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Define colors based on rocket type
    if rocket_type == 'probe':
        # Small, simple probe - gray/silver
        body_color = (192, 192, 192, 255)
        accent_color = (255, 0, 0, 255)  # Red accents
        nose_color = (128, 128, 128, 255)
    elif rocket_type == 'personal':
        # Personal rocket - white with blue
        body_color = (240, 240, 240, 255)
        accent_color = (0, 128, 255, 255)  # Blue accents
        nose_color = (200, 200, 200, 255)
    else:  # cargo
        # Cargo shuttle - industrial orange/gray
        body_color = (64, 64, 64, 255)
        accent_color = (255, 128, 0, 255)  # Orange accents
        nose_color = (96, 96, 96, 255)
    
    # Draw rocket body (front view texture)
    # Main body
    draw.rectangle([16, 8, 48, 48], fill=body_color)
    
    # Nose cone
    draw.polygon([(16, 8), (48, 8), (32, 0)], fill=nose_color)
    
    # Engine nozzles
    draw.rectangle([20, 48, 28, 56], fill=(32, 32, 32, 255))
    draw.rectangle([36, 48, 44, 56], fill=(32, 32, 32, 255))
    
    # Accent stripes
    draw.rectangle([16, 20, 48, 24], fill=accent_color)
    draw.rectangle([16, 32, 48, 36], fill=accent_color)
    
    # Windows/ports
    if rocket_type == 'personal':
        # Cockpit window
        draw.ellipse([28, 12, 36, 20], fill=(64, 128, 255, 255))
    elif rocket_type == 'cargo':
        # Cargo doors
        draw.rectangle([24, 24, 40, 32], fill=(48, 48, 48, 255))
        draw.rectangle([25, 25, 39, 31], fill=(32, 32, 32, 255))
    
    # Side fins
    draw.polygon([(16, 40), (8, 48), (16, 48)], fill=body_color)
    draw.polygon([(48, 40), (56, 48), (48, 48)], fill=body_color)
    
    # Add some shading
    for y in range(size):
        for x in range(size):
            pixel = img.getpixel((x, y))
            if pixel[3] > 0:  # If not transparent
                # Add slight gradient
                factor = 1 - (y / size) * 0.2
                new_pixel = (
                    int(pixel[0] * factor),
                    int(pixel[1] * factor),
                    int(pixel[2] * factor),
                    pixel[3]
                )
                img.putpixel((x, y), new_pixel)
    
    return img

def main():
    # Generate textures for all rocket types
    rocket_types = ['probe', 'personal', 'cargo']
    item_names = ['probe_rocket', 'personal_rocket', 'cargo_shuttle']
    
    for rocket_type, item_name in zip(rocket_types, item_names):
        texture = create_rocket_texture(rocket_type)
        output_path = f'src/main/resources/assets/astroexpansion/textures/item/{item_name}.png'
        texture.save(output_path)
        print(f"Created {item_name} texture at: {output_path}")

if __name__ == "__main__":
    main()