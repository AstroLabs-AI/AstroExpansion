package com.astrolabs.astroexpansion.client.event;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.entity.PersonalRocketEntity;
import com.astrolabs.astroexpansion.common.network.ModPacketHandler;
import com.astrolabs.astroexpansion.common.network.packet.LaunchRocketPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AstroExpansion.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientInputEvents {
    
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        
        if (player == null) return;
        
        // Check if player is riding a personal rocket and pressing space
        if (player.getVehicle() instanceof PersonalRocketEntity rocket) {
            if (minecraft.options.keyJump.isDown()) {
                // Send launch packet to server
                ModPacketHandler.sendToServer(new LaunchRocketPacket());
            }
        }
    }
}