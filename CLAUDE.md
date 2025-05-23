# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew build              # Build the mod JAR (output in build/libs/)
./gradlew runClient          # Launch Minecraft client with mod loaded
./gradlew runServer          # Launch dedicated server with mod loaded
./gradlew runData            # Generate data files (recipes, advancements, etc.)
./gradlew runGameTestServer  # Run game tests
./gradlew clean              # Clean build artifacts

# Release automation
./release.sh                 # Create a new release
./autopush.sh enable         # Enable automatic git push after commits
./generate_textures.sh       # Generate missing textures using texture_generator
```

## Architecture Overview

This is a Minecraft Forge mod for version 1.20.1 implementing a comprehensive space exploration and technology system. The mod ID is `astroexpansion` and uses the package structure `com.astrolabs.astroexpansion`.

### Core Architecture Patterns

- **Registry System**: All game objects use DeferredRegister pattern in `common/registry/` 
  - Each registry class (ModBlocks, ModItems, etc.) follows the same registration pattern
  - Block items are automatically registered alongside blocks
  
- **BlockEntity Pattern**: Machines extend custom base classes
  - Energy-using machines typically extend a base that implements energy capabilities
  - GUI machines implement menu providers and container listeners
  
- **Multiblock System**: Complex structures use the multiblock API in `api/multiblock/`
  - Controllers validate structure patterns
  - Patterns define the expected block layout
  - Examples: Fusion Reactor (5x5x5), Industrial Furnace (3x3x3), Rocket Assembly (9x15x9)

- **Client/Server Separation**:
  - Client-only code in `client/` package (screens, renderers)
  - Common code in `common/` package
  - Network packets handled via ModPacketHandler

### Key Systems

**Energy System**:
- Custom capability `AstroEnergyStorage` for energy handling
- Energy flows through conduits with automatic distribution
- Generators produce, machines consume, batteries store

**Storage Network**:
- Digital item storage with drives (1k, 4k, 16k, 64k capacity)
- Storage Core acts as network controller
- Storage Terminal provides access interface
- Import/Export buses for automation

**Dimension System**:
- Custom dimensions: Space (void with zero gravity) and Moon (low gravity)
- Custom chunk generators for each dimension
- Teleporter blocks for dimension travel
- Special environmental effects per dimension

## Development Requirements

- **Java 17** (required for Minecraft 1.20.1)
- **Minecraft 1.20.1** with **Forge 47.2.0+**
- Gradle wrapper included (./gradlew)

## Project-Specific Notes

- Built JARs appear in `build/libs/` as `astroexpansion-{version}-mc1.20.1.jar`
- Data generation creates recipes, advancements, and tags in `src/main/resources/data/`
- Texture generator Python scripts in `texture_generator/` can create placeholder textures
- GitHub Actions automatically build releases when tags are pushed
- The mod includes 100+ blocks/items across 10 major tech phases