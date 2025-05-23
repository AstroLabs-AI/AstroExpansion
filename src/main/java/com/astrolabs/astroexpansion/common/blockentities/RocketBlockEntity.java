package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.menu.RocketMenu;
import com.astrolabs.astroexpansion.common.registry.AEBlockEntities;
import com.astrolabs.astroexpansion.common.registry.AEItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class RocketBlockEntity extends AbstractMachineBlockEntity implements MenuProvider, Container {
    private static final int FUEL_CAPACITY = 16000;
    private static final int OXYGEN_CAPACITY = 8000;
    private static final int INVENTORY_SIZE = 27; // 3x9 cargo space
    
    private final FluidTank fuelTank = new FluidTank(FUEL_CAPACITY);
    private final FluidTank oxygenTank = new FluidTank(OXYGEN_CAPACITY);
    private final SimpleContainer inventory = new SimpleContainer(INVENTORY_SIZE);
    
    private int tier = 1; // 1-3, determines max height/destinations
    private boolean isReady = false;
    
    public RocketBlockEntity(BlockPos pos, BlockState blockState) {
        super(AEBlockEntities.ROCKET.get(), pos, blockState, 0); // Rockets don't use energy directly
    }
    
    @Override
    protected void applyImplicitComponents(DataComponentMap.Builder builder) {
        super.applyImplicitComponents(builder);
    }
    
    @Override
    protected void collectImplicitComponents(DataComponentMap components) {
        super.collectImplicitComponents(components);
    }
    
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        fuelTank.readFromNBT(registries, tag.getCompound("FuelTank"));
        oxygenTank.readFromNBT(registries, tag.getCompound("OxygenTank"));
        inventory.fromTag(tag.getList("Inventory", 10), registries);
        tier = tag.getInt("Tier");
        isReady = tag.getBoolean("IsReady");
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("FuelTank", fuelTank.writeToNBT(registries, new CompoundTag()));
        tag.put("OxygenTank", oxygenTank.writeToNBT(registries, new CompoundTag()));
        tag.put("Inventory", inventory.createTag(registries));
        tag.putInt("Tier", tier);
        tag.putBoolean("IsReady", isReady);
    }
    
    @Override
    public void tick() {
        // Rockets don't have continuous tick operations
    }
    
    public boolean canLaunch() {
        return fuelTank.getFluidAmount() >= getRequiredFuel() &&
               oxygenTank.getFluidAmount() >= getRequiredOxygen() &&
               hasRequiredComponents();
    }
    
    private int getRequiredFuel() {
        return switch (tier) {
            case 1 -> 4000;
            case 2 -> 8000;
            case 3 -> 12000;
            default -> 16000;
        };
    }
    
    private int getRequiredOxygen() {
        return switch (tier) {
            case 1 -> 2000;
            case 2 -> 4000;
            case 3 -> 6000;
            default -> 8000;
        };
    }
    
    private boolean hasRequiredComponents() {
        int processorsNeeded = tier;
        int energyCoresNeeded = tier - 1;
        
        int processorCount = 0;
        int energyCoreCount = 0;
        
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(AEItems.ADVANCED_PROCESSOR.get())) {
                processorCount += stack.getCount();
            } else if (stack.is(AEItems.ENERGY_CORE.get())) {
                energyCoreCount += stack.getCount();
            }
        }
        
        return processorCount >= processorsNeeded && energyCoreCount >= energyCoresNeeded;
    }
    
    public void launch(ServerPlayer player) {
        if (canLaunch()) {
            fuelTank.drain(getRequiredFuel(), IFluidHandler.FluidAction.EXECUTE);
            oxygenTank.drain(getRequiredOxygen(), IFluidHandler.FluidAction.EXECUTE);
            
            player.displayClientMessage(Component.literal("Launching rocket to space!"), true);
            
            setChanged();
        }
    }
    
    public int getTier() {
        return tier;
    }
    
    public void setTier(int tier) {
        this.tier = Math.min(3, Math.max(1, tier));
        setChanged();
    }
    
    public int getFuelAmount() {
        return fuelTank.getFluidAmount();
    }
    
    public int getMaxFuel() {
        return fuelTank.getCapacity();
    }
    
    public int getOxygenAmount() {
        return oxygenTank.getFluidAmount();
    }
    
    public int getMaxOxygen() {
        return oxygenTank.getCapacity();
    }
    
    @Override
    public Component getDisplayName() {
        return Component.literal("Rocket Tier " + tier);
    }
    
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new RocketMenu(id, playerInventory, this);
    }
    
    public void openMenu(ServerPlayer player) {
        player.openMenu(this, pos -> {
            pos.writeBlockPos(worldPosition);
        });
    }
    
    @Override
    public int getContainerSize() {
        return inventory.getContainerSize();
    }
    
    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }
    
    @Override
    public ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }
    
    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = inventory.removeItem(slot, amount);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }
    
    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return inventory.removeItemNoUpdate(slot);
    }
    
    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.setItem(slot, stack);
        setChanged();
    }
    
    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }
    
    @Override
    public void clearContent() {
        inventory.clearContent();
        setChanged();
    }
}