# Animated GUI Implementation

## Overview
Added dynamic, animated GUI elements to machine screens to make them feel more responsive and engaging.

## New Widget Classes

### 1. AnimatedWidget (Base Class)
- Abstract base class for all animated widgets
- Handles animation timing and update cycles
- Provides framework for custom animations

### 2. PulsingEnergyBar
- Animated energy display with pulsing glow effect
- Glows brighter when energy is actively flowing
- Shows smooth transitions between energy levels
- Includes hover effects for better feedback

### 3. AnimatedProgressArrow
- Smooth progress transitions instead of jumpy updates
- Wave animation along the arrow during processing
- Glowing edge at the progress point with particle effects
- Visual feedback for active processing

### 4. RotatingGear
- Spinning gears/cogs for processing machines
- Smooth acceleration/deceleration based on machine state
- Motion blur effects at high speeds
- Scale animation on hover

### 5. BubblingLiquid
- Realistic liquid rendering with animated surface waves
- Bubble particle system for active liquids
- Transparency gradient for depth effect
- Dynamic bubble spawning based on machine activity

### 6. SparkingEffect
- Electrical spark particles for generators
- Intensity scales with power generation
- Multiple spark colors (yellow, orange, blue)
- Central glow effect when generating

## Updated Screens

### Material Processor Screen
- **Pulsing Energy Bar**: Shows energy flow with glowing effects
- **Animated Progress Arrow**: Smooth transitions with wave effects
- **Rotating Gears**: Two counter-rotating gears that speed up during processing
- **Hover Effects**: Subtle glow on upgrade slots

### Ore Washer Screen
- **Pulsing Energy Bar**: Energy visualization
- **Animated Progress Arrow**: Processing progress
- **Bubbling Water Tank**: Animated water with bubbles when active
- **Washing Gear**: Rotating agitator that spins during washing

### Basic Generator Screen
- **Pulsing Energy Bar**: Shows energy generation
- **Sparking Effect**: Electrical sparks around fuel slot when generating
- **Animated Flame**: Flickering flame effect with glow
- **Hover Effects**: Interactive upgrade slots

## Features

1. **Smooth Transitions**
   - All animations use interpolation for smooth movement
   - No jarring state changes

2. **Performance Optimized**
   - Efficient rendering with minimal overhead
   - Animations only update when visible

3. **Interactive Feedback**
   - Hover effects on all interactive elements
   - Visual cues for machine states

4. **Customizable**
   - Easy to adjust animation speeds and intensities
   - Reusable widgets for other screens

## Usage Example
```java
// Create animated widgets in screen init()
energyBar = new PulsingEnergyBar(x + 152, y + 17, 16, 52, TEXTURE, maxEnergy);
addRenderableWidget(energyBar);

// Update widget state in renderBg()
energyBar.setEnergy(menu.getEnergyStored());
energyBar.setFlowing(menu.isCrafting());
```

## Future Enhancements
- Sound effects synchronized with animations
- More particle effects for different machine types
- Animated slot highlights when items are inserted
- Custom animations for multiblock structures