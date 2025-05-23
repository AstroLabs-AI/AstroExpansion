# Manual Release Instructions for v1.0.0

Due to GitHub API permissions in the codespace, please create the release manually:

## Steps to Create Release

1. **Go to the releases page:**
   https://github.com/AstroLabs-AI/AstroExpansion/releases/new

2. **Fill in the release details:**
   - **Tag:** Select existing tag `v1.0.0`
   - **Target:** `forge-1.20.1`
   - **Release title:** `AstroExpansion v1.0.0 - The Final Frontier 🚀`

3. **Copy this release description:**

```markdown
# Release Notes - AstroExpansion v1.0.0

## The Final Frontier Update

This major release completes the space exploration experience with fully functional dimensions, gravity effects, and interdimensional travel!

### New Features

#### Space Dimension
- **The Void of Space**: A completely empty dimension at y=256
- **Zero Gravity**: Slow falling and levitation effects simulate weightlessness
- **Hostile Environment**: Requires space suit or suffer:
  - Suffocation damage (2 damage/second)
  - Wither, blindness, and slowness effects
- **Enhanced Solar Power**: Solar panels generate 2x power in space

#### Moon Dimension
- **Lunar Terrain**: Realistic moon surface with craters
  - Moon Stone base terrain
  - Moon Dust surface layer
  - Helium-3 Ore generates below y=40
- **Low Gravity**: 1/6th Earth gravity
  - Jump Boost III effect
  - Slow Falling I effect
- **Vacuum Environment**: Less hostile than space but still requires space suit

#### Dimensional Travel
- **Space Teleporter**: Travel to the Space dimension
  - Crafted with Quantum Core, Space Research Data, and Rocket parts
- **Moon Teleporter**: Travel to the Moon dimension
  - Crafted with Helium-3 Crystals, Lunar Rover, and Quantum components
- **Earth Teleporter**: Return home from space
  - Simple recipe using dirt, grass, and circuits
- **Safety Features**: Teleporters check for space suit before travel

#### Gravity System
- Automatic gravity effects based on dimension
- Space suit protection from environmental hazards
- Dimension-aware block behaviors

### Technical Improvements
- Custom chunk generators for each dimension
- Dimension type configurations with appropriate lighting
- Teleporter system with automatic platform generation
- Event-based gravity and environmental effects

### Bug Fixes
- Fixed duplicate registration error for fusion_core

### How to Use
1. Craft a full Space Suit (helmet, chestplate, leggings, boots)
2. Build a Space or Moon Teleporter
3. Right-click the teleporter to travel between dimensions
4. Teleporters create a safe arrival platform on first use
5. Use Earth Teleporter to return home

### Tips
- Always wear a full space suit when traveling to space or the moon
- Build your space station around the arrival platform
- Solar panels are twice as effective in space
- Mine Helium-3 on the moon for fusion fuel
- Life Support Systems are essential for space stations

### Complete Feature List
- 65+ Blocks
- 80+ Items
- 10 Multiblock structures
- 6 Entities (5 drones + lunar rover)
- 2 New dimensions
- Complete tech progression from mining to space travel

---

**Warning**: Space is extremely dangerous without proper equipment. Ensure you have a space suit and adequate life support before venturing beyond Earth!
```

4. **Upload the JAR file:**
   - Click "Attach binaries by dropping them here"
   - Upload: `releases/v1.0.0/astroexpansion-1.0.0-mc1.20.1-fixed.jar`
   - The file is already in the repository, you can download it first if needed

5. **Publish:**
   - Ensure "Set as the latest release" is checked
   - Click "Publish release"

## Alternative: Using GitHub Actions

Once the Actions infrastructure is working again, future releases will be automatic:

1. Push a tag: `git tag -a v1.0.1 -m "Version 1.0.1" && git push origin v1.0.1`
2. GitHub Actions will build and create the release automatically

Or use the manual workflow:
1. Go to Actions tab
2. Select "Create Release"
3. Click "Run workflow"
4. Enter version number
5. Enter release notes

## File Location

The fixed JAR file is located at:
`/workspaces/AstroExpansion/releases/v1.0.0/astroexpansion-1.0.0-mc1.20.1-fixed.jar`

This file has the fusion_core duplicate registration fix applied.