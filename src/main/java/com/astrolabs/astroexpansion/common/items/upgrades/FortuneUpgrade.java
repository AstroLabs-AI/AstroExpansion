package com.astrolabs.astroexpansion.common.items.upgrades;

public class FortuneUpgrade extends UpgradeItem {
    public FortuneUpgrade(Properties properties, int tier) {
        super(properties, UpgradeType.FORTUNE, tier, 1.0f + (tier * 0.20f));
    }
}