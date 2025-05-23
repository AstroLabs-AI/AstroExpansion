package com.astrolabs.astroexpansion.common.menu;

import com.astrolabs.astroexpansion.common.blockentities.FusionReactorControllerBlockEntity;
import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import com.astrolabs.astroexpansion.common.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;

public class FusionReactorMenu extends AbstractContainerMenu {
    private final FusionReactorControllerBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;
    
    // Data slots for syncing
    private final DataSlot energyStored = DataSlot.standalone();
    private final DataSlot energyCapacity = DataSlot.standalone();
    private final DataSlot temperature = DataSlot.standalone();
    private final DataSlot maxTemperature = DataSlot.standalone();
    private final DataSlot running = DataSlot.standalone();
    private final DataSlot deuteriumAmount = DataSlot.standalone();
    private final DataSlot tritiumAmount = DataSlot.standalone();
    private final DataSlot fluidCapacity = DataSlot.standalone();
    
    public FusionReactorMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }
    
    public FusionReactorMenu(int id, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.FUSION_REACTOR.get(), id);
        
        if (entity instanceof FusionReactorControllerBlockEntity be) {
            this.blockEntity = be;
        } else {
            throw new IllegalStateException("Invalid block entity type");
        }
        
        this.levelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        
        // Add data slots
        addDataSlot(energyStored);
        addDataSlot(energyCapacity);
        addDataSlot(temperature);
        addDataSlot(maxTemperature);
        addDataSlot(running);
        addDataSlot(deuteriumAmount);
        addDataSlot(tritiumAmount);
        addDataSlot(fluidCapacity);
        
        // Add player inventory slots
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        
        // Add player hotbar slots
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }
    }
    
    @Override
    public boolean stillValid(Player player) {
        return stillValid(levelAccess, player, ModBlocks.FUSION_REACTOR_CONTROLLER.get());
    }
    
    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        
        energyStored.set(blockEntity.getEnergyStored());
        energyCapacity.set(blockEntity.getMaxEnergyStored());
        temperature.set(blockEntity.getTemperature());
        maxTemperature.set(blockEntity.getMaxTemperature());
        running.set(blockEntity.isRunning() ? 1 : 0);
        deuteriumAmount.set(blockEntity.getDeuterium().getAmount());
        tritiumAmount.set(blockEntity.getTritium().getAmount());
        fluidCapacity.set(blockEntity.getFluidCapacity());
    }
    
    public int getEnergyStored() {
        return energyStored.get();
    }
    
    public int getEnergyCapacity() {
        return energyCapacity.get();
    }
    
    public int getTemperature() {
        return temperature.get();
    }
    
    public int getMaxTemperature() {
        return maxTemperature.get();
    }
    
    public boolean isRunning() {
        return running.get() == 1;
    }
    
    public int getDeuteriumAmount() {
        return deuteriumAmount.get();
    }
    
    public int getTritiumAmount() {
        return tritiumAmount.get();
    }
    
    public int getFluidCapacity() {
        return fluidCapacity.get();
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}