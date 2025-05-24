#!/usr/bin/env python3
"""Generate missing GUI textures for AstroExpansion mod."""

from PIL import Image, ImageDraw
import os

# Create textures directory if it doesn't exist
os.makedirs("src/main/resources/assets/astroexpansion/textures/gui", exist_ok=True)

def create_base_gui(width=176, height=166):
    """Create a base GUI texture."""
    img = Image.new('RGBA', (256, 256), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Main panel background
    draw.rectangle((0, 0, width-1, height-1), fill=(198, 198, 198, 255), outline=(85, 85, 85, 255))
    
    # Inner panel
    draw.rectangle((7, 15, width-8, height-8), fill=(139, 139, 139, 255))
    
    # Title bar
    draw.rectangle((7, 4, width-8, 14), fill=(169, 169, 169, 255))
    
    # Player inventory area
    inv_y = height - 82
    draw.rectangle((7, inv_y, width-8, height-8), fill=(139, 139, 139, 255))
    
    # Player inventory slots (9x3)
    for row in range(3):
        for col in range(9):
            x = 7 + col * 18
            y = inv_y + 17 + row * 18
            draw.rectangle((x, y, x+17, y+17), fill=(56, 56, 56, 255), outline=(0, 0, 0, 255))
    
    # Hotbar slots
    for col in range(9):
        x = 7 + col * 18
        y = height - 24
        draw.rectangle((x, y, x+17, y+17), fill=(56, 56, 56, 255), outline=(0, 0, 0, 255))
    
    return img, draw

def create_research_terminal_gui():
    """Create Research Terminal GUI texture."""
    img, draw = create_base_gui(176, 222)
    
    # Research display area
    draw.rectangle((7, 30, 168, 110), fill=(32, 32, 32, 255), outline=(0, 0, 0, 255))
    
    # Research lines
    for i in range(5):
        y = 35 + i * 15
        draw.rectangle((10, y, 165, y+12), fill=(48, 48, 48, 255))
    
    # Save
    img.save("src/main/resources/assets/astroexpansion/textures/gui/research_terminal.png")
    print("Created research_terminal.png")

def create_fuel_refinery_gui():
    """Create Fuel Refinery GUI texture."""
    img, draw = create_base_gui()
    
    # Oil tank (left)
    draw.rectangle((25, 15, 43, 69), fill=(32, 32, 32, 255), outline=(0, 0, 0, 255))
    draw.rectangle((26, 16, 42, 68), fill=(56, 56, 56, 255))
    
    # Fuel tank (right)
    draw.rectangle((133, 15, 151, 69), fill=(32, 32, 32, 255), outline=(0, 0, 0, 255))
    draw.rectangle((134, 16, 150, 68), fill=(56, 56, 56, 255))
    
    # Processing area
    draw.rectangle((51, 33, 125, 52), fill=(139, 139, 139, 255))
    
    # Input slot
    draw.rectangle((55, 35, 72, 52), fill=(56, 56, 56, 255), outline=(0, 0, 0, 255))
    
    # Output slot
    draw.rectangle((104, 35, 121, 52), fill=(56, 56, 56, 255), outline=(0, 0, 0, 255))
    
    # Arrow background
    draw.rectangle((75, 35, 100, 52), fill=(169, 169, 169, 255))
    
    # Progress arrow overlay (at x=176 for use in GUI)
    draw.polygon([(176, 0), (200, 8), (176, 17)], fill=(255, 255, 255, 255))
    
    # Save
    img.save("src/main/resources/assets/astroexpansion/textures/gui/fuel_refinery.png")
    print("Created fuel_refinery.png")

def create_rocket_assembly_gui():
    """Create Rocket Assembly GUI texture."""
    img, draw = create_base_gui(176, 222)
    
    # Status display area
    draw.rectangle((7, 15, 168, 45), fill=(32, 32, 32, 255), outline=(0, 0, 0, 255))
    
    # Checklist area
    draw.rectangle((7, 48, 168, 115), fill=(48, 48, 48, 255), outline=(0, 0, 0, 255))
    
    # Component slots (3x3 grid)
    for row in range(3):
        for col in range(3):
            x = 25 + col * 42
            y = 150 + row * 18
            draw.rectangle((x, y, x+17, y+17), fill=(56, 56, 56, 255), outline=(0, 0, 0, 255))
    
    # Fuel gauge background
    draw.rectangle((151, 15, 169, 69), fill=(32, 32, 32, 255), outline=(0, 0, 0, 255))
    draw.rectangle((152, 16, 168, 68), fill=(56, 56, 56, 255))
    
    # Save
    img.save("src/main/resources/assets/astroexpansion/textures/gui/rocket_assembly.png")
    print("Created rocket_assembly.png")

def create_quantum_computer_gui():
    """Create Quantum Computer GUI texture."""
    img, draw = create_base_gui(176, 222)
    
    # Quantum core display
    draw.rectangle((68, 20, 108, 60), fill=(0, 0, 64, 255), outline=(0, 255, 255, 255))
    
    # Processing slots (left side)
    for i in range(4):
        y = 20 + i * 20
        draw.rectangle((20, y, 37, y+17), fill=(56, 56, 56, 255), outline=(0, 0, 0, 255))
    
    # Output slots (right side)
    for i in range(4):
        y = 20 + i * 20
        draw.rectangle((139, y, 156, y+17), fill=(56, 56, 56, 255), outline=(0, 0, 0, 255))
    
    # Power indicator
    draw.rectangle((78, 70, 98, 90), fill=(32, 32, 32, 255), outline=(0, 0, 0, 255))
    
    # Save
    img.save("src/main/resources/assets/astroexpansion/textures/gui/quantum_computer.png")
    print("Created quantum_computer.png")

if __name__ == "__main__":
    create_research_terminal_gui()
    create_fuel_refinery_gui()
    create_rocket_assembly_gui()
    create_quantum_computer_gui()
    print("\nAll GUI textures created successfully!")