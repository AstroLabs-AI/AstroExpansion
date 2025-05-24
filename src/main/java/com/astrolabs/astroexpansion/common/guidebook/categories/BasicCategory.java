package com.astrolabs.astroexpansion.common.guidebook.categories;

import com.astrolabs.astroexpansion.api.guidebook.IGuideCategory;
import com.astrolabs.astroexpansion.api.guidebook.IGuideEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic implementation of a guide category
 */
public class BasicCategory implements IGuideCategory {
    private final ResourceLocation id;
    private final Component name;
    private final Component description;
    private final ItemStack icon;
    private final int sortOrder;
    private final int color;
    private final List<IGuideEntry> entries = new ArrayList<>();
    private IGuideCategory parent;
    private final List<IGuideCategory> subcategories = new ArrayList<>();
    
    public BasicCategory(ResourceLocation id, Component name, Component description, 
                        ItemStack icon, int sortOrder, int color) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sortOrder = sortOrder;
        this.color = color;
    }
    
    public void addEntry(IGuideEntry entry) {
        entries.add(entry);
    }
    
    @Override
    public ResourceLocation getId() {
        return id;
    }
    
    @Override
    public Component getName() {
        return name;
    }
    
    @Override
    public Component getDescription() {
        return description;
    }
    
    @Override
    public ItemStack getIcon() {
        return icon;
    }
    
    @Override
    public List<IGuideEntry> getEntries() {
        return entries;
    }
    
    @Override
    public int getSortOrder() {
        return sortOrder;
    }
    
    @Override
    public boolean isLocked() {
        return false;
    }
    
    @Override
    public IGuideCategory getParent() {
        return parent;
    }
    
    @Override
    public List<IGuideCategory> getSubcategories() {
        return subcategories;
    }
    
    @Override
    public ResourceLocation getBackgroundTexture() {
        return null;
    }
    
    @Override
    public int getColor() {
        return color;
    }
}