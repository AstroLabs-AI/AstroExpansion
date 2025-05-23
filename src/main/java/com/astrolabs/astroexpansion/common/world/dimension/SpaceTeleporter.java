package com.astrolabs.astroexpansion.common.world.dimension;

import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

public class SpaceTeleporter implements ITeleporter {
    private final boolean toSpace;
    
    public SpaceTeleporter(boolean toSpace) {
        this.toSpace = toSpace;
    }
    
    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        Entity repositionedEntity = repositionEntity.apply(false);
        
        if (repositionedEntity instanceof ServerPlayer player) {
            // Find or create landing platform
            BlockPos landingPos = findOrCreateLandingPlatform(destWorld, player.blockPosition());
            
            // Teleport to platform
            player.teleportTo(
                landingPos.getX() + 0.5,
                landingPos.getY() + 1,
                landingPos.getZ() + 0.5
            );
        }
        
        return repositionedEntity;
    }
    
    private BlockPos findOrCreateLandingPlatform(ServerLevel world, BlockPos originalPos) {
        // Search for existing platform
        BlockPos searchPos = new BlockPos(originalPos.getX(), toSpace ? 256 : 100, originalPos.getZ());
        
        // Look for existing docking port
        for (int y = -10; y <= 10; y++) {
            for (int x = -10; x <= 10; x++) {
                for (int z = -10; z <= 10; z++) {
                    BlockPos checkPos = searchPos.offset(x, y, z);
                    if (world.getBlockState(checkPos).is(ModBlocks.DOCKING_PORT.get())) {
                        return checkPos;
                    }
                }
            }
        }
        
        // No platform found, create one
        return createLandingPlatform(world, searchPos);
    }
    
    private BlockPos createLandingPlatform(ServerLevel world, BlockPos pos) {
        BlockState platformBlock = toSpace ? 
            ModBlocks.STATION_HULL.get().defaultBlockState() : 
            ModBlocks.MOON_STONE.get().defaultBlockState();
        
        // Create 5x5 platform
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos platformPos = pos.offset(x, 0, z);
                world.setBlock(platformPos, platformBlock, 3);
                
                // Clear space above
                for (int y = 1; y <= 3; y++) {
                    world.setBlock(platformPos.above(y), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
        
        // Place docking port in center
        world.setBlock(pos, ModBlocks.DOCKING_PORT.get().defaultBlockState(), 3);
        
        // Add some glass walls for space station
        if (toSpace) {
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    if (Math.abs(x) == 3 || Math.abs(z) == 3) {
                        for (int y = 1; y <= 3; y++) {
                            world.setBlock(pos.offset(x, y, z), ModBlocks.STATION_GLASS.get().defaultBlockState(), 3);
                        }
                    }
                }
            }
            
            // Add ceiling
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    world.setBlock(pos.offset(x, 4, z), ModBlocks.STATION_HULL.get().defaultBlockState(), 3);
                }
            }
        }
        
        return pos;
    }
    
    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        return defaultPortalInfo.apply(destWorld);
    }
}