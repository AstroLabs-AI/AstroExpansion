package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.api.multiblock.IMultiblockPattern;
import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.menu.RocketAssemblyMenu;
import com.astrolabs.astroexpansion.common.multiblock.MultiblockControllerBase;
import com.astrolabs.astroexpansion.common.multiblock.patterns.RocketAssemblyPattern;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import com.astrolabs.astroexpansion.common.registry.ModFluids;
import com.astrolabs.astroexpansion.common.registry.ModItems;
import com.astrolabs.astroexpansion.common.registry.ModSounds;
import com.astrolabs.astroexpansion.common.registry.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RocketAssemblyControllerBlockEntity extends MultiblockControllerBase implements MenuProvider {
    private static final int ENERGY_CAPACITY = 1000000; // 1M FE
    private static final int ENERGY_PER_BUILD = 100000; // 100k FE to build rocket
    private static final int FUEL_CAPACITY = 32000; // 32 buckets
    
    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(ENERGY_CAPACITY, 50000, 0);
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);
    
    private final FluidTank fuelTank = new FluidTank(FUEL_CAPACITY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.ROCKET_FUEL.get();
        }
    };
    
    private final FluidTank oxygenTank = new FluidTank(FUEL_CAPACITY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.LIQUID_OXYGEN.get();
        }
    };
    
    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    
    private final LazyOptional<IItemHandler> itemHandlerOpt = LazyOptional.of(() -> itemHandler);
    
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> new IFluidHandler() {
        @Override
        public int getTanks() {
            return 2;
        }
        
        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return tank == 0 ? fuelTank.getFluid() : oxygenTank.getFluid();
        }
        
        @Override
        public int getTankCapacity(int tank) {
            return FUEL_CAPACITY;
        }
        
        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return tank == 0 ? fuelTank.isFluidValid(stack) : oxygenTank.isFluidValid(stack);
        }
        
        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (resource.getFluid() == ModFluids.ROCKET_FUEL.get()) {
                return fuelTank.fill(resource, action);
            } else if (resource.getFluid() == ModFluids.LIQUID_OXYGEN.get()) {
                return oxygenTank.fill(resource, action);
            }
            return 0;
        }
        
        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            return FluidStack.EMPTY;
        }
        
        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return FluidStack.EMPTY;
        }
    });
    
    private boolean rocketBuilt = false;
    private int buildProgress = 0;
    private static final int BUILD_TIME = 200; // 10 seconds
    
    private final IMultiblockPattern pattern = new RocketAssemblyPattern();
    
    public RocketAssemblyControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROCKET_ASSEMBLY_CONTROLLER.get(), pos, state);
    }
    
    @Override
    public IMultiblockPattern getPattern() {
        return pattern;
    }
    
    @Override
    public void onFormed() {
        // Multiblock successfully formed
    }
    
    @Override
    public void onBroken() {
        rocketBuilt = false;
        buildProgress = 0;
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, RocketAssemblyControllerBlockEntity blockEntity) {
        if (blockEntity.isFormed()) {
            blockEntity.updateAssembly();
        }
    }
    
    private void updateAssembly() {
        if (!rocketBuilt && canBuildRocket()) {
            if (energyStorage.extractEnergy(500, false) == 500) {
                buildProgress++;
                
                if (buildProgress >= BUILD_TIME) {
                    buildRocket();
                    buildProgress = 0;
                }
                setChanged();
            }
        }
    }
    
    private boolean canBuildRocket() {
        // Check for required rocket parts in inventory
        return hasRocketParts() && energyStorage.getEnergyStored() >= ENERGY_PER_BUILD;
    }
    
    private boolean hasRocketParts() {
        // Check for engine, fuel tank, hull, and nose cone
        boolean hasEngine = false;
        boolean hasFuelTank = false;
        boolean hasHull = false;
        boolean hasNoseCone = false;
        
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.getItem() == ModBlocks.ROCKET_ENGINE.get().asItem()) hasEngine = true;
            if (stack.getItem() == ModBlocks.ROCKET_FUEL_TANK.get().asItem()) hasFuelTank = true;
            if (stack.getItem() == ModBlocks.ROCKET_HULL.get().asItem()) hasHull = true;
            if (stack.getItem() == ModBlocks.ROCKET_NOSE_CONE.get().asItem()) hasNoseCone = true;
        }
        
        return hasEngine && hasFuelTank && hasHull && hasNoseCone;
    }
    
    private void buildRocket() {
        // Consume rocket parts
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.getItem() == ModBlocks.ROCKET_ENGINE.get().asItem() ||
                stack.getItem() == ModBlocks.ROCKET_FUEL_TANK.get().asItem() ||
                stack.getItem() == ModBlocks.ROCKET_HULL.get().asItem() ||
                stack.getItem() == ModBlocks.ROCKET_NOSE_CONE.get().asItem()) {
                itemHandler.extractItem(i, 1, false);
            }
        }
        
        // Consume energy
        energyStorage.extractEnergy(ENERGY_PER_BUILD, false);
        
        rocketBuilt = true;
        setChanged();
    }
    
    public boolean canLaunch() {
        return rocketBuilt && 
               fuelTank.getFluidAmount() >= 16000 && // 16 buckets of fuel
               oxygenTank.getFluidAmount() >= 16000; // 16 buckets of oxygen
    }
    
    public void launchRocket() {
        if (canLaunch() && level != null && !level.isClientSide) {
            // Find the player who triggered the launch (within 10 blocks)
            BlockPos pos = getBlockPos();
            level.getEntitiesOfClass(net.minecraft.world.entity.player.Player.class, 
                new net.minecraft.world.phys.AABB(pos).inflate(10))
                .stream()
                .findFirst()
                .ifPresent(player -> {
                    // Consume fuel
                    fuelTank.drain(16000, IFluidHandler.FluidAction.EXECUTE);
                    oxygenTank.drain(16000, IFluidHandler.FluidAction.EXECUTE);
                    
                    // Reset rocket
                    rocketBuilt = false;
                    
                    // Launch sequence
                    if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                        // Play launch sound
                        level.playSound(null, pos, ModSounds.ROCKET_LAUNCH.get(), 
                            net.minecraft.sounds.SoundSource.BLOCKS, 2.0F, 1.0F);
                        
                        // Create particle effects
                        for (int i = 0; i < 100; i++) {
                            double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 3;
                            double y = pos.getY() + level.random.nextDouble() * 2;
                            double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 3;
                            ((net.minecraft.server.level.ServerLevel)level).sendParticles(
                                net.minecraft.core.particles.ParticleTypes.FLAME,
                                x, y, z, 1, 0, 0.1, 0, 0.1);
                        }
                        
                        // Teleport to space dimension
                        net.minecraft.server.level.ServerLevel spaceDim = serverPlayer.server.getLevel(ModDimensions.SPACE_KEY);
                        if (spaceDim != null) {
                            // Give player space suit if not wearing one
                            ensurePlayerHasSpaceSuit(serverPlayer);
                            
                            // Teleport using custom teleporter
                            serverPlayer.changeDimension(spaceDim, new com.astrolabs.astroexpansion.common.world.dimension.SpaceTeleporter(true));
                            
                            // Send success message
                            serverPlayer.sendSystemMessage(
                                net.minecraft.network.chat.Component.literal("Launching to space station!")
                                    .withStyle(net.minecraft.ChatFormatting.GREEN));
                        }
                    }
                    
                    setChanged();
                });
        }
    }
    
    private void ensurePlayerHasSpaceSuit(net.minecraft.server.level.ServerPlayer player) {
        // Check if player is wearing space suit
        boolean hasHelmet = player.getInventory().armor.get(3).is(ModItems.SPACE_HELMET.get());
        boolean hasChestplate = player.getInventory().armor.get(2).is(ModItems.SPACE_CHESTPLATE.get());
        boolean hasLeggings = player.getInventory().armor.get(1).is(ModItems.SPACE_LEGGINGS.get());
        boolean hasBoots = player.getInventory().armor.get(0).is(ModItems.SPACE_BOOTS.get());
        
        if (!hasHelmet || !hasChestplate || !hasLeggings || !hasBoots) {
            // Give warning
            player.sendSystemMessage(
                net.minecraft.network.chat.Component.literal("Warning: Space suit equipped automatically!")
                    .withStyle(net.minecraft.ChatFormatting.YELLOW));
            
            // Auto-equip space suit
            if (!hasHelmet) player.getInventory().armor.set(3, new net.minecraft.world.item.ItemStack(ModItems.SPACE_HELMET.get()));
            if (!hasChestplate) player.getInventory().armor.set(2, new net.minecraft.world.item.ItemStack(ModItems.SPACE_CHESTPLATE.get()));
            if (!hasLeggings) player.getInventory().armor.set(1, new net.minecraft.world.item.ItemStack(ModItems.SPACE_LEGGINGS.get()));
            if (!hasBoots) player.getInventory().armor.set(0, new net.minecraft.world.item.ItemStack(ModItems.SPACE_BOOTS.get()));
        }
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerOpt.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyHandler.invalidate();
        itemHandlerOpt.invalidate();
        fluidHandler.invalidate();
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energyStorage.deserializeNBT(tag.get("energy"));
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        fuelTank.readFromNBT(tag.getCompound("fuel"));
        oxygenTank.readFromNBT(tag.getCompound("oxygen"));
        rocketBuilt = tag.getBoolean("rocketBuilt");
        buildProgress = tag.getInt("buildProgress");
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("energy", energyStorage.serializeNBT());
        tag.put("inventory", itemHandler.serializeNBT());
        tag.put("fuel", fuelTank.writeToNBT(new CompoundTag()));
        tag.put("oxygen", oxygenTank.writeToNBT(new CompoundTag()));
        tag.putBoolean("rocketBuilt", rocketBuilt);
        tag.putInt("buildProgress", buildProgress);
    }
    
    @Override
    public Component getDisplayName() {
        return Component.literal("Rocket Assembly");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new RocketAssemblyMenu(id, inventory, this);
    }
    
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }
    
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }
    
    public boolean isRocketBuilt() {
        return rocketBuilt;
    }
    
    public int getBuildProgress() {
        return buildProgress;
    }
    
    public int getMaxBuildProgress() {
        return BUILD_TIME;
    }
    
    public FluidStack getFuel() {
        return fuelTank.getFluid();
    }
    
    public FluidStack getOxygen() {
        return oxygenTank.getFluid();
    }
    
    public int getFluidCapacity() {
        return FUEL_CAPACITY;
    }
    
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
}