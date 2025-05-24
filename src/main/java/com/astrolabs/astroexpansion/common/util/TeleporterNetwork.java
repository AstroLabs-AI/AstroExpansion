package com.astrolabs.astroexpansion.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TeleporterNetwork extends SavedData {
    private static final String DATA_NAME = "astroexpansion_teleporter_network";
    private final Map<String, Set<BlockPos>> networkMap = new ConcurrentHashMap<>();
    
    public TeleporterNetwork() {
        super();
    }
    
    public static TeleporterNetwork get(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            throw new RuntimeException("Attempted to get teleporter network on client side!");
        }
        
        DimensionDataStorage storage = serverLevel.getDataStorage();
        return storage.computeIfAbsent(TeleporterNetwork::load, TeleporterNetwork::new, DATA_NAME);
    }
    
    private static TeleporterNetwork clientInstance = null;
    
    public static void registerTeleporter(String frequency, BlockPos pos, Level level) {
        if (frequency == null || frequency.isEmpty() || pos == null || level == null) return;
        
        if (level instanceof ServerLevel serverLevel) {
            TeleporterNetwork network = get(serverLevel);
            network.addTeleporter(frequency, pos);
        }
    }
    
    public static void unregisterTeleporter(String frequency, BlockPos pos, Level level) {
        if (frequency == null || frequency.isEmpty() || pos == null || level == null) return;
        
        if (level instanceof ServerLevel serverLevel) {
            TeleporterNetwork network = get(serverLevel);
            network.removeTeleporter(frequency, pos);
        }
    }
    
    public static Set<BlockPos> getTeleporters(String frequency, Level level) {
        if (frequency == null || frequency.isEmpty() || level == null) {
            return new HashSet<>();
        }
        
        if (level instanceof ServerLevel serverLevel) {
            TeleporterNetwork network = get(serverLevel);
            return network.getTeleportersForFrequency(frequency);
        } else {
            // Client-side fallback
            if (clientInstance == null) {
                clientInstance = new TeleporterNetwork();
            }
            return clientInstance.getTeleportersForFrequency(frequency);
        }
    }
    
    public void addTeleporter(String frequency, BlockPos pos) {
        networkMap.computeIfAbsent(frequency, k -> new HashSet<>()).add(pos);
        setDirty();
    }
    
    public void removeTeleporter(String frequency, BlockPos pos) {
        Set<BlockPos> positions = networkMap.get(frequency);
        if (positions != null) {
            positions.remove(pos);
            if (positions.isEmpty()) {
                networkMap.remove(frequency);
            }
            setDirty();
        }
    }
    
    public Set<BlockPos> getTeleportersForFrequency(String frequency) {
        return new HashSet<>(networkMap.getOrDefault(frequency, new HashSet<>()));
    }
    
    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag networkList = new ListTag();
        
        for (Map.Entry<String, Set<BlockPos>> entry : networkMap.entrySet()) {
            CompoundTag frequencyTag = new CompoundTag();
            frequencyTag.putString("frequency", entry.getKey());
            
            ListTag posList = new ListTag();
            for (BlockPos pos : entry.getValue()) {
                posList.add(NbtUtils.writeBlockPos(pos));
            }
            frequencyTag.put("positions", posList);
            
            networkList.add(frequencyTag);
        }
        
        tag.put("network", networkList);
        return tag;
    }
    
    public static TeleporterNetwork load(CompoundTag tag) {
        TeleporterNetwork network = new TeleporterNetwork();
        
        ListTag networkList = tag.getList("network", 10);
        for (int i = 0; i < networkList.size(); i++) {
            CompoundTag frequencyTag = networkList.getCompound(i);
            String frequency = frequencyTag.getString("frequency");
            
            ListTag posList = frequencyTag.getList("positions", 10);
            Set<BlockPos> positions = new HashSet<>();
            for (int j = 0; j < posList.size(); j++) {
                positions.add(NbtUtils.readBlockPos(posList.getCompound(j)));
            }
            
            network.networkMap.put(frequency, positions);
        }
        
        return network;
    }
}