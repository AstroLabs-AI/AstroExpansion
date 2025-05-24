package com.astrolabs.astroexpansion.common.network.packets;

import com.astrolabs.astroexpansion.common.guidebook.AstroGuideBook;
import com.astrolabs.astroexpansion.common.guidebook.GuideProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Syncs guide book progress between client and server
 */
public class GuideProgressSyncPacket {
    private final Set<ResourceLocation> viewedEntries;
    private final Set<ResourceLocation> bookmarkedEntries;
    private final Set<ResourceLocation> completedChecklists;
    private final String theme;
    
    public GuideProgressSyncPacket(GuideProgress progress) {
        this.viewedEntries = new HashSet<>(progress.getViewedEntries());
        this.bookmarkedEntries = new HashSet<>(progress.getBookmarkedEntries());
        this.completedChecklists = new HashSet<>(progress.getCompletedChecklists());
        this.theme = progress.getTheme();
    }
    
    public GuideProgressSyncPacket(FriendlyByteBuf buf) {
        int viewedCount = buf.readVarInt();
        viewedEntries = new HashSet<>();
        for (int i = 0; i < viewedCount; i++) {
            viewedEntries.add(buf.readResourceLocation());
        }
        
        int bookmarkCount = buf.readVarInt();
        bookmarkedEntries = new HashSet<>();
        for (int i = 0; i < bookmarkCount; i++) {
            bookmarkedEntries.add(buf.readResourceLocation());
        }
        
        int checklistCount = buf.readVarInt();
        completedChecklists = new HashSet<>();
        for (int i = 0; i < checklistCount; i++) {
            completedChecklists.add(buf.readResourceLocation());
        }
        
        theme = buf.readUtf();
    }
    
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(viewedEntries.size());
        for (ResourceLocation id : viewedEntries) {
            buf.writeResourceLocation(id);
        }
        
        buf.writeVarInt(bookmarkedEntries.size());
        for (ResourceLocation id : bookmarkedEntries) {
            buf.writeResourceLocation(id);
        }
        
        buf.writeVarInt(completedChecklists.size());
        for (ResourceLocation id : completedChecklists) {
            buf.writeResourceLocation(id);
        }
        
        buf.writeUtf(theme);
    }
    
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                // Update server-side progress
                GuideProgress progress = (GuideProgress) AstroGuideBook.getInstance().getProgress(player);
                progress.getViewedEntries().clear();
                progress.getViewedEntries().addAll(viewedEntries);
                progress.getBookmarkedEntries().clear();
                progress.getBookmarkedEntries().addAll(bookmarkedEntries);
                progress.getCompletedChecklists().clear();
                progress.getCompletedChecklists().addAll(completedChecklists);
                progress.setTheme(theme);
                
                // Save to player data
                saveProgressToPlayer(player, progress);
            }
        });
        ctx.get().setPacketHandled(true);
    }
    
    private void saveProgressToPlayer(ServerPlayer player, GuideProgress progress) {
        // TODO: Save to player's persistent data
        var data = player.getPersistentData();
        var guideData = data.getCompound("astroexpansion:guide_progress");
        
        // Save viewed entries
        var viewedList = new net.minecraft.nbt.ListTag();
        for (ResourceLocation id : progress.getViewedEntries()) {
            viewedList.add(net.minecraft.nbt.StringTag.valueOf(id.toString()));
        }
        guideData.put("viewed", viewedList);
        
        // Save bookmarks
        var bookmarkList = new net.minecraft.nbt.ListTag();
        for (ResourceLocation id : progress.getBookmarkedEntries()) {
            bookmarkList.add(net.minecraft.nbt.StringTag.valueOf(id.toString()));
        }
        guideData.put("bookmarks", bookmarkList);
        
        // Save theme
        guideData.putString("theme", progress.getTheme());
        
        data.put("astroexpansion:guide_progress", guideData);
    }
}