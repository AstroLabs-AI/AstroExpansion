package com.astrolabs.astroexpansion.common.network;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.blockentities.AbstractMachineBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EnergySyncPacket(BlockPos pos, int energy) implements CustomPacketPayload {
    
    public static final Type<EnergySyncPacket> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(AstroExpansion.MODID, "energy_sync")
    );
    
    public static final StreamCodec<FriendlyByteBuf, EnergySyncPacket> CODEC = StreamCodec.of(
        EnergySyncPacket::write,
        EnergySyncPacket::new
    );
    
    public EnergySyncPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readInt());
    }
    
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(energy);
    }
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().level != null) {
                BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);
                if (be instanceof AbstractMachineBlockEntity machine) {
                    machine.getEnergyStorage().setEnergy(energy);
                }
            }
        });
    }
}