package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.menu.ResearchTerminalMenu;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ResearchTerminalBlockEntity extends BlockEntity implements MenuProvider {
    private int researchPoints = 0;
    
    public ResearchTerminalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESEARCH_TERMINAL.get(), pos, state);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, ResearchTerminalBlockEntity blockEntity) {
        // Terminal doesn't need constant updates
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        researchPoints = tag.getInt("researchPoints");
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("researchPoints", researchPoints);
    }
    
    @Override
    public Component getDisplayName() {
        return Component.literal("Research Terminal");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ResearchTerminalMenu(id, inventory, this);
    }
    
    public int getResearchPoints() {
        return researchPoints;
    }
    
    public void addResearchPoints(int points) {
        researchPoints += points;
        setChanged();
    }
    
    public boolean consumeResearchPoints(int points) {
        if (researchPoints >= points) {
            researchPoints -= points;
            setChanged();
            return true;
        }
        return false;
    }
}