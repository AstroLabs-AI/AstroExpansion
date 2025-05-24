package com.astrolabs.astroexpansion.common.network.packet;

import com.astrolabs.astroexpansion.common.entity.PersonalRocketEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LaunchRocketPacket {
    
    public LaunchRocketPacket() {
    }
    
    public LaunchRocketPacket(FriendlyByteBuf buf) {
    }
    
    public void encode(FriendlyByteBuf buf) {
    }
    
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null && player.getVehicle() instanceof PersonalRocketEntity rocket) {
                rocket.launch();
            }
        });
        return true;
    }
}