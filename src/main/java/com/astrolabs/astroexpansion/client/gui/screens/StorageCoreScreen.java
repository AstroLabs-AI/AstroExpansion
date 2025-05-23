package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.menu.StorageCoreMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class StorageCoreScreen extends AbstractContainerScreen<StorageCoreMenu> {
    private static final ResourceLocation TEXTURE = 
        new ResourceLocation(AstroExpansion.MODID, "textures/gui/storage_core.png");
    
    public StorageCoreScreen(StorageCoreMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 176; // Standard chest-like GUI height
    }
    
    @Override
    protected void init() {
        super.init();
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        
        int x = this.leftPos;
        int y = this.topPos;
        
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
        
        // Render energy bar
        if (menu.getBlockEntity() != null) {
            int energy = menu.getBlockEntity().getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ENERGY)
                .map(e -> e.getEnergyStored()).orElse(0);
            int maxEnergy = menu.getBlockEntity().getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ENERGY)
                .map(e -> e.getMaxEnergyStored()).orElse(1);
            
            if (maxEnergy > 0) {
                int energyHeight = energy * 52 / maxEnergy;
                guiGraphics.blit(TEXTURE, x + 10, y + 69 - energyHeight, 176, 52 - energyHeight, 16, energyHeight);
            }
        }
        
        // Render network status indicator
        if (menu.getBlockEntity() != null && menu.getBlockEntity().isNetworkFormed()) {
            guiGraphics.blit(TEXTURE, x + 152, y + 8, 176, 52, 16, 16);
        }
    }
    
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
        
        // Render network info
        if (menu.getBlockEntity() != null) {
            long stored = menu.getBlockEntity().getTotalItemCount();
            long capacity = menu.getBlockEntity().getTotalCapacity();
            int drives = 0;
            
            for (int i = 0; i < 6; i++) {
                if (!menu.getBlockEntity().getDriveHandler().getStackInSlot(i).isEmpty()) {
                    drives++;
                }
            }
            
            Component storageText = Component.literal(String.format("%d / %d", stored, capacity));
            Component drivesText = Component.literal(String.format("Drives: %d/6", drives));
            Component energyText = Component.literal(String.format("%d FE/t", menu.getBlockEntity().getEnergyConsumption()));
            
            guiGraphics.drawString(font, storageText, 8, 58, 0x404040, false);
            guiGraphics.drawString(font, drivesText, 8, 68, 0x404040, false);
            guiGraphics.drawString(font, energyText, 100, 68, 0x404040, false);
        }
    }
}