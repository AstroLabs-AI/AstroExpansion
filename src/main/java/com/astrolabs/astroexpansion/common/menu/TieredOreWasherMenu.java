package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.TieredOreWasherBlockEntity;
import com.astrolabs.astroexpansion.common.blocks.machines.base.MachineTier;
import com.astrolabs.astroexpansion.common.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.SlotItemHandler;

public class TieredOreWasherMenu extends AbstractContainerMenu {
    private final TieredOreWasherBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    private final MachineTier tier;
    private FluidStack fluidStack = FluidStack.EMPTY;
    
    public TieredOreWasherMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(6), MachineTier.BASIC);
    }
    
    public TieredOreWasherMenu(int id, Inventory inv, BlockEntity entity, ContainerData data, MachineTier tier) {
        super(ModMenuTypes.TIERED_ORE_WASHER.get(), id);
        checkContainerSize(inv, 6);
        this.blockEntity = (TieredOreWasherBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;
        this.tier = tier;
        
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // Input slot
            this.addSlot(new SlotItemHandler(handler, 0, 56, 35));
            // Primary output slot
            this.addSlot(new SlotItemHandler(handler, 1, 101, 35) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
            // Secondary output slot
            this.addSlot(new SlotItemHandler(handler, 2, 123, 35) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
            
            // Upgrade slots (positioned based on tier)
            int upgradeStartX = 8;
            int upgradeStartY = 60;
            for (int i = 0; i < tier.getUpgradeSlots(); i++) {
                this.addSlot(new SlotItemHandler(blockEntity.getUpgradeInventory(), i, 
                    upgradeStartX + (i % 3) * 18, upgradeStartY + (i / 3) * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return blockEntity.isValidUpgrade(stack);
                    }
                });
            }
        });
        
        // Get fluid info
        if (blockEntity != null && blockEntity.getFluidTank() != null) {
            this.fluidStack = blockEntity.getFluidTank().getFluid();
        }
        
        addDataSlots(data);
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
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        
        ItemStack stack = slot.getItem();
        ItemStack stackCopy = stack.copy();
        
        int inventoryStart = 3 + tier.getUpgradeSlots();
        int inventoryEnd = inventoryStart + 36;
        
        // From output slots
        if (index == 1 || index == 2) {
            if (!this.moveItemStackTo(stack, inventoryStart, inventoryEnd, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, stackCopy);
        }
        // From input slot or upgrade slots
        else if (index < inventoryStart) {
            if (!this.moveItemStackTo(stack, inventoryStart, inventoryEnd, false)) {
                return ItemStack.EMPTY;
            }
        }
        // From player inventory
        else {
            // Try input slot first
            if (!this.moveItemStackTo(stack, 0, 1, false)) {
                // Try upgrade slots if it's an upgrade
                if (blockEntity.isValidUpgrade(stack)) {
                    if (!this.moveItemStackTo(stack, 3, 3 + tier.getUpgradeSlots(), false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }
        }
        
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        
        if (stack.getCount() == stackCopy.getCount()) {
            return ItemStack.EMPTY;
        }
        
        slot.onTake(player, stack);
        return stackCopy;
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
        int energyBarSize = 52;
        
        return maxEnergy != 0 && energy != 0 ? energy * energyBarSize / maxEnergy : 0;
    }
    
    public int getScaledFluid() {
        int fluid = this.data.get(4);
        int maxFluid = this.data.get(5);
        int fluidBarSize = 52;
        
        return maxFluid != 0 && fluid != 0 ? fluid * fluidBarSize / maxFluid : 0;
    }
    
    public FluidStack getFluidStack() {
        return fluidStack;
    }
    
    public MachineTier getTier() {
        return tier;
    }
}