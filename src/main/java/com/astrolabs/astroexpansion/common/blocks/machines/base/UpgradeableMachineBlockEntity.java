package com.astrolabs.astroexpansion.common.blocks.machines.base;

import com.astrolabs.astroexpansion.common.items.upgrades.UpgradeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class UpgradeableMachineBlockEntity extends BlockEntity implements IUpgradeable {
    protected static final int DEFAULT_UPGRADE_SLOTS = 4;
    
    protected final ItemStackHandler upgradeHandler;
    protected float cachedSpeedMultiplier = 1.0f;
    protected float cachedEfficiencyMultiplier = 1.0f;
    protected float cachedFortuneMultiplier = 1.0f;
    protected final int maxUpgradeSlots;
    
    public UpgradeableMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, DEFAULT_UPGRADE_SLOTS);
    }
    
    public UpgradeableMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int maxUpgradeSlots) {
        super(type, pos, state);
        this.maxUpgradeSlots = maxUpgradeSlots;
        this.upgradeHandler = new UpgradeItemStackHandler(maxUpgradeSlots);
    }
    
    // Named inner class instead of anonymous class to avoid class loading issues
    public class UpgradeItemStackHandler extends ItemStackHandler {
        public UpgradeItemStackHandler(int size) {
            super(size);
        }
        
        @Override
        protected void onContentsChanged(int slot) {
            UpgradeableMachineBlockEntity.this.onUpgradesChanged();
            UpgradeableMachineBlockEntity.this.setChanged();
        }
        
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return UpgradeableMachineBlockEntity.this.isValidUpgrade(stack);
        }
        
        @Override
        public int getSlotLimit(int slot) {
            return 1; // Only one upgrade per slot
        }
    }
    
    @Override
    public IItemHandler getUpgradeInventory() {
        return upgradeHandler;
    }
    
    @Override
    public int getMaxUpgradeSlots() {
        return maxUpgradeSlots;
    }
    
    @Override
    public boolean isValidUpgrade(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof UpgradeItem;
    }
    
    @Override
    public List<UpgradeItem> getInstalledUpgrades() {
        List<UpgradeItem> upgrades = new ArrayList<>();
        for (int i = 0; i < upgradeHandler.getSlots(); i++) {
            ItemStack stack = upgradeHandler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof UpgradeItem) {
                upgrades.add((UpgradeItem) stack.getItem());
            }
        }
        return upgrades;
    }
    
    @Override
    public float getSpeedMultiplier() {
        return cachedSpeedMultiplier;
    }
    
    @Override
    public float getEfficiencyMultiplier() {
        return cachedEfficiencyMultiplier;
    }
    
    public float getEnergyEfficiency() {
        return cachedEfficiencyMultiplier;
    }
    
    @Override
    public float getFortuneMultiplier() {
        return cachedFortuneMultiplier;
    }
    
    @Override
    public void onUpgradesChanged() {
        recalculateUpgradeModifiers();
    }
    
    protected void recalculateUpgradeModifiers() {
        float speedMult = 1.0f;
        float efficiencyMult = 1.0f;
        float fortuneMult = 1.0f;
        
        for (UpgradeItem upgrade : getInstalledUpgrades()) {
            switch (upgrade.getType()) {
                case SPEED -> speedMult *= upgrade.getModifier();
                case EFFICIENCY -> efficiencyMult *= upgrade.getModifier();
                case FORTUNE -> fortuneMult *= upgrade.getModifier();
            }
        }
        
        cachedSpeedMultiplier = speedMult;
        cachedEfficiencyMultiplier = efficiencyMult;
        cachedFortuneMultiplier = fortuneMult;
    }
    
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("upgrades", upgradeHandler.serializeNBT());
    }
    
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("upgrades")) {
            upgradeHandler.deserializeNBT(nbt.getCompound("upgrades"));
            recalculateUpgradeModifiers();
        }
    }
    
    public void dropUpgrades() {
        if (level != null && !level.isClientSide()) {
            for (int i = 0; i < upgradeHandler.getSlots(); i++) {
                ItemStack upgrade = upgradeHandler.getStackInSlot(i);
                if (!upgrade.isEmpty()) {
                    net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), upgrade);
                }
            }
        }
    }
}