package com.astrolabs.astroexpansion.common.items;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.registry.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public class SpaceSuitArmorItem extends ArmorItem {
    private static final SpaceSuitMaterial MATERIAL = new SpaceSuitMaterial();
    
    public SpaceSuitArmorItem(ArmorItem.Type type, Properties properties) {
        super(MATERIAL, type, properties);
    }
    
    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (!level.isClientSide && hasFullSuit(player)) {
            // Provide oxygen (water breathing effect)
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 60, 0, false, false));
            
            // Provide protection from vacuum (resistance)
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 1, false, false));
            
            // Slow falling in space
            if (player.getY() > 300) { // Above build height = "in space"
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, false, false));
            }
        }
    }
    
    public static boolean hasFullSuit(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ModItems.SPACE_HELMET.get() &&
               player.getItemBySlot(EquipmentSlot.CHEST).getItem() == ModItems.SPACE_CHESTPLATE.get() &&
               player.getItemBySlot(EquipmentSlot.LEGS).getItem() == ModItems.SPACE_LEGGINGS.get() &&
               player.getItemBySlot(EquipmentSlot.FEET).getItem() == ModItems.SPACE_BOOTS.get();
    }
    
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (slot == EquipmentSlot.LEGS) {
            return AstroExpansion.MODID + ":textures/models/armor/space_suit_layer_2.png";
        }
        return AstroExpansion.MODID + ":textures/models/armor/space_suit_layer_1.png";
    }
    
    private static class SpaceSuitMaterial implements ArmorMaterial {
        private static final int[] HEALTH_PER_SLOT = new int[]{3, 6, 8, 3};
        private static final int[] DEFENSE_VALUES = new int[]{3, 6, 8, 3};
        
        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return HEALTH_PER_SLOT[type.getSlot().getIndex()] * 37;
        }
        
        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return DEFENSE_VALUES[type.getSlot().getIndex()];
        }
        
        @Override
        public int getEnchantmentValue() {
            return 15;
        }
        
        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }
        
        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.TITANIUM_INGOT.get());
        }
        
        @Override
        public String getName() {
            return "space_suit";
        }
        
        @Override
        public float getToughness() {
            return 2.5F;
        }
        
        @Override
        public float getKnockbackResistance() {
            return 0.1F;
        }
    }
}