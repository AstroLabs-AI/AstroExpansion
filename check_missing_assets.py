#!/usr/bin/env python3
"""Check for missing models and textures in AstroExpansion mod."""

import os
import re
import json

# Paths
ITEMS_JAVA = "src/main/java/com/astrolabs/astroexpansion/common/registry/ModItems.java"
BLOCKS_JAVA = "src/main/java/com/astrolabs/astroexpansion/common/registry/ModBlocks.java"
MODELS_ITEM_PATH = "src/main/resources/assets/astroexpansion/models/item"
MODELS_BLOCK_PATH = "src/main/resources/assets/astroexpansion/models/block"
TEXTURES_ITEM_PATH = "src/main/resources/assets/astroexpansion/textures/item"
TEXTURES_BLOCK_PATH = "src/main/resources/assets/astroexpansion/textures/block"
BLOCKSTATES_PATH = "src/main/resources/assets/astroexpansion/blockstates"

def extract_registered_items(file_path):
    """Extract registered item names from Java file."""
    items = []
    with open(file_path, 'r') as f:
        content = f.read()
        # Find all ITEMS.register("name", ...) patterns
        pattern = r'ITEMS\.register\("([^"]+)"'
        items = re.findall(pattern, content)
    return items

def extract_registered_blocks(file_path):
    """Extract registered block names from Java file."""
    blocks = []
    with open(file_path, 'r') as f:
        content = f.read()
        # Find all registerBlock("name", ...) patterns
        pattern = r'registerBlock\("([^"]+)"'
        blocks = re.findall(pattern, content)
    return blocks

def check_file_exists(base_path, name, extension):
    """Check if a file exists."""
    return os.path.exists(os.path.join(base_path, f"{name}.{extension}"))

def check_texture_references(model_path):
    """Check if textures referenced in model exist."""
    missing_textures = []
    if os.path.exists(model_path):
        with open(model_path, 'r') as f:
            try:
                model = json.load(f)
                if 'textures' in model:
                    for key, texture in model['textures'].items():
                        if texture.startswith('astroexpansion:'):
                            texture_path = texture.replace('astroexpansion:', '')
                            if texture_path.startswith('block/'):
                                actual_path = os.path.join('src/main/resources/assets/astroexpansion/textures', texture_path + '.png')
                            elif texture_path.startswith('item/'):
                                actual_path = os.path.join('src/main/resources/assets/astroexpansion/textures', texture_path + '.png')
                            else:
                                actual_path = os.path.join('src/main/resources/assets/astroexpansion/textures', texture_path + '.png')
                            
                            if not os.path.exists(actual_path):
                                missing_textures.append(texture)
            except:
                pass
    return missing_textures

# Extract registered items and blocks
items = extract_registered_items(ITEMS_JAVA)
blocks = extract_registered_blocks(BLOCKS_JAVA)

print("=== AstroExpansion Asset Check ===\n")

# Check items
print(f"Found {len(items)} registered items")
missing_item_models = []
missing_item_textures = []

for item in items:
    if not check_file_exists(MODELS_ITEM_PATH, item, "json"):
        missing_item_models.append(item)
    else:
        # Check textures referenced in model
        model_path = os.path.join(MODELS_ITEM_PATH, f"{item}.json")
        missing_refs = check_texture_references(model_path)
        if missing_refs:
            missing_item_textures.extend(missing_refs)
    
    if not check_file_exists(TEXTURES_ITEM_PATH, item, "png"):
        missing_item_textures.append(f"astroexpansion:item/{item}")

# Check blocks
print(f"Found {len(blocks)} registered blocks")
missing_blockstates = []
missing_block_models = []
missing_block_textures = []

for block in blocks:
    if not check_file_exists(BLOCKSTATES_PATH, block, "json"):
        missing_blockstates.append(block)
    
    # Many blocks use item models, so check both
    if not check_file_exists(MODELS_BLOCK_PATH, block, "json") and \
       not check_file_exists(MODELS_ITEM_PATH, block, "json"):
        missing_block_models.append(block)
    
    if not check_file_exists(TEXTURES_BLOCK_PATH, block, "png"):
        # Some blocks might use item textures
        if not check_file_exists(TEXTURES_ITEM_PATH, block, "png"):
            missing_block_textures.append(block)

# Report findings
print("\n=== Missing Assets ===")

if missing_item_models:
    print(f"\n❌ Missing Item Models ({len(missing_item_models)}):")
    for item in sorted(missing_item_models):
        print(f"  - {item}")

if missing_item_textures:
    print(f"\n❌ Missing Item Textures ({len(set(missing_item_textures))}):")
    for texture in sorted(set(missing_item_textures)):
        print(f"  - {texture}")

if missing_blockstates:
    print(f"\n❌ Missing Blockstates ({len(missing_blockstates)}):")
    for block in sorted(missing_blockstates):
        print(f"  - {block}")

if missing_block_models:
    print(f"\n❌ Missing Block Models ({len(missing_block_models)}):")
    for block in sorted(missing_block_models):
        print(f"  - {block}")

if missing_block_textures:
    print(f"\n❌ Missing Block Textures ({len(missing_block_textures)}):")
    for block in sorted(missing_block_textures):
        print(f"  - {block}")

# Summary
total_missing = len(missing_item_models) + len(set(missing_item_textures)) + \
                len(missing_blockstates) + len(missing_block_models) + \
                len(missing_block_textures)

if total_missing == 0:
    print("\n✅ All assets are present!")
else:
    print(f"\n⚠️  Total missing assets: {total_missing}")
    
# List all items for reference
print("\n=== All Registered Items ===")
for item in sorted(items):
    print(f"  - {item}")

print("\n=== All Registered Blocks ===")
for block in sorted(blocks):
    print(f"  - {block}")