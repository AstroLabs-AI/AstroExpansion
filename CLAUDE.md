# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Astro Expansion is a comprehensive space technology and exploration mod for Minecraft NeoForge 1.21.5. The mod transforms Minecraft into a sci-fi sandbox with advanced automation, space travel, and cosmic mysteries. Built using Gradle and requires Java 21.

## Key Commands

```bash
# Set Java 21 if needed
export JAVA_HOME=/home/codespace/java/21.0.6-ms && export PATH=$JAVA_HOME/bin:$PATH

# Build the mod
./gradlew build

# Run Minecraft with the mod
./gradlew runClient      # Run client
./gradlew runServer      # Run dedicated server
./gradlew runGameTest    # Run game tests
./gradlew runData        # Generate data files

# Development utilities
./gradlew clean                  # Clean build artifacts
./gradlew --refresh-dependencies # Refresh dependencies if IDE is missing libraries
```

## Architecture

The mod follows a modular architecture with clear separation of concerns:

### Package Structure
```
com.astrolabs.astroexpansion/
├── api/                    # Public APIs for addons
│   └── energy/            # Energy system interfaces
├── common/                # Shared game logic
│   ├── blocks/           # Block implementations
│   ├── items/            # Item implementations
│   ├── blockentities/    # Tile entities
│   ├── capabilities/     # Forge capabilities
│   ├── network/          # Networking packets
│   ├── world/            # World generation
│   └── registry/         # Object registries
├── client/                # Client-side only
│   ├── gui/              # GUIs and screens
│   └── render/           # Renderers
├── datagen/              # Data generation
└── integration/          # Mod compatibility
```

### Core Systems

- **Main Class**: `com.astrolabs.astroexpansion.AstroExpansion` - Entry point and mod initialization
- **Registries**: Separate registry classes for blocks (`AEBlocks`), items (`AEItems`), and creative tabs (`AECreativeTabs`)
- **Energy System**: Custom energy API with `IEnergyHandler` interface and `EnergyStorage` implementation
- **Machine Framework**: `AbstractMachineBlockEntity` base class for all machines with energy and processing
- **Ore Generation**: `AEOreGeneration` handles worldgen for new ores

## Important Files

- `gradle.properties`: Contains mod metadata (ID: `astroexpansion`)
- `src/main/resources/META-INF/neoforge.mods.toml`: Mod manifest
- `src/main/resources/assets/astroexpansion/lang/en_us.json`: Localization
- `ASTRO_EXPANSION_PLAN.md`: Detailed development roadmap

## Current Implementation Status

### Phase 1: Foundation ✅ (Complete)
- ✓ Core mod structure and registries
- ✓ Basic resources (Titanium, Lithium, Uranium ores and materials)
- ✓ Energy system framework
- ✓ Machine base classes
- ✓ Ore generation setup
- ✓ Basic machines (Material Processor, Generator)
- ✓ GUI base classes
- ✓ Networking framework

### Phase 2: Early Game Tech ✅ (75% Complete)
- ✓ Energy Conduits for power transfer
- ✓ Component Assembler with 3x3 crafting
- ✓ Assembly recipe system
- ✓ Research system foundation
- ✓ Capability system integration
- ⏳ Research Console GUI
- ⏳ Recipe research locks

## Development Notes

- The mod ID is `astroexpansion` - use this in all resource paths and registry names
- Energy system is custom but designed to be compatible with other mods
- All machines extend `AbstractMachineBlockEntity` for consistent behavior
- Ore generation uses data-driven features for easy configuration
- Client-only code must be properly sided to avoid server crashes