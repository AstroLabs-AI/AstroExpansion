package com.astrolabs.astroexpansion.common.entities;

import com.astrolabs.astroexpansion.common.entities.drones.AbstractDroneEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class ConstructionDroneEntity extends AbstractDroneEntity {
    private static final int BUILD_RANGE = 16;
    private static final int ENERGY_PER_BLOCK = 100;
    
    private Queue<BlockPos> buildQueue = new LinkedList<>();
    private Map<BlockPos, BlockState> blueprint = new HashMap<>();
    private BlockPos currentTarget = null;
    
    public ConstructionDroneEntity(EntityType<? extends AbstractDroneEntity> type, Level level) {
        super(type, level, 27, 20000); // 27 slots for materials, 20k energy
    }
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new ConstructionGoal());
        // Add return to dock goal later if needed
    }
    
    public void setBlueprint(Map<BlockPos, BlockState> blueprint) {
        this.blueprint.clear();
        this.blueprint.putAll(blueprint);
        this.buildQueue.clear();
        this.buildQueue.addAll(blueprint.keySet());
    }
    
    class ConstructionGoal extends Goal {
        private int cooldown = 0;
        
        @Override
        public boolean canUse() {
            if (ConstructionDroneEntity.this.energyStorage.getEnergyStored() < ENERGY_PER_BLOCK || buildQueue.isEmpty()) {
                return false;
            }
            
            // Check if we have materials
            if (!hasRequiredMaterials()) {
                return false;
            }
            
            return true;
        }
        
        @Override
        public void tick() {
            if (cooldown > 0) {
                cooldown--;
                return;
            }
            
            if (currentTarget == null && !buildQueue.isEmpty()) {
                currentTarget = buildQueue.poll();
            }
            
            if (currentTarget != null) {
                double distance = distanceToSqr(Vec3.atCenterOf(currentTarget));
                
                if (distance > 9.0) {
                    // Move towards target
                    getNavigation().moveTo(currentTarget.getX() + 0.5, 
                        currentTarget.getY() + 1, currentTarget.getZ() + 0.5, 1.0);
                } else {
                    // Place block
                    BlockState targetState = blueprint.get(currentTarget);
                    if (targetState != null && level().getBlockState(currentTarget).isAir()) {
                        if (placeBlock(currentTarget, targetState)) {
                            ConstructionDroneEntity.this.energyStorage.extractEnergy(ENERGY_PER_BLOCK, false);
                            cooldown = 20; // 1 second cooldown
                        }
                    }
                    currentTarget = null;
                }
            }
        }
        
        private boolean hasRequiredMaterials() {
            // Check inventory for required blocks
            for (BlockState state : blueprint.values()) {
                boolean found = false;
                for (int i = 0; i < inventory.getSlots(); i++) {
                    ItemStack stack = inventory.getStackInSlot(i);
                    if (stack.getItem() instanceof BlockItem blockItem) {
                        if (blockItem.getBlock() == state.getBlock()) {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) return false;
            }
            return true;
        }
        
        private boolean placeBlock(BlockPos pos, BlockState state) {
            // Find matching block in inventory
            for (int i = 0; i < inventory.getSlots(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (stack.getItem() instanceof BlockItem blockItem) {
                    if (blockItem.getBlock() == state.getBlock()) {
                        // Place block
                        level().setBlock(pos, state, 3);
                        stack.shrink(1);
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    @Override
    protected ItemStack getAsItemStack() {
        ItemStack stack = new ItemStack(com.astrolabs.astroexpansion.common.registry.ModItems.CONSTRUCTION_DRONE.get());
        // Store drone data in item NBT if needed
        return stack;
    }
    
    @Override
    protected void performTask() {
        // Handled by goal system
    }
    
    @Override
    protected int getEnergyConsumptionPerSecond() {
        return 10; // Base energy consumption
    }
    
    @Override
    protected int getTaskCooldown() {
        return 20; // 1 second between tasks
    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        
        // Save blueprint
        ListTag blueprintList = new ListTag();
        for (Map.Entry<BlockPos, BlockState> entry : blueprint.entrySet()) {
            CompoundTag blueprintTag = new CompoundTag();
            blueprintTag.put("pos", NbtUtils.writeBlockPos(entry.getKey()));
            blueprintTag.putString("state", entry.getValue().toString());
            blueprintList.add(blueprintTag);
        }
        compound.put("blueprint", blueprintList);
        
        // Save build queue
        ListTag queueList = new ListTag();
        for (BlockPos pos : buildQueue) {
            queueList.add(NbtUtils.writeBlockPos(pos));
        }
        compound.put("buildQueue", queueList);
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        
        // Load blueprint
        blueprint.clear();
        ListTag blueprintList = compound.getList("blueprint", 10);
        for (int i = 0; i < blueprintList.size(); i++) {
            CompoundTag blueprintTag = blueprintList.getCompound(i);
            BlockPos pos = NbtUtils.readBlockPos(blueprintTag.getCompound("pos"));
            // Note: Simplified state loading, would need proper parsing in production
            blueprint.put(pos, Blocks.STONE.defaultBlockState());
        }
        
        // Load build queue
        buildQueue.clear();
        ListTag queueList = compound.getList("buildQueue", 10);
        for (int i = 0; i < queueList.size(); i++) {
            buildQueue.add(NbtUtils.readBlockPos(queueList.getCompound(i)));
        }
    }
}