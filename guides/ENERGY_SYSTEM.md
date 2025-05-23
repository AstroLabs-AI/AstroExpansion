# Energy System Guide

AstroExpansion uses Forge Energy (FE) as its power system. This guide covers generation, storage, and distribution of energy.

## ⚡ Energy Basics

### Units
- **FE** = Forge Energy (same as RF - Redstone Flux)
- **FE/t** = Forge Energy per tick (20 ticks = 1 second)
- Compatible with most tech mods using FE/RF

### Energy Flow
1. **Generators** produce energy
2. **Energy Storage** blocks buffer energy
3. **Energy Conduits** transport energy
4. **Machines** consume energy

## 🔥 Power Generation

### Basic Generator
Your first and most reliable power source.

**Recipe:**
```
[I] [F] [I]
[I] [R] [I]
[C] [C] [C]

I = Iron Ingot
F = Furnace
R = Redstone Block
C = Cobblestone
```

**Specifications:**
- **Output**: 40 FE/tick
- **Fuel**: Any furnace fuel (coal, wood, etc.)
- **Buffer**: 10,000 FE internal storage
- **Efficiency**: 1 Coal = 1,600 FE total

**Fuel Values:**
- Coal/Charcoal: 40 seconds (32,000 FE)
- Coal Block: 360 seconds (288,000 FE)
- Wood/Planks: 7.5 seconds (6,000 FE)
- Sticks: 2.5 seconds (2,000 FE)

### Future Generators (Planned)
- **Solar Generator** - Passive daytime generation
- **Geothermal Generator** - Uses lava
- **Nuclear Reactor** - High output, requires Uranium
- **Fusion Reactor** - End-game power

## 🔋 Energy Storage

### Energy Storage Block
Essential for any power network.

**Recipe:**
```
[I] [R] [I]
[R] [E] [R]
[I] [R] [I]

I = Iron Ingot
R = Redstone
E = Energy Core
```

**Specifications:**
- **Capacity**: 100,000 FE
- **Max Input**: 1,000 FE/tick
- **Max Output**: 1,000 FE/tick
- **Loss**: None (100% efficient)

**Usage Tips:**
- Place between generators and machines
- Acts as a buffer for power spikes
- Multiple blocks increase total capacity
- Shows charge level visually

## 🔌 Energy Distribution

### Energy Conduits
Transport energy between blocks.

**Recipe:**
```
[I] [R] [I]

I = Iron Ingot
R = Redstone
(Crafts 8 conduits)
```

**Specifications:**
- **Transfer Rate**: 1,000 FE/tick per connection
- **Loss**: None over any distance
- **Connections**: Up to 6 sides
- **Smart Routing**: Automatically finds shortest path

**Connection Rules:**
1. Conduits connect to all adjacent energy blocks
2. Form networks automatically
3. Energy flows from producers to consumers
4. Prioritizes storage blocks when available

## 📊 Power Networks

### Basic Setup
```
[G] → [S] → [M]

G = Generator
S = Storage
M = Machine
→ = Conduit
```

### Expanded Network
```
[G] → [S] → [C] → [M]
[G] ↗     ↘ [C] → [M]
            [C] → [M]

Multiple generators feed storage,
which distributes to machines
```

### Power Priority
1. **Machines** actively processing
2. **Energy Storage** blocks (charging)
3. **Idle machines** (maintaining buffer)
4. **Export buses** and automation

## 🏭 Machine Power Usage

### Current Machines

**Material Processor**
- Idle: 5 FE/tick
- Active: 20 FE/tick
- Per Operation: 1,000 FE

**Ore Washer**
- Idle: 5 FE/tick
- Active: 30 FE/tick
- Per Operation: 500 FE

**Component Assembler**
- Idle: 10 FE/tick
- Active: 40 FE/tick
- Per Operation: 2,000 FE

**Industrial Furnace** (Multiblock)
- Idle: 20 FE/tick
- Active: 80 FE/tick
- Per Operation: 500 FE (but 4x faster)

**Storage Network**
- Core: 10 FE/tick base + 1 per drive
- Terminal: 5 FE/tick when active
- Import/Export Bus: 5 FE per operation

## 💡 Power Management Tips

### Early Game
1. **One Generator Start**: Begin with a single Basic Generator
2. **Coal Efficiency**: Use coal blocks for 9x burn time
3. **Manual Feeding**: Check fuel levels regularly
4. **Storage First**: Build Energy Storage before adding machines

### Mid Game
1. **Multiple Generators**: 2-3 generators for consistent power
2. **Automated Fuel**: Use hoppers to auto-feed generators
3. **Dedicated Networks**: Separate power for different systems
4. **Monitor Usage**: Watch storage levels to identify needs

### Late Game
1. **Generator Arrays**: Banks of generators for massive power
2. **Smart Distribution**: Use conduits efficiently
3. **Overflow Protection**: Excess storage for peak demands
4. **Future Proof**: Plan for Fusion Reactor placement

## 🔧 Troubleshooting

### No Power Flow
- Check all connections are touching
- Verify generator has fuel
- Ensure machines aren't full
- Look for breaks in conduit lines

### Insufficient Power
- Add more generators
- Upgrade to better fuel sources
- Add more storage blocks
- Optimize machine usage

### Power Drain
- Identify high-consumption machines
- Use redstone signals to disable when not needed
- Separate critical and non-critical systems
- Add dedicated generators for heavy users

## 📈 Power Calculations

### Example Setup Requirements

**Small Workshop** (Early Game)
- 1 Material Processor: 20 FE/t
- 1 Basic Generator: 40 FE/t output
- 1 Energy Storage: 100k FE buffer
- **Result**: 20 FE/t surplus for growth

**Processing Facility** (Mid Game)
- 2 Material Processors: 40 FE/t
- 1 Ore Washer: 30 FE/t
- Storage Network: 15 FE/t
- **Total Need**: 85 FE/t
- **Solution**: 3 Basic Generators (120 FE/t)

**Automated Factory** (Late Game)
- Full machine array: 200 FE/t
- Storage systems: 50 FE/t
- Drone operations: 100 FE/t
- **Total Need**: 350 FE/t
- **Solution**: 9 Basic Generators or wait for Fusion

## 🚀 Optimization Strategies

### Fuel Efficiency
1. **Batch Processing**: Run machines in batches to minimize idle drain
2. **Scheduled Operations**: Use redstone timers for periodic processing
3. **Fuel Upgrades**: Progress from wood → coal → coal blocks
4. **Smart Storage**: Keep fuel reserves for continuous operation

### Network Design
1. **Star Topology**: Central storage with radiating connections
2. **Minimal Conduits**: Direct connections where possible
3. **Segmentation**: Separate networks for different areas
4. **Redundancy**: Multiple paths prevent single point failures

### Future Preparation
- Reserve space for advanced generators
- Plan conduit routing for expansion
- Build excess storage capacity early
- Design with automation in mind

---

*Remember: Power is the lifeblood of your technological empire. Plan your energy infrastructure carefully!*