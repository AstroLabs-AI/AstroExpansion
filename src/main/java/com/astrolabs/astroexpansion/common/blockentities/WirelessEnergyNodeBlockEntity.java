package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.api.energy.IEnergyHandler;
import com.astrolabs.astroexpansion.common.capabilities.AECapabilities;
import com.astrolabs.astroexpansion.common.capabilities.EnergyStorage;
import com.astrolabs.astroexpansion.common.menu.WirelessEnergyNodeMenu;
import com.astrolabs.astroexpansion.common.registry.AEBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class WirelessEnergyNodeBlockEntity extends BlockEntity implements Container, MenuProvider {
    
    private static final int CAPACITY = 100000; // 100k FE
    private static final int MAX_TRANSFER = 1000; // 1000 FE/tick per connection
    private static final int MAX_RANGE = 64; // 64 block range
    private static final int MAX_CONNECTIONS = 8;
    
    private final EnergyStorage energyStorage = new EnergyStorage(CAPACITY, MAX_TRANSFER * MAX_CONNECTIONS, MAX_TRANSFER * MAX_CONNECTIONS);
    private final List<BlockPos> linkedNodes = new ArrayList<>();
    private NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY); // Linking card slot
    
    private int mode = 0; // 0 = transmit, 1 = receive, 2 = both
    
    public WirelessEnergyNodeBlockEntity(BlockPos pos, BlockState state) {
        super(AEBlockEntities.WIRELESS_ENERGY_NODE.get(), pos, state);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, WirelessEnergyNodeBlockEntity blockEntity) {
        blockEntity.tick();
    }
    
    private void tick() {
        if (level == null || level.isClientSide) return;
        
        // Clean up invalid links
        linkedNodes.removeIf(linkedPos -> {
            if (linkedPos.distSqr(worldPosition) > MAX_RANGE * MAX_RANGE) return true;
            BlockEntity be = level.getBlockEntity(linkedPos);
            return !(be instanceof WirelessEnergyNodeBlockEntity);
        });
        
        // Handle energy transfer based on mode
        if (mode == 0 || mode == 2) { // Transmit mode
            transmitEnergy();
        }
        
        if (mode == 1 || mode == 2) { // Receive mode
            receiveEnergy();
        }
        
        // Interact with adjacent blocks
        handleAdjacentBlocks();
    }
    
    private void transmitEnergy() {
        if (energyStorage.getEnergyStored() <= 0) return;
        
        int energyPerNode = Math.min(MAX_TRANSFER, energyStorage.getEnergyStored() / Math.max(1, linkedNodes.size()));
        
        for (BlockPos linkedPos : linkedNodes) {
            if (energyStorage.getEnergyStored() <= 0) break;
            
            BlockEntity be = level.getBlockEntity(linkedPos);
            if (be instanceof WirelessEnergyNodeBlockEntity linkedNode) {
                if (linkedNode.mode == 1 || linkedNode.mode == 2) { // Can receive
                    int toTransfer = Math.min(energyPerNode, energyStorage.getEnergyStored());
                    int transferred = linkedNode.energyStorage.receiveEnergy(toTransfer, false);
                    energyStorage.extractEnergy(transferred, false);
                }
            }
        }
    }
    
    private void receiveEnergy() {
        // Energy is received passively from transmitting nodes
    }
    
    private void handleAdjacentBlocks() {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(direction);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);
            
            if (neighborBE != null) {
                IEnergyHandler neighborEnergy = neighborBE.getCapability(AECapabilities.ENERGY, direction.getOpposite());
                if (neighborEnergy != null) {
                    // Extract from neighbors if receiving
                    if ((mode == 1 || mode == 2) && neighborEnergy.canExtract()) {
                        int space = energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
                        if (space > 0) {
                            int extracted = neighborEnergy.extractEnergy(Math.min(space, MAX_TRANSFER), false);
                            energyStorage.receiveEnergy(extracted, false);
                        }
                    }
                    
                    // Send to neighbors if transmitting
                    if ((mode == 0 || mode == 2) && neighborEnergy.canReceive()) {
                        int available = energyStorage.getEnergyStored();
                        if (available > 0) {
                            int transferred = neighborEnergy.receiveEnergy(Math.min(available, MAX_TRANSFER), false);
                            energyStorage.extractEnergy(transferred, false);
                        }
                    }
                }
            }
        }
    }
    
    public void addLink(BlockPos pos) {
        if (linkedNodes.size() < MAX_CONNECTIONS && !linkedNodes.contains(pos)) {
            linkedNodes.add(pos);
            setChanged();
        }
    }
    
    public void removeLink(BlockPos pos) {
        linkedNodes.remove(pos);
        setChanged();
    }
    
    public List<BlockPos> getLinkedNodes() {
        return new ArrayList<>(linkedNodes);
    }
    
    public void setMode(int mode) {
        this.mode = Math.max(0, Math.min(2, mode));
        setChanged();
    }
    
    public int getMode() {
        return mode;
    }
    
    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        energyStorage.writeToNBT(tag);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putInt("Mode", mode);
        
        ListTag linksList = new ListTag();
        for (BlockPos pos : linkedNodes) {
            linksList.add(NbtUtils.writeBlockPos(pos));
        }
        tag.put("LinkedNodes", linksList);
    }
    
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energyStorage.readFromNBT(tag);
        items = NonNullList.withSize(1, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
        mode = tag.getInt("Mode");
        
        linkedNodes.clear();
        ListTag linksList = tag.getList("LinkedNodes", 10); // Compound type
        for (int i = 0; i < linksList.size(); i++) {
            linkedNodes.add(NbtUtils.readBlockPos(linksList.getCompound(i), worldPosition));
        }
    }
    
    // Container implementation
    @Override
    public int getContainerSize() {
        return items.size();
    }
    
    @Override
    public boolean isEmpty() {
        return items.get(0).isEmpty();
    }
    
    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }
    
    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(items, slot, amount);
    }
    
    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }
    
    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        setChanged();
    }
    
    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }
    
    @Override
    public void clearContent() {
        items.clear();
    }
    
    // MenuProvider implementation
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.astroexpansion.wireless_energy_node");
    }
    
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new WirelessEnergyNodeMenu(containerId, playerInventory, this, getContainerData());
    }
    
    protected ContainerData getContainerData() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> energyStorage.getEnergyStored();
                    case 1 -> energyStorage.getMaxEnergyStored();
                    case 2 -> mode;
                    case 3 -> linkedNodes.size();
                    default -> 0;
                };
            }
            
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> energyStorage.setEnergy(value);
                    case 2 -> mode = value;
                }
            }
            
            @Override
            public int getCount() {
                return 4;
            }
        };
    }
}