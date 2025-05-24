# Changelog

All notable changes to AstroExpansion will be documented in this file.

## [1.0.10] - 2024-01-24

### 🚀 Added
- **Space Suit Crafting Recipes**
  - Space Helmet: 3x Titanium Ingot + 1x Glass
  - Space Chestplate: 5x Titanium Ingot + 1x Titanium Plate + 1x Energy Core
  - Space Leggings: 6x Titanium Ingot
  - Space Boots: 4x Titanium Ingot
  
- **Oxygen System Recipes**
  - Oxygen Tank: 4x Titanium Ingot + 1x Glass
  - Oxygen Canister: 4x Iron Ingot + 1x Glass (crafts 4x)

### 📚 Documentation
- Added comprehensive Storage System Setup Guide
- Detailed power requirements and troubleshooting steps

## [1.0.9] - 2024-01-24
*Skipped - GitHub Actions auto-incremented to 1.0.10*

## [1.0.8] - 2024-01-24

### 🚀 Added
- **Rocket Component Recipes**
  - Rocket Engine: Titanium + Energy Core + Fusion Core
  - Rocket Fuel Tank: Titanium Plates + Titanium Ingots
  - Rocket Hull: 8x Titanium Plate + 1x Circuit Board
  - Rocket Nose Cone: Titanium + Advanced Processor
  - Rocket Assembly Controller: Quantum Processor + Energy Cores + Titanium Blocks
  - Launch Pad: Obsidian + Titanium Blocks + Stone (crafts 4x)
  - Assembly Frame: Titanium Ingots + Plates (crafts 2x)

### 🌐 Localization
- Added English translations for all rocket-related blocks

## [1.0.7] - 2024-01-24
*Released as 1.0.8*

## [1.0.6] - 2024-01-23

### 🐛 Fixed
- **Critical**: Fixed duplicate ResearchCapability registration crash
  - Removed manual registration in CapabilityEvents
  - @AutoRegisterCapability now handles registration automatically
  - Fixes "Cannot register a capability implementation multiple times" error

## [1.0.5] - 2024-01-23

### ✨ Added
- **3 Missing GUI Screens**
  - ResearchTerminalScreen with research point display and clickable items
  - FuelRefineryScreen with fluid tank visualization
  - RocketAssemblyScreen with launch button and status checklist
  
- **GUI Features**
  - Research Terminal: View points, available researches, click to unlock
  - Fuel Refinery: Oil/fuel tank levels, processing progress
  - Rocket Assembly: Launch checklist, fuel gauge, component validation

### 🔧 Fixed
- Added missing methods to BlockEntities for GUI functionality
- Created all required GUI textures

## [1.0.4] - 2024-01-23

### 🐛 Fixed
- Fixed QuantumComputerPattern compilation error
- Changed quantum_processor block reference to quantum_casing
- Resolved build failure after duplicate registration fix

## [1.0.3] - 2024-01-23

### 🐛 Fixed
- **Storage Terminal Issues**
  - Added missing ticker to StorageTerminalBlock
  - Fixed network scanning functionality
  - Terminals now properly detect Storage Cores
  - Added debug logging for troubleshooting

### 📚 Added
- Storage Terminal Troubleshooting Guide

## [1.0.2] - 2024-01-23

### 🐛 Fixed
- Fixed duplicate registration error for moon_dust
- Renamed item to moon_dust_item to avoid conflicts

## [1.0.1] - 2024-01-23

### 🎮 Major Features Completed

#### Phase 1: Core Systems
- ✅ **31 Missing Block Textures Generated**
- ✅ **24 Missing Item Textures Generated**
- ✅ **Rocket Launch System** - Complete dimension teleportation
- ✅ **Research System** - Player capabilities and point tracking
- ✅ **Configuration System** - ForgeConfigSpec implementation

#### Content Added
- **Machines**: Basic Generator, Material Processor, Ore Washer, Component Assembler
- **Storage System**: Storage Core, Storage Terminal, Import/Export Bus
- **Multiblocks**: Industrial Furnace, Fusion Reactor, Quantum Computer, Fuel Refinery
- **Space Travel**: Rocket Assembly, Launch Pads, Teleporters
- **Drones**: Mining, Construction, Farming, Combat, Logistics
- **Resources**: Titanium, Lithium, Uranium ores and processing

### 🔧 Technical Improvements
- Fixed all compilation errors
- Implemented proper energy networks
- Added player research capabilities
- Created comprehensive recipe system

## [1.0.0] - 2024-01-23

### 🚀 Initial Release
- Minecraft 1.20.1 + Forge 47.2.0 compatibility
- Complete space exploration mod framework
- Advanced technology and automation systems
- Digital storage networks
- Multiblock structures
- Custom dimensions (Space, Moon)
- Full JEI integration ready

---

## Feature Categories

### 🏭 Machines & Processing
- Basic Generator (Coal → FE)
- Material Processor (Ore → Dust)
- Ore Washer (Raw Ore → 2x Output)
- Component Assembler (Advanced Crafting)
- Solar Panels (Daylight → FE)

### 💾 Digital Storage
- Storage Core (Network Controller)
- Storage Terminal (Access Point)
- Storage Drives (1k, 4k, 16k, 64k)
- Import/Export Bus (Automation)

### 🔬 Multiblock Structures
- Industrial Furnace (3x3x3)
- Fusion Reactor (5x5x5)
- Quantum Computer (7x7x7)
- Fuel Refinery (5x5x7)
- Rocket Assembly Platform

### 🚀 Space Exploration
- Space Suit (Full Set)
- Oxygen System
- Rocket Parts & Assembly
- Space/Moon Dimensions
- Lunar Rover Vehicle

### 🤖 Automation
- 5 Drone Types
- Drone Dock (Charging Station)
- Energy Conduits
- Fluid Pipes

### ⚗️ Resources
- Titanium (Overworld)
- Lithium (Overworld)
- Uranium (Deep Caves)
- Helium-3 (Moon)
- Moon Dust (Moon)