# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew build              # Build the mod JAR
./gradlew runClient          # Launch Minecraft client with mod loaded
./gradlew runServer          # Launch dedicated server with mod loaded
./gradlew runData            # Generate data files (recipes, advancements, etc.)
./gradlew runGameTestServer  # Run game tests
./gradlew clean              # Clean build artifacts
```

## Architecture Overview

This is a Minecraft Forge mod for version 1.20.1. The codebase follows Forge's standard architecture:

- **Event-Driven System**: Uses Forge's event bus for mod lifecycle and game events
- **Deferred Registration**: Items, blocks, and other game objects are registered using DeferredRegister
- **Configuration System**: Uses ForgeConfigSpec for player-configurable settings
- **Client/Server Separation**: Code is properly separated between client-only and common code

### Key Components

- `ExampleMod.java`: Main mod class with @Mod annotation, handles initialization
- `Config.java`: Configuration definition using ForgeConfigSpec
- `mods.toml`: Mod metadata including dependencies and description
- Resources are in `src/main/resources/` including textures, models, and lang files

## Development Requirements

- **Java 17** (required for Minecraft 1.20.1)
- **Minecraft 1.20.1** with **Forge 47.4.0**
- Gradle wrapper included (./gradlew)

## Important Notes

- The mod ID is currently `examplemod` - this should be changed for actual mods
- Package structure is `com.example.examplemod` - update to match your organization
- Built JARs appear in `build/libs/` after running `./gradlew build`
- Use `runClient` for quick testing during development
- Forge automatically handles obfuscation when building for release