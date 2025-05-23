package com.astrolabs.astroexpansion.common.blocks;

import com.astrolabs.astroexpansion.common.items.SpaceSuitArmorItem;
import com.astrolabs.astroexpansion.common.registry.ModDimensions;
import com.astrolabs.astroexpansion.common.world.dimension.SpaceTeleporter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public class TeleporterBlock extends Block {
    private final ResourceKey<Level> targetDimension;
    private final String dimensionName;
    
    public TeleporterBlock(ResourceKey<Level> targetDimension, String dimensionName) {
        super(Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .strength(50.0F, 1200.0F)
                .requiresCorrectToolForDrops()
                .lightLevel(state -> 15));
        this.targetDimension = targetDimension;
        this.dimensionName = dimensionName;
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            // Check if player has space suit for space/moon travel
            if ((targetDimension == ModDimensions.SPACE_KEY || targetDimension == ModDimensions.MOON_KEY) 
                && !SpaceSuitArmorItem.hasFullSuit(player)) {
                player.displayClientMessage(
                    Component.literal("You need a full space suit to travel to " + dimensionName + "!")
                        .withStyle(ChatFormatting.RED), 
                    true
                );
                return InteractionResult.FAIL;
            }
            
            MinecraftServer server = serverPlayer.getServer();
            ServerLevel targetWorld = server.getLevel(targetDimension);
            
            if (targetWorld == null) {
                player.displayClientMessage(
                    Component.literal("Cannot access " + dimensionName + " dimension!")
                        .withStyle(ChatFormatting.RED), 
                    false
                );
                return InteractionResult.FAIL;
            }
            
            // Teleport player
            boolean toSpace = targetDimension == ModDimensions.SPACE_KEY;
            serverPlayer.changeDimension(targetWorld, new SpaceTeleporter(toSpace));
            
            player.displayClientMessage(
                Component.literal("Teleporting to " + dimensionName + "...")
                    .withStyle(ChatFormatting.GREEN), 
                true
            );
        }
        
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}