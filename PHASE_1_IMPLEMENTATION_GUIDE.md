# Phase 1 Implementation Guide - Minecraft Forge 1.20.1

## 🎯 Phase 1 Goals
Establish the core foundation with basic ores, energy system, and first machines.

## 📁 Initial Project Structure

### 1. Update build.gradle
```gradle
plugins {
    id 'eclipse'
    id 'maven-publish'
    id 'net.minecraftforge.gradle' version '6.0.+'
}

version = '1.0.0'
group = 'com.astrolabs.astroexpansion'
archivesBaseName = 'astroexpansion'

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

minecraft {
    mappings channel: 'official', version: '1.20.1'
    
    runs {
        client {
            workingDirectory project.file('run')
            mods {
                astroexpansion {
                    source sourceSets.main
                }
            }
        }
        
        server {
            workingDirectory project.file('run')
            mods {
                astroexpansion {
                    source sourceSets.main
                }
            }
        }
        
        data {
            workingDirectory project.file('run')
            args '--mod', 'astroexpansion', '--all', '--output', file('src/generated/resources/'), '--existing', file('src/main/resources/')
            mods {
                astroexpansion {
                    source sourceSets.main
                }
            }
        }
    }
}

dependencies {
    minecraft 'net.minecraftforge:forge:1.20.1-47.2.0'
    
    // JEI for recipe viewing
    compileOnly fg.deobf("mezz.jei:jei-1.20.1-forge-api:15.2.0.27")
    runtimeOnly fg.deobf("mezz.jei:jei-1.20.1-forge:15.2.0.27")
}
```

### 2. Main Mod Class
```java
package com.astrolabs.astroexpansion;

import com.astrolabs.astroexpansion.common.registry.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AstroExpansion.MODID)
public class AstroExpansion {
    public static final String MODID = "astroexpansion";
    
    public AstroExpansion() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register all deferred registers
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        
        // Register mod event listeners
        modEventBus.addListener(this::commonSetup);
        
        // Register forge event bus
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        // Initialize capabilities, packets, etc.
    }
}
```

### 3. Basic Items Registry
```java
package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.*;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, AstroExpansion.MODID);
    
    // Raw Ores
    public static final RegistryObject<Item> RAW_TITANIUM = ITEMS.register("raw_titanium",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_LITHIUM = ITEMS.register("raw_lithium",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_URANIUM = ITEMS.register("raw_uranium",
        () -> new Item(new Item.Properties()));
    
    // Ingots
    public static final RegistryObject<Item> TITANIUM_INGOT = ITEMS.register("titanium_ingot",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LITHIUM_INGOT = ITEMS.register("lithium_ingot",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> URANIUM_INGOT = ITEMS.register("uranium_ingot",
        () -> new Item(new Item.Properties()));
    
    // Dusts
    public static final RegistryObject<Item> TITANIUM_DUST = ITEMS.register("titanium_dust",
        () -> new Item(new Item.Properties()));
    
    // Components
    public static final RegistryObject<Item> CIRCUIT_BOARD = ITEMS.register("circuit_board",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROCESSOR = ITEMS.register("processor",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ENERGY_CORE = ITEMS.register("energy_core",
        () -> new Item(new Item.Properties()));
}
```

### 4. Blocks Registry
```java
package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.blocks.*;
import net.minecraft.world.level.block.*;
import net.minecraftforge.registries.*;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = 
        DeferredRegister.create(ForgeRegistries.BLOCKS, AstroExpansion.MODID);
    
    // Ores
    public static final RegistryObject<Block> TITANIUM_ORE = registerBlock("titanium_ore",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
    public static final RegistryObject<Block> DEEPSLATE_TITANIUM_ORE = registerBlock("deepslate_titanium_ore",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)));
    public static final RegistryObject<Block> LITHIUM_ORE = registerBlock("lithium_ore",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
    public static final RegistryObject<Block> URANIUM_ORE = registerBlock("uranium_ore",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_ORE)));
    
    // Storage Blocks
    public static final RegistryObject<Block> TITANIUM_BLOCK = registerBlock("titanium_block",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    
    // Machines
    public static final RegistryObject<Block> BASIC_GENERATOR = registerBlock("basic_generator",
        () -> new BasicGeneratorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_PROCESSOR = registerBlock("material_processor",
        () -> new MaterialProcessorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> ENERGY_CONDUIT = BLOCKS.register("energy_conduit",
        () -> new EnergyConduitBlock(BlockBehaviour.Properties.copy(Blocks.GLASS)));
    
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }
    
    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
```

### 5. Energy Storage Implementation
```java
package com.astrolabs.astroexpansion.common.capabilities;

import net.minecraftforge.energy.EnergyStorage;

public class AstroEnergyStorage extends EnergyStorage {
    public AstroEnergyStorage(int capacity) {
        super(capacity);
    }
    
    public AstroEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }
    
    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(energy, capacity));
    }
    
    public void addEnergy(int energy) {
        this.energy = Math.min(this.energy + energy, capacity);
    }
    
    public void consumeEnergy(int energy) {
        this.energy = Math.max(0, this.energy - energy);
    }
}
```

### 6. Basic Generator Block Entity
```java
package com.astrolabs.astroexpansion.common.blockentities;

import com.astrolabs.astroexpansion.common.capabilities.AstroEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

public class BasicGeneratorBlockEntity extends BlockEntity {
    private final ItemStackHandler itemHandler = new ItemStackHandler(1);
    private final AstroEnergyStorage energyStorage = new AstroEnergyStorage(10000, 0, 100);
    private final LazyOptional<ItemStackHandler> itemHandlerOpt = LazyOptional.of(() -> itemHandler);
    private final LazyOptional<AstroEnergyStorage> energyStorageOpt = LazyOptional.of(() -> energyStorage);
    
    private int burnTime = 0;
    private int maxBurnTime = 0;
    private static final int ENERGY_PER_TICK = 40;
    
    public BasicGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASIC_GENERATOR.get(), pos, state);
    }
    
    public void tick() {
        if (level == null || level.isClientSide) return;
        
        boolean wasGenerating = burnTime > 0;
        
        if (burnTime > 0) {
            burnTime--;
            energyStorage.addEnergy(ENERGY_PER_TICK);
            setChanged();
        }
        
        if (burnTime == 0) {
            ItemStack fuel = itemHandler.getStackInSlot(0);
            int fuelValue = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);
            
            if (fuelValue > 0 && energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
                burnTime = maxBurnTime = fuelValue;
                fuel.shrink(1);
                setChanged();
            }
        }
        
        if (wasGenerating != (burnTime > 0)) {
            level.setBlock(worldPosition, state.setValue(BasicGeneratorBlock.LIT, burnTime > 0), 3);
        }
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("Items"));
        energyStorage.deserializeNBT(tag.get("Energy"));
        burnTime = tag.getInt("BurnTime");
        maxBurnTime = tag.getInt("MaxBurnTime");
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Items", itemHandler.serializeNBT());
        tag.put("Energy", energyStorage.serializeNBT());
        tag.putInt("BurnTime", burnTime);
        tag.putInt("MaxBurnTime", maxBurnTime);
    }
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerOpt.cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return energyStorageOpt.cast();
        }
        return super.getCapability(cap, side);
    }
}
```

### 7. Material Processor Recipe
```java
package com.astrolabs.astroexpansion.common.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

public class ProcessingRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient input;
    private final ItemStack output;
    private final int processTime;
    private final int energyCost;
    
    public ProcessingRecipe(ResourceLocation id, Ingredient input, ItemStack output, int processTime, int energyCost) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.processTime = processTime;
        this.energyCost = energyCost;
    }
    
    @Override
    public boolean matches(Container container, Level level) {
        return input.test(container.getItem(0));
    }
    
    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return output.copy();
    }
    
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output;
    }
    
    public Ingredient getInput() { return input; }
    public int getProcessTime() { return processTime; }
    public int getEnergyCost() { return energyCost; }
    
    public static class Serializer implements RecipeSerializer<ProcessingRecipe> {
        @Override
        public ProcessingRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.get("input"));
            ItemStack output = ShapedRecipe.itemStackFromJson(JsonUtils.getAsJsonObject(json, "output"));
            int processTime = JsonUtils.getAsInt(json, "process_time", 200);
            int energyCost = JsonUtils.getAsInt(json, "energy_cost", 1000);
            
            return new ProcessingRecipe(id, input, output, processTime, energyCost);
        }
        
        @Override
        public ProcessingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            ItemStack output = buffer.readItem();
            int processTime = buffer.readVarInt();
            int energyCost = buffer.readVarInt();
            
            return new ProcessingRecipe(id, input, output, processTime, energyCost);
        }
        
        @Override
        public void toNetwork(FriendlyByteBuf buffer, ProcessingRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeItem(recipe.output);
            buffer.writeVarInt(recipe.processTime);
            buffer.writeVarInt(recipe.energyCost);
        }
    }
}
```

### 8. Creative Tab
```java
package com.astrolabs.astroexpansion.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AstroExpansion.MODID);
    
    public static final RegistryObject<CreativeModeTab> ASTRO_TAB = CREATIVE_MODE_TABS.register("astro_tab",
        () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.TITANIUM_INGOT.get()))
            .title(Component.translatable("creativetab.astroexpansion"))
            .displayItems((params, output) -> {
                // Add all mod items
                ModItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
            })
            .build());
}
```

## 📂 Data Generation

### 1. BlockState Provider
```java
package com.astrolabs.astroexpansion.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, AstroExpansion.MODID, existingFileHelper);
    }
    
    @Override
    protected void registerStatesAndModels() {
        // Simple blocks
        simpleBlock(ModBlocks.TITANIUM_ORE.get());
        simpleBlock(ModBlocks.TITANIUM_BLOCK.get());
        
        // Machine blocks with directional states
        horizontalBlock(ModBlocks.BASIC_GENERATOR.get(),
            models().cube("basic_generator",
                modLoc("block/machine_bottom"),
                modLoc("block/machine_top"),
                modLoc("block/basic_generator_front"),
                modLoc("block/machine_side"),
                modLoc("block/machine_side"),
                modLoc("block/machine_side"))
                .texture("particle", modLoc("block/machine_side")));
    }
}
```

### 2. Recipe Provider
```java
package com.astrolabs.astroexpansion.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output) {
        super(output);
    }
    
    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        // Smelting recipes
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModItems.RAW_TITANIUM.get()),
                RecipeCategory.MISC, ModItems.TITANIUM_INGOT.get(), 0.7f, 200)
                .unlockedBy("has_raw_titanium", has(ModItems.RAW_TITANIUM.get()))
                .save(consumer);
        
        // Crafting recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.TITANIUM_BLOCK.get())
                .pattern("TTT")
                .pattern("TTT")
                .pattern("TTT")
                .define('T', ModItems.TITANIUM_INGOT.get())
                .unlockedBy("has_titanium_ingot", has(ModItems.TITANIUM_INGOT.get()))
                .save(consumer);
        
        // Machine recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BASIC_GENERATOR.get())
                .pattern("TTT")
                .pattern("TFT")
                .pattern("CRC")
                .define('T', ModItems.TITANIUM_INGOT.get())
                .define('F', Items.FURNACE)
                .define('C', ModItems.CIRCUIT_BOARD.get())
                .define('R', Items.REDSTONE)
                .unlockedBy("has_titanium", has(ModItems.TITANIUM_INGOT.get()))
                .save(consumer);
    }
}
```

## 🎮 Testing Phase 1

### Checklist
- [ ] Ores generate in world
- [ ] Items have correct textures
- [ ] Basic generator accepts fuel
- [ ] Energy conduits connect visually
- [ ] Material processor doubles ores
- [ ] JEI shows all recipes
- [ ] Creative tab contains all items
- [ ] No crashes in multiplayer

### Commands for Testing
```
/give @p astroexpansion:titanium_ore 64
/give @p astroexpansion:basic_generator
/give @p astroexpansion:material_processor
```

## 🚀 Next Steps

After completing Phase 1:
1. Test all features thoroughly
2. Fix any bugs found
3. Optimize performance
4. Begin Phase 2 (Storage System)

This guide provides the concrete implementation for Phase 1 of Astro Expansion in Minecraft Forge 1.20.1.