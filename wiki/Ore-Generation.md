# Ore Generation Guide

AstroExpansion adds three new ores to world generation, each with specific spawn conditions and uses.

## Titanium Ore

### Generation Details
- **Y-Range:** -64 to +20
- **Vein Size:** 3-8 blocks
- **Frequency:** Common (especially below Y=0)
- **Biomes:** All overworld biomes

### Variants
- **Titanium Ore** (Y=0 to Y=20) - Found in stone
- **Deepslate Titanium Ore** (Y=-64 to Y=0) - Found in deepslate

### Mining
- **Required Tool:** Iron pickaxe or better
- **Fortune:** Affects raw titanium drops
- **Silk Touch:** Drops ore block

### Uses
- Foundation of all technology
- Required for most machines
- Used in titanium plates and blocks

## Lithium Ore

### Generation Details
- **Y-Range:** -32 to +32
- **Vein Size:** 4-9 blocks
- **Frequency:** Uncommon
- **Biomes:** All overworld biomes

### Variants
- **Lithium Ore** (Y=0 to Y=32) - Found in stone
- **Deepslate Lithium Ore** (Y=-32 to Y=0) - Found in deepslate

### Mining
- **Required Tool:** Iron pickaxe or better
- **Fortune:** Affects raw lithium drops
- **Silk Touch:** Drops ore block

### Uses
- Energy storage components
- Battery crafting
- Advanced circuits

## Uranium Ore

### Generation Details
- **Y-Range:** -64 to -16
- **Vein Size:** 1-4 blocks
- **Frequency:** Rare
- **Biomes:** All overworld biomes
- **Special:** Only spawns in deepslate

### Variants
- **Deepslate Uranium Ore** only

### Mining
- **Required Tool:** Diamond pickaxe or better
- **Fortune:** Affects raw uranium drops
- **Silk Touch:** Drops ore block
- **Warning:** Radioactive! (No actual damage in current version)

### Uses
- Nuclear fuel production
- Advanced energy systems
- Quantum technology

## Moon Ores

### Helium-3 Ore
- **Dimension:** Moon only
- **Y-Range:** 0 to 64
- **Vein Size:** 2-5 blocks
- **Frequency:** Common on moon
- **Uses:** Fusion fuel, advanced technology

## Ore Processing Tips

### Maximum Yield Strategy
1. **Mine with Fortune III** pickaxe
2. **Ore Wash** raw ores for bonus dust
3. **Store excess** raw ores for later processing

### Processing Ratios
- **Direct Smelting:** 1 raw ore → 1 ingot
- **Material Processor:** 1 raw ore → 2 dust → 2 ingots
- **Ore Washer:** 1 raw ore → 2-3 dust (RNG) → 2-3 ingots

### Early Game Strategy
1. Mine titanium first (most common)
2. Set up basic processing
3. Target lithium for energy storage
4. Save uranium for later phases

### Mining Efficiency

#### Best Y-Levels
- **Titanium:** Y=-16 (high concentration)
- **Lithium:** Y=0 (balanced spawn rate)
- **Uranium:** Y=-40 (only option)

#### Branch Mining Pattern
```
  [T] = Tunnel
  [B] = Branch
  
  [T][T][T][T][T]
  [B]   [B]   [B]
  [B]   [B]   [B]
  [B]   [B]   [B]
```
Space branches 3 blocks apart for maximum coverage.

## Ore Distribution Heatmap

### Titanium Distribution
```
Y  +20 |▓▓░░░░░░░░| Rare
Y    0 |▓▓▓▓▓▓░░░░| Common
Y  -16 |▓▓▓▓▓▓▓▓▓▓| Very Common
Y  -32 |▓▓▓▓▓▓▓▓░░| Common
Y  -64 |▓▓▓▓░░░░░░| Uncommon
```

### Lithium Distribution
```
Y  +32 |▓▓░░░░░░░░| Rare
Y  +16 |▓▓▓▓░░░░░░| Uncommon
Y    0 |▓▓▓▓▓▓░░░░| Common
Y  -16 |▓▓▓▓▓▓░░░░| Common
Y  -32 |▓▓░░░░░░░░| Rare
```

### Uranium Distribution
```
Y  -16 |▓░░░░░░░░░| Very Rare
Y  -32 |▓▓▓░░░░░░░| Rare
Y  -40 |▓▓▓▓▓░░░░░| Uncommon
Y  -48 |▓▓▓▓░░░░░░| Rare
Y  -64 |▓▓░░░░░░░░| Very Rare
```

## Advanced Mining

### Vein Miner Integration
- Works with all AstroExpansion ores
- Recommended for large titanium veins

### Digital Miner Setup
- Configure for deepslate variants
- Filter by ore type for targeted mining

### Mining Drone Usage
- Requires Drone Dock
- Program for specific Y-levels
- Automatic ore collection

---

See also: [Ore Processing](Ore-Processing) | [Material Processor](Material-Processor) | [Ore Washer](Ore-Washer)