# Drone Automation Guide

Drones are autonomous helpers that can perform various tasks without player intervention. Each drone type specializes in different activities.

## 🤖 Drone Overview

### Available Drone Types
- **Mining Drone** - Excavates ores and blocks
- **Construction Drone** - Builds structures from blueprints
- **Farming Drone** - Plants, tends, and harvests crops
- **Combat Drone** - Defends against hostile mobs
- **Logistics Drone** - Transports items between inventories

### Common Features
- Autonomous operation within range
- Returns to dock when idle or low battery
- Configurable work areas
- Upgradeable with modules (future feature)

## 🏗️ Drone Dock

The Drone Dock is the home base for all drones.

### Recipe
```
[I] [A] [I]
[C] [E] [C]
[I] [R] [I]

I = Iron Ingot
A = Advanced Processor
C = Circuit Board
E = Energy Core
R = Redstone Block
```

### Specifications
- **Power Storage**: 50,000 FE
- **Charging Rate**: 100 FE/tick
- **Drone Capacity**: 1 drone per dock
- **Range**: 32 blocks radius (configurable)

### Setup Instructions
1. Place Drone Dock and supply power
2. Right-click to open interface
3. Place drone in dock slot
4. Configure work area and parameters
5. Activate drone with button

## ⛏️ Mining Drone

Automated mining within designated areas.

### Recipe
```
[I] [D] [I]
[P] [C] [P]
[I] [T] [I]

I = Iron Ingot
D = Diamond Pickaxe
P = Drone Propeller
C = Drone Core
T = Drone Shell
```

### Capabilities
- Mines all blocks except bedrock
- Collects drops automatically
- Returns when inventory full
- Avoids lava and water hazards
- Respects claim/protection mods

### Configuration
- **Mode**: Surface, Tunnel, or Quarry
- **Depth**: How deep to mine (Y-level)
- **Pattern**: Spiral, Grid, or Random
- **Filter**: Whitelist/Blacklist blocks

### Mining Patterns

**Surface Mode:**
- Clears top layers only
- Good for clearing land
- Preserves underground

**Tunnel Mode:**
- 3x3 horizontal tunnels
- Branch mining pattern
- Efficient ore finding

**Quarry Mode:**
- Complete excavation
- Layer by layer removal
- Maximum resource extraction

## 🏠 Construction Drone

Builds structures from provided materials.

### Recipe
```
[I] [B] [I]
[P] [C] [P]
[I] [T] [I]

I = Iron Ingot
B = Block Placer (Dispenser + Piston)
P = Drone Propeller
C = Drone Core
T = Drone Shell
```

### Capabilities
- Places blocks from inventory
- Follows simple blueprints
- Can build up to 16 blocks high
- Respects physics (gravity blocks)

### Blueprint System (Simplified)
1. Place ghost blocks to mark positions
2. Drone reads pattern
3. Supply matching materials
4. Drone builds structure

### Building Tips
- Start with simple shapes
- Ensure adequate materials
- Clear the building area first
- Use Mining Drone for excavation

## 🌾 Farming Drone

Automated crop management.

### Recipe
```
[I] [H] [I]
[P] [C] [P]
[I] [T] [I]

I = Iron Ingot
H = Diamond Hoe
P = Drone Propeller
C = Drone Core
T = Drone Shell
```

### Capabilities
- Plants seeds automatically
- Harvests mature crops
- Replants after harvest
- Manages multiple crop types
- Deposits harvest in dock

### Supported Crops
- Wheat, Carrots, Potatoes
- Beetroot, Melons, Pumpkins
- Sugar Cane (with water detection)
- Nether Wart (detects soul sand)
- Future: Tree farming

### Farm Management
- **Field Size**: Up to 32x32 blocks
- **Crop Rotation**: Can alternate crops
- **Bone Meal**: Uses if available (future)
- **Water Detection**: Won't plant without water

## ⚔️ Combat Drone

Perimeter defense system.

### Recipe
```
[I] [S] [I]
[P] [C] [P]
[I] [T] [I]

I = Iron Ingot
S = Diamond Sword
P = Drone Propeller
C = Drone Core
T = Drone Shell
```

### Capabilities
- Attacks hostile mobs
- 10 hearts health
- 5 attack damage
- Returns when damaged
- Collects mob drops

### Combat Behavior
- **Patrol Mode**: Circles perimeter
- **Guard Mode**: Stays near dock
- **Hunt Mode**: Seeks targets actively
- **Passive**: Ignores peaceful mobs

### Target Priority
1. Zombies and Skeletons
2. Creepers (maintains distance)
3. Spiders (day/night aware)
4. Other hostile mobs
5. Never attacks players

## 📦 Logistics Drone

Item transportation specialist.

### Recipe
```
[I] [C] [I]
[P] [C] [P]
[I] [T] [I]

I = Iron Ingot
C = Chest
P = Drone Propeller
C = Drone Core
T = Drone Shell
```

### Capabilities
- Transfers items between inventories
- 18 slot internal storage
- Configurable routes
- Filter support
- Round-trip or one-way modes

### Route Configuration
1. Set pickup location
2. Set delivery location
3. Configure item filter
4. Choose transfer amount
5. Set schedule (continuous/on-demand)

### Use Cases
- Mining output collection
- Farm produce transport
- Fuel delivery to generators
- Distributed storage management
- Cross-base item transfer

## 🔧 Drone Components

### Drone Core
**Recipe:**
```
[G] [A] [G]
[E] [P] [E]
[G] [R] [G]

G = Gold Ingot
A = Advanced Processor
E = Ender Pearl
P = Eye of Ender
R = Redstone Block
```

### Drone Shell
**Recipe:**
```
[I] [I] [I]
[I] [G] [I]
[I] [I] [I]

I = Iron Ingot
G = Glass Pane
```

### Drone Propeller
**Recipe:**
```
    [I]
[I] [R] [I]
    [I]

I = Iron Ingot
R = Redstone
(Crafts 4 propellers)
```

## 📊 Drone Management

### Power Consumption
| Drone Type | Idle | Working | Per Operation |
|------------|------|---------|---------------|
| Mining | 5 FE/t | 20 FE/t | 100 FE/block |
| Construction | 5 FE/t | 15 FE/t | 50 FE/block |
| Farming | 3 FE/t | 10 FE/t | 20 FE/action |
| Combat | 5 FE/t | 25 FE/t | 200 FE/kill |
| Logistics | 3 FE/t | 10 FE/t | 50 FE/trip |

### Efficiency Tips
1. **Multiple Docks**: Use several drones for large operations
2. **Specialized Roles**: Each drone type for its purpose
3. **Power Planning**: Ensure adequate charging capacity
4. **Range Optimization**: Place docks centrally
5. **Maintenance**: Drones auto-repair at dock

## 🎯 Automation Examples

### Automated Mine
```
[Mining Drone] → [Collection Point] → [Logistics Drone] → [Storage]
      ↓
[Drone Dock] ← [Power Supply]
```

### Farm Complex
```
[Farming Drone] → [Harvest Storage]
      ↓                ↓
[Drone Dock]    [Logistics Drone] → [Processing]
      ↓
[Combat Drone] (Protection)
```

### Defense Network
```
[Combat Drone] ←→ [Combat Drone]
      ↓                ↓
[Drone Dock]     [Drone Dock]
      ↓                ↓
[Central Power Distribution]
```

## 🚀 Advanced Strategies

### Early Game
- Start with one Mining Drone
- Manual dock management
- Focus on resource gathering
- Simple automation only

### Mid Game
- Add Farming and Logistics Drones
- Create supply chains
- Automate material processing
- Expand operational range

### Late Game
- Full drone networks
- Complex routing systems
- Integrated with storage
- Prepared for space operations

## ⚠️ Limitations & Tips

### Current Limitations
- One drone per dock
- Fixed range (32 blocks)
- No inter-drone communication
- Cannot cross dimensions
- Basic pathfinding only

### Best Practices
1. **Clear Paths**: Remove obstacles for efficiency
2. **Lit Areas**: Prevent mob spawns in work zones
3. **Backup Power**: Drones stop without energy
4. **Regular Checks**: Monitor drone status
5. **Inventory Management**: Empty drones regularly

## 🔮 Future Features (Planned)

### Drone Upgrades
- Speed modules
- Inventory expansion
- Range extension
- Efficiency cores
- Special abilities

### New Drone Types
- **Repair Drone** - Fixes machines
- **Explorer Drone** - Maps new areas
- **Rescue Drone** - Retrieves items/player
- **Space Drone** - Orbital operations

### Advanced Features
- Drone networking
- Swarm coordination
- Remote control
- Programming interface
- Cross-dimensional travel

---

*Remember: Drones are tools to enhance, not replace, player interaction. Use them wisely to create your automated empire!*