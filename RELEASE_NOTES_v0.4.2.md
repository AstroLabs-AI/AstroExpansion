# AstroExpansion v0.4.2 Critical Hotfix

## 🔧 Energy System Fix

This critical hotfix resolves a major bug where several machines could not receive power from energy conduits.

### 🐛 Bug Fixes

#### Fixed Machines Not Accepting Power
The following machines were incorrectly configured with `maxReceive = 0`, preventing them from accepting energy:
- **Storage Core** - Now properly accepts 1000 FE/tick
- **Material Processor** - Now properly accepts 100 FE/tick
- **Ore Washer** - Now properly accepts 100 FE/tick
- **Component Assembler** - Now properly accepts 100 FE/tick
- **Industrial Furnace Controller** - Now properly accepts 100 FE/tick

### 📝 Technical Details

The bug was in the energy storage initialization where the third parameter (maxReceive) was set to 0, which blocked all incoming energy transfers. All affected machines now have proper energy input rates.

### 🎮 What This Fixes
- Storage Core can now be powered and form networks properly
- All processing machines can now receive power from generators
- Energy conduits will properly transfer power to all machines
- No more "Storage network not formed!" errors due to lack of power

### 📦 Installation
Drop the JAR file into your mods folder. This release includes all previous fixes.

### ⚠️ Important Note
If you have existing machines that aren't receiving power:
1. Break and replace the machine
2. Or wait for the chunk to reload
3. The fix will apply to newly placed machines immediately

---

**Critical fix - all players should update to this version!**