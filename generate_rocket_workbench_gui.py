#!/usr/bin/env python3
import numpy as np
from PIL import Image, ImageDraw

def create_rocket_workbench_gui():
    """Create GUI texture for rocket workbench with 3x3 crafting grid"""
    # Create base image
    img = Image.new('RGBA', (256, 256), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Define colors
    bg_dark = (139, 139, 139, 255)
    bg_light = (198, 198, 198, 255)
    bg_mid = (169, 169, 169, 255)
    border_dark = (55, 55, 55, 255)
    border_light = (255, 255, 255, 255)
    slot_dark = (90, 90, 90, 255)
    slot_light = (110, 110, 110, 255)
    
    # Main GUI panel (176x166)
    # Draw background
    draw.rectangle([0, 0, 175, 165], fill=bg_mid)
    
    # Draw borders
    draw.line([(0, 0), (175, 0), (175, 165), (0, 165), (0, 0)], fill=border_light, width=2)
    draw.line([(2, 2), (173, 2), (173, 163), (2, 163), (2, 2)], fill=border_dark, width=2)
    
    # Title area background
    draw.rectangle([4, 4, 171, 16], fill=bg_dark)
    
    # Crafting grid area (3x3)
    # Grid starts at x=30, y=17
    for row in range(3):
        for col in range(3):
            x = 30 + col * 18
            y = 17 + row * 18
            # Slot background
            draw.rectangle([x-1, y-1, x+17, y+17], fill=slot_dark)
            draw.rectangle([x, y, x+16, y+16], fill=slot_light)
    
    # Arrow pointing to result
    arrow_x = 90
    arrow_y = 35
    # Arrow shaft
    draw.rectangle([arrow_x, arrow_y+6, arrow_x+22, arrow_y+10], fill=bg_dark)
    # Arrow head
    draw.polygon([
        (arrow_x+22, arrow_y+2),
        (arrow_x+22, arrow_y+14),
        (arrow_x+28, arrow_y+8)
    ], fill=bg_dark)
    
    # Output slot (larger)
    out_x = 124
    out_y = 35
    draw.rectangle([out_x-2, out_y-2, out_x+18, out_y+18], fill=slot_dark)
    draw.rectangle([out_x-1, out_y-1, out_x+17, out_y+17], fill=slot_light)
    
    # Player inventory label area
    draw.rectangle([4, 72, 171, 82], fill=bg_dark)
    
    # Player inventory slots (9x3)
    for row in range(3):
        for col in range(9):
            x = 8 + col * 18
            y = 84 + row * 18
            draw.rectangle([x-1, y-1, x+17, y+17], fill=slot_dark)
            draw.rectangle([x, y, x+16, y+16], fill=slot_light)
    
    # Player hotbar slots (9x1)
    for col in range(9):
        x = 8 + col * 18
        y = 142
        draw.rectangle([x-1, y-1, x+17, y+17], fill=slot_dark)
        draw.rectangle([x, y, x+16, y+16], fill=slot_light)
    
    # Add some decorative elements
    # Rocket icon in top right
    rocket_x = 150
    rocket_y = 20
    # Rocket body
    draw.rectangle([rocket_x, rocket_y, rocket_x+10, rocket_y+20], fill=border_light)
    # Rocket nose
    draw.polygon([
        (rocket_x, rocket_y),
        (rocket_x+10, rocket_y),
        (rocket_x+5, rocket_y-5)
    ], fill=border_light)
    # Rocket fins
    draw.polygon([
        (rocket_x-2, rocket_y+20),
        (rocket_x, rocket_y+15),
        (rocket_x, rocket_y+20)
    ], fill=border_light)
    draw.polygon([
        (rocket_x+10, rocket_y+15),
        (rocket_x+12, rocket_y+20),
        (rocket_x+10, rocket_y+20)
    ], fill=border_light)
    
    # Save the image
    output_path = 'src/main/resources/assets/astroexpansion/textures/gui/rocket_workbench.png'
    img.save(output_path, 'PNG')
    print(f"Created rocket workbench GUI texture at: {output_path}")

if __name__ == "__main__":
    create_rocket_workbench_gui()