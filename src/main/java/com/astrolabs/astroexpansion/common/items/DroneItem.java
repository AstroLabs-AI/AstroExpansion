package com.astrolabs.astroexpansion.common.items;

import com.astrolabs.astroexpansion.common.entities.drones.AbstractDroneEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class DroneItem extends Item {
    private final Supplier<? extends EntityType<? extends AbstractDroneEntity>> entityType;
    private final String droneType;
    
    public DroneItem(Properties properties, Supplier<? extends EntityType<? extends AbstractDroneEntity>> entityType, String droneType) {
        super(properties.stacksTo(1));
        this.entityType = entityType;
        this.droneType = droneType;
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide) {
            BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
            Player player = context.getPlayer();
            ItemStack stack = context.getItemInHand();
            
            // Spawn drone
            AbstractDroneEntity drone = entityType.get().create(level);
            if (drone != null) {
                drone.setPos(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
                drone.setOwner(player.getUUID());
                drone.setHomePos(pos);
                
                // Restore energy from item
                CompoundTag tag = stack.getTag();
                if (tag != null && tag.contains("Energy")) {
                    drone.getEnergyStorage().setEnergy(tag.getInt("Energy"));
                }
                
                level.addFreshEntity(drone);
                
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                
                return InteractionResult.SUCCESS;
            }
        }
        
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        tooltip.add(Component.literal("Type: " + droneType).withStyle(ChatFormatting.GRAY));
        
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("Energy")) {
            int energy = tag.getInt("Energy");
            tooltip.add(Component.literal("Energy: " + energy + " FE").withStyle(ChatFormatting.GREEN));
        } else {
            tooltip.add(Component.literal("Energy: 0 FE").withStyle(ChatFormatting.RED));
        }
        
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Right-click to deploy").withStyle(ChatFormatting.YELLOW));
    }
}