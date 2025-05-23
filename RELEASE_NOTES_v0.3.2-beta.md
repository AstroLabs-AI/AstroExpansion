# Astro Expansion v0.3.2-beta Hotfix

## 🔧 Critical Fix - Creative Tab Crash Resolved

This hotfix properly addresses the creative inventory crash issue.

### Fixed
- **Energy Conduit Registration**: Fixed the root cause of the creative tab crash
  - Energy Conduit block was not properly registered with a BlockItem
  - Changed from direct block registration to use the registerBlock helper method
  - This ensures the block has a proper item representation for creative tabs

### Technical Details
The crash was caused by ENERGY_CONDUIT being registered directly with `BLOCKS.register()` instead of using the `registerBlock()` helper method. This meant the block didn't have a corresponding BlockItem, causing an IllegalArgumentException when the creative tab tried to display it.

### Verification
- Creative inventory now opens without crashing
- All items are properly displayed in the Astro Expansion creative tab
- Energy Conduits can now be obtained from creative mode

### Known Issues (from v0.3.0-beta)
- Many block/item textures are missing (purple/black placeholders)
- Fusion Reactor GUI texture is placeholder
- Fluid rendering in tanks may flicker
- Multiblock structures don't persist rotation on world reload

---

**Important**: This replaces v0.3.1-beta which did not fully resolve the issue.

**Requires**: Minecraft 1.20.1, Forge 47.2.0+