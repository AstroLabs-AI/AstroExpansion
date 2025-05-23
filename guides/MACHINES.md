# Machines & Processing Guide

This guide covers all machines in AstroExpansion, their recipes, uses, and processing chains.

## 🏭 Machine Overview

### Processing Machines
- **Material Processor** - Doubles ore output
- **Ore Washer** - Bonus materials from raw ores
- **Component Assembler** - Crafts advanced components
- **Industrial Furnace** - Fast bulk smelting (multiblock)

### Utility Machines
- **Basic Generator** - Power generation
- **Energy Storage** - Power buffer
- **Fluid Tank** - Liquid storage
- **Drone Dock** - Drone management station

## ⚙️ Material Processor

Your first and most important processing machine.

### Specifications
- **Power**: 20 FE/tick active, 5 FE/tick idle
- **Speed**: 10 seconds per operation
- **Energy**: 1,000 FE per item

### Recipe
```
[I] [P] [I]
[R] [F] [R]
[I] [C] [I]

I = Iron Ingot
P = Piston
R = Redstone
F = Furnace
C = Circuit Board
```

### Processing Recipes

**Ore Doubling:**
- Raw Titanium → 2x Titanium Dust
- Raw Lithium → 2x Lithium Dust
- Raw Uranium → 2x Uranium Dust
- Raw Iron → 2x Iron Dust
- Raw Gold → 2x Gold Dust
- Raw Copper → 2x Copper Dust

**Other Materials:**
- Cobblestone → Gravel
- Gravel → Sand
- Stone → Cobblestone
- Wood Logs → 6x Planks

### Usage Tips
- Always process ores before smelting
- Can be automated with hoppers
- Combine with Ore Washer for maximum yield

## 💧 Ore Washer

Processes raw ores with water for bonus materials.

### Specifications
- **Power**: 30 FE/tick active, 5 FE/tick idle
- **Speed**: 5 seconds per operation
- **Energy**: 500 FE per item
- **Water**: 100 mB per operation

### Recipe
```
[I] [B] [I]
[G] [C] [G]
[I] [P] [I]

I = Iron Ingot
B = Bucket
G = Glass
C = Cauldron
P = Processor
```

### Processing Recipes

**Primary Outputs (100% chance):**
- Raw Titanium → Raw Titanium
- Raw Lithium → Raw Lithium
- Raw Uranium → Raw Uranium
- Raw Iron → Raw Iron
- Raw Gold → Raw Gold

**Bonus Outputs (10-25% chance):**
- Raw Titanium → Extra Raw Titanium (10%)
- Raw Lithium → Extra Raw Lithium (15%)
- Raw Uranium → Extra Raw Uranium (5%)
- Raw Iron → Raw Gold Nuggets (10%)
- Raw Gold → Raw Iron Nuggets (10%)

### Water Supply
- Fill manually with water buckets
- Connect fluid pipes from water source
- Internal tank holds 4,000 mB (40 operations)

### Optimal Setup
```
[Water Tank]
     |
[Ore Washer] → [Material Processor] → [Furnace]
```

## 🔧 Component Assembler

Crafts advanced components for tech progression.

### Specifications
- **Power**: 40 FE/tick active, 10 FE/tick idle
- **Speed**: 15 seconds per craft
- **Energy**: 2,000 FE per item

### Recipe
```
[I] [A] [I]
[C] [T] [C]
[I] [P] [I]

I = Iron Ingot
A = Advanced Processor
C = Circuit Board
T = Crafting Table
P = Piston
```

### Crafting Recipes

**Basic Components:**
- Circuit Board: Redstone + Green Dye + Iron
- Processor: Circuit Board + Gold + Redstone
- Energy Core: Redstone Block + Gold + Iron

**Advanced Components:**
- Advanced Processor: Processor + Diamond + Redstone Block
- Storage Processor: Processor + Lapis + Redstone
- Drone Core: Advanced Processor + Ender Pearl + Gold

**Machine Parts:**
- Storage Housing: Iron + Glass + Redstone
- Plasma Injector: Advanced Processor + Blaze Rod + Gold
- Fusion Core: Nether Star + Advanced Processor + Energy Core

### Automation Tips
- Queue up to 64 items per slot
- Use Export Bus to supply materials
- Import Bus collects finished products

## 🔥 Industrial Furnace (Multiblock)

High-speed bulk smelting system.

### Structure (3x3x3)
```
Layer 1 (Bottom):    Layer 2 (Middle):    Layer 3 (Top):
[C] [C] [C]          [C] [F] [C]          [C] [C] [C]
[C] [C] [C]          [F] [A] [F]          [C] [C] [C]
[C] [C] [C]          [C] [F] [C]          [C] [C] [C]

C = Furnace Casing
F = Industrial Furnace Controller (any face)
A = Air (empty space)
```

### Specifications
- **Power**: 80 FE/tick active, 20 FE/tick idle
- **Speed**: 2.5 seconds per item (4x faster)
- **Energy**: 500 FE per item
- **Batch Size**: Processes 4 items simultaneously

### Building Instructions
1. Place 26 Furnace Casing blocks in a 3x3x3 hollow cube
2. Replace any middle-layer casing with the Controller
3. Right-click Controller to form the structure
4. Supply power to the Controller

### Advantages
- 4x faster than regular furnace
- Processes 4 items at once
- More energy efficient per item
- Bulk processing capability

## 📊 Processing Chains

### Optimal Ore Processing
```
Raw Ore → Ore Washer → Material Processor → Furnace
         ↓ (10% chance)    ↓ (2x dust)        ↓ (ingots)
      Bonus Raw Ore    Titanium Dust    Titanium Ingot
```

**Yield Comparison:**
- Direct Smelting: 1 ore → 1 ingot
- With Processor: 1 ore → 2 ingots
- With Washer + Processor: 1 ore → 2.1 ingots average

### Component Production Line
```
Basic Materials → Component Assembler → Advanced Components
                        ↓                        ↓
                  Circuit Board           Advanced Processor
                     Processor              Storage Parts
                   Energy Core              Drone Components
```

### Automation Example
```
[Storage] → [Export Bus] → [Ore Washer] → [Material Processor]
                               ↓               ↓
                          [Import Bus]    [Import Bus]
                               ↓               ↓
                          [Storage] ←←←←← [Storage]
```

## 🔋 Power Requirements

### Machine Power Summary
| Machine | Idle | Active | Per Operation |
|---------|------|--------|---------------|
| Material Processor | 5 FE/t | 20 FE/t | 1,000 FE |
| Ore Washer | 5 FE/t | 30 FE/t | 500 FE |
| Component Assembler | 10 FE/t | 40 FE/t | 2,000 FE |
| Industrial Furnace | 20 FE/t | 80 FE/t | 500 FE |

### Planning Power Needs
- Calculate active power for all machines
- Add 20% buffer for idle drain
- Include storage system overhead
- Plan for future expansion

## 🛠️ Maintenance & Tips

### General Machine Tips
1. **Power First**: Ensure adequate power before building
2. **Buffer Storage**: Keep Energy Storage blocks nearby
3. **Input/Output**: Most machines have sided inventory
4. **Automation**: Use hoppers or import/export buses
5. **Efficiency**: Process in batches to minimize idle time

### Troubleshooting

**Machine Not Working:**
- Check power connection
- Verify correct recipe/materials
- Ensure output slot isn't full
- Check for redstone signals

**Slow Processing:**
- Insufficient power supply
- Multiple machines sharing power
- Add more generators
- Upgrade to Industrial Furnace

**Automation Issues:**
- Check side configuration
- Verify hopper/pipe connections
- Ensure storage has space
- Check import/export bus filters

## 🚀 Optimization Strategies

### Early Game
1. Focus on Material Processor first
2. Manual operation is fine initially
3. One generator can power one machine
4. Process ores before smelting always

### Mid Game
1. Add Ore Washer for bonus materials
2. Begin basic automation with hoppers
3. Build Component Assembler for tech items
4. Consider Industrial Furnace for bulk processing

### Late Game
1. Full automation with storage integration
2. Dedicated processing lines per material
3. Parallel processing arrays
4. Prepare for advanced machines (future updates)

---

*Tip: Efficient machine setups are the backbone of progression. Plan your processing carefully!*