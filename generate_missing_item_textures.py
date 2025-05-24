#!/usr/bin/env python3
"""Generate missing item textures for AstroExpansion mod."""

from PIL import Image, ImageDraw
import os

# Create directories if they don't exist
os.makedirs("src/main/resources/assets/astroexpansion/textures/item", exist_ok=True)

def create_item_texture(name, color, shape="item"):
    """Create a 16x16 item texture."""
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    if shape == "item":
        # Generic item shape
        draw.rectangle((4, 4, 11, 11), fill=color, outline=(0, 0, 0, 255))
    elif shape == "cell":
        # Fuel cell shape
        draw.rectangle((5, 2, 10, 13), fill=color, outline=(0, 0, 0, 255))
        draw.rectangle((6, 1, 9, 2), fill=color, outline=(0, 0, 0, 255))
        draw.rectangle((6, 13, 9, 14), fill=color, outline=(0, 0, 0, 255))
    elif shape == "canister":
        # Canister shape
        draw.rectangle((4, 3, 11, 12), fill=color, outline=(0, 0, 0, 255))
        draw.rectangle((6, 2, 9, 3), fill=(100, 100, 100, 255))
        draw.rectangle((6, 12, 9, 13), fill=(100, 100, 100, 255))
    elif shape == "crystal":
        # Crystal shape
        points = [(8, 1), (12, 5), (11, 11), (8, 14), (5, 11), (4, 5)]
        draw.polygon(points, fill=color, outline=(0, 0, 0, 255))
    elif shape == "data":
        # Data chip shape
        draw.rectangle((3, 5, 12, 10), fill=color, outline=(0, 0, 0, 255))
        # Circuit pattern
        for i in range(4, 12, 2):
            draw.line([(i, 6), (i, 9)], fill=(0, 0, 0, 255))
    
    return img

# Missing items to generate
missing_items = [
    # Fuel cells
    ("deuterium_cell", (0, 162, 232, 255), "cell"),  # Blue
    ("tritium_cell", (255, 127, 39, 255), "cell"),   # Orange
    ("empty_fuel_cell", (200, 200, 200, 255), "cell"), # Gray
    
    # Research data
    ("research_data_basic", (100, 200, 100, 255), "data"),     # Green
    ("research_data_advanced", (100, 100, 200, 255), "data"),  # Blue
    ("research_data_quantum", (200, 100, 200, 255), "data"),   # Purple
    ("research_data_fusion", (255, 200, 100, 255), "data"),    # Yellow
    ("research_data_space", (100, 200, 255, 255), "data"),     # Cyan
    
    # Oxygen
    ("oxygen_canister", (200, 255, 255, 255), "canister"),  # Light blue
    
    # Moon resources
    ("helium3_crystal", (255, 200, 255, 255), "crystal"),  # Pink crystal
]

# Generate textures
for name, color, shape in missing_items:
    texture = create_item_texture(name, color, shape)
    path = f"src/main/resources/assets/astroexpansion/textures/item/{name}.png"
    texture.save(path)
    print(f"Generated: {name}.png")

print(f"\nGenerated {len(missing_items)} missing item textures!")