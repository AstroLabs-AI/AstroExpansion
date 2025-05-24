#!/bin/bash

# Deploy generated textures to the mod's texture directories

echo "========================================"
echo "Deploying Textures to AstroExpansion"
echo "========================================"

# Source and destination directories
SRC_DIR="output/textures"
DEST_BASE="../src/main/resources/assets/astroexpansion/textures"

# Check if textures have been generated
if [ ! -d "$SRC_DIR" ]; then
    echo "Error: No generated textures found in $SRC_DIR"
    echo "Please run ./generate_all_textures.sh first"
    exit 1
fi

# Create destination directories if they don't exist
mkdir -p "$DEST_BASE/block"
mkdir -p "$DEST_BASE/item"
mkdir -p "$DEST_BASE/gui"

# Deploy block textures
if [ -d "$SRC_DIR/blocks" ]; then
    echo "Deploying block textures..."
    cp -v "$SRC_DIR/blocks/"*.png "$DEST_BASE/block/" 2>/dev/null || echo "  No block textures found"
fi

# Deploy item textures
if [ -d "$SRC_DIR/items" ]; then
    echo "Deploying item textures..."
    cp -v "$SRC_DIR/items/"*.png "$DEST_BASE/item/" 2>/dev/null || echo "  No item textures found"
fi

# Deploy GUI textures
if [ -d "$SRC_DIR/gui" ]; then
    echo "Deploying GUI textures..."
    cp -v "$SRC_DIR/gui/"*.png "$DEST_BASE/gui/" 2>/dev/null || echo "  No GUI textures found"
fi

echo ""
echo "Texture deployment complete!"
echo "Textures deployed to: $DEST_BASE"

# Optional: Show texture counts
BLOCK_COUNT=$(find "$DEST_BASE/block" -name "*.png" 2>/dev/null | wc -l)
ITEM_COUNT=$(find "$DEST_BASE/item" -name "*.png" 2>/dev/null | wc -l)
GUI_COUNT=$(find "$DEST_BASE/gui" -name "*.png" 2>/dev/null | wc -l)

echo ""
echo "Texture Summary:"
echo "  Block textures: $BLOCK_COUNT"
echo "  Item textures: $ITEM_COUNT"
echo "  GUI textures: $GUI_COUNT"
echo "  Total: $((BLOCK_COUNT + ITEM_COUNT + GUI_COUNT))"