package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.TieredPowerGeneratorBlockEntity;
import com.astrolabs.astroexpansion.common.blocks.machines.base.MachineTier;
import com.astrolabs.astroexpansion.common.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class TieredPowerGeneratorMenu extends AbstractContainerMenu {
    private final TieredPowerGeneratorBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    private final MachineTier tier;
    
    public TieredPowerGeneratorMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(5), MachineTier.BASIC);
    }
    
    public TieredPowerGeneratorMenu(int id, Inventory inv, BlockEntity entity, ContainerData data, MachineTier tier) {
        super(ModMenuTypes.TIERED_POWER_GENERATOR.get(), id);
        checkContainerSize(inv, 5);
        this.blockEntity = (TieredPowerGeneratorBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;
        this.tier = tier;
        
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // Fuel slot
            this.addSlot(new SlotItemHandler(handler, 0, 80, 35) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
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
        
        int inventoryStart = 1 + tier.getUpgradeSlots();
        int inventoryEnd = inventoryStart + 36;
        
        // From fuel slot or upgrade slots
        if (index < inventoryStart) {
            if (!this.moveItemStackTo(stack, inventoryStart, inventoryEnd, false)) {
                return ItemStack.EMPTY;
            }
        }
        // From player inventory
        else {
            // Check if it's fuel
            if (ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0) {
                if (!this.moveItemStackTo(stack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            // Check if it's an upgrade
            else if (blockEntity.isValidUpgrade(stack)) {
                if (!this.moveItemStackTo(stack, 1, 1 + tier.getUpgradeSlots(), false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
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
    
    public boolean isBurning() {
        return data.get(0) > 0;
    }
    
    public int getScaledBurnTime() {
        int burnTime = this.data.get(0);
        int maxBurnTime = this.data.get(1);
        int fireSize = 14; // Fire icon size
        
        return maxBurnTime != 0 && burnTime != 0 ? burnTime * fireSize / maxBurnTime : 0;
    }
    
    public int getScaledEnergy() {
        int energy = this.data.get(2);
        int maxEnergy = this.data.get(3);
        int energyBarSize = 52;
        
        return maxEnergy != 0 && energy != 0 ? energy * energyBarSize / maxEnergy : 0;
    }
    
    public int getEnergyGeneration() {
        return this.data.get(4);
    }
    
    public MachineTier getTier() {
        return tier;
    }
}