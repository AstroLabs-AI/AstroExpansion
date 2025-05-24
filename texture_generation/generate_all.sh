#!/bin/bash
# Master texture generation script for AstroExpansion

echo "AstroExpansion Texture Generation System"
echo "========================================"
echo ""

# Check if Python is installed
if ! command -v python3 &> /dev/null; then
    echo "Error: Python 3 is not installed"
    exit 1
fi

# Check if we're in the right directory
if [ ! -f "texture_manifest.json" ]; then
    echo "Error: Must run from texture_generation directory"
    exit 1
fi

# Install requirements if needed
echo "Checking Python dependencies..."
pip3 install -r requirements.txt --quiet

# Create output directories
OUTPUT_DIR="../src/main/resources/assets/astroexpansion/textures"
echo "Creating output directories in $OUTPUT_DIR..."
mkdir -p "$OUTPUT_DIR"/{block,item,gui,entity,particle}
mkdir -p "$OUTPUT_DIR"/gui/{container,widgets,icons}
mkdir -p "$OUTPUT_DIR"/entity/{rocket,drone,vehicle}

# Run generators
echo ""
echo "Starting texture generation..."
echo ""

# Generate main textures
echo "1. Generating block and item textures..."
python3 generate_all_textures.py

# Generate GUI textures
echo ""
echo "2. Generating GUI textures..."
python3 gui_texture_generator.py

# Generate any missing textures with unified generator
echo ""
echo "3. Running unified texture generator for any missing textures..."
python3 texture_generator_unified.py

# Summary
echo ""
echo "========================================"
echo "Texture generation complete!"
echo ""
echo "Output location: $OUTPUT_DIR"
echo ""

# Count generated textures
BLOCK_COUNT=$(find "$OUTPUT_DIR/block" -name "*.png" 2>/dev/null | wc -l)
ITEM_COUNT=$(find "$OUTPUT_DIR/item" -name "*.png" 2>/dev/null | wc -l)
GUI_COUNT=$(find "$OUTPUT_DIR/gui" -name "*.png" 2>/dev/null | wc -l)
ENTITY_COUNT=$(find "$OUTPUT_DIR/entity" -name "*.png" 2>/dev/null | wc -l)

echo "Generated textures:"
echo "  Blocks: $BLOCK_COUNT"
echo "  Items: $ITEM_COUNT"
echo "  GUIs: $GUI_COUNT"
echo "  Entities: $ENTITY_COUNT"
echo "  Total: $((BLOCK_COUNT + ITEM_COUNT + GUI_COUNT + ENTITY_COUNT))"
echo ""

echo "Don't forget to:"
echo "  1. Review generated textures"
echo "  2. Fine-tune any that need adjustment"
echo "  3. Add custom details to specific textures"
echo "  4. Test in-game for visibility and consistency"