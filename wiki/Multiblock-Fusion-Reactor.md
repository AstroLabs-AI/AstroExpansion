# Fusion Reactor Multiblock Guide

The Fusion Reactor is a 5x5x5 multiblock structure that generates massive amounts of power through nuclear fusion.

## Overview

- **Size:** 5x5x5
- **Power Generation:** 100,000 FE/tick
- **Fuel:** Deuterium + Tritium
- **Startup Energy:** 1,000,000 FE
- **Self-Sustaining:** Yes (after startup)

## Required Blocks

- **1x** Fusion Reactor Controller
- **12x** Fusion Coils (for edges)
- **98x** Fusion Reactor Casing
- **2x** Fusion Core Blocks

## Structure Layout

### Layer 1 (Bottom) - Y=0
```
C C C C C
C S S S C
C S S S C
C S S S C
C C C C C

C = Casing
S = Casing (except center which is Core Block)
```

### Layer 2 - Y=1
```
C F C F C
F - - - F
C - - - C
F - - - F
C F C F C

C = Casing
F = Fusion Coil
- = Air (empty)
```

### Layer 3 (Middle) - Y=2
```
C F R F C
F - - - F
C - - - C
F - - - F
C F C F C

R = Reactor Controller (front center)
C = Casing
F = Fusion Coil
- = Air (empty)
```

### Layer 4 - Y=3
```
C F C F C
F - - - F
C - - - C
F - - - F
C F C F C

(Same as Layer 2)
```

### Layer 5 (Top) - Y=4
```
C C C C C
C S S S C
C S S S C
C S S S C
C C C C C

C = Casing
S = Casing (except center which is Core Block)
```

## Building Instructions

1. **Start with the base** (Layer 1)
   - Place 5x5 platform of Fusion Reactor Casing
   - Replace center block with Fusion Core Block

2. **Build the walls** (Layers 2-4)
   - Place casings at corners and edge centers
   - Place Fusion Coils at edge corners (not top/bottom layers)
   - Leave center 3x3 hollow

3. **Place the controller** (Layer 3)
   - Replace front-center casing with Fusion Reactor Controller
   - Controller must face outward

4. **Complete the top** (Layer 5)
   - Mirror the bottom layer
   - Center block is second Fusion Core Block

## Fuel Production

### Deuterium
- Produced from water in Fuel Refinery
- 1000mB water → 100mB Deuterium
- Requires 200 FE/tick

### Tritium
- Produced from Lithium in specialized setup
- Requires neutron bombardment
- Advanced process (Phase 5+)

## Operation

### Startup Sequence
1. Ensure 1M FE available in connected storage
2. Supply Deuterium to top input
3. Supply Tritium to bottom input
4. Right-click controller to start
5. Reactor will consume startup energy
6. Plasma ignition begins
7. Power generation starts

### Safety Features
- Automatic shutdown on fuel depletion
- No explosion risk (unlike fission)
- Contained plasma field
- Safe to build near base

## Power Output

- **Base Output:** 100,000 FE/tick
- **Fuel Consumption:** 10mB/tick each
- **Efficiency:** 100% when properly fueled
- **Total Daily Output:** 144M FE (if run continuously)

## Optimization Tips

1. **Fuel Buffer**
   - Use Fluid Tanks for fuel storage
   - Maintain 10,000mB minimum of each fuel

2. **Power Distribution**
   - Connect multiple Energy Conduits
   - Use high-capacity storage nearby
   - Distribute to sub-networks

3. **Automation**
   - Auto-import fuel via pipes
   - Monitor with Redstone Comparator
   - Set up auto-restart system

## Troubleshooting

### Reactor Won't Start
- Check structure completeness (all blocks placed correctly)
- Verify 1M FE available
- Ensure both fuels present
- Controller facing outward

### No Power Generation
- Check fuel levels
- Verify startup completed
- Ensure output is connected

### Structure Invalid
- Use wrench on controller for error message
- Common issues:
  - Missing Fusion Coils
  - Wrong block types
  - Incorrect Core Block placement

## Integration Examples

### With Storage System
```
[Fusion Reactor] → [Energy Conduits] → [Energy Storage Array]
                                          ↓
                                    [Distribution Network]
```

### With Other Machines
```
[Water Source] → [Fuel Refinery] → [Deuterium]
                                        ↓
                                 [Fusion Reactor]
                                        ↑
[Lithium Processing] → [Tritium Production]
```

## Fun Facts

- Generates enough power for 100+ basic machines
- One reactor can power an entire base
- Based on real tokamak fusion designs
- Most powerful single energy source in the mod

---

See also: [Fuel Refinery](Fuel-Refinery) | [Energy Systems](Energy-Systems) | [Multiblock Structures](Multiblock-Index)