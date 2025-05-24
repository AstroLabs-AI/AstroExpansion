#!/usr/bin/env python3
"""Generate missing block textures for AstroExpansion mod."""

from PIL import Image, ImageDraw
import os

# Create directories if they don't exist
os.makedirs("src/main/resources/assets/astroexpansion/textures/block", exist_ok=True)

def create_conduit_texture():
    """Create energy conduit texture."""
    img = Image.new('RGBA', (16, 16), (139, 139, 139, 255))  # Gray base
    draw = ImageDraw.Draw(img)
    
    # Center core
    draw.rectangle((6, 6, 9, 9), fill=(255, 0, 0, 255))  # Red energy core
    
    # Connection points
    draw.rectangle((7, 0, 8, 6), fill=(100, 100, 100, 255))   # Top
    draw.rectangle((7, 9, 8, 16), fill=(100, 100, 100, 255))  # Bottom
    draw.rectangle((0, 7, 6, 8), fill=(100, 100, 100, 255))   # Left
    draw.rectangle((9, 7, 16, 8), fill=(100, 100, 100, 255))  # Right
    
    return img

def create_pipe_texture():
    """Create fluid pipe texture."""
    img = Image.new('RGBA', (16, 16), (139, 139, 139, 255))  # Gray base
    draw = ImageDraw.Draw(img)
    
    # Center core (glass window)
    draw.rectangle((6, 6, 9, 9), fill=(200, 230, 255, 255))  # Light blue glass
    
    # Pipe connections
    draw.rectangle((7, 0, 8, 6), fill=(100, 100, 100, 255))   # Top
    draw.rectangle((7, 9, 8, 16), fill=(100, 100, 100, 255))  # Bottom
    draw.rectangle((0, 7, 6, 8), fill=(100, 100, 100, 255))   # Left
    draw.rectangle((9, 7, 16, 8), fill=(100, 100, 100, 255))  # Right
    
    return img

# Generate textures
conduit = create_conduit_texture()
conduit.save("src/main/resources/assets/astroexpansion/textures/block/energy_conduit.png")
print("Generated: energy_conduit.png")

pipe = create_pipe_texture()
pipe.save("src/main/resources/assets/astroexpansion/textures/block/fluid_pipe.png")
print("Generated: fluid_pipe.png")

print("\nGenerated missing block textures!")