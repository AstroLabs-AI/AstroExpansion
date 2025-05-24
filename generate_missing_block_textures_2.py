#!/usr/bin/env python3
"""Generate missing block textures for AstroExpansion mod."""

from PIL import Image, ImageDraw
import os

# Create directories if they don't exist
os.makedirs("src/main/resources/assets/astroexpansion/textures/block", exist_ok=True)

def create_machine_texture(base_color, accent_color, has_display=False):
    """Create a 16x16 machine texture."""
    img = Image.new('RGBA', (16, 16), base_color)
    draw = ImageDraw.Draw(img)
    
    # Frame
    draw.rectangle((0, 0, 15, 15), outline=(50, 50, 50, 255))
    draw.rectangle((1, 1, 14, 14), outline=(70, 70, 70, 255))
    
    if has_display:
        # Display screen
        draw.rectangle((4, 4, 11, 8), fill=accent_color, outline=(0, 0, 0, 255))
    else:
        # Vent/detail lines
        for i in range(3, 13, 2):
            draw.line([(3, i), (12, i)], fill=accent_color, width=1)
    
    return img

# Missing block textures
missing_textures = [
    ("component_assembler", (150, 150, 150, 255), (100, 200, 255, 255), True),  # Gray with blue display
    ("drone_dock", (120, 120, 140, 255), (255, 200, 100, 255), True),          # Dark gray with yellow
    ("industrial_furnace_controller", (180, 120, 80, 255), (255, 150, 50, 255), False)  # Brown with orange
]

# Generate textures
for name, base_color, accent_color, has_display in missing_textures:
    # Generate multiple sides if needed
    sides = {
        "": create_machine_texture(base_color, accent_color, has_display),  # Default
        "_top": create_machine_texture(base_color, (100, 100, 100, 255), False),
        "_side": create_machine_texture(base_color, accent_color, False)
    }
    
    # Component assembler needs special front texture
    if name == "component_assembler":
        front = create_machine_texture(base_color, accent_color, True)
        # Add crafting grid pattern
        draw = ImageDraw.Draw(front)
        for x in [5, 8, 11]:
            for y in [10, 13]:
                draw.rectangle((x-1, y-1, x, y), fill=(80, 80, 80, 255))
        sides["_front"] = front
    
    # Save textures
    for suffix, texture in sides.items():
        if suffix or name != "drone_dock":  # drone_dock only needs base texture
            path = f"src/main/resources/assets/astroexpansion/textures/block/{name}{suffix}.png"
            texture.save(path)
            print(f"Generated: {name}{suffix}.png")

print(f"\nGenerated missing block textures!")