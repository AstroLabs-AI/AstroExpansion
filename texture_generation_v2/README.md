# AstroExpansion Professional Texture Generator

A professional-grade texture generation system for creating high-quality Minecraft pixel art textures with proper shading, dithering, and material-based rendering.

## Features

- **Professional Pixel Art Techniques**:
  - Material-based texture generation with realistic patterns
  - Limited color palettes matching Minecraft's art style
  - Floyd-Steinberg dithering for smooth color transitions
  - Selective edge highlighting for 3D depth effect
  - Directional lighting simulation
  - Tier-specific visual indicators

- **Comprehensive Texture Types**:
  - Machine blocks (Basic, Advanced, Elite tiers)
  - Ore textures with crystalline formations
  - Item textures (ingots, dust, circuits, processors, upgrades)
  - GUI textures with proper Minecraft standards
  - Special items (tools, components, containers, cells, fuel, data)

- **Material Library**:
  - Metal textures (rough, polished, brushed)
  - Stone and deepslate variations
  - Crystal formations
  - Tech panels with circuit patterns
  - Energy/glow effects

## Usage

1. **Install dependencies**:
   ```bash
   pip install -r requirements.txt
   ```

2. **Run the batch generator**:
   ```bash
   ./generate_all_textures.sh
   ```

   Or manually:
   ```bash
   python texture_generator_pro.py --output ../src/main/resources/assets/astroexpansion/textures --config texture_config.json
   ```

## Configuration

Edit `texture_config.json` to control which textures are generated:

```json
{
  "tiers": ["basic", "advanced", "elite"],
  "machines": ["generator", "processor", "washer"],
  "ores": ["titanium", "lithium", "uranium"],
  "items": {
    "ingot": ["titanium", "lithium"],
    "dust": ["titanium", "lithium"]
  },
  "guis": {
    "basic_generator": {"type": "generator"},
    "storage_terminal": {"type": "storage", "width": 200, "height": 224}
  }
}
```

## Texture Standards

### Block Textures (16x16)
- Use material-based rendering
- Apply tier-specific color palettes
- Include wear/damage for basic tier
- Add tech details for advanced tier
- Include energy effects for elite tier

### Item Textures (16x16)
- Maintain consistent perspective
- Use proper shading for 3D appearance
- Follow Minecraft's item conventions
- Include material-specific details

### GUI Textures
- Standard size: 176x166 pixels
- Player inventory at x:8, y:84
- Slots are 18x18 pixels
- Use proper border styles (inset for slots, raised for panels)
- Maintain Minecraft's GUI color palette

## Architecture

The generator uses a layered approach:

1. **Base Material Layer**: Creates foundational textures (metal, stone, etc.)
2. **Pattern Layer**: Adds details like rivets, vents, panel lines
3. **Lighting Layer**: Applies directional lighting for depth
4. **Palette Layer**: Applies tier-specific color schemes
5. **Effect Layer**: Adds final touches like edge highlighting and dithering

## Color Palettes

### Basic Tier
- Primary: Gray metals (89, 105, 121)
- Accents: Orange energy (255, 136, 0)

### Advanced Tier
- Primary: Blue-gray metals (55-89, 71-115, 89-142)
- Accents: Cyan energy (0, 174, 255)

### Elite Tier
- Primary: Purple-tinted metals (35-71, 25-58, 56-105)
- Accents: Purple energy (170, 0, 255)

## Adding New Textures

1. Add the texture type to the appropriate generator method
2. Update the configuration file
3. Implement the texture creation logic following existing patterns
4. Test with a single texture before batch generation

## Quality Guidelines

- Limit colors to 4-8 per texture
- Use dithering for gradients
- Apply subtle noise for texture
- Maintain consistent lighting direction
- Test textures in-game for visibility
- Ensure GUIs align with vanilla Minecraft standards