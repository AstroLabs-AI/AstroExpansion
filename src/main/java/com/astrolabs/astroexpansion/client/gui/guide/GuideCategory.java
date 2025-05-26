package com.astrolabs.astroexpansion.client.gui.guide;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a category of guide entries
 */
public class GuideCategory {
    private final ResourceLocation id;
    private final Component name;
    private final ItemStack icon;
    private final List<GuideEntry> entries;
    private final int sortOrder;
    
    public GuideCategory(ResourceLocation id, Component name, ItemStack icon, int sortOrder) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.sortOrder = sortOrder;
        this.entries = new ArrayList<>();
    }
    
    public GuideCategory addEntry(GuideEntry entry) {
        this.entries.add(entry);
        // Keep entries sorted by their sort order
        this.entries.sort(Comparator.comparingInt(GuideEntry::getSortOrder));
        return this;
    }
    
    public ResourceLocation getId() {
        return id;
    }
    
    public Component getName() {
        return name;
    }
    
    public ItemStack getIcon() {
        return icon;
    }
    
    public List<GuideEntry> getEntries() {
        return entries;
    }
    
    public int getEntryCount() {
        return entries.size();
    }
    
    public GuideEntry getEntry(int index) {
        if (index < 0 || index >= entries.size()) {
            return null;
        }
        return entries.get(index);
    }
    
    public GuideEntry getEntry(ResourceLocation id) {
        return entries.stream()
                .filter(entry -> entry.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public int getSortOrder() {
        return sortOrder;
    }
}
