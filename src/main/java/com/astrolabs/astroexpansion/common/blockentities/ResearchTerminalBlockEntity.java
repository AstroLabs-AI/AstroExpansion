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
    
    public java.util.List<String> getAvailableResearches() {
        // Return list of available research topics
        java.util.List<String> researches = new java.util.ArrayList<>();
        researches.add("advanced_materials");
        researches.add("energy_efficiency");
        researches.add("rocket_propulsion");
        researches.add("quantum_computing");
        researches.add("fusion_technology");
        researches.add("drone_automation");
        researches.add("multiblock_optimization");
        researches.add("space_exploration");
        return researches;
    }
    
    public int getResearchCost(String researchId) {
        // Return cost for each research topic
        switch (researchId) {
            case "advanced_materials":
                return 100;
            case "energy_efficiency":
                return 150;
            case "rocket_propulsion":
                return 200;
            case "quantum_computing":
                return 300;
            case "fusion_technology":
                return 400;
            case "drone_automation":
                return 250;
            case "multiblock_optimization":
                return 350;
            case "space_exploration":
                return 500;
            default:
                return 100;
        }
    }
    
    public void attemptResearch(String researchId) {
        int cost = getResearchCost(researchId);
        if (consumeResearchPoints(cost)) {
            // Research successful - in a real implementation, this would unlock new recipes/features
            if (level != null && !level.isClientSide) {
                // Play success sound or send message to player
            }
        }
    }
}