# Release Notes - AstroExpansion v1.0.1

## Bug Fix Release

This release fixes critical duplicate registration errors that prevented the mod from loading.

### Bug Fixes
- Fixed duplicate registration error for `fusion_core` - renamed block to `fusion_core_block`
- Fixed duplicate registration error for `moon_dust` - renamed item to `moon_dust_item`
- Updated all references to use the new names
- Properly versioned the mod to 1.0.1

### Technical Details
The issue occurred because both blocks and items were registered with the same name. Since blocks automatically register items, this caused a conflict. The fix involves renaming the conflicting registrations to use unique names.

### Installation
1. Install Minecraft Forge 1.20.1 (version 47.2.0 or newer)
2. Download the JAR file below
3. Place it in your `.minecraft/mods` folder

### Upgrading from v1.0.0
Simply replace the old JAR with the new one. No world data will be lost.

---

**This version has been tested and confirmed working!**