package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.OreWasherBlockEntity;
import com.astrolabs.astroexpansion.common.capabilities.CombinedItemHandler;
import com.astrolabs.astroexpansion.common.items.upgrades.UpgradeItem;
import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import com.astrolabs.astroexpansion.common.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class OreWasherMenu extends AbstractContainerMenu {
    public final OreWasherBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    
    public OreWasherMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(6));
    }
    
    public OreWasherMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.ORE_WASHER_MENU.get(), id);
        checkContainerSize(inv, 3);
        this.blockEntity = (OreWasherBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;
        
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // Combined handler includes main inventory (0-2) and upgrades (3-6)
            if (handler instanceof CombinedItemHandler) {
                // Main slots
                this.addSlot(new SlotItemHandler(handler, 0, 53, 50));
                this.addSlot(new SlotItemHandler(handler, 1, 109, 41) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }
                });
                this.addSlot(new SlotItemHandler(handler, 2, 109, 59) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }
                });
                
                // Upgrade slots (in a row at the top)
                for (int i = 0; i < 4; i++) {
                    this.addSlot(new SlotItemHandler(handler, 3 + i, 44 + i * 18, 17) {
                        @Override
                        public boolean mayPlace(ItemStack stack) {
                            return stack.getItem() instanceof UpgradeItem;
                        }
                        
                        @Override
                        public int getMaxStackSize() {
                            return 1;
                        }
                    });
                }
            } else {
                // Fallback for old behavior
                this.addSlot(new SlotItemHandler(handler, 0, 53, 50));
                this.addSlot(new SlotItemHandler(handler, 1, 109, 41) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }
                });
                this.addSlot(new SlotItemHandler(handler, 2, 109, 59) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }
                });
            }
        });
        
        addDataSlots(data);
    }
    
    public boolean isCrafting() {
        return data.get(0) > 0;
    }
    
    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 26;
        
        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }
    
    public int getScaledEnergy() {
        int energy = this.data.get(2);
        int maxEnergy = this.data.get(3);
        int energyBarSize = 60;
        
        return maxEnergy != 0 ? (int)(((float)energy / maxEnergy) * energyBarSize) : 0;
    }
    
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    
    private static final int TE_INVENTORY_SLOT_COUNT = 7; // 3 main slots + 4 upgrade slots
    
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();
        
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // Moving from player inventory
            if (sourceStack.getItem() instanceof UpgradeItem) {
                // Try to move upgrades to upgrade slots
                if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX + 3, TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Try to move other items to input slot
                if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // Moving from machine slots back to player inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }
    
    public int getEnergyStored() {
        return data.get(2);
    }
    
    public int getMaxEnergyStored() {
        return data.get(3);
    }
    
    public int getFluidAmount() {
        return data.get(4);
    }
    
    public int getFluidCapacity() {
        return data.get(5);
    }
    
    public int getScaledWater() {
        int fluid = getFluidAmount();
        int capacity = getFluidCapacity();
        int barHeight = 52;
        
        return capacity != 0 && fluid != 0 ? fluid * barHeight / capacity : 0;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
            player, ModBlocks.ORE_WASHER.get());
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
}