package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.RocketWorkbenchBlockEntity;
import com.astrolabs.astroexpansion.common.registry.ModBlocks;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class RocketWorkbenchMenu extends AbstractContainerMenu {
    private final RocketWorkbenchBlockEntity blockEntity;
    private final Level level;
    
    public RocketWorkbenchMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }
    
    public RocketWorkbenchMenu(int containerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.ROCKET_WORKBENCH.get(), containerId);
        checkContainerSize(inv, 10);
        blockEntity = ((RocketWorkbenchBlockEntity) entity);
        this.level = inv.player.level();
        
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            // Crafting grid (3x3)
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    this.addSlot(new SlotItemHandler(iItemHandler, row * 3 + col, 30 + col * 18, 17 + row * 18));
                }
            }
            
            // Output slot
            this.addSlot(new SlotItemHandler(iItemHandler, 9, 124, 35) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
                
                @Override
                public void onTake(Player player, ItemStack stack) {
                    blockEntity.craftItem();
                    super.onTake(player, stack);
                }
            });
        });
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();
        
        // Check if the slot clicked is one of the vanilla container slots
        if (index < 36) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, 36, 45, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < 46) {
            // This is a tile slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
        
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.ROCKET_WORKBENCH.get());
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
    
    public RocketWorkbenchBlockEntity getBlockEntity() {
        return blockEntity;
    }
}