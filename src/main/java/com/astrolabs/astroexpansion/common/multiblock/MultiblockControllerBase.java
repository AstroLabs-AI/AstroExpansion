package com.astrolabs.astroexpansion.common.multiblock;

import com.astrolabs.astroexpansion.api.multiblock.IMultiblockController;
import com.astrolabs.astroexpansion.api.multiblock.IMultiblockPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashSet;
import java.util.Set;

public abstract class MultiblockControllerBase extends BlockEntity implements IMultiblockController {
    protected boolean formed = false;
    protected Set<BlockPos> multiblockPositions = new HashSet<>();
    
    public MultiblockControllerBase(net.minecraft.world.level.block.entity.BlockEntityType<?> type, BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
    }
    
    @Override
    public boolean isFormed() {
        return formed;
    }
    
    
    @Override
    public void checkFormation() {
        if (level == null || level.isClientSide) return;
        
        boolean wasFormed = formed;
        IMultiblockPattern pattern = getPattern();
        
        if (pattern != null && pattern.checkPattern(level, worldPosition)) {
            if (!wasFormed) {
                formed = true;
                cacheMultiblockPositions();
                onFormed();
                setChanged();
            }
        } else {
            if (wasFormed) {
                formed = false;
                multiblockPositions.clear();
                onBroken();
                setChanged();
            }
        }
    }
    
    protected void cacheMultiblockPositions() {
        multiblockPositions.clear();
        IMultiblockPattern pattern = getPattern();
        if (pattern == null) return;
        
        BlockPos offset = pattern.getMasterOffset();
        String[][][] patternArray = pattern.getPattern();
        
        for (int y = 0; y < pattern.getHeight(); y++) {
            for (int z = 0; z < pattern.getDepth(); z++) {
                for (int x = 0; x < pattern.getWidth(); x++) {
                    char key = patternArray[y][z][x].charAt(0);
                    if (key != ' ') {
                        BlockPos pos = worldPosition.offset(
                            x - offset.getX(),
                            y - offset.getY(),
                            z - offset.getZ()
                        );
                        multiblockPositions.add(pos);
                    }
                }
            }
        }
    }
    
    @Override
    public BlockPos getMasterPos() {
        return getBlockPos();
    }
    
    @Override
    public Set<BlockPos> getMultiblockPositions() {
        return new HashSet<>(multiblockPositions);
    }
    
    @Override
    public Level getLevel() {
        return level;
    }
    
    @Override
    public void invalidateMultiblock() {
        if (formed) {
            formed = false;
            multiblockPositions.clear();
            onBroken();
            setChanged();
        }
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        formed = tag.getBoolean("formed");
        
        multiblockPositions.clear();
        ListTag posList = tag.getList("multiblockPositions", Tag.TAG_COMPOUND);
        for (int i = 0; i < posList.size(); i++) {
            multiblockPositions.add(NbtUtils.readBlockPos(posList.getCompound(i)));
        }
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("formed", formed);
        
        ListTag posList = new ListTag();
        for (BlockPos pos : multiblockPositions) {
            posList.add(NbtUtils.writeBlockPos(pos));
        }
        tag.put("multiblockPositions", posList);
    }
}