#!/usr/bin/env python3
"""
GUI Texture Generator for AstroExpansion
Generates all GUI textures including containers, widgets, and HUD elements
"""

import os
from PIL import Image, ImageDraw, ImageFont
import numpy as np
from texture_generator_unified import Colors, TextureGenerator

class GUITextureGenerator:
    def __init__(self, output_dir):
        self.output_dir = output_dir
        self.generator = TextureGenerator(output_dir)
        
    def create_gui_background(self, size=(256, 256), title_area=True):
        """Create standard GUI background"""
        img = Image.new('RGBA', size, Colors.UI_BG)
        draw = ImageDraw.Draw(img)
        
        # Outer border
        self._draw_rounded_rect(draw, [0, 0, size[0]-1, size[1]-1], 
                               Colors.UI_BORDER, Colors.UI_BG, radius=4)
        
        # Inner panel
        inner = [4, 4, size[0]-5, size[1]-5]
        self._draw_rounded_rect(draw, inner, Colors.UI_PANEL, Colors.UI_PANEL, radius=2)
        
        if title_area:
            # Title bar
            title_rect = [7, 7, size[0]-8, 24]
            self._draw_rounded_rect(draw, title_rect, Colors.UI_ACCENT, Colors.SPACE_DARK, radius=2)
            
            # Title highlight
            draw.rectangle([8, 8, size[0]-9, 9], fill=(*Colors.UI_ACCENT, 128))
        
        # Player inventory area
        inv_y = size[1] - 100
        inv_rect = [7, inv_y, size[0]-8, size[1]-8]
        self._draw_rounded_rect(draw, inv_rect, Colors.UI_BORDER, Colors.UI_BG, radius=2)
        
        # Inventory label area
        label_rect = [12, inv_y + 4, 80, inv_y + 14]
        draw.rectangle(label_rect, fill=Colors.SPACE_DARK)
        
        return img
    
    def _draw_rounded_rect(self, draw, coords, outline, fill, radius=5):
        """Draw rounded rectangle"""
        x1, y1, x2, y2 = coords
        
        # Main rectangle
        draw.rectangle([x1 + radius, y1, x2 - radius, y2], fill=fill)
        draw.rectangle([x1, y1 + radius, x2, y2 - radius], fill=fill)
        
        # Corners
        draw.pieslice([x1, y1, x1 + radius * 2, y1 + radius * 2], 180, 270, fill=fill)
        draw.pieslice([x2 - radius * 2, y1, x2, y1 + radius * 2], 270, 360, fill=fill)
        draw.pieslice([x1, y2 - radius * 2, x1 + radius * 2, y2], 90, 180, fill=fill)
        draw.pieslice([x2 - radius * 2, y2 - radius * 2, x2, y2], 0, 90, fill=fill)
        
        # Outline
        if outline != fill:
            draw.arc([x1, y1, x1 + radius * 2, y1 + radius * 2], 180, 270, fill=outline)
            draw.arc([x2 - radius * 2, y1, x2, y1 + radius * 2], 270, 360, fill=outline)
            draw.arc([x1, y2 - radius * 2, x1 + radius * 2, y2], 90, 180, fill=outline)
            draw.arc([x2 - radius * 2, y2 - radius * 2, x2, y2], 0, 90, fill=outline)
            draw.line([x1 + radius, y1, x2 - radius, y1], fill=outline)
            draw.line([x1 + radius, y2, x2 - radius, y2], fill=outline)
            draw.line([x1, y1 + radius, x1, y2 - radius], fill=outline)
            draw.line([x2, y1 + radius, x2, y2 - radius], fill=outline)
    
    def create_slot(self, size=(18, 18), highlighted=False, type='normal'):
        """Create inventory slot"""
        img = Image.new('RGBA', size, (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        if highlighted:
            # Glowing border for highlighted slots
            glow_color = Colors.UI_ACCENT
            for i in range(3, 0, -1):
                alpha = int(255 * (0.3 / i))
                draw.rectangle([3-i, 3-i, size[0]-4+i, size[1]-4+i], 
                             outline=(*glow_color, alpha))
        
        # Outer shadow
        draw.rectangle([0, 0, size[0]-1, size[1]-1], 
                      outline=Colors.SPACE_DARK)
        
        # Inner slot
        slot_color = Colors.UI_BG
        if type == 'output':
            slot_color = tuple(c - 10 for c in Colors.UI_BG)
        elif type == 'fuel':
            slot_color = (40, 20, 10)
        elif type == 'upgrade':
            slot_color = (20, 30, 50)
        
        draw.rectangle([1, 1, size[0]-2, size[1]-2], fill=slot_color)
        
        # Inner highlight for depth
        draw.line([1, 1, size[0]-2, 1], fill=Colors.METAL_DARK)
        draw.line([1, 1, 1, size[1]-2], fill=Colors.METAL_DARK)
        
        return img
    
    def create_progress_arrow(self, size=(24, 16), filled_ratio=0.0):
        """Create progress arrow for crafting"""
        img = Image.new('RGBA', size, (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # Arrow outline
        arrow_points = [
            (0, 7), (16, 7), (16, 3), (23, 8), (16, 13), (16, 9), (0, 9)
        ]
        
        # Background arrow (empty)
        draw.polygon(arrow_points, fill=Colors.UI_BG, outline=Colors.UI_BORDER)
        
        # Filled portion
        if filled_ratio > 0:
            filled_width = int(23 * filled_ratio)
            filled_points = []
            
            for x, y in arrow_points:
                if x <= filled_width:
                    filled_points.append((x, y))
                else:
                    # Interpolate edge
                    if filled_points and x > filled_width:
                        prev_x = filled_points[-1][0]
                        if prev_x < filled_width:
                            ratio = (filled_width - prev_x) / (x - prev_x)
                            new_y = filled_points[-1][1] + ratio * (y - filled_points[-1][1])
                            filled_points.append((filled_width, int(new_y)))
                    break
            
            if len(filled_points) > 2:
                draw.polygon(filled_points, fill=Colors.ENERGY_GREEN)
        
        return img
    
    def create_energy_bar(self, size=(16, 52), filled_ratio=0.0):
        """Create energy bar"""
        img = Image.new('RGBA', size, (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # Bar background
        draw.rectangle([0, 0, size[0]-1, size[1]-1], 
                      fill=Colors.SPACE_DARK, outline=Colors.UI_BORDER)
        
        # Energy fill
        if filled_ratio > 0:
            fill_height = int((size[1] - 4) * filled_ratio)
            fill_y = size[1] - 2 - fill_height
            
            # Gradient fill
            for y in range(fill_height):
                ratio = y / fill_height
                color = self._blend_colors(Colors.ENERGY_CYAN, Colors.ENERGY_BLUE, ratio)
                draw.line([2, fill_y + y, size[0]-3, fill_y + y], fill=color)
            
            # Energy glow effect
            if filled_ratio > 0.8:
                glow = self.generator.create_energy_glow((12, 12), Colors.ENERGY_CYAN, 0.5)
                img.paste(glow, (2, fill_y - 6), glow)
        
        # Tick marks
        for i in range(1, 5):
            y = int(size[1] * (i / 5))
            draw.line([0, y, 2, y], fill=Colors.UI_BORDER)
            draw.line([size[0]-3, y, size[0]-1, y], fill=Colors.UI_BORDER)
        
        return img
    
    def create_fluid_tank(self, size=(16, 52), fluid_color=(0, 100, 200), filled_ratio=0.0):
        """Create fluid tank display"""
        img = Image.new('RGBA', size, (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # Glass tank background
        draw.rectangle([0, 0, size[0]-1, size[1]-1], 
                      fill=(*Colors.UI_BG, 200), outline=Colors.UI_BORDER)
        
        # Glass shine effect
        draw.rectangle([1, 1, 2, size[1]-2], fill=(*Colors.METAL_SHINE, 100))
        
        # Fluid
        if filled_ratio > 0:
            fluid_height = int((size[1] - 4) * filled_ratio)
            fluid_y = size[1] - 2 - fluid_height
            
            # Fluid with transparency
            for y in range(fluid_height):
                # Add wave effect
                wave = int(np.sin((y + fluid_y) * 0.2) * 2)
                draw.line([2 + wave, fluid_y + y, size[0]-3 + wave, fluid_y + y], 
                         fill=(*fluid_color, 200))
            
            # Bubble effects
            if filled_ratio > 0.2:
                for _ in range(3):
                    bubble_x = np.random.randint(4, size[0]-4)
                    bubble_y = np.random.randint(fluid_y + 5, size[1]-5)
                    draw.ellipse([bubble_x-1, bubble_y-1, bubble_x+1, bubble_y+1], 
                               fill=(*Colors.METAL_SHINE, 150))
        
        return img
    
    def create_button(self, size=(200, 20), text="Button", state='normal'):
        """Create GUI button"""
        img = Image.new('RGBA', size, (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # Button appearance based on state
        if state == 'normal':
            bg_color = Colors.UI_PANEL
            border_color = Colors.UI_BORDER
            text_color = Colors.UI_TEXT
        elif state == 'hover':
            bg_color = tuple(min(255, c + 20) for c in Colors.UI_PANEL)
            border_color = Colors.UI_ACCENT
            text_color = Colors.UI_TEXT
        elif state == 'pressed':
            bg_color = Colors.UI_BG
            border_color = Colors.UI_ACCENT
            text_color = Colors.UI_ACCENT
        else:  # disabled
            bg_color = Colors.UI_BG
            border_color = Colors.METAL_DARK
            text_color = Colors.METAL_MID
        
        # Draw button
        self._draw_rounded_rect(draw, [0, 0, size[0]-1, size[1]-1], 
                               border_color, bg_color, radius=3)
        
        # 3D effect
        if state != 'pressed':
            # Top highlight
            draw.line([2, 1, size[0]-3, 1], fill=(*Colors.METAL_SHINE, 100))
            # Bottom shadow
            draw.line([2, size[1]-2, size[0]-3, size[1]-2], fill=(*Colors.METAL_DARK, 100))
        
        return img
    
    def create_tab(self, size=(28, 32), icon_type='crafting', selected=False):
        """Create GUI tab"""
        img = Image.new('RGBA', size, (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # Tab shape
        if selected:
            bg_color = Colors.UI_PANEL
            border_color = Colors.UI_ACCENT
        else:
            bg_color = Colors.UI_BG
            border_color = Colors.UI_BORDER
        
        # Draw tab with rounded top
        tab_points = [
            (0, size[1]), (0, 4), (4, 0), (size[0]-4, 0), 
            (size[0], 4), (size[0], size[1])
        ]
        draw.polygon(tab_points, fill=bg_color, outline=border_color)
        
        # Icon area
        icon_rect = [6, 6, size[0]-6, size[1]-8]
        draw.rectangle(icon_rect, fill=Colors.SPACE_DARK)
        
        # Simple icon based on type
        icon_color = Colors.UI_ACCENT if selected else Colors.UI_TEXT
        cx, cy = size[0]//2, 16
        
        if icon_type == 'crafting':
            # Crafting grid icon
            for x in [-3, 0, 3]:
                for y in [-3, 0, 3]:
                    draw.rectangle([cx+x-1, cy+y-1, cx+x+1, cy+y+1], fill=icon_color)
        elif icon_type == 'inventory':
            # Chest icon
            draw.rectangle([cx-4, cy-2, cx+4, cy+2], fill=icon_color)
            draw.rectangle([cx-5, cy-3, cx+5, cy-2], fill=icon_color)
        
        return img
    
    def _blend_colors(self, color1, color2, ratio):
        """Blend two colors"""
        return tuple(int(c1 * (1 - ratio) + c2 * ratio) for c1, c2 in zip(color1, color2))
    
    def generate_all_gui_textures(self):
        """Generate all GUI textures"""
        print("Generating GUI textures...")
        
        # Container GUIs
        containers = {
            'basic_generator': {'slots': [('fuel', 80, 45)], 'energy': True},
            'material_processor': {'slots': [('input', 56, 35), ('output', 116, 35)], 
                                 'progress': (79, 35), 'energy': True},
            'ore_washer': {'slots': [('input', 56, 35), ('output', 116, 35)], 
                          'progress': (79, 35), 'energy': True, 'fluid': True},
            'energy_storage': {'energy': True, 'large': True},
            'storage_terminal': {'search': True, 'grid': True},
            'rocket_workbench': {'special': 'rocket_assembly'},
        }
        
        for name, config in containers.items():
            print(f"  - {name}")
            gui = self.create_gui_background()
            draw = ImageDraw.Draw(gui)
            
            # Add slots
            if 'slots' in config:
                for slot_type, x, y in config['slots']:
                    slot = self.create_slot(type=slot_type)
                    gui.paste(slot, (x, y), slot)
            
            # Add progress arrow
            if 'progress' in config:
                arrow = self.create_progress_arrow()
                gui.paste(arrow, config['progress'], arrow)
            
            # Add energy bar
            if config.get('energy'):
                energy = self.create_energy_bar()
                x = 152 if config.get('large') else 8
                gui.paste(energy, (x, 24), energy)
            
            # Add fluid tank
            if config.get('fluid'):
                fluid = self.create_fluid_tank()
                gui.paste(fluid, (152, 24), fluid)
            
            # Save
            output_path = os.path.join(self.output_dir, 'gui', f'{name}.png')
            os.makedirs(os.path.dirname(output_path), exist_ok=True)
            gui.save(output_path)
        
        # Widgets
        print("\n  Generating widgets...")
        
        # Buttons (multiple states)
        button_img = Image.new('RGBA', (200, 60), (0, 0, 0, 0))
        for i, state in enumerate(['normal', 'hover', 'pressed']):
            btn = self.create_button(state=state)
            button_img.paste(btn, (0, i * 20), btn)
        
        output_path = os.path.join(self.output_dir, 'gui', 'widgets', 'button.png')
        os.makedirs(os.path.dirname(output_path), exist_ok=True)
        button_img.save(output_path)
        
        # Progress arrows (empty and full)
        for filled in [0.0, 1.0]:
            name = 'progress_arrow' if filled == 0.0 else 'progress_arrow_filled'
            arrow = self.create_progress_arrow(filled_ratio=filled)
            output_path = os.path.join(self.output_dir, 'gui', 'widgets', f'{name}.png')
            arrow.save(output_path)
        
        # Energy and fluid bars
        for filled in [0.0, 1.0]:
            suffix = '_empty' if filled == 0.0 else '_full'
            
            energy = self.create_energy_bar(filled_ratio=filled)
            output_path = os.path.join(self.output_dir, 'gui', 'widgets', f'energy_bar{suffix}.png')
            energy.save(output_path)
            
            fluid = self.create_fluid_tank(filled_ratio=filled)
            output_path = os.path.join(self.output_dir, 'gui', 'widgets', f'fluid_bar{suffix}.png')
            fluid.save(output_path)
        
        # Tabs
        for selected in [False, True]:
            suffix = '_selected' if selected else ''
            tab = self.create_tab(selected=selected)
            output_path = os.path.join(self.output_dir, 'gui', 'widgets', f'tab{suffix}.png')
            tab.save(output_path)
        
        # Slot highlight
        highlight = self.create_slot(highlighted=True)
        output_path = os.path.join(self.output_dir, 'gui', 'widgets', 'slot_highlight.png')
        highlight.save(output_path)
        
        print("\nGUI texture generation complete!")


def main():
    output_dir = "/workspaces/AstroExpansion/src/main/resources/assets/astroexpansion/textures"
    generator = GUITextureGenerator(output_dir)
    generator.generate_all_gui_textures()


if __name__ == "__main__":
    main()