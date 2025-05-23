# AstroExpansion Texture Generator

This texture generator creates placeholder textures for all AstroExpansion mod assets.

## Quick Start

1. Install dependencies:
```bash
pip install -r requirements.txt
```

2. Generate all textures:
```bash
# Basic textures (fast, simple)
python generate_all.py

# Advanced textures (slower, more detailed)
python generate_all.py --advanced
```

## Features

### Basic Generator (`texture_generator.py`)
- Simple, fast texture generation
- Consistent color schemes for materials
- Basic patterns for blocks and items
- Standard GUI layouts

### Advanced Generator (`advanced_generator.py`)
- Procedural texture generation
- Metallic and glass effects
- Wear and damage simulation
- Animated texture support
- More realistic materials

### Configuration (`texture_config.json`)
- Customize colors and styles
- Define material properties
- Configure machine appearances
- Set GUI themes

## Texture Types

- **Blocks** (16x16): Ores, machines, multiblock components
- **Items** (16x16): Components, materials, tools
- **GUIs** (256x256): Machine interfaces with slots and progress bars
- **Entities** (64x32): Drone textures with team colors

## Usage Examples

### Generate specific texture type
```python
from texture_generator import TextureGenerator

generator = TextureGenerator("output_dir")
generator.generate_block_texture("titanium_ore")
generator.generate_gui_texture("basic_generator")
```

### Customize generation
Edit `texture_config.json` to change:
- Material colors (titanium, lunar metals, etc.)
- Machine styles (industrial, quantum, etc.)
- GUI themes and layouts

## Output Structure
```
assets/astroexpansion/
├── textures/
│   ├── block/      # Block textures
│   ├── item/       # Item textures
│   ├── gui/        # GUI textures
│   └── entity/     # Entity textures
```

## Tips
- Use basic generator for quick prototyping
- Use advanced generator for release-quality textures
- Textures are automatically placed in correct directories
- Existing textures are not overwritten