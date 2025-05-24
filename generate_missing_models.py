#!/usr/bin/env python3
"""Generate missing item models for AstroExpansion mod."""

import os
import json

# Create directories if they don't exist
os.makedirs("src/main/resources/assets/astroexpansion/models/item", exist_ok=True)

# Missing item models
missing_models = [
    "deuterium_cell",
    "helium3_crystal", 
    "oxygen_canister",
    "research_data_advanced",
    "research_data_basic",
    "research_data_fusion",
    "research_data_quantum",
    "research_data_space",
    "tritium_cell"
]

# Generate item models
for item in missing_models:
    model = {
        "parent": "item/generated",
        "textures": {
            "layer0": f"astroexpansion:item/{item}"
        }
    }
    
    path = f"src/main/resources/assets/astroexpansion/models/item/{item}.json"
    with open(path, 'w') as f:
        json.dump(model, f, indent=2)
    print(f"Generated model: {item}.json")

print(f"\nGenerated {len(missing_models)} missing item models!")