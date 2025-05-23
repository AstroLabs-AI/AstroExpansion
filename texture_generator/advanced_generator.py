#!/usr/bin/env python3
"""
Advanced Texture Generator for Astro Expansion
Creates more sophisticated textures with effects and variations
"""

from PIL import Image, ImageDraw, ImageFilter, ImageEnhance
import numpy as np
import json
import random
from pathlib import Path
from typing import Dict, Tuple, List, Optional
import colorsys

from texture_generator import TextureGenerator

class AdvancedTextureGenerator(TextureGenerator):
    """Advanced texture generator with effects and procedural generation"""
    
    def __init__(self, output_dir: str = None, config: dict = None):
        # Call parent constructor
        if output_dir:
            super().__init__(output_dir)
        else:
            super().__init__()
            
        # Load config
        if config:
            self.config = config
        else:
            config_path = Path(__file__).parent / "texture_config.json"
            with open(config_path, 'r') as f:
                self.config = json.load(f)
        
        self.block_size = self.config['texture_settings']['block_size']
        self.item_size = self.config['texture_settings']['item_size']
        
    def generate_ore_texture(self, ore_type: str, base_type: str = "stone") -> Image.Image:
        """Generate realistic ore textures with veins"""
        size = self.block_size
        img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
        
        # Generate base texture
        base = self._generate_base_texture(base_type, size)
        img.paste(base, (0, 0))
        
        # Get ore color
        ore_config = self.config['material_overrides'].get(ore_type, {})
        ore_color = tuple(ore_config.get('base_color', [200, 200, 200]))
        
        # Generate ore veins
        ore_mask = self._generate_ore_veins(size)
        
        # Apply ore color with variation
        ore_layer = Image.new('RGBA', (size, size), (0, 0, 0, 0))
        pixels = ore_layer.load()
        mask_pixels = ore_mask.load()
        
        for y in range(size):
            for x in range(size):
                if mask_pixels[x, y] > 128:
                    # Add color variation
                    variation = random.randint(-20, 20)
                    color = tuple(max(0, min(255, c + variation)) for c in ore_color)
                    pixels[x, y] = color + (255,)
                    
        # Blend ore with base
        img = Image.alpha_composite(img, ore_layer)
        
        # Apply effects
        if ore_config.get('glowing'):
            img = self._add_glow_effect(img, ore_color)
            
        return img
        
    def generate_machine_texture(self, machine_name: str, tech_level: int = 1) -> Image.Image:
        """Generate detailed machine textures"""
        size = self.block_size
        img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # Get style configuration
        style_key = f"tech_level_{tech_level}"
        style = self.config['machine_styles'].get(style_key, self.config['machine_styles']['tech_level_1'])
        
        # Base machine color
        base_color = (140, 140, 140)
        dark_color = tuple(int(c * 0.7) for c in base_color)
        light_color = tuple(int(c * 1.3) for c in base_color)
        
        # Draw base
        draw.rectangle([0, 0, size-1, size-1], fill=base_color)
        
        # Add panel details based on style
        if style['panel_style'] == 'riveted':
            self._add_rivets(draw, size, dark_color)
        elif style['panel_style'] == 'smooth':
            self._add_smooth_panels(draw, size, dark_color, light_color)
        elif style['panel_style'] == 'seamless':
            self._add_futuristic_panels(draw, size, dark_color, light_color)
            
        # Add machine-specific details
        self._add_machine_details(draw, machine_name, size, tech_level)
        
        # Apply wear if configured
        if style.get('worn_edges'):
            img = self._add_wear(img)
            
        return img
        
    def generate_item_texture(self, item_type: str, material: str) -> Image.Image:
        """Generate detailed item textures"""
        size = self.item_size
        img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
        
        material_config = self.config['material_overrides'].get(material, {})
        base_color = tuple(material_config.get('base_color', [150, 150, 150]))
        
        if item_type == 'ingot':
            img = self._generate_ingot(size, base_color, material_config.get('metallic', False))
        elif item_type == 'dust':
            img = self._generate_dust(size, base_color)
        elif item_type == 'plate':
            img = self._generate_plate(size, base_color, material_config.get('metallic', False))
        elif item_type == 'component':
            img = self._generate_component(size, material)
        elif item_type == 'raw':
            img = self._generate_raw_ore(size, base_color)
            
        return img
        
    def generate_animated_texture(self, base_name: str, frames: int = 4) -> List[Image.Image]:
        """Generate animated texture frames"""
        base_img = self._get_base_texture(base_name)
        frames_list = []
        
        for i in range(frames):
            frame = base_img.copy()
            # Apply animation effect
            if 'energy' in base_name:
                frame = self._animate_energy(frame, i, frames)
            elif 'fluid' in base_name:
                frame = self._animate_fluid(frame, i, frames)
            elif 'uranium' in base_name:
                frame = self._animate_radioactive(frame, i, frames)
                
            frames_list.append(frame)
            
        return frames_list
        
    # Helper methods for texture generation
    
    def _generate_base_texture(self, base_type: str, size: int) -> Image.Image:
        """Generate base textures like stone or deepslate"""
        img = Image.new('RGBA', (size, size), (0, 0, 0, 255))
        pixels = img.load()
        
        if base_type == 'stone':
            base_gray = 120
            for y in range(size):
                for x in range(size):
                    noise = self._perlin_noise(x * 0.1, y * 0.1) * 30
                    gray = int(base_gray + noise)
                    pixels[x, y] = (gray, gray, gray, 255)
                    
        elif base_type == 'deepslate':
            base_gray = 60
            for y in range(size):
                for x in range(size):
                    # Layered appearance
                    layer_offset = (y % 4) * 5
                    noise = self._perlin_noise(x * 0.1, y * 0.1) * 20
                    gray = int(base_gray + noise - layer_offset)
                    pixels[x, y] = (gray, gray, gray + 10, 255)
                    
        return img
        
    def _generate_ore_veins(self, size: int) -> Image.Image:
        """Generate ore vein patterns"""
        img = Image.new('L', (size, size), 0)
        draw = ImageDraw.Draw(img)
        
        # Create vein pattern
        num_veins = random.randint(3, 5)
        for _ in range(num_veins):
            # Random vein path
            points = []
            x = random.randint(0, size)
            y = random.randint(0, size)
            
            for _ in range(random.randint(3, 6)):
                points.append((x, y))
                x = max(0, min(size, x + random.randint(-4, 4)))
                y = max(0, min(size, y + random.randint(-4, 4)))
                
            # Draw thick vein
            if len(points) > 1:
                draw.line(points, fill=255, width=random.randint(2, 3))
                
        # Blur for natural appearance
        img = img.filter(ImageFilter.GaussianBlur(0.5))
        
        return img
        
    def _add_glow_effect(self, img: Image.Image, color: Tuple[int, int, int]) -> Image.Image:
        """Add glowing effect to texture"""
        # Create glow layer
        glow = img.copy()
        glow = glow.filter(ImageFilter.GaussianBlur(2))
        
        # Tint glow
        enhancer = ImageEnhance.Color(glow)
        glow = enhancer.enhance(2.0)
        
        # Composite
        img = Image.blend(img, glow, 0.3)
        
        return img
        
    def _add_rivets(self, draw: ImageDraw.Draw, size: int, color: Tuple[int, int, int]):
        """Add rivet details to machine texture"""
        rivet_positions = [(2, 2), (size-3, 2), (2, size-3), (size-3, size-3)]
        for x, y in rivet_positions:
            draw.ellipse([x, y, x+1, y+1], fill=color)
            
    def _add_smooth_panels(self, draw: ImageDraw.Draw, size: int, dark: Tuple[int, int, int], light: Tuple[int, int, int]):
        """Add smooth panel details"""
        # Panel lines
        draw.line([3, 0, 3, size], fill=dark, width=1)
        draw.line([size-4, 0, size-4, size], fill=dark, width=1)
        draw.line([0, 3, size, 3], fill=light, width=1)
        draw.line([0, size-4, size, size-4], fill=dark, width=1)
        
    def _add_futuristic_panels(self, draw: ImageDraw.Draw, size: int, dark: Tuple[int, int, int], light: Tuple[int, int, int]):
        """Add futuristic panel details"""
        # Hexagonal patterns
        for y in range(0, size, 6):
            for x in range(0, size, 5):
                if y % 12 == 0:
                    x += 2
                points = self._get_hex_points(x + 3, y + 3, 2)
                draw.polygon(points, outline=light)
                
    def _add_machine_details(self, draw: ImageDraw.Draw, machine_name: str, size: int, tech_level: int):
        """Add machine-specific details"""
        accent_color = (100, 200, 255) if tech_level >= 2 else (200, 100, 0)
        
        if 'generator' in machine_name:
            # Power indicator
            draw.rectangle([size//2-2, size//2-2, size//2+2, size//2+2], fill=accent_color)
        elif 'processor' in machine_name:
            # Processing slots
            draw.rectangle([3, 3, 6, 6], fill=(80, 80, 80))
            draw.rectangle([size-7, size-7, size-4, size-4], fill=(80, 80, 80))
        elif 'storage' in machine_name:
            # Data lights
            for i in range(3):
                y = 4 + i * 4
                draw.ellipse([size-5, y, size-3, y+2], fill=accent_color)
                
    def _generate_ingot(self, size: int, color: Tuple[int, int, int], metallic: bool) -> Image.Image:
        """Generate realistic ingot texture"""
        img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # Ingot shape
        ingot_coords = [(3, 5), (12, 5), (13, 6), (13, 9), (12, 10), (3, 10), (2, 9), (2, 6)]
        
        # Base color
        draw.polygon(ingot_coords, fill=color)
        
        if metallic:
            # Add metallic shading
            # Highlight
            highlight = tuple(min(255, int(c * 1.4)) for c in color)
            draw.polygon([(3, 5), (12, 5), (12, 6), (3, 6)], fill=highlight)
            draw.polygon([(2, 6), (3, 6), (3, 9), (2, 9)], fill=highlight)
            
            # Shadow
            shadow = tuple(int(c * 0.6) for c in color)
            draw.polygon([(3, 9), (12, 9), (12, 10), (3, 10)], fill=shadow)
            draw.polygon([(12, 6), (13, 6), (13, 9), (12, 9)], fill=shadow)
            
        return img
        
    def _generate_dust(self, size: int, color: Tuple[int, int, int]) -> Image.Image:
        """Generate dust pile texture"""
        img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
        
        # Create dust particles
        num_particles = 100
        for _ in range(num_particles):
            x = random.gauss(size/2, size/4)
            y = random.gauss(size*0.7, size/6)
            
            if 0 <= x < size and 0 <= y < size:
                # Vary particle color
                variation = random.randint(-30, 30)
                particle_color = tuple(max(0, min(255, c + variation)) for c in color)
                
                # Vary particle size
                particle_size = random.choice([1, 1, 1, 2])
                if particle_size == 1:
                    img.putpixel((int(x), int(y)), particle_color + (255,))
                else:
                    draw = ImageDraw.Draw(img)
                    draw.ellipse([int(x), int(y), int(x)+1, int(y)+1], fill=particle_color + (200,))
                    
        return img
        
    def _generate_plate(self, size: int, color: Tuple[int, int, int], metallic: bool) -> Image.Image:
        """Generate metal plate texture"""
        img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # Plate shape
        draw.rectangle([2, 3, size-3, size-4], fill=color)
        
        if metallic:
            # Add brushed metal effect
            for y in range(3, size-4):
                for x in range(2, size-3):
                    if random.random() < 0.3:
                        variation = random.randint(-10, 10)
                        pixel_color = tuple(max(0, min(255, c + variation)) for c in color)
                        img.putpixel((x, y), pixel_color + (255,))
                        
            # Edge highlights
            highlight = tuple(min(255, int(c * 1.2)) for c in color)
            draw.line([2, 3, size-3, 3], fill=highlight)
            draw.line([2, 3, 2, size-4], fill=highlight)
            
        # Screw holes
        draw.ellipse([3, 4, 4, 5], fill=(60, 60, 60))
        draw.ellipse([size-5, 4, size-4, 5], fill=(60, 60, 60))
        draw.ellipse([3, size-6, 4, size-5], fill=(60, 60, 60))
        draw.ellipse([size-5, size-6, size-4, size-5], fill=(60, 60, 60))
        
        return img
        
    def _generate_component(self, size: int, material: str) -> Image.Image:
        """Generate electronic component texture"""
        img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # PCB base
        pcb_color = (40, 100, 40) if material == 'tech' else (40, 40, 100)
        draw.rectangle([2, 2, size-3, size-3], fill=pcb_color)
        
        # Circuit traces
        trace_color = (200, 180, 100)
        # Horizontal traces
        for y in [4, 7, 10]:
            draw.line([3, y, size-4, y], fill=trace_color, width=1)
            
        # Components
        if material == 'tech':
            # Chips
            draw.rectangle([4, 4, 7, 6], fill=(20, 20, 20))
            draw.rectangle([8, 8, 11, 10], fill=(20, 20, 20))
            # Capacitors
            draw.ellipse([4, 9, 5, 10], fill=(100, 100, 200))
            draw.ellipse([10, 5, 11, 6], fill=(100, 100, 200))
        else:
            # Energy components
            # Crystal
            draw.polygon([(size//2, 4), (size//2+2, 7), (size//2, 10), (size//2-2, 7)], 
                        fill=(255, 100, 255))
            
        return img
        
    def _generate_raw_ore(self, size: int, color: Tuple[int, int, int]) -> Image.Image:
        """Generate raw ore chunk texture"""
        img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # Irregular chunk shape
        points = []
        num_points = 8
        for i in range(num_points):
            angle = (i / num_points) * 2 * 3.14159
            radius = size/3 + random.randint(-2, 2)
            x = size/2 + radius * np.cos(angle)
            y = size/2 + radius * np.sin(angle)
            points.append((int(x), int(y)))
            
        # Base chunk
        draw.polygon(points, fill=color)
        
        # Add rough texture
        for _ in range(20):
            x = random.randint(4, size-5)
            y = random.randint(4, size-5)
            if self._point_in_polygon(x, y, points):
                variation = random.randint(-40, 40)
                spot_color = tuple(max(0, min(255, c + variation)) for c in color)
                draw.ellipse([x, y, x+1, y+1], fill=spot_color)
                
        # Edge shading
        edge_color = tuple(int(c * 0.7) for c in color)
        draw.polygon(points, outline=edge_color, width=1)
        
        return img
        
    def _perlin_noise(self, x: float, y: float) -> float:
        """Simple perlin noise implementation"""
        # Simplified version for texture generation
        return np.sin(x * 2) * np.cos(y * 2) * 0.5
        
    def _get_hex_points(self, cx: int, cy: int, radius: int) -> List[Tuple[int, int]]:
        """Get hexagon points"""
        points = []
        for i in range(6):
            angle = i * 60 * 3.14159 / 180
            x = cx + radius * np.cos(angle)
            y = cy + radius * np.sin(angle)
            points.append((int(x), int(y)))
        return points
        
    def _point_in_polygon(self, x: int, y: int, poly: List[Tuple[int, int]]) -> bool:
        """Check if point is inside polygon"""
        n = len(poly)
        inside = False
        j = n - 1
        
        for i in range(n):
            xi, yi = poly[i]
            xj, yj = poly[j]
            
            if ((yi > y) != (yj > y)) and (x < (xj - xi) * (y - yi) / (yj - yi) + xi):
                inside = not inside
            j = i
            
        return inside
        
    def _add_wear(self, img: Image.Image) -> Image.Image:
        """Add wear and tear to texture"""
        img_array = np.array(img)
        
        # Add scratches
        for _ in range(random.randint(2, 5)):
            start_x = random.randint(0, img.width)
            start_y = random.randint(0, img.height)
            end_x = start_x + random.randint(-5, 5)
            end_y = start_y + random.randint(-5, 5)
            
            # Draw scratch
            draw = ImageDraw.Draw(img)
            draw.line([start_x, start_y, end_x, end_y], 
                     fill=(80, 80, 80, 100), width=1)
            
        return img
        
    def _animate_energy(self, img: Image.Image, frame: int, total_frames: int) -> Image.Image:
        """Animate energy effects"""
        # Create pulsing glow
        intensity = (np.sin(frame / total_frames * 2 * np.pi) + 1) / 2
        
        enhancer = ImageEnhance.Brightness(img)
        return enhancer.enhance(1 + intensity * 0.3)
        
    def _animate_fluid(self, img: Image.Image, frame: int, total_frames: int) -> Image.Image:
        """Animate fluid movement"""
        # Simple wave effect
        img_array = np.array(img)
        height, width = img_array.shape[:2]
        
        # Create wave displacement
        for y in range(height):
            offset = int(np.sin((y + frame * 2) / 4) * 2)
            if 0 < offset < width:
                img_array[y] = np.roll(img_array[y], offset, axis=0)
                
        return Image.fromarray(img_array)
        
    def _animate_radioactive(self, img: Image.Image, frame: int, total_frames: int) -> Image.Image:
        """Animate radioactive glow"""
        # Pulsing green glow
        glow_layer = Image.new('RGBA', img.size, (0, 255, 0, 0))
        
        # Create glow mask based on brightness
        img_gray = img.convert('L')
        threshold = 128
        mask = img_gray.point(lambda x: 255 if x > threshold else 0)
        
        # Apply pulsing alpha
        alpha = int((np.sin(frame / total_frames * 2 * np.pi) + 1) / 2 * 100)
        glow_layer.putalpha(alpha)
        
        # Composite
        result = Image.alpha_composite(img, glow_layer)
        
        return result
    
    def generate_all(self):
        """Generate all textures using advanced techniques"""
        # Use the parent class method which will call our overridden texture generation methods
        return super().generate_all()
    
    def generate_block_textures(self):
        """Override parent method to use advanced generation"""
        blocks = {
            # Ores
            'titanium_ore': lambda: self.generate_ore_texture('titanium', 'stone'),
            'deepslate_titanium_ore': lambda: self.generate_ore_texture('titanium', 'deepslate'),
            'lithium_ore': lambda: self.generate_ore_texture('lithium', 'stone'),
            'deepslate_lithium_ore': lambda: self.generate_ore_texture('lithium', 'deepslate'),
            'uranium_ore': lambda: self.generate_ore_texture('uranium', 'stone'),
            'deepslate_uranium_ore': lambda: self.generate_ore_texture('uranium', 'deepslate'),
            
            # Machines
            'basic_generator': lambda: self.generate_machine_texture('generator', tech_level=1),
            'material_processor': lambda: self.generate_machine_texture('processor', tech_level=1),
            'ore_washer': lambda: self.generate_machine_texture('washer', tech_level=1),
            'energy_storage': lambda: self.generate_machine_texture('battery', tech_level=1),
            'component_assembler': lambda: self.generate_machine_texture('assembler', tech_level=2),
            'industrial_furnace_controller': lambda: self.generate_machine_texture('furnace', tech_level=2),
            'fusion_reactor_controller': lambda: self.generate_machine_texture('reactor', tech_level=3),
            'quantum_computer_controller': lambda: self.generate_machine_texture('quantum', tech_level=3),
        }
        
        # Generate each texture
        for name, generator_func in blocks.items():
            if name in self.missing_textures['blocks']:
                path = self.output_dir / "block" / f"{name}.png"
                path.parent.mkdir(parents=True, exist_ok=True)
                texture = generator_func()
                texture.save(path)
                print(f"Generated advanced block texture: {name}")
        
        # Use parent method for remaining blocks
        remaining_blocks = [block for block in self.missing_textures['blocks'] if block not in blocks]
        if remaining_blocks:
            # Temporarily modify missing_textures to only include remaining blocks
            original_blocks = self.missing_textures['blocks']
            self.missing_textures['blocks'] = remaining_blocks
            super().generate_block_textures()
            self.missing_textures['blocks'] = original_blocks
    
    def generate_item_textures(self):
        """Override parent method to use advanced generation"""
        items = {
            # Materials
            'titanium_ingot': lambda: self.generate_item_texture('ingot', 'titanium'),
            'titanium_dust': lambda: self._generate_dust(16, tuple(self.config['material_overrides']['titanium']['base_color'])),
            'titanium_plate': lambda: self.generate_item_texture('plate', 'titanium'),
            'raw_titanium': lambda: self.generate_item_texture('raw', 'titanium'),
            
            # Components
            'circuit_board': lambda: self.generate_item_texture('component', 'circuit'),
            'processor': lambda: self.generate_item_texture('component', 'processor'),
            'advanced_processor': lambda: self.generate_item_texture('component', 'advanced'),
            'energy_core': lambda: self.generate_item_texture('component', 'energy'),
            'drone_core': lambda: self.generate_item_texture('component', 'drone'),
        }
        
        # Generate each texture
        for name, generator_func in items.items():
            if name in self.missing_textures['items']:
                path = self.output_dir / "item" / f"{name}.png"
                path.parent.mkdir(parents=True, exist_ok=True)
                texture = generator_func()
                texture.save(path)
                print(f"Generated advanced item texture: {name}")
        
        # Use parent method for remaining items
        remaining_items = [item for item in self.missing_textures['items'] if item not in items]
        if remaining_items:
            # Temporarily modify missing_textures to only include remaining items
            original_items = self.missing_textures['items']
            self.missing_textures['items'] = remaining_items
            super().generate_item_textures()
            self.missing_textures['items'] = original_items
    
    def generate_gui_textures(self):
        """Override parent method to use advanced generation"""
        # Use parent's GUI list but with advanced generation
        super().generate_gui_textures()
    
    def generate_entity_textures(self):
        """Override parent method to use advanced generation"""
        # Use parent's entity list but with advanced generation
        super().generate_entity_textures()


if __name__ == "__main__":
    # Example usage
    generator = AdvancedTextureGenerator()
    
    # Generate some example textures
    titanium_ore = generator.generate_ore_texture("titanium", "stone")
    titanium_ore.save("titanium_ore_example.png")
    
    fusion_reactor = generator.generate_machine_texture("fusion_reactor", tech_level=3)
    fusion_reactor.save("fusion_reactor_example.png")
    
    titanium_ingot = generator.generate_item_texture("ingot", "titanium")
    titanium_ingot.save("titanium_ingot_example.png")