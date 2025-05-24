package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.blocks.AdvancedTeleporterBlock;
import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.items.TeleporterFrequencyCardItem;
import com.astrolabs.astroexpansion.common.menu.AdvancedTeleporterMenu;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import com.astrolabs.astroexpansion.common.registry.ModItems;
import com.astrolabs.astroexpansion.common.util.TeleporterNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdvancedTeleporterBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            updateFrequency();
        }
        
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.getItem() instanceof TeleporterFrequencyCardItem;
        }
    };
    
    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(100000, 1000, 0);
    
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    
    protected final ContainerData data;
    private String customName = "";
    private String frequency = "";
    private boolean isMultiblockFormed = false;
    private int teleportCooldown = 0;
    private static final int COOLDOWN_TIME = 100; // 5 seconds
    private static final int ENERGY_PER_BLOCK = 10;
    
    // Animation
    private float portalAnimation = 0.0f;
    private boolean isActive = false;
    
    public AdvancedTeleporterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ADVANCED_TELEPORTER.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> AdvancedTeleporterBlockEntity.this.energyStorage.getEnergyStored();
                    case 1 -> AdvancedTeleporterBlockEntity.this.energyStorage.getMaxEnergyStored();
                    case 2 -> AdvancedTeleporterBlockEntity.this.teleportCooldown;
                    case 3 -> AdvancedTeleporterBlockEntity.this.isMultiblockFormed ? 1 : 0;
                    default -> 0;
                };
            }
            
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> AdvancedTeleporterBlockEntity.this.energyStorage.setEnergy(value);
                    case 2 -> AdvancedTeleporterBlockEntity.this.teleportCooldown = value;
                    case 3 -> AdvancedTeleporterBlockEntity.this.isMultiblockFormed = value > 0;
                }
            }
            
            @Override
            public int getCount() {
                return 4;
            }
        };
    }
    
    @Override
    public Component getDisplayName() {
        if (!customName.isEmpty()) {
            return Component.literal(customName);
        }
        return Component.translatable("block.astroexpansion.advanced_teleporter");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new AdvancedTeleporterMenu(id, inventory, this, this.data);
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        
        return super.getCapability(cap, side);
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
        
        if (!level.isClientSide) {
            checkMultiblockStructure();
            updateFrequency();
            if (!frequency.isEmpty()) {
                TeleporterNetwork.registerTeleporter(frequency, worldPosition, level);
            }
        }
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
    }
    
    @Override
    public void setRemoved() {
        super.setRemoved();
        if (!level.isClientSide && !frequency.isEmpty()) {
            TeleporterNetwork.unregisterTeleporter(frequency, worldPosition, level);
        }
    }
    
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.put("energy", energyStorage.writeToNBT(new CompoundTag()));
        nbt.putString("custom_name", customName);
        nbt.putString("frequency", frequency);
        nbt.putBoolean("multiblock_formed", isMultiblockFormed);
        nbt.putInt("cooldown", teleportCooldown);
        super.saveAdditional(nbt);
    }
    
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        energyStorage.readFromNBT(nbt.getCompound("energy"));
        customName = nbt.getString("custom_name");
        frequency = nbt.getString("frequency");
        isMultiblockFormed = nbt.getBoolean("multiblock_formed");
        teleportCooldown = nbt.getInt("cooldown");
    }
    
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    
    private void updateFrequency() {
        ItemStack card = itemHandler.getStackInSlot(0);
        String oldFrequency = frequency;
        
        if (card.getItem() instanceof TeleporterFrequencyCardItem && card.hasTag()) {
            frequency = card.getTag().getString("frequency");
        } else {
            frequency = "";
        }
        
        if (!level.isClientSide && !oldFrequency.equals(frequency)) {
            if (!oldFrequency.isEmpty()) {
                TeleporterNetwork.unregisterTeleporter(oldFrequency, worldPosition, level);
            }
            if (!frequency.isEmpty()) {
                TeleporterNetwork.registerTeleporter(frequency, worldPosition, level);
            }
        }
    }
    
    private boolean checkMultiblockStructure() {
        // Check for 3x3x3 structure with frame blocks
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue; // Skip center
                    
                    BlockPos checkPos = worldPosition.offset(x, y, z);
                    BlockState state = level.getBlockState(checkPos);
                    
                    // Corners and edges must be frame blocks
                    if (!state.is(ModBlocks.TELEPORTER_FRAME.get())) {
                        isMultiblockFormed = false;
                        return false;
                    }
                }
            }
        }
        
        isMultiblockFormed = true;
        return true;
    }
    
    public boolean teleportEntity(Entity entity, BlockPos targetPos) {
        if (!isMultiblockFormed || frequency.isEmpty() || teleportCooldown > 0) {
            return false;
        }
        
        int distance = (int) Math.sqrt(worldPosition.distSqr(targetPos));
        int energyCost = distance * ENERGY_PER_BLOCK;
        
        if (energyStorage.getEnergyStored() < energyCost) {
            return false;
        }
        
        // Teleport the entity
        entity.teleportTo(targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5);
        
        // Consume energy
        energyStorage.extractEnergy(energyCost, false);
        
        // Set cooldown
        teleportCooldown = COOLDOWN_TIME;
        
        // Effects
        if (level instanceof ServerLevel serverLevel) {
            // Departure effects
            serverLevel.sendParticles(ParticleTypes.PORTAL, 
                worldPosition.getX() + 0.5, worldPosition.getY() + 1.5, worldPosition.getZ() + 0.5,
                50, 0.5, 0.5, 0.5, 1.0);
            
            // Arrival effects
            serverLevel.sendParticles(ParticleTypes.PORTAL,
                targetPos.getX() + 0.5, targetPos.getY() + 1.5, targetPos.getZ() + 0.5,
                50, 0.5, 0.5, 0.5, 1.0);
            
            // Sound effects
            level.playSound(null, worldPosition, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.playSound(null, targetPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        
        return true;
    }
    
    public List<BlockPos> getConnectedTeleporters() {
        if (frequency.isEmpty()) {
            return new ArrayList<>();
        }
        List<BlockPos> connected = new ArrayList<>(TeleporterNetwork.getTeleporters(frequency, level));
        connected.remove(worldPosition); // Remove self
        return connected;
    }
    
    public void setCustomName(String name) {
        this.customName = name;
        setChanged();
    }
    
    public String getCustomName() {
        return customName;
    }
    
    public String getFrequency() {
        return frequency;
    }
    
    public int getEnergyCost(BlockPos target) {
        int distance = (int) Math.sqrt(worldPosition.distSqr(target));
        return distance * ENERGY_PER_BLOCK;
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, AdvancedTeleporterBlockEntity entity) {
        if (entity.teleportCooldown > 0) {
            entity.teleportCooldown--;
        }
        
        if (!level.isClientSide) {
            // Check multiblock every second
            if (level.getGameTime() % 20 == 0) {
                boolean wasFormed = entity.isMultiblockFormed;
                entity.checkMultiblockStructure();
                
                if (wasFormed != entity.isMultiblockFormed) {
                    level.setBlock(pos, state.setValue(AdvancedTeleporterBlock.FORMED, entity.isMultiblockFormed), 3);
                }
            }
            
            // Check for entities to teleport
            if (entity.isMultiblockFormed && entity.teleportCooldown == 0 && !entity.frequency.isEmpty()) {
                AABB scanArea = new AABB(pos).inflate(0.5, 1, 0.5).move(0, 1, 0);
                List<Entity> entities = level.getEntities((Entity)null, scanArea);
                
                if (!entities.isEmpty()) {
                    entity.isActive = true;
                    // Handle teleportation in menu/screen
                } else {
                    entity.isActive = false;
                }
            }
        } else {
            // Client-side animation
            if (entity.isActive || entity.teleportCooldown > 0) {
                entity.portalAnimation += 0.05f;
                
                // Spawn portal particles
                if (level.random.nextInt(3) == 0) {
                    double x = pos.getX() + 0.5;
                    double y = pos.getY() + 1.5;
                    double z = pos.getZ() + 0.5;
                    
                    double angle = level.random.nextDouble() * Math.PI * 2;
                    double radius = level.random.nextDouble() * 0.5;
                    
                    level.addParticle(ParticleTypes.PORTAL,
                        x + Math.cos(angle) * radius,
                        y + level.random.nextDouble() - 0.5,
                        z + Math.sin(angle) * radius,
                        0, 0.1, 0);
                }
            } else {
                entity.portalAnimation *= 0.95f;
            }
        }
    }
    
    public float getPortalAnimation() {
        return portalAnimation;
    }
    
    public boolean isMultiblockFormed() {
        return isMultiblockFormed;
    }
}