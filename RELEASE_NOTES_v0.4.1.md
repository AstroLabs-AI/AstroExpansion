# AstroExpansion v0.4.1 Hotfix Release

## 🔧 Model & Blockstate Fix

This hotfix addresses the missing model and blockstate files that were preventing blocks and items from rendering correctly in v0.4.0.

### 🐛 Bug Fixes

#### Missing Models & Blockstates
- Added all missing blockstate JSON files for blocks
- Added all missing block model JSON files
- Added all missing item model JSON files
- Fixed energy conduit and fluid pipe multipart models
- Fixed directional block rotations for machines

### 📝 Technical Details

#### Files Added
- **18 Blockstate files**: Proper block state definitions with variants
- **21 Block model files**: Including multipart models for pipes/conduits
- **18 Item model files**: For all items and block items

#### Model Types Created
- Simple cube models for ores and storage blocks
- Orientable models for machines with proper face textures
- Multipart models for energy conduits and fluid pipes
- Item models using generated textures

### 🎮 What This Fixes
- All blocks now render correctly in the world
- Items display properly in inventory and when held
- Machines show correct orientations when placed
- Pipes and conduits connect visually to adjacent blocks

### 📦 Installation
Drop the JAR file into your mods folder. This release includes all changes from v0.4.0.

---

**Note**: The recipe category warnings in the log are expected and don't affect gameplay. Full JEI integration will come in a future update.