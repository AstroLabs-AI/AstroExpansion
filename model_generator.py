#!/usr/bin/env python3
"""
Model and Blockstate Generator for AstroExpansion
Generates all missing model and blockstate JSON files
"""

import json
import os
from pathlib import Path

class ModelGenerator:
    def __init__(self, mod_root: Path):
        self.mod_root = mod_root
        self.assets_dir = mod_root / "src/main/resources/assets/astroexpansion"
        
    def generate_all(self):
        """Generate all missing models and blockstates"""
        print("Generating models and blockstates...")
        
        # Generate blockstates
        self.generate_blockstates()
        
        # Generate block models
        self.generate_block_models()
        
        # Generate item models
        self.generate_item_models()
        
        print("Model generation complete!")
        
    def generate_blockstates(self):
        """Generate blockstate JSON files"""
        blockstates_dir = self.assets_dir / "blockstates"
        blockstates_dir.mkdir(parents=True, exist_ok=True)
        
        # Simple blocks (ores, storage blocks)
        simple_blocks = [
            "titanium_ore", "deepslate_titanium_ore", "titanium_block",
            "lithium_ore", "deepslate_lithium_ore", "lithium_block",
            "uranium_ore", "deepslate_uranium_ore", "uranium_block",
            "energy_storage"
        ]
        
        for block in simple_blocks:
            blockstate = {
                "variants": {
                    "": {"model": f"astroexpansion:block/{block}"}
                }
            }
            
            path = blockstates_dir / f"{block}.json"
            with open(path, 'w') as f:
                json.dump(blockstate, f, indent=2)
            print(f"Generated blockstate: {block}.json")
            
        # Directional blocks (machines)
        directional_blocks = [
            "basic_generator", "material_processor", "ore_washer",
            "storage_terminal"
        ]
        
        for block in directional_blocks:
            blockstate = {
                "variants": {}
            }
            
            # Add lit variants for machines that can be active
            if block in ["basic_generator", "material_processor", "ore_washer"]:
                for facing in ["north", "south", "west", "east"]:
                    for lit in ["true", "false"]:
                        y_rot = {"north": 0, "south": 180, "west": 270, "east": 90}[facing]
                        blockstate["variants"][f"facing={facing},lit={lit}"] = {
                            "model": f"astroexpansion:block/{block}",
                            "y": y_rot
                        }
            else:
                # Just facing for non-lit blocks
                for facing in ["north", "south", "west", "east"]:
                    y_rot = {"north": 0, "south": 180, "west": 270, "east": 90}[facing]
                    blockstate["variants"][f"facing={facing}"] = {
                        "model": f"astroexpansion:block/{block}",
                        "y": y_rot
                    }
                    
            path = blockstates_dir / f"{block}.json"
            with open(path, 'w') as f:
                json.dump(blockstate, f, indent=2)
            print(f"Generated blockstate: {block}.json")
            
        # Special blocks
        self.generate_storage_core_blockstate()
        self.generate_conduit_blockstate()
        self.generate_pipe_blockstate()
        self.generate_fluid_tank_blockstate()
        
    def generate_storage_core_blockstate(self):
        """Generate storage core blockstate with formed/powered states"""
        blockstate = {
            "variants": {}
        }
        
        for formed in ["true", "false"]:
            for powered in ["true", "false"]:
                model = "storage_core_active" if formed == "true" and powered == "true" else "storage_core"
                blockstate["variants"][f"formed={formed},powered={powered}"] = {
                    "model": f"astroexpansion:block/{model}"
                }
                
        path = self.assets_dir / "blockstates/storage_core.json"
        with open(path, 'w') as f:
            json.dump(blockstate, f, indent=2)
        print("Generated blockstate: storage_core.json")
        
    def generate_conduit_blockstate(self):
        """Generate energy conduit blockstate with all connection combinations"""
        blockstate = {
            "multipart": [
                # Core
                {"apply": {"model": "astroexpansion:block/energy_conduit_core"}},
                # Connections
                {"when": {"north": "true"}, "apply": {"model": "astroexpansion:block/energy_conduit_side"}},
                {"when": {"east": "true"}, "apply": {"model": "astroexpansion:block/energy_conduit_side", "y": 90}},
                {"when": {"south": "true"}, "apply": {"model": "astroexpansion:block/energy_conduit_side", "y": 180}},
                {"when": {"west": "true"}, "apply": {"model": "astroexpansion:block/energy_conduit_side", "y": 270}},
                {"when": {"up": "true"}, "apply": {"model": "astroexpansion:block/energy_conduit_side", "x": 270}},
                {"when": {"down": "true"}, "apply": {"model": "astroexpansion:block/energy_conduit_side", "x": 90}}
            ]
        }
        
        path = self.assets_dir / "blockstates/energy_conduit.json"
        with open(path, 'w') as f:
            json.dump(blockstate, f, indent=2)
        print("Generated blockstate: energy_conduit.json")
        
    def generate_pipe_blockstate(self):
        """Generate fluid pipe blockstate with all connection combinations"""
        blockstate = {
            "multipart": [
                # Core
                {"apply": {"model": "astroexpansion:block/fluid_pipe_core"}},
                # Connections
                {"when": {"north": "true"}, "apply": {"model": "astroexpansion:block/fluid_pipe_side"}},
                {"when": {"east": "true"}, "apply": {"model": "astroexpansion:block/fluid_pipe_side", "y": 90}},
                {"when": {"south": "true"}, "apply": {"model": "astroexpansion:block/fluid_pipe_side", "y": 180}},
                {"when": {"west": "true"}, "apply": {"model": "astroexpansion:block/fluid_pipe_side", "y": 270}},
                {"when": {"up": "true"}, "apply": {"model": "astroexpansion:block/fluid_pipe_side", "x": 270}},
                {"when": {"down": "true"}, "apply": {"model": "astroexpansion:block/fluid_pipe_side", "x": 90}}
            ]
        }
        
        path = self.assets_dir / "blockstates/fluid_pipe.json"
        with open(path, 'w') as f:
            json.dump(blockstate, f, indent=2)
        print("Generated blockstate: fluid_pipe.json")
        
    def generate_fluid_tank_blockstate(self):
        """Generate fluid tank blockstate"""
        blockstate = {
            "variants": {}
        }
        
        for facing in ["north", "south", "west", "east"]:
            y_rot = {"north": 0, "south": 180, "west": 270, "east": 90}[facing]
            blockstate["variants"][f"facing={facing}"] = {
                "model": "astroexpansion:block/fluid_tank",
                "y": y_rot
            }
            
        path = self.assets_dir / "blockstates/fluid_tank.json"
        with open(path, 'w') as f:
            json.dump(blockstate, f, indent=2)
        print("Generated blockstate: fluid_tank.json")
        
    def generate_block_models(self):
        """Generate block model JSON files"""
        models_dir = self.assets_dir / "models/block"
        models_dir.mkdir(parents=True, exist_ok=True)
        
        # Cube-all models (ores, storage blocks)
        cube_all_blocks = [
            ("titanium_ore", "block/titanium_ore"),
            ("deepslate_titanium_ore", "block/deepslate_titanium_ore"),
            ("titanium_block", "block/titanium_block"),
            ("lithium_ore", "block/lithium_ore"),
            ("deepslate_lithium_ore", "block/deepslate_lithium_ore"),
            ("lithium_block", "block/lithium_block"),
            ("uranium_ore", "block/uranium_ore"),
            ("deepslate_uranium_ore", "block/deepslate_uranium_ore"),
            ("uranium_block", "block/uranium_block"),
            ("energy_storage", "block/energy_storage"),
            ("storage_core", "block/storage_core"),
            ("storage_core_active", "block/storage_core"),
            ("fluid_tank", "block/fluid_tank")
        ]
        
        for model_name, texture in cube_all_blocks:
            model = {
                "parent": "minecraft:block/cube_all",
                "textures": {
                    "all": f"astroexpansion:{texture}"
                }
            }
            
            path = models_dir / f"{model_name}.json"
            with open(path, 'w') as f:
                json.dump(model, f, indent=2)
            print(f"Generated block model: {model_name}.json")
            
        # Orientable models (machines)
        orientable_blocks = [
            ("basic_generator", "block/basic_generator", "block/machine_top", "block/basic_generator"),
            ("material_processor", "block/material_processor", "block/machine_top", "block/material_processor"),
            ("ore_washer", "block/ore_washer", "block/machine_top", "block/ore_washer"),
            ("storage_terminal", "block/storage_terminal", "block/storage_terminal", "block/storage_terminal")
        ]
        
        for model_name, side, top, front in orientable_blocks:
            model = {
                "parent": "minecraft:block/orientable",
                "textures": {
                    "top": f"astroexpansion:{top}",
                    "front": f"astroexpansion:{front}",
                    "side": f"astroexpansion:{side}"
                }
            }
            
            path = models_dir / f"{model_name}.json"
            with open(path, 'w') as f:
                json.dump(model, f, indent=2)
            print(f"Generated block model: {model_name}.json")
            
        # Conduit and pipe models
        self.generate_conduit_models()
        self.generate_pipe_models()
        
    def generate_conduit_models(self):
        """Generate energy conduit models"""
        models_dir = self.assets_dir / "models/block"
        
        # Core model
        core_model = {
            "parent": "minecraft:block/block",
            "textures": {
                "texture": "astroexpansion:block/energy_conduit",
                "particle": "astroexpansion:block/energy_conduit"
            },
            "elements": [
                {
                    "from": [6, 6, 6],
                    "to": [10, 10, 10],
                    "faces": {
                        "north": {"uv": [6, 6, 10, 10], "texture": "#texture"},
                        "east": {"uv": [6, 6, 10, 10], "texture": "#texture"},
                        "south": {"uv": [6, 6, 10, 10], "texture": "#texture"},
                        "west": {"uv": [6, 6, 10, 10], "texture": "#texture"},
                        "up": {"uv": [6, 6, 10, 10], "texture": "#texture"},
                        "down": {"uv": [6, 6, 10, 10], "texture": "#texture"}
                    }
                }
            ]
        }
        
        path = models_dir / "energy_conduit_core.json"
        with open(path, 'w') as f:
            json.dump(core_model, f, indent=2)
        print("Generated block model: energy_conduit_core.json")
        
        # Side connection model
        side_model = {
            "parent": "minecraft:block/block",
            "textures": {
                "texture": "astroexpansion:block/energy_conduit",
                "particle": "astroexpansion:block/energy_conduit"
            },
            "elements": [
                {
                    "from": [6, 6, 0],
                    "to": [10, 10, 6],
                    "faces": {
                        "north": {"uv": [6, 6, 10, 10], "texture": "#texture"},
                        "east": {"uv": [10, 6, 16, 10], "texture": "#texture"},
                        "south": {"uv": [6, 6, 10, 10], "texture": "#texture"},
                        "west": {"uv": [0, 6, 6, 10], "texture": "#texture"},
                        "up": {"uv": [6, 10, 10, 16], "texture": "#texture"},
                        "down": {"uv": [6, 0, 10, 6], "texture": "#texture"}
                    }
                }
            ]
        }
        
        path = models_dir / "energy_conduit_side.json"
        with open(path, 'w') as f:
            json.dump(side_model, f, indent=2)
        print("Generated block model: energy_conduit_side.json")
        
    def generate_pipe_models(self):
        """Generate fluid pipe models"""
        models_dir = self.assets_dir / "models/block"
        
        # Core model
        core_model = {
            "parent": "minecraft:block/block",
            "textures": {
                "texture": "astroexpansion:block/fluid_pipe",
                "particle": "astroexpansion:block/fluid_pipe"
            },
            "elements": [
                {
                    "from": [5, 5, 5],
                    "to": [11, 11, 11],
                    "faces": {
                        "north": {"uv": [5, 5, 11, 11], "texture": "#texture"},
                        "east": {"uv": [5, 5, 11, 11], "texture": "#texture"},
                        "south": {"uv": [5, 5, 11, 11], "texture": "#texture"},
                        "west": {"uv": [5, 5, 11, 11], "texture": "#texture"},
                        "up": {"uv": [5, 5, 11, 11], "texture": "#texture"},
                        "down": {"uv": [5, 5, 11, 11], "texture": "#texture"}
                    }
                }
            ]
        }
        
        path = models_dir / "fluid_pipe_core.json"
        with open(path, 'w') as f:
            json.dump(core_model, f, indent=2)
        print("Generated block model: fluid_pipe_core.json")
        
        # Side connection model
        side_model = {
            "parent": "minecraft:block/block",
            "textures": {
                "texture": "astroexpansion:block/fluid_pipe",
                "particle": "astroexpansion:block/fluid_pipe"
            },
            "elements": [
                {
                    "from": [5, 5, 0],
                    "to": [11, 11, 5],
                    "faces": {
                        "north": {"uv": [5, 5, 11, 11], "texture": "#texture"},
                        "east": {"uv": [11, 5, 16, 11], "texture": "#texture"},
                        "south": {"uv": [5, 5, 11, 11], "texture": "#texture"},
                        "west": {"uv": [0, 5, 5, 11], "texture": "#texture"},
                        "up": {"uv": [5, 11, 11, 16], "texture": "#texture"},
                        "down": {"uv": [5, 0, 11, 5], "texture": "#texture"}
                    }
                }
            ]
        }
        
        path = models_dir / "fluid_pipe_side.json"
        with open(path, 'w') as f:
            json.dump(side_model, f, indent=2)
        print("Generated block model: fluid_pipe_side.json")
        
    def generate_item_models(self):
        """Generate item model JSON files"""
        item_models_dir = self.assets_dir / "models/item"
        item_models_dir.mkdir(parents=True, exist_ok=True)
        
        # Simple items (use generated texture)
        simple_items = [
            "raw_titanium", "titanium_ingot", "titanium_dust",
            "raw_lithium", "lithium_ingot", "lithium_dust",
            "raw_uranium", "uranium_ingot", "uranium_dust",
            "wrench", "titanium_plate",
            "storage_drive_1k", "storage_drive_4k", "storage_drive_16k", "storage_drive_64k",
            "storage_housing", "storage_processor",
            "drone_shell"
        ]
        
        for item in simple_items:
            model = {
                "parent": "minecraft:item/generated",
                "textures": {
                    "layer0": f"astroexpansion:item/{item}"
                }
            }
            
            path = item_models_dir / f"{item}.json"
            with open(path, 'w') as f:
                json.dump(model, f, indent=2)
            print(f"Generated item model: {item}.json")
            
        # Block items (use block model)
        block_items = [
            "titanium_ore", "deepslate_titanium_ore", "titanium_block",
            "lithium_ore", "deepslate_lithium_ore", "lithium_block",
            "uranium_ore", "deepslate_uranium_ore", "uranium_block",
            "basic_generator", "material_processor", "ore_washer",
            "energy_conduit", "energy_storage",
            "storage_core", "storage_terminal",
            "fluid_tank", "fluid_pipe"
        ]
        
        for item in block_items:
            model = {
                "parent": f"astroexpansion:block/{item}"
            }
            
            path = item_models_dir / f"{item}.json"
            with open(path, 'w') as f:
                json.dump(model, f, indent=2)
            print(f"Generated item model: {item}.json")


if __name__ == "__main__":
    mod_root = Path(__file__).parent
    generator = ModelGenerator(mod_root)
    generator.generate_all()