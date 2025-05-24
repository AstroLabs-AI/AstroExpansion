package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.client.gui.widgets.AnimatedProgressArrow;
import com.astrolabs.astroexpansion.client.gui.widgets.PulsingEnergyBar;
import com.astrolabs.astroexpansion.client.gui.widgets.RotatingGear;
import com.astrolabs.astroexpansion.common.items.upgrades.UpgradeItem;
import com.astrolabs.astroexpansion.common.menu.MaterialProcessorMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MaterialProcessorScreen extends AbstractContainerScreen<MaterialProcessorMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstroExpansion.MODID, "textures/gui/material_processor.png");
    
    private PulsingEnergyBar energyBar;
    private AnimatedProgressArrow progressArrow;
    private RotatingGear gear1;
    private RotatingGear gear2;
    
    public MaterialProcessorScreen(MaterialProcessorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
    
    @Override
    protected void init() {
        super.init();
        // Standard label positions to avoid overlap
        this.inventoryLabelY = 72; // Player inventory starts at Y=84
        this.titleLabelY = 6;
        
        // Initialize animated widgets
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
        // Energy bar widget
        energyBar = new PulsingEnergyBar(x + 152, y + 17, 16, 52, TEXTURE, menu.getMaxEnergyStored());
        addRenderableWidget(energyBar);
        
        // Progress arrow widget
        progressArrow = new AnimatedProgressArrow(x + 79, y + 35, 24, 17, TEXTURE, 176, 14);
        addRenderableWidget(progressArrow);
        
        // Rotating gears
        gear1 = new RotatingGear(x + 26, y + 28, 32, TEXTURE, 176, 101);
        gear1.setRotationSpeed(0.5f);
        addRenderableWidget(gear1);
        
        gear2 = new RotatingGear(x + 118, y + 28, 32, TEXTURE, 208, 101);
        gear2.setRotationSpeed(-0.5f); // Rotate opposite direction
        addRenderableWidget(gear2);
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        
        // Update animated widgets
        energyBar.setEnergy(menu.getEnergyStored());
        energyBar.setFlowing(menu.isCrafting() && menu.getEnergyStored() > 0);
        
        progressArrow.setProgress(menu.isCrafting() ? menu.getScaledProgress() / 24.0f : 0.0f);
        
        gear1.setProcessing(menu.isCrafting());
        gear2.setProcessing(menu.isCrafting());
        
        // Render upgrade slot backgrounds with hover effects
        for (int i = 0; i < 4; i++) {
            int slotX = x + 44 + i * 18;
            int slotY = y + 17;
            
            // Draw special upgrade slot background from texture
            graphics.blit(TEXTURE, slotX - 1, slotY - 1, 176, 83, 18, 18);
            
            // Add subtle glow effect when hovering
            if (isHovering(44 + i * 18, 17, 16, 16, mouseX, mouseY)) {
                graphics.fillGradient(slotX, slotY, slotX + 16, slotY + 16,
                    0x40FFFFFF, 0x20FFFFFF);
            }
        }
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
        
        // Render energy tooltip
        if (isHovering(152, 17, 16, 52, mouseX, mouseY)) {
            graphics.renderTooltip(this.font, Component.literal(menu.getEnergyStored() + " / " + menu.getMaxEnergyStored() + " FE"), mouseX, mouseY);
        }
        
        // Render upgrade tooltips
        for (int i = 0; i < 4; i++) {
            if (isHovering(44 + i * 18, 17, 16, 16, mouseX, mouseY)) {
                Slot slot = menu.getSlot(36 + 2 + i); // 36 player slots + 2 main slots + upgrade index
                ItemStack stack = slot.getItem();
                
                List<Component> tooltip = new ArrayList<>();
                if (stack.isEmpty()) {
                    tooltip.add(Component.literal("Upgrade Slot").withStyle(ChatFormatting.GRAY));
                    tooltip.add(Component.literal("Insert an upgrade to enhance").withStyle(ChatFormatting.DARK_GRAY));
                    tooltip.add(Component.literal("this machine's performance").withStyle(ChatFormatting.DARK_GRAY));
                } else if (stack.getItem() instanceof UpgradeItem upgrade) {
                    // Don't show item tooltip - we'll show upgrade stats instead
                    tooltip.add(Component.literal(stack.getHoverName().getString()).withStyle(ChatFormatting.YELLOW));
                    tooltip.add(Component.literal("Type: " + upgrade.getType().getName()).withStyle(ChatFormatting.GRAY));
                    tooltip.add(Component.literal("Tier: " + upgrade.getTier()).withStyle(ChatFormatting.GRAY));
                    tooltip.add(Component.literal("Effect: +" + (int)((upgrade.getModifier() - 1.0f) * 100) + "%").withStyle(ChatFormatting.GREEN));
                }
                
                if (!tooltip.isEmpty()) {
                    graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
                }
            }
        }
        
        // Render machine stats tooltip
        if (isHovering(7, 7, 50, 10, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("Machine Stats").withStyle(ChatFormatting.GOLD));
            
            float speedMultiplier = menu.blockEntity.getSpeedMultiplier();
            float efficiencyMultiplier = menu.blockEntity.getEfficiencyMultiplier();
            float fortuneMultiplier = menu.blockEntity.getFortuneMultiplier();
            
            tooltip.add(Component.literal("Processing Speed: " + String.format("%.0f%%", speedMultiplier * 100))
                .withStyle(speedMultiplier > 1.0f ? ChatFormatting.GREEN : ChatFormatting.GRAY));
            tooltip.add(Component.literal("Energy Efficiency: " + String.format("%.0f%%", efficiencyMultiplier * 100))
                .withStyle(efficiencyMultiplier > 1.0f ? ChatFormatting.GREEN : ChatFormatting.GRAY));
            tooltip.add(Component.literal("Fortune Bonus: " + String.format("%.0f%%", fortuneMultiplier * 100))
                .withStyle(fortuneMultiplier > 1.0f ? ChatFormatting.GREEN : ChatFormatting.GRAY));
            
            graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }
}