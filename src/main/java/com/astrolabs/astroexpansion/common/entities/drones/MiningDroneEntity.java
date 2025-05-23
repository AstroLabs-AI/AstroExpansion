package com.astrolabs.astroexpansion.common.entities.drones;

import com.astrolabs.astroexpansion.common.registry.ModEntities;
import com.astrolabs.astroexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class MiningDroneEntity extends AbstractDroneEntity {
    private static final int SCAN_RADIUS = 8;
    private static final int MAX_MINE_DISTANCE = 32;
    private static final int ENERGY_PER_BLOCK = 50;
    
    private BlockPos targetBlock = null;
    private int miningProgress = 0;
    private List<BlockPos> scannedBlocks = new ArrayList<>();
    
    public MiningDroneEntity(EntityType<? extends MiningDroneEntity> type, Level level) {
        super(type, level, 9, 10000); // 9 slots, 10k energy
    }
    
    public MiningDroneEntity(Level level) {
        this(ModEntities.MINING_DRONE.get(), level);
    }
    
    @Override
    protected void performTask() {
        if (targetBlock == null || !isValidTarget(targetBlock)) {
            findNextTarget();
        }
        
        if (targetBlock != null) {
            double distance = distanceToSqr(Vec3.atCenterOf(targetBlock));
            
            if (distance > 2.0) {
                // Move to target
                getNavigation().moveTo(targetBlock.getX() + 0.5, targetBlock.getY() + 0.5, targetBlock.getZ() + 0.5, 1.0);
            } else {
                // Mine block
                mineBlock();
            }
        }
    }
    
    private void findNextTarget() {
        scannedBlocks.clear();
        BlockPos currentPos = blockPosition();
        
        // Scan for ores in a sphere around the drone
        for (BlockPos pos : BlockPos.betweenClosed(
            currentPos.offset(-SCAN_RADIUS, -SCAN_RADIUS, -SCAN_RADIUS),
            currentPos.offset(SCAN_RADIUS, SCAN_RADIUS, SCAN_RADIUS))) {
            
            if (pos.distSqr(currentPos) <= SCAN_RADIUS * SCAN_RADIUS && isOreBlock(pos)) {
                scannedBlocks.add(pos.immutable());
            }
        }
        
        // Find closest ore
        if (!scannedBlocks.isEmpty()) {
            targetBlock = scannedBlocks.stream()
                .min((a, b) -> Double.compare(distanceToSqr(Vec3.atCenterOf(a)), distanceToSqr(Vec3.atCenterOf(b))))
                .orElse(null);
        }
    }
    
    private boolean isValidTarget(BlockPos pos) {
        if (homePos != null && pos.distSqr(homePos) > MAX_MINE_DISTANCE * MAX_MINE_DISTANCE) {
            return false;
        }
        return isOreBlock(pos);
    }
    
    private boolean isOreBlock(BlockPos pos) {
        BlockState state = level().getBlockState(pos);
        return state.is(BlockTags.GOLD_ORES) || 
               state.is(BlockTags.IRON_ORES) || 
               state.is(BlockTags.COAL_ORES) ||
               state.is(BlockTags.COPPER_ORES) ||
               state.is(BlockTags.REDSTONE_ORES) ||
               state.is(BlockTags.LAPIS_ORES) ||
               state.is(BlockTags.DIAMOND_ORES) ||
               state.is(BlockTags.EMERALD_ORES) ||
               state.getBlock().getDescriptionId().contains("ore");
    }
    
    private void mineBlock() {
        if (targetBlock == null || !energyStorage.hasEnoughEnergy(ENERGY_PER_BLOCK)) {
            return;
        }
        
        BlockState state = level().getBlockState(targetBlock);
        
        miningProgress++;
        
        // Play mining sound
        if (miningProgress % 5 == 0) {
            level().playSound(null, targetBlock, state.getSoundType().getHitSound(), SoundSource.BLOCKS, 0.5f, 1.0f);
        }
        
        // Calculate mining time based on block hardness
        float hardness = state.getDestroySpeed(level(), targetBlock);
        int miningTime = (int)(hardness * 20); // 1 second per hardness point
        
        if (miningProgress >= miningTime) {
            // Break block and collect drops
            List<ItemStack> drops = Block.getDrops(state, (ServerLevel)level(), targetBlock, null, this, ItemStack.EMPTY);
            
            boolean hasSpace = false;
            for (ItemStack drop : drops) {
                for (int i = 0; i < inventory.getSlots(); i++) {
                    ItemStack remaining = inventory.insertItem(i, drop, false);
                    if (remaining.isEmpty()) {
                        hasSpace = true;
                        break;
                    }
                    drop = remaining;
                }
                
                if (!drop.isEmpty()) {
                    // Drop items if inventory full
                    spawnAtLocation(drop);
                }
            }
            
            // Break the block
            level().destroyBlock(targetBlock, false);
            level().playSound(null, targetBlock, state.getSoundType().getBreakSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
            
            // Consume energy
            energyStorage.extractEnergy(ENERGY_PER_BLOCK, false);
            
            // Reset
            targetBlock = null;
            miningProgress = 0;
            
            // If inventory is full, return home
            if (!hasSpace) {
                setActive(false);
            }
        }
    }
    
    @Override
    protected int getTaskCooldown() {
        return 1; // Check every tick when mining
    }
    
    @Override
    protected int getEnergyConsumptionPerSecond() {
        return 10; // Base consumption
    }
    
    @Override
    protected ItemStack getAsItemStack() {
        ItemStack stack = new ItemStack(ModItems.MINING_DRONE.get());
        CompoundTag tag = new CompoundTag();
        tag.putInt("Energy", energyStorage.getEnergyStored());
        stack.setTag(tag);
        return stack;
    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        
        if (targetBlock != null) {
            tag.putInt("TargetX", targetBlock.getX());
            tag.putInt("TargetY", targetBlock.getY());
            tag.putInt("TargetZ", targetBlock.getZ());
        }
        
        tag.putInt("MiningProgress", miningProgress);
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        
        if (tag.contains("TargetX")) {
            targetBlock = new BlockPos(tag.getInt("TargetX"), tag.getInt("TargetY"), tag.getInt("TargetZ"));
        }
        
        miningProgress = tag.getInt("MiningProgress");
    }
}