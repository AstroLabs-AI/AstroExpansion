package com.astrolabs.astroexpansion.client.gui.screens;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.blockentities.AdvancedTeleporterBlockEntity;
import com.astrolabs.astroexpansion.common.menu.AdvancedTeleporterMenu;
import com.astrolabs.astroexpansion.common.network.ModPacketHandler;
import com.astrolabs.astroexpansion.common.network.packets.TeleportPacket;
import com.astrolabs.astroexpansion.common.network.packets.UpdateTeleporterNamePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class AdvancedTeleporterScreen extends AbstractContainerScreen<AdvancedTeleporterMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AstroExpansion.MODID, "textures/gui/advanced_teleporter.png");
    
    private EditBox nameField;
    private Button teleportButton;
    private List<TeleporterEntry> connectedTeleporters = new ArrayList<>();
    private int selectedIndex = -1;
    private int scrollOffset = 0;
    private static final int MAX_VISIBLE_ENTRIES = 5;
    
    public AdvancedTeleporterScreen(AdvancedTeleporterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Name field
        this.nameField = new EditBox(this.font, this.leftPos + 8, this.topPos + 20, 100, 12, Component.literal("Name"));
        this.nameField.setCanLoseFocus(true);
        this.nameField.setTextColor(-1);
        this.nameField.setTextColorUneditable(-1);
        this.nameField.setBordered(false);
        this.nameField.setMaxLength(20);
        this.nameField.setValue(menu.getBlockEntity().getCustomName());
        this.nameField.setResponder(this::onNameChanged);
        this.addWidget(this.nameField);
        
        // Teleport button
        this.teleportButton = this.addRenderableWidget(
            Button.builder(Component.literal("Teleport"), this::onTeleportClicked)
                .pos(this.leftPos + 115, this.topPos + 60)
                .size(54, 20)
                .build()
        );
        this.teleportButton.active = false;
        
        // Update connected teleporters
        updateConnectedTeleporters();
    }
    
    private void onNameChanged(String name) {
        ModPacketHandler.sendToServer(new UpdateTeleporterNamePacket(menu.getBlockPos(), name));
    }
    
    private void onTeleportClicked(Button button) {
        if (selectedIndex >= 0 && selectedIndex < connectedTeleporters.size()) {
            TeleporterEntry entry = connectedTeleporters.get(selectedIndex);
            ModPacketHandler.sendToServer(new TeleportPacket(menu.getBlockPos(), entry.pos));
        }
    }
    
    private void updateConnectedTeleporters() {
        connectedTeleporters.clear();
        AdvancedTeleporterBlockEntity be = menu.getBlockEntity();
        
        for (BlockPos pos : be.getConnectedTeleporters()) {
            String name = "Teleporter at " + pos.toShortString();
            int cost = be.getEnergyCost(pos);
            connectedTeleporters.add(new TeleporterEntry(pos, name, cost));
        }
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        
        // Render name field
        this.nameField.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        
        // Render energy bar
        int energy = menu.getEnergy();
        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0) {
            int energyHeight = (int)(52 * ((float)energy / maxEnergy));
            guiGraphics.blit(TEXTURE, this.leftPos + 152, this.topPos + 17 + (52 - energyHeight), 
                176, 52 - energyHeight, 12, energyHeight);
        }
        
        // Render cooldown indicator
        if (menu.getCooldown() > 0) {
            guiGraphics.blit(TEXTURE, this.leftPos + 80, this.topPos + 35, 176, 52, 16, 16);
        }
        
        // Render multiblock status
        if (!menu.isFormed()) {
            guiGraphics.drawString(this.font, "Multiblock not formed!", 
                this.leftPos + 8, this.topPos + 72, 0xFF0000, false);
        }
    }
    
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Frequency
        String frequency = menu.getBlockEntity().getFrequency();
        if (!frequency.isEmpty()) {
            guiGraphics.drawString(this.font, "Freq: " + frequency, 8, 8, 0x404040, false);
        }
        
        // Connected teleporters list
        guiGraphics.drawString(this.font, "Connected Teleporters:", 8, 84, 0x404040, false);
        
        int y = 94;
        for (int i = scrollOffset; i < Math.min(scrollOffset + MAX_VISIBLE_ENTRIES, connectedTeleporters.size()); i++) {
            TeleporterEntry entry = connectedTeleporters.get(i);
            boolean selected = i == selectedIndex;
            int color = selected ? 0xFFFFFF : 0x404040;
            
            guiGraphics.drawString(this.font, entry.name, 8, y, color, false);
            guiGraphics.drawString(this.font, entry.cost + " FE", 140, y, color, false);
            
            y += 10;
        }
        
        // Update teleport button
        teleportButton.active = selectedIndex >= 0 && menu.getEnergy() >= 
            (selectedIndex < connectedTeleporters.size() ? connectedTeleporters.get(selectedIndex).cost : 0) 
            && menu.getCooldown() == 0 && menu.isFormed();
    }
    
    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
        
        // Energy tooltip
        if (x >= this.leftPos + 152 && x < this.leftPos + 164 && 
            y >= this.topPos + 17 && y < this.topPos + 69) {
            guiGraphics.renderTooltip(this.font, 
                Component.literal("Energy: " + menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE"), 
                x, y);
        }
        
        // Cooldown tooltip
        if (menu.getCooldown() > 0 && x >= this.leftPos + 80 && x < this.leftPos + 96 && 
            y >= this.topPos + 35 && y < this.topPos + 51) {
            guiGraphics.renderTooltip(this.font, 
                Component.literal("Cooldown: " + (menu.getCooldown() / 20) + "s").withStyle(ChatFormatting.RED), 
                x, y);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.nameField.isFocused()) {
            this.nameField.mouseClicked(mouseX, mouseY, button);
        }
        
        // Check teleporter list clicks
        int listX = this.leftPos + 8;
        int listY = this.topPos + 94;
        
        if (mouseX >= listX && mouseX < listX + 160 && mouseY >= listY && mouseY < listY + MAX_VISIBLE_ENTRIES * 10) {
            int clickedIndex = scrollOffset + (int)((mouseY - listY) / 10);
            if (clickedIndex >= 0 && clickedIndex < connectedTeleporters.size()) {
                selectedIndex = clickedIndex;
                return true;
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (connectedTeleporters.size() > MAX_VISIBLE_ENTRIES) {
            scrollOffset = Math.max(0, Math.min(scrollOffset - (int)delta, 
                connectedTeleporters.size() - MAX_VISIBLE_ENTRIES));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
    
    @Override
    public void containerTick() {
        super.containerTick();
        this.nameField.tick();
        
        // Update connected teleporters periodically
        if (this.minecraft.level.getGameTime() % 20 == 0) {
            updateConnectedTeleporters();
        }
    }
    
    private static class TeleporterEntry {
        public final BlockPos pos;
        public final String name;
        public final int cost;
        
        public TeleporterEntry(BlockPos pos, String name, int cost) {
            this.pos = pos;
            this.name = name;
            this.cost = cost;
        }
    }
}