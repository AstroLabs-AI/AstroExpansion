package com.astrolabs.astroexpansion.client.gui;

import com.astrolabs.astroexpansion.api.multiblock.IMultiblockMatcher;
import com.astrolabs.astroexpansion.api.multiblock.MultiblockMatcher;
import com.astrolabs.astroexpansion.client.renderer.MultiblockPreviewHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class MultiblockTooltipRenderer {
    
    private static final Map<Character, String> BLOCK_NAMES = new java.util.HashMap<>();
    static {
        // Fusion Reactor
        BLOCK_NAMES.put('C', "Controller Block");
        BLOCK_NAMES.put('L', "Fusion Reactor Casing");
        BLOCK_NAMES.put('O', "Fusion Coil");
        
        // Industrial Furnace
        BLOCK_NAMES.put('F', "Furnace Casing");
        
        // Fuel Refinery
        BLOCK_NAMES.put('R', "Refinery Casing");
        BLOCK_NAMES.put('D', "Distillation Column");
        
        // Quantum Computer
        BLOCK_NAMES.put('Q', "Quantum Casing");
        BLOCK_NAMES.put('K', "Quantum Core");
        
        // Rocket Assembly
        BLOCK_NAMES.put('S', "Rocket Hull");
        BLOCK_NAMES.put('E', "Rocket Engine");
        BLOCK_NAMES.put('T', "Rocket Fuel Tank");
        BLOCK_NAMES.put('N', "Rocket Nose Cone");
        BLOCK_NAMES.put('P', "Launch Pad");
        BLOCK_NAMES.put('H', "Assembly Frame");
        
        // Generic
        BLOCK_NAMES.put(' ', "Air");
        BLOCK_NAMES.put('A', "Any Block");
    }
    
    public static void renderMissingBlocksTooltip(GuiGraphics graphics, 
                                                  Map<Character, Integer> missingBlocks,
                                                  Map<Character, IMultiblockMatcher> matchers,
                                                  int mouseX, int mouseY) {
        if (missingBlocks.isEmpty()) return;
        
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        
        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal("Missing Blocks:").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        
        for (Map.Entry<Character, Integer> entry : missingBlocks.entrySet()) {
            char key = entry.getKey();
            int count = entry.getValue();
            
            String blockName = BLOCK_NAMES.getOrDefault(key, "Unknown Block");
            IMultiblockMatcher matcher = matchers.get(key);
            
            // Use the default name from BLOCK_NAMES map
            
            Component line = Component.literal("  • ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(count + "x ").withStyle(ChatFormatting.WHITE))
                .append(Component.literal(blockName).withStyle(ChatFormatting.YELLOW));
            
            lines.add(line);
        }
        
        // Calculate tooltip dimensions
        int width = 0;
        for (Component line : lines) {
            int lineWidth = font.width(line);
            if (lineWidth > width) width = lineWidth;
        }
        
        int height = lines.size() * 10 + 4;
        int x = mouseX + 12;
        int y = mouseY - 12;
        
        // Ensure tooltip stays on screen
        if (x + width + 6 > mc.getWindow().getGuiScaledWidth()) {
            x = mc.getWindow().getGuiScaledWidth() - width - 6;
        }
        if (y + height + 6 > mc.getWindow().getGuiScaledHeight()) {
            y = mc.getWindow().getGuiScaledHeight() - height - 6;
        }
        
        // Render background
        graphics.fillGradient(x - 3, y - 3, x + width + 3, y + height + 3, 
            0xF0100010, 0xF0100010);
        graphics.fillGradient(x - 2, y - 2, x + width + 2, y + height + 2, 
            0x505000FF, 0x505000FF);
        
        // Render text
        int textY = y;
        for (Component line : lines) {
            graphics.drawString(font, line, x, textY, 0xFFFFFF);
            textY += 10;
        }
    }
    
}