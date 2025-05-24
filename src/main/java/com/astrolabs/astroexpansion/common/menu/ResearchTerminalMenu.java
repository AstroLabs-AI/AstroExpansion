package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.ResearchTerminalBlockEntity;
import com.astrolabs.astroexpansion.common.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ResearchTerminalMenu extends AbstractContainerMenu {
    public final ResearchTerminalBlockEntity blockEntity;
    private final Level level;
    
    public ResearchTerminalMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }
    
    public ResearchTerminalMenu(int id, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.RESEARCH_TERMINAL.get(), id);
        checkContainerSize(inv, 0);
        blockEntity = (ResearchTerminalBlockEntity) entity;
        this.level = inv.player.level();
        
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }
    
    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, blockEntity.getBlockState().getBlock());
    }
    
    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }
    
    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
    
    public int getResearchPoints() {
        return blockEntity.getResearchPoints();
    }
    
    public java.util.List<String> getAvailableResearches() {
        return blockEntity.getAvailableResearches();
    }
    
    public int getResearchCost(String researchId) {
        return blockEntity.getResearchCost(researchId);
    }
    
    public void attemptResearch(int researchIndex) {
        if (researchIndex >= 0 && researchIndex < getAvailableResearches().size()) {
            String researchId = getAvailableResearches().get(researchIndex);
            blockEntity.attemptResearch(researchId);
        }
    }
}