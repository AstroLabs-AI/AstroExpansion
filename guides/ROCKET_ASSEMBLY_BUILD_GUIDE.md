# Rocket Assembly Platform Build Guide

The Rocket Assembly Platform is a massive 9x15x9 multiblock structure required to build and launch rockets. This guide provides detailed layer-by-layer instructions.

## Overview
- **Dimensions**: 9x15x9 blocks (Width x Height x Depth)
- **Total Blocks Needed**:
  - 81 Launch Pad blocks (9x9 base)
  - 504 Assembly Frame blocks (tower structure)
  - 1 Rocket Assembly Controller
  - Total: 586 blocks

## Materials Checklist
Before starting, craft:
- [ ] 81 Launch Pad blocks
- [ ] 504 Assembly Frame blocks  
- [ ] 1 Rocket Assembly Controller
- [ ] 1 Wrench (for adjustments)
- [ ] Building blocks for scaffolding

## Block Recipes

### Launch Pad (Crafts 4)
```
[Obsidian] [Titanium Block] [Obsidian]
[Titanium Block] [Stone] [Titanium Block]
[Stone] [Stone] [Stone]
```

### Assembly Frame (Crafts 2)
```
[Titanium Ingot] [Titanium Plate] [Titanium Ingot]
[Titanium Plate] [] [Titanium Plate]
[Titanium Ingot] [Titanium Plate] [Titanium Ingot]
```

### Rocket Assembly Controller
```
[Titanium Block] [Quantum Processor] [Titanium Block]
[Energy Core] [Advanced Processor] [Energy Core]
[Titanium Block] [Energy Core] [Titanium Block]
```

## Building Instructions

### Step 1: Clear the Area
- Find a flat area at least 11x17x11 (with 1 block buffer)
- Clear to bedrock if possible (rockets need vertical space)
- Mark the center with a temporary block

### Step 2: Build the Launch Pad Base (Layer 0)

```
Layer 0 (Y=64 or ground level) - 9x9 Launch Pad
L L L L L L L L L
L L L L L L L L L
L L L L L L L L L
L L L L L L L L L
L L L L C L L L L
L L L L L L L L L
L L L L L L L L L
L L L L L L L L L
L L L L L L L L L

L = Launch Pad
C = Center (mark for reference)
```

### Step 3: Build the Tower Frame (Layers 1-14)

The tower consists of Assembly Frame blocks forming a hollow structure around the launch pad.

```
Layers 1-14 (Vertical tower) - 9x9 with hollow center
A A A A A A A A A
A . . . . . . . A
A . . . . . . . A
A . . . . . . . A
A . . . X . . . A  ← Place Controller at Layer 7, center front
A . . . . . . . A
A . . . . . . . A
A . . . . . . . A
A A A A A A A A A

A = Assembly Frame
. = Empty space (air)
X = Rocket Assembly Controller (only on layer 7)
```

### Detailed Layer Breakdown:

**Layers 1-6**: Standard frame pattern (hollow 9x9)
- Each layer uses 32 Assembly Frame blocks
- Leave center 7x7 area empty for rocket

**Layer 7**: Controller layer
- Same pattern but place Rocket Assembly Controller in the center of one side
- Controller should face outward for easy access

**Layers 8-14**: Continue standard frame pattern
- Each layer uses 32 Assembly Frame blocks
- Total height reaches 15 blocks

### Step 4: Final Structure Check

The completed structure should have:
- Solid 9x9 Launch Pad base
- 14-layer tall hollow tower of Assembly Frames
- Controller accessible from outside at mid-height
- Clear 7x7x14 interior space for rocket assembly

## Visual Representation

### Side View (Cross-section):
```
Layer 14  A A A A A A A A A  ← Top
Layer 13  A . . . . . . . A
Layer 12  A . . . . . . . A
Layer 11  A . . . . . . . A
Layer 10  A . . . . . . . A
Layer 9   A . . . . . . . A
Layer 8   A . . . . . . . A
Layer 7   X . . . . . . . A  ← Controller
Layer 6   A . . . . . . . A
Layer 5   A . . . . . . . A
Layer 4   A . . . . . . . A
Layer 3   A . . . . . . . A
Layer 2   A . . . . . . . A
Layer 1   A . . . . . . . A
Layer 0   L L L L L L L L L  ← Launch Pad
```

### Top View (Looking down):
```
North
  ↑
A A A A A A A A A
A . . . . . . . A
A . . . . . . . A
A . . . . . . . A  West ← X → East
A . . . . . . . A         ↑
A . . . . . . . A    Controller
A . . . . . . . A
A . . . . . . . A
A A A A A A A A A
  ↓
South
```

## Using the Platform

### 1. Access the Controller
- Right-click the Rocket Assembly Controller
- Opens the Rocket Assembly GUI

### 2. Place Rocket Components
The GUI has slots for:
- **Nose Cone**: Navigation and control
- **Hull Sections**: Main body (6-8 recommended)
- **Fuel Tanks**: Propellant storage (2-3 needed)
- **Engines**: Propulsion (3-4 for best thrust)

### 3. Assembly Order
1. Place Nose Cone first
2. Add Hull sections
3. Install Fuel Tanks
4. Attach Engines at bottom
5. Click "Assemble" button

### 4. Fueling
- Pump Rocket Fuel into assembled rocket
- Need approximately 64 buckets
- Use Fluid Pipes or manual filling

### 5. Launch Sequence
1. Enter rocket (right-click)
2. Ensure space suit equipped
3. Check oxygen levels
4. Initiate launch from controller
5. Hold spacebar during ascent!

## Troubleshooting

### "Invalid Structure" Error
- Check all blocks are correct type
- Ensure no missing Assembly Frames
- Verify Controller placement
- No blocks inside the hollow area

### "No Rocket Detected"
- Assemble rocket first via GUI
- Check component placement
- Ensure sufficient materials

### Performance Issues
- Build away from main base
- Chunk load the area
- Reduce particle effects in settings

## Tips for Builders

1. **Use Scaffolding**: Build temporary scaffolding for easier access to upper layers

2. **Ghost Block Preview**: Hold the Controller to see transparent preview of structure

3. **Material Calculation**:
   - 81 ÷ 4 = 21 crafts of Launch Pad (84 total, 3 extra)
   - 504 ÷ 2 = 252 crafts of Assembly Frame (504 exact)

4. **Power Supply**: Connect power to Controller for faster assembly

5. **Automation Ready**: Leave space for:
   - Fuel pipes on sides
   - Item insertion for components
   - Player access around base

## Safety Considerations

- **Clear Sky Above**: No blocks above the platform
- **Explosion Protection**: Build away from valuable structures
- **Return Planning**: Set up landing pad nearby
- **Emergency Supplies**: Chest with backup oxygen and fuel

## Next Steps

After building the platform:
1. Craft rocket components
2. Produce rocket fuel
3. Set up oxygen generation
4. Prepare space suit
5. Plan your space base

**Happy launching, future astronaut!** 🚀