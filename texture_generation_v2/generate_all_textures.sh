#!/bin/bash

# Professional Texture Generation Script for AstroExpansion

echo "========================================"
echo "AstroExpansion Professional Texture Generator"
echo "========================================"

# Check if virtual environment exists
if [ ! -d "venv" ]; then
    echo "Creating virtual environment..."
    python3 -m venv venv
fi

# Activate virtual environment
source venv/bin/activate

# Install requirements
echo "Installing requirements..."
pip install -r requirements.txt

# Create output directory
OUTPUT_DIR="../src/main/resources/assets/astroexpansion/textures"
mkdir -p "$OUTPUT_DIR"

# Run texture generation
echo "Starting texture generation..."
python texture_generator_pro.py --output "$OUTPUT_DIR" --config texture_config.json

echo ""
echo "Texture generation complete!"
echo "Textures saved to: $OUTPUT_DIR"

# Deactivate virtual environment
deactivate