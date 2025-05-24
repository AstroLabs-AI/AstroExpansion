package com.astrolabs.astroexpansion.common.network.packets;

import com.astrolabs.astroexpansion.common.blockentities.AdvancedTeleporterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TeleportPacket {
    private final BlockPos fromPos;
    private final BlockPos toPos;
    
    public TeleportPacket(BlockPos fromPos, BlockPos toPos) {
        this.fromPos = fromPos;
        this.toPos = toPos;
    }
    
    public TeleportPacket(FriendlyByteBuf buf) {
        this.fromPos = buf.readBlockPos();
        this.toPos = buf.readBlockPos();
    }
    
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(fromPos);
        buf.writeBlockPos(toPos);
    }
    
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                BlockEntity be = player.level().getBlockEntity(fromPos);
                if (be instanceof AdvancedTeleporterBlockEntity teleporter) {
                    // Verify player is near the teleporter
                    if (player.distanceToSqr(fromPos.getX() + 0.5, fromPos.getY() + 0.5, fromPos.getZ() + 0.5) < 64) {
                        teleporter.teleportEntity(player, toPos);
                    }
                }
            }
        });
        return true;
    }
}