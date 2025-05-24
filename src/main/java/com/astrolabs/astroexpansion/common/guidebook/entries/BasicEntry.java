package com.astrolabs.astroexpansion.common.guidebook.entries;

import com.astrolabs.astroexpansion.api.guidebook.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.*;

/**
 * Basic implementation of a guide entry
 */
public class BasicEntry implements IGuideEntry {
    private final ResourceLocation id;
    private final Component name;
    private final ItemStack icon;
    private final IGuideCategory category;
    private final int sortOrder;
    private final List<IGuidePage> pages = new ArrayList<>();
    private final Set<String> tags = new HashSet<>();
    private final List<ItemStack> associatedItems = new ArrayList<>();
    private IUnlockRequirement unlockRequirement;
    private ResourceLocation advancement;
    
    public BasicEntry(ResourceLocation id, Component name, ItemStack icon, 
                     IGuideCategory category, int sortOrder) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.category = category;
        this.sortOrder = sortOrder;
    }
    
    public void addPage(IGuidePage page) {
        pages.add(page);
    }
    
    public void addTag(String tag) {
        tags.add(tag);
    }
    
    public void addAssociatedItem(ItemStack item) {
        associatedItems.add(item);
    }
    
    public void setUnlockRequirement(IUnlockRequirement requirement) {
        this.unlockRequirement = requirement;
    }
    
    public void setAdvancement(ResourceLocation advancement) {
        this.advancement = advancement;
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
    public ItemStack getIcon() {
        return icon;
    }
    
    @Override
    public List<IGuidePage> getPages() {
        return pages;
    }
    
    @Override
    public IGuideCategory getCategory() {
        return category;
    }
    
    @Override
    public Set<String> getTags() {
        return tags;
    }
    
    @Override
    public boolean isUnlocked(Player player) {
        if (unlockRequirement == null) {
            return true;
        }
        return unlockRequirement.test(player);
    }
    
    @Override
    public IUnlockRequirement getUnlockRequirement() {
        return unlockRequirement;
    }
    
    @Override
    public List<IGuideEntry> getRelatedEntries() {
        // TODO: Implement related entry system
        return Collections.emptyList();
    }
    
    @Override
    public int getSortOrder() {
        return sortOrder;
    }
    
    @Override
    public boolean isNew(Player player) {
        // TODO: Track when entries are added
        return false;
    }
    
    @Override
    public ResourceLocation getAdvancement() {
        return advancement;
    }
    
    @Override
    public boolean hasSpoilers() {
        return tags.contains("spoiler");
    }
    
    @Override
    public List<ItemStack> getAssociatedItems() {
        return associatedItems;
    }
}