# AstroExpansion v0.4.3 GUI Polish Update

## 🎨 GUI Improvements

This update fixes overlapping text and UI elements in machine interfaces for better readability.

### 🐛 Bug Fixes

#### Fixed GUI Text Overlapping
- **Basic Generator**: Fixed inventory label position (was -110, now -94)
- **Material Processor**: Fixed inventory label position
- **Ore Washer**: Fixed inventory label position  
- **Energy Storage**: Fixed inventory label position
- **Storage Terminal**: Moved network info text to bottom of screen (Y=200)
- **Industrial Furnace**: Reduced "Multiblock not formed!" hover area

#### Standardized Label Positions
All machine GUIs now use consistent label positioning:
- Title label: Y=6 (top of GUI)
- Inventory label: Y=imageHeight-94 (standard bottom position)

### 📝 Technical Details

The overlapping was caused by:
1. Incorrect `inventoryLabelY` calculations using -110 offset
2. Storage Terminal network info placed too high (Y=95)
3. Industrial Furnace tooltip hover area too large (144x54)

All GUIs now follow Minecraft's standard label positioning conventions.

### 🎮 What This Improves
- Text no longer overlaps with slots or buttons
- All labels are clearly readable
- Tooltips don't interfere with GUI interaction
- Consistent UI experience across all machines

### 📦 Installation
Drop the JAR file into your mods folder. This release includes all previous fixes.

---

**Quality of life improvement - recommended for all players!**