package com.astrolabs.astroexpansion.common.entities;

import com.astrolabs.astroexpansion.common.entities.drones.AbstractDroneEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.IPlantable;

import java.util.ArrayList;
import java.util.List;

public class FarmingDroneEntity extends AbstractDroneEntity {
    private static final int FARM_RADIUS = 8;
    private static final int ENERGY_PER_ACTION = 50;
    
    private BlockPos farmCenter = null;
    private List<BlockPos> farmland = new ArrayList<>();
    private int currentIndex = 0;
    
    public FarmingDroneEntity(EntityType<? extends AbstractDroneEntity> type, Level level) {
        super(type, level, 18, 15000); // 18 slots for seeds/crops, 15k energy
    }
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new FarmingGoal());
        // Add return to dock goal later if needed
    }
    
    public void setFarmArea(BlockPos center) {
        this.farmCenter = center;
        this.farmland.clear();
        
        // Scan for farmland blocks
        for (int x = -FARM_RADIUS; x <= FARM_RADIUS; x++) {
            for (int z = -FARM_RADIUS; z <= FARM_RADIUS; z++) {
                for (int y = -2; y <= 2; y++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = level().getBlockState(pos);
                    if (state.getBlock() instanceof FarmBlock) {
                        farmland.add(pos);
                    }
                }
            }
        }
    }
    
    class FarmingGoal extends Goal {
        private int actionCooldown = 0;
        
        @Override
        public boolean canUse() {
            return FarmingDroneEntity.this.energyStorage.getEnergyStored() >= ENERGY_PER_ACTION && 
                   farmCenter != null && 
                   !farmland.isEmpty();
        }
        
        @Override
        public void tick() {
            if (actionCooldown > 0) {
                actionCooldown--;
                return;
            }
            
            if (currentIndex >= farmland.size()) {
                currentIndex = 0;
            }
            
            BlockPos farmPos = farmland.get(currentIndex);
            double distance = distanceToSqr(Vec3.atCenterOf(farmPos));
            
            if (distance > 9.0) {
                // Move to farm position
                getNavigation().moveTo(farmPos.getX() + 0.5, 
                    farmPos.getY() + 1, farmPos.getZ() + 0.5, 1.0);
            } else {
                // Perform farming action
                BlockPos cropPos = farmPos.above();
                BlockState cropState = level().getBlockState(cropPos);
                
                if (cropState.getBlock() instanceof CropBlock crop) {
                    // Harvest if mature
                    if (crop.isMaxAge(cropState)) {
                        harvestCrop(cropPos, cropState);
                        actionCooldown = 10;
                    }
                } else if (cropState.isAir()) {
                    // Plant seeds if we have any
                    plantSeeds(cropPos);
                    actionCooldown = 10;
                }
                
                currentIndex++;
            }
        }
        
        private void harvestCrop(BlockPos pos, BlockState state) {
            // Get drops
            List<ItemStack> drops = Block.getDrops(state, (ServerLevel)level(), pos, null);
            
            // Add to inventory
            for (ItemStack drop : drops) {
                for (int i = 0; i < inventory.getSlots(); i++) {
                    drop = inventory.insertItem(i, drop, false);
                    if (drop.isEmpty()) break;
                }
            }
            
            // Break crop
            level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            FarmingDroneEntity.this.energyStorage.extractEnergy(ENERGY_PER_ACTION, false);
        }
        
        private void plantSeeds(BlockPos pos) {
            // Look for seeds in inventory
            for (int i = 0; i < inventory.getSlots(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (stack.getItem() instanceof IPlantable plantable) {
                    BlockState plant = plantable.getPlant(level(), pos);
                    if (plant.canSurvive(level(), pos)) {
                        level().setBlock(pos, plant, 3);
                        stack.shrink(1);
                        FarmingDroneEntity.this.energyStorage.extractEnergy(ENERGY_PER_ACTION, false);
                        break;
                    }
                } else if (stack.is(Items.WHEAT_SEEDS)) {
                    level().setBlock(pos, Blocks.WHEAT.defaultBlockState(), 3);
                    stack.shrink(1);
                    FarmingDroneEntity.this.energyStorage.extractEnergy(ENERGY_PER_ACTION, false);
                    break;
                } else if (stack.is(Items.CARROT)) {
                    level().setBlock(pos, Blocks.CARROTS.defaultBlockState(), 3);
                    stack.shrink(1);
                    FarmingDroneEntity.this.energyStorage.extractEnergy(ENERGY_PER_ACTION, false);
                    break;
                } else if (stack.is(Items.POTATO)) {
                    level().setBlock(pos, Blocks.POTATOES.defaultBlockState(), 3);
                    stack.shrink(1);
                    FarmingDroneEntity.this.energyStorage.extractEnergy(ENERGY_PER_ACTION, false);
                    break;
                }
            }
        }
    }
    
    @Override
    protected ItemStack getAsItemStack() {
        ItemStack stack = new ItemStack(com.astrolabs.astroexpansion.common.registry.ModItems.FARMING_DRONE.get());
        // Store drone data in item NBT if needed
        return stack;
    }
    
    @Override
    protected void performTask() {
        // Handled by goal system
    }
    
    @Override
    protected int getEnergyConsumptionPerSecond() {
        return 5; // Base energy consumption
    }
    
    @Override
    protected int getTaskCooldown() {
        return 40; // 2 seconds between farming actions
    }
}