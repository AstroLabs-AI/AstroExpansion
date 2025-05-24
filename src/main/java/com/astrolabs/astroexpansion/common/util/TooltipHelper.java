package com.astrolabs.astroexpansion.common.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class TooltipHelper {
    
    // GUI Real-time tooltips
    public static void addEnergyBarTooltip(List<Component> tooltip, int stored, int capacity, int generation, int consumption) {
        tooltip.add(Component.translatable("tooltip.astroexpansion.energy_stored", formatEnergy(stored), formatEnergy(capacity))
            .withStyle(ChatFormatting.WHITE));
        
        if (generation > 0) {
            tooltip.add(Component.translatable("tooltip.astroexpansion.generating", formatEnergy(generation))
                .withStyle(ChatFormatting.GREEN));
        }
        
        if (consumption > 0) {
            tooltip.add(Component.translatable("tooltip.astroexpansion.consuming", formatEnergy(consumption))
                .withStyle(ChatFormatting.RED));
        }
        
        if (generation > 0 || consumption > 0) {
            int net = generation - consumption;
            ChatFormatting color = net > 0 ? ChatFormatting.GREEN : net < 0 ? ChatFormatting.RED : ChatFormatting.GRAY;
            tooltip.add(Component.translatable("tooltip.astroexpansion.net_flow", 
                (net > 0 ? "+" : "") + formatEnergy(Math.abs(net)))
                .withStyle(color));
        }
        
        // Time to fill/empty
        if (generation > consumption && stored < capacity) {
            int ticksToFill = (capacity - stored) / (generation - consumption);
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("tooltip.astroexpansion.time_to_fill", formatTime(ticksToFill))
                .withStyle(ChatFormatting.DARK_GRAY));
        } else if (consumption > generation && stored > 0) {
            int ticksToEmpty = stored / (consumption - generation);
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("tooltip.astroexpansion.time_to_empty", formatTime(ticksToEmpty))
                .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
    
    // Progress tooltips
    public static void addProgressTooltip(List<Component> tooltip, int progress, int maxProgress, String recipeName) {
        if (maxProgress > 0) {
            float percentage = (float) progress / maxProgress * 100;
            tooltip.add(Component.translatable("tooltip.astroexpansion.progress", String.format("%.1f%%", percentage))
                .withStyle(ChatFormatting.WHITE));
            
            if (recipeName != null) {
                tooltip.add(Component.translatable("tooltip.astroexpansion.processing", recipeName)
                    .withStyle(ChatFormatting.GRAY));
            }
            
            int remainingTicks = maxProgress - progress;
            tooltip.add(Component.translatable("tooltip.astroexpansion.time_remaining", formatTime(remainingTicks))
                .withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltip.add(Component.translatable("tooltip.astroexpansion.idle")
                .withStyle(ChatFormatting.GRAY));
        }
    }
    
    // Tank tooltips
    public static void addFluidTooltip(List<Component> tooltip, String fluidName, int amount, int capacity) {
        if (fluidName != null && amount > 0) {
            tooltip.add(Component.translatable("tooltip.astroexpansion.fluid", fluidName)
                .withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.translatable("tooltip.astroexpansion.fluid_amount", formatFluid(amount), formatFluid(capacity))
                .withStyle(ChatFormatting.GRAY));
            
            float percentage = (float) amount / capacity * 100;
            tooltip.add(Component.translatable("tooltip.astroexpansion.percentage_full", String.format("%.1f%%", percentage))
                .withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("tooltip.astroexpansion.empty_tank")
                .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.astroexpansion.tank_capacity", formatFluid(capacity))
                .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
    
    // Upgrade slot tooltips
    public static void addUpgradeSlotTooltip(List<Component> tooltip, @javax.annotation.Nullable ItemStack upgrade, String machineType) {
        if (upgrade == null || upgrade.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.astroexpansion.upgrade_slot")
                .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.astroexpansion.upgrade_slot.desc")
                .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
    
    // Temperature/heat tooltips
    public static void addTemperatureTooltip(List<Component> tooltip, int temperature, int maxTemp) {
        ChatFormatting color = temperature > maxTemp * 0.9 ? ChatFormatting.RED :
                              temperature > maxTemp * 0.7 ? ChatFormatting.YELLOW :
                              temperature > maxTemp * 0.5 ? ChatFormatting.GREEN :
                              ChatFormatting.AQUA;
        
        tooltip.add(Component.translatable("tooltip.astroexpansion.temperature", temperature + "K")
            .withStyle(color));
        
        if (temperature > maxTemp * 0.8) {
            tooltip.add(Component.translatable("tooltip.astroexpansion.overheat_warning")
                .withStyle(ChatFormatting.RED));
        }
    }
    
    // Status tooltips
    public static void addMachineStatus(List<Component> tooltip, boolean active, String... problems) {
        if (active) {
            tooltip.add(Component.translatable("tooltip.astroexpansion.status.active")
                .withStyle(ChatFormatting.GREEN));
        } else if (problems.length > 0) {
            tooltip.add(Component.translatable("tooltip.astroexpansion.status.inactive")
                .withStyle(ChatFormatting.RED));
            for (String problem : problems) {
                tooltip.add(Component.literal("  • ")
                    .withStyle(ChatFormatting.RED)
                    .append(Component.translatable("tooltip.astroexpansion.problem." + problem)
                        .withStyle(ChatFormatting.GRAY)));
            }
        } else {
            tooltip.add(Component.translatable("tooltip.astroexpansion.status.idle")
                .withStyle(ChatFormatting.YELLOW));
        }
    }
    
    // Energy formatting
    public static void addEnergyInfo(List<Component> tooltip, int capacity, int consumption) {
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.astroexpansion.energy_info")
            .withStyle(ChatFormatting.YELLOW));
        
        if (capacity > 0) {
            tooltip.add(Component.literal("  ")
                .append(Component.translatable("tooltip.astroexpansion.energy_capacity", formatEnergy(capacity))
                    .withStyle(ChatFormatting.GRAY)));
        }
        
        if (consumption > 0) {
            tooltip.add(Component.literal("  ")
                .append(Component.translatable("tooltip.astroexpansion.energy_per_operation", formatEnergy(consumption))
                    .withStyle(ChatFormatting.RED)));
        }
    }
    
    // Processing info
    public static void addProcessingInfo(List<Component> tooltip, int baseTime, float speedMultiplier) {
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.astroexpansion.processing_info")
            .withStyle(ChatFormatting.AQUA));
        
        int actualTime = (int)(baseTime / speedMultiplier);
        tooltip.add(Component.literal("  ")
            .append(Component.translatable("tooltip.astroexpansion.processing_time", formatTime(actualTime))
                .withStyle(ChatFormatting.GRAY)));
        
        if (speedMultiplier != 1.0f) {
            tooltip.add(Component.literal("  ")
                .append(Component.translatable("tooltip.astroexpansion.speed_modifier", String.format("%.1fx", speedMultiplier))
                    .withStyle(speedMultiplier > 1 ? ChatFormatting.GREEN : ChatFormatting.RED)));
        }
    }
    
    // Machine features
    public static void addFeatures(List<Component> tooltip, String... features) {
        if (features.length > 0) {
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("tooltip.astroexpansion.features")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
            
            for (String feature : features) {
                tooltip.add(Component.literal("  • ")
                    .withStyle(ChatFormatting.DARK_PURPLE)
                    .append(Component.translatable("tooltip.astroexpansion.feature." + feature)
                        .withStyle(ChatFormatting.GRAY)));
            }
        }
    }
    
    // Tier info
    public static void addTierInfo(List<Component> tooltip, String tier) {
        tooltip.add(Component.literal("  ")
            .append(Component.translatable("tooltip.astroexpansion.tier")
                .withStyle(ChatFormatting.GOLD))
            .append(": ")
            .append(Component.translatable("tooltip.astroexpansion.tier." + tier)
                .withStyle(ChatFormatting.YELLOW)));
    }
    
    // Multiblock info
    public static void addMultiblockInfo(List<Component> tooltip, String structure, String size) {
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.astroexpansion.multiblock_structure")
            .withStyle(ChatFormatting.DARK_AQUA));
        tooltip.add(Component.literal("  ")
            .append(Component.translatable("tooltip.astroexpansion.structure." + structure)
                .withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal("  ")
            .append(Component.translatable("tooltip.astroexpansion.structure_size", size)
                .withStyle(ChatFormatting.GRAY)));
    }
    
    // Storage info
    public static void addStorageInfo(List<Component> tooltip, int slots, int capacity) {
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.astroexpansion.storage_info")
            .withStyle(ChatFormatting.DARK_GREEN));
        
        if (slots > 0) {
            tooltip.add(Component.literal("  ")
                .append(Component.translatable("tooltip.astroexpansion.storage_slots", slots)
                    .withStyle(ChatFormatting.GRAY)));
        }
        
        if (capacity > 0) {
            tooltip.add(Component.literal("  ")
                .append(Component.translatable("tooltip.astroexpansion.storage_capacity", formatNumber(capacity))
                    .withStyle(ChatFormatting.GRAY)));
        }
    }
    
    // Upgrade info
    public static void addUpgradeInfo(List<Component> tooltip, int slots) {
        if (slots > 0) {
            tooltip.add(Component.literal("  ")
                .append(Component.translatable("tooltip.astroexpansion.upgrade_slots", slots)
                    .withStyle(ChatFormatting.LIGHT_PURPLE)));
        }
    }
    
    // Efficiency info
    public static void addEfficiencyInfo(List<Component> tooltip, float efficiency) {
        if (efficiency != 1.0f) {
            ChatFormatting color = efficiency > 1.0f ? ChatFormatting.GREEN : ChatFormatting.RED;
            tooltip.add(Component.literal("  ")
                .append(Component.translatable("tooltip.astroexpansion.efficiency", String.format("%.0f%%", efficiency * 100))
                    .withStyle(color)));
        }
    }
    
    // Hold shift for details
    public static boolean addShiftTooltip(List<Component> tooltip, boolean isShiftDown) {
        if (!isShiftDown) {
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("tooltip.astroexpansion.hold_shift")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            return false;
        }
        return true;
    }
    
    // Formatting helpers
    public static String formatEnergy(int energy) {
        if (energy >= 1000000) {
            return String.format("%.1fM FE", energy / 1000000.0);
        } else if (energy >= 1000) {
            return String.format("%.1fk FE", energy / 1000.0);
        }
        return energy + " FE";
    }
    
    public static String formatTime(int ticks) {
        float seconds = ticks / 20.0f;
        if (seconds >= 60) {
            return String.format("%.1f min", seconds / 60);
        }
        return String.format("%.1fs", seconds);
    }
    
    public static String formatNumber(int number) {
        if (number >= 1000000) {
            return String.format("%.1fM", number / 1000000.0);
        } else if (number >= 1000) {
            return String.format("%.1fk", number / 1000.0);
        }
        return String.valueOf(number);
    }
    
    public static String formatFluid(int millibuckets) {
        if (millibuckets >= 1000) {
            return String.format("%.1f B", millibuckets / 1000.0);
        }
        return millibuckets + " mB";
    }
    
    // Add warning tooltips
    public static void addWarning(List<Component> tooltip, String warning) {
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("⚠ ")
            .withStyle(ChatFormatting.YELLOW)
            .append(Component.translatable("tooltip.astroexpansion.warning." + warning)
                .withStyle(ChatFormatting.YELLOW)));
    }
    
    // Add requirement tooltips
    public static void addRequirement(List<Component> tooltip, String requirement) {
        tooltip.add(Component.literal("  • ")
            .withStyle(ChatFormatting.RED)
            .append(Component.translatable("tooltip.astroexpansion.requires." + requirement)
                .withStyle(ChatFormatting.GRAY)));
    }
}