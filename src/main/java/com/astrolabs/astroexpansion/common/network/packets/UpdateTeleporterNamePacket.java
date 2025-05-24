package com.astrolabs.astroexpansion.common.network.packets;

import com.astrolabs.astroexpansion.common.blockentities.AdvancedTeleporterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateTeleporterNamePacket {
    private final BlockPos pos;
    private final String name;
    
    public UpdateTeleporterNamePacket(BlockPos pos, String name) {
        this.pos = pos;
        this.name = name;
    }
    
    public UpdateTeleporterNamePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.name = buf.readUtf(20);
    }
    
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(name, 20);
    }
    
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                BlockEntity be = player.level().getBlockEntity(pos);
                if (be instanceof AdvancedTeleporterBlockEntity teleporter) {
                    // Verify player is near the teleporter
                    if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 64) {
                        teleporter.setCustomName(name);
                    }
                }
            }
        });
        return true;
    }
}