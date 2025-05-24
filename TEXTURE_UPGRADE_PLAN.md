# Texture Upgrade Plan for AstroExpansion

## Overview
Upgrade all textures from basic generated ones to professional-quality 16x16 pixel art that matches Minecraft's aesthetic while giving the mod a unique sci-fi identity.

## Design Principles

### 1. **Minecraft Style Consistency**
- Use Minecraft's color palette as a base
- Follow vanilla shading patterns (top-light source)
- Maintain recognizable shapes and patterns
- Use noise/dithering sparingly for texture

### 2. **Sci-Fi Theme Elements**
- Metallic surfaces with proper highlights
- Glowing elements for energy/tech items
- Circuit patterns for electronics
- Clean, futuristic lines for machines
- Crystalline structures for advanced materials

### 3. **Color Coding System**
- **Titanium**: Silver/light gray metallic
- **Lithium**: White/pale blue
- **Uranium**: Green with subtle glow
- **Energy**: Red/orange for power
- **Digital**: Blue/cyan for data storage
- **Quantum**: Purple for advanced tech
- **Fusion**: Yellow/gold for plasma

## Texture Categories

### 1. **Raw Materials & Ingots**
- Add depth with shadows and highlights
- Metallic sheen for ingots
- Rough/crystalline texture for raw ores
- Consistent shape language

### 2. **Machine Blocks**
- Front face: Functional display/interface
- Sides: Vents, panels, or maintenance ports
- Top: Heat vents or access panels
- Connected texture support where applicable

### 3. **Multiblock Components**
- Seamless tiling patterns
- Visual indicators for connection points
- Consistent style across same multiblock

### 4. **Items**
- Clear silhouettes
- Functional details (buttons, screens, connections)
- Consistent perspective (45° for tools, front-facing for components)

## Implementation Plan

### Phase 1: Texture Generator 2.0
Create an advanced texture generator with:
- Predefined color palettes
- Shading algorithms
- Pattern libraries (circuits, vents, panels)
- Noise generation for texture
- Metallic surface rendering
- Glow/emission effects

### Phase 2: Base Textures
1. Create texture templates for each category
2. Define consistent shading patterns
3. Establish color palettes per material type
4. Build reusable elements library

### Phase 3: Batch Generation
1. Ores and raw materials
2. Ingots and processed materials
3. Basic machine blocks
4. Advanced machine blocks
5. Multiblock components
6. Items and tools
7. Special effects (particles, animations)

### Phase 4: Manual Polish
1. Review generated textures
2. Hand-adjust problem areas
3. Add unique details to important items
4. Create animated textures for active machines
5. Ensure visual consistency

## Technical Specifications

### Texture Features
- **Base**: 16x16 pixels, PNG format
- **Color Depth**: 32-bit RGBA
- **Animation**: Use .mcmeta for animated textures
- **Emissive**: Use optifine emissive layers
- **Connected**: CTM support for glass/frames

### File Structure
```
textures/
├── block/
│   ├── machines/
│   ├── multiblock/
│   ├── ores/
│   └── storage/
├── item/
│   ├── components/
│   ├── materials/
│   ├── tools/
│   └── armor/
└── gui/
    └── container/
```

## Advanced Generator Features

### 1. **Material System**
```python
materials = {
    "metal": {"shine": 0.8, "roughness": 0.2},
    "crystal": {"shine": 0.6, "transparency": 0.3},
    "plastic": {"shine": 0.3, "roughness": 0.5},
    "energy": {"shine": 1.0, "glow": 0.8}
}
```

### 2. **Shading Algorithm**
- Top edge: Brightest (light source from above)
- Bottom edge: Darkest (shadow)
- Left/Right: Medium shading
- Center: Base color with texture

### 3. **Pattern Library**
- Circuit patterns for tech items
- Vent grilles for machines
- Warning stripes for hazardous materials
- Energy flows for powered items
- Crystal facets for gems

### 4. **Color Harmonics**
- Analogous colors for related items
- Complementary colors for opposing functions
- Triadic schemes for complete sets

## Quality Metrics

### Visual Consistency
- All textures use same shading style
- Color palettes are cohesive
- Detail level is uniform

### Readability
- Items are distinguishable at a glance
- Important features are highlighted
- Contrast is appropriate

### Performance
- File sizes optimized
- No unnecessary transparency
- Efficient use of texture atlas

## Deliverables

1. **Texture Generator 2.0** - Python script with advanced features
2. **Texture Template Library** - Base patterns and elements
3. **Complete Texture Pack** - All blocks and items
4. **Animation Files** - .mcmeta for animated textures
5. **Documentation** - Style guide and color palettes

## Timeline

- Week 1: Develop advanced texture generator
- Week 2: Create base templates and patterns
- Week 3: Generate and refine all textures
- Week 4: Polish and add special effects

## Success Criteria

- Textures look cohesive with vanilla Minecraft
- Clear visual hierarchy and readability
- Consistent sci-fi aesthetic
- Professional polish comparable to popular mods
- Positive community feedback