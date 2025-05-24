package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.client.gui.widgets.PulsingEnergyBar;
import com.astrolabs.astroexpansion.client.gui.widgets.SparkingEffect;
import com.astrolabs.astroexpansion.common.items.upgrades.UpgradeItem;
import com.astrolabs.astroexpansion.common.menu.BasicGeneratorMenu;
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

public class BasicGeneratorScreen extends AbstractContainerScreen<BasicGeneratorMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstroExpansion.MODID, "textures/gui/basic_generator.png");
    
    private PulsingEnergyBar energyBar;
    private SparkingEffect sparkEffect;
    
    public BasicGeneratorScreen(BasicGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
    
    @Override
    protected void init() {
        super.init();
        // Standard inventory label position (player inventory starts at Y=99)
        this.inventoryLabelY = 88;
        this.titleLabelY = 6;
        
        // Initialize animated widgets
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
        // Energy bar widget
        energyBar = new PulsingEnergyBar(x + 152, y + 17, 16, 52, TEXTURE, menu.getMaxEnergyStored());
        addRenderableWidget(energyBar);
        
        // Sparking effect around the fuel slot when generating
        sparkEffect = new SparkingEffect(x + 55, y + 42, 32, 32, TEXTURE);
        addRenderableWidget(sparkEffect);
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        
        // Render burn progress (flame icon) with animated effect
        if (menu.isCrafting()) {
            int flameHeight = menu.getScaledProgress();
            graphics.blit(TEXTURE, x + 63, y + 36 + 14 - flameHeight, 176, 14 - flameHeight, 14, flameHeight);
            
            // Add flickering glow effect
            float flicker = (float) Math.sin(System.currentTimeMillis() / 100.0) * 0.3f + 0.7f;
            graphics.setColor(1.0f, 1.0f, 1.0f, flicker * 0.5f);
            graphics.blit(TEXTURE, x + 62, y + 35 + 14 - flameHeight - 1, 190, 0, 16, flameHeight + 2);
            graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        
        // Update animated widgets
        energyBar.setEnergy(menu.getEnergyStored());
        energyBar.setFlowing(menu.isCrafting() && menu.getEnergyStored() < menu.getMaxEnergyStored());
        
        sparkEffect.setGenerating(menu.isCrafting());
        sparkEffect.setIntensity(menu.getEnergyPerTick() / 40.0f); // Scale intensity by generation rate
        
        // Render upgrade slot backgrounds with hover effects
        for (int i = 0; i < 4; i++) {
            int slotX = x + 44 + i * 18;
            int slotY = y + 17;
            
            // Draw special upgrade slot background from texture
            graphics.blit(TEXTURE, slotX - 1, slotY - 1, 176, 91, 18, 18);
            
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
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("Energy: " + menu.getEnergyStored() + " / " + menu.getMaxEnergyStored() + " FE"));
            tooltip.add(Component.literal("Generation: " + menu.getEnergyPerTick() + " FE/t").withStyle(ChatFormatting.GRAY));
            graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }
        
        // Render upgrade tooltips
        for (int i = 0; i < 4; i++) {
            if (isHovering(44 + i * 18, 17, 16, 16, mouseX, mouseY)) {
                Slot slot = menu.getSlot(36 + 1 + i); // 36 player slots + 1 fuel slot + upgrade index
                ItemStack stack = slot.getItem();
                
                List<Component> tooltip = new ArrayList<>();
                if (stack.isEmpty()) {
                    tooltip.add(Component.literal("Upgrade Slot").withStyle(ChatFormatting.GRAY));
                    tooltip.add(Component.literal("Insert an upgrade to enhance").withStyle(ChatFormatting.DARK_GRAY));
                    tooltip.add(Component.literal("this generator's performance").withStyle(ChatFormatting.DARK_GRAY));
                } else if (stack.getItem() instanceof UpgradeItem upgrade) {
                    // Don't show item tooltip - we'll show upgrade stats instead
                    tooltip.add(Component.literal(stack.getHoverName().getString()).withStyle(ChatFormatting.YELLOW));
                    tooltip.add(Component.literal("Type: " + upgrade.getType().getName()).withStyle(ChatFormatting.GRAY));
                    tooltip.add(Component.literal("Tier: " + upgrade.getTier()).withStyle(ChatFormatting.GRAY));
                    
                    // Show what the upgrade does for generators
                    switch (upgrade.getType()) {
                        case SPEED -> {
                            tooltip.add(Component.literal("Effect: +" + (int)((upgrade.getModifier() - 1.0f) * 100) + "% Energy/tick").withStyle(ChatFormatting.GREEN));
                            tooltip.add(Component.literal("Increases energy generation rate").withStyle(ChatFormatting.DARK_GRAY));
                        }
                        case EFFICIENCY -> {
                            tooltip.add(Component.literal("Effect: +" + (int)((upgrade.getModifier() - 1.0f) * 100) + "% Fuel Duration").withStyle(ChatFormatting.GREEN));
                            tooltip.add(Component.literal("Makes fuel last longer").withStyle(ChatFormatting.DARK_GRAY));
                        }
                        case FORTUNE -> {
                            tooltip.add(Component.literal("Effect: +" + (int)((upgrade.getModifier() - 1.0f) * 100) + "% Bonus Energy").withStyle(ChatFormatting.GREEN));
                            tooltip.add(Component.literal("Chance for extra energy per tick").withStyle(ChatFormatting.DARK_GRAY));
                        }
                    }
                }
                
                if (!tooltip.isEmpty()) {
                    graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
                }
            }
        }
        
        // Render generator stats tooltip
        if (isHovering(7, 7, 50, 10, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("Generator Stats").withStyle(ChatFormatting.GOLD));
            
            float speedMultiplier = menu.blockEntity.getSpeedMultiplier();
            float efficiencyMultiplier = menu.blockEntity.getEfficiencyMultiplier();
            float fortuneMultiplier = menu.blockEntity.getFortuneMultiplier();
            
            tooltip.add(Component.literal("Energy Rate: " + String.format("%.0f%%", speedMultiplier * 100))
                .withStyle(speedMultiplier > 1.0f ? ChatFormatting.GREEN : ChatFormatting.GRAY));
            tooltip.add(Component.literal("Fuel Efficiency: " + String.format("%.0f%%", efficiencyMultiplier * 100))
                .withStyle(efficiencyMultiplier > 1.0f ? ChatFormatting.GREEN : ChatFormatting.GRAY));
            tooltip.add(Component.literal("Bonus Energy: " + String.format("%.0f%%", fortuneMultiplier * 100))
                .withStyle(fortuneMultiplier > 1.0f ? ChatFormatting.GREEN : ChatFormatting.GRAY));
            
            graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }
        
        // Render fuel tooltip
        if (isHovering(63, 50, 16, 16, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            if (menu.getSlot(36).hasItem()) {
                int burnTime = menu.blockEntity.getBurnTime();
                int maxBurnTime = menu.blockEntity.getMaxBurnTime();
                if (burnTime > 0) {
                    int seconds = burnTime / 20;
                    tooltip.add(Component.literal("Fuel remaining: " + seconds + "s").withStyle(ChatFormatting.GRAY));
                    tooltip.add(Component.literal("Progress: " + (int)((1.0f - (float)burnTime / maxBurnTime) * 100) + "%").withStyle(ChatFormatting.GRAY));
                }
            } else {
                tooltip.add(Component.literal("Fuel Slot").withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.literal("Insert burnable items").withStyle(ChatFormatting.DARK_GRAY));
            }
            
            if (!tooltip.isEmpty()) {
                graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
            }
        }
    }
}