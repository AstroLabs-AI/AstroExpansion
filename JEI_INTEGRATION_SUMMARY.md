# JEI Integration Summary

## Overview
JEI (Just Enough Items) integration has been successfully added to AstroExpansion. The integration provides recipe viewing for custom machine recipes within the JEI interface.

## Added Components

### 1. JEI Plugin Main Class
- **File**: `src/main/java/com/astrolabs/astroexpansion/integration/jei/AstroExpansionJEIPlugin.java`
- **Purpose**: Main entry point for JEI integration
- **Features**:
  - Registers recipe categories
  - Registers recipes from data files
  - Sets up recipe catalysts (which machines show which recipes)
  - Configures GUI handlers for recipe click areas
  - Sets up recipe transfer handlers

### 2. Recipe Categories

#### Material Processing
- **File**: `integration/jei/categories/ProcessingRecipeCategory.java`
- **Machine**: Material Processor
- **Recipe Type**: `astroexpansion:processing`
- **Features**: Shows energy cost, processing time, animated progress arrow

#### Ore Washing
- **File**: `integration/jei/categories/WashingRecipeCategory.java`
- **Machine**: Ore Washer
- **Recipe Type**: `astroexpansion:washing`
- **Features**: Shows energy cost, processing time, secondary outputs with chance

#### Component Assembly
- **File**: `integration/jei/categories/ComponentAssemblerCategory.java`
- **Machine**: Component Assembler
- **Recipe Type**: Hardcoded recipes (not data-driven)
- **Features**: 2x2 input grid, energy cost display
- **Recipes**:
  - Circuit Board
  - Processor
  - Energy Core
  - Advanced Processor

### 3. Additional Machine Support

#### Industrial Furnace
- Uses vanilla smelting recipes
- Registered as catalyst for `RecipeTypes.SMELTING`

#### Basic Generator
- Registered as catalyst for `RecipeTypes.FUELING`
- Shows fuel burn times in JEI

## Usage

### For Players
1. Open JEI (default key: E or R)
2. Click on machine blocks to see their recipes
3. Click the arrow in machine GUIs to view recipes
4. Use '+' button to transfer items from JEI to machine

### For Modpack Makers
- Processing recipes: Create JSON files in `data/astroexpansion/recipes/processing/`
- Washing recipes: Create JSON files in `data/astroexpansion/recipes/washing/`
- Component Assembler recipes are hardcoded and cannot be modified via data packs

## Recipe JSON Format Examples

### Processing Recipe
```json
{
  "type": "astroexpansion:processing",
  "input": {
    "item": "astroexpansion:raw_titanium"
  },
  "output": {
    "item": "astroexpansion:titanium_dust",
    "count": 2
  },
  "process_time": 200,
  "energy_cost": 1000
}
```

### Washing Recipe
```json
{
  "type": "astroexpansion:washing",
  "input": {
    "item": "astroexpansion:raw_titanium"
  },
  "output": {
    "item": "astroexpansion:raw_titanium",
    "count": 1
  },
  "secondary_output": {
    "item": "astroexpansion:raw_titanium",
    "count": 1,
    "chance": 0.1
  },
  "process_time": 100,
  "energy_cost": 500
}
```

## Future Enhancements
- Add JEI support for Fuel Refinery (fluid recipes)
- Add JEI support for Fusion Reactor recipes
- Add JEI support for Quantum Computer recipes
- Consider making Component Assembler recipes data-driven