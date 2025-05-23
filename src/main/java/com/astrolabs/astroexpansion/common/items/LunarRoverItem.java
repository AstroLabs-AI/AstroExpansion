package com.astrolabs.astroexpansion.common.items;

import com.astrolabs.astroexpansion.common.entities.vehicles.LunarRoverEntity;
import com.astrolabs.astroexpansion.common.registry.ModCreativeTabs;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

public class LunarRoverItem extends Item {
    private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
    
    public LunarRoverItem() {
        super(new Item.Properties()
                .stacksTo(1));
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        
        if (blockhitresult.getType() == BlockHitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
        } else {
            Vec3 vec3 = player.getViewVector(1.0F);
            double d0 = 5.0D;
            List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(vec3.scale(d0)).inflate(1.0D), ENTITY_PREDICATE);
            
            if (!list.isEmpty()) {
                Vec3 vec31 = player.getEyePosition();
                
                for(Entity entity : list) {
                    AABB aabb = entity.getBoundingBox().inflate((double)entity.getPickRadius());
                    if (aabb.contains(vec31)) {
                        return InteractionResultHolder.pass(itemstack);
                    }
                }
            }
            
            if (blockhitresult.getType() == BlockHitResult.Type.BLOCK) {
                BlockPos blockpos = blockhitresult.getBlockPos();
                
                // Place the rover slightly above the block
                double x = blockpos.getX() + 0.5;
                double y = blockpos.getY() + 1.0;
                double z = blockpos.getZ() + 0.5;
                
                LunarRoverEntity rover = new LunarRoverEntity(level, x, y, z);
                rover.setYRot(player.getYRot());
                
                if (!level.isClientSide) {
                    level.addFreshEntity(rover);
                    level.gameEvent(player, GameEvent.ENTITY_PLACE, blockpos);
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                }
                
                player.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
            } else {
                return InteractionResultHolder.pass(itemstack);
            }
        }
    }
}