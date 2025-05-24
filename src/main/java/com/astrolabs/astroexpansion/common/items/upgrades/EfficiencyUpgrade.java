package com.astrolabs.astroexpansion.common.items.upgrades;

public class EfficiencyUpgrade extends UpgradeItem {
    public EfficiencyUpgrade(Properties properties, int tier) {
        super(properties, UpgradeType.EFFICIENCY, tier, 1.0f - (tier * 0.15f));
    }
}