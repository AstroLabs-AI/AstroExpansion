package com.astrolabs.astroexpansion.common.items.upgrades;

public class SpeedUpgrade extends UpgradeItem {
    public SpeedUpgrade(Properties properties, int tier) {
        super(properties, UpgradeType.SPEED, tier, 1.0f + (tier * 0.25f));
    }
}