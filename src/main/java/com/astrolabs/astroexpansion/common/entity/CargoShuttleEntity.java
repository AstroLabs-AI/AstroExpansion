package com.astrolabs.astroexpansion.common.entity;

import com.astrolabs.astroexpansion.common.registry.ModDimensions;
import com.astrolabs.astroexpansion.common.registry.ModEntities;
import com.astrolabs.astroexpansion.common.registry.ModItems;
import com.astrolabs.astroexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import net.minecraft.network.chat.Component;

public class CargoShuttleEntity extends Entity implements MenuProvider {
    private static final EntityDataAccessor<Integer> FUEL = SynchedEntityData.defineId(CargoShuttleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> LAUNCHED = SynchedEntityData.defineId(CargoShuttleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AUTO_PILOT = SynchedEntityData.defineId(CargoShuttleEntity.class, EntityDataSerializers.BOOLEAN);
    
    private final ItemStackHandler itemHandler = new ItemStackHandler(54); // Large inventory
    private final LazyOptional<IItemHandler> itemHandlerOpt = LazyOptional.of(() -> itemHandler);
    
    private int launchTimer = 0;
    private static final int LAUNCH_TIME = 150; // 7.5 seconds
    private static final int MAX_FUEL = 2000;
    private Player pilot;
    private BlockPos targetPos;
    
    public CargoShuttleEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    
    public CargoShuttleEntity(Level level, BlockPos pos) {
        this(ModEntities.CARGO_SHUTTLE.get(), level);
        this.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }
    
    @Override
    protected void defineSynchedData() {
        this.entityData.define(FUEL, 0);
        this.entityData.define(LAUNCHED, false);
        this.entityData.define(AUTO_PILOT, false);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (!level().isClientSide) {
            boolean launched = entityData.get(LAUNCHED);
            boolean autoPilot = entityData.get(AUTO_PILOT);
            
            if (launched) {
                launchTimer++;
                
                // Consume fuel
                int fuel = entityData.get(FUEL);
                if (fuel > 0) {
                    entityData.set(FUEL, fuel - 1); // Slower fuel consumption
                }
                
                // Apply motion
                if (fuel > 0 && launchTimer < LAUNCH_TIME) {
                    double launchPower = 0.3 + (launchTimer / (double)LAUNCH_TIME) * 1.5;
                    this.setDeltaMovement(0, launchPower, 0);
                } else if (fuel <= 0) {
                    // Out of fuel - start falling
                    this.setDeltaMovement(this.getDeltaMovement().add(0, -0.08, 0));
                }
                
                // Auto-pilot to space station
                if (autoPilot && this.getY() > 300 && fuel > 0) {
                    if (pilot != null && pilot instanceof ServerPlayer serverPlayer) {
                        ServerLevel spaceDim = serverPlayer.server.getLevel(ModDimensions.SPACE_KEY);
                        if (spaceDim != null) {
                            // Find or create space station
                            BlockPos stationPos = findOrCreateSpaceStation(spaceDim);
                            serverPlayer.changeDimension(spaceDim, new SpaceTeleporter(stationPos));
                            
                            // Spawn shuttle at station
                            CargoShuttleEntity newShuttle = new CargoShuttleEntity(spaceDim, stationPos);
                            newShuttle.itemHandler.deserializeNBT(this.itemHandler.serializeNBT());
                            spaceDim.addFreshEntity(newShuttle);
                            
                            this.discard();
                            return;
                        }
                    } else {
                        // Unmanned cargo delivery
                        this.discard();
                        return;
                    }
                }
                
                // Crash if hit ground with momentum
                if (this.onGround() && this.getDeltaMovement().y < -0.5) {
                    this.dropAllItems();
                    this.explode();
                    return;
                }
            }
            
            this.move(MoverType.SELF, this.getDeltaMovement());
        } else {
            // Client-side particles
            if (entityData.get(LAUNCHED) && entityData.get(FUEL) > 0) {
                for (int i = 0; i < 8; i++) {
                    level().addParticle(ParticleTypes.FLAME,
                        getX() + (random.nextDouble() - 0.5) * 1.0,
                        getY() - 1.0,
                        getZ() + (random.nextDouble() - 0.5) * 1.0,
                        0, -0.5, 0);
                    level().addParticle(ParticleTypes.LARGE_SMOKE,
                        getX() + (random.nextDouble() - 0.5) * 1.0,
                        getY() - 1.0,
                        getZ() + (random.nextDouble() - 0.5) * 1.0,
                        0, -0.3, 0);
                }
            }
        }
    }
    
    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!level().isClientSide && !entityData.get(LAUNCHED)) {
            ItemStack heldItem = player.getItemInHand(hand);
            
            // Check if player is trying to fuel the rocket with plasma
            if (heldItem.getItem() == ModItems.DEUTERIUM_CELL.get() && entityData.get(FUEL) < MAX_FUEL) {
                entityData.set(FUEL, Math.min(entityData.get(FUEL) + 500, MAX_FUEL));
                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }
                player.displayClientMessage(Component.literal("Fuel: " + entityData.get(FUEL) + "/" + MAX_FUEL), true);
                level().playSound(null, getX(), getY(), getZ(), 
                    SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
            
            // Shift-click to set auto-pilot
            if (player.isShiftKeyDown() && heldItem.isEmpty()) {
                boolean autoPilot = !entityData.get(AUTO_PILOT);
                entityData.set(AUTO_PILOT, autoPilot);
                player.displayClientMessage(Component.literal("Auto-pilot: " + (autoPilot ? "ON" : "OFF")), true);
                return InteractionResult.SUCCESS;
            }
            
            // Open cargo interface
            if (pilot == null && heldItem.isEmpty() && entityData.get(FUEL) > 0) {
                player.openMenu(this);
                return InteractionResult.SUCCESS;
            }
            
            // Mount the shuttle
            if (pilot == null && !player.isShiftKeyDown() && entityData.get(FUEL) > 0) {
                player.startRiding(this);
                pilot = player;
                return InteractionResult.SUCCESS;
            } else if (entityData.get(FUEL) <= 0) {
                player.displayClientMessage(Component.literal("Shuttle needs Deuterium fuel!"), true);
            }
        }
        return InteractionResult.PASS;
    }
    
    public void launch() {
        if (!level().isClientSide && !entityData.get(LAUNCHED)) {
            entityData.set(LAUNCHED, true);
            level().playSound(null, getX(), getY(), getZ(), 
                SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.NEUTRAL, 4.0F, 0.4F);
        }
    }
    
    private void explode() {
        if (!level().isClientSide) {
            level().explode(this, getX(), getY(), getZ(), 3.0F, Level.ExplosionInteraction.BLOCK);
            if (pilot != null) {
                pilot.hurt(level().damageSources().explosion(this, this), 30.0F);
            }
            this.discard();
        }
    }
    
    private void dropAllItems() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                this.spawnAtLocation(stack);
            }
        }
    }
    
    private BlockPos findOrCreateSpaceStation(ServerLevel spaceDim) {
        // Simple implementation - would be expanded with actual station generation
        return new BlockPos(0, 100, 0);
    }
    
    @Override
    public void remove(RemovalReason reason) {
        if (pilot != null) {
            pilot.stopRiding();
        }
        itemHandlerOpt.invalidate();
        super.remove(reason);
    }
    
    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        entityData.set(FUEL, compound.getInt("Fuel"));
        entityData.set(LAUNCHED, compound.getBoolean("Launched"));
        entityData.set(AUTO_PILOT, compound.getBoolean("AutoPilot"));
        launchTimer = compound.getInt("LaunchTimer");
        itemHandler.deserializeNBT(compound.getCompound("Inventory"));
    }
    
    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Fuel", entityData.get(FUEL));
        compound.putBoolean("Launched", entityData.get(LAUNCHED));
        compound.putBoolean("AutoPilot", entityData.get(AUTO_PILOT));
        compound.putInt("LaunchTimer", launchTimer);
        compound.put("Inventory", itemHandler.serializeNBT());
    }
    
    @Override
    public boolean isPickable() {
        return !entityData.get(LAUNCHED);
    }
    
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide && !entityData.get(LAUNCHED)) {
            this.dropAllItems();
            this.spawnAtLocation(new ItemStack(ModItems.CARGO_SHUTTLE.get()));
            this.discard();
            return true;
        }
        return false;
    }
    
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
    
    public Vec3 getPassengerAttachmentPoint(Entity entity) {
        return new Vec3(0, this.getBbHeight() * 0.75, 0);
    }
    
    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() < 1 && passenger instanceof Player;
    }
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerOpt.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("entity.astroexpansion.cargo_shuttle");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return ChestMenu.sixRows(containerId, playerInventory, new SimpleContainer(itemHandler));
    }
    
    // Simple container wrapper for the item handler
    private static class SimpleContainer implements net.minecraft.world.Container {
        private final ItemStackHandler handler;
        
        public SimpleContainer(ItemStackHandler handler) {
            this.handler = handler;
        }
        
        @Override
        public int getContainerSize() {
            return handler.getSlots();
        }
        
        @Override
        public boolean isEmpty() {
            for (int i = 0; i < handler.getSlots(); i++) {
                if (!handler.getStackInSlot(i).isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public ItemStack getItem(int slot) {
            return handler.getStackInSlot(slot);
        }
        
        @Override
        public ItemStack removeItem(int slot, int amount) {
            return handler.extractItem(slot, amount, false);
        }
        
        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return handler.extractItem(slot, handler.getStackInSlot(slot).getCount(), false);
        }
        
        @Override
        public void setItem(int slot, ItemStack stack) {
            handler.setStackInSlot(slot, stack);
        }
        
        @Override
        public void setChanged() {}
        
        @Override
        public boolean stillValid(Player player) {
            return true;
        }
        
        @Override
        public void clearContent() {
            for (int i = 0; i < handler.getSlots(); i++) {
                handler.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }
}