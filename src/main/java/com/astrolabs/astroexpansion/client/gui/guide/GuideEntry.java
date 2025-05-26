package com.astrolabs.astroexpansion.client.gui.guide;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single entry in the guide book with multiple pages
 */
public class GuideEntry {
    private final ResourceLocation id;
    private final Component name;
    private final ItemStack icon;
    private final List<GuidePage> pages;
    private final int sortOrder;
    private boolean isRead;
    
    public GuideEntry(ResourceLocation id, Component name, ItemStack icon, int sortOrder) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.sortOrder = sortOrder;
        this.pages = new ArrayList<>();
        this.isRead = false;
    }
    
    public GuideEntry addPage(GuidePage page) {
        this.pages.add(page);
        return this;
    }
    
    public GuideEntry addTextPage(Component title, Component content) {
        return addPage(new TextPage(title, content));
    }
    
    public GuideEntry addSpotlightPage(Component title, ItemStack item, Component description) {
        return addPage(new SpotlightPage(title, item, description));
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
    
    public List<GuidePage> getPages() {
        return pages;
    }
    
    public int getPageCount() {
        return pages.size();
    }
    
    public GuidePage getPage(int index) {
        if (index < 0 || index >= pages.size()) {
            return null;
        }
        return pages.get(index);
    }
    
    public int getSortOrder() {
        return sortOrder;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void markAsRead() {
        this.isRead = true;
    }
    
    public void markAsUnread() {
        this.isRead = false;
    }
}
