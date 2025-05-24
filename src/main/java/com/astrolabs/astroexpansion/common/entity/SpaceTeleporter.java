package com.astrolabs.astroexpansion.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.function.Function;

public class SpaceTeleporter implements ITeleporter {
    private final BlockPos targetPos;
    
    public SpaceTeleporter(BlockPos targetPos) {
        this.targetPos = targetPos;
    }
    
    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        return new PortalInfo(
            new Vec3(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5),
            Vec3.ZERO,
            entity.getYRot(),
            entity.getXRot()
        );
    }
    
    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        Entity repositioned = repositionEntity.apply(false);
        if (repositioned != null) {
            repositioned.moveTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
        }
        return repositioned;
    }
}