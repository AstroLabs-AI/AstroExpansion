# Digital Storage System Guide

The Digital Storage System provides massive item storage in a compact, searchable network.

## System Components

### Storage Core
- **Function:** Network controller
- **Storage:** Houses up to 8 drives
- **Power:** 20 FE/tick base + 5 FE/tick per drive
- **Range:** Unlimited with cables

### Storage Drives
| Drive Type | Capacity | Items | Types |
|------------|----------|-------|-------|
| 1k Drive   | 1,024    | 1,024 | 63    |
| 4k Drive   | 4,096    | 4,096 | 63    |
| 16k Drive  | 16,384   | 16,384| 63    |
| 64k Drive  | 65,536   | 65,536| 63    |

### Storage Terminal
- **Function:** Access point for items
- **Features:** Search, sort, craft
- **Power:** 10 FE/tick when active

### Import Bus
- **Function:** Pulls items into network
- **Speed:** 1 stack/second
- **Power:** 5 FE/tick
- **Filter:** 9 slots

### Export Bus
- **Function:** Pushes items out of network
- **Speed:** 1 stack/second  
- **Power:** 5 FE/tick
- **Filter:** 9 slots

## Setting Up Your First Network

### Basic Setup
```
[C] = Storage Core
[T] = Storage Terminal
[I] = Import Bus
[E] = Export Bus
[=] = Cable

[Chest] ← [I]===[C]===[T]
                 |
                [E] → [Machine]
```

### Step-by-Step
1. **Place Storage Core**
   - Needs power connection
   - Right-click to open

2. **Insert Storage Drives**
   - Start with 1k drives
   - Upgrade as needed

3. **Connect Terminal**
   - Use cables or direct connection
   - Must be powered

4. **Add Import/Export**
   - Import from chests
   - Export to machines

## Network Cables

### Types
- **Basic Cable:** Standard connection
- **Covered Cable:** Decorative variant
- **Smart Cable:** Shows channel usage (future feature)

### Cable Management
- Cables connect automatically
- Right-click with wrench to disconnect
- Can run through walls
- No distance limit

## Advanced Features

### Storage Priority
- Higher priority fills first
- Set in Storage Core GUI
- Useful for overflow systems

### Partitioning
- Limit drive to specific items
- Prevents drive overflow
- Optimal type usage

### Crafting Integration
- Request crafting from terminal
- Auto-craft with patterns (future)
- Track crafting progress

## Storage Capacity Explained

### Items vs Types
- **Items:** Total quantity stored
- **Types:** Different item types
- Each drive limited to 63 types
- Plan accordingly!

### Optimal Usage
- **Bulk Storage:** Few types, many items (e.g., cobblestone)
- **Diverse Storage:** Many types, few items (e.g., tools)
- Mix drive sizes for efficiency

## Network Layouts

### Centralized
```
        [T]
         |
[I]--[C]--[E]
         |
        [T]
```

### Distributed
```
[T]--[Cable Network]--[T]
         |
        [C]
         |
[I]--[Machines]--[E]
```

### Subnetworks
```
[Main Core]====[Subnet Core]
    |               |
[Storage]      [Processing]
```

## Power Management

### Energy Usage
- Base: 20 FE/tick (Core)
- Per Drive: +5 FE/tick
- Per Terminal: +10 FE/tick
- Per Bus: +5 FE/tick

### Calculation Example
- 1 Core + 4 Drives + 2 Terminals + 4 Buses
- = 20 + (4×5) + (2×10) + (4×5)
- = 80 FE/tick

## Tips & Tricks

### Organization
1. **Label chests** feeding import buses
2. **Use multiple cores** for organization
3. **Separate networks** by function
4. **Color-code cables** (with dyes)

### Performance
1. **Minimize buses** - they use constant power
2. **Turn off unused** terminals
3. **Use appropriate** drive sizes
4. **Partition drives** to prevent overflow

### Automation
1. **Stock keeping** - Export bus with redstone
2. **Overflow prevention** - Priority + partition
3. **Auto-sorting** - Import everything, export specific
4. **Buffer chests** - Between machines

## Common Setups

### Mining Network
```
[Quarry] → [Import Bus] → [Core] → [Export Bus] → [Ore Processing]
```

### Farming Network
```
[Farm] → [Import] → [Core] → [Export] → [Composters/Cookers]
```

### Workshop Network
```
[Crafting Area] ← [Terminal]
                      |
                   [Core]
                      |
              [Resource Storage]
```

## Troubleshooting

### No Power
- Check energy connections
- Verify generator output
- Add energy storage buffer

### Items Not Importing
- Check import bus connection
- Verify network has space
- Ensure bus has power

### Can't Access Items
- Terminal must be powered
- Check cable connections
- Verify core has drives

### Network Full
- Add more drives
- Check type limits (63 per drive)
- Use overflow system

## Integration with Other Mods

### Applied Energistics 2
- Similar concepts
- Cannot interconnect
- Choose one system

### Refined Storage
- Similar functionality
- Separate networks
- Different recipes

### Drawer Controllers
- Great for bulk items
- Connect via import bus
- Reduces type usage

---

See also: [Import Export Bus](Import-Export-Bus) | [Automation Setups](Automation-Setups) | [Power Systems](Energy-Systems)