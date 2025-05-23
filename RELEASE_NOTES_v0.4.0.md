# AstroExpansion v0.4.0 Release Notes

## 🎨 Visual Overhaul Update

This release brings a complete visual overhaul to AstroExpansion with brand new textures for every block, item, and GUI in the mod!

### ✨ New Features

#### Texture System
- **93 brand new textures** generated for all mod content
- Professional texture generation system for future content
- Consistent visual style across all tech tiers
- Support for both basic and advanced texture generation modes

#### Visual Improvements
- **Block Textures (37)**: All ores, machines, and multiblock components now have unique textures
- **Item Textures (42)**: Materials, components, and tools with proper styling
- **GUI Textures (9)**: All machine interfaces redesigned with proper layouts
- **Entity Textures (5)**: Drone models with distinctive team colors

### 🔧 Technical Additions

#### Texture Generator
- Comprehensive texture generation system in Python
- Basic mode for quick placeholder textures
- Advanced mode with procedural generation and effects
- Configurable material colors and styles
- Automated batch generation for all textures

### 🐛 Bug Fixes
- Fixed creative inventory crash (IllegalArgumentException)
- Fixed missing GUI screens for Phase 1 machines
- Fixed ENERGY_CONDUIT registration issue
- Added fluid tank support to OreWasherBlockEntity

### 📝 Development Tools
- Added `generate_textures.sh` script for easy texture generation
- Texture configuration system via `texture_config.json`
- Support for animated textures (future use)
- Procedural ore vein generation
- Metallic and glass material effects

### 🎮 Player Experience
- All blocks and items now have proper visual representation
- No more purple/black missing textures
- Consistent tech-tier styling (Basic → Advanced → Quantum)
- Material-specific color schemes for different resources

### 📦 Installation
Same as previous versions - drop the JAR file into your mods folder.

### 🔜 Coming Next
- Phase 3 completion: Quantum Computer, Fuel Refinery, Rocket systems
- Research system implementation
- Space dimension exploration
- More advanced automation features

---

**Full Changelog**: [v0.3.3-beta...v0.4.0](https://github.com/yourusername/AstroExpansion/compare/v0.3.3-beta...v0.4.0)