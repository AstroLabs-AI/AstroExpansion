package com.astrolabs.astroexpansion.common.items.upgrades;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class UpgradeItem extends Item {
    private final UpgradeType type;
    private final int tier;
    private final float modifier;
    
    public enum UpgradeType {
        SPEED("Speed", "Increases processing speed"),
        EFFICIENCY("Efficiency", "Reduces energy consumption"),
        FORTUNE("Fortune", "Increases output and byproducts");
        
        private final String name;
        private final String description;
        
        UpgradeType(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        public String getName() { return name; }
        public String getDescription() { return description; }
    }
    
    public UpgradeItem(Properties properties, UpgradeType type, int tier, float modifier) {
        super(properties.stacksTo(16));
        this.type = type;
        this.tier = tier;
        this.modifier = modifier;
    }
    
    public UpgradeType getType() {
        return type;
    }
    
    public int getTier() {
        return tier;
    }
    
    public float getModifier() {
        return modifier;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7" + type.getDescription()));
        tooltip.add(Component.literal("§7Tier: §e" + tier));
        tooltip.add(Component.literal("§7Effect: §a+" + (int)((modifier - 1.0f) * 100) + "%"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return tier >= 3; // Make tier 3+ upgrades have enchantment glint
    }
}