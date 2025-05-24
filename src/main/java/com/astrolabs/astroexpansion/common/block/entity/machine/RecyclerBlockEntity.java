package com.astrolabs.astroexpansion.common.block.entity.machine;

import com.astrolabs.astroexpansion.api.capability.AstroEnergyStorage;
import com.astrolabs.astroexpansion.common.block.machine.RecyclerBlock;
import com.astrolabs.astroexpansion.common.menu.RecyclerMenu;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import com.astrolabs.astroexpansion.common.registry.ModItems;
import com.astrolabs.astroexpansion.common.items.upgrades.UpgradeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RecyclerBlockEntity extends BlockEntity implements MenuProvider {
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT_1 = 1;
    private static final int OUTPUT_SLOT_2 = 2;
    private static final int OUTPUT_SLOT_3 = 3;
    private static final int UPGRADE_SLOT_START = 4;
    private static final int UPGRADE_SLOT_COUNT = 3;
    
    private static final int BASE_ENERGY_PER_TICK = 20;
    private static final int BASE_PROCESS_TIME = 100;
    private static final double BASE_SCRAP_CHANCE = 0.15;
    private static final double BASE_MATERIAL_CHANCE = 0.10;
    private static final int ENERGY_CAPACITY = 10000;

    private final ItemStackHandler inputHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return true; // Accept any item
        }
    };

    private final ItemStackHandler outputHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return false; // Output only
        }
    };

    private final ItemStackHandler upgradeHandler = new ItemStackHandler(UPGRADE_SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            recalculateUpgrades();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.getItem() instanceof UpgradeItem;
        }
    };

    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(ENERGY_CAPACITY, 100, 0);
    private final LazyOptional<IItemHandler> inputHandlerLazy = LazyOptional.of(() -> inputHandler);
    private final LazyOptional<IItemHandler> outputHandlerLazy = LazyOptional.of(() -> outputHandler);
    private final LazyOptional<IItemHandler> combinedHandlerLazy = LazyOptional.of(() -> new CombinedInvWrapper(inputHandler, outputHandler));
    private final LazyOptional<AstroEnergyStorage> energyHandlerLazy = LazyOptional.of(() -> energyStorage);

    private int processTime = 0;
    private int maxProcessTime = BASE_PROCESS_TIME;
    private int energyPerTick = BASE_ENERGY_PER_TICK;
    private double scrapChance = BASE_SCRAP_CHANCE;
    private double materialChance = BASE_MATERIAL_CHANCE;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> processTime;
                case 1 -> maxProcessTime;
                case 2 -> energyStorage.getEnergyStored();
                case 3 -> energyStorage.getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> processTime = value;
                case 1 -> maxProcessTime = value;
                case 2 -> energyStorage.setEnergy(value);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    private final Random random = new Random();
    private final Map<UpgradeItem.UpgradeType, Integer> upgradeLevels = new HashMap<>();

    public RecyclerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RECYCLER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, RecyclerBlockEntity entity) {
        boolean wasProcessing = entity.processTime > 0;
        
        if (entity.canProcess()) {
            if (entity.energyStorage.getEnergyStored() >= entity.energyPerTick) {
                entity.energyStorage.extractEnergy(entity.energyPerTick, false);
                entity.processTime++;

                if (entity.processTime >= entity.maxProcessTime) {
                    entity.processItem();
                    entity.processTime = 0;
                }
            }
        } else {
            entity.processTime = 0;
        }

        boolean isProcessing = entity.processTime > 0;
        if (wasProcessing != isProcessing) {
            state = state.setValue(RecyclerBlock.POWERED, isProcessing);
            level.setBlock(pos, state, 3);
        }
    }

    private boolean canProcess() {
        ItemStack input = inputHandler.getStackInSlot(0);
        if (input.isEmpty()) {
            return false;
        }

        // Check if output slots have space
        return hasOutputSpace();
    }

    private boolean hasOutputSpace() {
        for (int i = 0; i < outputHandler.getSlots(); i++) {
            ItemStack outputStack = outputHandler.getStackInSlot(i);
            if (outputStack.isEmpty() || (outputStack.getCount() < outputStack.getMaxStackSize())) {
                return true;
            }
        }
        return false;
    }

    private void processItem() {
        ItemStack input = inputHandler.extractItem(0, 1, false);
        if (!input.isEmpty()) {
            // Generate outputs based on chances
            double scrapRoll = random.nextDouble();
            double materialRoll = random.nextDouble();

            // Scrap generation
            if (scrapRoll < scrapChance) {
                ItemStack scrap = new ItemStack(ModItems.SCRAP.get(), 1 + random.nextInt(2));
                insertIntoOutput(scrap);
            }

            // Material recovery
            if (materialRoll < materialChance) {
                ItemStack recovered = getRecoveredMaterials(input);
                if (!recovered.isEmpty()) {
                    insertIntoOutput(recovered);
                }
            }

            // Play recycling sound
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, worldPosition, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.5F, 1.0F);
            }
        }
    }

    private ItemStack getRecoveredMaterials(ItemStack input) {
        Item item = input.getItem();
        
        // Tools to ingots
        if (item instanceof TieredItem tieredItem) {
            Tier tier = tieredItem.getTier();
            if (tier == Tiers.IRON) {
                return new ItemStack(Items.IRON_NUGGET, 1 + random.nextInt(3));
            } else if (tier == Tiers.GOLD) {
                return new ItemStack(Items.GOLD_NUGGET, 1 + random.nextInt(2));
            } else if (tier == Tiers.DIAMOND) {
                return new ItemStack(Items.DIAMOND, random.nextDouble() < 0.3 ? 1 : 0);
            }
        }

        // Armor to materials
        if (item instanceof ArmorItem armorItem) {
            ArmorMaterial material = armorItem.getMaterial();
            if (material == ArmorMaterials.IRON) {
                return new ItemStack(Items.IRON_NUGGET, 2 + random.nextInt(3));
            } else if (material == ArmorMaterials.GOLD) {
                return new ItemStack(Items.GOLD_NUGGET, 2 + random.nextInt(2));
            } else if (material == ArmorMaterials.DIAMOND) {
                return new ItemStack(Items.DIAMOND, random.nextDouble() < 0.4 ? 1 : 0);
            } else if (material == ArmorMaterials.LEATHER) {
                return new ItemStack(Items.LEATHER, 1);
            }
        }

        // Blocks to items
        if (item == Items.IRON_BLOCK) {
            return new ItemStack(Items.IRON_INGOT, 2 + random.nextInt(4));
        } else if (item == Items.GOLD_BLOCK) {
            return new ItemStack(Items.GOLD_INGOT, 2 + random.nextInt(3));
        } else if (item == Items.DIAMOND_BLOCK) {
            return new ItemStack(Items.DIAMOND, 1 + random.nextInt(3));
        }

        // Mod items
        if (item == ModItems.TITANIUM_INGOT.get()) {
            return new ItemStack(ModItems.TITANIUM_DUST.get(), 1);
        } else if (item == ModItems.LITHIUM_INGOT.get()) {
            return new ItemStack(ModItems.LITHIUM_DUST.get(), 1);
        }

        // Default: small chance for generic materials
        if (random.nextDouble() < 0.1) {
            return new ItemStack(Items.IRON_NUGGET, 1);
        }

        return ItemStack.EMPTY;
    }

    private void insertIntoOutput(ItemStack stack) {
        for (int i = 0; i < outputHandler.getSlots(); i++) {
            stack = outputHandler.insertItem(i, stack, false);
            if (stack.isEmpty()) {
                break;
            }
        }
    }

    private void recalculateUpgrades() {
        upgradeLevels.clear();
        
        for (int i = 0; i < upgradeHandler.getSlots(); i++) {
            ItemStack upgradeStack = upgradeHandler.getStackInSlot(i);
            if (upgradeStack.getItem() instanceof UpgradeItem upgradeItem) {
                UpgradeItem.UpgradeType type = upgradeItem.getType();
                upgradeLevels.merge(type, upgradeItem.getTier(), Integer::sum);
            }
        }

        // Apply upgrades
        int speedLevel = upgradeLevels.getOrDefault(UpgradeItem.UpgradeType.SPEED, 0);
        int efficiencyLevel = upgradeLevels.getOrDefault(UpgradeItem.UpgradeType.EFFICIENCY, 0);
        int fortuneLevel = upgradeLevels.getOrDefault(UpgradeItem.UpgradeType.FORTUNE, 0);

        // Speed upgrade reduces process time
        maxProcessTime = Math.max(20, BASE_PROCESS_TIME - (speedLevel * 15));
        
        // Efficiency upgrade reduces energy consumption
        energyPerTick = Math.max(5, BASE_ENERGY_PER_TICK - (efficiencyLevel * 3));
        
        // Fortune upgrade increases output chances
        scrapChance = Math.min(0.5, BASE_SCRAP_CHANCE + (fortuneLevel * 0.05));
        materialChance = Math.min(0.4, BASE_MATERIAL_CHANCE + (fortuneLevel * 0.05));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.astroexpansion.recycler");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new RecyclerMenu(id, playerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("input", inputHandler.serializeNBT());
        tag.put("output", outputHandler.serializeNBT());
        tag.put("upgrades", upgradeHandler.serializeNBT());
        tag.put("energy", energyStorage.serializeNBT());
        tag.putInt("processTime", processTime);
        tag.putInt("maxProcessTime", maxProcessTime);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        inputHandler.deserializeNBT(tag.getCompound("input"));
        outputHandler.deserializeNBT(tag.getCompound("output"));
        upgradeHandler.deserializeNBT(tag.getCompound("upgrades"));
        energyStorage.deserializeNBT(tag.getCompound("energy"));
        processTime = tag.getInt("processTime");
        maxProcessTime = tag.getInt("maxProcessTime");
        recalculateUpgrades();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        inputHandlerLazy.invalidate();
        outputHandlerLazy.invalidate();
        combinedHandlerLazy.invalidate();
        energyHandlerLazy.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == Direction.UP) {
                return inputHandlerLazy.cast();
            } else if (side == Direction.DOWN) {
                return outputHandlerLazy.cast();
            } else {
                return combinedHandlerLazy.cast();
            }
        } else if (cap == ForgeCapabilities.ENERGY) {
            return energyHandlerLazy.cast();
        }
        return super.getCapability(cap, side);
    }

    public void dropContents() {
        SimpleContainer inventory = new SimpleContainer(inputHandler.getSlots() + outputHandler.getSlots() + upgradeHandler.getSlots());
        int index = 0;
        
        for (int i = 0; i < inputHandler.getSlots(); i++) {
            inventory.setItem(index++, inputHandler.getStackInSlot(i));
        }
        
        for (int i = 0; i < outputHandler.getSlots(); i++) {
            inventory.setItem(index++, outputHandler.getStackInSlot(i));
        }
        
        for (int i = 0; i < upgradeHandler.getSlots(); i++) {
            inventory.setItem(index++, upgradeHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public ItemStackHandler getInputHandler() {
        return inputHandler;
    }

    public ItemStackHandler getOutputHandler() {
        return outputHandler;
    }

    public ItemStackHandler getUpgradeHandler() {
        return upgradeHandler;
    }

    public AstroEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

}