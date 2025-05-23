# Astro Expansion v0.3.1-beta Hotfix

## 🔧 Critical Fix

This hotfix addresses a crash that occurred when opening the creative inventory.

### Fixed
- **Creative Tab Crash**: Fixed IllegalArgumentException when opening creative inventory
- **Missing Items**: Added all Phase 3 items to the creative tab including:
  - Fusion Reactor components (Controller, Casing, Core, Plasma Injector)
  - All drone types (Construction, Farming, Combat, Logistics)
  - Import/Export buses and Component Assembler
  - Fluid system blocks (Tank, Pipe)
  - Advanced Processor and Titanium Plate

### Known Issues (from v0.3.0-beta)
- Fusion Reactor GUI texture is placeholder
- Fluid rendering in tanks may flicker
- Multiblock structures don't persist rotation on world reload
- Many block/item textures are missing (purple/black placeholders)

---

**Note**: This is a critical hotfix for v0.3.0-beta. All features from v0.3.0-beta are included.

**Requires**: Minecraft 1.20.1, Forge 47.2.0+