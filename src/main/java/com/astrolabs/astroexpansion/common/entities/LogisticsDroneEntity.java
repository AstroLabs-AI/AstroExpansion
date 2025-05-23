package com.astrolabs.astroexpansion.common.entities;

import com.astrolabs.astroexpansion.common.entities.drones.AbstractDroneEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class LogisticsDroneEntity extends AbstractDroneEntity {
    private static final int TRANSFER_RANGE = 32;
    private static final int ENERGY_PER_TRANSFER = 25;
    
    private List<BlockPos> waypoints = new ArrayList<>();
    private int currentWaypointIndex = 0;
    private BlockPos currentDestination = null;
    
    // Transfer rules
    private List<TransferRule> transferRules = new ArrayList<>();
    
    public LogisticsDroneEntity(EntityType<? extends AbstractDroneEntity> type, Level level) {
        super(type, level, 27, 15000); // 27 slots for items, 15k energy
    }
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new LogisticsGoal());
        // Add return to dock goal later if needed
    }
    
    public void addWaypoint(BlockPos pos) {
        waypoints.add(pos);
    }
    
    public void clearWaypoints() {
        waypoints.clear();
        currentWaypointIndex = 0;
    }
    
    public void addTransferRule(TransferRule rule) {
        transferRules.add(rule);
    }
    
    class LogisticsGoal extends Goal {
        private int transferCooldown = 0;
        
        @Override
        public boolean canUse() {
            return LogisticsDroneEntity.this.energyStorage.getEnergyStored() >= ENERGY_PER_TRANSFER && !waypoints.isEmpty();
        }
        
        @Override
        public void tick() {
            if (transferCooldown > 0) {
                transferCooldown--;
                return;
            }
            
            if (currentDestination == null && !waypoints.isEmpty()) {
                currentDestination = waypoints.get(currentWaypointIndex);
            }
            
            if (currentDestination != null) {
                double distance = distanceToSqr(Vec3.atCenterOf(currentDestination));
                
                if (distance > 9.0) {
                    // Move to waypoint
                    getNavigation().moveTo(currentDestination.getX() + 0.5, 
                        currentDestination.getY() + 1, currentDestination.getZ() + 0.5, 1.0);
                } else {
                    // At waypoint, perform transfers
                    performTransfers();
                    
                    // Move to next waypoint
                    currentWaypointIndex = (currentWaypointIndex + 1) % waypoints.size();
                    currentDestination = null;
                    transferCooldown = 20; // 1 second cooldown
                }
            }
        }
        
        private void performTransfers() {
            BlockEntity be = level().getBlockEntity(currentDestination);
            if (be == null) return;
            
            be.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(targetHandler -> {
                // Check transfer rules
                for (TransferRule rule : transferRules) {
                    if (rule.matches(currentDestination)) {
                        if (rule.isInput) {
                            // Take items from target
                            transferFromTarget(targetHandler, rule);
                        } else {
                            // Give items to target
                            transferToTarget(targetHandler, rule);
                        }
                    }
                }
            });
        }
        
        private void transferFromTarget(IItemHandler target, TransferRule rule) {
            for (int i = 0; i < target.getSlots(); i++) {
                ItemStack stack = target.extractItem(i, 64, true);
                if (!stack.isEmpty() && rule.matchesItem(stack)) {
                    // Try to insert into drone inventory
                    ItemStack remaining = stack.copy();
                    for (int j = 0; j < inventory.getSlots(); j++) {
                        remaining = inventory.insertItem(j, remaining, false);
                        if (remaining.isEmpty()) break;
                    }
                    
                    int transferred = stack.getCount() - remaining.getCount();
                    if (transferred > 0) {
                        target.extractItem(i, transferred, false);
                        LogisticsDroneEntity.this.energyStorage.extractEnergy(ENERGY_PER_TRANSFER, false);
                        break; // One transfer per tick
                    }
                }
            }
        }
        
        private void transferToTarget(IItemHandler target, TransferRule rule) {
            for (int i = 0; i < inventory.getSlots(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (!stack.isEmpty() && rule.matchesItem(stack)) {
                    // Try to insert into target
                    ItemStack toTransfer = inventory.extractItem(i, 64, true);
                    ItemStack remaining = toTransfer.copy();
                    
                    for (int j = 0; j < target.getSlots(); j++) {
                        remaining = target.insertItem(j, remaining, false);
                        if (remaining.isEmpty()) break;
                    }
                    
                    int transferred = toTransfer.getCount() - remaining.getCount();
                    if (transferred > 0) {
                        inventory.extractItem(i, transferred, false);
                        LogisticsDroneEntity.this.energyStorage.extractEnergy(ENERGY_PER_TRANSFER, false);
                        break; // One transfer per tick
                    }
                }
            }
        }
    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        
        // Save waypoints
        ListTag waypointList = new ListTag();
        for (BlockPos pos : waypoints) {
            waypointList.add(NbtUtils.writeBlockPos(pos));
        }
        compound.put("waypoints", waypointList);
        compound.putInt("waypointIndex", currentWaypointIndex);
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        
        // Load waypoints
        waypoints.clear();
        ListTag waypointList = compound.getList("waypoints", 10);
        for (int i = 0; i < waypointList.size(); i++) {
            waypoints.add(NbtUtils.readBlockPos(waypointList.getCompound(i)));
        }
        currentWaypointIndex = compound.getInt("waypointIndex");
    }
    
    public static class TransferRule {
        public final BlockPos position;
        public final ItemStack filterItem;
        public final boolean isInput;
        
        public TransferRule(BlockPos position, ItemStack filterItem, boolean isInput) {
            this.position = position;
            this.filterItem = filterItem;
            this.isInput = isInput;
        }
        
        public boolean matches(BlockPos pos) {
            return position.equals(pos);
        }
        
        public boolean matchesItem(ItemStack stack) {
            return filterItem.isEmpty() || ItemStack.isSameItemSameTags(stack, filterItem);
        }
    }
    
    @Override
    protected ItemStack getAsItemStack() {
        ItemStack stack = new ItemStack(com.astrolabs.astroexpansion.common.registry.ModItems.LOGISTICS_DRONE.get());
        // Store drone data in item NBT if needed
        return stack;
    }
    
    @Override
    protected void performTask() {
        // Handled by goal system
    }
    
    @Override
    protected int getEnergyConsumptionPerSecond() {
        return 8; // Moderate energy consumption
    }
    
    @Override
    protected int getTaskCooldown() {
        return 20; // 1 second between waypoint checks
    }
}