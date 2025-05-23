# AstroExpansion v0.4.5 Storage GUI Fixes

## 🎮 User Interface Improvements

This update fixes several GUI issues with the Storage system, improving usability and alignment.

### 🐛 Bug Fixes

#### Storage Terminal ESC Key Fix
- **Fixed: ESC key now properly closes Storage Terminal**
- Previously, the search box was capturing all keyboard input
- ESC key (keycode 256) now takes priority to close the GUI

#### GUI Slot Alignment Fixes
- **Storage Core**: Adjusted player inventory positions
  - Inventory: Y=94 (was 84)
  - Hotbar: Y=152 (was 142)
- **Storage Terminal**: Realigned inventory slots
  - Inventory: X=18, Y=140 (was X=8, Y=107)  
  - Hotbar: X=18, Y=198 (was X=8, Y=165)
- **Storage Core**: Set standard GUI height (176 pixels)

#### Storage Core Access
- Added missing getDriveHandler() method for GUI access
- Fixed compilation issue with drive visibility

### 📝 Technical Details

The GUI issues were caused by:
1. Search box consuming all keyboard events, blocking ESC
2. Non-standard inventory slot positions not matching GUI textures
3. Missing texture alignment with slot positions

### 🎮 What This Improves
- Can now press ESC to exit Storage Terminal properly
- Inventory slots align correctly with GUI backgrounds
- No more misaligned bars or slots
- Consistent GUI experience with vanilla Minecraft

### 📦 Installation
Drop the JAR file into your mods folder. This release includes all previous fixes.

---

**Quality of life improvements for the Storage system!**