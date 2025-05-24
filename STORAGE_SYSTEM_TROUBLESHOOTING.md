# Storage System Troubleshooting Guide

## Storage Terminal Not Accepting Items

If your Storage Terminal is returning items to your inventory instead of storing them, check the following:

### 1. Storage Core Setup
- **Power Required**: The Storage Core needs RF/FE power to function
  - Connect a Basic Generator or other power source
  - The core consumes 10 FE/t base + 5 FE/t per drive
  - Check if the core block shows the "powered" texture

### 2. Storage Drives
- **At least one drive required**: The Storage Core needs storage drives to work
  - Craft and insert Storage Drives (1k, 4k, 16k, or 64k)
  - Each drive provides item storage capacity
  - No drives = no storage!

### 3. Network Connection
- **Terminal must find the Core**: Storage Terminals search for cores within 16 blocks
  - Place the Terminal within 16 blocks of a Storage Core
  - The Terminal periodically scans for networks (every 5 seconds)
  - If you just placed the core, wait a moment for detection

### 4. Network Formation
The Storage Core will show as "formed" (different texture) when:
- It has power (any amount)
- It has at least one storage drive installed
- Both conditions must be met!

### 5. Storage Capacity
- Check if your storage is full:
  - 1k Drive = 1,000 items
  - 4k Drive = 4,000 items
  - 16k Drive = 16,000 items
  - 64k Drive = 64,000 items
- The system counts total items, not item types

## Quick Testing Setup

1. Place a Storage Core
2. Place a Basic Generator next to it (add coal)
3. Insert at least one Storage Drive into the Core
4. Place a Storage Terminal within 16 blocks
5. Wait 5 seconds for network detection
6. Right-click the Terminal - it should say "Connected" not "Not connected to storage network!"

## Debug Commands

Enable debug logging to see what's happening:
```
/forge logging com.astrolabs.astroexpansion debug
```

Then check the game logs for messages about:
- Storage Core network formation
- Terminal network connections
- Item insertion attempts

## Common Issues

1. **"Not connected to storage network!"**
   - No Storage Core within range
   - Storage Core has no power
   - Storage Core has no drives

2. **Items bounce back immediately**
   - Storage is full
   - Network not formed (check power/drives)
   - Terminal hasn't detected the network yet (wait 5 seconds)

3. **Can see items but can't extract**
   - This is usually a separate issue from insertion
   - Check if the core still has power

## Technical Details

The storage system uses:
- `IStorageNetwork` interface for network operations
- Storage Cores act as the network controller
- Terminals are just access points (no storage themselves)
- Items are stored in a HashMap with stack compression
- Network scanning happens every 100 ticks (5 seconds)