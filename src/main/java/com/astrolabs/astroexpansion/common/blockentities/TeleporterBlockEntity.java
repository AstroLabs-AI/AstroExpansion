package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.api.energy.IEnergyHandler;
import com.astrolabs.astroexpansion.common.capabilities.AECapabilityProviders;
import com.astrolabs.astroexpansion.common.menu.TeleporterMenu;
import com.astrolabs.astroexpansion.common.registry.AEBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.energy.EnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeleporterBlockEntity extends AbstractMachineBlockEntity implements MenuProvider {
    private static final int ENERGY_CAPACITY = 100000;
    private static final int ENERGY_PER_TELEPORT = 5000;
    private static final int ENERGY_PER_BLOCK = 10;
    private static final int MAX_RANGE = 1000;
    
    private final EnergyStorage energyStorage = new EnergyStorage(ENERGY_CAPACITY, 1000, 0);
    private final List<TeleporterDestination> destinations = new ArrayList<>();
    private String name = "";
    private boolean isPublic = false;
    private UUID owner;
    
    public TeleporterBlockEntity(BlockPos pos, BlockState blockState) {
        super(AEBlockEntities.TELEPORTER.get(), pos, blockState, ENERGY_CAPACITY);
    }
    
    @Override
    protected void applyImplicitComponents(DataComponentMap.Builder builder) {
        super.applyImplicitComponents(builder);
    }
    
    @Override
    protected void collectImplicitComponents(DataComponentMap components) {
        super.collectImplicitComponents(components);
    }
    
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        tag.readCompound("Energy").ifPresent(energyTag -> energyStorage.deserializeNBT(registries, energyTag));
        name = tag.getStringOr("Name", "Teleporter");
        isPublic = tag.getBooleanOr("IsPublic", false);
        tag.readUUID("Owner").ifPresent(uuid -> owner = uuid);
        
        destinations.clear();
        ListTag destList = tag.getList("Destinations", Tag.TAG_COMPOUND);
        for (int i = 0; i < destList.size(); i++) {
            destinations.add(TeleporterDestination.fromNBT(destList.getCompound(i)));
        }
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Energy", energyStorage.serializeNBT(registries));
        tag.putString("Name", name);
        tag.putBoolean("IsPublic", isPublic);
        if (owner != null) {
            tag.putUUID("Owner", owner);
        }
        
        ListTag destList = new ListTag();
        for (TeleporterDestination dest : destinations) {
            destList.add(dest.toNBT());
        }
        tag.put("Destinations", destList);
    }
    
    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
            extractEnergy();
        }
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, TeleporterBlockEntity blockEntity) {
        blockEntity.tick();
    }
    
    private void extractEnergy() {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(direction);
            if (level.getBlockEntity(neighborPos) instanceof IEnergyHandler energyHandler) {
                int extracted = energyHandler.extractEnergy(1000, false);
                energyStorage.receiveEnergy(extracted, false);
                if (extracted > 0) {
                    setChanged();
                }
            }
        }
    }
    
    public boolean teleportPlayer(ServerPlayer player, TeleporterDestination destination) {
        int distance = (int) Math.sqrt(
            Math.pow(destination.pos().getX() - worldPosition.getX(), 2) +
            Math.pow(destination.pos().getY() - worldPosition.getY(), 2) +
            Math.pow(destination.pos().getZ() - worldPosition.getZ(), 2)
        );
        
        if (distance > MAX_RANGE) {
            player.displayClientMessage(Component.literal("Destination too far!"), true);
            return false;
        }
        
        int energyCost = ENERGY_PER_TELEPORT + (distance * ENERGY_PER_BLOCK);
        if (energyStorage.getEnergyStored() < energyCost) {
            player.displayClientMessage(Component.literal("Not enough energy!"), true);
            return false;
        }
        
        energyStorage.extractEnergy(energyCost, false);
        player.teleportTo(
            destination.pos().getX() + 0.5,
            destination.pos().getY() + 1,
            destination.pos().getZ() + 0.5
        );
        setChanged();
        return true;
    }
    
    public void addDestination(TeleporterDestination destination) {
        destinations.add(destination);
        setChanged();
    }
    
    public void removeDestination(int index) {
        if (index >= 0 && index < destinations.size()) {
            destinations.remove(index);
            setChanged();
        }
    }
    
    public List<TeleporterDestination> getDestinations() {
        return destinations;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        setChanged();
    }
    
    public boolean isPublic() {
        return isPublic;
    }
    
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
        setChanged();
    }
    
    public UUID getOwner() {
        return owner;
    }
    
    public void setOwner(UUID owner) {
        this.owner = owner;
        setChanged();
    }
    
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }
    
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }
    
    @Override
    public Component getDisplayName() {
        return Component.literal("Teleporter");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new TeleporterMenu(id, playerInventory, this);
    }
    
    public void openMenu(ServerPlayer player) {
        player.openMenu(this, pos -> {
            pos.writeBlockPos(worldPosition);
        });
    }
    
    @Override
    public <T> T getCapability(BlockCapability<T, Direction> capability, Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction side) {
        if (capability == AECapabilityProviders.ENERGY_HANDLER) {
            return (T) energyStorage;
        }
        return null;
    }
    
    public static class TeleporterDestination {
        private final String name;
        private final BlockPos pos;
        
        public TeleporterDestination(String name, BlockPos pos) {
            this.name = name;
            this.pos = pos;
        }
        
        public String name() {
            return name;
        }
        
        public BlockPos pos() {
            return pos;
        }
        
        public CompoundTag toNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Name", name);
            tag.putInt("X", pos.getX());
            tag.putInt("Y", pos.getY());
            tag.putInt("Z", pos.getZ());
            return tag;
        }
        
        public static TeleporterDestination fromNBT(CompoundTag tag) {
            return new TeleporterDestination(
                tag.getString("Name"),
                new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"))
            );
        }
    }
}