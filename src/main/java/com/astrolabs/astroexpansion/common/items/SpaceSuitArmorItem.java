package com.astrolabs.astroexpansion.common.items;

import com.astrolabs.astroexpansion.common.registry.AEDataComponentTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.Level;

public class SpaceSuitArmorItem extends Item {
    private static final int OXYGEN_CAPACITY = 6000; // 5 minutes worth
    private static final int OXYGEN_USE_PER_TICK = 1;
    
    private final ArmorType type;
    
    public SpaceSuitArmorItem(ArmorType type, Properties properties) {
        super(properties);
        this.type = type;
    }
    
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            if (isWearingFullSuit(player)) {
                int oxygen = getOxygen(stack);
                
                if (oxygen > 0 && needsOxygen(player)) {
                    consumeOxygen(stack, OXYGEN_USE_PER_TICK);
                    
                    player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 60, 0, true, false));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 0, true, false));
                    
                    if (player.getY() > 256) {
                        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, true, false));
                    }
                }
            }
        }
    }
    
    private boolean isWearingFullSuit(Player player) {
        for (ItemStack armor : player.getArmorSlots()) {
            if (!(armor.getItem() instanceof SpaceSuitArmorItem)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean needsOxygen(Player player) {
        return player.getY() > 256 || player.isUnderWater() || !player.level().canSeeSky(player.blockPosition());
    }
    
    public static int getOxygen(ItemStack stack) {
        return stack.getOrDefault(AEDataComponentTypes.OXYGEN.get(), 0);
    }
    
    public static void setOxygen(ItemStack stack, int oxygen) {
        stack.set(AEDataComponentTypes.OXYGEN.get(), Math.min(oxygen, OXYGEN_CAPACITY));
    }
    
    public static void consumeOxygen(ItemStack stack, int amount) {
        int currentOxygen = getOxygen(stack);
        setOxygen(stack, Math.max(0, currentOxygen - amount));
    }
    
    public static void refillOxygen(ItemStack stack, int amount) {
        int currentOxygen = getOxygen(stack);
        setOxygen(stack, Math.min(OXYGEN_CAPACITY, currentOxygen + amount));
    }
    
    public static int getMaxOxygen() {
        return OXYGEN_CAPACITY;
    }
    
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getOxygen(stack) < OXYGEN_CAPACITY;
    }
    
    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * (float)getOxygen(stack) / (float)OXYGEN_CAPACITY);
    }
    
    @Override
    public int getBarColor(ItemStack stack) {
        float f = Math.max(0.0F, (float)getOxygen(stack) / (float)OXYGEN_CAPACITY);
        return net.minecraft.util.Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }
}