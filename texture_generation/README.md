# AstroExpansion Texture Generation System

A comprehensive texture generation system for creating unified, modern textures with a space/astro theme and 3D-ish appearance.

## Features

- **Unified Design Language**: Consistent color palette and visual style across all textures
- **3D Effects**: Beveled edges, shadows, and highlights for depth
- **Tier Differentiation**: Visual progression from Basic → Advanced → Elite
- **Animated Textures**: Support for animated blocks with .mcmeta generation
- **Batch Processing**: Generate all mod textures with a single command
- **Customizable**: Easy to modify colors, patterns, and styles

## Quick Start

```bash
# Run from texture_generation directory
./generate_all.sh
```

This will generate all textures and place them in the appropriate directories.

## Color Palette

### Primary Colors (Space Theme)
- **Space Dark**: `#0A1929` - Deep space background
- **Space Mid**: `#1E3A5F` - Medium space blue
- **Space Light**: `#2C5282` - Light space accent

### Secondary Colors (Metallic)
- **Metal Dark**: `#404040` - Dark metal/shadow
- **Metal Mid**: `#808080` - Standard metal
- **Metal Light**: `#C0C0C0` - Light metal
- **Metal Shine**: `#E0E0E0` - Highlights

### Energy Colors
- **Energy Blue**: `#00D4FF` - Standard energy
- **Energy Cyan**: `#0099CC` - Data/tech energy
- **Energy Orange**: `#FF6B35` - Heat/power
- **Energy Red**: `#FF4500` - Danger/combat
- **Energy Green**: `#00FF88` - Nature/healing
- **Energy Purple**: `#9D4EDD` - Quantum/endgame

## Texture Types

### Blocks
- **Machines**: Tiered designs with status indicators
- **Ores**: Crystalline formations in stone/deepslate
- **Storage**: Clean tech aesthetic with data patterns
- **Multiblock**: Modular components that connect visually

### Items
- **Ingots**: Metallic with realistic shading
- **Components**: High-tech circuits and processors
- **Tools**: Ergonomic designs with energy indicators
- **Upgrades**: Tiered modules with visual progression

### GUI
- **Containers**: Dark theme with rounded corners
- **Widgets**: Consistent button and control styles
- **Bars**: Energy/fluid indicators with animations
- **Icons**: Clear, simple iconography

### Entities
- **Rockets**: Detailed hull plating and engines
- **Drones**: Modular design with tool attachments
- **Effects**: Particle and beam textures

## File Structure

```
texture_generation/
├── generate_all.sh          # Master generation script
├── texture_generator_unified.py  # Main texture generator
├── gui_texture_generator.py     # Specialized GUI generator
├── generate_all_textures.py     # Batch processor
├── texture_manifest.json        # Complete texture list
├── requirements.txt            # Python dependencies
└── README.md                  # This file
```

## Customization

### Adding New Textures

1. Add entry to `texture_manifest.json`
2. Define colors in `get_colors_for_texture()`
3. Run generation script

### Modifying Styles

Edit constants in `texture_generator_unified.py`:
- `Colors` class for palette changes
- Generator methods for style changes
- Size definitions for dimensions

### Creating Variants

Use the tier system:
```python
config = TextureConfig(
    name="my_machine",
    tier="advanced",  # basic, advanced, elite
    accent_color=Colors.ENERGY_BLUE
)
```

## Advanced Features

### Animated Textures
```python
config.has_animation = True
config.animation_frames = 16
```

### Multi-face Blocks
Define in `generate_special_textures()` for blocks with different textures per face.

### Custom Patterns
- Circuit patterns for tech blocks
- Hexagonal patterns for quantum tech
- Energy flow effects

## Tips

1. **Consistency**: Always use the predefined color palette
2. **Contrast**: Ensure textures are readable at distance
3. **Performance**: Keep textures at 16x16 for items/blocks
4. **Testing**: Always test in-game after generation

## Troubleshooting

- **Missing textures**: Check `texture_manifest.json` for proper listing
- **Wrong colors**: Verify color mappings in `get_colors_for_texture()`
- **No 3D effect**: Ensure `create_3d_border()` is being called
- **GUI issues**: Check size definitions match Minecraft standards

## Future Enhancements

- [ ] Connected textures (CTM) support
- [ ] Emissive texture layers
- [ ] Normal/specular maps for shaders
- [ ] Procedural wear/damage variants
- [ ] Biome-specific variations