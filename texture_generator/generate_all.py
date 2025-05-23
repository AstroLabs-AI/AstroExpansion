#!/usr/bin/env python3
"""
Batch generation script for all AstroExpansion textures
Generates all missing textures and places them in the correct resource directories
"""

import os
import sys
import json
from pathlib import Path
from texture_generator import TextureGenerator
from advanced_generator import AdvancedTextureGenerator

def main():
    # Setup paths
    generator_dir = Path(__file__).parent
    mod_root = generator_dir.parent
    assets_dir = mod_root / "src/main/resources/assets/astroexpansion/textures"
    
    # Load configuration
    config_path = generator_dir / "texture_config.json"
    with open(config_path, 'r') as f:
        config = json.load(f)
    
    # Choose generator based on command line argument
    use_advanced = "--advanced" in sys.argv
    generator_class = AdvancedTextureGenerator if use_advanced else TextureGenerator
    
    print(f"Using {'advanced' if use_advanced else 'basic'} texture generator")
    print(f"Output directory: {assets_dir}")
    
    # Create generator instance
    generator = generator_class(assets_dir, config)
    
    # Generate all textures
    print("\nGenerating textures...")
    results = generator.generate_all()
    
    # Print summary
    print("\n=== Generation Summary ===")
    print(f"Total textures to generate: {results['total']}")
    print(f"Successfully generated: {results['generated']}")
    print(f"Skipped (already exist): {results['skipped']}")
    print(f"Failed: {results['failed']}")
    
    if results['failures']:
        print("\nFailed textures:")
        for failure in results['failures']:
            print(f"  - {failure}")
    
    print("\n=== Texture Breakdown ===")
    print(f"Blocks: {results['blocks']}")
    print(f"Items: {results['items']}")
    print(f"GUIs: {results['guis']}")
    print(f"Entities: {results['entities']}")
    
    if results['generated'] > 0:
        print(f"\n✓ Successfully generated {results['generated']} textures!")
        print("You can now run './gradlew runClient' to test the mod with the new textures.")
    
    return 0 if results['failed'] == 0 else 1

if __name__ == "__main__":
    # Check if PIL is installed
    try:
        import PIL
    except ImportError:
        print("Error: Pillow is not installed!")
        print("Please run: pip install -r requirements.txt")
        sys.exit(1)
    
    # Run generation
    sys.exit(main())