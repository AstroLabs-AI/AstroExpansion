#!/bin/bash
# Quick texture generation script for AstroExpansion

echo "AstroExpansion Texture Generator"
echo "================================"

# Check if Python is installed
if ! command -v python3 &> /dev/null; then
    echo "Error: Python 3 is required but not installed."
    exit 1
fi

# Navigate to texture generator directory
cd texture_generator

# Install dependencies if needed
if ! python3 -c "import PIL" 2>/dev/null; then
    echo "Installing required Python packages..."
    pip install -r requirements.txt || pip3 install -r requirements.txt
fi

# Run texture generation
if [ "$1" == "--advanced" ]; then
    echo "Generating textures with advanced mode..."
    python3 generate_all.py --advanced
else
    echo "Generating textures with basic mode..."
    echo "(Use --advanced flag for higher quality textures)"
    python3 generate_all.py
fi

echo ""
echo "Done! Check the generated textures in src/main/resources/assets/astroexpansion/textures/"