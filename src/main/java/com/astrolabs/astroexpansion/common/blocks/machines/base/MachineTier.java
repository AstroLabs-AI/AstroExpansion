package com.astrolabs.astroexpansion.common.blocks.machines.base;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum MachineTier implements StringRepresentable {
    BASIC("basic", 1.0f, 1.0f, 2, ChatFormatting.WHITE, 0x808080),
    ADVANCED("advanced", 2.0f, 1.5f, 4, ChatFormatting.AQUA, 0x00AAFF),
    ELITE("elite", 4.0f, 2.0f, 6, ChatFormatting.LIGHT_PURPLE, 0xFF00FF);

    private final String name;
    private final float speedMultiplier;
    private final float efficiencyMultiplier;
    private final int upgradeSlots;
    private final ChatFormatting color;
    private final int tintColor;

    MachineTier(String name, float speedMultiplier, float efficiencyMultiplier, int upgradeSlots, ChatFormatting color, int tintColor) {
        this.name = name;
        this.speedMultiplier = speedMultiplier;
        this.efficiencyMultiplier = efficiencyMultiplier;
        this.upgradeSlots = upgradeSlots;
        this.color = color;
        this.tintColor = tintColor;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public float getEfficiencyMultiplier() {
        return efficiencyMultiplier;
    }

    public int getUpgradeSlots() {
        return upgradeSlots;
    }

    public ChatFormatting getColor() {
        return color;
    }

    public int getTintColor() {
        return tintColor;
    }

    public Component getDisplayName() {
        return Component.translatable("tier.astroexpansion." + name).withStyle(color);
    }

    public int getEnergyCapacity() {
        return switch (this) {
            case BASIC -> 10000;
            case ADVANCED -> 50000;
            case ELITE -> 200000;
        };
    }

    public int getEnergyPerTick() {
        return switch (this) {
            case BASIC -> 20;
            case ADVANCED -> 40;
            case ELITE -> 80;
        };
    }
}