# Digital Storage System Guide

The Digital Storage System is AstroExpansion's answer to mass item storage, allowing you to store thousands of items in a compact, searchable network.

## 📦 System Overview

The storage system consists of:
- **Storage Core** - The brain of your network
- **Storage Drives** - Hold your items digitally
- **Storage Terminal** - Access and manage items
- **Import/Export Buses** - Automate item flow

## 🏗️ Building Your First Storage Network

### ⚠️ Critical Setup Order
1. **Power FIRST** - Storage Core needs active power to form network
2. **Drive SECOND** - Insert at least one drive after powering
3. **Terminal LAST** - Only works with formed network

### Step 1: Craft the Storage Core

**Recipe:**
```
[I] [P] [I]
[C] [D] [C]
[I] [E] [I]

I = Iron Ingot
P = Storage Processor
C = Circuit Board
D = Diamond
E = Energy Core
```

The Storage Core is the heart of your network. Place it down and provide it with power (minimum 1,000 FE buffer).

### Step 2: Add Storage Drives

**Storage Drive Tiers:**
- **1k Drive** - Stores 1,024 items (63 types)
- **4k Drive** - Stores 4,096 items (63 types)
- **16k Drive** - Stores 16,384 items (63 types)
- **64k Drive** - Stores 65,536 items (63 types)

**Important:** Each drive can only store 63 different item types, regardless of capacity!

**1k Drive Recipe:**
```
[R] [P] [R]
[G] [H] [G]
[I] [I] [I]

R = Redstone
P = Storage Processor
G = Glass
H = Storage Housing
I = Iron Ingot
```

### Step 3: Insert Drives into Storage Core

**IMPORTANT: Network Formation Requirements**
The Storage Core requires TWO things to form a network:
1. **Power** - Must have energy stored (connect to power first!)
2. **At least one Storage Drive** - Network won't form without drives

**Steps to Form Network:**
1. Connect Storage Core to power source (Energy Conduit from generator)
2. Wait for power to charge (check if block has energy)
3. Right-click Storage Core to open GUI (always accessible as of v0.4.4)
4. Insert at least one Storage Drive in the drive slots
5. The network will form automatically within 1 second
6. The block appearance will change when network is formed

**Status Messages (v0.4.4+):**
- "Storage Core needs power!" - Connect to power source
- "Insert storage drives to form network!" - Add at least one drive
- No message - Network is formed and working!

### Step 4: Place Storage Terminal

**Recipe:**
```
[I] [S] [I]
[C] [G] [C]
[I] [P] [I]

I = Iron Ingot
S = Screen (Glass Pane + Glowstone)
C = Circuit Board
G = Glass
P = Storage Processor
```

Place the Storage Terminal adjacent to or connected via cable to the Storage Core.

### Complete Working Setup Example
```
[G] → [E] → [C] ← [T]
           ↑
         [Drive]

G = Generator (with fuel!)
E = Energy Storage (optional but recommended)
C = Storage Core (with drive inserted)
T = Storage Terminal
→ = Energy Conduit
```

**Setup Checklist:**
- [ ] Generator has fuel and is producing power
- [ ] Storage Core is connected to power via conduits
- [ ] Storage Core has at least one drive inserted
- [ ] Wait 1 second for network to form
- [ ] Storage Terminal is placed adjacent to Core
- [ ] Network formed! (Core appearance changes)

## 💡 How It Works

### Storage Mechanics
1. **Type Limits**: Each drive stores up to 63 different item types
2. **Capacity**: The number shown (1k, 4k, etc.) is total items, not types
3. **Power Usage**: 
   - Core: 10 FE/tick idle, 20 FE/tick active
   - Terminal: 5 FE/tick when in use
   - Each drive: 1 FE/tick

### Using the Terminal
1. Right-click to open the search interface
2. Type to search for items
3. Left-click to extract a stack
4. Right-click to extract a single item
5. Shift-click to extract all matching items

### Storage Priority
- Items go to drives with existing stacks first
- Then to the drive with the most free space
- Types are distributed across drives automatically

## 🔧 Import/Export Automation

### Import Bus
Automatically pulls items from adjacent inventories into the storage network.

**Recipe:**
```
[I] [H] [I]
[C] [P] [C]
[I] [A] [I]

I = Iron Ingot
H = Hopper
C = Circuit Board
P = Piston
A = Advanced Processor
```

**Configuration:**
- Place against any inventory (chest, machine output, etc.)
- Items are imported every 20 ticks (1 second)
- Requires 5 FE per operation

### Export Bus
Automatically pushes specified items from storage to adjacent inventories.

**Recipe:**
```
[I] [P] [I]
[C] [H] [C]
[I] [A] [I]

I = Iron Ingot
P = Piston
C = Circuit Board
H = Hopper
A = Advanced Processor
```

**Configuration:**
- Right-click to set filter (which items to export)
- Exports up to 64 items per operation
- Requires 5 FE per operation

## 📊 Network Management

### Power Requirements
- **Minimum**: 100 FE/tick for basic network
- **Recommended**: 500+ FE/tick for automation
- **Buffer**: At least 10,000 FE storage recommended

### Performance Tips
1. **Organize by Type**: Use dedicated drives for common items
2. **Avoid Overfilling**: Leave 10% space on each drive
3. **Use External Storage**: Keep bulk items in regular chests
4. **Partition Networks**: Large bases benefit from multiple small networks

### Troubleshooting

**"Storage network not formed!" Error:**
This is the most common issue! The network requires:
1. **Power connected and charged** - Connect Energy Conduit from generator
2. **At least one Storage Drive inserted** - No drives = no network
3. Wait a second after inserting drive for network to form

**Network Not Working:**
- Check power supply (must have energy stored, not just connected)
- Ensure Storage Core has at least one drive installed
- Verify all components are connected
- The Storage Core checks formation every second (20 ticks)

**Can't Store Items:**
- Check if drives are full (type limit reached)
- Ensure network has power
- Try removing and reinserting drives

**Terminal Not Connecting:**
- Must be within 16 blocks of Storage Core
- Check for obstructions in the connection path
- Ensure terminal is facing the correct direction

## 🎯 Advanced Tips

### Drive Management
- **Color Coding**: Future update will add drive labeling
- **Hot Swapping**: Drives can be removed without losing items
- **Backup Important Items**: Keep a separate drive for valuables

### Integration with Other Systems
- Connect Export Buses to machine inputs for automated processing
- Use Import Buses on machine outputs to collect products
- Combine with Drone Docks for wireless item transport

### Optimal Setups

**Early Game (Basic Network):**
```
[T] [C] [E]
    [G]

T = Terminal
C = Storage Core (with 1-2 1k drives)
E = Energy Storage
G = Basic Generator
```

**Mid Game (Automated Processing):**
```
[I] [M] [E]
[T] [C] [E]
    [G] [G]

I = Import Bus (on Material Processor output)
M = Material Processor
E = Export Bus (configured for raw ores)
T = Terminal
C = Storage Core (with 4k drives)
G = Basic Generators
```

**Late Game (Full Automation):**
- Multiple Storage Cores for different systems
- Dedicated drives for each material type
- Import/Export buses on all machines
- Drone integration for remote operations

## 🔮 Future Features (Planned)

- **Wireless Access Terminal** - Access storage from anywhere
- **Storage Monitor** - Display item counts
- **Crafting Terminal** - Craft using network items
- **Fluid Storage** - Store liquids digitally
- **P2P Tunnels** - Long-range connections

---

*Remember: The storage system is meant to complement, not replace, traditional storage. Keep emergency supplies in regular chests!*