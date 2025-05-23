NeoForge 21.5 for Minecraft 1.21.5
Hello everyone,
The first beta release of NeoForge 21.5 for Minecraft 1.21.5 is now available: 21.5.0-beta.
And with that the Team wants to hand out a large thanks for all the porting efforts this time around to the entire team, you all did amazing work!
Now let's get into the changes, shall we?
Changes
This section will draw heavily from the Porting Primer, as well as from changes noticed by the porting team.
Weapons, Tools, and Armor: Removing the Redundancies
There have been a lot of updates to weapons, tools, and armor that removes the reliance on the hardcoded base classes of SwordItem, DiggerItem, and ArmorItem, respectively. These have been replaced with their associated data components WEAPON for damage, TOOL for mining, ARMOR for protection, and BLOCKS_ATTACKS for shields. Additionally, the missing attributes are usually specified by setting the ATTRIBUTE_MODIFIERS, MAX_DAMAGE, MAX_STACK_SIZE, DAMAGE, REPAIRABLE, and ENCHANTABLE.
Given that pretty much all the non-specific logic has moved to a data component, these classes have now been completely removed. Use one of the available item property methods or call Item$Properties#component directly to set up each item as a weapon, tool, armor, or some combination of the three.
We have also removed the _DIG ItemAbilities these tools previously had in favour of the new components.
Extrapolating the Saddles: Equipment Changes
A new EquipmentSlot has been added for saddles, which brings with it new changes for generalizing slot logic.
First, rendering an equipment slot for an entity can now be handled as an additional RenderLayer called SimpleEquipmentLayer. This takes in the entity renderer, the EquipmentLayerRenderer, the layer type to render, a function to get the ItemStack from the entity state, and the adult and baby models. The renderer will attempt to look up the client info from the associated equippable data component and use that to render the laters as necessary.
Next, instead of having individual lists for each equipment slot on the entity, there is now a general EntityEquipment object that holds a delegate to a map of slots to ItemStacks. This simplifies the storage logic greatly.
Finally, equippables can now specify whether an item should be equipped to a mob on interact (usually on right-click) by setting equipOnInteract.
Data Component Getters
The data component system can now be represented on arbitrary objects through the use of the DataComponentGetter. As the name implies, the getter is responsible for getting the component from the associated type key.
Both block entities and entities use the DataComponentGetter to allow querying internal data, such as variant information or custom names. They both also have methods for collecting the data components from another holder (via applyImplicitComponents or applyImplicitComponent). Block entities also contain a method for collection to another holder via collectImplicitComponents.
NBT Tags and Parsing
NBT tags have received a rewrite, removing any direct references to types while also sealing and finalizing related classes. Getting a value from the tag now returns an Optional-wrapped entry, unless you use one of the get*Or methods, which have a parameter for a fallback default value. Objects, on the other hand, do not take in a default, instead returning an empty variant of the desired tag.
Writing with Codecs
CompoundTags now have methods to write and read using a Codec or MapCodec. For a Codec, it will store the serialized data inside the key specified. For a MapCodec, it will merge the fields into the top level tag.
java// For some Codec<ExampleObject> CODEC and MapCodec<ExampleObject> MAP_CODEC
// We will also have ExampleObject example
CompoundTag tag = new CompoundTag();

// For a codec
tag.store("example_key", CODEC, example);
Optional<ExampleObject> fromCodec = tag.read("example_key", CODEC);

// For a map codec
tag.store(MAP_CODEC, example);
Optional<ExampleObject> fromMapCodec = tag.read(MAP_CODEC);
Render Pipeline Rework
Rendering an object to the screen, whether through a shader or a RenderType, has been fully or partially reworked, depending on what systems you were using previously. As such, a lot of things need to be reexplained, the primer mentioned below will provide a very in-depth look. However, for the people who don't care, here's the TL;DR.
First, core-shader JSONs no longer exist. They are replaced by a RenderPipeline, which is effectively an in-code substitute.
Second, the RenderPipelines turns most flag values into enum constants. For example, instead of storing the blend function mode id, you store a BlendFunction. Similarly, you no longer store or set up the direct texture objects, but instead manage it through a GpuTexture.
Finally, the VertexBuffer can either draw to the framebuffer by directly passing in the RenderPipeline, updating any necessary uniforms in the consumer, or by passing in the RenderType.
Abstracting OpenGL
As many are aware, Minecraft has been abstracting away their OpenGL calls and constants, and this release is no different. All the calls to GL codes, except BufferUsage, have been moved out of object references, to be obtained typically by calling GlConst$toGl. However, with all the other rendering reworks, there are numerous changes and complexities that require learning an entirely new system, assuming you're not using RenderTypes.

Warning
Please be aware that while these changes are wide sweeping, OpenGL is still the underlying platform, so they will probably not cause compile errors. Mods are however advised switch to the new platform.

Model rework
Besides the abstracting of OpenGL, there are also significant changes to how models are loaded into the game, and how they are processed. Examples of this include rewrites to model discovery, parsing and instantiation. Additionally, NeoForge has aligned its BakedModel framework closer to what vanilla wants to use in the new model part system.

Note
The changes in this area are significant, and would blow up the size of this announcement. We will post additional information and documentation in the next few days that describe the changes and how to use them.

Fabric is also going in this direction, causing less headaches for cross-platform modders.
Client Mod Initialization
Previously, in the case of the client, mods were instantiated during client initialization. This meant that Minecraft.getInstance() returned a valid object and was usable to a certain degree.
To better align with vanilla changes to game option registration (such as keybinds), your @Mod entrypoints will now be constructed before Minecraft. As a consequence, accessing the Minecraft instance during mod loading is not possible, and mods that require access to it will have to adjust their code (for example, to use the FMLClientSetupEvent).
NeoForge internal changes
Besides the myriad of user and modder facing changes, there are also some internal changes to NeoForge's build system, which modders will not experience.
Split SourceSets
To prepare for supporting split sourcesets in mod development environments, we needed to split the NeoForge code base first, based on whether the code in question was common, or client only. This split has now been performed and PRs written for older versions will need to be adapted, especially if they touch client side code. Players will not experience any changes, however some classes had to be moved from a common package to a client one or vice versa so some mods will have to be updated to account for this.
Porting Primer
A porting primer covering Minecraft's changes is available here (courtesy of @ChampionAsh5357).
We might update this blog post later with more NeoForge-specific changes.
Stay tuned, and happy porting!

# Minecraft 1.21.4 -> 1.21.5 Mod Migration Primer

This is a high level, non-exhaustive overview on how to migrate your mod from 1.21.4 to 1.21.5. This does not look at any specific mod loader, just the changes to the vanilla classes. All provided names use the official mojang mappings.

This primer is licensed under the [Creative Commons Attribution 4.0 International](http://creativecommons.org/licenses/by/4.0/), so feel free to use it as a reference and leave a link so that other readers can consume the primer.

If there's any incorrect or missing information, please file an issue on this repository or ping @ChampionAsh5357 in the Neoforged Discord server.

Thank you to:

- @TelepathicGrunt for the information within the 'Very Technical Changes' section
- @RogueLogix for their review and comments on the 'Render Pipeline Rework' section
- @Tslat for catching an error about `equipOnInteract`

## Pack Changes

There are a number of user-facing changes that are part of vanilla which are not discussed below that may be relevant to modders. You can find a list of them on [Misode's version changelog](https://misode.github.io/versions/?id=1.21.5&tab=changelog).

## Handling the Removal of Block Entities Properly

Previously, `BlockEntity` would handle all of their removal logic within `BlockBehaviour#onRemove`, including both dropping any stored items and removing the block entity itself. However, depending on how the method is used, it can cause some strange behaviors due to the mutable state of the block entity. For this reason, the logic that makes up the removal process has been split between two methods: `BlockEntity#preRemoveSideEffects` and `BlockBehaviour#affectNeighborsAfterRemoval`.

`BlockEntity#preRemoveSideEffects` is now responsible for removing anything from the block entity before it is removed from the level. By default, if the `BlockEntity` is a `Container` instance, it will drop the contents of the container into the level. Other logic can be handled within here, but it should generally avoid removing the `BlockEntity` itself, unless the position of the block entity tends to change dynamically, like for a piston.

From there, the `LevelChunk` logic will call `removeBlockEntity` before calling `BlockBehaviour#affectNeighborsAfterRemoval`. This should only send the updates to other blocks indicating that this block has been removed from the level. For `BlockEntity` holders, this can be done easily by calling `Containers#updateNeighboursAfterDestroy`. Otherwise may want to call `Level#updateNeighborsAt` themselves, depending on the situation.

- `net.minecraft.world.Containers`
    - `updateNeighboursAfterDestroy` - Updates the neighbor state aftering destroying the block at the specified position.
    - `dropContentsOnDestroy` is removed, handled within `BlockEntity#preRemoveSideEffects` for `Container` instances
- `net.minecraft.world.level.block.entity.BlockEntity#preRemoveSideEffects` - Handles logic on the block entity that should happen before being removed from the level.
- `net.minecraft.world.level.block.state.BlockBehaviour#onRemove`, `$BlockStateBase#onRemove` -> `affectNeighborsAfterRemoval`, should only handle logic to update the surrounding neighbors rather than dropping container data

## Voxel Shape Helpers

`VoxelShape`s have received numerous helpers for more common transformations of its base state. There are the `Block` methods for creating a centered (if desired) box and the `Shapes` methods for rotating a `VoxelShape` to its appropriate axis or direction. There is also a `Shapes#rotateAttachFace` method for rotating some `VoxelShape` that is attached to a face of a different block. The results are either stored in a `Map` of some key to a `VoxelShape`, or when using `Block#getShapeForEachState`, a `Function<BlockState, VoxelShape>`.

Most of the `Block` subclasses that had previous public or protected `VoxelShape`s are now private, renamed to a field typically called `SHAPE` or `SHAPES`. Stored `VoxelShape`s may also be in a `Function` instead of directly storing the map itself.

- `com.mojang.math.OctahedralGroup`
    - `permute` - Returns the axis that the given axis is permuted to within the specified group.
    - `fromAngles` - Creates a group with the provided X and Y rotations.
- `net.minecraft.core.Direction$Axis#choose` now has an overload that takes in three booleans
- `net.minecraft.world.level.block.Block`
    - `boxes` - Creates one more than the specified number of boxes, using the index as part of the function to create the `VoxelShape`s.
    - `cube` - Creates a centered cube of the specified size.
    - `column` - Creates a horizontally centered column of the specified size.
    - `boxZ` - Creates a vertically centered (around the X axis) cube/column of the specified size.
    - `getShapeForEachState` now returns a `Function` which wraps the `ImmutableMap`, there is also a method that only considers the specified properties instead of all possible states.
- `net.minecraft.world.phys.shapes`
    - `DiscreteVoxelShape#rotate` - Rotates a voxel shape according to the permutation of the `OctahedralGroup`.
    - `Shapes`
        - `blockOccudes` -> `blockOccludes`
        - `rotate` - Rotates a given voxel shape according to the permutation of the `OctahedralGroup` around the provided vector, or block center if not specified.
        - `equal` - Checks if two voxel shapes are equivalent.
        - `rotateHorizontalAxis` - Creates a map of axes to `VoxelShape`s of a block being rotated around the y axis.
        - `rotateAllAxis` - Creates a map of axes to `VoxelShape`s of a block being rotated around any axis.
        - `rotateHorizontal` - Creates a map of directions to `VoxelShape`s of a block being rotated around the y axis.
        - `rotateAll` - Creates a map of directions to `VoxelShape`s of a block being rotated around the any axis.
        - `rotateAttachFace` - Creates a map of faces to a map of directions to `VoxelShape`s of a block being rotated around the y axis when attaching to the faces of other blocks.
    - `VoxelShape#move` now has an overload to take in a `Vec3i`

## Weapons, Tools, and Armor: Removing the Redundancies

There have been a lot of updates to weapons, tools, and armor that removes the reliance on the hardcoded base classes of `SwordItem`, `DiggerItem`, and `ArmorItem`, respectively. These have been replaced with their associated data components `WEAPON` for damage, `TOOL` for mining, `ARMOR` for protection, and `BLOCKS_ATTACKS` for shields. Additionally, the missing attributes are usually specified by setting the `ATTRIBUTE_MODIFIERS`, `MAX_DAMAGE`, `MAX_STACK_SIZE`, `DAMAGE`, `REPAIRABLE`, and `ENCHANTABLE`. Given that pretty much all of the non-specific logic has moved to a data component, these classes have now been completely removed. Use one of the available item property methods or call `Item$Properties#component` directly to set up each item as a weapon, tool, armor, or some combination of the three.

Constructing a `BlockAttacks` component for a shield-like item:

```java
var blocker = new BlocksAttacks(
    // The number of seconds to wait when the item is being used
    // before the blocking effect is applied.
    1.2f,
    // A scalar to change how many ticks the blocker is disabled
    // for. If negative, the blocker cannot normally be disabled.
    0.5f,
    // A list of reductions for what type and how much of a damage type
    // is blocked by this blocker.
    List.of(
        new DamageReduction(
            // The horizontal blocking angle of the shield required to apply
            // the reduction
            90f,
            // A set of damage types this reduction should apply for.
            // When empty, it applies for all damage types.
            Optional.empty(),
            // The base damage to reduce the attack by.
            1f,
            // A scalar representing the fraction of the damage blocked.
            0.5f
        )
    ),
    // A function that determines how much durability to remove to the blocker.
    new ItemDamageFunction(
        // A threshold that specifies the minimum amount of damage required
        // to remove durability from the blocker.
        4f,
        // The base durability to remove from the blocker.
        1f,
        // A scalar representing the fraction of the damage to convert into
        // removed durability.
        0.5f
    ),
    // A tag key containing the items that can bypass the blocker and deal
    // damage directly to the wielding entity. If empty, no item can bypass
    // the blocker.
    Optional.of(DamageTypeTags.BYPASSES_SHIELD),
    // The sound to play when the blocker successfully mitigates some damage.
    Optional.of(SoundEvents.SHIELD_BLOCK),
    // The sound to play when the blocker is disabled by a weapon.
    Optional.of(SoundEvents.SHIELD_BREAK)
);
```

Constructing a `Weapon` component for a sword-like item:

```java
var weapon = new Weapon(
    // The amount of durability to remove from the item.
    3,
    // The number of seconds a `BlocksAttack`s component item should
    // be disabled for when hit with this weapon.
    5f
);
```

- `net.minecraft.core.component.DataComponents`
    - `UNBREAKABLE` is now a `Unit` instance
    - `HIDE_ADDITIONAL_TOOLTIP`, `HIDE_TOOLTIP` have been bundled in `TOOLTIP_DISPLAY`, taking in a `TooltipDisplay`
    - `BLOCKS_ATTACKS` - A component that determines whether a held item can block an attack from some damage source
    - `INSTRUMENT` now takes in an `InstrumentComponent`
    - `PROVIDES_TRIM_MATERIAL`, `PROVIDES_BANNER_PATTERNS` handles a provider for their associated types.
    - `BEES` now takes in a `Bees` component
    - `BREAK_SOUND` - The sound to play when the item breaks.
- `net.minecraft.data.recipes`
    - `RecipeProvider#trimSmithing` now takes in the key for the `TrimPattern`
    - `SmithingTrimRecipeBuilder` now takes in a holder for the `TrimPattern`
- `net.minecraft.world.entity.LivingEntity`
    - `blockUsingShield` -> `blockUsingItem`
    - `blockedByShield` -> `blockedByItem`
    - `hurtCurrentlyUsedShield` is removed
    - `canDisableBlocking` -> `getSecondsToDisableBlocking`, not one-to-one
    - `applyItemBlocking` - Applies the damage reduction done when blocking an attack with an item.
    - `isDamageSourceBlocked` is removed
- `net.minecraft.world.entity.player.Player#disableShield` -> `net.minecraft.world.item.component.BlocksAttacks#disable`
- `net.minecraft.world.item`
    - `AnimalArmorItem` class is removed
    - `ArmorItem` class is removed
    - `AxeItem` now extends `Item`
    - `BannerPatternItem` class is removed
    - `DiggerItem` class is removed
    - `FireworkStarItem` class is removed
    - `HoeItem` now extends `Item`
    - `InstrumentItem` no longer takes in the tag key
    - `Item`
        - `getBreakingSound` is removed
        - `$Properties`
            - `tool` - Sets the item as a tool.
            - `pickaxe` - Sets the item as a pickaxe.
            - `sword` - Sets the item as a sword.
            - `axe` - Sets the item as an axe.
            - `hoe` - Sets the item as a hoe.
            - `shovel` - Sets the item as a shovel.
            - `trimMaterial` - Sets the item as providing a trim material.
    - `ItemStack#getBreakingSound` is removed
    - `PickaxeItem` class is removed
    - `ShovelItem` now extends `Item`
    - `SwordItem` class is removed
    - `ToolMaterial#applyToolProperties` now takes in a boolean of whether the weapon can disable a blocker (e.g., shield)
- `net.minecraft.world.item.component`
    - `Bees` - A component that holds the occupants of a beehive.
    - `BlocksAttacks` - A component for blocking an attack with a held item.
    - `InstrumentComponent` - A component that holds the sound an instrument plays.
    - `ProvidesTrimMaterial` - A component that provides a trim material to use on some armor.
    - `Tool` now takes in a boolean representing if the tool can destroy blocks in creative
    - `Unbreakable` class is removed
    - `Weapon` - A data component that holds how much damage the item can do and for how long it disables blockers (e.g., shield).
- `net.minecraft.world.item.equipment`
    - `AllowedEntitiesProvider` - A functional interface for getting the entities that are allowed to handle the associated logic.
    - `ArmorMaterial`
        - `humanoidProperties` -> `Item$Properties#humanoidArmor`
        - `animalProperties` -> `Item$Properties#wolfArmor`, `horseArmor`
        - `createAttributes` is now public
    - `Equippable`
        - `equipOnInteract` - When true, the item can be equipped to another entity when interacting with them.
        - `saddle` - Creates an equippable for a saddle.
        - `equipOnTarget` - Equips the item onto the target entity.


### Extrapolating the Saddles: Equipment Changes

A new `EquipmentSlot` has been added for saddles, which brings with it new changes for genercizing slot logic.

First, rendering an equipment slot for an entity can now be handled as an additional `RenderLayer` called `SimpleEquipmentLayer`. This takes in the entity renderer, the `EquipmentLayerRenderer`, the layer type to render, a function to get the `ItemStack` from the entity state, and the adult and baby models. The renderer will attempt to look up the client info from the associated equippable data component and use that to render the laters as necessary.

Next, instead of having individual lists for each equipment slot on the entity, there is now a general `EntityEquipment` object that holds a delegate to a map of slots to `ItemStack`s. This simplifies the storage logic greatly.

Finally, equippables can now specify whether an item should be equipped to a mob on interact (usually right-click) by setting `equipOnInteract`.

- `net.minecraft.client.model`
    - `CamelModel`
        - `head` is now public
        - `createBodyMesh` - Creates the mesh definition for a camel.
    - `CamelSaddleModel` - A model for a camel with a saddle.
    - `DonkeyModel#createSaddleLayer` - Creates the layer definition for a donkey with a saddle.
    - `EquineSaddleModel` - A model for an equine animal with a saddle.
    - `PolarBearModel#createBodyLayer` now takes in a boolean for if the entity is a baby
- `net.minecraft.client.renderer.entity.layers.HorseArmorLayer`, `SaddleLayer` -> `SimpleEquipmentLayer`
- `net.minecraft.client.renderer.entity.state`
    - `CamelRenderState#isSaddled` -> `saddle`, not one-to-one
    - `EquineRenderState#isSaddled` -> `saddle`, not one-to-one
    - `PigRenderState#isSaddled` -> `saddle`, not one-to-one
    - `SaddleableRenderState` class is removed
    - `StriderRenderState#isSaddled` -> `saddle`, not one-to-one
    - `CamelRenderState#isSaddled` -> `saddle`, not one-to-one
- `net.minecraft.client.resources.model.EquipmentClientInfo$LayerType` now has:
    - `PIG_SADDLE`
    - `STRIDER_SADDLE`
    - `CAMEL_SADDLE`
    - `HORSE_SADDLE`
    - `DONKEY_SADDLE`
    - `MULE_SADDLE`
    - `ZOMBIE_HORSE_SADDLE`
    - `SKELETON_HORSE_SADDLE`
    - `trimAssetPrefix` - Returns the prefix applied to the texture containing the armor trims for the associated type.
- `net.minecraft.world.entity`
    - `EntityEquipment` - A map of slots to item stacks representing the equipment of the entity.
    - `EquipmentSlot`
        - `SADDLE`, `$Type#SADDLE`
        - `canIncreaseExperience` - Whether the slot can increase the amount of experience earned when killing a mob.
    - `EquipmentSlotGroup` is now an iterable
        - `SADDLE`
        - `slots` - Returns the slots within the group.
    - `LivingEntity`
        - `getEquipSound` - Gets the sound to play when equipping an item into a slot.
        - `getArmorSlots`, `getHandSlots`, `getArmorAndBodyArmorSlots`, `getAllSlots` are removed
        - `equipment` - The equipment worn by the entity.
        - `createEquipment` - Sets the default equipment worn by the entity.
        - `drop` - Drops the specified stack.
        - `getItemBySlot`, `setItemBySlot` are no longer abstract.
        - `verfiyEquippedItem` is removed
    - `Mob`
        - `isSaddled` - Checks if an item is in the saddle slot.
        - `createEquipmentSlotContainer` - Creates a single item container for the equipment slot.
    - `OwnableEntity#getRootOwner` - Gets the highest level owner of the entity.
    - `Saddleable` interface is removed
- `net.minecraft.world.entity.animal.horse.AbstractHorse`
    - `syncSaddletoClients` is removed
    - `getBodyArmorAccess` is removed
- `net.minecraft.world.entity.player`
    - `Inventory`
        - `armor`, `offhand` -> `EQUIPMENT_SLOT_MAPPING`, not one-to-one
        - `selected` is now private
        - `setSelectedHotbarSlot` -> `setSelectedSlot`
            - Getter also exists `getSelectedSlot`
        - `getSelected` -> `getSelectedItem`
            - Setter also exists `setSelectedItem`
        - `getNonEquipmentItems` - Returns the list of non-equipment items in the inventory.
        - `getDestroySpeed` is removed
        - `getArmor` is removed
    - `PlayerEquipment` - Equipment that is worn by the player.
- `net.minecraft.world.item`
    - `Item#inventoryTick(ItemStack, Level, Entity, int, boolean)` -> `inventoryTick(ItemStack, ServerLevel, Entity, EquipmentSlot)`
    - `SaddleItem` class is removed

## Weighted List Rework

The weighted random lists have been redesigned into a basic class that hold weighted entries, and a helper class that can obtain weights from the objects themselves.

First there is `WeightedList`. It is effectively the replacement for `SimpleWeightedRandomList`, working the exact same way by storing `Weighted` (replacement for `WeightedEntry`) entries in the list itself. Internally, the list is either stored as a flat array of object entries, or the compact weighted list if the total weight is greater than 64. Then, to get a random element, either `getRandom` or `getRandomOrThrow` can be called to obtain an entry. Both of these methods will either return some form of an empty object or exception if there are no elements in the list.

Then there are the static helpers within `WeightedRandom`. These take in raw lists and some `ToIntFunction` that gets the weight from the list's object. Some methods also take in an integer either representing the largest index to choose from or the entry associated with the weighted index.

- `net.minecraft.client.resources.model.WeightedBakedModel` now takes in a `WeightedList` instead of a `SimpleWeightedRandomList`
- `net.minecraft.util.random`
    - `SimpleWeightedRandomList`, `WeightedRandomList` -> `WeightedList`, now final and not one-to-one
        - `contains` - Checks if the list contains this element.
    - `Weight` class is removed
    - `WeightedEntry` -> `Weighted`
    - All `WeightedRandom` static methods now take in a `ToIntFunction` to get the weight of some entry within the provided list
- `net.minecraft.util.valueproviders.WeightedListInt` now takes in a `WeightedList`
- `net.minecraft.world.level.SpawnData#LIST_CODEC` is now a `WeightedList` of `SpawnData`
- `net.minecraft.world.level.biome`
    - `Biome#getBackgroundMusic` is now a `WeightedList` of `Music`
    - `BiomeSpecialEffects#getBackgroundMusic`, `$Builder#backgroundMusic` is now a `WeightedList` of `Music`
    - `MobSpawnSettings#EMPTY_MOB_LIST`, `getMobs` is now a `WeightedList`
- `net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig#spawnPotentialsDefinition`, `lootTablesToEject` now takes in a `WeightedList`
- `net.minecraft.world.level.chunk.ChunkGenerator#getMobsAt` now returns a `WeightedList`
- `net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider` now works with `WeightedList`s
- `net.minecraft.world.level.levelgen.heightproviders.WeightedListHeight` now works with `WeightedList`s
- `net.minecraft.world.level.levelgen.structure.StructureSpawnOverride` now takes in a `WeightedList`
- `net.minecraft.world.level.levelgen.structure.pools.alias`
    - `PoolAliasBinding#random`, `randomGroup` now takes in a `WeightedList`
    - `Random` now takes in a `WeightedList`
    - `RandomGroup` now takes in a `WeightedList`
- `net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure#FORTRESS_ENEMIES` is now a `WeightedList`

## Tickets

Tickets have been reimplemented into a half type registry-like, half hardcoded system. The underlying logic of keeping a chunk loaded or simulated for a certain period of time still exists; however, the logic associated with each ticket is hardcoded into their appropriate locations, such as the forced or player loading tickets.

Tickets begin with their registered `TicketType`, which contains information about how many ticks should the ticket should last for (or `0` when permanent), whether the ticket should be saved to disk, and what the ticket is used for. A ticket has two potential uses: one for loading the chunk and keeping it loaded, and one for simulating the chunk based on the expected movement of the ticket creator. Most ticks specify that they are for both loading and simulation.

There are two special types that have additional behavior associated with them. `TicketType#FORCED` has some logic for immediately loading the chunk and keeping it loaded. `TicketType#UNKNOWN` cannot be automatically timed out, meaning they are never removed unless explicitly specified.

```java
// You need to register the ticket type to `BuiltInRegistries#TICKET_TYPE`
public static final TicketType EXAMPLE = new TicketType(
    // The amount of ticks before the ticket is removed
    // Set to 0 if it should not be removed
    0L,
    // Whether the ticket should be saved to disk
    true,
    // What the ticket will be used for
    TicketType.TicketUse.LOADING_AND_SIMULATION
);
```

Then there is the `Ticket` class, which are actually stored and handled within the `TicketStorage`. The `Ticket` class takes in the type of the ticket and uses it to automatically populate how long until it expires. It also takes in the ticket level, which is a generally a value of 31 (for entity ticking and block ticking), 32 (for block ticking), or 33 (only can access static or modify, not naturally update) minus the radius of chunks that can be loaded. A ticket is then added to the process by calling `TicketStorage#addTicketWithRadius` or its delegate `ServerChunkCache#addTicketWithRadius`. There is also `addTicket` if you wish to specify the ticket manually rather than having it computed based on its radius.

- `net.minecraft.server.level`
    - `ChunkMap` now takes in a `TicketStorage`
        - `$TrackedEntity#broadcastIgnorePlayers` - Broadcasts the packet to all player but those within the UUID list.
    - `DistanceManager`
        - `chunksToUpdateFutures` is now protected and takes in a `TicketStorage`
        - `purgeStaleTickets` -> `net.minecraft.world.level.TicketStorage#purgeStaleTickets`
        - `getTicketDebugString` -> `net.minecraft.world.level.TicketStorage#getTicketDebugString`
        - `getChunkLevel` - Returns the current chunk level or the simulated level when the provided boolean is true.
        - `getTickingChunks` is removed
        - `removeTicketsOnClosing` is removed
        - `$ChunkTicketTracker` -> `LoadingChunkTracker`, or `SimulationChunkTracker`
    - `ServerChunkCache`
        - `addRegionTicket` -> `addTicketWithRadius`, or `addTicket`
        - `removeRegionTicket` -> `removeTicketWithRadius`
        - `removeTicketsOnClosing` -> `deactivateTicketsOnClosing`
    - `Ticket` is no longer final or implements `Comparable`
        - The constructor no longer takes in a key
        - `CODEC`
        - `setCreatedTick`, `timedOut` -> `resetTicksLeft`, `decreaseTicksLeft`, `isTimedOut`; not one-to-one
    - `TicketType` is now a record and no longer has a generic
        - `getComparator` is removed
        - `doesLoad`, `doesSimulate` - Checks whether the ticket use is for their particular instance.
        - `$TicketUse` - What a ticket can be used for.
    - `TickingTracker` -> `SimulationChunkTracker`
- `net.minecraft.world.level.ForcedChunksSavedData` -> `TicketStorage`
- `net.minecraft.world.level.chunk.ChunkSource`
    - `updateChunkForced` now returns a boolean indicating if the chunk has been forcefully loaded
    - `getForceLoadedChunks` - Returns all chunks that have been forcefully loaded.

## The Game Test Overhaul

Game tests have been completely overhauled into a registry based system, completely revamped from the previous automatic annotation-driven system. However, most implementations required to use the system must be implemented yourself rather than provided by vanilla. As such, this explanation will go over the entire system, including which parts need substantial work to use it similarly to the annotation-driven system of the previous version.

### The Environment

All game tests happen within some environment. Most of the time, a test can occur independent of the area, but sometimes, the environment needs to be curated in some fashion, such as checking whether an entity or block does something at a given time or whether. To facilitate the setup and teardown of the environment for a given test instance, a `TestEnvironmentDefinition` is created.

A `TestEnvironmentDefinition` works similarly to the `BeforeBatch` and `AfterBatch` annotations. The environment contains two methods `setup` and `teardown` that manage the `ServerLevel` for the test. The environments are structured in a type-based registry system, meaning that every environment registers a `MapCodec` to built-in registry `minecraft:test_environment_definition_type` that is then consumed via the `TestEnvironmentDefinition` in a datapack registry `minecraft:test_environment`.

Vanilla, by default, provides the `minecraft:default` test environment which does not do anything. However, additional test environments can be created using the available test definition types.

#### Game Rules

This environment type sets the game rules to use for the test. During teardown, the game rules are set back to their default value.

```json5
// examplemod:example_environment
// In 'data/examplemod/test_environment/example_environment.json'
{
    "type": "minecraft:game_rules",

    // A list of game rules with boolean values to set
    "bool_rules": [
        {
            // The name of the rule
            "rule": "doFireTick",
            "value": false
        }
        // ...
    ],

    // A list of game rules with integer values to set
    "int_rules": [
        {
            "rule": "playersSleepingPercentage",
            "value": 50
        }
        // ...
    ]
}
```

#### Time of Day

This environment type sets the time to some non-negative integer, similar to how the `/time set <number>` command is used.

```json5
// examplemod:example_environment
// In 'data/examplemod/test_environment/example_environment.json'
{
    "type": "minecraft:time_of_day",

    // Sets the time of day in the world
    // Common values:
    // - Day      -> 1000
    // - Noon     -> 6000
    // - Night    -> 13000
    // - Midnight -> 18000
    "time": 13000
}
```

#### Weather

This environment type sets the weather, similar to how the `/weather` command is used.

```json5
// examplemod:example_environment
// In 'data/examplemod/test_environment/example_environment.json'
{
    "type": "minecraft:weather",

    // Can be one of three values:
    // - clear   (No weather)
    // - rain    (Rain)
    // - thunder (Rain and thunder)
    "weather": "thunder"
}
```

#### Function

This environment type provides two `ResourceLocation`s to mcfunctions to setup and teardown the level, respectively.

```json5
// examplemod:example_environment
// In 'data/examplemod/test_environment/example_environment.json'
{
    "type": "minecraft:function",

    // The setup mcfunction to use
    // If not specified, nothing will be ran
    // Points to 'data/examplemod/function/example/setup.mcfunction'
    "setup": "examplemod:example/setup",

    // The teardown mcfunction to use
    // If not specified, nothing will be ran
    // Points to 'data/examplemod/function/example/teardown.mcfunction'
    "teardown": "examplemod:example/teardown"
}
```

#### Composite

If multiple combinations are required, then the composite environment type (aptly named `all_of`) can be used to string multiple of the above environment types together.

```json5
// examplemod:example_environment
// In 'data/examplemod/test_environment/example_environment.json'
{
    "type": "minecraft:all_of",

    // A list of test environments to use
    // Can either specified the registry name or the environment itself
    "definitions": [
        // Points to 'data/minecraft/test_environment/default.json'
        "minecraft:default",
        {
            // A raw environment definition
            "type": "..."
        }
        // ...
    ]
}
```

### Custom Types

If none of the types above work, then a custom definition can be created by implementing `TestEnvironmentDefinition` and creating an associated `MapCodec`:

```java
public record ExampleEnvironmentType(int value1, boolean value2) implements TestEnvironmentDefinition {

    // Construct the map codec to register
    public static final MapCodec<ExampleEnvironmentType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("value1").forGetter(ExampleEnvironmentType::value1),
            Codec.BOOL.fieldOf("value2").forGetter(ExampleEnvironmentType::value2)
        ).apply(instance, ExampleEnvironmentType::new)
    );

    @Override
    public void setup(ServerLevel level) {
        // Setup whatever is necessary here
    }

    @Override
    public void teardown(ServerLevel level) {
        // Undo whatever was changed within the setup method
        // This should either return to default or the previous value
    }

    @Override
    public MapCodec<ExampleEnvironmentType> codec() {
        return CODEC;
    }
}
```

Then register the `MapCodec` using whatever registry method is required by your mod loader:

```java
Registry.register(
    BuiltInRegistries.TEST_ENVIRONMENT_DEFINITION_TYPE,
    ResourceLocation.fromNamespaceAndPath("examplemod", "example_environment_type"),
    ExampleEnvironmentType.CODEC
);
```

Finally, you can use it in your environment definition:

```json5
// examplemod:example_environment
// In 'data/examplemod/test_environment/example_environment.json'
{
    "type": "examplemod:example_environment_type",

    "value1": 0,
    "value2": true
}
```

### Test Functions

The initial concept of game tests were structured around running functions from `GameTestHelper` determining whether the test succeeds or fails. Test functions are the registry-driven representation of those. Essentially, every test function is a method that takes in a `GameTestHelper`.

At the moment, vanilla only provides `minecraft:always_pass`, which just calls `GameTestHelper#succeed`. Test functions are also not generated, meaning it simply runs the value with whatever is provided. As such, a test function should generally represent a single old game test:

```java
Registry.register(
    BuiltInRegistries.TEST_FUNCTION,
    ResourceLocation.fromNamespaceAndPath("examplemod", "example_function"),
    (GameTestHelper helper) -> {
        // Run whatever game test commands you want
        helper.assertBlockPresent(...);

        // Make sure you have some way to succeed
        helper.succeedIf(() -> ...);
    }
);
```

### Test Data

Now that we have environments and test functions, we can get into defining our game test. This is done through `TestData`, which is the equivalent of the `GameTest` annotation. The only things changed are that structures are now referenced by their `ResourceLocation` via `structure`, `GameTest#timeoutTicks` is now renamed to `TestData#maxTicks`, and instead of specifying `GameTest#rotationSteps`, you simply provide the `Rotation` via `TestData#rotation`. Everything else remains the same, just represented in a different format.

### The Game Test Instance

With the `TestData` in hand, we can now link everything together through the `GameTestInstance`. This instance is what actually represents a single test. Once again, vanilla only provides the default `minecraft:always_pass`, so we will need to construct the instance ourselves.

#### The Original Instance

The previous game tests are implemented using `minecraft:function`, which links a test function to the test data.

```json
// examplemod:example_test
// In 'data/examplemod/test_instance/example_test.json'
{
    "type": "minecraft:function",

    // Points to a 'Consumer<GameTestHelper>' in the test function registry
    "function": "examplemod:example_function",

    // The 'TestData' information

    // The environment to run the test in
    // Points to 'data/examplemod/test_environment/example_environment.json'
    "environment": "examplemod:example_environment",
    // The structure used for the game test
    // Points to 'data/examplemod/structure/example_structure.nbt'
    "structure": "examplemod:example_structure",
    // The number of ticks that the game test will run until it automatically fails
    "max_ticks": 400,
    // The number of ticks that are used to setup everying required for the game test
    // This is not counted towards the maximum number of ticks the test can take
    // If not specified, defaults to 0
    "setup_ticks": 50,
    // Whether the test is required to succeed to mark the batch run as successful
    // If not specified, defaults to true
    "required": true,
    // Specifies how the structure and all subsequent helper methods should be rotated for the test
    // If not specified, nothing is rotated
    "rotation": "clockwise_90",
    // When true, the test can only be ran through the `/test` command
    // If not specified, defaults to false
    "manual_only": true,
    // Specifies the maximum number of times that the test can be reran
    // If not specified, defaults to 1
    "max_attempts": 3,
    // Specifies the minimum number of successes that must occur for a test to be marked as successful
    // This must be less than or equal to the maximum number of attempts allowed
    // If not specified, defaults to 1
    "required_successes": 1,
    // Returns whether the structure boundary should keep the top empty
    // This is currently only used in block-based test instances
    // If not specified, defaults to false 
    "sky_access": false
}
```

#### Block-Based Instances

Vanilla also provides a block-based test instance via `minecraft:block_based`. This is handled through via structures with test blocks receiving signals via `Level#hasNeighborSignal`. To start, a structure must have one test block which is set to its start mode. This block is then triggered, sending a fifteen signal pulse for one tick. Structures may then have as many test blocks with either a log, accept, or fail mode set. Log test blocks also send a fifteen signal pulse when activated. Accept and fail test blocks either succeed or fail the game test if any of them are activated (success takes precedent over failure).

As this test relies on test blocks in the structure, no additional information is required other than the test data:

```json
// examplemod:example_test
// In 'data/examplemod/test_instance/example_test.json'
{
    "type": "minecraft:block_based",

    // The 'TestData' information

    // Points to 'data/examplemod/test_environment/example_environment.json'
    "environment": "examplemod:example_environment",
    // Points to 'data/examplemod/structure/example_structure.nbt'
    "structure": "examplemod:example_structure",
    "max_ticks": 400,
    "setup_ticks": 50,
    "required": true,
    "rotation": "clockwise_90",
    "manual_only": true,
    "max_attempts": 3,
    "required_successes": 1,
    "sky_access": false
}
```

#### Custom Tests

If you need to implement your own test-based logic, whether using a more dynamic feature ~~or because you can't be bothered to migrated all of your data logic to the new systems~~, you can create your own custom test instance by extending `GameTestInstance` and creating an associated `MapCodec`:

```java
public class ExampleTestInstance extends GameTestInstance {

    // Construct the map codec to register
    public static final MapCodec<ExampleTestInstance> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("value1").forGetter(test -> test.value1),
            Codec.BOOL.fieldOf("value2").forGetter(test -> test.value2),
            TestData.CODEC.forGetter(ExampleTestInstance::info)
        ).apply(instance, ExampleTestInstance::new)
    );

    public ExampleTestInstance(int value1, boolean value2, TestData<Holder<TestEnvironmentDefinition>> info) {
        super(info);
    }

    @Override
    public void run(GameTestHelper helper) {
        // Run whatever game test commands you want
        helper.assertBlockPresent(...);

        // Make sure you have some way to succeed
        helper.succeedIf(() -> ...);
    }

    @Override
    public MapCodec<ExampleTestInstance> codec() {
        return CODEC;
    }

    @Override
    protected MutableComponent typeDescription() {
        // Provides a description about what this test is supposed to be
        // Should use a translatable component
        return Component.literal("Example Test Instance");
    }
}
```

Then register the `MapCodec` using whatever registry method is required by your mod loader:

```java
Registry.register(
    BuiltInRegistries.TEST_INSTANCE_TYPE,
    ResourceLocation.fromNamespaceAndPath("examplemod", "example_test_instance"),
    ExampleTestInstance.CODEC
);
```

Finally, you can use it in your test instance:

```json
// examplemod:example_test
// In 'data/examplemod/test_instance/example_test.json'
{
    "type": "examplemod:example_test_instance",

    "value1": 0,
    "value2": true,

    // The 'TestData' information

    // Points to 'data/examplemod/test_environment/example_environment.json'
    "environment": "examplemod:example_environment",
    // Points to 'data/examplemod/structure/example_structure.nbt'
    "structure": "examplemod:example_structure",
    "max_ticks": 400,
    "setup_ticks": 50,
    "required": true,
    "rotation": "clockwise_90",
    "manual_only": true,
    "max_attempts": 3,
    "required_successes": 1,
    "sky_access": false
}
```

- `net.minecraft.client.renderer.blockentity`
    - `BeaconRenderer` now has a generic that takes in a subtype of `BlockEntity` and `BeaconBeamOwner`
    - `StructureBlockRenderer` -> `BlockEntityWithBoundingBoxRenderer`, not one-to-one
- `net.minecraft.core.registries.Registries#TEST_FUNCTION`, `TEST_ENVIRONMENT_DEFINITION_TYPE`, `TEST_INSTANCE_TYPE`
- `net.minecraft.gametest.Main` - The entrypoint for the game test server.
- `net.minecraft.gametest.framework`
    - `AfterBatch`, `BeforeBatch` annotations are removed
    - `BlockBasedTestInstance` - A test instance for testing the test block.
    - `BuiltinTestFunctions` - Contains all registered test functions.
    - `FailedTestTracker` - An object for holding all game tests that failed.
    - `FunctionGameTestInstance` - A test instance for running a test function.
    - `GameTest` annotation is removed
    - `GameTestAssertException` now extends `GameTestException`
    - `GameTestException` - An exception thrown during the execution of a game test.
    - `GameTestBatch` now takes in an index and environment definition instead of a name and batch setups
    - `GameTestBatchFactory`
        - `fromTestFunction` -> `divideIntoBatches`, not one-to-one
        - `toGameTestInfo` is removed
        - `toGameTestBatch` now takes in an environment definition and an index
        - `$TestDecorator` - Creates a list of test infos from a test instance and level.
    - `GameTestEnvironments` - Contains all environments used for batching game test instances.
    - `GameTestGenerator` annotation is removed
    - `GameTestHelper`
        - `tickBlock` - Ticks the block at the specific position.
        - `assertionException` - Returns a new exception to throw on error.
        - `getBlockEntity` now takes in a `Class` to cast the block entity to
        - `assertBlockTag` - Checks whether the block at the position is within the provided tag.
        - `assertBlock` now takes in a block -> component function for the error message.
        - `assertBlockProperty` now takes in a `Component` instead of a string
        - `assertBlockState` now takes in either nothing, a blockstate -> component function, or a supplied component
        - `assertRedstoneSignal` now takes in a supplied component
        - `assertContainerSingle` - Asserts that a container contains exactly one of the item specified.
        - `assertEntityPosition`, `assertEntityProperty` now takes in a component
        - `fail` now takes in a `Component` for the error message
        - `assertTrue`, `assertValueEqual`, `assertFalse` now takes in a component
    - `GameTestInfo` now takes in a holder-wrapped `GameTestInstance` instead of a `TestFunction`
        - `setStructureBlockPos` -> `setTestBlockPos`
        - `placeStructure` now returns nothing
        - `getTestName` - `id`, not one-to-one
        - `getStructureBlockPos` -> `getTestBlockPos`
        - `getStructureBlockEntity` -> `getTestInstanceBlockEntity`
        - `getStructureName` -> `getStructure`
        - `getTestFunction` -> `getTest`, `getTestHolder`, not one-to-one
        - `getOrCalculateNorthwestCorner`, `setNorthwestCorner` are removed
        - `fail` now takes in a `Component` or `GameTestException` instead of a `Throwable`
        - `getError` now returns a `GameTestException` instead of a `Throwable`
    - `GameTestInstance` - Defines a test to run.
    - `GameTestInstances` - Contains all registered tests.
    - `GameTestMainUtil` - A utility for running the game test server.
    - `GameTestRegistry` class is removed
    - `GameTestSequence`
        - `tickAndContinue`, `tickAndFailIfNotComplete` now take in an integer for the tick instead of a long
        - `thenFail` now takes in a supplied `GameTestException` instead of a `Throwable`
    - `GameTestServer#create` now takes in an optional string and boolean instead of the collection of test functions and the starting position
    - `GeneratedTest` - A object holding the test to run for the given environment and the function to apply
    - `GameTestTicker$State` - An enum containing what state the game test ticker is currently executing.
    - `GameTestTimeoutException` now extends `GameTestException`
    - `ReportGameListener#spawnBeacon` is removed
    - `StructureBlockPosFinder` -> `TestPosFinder`
    - `StructureUtils`
        - `testStructuresDir` is now a path
        - `getStructureBounds`, `getStructureBoundingBox`, `getStructureOrigin`, `addCommandBlockAndButtonToStartTest` are removed
        - `createNewEmptyStructureBlock` -> `createNewEmptyTest`, not one-to-one
        - `getStartCorner`, `prepareTestStructure`, `encaseStructure`, `removeBarriers` are removed
        - `findStructureBlockContainingPos` -> `findTestContainingPos`
        - `findNearestStructureBlock` -> `findNearestTest`
        - `findStructureByTestFunction`, `createStructureBlock` are removed
        - `findStructureBlocks` -> `findTestBlocks`
        - `lookedAtStructureBlockPos` -> `lookedAtTestPos`
    - `TestClassNameArgument` is removed
    - `TestEnvironmentDefinition` - Defines the environment that the test is run on by setting the data on the level appropriately.
    - `TestFinder` no longer contains a generic for the context
        - `$Builder#allTests`, `allTestsInClass`, `locateByName` are removed
        - `$Builder#byArgument` -> `byResourceSelection`
    - `TestFunction` -> `TestData`, not one-to-one
    - `TestFunctionArgument` -> `net.minecraft.commands.arguments.ResourceSelectorArgument`
    - `TestFunctionFinder` -> `TestInstanceFinder`
    - `TestFunctionLoader` - Holds the list of test functions to load and run.
    - `UnknownGameTestException` - An exception that is thrown when the error of the game test is unknown.
- `net.minecraft.network.protocol.game`
    - `ClientboundTestInstanceBlockState` - A packet sent to the client containing the status of a test along with its size.
    - `ServerboundSetTestBlockPacket` - A packet sent to the server to set the information within the test block to run.
    - `ServerboundTestInstanceBlockActionPacket` - A packet sent to the server to set up the test instance within the test block.
- `net.minecraft.world.entity.player.Player`
    - `openTestBlock` - Opens a test block.
    - `openTestInstanceBlock` - Opens a test block for a game test instance.
- `net.minecraft.world.level.block`
    - `TestBlock` - A block used for running game tests.
    - `TestInstanceBlock` - A block used for managing a single game test.
- `net.minecraft.world.level.block.entity`
    - `BeaconBeamOwner` - An interface that represents a block entity with a beacon beam.
    - `BeaconBlockEntity` now implements `BeaconBeamOwner`
        - `BeaconBeamSection` -> `BeaconBeamOwner$Section`
    - `BoundingBoxRenderable` - An interface that represents a block entity that can render an arbitrarily-sized bounding box.
    - `StructureBlockEntity` now implements `BoundingBoxRenderable`
    - `TestBlockEntity` - A block entity used for running game tests.
    - `TestInstanceBlockEntity` - A block entity used for managing a single game test.
- `net.minecraft.world.level.block.state.properties.TestBlockMode` - A property for representing the current state of the game tests associated with a test block.

## Data Component Getters

The data component system can now be represented on arbitrary objects through the use of the `DataComponentGetter`. As the name implies, the getter is responsible for getting the component from the associated type key. Both block entities and entities use the `DataComponentGetter` to allow querying the internal data, such as variant information or custom names. They both also have methods for collecting the data components from another holder (via `applyImplicitComponents` or `applyImplicitComponent`). Block entities also contain a method for collection to another holder via `collectImplicitComponents`.

### Items

`ItemSubPredicate`s have been completely replaced with `DataComponentPredicate`s. Each sub predicate has its appropriate analog within the system.

- `net.minecraft.advancements.critereon.*` -> `net.minecraft.core.component.predicates.*`
    - `ItemAttributeModifiersPredicate` -> `AttributeModifiersPredicate`
    - `ItemBundlePredicate`  -> `BundlePredicate`
    - `ItemContainerPredicate` -> `ContainerPredicate`
    - `ItemCustomDataPredicate` -> `CustomDataPredicate`
    - `ItemDamagePredicate` -> `DamagePredicate`
    - `ItemEnchantmentsPredicate` -> `EnchantmentsPredicate`
    - `ItemFireworkExplosionPredicate` -> `FireworkExplosionPredicate`
    - `ItemFireworksPredicate` -> `FireworksPredicate`
    - `ItemJukeboxPlayablePredicate` -> `JukeboxPlayablePredicate`
    - `ItemPotionsPredicate` -> `PotionsPredicate`
    - `ItemSubPredicate` -> `DataComponentPredicate`, not one-to-one
        - `SINGLE_STREAM_CODEC`
    - `ItemSubPredicates` -> `DataComponentPredicates`, not one-to-one
    - `ItemTrimPredicate` -> `TrimPredicate`
    - `ItemWritableBookPredicate` -> `WritableBookPredicate`
    - `ItemWrittenBookPredicate` -> `WrittenBookPredicate`
- `net.minecraft.advancements.critereon`
    - `BlockPredicate` now takes in a `DataComponentMatchers` for matching any delegated component data
    - `DataComponentMatchers` - A predicate that operates on a `DataComponentGetter`, matching any exact and partial component data on the provider.
    - `EntityPredicate` now takes in a `DataComponentMatchers` instead of a `Optional<DataComponentExactPredicate>`
    - `ItemPredicate` now takes in a `DataComponentMatchers` for matching any delegated component data
    - `NbtPredicate#matches` now takes in a `DataComponentGetter` instead of an `ItemStack`
    - `SingleComponentItemPredicate` now implements `DataComponentPredicate` instead of `ItemSubPredicate`
        - `matches(ItemStack, T)` -> `matches(T)`
- `net.minecraft.core.component`
    - `DataComponentPatch`
        - `DELIMITED_STREAM_CODEC`
        - `$CodecGetter` - Gets the codec for a given component type.
    - `DataComponentPredicate` -> `DataComponentExactPredicate`
        - `isEmpty` - Checks if the expected components list within the predicate is empty.
- `net.minecraft.core.registries.Registries#ITEM_SUB_PREDICATE_TYPE` -> `DATA_COMPONENT_PREDICATE_TYPE`, not one-to-one
- `net.minecraft.world.item.AdventureModePredicate` no longer takes in a boolean to show in tooltip
- `net.minecraft.world.item`
    - `BannerItem#appendHoverTextFromBannerBlockEntityTag` is removed
    - `Item#appendHoverText(ItemStack, Item.TooltipContext, List<Component>, TooltipFlag)` -> `appendHoverText(ItemStack, Item.TooltipContext, TooltipDisplay, Consumer<Component>, TooltipFlag)`, now deprecated
    - `ItemStack`
        - `addToTooltip` is now public
        - `addDetailsToTooltip` - Appends the component details of an item to the tooltip.
    - `JukeboxPlayable#showInTooltip` is removed
- `net.minecraft.world.item.component`
    - `BlockItemStateProperties` now implements `TooltipProvider`
    - `ChargedProjectiles` now implements `TooltipProvider`
    - `CustomData#itemMatcher` is removed
    - `DyedItemColor#showInTooltip` is removed
    - `FireworkExplosion#addShapeNameTooltip` is removed
    - `ItemAttributeModifiers#showInTooltip` is removed
    - `ItemContainerContents` now implements `TooltipProvider`
    - `SeededContainerLoot` now implements `TooltipProvider`
    - `TooltipDisplay` - A component that handles what should be hidden within an item's tooltip.
    - `TooltipProvider#addToTooltip` now takes in a `DataComponentGetter`
- `net.minecraft.world.item.enchantment.ItemEnchantments#showInTooltip` is removed
- `net.minecraft.world.item.equipment.trim.ArmorTrim#showInTooltip` is removed
- `net.minecraft.world.item.trading.ItemCost` now takes in a `DataComponentExactPredicate` instead of a `DataComponentPredicate`
- `net.minecraft.world.level.block.Block#appendHoverText` is removed
- `net.minecraft.world.level.block.entity`
    - `BannerPatternLayers` now implements `TooltipProvider`
    - `PotDecorations` now implements `TooltipProvider`
- `net.minecraft.world.level.saveddata.maps.MapId` now implements `TooltipProvider`

### Entities

Some `EntitySubPredicate`s for entity variants have been transformed into data components stored on the held item due to a recent change on `EntityPredicate` now taking in a `DataComponentExactPredicate` for matching the slots on an entity.

- `net.minecraft.advancements.critereon`
    - `EntityPredicate` now takes in a `DataComponentExactPredicate` to match the equipment slots checked
    - `EntitySubPredicate`
        - `AXOLTOL` -> `DataComponents#AXOLOTL_VARIANT`
        - `FOX` -> `DataComponents#FOX_VARIANT`
        - `MOOSHROOM` -> `DataComponents#MOOSHROOM_VARIANT`
        - `RABBIT` -> `DataComponents#RABBIT_VARIANT`
        - `HORSE` -> `DataComponents#HORSE_VARIANT`
        - `LLAMA` -> `DataComponents#LLAMA_VARIANT`
        - `VILLAGER` -> `DataComponents#VILLAGER_VARIANT`
        - `PARROT` -> `DataComponents#PARROT_VARIANT`
        - `SALMON` -> `DataComponents#SALMON_SIZE`
        - `TROPICAL_FISH` -> `DataComponents#TROPICAL_FISH_PATTERN`, `TROPICAL_FISH_BASE_COLOR`, `TROPICAL_FISH_PATTERN_COLOR`
        - `PAINTING` -> `DataComponents#PAINTING_VARIANT`
        - `CAT` -> `DataComponents#CAT_VARIANT`, `CAT_COLLAR`
        - `FROG` -> `DataComponents#FROG_VARIANT`
        - `WOLF` -> `DataComponents#WOLF_VARIANT`, `WOLF_COLLAR`
        - `PIG` -> `DataComponents#PIG_VARIANT`
        - `register` with variant subpredicates have been removed
        - `catVariant`, `frogVariant`, `wolfVariant` are removed
        - `$EntityHolderVariantPredicateType`, `$EntityVariantPredicateType` are removed
    - `SheepPredicate` no longer takes in the `DyeColor`
- `net.minecraft.client.renderer.entity.state.TropicalFishRenderState#variant` -> `pattern`
- `net.minecraft.core.component`
    - `DataComponentGetter` - A getter that obtains data components from some object.
    - `DataComponentHolder`, `DataComponentMap` now extends `DataComponentGetter`
    - `DataComponentExactPredicate` is now a predicate of a `DataComponentGetter`
        - `expect` - A predicate that expects a certain value for a data component.
        - `test(DataComponentHolder)` is removed
    - `DataComponents`
        - `SHEEP_COLOR` - The dye color of a sheep.
        - `SHULKER_COLOR` - The dye color of a shulker (box).
        - `COW_VARIANT` - The variant of a cow.
        - `CHICKEN_VARIANT` - The variant of a chicken.
        - `WOLF_SOUND_VARIANT` - The sounds played by a wolf. 
- `net.minecraft.world.entity`
    - `Entity` now implements `DataComponentGetter`
        - `applyImplicitComponents` - Applies the components from the getter onto the entity. This should be overriden by the modder.
        - `applyComponentsFromItemStack` - Applies the components from the stack onto the entity.
        - `castComponentValue` - Casts the type of the object to the component type.
        - `setComponent` - Sets the component data onto the entity.
        - `applyImplicitComponent` - Applies the component data to the entity. This should be overriden by the modder.
        - `applyImplicitComponentIfPresent` - Applies the component if it is present on the getter.
    - `EntityType#appendCustomNameConfig` -> `appendComponentsConfig`
    - `VariantHolder` interface is removed
        - As such, all `setVariant` methods on relevant entities are private while the associated data can also be obtained from the `DataComponentGetter`
- `net.minecraft.world.entity.animal`
    - `CatVariant#CODEC`
    - `Fox$Variant#STREAM_CODEC`
    - `FrogVariant#CODEC`
    - `MushroomCow$Variant#STREAM_CODEC`
    - `Parrot$Variant#STREAM_CODEC`
    - `Rabbit$Variant#STREAM_CODEC`
    - `Salmon$Variant#STREAM_CODEC`
    - `TropicalFish`
        - `getVariant` -> `getPattern`
        - `$Pattern` now implements `TooltipProvider`
    - `Wolf` -> `.wolf.Wolf`
    - `WolfVariant` -> `.wolf.WolfVariant`, now a record, taking in an `$AssetInfo` and a `SpawnPrioritySelectors`
    - `WolfVariants` -> `.wolf.WolfVariants`
- `net.minecraft.world.entity.animal.axolotl.Axolotl$Variant#STREAM_CODEC`
- `net.minecraft.world.entity.animal.horse`
    - `Llama$Variant#STREAM_CODEC`
    - `Variant#STREAM_CODEC`
- `net.minecraft.world.entity.animal.wolf`
    - `WolfSoundVariant` - The sounds played by a wolf.
    - `WolfSoundVariants` - All vanilla wolf sound variants.
- `net.minecraft.world.entity.decoration.Painting`
    - `VARIANT_MAP_CODEC` is removed
    - `VARIANT_CODEC` is now private
- `net.minecraft.world.entity.npc.VillagerDataHolder#getVariant`, `setVariant` are removed
- `net.minecraft.world.entity.variant.VariantUtils`- A utility for getting the variant info of an entity.
- `net.minecraft.world.item`
    - `ItemStack#copyFrom` - Copies the component from the getter.
    - `MobBucketItem#VARIANT_FIELD_CODEC` -> `TropicalFish$Pattern#STREAM_CODEC`
- `net.minecraft.world.level.block.entity.BlockEntity#applyImplicitComponents` now takes in a `DataComponentGetter`
    - `$DataComponentInput` -> `DataComponentGetter`
- `net.minecraft.world.level.Spawner` methods now takes in a `CustomData` instead of the `ItemStack` itself

#### Spawn Conditions

To allow entities to spawn variants randomly but within given conditions, a new registry called `SPAWN_CONDITION_TYPE` was added. These take in `SpawnCondition`s: a selector that acts like a predicate to take in the context to see whether the given variant can spawn there. All of the variants are thrown into a list and then ordered based on the selected priorty stored in the `SpawnProritySelectors`. Those with a higher priority will be checked first, with multiple of the same priority selected in the order they are provided. Then, all variants on the same priority level where a condition has been met is selected at random.

```json5
// For some object where there are spawn conditions
[
    {
        // The spawn condition being checked
        "condition": {
            "type": "minecraft:biome",
            // Will check that the biome the variant is attempting to spawn in is in the forest
            "biomes": "#minecraft:is_forest"
        },
        // Will check this condition first
        "priority": 1
    },
    {
        // States that the condition will always be true
        "priority": 0
    }
]
```

- `net.minecraft.core.registries.Registries#SPAWN_CONDITION_TYPE`
- `net.minecraft.world.entity.variant`
    - `BiomeCheck` - A spawn condition that checks whether the entity is in one of the given biomes.
    - `MoonBrightnessCheck` - A spawn condition that checks the brightness of the moon.
    - `PriorityProvider` - An interface which orders the condition selectors based on some priority integer.
    - `SpawnCondition` - Checks whether an entity can spawn at this location.
    - `SpawnConditions` - The available spawn conditions to choose from.
    - `SpawnContext` - An object holding the current position, level, and biome the entity is being spawned within.
    - `SpawnPrioritySelectors` - A list of spawn conditions to check against the entity. Used to select a random variant to spawn in a given location.
    - `StructureCheck` - A spawn condition that checks whether the entity is within a structure.

#### Variant Datapack Registries

Frog, cat, cow, chicken, pig, and wolf, and wolf sound variants are datapack registry objects, meaning that most references now need to be referred to through the `RegistryAccess` or `HolderLookup$Provider` instance.

For a frog, cat, or wolf:

```json5
// A file located at:
// - `data/examplemod/frog_variant/example_frog.json`
// - `data/examplemod/cat_variant/example_cat.json`
// - `data/examplemod/wolf_variant/example_wolf.json`
{
    // Points to a texture at `assets/examplemod/textures/entity/cat/example_cat.png`
    "asset_id": "examplemod:entity/cat/example_cat",
    "spawn_conditions": [
        // The conditions for this variant to spawn
        {
            "priority": 0
        }
    ]
}
```

For a pig, cow, or chicken:
```json5
// A file located at:
// - `data/examplemod/pig_variant/example_pig.json`
// - `data/examplemod/cow_variant/example_cow.json`
// - `data/examplemod/chicken_variant/example_chicken.json`
{
    // Points to a texture at `assets/examplemod/textures/entity/pig/example_pig.png`
    "asset_id": "examplemod:entity/pig/example_pig",
    // Defines the `PigVariant$ModelType` that's used to select what entity model to render the pig variant with
    "model": "cold",
    "spawn_conditions": [
        // The conditions for this variant to spawn
        {
            "priority": 0
        }
    ]
}
```

For a wolf sound variant:
```json5
// A file located at:
// - `data/examplemod/wolf_sound_variant/example_wolf_sound.json``
{
    // The registry name of the sound event to play randomly on idle
    "ambient_sound": "minecraft:entity.wolf.ambient",
    // The registry name of the sound event to play when killed
    "death_sound": "minecraft:entity.wolf.death",
    // The registry name of the sound event to play randomly when angry on idle
    "growl_sound": "minecraft:entity.wolf.growl",
    // The registry name of the sound event to play when hurt
    "hurt_sound": "minecraft:entity.wolf.hurt",
    // The registry name of the sound event to play randomly
    // 1/3 of the time on idle when health is max
    "pant_sound": "minecraft:entity.wolf.pant",
    // The registry name of the sound event to play randomly
    // 1/3 of the time on idle when health is below max
    "whine_sound": "minecraft:entity.wolf.whine"
}
```

#### Client Assets

Raw `ResourceLocation`s within client-facing files for identifiers or textures are being replaced with objects defining an idenfitier along with a potential texture path. There are three main objects to be aware of: `ClientAsset`, `ModelAndTexture`, and `MaterialAssetGroup`.

`ClientAsset` is an id/texture pair used to point to a texture location. By default, the texture path is contructed from the id, with the path prefixed with `textures` and suffixed with the PNG extension.

`ModelAndTexture` is a object/client asset pair used when a renderer should select between multiple models. Usually, the renderer creates a map of the object type to the model, and the object provided to the `ModelAndTexture` is used as a lookup into the map.

`MaterialAssetGroup` is a handler for rendering an equipment asset with some trim material. It takes in the base texture used to overlay onto the armor along with any overrides for a given equipment asset.

- `net.minecraft.advancements.DisplayInfo` now takes in a `ClientAsset` instead of only a `ResourceLocation` for the background texture
- `net.minecraft.client.model`
    - `AdultAndBabyModelPair` - Holds two `Model` instances that represents the adult and baby of some entity.
    - `ChickenModel#createBaseChickenModel` - Creates the default chicken model.
    - `ColdChickenModel` - A variant model for a chicken in cold temperatures.
    - `ColdCowModel` - A variant model for a cow in cold temperatures.
    - `ColdPigModel` - A variant model for a big in cold temperatures.
    - `CowModel#createBaseCowModel` - Creates the base model for a cow.
    - `PigModel#createBasePigModel` - Creates the default pig model.
    - `WarmCowModel` - A variant model for a cow in warm temperatures.
- `net.minecraft.client.renderer.entity`
    - `ChickenRenderer` now extends `MobRenderer` instead of `AgeableMobRenderer`
    - `CowRenderer` now extends `MobRenderer` instead of `AgeableMobRenderer`
    - `PigRenderer` now extends `MobRenderer` instead of `AgeableMobRenderer`
- `net.minecraft.client.renderer.entity.layers.SheepWoolUndercoatLayer` - A layer that renders the wool undercoat of a sheep.
- `net.minecraft.client.renderer.entity.state`
    - `CowRenderState` - A render state for a cow entity.
    - `SheepRenderState`
        - `getWoolColor` - Returns the integer color of the sheep wool.
        - `isJebSheep` - Returns whether the sheep's name contains the `jeb_` prefix.
- `net.minecraft.core.ClientAsset` - An object that holds an identifier and a path to some texture.
- `net.minecraft.data.loot.EntityLootSubProvider#killedByFrogVariant` now takes in a `HolderGetter` for the `FrogVariant`
- `net.minecraft.data.tags.CatVariantTagsProvider` class is removed
- `net.minecraft.tags.CatVariantTags` class is removed
- `net.minecraft.world.entity.animal`
    - `AbstractCow` - An abstract animal that represents a cow.
    - `Chicken#setVariant`, `getVariant` - Handles the variant information of the chicken.
    - `ChickenVariant` - A class which defines the common-sideable rendering information and biome spawns of a given chicken.
    - `ChickenVariants` - Holds the keys for all vanilla chicken variants.
    - `Cow` now extends `AbstractCow`.
    - `CowVariant` - A class which defines the common-sideable rendering information and biome spawns of a given cow.
    - `CowVariants` - Holds the keys for all vanilla cow variants.
    - `CatVariant(ResourceLocation)` -> `CatVariant(ClientAsset, SpawnPrioritySelectors)`
    - `CatVariants` - Holds the keys for all vanilla cat variants.
    - `FrogVariant` -> `.frog.FrogVariant`
        - `FrogVariant(ResourceLocation)` -> `FrogVariant(ClientAsset, SpawnPrioritySelectors)`
    - `MushroomCow` now extends `AbstractCow`
    - `PigVariant` - A class which defines the common-sideable rendering information and biome spawns of a given pig.
    - `TemperatureVariants` - An interface which holds the `ResourceLocation`s that indicate an entity within a different temperature.
- `net.minecraft.world.entity.variant.ModelAndTexture` - Defines a model with its associated texture.
- `net.minecraft.world.item.equipment.trim`
    - `MaterialAssetGroup` - An asset defines some base and the permutations based on the equipment worn.
    - `TrimMaterial` now takes in a `MaterialAssetGroup` instead of the raw base and overrides

## Tags and Parsing

Tags have received a rewrite, removing any direct references to types while also sealing and finalizing related classes. Getting a value from the tag now returns an `Optional`-wrapped entry, unless you call one of the `get*Or`, where you specify the default value. Objects, on the other hand, do not take in a default, instead returning an empty variant of the desired tag.

```java
// For some `CompoundTag` tag

// Read a value
Optional<Integer> value1 = tag.getInt("value1");
int value1Raw = tag.getIntOr("value1", 0);

// Reading another object
Optional<CompoundTag> childTag = tag.getCompound("childTag");
CompoundTag childTagRaw = tag.getCopmoundOrEmpty("childTag");
```

### Writing with Codecs

`CompoundTag`s now have methods to write and read using a `Codec` or `MapCodec`. For a `Codec`, it will store the serialized data inside the key specified. For a `MapCodec`, it will merge the fields onto the top level tag.

```java
// For some Codec<ExampleObject> CODEC and MapCodec<ExampleObject> MAP_CODEC
// We will also have ExampleObject example
CompoundTag tag = new CompoundTag();

// For a codec
tag.store("example_key", CODEC, example);
Optional<ExampleObject> fromCodec = tag.read("example_key", CODEC);

// For a map codec
tag.store(MAP_CODEC, example);
Optional<ExampleObject> fromMapCodec = tag.read(MAP_CODEC);
```

### Command Parsers

The packrat parser has been updated with new rules and systems, allowing commands to have parser-based arguments. This comes from the `CommandArgumentParser`, which parses some grammar to return the desired object. The parser is then consumed by the `ParserBasedArgument`, where it attempts to parse a string and build any suggestions based on what you're currently typing. These are both handled through the `Grammar` class, which implements `CommandArgumentParser`, constructed using a combination of atoms, dictionaries, rules, and terms.

- `net.minecraft.commands.ParserUtils#parseJson`
- `net.minecraft.commands.arguments`
    - `ComponentArgument` now extends `ParserBasedArgument`
    - `NbtTagArgument` now extends `ParserBasedArgument`
    - `StyleArgument` now extends `ParserBasedArgument`
- `net.minecraft.commands.arguments.item.ItemPredicateArgument` now extends `ParserBasedArgument`
- `net.minecraft.nbt`
    - `ByteArrayTag`, now final, no longer takes in a list object
    - `ByteTag` is now a record
    - `CollectionTag` is now a sealed interface, no longer extending `AbstractList` or has a generic
        - `set`, `add` is removed
        - `remove` now returns a `Tag`
        - `get` - Returns the tag at the specified index.
        - `getElementType` is removed
        - `size` - Returns the size of the collection.
        - `isEmpty` - Returns whether the collection has no elements.
        - `stream` - Streams the elements of the collection.
    - `CompoundTag` is now final
        - `store` - Writes a codec or map codec to the tag.
        - `read` - Reads the codec or map codec-encoded value from the tag.
        - `getFloatOrDefault`, `getIntOrDefault`, `getLongOrDefault` - Gets the value with the associated key, or the default if not present or an exception is thrown.
        - `storeNullable` - When not null, uses the codec to write the value to the tag.
        - `putUUID`, `getUUID`, `hasUUID` is removed
        - `getAllKeys` -> `keySet`
        - `values`, `forEach` - Implements the standard map operations.
        - `putByteArray`, `putIntArray` with list objects are removed
        - `getTagType` is removed
        - `contains` is removed
        - `get*`, `get*Or` - Returns an optional wrapped object for the key, or the default value specified is using the `Or` methods.
    - `DoubleTag` is now a record
    - `EndTag` is now a record
    - `FloatTag` is now a record
    - `IntArrayTag`, now final, no longer takes in a list object
    - `IntTag` is now a record
    - `ListTag`, now final, extends `AbstractList`
        - `addAndUnwrap` - Adds the tag to the list where, if a compound with one element, adds the inner tag instead.
        - `get*`, `get*Or` - Returns an optional wrapped object for the key, or the default value specified is using the `Or` methods.
        - `compoundStream` - Returns a flat map of all `CompoundTag`s within the list.
    - `LongArrayTag`, now final, no longer takes in a list object
    - `LongTag` is now a record
    - `NbtIo#readUnnamedTag` is now public, visible for testing
    - `NbtOps` now has a private constructor
    - `NbtUtils`
        - `getDataVersion` now has an overload that takes in a `Dynamic`
        - `createUUID`, `loadUUID` is removed
        - `readBlockPos`, `writeBlockPos` is removed
    - `NumericTag` is now a sealed interface that implements `PrimitiveTag`
        - `getAsLong` -> `longValue`
        - `getAsInt` -> `intValue`
        - `getAsShort` -> `shortValue`
        - `getAsByte` -> `byteValue`
        - `getAsDouble` -> `doubleValue`
        - `getAsFloat` -> `floatValue`
        - `getAsNumber` -> `box`
        - `as*` - Returns an optional wrapped of the numeric value.
    - `PrimitiveTag` - A sealed interface that represents the tag data as being a primitive object.
    - `ShortTag` is now a record
    - `SnbtGrammar` - A parser creater for stringified NBTs.
    - `SnbtOperations` - A helper that contains the built in operations for parsing some value.
    - `StringTag` is now a record
    - `StringTagVisitor`
        - `visit` -> `build`, not one-to-one
        - `handleEscape` -> `handleKeyEscape`, now private
    - `Tag` is now a sealed interface
        - `as*` -> Attempts to cast the tag as one of its subtypes, returning an empty optional on failure.
        - `getAsString` -> `asString`, not one-to-one
    - `TagParser` now holds a generic referncing the type of the intermediate object to parse to
        - The constructor now takes in a grammar, or `create` constructs the grammar from a `DynamicOps`
        - `AS_CODEC` -> `FLATTENED_CODEC`
        - `parseTag` -> `parseCompoundFully` or `parseCompoundAsArgument`
            - Additional methods such as `parseFully`, `parseAsArgument` parse to some intermediary object
            - These are all instance methods
        - `readKey`, `readTypedValue` is removed
    - `TagType#isValue` is removed
- `net.minecraft.util.parsing.packrat`
    - `CachedParseState` - A parse state that caches the parsed positions and controls when reading.
    - `Control#hasCut` - Returns whether the control flow for the grammar has a cut for the reading object.
    - `DelayedException` - An interface that creates an exception to throw.
    - `Dictionary`
        - `put` now returns a `NamedRule`
        - `put(Atom<T>, Term<S>, Rule.RuleAction<S, T>)` -> `putComplex`
        - `get` -> `getOtThrow`, not one-to-one
        - `forward` - Gets or writes the term to the dictionary.
        - `namedWithAlias` - Creates a new reference to the named atom or its alias.
    - `ErrorCollector$Nop` - A error collector that does nothing.
    - `NamedRule` - A rule that has an associated name.
    - `ParseState` is now an interface
        - Caching logic has moved to `CachedParseState`
        - `scope` - Returns the current scope being analyzed within the parsing object.
        - `parse` now takes in a `NamedRule` instead of an `Atom`
        - `acquireControl`, `releaseControl` - Handles obtaining the `Control` used during parsing.
        - `silent` - Returns a `ParseState` that does not collect any errors.
    - `Rule`
        - `parse`, `$RuleAction#run` now returns a nullable value rather than an optional
        - `SimpleRuleAction` now implements `$RuleAction`
    - `Scope#pushFrame`, `popFrame`, `splitFrame`, `clearFrameValues`, `mergeFrame` - Handles the management of parsing terms into sections called frames.
    - `Term`
        - `named` -> `Dictionary#named`, not one-to-one
        - `repeated`, `repeatedWithTrailingSeparator`, `repeatedWithoutTrailingSeparator` - Handles terms similar to varargs that are repeated and sticks them into a list.
        - `positiveLookahead`, `negativeLookahead` - Handles a term that matches information based on what is following.
        - `fail` - Mark a term as having failed during parsing.
- `net.minecraft.util.parsing.packrat.commands`
    - `CommandArgumentParser` - Parses a string into an argument for use with a command.
    - `Grammar` now takes in a `NamedRule` for the top rather than an `Atom`
    - `GreedyPatternParseRule` - A rule that attempts to match the provided pattern greedily, assuming that if a region matches, that the matched group can be obtained.
    - `GreedyPredicateParseRule` - A rule that attempts to match the accepted characters greedily, making sure that the string reaches a minimum size.
    - `NumberRunParseRule` - A rule that attempts to parse a number from the string.
    - `ParserBasedArgument` - A command argument that uses a parser to extract the value.
    - `ResourceLookupRule` now takes in a `NamedRule` for the id parser rather than an `Atom`
    - `StringReaderParserState` now extends `CachedParsedState`
        - The `Dictoionary` is no longer taken in
    - `StringReaderTerms#characters` - Matches multiple characters in a string, usually for catching both the lowercase and uppercase variant.
    - `UnquotedStringParseRule` - A rule that reads part of the sequence as an unquoted string, making sure it reaches a minimum size.

## Saved Data, now with Types

`SavedData` has been reworked to abstract most of its save and loading logic into a separate `SavedDataType`. This means that the `save` override and additional `load` and `factory` methods are now handled within the `SavedDataType` itself.

To construct a `SavedDataType`, you need to pass in four paramters. First is the string identifier, used to resolve the `.dat` file holding your information. This must be a vaild path. Then there is the constructor, which takes in the `SavedData$Context` to return an instance of your data object when no information is present. Following that is the codec, which takes in the `SavedData$Context` and returns a `Codec` to read and write your saved data. Finally, there is the `DataFixTypes` used for data fixers. As this is a static enum, you will either need to inject into the enum itself, if you plan on using vanilla data fixers, or patch out the `update` call within `DimensionDataStorage#readTagFromDisk` to pass in a null value.

```java
// Our saved data instance
public class ExampleSavedData extends SavedData {

    // The saved data type
    public static final SavedDataType<ExampleSavedData> TYPE = new SavedDataType<>(
        // Best to preface the identifier with your mod id followed by an underscore
        // Slashes will throw an error as the folders are not present
        // Will resolve to `saves/<world_name>/data/examplemod_example.dat`
        "examplemod_example",
        // Constructor for the new instance
        ExampleSavedData::new,
        // Codec factory to encode and decode the data
        ctx -> RecordCodecBuilder.create(instance -> instance.group(
            RecordCodecBuilder.point(ctx.levelOrThrow()),
            Codec.INT.fieldOf("value1").forGetter(data -> data.value1),
            Codec.BOOL.fieldOf("value2").forGetter(data -> data.value2)
        ).apply(instance, ExampleSavedData::new));
    );

    private final ServerLevel level;
    private final int value1;
    private final boolean value2;


    // For the new instance
    private ExampleSavedData(ServerLevel.Context ctx) {
        this(ctx.levelOrThrow(), 0, false);
    }

    // For the codec
    // The constructors don't need to be public if not using `DimensionDataStorage#set`
    private ExampleSavedData(ServerLevel level, int value1, boolean value2) {
        this.level = level;
        this.value1 = value1;
        this.value2 = value2;
    }

    // Other methods here
}

// With access to the DimensionDataStorage storage
ExampleSavedData data = storage.computeIfAbsent(ExampleSavedData.TYPE);
```

- `net.minecraft.server.ServerScoreboard`
    - `dataFactory` is removed
    - `createData` now takes in a `$Packed` instance
- `net.minecraft.world.RandomSequences`
    - `factory`, `load` is removed
    - `codec` - Constructs a codec for the random sequence given the current world seed.
- `net.minecraft.world.entity.raid.Raids` no longer takes in anything
    - `getType` - Returns the saved data type based on the current dimension.
    - `factory` is removed
    - `tick` now takes in the `ServerLevel`
    - `getId` - Gets the identifier for the raid instance.
    - `canJoinRaid` no longer takes in the raid instance
    - `load` no longer takes in the `ServerLevel`
- `net.minecraft.world.level.levelgen.structure.structures.StructureFeatureIndexSavedData`
    - `factory`, `load` is removed
    - `type` - Returns the feature saved data type with its specified id.
- `net.minecraft.world.level.saveddata`
    - `SavedData`
        - `save` is removed
        - `$Factory` record is removed
        - `$Context` - Holds the current context that the saved data is being written to.
    - `SavedDataType` - A record that represents the type of the saved data, including information on how to construct, save, and load the data.
- `net.minecraft.world.level.saveddata.maps`
    - `MapIndex` now has a constructor to take in the last map id
        - `factory`, `load` is removed
        - `getFreeAuxValueForMap` -> `getNextMapId`
    - `MapItemSavedData`
        - `factory`, `load` is removed
        - `type` - Returns the saved data type using the map id's key.
- `net.minecraft.world.level.storage.DimensionDataStorage` now takes in a `SavedData$Context`
    - `computeIfAbsent`, `get` now take in only the `SavedDataType`
    - `set` now takes in the `SavedDataType` along with the data instance
- `net.minecraft.world.scores.ScoreboardSaveData`
    - `load` -> `loadFrom`
    - `pack` - Packs the data into its saved data format.
    - `$Packed` - Represents the serializable packed data.

## Render Pipeline Rework

Rendering an object to the screen, whether through a shader or a `RenderType`, has been fully or partially reworked, depending on what systems you were using previously. As such, a lot of things need to be reexplained, which a more in-depth look will be below. However, for the people who don't care, here's the TL;DR.

First, shader JSONs no longer exist. This is replaced by a `RenderPipeline`, which is effectively an in-code substitute. Second, the `RenderPipeline`s forcibly make most abtrarily values into objects. For example, instead of storing the blend function mode id, you store a `BlendFunction` object. Similarly, you no longer store or setup the direct texture objects, but instead manage it through a `GpuTexture`. Finally, the `VertexBuffer` can either draw to the framebuffer by directly passing in the `RenderPipeline`, updating any necessary uniforms in the consumer, or by passing in the `RenderType`.

Now, for those who need the details, let's jump into them.

### Abstracting Open GL

As many are aware, Minecraft has been abstracting away their OpenGL calls and constants, and this release is no different. All of the calls to GL codes, except `BufferUsage`, have been moved out of object references, to be obtained typically by calling `GlConst$toGl`. However, with all of the other rendering reworks, there are numerous changes and complexities that require learning an entirely new system, assuming you're not using `RenderType`s.

Starting from the top, all calls to the underlying render system goes through `GpuDevice`, an interface that acts like a general implementation of a render library like OpenGL or Vulkan. The device is responsible for creating buffers and textures, executing whatever commands are desired. Getting the current `GpuDevice` can be accessed through the `RenderSystem` via `getDevice` like so:

```java
GpuDevice device = RenderSystem.getDevice();
```

The `GpuDevice` can the create either buffers with the desired data or a texture containing information on what to render using `createBuffer` and `createTexture`, respectively. Just for redundancy, buffers hold the vertex data while textures hold the texture (color and depth) data. You should generally cache the buffer or texture object for later use with any additional data updated as needed. For reference, buffers are typically created by using a `BufferBuilder` with a `ByteBufferBuilder` to build the `MeshData` first, before passing that into `createBuffer`.

With the desired buffers and textures set up, how do we actually modify render them to the screen? Well, this is handled through the `CommandEncoder`, which can also be optained from the device via `GpuDevice#createCommandEncoder`. The encoder contain the familiar read and write methods along with a few extra to clear texture to a given color or simply blit the the texture immediately to the screen (`presentTexture`). However, the most important method here is `createRenderPass`. This takes in the `GpuTexture` to draw to the screen along with a default ARGB color for the background. Additionally, it can take in a depth texture as well. This should be created using a try with resources block like so:

```java
// We will assume you have constructed a `GpuTexture` texture for the color data
try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(this.texture, OptionalInt.of(0xFFFFFFFF))) {
    // Setup things here

}
```

Within the `RenderPass`, you can set the `RenderPipeline` to use, which defines the associated shaders, bind any samplers from other targets or set uniforms, scissor a part of a screen to render, and set the vertex and index buffers used to define the vertices to render. Finally, everything can be drawn to the screen using one of the `draw` methods, providing the starting index and the vertex count.

```java
// If the buffers/textures are not created or cached, create them here
// Any methods ran from `CommandEncoder` cannot be run while a render pass is open
RenderSystem.AutoStorageIndexBuffer indices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
GpuBuffer vertexBuffer = RenderSystem.getQuadVertexBuffer();
GpuBuffer indexBuffer = indices.getBuffer(6);

// We will assume you have constructed a `GpuTexture` texture for the color data
try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(this.texture, OptionalInt.of(0xFFFFFFFF))) {

    // Set pipeline information along with any samplers and uniforms
    pass.setPipeline(EXAMPLE_PIPELINE);
    pass.setVertexBuffer(0, vertexBuffer);
    pass.setIndexBuffer(indexBuffer, indices.type());
    pass.bindSampler("Sampler0", RenderSystem.getShaderTexture(0));

    // Then, draw everything to the screen
    // In this example, the buffer just contains a single quad
    // For those unaware, the vertex count is 6 as a quad is made up of 2 triangles, so 2 vertices overlap
    pass.drawIndexed(0, 6);
}
```

However, unless you need such fine control, it is recommended to use a `RenderType` with the `MultiBufferSource` when necessary as that sets up most things for you.

### Object References

Most raw references to GL codes used for determining the mode and handling the texture have been replaced with objects. As the TL;DR previously mentioned, these are stored typically as some sort of enum or object that can then be resolved to their GL counterparts. Some objects contain their reference identifier directly, like `BlendFunction`. Others are simply placeholders that are resolved in their appropriate location, like `DepthTestFunction` whose enum values are converted via `RenderPipeline#toGl`.

However, the biggest change is the addition of the `GpuTexture`. This is responsible for managing anything to do with creating, writing, and releasing a texture written to some buffer. At initialization, the texture is created and bound, with any necessary parameters set for mipmaps and texture formats. These `GpuTexture`s are stored and referenced everywhere, from the depth and color targets for a `RenderTarget` to the texture backing a `TextureAtlas`. Then, once no longer need, the texture is released by calling `#close`. Note that although technically `#bind` can be called again, the texture is already considered deleted and should not be used.

If, for some reason, you need to use a `GpuTexture`, it's actually quite simple to use. First, you just construct the `GpuTexture` via `GpuDevice#createTexture`. Then, if you need to change any of the addressing or texture mipmap filters, you can apply them whenever before writing.

```java
public class MyTextureManager {
    
    private final GpuTexture texture;

    public MyTextureManager() {
        this.texture = RenderSystem.getDevice().createTexture(
            // The texture name, used for logging and debugging
            "Example Texture",
            // The format of the texture pixels, can be one of three values that
            // Values:   (texture internal format, texel data format,  texel data type,  pixel size)
            // - RGBA8   (GL_RGBA8,                GL_RGBA,            GL_UNSIGNED_BYTE, 4)
            // - RED8    (GL_R8,                   GL_RED,             GL_UNSIGNED_BYTE, 1)
            // - DEPTH32 (GL_DEPTH_COMPONENT32,    GL_DEPTH_COMPONENT, GL_FLOAT,         4)
            TextureFormat.RGBA8,
            // Width of the texture
            16,
            // Height of the texture
            16,
            // The mipmap level and maximum level-of-detail (minimum of 1)
            1
        );

        // Set the texture mode for the UV component
        // Values:
        // - REPEAT        (GL_REPEAT)
        // - CLAMP_TO_EDGE (GL_CLAMP_TO_EDGE)
        this.texture.setAddressMode(
            // The mode to use for the U component (GL_TEXTURE_WRAP_S)
            AddressMode.CLAMP_TO_EDGE,
            // The mode to use for the V component (GL_TEXTURE_WRAP_R)
            AddressMode.REPEAT
        );

        // Sets the filter functions used for scaling the texture on the screen
        // Values    (default,    for mipmaps):
        // - NEAREST (GL_NEAREST, GL_NEAREST_MIPMAP_LINEAR)
        // - LINEAR  (GL_LINEAR,  GL_LINEAR_MIPMAP_LINEAR)
        this.texture.setTextureFilter(
            // The mode to use for the texture minifying function (GL_TEXTURE_MIN_FILTER)
            FilterMode.LINEAR,
            // The mode to use for the texture magnifying function (GL_TEXTURE_MAG_FILTER)
            FilterMode.NEAREST,
            // Whether mipmaps should be used for the minifying function (should have a higher mipmap level than 1 when true)
            false
        );
    }
}
```

Then, whenever you want to upload something to the texture, you call `CommandEncoder#writeToTexture` or `CommandEncoder#copyTextureToTexture`. This either takes in the `NativeImage` to write from or an `IntBuffer` with the texture data and a `NativeImage$Format` to use.

```java
// Like other buffer/texture modification methods, this must be done outside of a render pass
// We will assume you have some `NativeImage` image to load into the texture
RenderSystem.getDevice().createCommandEncoder().writeToTexture(
    // The texture (destination) being written to
    this.texture,
    // The image (source) being read from
    image,
    // The mipmap level
    0,
    // The starting destination x offset
    0,
    // The starting destination y offset
    0,
    // The destination width (x size)
    16,
    // The desintation height (y size)
    16,
    // The starting source x offset
    0,
    // The starting source y offset
    0
)
```

Finally, once you're done with the texture, don't forget to release it via `#close` if it's not already handled for you.

### Render Pipelines

Previously, a pipeline was constructed using a JSON that contained all metadata from the vertex and fragement shader to their defined values, samplers, and uniforms. However, this has all been replaced with an in-code solution that more localizes some parts of the JSON and some parts that were relegated to the `RenderType`. This is known as a `RenderPipeline`.

A `RenderPipeline` can be constructed using its builder via `RenderPipeline#builder`. A pipeline can then be built by calling `build`. If you want the shader to be pre-compiled without any additional work, the final pipeline can be passed to `RenderPipeline#register`. However, you can also handle the compilation yourself if more graceful fail states are desired. If you have snippets that are used across multiple pipelines, then a partial pipeline can be built via `$Builder#buildSnippet` and then passed to the constructing pipelines in the `builder` method.

> The following enums described in the examples have their GL codes provided with them as they have been abstracted away.

```java
// This assumes that RenderPipeline#register has been made public through some form
public static final RenderPipeline EXAMPLE_PIPELINE = RenderPipelines.register(
    RenderPipeline.builder()
    // The name of the pipeline (required)
    .withLocation(ResourceLocation.fromNamespaceAndPath("examplemod", "pipeline/example"))
    // The location of the vertex shader, relative to 'shaders' (required)
    // Points to 'assets/examplemod/shaders/example.vsh'
    .withVertexShader(ResourceLocation.fromNamespaceAndPath("examplemod", "example"))
    // The location of the fragment shader, relative to 'shaders' (required)
    // Points to 'assets/examplemod/shaders/example.fsh'
    .withFragmentShader(ResourceLocation.fromNamespaceAndPath("examplemod", "example"))
    // The format of the vertices within the shader (required)
    .withVertexFormat(
        // The vertex format
        DefaultVertexFormat.POSITION_TEX_COLOR,
        // The mode of the format
        VertexFormat.Mode.QUADS
    )
    // Adds constants that can be referenced within the shaders
    // Can specify a name in addition to an int / float to represent its value
    // If no value is specified, then it should be gated with a #ifdef / #endif block
    .withShaderDefines("ALPHA_CUTOUT", 0.5)
    // Adds the texture sampler2Ds that can be referenced within the shaders
    // Typically, the shader textures stored in the `RenderSystem` is referenced via `Sampler0` - `Sampler11`
    // - `Sampler0` is usually always present, but these should be set up beforehand
    // Additionally, for render targets, `InSampler` is typically present, along with any defined in a postpass
    .withSampler("Sampler0")
    // Adds uniforms that can be referenced within the shaders
    // These are just definitions which are then populated by default or by the caller depending on the scenario
    // Defaults can be found in `CompiledShaderProgram#setupUniforms`
    .withUniform("ModelOffset", UniformType.VEC3)
    // Custom uniforms must be set manually as the vanilla batching system does not support such an operation
    .withUniform("CustomUniform", UniformType.INT)
    // Sets the depth test functions used rendering objects at varying distances from the camera
    // Values:
    // - NO_DEPTH_TEST      (GL_ALWAYS)
    // - EQUAL_DEPTH_TEST   (GL_EQUAL)
    // - LEQUAL_DEPTH_TEST  (GL_LEQUAL)
    // - LESS_DEPTH_TEST    (GL_LESS)
    // - GREATER_DEPTH_TEST (GL_GREATER)
    .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
    // Sets how the polygons should render
    // Values:
    // - FILL      (GL_FILL)
    // - WIREFRAME (GL_LINE)
    .withPolygonMode(PolygonMode.FILL)
    // When true, can cull front or back-facing polygons
    .withCull(false)
    // Specifies the functions to use when blending two colors with alphas together
    // Made up of the `GlStateManager$SourceFactor` and `GlStateManager$DestFactor`
    // First two are for RGB, the last two are for alphas
    // If nothing is specified, then blending is disabled
    .withBlend(BlendFunction.TRANSLUCENT)
    // Determines whether to mask writing colors and alpha to the draw buffer
    .withColorWrite(
        // Mask RGB
        false,
        // Mask alpha
        false
    )
    // Determines whether to mask writing values to the depth buffer
    .withDepthWrite(false)
    // Determines the logical operation to apply when applying an RGBA color to the framebuffer
    .withColorLogic(LogicOp.NONE)
    // Sets the scale and units used to calculate the depth values for the polygon.
    // This takes the place of the polygon offset.
    .withDepthBias(0f, 0f)
    .build()
);
```

From there, the pipeline can either be used directly or through some `RenderType`:

```java
// This will assume that RenderType#create is made public
public static final RenderType EXAMPLE_RENDER_TYPE = RenderType.create(
    // The name of the render type
    "examplemod:example",
    // The size of the buffer
    // Or 4MB
    4194304,
    // Whether it effects crumbling that is applied to block entities
    false,
    // Whether the vertices should be sorted before upload
    true,
    // The pipeline to use
    EXAMPLE_PIPIELINE,
    // Any additional composite state settings to apply
    RenderType.CompositeState.builder().createCompositeState(RenderType.OutlineProperty.NONE)
);
```

The pipeline can then be drawn by creating the `RenderPass` and setting the `RenderPipeline` to use your pipeline. As for the `RenderType`, the associated buffer can be obtained using  `MultiBufferSource#getBuffer`. Note that custom uniforms should not be used within `RenderType`s as they cannot be set easily.

```java
// Since we are using a custom uniform, we must handle it ourselves
// We will assume we have some `GpuTexture` texture to write to

// Create the render pass to use
try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
        // The GPU color texture to write to
        this.texture,
        // The clear color in ARGB format
        OptionalInt.of(0xFFFFFFFF),
        // The depth texture and the clear depth value can also be constructed here
    )
) {
    // Add the pipeline and our uniform
    pass.setPipeline(EXAMPLE_PIPELINE);
    pass.setUniform("CustomUniform", 1);
    
    // Set any additional sampler and the vertex/index buffers to use

    // Finally, call one of the draw functions
    // Takes in the first index and the index count to draw for the vertices
    pass.draw(...);
}
```

### Post Effects

Given that the pipeline JSONs have been stripped, this also effects the post effects. The `program` is replaced with directly specifying the `vertex_shader` and the `fragment_shader`. Additionally, uniforms must specify their `type`.

```json5
// Before 1.21.5 (for some pass in 'passes')
{
    // Same as before
    "inputs": [ /*...*/ ],
    "output": "swap",

    // Replaced by 'vertex_shader', 'fragement_shader'
    "program": "minecraft:post/box_blur",

    "uniforms": [
        {
            "name": "BlurDir",
            // Required
            "values": [ 1.0, 0.0 ]
        },
        {
            "name": "Radius",
            // Required
            "values": [ 0.0 ]
        }
    ]
}

// 1.21.1 (for some pass in 'passes')
{
    // Same as before
    "inputs": [ /*...*/ ],
    "output": "swap",

    // Relative to 'shaders'
    // Points to 'assets/minecraft/shaders/post/blur.vsh'
    "vertex_shader": "minecraft:post/blur",
    // Points to 'assets/minecraft/shaders/post/box_blur.fsh'
    "fragment_shader": "minecraft:post/box_blur",


    "uniforms": [
        {
            "name": "BlurDir",
            // Specifies the type to use for this uniform
            // One of `Uniform$Type`:
            // - int
            // - ivec3
            // - float
            // - vec2
            // - vec3
            // - vec4
            // - matrix4x4
            "type": "vec2",
            "values": [ 1.0, 0.0 ]
        },
        {
            "name": "Radius",
            "type": "float"
            // Values are no longer required
        }
    ]
}
```

Note that if you do not define a value for a uniform, they still must be specified before processing the `PostChain` by calling `#setUniform` within the `RenderPass` consumer of `PostChain#process`.

```java
// Assume we already got the `PostChain` post
post.process(Minecraft.getInstance().getMainRenderTarget(), GraphicsResourceAllocator.UNPOOLED, pass -> {
    pass.setUniform("Radius", 0.4f);
});
```

- `com.mojang.blaze3d.GpuOutOfMemoryException` - An exception thrown when a texture could not be allocated on the GPU.
- `com.mojang.blaze3d.buffers`
    - `BufferType` no longer stores the GL codes, now in `GlConst#toGl`
    - `BufferUsage` no longer stores the GL codes, now in `GlConst#toGl`
        - `isReadable`, `isWritable` - Returns whether the buffer can be read from or written to.
    - `GpuBuffer` is now abstract
        - Constructor with `ByteBuffer` is removed
        - `size` - Returns the size of the buffer.
        - `type` -  Returns the type of the buffer.
        - `resize`, `write`, `read`, `bind` is removed
        - `usage` - Returns the usage of the buffer.
        - `close` is now abstract
        - `isClosed` - Returns whether the buffer has been closed.
        - `$ReadView` is now an interface that defines the buffer data and how to close the view
- `com.mojang.blaze3d.font.SheetGlyphInfo#upload` now takes in a `GpuTexture`
- `com.mojang.blaze3d.opengl`
    - `DirectStateAccess` - An interface that creates and binds data to some framebuffer.
        - `$Core` - An implementation of DSA that modifies the framebuffer without binding them to the context.
        - `$Emulated` - An abstraction over DSA that still binds the context.
    - `GlBuffer` - An implementation of the `GpuBuffer` for OpenGL.
    - `GlCommandEncoder` - An implementation of the `CommandEncoder` for OpenGL.
    - `GlDebugLabel` - A labeler for handling debug references to GL-specified data structures.
    - `GlDevice` - An implementation of `GpuDevice` for OpenGL.
    - `GlRenderPass` - An implementation of `RenderPass` for OpenGL.
    - `GlRenderPipeline` - An implementation of `CompiledRenderPipeline` for OpenGL.
    - `GlTexture` - An implementation of `GpuTexture` for OpenGL.
    - `VertexArrayCache` - A cache for binding and uploading a vertex array to the OpenGL pipeline.
- `com.mojang.blaze3d.pipeline`
    - `BlendFunction` - A class that holds the source and destination colors and alphas to apply when overlaying pixels in a target. This also holds all vanilla blend functions.
    - `CompiledRenderPipeline` - An interface that holds the pipeline with all necessary information to render to the screen.
    - `RenderPipeline` - A class that contains everything required to render some object to the screen. It acts similarly to a render state before being applied.
    - `RenderTarget` now takes in a string representing the name of the target
        - `colorTextureId` -> `colorTexture`, now a `GpuTexture`
            - Same with `getColorTextureId` -> `getColorTexture`
        - `depthBufferId` -> `depthTexture`, now a `GpuTexture`
            - Same with `getDepthTextureId` -> `getDepthTexture`
        - `filterMode` is now a `FilterMode`
            - Same with `setFilterMode` for the int parameter
        - `blitAndBlendToScreen` no longer takes in the viewport size parameters
        - `framebufferId` is removed
        - `checkStatus` is removed
        - `bindWrite`, `unbindWrite`, `setClearColor` is removed
        - `blitToScreen` no longer takes in any parameters
        - `blitAndBlendToScreen` -> `blitAndBlendToTexture`, not one-to-one
        - `clear` is removed
        - `unbindRead` is removed
- `com.mojang.blaze3d.platform`
    - `DepthTestFunction` - An enum representing the supported depth tests to apply when rendering a sample to the framebuffer.
    - `DisplayData` is now a record
        - `withSize` - Creates a new instance with the specified width/height.
        - `withFullscreen` - Creates a new instance with the specified fullscreen flag.
    - `FramerateLimitTracker`
        - `getThrottleReason` - Returns the reason that the framerate of the game was throttled.
        - `isHeavilyThrottled` - Returns whether the current throttle significantly impacts the game speed.
        - `$FramerateThrottleReason` - The reason the framerate is throttled.
    - `GlConst` -> `com.mojang.blaze3d.opengl.GlConst`
        - `#toGl` - Maps some reference object to its associated OpenGL code.
    - `GlDebug` -> `com.mojang.blaze3d.opengl.GlDebug`
        - `enableDebugCallback` now takes in a set of the enabled extensions.
    - `GlStateManager` -> `com.mojang.blaze3d.opengl.GlStateManager`
        - `_blendFunc`, `_blendEquation` is removed
        - `_glUniform2(int, IntBuffer)`, `_glUniform4(int, IntBuffer)` is removed
        - `_glUniformMatrix2(int, boolean, FloatBuffer)`, `_glUniformMatrix3(int, boolean, FloatBuffer)` is removed
        - `_glUniformMatrix4(int, boolean, FloatBuffer)` -> `_glUniformMatrix4(int, FloatBuffer)`, transpose is now always false
        - `_glGetAttribLocation` is removed
        - `_glMapBuffer` is removed
        - `_glCopyTexSubImage2D` is removed
        - `_glBindRenderbuffer`, `_glDeleteRenderbuffers` is removed
        - `glGenRenderbuffers`, `_glRenderbufferStorage`, `_glFramebufferRenderbuffer` is removed
        - `_texParameter(int, int, float)` is removed
        - `_genTextures`, `_deleteTextures` is removed
        - `_texSubImage2D` now has an overload that takes in an `IntBuffer` instead of a `long` for the pixel data
        - `upload` is removed
        - `_stencilFunc`, `_stencilMask`, `_stencilOp`, `_clearStencil` is removed
        - `_getTexImage` is removed
        - `_glDrawPixels`, `_readPixels` is removed
        - `$CullState#mode` is removed
        - `$DestFactor` -> `DestFactor`, codes are removed to be called through `GlConst#toGl`
        - `$FramebufferState` enum is removed
        - `$LogicOp` -> `LogicOp`, codes are removed to be called through `GlConst#toGl`
            - All but `OR_REVERSE` is removed
            - `NONE` - Performs no logic operation.
        - `$PolygonOffsetState#line` is removed
        - `$SourceFactor` -> `SourceFactor`, codes are removed to be called through `GlConst#toGl`
        - `$StencilFunc`, `$StencilState` class is removed
        - `$Viewport` enum is removed
    - `GlUtil` class is removed
        - `getVendor`, `getRenderer`, `getOpenGlVersion` (now `getVersion`) have been moved to instance abstract methods on `GpuDevice`
        - `getCpuInfo` -> `GLX#_getCpuInfo`
    - `GLX`
        - `getOpenGLVersionString` is removed
        - `_init` -> `_getCpuInfo`, not one-to-one
        - `_renderCrosshair`, `com.mojang.blaze3d.systems.RenderSystem#renderCrosshair` -> `net.minecraft.client.gui.components.DebugScreenOverlay#render3dCrosshair`, not one-to-one
    - `PolygonMode` - A enum that defines how the polygons will render in the buffer.
    - `NativeImage` constructor is now public
        - `upload` is removed
        - `getPointer` - Returns the pointer to the image data.
        - `setPixelABGR` is now public
        - `applyToAllPixels` is removed
        - `downloadTexture`, `downloadDepthBuffer` is removed
        - `flipY` is removed
        - `setPackPixelStoreState`, `setUnpackPixelStoreState` is removed
        - `$InternalGlFormat` enum is removed
        - `$Format` no longer contains the GL codes, now in `GlConst#toGl`
    - `TextureUtil`
        - `generateTextureId`, `releaseTextureId` is removed
        - `prepareImage` is removed
        - `writeAsPNG` now takes in a `GpuTexture` instead of the direct three integers
            - The overload without the `IntUnaryOperator` is removed
- `com.mojang.blaze3d.resource`
    - `RenderTargetDescriptor` now takes in an integer representing the color to clear to
    - `ResourceDescriptor`
        - `prepare` - Prepares the resource for use after allocation.
        - `canUsePhysicalResource` - Typically returns whether a descriptor is already allocated with the same information.
- `com.mojang.blaze3d.shaders`
    - `AbstractUniform` -> `com.mojang.blaze3d.opengl.AbstractUniform`
        - `setSafe` methods are removed
        - `setMat*` methods are removed
        - `set(Matrix3f)` is removed
    - `CompiledShader` -> `com.mojang.blaze3d.opengl.GlShaderModule`
        - `$Type` -> `com.mojang.blaze3d.shaders.ShaderType`
    - `Uniform` -> `com.mojang.blaze3d.opengl.Uniform`
        - Constructor now takes in a `$Type` instead of the count and an integer representing the type
        - `UT_*` fields are removed
        - `setFromConfig(ShaderProgramConfig.Uniform)` is removed
        - `getTypeFromString` is removed
        - `getType` now returns a `$Type`
        - `set(int, float)` is removed
        - `setSafe` is now private
        - `$Type` - Holds the type name as well as how many values it holds.
        - `getLocation` is removed
        - `getCount` -> `$Type#count`
        - `getIntBuffer`, `getFloatBuffer` is removed
        - `$Type` -> `com.mojang.blaze3d.shaders.UniformType`
- `com.mojang.blaze3d.systems`
    - `CommandEncoder` - An interface that defines how to encode various commands to the underlying render system, such as creating a pass, clearing and writing textures, or reading from the buffer.
    - `GpuDevice` - An interface that defines the device or underlying render system used to draw to the screen. This is responsible for creating the buffers and textures while compiling any pipelines.
    - `RenderPass` - An interface that defines how a given pass is rendered to some buffer using the underlying render system. This allows binding any samplers and setting the required uniforms.
    - `RenderSystem`
        - `isOnRenderThreadOrInit`, `assertOnRenderThreadOrInit` is removed
        - `recordRenderCall`, `replayQueue` is removed
        - `blendFunc`, `blendFuncSeparate`, `blendEquation` is removed
        - `texParameter`, `deleteTexture`, `bindTextureForSetup` is removed
        - `stencilFunc`, `stencilMask`, `stencilOp` is removed
        - `clearDepth` is removed
        - `glBindBuffer`, `glBindVertexArray`, `glBufferData`, `glDeleteBuffers` is removed
        - `glUniform1i` is removed
        - `glUniform1`, `glUniform2`, `glUniform3`, `glUniform4` is removed
        - `glUniformMatrix2`, `glUniformMatrix3`, `glUniformMatrix4` is removed
        - `setupOverlayColor` now takes in a `GpuTexture` instead of two ints
        - `beginInitialization`, `finishInitialization` is removed
        - `renderThreadTesselator` is removed
        - `setShader`, `clearShader`, `getShader` is removed
        - `setShaderTexture` now takes in a `GpuTexture` instead of a bind address
        - `getShaderTexture` now returns a `GpuTexture` or null if not present
        - `pixelStore`, `readPixels` is removed
        - `queueFencedTask`, `executePendingTasks` - Handles sending tasks that run on the GPU asyncronously.
        - `SCISSOR_STATE` - Holds the main scissor state.
        - `disableDepthTest`, `enableDepthTest` is removed
        - `depthFunc`, `depthMask` is removed
        - `enableBlend`, `disableBlend` is removed
        - `neableCull`, `disableCull` is removed
        - `polygonMode`, `enablePolygonOffset`, `disablePolygonOffset`, `polygonOffset` is removed
        - `enableColorLogicOp`, `disableColorLogicOp`, `logicOp` is removed
        - `bindTexture`, `viewport` is removed
        - `colorMask`, `clearColor`, `clear` is removed
        - `setupShaderLights(CompiledShaderProgram)` is removed
        - `getShaderLights` - Returns the vectors representing the block and sky lights.
        - `drawElements`, `getString` is removed
        - `initRenderer` now takes in the window pointer, the default shader source, and a boolean of whether to use debug labels
        - `setupDefaultState` no longer takes in any parameters
        - `maxSupportTextureSize` is removed
        - `glDeleteVertexArrays` is removed
        - `defaultBlendFunc` is removed
        - `setShaderTexture` is removed
        - `getQuadVertexBuffer` - Returns a vertex buffer with a quad bound to it.
        - `getDevice`, `tryGetDevice` - Returns the `GpuDevice` representing the underlying render system to use.
        - `getCapsString` is removed
        - `activeTexture` is removed
        - `setModelOffset`, `resetModelOffset`, `getModelOffset` - Handles the offset to apply to a model when rendering for the uniform `ModelOffset`. Typically for clouds and world borders.
        - `$AutoStorageIndexBuffer#bind` -> `getBuffer`, not one-to-one
        - `$GpuAsyncTask` - A record that holds the callback and fence object used to sync information to the GPU.
    - `ScissorState` - A class which holds the part of the screen to render.
- `com.mojang.blaze3d.textures`
    - `AddressMode` - The mode set for how to render a texture to a specific location.
    - `FilterMode` - The mode set for how to render a texture whenever the level-of-detail function determines how the texture should be maximized or minimized.
    - `GpuTexture` - A texture that is bound and written to the GPU as required.
    - `TextureFormat` - Specifies the format that the texture should be allocated with.
- `com.mojang.blaze3d.vertex`
    - `PoseStack`
        - `mulPose(Quaternionf)`, `rotateAround` now takes in a `Quaternionfc` instead of a `Quaternionf`
        - `clear` -> `isEmpty`
        - `mulPose(Matrix4f)` -> `mulPose(Matrix4fc)`
        - `$Pose`
            - `computeNormalMatrix` is now private
            - `transformNormal` now takes in a `Vector3fc` as its first parameter
            - `translate`, `scale`, `rotate`, `rotateAround`, `setIdentity`, `mulPose` are now available on the pose itself in addition to the stack
    - `VertexBuffer` -> `com.mojang.blaze3d.buffers.GpuBuffer`, not one-to-one
        - Some logic is also moved to `VertexFormat`
    - `VertexFormat`
        - `bindAttributes` is removed
        - `setupBufferState`, `clearBufferState`, `getImmediateDrawVertexBuffer` -> `uploadImmediateVertexBuffer`, `uploadImmediateIndexBuffer`; not one-to-one
        - `$IndexType` no longer stores the GL codes, now in `GlConst#toGl`
        - `$Mode` no longer stores the GL codes, now in `GlConst#toGl`
    - `VertexFormatElement`
        - `setupBufferState` is removed
        - `$Type` no longer stores the GL codes, now in `GlConst#toGl`
        - `$Usage` no longer stores the GL function calls, now in `VertexArrayCache#setupCombinedAttributes`
- `com.mojang.math`
    - `MatrixUtil`
        - `isIdentity`, `isPureTranslation`, `isOrthonormal` now take in a `Matrix4fc`
        - `checkProperty` - Checks if the provided property is represented within the matrix.
    - `OctahedralGroup`
        - `transformation` now returns a `Matrix3fc`
        - `fromAnges` -> `fromXYAngles`, not one-to-one
    - `Quadrant` - An enum that contains rotations in 90 degree increments.
    - `SymmetricGroup3#transformation` now returns a `Matrix3fc`
    - `Transformation` now takes in a `Matrix4fc`
        - `getMatrix` now returns a `Matrix4fc`
        - `getMatrixCopy` - Returns a deep copy of the current matrix.
- `net.minecraft.client.gui.font.FontTexture` now takes in a supplied label string
- `net.minecraft.client.main.GameConfig` now takes in a boolean representing whether to render debug labels
- `net.minecraft.client.renderer`
    - `CloudRenderer#render` no longer takes in the `Matrix4f`s used for projection or posing
    - `CompiledShaderProgram` -> `com.mojang.blaze3d.opengl.GlProgram`
        - `link` now takes in a string for the shader name
        - `setupUniforms` now take in the list of `$UniformDescription`s along with a list of names used by the samplers
        - `getUniformConfig` is removed
        - `bindSampler` now takes in a `GpuTexture` instead of the integer bind identifier
        - `parseUniformNode` is removed
    - `CoreShaders` -> `RenderPipelines`, not one-to-one
    - `LightTexture#getTarget` - Returns the `GpuTexture` that contains the light texture for the current level based on the player.
    - `PostChain`
        - `load` no longer takes in the `ShaderManager`, now taking in a `ResourceLocation` representing the name of the chain
        - `addToFrame`, `process` now takes in a `RenderPass` consumer to apply any additional settings to the pass to render
        - `setUniform` is removed
        - `setOnRenderPass` - Sets the uniform within the post chain on the `RenderPass` for use in the shaders.
    - `PostChainConfig`
        - `$Pass` now takes in the ids of the vertex and fragment shader instead of the program id
            - `referencedTargets` - Returns the targets referenced in the pass to apply.
            - `program` is removed
        - `$Uniform` now takes in the type of the uniform along with an optional list of floats if the value does not need to be overridden
    - `PostPass` no longer takes in the `CompiledShaderProgram`, now taking in the `RenderPipeline` instead of a string representing the name of the pass
         - `addToFrame` now takes in a `RenderPass` consumer to apply any additional settings to the pass to render
         - `getShader` is removed
         - `$Input#bindTo` now takes in a `RenderPass` instead of the `CompiledShaderProgram`
    - `RenderStateShard`
        - `$LayerStateShard`s using polygon offsets have been removed
        - `getName` - Returns the name of the shard.
        - `$TransparencyStateShard` class is removed
            - Now handled through `BlendFunction`
        - `$ShaderStateShard` class is removed
            - Directly referred to by the `VertexBuffer`
        - `$CullStateShard` class is removed
            - Now handled as a setting on the `RenderPipeline`
        - `$DepthTestStateShard` class is removed
            - Now handled through `DepthTestFunction`
        - `$WriteMaskStateShard` class is removed
            - Now handled as a setting on the `RenderPipeline`
        - `$ColorLogicStateShard` class is removed
            - Now handled as a setting on the `RenderPipeline`
        - `$OutputStateShard` now takes in a supplied `RenderTarget` instead of the runnables for the startup and teardown states
    - `RenderType` no longer takes in the `VertexFormat` or `VertexFormat$Mode`
        - `SKY`, `END_SKY`, `sky`, `endSky`, `stars` is removed
        - `ENTITY_OUTLINE_BLIT`, `entityOutlineBlit` is removed
        - `PANORAMA`, `panorama` is removed
        - `CREATE_LIGHTMAP`, `createLightmap` is removed
        - `createClouds`, `flatClouds`, `clouds`, `cloudsDepthOnly` is removed
        - `worldBorder` is removed
        - `debugLine` - Returns the `RenderType` associated with the debug line.
        - `entityOutlineBlit` - Returns the `RenderType` used for rendering an entity outline.
        - `panorama` - Returns the `RenderType` used for rendering panorama mode.
        - `createLightmap` - Returns the `RenderType` used for rendering the lightmap texture.
        - `create` no longer takes in the `VertexFormat` or `VertexFormat$Mode`, instead the `RenderPipeline`
        - `getRenderTarget`, `getRenderPipeline` - Returns the target and pipeline used for rendering.
        - `format`, `mode`, `draw` are now abstract
        - `$CompositeStateBuilder` methods are now protected
        - `$OutlineProperty` is now protected
    - `ShaderDefines$Builder#define` now has an overload that takes in an integer
    - `ShaderManager`
        - `SHADER_INCLUDE_PATH` is now private
        - `MAX_LOG_LENGTH` is removed
        - `preloadForStartup` is removed, replaced by `GpuDevice#precompilePipeline`
        - `getProgram`, `getProgramForLoading` -> `getShader`, not one-to-one
        - `linkProgram` now takes in a `RenderPipeline` instead of a `ShaderProgram` and `ShaderProgramConfig`
        - `$CompilationCache#getOrCompileProgram`, `getOrCompileShader` -> `getShaderSource`, not one-to-one
        - `$Configs` no longer takes in the map of programs
        - `$ShaderCompilationKey` record is removed
    - `ShaderProgram`, `ShaderProgramConfig` -> `RenderPipeline`, not one-to-one
    - `SkyRenderer#renderDarkDisc` no longer takes in the `PoseStack`
- `net.minecraft.client.renderer.chunk.SectionRenderDispatcher`
    - `uploadSectionLayer`, `uploadSectionIndexBuffer` -> `$RenderSection#uploadSectionLayer`, `uploadSectionIndexBuffer`
    - `$SectionBuffers` - A class that holds the buffers used to render the sections.
- `net.minecraft.client.renderer.texture`
    - `AbstractTexture`
        - `NOT_ASSIGNED` is removed
        - `texture`, `getTexture` - Holds the reference to the texture to render.
        - `getId`, `releaseId` is removed
        - `bind` is removed
    - `DynamicTexture` now takes in the label of the texture
    - `SpriteContents#uploadFirstFrame`, `$AnimatedTexture#uploadFirstFrame` now takes in a `GpuTexture`
    - `SpriteTicker#tickAndUpload` now takes in the `GpuTexture`
    - `TextureAtlasSprite#uploadFirstFrame`, `$Ticker#tickAndUpload` now takes in the `GpuTexture`

## Model Rework

The model system has been further separated into models for block states, blocks, and items. As such, the unifying `BakedModel` has been completely removed and separated into their own  sections, loaded in three steps: from JSON, resolving dependencies, and then baking for use with the associated block state model or item model. For reference, everything discussed below is what's happenening within `ModelManager#reload` in parallel.

First, let's start from the base model JSON used between blocks and items. These are loaded into an `UnbakedModel` (specifically `BlockModel`) which contains the familiar properties such as gui light and texture slots. However, one change is the splitting of the elements from their render settings. These elements that hold the render quads are stored in an `UnbakedGeometry`. The `UnbakedGeometry` is responsible for baking the model into a `QuadCollection`, which effectively holds the list of `BakedQuad`s to render. Currently, vanilla only has the `SimpleUnbakedGeometry`, which holds the familiar list of `BlockElement`s. These `UnbakedModel`, once loaded, are then passed to the `ModelDiscovery` for resolving the block state and item models.

Next we have the `ResolvableModel`s, which is the base of both block state and item models. These models essentially function as markers requesting the `UnbakedModel`s that they will be using. From there, we have their subtypes `BlockStateModel$UnbakedRoot` for the block state JSON and `ItemModel$Unbaked` for the model referenced in the client item JSON. Each of these implement `resolveDependencies` in some way to call `ResolvableModel$Resolver#markDependency` with the model location they would like to use.

> Technically, `BlockStateModel`s are a bit more complex as variants use `BlockStateModel$Unbaked` during loading which are then transformed into an `$UnbakedRoot` during initialization.

Now that we know what models that should be loaded, they now have to be put into a usable state for baking. This is the job of the `ModelDiscovery`, which takes in a `ResolvableModel` and loads the `UnbakedModel`s into a `ResolvedModel` on first reference. `ResolvedModel`s are functionally wrappers around `UnbakedModel`s used to resolve all dependency chains, as the name implies.

From there, model groups are built for `BlockState`s and the textures are loaded, leading to the final step of actually baking the `BlockStateModel` and the `ItemModel`. This is handled through the `bake` methods provided on the `$UnbakedRoot` (or `$Unbaked`) and the `ModelBakery`. In a nutshell, `bake` constructs the list of `BakedQuad`s stored with whatever additional information is desired by the block state or item model itself. The `ResolvedModel`s are obtained from the baker, from which the instance methods are called. For `BlockStateModel`s, this is resolved via `SimpleModelWrapper#bake`, from which the `ModelState` is obtained from the `Variant` data. They stored the baked quads in a `BlockModelPart`. For `ItemModel`s, it just consumes the `BakedQuad`s list directly along with information provided by `ModelRenderProperties#fromResolvedModel`. This does mean that each `BlockStateModel` and `ItemModel` may contain duplicated (but unique) `BakedQuad`s if the same model is referenced in multiple locations.

### Block Generators: The Variant Mutator

Given all the changes that separated out the block state JSON loading, there have also been a number of changes to the `BlockModelGenerators`. While most of them are simply renames (e.g., `BlockStateGenerator` -> `BlockModelDefinitionGenerator`), the major change is the addition of the `VariantMutator`. The `VariantMutator` is functionally a `UnaryOperator` on a `Variant` used to set some setting. This addition has simplified (or more like codec construction) the use of the `PropertyDispatch` for more quickly dispatching blocks with variants based on their properties.

```java
// Creates a property dispatch on the horizontal facing property
// Applies the associated variants, though if desired, a functional interface can be provided instead
public static final PropertyDispatch<VariantMutator> ROTATION_HORIZONTAL_FACING = PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
    .select(Direction.EAST, BlockModelGenerators.Y_ROT_90)
    .select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
    .select(Direction.WEST, BlockModelGenerators.Y_ROT_270)
    .select(Direction.NORTH, BlockModelGenerators.NOP);

// Then, with access to the `Consumer<BlockModelDefinitionGenerator>` blockStateOutput
this.blockStateOutput.accept(
    MultiVariantGenerator.dispatch(EXAMPLE_BLOCK).with(ROTATION_HORIZONTAL_FACING)
);
```

- `net.minecraft.client.data.models`
    - `BlockModelGenerators`
        - `nonOrientableTrapdoor` -> `NON_ORIENTABLE_TRAPDOOR`, now static
        - Constants are now available for common `VariantMutator`s, like rotating the block model some number of degrees
        - `texturedModels` -> `TEXTURED_MODELS`, now static
        - `MULTIFACE_GENERATOR` is now private
        - `plainModel` - Creates a variant from the model location.
        - `variant`, `variants` - Creates a regular `MultiVariant` from some number of `Variant`s
        - `plainVariant` - Creates a `MultiVariant` with only one model from its location.
        - `condition` - Creates a new condition builder for multipart models
        - `or` - ORs multiple conditions together.
        - Most generator methods now return the `BlockModelDefinitionGenerator`, `Variant`, or `MultiVariant` and take in a `Variant` or `MultiVariant` instead of a `ResourceLocation` pointing to the desired model
            - `VariantProperties` have been replaced with `VariantMutator`s
            - `Condition$TerminalCondition` is replaced with `Condition`
        - `createHorizontalFacingDispatch`, `createHorizontalFacingDispatchAlt`, `createTorchHorizontalDispatch` is removed
        - `createFacingDispatch` is removed
        - `createRotatedVariant(Block, ResourceLocation)` is removed
        - `selectMultifaceProperties` - Creates a map of properties to `VariantMutator`s based on the provided `BlockState` and direction to property function.
        - `applyRotation` no longer takes in the `Variant` and returns a `VariantMutator`
    - `ItemModelGenerators#generateSpawnEgg` is removed
    - `ModelProvider#saveAll` is removed
- `net.minecraft.client.data.models.blockstates`
    - `BlockStateGenerator` -> `BlockModelDefinitionGenerator`, not one-to-one
    - `Condition` -> `net.minecraft.client.renderer.block.model.multipart.Condition`, not one-to-one
        - `validate` -> `instantiate`, not one-to-one
    - `ConditionBuilder` - Builds a condition using property values
    - `MultiPartGenerator` now implements `BlockModelDefinitionGenerator`
        - `with(List<Variant>)` -> `with(MultiVariant)`
        - `with(Variant)` is removed
        - `with(Condition, ...)` -> `with(Condition, MultiVariant)`
            - Overload taking in `ConditionBuilder`
        - `$ConditionalEntry`, `$Entry` is removed
    - `MultiVariantGenerator` now implements `BlockModelDefinitionGenerator`
        - `multiVariant` -> `dispatch`
        - `multiVariant(Block, ...)` -> `dispatch(Block, MultiVariant)`
        - `$Empty` - A multi variant entry that matches every block state.
    - `PropertyDispatch` has a generic containing the value of the dispatch
        - The generic `V` replaces all values of `List<Variant>`
        - `property`, `properties` -> `initial` or `modify`
        - `$C*#generateList` methods are removed
        - `$*Function` are removed
    - `Selector` -> `PropertyValueList`, not one-to-one
    - `Variant` -> `net.minecraft.client.renderer.block.model.Variant`, not one-to-one
    - `VariantProperties` -> `net.minecraft.client.renderer.block.model.VariantMutator`, not one-to-one
    - `VariantProperty` -> `net.minecraft.client.renderer.block.model.VariantMutator$VariantProperty`, not one-to-one
- `net.minecraft.client.renderer.ItemInHandRenderer#renderItem` no longer takes in the boolean representing if the item is held in the left hand
- `net.minecraft.client.renderer.block`
    - `BlockModelShaper#stateToModelLocation`, `statePropertiesToString` is removed
    - `BlockRenderDispatcher#renderBatched` now takes in a list of `BlockModelPart`s instead of a `RandomSource`
    - `ModelBlockRenderer`
        - `tesselateBlock`, `tesselateWithAO`, `tesselateWithoutAO` no longer takes in a `RandomSource` and replaces `BlockStateModel` with a list of `BlockModelPart`s
        - `renderModel` is now static and no longer takes in the `BlockState`
        - `$AmbientOcclusionFace` -> `$AmbientOcclusionRenderStorage`
        - `$CommonRenderStorage` - A class that holds some metadata used to render a block at its given position.
        - `$SizeInfo` now takes in the direct index rather than computing the info from its direction and a flipped boolean
- `net.minecraft.client.renderer.block.model`
    - `BakedQuad` is now a record
    - `BlockElement` is now a record
        - `from`, `to` are now `Vector3fc`
    - `BlockElementFace` now takes in a `Quadrant` for the face rotation
        - `getU`, `getV` - Returns the texture coordinate after rotation.
        - `$Deserializer#getTintIndex` is now private and static
    - `BlockFaceUV` -> `BlockElementFace$UVs`, not one-to-one
    - `BlockModel` is now a record, taking in an `UnbakedGeometry` instead of the direct list of `BlockElement`s
        - `$Deserializer#getElements` now returns an `UnbakedGeometry`
    - `BlockModelDefinition` is now a record, taking in `$SimpleModelSelectors` and `$MultiPartDefinition`s
        - `GSON`, `fromStream`, `fromJsonElement` -> `CODEC`, not one-to-one
        - `instantiate` now takes in a supplied string instead of the string directly
        - `$Deserializer` is removed
        - `$MultiPartDefinition` - A record that holds a list of selectors to get for the multi part model.
        - `$SimpleModelSelectors` - A record that holds a map of variants to their unbaked model instances.
    - `BlockModelPart` - A baked model representation of a block.
    - `BlockStateModel` - A baked representation of a block state.
        - `collectParts` - Obtains the list of baked models used to render this state.
        - `$SimpleCachedUnbakedRoot` - A class that represents a delegate of some `$Unbaked` model.
        - `$Unbaked` - An extension over `$UnbakedRoot` that can create a `$SimpleCachedUnbakedRoot`
    - `FaceBakery`
        - `bakeQuad` now takes in `Vector3fc`s instead of `Vector3f`s
        - `recomputeUVs` is removed
        - `extractPositions` - Extracts the face positions and passes them to a consumer for use.
    - `ItemTransform` is now a record, vectors are `Vector3fc`s
    - `MultiVariant` -> `net.minecraft.client.data.models.MultiVariant`
        - `CODEC`
        - `with` - Creates a `MultiVariant` with the specified mutators.
        - `$Deserializer` class is removed
    - `SimpleModelWrapper` now implements `BlockModelPart`
    - `SimpleUnbakedGeometry` - An unbaked geometry that holds a list of `BlockElement`s to bake.
    - `SingleVariant` - A `BlockStateModel` implementation with only one model for its state.
    - `UnbakedBlockStateModel` -> `BlockStateModel$UnbakedRoot`
    - `Variant` no longer implements `ModelState`, now taking in a `$SimpleModelState` instead of the direct rotation and uv lock
        - The constructor now has an overload for only providing the `ResourceLocation` and no longer takes in the weight, leaving that to the `MultiVariant`
        - `CODEC`
        - `withXRot`, `withYRot`, `withUvLock`, `withModel`, `withState`, `with` - Mutates the variant into a new object with the given setting applied.
        - `$Deserializer` class is removed
        - `$SimpleModelState` - A record that holds the x/y rotations and uv lock.
    - `VariantMutator` - A unary operator on a variant that applies the specified setting to the variant. Used during state generation.
- `net.minecraft.client.renderer.block.model.multipart`
    - `AndCondition`, `OrCondition` -> `CombinedCondition`, not one-to-one
    - `KeyValueCondition` is now a record that takes in a map of keys to terms to test
    - `MultiPart` -> `MultiPartModel$Unbaked`
        - `$Definition`
            - `CODEC`
            - `getMultiVariants` is removed
        - `$Deserializer` class is removed
    - `Selector` is now a record, taking in a `BlockStateModel$Unbaked` instead of a `MultiVariant`
        - `$Deserializer` class is removed
- `net.minecraft.client.renderer.entity.ItemRenderer`
    - `renderItem` now takes in a `List<BakedQuad>` instead of a `BakedModel`
    - `renderStatic` no longer takes in a boolean indicating what hand the item was held in
- `net.minecraft.client.renderer.item`
    - `BlockModelWrapper` now has a public constructor that takes in the list of tint sources, the list of quads, and the `ModelRenderProperties`
        - The list of quads and `ModelRenderProperties` replaces the direct `BakedModel`, or now `BlockStateModel`
        - `computeExtents` - Extracts the vertices of the baked quads into an array.
    - `ItemModel$BakingContext#bake` is removed
    - `ItemModelResolver#updateForLiving`, `updateForTopItem` no longer takes in a boolean representing if the item is in the left hand
    - `ItemStackReenderState`
        - `isGui3d` is removed
        - `transform` is removed
        - `visitExtents` - Visits all vertices of the model to render and passes them into the provided consumer.
        - `$LayerRenderState`
            - `NO_EXTENTS_SUPPLIER` - An empty list of vertices.
            - `setupBlockModel` has been broken into `prepareQuadList`, `setRenderType`, `setUsesBlockLight`, `setExtents`, `setParticleIcon`, `setTransform`
            - `setupSpecialModel` no longer takes in the base `BakedModel`
    - `MissingItemModel` now takes in a list of `BakedQuad`s and `ModelRenderProperties` instead of the direct `BakedModel`
    - `ModelRenderProperties` - The properties used to render a model, typically retrieved from the `ResolvedModel`.
    - `SpecialModelRenderer` now takes in the `ModelRenderProperties` insetad of the base `BakedModel`
- `net.minecraft.client.resources.model`
    - `BakedModel` -> `net.minecraft.client.resources.model.QuadCollection`, not one-to-one
    - `BlockModelRotation`
        - `by` now takes in `Quadrant`s instead of integers
        - `withUvLock` - Returns the model state with the rotation and a mention that it locks the UV for the rotation.
    - `BlockStateDefinitions` - A manager for creating the mapper of block names to their state defintions.
    - `BlockStateModelLoader`
        - `ModelResourceLocation` fields are removed
        - `loadBlockState` no longer takes in the missing model
        - `$LoadedModel` class is removed
        - `$LoadedModels` now takes in a `BlockStateModel$UnbakedRoot` instead of an `$Unbaked`
            - `forResolving`, `plainModels` is removed
    - `DelegateBakedModel` -> `net.minecraft.client.renderer.block.model.SimpleModelWrapper`, not one-to-one
    - `MissingBlockModel#VARIANT` is removed
    - `ModelBaker`
        - `bake` -> `getModel`, not one-to-one
            - The baker is simply retrieving the `ResolvedModel`
        - `rootName` is removed
        - `compute` - Computes the provided key that contains the `ModelBaker`. Typically used for baking `BlockStateModel`s
        - `$SharedOperationKey` - An interface which typically computes some baking process for an unbaked model.
    - `ModelBakery` now takes in a `Map<BlockState, BlockStateModel$UnbakedRoot>` for the unbaked block state models, a `Map<ResourceLocation, ResolvedModel>` for the loaded models, and a `ResolvedModel` for the missing model
        - `bakeModels` now takes in a `SpriteGetter` and an `Executor` while returning a `CompletableFuture` for parallel loading and baking
        - `$BakingResult` now takes in a `$MissingModels` for the missing block state and item model and a `Map<BlockState, BlockStateModel>` for the baked block state models; the missing item model is stored within `$MissingModels`
        - `$MissingModels` - Holds the missing models for a block state and item.
        - `$TextureGetter` interface is removed
    - `ModelDebugName` no longer extends `Supplier<String>`, instead using `debugName`
    - `ModelDiscovery`
        - `registerSpecialModels` is removed
        - `discoverDependencies` is now private
        - `getReferencedModels`, `getUnreferencedModels` is removed
        - `addSpecialModel` - Adds a root model to the list of arbitrarily loaded models.
        - `missingModel` - Returns the missing model
        - `resolve` - Resolves all model dependencies, returning a map of model names to their models.
    - `ModelGroupCollector$GroupKey#create` now takes in a `BlockStateModel$UnbakedRoot` instead of an `$Unbaked`
    - `ModelManager`
        - `getModel` is removed
        - `getMissingModel` -> `getMissingBlockStateModel`
        - `$ResolvedModels` - A map of models with their dependencies resolved.
    - `ModelResourceLocation` record is removed
    - `ModelState`
        - `getRotation` -> `transformation`
        - `isUvLocked` is removed
        - `faceTransfomration`, `inverseFaceTransformation` - Handles returning the transformed `Matrix4fc` for baking the face vertices.
    - `MultiPartBakedModel` -> `net.minecraft.client.renderer.block.model.multipart.MultiPartModel`
        - Now implements `BlockStateModel` instead of extending `DelegateBakedModel`
        - `$SharedBlockState` - A holder that contains the `BlockStateModel`s mapped to their `$Selector`s.
    - `QuadCollection` - A data object containing the list of quads to render based on the associated direction and culling.
    - `ResolvableModel$Resolver#resolve` -> `markDependency`, not one-to-one
        - Instead of directly resolving, the dependency is marked for a later post processing step
    - `ResolvedModel` - An `UnbakedModel` whose model and texture dependencies have been completely resolved.
    - `SimpleBakedModel` -> `net.minecraft.client.renderer.block.model.SimpleModelWrapper` or `net.minecraft.client.renderer.block.model.SimpleUnbakedGeometry`, not one-to-one
    - `SpriteGetter`
        - `get`, `reportMissingReference` now take in the `ModelDebugName`
        - `resolveSlot` - Resolves the key from the `TextureSlot`s into its `TextureAtlasSprite`.
    - `UnbakedGeometry` - An interface that constructs a collection of quads the render when baked.
    - `UnbakedModel` no longer implements `ResolvableModel`
        - `DEFAULT_AMBIENT_OCCLUSION`, `DEFAULT_GUI_LIGHT` is removed
        - `PARTICLE_TEXTURE_REFERENCE` - Holds the key representing the particle texture.
        - `bake` is removed
        - `getAmbientOcclusion` -> `ambientOcclusion`
        - `getGuiLight` -> `guiLight`
        - `getTransforms` - `transforms`
        - `getTextureSlots` - `textureSlots`
        - `geometry` - Holds the unbaked geometry representing the model elements.
        - `getParent` -> `parent`, not one-to-one
        - `bakeWithTopModelValues` is removed
        - `getTopTextureSlots`, `getTopAmbientOcclusion`, `getTopGuiLight`, `getTopTransform`, `getTopTransforms` is removed
    - `WeightedBakedModel` -> `WeightedVariants`
        - Now implements `BlockStateModel` instead of extending `DelegateBakedModel`
- `net.minecraft.world.item.ItemDisplayContext#leftHand` - Returns whether the display context is rendering with the entity's left hand.

## Minor Migrations

The following is a list of useful or interesting additions, changes, and removals that do not deserve their own section in the primer.

### Entity References

Generally, the point of storing the UUID of another entity was to later grab that entity to perform some logic. However, storing the raw entity could lead to issues if the entity was removed at some point in time. As such, the `EntityReference` was added to handle resolving the entity from its UUID while also making sure it still existed at the time of query.

An `EntityReference` is simply a wrapped `Either` which either holds the entity instance or the UUID. When resolving via `getEntity`, it will attempt to verify that the stored entity, when present, isn't removed. If it is, it grabs the UUID to perform another lookup for the entity itself. If that entity does exist, it will be return, or otherwise null.

Most references to a UUID within an entity have been replaced with an `EntityReference` to facilitate this change.

- `net.minecraft.network.syncher.EntityDataSerializers#OPTIONAL_UUID` -> `OPTIONAL_LIVING_ENTITY_REFERENCE`, not one to one as it can hold the entity reference
- `net.minecraft.server.level.ServerLevel#getEntity(UUID)` -> `Level#getEntity(UUID)`
- `net.minecraft.world.entity`
    - `EntityReference` - A reference to an entity either by its entity instance when present in the world, or a UUID.
    - `LivingEntity#lastHurtByPlayer`, `lastHurtByMob` are now `EntityReference`s
    - `OwnableEntity`
        - `getOwnerUUID` -> `getOwnerReference`, not one-to-one
        - `level` now returns a `Level` instead of an `EntityGetter`
    - `TamableAnimal#setOwnerUUID` -> `setOwner`, or `setOwnerReference`; not one-to-one
- `net.minecraft.world.entity.animal.horse.AbstractHorse#setOwnerUUID` -> `setOwner`, not one-to-one
- `net.minecraft.world.level.Level` now implements `UUIDLookup<Entity>`
- `net.minecraft.world.level.entity`
    - `EntityAccess` now implements `UniquelyIdentifyable`
    - `UniquelyIdentifyable` - An interface that claims the object as a UUID and keeps tracks of whether the object is removed or not.
    - `UUIDLookup` - An interface that looks up a type by its UUID.

### Descoping Player Arguments

Many methods that take in the `Player` has been descoped to take in a `LivingEntity` or `Entity` depending on the usecase. The following methods below are a non-exhaustive list of this.

- `net.minecraft.world.entity.EntityType`
    - `spawn`
    - `createDefaultStackConfig`, `appendDefaultStackConfig`
    - `appendCustomEntityStackConfig`, `updateCustomEntityTag`
- `net.minecraft.world.item`
    - `BucketItem#playEmptySound`
    - `DispensibleContainerItem#checkExtraContent`, `emptyContents`
- `net.minecraft.world.level`
    - `Level`
        - `playSeededSound`
        - `mayInteract`
    - `LevelAccessor`
        - `playSound`
        - `levelEvent`
- `net.minecraft.world.level.block`
    - `BucketPickup#pickupBlock`
    - `LiquidBlockContainer#canPlaceLiquid`
- `net.minecraft.world.level.block.entity.BrushableBlockEntity#brush`

### Component Interaction Events

Click and hover events on a `MutableComponent` have been reworked into `MapCodec` registry-like system. They are both now interfaces that register their codecs to an `$Action` enum. The implementation then creates a codec that references the `$Action` type and stores any necessary information that is needed for the logic to apply. However, there is no direct 'action' logic associated with the component interactions. Instead, they are hardcoded into their use locations. For click events, this is within `Screen#handleComponentClicked`. For hover events, this is in `GuiGraphics#renderComponentHoverEffect`. As such, any additional events added will need to inject into both the enum and one or both of these locations.

- `net.minecraft.network.chat`
    - `ClickEvent` is now an interface
        - `getAction` -> `action`
        - `getValue` is now on the subclasses as necessary for their individual types
    - `HoverEvent` is now an interface
        - `getAction` -> `action`
        - `$EntityTooltipInfo`
            - `CODEC` is now a `MapCodec`
            - `legacyCreate` is removed
        - `$ItemStackInfo` is removed, replaced by `$ShowItem`
        - `$LegacyConverter` interface is removed

### Texture Atlas Reworks

The texture atlas logic has been finalized into a registry codec system; however, the querying of the atlas data has changed. First, all atlas identifiers are stored within `AtlasIds` while the corresponding texture location is stored within `Sheets`. To get a material from an atlas, a `MaterialMapper` is used as a wrapper around the texture location and the associated prefix to append to the material. The `Material` can then be obtained using `apply` by passing in the id of the material you would like to use.

For example:

```java
// Found in sheets
public static final MaterialMapper ITEMS_MAPPER = new MaterialMapper(TextureAtlas.LOCATION_BLOCKS, "item");
public static final MaterialMapper BLOCKS_MAPPER = new MaterialMapper(TextureAtlas.LOCATION_BLOCKS, "block");

// Finds the texture for the material at `assets/examplemod/textures/item/example_item.png`
public static final Material EXAMPLE_ITEM = ITEMS_MAPPER.apply(ResourceLocation.fromNamespaceAndPath("examplemod", "example_item"));

// Finds the texture for the material at `assets/examplemod/textures/block/example/block.png`
public static final Material EXAMPLE_BLOCK = ITEMS_MAPPER.apply(ResourceLocation.fromNamespaceAndPath("examplemod", "example/block"));
```

- `net.minecraft.client.data.AtlasProvider` - A data provider for generating the providers of a texture atlas.
- `net.minecraft.client.data.models.ItemModelGenerators`
    - `SLOT_*` -> `TRIM_PREFIX_*`, now public and `ResourceLocation`s
    - `TRIM_MATERIAL_MODELS` is now public
    - `generateTrimmableItem` now takes in a `ResourceLocation` instead of a `String`
    - `$TrimMaterialData` is now public, taking in a `MaterialAssetGroup` instead of the name and override materials
- `net.minecraft.client.renderer`
    - `MaterialMapper` - An object that stores the location of the atlas texture and the prefix applied to the ids within the texture.
    - `Sheets`
        - `*_MAPPER` - `MaterialMapper`s for each texture atlas texture.
        - `createBedMaterial(ResourceLocation)` is removed
        - `createShulkerMaterial(ResourceLocation)` is removed
        - `createSignMaterial(ResourceLocation)` is removed
        - `chestMaterial(String)`, `chestMaterial(ResourceLocation)` are removed
        - `createDecoratedPotMaterial(ResourceLocation)` is removed
- `net.minecraft.client.renderer.blockentity.ConduitRenderer#MAPPER` - A mapper to get the conduit textures from the block atlas.
- `net.minecraft.client.renderer.texture.atlas`
    - `SpriteSource#type` -> `codec`, not one-to-one
    - `SpriteSources` now contains logic similar to client registries via their id mapper
    - `SpriteSourceType` record is removed
- `net.minecraft.client.renderer.texture.atlas.sources`
    - `DirectoryLister` is now a record
    - `PalettedPermutations` is now a record
    - `SingleFile` is now a record
    - `SourceFilter` is now a record
    - `Unstitcher` is now a record
        - `$Region` is now public
- `net.minecraft.client.resources.model.AtlasIds` - A class which holds the `ResourceLocation`s of all vanilla texture atlases.

### Registry Context Swapper

Client items now store a `RegistryContextSwapper`, which is used to properly check client item information that accesses registry objects. Before level load, this is provided a placeholder to avoid crashing and populated with the correct value during rendering.

- `net.minecraft.client.multiplayer`
    - `CacheSlot` - An object that contains a value computed from some context. When updated, the previous value is overwritten and the context registers the slot to be cleaned.
    - `ClientLevel` now implements `CacheSlot$Cleaner`
- `net.minecraft.client.renderer.item`
    - `ClientItem` can now take in a nullable `RegistryContextSwapper`
        - `withRegistrySwapper` - Sets the `RegistryContextSwapper` within a `ClientItem`
    - `ItemModel$BakingContext` now takes in a `RegistryContextSwapper`
- `net.minecraft.util`
    - `PlaceholderLookupProvider` - A provider that contains placeholders for referenced objects. Used within client items as they will be loaded before the `RegistyAccess` is populated.
    - `RegistryContextSwapper` - An interface used to swap out some object for a different one. Used by client items to swap the placeholders for the loaded `RegistryAccess`.

### Reload Instance Creation

Reload instances have been slightly rearranged. The `SimpleReloadInstance` base now only takes in the `List<PreparableReloadListener>`, where the other fields are passed into the `of` function such that `#startTasks` can be called immediately.

- `net.minecraft.server.packs.resources`
    - `ProfiledReloadInstance` construct is now private, accessed through `of`
    - `SimpleReloadInstance` only takes in the `List<PreparableReloadListener>`
        - `of` now returns a `ReloadInstance`, not one-to-one
        - `allPreparations` is now package private
        - `allDone` is now private
        - `startTasks` - Begins the reload of the listener.
        - `prepareTasks` - Runs the executor and sets up the futures needed to read and load all desired data.
        - `StateFactory$SIMPLE` - A factory that calls `PreparableReloadListener#reload`

### Block Effect Appliers

Effects that are applied to entities when inside a block are now handled through the `InsideBlockEffectApplier` and `InsideBlockEffectType`. The `InsideBlockEffectType` is an enum that contains a consumer on what to apply to an entity when called. `InsideBlockEffectApplier`, on the other hand, is stored on the entity has a way to apply an effect in a ordered manner based on the enum ordinals.

To call one of the effect types, you must override `BlockBehaviour#entityInside` or `Fluid#entityInside` and call `InsideBlockEffectApplier#apply`. If something should apply before the effect type, like entinguishing fire before freezing in powder snow, then `InsideBlockEffectApplier#runBefore` should be called before `apply`. Similarly, if something should run afterward, like hurting an enemy after being placed in lava, then `runAfter` should be called.

```java
// In some block or fluid subclass
@Override
protected void entityInside(Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier) {
    applier.runBefore(InsideBlockEffectType.EXTINGUISH, entity -> {
        // Modify entity here.
    });

    // Do the base application logic stored on the type
    applier.apply(InsideBlockEffectType.FIRE_IGNITE);

    applier.runAfter(InsideBlockEffectType.FIRE_IGNITE, entity -> {
        // Perform any final checks that are as a result of the effect being applied
        entity.hurt(...);
    });
}
```

- `net.minecraft.world.entity`
    - `InsideBlockEffectApplier` - An interface that defines how an entity should interact when within a given block.
    - `InsideBlockEffectType` - An enum that defines what behavior to perform when side the specific block that references the type.
- `net.minecraft.world.level.block.state.BlockBehaviour#entityInside`, `$BlockStateBase#entityInside` now takes in an `InsideBlockEffectApplier`
- `net.minecraft.world.level.material.Fluid#entityInside`, `FluidState#entityInside` - A method called whenever the entity is considered inside the bounding box of the fluid.

### Timer Callbacks, joining the codec club!

`TimerCallback`s, used in the server schedule for executing events, typically mcfunctions in datapacks, have now been reworked into a codec form. This means that a callback can be registered to the list of available callbacks by passing in the `MapCodec` to `TimerCallbacks#register` (via `TimerCallbacks#SERVER_CALLBACKS`) instead of the serializer.

- `net.minecraft.world.level.timers`
    - `FunctionCallback` is now a record
    - `FunctionTagCallback` is now a record
    - `TimerCallback`
        - `codec` - Returns the codec used for serialization.
        - `$Serializer` class is removed
    - `TimerCallbacks`
        - `serialize`, `deserialize` -> `codec`, not one-to-one

### The JOML Backing Interfaces

Mojang has opted to lessen the restriction on JOML objects by passing around the implementing interface of their logic objects (usually implemented with a tacked on `c`). For example, `Vector3f` becomes `Vector3fc` or `Matrix4f` becomes `Matrix4fc`. This does not change any logic itself as the `c` interfaces are implemented by the class components.

### Tag Changes

- `minecraft:worldgen/biome`
    - `spawns_cold_variant_farm_animals`
    - `spawns_warm_variant_farm_animals`
- `minecraft:block`
    - `sword_instantly_mines`
    - `replaceable_by_mushrooms`
    - `plays_ambient_desert_block_sounds`
    - `edible_for_sheep`
    - `dead_bush_may_place_on` -> `dry_vegetation_may_place_on`
    - `camels_spawnable_on`
- `minecraft:cat_variant` are removed
- `minecraft:entity_type`
    - `can_equip_saddle`
    - `can_wear_horse_armor`
- `minecraft:item`
    - `book_cloning_target`
    - `eggs`
    - `flowers`

### Mob Effects Field Renames

Some mob effects have been renamed to their in-game name, rather than some internal descriptor.

- `MOVEMENT_SPEED` -> `SPEED`
- `MOVEMENT_SLOWDOWN` -> `SLOWNESS`
- `DIG_SPEED` -> `HASTE`
- `DIG_SLOWDOWN` -> `MINING_FATIGUE`
- `DAMAGE_BOOST` -> `STRENGTH`
- `HEAL` -> `INSTANT_HEALTH`
- `HARM` -> `INSTANT_DAMAGE`
- `JUMP` -> `JUMP_BOOST`
- `CONFUSION` -> `NAUSEA`
- `DAMAGE_RESISTANCE` -> `RESISTANCE`

### Very Technical Changes

This is a list of technical changes that could cause highly specific errors depending on your specific setup.

- The order of the `minecraft:patch_sugar_cane` feature and `minecraft:patch_pumpkin` feature have swapped orders (first pumpkin, then sugar cane), meaning modded biomes that generate both of these features will need to update their JSONs to the new ordering.

- Serveral vanilla oak tree and tree selector features now have `_leaf_litter` appended at the end.
    - For example: `trees_birch_and_oak` -> `trees_birch_and_oak_leaf_litter`

### List of Additions

- `net.minecraft`
    - `ChatFormatting#COLOR_CODEC`
    - `CrashReportCategory#populateBlockLocationDetails` - Adds the block location details to a crash report.
- `net.minecraft.advancements.critereon.MinMaxBounds#createStreamCodec` - Constructs a stream codec for a `MinMaxBounds` implementation.
- `net.minecraft.client.Options#startedCleanly` - Sets whether the game started cleanly on last startup.
- `net.minecraft.client.data.models`
    - `BlockModelGenerators#createSegmentedBlock` - Generates a multipart blockstate definition with horizontal rotation that displays up to four models based on some integer property.
    - `ItemModelGenerators#prefixForSlotTrim` - Generates a vanilla `ResourceLocation` for a trim in some slot.
- `net.minecraft.client.MouseHandler`
    - `fillMousePositionDetails` - Adds details about the current mouse location and screen size to a crash report.
    - `getScaledXPos` - Gets the current x position scaled by the gui scaling option.
    - `getScaledYPos` - Gets the current y position scaled by the gui scaling option.
    - `drawDebugMouseInfo` - Draws information about the scaled position of the mouse to the screen.
- `net.minecraft.client.gui.components.toasts.Toast#getSoundEvent` - Returns the sound to play when the toast is displayed.
- `net.minecraft.client.gui.screens.options.VideoSettingsScreen#updateFullscreenButton` - Sets the fullscreen option to the specified boolean.
- `net.minecraft.client.model.geom.builders`
    - `MeshDefinition#apply` - Applies the given transformer to the mesh before returning a new instance.
    - `MeshTransformer#IDENTITY`- Performs the identity transformation.
- `net.minecraft.client.multiplayer.ClientPacketListener#decoratedHashOpsGenenerator` - Returns the generator used to create a hash of a data component and its value.
- `net.minecraft.client.particle`
    - `FallingLeavesParticle$TintedLeavesProvider` - A provider for a `FallingLeavesParticle` that uses the color specified by the block above the particle the spawn location.
    - `FireflyParticle` - A particle that spawns fireflies around a given non-air block position.
- `net.minecraft.client.renderer`
    - `BiomeColors#getAverageDryFoliageColor` - Returns the average foliage color for dry biomes.
    - `LevelRenderer$BrightnessGetter` - An interfaces which obtains the packed brightness at a given block position.
    - `WorldBorderRenderer#invalidate` - Invalidates the current render of the world border to be rerendered.
- `net.minecraft.client.renderer.entity`
    - `EntityRenderDispatcher#getRenderer` - Gets the renderer to use from the data stored on the render state.
    - `EntityRenderer#extractAdditionalHitboxes` - Gets any additional hitboxes to render when the 'show hitboxes' debug state is enabled.
- `net.minecraft.client.renderer.entity.state`
    - `EntityRenderState`
        - `entityType` - The type of the entity.
        - `hitboxesRenderState` - The hitbox information of the entity relative to the entity's position.
        - `serverHitboxesRenderState` - The hitbox information of the entity synced from the server.
        - `fillCrashReportCategory` - Sets the details for any crashes related to the render state.
    - `HitboxesRenderState` - The render state of the hitboxes for the entity relative to the entity's position.
    - `HitboxRenderState` - The render state of a single hitbox to render along with its color, such as the eye height of an entity.
    - `ServerHitboxesRenderState` - The render state containing the last synced information from the related server entity.
    - `PigRenderState#variant` - The variant of the pig.
- `net.minecraft.client.renderer.item.SelectItemModel$ModelSelector` - A functional interface that selects the item model based on the switch case and level.
- `net.minecraft.client.renderer.item.properties.conditional.ComponentMatches` - A conditional property that checks whether the given predicate matches the component data.
- `net.minecraft.client.renderer.item.properties.select`
    - `ComponentContents` - A switch case property that operates on the contents within a data component.
    - `SelectItemModelProperty#valueCodec` - Returns the `Codec` for the property type.
- `net.minecraft.client.resources.DryFoliageColorReloadListener` - A reload listener that loads the colormap for dry foliage.
- `net.minecraft.commands.arguments.ComponentArgument#getResolvedComponent` - Constructs a component with the resolved information of its contents.
- `net.minecraft.core`
    - `Direction#getUnitVec3f` - Returns the float unit vector of the direction.
    - `HolderGetter$Provider#getOrThrow` - Gets a holder reference from a resource key.
    - `SectionPos#sectionToChunk` - Converts a compressed section position to a compressed chunk position.
    - `Vec3i#STREAM_CODEC`
- `net.minecraft.network`
    - `HashedPatchMap` - A record containing a map of components to their hashed type/value along with a set of removed components.
    - `HashedStack` - An `ItemStack` representation that hashes the stored components.
    - `ProtocolInfo$DetailsProvider` - Provides the details for a given protocol.
    - `SkipPacketDecoderException` - An exception thrown when an error occurs during decoding before having its data ignored.
    - `SkipPacketEncoderException` - An exception thrown when an error occurs during encoding before having its data ignored.
- `net.minecraft.network.chat`
    - `LastSeenMessages`
        - `computeChecksum` - Computes a byte representing the merged checksums of all message signatures.
        - `$Update#verifyChecksum` - Verifies that the update checksum matches those within the last seen messages.
    - `LastSeenMessagesValidator$ValidationException` - An exception thrown if the messages can not be validated.
    - `MessageSignature`
        - `describe` - Returns a stringified version of the message signature.
        - `checksum` - Hashes the bytes within the signature into a single integer.
    - `PlayerChatMessage#describeSigned` - Returns a stringified version of the chat message.
- `net.minecraft.network.codec`
    - `ByteBufCodecs`
        - `LONG_ARRAY`
        - `lengthPrefixed` - Returns an operation that limits the size of the buffer to the given size.
    - `IdDispatchCodec$DontDecorateException` - An interface that tells the exception handler to rethrow the raw exception rather than wrap it within an `EncoderException`.
- `net.minecraft.network.protocol`
    - `CodecModifier` - A function that modifies some codec using a given object.
    - `ProtocolInfoBuilder#context*Protocol` - Builds an `UnboundProtocol` with the given context used to modify the codecs to send.
- `net.minecraft.network.protocol.game.GameProtocols`
    - `HAS_INFINITE_MATERIALS` - A modifier that checks the `ServerboundSetCreativeModeSlotPacket` for if the player has the necessary settings. If not, the packet is discarded.
    - `$Context` - Returns the context used by the packet to modify the incoming codec.
- `net.minecraft.resources.DelegatingOps`
    - `$DelegateListBuilder` - A list builder that can be subclassed if needed.
    - `$DelegateRecordBuilder` - A record builder that can be subclassed if needed.
- `net.minecraft.server.bossevents.CustomBossEvent$Packed` - A record that backs the event information for serialization.
- `net.minecraft.server.commands.InCommandFunction` - A command function that takes in some input and returns a result.
- `net.minecraft.server.level`
    - `DistanceManager#forEachBlockTickingChucnks` - Applies the provided consumer for each chunk with block ticking enabled.
    - `ServerLevel`
        - `areEntitiesActuallyLoadedAndTicking` - Returns whether the entity manager is actually ticking and loading entities in the given chunk.
        - `tickThunder` - Ticks the thunger logic within a given level.
        - `anyPlayerCloseEnoughForSpawning` - Returns if a player is close enough to spawn the entity at the given location.
    - `ServerPlayer$RespawnConfig` - A record containing the respawn information for the player.
- `net.minecraft.util`
    - `AbstractListBuilder` - A ops list builder which boils the implementation down to three methods which initializes, appends, and builds the final list.
    - `Brightness`
        - `block` - Returns the block light from a packed value.
        - `sky` - Returns the sky light from a packed value.
    - `HashOps` - A dynamic ops that generates a hashcode for the data.
    - `ExtraCodecs`
        - `UNTRUSTED_URI` - A codec for a URI that is not trusted by the game.
        - `CHAT_STRING` - A codec for a string in a chat message.
        - `legacyEnum` - A codec that maps an enum to its output in `Enum#toString`.
    - `FileSystemUtil` - A utility for interacting with the file system.
    - `GsonHelper#encodesLongerThan` - Returns whether the provided element can be written in the specified number of characters.
    - `Unit#STREAM_CODEC` - A stream codec for a unit instance.
    - `Util`
        - `mapValues` - Updates the values of a map with the given function.
        - `mapValuesLazy` - Updates the values of a map with the given function, but each value is resolved when first accessed.
        - `growByHalf` - Returns an integer multiplied by 1.5, rounding down, clamping the value to some minimum and the max integer size.
- `net.minecraft.util.random.Weighted#map`, `WeightedList#map` - Transforms the stored object(s) to a new type.
- `net.minecraft.util.thread.ParallelMapTransform` - A helper that handles scheduling and batching tasks in parallel.
- `net.minecraft.world.effect.MobEffectInstance#withScaledDuration` - Constructs a new instance with the duration scaled by some float value.
- `net.minecraft.world.entity`
    - `AreaEffectCloud#setPotionDurationScale` - Sets the scale of how long the potion should apply for.
    - `DropChances` - A map of slots to probabilities indicating how likely it is for an entity to drop that piece of equipment.
    - `Entity`
        - `isInterpolating` - Returns whether the entity is interpolating between two steps.
        - `sendBubbleColumnParticles` - Spawns bubble column particles from the server.
        - `canSimulateMovement` - Whether the entity's movement can be simulated, usually from being the player.
        - `propagateFallToPassengers` - Propogates the fall damage of a vehicle to its passengers.
        - `lavaIgnite` - Ignites the entity for 15 seconds if not immune.
        - `clearFreeze` - Sets the number of ticks the entity is frozen for to 0.
        - `removeLatestMovementRecordingBatch` - Removes the last element from all movements performed this tick.
    - `InterpolationHandler` - A class meant to easily handle the interpolation of the position and rotation of the given entity as necessary.
    - `LivingEntity`
        - `getLuck` - Returns the luck of the entity for random events.
        - `getLastHurtByPlayer`, `setLastHurtByPlayer` - Handles the last player to hurt this entity.
        - `getEffectBlendFactor` - Gets the blend factor of an applied mob effect.
        - `applyInput` - Applies the entity's input as its AI, typically for local players.
        - `INPUT_FRICTION` - The scalar to apply to the movements of the entity.
- `net.minecraft.world.entity.animal.camel.Camel#checkCamelSpawnRules` - Checks if a camel can spawn at a particular position.
- `net.minecraft.world.entity.animal.sheep.SheepColorSpawnRules` - A class that contains the color spawn configurations for a sheep's wool when spawning within a given climate.
- `net.minecraft.world.entity.npc.Villager#createDefaultVillagerData` - Returns the default type and profession of the villager to use when no data is set.
- `net.minecraft.world.entity.player.Player`
    - `preventsBlockDrops` - Whether the player cannot drop any blocks on destruction.
    - `gameMode` - Returns the current game mode of the player.
    - `debugInfo` - Returns the common information about the player as a single string.
- `net.minecraft.world.inventory`
    - `ContainerSynchronizer#createSlot` - Creates a `RemoteSlot` that represents a slot on the opposite side.
    - `RemoteSlot` - A slot that represents the data on the opposing side, syncing when the data is not consistent. 
- `net.minecraft.world.item`
    - `EitherHolder#key` - Returns the resource key of the held registry object.
    - `Item#STREAM_CODEC`
    - `ItemStack`
        - `OPTIONAL_UNTRUSTED_STREAM_CODEC`
        - `MAP_CODEC`
        - `canDestroyBlock` - Returns whether this item can destroy the provided block state.
- `net.minecraft.world.item.alchemy.PotionContents#getPotionDescription` - Returns the description of the mob effect with some amplifier.
- `net.minecraft.world.item.crafting`
    - `Recipe#KEY_CODEC`
    - `TransmuteResult` - A recipe result object that represents an item, count, and the applied components.
- `net.minecraft.world.item.equipment.trim.ArmorTrim#layerAssetId` - Returns the location of the the trim asset.
- `net.minecraft.world.level`
    - `BlockGetter$BlockStepVisitor` - A consumer that takes in the current position and how many collisions within the desired path of travel.
    - `ColorMapColorUtil` - A helper for getting the color from a map given the biome's temperature, downfall, colormap, and default color.
    - `DryFoliageColor` - A color resolver for biomes with dry foliage.
    - `GameRules`
        - `getType` - Gets the game rule type from its key.
        - `keyCodec` - Creates the codec for the key of a game rule type.
    - `Level`
        - `isMoonVisible` - Returns wehther the moon is currently visible in the sky.
        - `getPushableEntities` - Gets all entities except the specified target within the provided bounding box.
        - `getClientLeafTintColor` - Returns the color of the leaf tint at the specified location.
        - `playPlayerSound` - Plays a sound to the current player on the client.
    - `LevelReader#getHeight` - Returns the height of the map at the given position.
    - `NaturalSpawner#INSCRIBED_SQUARE_SPAWN_DISTANCE_CHUNK` - Provides the minimum distance that the player is close enough for spawning to occur.
- `net.minecraft.world.level.biome`
    - `Biome`   
        - `getDryFoliageColor`, `getDryFoliageColorFromTexture` - Gets the dry foliage color of the biome, either from the effects or from the climate settings.
    - `BiomeSpecialEffects#getDryFoliageColorOverride`, `$Builder#dryFoliageColorOverride` - Returns the default dry foliage color when not pulling from a colormap texture.
- `net.minecraft.world.level.block`
    - `BaseFireBlock#fireIgnite` - Lights an entity on fire.
    - `Block`
        - `UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS` - A flag that skips all potential sideeffects when updating a block entity.
        - `UPDATE_SKIP_ALL_SIDEEFFECTS` - A flag that skips all sideeffects by skipping certain block entity logic, supressing drops, and updating the known shape.
        - `UPDATE_SKIP_ON_PLACE` - A flag that skips calling `BlockState#onPlace` when set.
    - `BonemealableBlock#hasSpreadableNeighbourPos`, `findSpreadableNeighbourPos` - Handles finding other positions that the vegetation can spread to on bonemeal.
    - `CactusFlowerBlock` - A flower that grows on a cactus.
    - `FireflyBushBlock` - A bush that spawns firefly particles around it.
    - `SandBlock` - A colored sand block that can play ambient sounds.
    - `SegmentableBlock` - A block that can typically be broken up into segments with unique sizes and placements.
    - `ShortDryGrassBlock` - A single grass block that has been dried out.
    - `TallDryGrassBlock` - A double grass block that has been dried out.
    - `TerracottaBlock` - A terracotta block that can play ambient sounds.
    - `TintParticleLeavesBlock` - A leaves block whose particles are tinted.
    - `UntintedParticleLeavesBlock` - A leaves block whose particles are not tinted.
    - `VegetationBlock` - A block that represents some sort of vegetation that can propogate light and need some sort of farmland or dirt to survive.
- `net.minecraft.world.level.block.entity.StructureBlockEntity#isStrict`, `setStrict` - Sets strict mode when generating structures.
- `net.minecraft.world.level.block.sounds.AmbientDesertBlockSoundsPlayer` - A helper to play sounds for a given block, typically during `animateTick`.
- `net.minecraft.world.level.block.state.BlockBehaviour#getEntityInsideCollisionShape` - Gets the collision shape of the block when the entity is within it.
- `net.minecraft.world.level.border.WorldBorder`
    - `closestBorder` - Returns a list of the closest borders to the player based on their horizontal direction.
    - `$DistancePerDirection` - A record containing the distance from the entity of the world border in a given direction.
- `net.minecraft.world.level.chunk.status.ChunkStatus#CODEC`
- `net.minecraft.world.level.entity.PersistentEntitySectionManager#isTicking` - Returns whether the specified chunk is currently ticking.
- `net.minecraft.world.level.levelgen.Heightmap$Types#STREAM_CODEC`
- `net.minecraft.world.level.levelgen.feature`
    - `AbstractHugeMushroomFeature#placeMushroomBlock` - Places a mushroom block that specified location, replacing a block if it can.
    - `FallenTreeFeature` - A feature that generates flane trees with a stump of given lengths.
    - `TreeFeature#getLowestTrunkOrRootOfTree` - Retruns the lowest block positions of the tree decorator.
- `net.minecraft.world.level.levelgen.feature.configurations.FallenTreeConfiguration` - A configuration for fallen trees with stumps.
- `net.minecraft.world.level.levelgen.feature.treedecorators`
    - `AttachedToLogsDecorator` - A decorator that attaches a random block to a given direction on a log with a set probability.
    - `PlaceOnGroundDecorator` - A decorator that places the tree on a valid block position.
- `net.minecraft.world.level.levelgen.structure.pools`
    - `ListPoolElement#getElements` - Returns the elements of the structure pool.
    - `SinglePoolElement#getTemplateLocation` - Returns the location of the template used by the element.
    - `StructureTemplatePool#getTemplates` - Returns a list of elements with their weights.
- `net.minecraft.world.level.levelgen.structure.structures.JigsawStructure`
    - `getStartPool` - Returns the starting pool of the jigsaw to generate.
    - `getPoolAliases` - Returns all pools used by the jigsaw.
- `net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate#getDefaultJointType` - Returns the default join type between two jigsaw pieces if none is specified or an error occurs during loading.
- `net.minecraft.world.level.material.Fluid#getAABB`, `FluidState#getAABB` - Returns the bounding box of the fluid.
- `net.minecraft.world.scores`
    - `Objective#pack`, `$Packed` - Handles the serializable form of the objective data.
    - `PlayerTeam#pack`, `$Packed` - Handles the serializable form of the player team data.
    - `Scoreboard`
        - `loadPlayerTeam`, `loadObjective` - Loads the data from the packed object.
        - `$PackedScore` - Handles the serializable form of the scoreboard data.
- `net.minecraft.world.level.storage.loot.LootTable#KEY_CODEC`
- `net.minecraft.world.phys`
    - `AABB$Builder` - A builder for constructing a bounding box by providing the vectors within.
    - `Vec2#CODEC`
- `net.minecraft.world.phys.shapes.CollisionContext`
    - `placementContext` - Constructs the context when placing a block from its item.
    - `isPlacement` - Returns whether the context is being used for placing a block.
- `net.minecraft.world.ticks.TickPriority#CODEC`

### List of Changes

- `net.minecraft.client.Screenshot` is now a utility instead of an instance class, meaning all instance methods are removed
    - `takeScreenshot(RenderTarget)` -> `takeScreenshot(RenderTarget, Consumer<NativeImage>)`, not returning anything
- `net.minecraft.client.multiplayer`
    - `ClientChunkCache#replaceWithPacketData` now takes in a `Map<Heightmap$Types, long[]>` instead of a `CompoundTag`
    - `MultiPlayerGameMode#hasInfiniteItems` -> `net.minecraft.world.entity.LivingEntity#hasInfiniteMaterials`
    - `ClientPacketListener#markMessageAsProcessed` now takes in a `MessageSignature` instead of a `PlayerChatMessage`
- `net.minecraft.client.multiplayer.chat.ChatListener#handleChatMessageError` now takes in a nullable `MessageSignature`
- `net.minecraft.client.player`
    - `ClientInput#leftImpulse`, `forwardImpulse` -> `moveVector`, now protected
    - `LocalPlayer#spinningEffectIntensity`, `oSpinningEffectIntensity` -> `portalEffectIntensity`, `oPortalEffectIntensity`
- `net.minecraft.client.renderer.LevelRenderer#getLightColor(BlockAndTintGetter, BlockState, BlockPos)` -> `getLightColor(LevelRenderer$BrightnessGetter, BlockAndTintGetter, BlockState, BlockPos)`
- `net.minecraft.client.renderer.blockentity.BlockEntityRenderer#render` now takes in a `Vec3` representing the camera's position
- `net.minecraft.client.renderer.chunk.SectionRenderDispatcher`
    - `$RenderSection`
        - `getOrigin` -> `getRenderOrigin`
        - `reset` is now public
        - `releaseBuffers` is removed
    - `$CompileTask#getOrigin` -> `getRenderOrigin`
- `net.minecraft.client.renderer.entity`
    - `DonkeyRenderer` now takes in a `DonekyRenderer$Type` containing the textures, model layers, and equipment information
    - `ItemEntityRenderer#renderMultipleFromCount` now has an overload that takes in the model bounding box
    - `UndeadHorseRenderer` now takes in a `UndeadHorseRenderer$Type` containing the textures, model layers, and equipment information
- `net.minecraft.client.renderer.entity.layers`
    - `EquipmentLayerRenderer$TrimSpriteKey#textureId` -> `spriteId`
    - `VillagerProfessionLayer#getHatData` now takes in a map of resource keys to metadata sections and swaps the registry and value for a holder instance
- `net.minecraft.client.renderer.item`
    - `ConditionalItemModel` now takes in a `ItemModelPropertyTest` instead of a `ConditionalItemModelProperty`
    - `SelectItemModel` now takes in a `$ModelSelector` instead of an object map
- `net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty` now implements `ItemModelPropertyTest`
    - `ItemModelPropertyTest` holds the `get` method previously within `ConditionalItemModelProperty`
- `net.minecraft.commands.arguments`
    - `ComponentArgument`
        - `ERROR_INVALID_JSON` -> `ERROR_INVALID_COMPONENT`
        - `getComponent` -> `getRawComponent`
    - `ResourceKeyArgument#getRegistryKey` is now public
    - `StyleArgument#ERROR_INVALID_JSON` -> `ERROR_INVALID_STYLE`
- `net.minecraft.commands.arguments.item`
    - `ComponentPredicateParser$Context#createComponentTest`, `createPredicateTest` now takes in a `Dynamic` instead of a `Tag`
    - `ItemPredicateArgument`
        - `$ComponentWrapper#decode` now takes in a `Dynamic` instead of a `RegistryOps`, `Tag` pair
        - `$PredicateWrapper#decode` now takes in a `Dynamic` instead of a `RegistryOps`, `Tag` pair
- `net.minecraft.core`
    - `BlockMath`
        - `VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL`, `VANILLA_UV_TRANSFORM_GLOBAL_TO_LOCAL` is now private
        - `getUVLockTransform` -> `getFaceTransformation`
    - `Direction#rotate` now takes in a `Matrix4fc` instead of a `Matrix4f`
    - `Rotations` is now a record
- `net.minecraft.data.loot.BlockLootSubProvider#createPetalDrops` -> `createSegmentedBlockDrops`
- `net.minecraft.network`
    - `FriendlyByteBuf`
        - `writeLongArray`, `readLongArray` now have static delegates which take in the `ByteBuf` and `*Fixed*` versions for fixed size arrays
    - `ProtocolInfo$Unbound` -> `$Details`, `net.minecraft.network.protocol.SimpleUnboundProtocol`, `net.minecraft.network.protocol.UnboundProtocol`; not one-to-one
        - `#bind` -> `net.minecraft.network.protocol.SimpleUnboundProtocol#bind`, `UnboundProtocol#bind`; not one-to-one
    - `SkipPacketException` is now an interface instead of a subclass of `EncoderException`
- `net.minecraft.network.chat`
    - `ComponentSerialization#flatCodec` -> `flatRestrictedCodec`
    - `LastSeenMessages$Update` now takes in a byte representing the checksum value
    - `LastSeenMessagesValidator`
        - `applyOffset` now returns nothing and can throw a `$ValidationException`
        - `applyUpdate` now returns the raw messages and can throw a `$ValidationException`
- `net.minecraft.network.codec.StreamCodec#composite` now has an overload for nine parameters
- `net.minecraft.network.protocol.ProtocolInfoBuilder` now takes in a third generic representing how to modify the provided codec.
    - `addPacket` now has an overload that takes in a `CodecModifier`
    - `build` -> `buildUnbound`, not one-to-one
    - `protocol`, `serverboundProtocol`, `clientboundProtocol` now returns a `SimpleUnboundProtocol`
- `net.minecraft.network.protocol.ConfigurationProtocols` now contain `SimpleUnboundProtocol` constants
- `net.minecraft.network.protocol.game`
    - `ClientboundContainerSetContentPacket` is now a record
    - `ClientboundMoveEntityPacket#getyRot`, `getxRot` -> `getYRot`, `getXRot`
    - `ClientboundPlayerChatPacket` now takes in a global index for the chat message
    - `ClientboundLevelChunkPacketdata#getHeightmaps` now returns a `Map<Heightmap.Types, long[]>`
    - `ClientboundUpdateAdvancementsPacket` now takes in a boolean representing whether to show the adavncements as a toast
    - `GameProtocols` constants are now either `SimpleUnboundProtocol`s or `UnboundProtocol`s
    - `ServerboundContainerClickPacket` is now a record
    - `ServerboundMovePlayerPacket$Pos`, `$PosRot` now has an overload that takes in a `Vec3` for the position
    - `ServerboundSetStructureBlockPacket` now takes in an additional boolean representing whether the structure should be generated in strict mode
- `net.minecraft.network.protocol.handshake.HandshakeProtocols#SERVERBOUND_TEMPLATE` is now a `SimpleUnboundProtocol`
- `net.minecraft.network.protocol.login.LoginProtocols#SERVERBOUND_TEMPLATE` constants are now `SimpleUnboundProtocol`s
- `net.minecraft.network.protocol.status.StatusProtocols#SERVERBOUND_TEMPLATE` constants are now `SimpleUnboundProtocol`s
- `net.minecraft.server.PlayerAdvancements#flushDirty` now takes in a boolean that represents whether the advancements show display as a toast
- `net.minecraft.server.bossevents.CustomBossEvent`
    - `save` -> `pack`, not one-to-one
    - `load` now takes in the id and the packed variant to unpack
- `net.minecraft.server.level`
    - `DistanceManager`
        - `hasPlayersNearby` now returns a `TriState`
        - `forEachBlockTickingChunks` -> `forEachEntityTickingChunk`, not one-to-one
    - `ServerEntity` now takes in a consumer for broadcasting a packet to all players but those in the ignore list
    - `ServerLevel`
        - `getForcedChunks` -> `getForceLoadedChunks`
        - `isPositionTickingWithEntitiesLoaded` is now public
        - `isNaturalSpawningAllowed` -> `canSpawnEntitiesInChunk`, `BlockPos` variant is removed
    - `ServerPlayer`
        - `getRespawnPosition`, `getRespawnAngle`, `getRespawnDimension`, `isRespawnForced` -> `getRespawnConfig`, not one-to-one
        - `setRespawnPosition` now takes in a `$RespawnConfig` instead of the individual respawn information
        - `loadAndSpawnParentVehicle`, `loadAndSpawnEnderpearls` now takes in a `CompoundTag` without the optional wrapping\
- `net.minecraft.server.network.ServerGamePacketListenerImpl` now implements `GameProtocols$Context`
- `net.minecraft.sounds.SoundEvents` have the following sounds now `Holder` wrapped:
    - `ITEM_BREAK`
    - `SHIELD_BLOCK`, `SHIELD_BREAK`,
    - `WOLF_ARMOR_BREAK`
- `net.minecraft.util`
    - `Brightness`
        - `FULL_BRIGHT` is now final
        - `pack` now has a static overload that takes in the block and sky light.
    - `ExtraCodecs#MATRIX4f` now is a `Codec<Matrix4fc>`
    - `Util#makeEnumMap` returns the `Map` superinstance rather than the specific `EnumMap`
- `net.minecraft.util.parsing.packrat.commands.TagParseRule` now takes in a generic for the tag type
    - The construct is now public, taking in a `DynamicOps`
- `net.minecraft.util.profiling`
    - `ActiveProfiler` now takes in a `BooleanSupplier` instead of a boolean
    - `ContinuousProfiler` now takes in a `BooleanSupplier` instead of a boolean
- `net.minecraft.util.worldupdate.WorldUpgrader` now takes in the current `WorldData`
- `net.minecraft.world`
    - `BossEvent$BossBarColor`, `$BossBarOverlay` now implements `StringRepresentable`
    - `Container` now implements `Iterable<ItemStack>`
- `net.minecraft.world.effect`
    - `MobEffect`
        - `getBlendDurationTicks` -> `getBlendInDurationTicks`, `getBlendOutDurationTicks`, `getBlendOutAdvanceTicks`; not one-to-one
        - `setBlendDuration` now has an overload that takes in three integers to set the blend in, blend out, and blend out advance ticks
    - `MobEffectInstance#tick` -> `tickServer`, `tickClient`; not one-to-one
- `net.minecraft.world.entity`
    - `Entity`
        - `cancelLerp` -> `InterpolationHandler#cancel`
        - `lerpTo` -> `moveOrInterpolateTo`
        - `lerpTargetX`, `lerpTargetY`, `lerpTargetZ`, `lerpTargetXRot`, `lerpTargetYRot` -> `getInterpolation`
        - `onAboveBubbleCol` -> `onAboveBubbleColumn` now takes in a `BlockPos` for the bubble column particles spawn location
            - Logic delegates to the protected static `handleOnAboveBubbleColumn`
        - `isControlledByOrIsLocalPlayer` -> `isLocalInstanceAuthoritative`, now final
        - `isControlledByLocalInstance` -> `isLocalClientAuthoritative`, now protected
        - `isControlledByClient` -> `isClientAuthoritative`
        - `fallDistance`, `causeFallDamage` is now a double
        - `absMoveto` -> `absSnapTo`
        - `absRotateTo` -> `asbSnapRotationTo`
        - `moveTo` -> `snapTo`
        - `sendBubbleColumnParticles` is now static, taking in the `Level`
        - `onInsideBubbleColumn` logic delegates to the protected static `handleOnInsideBubbleColumn`
    - `EntityType`
        - `POTION` -> `SPLASH_POTION`, `LINGERING_POTION`, not one-to-one
        - `$EntityFactory#create` can now return a null instance
    - `ExperienceOrb#value` -> `DATA_VALUE`
    - `ItemBasedSteering` no longer takes in the accessor for having a saddle
    - `LivingEntity`
        - `lastHurtByPlayerTime` -> `lastHurtByPlayerMemoryTime`
        - `lerpSteps`, `lerpX`, `lerpY`, `lerpZ`, `lerpYRot`, `lerpXRot` -> `interpolation`, not one-to-one
        - `isAffectedByFluids` is now public
        - `removeEffectNoUpdate` is now final
        - `tickHeadTurn` now returns nothing
        - `canDisableShield` -> `canDisableBlocking`, now set via the `WEAPON` data component
        - `calculateFallDamage` now takes in a double instead of a float
    - `Mob`
        - `handDropChances`, `armorDropChances`, `bodyArmorDropChance` -> `dropChances`, not one-to-one
        - `getEquipmentDropChance` -> `getDropChances`, not one-to-one
- `net.minecraft.world.entity.ai.Brain#addActivityWithConditions` now has an overload that takes in an integer indiciating the starting priority
- `net.minecraft.world.entity.ai.behavior`
    - `LongJumpToRandomPos$PossibleJump` is now a record
    - `VillagerGoalPackages#get*Package` now takes in a holder-wrapped profession
- `net.minecraft.world.entity.ai.gossip.GossipContainer#store`, `update` -> `clear`, `putAll`, `copy`; not one-to-one
- `net.minecraft.world.entity.animal`
    - `Pig` is now a `VariantHolder`
    - `Sheep` -> `.sheep.Sheep`
    - `WaterAnimal#handleAirSupply` now takes in a `ServerLevel`
- `net.minecraft.world.entity.animal.axolotl.Axolotl#handleAirSupply` now takes in a `ServerLevel`
- `net.minecraft.world.entity.monster.ZombieVillager#setGossips` now takes in a `GossipContainer`
- `net.minecraft.world.entity.monster.warden.WardenSpawnTracker` now has an overload which sets the initial parameters to zero
- `net.minecraft.world.entity.npc`
    - `Villager` now takes in either a key or a holder of the `VillagerType`
        - `setGossips` now takes in a `GossipContainer`
    - `VillagerData` is now a record
        - `set*` -> `with*`
    - `VillagerProfession` now takes in a `Component` for the name
    - `VillagerTrades`
        - `TRADES` now takes in a resource key as the key of the map
            - This is similar for all other type specific trades
        - `$FailureItemListing` is now private
- `net.minecraft.world.entity.player.Player`
    - `stopFallFlying` -> `LivingEntity#stopFallFlying`
    - `isSpectator`, `isCreative` no longer abstract in the `Player` class
- `net.minecraft.world.entity.projectile.ThrownPotion` -> `AbstractThrownPotion`, implemented in `ThrownLingeringPotion` and `ThrownSplashPotion`
- `net.minecraft.world.entity.raid.Raid(int, ServerLevel, BlockPos)` -> `Raid(BlockPos, Difficulty)`
    - `tick`, `addWaveMob` now takes in the `ServerLevel`
- `net.minecraft.world.entity.vehicle`
    - `AbstractMinecart#setDisplayBlockState` -> `setCustomDisplayBlockState`
    - `MinecartBehavior` 
        - `cancelLerp` -> `InterpolationHandler#cancel`
        - `lerpTargetX`, `lerpTargetY`, `lerpTargetZ`, `lerpTargetXRot`, `lerpTargetYRot` -> `getInterpolation`
    - `MinecartTNT#primeFuse` now takes in the `DamageSource` cause
- `net.minecraft.world.inventory`
    - `AbstractContainerMenu`
        - `setRemoteSlotNoCopy` -> `setRemoteSlotUnsafe`, not one-to-one
        - `setRemoteCarried` now takes in a `HashedStack`
    - `ClickType` now takes in an id for its representations
    - `ContainerSynchronizer#sendInitialData` now takes in a list of stacks rather than a `NonNullList`
- `net.minecraft.world.item`
    - `EitherHolder` now takes in an `Either` instance rather than just an `Optional` holder and `ResourceKey`
    - `Item`
        - `canAttackBlock` -> `canDestroyBlock`
        - `hurtEnemy` no longer returns anything
        - `onCraftedBy` no longer takes in a separate `Level` instance, now relying on the one provided by the `Player`
    - `ItemStack`
        - `validateStrict` is now public
        - `onCraftedBy` no longer takes in a separate `Level` instance, now relying on the one provided by the `Player`
    - `MapItem`
        - `create` now takes in a `ServerLevel` instead of a `Level`
        - `lockMap` is now private
    - `ThrowablePotionItem` is now abstract, containing two methods to create the `AbstractThrownPotion` entity
    - `WrittenBookItem#resolveBookComponents` -> `WrittenBookContent#resolveForItem`
- `net.minecraft.world.item.alchemy.PotionContents` now implements `TooltipProvider`
    - `forEachEffect`, `applyToLivingEntity` now takes in a float representing a scalar for the duration
- `net.minecraft.world.item.component.WrittenBookContent` now implements `TooltipProvider`
- `net.minecraft.world.item.crafting`
    - `SmithingRecipe#baseIngredient` now returns an `Ingredient`
    - `SmithingTransformRecipe` now takes in a `TransmuteResult` instead of an `ItemStack` and an `Ingredient` for the base
    - `SmithingTrimRecipe` now takes in `Ingredient`s instead of `Optional` wrapped entries along with a `TrimPattern` holder
    - `TransmuteRecipe` now takes in a `TransmuteResult` instead of an `Item` holder
- `net.minecraft.world.item.crafting.display.SlotDisplay$SmithingTrimDemoSlotDisplay` now takes in a `TrimPattern` holder
- `net.minecraft.world.item.enchantment.EnchantmentInstance` is now a record
- `net.minecraft.world.level`
    - `BlockGetter#boxTraverseBlocks` -> `forEachBlockIntersectedBetween`, not one-to-one
    - `CustomSpawner#tick` no longer returns anything
    - `GameRules$Type` now takes in a value class
    - `Level`
        - `onBlockStateChange` -> `updatePOIOnBlockStateChange`
        - `isDay` -> `isBrightOutside`
        - `isNight` -> `isDarkOutside`
        - `setMapData` -> `net.minecraft.server.level.ServerLevel#setMapData`
        - `getFreeMapId` -> `net.minecraft.server.level.ServerLevel#getFreeMapId`
    - `LevelAccessor#blockUpdated` -> `updateNeighborsAt`
- `net.minecraft.world.level.biome.MobSpawnSettings$SpawnerData` is now a record
- `net.minecraft.world.level.block`
    - `AttachedStemBlock` now extends `VegetationBlock`
    - `AzaleaBlock` now extends `VegetationBlock`
    - `Block#fallOn` now takes a double for the fall damage instead of a float
    - `BushBlock` now extends `VegetationBlock` and implements `BonemealableBlock`
    - `ColoredFallingBlock#dustColor` is now protected
    - `CropBlock` now extends `VegetationBlock`
    - `DeadBushBlock` -> `DryVegetationBlock`
    - `DoublePlantBlock` now extends `VegetationBlock`
    - `FallingBlock#getDustColor` is now abstract
    - `FlowerBedBlock` now extends `VegetationBlock`
    - `FlowerBlock` now extends `VegetationBlock`
    - `FungusBlock` now extends `VegetationBlock`
    - `LeafLitterBlock` now extends `VegetationBlock`
    - `LeavesBlock` is now abstract, taking in the chance for a particle to spawn
        - Particles are spawned via `spawnFallingLeavesParticle`
    - `MangroveLeavesBlock` now extends `TintedParticleLeavesBlock`
    - `MushroomBlock` now extends `VegetationBlock`
    - `NetherSproutsBlock` now extends `VegetationBlock`
    - `NetherWartBlock` now extends `VegetationBlock`
    - `ParticleLeavesBlock` -> `LeafLitterBlock`
    - `PinkPetalsBlock` -> `FlowerBedBlock`
    - `RootsBlock` now extends `VegetationBlock`
    - `Rotation` now has an index used for syncing across the network
    - `SaplingBlock` now extends `VegetationBlock`
    - `SeagrassBlock` now extends `VegetationBlock`
    - `SeaPickleBlock` now extends `VegetationBlock`
    - `StemBlock` now extends `VegetationBlock`
    - `SweetBerryBushBlock` now extends `VegetationBlock`
    - `TallGrassBlock` now extends `VegetationBlock`
    - `TntBlock#prime` now returns whether the primed tnt was spawned.
    - `WaterlilyBlock` now extends `VegetationBlock`
- `net.minecraft.world.level.block.entity`
    - `BlockEntity`
        - `parseCustomNameSafe` now takes in a nullable `Tag` instead of a string
        - `getPosFromTag` now takes in the `ChunkPos`
        - `$ComponentHolder#COMPONENTS_CODEC` is now a `MapCodec`
    - `BLockEntityType#create` is no longer nullable
- `net.minecraft.world.level.block.entity.trialspawner.TrialSpawner#codec` now returns a `MapCodec`
- `net.minecraft.world.level.block.state.StateHolder`
    - `getNullableValue` is now private
    - `hasProperty` no longer contains a generic
- `net.minecraft.world.level.chunk`
    - `ChunkAccess#setBlockState` now takes in the block flags instead of a boolean, and has an overload to update all set
    - `LevelChunk#replaceWithPacketData` now takes in a `Map<Heightmap$Types, long[]>` instead of a `CompoundTag`
- `net.minecraft.world.level.chunk.storage.SerializableChunkData#getChunkTypeFromTag` -> `getChunkStatusFromTag`, not one-to-one
- `net.minecraft.world.level.gameevent.vibrations.VibrationSystem#DEFAULT_VIBRATION_FREQUENCY` -> `NO_VIBRATION_FREQUENCY`
- `net.minecraft.world.level.levelgen.feature.TreeFeature#isVine` is now public
- `net.minecraft.world.level.levelgen.structure.pools.alias`
    - `Direct` -> `DirectPoolAlias`
    - `Random` -> `RandomPoolAlias`
    - `RandomGroup` -> `RandomGroupPoolAlias`
- `net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$JigsawBlockInfo` now takes in a `ResourceKey` to the `StructureTemplatePool` instead of a raw `ResourceLocation`
- `net.minecraft.world.level.saveddata.maps.MapFrame` is now a record
    - `save`, `load` -> `CODEC`, not one-to-one
- `net.minecraft.world.level.storage.loot.functions.SetWrittenBookPagesFunction#PAGE_CODEC` -> `WrittenBookContent#PAGES_CODEC`
- `net.minecraft.world.scores`
    - `Score#write` -> `CODEC`, not one-to-one
    - `Scoreboard`
        - `savePlayerScores` -> `packPlayerScores`, not one-to-one
        - `loadPlayerScores` -> `loadPlayerScore`, not one-to-one
    - `Team$CollisionRule`, `$Visibility` are now `StringRepresentable`
- `net.minecraft.world.phys.shapes.EntityCollisionContext` now takes in a boolean representing if it is used for placing a block
- `net.minecraft.world.ticks.SavedTick`
    - `loadTick`, `saveTick`, `save` -> `codec`, not one-to-one
    - `loadTickList` -> `filterTickListForChunk`, not one-to-one

### List of Removals

- `com.mojang.blaze3d.vertex.BufferUploader`
- `net.minecraft.core.Rotations#getWrapped*`
- `net.minecraft.network.chat.ComponentSerialization#FLAT_CODEC`
- `net.minecraft.network.protocol.game`
    - `ClientboundAddExperimentOrbPacket`
    - `ClientGamePacketListener#handleAddExperienceOrb`
- `net.minecraft.resources.ResourceLocation$Serializer`
- `net.minecraft.server.network.ServerGamePacketListenerImpl#addPendingMessage`
- `net.minecraft.world`
    - `BossEvent$BossBarColor#byName`, `$BossBarOverlay#byName`
    - `Clearable#tryClear`
- `net.minecraft.world.effect.MobEffectInstance#save`, `load`
- `net.minecraft.world.entity`
    - `Entity`
        - `isInBubbleColumn`
        - `isInWaterRainOrBubble`, `isInWaterOrBubble`
        - `newDoubleList`, `newFloatList`
        - `recordMovementThroughBlocks`
    - `EntityEvent#ATTACK_BLOCKED`, `SHIELD_DISABLED`
    - `ItemBasedSteering`
        - `addAdditionalSaveData`, `readAdditionalSaveData`
        - `setSaddle`, `hasSadddle`
    - `LivingEntity`
        - `timeOffs`, `rotOffs`
        - `rotA`
        - `oRun`, `run`
        - `animStep`, `animStep0`
        - `appliedScale`
        - `canBeNameTagged`
    - `Mob`
        - `DEFAULT_EQUIPMENT_DROP_CHANCE`
        - `PRESERVE_ITEM_DROP_CHANCE_THRESHOLD`, `PRESERVE_ITEM_DROP_CHANCE`
    - `NeutralMob#setLastHurtByPlayer`
    - `PositionMoveRotation#ofEntityUsingLerpTarget`
- `net.minecraft.world.entity.ai.attributes.AttributeModifier#save`, `load`
- `net.minecraft.world.entity.animal`
    - `Dolphin#setTreasurePos`, `getTreasurePos`
    - `Fox$Variant#byName`
    - `MushroomCow$Variant#byName`
    - `Panda$Gene#byName`
    - `Salmon$Variant#byName`
    - `Turtle`
        - `getHomePos`
        - `setTravelPos`, `getTravelPos`
        - `isGoingHome`, `setGoingHome`
        - `isTravelling`, `setTravelling`
- `net.minecraft.world.entity.animal.armadillo.Armadillo$ArmadilloState#fromName`
- `net.minecraft.world.entity.npc.VillagerTrades#EXPERIMENTAL_WANDERING_TRADER_TRADES`
- `net.minecraft.world.entity.projectile.AbstractArrow#getBaseDamage`
- `net.minecraft.world.entity.raid.Raid`
    - `getLevel`, `getId`
    - `save`
- `net.minecraft.world.entity.vehicle.AbstractMinecart#hasCustomDisplay`, `setCustomDisplay`
- `net.minecraft.world.item.ItemStack#parseOptional`, `saveOptional`
- `net.minecraft.world.item.equipment.trim.TrimPattern#templateItem`
- `net.minecraft.world.level.Level#updateNeighborsAt(BlockPos, Block)`
- `net.minecraft.world.level.block.entity`
    - `CampfireBlockEntity#dowse`
    - `PotDecorations#save`, `load`
- `net.minecraft.world.level.levelgen.BelowZeroRetrogen#read`
- `net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece$VerticalPlacement#byName`
- `net.minecraft.world.level.saveddata.maps.MapBanner#LIST_CODEC`
- `net.minecraft.world.scores.Team`
    - `$CollisionRule#byName`
    - `$Visibility#getAllNames`, `byName`
- `net.minecraft.world.ticks.LevelChunkTicks#save`, `load`

# Minecraft 1.21.2/3 -> 1.21.4 Mod Migration Primer

This is a high level, non-exhaustive overview on how to migrate your mod from 1.21.2/3 to 1.21.4. This does not look at any specific mod loader, just the changes to the vanilla classes.

This primer is licensed under the [Creative Commons Attribution 4.0 International](http://creativecommons.org/licenses/by/4.0/), so feel free to use it as a reference and leave a link so that other readers can consume the primer.

If there's any incorrect or missing information, please file an issue on this repository or ping @ChampionAsh5357 in the Neoforged Discord server.

## Pack Changes

There are a number of user-facing changes that are part of vanilla which are not discussed below that may be relevant to modders. You can find a list of them on [Misode's version changelog](https://misode.github.io/versions/?id=1.21.4&tab=changelog).

## Client Items

Minecraft has moved the lookup and definition of how an item should be rendered to its own data generated system, which will be referred to as **Client Items**, located at `assets/<namespace>/items/<path>.json`. Client Items is similar to the block state model definition, but has the potential to have more information enscribed in the future. Currently, it functions as simply a linker to the models used for rendering.

All client items contain some `ItemModel$Unbaked` using the `model` field. Each unbaked model has an associated type, which defines how the item should be set up for rendering, or rendered in one specific case. These `type`s can be found within `ItemModels`. This primer will review all but one type, as that unbaked model type is specifically for bundles when selecting an item.

The item also contains a `properties` field which holds some metadata-related parameters. Currently, it only specifies a boolean that, when false, make the hand swap the currently held item instantly rather than animate the hand coming up.

```json5
// For some item 'examplemod:example_item'
// JSON at 'assets/examplemod/items/example_item.json'
{
    "model": {
        "type": "" // Set type here
        // Add additional parameters
    },
    "properties": {
        // When false, disables animation when swapping this item into the hand
        "hand_animation_on_swap": false
    }
}
```

### A Basic Model

The basic model definition is handled by the `minecraft:model` type. This contains two fields: `model`, to define the relative location of the model JSON, and an optional list of `tints`, to define how to tint each index.

`model` points to the model JSON, relative to `assets/<namespace>/models/<path>.json`. In most instances, a client item defintion will look something like this:

```json5
// For some item 'examplemod:example_item'
// JSON at 'assets/examplemod/items/example_item.json'
{
    "model": {
        "type": "minecraft:model",
        // Points to 'assets/examplemod/models/item/example_item.json'
        "model": "examplemod:item/example_item"
    }
}
```

#### Tint Sources

In model JSONs, some element faces will have a `tintindex` field which references some index into the `tints` list in the `minecraft:model` unbaked model type. The list of tints are `ItemTintSource`s, which are all defined in `net.minecraft.client.color.item.*`. All defined tint sources can be found within `ItemTintSources`, like `minecraft:constant` for a constant color, or `minecraft:dye`, to use the color of the `DataComponents#DYED_COLOR` or default if not present. All tint sources must return an opaque color, though all sources typically apply this by calling `ARGB#opaque`.

```json5
// For some item 'examplemod:example_item'
// JSON at 'assets/examplemod/items/example_item.json'
{
    "model": {
        "type": "minecraft:model",
        // Points to 'assets/examplemod/models/item/example_item.json'
        "model": "examplemod:item/example_item",
        // A list of tints to apply
        "tints": [
            {
                // For when tintindex: 0
                "type": "minecraft:constant",
                // 0x00FF00 (or pure green)
                "value": 65280
            },
            {
                // For when tintindex: 1
                "type": "minecraft:dye",
                // 0x0000FF (or pure blue)
                // Only is called if `DataComponents#DYED_COLOR` is not set
                "default": 255
            }
        ]
    }
}
```

To create your own `ItemTintSource`, you need to implement the `calculate` method register the `MapCodec` associated for the `type` field. `calculate` takes in the current `ItemStack`, level, and holding entity and returns an RGB integer with an opaque alpha, defining how the layer should be tinted.

Then, the `MapCodec` needs to be registered to `ItemTintSources#ID_MAPPER`, though this field is private by default, so some access changes or reflection needs to be applied.

```java
// The item source class
public record FromDamage(int defaultColor) implements ItemTintSource {

    public static final MapCodec<FromDamage> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default").forGetter(FromDamage::defaultColor)
        ).apply(instance, FromDamage::new)
    );

    public FromDamage(int defaultColor) {
        this.defaultColor = ARGB.opaque(defaultColor);
    }

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        return stack.isDamaged() ? ARGB.opaque(stack.getBarColor()) : defaultColor;
    }

    @Override
    public MapCodec<FromDamage> type() {
        return MAP_CODEC;
    }
}

// Then, in some initialization location where ItemTintSources#ID_MAPPER is exposed
ItemTintSources.ID_MAPPER.put(
    // The registry name
    ResourceLocation.fromNamespaceAndPath("examplemod", "from_damage"),
    // The map codec
    FromDamage.MAP_CODEC
);
```

```json5
// For some object in the 'tints' array
{
    "type": "examplemod:from_damage",
    // 0x0000FF (or pure blue)
    // Only is called if the item has not been damaged yet
    "default": 255
}
```

### Ranged Property Model

Ranged property models, as defined by the `minecraft:range_dispatch` unbaked model type, are the most similar to the previous item override system. Essentially, the type defines some item property that can be scaled along with a list of thresholds and associated models. The model chosen is the one with the closest threshold value that is not over the property (e.g. if the property value is `4` and we have thresholds `3` and `5`, `3` would be chosen as it is the cloest without going over). The item property is defined via a `RangeSelectItemModelProperty`, which takes in the stack, level, entity, and some seeded value to get a float, usually scaled betwen 0 and 1 depending on the implementation. All properties can be found within `net.minecraft.client.renderer.item.properties.numeric.*` and are registered in `RangeSelectItemModelProperties`, such as `minecraft:cooldown`, for the cooldown percentage, or `minecraft:count`, for the current number of items in the stack or percentage of the max stack size when normalized.

```json5
// For some item 'examplemod:example_item'
// JSON at 'assets/examplemod/items/example_item.json'
{
    "model": {
        "type": "minecraft:range_dispatch",

        // The `RangeSelectItemModelProperty` to use
        "property": "minecraft:count",
        // A scalar to multiply to the computed property value
        // If count was 0.3 and scale was 0.2, then the threshold checked would be 0.3*0.2=0.06
        "scale": 1,
        "fallback": {
            // The fallback model to use if no threshold matches
            // Can be any unbaked model type
            "type": "minecraft:model",
            "model": "examplemod:item/example_item"
        },

        // ~~ Properties defined by `Count` ~~
        // When true, normalizes the count using its max stack size
        "normalize": true,

        // ~~ Entries with threshold information ~~
        "entries": [
            {
                // When the count is a third of its current max stack size
                "threshold": 0.33,
                "model": {
                    // Can be any unbaked model type
                }
            },
            {
                // When the count is two thirds of its current max stack size
                "threshold": 0.66,
                "model": {
                    // Can be any unbaked model type
                }
            }
        ]
    }
}
```

To create your own `RangeSelectItemModelProperty`, you need to implement the `get` method register the `MapCodec` associated for the `type` field. `get` takes in the stack, level, entity, and seeded value and returns an arbitrary float to be interpreted by the ranged dispatch model.

Then, the `MapCodec` needs to be registered to `RangeSelectItemModelProperties#ID_MAPPER`, though this field is private by default, so some access changes or reflection needs to be applied.

```java
// The ranged property class
public record AppliedEnchantments() implements RangeSelectItemModelProperty {

    public static final MapCodec<AppliedEnchantments> MAP_CODEC = MapCodec.unit(new AppliedEnchantments());

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        return (float) stack.getEnchantments().size();
    }

    @Override
    public MapCodec<AppliedEnchantments> type() {
        return MAP_CODEC;
    }
}

// Then, in some initialization location where RangeSelectItemModelProperties#ID_MAPPER is exposed
RangeSelectItemModelProperties.ID_MAPPER.put(
    // The registry name
    ResourceLocation.fromNamespaceAndPath("examplemod", "applied_enchantments"),
    // The map codec
    AppliedEnchantments.MAP_CODEC
);
```

```json5
// For some client item in 'model'
{
    "type": "minecraft:range_dispatch",

    // The `RangeSelectItemModelProperty` to use
    "property": "examplemod:applied_enchantments",
    // A scalar to multiply to the computed property value
    "scale": 0.5,
    "fallback": {
        // The fallback model to use if no threshold matches
        // Can be any unbaked model type
        "type": "minecraft:model",
        "model": "examplemod:item/example_item"
    },

    // ~~ Properties defined by `AppliedEnchantments` ~~
    // N/A (no arguments to constructor)

    // ~~ Entries with threshold information ~~
    "entries": [
        {
            // When there is one enchantment present
            // Since 1 * the scale 0.5 = 0.5
            "threshold": 0.5,
            "model": {
                // Can be any unbaked model type
            }
        },
        {
            // When there are two enchantments present
            "threshold": 1,
            "model": {
                // Can be any unbaked model type
            }
        }
    ]
}
```

### Select Property Model

Select property models, as defined by the `minecraft:select` unbaked model type, are functionally similar to ranged property models, except now it switches on some property, typically an enum. The item property is defined via a `SelectItemModelProperty`, which takes in the stack, level, entity, some seeded value, and the current display context to get one of the property values. All properties can be found within `net.minecraft.client.renderer.item.properties.select.*` and are registered in `SelectItemModelProperties`, such as `minecraft:block_state`, for the stringified value of a specified block state property, or `minecraft:display_context`, for the current `ItemDisplayContext`.

```json5
// For some item 'examplemod:example_item'
// JSON at 'assets/examplemod/items/example_item.json'
{
    "model": {
        "type": "minecraft:select",

        // The `SelectItemModelProperty` to use
        "property": "minecraft:display_context",
        "fallback": {
            // The fallback model to use if no threshold matches
            // Can be any unbaked model type
            "type": "minecraft:model",
            "model": "examplemod:item/example_item"
        },

        // ~~ Properties defined by `DisplayContext` ~~
        // N/A (no arguments to constructor)

        // ~~ Switch cases based on Selectable Property ~~
        "cases": [
            {
                // When the display context is `ItemDisplayContext#GUI`
                "when": "gui",
                "model": {
                    // Can be any unbaked model type
                }
            },
            {
                // When the display context is `ItemDisplayContext#FIRST_PERSON_RIGHT_HAND`
                "when": "firstperson_righthand",
                "model": {
                    // Can be any unbaked model type
                }
            }
        ]
    }
}
```

To create your own `SelectItemModelProperty`, you need to implement the `get` method register the `SelectItemModelProperty$Type` associated for the `type` field. `get` takes in the stack, level, entity, seeded value, and display context and returns an encodable object to be interpreted by the select model.

Then, the `MapCodec` needs to be registered to `SelectItemModelProperties#ID_MAPPER`, though this field is private by default, so some access changes or reflection needs to be applied.

```java
// The select property class
public record StackRarity() implements SelectItemModelProperty<Rarity> {

    public static final SelectItemModelProperty.Type<StackRarity, Rarity> TYPE = SelectItemModelProperty.Type.create(
        // The map codec for this property
        MapCodec.unit(new StackRarity()),
        // The codec for the object being selected
        Rarity.CODEC
    );

    @Nullable
    @Override
    public Rarity get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        // When null, uses the fallback model
        return stack.get(DataComponents.RARITY);
    }

    @Override
    public SelectItemModelProperty.Type<StackRarity, Rarity> type() {
        return TYPE;
    }
}

// Then, in some initialization location where SelectItemModelProperties#ID_MAPPER is exposed
SelectItemModelProperties.ID_MAPPER.put(
    // The registry name
    ResourceLocation.fromNamespaceAndPath("examplemod", "rarity"),
    // The property type
    StackRarity.TYPE
);
```

```json5
// For some item 'examplemod:example_item'
// JSON at 'assets/examplemod/items/example_item.json'
{
    "model": {
        "type": "minecraft:select",

        // The `SelectItemModelProperty` to use
        "property": "examplemod:rarity",
        "fallback": {
            // The fallback model to use if no threshold matches
            // Can be any unbaked model type
            "type": "minecraft:model",
            "model": "examplemod:item/example_item"
        },

        // ~~ Properties defined by `StackRarity` ~~
        // N/A (no arguments to constructor)

        // ~~ Switch cases based on Selectable Property ~~
        "cases": [
            {
                // When rarity is `Rarity#UNCOMMON`
                "when": "uncommon",
                "model": {
                    // Can be any unbaked model type
                }
            },
            {
                // When rarity is `Rarity#RARE`
                "when": "rare",
                "model": {
                    // Can be any unbaked model type
                }
            }
        ]
    }
}
```

### Conditional Property Model

Conditional property models, as defined by the `minecraft:condition` unbaked model type, are functionally similar to ranged property models, except now it switches on boolean. These are usually combined with range dispatch, such as when pulling the bow. The item property is defined via a `ConditionalItemModelProperty`, which takes in the stack, level, entity,  some seeded value, and the current display context to get a true of false statement. All properties can be found within `net.minecraft.client.renderer.item.properties.conditional.*` and are registered in `ConditionalItemModelProperties`, such as `minecraft:damaged`, for if the item is damaged, or `minecraft:has_component`, if it has a given data component.

```json5
// For some item 'examplemod:example_item'
// JSON at 'assets/examplemod/items/example_item.json'
{
    "model": {
        "type": "minecraft:condition",

        // The `SelectItemModelProperty` to use
        "property": "minecraft:damaged",

        // ~~ Properties defined by `Damaged` ~~
        // N/A (no arguments to constructor)

        // ~~ What the boolean outcome is ~~
        "on_true": {
            // Can be any unbaked model type
        },
        "on_false": {
            // Can be any unbaked model type
        }
    }
}
```

To create your own `ConditionalItemModelProperty`, you need to implement the `get` method register the `MapCodec` associated for the `type` field. `get` takes in the stack, level, entity, seeded value, and display context and returns a boolean to be interpreted by `on_true` and `on_false`, respectively.

Then, the `MapCodec` needs to be registered to `ConditionalItemModelProperties#ID_MAPPER`, though this field is private by default, so some access changes or reflection needs to be applied.

```java
// The predicate property class
public record TimePeriod(int month, MinMaxBounds.Ints dates, boolean enabled) implements ConditionalItemModelProperty {

    public static final MapCodec<TimePeriod> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.intRange(1, 12).fieldOf("month").forGetter(TimePeriod::month),
            MinMaxBounds.Ints.CODEC.fieldOf("dates").forGetter(TimePeriod::dates)
        ).apply(instance, TimePeriod::new)
    );

    public TimePeriod(int month, MinMaxBounds.Ints dates) {
        this.month = month;
        this.dates = dates;

        Calendar cal = Calendar.getInstance();
        this.enabled = cal.get(Calendar.MONTH) + 1 == this.month
            && this.dates.matches(cal.get(Calendar.DATE));
    }

    @Override
    public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext context) {
        return this.enabled;
    }

    @Override
    public MapCodec<TimePeriod> type() {
        return MAP_CODEC;
    }
}

// Then, in some initialization location where ConditionalItemModelProperties#ID_MAPPER is exposed
ConditionalItemModelProperties.ID_MAPPER.put(
    // The registry name
    ResourceLocation.fromNamespaceAndPath("examplemod", "time_period"),
    // The map codec
    TimePeriod.MAP_CODEC
);
```

```json5
// For some item 'examplemod:example_item'
// JSON at 'assets/examplemod/items/example_item.json'
{
    "model": {
        "type": "minecraft:condition",

        // The `SelectItemModelProperty` to use
        "property": "examplemod:time_period",

        // ~~ Properties defined by `TimePeriod` ~~
        // Month of July
        "month": 7,
        "dates": {
            // Between July 1st - 14th
            "min": 1,
            "max": 14
        },

        // ~~ What the boolean outcome is ~~
        "on_true": {
            // Can be any unbaked model type
        },
        "on_false": {
            // Can be any unbaked model type
        }
    }
}
```

### Composite Model

Composite models, defined by `minecraft:composite`, are essentially a combination of other model types to render. Specifically, this sets up multiple players to overlay models on top of one another when rendering.

```json5
// For some item 'examplemod:example_item'
// JSON at 'assets/examplemod/items/example_item.json'
{
    "model": {
        "type": "minecraft:composite",

        // Will render in the order they appear in the list
        "models": [
            {
                // Can be any unbaked model type
            },
            {
                // Can be any unbaked model type
            }
        ]
    }
}
```

### Special Dynamic Models

Special dynamic models, as defined by the `minecraft:special` unbaked model type, are the new system for block entity without level renderers (e.g., chests, banners, and the like). Instead of storing a baked model, these provide a render method to call. The special model wrapper takes in a base model used for grabbing the basic model settings (not the elements) and a `SpecialModelRenderer`. All special model renderers can be found within `net.minecraft.client.renderer.special.*` and are registered in `SpecialModelRenderers`.

```json5
// For some item 'examplemod:example_item'
// JSON at 'assets/examplemod/items/example_item.json'
{
    "model": {
        "type": "minecraft:special",

        // The model to read the particle texture and display transformation from
        "base": "minecraft:item/template_skull",
        "model": {
            // The special model renderer to use
            "type": "minecraft:head",

            // ~~ Properties defined by `SkullSpecialRenderer.Unbaked` ~~
            // The type of the skull block
            "kind": "wither_skeleton"
        }
    }
}
```

To create your own `SpecialModelRenderer`, you need to implement both the renderer and the `$Unbaked` model to read the data from the JSON. The `$Unbaked` model creates the `SpecialModelRenderer` via `bake` and is registered using a `MapCodec` for its `type`. The `SpecialModelRenderer` then extracts the necessary data from the stack needed to render via `extractArgument` and passes that to the `render` method. If you do not need any information from the stack, you can implement `NoDataSpecialModelRenderer` instead.

Then, the `MapCodec` needs to be registered to `SpecialModelRenderers#ID_MAPPER`, though this field is private by default, so some access changes or reflection needs to be applied.

If your item is a held block, it also needs to be added to the `SpecialModelRenderers#STATIC_BLOCK_MAPPING` for specific rendering scenarios via `BlockRenderDispatcher#renderSingleBLock` (e.g. in minecart, or picked up by endermen). Both the default model renderer and the special model renderer are called in this method; allowing for both the static block model and the dynamic special model to be rendered at the same time. As this map is immutable, you will either need to replace it or hook into `SpecialBlockModelRenderer` and somehow add to the stored map there.

```java
// The special renderer
public record SignSpecialRenderer(WoodType defaultType, Model model) implements SpecialModelRenderer<WoodType> {

    // Render the model
    @Override
    public void render(@Nullable WoodType type, ItemDisplayContext displayContext, PoseStack pose, MultiBufferSource bufferSource, int light, int overlay, boolean hasFoil) {
        VertexConsumer consumer = Sheets.getSignMaterial(type).buffer(bufferSource, this.model::renderType);
        this.model.renderToBuffer(pose, consumer, light, overlay);
    }

    // Get the wood type from the stack
    @Nullable
    @Override
    public WoodType extractArgument(ItemStack stack) {
        return (stack.getItem() instanceof BlockItem item && item.getBlock() instanceof SignBlock sign)
            ? sign.type() : this.defaultType;
    }

    // The model to read the json from
    public static record Unbaked(WoodType defaultType) implements SpecialModelRenderer.Unbaked {

        public static final MapCodec<SignSpecialRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                WoodType.CODEC.fieldOf("default").forGetter(SignSpecialRenderer.Unbaked::defaultType)
            ).apply(instance, SignSpecialRenderer.Unbaked::new)
        );

        // Create the special model renderer, or null if it fails
        @Nullable
        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet modelSet) {
            return new SignSpecialRenderer(
                this.defaultType,
                SignRenderer.createSignModel(modelSet, defaultType, true)
            )
        }

        @Overrides
        public MapCodec<SignSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}

// Then, in some initialization location where SpecialModelRenderers#ID_MAPPER is exposed
SpecialModelRenderers.ID_MAPPER.put(
    // The registry name
    ResourceLocation.fromNamespaceAndPath("examplemod", "sign"),
    // The map codec
    SignSpecialRenderer.Unbaked.MAP_CODEC
);
// Let assume we can add directly to SpecialModelRenderers#STATIC_BLOCK_MAPPING as well
// We'll have a Block EXAMPLE_SIGN
SpecialModelRenderers.STATIC_BLOCK_MAPPING.put(
    // The block with a special rendering as an item
    EXAMPLE_SIGN,
    // The unbaked renderer to use
    new SignSpecialRenderer.Unbaked(WoodType.BAMBOO)
);
```

```json5
// For some item 'examplemod:example_item'
// JSON at 'assets/examplemod/items/example_item.json'
{
    "model": {
        "type": "minecraft:special",

        // The model to read the particle texture and display transformation from
        "base": "minecraft:item/bamboo_sign",
        "model": {
            // The special model renderer to use
            "type": "examplemod:sign",

            // ~~ Properties defined by `SignSpecialRenderer.Unbaked` ~~
            // The default wood type if none can be found
            "default": "bamboo"
        }
    }
}
```

### Rendering an Item

Rendering an item is now done through the `ItemModelResolver` and `ItemStackRenderState`. This is similar to how the `EntityRenderState` works: first, the `ItemModelResolver` sets up the `ItemStackRenderState`, then the state is rendered via `ItemStackRenderState#render`.

Let's start with the `ItemStackRenderState`. For rendering, the only one we care about the following methods: `isEmpty`, `isGui3d`, `usesBlockLight`, `transform`, and `render`. `isEmpty` is used to determine whether the stack should render at all. Then `isGui3d`, `usesBlockLight`, and `transform` are used in their associated contexts to properly position the stack to render. Finally, `render` takes in the pose stack, buffer source, packed light, and overlay texture to render the item in its appropriate location.

`ItemModelResolver` is responsible for setting the information on the `ItemStackRenderState` needed to render. This is done through `updateForLiving` for items held by living entities, `updateForNonLiving` for items held by other kinds of entities, and `updateforTopItem`, for all other scenarios. `updateForItem`, which the previous two methods delegate to, take in the render state, the stack, the display context, if the stack is in the left hand, the level, the entity, and some seeded value. This will effectively clear the previous state via `ItemStackRenderState#clear` and then set up the new state via a delegate to `ItemModel#update`. The `ItemModelResolver` can always be obtained via `Minecraft#getItemModelResolver`, if you are not within a renderer context (e.g., block entity, entity).

```java
// In its most simple form, assuming you are not doing any transformations (which you should as necessary)
public class ExampleRenderer {
    private final ItemStackRenderState state = new ItemStackRenderState();

    public void render(ItemStack stack, Level level, PoseStack pose, MultiBufferSource bufferSource) {
        // First update the render state
        Minecraft.getInstance().getItemModelResolver().updateForTopItem(
            // The render state
            this.state,
            // The stack to update the state with
            stack,
            // The display context to render within
            ItemDisplayContext.NONE,
            // Whether it is in the left hand of the entity (use false when unknown)
            false,
            // The current level (can be null)
            level,
            // The holding entity (can be null)
            null,
            // An arbitrary seed value
            0
        );

        // Perform any desired transformations here

        // Then render the state
        this.state.render(
            // The pose stack with the required transformations
            pose,
            // The buffer sources
            bufferSource,
            // The packed light value
            LightTexture.FULL_BRIGHT,
            // The overlay texture value
            OverlayTexture.NO_OVERLAY
        );
    }
}
```

### Custom Item Model Defintions

To make a custom item model definition, we need to look at a few more methods in `ItemStackRenderState` which, although you won't typically use, is useful to understand: `ensureCapacity` and `newLayer`. Both of these are responsible for ensuring there are enough `ItemStackRenderState$LayerRenderState`s if you happen to be overlaying multiple models at once. Effectively, every time you are planning to render something in an unbaked model, `newLayer` should be called. If you plan on rendering multiple things on top of one another, then `ensureCapacity` should be set with the number of layers that you plan to render before calling `newLayer`.

An item model definition is made up of the `ItemModel`, which functionally defines a 'baked model' and its `ItemModel$Unbaked`, used for serialization.

The unbaked variant has two methods: `bake`, which is used to create the `ItemModel`, and `type`, which references the `MapCodec` to be registered to `ItemModels#ID_MAPPER`, though this field is private by default, so some access changes or reflection needs to be applied. `bake` takes in the `$BakingContext`, which contains the `ModelBaker` to get `BakedModel`s, the `EntityModelSet` for entity models, and the missing `ItemModel`.

The baked variant only has one method `update`, which is responsible for setting up everything necessary on the `ItemStackRenderState`. The model does no rendering itself.

```java
public record RenderTypeModelWrapper(BakedModel model, RenderType type) implements ItemModel {

    // Update the render state
    @Override
    public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        ItemStackRenderState.LayerRenderState layerState = state.newLayer();
        if (stack.hasFoil()) {
            layerState.setFoilType(ItemStackRenderState.FoilType.STANDARD);
        }
        layerState.setupBlockModel(this.model, this.type);
    }

    public static record Unbaked(ResourceLocation model, RenderType type) implements ItemModel.Unbaked {
        // Create a render type map for the codec
        private static final BiMap<String, RenderType> RENDER_TYPES = Util.make(HashBiMap.create(), map -> {
            map.put("translucent_item", Sheets.translucentItemSheet());
            map.put("cutout_block", Sheets.cutoutBlockSheet());
        });
        private static final Codec<RenderType> RENDER_TYPE_CODEC = ExtraCodecs.idResolverCodec(Codec.STRING, RENDER_TYPES::get, RENDER_TYPES.inverse()::get);

        // The map codec to register
        public static final MapCodec<RenderTypeModelWrapper.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                ResourceLocation.CODEC.fieldOf("model").forGetter(RenderTypeModelWrapper.Unbaked::model),
                RENDER_TYPE_CODEC.fieldOf("render_type").forGetter(RenderTypeModelWrapper.Unbaked::type)
            )
            .apply(instance, RenderTypeModelWrapper.Unbaked::new)
        );

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
            // Resolve model dependencies, so pass in all known resource locations
            resolver.resolve(this.model);
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext context) {
            // Get the baked model and return
            BakedModel baked = context.bake(this.model);
            return new RenderTypeModelWrapper(baked, this.type);
        }

        @Override
        public MapCodec<RenderTypeModelWrapper.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}

// Then, in some initialization location where ItemModels#ID_MAPPER is exposed
ItemModels.ID_MAPPER.put(
    // The registry name
    ResourceLocation.fromNamespaceAndPath("examplemod", "render_type"),
    // The map codec
    RenderTypeModelWrapper.Unbaked.MAP_CODEC
);
```

```json5
// For some item 'examplemod:example_item'
// JSON at 'assets/examplemod/items/example_item.json'
{
    "model": {
        "type": "examplemod:render_type",
        // Points to 'assets/examplemod/models/item/example_item.json'
        "model": "examplemod:item/example_item",
        // Set the render type to use when rendering
        "render_type": "cutout_block"
    }
}
```

- `net.minecraft.client`
    - `ClientBootstrap` - Registers the maps backing the client; currently used for item model definitions.
    - `Minecraft`
        - `getEquipmentModels` is removed, only directly accessible in the `EntityRendererProvider$Context#getEquipmentAssets`
        - `getItemModelResolver` - Returns the updater for resolving the current model to be rendered in the `ItemStackRenderState$LayerRenderState`.
    - `KeyMapping#get` - Gets a key mapping based on its translation key.
- `net.minecraft.client.color.item`
    - `Constant` - A constant to tint the item texture.
    - `CustomModelDataSource` - Gets the color to tint based on an index in the `DataComponent#CUSTOM_MODEL_DATA` data component. If no index is found or is out of bounds, then the default color is used.
    - `Dye` - Gets the color to tint using the `DataComponent#DYED_COLOR` data component.
    - `Firework` - Gets the color to tint using the `DataComponent#FIRE_EXPLOSION` data component.
    - `GrassColorSource` - Gets the color to tint based on the provided temperature and downfall values.
    - `ItemColor` -> `ItemTintSource`, not one-to-one as indexing is setup by providing multiple `ItemTintSource`s in the model list.
    - `ItemColors` class is removed, now data generated as `ItemTintSource`s
    - `ItemTintSources` - A registry of sources for tinting an item texture in a model.
    - `MapColor` - Gets the color to tint using the `DataComponent#MAP_COLOR` data component.
    - `Potion` - Gets the color to tint using the `DataComponent#POTION_CONTENTS` data component.
    - `TeamColor` - Gets the color based on the holding entity's team color.
- `net.minecraft.client.data.Main` - The entrypoint for client data generation.
- `net.minecraft.client.particle.BreakingItemParticle` now takes in an `ItemStackRenderState` instead of an `ItemStack`
    - `$ItemParticleProvider` - An abstract particle provide that provides a simple method to calculate the `ItemStackRenderState`.
- `net.minecraft.client.renderer`
    - `BlockEntityWithoutLevelRenderer` class is removed, replaced by the `NoDataSpecialModelRenderer` datagen system
    - `ItemInHandRenderer` now takes in an `ItemModelResolver`
    - `ItemModelShaper` is removed, as the methods are available within the `ModelManager`
    - `Sheets`
        - `getBedMaterial` - Gets the bed material from the dye color.
        - `colorToResourceMaterial` - Gets the resource location of the dye color.
        - `createBedMaterial` - Creates the bed material from the dye color or resource location.
        - `getShulkerBoxMaterial` - Gets the shulker box material from the dye color.
        - `colorToShulkerMaterial` - Gets the resource location of the dye color for the shulker box.
        - `createShulkerMaterial` - Creates the shulker box material from the dye color or resource location.
        - `chestMaterial` - Creates a new material for a chest with the given resource location.
    - `SpecialBlockModelRenderer` - A map of blocks to special renderers for item variants.
- `net.minecraft.client.renderer.block.BlockRenderDispatcher` now takes in a supplied `SpecialBlockModelRenderer` instead of a `BlockEntityWithoutLevelRenderer`
- `net.minecraft.client.renderer.block.model`
    - `BakedOverrides` class is removed, replaced by the `RangeSelectItemModelProperty` datagen system
    - `BlockModel` now takes in a `TextureSlots$Data` instead of just a material map, and no longer takes in a list of `ItemOverride`s
        - `MISSING_MATERIAL` is removed, replaced by `minecraft:missingno`
        - `textureMap` -> `textureSlots`, now private, not one-to-one
        - `parent` is now private, not one-to-one
        - `parentLocation` is now private
        - `hasAmbientOcclusion` -> `getAmbientOcclusion`
        - `isResolved` is removed
        - `getOverrides` is removed
        - `getParent` - Returns the unbaked parent model.
        - `getTextureSlots` - Returns the texture data for the model.
        - `getElements` is now package-private
        - `$GuiLight` -> `UnbakedModel$GuiLight`
    - `FaceBakery`
        - `bakeQuad` is now static
        - `calculateFacing` is now private
    - `ItemModelGenerator` now implements `UnbakedModel`
    - `ItemOverride` class is removed, replaced by the `RangeSelectItemModelProperty` datagen system
    - `ItemTransforms` is now a record
        - `hasTransform` is removed
    - `TextureSlots` - A class which handles the texture mapping within a model. The data is read from `$Data` and stored as `$SlotContents` until it is resolved during the baking process into `Material`s.
    - `UnbakedBlockStateModel` now extends `ResolvableModel` instead of `UnbakedModel`
        - `bake` - Bakes the block state into its selectable models.
    - `Variant` is now a record
- `net.minecraft.client.renderer.blockentity`
    - `BannerRenderer` now has an overload constructor which takes in the `EntityModelSet`
        - `renderInHand` - Renders the item model of the banner.
    - `BedRenderer` now has an overload constructor which takes in the `EntityModelSet`
        - `renderInHand` - Renders the item model of the bed.
    - `BlockEntityRenderDispatcher(Font, EntityModelSet, Supplier<BlockRenderDispatcher>, Supplier<ItemRenderer>, Supplier<EntityRenderDispatcher>)` -> `BlockEntityRenderDispatcher(Font, Supplier<EntityModelSet>, BlockRenderDispatcher, ItemModelResolver, ItemRenderer, EntityRenderDispatcher)`
        - `renderItem` is removed, implemented in their specific classes
    - `BlockEntityRendererProvider` now takes in an `ItemModelResolver`
        - `getItemModelResolver` - Gets the resolver which returns the item models.
    - `ChestRenderer#xmasTextures` - Returns whether christmas textures should render on a chest.
    - `DecoratedPotRenderer` now has an overload constructor which takes in the `EntityModelSet`
        - `renderInHand` - Renders the item model of the pot.
    - `ShulkerBoxRenderer` now has an overload constructor which takes in the `EntityModelSet`
        - `render` - Renders the shulker box.
        - `$ShulkerBoxModel#animate` no longer takes in the `ShulkerBoxBlockEntity`
    - `SkullblockRenderer#createSkullRenderers` -> `createModel`, not one-to-one
- `net.minecraft.client.renderer.entity`
    - `EntityRenderDispatcher` now takes in an `IteModelResolver`, a supplied `EntityModelSet` instead of the instance, and an `EquipmentAssetManager` instead of a `EquipmentModelSet`
    - `EntityRendererProvider$Context` now takes in an `ItemModelResolver` instead of an `ItemRenderer`, and an `EquipmentAssetManager` instead of a `EquipmentModelSet`
        - `getItemRenderer` -> `getItemModelResolver`, not one-to-one
        - `getEquipmentModels` -> `getEquipmentAssets`
    - `FishingHookRenderer` - Returns the holding arm of the fishing hook.
    - `HumanoidMobRenderer`
        - `getArmPose` - Returns the arm pose of the entity.
        - `extractHumanoidRenderState` now takes in an `ItemModelResolver`
    - `ItemEntityRenderer`
        - `getSeedForItemStack` is removed
        - `renderMultipleFromCount` now takes in the `ItemClusterRenderState`, and removes the `ItemRenderer`, `ItemStack`, `BakedModel`, and 3d boolean
    - `ItemRenderer` no longer implements `ResourceManagerReloadListener`
        - The constructor now only takes in the `ItemModelResolver`
        - `render` -> `renderItem`, not one-to-one
        - `renderBundleItem` is removed
        - `getModel`, `resolveItemModel` is removed
    - `LivingEntityRenderer#itemRenderer` -> `itemModelResolver`, not one-to-one
    - `OminousItemSpawnerRenderer` now uses the `ItemClusterRenderState`
    - `SkeletonRenderer#getArmPose` -> `AbstractSkeletonRenderer#getArmPose`
    - `SnowGolemRenderer` now uses the `SnowGolemRenderState`
- `net.minecraft.client.renderer.entity.layers`
    - `CrossArmsItemLayer` now uses the `HoldingEntityRenderState`
    - `CustomHeadLayer` no longer takes in the `ItemRenderer`
    - `DolphinCarryingItemLayer` no longer takes in the `ItemRenderer`
    - `EquipmentLayerRenderer$TrimSpriteKey` now takes in a `ResourceKey<EquipmentAsset>`
        - `textureId` - Gets the texture id for the trim.
    - `FoxHeldItemLayer` no longer takes in the `ItemRenderer`
    - `ItemInHandLayer` now uses the `ArmedEntityRenderState`
        - The constructor no longer takes in the `ItemRenderer`
        - `renderArmWithItem` no longer takes in the `BakedModel`, `ItemStack`, or `ItemDisplayContext` and instead the `ItemStackRenderState`
    - `LivingEntityEmissiveLayer` now takes in a boolean which determines whether the layer is always visible
    - `PandaHoldsItemLayer` no longer takes in the `ItemRenderer`
    - `PlayerItemInHandLayer` no longer takes in the `ItemRenderer`
        - `renderArmWithItem` no longer takes in the `BakedModel`, `ItemStack`, or `ItemDisplayContext` and instead the `ItemStackRenderState`
    - `SnowGolemHeadLayer` now uses the `SnowGolemRenderState`
    - `WitchItemLayer` no longer takes in the `ItemRenderer`
- `net.minecraft.client.renderer.entity.player.PlayerRenderer#getArmPose` is now private
- `net.minecraft.client.renderer.entity.state`
    - `ArmedEntityRenderState` - A render state for an entity that holds items in their right and left hands.
    - `HoldingEntityRenderState` - A render state for an entity that holds a single item.
    - `ItemClusterRenderState` - A render state for an item that should be rendered multiple times.
    - `ItemDisplayEntityRenderState#itemRenderState`, `itemModel` -> `item`, not one-to-one
    - `ItemEntityRenderState#itemModel`, `item` -> `ItemClusterRenderState#item`, not one-to-one
    - `ItemFrameRenderState#itemStack`, `itemModel` -> `item`, not one-to-one
    - `LivingEntityRenderState`
        - `headItemModel`, `headItem` -> `headItem`, not one-to-one
        - Arm and Hand methods moved to `ArmedEntityRenderState`
    - `OminousItemSpawnerRenderState` -> `ItemClusterRenderState`
    - `PlayerRenderState`
        - `mainHandState`, `offHandState` -> `ArmedEntityRenderState` methods
        - `heldOnHead` - Represents the item stack on the head of the player.
    - `SkeletonRenderState#isHoldingBow` - Represents if the skeleton is holding a bow.
    - `SnowGolemRenderState` - The render state for the snow golem.
    - `ThrownItemRenderState#item`, `itemModel` -> `item`, not one-to-one
    - `WitchRenderState#isHoldingPotion` - Whether the witch is holding a potion or not.
- `net.minecraft.client.renderer.item`
    - `BlockModelWrapper` - The basic model definition that contains the model and its associated tints.
    - `BundleSelectedItemSpecialRenderer`- A special renderer for a stack selected by a bundle.
    - `ClampedItemPropertyFunction`, `ItemPropertyFunction` -> `.properties.numeric.*` classes depending on the situation and property
    - `ClientItem` - The base item that represents the model definition in `assets/<modid>/items`.
    - `CompositeModel` - Overlays multiple models together.
    - `ConditionalItemModel` - A model that shows a different model based on a boolean.
    - `EmptyModel` - A model that renders nothing.
    - `ItemModel` - The base item model that updates the stack render state as necessary.
    - `ItemModelResolver` - The resolver that updates the stack render state.
    - `ItemModels` - Contains all potential item models for a `ClientItem`.
    - `ItemProperties` class is removed
    - `ItemStackRenderState` - The render state representing the stack to render.
    - `MissingItemModel` - A model that represents the missing model.
    - `RangeSelectItemModel` - A model that contains some range of values that applies the associated model that meets the threshold.
    - `SelectItemModel` - An item model that switches based on the provided property.
    - `SpecialModelWrapper` - An item model for models that are rendered dynamically, such as chests.
- `net.minecraft.client.renderer.item.properties.conditional`
    - `Broken` - If the item only has one durability left.
    - `BundleHasSelectedItem` - If the bundle is holding the selected item.
    - `ConditionalItemModelProperties` - Contains all potential conditional property types.
    - `ConditionalItemModelProperty` - Represents a property that returns some boolean.
    - `CustomModelDataProperty` - If the current index is set to true within `DataComponents#CUSTOM_MODEL_DATA`.
    - `Damaged` - If the item is damaged.
    - `ExtendedView` - If the display context is a gui and the shift key is down.
    - `FishingRodCast` - If the fishing rod is being used.
    - `HasComponent` - Whether it has the associated data component.
    - `IsCarried` - If the item is being carried in the current menu.
    - `IsKeybindDown` - If the key mapping is being pressed.
    - `IsSelected` - If the item is selected in the hotbar.
    - `IsUsingItem` - If the item is being used.
    - `IsViewEntity` - Whether the holding entity is the current camera entity.
- `net.minecraft.client.renderer.item.properties.numeric`
    - `BundleFullness` - A threshold based on the bundle contents.
    - `CompassAngle` - A threshold on the currrent angle state.
    - `CompassAngleState` - A threshold based on the current compass angle towards its target.
    - `Cooldown` - A threshold based on the current cooldown percentage.
    - `Count` - A threshold based on the stack count.
    - `CrossbowPull` - A threshold based on the crossbow being pulled.
    - `CustomModelDataProperty` - If the current index has set theshold value within `DataComponents#CUSTOM_MODEL_DATA`.
    - `Damage` - A threshold based on the durability percentage remaining.
    - `NeedleDirectionHelper` - An abstract class which help point the position needle in the correct direction.
    - `RangeSelectItemModelProperties` - Contains all potential ranged property types.
    - `RangeSelectItemModelProperty` - Represents a property that returns some threshold of a float.
    - `Time` - A threshold based on the current time of day.
    - `UseCycle` - A threshold based on the remaining time left normalized to some period modulo in the stack being used.
    - `UseDuration` - A threshold based on the remaining time left in the stack being used.
- `net.minecraft.client.renderer.item.properties.select`
    - `Charge` - A case based on the charge type of a crowssbow.
    - `ContextDimension` - A case based on the dimension the item is currently within.
    - `ContextEntityType` - A case based on the holding entity's type.
    - `CustomModelDataProperty` - If the current index is set to the string within `DataComponents#CUSTOM_MODEL_DATA`.
    - `DisplayContext` - A case based on the display context.
    - `ItemBlockState` - A case based on getting a property value from an item holding the block state properties.
    - `LocalTime` - A case based on a simple date format pattern.
    - `MainHand` - A case based on the arm holding the item.
    - `SelectItemModelProperties` - Contains all potential select-cased property types.
    - `SelectItemModelProperty` - Represents a property that returns some cased selection.
    - `TrimMaterialProperty` - A case based on the trim material on the item.
- `net.minecraft.client.renderer.special`
    - `BannerSpecialRenderer` - An item renderer for a banner.
    - `BedSpecialRenderer` - An item renderer for a bed.
    - `ChestSpecialRenderer` - An item renderer for a chest.
    - `ConduitSpecialRenderer` - An item renderer for a conduit.
    - `DecoratedPotSpecialRenderer` - An item renderer for a decorated pot.
    - `HangingSignSpecialRenderer` - An item renderer for a hanging sign.
    - `NoDataSpecialModelRenderer` - An item renderer that does not need to read any data from the stack.
    - `ShieldSpecialRenderer` - An item renderer for a shield.
    - `ShulkerBoxSpecialRenderer` - An item renderer for a shulker box.
    - `SkullSpecialRenderer` - An item renderer for a skull.
    - `SpecialModelRenderer` - Represents a model that reads data from the stack and renders the object without needing the render state.
    - `SpecialModelRenderers` - Contains all potential special renderers.
    - `StandingSignSpecialRenderer` - An item renderer for a standing sign.
    - `TridentSpecialRenderer` - An item renderer for a trident.
- `net.minecraft.client.resources.model`
    - `BakedModel`
        - `isCustomRenderer` is removed, replaced by the special renderer system
        - `overrides` is removed, replaced by the properties renderer system
    - `BlockStateModelLoader` no longer takes in the missing model
        - `definitionLocationToBlockMapper` is now private
        - `loadBlockStateDefinitionStack` is now private
        - `loadBlockStates` - Gets the loaded models for the block state.
        - `$LoadedBlockModelDefinition` is now package-private
        - `$LoadedModel` now takes in an `UnbakedBlockStateModel` instead of a `UnbakedModel`
        - `$LoadedModels`
            - `forResolving` - Returns all models hat need to be resolved.
            - `plainModels` - Returns a map from the model location to the unbaked model.
    - `BuiltInModel` class is removed
    - `ClientItemInfoLoader` - Loads all models for all item stacks.
    - `EquipmentModelSet` -> `EquipmentAssetManager`
    - `ItemModel` -> `net.minecraft.client.renderer.item.ItemModel`
    - `MissingBlockModel#MISSING` is now private
    - `ModelBaker`
        - `sprites` - Returns the getter to get sprites.
        - `rootName` - Gets the name of the model for debugging.
    - `ModelBakery(Map<ModelResourceLocation, UnbakedModel>, Map<ResourceLocation, UnbakedModel>, UnbakedModel)` -> `ModelBakery(EntityModelSet, Map<ModelResourceLocation, UnbakedBlockStateModel>, Map<ResourceLocation, ClientItem>, Map<ResourceLocation, UnbakedModel>, UnbakedModel)`
        - `bakeModels` now returns a `$BakingResult`
        - `getBakedTopLevelModels` is removed
        - `$BakingResult` - Holds all models that have been lodaded.
        - `$TextureGetter`
            - `get` now takes in the `ModelDebugName` instead of the `ModelResourceLocation`
            - `reportingMissingReference` - Handles how a texture is reported when not set.
            - `bind` - Creates a spriate getter bound to the current model.
    - `ModelDebugName` - Returns the name of the model for debugging.
    - `ModelDiscovery`
        - `registerStandardModels` is removed
        - `registerSpecialModels` - Adds the internal models loaded by the system.
        - `addRoot` - Adds a new model that can be resolved.
        - `getUnreferencedModels` - Returns the difference between the models loaded vs the models used.
        - `getTopModels` is removed
    - `ModelGroupCollector$GroupKey#create` now takes in an `UnbakedBlockStateModel` instead of a `UnbakedModel`
    - `ModelManager`
        - `specialBlockModelRenderer` - Returns the renderer for special block models.
        - `entityModels` - Returns the model set for the entities.
        - `getItemProeprties` - Returns the properties for the client item based on its resource location.
    - `ModelResourceLocation#inventory` is removed
    - `ResolvableModel` - The base model, usually unbaked, that have references to resolve.
    - `SimpleBakedModel` fields are now all private
        - `bakeElements` - Bakes a model given the block elements.
        - `$Builder` no longer has an overload that takes in the `BlockModel`
    - `SpecialModels` class is removed
    - `SpriteGetter` - A getter for atlas sprites for the associated materials.
    - `UnbakedModel` is now a `ResolvableModel`
        - `bake(ModelBaker, Function<Material, TextureAtlasSprite>, ModelState)` -> `bake(TextureSlots, ModelBaker, ModelState, boolean, boolean, ItemTransforms)`
        - `getAmbientOcclusion`, `getTopAmbientOcclusion` - Returns whether ambient occlusion should be enabled on the item.
        - `getGuiLight`, `getTopGuiLight` - Returns the lighting side within a gui.
        - `getTransforms`, `getTopTransform`, `getTopTransforms` - Returns the transformations to apply based on the display context.
        - `getTextureSlots`, `getTopTextureSlots` - Returns the texture data for the model.
        - `getParent` - Returns the parent of this model.
        - `bakeWithTopModelValues` - Bakes the model.
- `net.minecraft.data.models.*` -> `net.minecraft.client.data.models.*`
- `net.minecraft.world.item`
    - `BundleItem` no longer takes in any `ResourceLocation`s
        - `openFrontModel`, `openBackModel` is removed
    - `CrossbowItem$ChargeType` - The item being charged by the crossbow.
    - `DyeColor#getMixedColor` - Returns the dye most closely representing the mixed color.
    - `Item$Properties#overrideModel` is removed
    - `SpawnEggItem` no longer takes in its tint colors
        - `getColor` is removed
- `net.minecraft.world.item.alchemy.PotionContents`
    - `getColor(*)` is removed
    - `getColorOr` - Gets a custom color fro the potion or the default if not present.
- `net.minecraft.world.item.component.CustomModelData` now takes in a list of floats, flags, strings, and colors to use in the custom model properties based on the provided index
- `net.minecraft.world.item.equipment`
    - `ArmorMaterial` now takes in a `ResourceKey<EquipmentAsset>` instead of just the model id
    - `EquipmentAsset` - A marker to represent the equipment client info key
    - `EquipmentAssets` - All vanilla equipment assets.
    - `EquipmentModel` -> `net.minecraft.client.resources.model.EquipmentClientInfo`
    - `EquipmentModels` -> `net.minecraft.client.data.models.EquipmentAssetProvider`, not one-to-one
    - `Equippable` now takes in a `ResourceKey<EquipmentAsset>` instead of just the model id
        - `$Builder#setModel` -> `setAsset`
- `net.minecraft.world.item.equipment.trim`
    - `ArmorTrim#getTexture` is removed
    - `TrimMaterial` no longer takes in an item model index, and the key over the override armor materials points to `ResourceKey<EquipmentAsset>`
- `net.minecraft.world.level.FoliageColor`
    - `getEvergreenColor` -> `FOLIAGE_EVERGREEN`
    - `getBirchColor` -> `FOLIAGE_BIRCH`
    - `getDefaultColor` -> `FOLIAGE_DEFAULT`
    - `getMangroveColor` -> `FOLIAGE_MANGROVE`
- `net.minecraft.world.level.block.RenderShape#ENTITYBLOCK_ANIMATED` is removed
- `net.minecraft.world.level.block.entity`
    - `BannerBlockEntity#fromItem` is removed
    - `BedBlockEntitty#setColor` is removed
    - `BlockEntity#saveToItem` is removed
    - `DecoratedPotBlockEntity#setFromItem`, `getPotAsItem` is removed
- `net.minecraft.world.level.storage.loot.functions.SetCustomModelDataFunction` now takes in a list of floats, flags, strings, and colors to use in the custom model properties based on the provided index

## Mob Replacing Current Items

One of the last hardcoded instances relating to tools and armor being subtypes of `DiggerItem` and `ArmorItem`, respectively, have been reworked: `Mob#canReplaceCurrentItem`. Now, it reads the `EquipmentSlot` of the stack from the `DataComponents#EQUIPPABLE` data component. Then, using that, different logic occurs depending on the situation.

For armor slots, it cannot be changed if the armor is enchanted with a `EnchantmentEffectComponents#PREVENT_ARMOR_CHANGE` effect component. Otherwise, it will attempt to compare the armor attributes first, then armor toughness if equal.

For weapons (via hand slots), it will first check if the mob has a preferred weapon type tag. If so, it will switch the item to the weapon in the tag, provided one item is in the tag and the other is not. Otherwise, it will attempt to compare attack damage attributes.

If all attributes are equal, then they will both default to the following logic. First, it will try to pick the item with the most enchantments. Then, it will attempt to pick the item with the most durability remaining (the raw value, not the percentage). Finally, it will check whether one of the items hsa a custom name via the `DataComponents#CUSTOM_NAME`.

> As a small caveat, `BambooSaplingBlock` and `BambooStalkBLock` still hardcode a check for check if the mainhand item is a `SwordItem`, though this could probably be replaced with a change to `ToolMaterial#applySwordProperties` in the future.

## Particles, rendered through Render Types

Particles are now rendered using a `RenderType`, rather than setting a buffer builder themselves. The only special cases are `ParticleRenderType#CUSTOM`, which allows the modder to implement their own rendering via `Particle#renderCustom`; and `ParticleRenderType#NO_RENDER`, which renders nothing.

To create a new `ParticleRenderType`, it can be created by passing in its name for logging and the `RenderType` to use. Then, the type is returned in `Particle#getRenderType`.

```java
public static final ParticleRenderType TERRAIN_SHEET_OPAQUE = new ParticleRenderType(
    "TERRAIN_SHEET_OPAQUE", // Typically something recognizable, like the name of the field
    RenderType.opaqueParticle(TextureAtlas.LOCATION_BLOCKS) // The Render Type to use
);
```

- `net.minecraft.client.particle`
    - `CherryParticle` -> `FallingLeavesParticle`, not one-to-one as the new class has greater configuration for its generalization
    - `ItemPickupParticle` no longer takes in the `RenderBuffers`
    - `Particle#renderCustom` - Renders particles with the `ParticleRenderType#CUSTOM` render type.
    - `ParticleEngine#render(LightTexture, Camera, float)` -> `render(Camera, float, MutliBufferSource$BufferSource)`
    - `ParticleRenderType` is now a record which takes in the name and the `RenderType` it uses.

## Minor Migrations

The following is a list of useful or interesting additions, changes, and removals that do not deserve their own section in the primer.

### `SimpleJsonResourceReloadListener`

`SimpleJsonResourceReloadListener`s now take in a converter to map some key to a resource location. An abstract has been provided for registry keys. This is done through a `FileToIdConverter`, which essentially holds a prefix and extension to apply to some `ResourceLocation`.

```java
// We will assume this is a server reload listener (meaning in the 'data' folder)
public class MyLoader extends SimpleJsonResourceReloadListener<ExampleObject> {

    public MyLoader() {
        super(
            // The codec to encode/decode the object
            ExampleObject.CODEC,
            // The file converter
            // Will place files in data/<namespace>/example/object/<path>.json
            FileToIdConverter.json(
                // The prefix
                "example/object"
            )
        );
    }

    // Below is the same
}
```

- `net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener` now takes in a resource key for a registry, or a file to id converter instead of just a string
    - `scanDirectory` now takes in a resource key for a registry, or a file to id converter instead of just a string

### MetadataSectionSerializer, replaced by Codecs

`MetadataSectionSerializer` has been removed in favor of using `Codec`s to serialize the metadata sections. As such all `MetadataSectionSerializer`s have been replaced by its `MetadataSectionType` instead, which holds the name of the section and the codec for the metadata section.

- `net.minecraft.client.renderer.texture`
    - `HttpTexture` -> `SkinTextureDownloader`, not one-to-one as the new class is just a utility that returns the content to store
    - `MissingTextureAtlasSprite`
        - `getTexture` -> `generateMissingImage`, not one-to-one
        - `getMissingImage(int, int)` is now public
    - `SpriteLoader#loadAndStitch` now takes in a collection of `MetadataSectionType`s rather than `MetadataSectionSerializer`s
- `net.minecraft.client.resources.SkinManager` no longer takes in the `TextureManager`
    - `getOrLoad` now returns an `Optional<PlayerSkin>` future instead of just the `PlayerSkin`
- `net.minecraft.client.resources.metadata.animation`
    - `AnimationFrame` is now a record
    - `AnimationMetadataSection` is now a record
    - `AnimationMetadataSectionSerializer` class is removed
    - `VillagerMetaDataSection` -> `VillagerMetadataSection`
    - `VillagerMetadataSectionSerializer` class is removed
- `net.minecraft.client.resources.metadata.texture`
    - `TextureMetadataSection` is now a record
    - `TextureMetadataSectionSerializer` class is removed
- `net.minecraft.server.packs.PackResources#getMetadataSection` now takes in a `MetadataSectionType` instead of a `MetadataSectionSerializer`
- `net.minecraft.server.packs.metadata`
    - `MetadataSectionSerializer` is removed in favor of section codecs
    - `MetadataSectionType` is a now a record instead of a `MetadataSectionSerializer` extension
- `net.minecraft.server.packs.resources.ResourceMetadata`
    - `getSection` now takes in a `MetadataSectionType` instead of a `MetadataSectionSerializer`
    - `copySections` now takes in a collection of `MetadataSectionType`s instead of a `MetadataSectionSerializer`s

### Music, now with Volume Controls

The background music is now handled through a `MusicInfo` class, which also stores the volume along with the associated `Music`.

- `net.minecraft.client.Minecraft#getSituationalMusic` now returns a `MusicInfo` instead of a `Music`
- `net.minecraft.client.sounds`
    - `MusicInfo` - A record that holds the currently playing `Music` and the volume its at.
    - `MusicManager#startPlaying` now takes in a `MusicInfo` instead of a `Music`
    - `SoundEngine#setVolume`, `SoundManager#setVolume` - Sets the volume of the associated sound instance.
- `net.minecraft.world.level.biome`
    - `Biome`
        - `getBackgroundMusic` now returns a optional `SimpleWeightedRandomList` of music.
        - `getBackgroundMusicVolume` - Gets the volume of the background music.
    - `BiomeSpecialEffects$Builder#silenceAllBackgroundMusic`, `backgroundMusic(SimpleWeightedRandomList<Music>)` - Handles setting the background music for the biome.

### Tag Changes

- `minecraft:block`
    - `tall_flowers` -> `bee_attractive`
- `minecraft:item`
    - `tall_flowers`, `flowers` is removed
    - `trim_templates` is removed
    - `skeleton_preferred_weapons`
    - `drowned_preferred_weapons`
    - `piglin_preferred_weapons`
    - `pillager_preferred_weapons`
    - `wither_skeleton_disliked_weapons`

### List of Additions

- `com.mojang.blaze3d.platform.Window#isMinimized` - Returns whether the application window is minimized.
- `com.mojang.blaze3d.vertex.VertexBuffer`
    - `uploadStatic` - Immediately uploads the provided vertex data via the `Consumer<VertexConsumer>` using the `Tesselator` with a `STATIC_WRITE` `VertexBuffer`.
    - `drawWithRenderType` - Draws the current buffer to the screen with the given `RenderType`.
- `com.mojang.math.MatrixUtil#isIdentity` - Checks whether the current `Matrix4f` is an identity matrix.
- `net.minecraft`
    - `SuppressForbidden` - An annotation that holds some reason, usually related to needing the sysout stream.
    - `Util#maxAllowedExecutorThreads` - Returns the number of available processors clamped between one and the maximum number of threads.
- `net.minecraft.client.gui.components.events.GuiEventListener#getBorderForArrowNavigation` - Returns the `ScreenRectangle` bound to the current direction.
- `net.minecraft.client.gui.navigation.ScreenRectangle#transformAxisAligned` - Creates a new `ScreenRectangle` by transforming the position using the provided `Matrix4f`.
- `net.minecraft.client.gui.narration.NarratableEntry#getNarratables` - Returns the list of narratable objects within the current object.
- `net.minecraft.client.gui.screens.recipebook.RecipeCollection#EMPTY` - An empty collection of recipes.
- `net.minecraft.client.gui.screens.worldselection`
    - `ExperimentsScreen$ScrollArea` - Represents a narratable scroll area of the currently available experiments.
    - `SwitchGrid#layout` - Returns the layout of the grid to visit.
- `net.minecraft.client.model`
    - `BannerFlagModel`, `BannerModel` - Models for the banner and hanging banner.
    - `VillagerLikeModel#translateToArms` - Translates the pose stack such that the current relative position is at the entity's arms.
- `net.minecraft.client.model.geom.EntityModelSet#vanilla` - Creates a new model set with all vanilla models. 
- `net.minecraft.client.multiplayer.PlayerInfo#setShowHat`, `showHat` - Handles showing the hat layer of the player in the tab overlay.
- `net.minecraft.client.renderer.blockentity`
    - `AbstractSignRenderer` - How a sign should render as a block entity.
    - `HangingSignRenderer`
        - `createSignModel` - Creates a sign model given the wood and the attachment location.
        - `renderInHand` - Renders the model in the entity's hand.
        - `$AttachmentType` - An enum which represents where the model is attached to, given its properies.
        - `$ModelKey` - A key for the model that combines the `WoodType` with its `$AttachmentType`.
    - `SignRenderer`
        - `renderInHand` - Renders the model in the entity's hand.
- `net.minecraft.client.renderer.entity.EntityRenderer#getShadowStrength` - Returns the raw opacity of the display's shadow.
- `net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer#applyTranslation` - Applies the translation to render the item in the model's arms.
- `net.minecraft.client.renderer.texture`
    - `ReloadableTexture` - A texture that can be reloaded from its associated contents.
    - `TextureContents` - Holds the image and metadata associated with a given texture.
    - `TextureManager`
        - `registerAndLoad` - Registers a reloadable texture with the given name.
        - `registerForNextReload` - Registers a texture by its resource location to be loaded on next reload.
- `net.minecraft.commands.SharedSuggestionProvider#MATCH_SPLITTER` - Defines a matcher that matches a period, underscore, or forward slash.
- `net.minecraft.core.BlockPos$TraversalNodeStatus` - A marker indicating whether the `BlockPos` should be used, skipped, or stopped from any further traversal.
- `net.minecraft.core.component.PatchedDataComponentMap`
    - `toImmutableMap` - Returns either the immutable patch or a copy of the current map.
    - `hasNonDefault` - Returns whether there is a custom value for the data component instead of just the default.
- `net.minecraft.data.PackOutput$PathProvider#json` - Gets the JSON path from a resource key.
- `net.minecraft.data.loot.BlockLootSubProvider#createMultifaceBlockDrops` - Drops a block depending on the block face mined.
- `net.minecraft.data.worldgen.placement.PlacementUtils#HEIGHTMAP_NO_LEAVES` - Creates a y placement using the `Heightmap$Types#MOTION_BLOCKING_NO_LEAVES` heightmap.
- `net.minecraft.network.chat.Style#getShadowColor`, `withShadowColor` - Methods for handling the shadow color of a component.
- `net.minecraft.network.protocol.game.ServerboundPlayerLoadedPacket` - A packet for when the client player loads into a client world.
- `net.minecraft.resources.FileToIdConverter#registry` - Gets the file converter from a registry key.
- `net.minecraft.util.ExtraCodecs`
    - `idResolverCodec` - Creates a codec that maps some key to some value.
    - `compactListCodec` - Creates a codec that can either be an element of a list of elements.
    - `floatRange` - Creates a codec that must be between two float values.
    - `$LateBoundIdMapper` - A mapper that functionally acts like a registry with an associated codec. 
- `net.minecraft.util.profiling.jfr.JvmProfiler#onStructureGenerate` - Returns the profiled duration on when a structure attempts to generate in the world.
- `net.minecraft.util.profiling.jfr.event.StructureGenerationEvent` - A profiler event when a structure is being generated.
- `net.minecraft.util.profiling.jfr.stats.StructureGenStat` - A result of a profiled structure generation.
- `net.minecraft.world.entity`
    - `LivingEntity`
        - `resolvePlayerResponsibleForDamage` - Gets the player responsible for hurting the current entity.
        - `canBeNameTagged` - When true, the entity's can be set with a name tag.
    - `Mob#getPreferredWeaponType` - Gets the tag that represents the weapons the entity wants to pick up.
- `net.minecraft.world.entity.ai.attributes.AttributeMap#resetBaseValue` - Resets the attribute instance to its default value.
- `net.minecraft.world.entity.monster.creaking`
    - `Creaking`
        - `activate`, `deactivate` - Handles the activateion of the brain logic for the creaking.
        - `setTransient`, `isHeartBound`, `setHomePos`, `getHomePos` - Handles the home position.
        - `blameSourceForDamage` - Finds the player responsible for the damage.
        - `tearDown` - Handles when the creaking is destroyed.
        - `creakingDeathEffects` - Handles the death of a creaking.
        - `playerIsStuckInYou` - Checks whether there are at least four players stuck in a creaking.
        - `setTearingDown`, `isTearingDown` - Handles the tearing down state.
        - `hasGlowingEyes`, `checkEyeBlink` - Handles the eye state.
- `net.minecraft.world.entity.player.Player`
    - `hasClientLoaded`, `setClientLoaded` - Whether the client player has been loaded.
    - `tickClientLoadTimeout` - Ticks the timer on how long to wait before kicking out the client player if not loaded.
- `net.minecraft.world.item`
    - `Item#shouldPrintOpWarning` - Whether a warning should be printed to the player based on stored block entity data and adminstrator permissions.
    - `ItemStack`
        - `getCustomName` - Returns the custom name of the item, or `null` if no component exists.
        - `immutableComponents` - Returns either the immutable patch or a copy of the stack component map.
        - `hasNonDefault` - Returns whether there is a custom value for the data component instead of just the default.
- `net.minecraft.world.item.component.CustomData`
    - `parseEntityId` - Reads the entity id off of the component.
    - `parseEntityType` - Reads the entity type from the id and maps it to its registry object.
- `net.minecraft.world.item.crafting.Ingredient#isEmpty` - Returns whether the ingredient has no values.
- `net.minecraft.world.item.trading.Merchant#stillValid` - Checks whether the merchant can still be accessed by the player.
- `net.minecraft.world.level`
    - `Level#dragonParts` - Returns the list of entities that are the parts of the ender dragon.
    - `ServerExplosion#getDamageSource` - Returns the damage source of the explosion.
- `net.minecraft.world.level.block`
    - `EyeblossomBlock$Type`
        - `block` - Gets the block for the current type.
        - `state` - Gets the block state for the current type.
        - `transform` - Returns the opposiate state of this type.
    - `FlowerBlock#getBeeInteractionEffect` - Returns the effect that bees obtain when interacting with the flower.
    - `FlowerPotBlock#opposite` - Returns the opposite state of the block, only for potted eyeblossoms.
    - `MultifaceBlock#canAttachTo` - Returns whether this block can attach to another block.
    - `MultifaceSpreadeableBlock` - A multiface block that can naturally spread.
- `net.minecraft.world.level.block.entity.trialspawner`
    - `TrialSpawner#overrideEntityToSpawn` - Changes the entity to spawn in the trial.
    - `TrialSpawnerConfig#withSpawning` - Sets the entity to spawn within the trial.

### List of Changes

- `com.mojang.blaze3d.platform.NativeImage#upload` no longer takes in three booleans that set the filter mode or texture wrap clamping for `TEXTURE_2D`
    - This has been moved to `AbstractTexture#setClamp` and `#setFilter`
- `net.minecraft.client.gui`
    - `Gui#clear` -> `clearTitles`
    - `GuiGraphics#drawWordWrap` has a new overload that takes in whether a drop shadow should be applied to the text
        - The default version enables drop shadows instead of disabling it
- `net.minecraft.client.gui.components`
    - `AbstractContainerWidget` now implements `AbstractScrollArea`
    - `AbstractScrollWidget` -> `AbstractScrollArea` or `AbstractTextAreaWidget` depending on use-case, not one-to-one
    - `AbstractSelectionList`
        - `setRenderHeader` is now bundled into a new constructor with an extra integer
        - `getMaxScroll` -> `AbstractScrollArea#maxScrollAmount`
        - `getScrollAmount` -> `AbstractScrollArea#scrollAmount`
        - `scrollbarVisible` -> `AbstractScrollArea#scrollbarVisible`
        - `setClampedScrollAmount`, `setScrollAmount` -> `AbstractScrollArea#setScrollAmount`
        - `clampScrollAmount` -> `refreshScrollAmount`
        - `updateScrollingState` -> `AbstractScrollArea#updateScrolling`
        - `getScrollbarPosition`, `getDefaultScrollbarPosition` -> `scrollBarY`, not one-to-one
    - `AbstractWidget#clicked` -> `isMouseOver`, already exists
- `net.minecraft.client.gui.components.toasts.TutorialToast` now requires a `Font` as the first argument in its constructor
- `net.minecraft.client.gui.font.glyphs.BakedGlyph$Effect` and `$GlyphInstance` now take in the color and offset of the text shadow
- `net.minecraft.client.gui.screens`
    - `LoadingOverlay#registerTextures` now takes in a `TextureManager` instead of the `Minecraft` instance
    - `TitleScreen#preloadResources` -> `registerTextures`, not one-to-one
- `net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen$GameModeSlot` is now a static inner class
- `net.minecraft.client.gui.screens.reporting.ChatSelectionScreen$Entry`, `$PaddingEntry` are now static inner classes
- `net.minecraft.client.gui.screens.worldselection.SwitchGrid$Builder#build` no longer takes in a `Consumer<LayoutElement>`
- `net.minecraft.client.model`
    - `DonkeyModel#createBodyLayer`, `createBabyLayer` now take in a scaling factor
    - `VillagerHeadModel` -> `VillagerLikeModel`
- `net.minecraft.client.model.geom.EntityModelSet` is no longer a `ResourceManagerReloadListener`
- `net.minecraft.client.multiplayer.MultiPlayerGameMode#handlePickItem` -> `handlePickItemFromBlock` or `handlePickItemFromEntity`, providing both the actual object data to sync and a `boolean` about whether to include the data of the object being picked
- `net.minecraft.client.particle.CherryParticle` -> `FallingLeavesParticle`, not one-to-one as the new class has greater configuration for its generalization
- `net.minecraft.client.player.ClientInput#tick` no longer takes in any parameters
- `net.minecraft.client.renderer`
    - `CubeMap#preload` -> `registerTextures`, not one-to-one
    - `LevelRenderer`
        - `renderLevel` no longer takes in the `LightTexture`
        - `onChunkLoaded` -> `onChunkReadyToRender`
    - `PostChainConfig$Pass#program` -> `programId`
        - `program` now returns the `ShaderProgram` with the given `programId`
    - `ScreenEffectRenderer#renderScreenEffect` now takes in a `MultiBufferSource`
    - `SectionOcclusionGraph#onChunkLoaded` -> `onChunkReadyToRender`
    - `Sheets#createSignMaterial`, `createHangingSignMaterial` now has an overload that takes in a `ResourceLocation`
    - `SkyRenderer`
        - `renderSunMoonAndStars`, `renderSunriseAndSunset` now takes in a `MultiBufferSource$BufferSource` instead of a `Tesselator`
        - `renderEndSky` no longer takes in the `PoseStack`
    - `WeatherEffectRenderer#render` now takes in a `MultiBufferSource$BufferSource` instead of a `LightTexture`
- `net.minecraft.client.renderer.blockentity`
    - `BannerRenderer#createBodyLayer` -> `BannerModel#createBodyLayer`, not one-to-one
    - `HangingSignRenderer`
        - `createHangingSignLayer` now takes in a `HangingSignRenderer$AttachmentType`
        - `$HangingSignModel` is now replaced with a `Model$Simple`, though its fields can be obtained from the root
    - `SkullBlockRenderer#getRenderType` now has an overload that takes in a `ResourceLocation` to override representing the player's texture
- `net.minecraft.client.renderer.entity.AbstractHorseRenderer`, `DonkeyRenderer` no longer takes in a float scale
- `net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer` now requires the generic `M` to be a `VillagerLikeModel`
- `net.minecraft.client.renderer.entity.state.CreakingRenderState#isActive` -> `eyesGlowing`
    - The original parameter still exists on the `Creaking`, but is not necessary for rendering
- `net.minecraft.core.BlockPos#breadthFirstTraversal` now takes in a function that returns a `$TraversalNodeStatus` instead of a simple predicate to allow certain positions to be skipped
- `net.minecraft.core.particles.TargetColorParticleOption` -> `TrailParticleOption`, not one-to-one
- `net.minecraft.data.DataProvider#savelAll` now has overloads for maps with a key function to get the associated path
- `net.minecraft.network`
    - `NoOpFrameEncoder` replaced by `LocalFrameEncoder`, not one-to-one
    - `NoOpFrameDecoder` replaced by `LocalFrameDecoder`, not one-to-one
    - `MonitorFrameDecoder` replaced by `MonitoredLocalFrameDecoder`, not one-to-one
- `net.minecraft.network.protocol.game`
    - `ClientboundLevelParticlesPacket` now takes in a boolean that determines whether the particle should always render
    - `ClientboundMoveVehiclePacket` is now a record
    - `ClientboundPlayerInfoUpdatePacket$Entry` now takes in a boolean representing whether the hat should be shown
    - `ClientboundSetHeldSlotPacket` is now a record
    - `ServerboundMoveVehiclePacket` is now a record
    - `ServerboundPickItemPacket` -> `ServerboundPickItemFromBlockPacket`, `ServerboundPickItemFromEntityPacket`; not one-to-one
- `net.minecraft.server.level
    - `ServerLevel#sendParticles` now has an overload that takes in the override limiter distance and whether the particle should always be shown
        - Other overloads that take in the override limiter now also take in the boolean for if the particle should always be shown
    - `ServerPlayer#doCheckFallDamage` -> `Entity#doCheckFallDamage`, now final
- `net.minecraft.util`
    - `ARGB#from8BitChannel` is now private, with individual float components obtained from `alphaFloat`, `redFloat`, `greenFloat`, and `blueFloat`
    - `SpawnUtil#trySpawnMob` now takes in a boolean that, when false, allows the entity to spawn regardless of collision status with the surrounding area
- `net.minecraft.util.profiling.jfr.callback.ProfiledDuration#finish` now takes in a boolean that indicates whether the profiled event was successful
- `net.minecraft.util.profiling.jfr.parse.JfrStatsResults` now takes in a list of structure generation statistics
- `net.minecraft.world.effect.PoisonMobEffect`, `WitherMobEffect` is now public
- `net.minecraft.world.entity`
    - `Entity`
        - `setOnGroundWithMovement` has an overload that sets the horizontal collision to whatever the entity's current state is.
        - `awardKillScore` no longer takes in an integer
        - `makeBoundingBox()` is now final
            - `makeBoundingBox(Vec3)` is now
        - `onlyOpCanSetNbt` -> `EntityType#onlyOpCanSetNbt`
    - `Leashable`
        - `readLeashData` is now private, replaced by a method that returns nothing
        - `dropLeash(boolean, boolean)` -> `dropLeash()`, `removeLeash`, `onLeashRemoved`; not one-to-one, as they all internally call the private `dropLeash`
    - `LivingEntity`
        - `isLookingAtMe` no longer takes in a `Predicate<LivingEntity>`, and array of `DoubleSupplier`s is now an array of `double`s
        - `hasLineOfSight` takes in a double instead of a `DoubleSupplier`
- `net.minecraft.world.entity.ai.behavior.AcquirePoi#create` now has overloads which take in a `BiPredicate<ServerLevel, BlockPos>` for filtering POI locations
- `net.minecraft.world.entity.animal.Bee#attractsBees` is now public
- `net.minecraft.world.entity.monster.Shulker#getProgressAabb`, `getProgressDeltaAabb` now take in a movement `Vec3`
- `net.minecraft.world.entity.player`
    - `Inventory`
        - `setPickedItem` -> `addAndPickItem`
        - `findSlotMatchingCraftingIngredient` now takes in an `ItemStack` to compare against
    - `Player#getPermissionLevel` is now public
    - `StackedContents$IngredientInfo` is now an interface that acts like a predicate for accepting some item
- `net.minecraft.world.entity.projectile.FishingHook` no longer takes in the `ItemStack`
- `net.minecraft.world.inventory.Slot#getNoItemIcon` now returns a single `ResourceLocation` rather than a pair of them
- `net.minecraft.world.item`
    - `Item$TooltipContext#of` now takes in the `Player` viewing the item
    - `MobBucketItem` now requires a `Mob` entity type
    -` SpawnEggItem#spawnsEntity`, `getType` now takes in a `HolderLookup$Provider`
- `net.minecraft.world.item.crafting`
    - `Ingredient` now implements `StackedContents$IngredientInfo<Holder<Item>>`
        - `items` now returns a stream instead of a list
    - `PlacementInfo#slotInfo` -> `slotsToIngredientIndex`, not one-to-one
- `net.minecraft.world.level.Level#addParticle` now takes in a boolean representing if the particle should always be shown
- `net.minecraft.world.level.block`
    - `Block#getCloneItemStack` -> `state.BlockBehaviour#getCloneItemStack`, now protected
    - `CherryLeavesBlock` -> `ParticleLeavesBlock`
    - `CreakingHeartBlock#canSummonCreaking` -> `isNaturalNight`
    - `MultifaceBlock` is no longer abstract and implements `SimpleWaterloggedBlock`
        - `getSpreader` -> `MultifaceSpreadeableBlock#getSpreader`
    - `SculkVeinBlock` is now an instance of `MultifaceSpreadeableBlock`
    - `SnowyDirtBlock#isSnowySetting` is now protected
- `net.minecraft.world.level.block.entity`
    - `AbstractFurnaceBlockEntity`
        - `litTime` -> `litTimeRemaining`
        - `litDuration` -> `litTotalTime`
        - `cookingProgress` -> `cookingTimer`
    - `BeehiveBlockEntity#addOccupant` now takes in a `Bee` rather than an `Entity`
    - `CreakingHeartBlockEntity#setCreakingInfo` - Sets the creaking the block entity is attached to.
- `net.minecraft.world.level.block.state.BlockBehaviour#getCloneItemStack`, `$BlockStateBase#getCloneItemStack` now takes in a boolean representing if there is infinite materials and whether the current block data should be saved.
- `net.minecraft.world.level.chunk.ChunkGenerator#createStructures` now takes in the `Level` resource key, only used for profiling
- `net.minecraft.world.level.levelgen.feature.configurations`
    - `MultifaceGrowthConfiguration` now takes in a `MultifaceSpreadableBlock` instead of a `MultifaceBlock`
    - `SimpleBlockConfiguration` now takes in a boolean on whether to schedule a tick update
- `net.minecraft.world.level.levelgen.structure.Structure#generate` now takes in the `Structure` holder and a `Level` resource key, only used for profiling

### List of Removals

- `com.mojang.blaze3d.systems.RenderSystem#overlayBlendFunc`
- `net.minecraft.client.gui.components.AbstractSelectionList`
    - `clickedHeader`
    - `isValidMouseClick`
- `net.minecraft.client.gui.screens.recipebook.RecipeCollection#hasSingleResultItem`
- `net.minecraft.client.model`
    - `DrownedModel#getArmPose`, now part of the `ArmedEntityRenderState`
    - `FelineModel#CAT_TRANSFORMER`
    - `HumanoidModel#getArmPose`, now part of the `ArmedEntityRenderState`
    - `PlayerModel#getArmPose`, now part of the `ArmedEntityRenderState`
    - `SkeletonModel#getArmPose`, now part of the `ArmedEntityRenderState`
    - `VillagerModel#BABY_TRANSFORMER`
- `net.minecraft.client.renderer.texture`
    - `AbstractTexture`
        - `load`
        - `reset`
        - `getDefaultBlur`
    - `PreloadedTexture`
    - `TextureManager`
        - `getTexture(ResourceLocation, AbstractTexture)`
        - `register(String, DynamicTexture)`
        - `preload`
- `net.minecraft.server.level.TicketType#POST_TELEPORT`
- `net.minecraft.world.entity.LivingEntity#deathScore`
- `net.minecraft.world.entity.ai.navigation.FlyingPathNavigation`, `GroundPathNavigation`
    - `canPassDoors`, `setCanPassDoors`
    - `canOpenDoors`
- `net.minecraft.world.entity.monster.creaking.CreakingTransient`
- `net.minecraft.world.entity.player.StackedItemContents#convertIngredientContents`
- `net.minecraft.world.item`
    - `CompassItem#getSpawnPosition`
    - `ItemStack#clearComponents`
- `net.minecraft.world.item.crafting.PlacementInfo`
    - `ingredientToContents`
    - `unpackedIngredients`
    - `$SlotInfo`
- `net.minecraft.world.level.block.CreakingHeartBlock$CreakingHeartState`
- `net.minecraft.world.level.block.entity.BlockEntity#onlyOpCanSetNbt`
- `net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerData#setEntityId`

# Minecraft 1.21.1-> 1.21.2 Mod Migration Primer

This is a high level, non-exhaustive overview on how to migrate your mod from 1.21.1 to 1.21.2. This does not look at any specific mod loader, just the changes to the vanilla classes.

This primer is licensed under the [Creative Commons Attribution 4.0 International](http://creativecommons.org/licenses/by/4.0/), so feel free to use it as a reference and leave a link so that other readers can consume the primer.

If there's any incorrect or missing information, please file an issue on this repository or ping @ChampionAsh5357 in the Neoforged Discord server.

## Pack Changes

There are a number of user-facing changes that are part of vanilla which are not discussed below that may be relevant to modders. You can find a list of them on [Misode's version changelog](https://misode.github.io/versions/?id=1.21.2&tab=changelog).

## The Holder Set Transition

Many of the methods that used `TagKey`s or raw registry objects have been replaced with the direct `HolderSet` object. A `HolderSet` is essentially a list of registry object references that can be dynamically updated and managed as needed by the game. There are effectively two kinds of `HolderSet`s: direct and named. Named `HolderSet`s are the object representation of tags in game. It's called a named set as the `HolderSet` is referenced by the tag's name. Direct `HolderSet`s, on the other hand, are created by `HolderSet#direct`, which functions as an inlined list of values. These are useful when a separate object doesn't need to be defined to construct some value.

For a JSON example:
```json5
// HolderSet#direct with one element
{
    "holder_set": "minecraft:apple"
}

// HolderSet#direct with multiple elements
{
    "holder_set": [
        "minecraft:apple",
        "minecraft:stick"
    ]
}

// HolderSet reference (tags)
{
    "holder_set": "#minecraft:planks"
}
```

Generally, you should never be constructing holder sets incode except during provider generation. Each set type has a different method of construction.

First, to even deal with `Holder`s or `HolderSet`s, you will need access to the static registry instance via `Registry` or the datapack registry via `HolderGetter`. The `HolderGetter` is either obtained from a `BootstrapContext#lookup` during datapack registry generation or `HolderLookup$Provider#lookupOrThrow` either as part of generation or `MinecraftServer#registryAccess` during gameplay.

Once that is available, for direct `HolderSet`s, you will need to get the `Holder` form of a registry object. For static registries, this is done through `Registry#wrapAsHolder`. For datapack registries, this is done through `HolderGetter#getOrThrow`.

```java
// Direct holder set for Items
HolderSet<Item> items = HolderSet.direct(BuiltInRegistries.ITEM.wrapAsHolder(Items.APPLE));

// Direct holder set for configured features
// Assume we have access to the HolderGetter<ConfiguredFeature<?, ?>> registry
Holderset<ConfiguredFeature<?, ?>> features = HolderSet.direct(registry.getOrThrow(OreFeatures.ORE_IRON));
```

For named `HolderSet`s, the process is similar. For both static and dynamic registries, you call `HolderGetter#getOrThrow`.

```java
// Named holder set for Items
HolderSet<Item> items = BuiltInRegistries.ITEM.getOrThrow(ItemTags.PLANKS);

// Named holder set for biomes
// Assume we have access to the HolderGetter<Biome> registry
Holderset<Biome> biomes = registry.getOrThrow(BiomeTags.IS_OCEAN);
```

As these changes are permeated throughout the entire codebase, they will be listed in more relevant subsections.

## Gui Render Types

Gui rendering methods within `GuiGraphics` now take in a `Function<ResourceLocation, RenderType>` to determine how to render the image. Also, `blit` methods now require the size of the PNG to be specified.

```java
// For some GuiGraphics graphics
graphics.blit(
    // How to render the texture
    RenderType::guiTextured,
    // The previous texture parameters
    ...,
    // The size of the PNG to use
    256, 256);
```

This means methods that provided helpers towards setting the texture or other properties that could be specified within a shader have been removed.

- `com.mojang.blaze3d.pipeline.RenderTarget#blitToScreen(int, int, boolean)` -> `blitAndBlendToScreen`
- `net.minecraft.client.gui.GuiGraphics`
    - `drawManaged` is removed
    - `setColor` is removed - Now a parameter within the `blit` and `blitSprite` methods
    - `blit(int, int, int, int, int, TextureAtlasSprite, *)` is removed
    - `bufferSource` -> `drawSpecial`, not one-to-one as this takes in a consumer of the `MultiBufferSource` and ends the current batch instead of just returning the `MultiBufferSource`
- `net.minecraft.client.gui.components.PlayerFaceRenderer`
    - All `draw` methods except `draw(GuiGraphics, PlayerSkin, int, int, int)` takes in an additional `int` that defines the color
- `net.minecraft.client.renderer.RenderType`
        - `guiTexturedOverlay` - Gets the render type for an image overlayed onto the game screen.
        - `guiOpaqueTexturedBackground` - Gets the render type for a GUI texture applied to the background of a menu.
        - `guiNauseaOverlay` - Gets the render type for the nausea overlay.
        - `guiTextured` - Gets the render type for an image within a GUI menu.
- `net.minecraft.client.resources.metadata.gui.GuiSpriteScaling$NineSlice` now takes in a boolean representing whether the center portion of the texture should be streched to fit the size

## Shader Rewrites

The internals of shaders have been rewritten quite a bit.

### Shaders Files

The main changes are the defined samplers and post shaders.

The `DiffuseSampler` and `DiffuseDepthSampler` have been given new names depending on the target to apply: `InSampler`, `MainSampler`, and `MainDepthSampler`. `InSampler` is used in everything but the `transparency` program shader.

```json5
// In some shader JSON
{
    "samplers": [
        { "name": "MainSampler" },
        // ...
    ]
}
```

Within post effect shaders, they have been changed completely. For a full breakdown of the changes, see `PostChainConfig`, but in general, all targets are now keys to objects, all pass inputs and filters are now lists of sampler inputs. As for how this looks:

```json5
// Old post effect shader JSON
// In assets/<namespace>/shaders/post
{
    "targets": [
        "swap"
    ],
    "passes": [
        {
            "name": "invert",
            "intarget": "minecraft:main",
            "outtarget": "swap",
            "use_linear_filter": true,
            "uniforms": [
                {
                    "name": "InverseAmount",
                    "values": [ 0.8 ]
                }
            ]
        },
        {
            "name": "blit",
            "intarget": "swap",
            "outtarget": "minecraft:main"
        }
    ]
}

// New post effect JSON
// In assets/<namespace>/post_effect
{
    "targets": {
        "swap": {} // Swap is now a target object (fullscreen unless otherwise specified)
    },
    "passes": [
        {
            // Name of the program to apply (previously 'name')
            // assets/minecraft/shaders/post/invert.json
            "program": "minecraft:post/invert",
            // Inputs is now a list
            "inputs": [
                {
                    // Targets the InSampler
                    // Sampler must be available in the program shader JSON
                    "sampler_name": "In",
                    // Reading from the main screen (previously 'intarget')
                    "target": "minecraft:main",
                    // Use GL_LINEAR (previously 'use_linear_filter')
                    "bilinear": true
                }
            ],
            // Writes to the swap target (previously 'outtarget')
            "output": "swap",
            "uniforms": [
                {
                    "name": "InverseAmount",
                    "values": [ 0.8 ]
                }
            ]
        },
        {
            "program": "minecraft:post/blit",
            "inputs": [
                {
                    "sampler_name": "In",
                    "target": "swap"
                }
            ],
            "output": "minecraft:main"
        }
    ]
}
```

### Shader Programs

All shaders, regardless of where they are used (as part of a program or post effect), have a JSON within `assets/<namespace>/shaders`. This JSON defines everything the shader will use, as defined by `ShaderProgramConfig`. The main addition is the change to `ResourceLocation` relative references, and adding the `defines` header dynamically during load time.

```json5
// For some assets/my_mod/shaders/my_shader.json
{
    // Points to assets/my_mod/shaders/my_shader.vsh (previously 'my_shader', without id specification)
    "vertex": "my_mod:my_shader",
    // Points to assets/my_mod/shaders/my_shader.fsh (previously 'my_shader', without id specification)
    "fragment": "my_mod:my_shader",
    // Adds '#define' headers to the shaders
    "defines": {
        // #define <key> <value>
        "values": {
            "ALPHA_CUTOUT": "0.1"
        },
        // #define flag
        "flags": [
            "NO_OVERLAY"
        ]
    },
    // A list of sampler uniforms to use in the shader
    // There are 12 texture sampler uniforms Sampler0-Sampler11, though usually only Sampler0 is supplied
    // Additionally, there are dynamic '*Sampler' for post effect shaders which are bound to read the specified targets or 'minecraft:main'
    "samplers": [
        { "name": "Sampler0" }
    ],
    // A list of uniforms that can be accessed within the shader
    // A list of available uniforms can be found in CompiledShaderProgram#setUniforms
    "uniforms": [
        { "name": "ModelViewMat", "type": "matrix4x4", "count": 16, "values": [ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 ] },
        { "name": "ProjMat", "type": "matrix4x4", "count": 16, "values": [ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 ] },
        { "name": "ModelOffset", "type": "float", "count": 3, "values": [ 0.0, 0.0, 0.0 ] },
        { "name": "ColorModulator", "type": "float", "count": 4, "values": [ 1.0, 1.0, 1.0, 1.0 ] }
    ]
}
```

```glsl
// For some assets/my_mod/shaders/my_shader.vsh (Vertex Shader)

// GLSL Version
#version 150

// Imports Mojang GLSL files
// Located in assets/<namespace>/shaders/include/<path>
#moj_import <minecraft:light.glsl>

// Defines are injected (can use 'ALPHA_CUTOUT' and 'NO_OVERLAY')

// Defined by the VertexFormat passed into the ShaderProgram below
in vec3 Position; // vec3 float
in vec4 Color; // vec4 unsigned byte (0-255)

// Samplers defined by the JSON
uniform sampler2D Sampler0;

// Uniforms provided by the JSON
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ModelOffset;

// Values to output to the fragment shader
out float vertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;

void main() {
    // Out values should be set here
}
```

```glsl
// For some assets/my_mod/shaders/my_shader.fsh (Fragment Shader)

// GLSL Version
#version 150

// Imports Mojang GLSL files
// Located in assets/<namespace>/shaders/include/<path>
#moj_import <minecraft:fog.glsl>

// Defines are injected (can use 'ALPHA_CUTOUT' and 'NO_OVERLAY')

// Defined by the output of the vertex shader above 
in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;

// Samplers defined by the JSON
uniform sampler2D Sampler0;

// Uniforms provided by the JSON
uniform vec4 ColorModulator;

// Values to output to the framebuffer (the color of the pixel)
out vec4 fragColor;

void main() {
    // Out values should be set here
}
```

On the code side, shaders are stored internally as either a `ShaderProgram` or a `CompiledShaderProgram`. `ShaderProgram` represents the identifier, while the `CompiledShaderProgram` represents the shader itself to run. Both are linked together through the `ShaderManager`.

Shader programs are compiled dynamically unless specified as a core shader. This is done by registering the `ShaderProgram` to `CoreShaders#PROGRAMS`.

```java
// List<ShaderProgram> PROGRAMS access
ShaderProgram MY_SHADER = new ShaderProgram(
    // Points to assets/my_mod/shaders/my_shader.json
    ResourceLocation.fromNamespaceAndPath('my_mod', 'my_shader'),
    // Passed in vertex format used by the shader
    DefaultVertexFormat.POSITION_COLOR,
    // Lists the '#define' values and flags
    // Value: '#define <key> <value>'
    // Flag: '#define <flag>'
    ShaderDefines.EMPTY
)
```

The shader programs are then set by calling `RenderSystem#setShader` with the `ShaderProgram` in question. In fact, all references to `GameRenderer#get*Shader` should be replaced with a `ShaderProgram` reference.

```java
// In some rendering method
RenderSystem.setShader(MY_SHADER);

// Creating a new ShaderStateShard for a RenderType
ShaderStateShard MY_SHARD = new ShaderStateShard(MY_SHADER);
```

- `com.mojang.blaze3d.ProjectionType` - An enum which holds the logic for how a projection matrix should be rendered.
- `com.mojang.blaze3d.buffers`
    - `BufferType` - An enum that specifies the GL target buffer type.
    - `GpuBuffer` - A wrapper around the GL buffer calls for handling the rendering of the screen.
    - `GpuFence` - A handle for managing the sync status of the GPU fence.
- `com.mojang.blaze3d.platform.GlStateManager`
    - `glShaderSource` now takes in a `String` rather than a `List<String>`
    - `_glMapBufferRange` - Delegates to `GL30#glMapBufferRange`.
    - `_glFenceSync` - Delegates to `GL32#glFenceSync`.
    - `_glClientWaitSync` - Delegates to `GL32#glClientWaitSync`.
    - `_glDeleteSync` - Delegates to `GL32#glDeleteSync`.
    - `_glBuffserSubData` - Delegates to `GL15#glBufferSubData`.
- `com.mojang.blaze3d.preprocessor.GlslPreprocessor#injectDefines` - Injects any defined sources to the top of a loaded `.*sh` file.
- `com.mojang.blaze3d.shaders`
    - `BlendMode`, `Effect`, `EffectProgram`, `Program`, `ProgramManager`, `Shader` has been bundled into `CompiledShader`
    - `Unform` no longer takes in a `Shader`
        - `glGetAttribLocation` is removed
        - `glBindAttribLocation` -> `VertexFormat#bindAttributes`
        - `setFromConfig` - Sets the uniform parameters given the values and count of another uniform configuration.
- `com.mojang.blaze3d.systems.RenderSystem`
    - `setShader` now takes in the `CompiledShaderProgram`, or `ShaderProgram`
    - `clearShader` - Clears the current system shader.
    - `runAsFancy` is removed, handled internally by `LevelRenderer#getTransparencyChain`
    - `setProjectionMatrix` now takes in a `ProjectionType` than just the `VertexSorting`
    - `getVertexSorting` -> `getProjectionType`; not one-to-one, but the `VertexSorting` is accessible on the `ProjectionType`
- `com.mojang.blaze3d.vertex.VertexBuffer`
    - `drawWithShader` will now noop when passing in a null `CompiledShaderProgram`
    - `$Usage` -> `com.mojang.blaze3d.buffers.BufferUsage`
- `net.minecraft.client.Minecraft#getShaderManager` - Returns the manager that loads all the shaders and post effects.
- `net.minecract.client.renderer`
    - `EffectInstance` class is removed, replaced by `CompiledShaderProgram` in most cases
    - `GameRenderer`
        - `get*Shader` -> `CoreShaders#*`
        - `shutdownEffect` -> `clearPostEffect`
        - `createReloadListener` -> `ShaderManager`
        - `currentEffect` -> `currentPostEffect`
    - `ItemBlockRenderTypes#getRenderType` no longer takes in a boolean indicating whether to use the translucent render type
    - `ShaderInstance` -> `CompiledShaderProgram`
        - `CHUNK_OFFSET` -> `MODEL_OFFSET`
            - JSON shaders: `ChunkOffset` -> `ModelOffset`
        - `getUniformConfig` - Returns the configuration of a uniform given its name.
    - `LevelRenderer#graphicsChanged` is removed, handled internally by `LevelRenderer#getTransparencyChain`
    - `PostChainConfig` - A configuration that represents how a post effect shader JSON is constructed.
    - `PostPass` now takes in the `ResourceLocation` representing the output target instead of the in and out `RenderTarget`s or the `boolean` filter mode, the `CompiledShaderProgram` to use instead of the `ResourceProvider`, and a list of uniforms for the shader to consume
        - No longer `AutoCloseable`
        - `addToFrame` no longer takes in the `float` time
        - `getEffect` -> `getShader`
        - `addAuxAsset` -> `addInput`
        - `process` -> `addToFrame`
        - `$Input` - Represents an input of the post effect shader.
        - `$TargetInput` - An input from a `RenderTarget`.
        - `$TextureInput` - An input from a texture.
    - `PostChain` constructor is now created via `load`
        - No longer `AutoCloseable`
        - `MAIN_RENDER_TARGET` is now public
        - `getName` is removed, replaced with `ShaderProgram#configId`
        - `process` no longer takes in the `DeltaTracker`
        - `$TargetBundle` - Handles the getting and replacement of resource handles within the chain.
    - `RenderType`
        - `entityTranslucentCull`, `entityGlintDirect` is removed
        - `armorTranslucent` - A render type which renders armor that can have a translucent texture.
    - `ShaderDefines` - The defined values and flags used by the shader as constants.
    - `ShaderManager` - The resource listener that loads the shaders.
    - `ShaderProgram` - An identifier for a shader.
    - `ShaderProgramConfig` - The definition of the program shader JSON.
    - `Sheets#translucentCullBlockSheet` is removed
    - `SkyRenderer` now implements `AutoCloseable`
- `net.minecraft.client.renderer.entity.ItemRenderer`
    - `getFoilBufferDirect` is removed, replaced by `getFoilBuffer`
    - `ITEM_COUNT_BLIT_OFFSET` -> `ITEM_DECORATION_BLIT_OFFSET`

## Entity Render States

Entity models and renderers have been more or less completely reworked due to the addition of `EntityRenderState`s. `EntityRenderState`s are essentially data object classes that only expose the computed information necessary to render the `Entity`. For example, a `Llama` does not need to know what is has in its inventory, just that it has a chest to render in the layer.

To start, you need to choose an `EntityRenderState` to use, or create one using a subclass if you need additional information passed to the renderer. The most common states to subclass is either `EntityRenderState` or `LivingEntityRenderState` for living entities. These fields should be mutable, as the state class is created only once for a renderer.

```java
// Assuming MyEntity extends LivingEntity
public class MyEntityRenderState extends LivingEntityRenderState {
    // An example of a field
    boolean hasExampleData;
}
```

From there, you create the `EntityModel` that will render your `Entity`. The `EntityModel` has a generic that takes in the `EntityRenderState` along with taking in the `ModelPart` root, and optionally the `RenderType` factory as part of its super. The are no methods to implement by default; however, if you need to setup any kind of model movement, you will need to overrride `setupAnim` which modifies the `ModelPart`'s mutable fields using the render state. If your model does not have any animation, then a `Model$Simple` implementation can be used instead. This does not need anything implemented.

```java
public class MyEntityModel extends EntityModel<MyEntityRenderState> {

    public MyEntityModel(ModelPart root) {
        super(root);
        // ...
    }

    @Override
    public void setupAnim(MyEntityRenderState state) {
        // Calls resetPose and whatever other transformations done by superclasses
        super.setupAnim(state); 

        // Perform transformations to model parts here
    }
}
```

`EntityModel` also has three `final` methods from the `Model` subclass: `root`, which grabs the root `ModelPart`; `allParts`, which returns a list of all `ModelPart`s flattened; and `resetPose`, which returns the `ModelPart` to their default state.

`LayerDefinition`s, `MeshDefinition`s, `PartDefinition`s, and `CubeDeformation`s remain unchanged in their implementation and construction for the `ModelLayerLocation` -> `LayerDefinition` map in `LayerDefinitions`.

What about model transformations? For example, having a baby version of the entity, or where the model switches out altogether? In those cases, a separate layer definition is registered for each. For example, a Llama would have a model layer for the main Llama model, the baby model, the decor for both the adult and baby, and finally one for the spit. Since models are generally similar to one another with only a slight transformation, a new method was added to `LayerDefinition` to take in a `MeshTransformer`. `MeshTransformer`s are basically unary operators on the `MeshDefinition`. For baby models, a `BabyModelTransform` mesh transformer is provided, which can be applied via `LayerDefinition#apply`.

```java
public class MyEntityModel extends EntityModel<MyEntityRenderState> {
    public static final MeshTransformer BABY_TRANSFORMS = ...;

    public static LayerDefinition create() {
        // ...
    }
}

// Wherever the model layers are registered
ModelLayerLocation MY_ENTITY = layers.register("examplemod:my_entity");
ModelLayerLocation MY_ENTITY_BABY = layers.register("examplemod:my_entity_baby");

// Wherever the layer definitions are registered
defns.register(MY_ENTITY, MyEntityModel.create());
defns.register(MY_ENTITY_BABY, MyEntityModel.create().apply(MyEntityModel.BABY_TRANSFORMS));
```

But how does the model know what render state to use? That's where the `EntityRenderer` comes in. The `EntityRenderer` has two generics: the type of the `Entity`, and the type of the `EntityRenderState`. The `EntityRenderer` takes in a `Context` object, similar to before. Additionally, `getTextureLocation` needs to be implemented, though this time it takes in the render state instead of the entity. The new methods to implement/override are `createRenderState` and `extractRenderState`. `createRenderState` constructs the default render state object. `extractRenderState`, meanwhile, populates the render state for the current entity being rendered. `extractRenderState` will need to be overridden if you are not using an existing render state class.

Of course, there are also subclasses of the `EntityRenderer`. First, there is `LivingEntityRenderer`. This has an additional generic of the `EntityModel` being rendered, and takes that value in the constructor along with the shadow radius. This renderer also accepts `RenderLayer`s, which largely remain unchanged if you access the previous arguments through the render state. Then, there is the `MobRenderer`, which is what all entities extend. Finally, there is `AgeableMobRenderer`, which takes in two models - the adult and the baby - and decides which to render dependent on `LivingEntityRenderState#isBaby`. `AgeableMobRenderer` should be used with `BabyModelTransform` if the entity has a baby form. Otherwise, you will most likely use `MobRenderer` or `EntityRenderer`.

```java
public class MyEntityRenderer extends AgeableMobRenderer<MyEntity, MyEntityRenderState, MyEntityModel> {

    public MyEntityRenderer(EntityRendererProvider.Context ctx) {
        super(
            ctx,
            new MyEntityModel(ctx.bakeLayer(MY_ENTITY)), // Adult model
            new MyEntityModel(ctx.bakeLayer(MY_ENTITY_BABY)), // Baby model
            0.7f // Shadow radius
        );

        // ...
    }

    @Override
    public ResourceLocation getTextureLocation(MyEntityRenderState state) {
        // Return entity texture here
    }

    @Override
    public MyEntityRenderState createRenderState() {
        // Constructs the reusable state
        return new MyEntityRenderState();
    }

    @Override
    public void extractRenderState(MyEntity entity, MyEntityRenderState state, float partialTick) {
        // Sets the living entity and entity render state info
        super.extractRenderState(entity, state, partialTick);
        // Set our own variables
        state.hasExampleData = entity.hasExampleData();
    }
}

// Wherever the entity renderers are registered
renderers.register(MyEntityTypes.MY_ENTITY, MyEntityRenderer::new);
```

- `net.minecraft.client.model`
    - `AbstractBoatModel` - A model that assumes there is a `left_paddle` and `right_paddle` that is animated according to the boat's rowing time.
    - `AgeableHierarchicalModel`, `ColorableAgeableListModel`, `AgeableListModel` -> `BabyModelTransform`
    - `AnimationUtils`
        - `animateCrossbowCharge` now takes in a `float` representing the charge duration and `int` representing the use ticks instead of a `LivingEntity`
        - `swingWeaponDown` now takes in a `HumanoidArm` instead of a `Mob`
    - `BabyModelTransform` - A mesh transformer that applies a baby scaled form of the model.
    - `BoatModel`
        - `createPartsBuilder` is removed
        - `createChildren` -> `addCommonParts`, now private
        - `createBodyModel` -> `createBoatModel`, `createChestBoatModel`
        - `waterPatch` -> `createWaterPatch`
        - `parts` is removed
    - `ChestBoatModel` -> `BoatModel#createChestBoatModel`
    - `ChestedHorseModel` class is removed and now purely lives in `LlamaModel` and `DonkeyModel`
    - `ChestRaftModel` -> `RaftModel#createChestRaftModel`
    - `ColorableHierarchicalModel` is now stored in the individual `EntityRenderState`
    - `EntityModel`
        - The generic now takes in a `EntityRenderState`
        - `setupAnim` only takes in the `EntityRenderState` generic
        - `prepareMobModel` is removed
        - `copyPropertiesTo` is removed, still exists in `HumanoidModel`
    - `HierarchicalModel` class is removed
    - `HumanoidModel#rotLerpRad` -> `Mth#rotLerpRad`
    - `ListModel` class is removed
    - `Model`
        - `renderToBuffer` is now final
        - `root` - Returns the root `ModelPart`.
        - `getAnyDescendantWithName` - Returns the first descendant of the root that has the specified name.
        - `animate` - Give the current state and definition of the aninmation, transforms the model between the current time and the maximum time to play the animation for.
        - `animateWalk` - Animates the walking cycle of the model.
        - `applyStatic` - Applies an immediate animation to the specified state.
        - `$Simple` - Constructs a simple model that has no additional animation.
    - `ModelUtils` class is removed
    - `ParrotModel#getState` -> `getPose`, now public
    - `PlayerModel` no longer has a generic
        - `renderEars` -> `PlayerEarsModel`
        - `renderCape` -> `PlayerCapeModel`
        - `getRandomModelPart` -> `getRandomBodyPart`
        - `getArmPose` - Returns the arm pose of the player given its render state.
    - `RaftModel#createBodyModel` -> `createRaftModel`
    - `WardenModel#getTendrilsLayerModelParts`, `getHeartLayerModelParts`, `getBioluminescentLayerModelParts`, `getPulsatingSpotsLayerModelParts` now take in the `WardenRenderState`
    - `WaterPatchModel` -> `BoatModel#createWaterPatch` and `Model$Simple`
- `net.minecraft.client.model.geom`
    - `ModelLayerLocation` is now a record
    - `ModelLayers`
        - `createRaftModelName`, `createChestRaftModelName` is removed
        - `createSignModelName` -> `createStandingSignModelName`, `createWallSignModelName`
        - `createBoatModelName`, `createChestBoatModelName` is removed
    - `ModelPart`
        - `rotateBy` - Rotates the part using the given `Quaternionf`.
        - `$Cube#polygons`, `$Polygon`, `$Vertex` is now public
    - `PartPose` is now a record
        - `translated` - Translates a pose.
        - `withScale`, `scaled` - Scales a pose.
- `net.minecraft.client.model.geom.builders`
    - `LayerDefinition#apply` - Applies a mesh transformer to the definition and returns a new one. 
    - `MeshDefinition#transformed` - Applies a transformation to the root pose and returns a new one.
    - `MeshTransformer` - Transforms an existing `MeshDefinition` into a given form.
    - `PartDefinition`
        - `addOrReplaceChild` now has an overload that takes in a `PartDefinition`
        - `clearChild` - Removes the child from the part definition.
        - `getChildren` - Gets all the children of the current part.
        - `transformed` - Applies a transformation to the current pose and returns a new one.
- `net.minecraft.client.renderer.entity`
    - `AbstractBoatRenderer` - A boat renderer that contains methods for the boat model and any additions to the boat itself.
    - `AgeableMobRenderer` - A mob renderer that takes in the baby and adult model.
    - `BoatRenderer` now takes in a `ModelLayerLocation` instead of a `boolean`
    - `EntityRenderDispatcher` now takes in a `MapRenderer`
        - `render` no longer takes in the entity Y rotation
    - `EntityRenderer` now takes in a generic for the `EntityRenderState`
        - `getRenderOffset` only takes in the `EntityRenderState`
        - `getBoundingBoxForCulling` - Returns the bounding box of the entity to determine whether to cull or not.
        - `affectedByCulling` - Returns whether the entity can be culled.
        - `render` only takes in the render state, along with the stack, buffer source, and packet light
        - `shouldShowName` now takes in a `double` for the camera squared distance from the entity
        - `getTextureLocation` is removed, being moved to the classes where it is used, like `LivingEntityRenderer`
            - Subsequent implementations of `getTextureLocation` may be protected or private
        - `renderNameTag` now takes in the render state instead of the entity and removes the partial tick `float`
        - `getNameTag` - Gets the name tag from the entity.
        - `getShadowRadius` now takes in the render state instead of the entity
        - `createRenderState` - Creates the render state object.
        - `extractRenderState` - Reads any data from the entity to the render state.
    - `EntityRendererProvider$Context` takes in the `MapRenderer` instead of the `ItemInHandRenderer`
    - `LivingRenderer`
        - `isShaking` now takes in the render state instead of the entity
        - `setupRotations` now takes in the render state instead of the entity
        - `getAttackAnim`, `getBob` are now within the render state
        - `getFlipDegrees` no longer takes in the entity
        - `getWhiteOverlayProgress` now takes in the render state instead of the entity and no longer takes in the entity Y rotation
        - `scale` now takes in the render state instead of the entity and no longer takes in the entity Y rotation
        - `shouldShowName` now takes in a `double` representing the squared distance to the camera
        - `getShadowRadius` now takes in the render state instead of the entity
    - `RaftRenderer` - A raft renderer that implements the `AbstractBoatRenderer`.
    - `RenderLayerParent#getTextureLocation` is removed
- `net.minecraft.client.renderer.entity.layers`
    - `EnergySwirlLayer#isPowered` - Returns whether the energy is powered.
    - `CustomHeadLayer` and `#translateToHead` takes in a `CustomHeadLayer$Transforms` instead of a scaling information hardcoding the transform
    - `PlayerItemInHandRenderer` takes in an `ItemRenderer` instead of a `ItemInHandRenderer`
    - `RenderLayer` takes in an `EntityRenderState` generic instead of an `Entity` generic
        - `coloredCutoutModelCopyLayerRender` takes in a single `EntityModel` with the state info bundled into the render state
        - `renderColoredCutoutModel` takes in non-generic forms of the rendering information, assuming a `LivingEntityRenderState`
        - `getTextureLocation` is removed, instead being passed directly into the appropriate location
        - `render` now takes in the render state instead of the entity and parameter information
    - `SaddleLayer` has a constructor to take in a baby model.
    - `SheepFurLayer` -> `SheepWoolLayer`
    - `StuckInBodyLayer` now takes in the model to apply the stuck objects to, the texture of the stuck objects, and the placement style of the objects
        - `numStuck` now takes in the render state instead of the entity
        - `renderStuckItem` is now private
    - `WardenEmissiveLayer` -> `LivingEntityEmissiveLayer`, a more generalized implementation
- `net.minecraft.client.renderer.entity.player.PlayerRenderer`
    - `renderRightHand`, `renderLeftHand` now take in a `ResourceLocation` instead of the `AbstractClientPlayer` and a `boolean` whether to render the left and/or right sleeve
    - `setupRotations` now takes in the render state instead of the entity and parameter information
- `net.minecraft.world.entity`
    - `AnimationState#copyFrom` - Copies the animation state from another state.
    - `Entity`
        - `noCulling` -> `EntityRenderer#affectedByCulling`
        - `getBoundingBoxForCulling` -> `EntityRenderer#getBoundingBoxForCulling`
    - `LerpingModel` class is removed
    - `PowerableMob` class is removed

### Model Baking

`UnbakedModel`s now have a different method to resolve any dependencies. Instead of getting the dependencies and resolving the parents, this is now done through a single method called `resolveDependencies`. This method takes in the `Resolver`. The `Resolver` is responsible for getting the `UnbakedModel` for the `ResourceLocation`.

```java
// For some UnbakedModel instance
public class MyUnbakedModel implements UnbakedModel {

    @Nullable
    protected ResourceLocation parentLocation;
    @Nullable
    protected UnbakedModel parent;
    private final List<ItemOverride> overrides;

    // ...

    @Override
    public void resolveDependencies(UnbakedModel.Resolver resolver) {
        // Get parent model for delegate resolution
        if (this.parentLocation != null) {
            this.parent = resolver.resolve(this.parentLocation);
        }
    }
}
```

- `net.minecraft.client.renderer.block`
    - `BlockModel#getDependencies`, `resolveParents` -> `resolveDependencies`
    - `BlockModelDefintion` now takes in a `MultiPart$Definition`, no `List<BlockModelDefinition>` constructor exists
        - `fromStream`, `fromJsonElement` no longer take in a `$Context`
        - `getVariants` is removed
        - `isMultiPart` is removed
        - `instantiate` -> `MultiPart$Definition#instantiate`
    - `MultiVariant` is now a record
    - `UnbakedBlockStateModel` - An interface that represents a block state model, contains a single method to group states together with the same model.
    - `VariantSelector` - A utility for constructing the state definitions from the model descriptor.
- `net.minecraft.client.renderer.block.model`
    - `BlockModel`
        - `MISSING_MATERIAL` - The material of the missing block texture.
        - `bake` no longer takes in the `ModelBaker` and `BlockModel`
        - `$LoopException` class is removed
- `net.minecraft.client.renderer.block.model.multipart.MultiPart` now implements `UnbakedBlockStateModel`
    - `getSelectors` -> `$Definition#selectors`
    - `getMultiVariants` ->` $Definition#getMultiVariants`
- `net.minecraft.client.resources.model`
    - `BakedModel#getOverrides` -> `overrides`, method is defaulted to an empty override
    - `BlockStateModelLoader` only takes in the missing unbaked model
        - `loadAllBlockStates` is removed
        - `definitionLocationToBlockMapper` - Gets the state definition from a given resource location
        - `loadBlockStateDefinitions` -> `loadBlockStateDefinitionStack`
        - `getModelGroups` -> `ModelGroupCollector`
        - `$LoadedJson` -> `$LoadedBlockModelDefinition`
        - `$LoadedModel` is now public
        - `$LoadedModels` - A record which maps a model location to a loaded model.
    - `BuiltInModel` no longer takes in the `ItemOverrides`
    - `DelegateBakedModel` - A utility implementation that delegates all logic to the supplied `BakedModel`
    - `Material#buffer` takes in another `boolean` that handles whether to apply the glint
    - `MissingBlockModel` - The missing model for a block.
    - `ModelBaker#getModel` is removed, implementation in `ModelBakery$ModelBakerImpl` is private
    - `ModelBakery` only takes in the top models, all unbacked models, and the missing model
        - `BUILTIN_SLASH` -> `SpecialModels#builtinModelId`
        - `BUILTIN_SLASH_GENERATED` -> `SpecialModels#BUILTIN_GENERATED`
        - `BUILTIN_BLOCK_ENTITY` -> `SpecialModels#BUILTIN_BLOCK_ENTITY`
        - `MISSING_MODEL_LOCATION` -> `MissingBlockModel#LOCATION`
        - `MISSING_MODEL_VARIANT` -> `MissingBlockModel#VARIANT`
        - `GENERATION_MARKER` -> `SpecialModels#GENERATED_MARKER`
        - `BLOCK_ENTITY_MARKER` -> `SpecialModels#BLOCK_ENTITY_MARKER`
        - `getModelGroups` -> `ModelGroupCollector`
    - `ModelDiscovery` - A loader for block and item models, such as how to resolve them when reading.
    - `ModelGroupCollector` - A blockstate collector meant to map states to their associated block models.
    - `ModelResourceLocation#vanilla` is removed
    - `MultiPartBakedModel` fields are now obtained from the first model in the selector and are private
        - `$Builder` class is removed, replaced with `$Selector`
    - `SimpleBakedModel`, `SimpleBakedModel$Builder` no longer takes in the `ItemOverrides`
    - `SpecialModels` - A utility for builtin models.
    - `UnbakedModel`
        - `getDependencies`, `resolveParents` -> `resolveDependencies`
        - `bake` is no longer nullable
        - `$Resolver` - Determines how the unbaked model should be loaded when on top or on override.
    - `WeightedBakedModel` now takes in a `SimpleWeightedRandomList` rather than a list of `WeightedEntry`s

## Equipments and Items, Models and All

Equipments and Items have had a major overhaul, most of which is spread throughout this documentation. This is some of the core change which, while they are important, do not deserve more than a cursory explanation due to their ease of change.

### Item Names and Models

The item name and model is now set directly within the properties using the `ITEM_NAME` and `ITEM_MODEL` data components, respectively. By default, this will use the same name and model location as previously, but these can be set via `Item$Properties#overrideDescription` and `#overrideModel`. `overrideDescription` takes in the translation key to use. There is also `useBlockDescriptionPrefix` and `useItemDescriptionPrefix` to change it to the default block and item translation keys, respectively. `overrideModel` takes in the relative `ResourceLocation` of the model JSON. For example, a value of `examplemod:example_item` will map to a `ModelResourceLocation` of `examplemod:example_item#inventory`. This is intended to link to the model JSON at `assets/examplemod/models/item/example_item.json`.

> There is a slight quirk to item models. The same key can also point to `assets/examplemod/models/example_item.json` if the modder decides to for a special model to load at that location under the `inventory` variant. So, it is recommended to avoid having model names with the same name in the root `models` and `models/item` subdirectory.

### Enchantable, Repairable Items

The enchantment value and repair item checks are being replaced with data components: `DataComponents#ENCHANTABLE` and `DataComponents#REPAIRABLE`, respectively. These can be set via the `Item$Properties#enchantable` and `#repairable`. As a result, `Item#getEnchantmentValue` and `isValidRepairItem` are removed.

### Elytras -> Gliders

Any item can act similarly to an elytra if they have the `DataComponents#GLIDER` value equipped. This essentially functions as a flag to indicate that the item can be used to glide. This works only if the item also has a `DataComponents#EQUIPPABLE` entry.

```java
new Item(
    new Item.Properties()
        .component(DataComponents.GLIDER, Unit.INSTANCE) // Sets as a glider
        .component(DataComponents.EQUIPPABLE, /*...*/) // Determines the slot to check whether it can be used
);
```

### Tools, via Tool Materials

`Tier`s within items have been replaced with `ToolMaterial`, which better handles the creation of tools and swords without having to implement each method manually. `ToolMaterial` takes in the same arguments as `Tier`, just as parameters to a single constructor rather than as implementable methods. From there, the `ToolMaterial` is passed to the `DiggerItem` subtypes, along with two floats representing the attack damage and attack speed. Interally, `ToolMaterial#apply*Properties` is called, which applies the `ToolMaterial` info to the `DataComponents#TOOL` and the attributes from the given `float`s.

```java
// Some tool material
public static final ToolMaterial WOOD = new ToolMaterial(
    BlockTags.INCORRECT_FOR_WOODEN_TOOL, // Tier#getIncorrectBlocksForDrops
    59, // Tier#getUses
    2.0F, // Tier#getSpeed
    0.0F, // Tier#getAttackDamageBonus
    15, // Tier#getEnchantmentValue
    ItemTags.WOODEN_TOOL_MATERIALS // Tier#getRepairIngredient
);

// When constructing the digger item subtype
new PickaxeItem(
    WOOD, // Tool material
    1.0f, // Attack damage
    -2.8f, // Attack speed
    new Item.Properties()
)
```

## Armor Materials, Equipment, and Model (Textures)

This is, by far, the largest change outside of consumables to items. `ArmorMaterial`s have effectively been made obsolete, as almost all of the logic is handled within data components, and attached to some resource pack JSON to load the associated textures. It is annoyingly complicated to understand at first glance, but is rather intuitive once you are familiar with the process.

### `ArmorMaterial`

`ArmorMaterial` is essentially a record that converts a list of properties to their proper location on the data components, NOT a registry object. This is done by passing in the item properties and an additional setting to either `#humanoidProperties` or `#animalProperties`. These settings should be familiar, as they remained unchanged from the previous version, the only difference is that they now specify a 'model id', which we will go into below. The armor material is used in conjunction with the `ArmorType`: an enum which defines the equipment slot the armor is placed into, the unit durability of each armor type, and the name (which is only used to construcct the attribute modifier id).

```java
ArmorMaterial exampleArmorMaterial = new ArmorMaterial(
    15, // The scalar durability to multiply the armor type against
    // A map of ArmorType -> half armor bars to apply to the entity ARMOR attribute
    // Should be set for all ArmorTypes
    Util.make(new EnumMap<>(ArmorType.class), map -> {
        map.put(ArmorType.BOOTS, 2);
        map.put(ArmorType.LEGGINGS, 5);
        map.put(ArmorType.CHESTPLATE, 6);
        map.put(ArmorType.HELMET, 2);
        map.put(ArmorType.BODY, 5);
    },
    25, // The enchantment value of the armor
    SoundEvents.ARMOR_EQUIP_IRON, // The holder wrapped sound event on what sound to make when the item is equipped
    0f, // The ARMOR_TOUGHNESS attribute value
    2f, // The KNOCKBACK_RESISTANCE attribute value,
    ItemTags.REPAIRS_DIAMOND_ARMOR, // An item tag representing the items that can repair this armor
    // The relative location of the EquipmentModel JSON
    // Points to assets/examplemod/models/equipment/example_armor_material.json
    ResourceLocation.fromNamespaceAndPath("examplemod", "example_armor_material")
)
```

With the `ArmorMaterial`, this is either applied to the item properties by calling `humanoidProperties`, to apply the armor to a specific `ArmorType`; or `animalProperties` to apply the armor to the `BODY` and only allow specific entities to wear them.

Does this mean that `ArmorItem` and `AnimalArmorItem` are effectively pointless? For `AnimalArmorItem`, this can be argued. The only thing that `AnimalArmorItem` does is have a `$BodyType` parameter, which means that the armor can only be applied to a horse or a wolf, and specifies the item breaking sound. `ArmorItem`, on the other hand, only has one specific usecase: determining whether the item can be taken off or swapped. This implicity checks the currently wearing armor item to see whether it can't be taken off (via `PREVENT_ARMOR_CHANGE` enchantment) and calculating the properties on the replacing armor material so that any hotswaps will only improve their wearer's armor attribute values.

Let's go one level deeper.

### The Data Components

`ArmorMaterial` specifies eight data components to apply to item:

- `MAX_DAMAGE` - Set to the maximum durability of the item multiplied by the `ArmorType` unit durability
- `MAX_STACK_SIZE` - Set to 1
- `DAMAGE` - Set to 0
- `ATTRIBUTE_MODIFIERS` - Sets `ARMOR` and `ARMOR_TOUGHNESS` attributes, and `KNOCKBACK_RESISTANCE` when greater than 0
- `ENCHANTABLE` - Set to the enchantment value (not set when calling `animalProperties`)
- `REPAIRABLE` - Set to the `HolderSet` of the tag key representing the repairing ingredients
- `EQUIPPABLE` - Sets the slot, equip sound, model id, what entities can wear the item, and whether it is dispensible

Everything but `EQUIPPABLE` has already been explained above or has been around from a prior version, so this primer will only focus on `EQUIPPABLE` from now on.

### Equippable

`Equippable`, which used to be an interface, is now a data component that contains how an entity can equip this item and if the equipment should be rendered. Because of this, an item can only be equipped to a single slot. This can be done using the `Equippable` constructor or through the builder via `Equippable#builder`.

```java
new Item(
    new Item.Properties()
    .component(DataComponents.EQUIPPABLE, new Equippable(
        EquipmentSlot.HEAD, // The slot the item can be equipped to
        SoundEvents.ARMOR_EQUIP_IRON, // The sound to play when equipping the item
        // The relative location of the EquipmentModel JSON
        // Points to assets/examplemod/models/equipment/example_armor_material.json
        // When set to an empty optional, the item does not attempt to render as equipment
        Optional.of(ResourceLocation.fromNamespaceAndPath("examplemod", "example_armor_material")),
        // The relative location over the texture to overlay on the player screen when wearing
        // Points to assets/examplemod/textures/example_overlay.png
        // When set to an empty optional, does not render on the player screen
        Optional.of(ResourceLocation.withDefaultNamespace("examplemod", "example_overlay")),
        // A HolderSet of entities (direct or tag) that can equip this item
        // When set to an empty optional, any entity can equip this item
        Optional.of(HolderSet.direct(EntityType::builtInRegistryHolder, EntityType.ZOMBIE)),
        // Whether the item can be equipped when dispensed from a dispenser 
        true,
        // Whether the item can be swapped off the player during a quick equip
        false,
        // Whether the item should be damaged when attacked (for equipment typically)
        // Must also be a damageable item
        false
    ))
);
```

### Equipment Models?

So, as mentioned previously, `Equippable` items, and by extension the `ArmorMaterial` delegate, can specify a model id to determine how the equipment should render. However, what does this id link to? Well, it points to an `EquipmentModel` serialized as a JSON within `models/equipment` of the resource pack. This JSON defines the layers and textures of the equippable item to render. This does NOT specify the model, making the record a misnomer. It is better to think of this as the equipment textures applied to the passed in model.

`EquipmentModel` functions as a more feature generic version of the previous `ArmorMaterial$Layer`, which has been removed. Each `EquipmentModel` is functionally a map of `$LayerType`s to a list of `$Layer` to render.

A `$LayerType` is an enum representing the layer to render the equipment model as. While these are non-specific, they are implemented and read by specific entity renderer through the layer renderers. For example, `HUMANOID` is used by the `HumanoidArmorLayer` to render the head, chest, and feet; so any usage of `HUMANOID` will be rendered using that system. Another example is `WOLF_BODY` is used by `WolfArmorLayer` to render the body armor. As such, if using existing layer types (which is the only scenario unless your mod loader supports enum extensions), make sure that they are compatible with the existing renderers in place.

The `$Layer` list specifies the texture and dyeable options to use when rendering over the passed in model. The first parameter specifes the texture location, relative to `textures/entity/equipment`. The second parameter specifies an optional indicating whether the texture can be tinted (stored via the `ItemTags#DYEABLE` in conjunction with the `DYED_COLOR` data component). When specified, an optional color can be specified for when the item is not dyed. If empty, the armor will be invisible when not dyed. The final parameter indicates whether it should use the texture provided to the renderer instead, such as when rendering a custom elytra texture for the player.

```json5
// In assets/examplemod/models/equipment/example_armor_material.json
{
    // The layer map
    "layers": {
        // The serialized name of the EquipmentModel$LayerType to apply to
        "humanoid": [
            // A list of layers to render in the order provided in the list
            {
                // The relative texture of the layer
                // Points to assets/examplemod/textures/entity/equipment/example.png
                "texture": "examplemod:example",
                // When specified, allows the texture to be tinted the color in DYED_COLOR data component
                // Otherwise, cannot be tinted
                "dyeable": {
                    // An RGB value (always opaque color)
                    // 0x7683DE as decimal
                    // When not specified, set to 0 (meaning transparent or invisible)
                    "color_when_undyed": 7767006
                },
                // When true, uses the texture passed into the layer renderer instead
                "use_player_texture": true
            }
            // ...
        ]
        // ...
    }
}
```

```java
EquipmentModel.builder()
    .addLayers(EquipmentModel.LayerType.HUMANOID, new EquipmentModel.Layer(
        // The relative texture of the layer
        // Points to assets/examplemod/textures/entity/equipment/example.png
        ResourceLocation.fromNamespaceAndPath("examplemod", "example"),
        // When specified, allows the texture to be tinted the color in DYED_COLOR data component
        // Otherwise, cannot be tinted
        Optional.of(new EquipmentModel.Dyeable(
            // An RGB value (always opaque color)
            // When not specified, set to 0 (meaning transparent or invisible)
            Optional.of(0x7683DE)
        )),
        // When true, uses the texture passed into the layer renderer instead
        true
    )/*, ... */)
    .build();
```

The equipment model is then rendered by calling `EquipmentLayerRenderer#renderLayers` in the render function of an `EntityRenderer` or `RenderLayer`. `EquipementLayerRenderer` is passed in as part of the render context via `EntityRendererProvider$Context#getEquipmentRenderer`.

```java
// In some render method where EquipmentLayerRenderer equipmentLayerRenderer is a field
this.equipmentLayerRenderer.renderLayers(
    // The layer type to render
    EquipmentModel.LayerType.HUMANOID,
    // The model id representing the EquipmentModel JSON
    // This would be set in the `EQUIPPABLE` data component via `model`
    ResourceLocation.fromNamespaceAndPath("examplemod", "example_armor_material"),
    // The model to apply the textures to
    // These are usually separate models from the entity model
    // and are separate ModelLayers linking to a LayerDefinition
    model,
    // The item stack representing the item being rendered as a model
    // This is only used to get the dyeable, foil, and armor trim information
    stack,
    // The stack used to render the model in the correct location
    poseStack,
    // The source of the buffers to get the vertex consumer of the render type
    bufferSource,
    // The packed light texture
    lighting,
    // An absolute path of the texture to render when use_player_texture is true for one of the layer if not null
    ResourceLocation.fromNamespaceAndPath("examplemod", "textures/other_texture.png");
)
```

### Technical Changes to Items

- `net.minecraft.client.Minecraft#getEquipmentModels` - Gets the `EquipmentModelSet` that contains the current equipment model textures.
- `net.minecraft.client.gui.GuiGraphics#renderTooltip`, `renderComponentTooltip` now has a parameter to take in the relative directory of the background and frame textures of the tooltip, or the default if `null`
- `net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil#renderTooltipBackground` now has a parameter to take in the relative directory of the background and frame textures of the tooltip, or the default if `null`
- `net.minecraft.client.renderer.block.model`
    - `ItemOverrides` -> `BakedOverrides`
        - The construct no longer takes in the parent `BlockModel`
        - `resolve` -> `findOverride`, does not take in the fallback model
    - `ItemOverride`, `ItemOverride$Predicate` is now a record
        - `getPredicates` is removed, use `predicates`
        - `getModel` -> `model`
- `net.minecraft.client.renderer.entity`
    - `EntityRenderDispatcher` now takes in the `EquipmentModelSet`
    - `EntityRendererProvider$Context`
        - `getEquipmentModels` - Gets the current equipment textures.
        - `getEquipmentRenderer` - Gets the renderer for the equipment.
    - `ItemRenderer` no longer takes in the `Minecraft` instance and `TextureManager`
        - `TRIDENT_MODEL`, `SPYGLASS_MODEL` is now public
        - `TRIDENT_IN_HAND_MODEL`, `SPYGLASS_IN_HAND_MODEL` is removed
        - `getItemModelShaper` is removed
        - `renderBundleWithSelectedItem` -> `renderBundleItem`, not one-to-one
- `net.minecraft.client.renderer.entity.layers`
    - `CapeLayer` now takes in the `EquipmentModelSet`
    - `ElytraLayer` -> `WingsLayer`
        - The constructor now takes in the `EquipmentLayerRenderer`
    - `EquipmentLayerRenderer` - A renderer for equipment layers on the provided model.
    - `HorseArmorLayer` now takes in the `EquipmentLayerRenderer`
    - `HumanoidArmorLayer` now teaks in the `EquipmentLayerRenderer` instead of the `ModelManager`
        - `shouldRender` - Returns whether the equippable item should be rendered in the given slot.
    - `LlamaDecorLayer` now takes in the `EquipmentLayerRenderer`
    - `WolfArmorLayer` now takes in the `EquipmentLayerRenderer`
- `net.minecraft.client.renderer.entity.player.PlayerRenderer#getArmPose` is now private, replaced publically with a method that only takes in the `HumanoidArm` and `PlayerRenderState`
- `net.minecraft.client.resources.model`
    - `EquipmentModelSet` - A resource listener that loads the `EquipmentModel`s from `models/equipment`.
    - `ItemModel` - A model for an item.
- `net.minecraft.core.component.DataComponents`
    - `ITEM_MODEL` - Returns the model of the item. The `item/` is stripped, meaning that `minecraft:apple` points to `minecraft/textures/models/item/apple.json`.
    - `EQUIPPABLE` - Indicates that an item is equippable in the given slot. Also contains the model to render for the equipment.
    - `GLIDER` - Indicates that an item can be used to glide across the air. Must also be used in conjunction with `EQUIPPABLE`.
    - `TOOLTIP_STYLE` - Determines the relative location representing how the tooltip should render
- `net.minecraft.core.dispenser.EquipmentDispenseItemBehavior` - Handles how equipment is dispensed from a dispenser.
- `net.minecraft.core.registries.BuiltInRegistries#`, `Registries#ARMOR_MATERIAL` is no longer a registry, handled purely through data components
- `net.minecraft.world.entity`
    - `EquipmentSlot#getFilterFlag` -> `getId`
        - Also a method `getFilterBit` for converting the ID to a bit mask
    - `LivingEntity`
        - `canContinueToGlide` -> `canGlide`, no longer takes in the `ItemStack`
        - `canTakeItem` replaced by `DataComponents#EQUIPPABLE`
        - `canEquipWithDispenser` - Returns whether the stack can be equipped when spat from a dispenser.
        - `canDispenserEquipIntoSlot` - An entity override that specifies whether a dispenser can put eequipment into a given slot.
        - `isEquippableInSlot` - Returns whether the stack can be equipped in the given slot.
        - `canGlideUsing` - Whether the entity can glide with the stack in the provided slot.
    - `Mob`
        - `canReplaceCurrentItem` now takes in the `EquipmentSlot`
        - `isBodyArmorItem` replaced by `DataComponents#EQUIPPABLE`
- `net.minecraft.world.entity.animal.horse`
    - `Horse#isBodyArmorItem` replaced by `DataComponents#EQUIPPABLE`
    - `Llama#isBodyArmorItem`, `getSwag` replaced by `DataComponents#EQUIPPABLE`
- `net.minecraft.world.item`
    - `AnimalArmorItem` no longer extends `ArmorItem`
        - The constructor no longer takes in a boolean indicating the overlay texture, as that is now part of the `EquipmentModel`
        - The constructor can take in an optional `Holder<SoundEvent>` of the equip sound
        - The constructor can take in a `boolean` representing whether the armor should be damaged if the entity is hurt
        - `$BodyType` no takes in the allowed entities to wear the armor rather than the path factory to the texture
    - `ArmorItem` is no longer equipable
        - Basically functions as an item class where its only remaining usage is to prevent armor change when enchanted and get the associated attributes
        - `$Type` -> `ArmorType`
    - `ArmorMaterial` -> `.equipment.ArmorMaterial`
        - Bascially a dummy record to easily handle applying the associated data components (`MAX_DAMAGE`, `ATTRIBUTE_MODIFIERS`, `ENCHANTABLE`, `EQUIPPABLE`, `REPAIRABLE`)
    - `ArmorMaterials` -> `.equipment.ArmorMaterials`
    - `BookItem`, `EnchantedBookItem` -> `DataComponents#WRITTEN_BOOK_CONTENT`
    - `BundleItem` now takes in a `ResourceLocation` for the model rather than just strings
        - `$Mutable#setSelectedItem` -> `toggleSelectedItem`
    - `ComplexItem` class is removed
    - `ElytraItem` class is removed, now just and item with `DataComponents#GLIDER`
    - `Equippable` -> `.equipment.Equippable`, now a record which defines how an item can be equipped
    - `FoodOnAStackItem` parameter order has been switched
    - `InstrumentItem` parameter order has been switched
    - `Item`
        - `descriptionId` is now protected
        - `getDescription` -> `getName`
        - `getOrCreateDescriptionId` is removed
        - `getDescriptionId(ItemStack)` -> `DataComponents#ITEM_NAME`
        - `isEnchantable`, `getEnchantmentValue` is removed
        - `isValidRepairItem` is removed
        - `getDefaultAttributeModifiers` is removed
        - `getDamageSource` - Returns the damage source this item makes against the `LivingEntity`
        - `isComplex` is removed
        - `$Properties`
            - `equippable` - Sets an equippable component, defining how an item can be equipped
            - `equippableUnswappable` - Sets an equippable commponent that cannot be swapped via a key shortcut.
            - `overrideDescription` - Sets the translation key of the item.
            - `overrideModel` - Sets the model resource location.
        - `getCraftingRemainingItem`, `hasCraftingRemainingItem` -> `getCraftingRemainder`
    - `ItemNameBlockItem` class is removed, just a normal `Item` `useItemDescriptionPrefix` as a property
    - `ItemStack`
        - `ITEM_NON_AIR_CODEC` -> `Item#CODEC`
        - `isValidRepairItem` - Returns whether the stack can be repaired by this stack.
        - `nextDamageWillBreak` - Checks if the next damage taken with break the item.
        - `getDescriptionId` -> `getItemName`, not one-to-one, as now it returns the full component
    - `ShieldItem` no longer implements `Equippable`, passed in through `DataComponents#EQUIPPABLE`
    - `SignItem` parameter order has been switched
    - `SmithingTemplateItem` parameter order has been swtiched, removes `FeatureFlag`s
    - `StandingAndWallBlockItem` paramter order has been switched
    - `AxeItem` now takes in two floats representing the attack damage and attack speed
    - `DiggerItem` now takes in two floats representing the attack damage and attack speed
        - `createAttributes` -> `ToolMaterial#applyToolProperties`
    - `HoeItem` now takes in two floats representing the attack damage and attack speed
    - `PickaxeItem` now takes in two floats representing the attack damage and attack speed
    - `ShovelItem` now takes in two floats representing the attack damage and attack speed
    - `SwordItem` now takes in two floats representing the attack damage and attack speed
        - `createAttributes` -> `ToolMaterial#applySwordProperties`
    - `Tier` -> `ToolMaterial`
    - `TieredItem` class is removed
    - `Tiers` constants are stored on `ToolMaterial`
- `net.minecraft.world.item.alchemy.Potion` name is now required
        - `getName` -> `name`, not one-to-one as this is stored directly on the potion without any other processing
- `net.minecraft.world.item.armortrim.*` -> `.equipment.trim.*`
- `net.minecraft.world.item.component`
    - `Tool` methods that return `Tool$Rule` now only take the `HolderSet` of blocks and not a list or tag key
    - `DamageResistant` - A component that holds a tag of damage types the item is resistant to as an entity or being worn
- `net.minecraft.world.item.enchantment`
    - `Enchantable` - The data component object for the item's enchantment value.
    - `Repairable` - The data component object for the items that can repair this item.
- `net.minecraft.world.level.block`
    - `AbstractSkullBlock` no longer implements `Equippable`
    - `EquipableCarvedPumpkinBlock` class is removed, as replaced by `DataComponents#EQUIPPABLE`
    - `WoolCarpetBlock` no longer implements `Equippable`

## Interaction Results

`InteracitonResult`s have been completely modified to encompass everything to one series of sealed implementations. The new implementation of `InteractionResult` combines both `InteractionResultHolder` and `ItemInteractionResult`, meaning that all uses have also been replcaed.

`InteractionResult` is now an interface with four implementations depending on the result type. First there is `$Pass`, which indicates that the interaction check should be passed to the next object in the call stack. `$Fail`, when used for items and blocks, prevents anything further in the call stack for executing. For entities, this is ignored. Finally, `$TryEmptyHandInteraction` tells the call stack to try to apply the click with no item in the hand, specifically for item-block interactions.

There is also `$Success`, which indicates that the interaction was successful and can be consumed. A success specifies two pieces of information: the `$SwingSource`, which indicates where the source of the swing originated from (`CLIENT` or `SERVER`) or `NONE` if not specified, and `$ItemContext` that handles whether there was an iteraction with the item in the hand, and what the item was transformed to.

None of the objects should be directly initialized. The implementations are handled through six constants on the `InteractionResult` interface:

- `SUCCESS` - A `$Success` object that swings the hand on the client.
- `SUCCESS_SERVER` - A `$Success` object that swings the hand on the server.
- `CONSUME` - A `$Success` object that does not swing the hand.
- `FAIL` - A `$Fail` object.
- `PASS` - A `$Pass` object.
- `TRY_WITH_EMPTY_HAND` - A `$TryEmptyHandInteraction` object.

```java
// For some method that returns an InteractionResult
return InteractionResult.PASS;
```

For success objects, if the item interaction should transform the held stack, then you call `$Success#heldItemTransformedTo`, or `$Success#withoutItem` if no item was used for the interaction.

```java
// For some method that returns an InteractionResult
return InteractionResult.SUCCESS.heldItemTransformedTo(new ItemStack(Items.APPLE));

// Or
return InteractionResult.SUCCESS.withoutItem();
```

- `net.minecraft.core.cauldron.CauldronInteraction`
    - `interact` now returns an `InteractionResult`
    - `fillBucket`, `emptyBucket` now returns an `InteractionResult`
- `net.minecraft.world`
    - `InteractionResultHolder`, `ItemInteractionResult` -> `InteractionResult`
- `net.minecraft.world.item`
    - `Equipable#swapWithEquipmentSlot` now returns an `InteractionResult`
    - `Item#use`, `ItemStack#use` now returns an `InteractionResult`
    - `ItemUtils#startUsingInstantly` now returns an `InteractionResult`
    - `JukeboxPlayable#tryInsertIntoJukebox` now returns an `InteractionResult`
- `net.minecraft.world.level.block.state.BlockBehaviour#useItemOn`, `$BlockStateBase#useItemOn` now returns an `InteractionResult`

## Instruments, the Datapack Edition

`Instrument`s (not `NoteBlockInstrument`s) are now a datapack registry, meaning they must be defined in JSON or datagenned.

```json5
// In data/examplemod/instrument/example_instrument.json
{
    // The registry name of the sound event
    "sound_event": "minecraft:entity.arrow.hit",
    // How many seconds the instrument is used for
    "use_duration": 7.0,
    // The block range, where each block is 16 units
    "range": 256.0,
    // The description of the instrument
    "description": {
        "translate": "instrument.examplemod.example_instrument"
    },
}
```

```java
// For some RegistrySetBuilder builder
builder.add(Registries.INSTRUMENT, bootstrap -> {
    bootstrap.register(
        ResourceKey.create(Registries.INSTRUMENT, ResourceLocation.fromNamespaceAndPath("examplemod", "example_instrument")),
        new Instrument(
            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.ARROW_HIT),
            7f,
            256f,
            Component.translatable(Util.makeDescriptionId("instrument", ResourceLocation.fromNamespaceAndPath("examplemod", "example_instrument")))
        )
    )
});
```

- `net.minecraft.world.item`
    - `Instrument` takes in a `float` for the use duration and a `Component` description.
    - `InstrumentItem#setRandom` is removed

## Trial Spawner Configurations, now in Datapack Form

`TrialSpawnConfig` are now a datapack registry, meaning they must be defined in JSON or datagenned.

```json5
// In data/examplemod/trial_spawner/example_config.json
{
    // The range the entities can spawn from the trial spawner block
    "spawn_range": 2,
    // The total number of mobs that can be spawned
    "total_mobs": 10.0,
    // The number of mobs that can be spawned at one time
    "simultaneous_mobs": 4.0,
    // The number of mobs that are added for each player in the trial
    "total_mobs_added_per_player": 3.0,
    // The number of mobs that can be spawned at one time that are added for each player in the trial
    "simultaneous_mobs_added_per_player": 2.0,
    // The ticks between each spawn
    "ticks_between_spawn": 100,
    // A weighted list of entities to select from when spawning
    "spawn_potentials": [
        {
            // The SpawnData
            "data": {
                // Entity to spawn
                "entity": {
                    "id": "minecraft:zombie"
                }
            },
            // Weighted value
            "weight": 1
        }
    ],
    // A weight list of loot tables to select from when the reward is given
    "loot_tables_to_eject": [
        {
            // The loot key
            "data": "minecraft:spawners/ominous/trial_chamber/key",
            // Weight value
            "weight": 1
        }
    ],
    // Returns the loot table to use when the the trial spawner is ominous
    "items_to_drop_when_ominous": "minecraft:shearing/bogged"
}
```

```java
// For some RegistrySetBuilder builder
builder.add(Registries.TRIAL_SPAWNER_CONFIG, bootstrap -> {
    var entityTag = new CompoundTag();
    entityTag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ZOMBIE).toString());

    bootstrap.register(
        ResourceKey.create(Registries.INSTRUMENT, ResourceLocation.fromNamespaceAndPath("examplemod", "example_config")),
        TrialSpawnerConfig.builder()
            .spawnRange(2)
            .totalMobs(10.0)
            .simultaneousMobs(4.0)
            .totalMobsAddedPerPlayer(3.0)
            .simultaneousMobsAddedPerPlayer(2.0)
            .ticksBetweenSpawn(100)
            .spawnPotentialsDefinition(
                SimpleWeightedRandomList.single(new SpawnData(entityTag, Optional.empty(), Optional.empty()))
            )
            .lootTablesToEject(
                SimpleWeightedRandomList.single(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY)
            )
            .itemsToDropWhenOminous(
                BuiltInLootTables.BOGGED_SHEAR
            )
            .build()
    )
});
```

- `net.minecraft.world.level.block.entity.trialspawner`
    - `TrialSpawner` now takes in a `Holder` of the `TrialSpawnerConfig`
        - `canSpawnInLevel` now takes in a `ServerLevel`
    - `TrialSpawnerConfig`
        - `CODEC` -> `DIRECT_CODEC`
        - `$Builder`, `builder` - A builder for a trial spawner configuration

## Recipe Providers, the 'not actually' of Data Providers

`RecipeProvider` is no longer a `DataProvider`. Instead, a `RecipeProvider` is constructed via a `RecipeProvider$Runner` by implementing `createRecipeProvider`. The name of the provider must also be specified.

```java
public class MyRecipeProvider extends RecipeProvider {

    // The parameters are stored in protected fields
    public MyRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        // Register recipes here
    }

    // The runner class, this should be added to the DataGenerator as a DataProvider
    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries)
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new VanillaRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "My Recipes";
        }
    }
}
```

- `net.minecraft.data.recipes`
    - `RecipeOutput#includeRootAdvancement` - Generates the root advancement for recipes.
    - `RecipeProvider` no longer extends `DataProvider`
        - The constructor takes in the lookup provider and a `RecipeOutput`, which are protected fields
        - `buildRecipes` does not take in any parameters
        - All generation methods do not take in a `RecipeOutput` and are instance methods
        - `$FamilyRecipeProvider` - Creates a recipe for a `BlockFamily` by passing in the `Block` the resulting block and the base block.
        - `$Runner` - A `DataProvider` that constructs the `RecipeProvider` via `createRecipeProvider`
    - `ShapedRecipeBuilder`, `ShapelessRecipeBuilder` now have private constructors and take in a holder getter for the items

## The Ingredient Shift

`Ingredient`s have be reimplemented to use a `HolderSet` as its base rather that it own internal `Ingredient$Value`. This most changes the call to `Ingredient#of` as you either need to supply it with `Item` objects or the `HolderSet` representing the tag. For more information on how to do this, see the [holder set section](#the-holder-set-transition).

- `net.minecraft.world.item.crafting.Ingredient`
    - `EMPTY` -> `Ingredient#of`, though the default usecases do not allow empty ingredients
    - `CODEC` is removed
    - `CODEC_NONEMPTY` -> `CODEC`
    - `testOptionalIngredient` - Tests whether the stack is within the ingredient if present, or default to an empty check if not.
    - `getItems` -> `items`
    - `getStackingIds` is removed
    - `of(ItemStack...)`, `of(Stream<ItemStack>)` is removed
    - `of(TagKey)` -> `of(HolderSet)`, need to resolve tag key

## BlockEntityTypes Privatized!

`BlockEntityType`s have been completely privatized and the builder being removed! This means that if a mod loader or mod does not provide some access widening to the constructor, you will not be able to create new block entities. The only other change is that the `Type` for data fixers was removed, meaning that all that needs to be supplied is the client constructor and the set of valid blocks the block entity can be on.

```java
// If the BlockEntityType constructor is made public
// MyBlockEntity(BlockPos, BlockState) constructor
BlockEntityType<MyBlockEntity> type = new BlockEntityType(MyBlockEntity::new, MyBlocks.EXAMPLE_BLOCK);
```

## Consumables

Consuming an item has been further expanded upon, with most being transitioned into separate data component entries.

### The `Consumable` Data Component

The `Consumable` data component defines how an item is used when an item is finished being used. This effectively functions as `FoodProperties` used to previously, except all consumable logic is consolidated in this one component. A consumable has five properties: the number of seconds it takes to consume or use the item, the animation to play while consuming, the sound to play while consuming, whether particles should appear during consumption, and the [effects to apply once the consumption is complete](#consumeeffect).

A `Consumable` can be applied using the `food` item property. If only the `Consumable` should be added, then `component` should be called. A list of vanilla consumables and builders can be found in `Consumables`.

```java
// For some item
Item exampleItem = new Item(new Item.Properties().component(DataComponents.CONSUMABLE,
    Consumable.builder()
    .consumeSeconds(1.6f) // Will use the item in 1.6 seconds, or 32 ticks
    .animation(ItemUseAnimation.EAT) // The animation to play while using
    .sound(SoundEvents.GENERIC_EAT) // The sound to play while using the consumable
    .soundAfterConsume(SoundEvents.GENERIC_DRINK) // The sound to play after consumption (delegates to 'onConsume')
    .hasConsumeParticles(true) // Sets whether to display particles
    .onConsume(
        // When finished consuming, applies the effects with a 30% chance
        new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3F)
    )
    // Can have multiple
    .onConsume(
        // Teleports the entity randomly in a 50 block radius
        new TeleportRandomlyConsumeEffect(100f)
    )
    .build()
));
```

#### `OnOverrideSound`

Sometimes, an entity may want to play a different sound when consuming an item. In that case, the entity can implement `Consumable$OverrideConsumeSound` and return the sound event that should be played.

```java
// On your own entity
public class MyEntity extends Mob implements Consumable.OverrideCustomSound {
    // ...

    @Override
    public SoundEvent getConsumeSound(ItemStack stack) {
        // Return the sound event to play
    }
}
```

### `ConsumableListener`

`ConsumableListener`s are data components that indicate an action to apply once the stack has been 'consumed'. This means whenever `Consumable#consumeTicks` has passed since the player started using the consumable. An example of this would be `FoodProperties`. `ConsumableListener` only has one method `#onConsume` that takes in the level, entity, stack doing the consumption, and the `Consumable` that has finished being consumed.

```java
// On your own data component
public record MyDataComponent() implements ConsumableListener {

    // ...

    @Override
    public void onConsume(Level level, LivingEntity entity, ItemStack stack, Consumable consumable) {
        // Perform stuff once the item has been consumed.
    }
}
```

### `ConsumeEffect`

There is now a data component that handles what happens when an item is consumed by an entity, aptly called a `ConsumeEffect`. The current effects range from adding/removing mob effects, teleporting the player randomly, or simply playing a sound. These are applied by passing in the effect to the `Consumable` or `onConsume` in the builder.

```java
// When constructing a consumable
Consumable exampleConsumable = Consumable.builder()
    .onConsume(
        // When finished consuming, applies the effects with a 30% chance
        new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3F)
    )
    // Can have multiple
    .onConsume(
        // Teleports the entity randomly in a 50 block radius
        // NOTE: CURRENTLY BUGGED, only allows for 8 block raidus
        new TeleportRandomlyConsumeEffect(100f)
    )
    .build();
```

### On Use Conversion

Converting an item into another stack on consumption is now handled through `DataComponents#USE_REMAINDER`. The remainder will only be converted if the stack is empty after this use. Otherwise, it will return the current stack, just with one item used.

```java
// For some item
Item exampleItem = new Item(new Item.Properties().usingConvertsTo(
    Items.APPLE // Coverts this into an apple on consumption
)); 
Item exampleItem2 = new Item(new Item.Properties().component(DataComponents.USE_REMAINDER,
    new UseCooldown(
        new ItemStack(Items.APPLE, 3) // Converts into three apples on consumption
    )
));
```

### Cooldowns

Item cooldowns are now handled through `DataComponents#USE_COOLDOWN`; however, they have been expanded to apply cooldowns to stacks based on their defined group. A cooldown group either refers to the `Item` registry name if not specified, or a custom resource location. When applying the cooldown, it will store the cooldown instance on anything that matches the defined group. This means that, if a stack has some defined cooldown group, it will not be affected when a normal item is used.

```java
// For some item
Item exampleItem = new Item(new Item.Properties().useCooldown(
    60 // Wait 60 seconds
    // Will apply cooldown to items in the 'my_mod:example_item' group (assuming that's the registry name)
)); 
Item exampleItem2 = new Item(new Item.Properties().component(DataComponents.USE_COOLDOWN,
    new UseCooldown(
        60, // Wait 60 seconds
        // Will apply cooldown to items in the 'my_mod:custom_group' group
        Optional.of(ResourceLocation.fromNamespaceAndPath("my_mod", "custom_group"))
    )
));
```

- `net.minecraft.core.component.DataComponents#FOOD` -> `CONSUMABLE`
- `net.minecraft.world.entity.LivingEntity`
    - `getDrinkingSound`, `getEatingSound` is removed, handled via `ConsumeEffect`
    - `triggerItemUseEffects` is removed
    - `eat` is removed
- `net.minecraft.world.entity.npc.WanderingTrader` now implements `Consumable$OverrideConsumeSound`
- `net.minecraft.world.food`
    - `net.minecraft.world.food.FoodData`
        - `tick` now takes in a `ServerPlayer`
        - `getLastFoodLevel`, `getExhaustionLevel`, `setExhaustion` is removed
    - `FoodProperties` is now a `ConsumableListener`
        - `eatDurationTicks`, `eatSeconds` -> `Consumable#consumeSeconds`
        - `usingConvertsTo` -> `DataComponents#USE_REMAINDER`,
        - `effects` -> `ConsumeEffect`
- `net.minecraft.world.item`
    - `ChorusFruitItem` class is removed
    - `HoneyBottleItem` class is removed
    - `Item`
        - `getDrinkingSound`, `#getEatingSound` is removed, handled via `ConsumeEffect`
        - `releaseUsing` now returns a `boolean` whether it was successfully released
        - `$Properties#food` can now take in a `Consumable` for custom logic
        - `$Properties#usingConvertsTo` - The item to convert to after use.
        - `$Properties#useCooldown` - The amount of seconds to wait before the item can be used again.
    - `ItemCooldowns` now take in `ItemStack`s or `ResourceLocation`s to their methods rather than just an `Item`
        - `getCooldownGroup` - Returns the key representing the group the cooldown is applied to
    - `ItemStack#getDrinkingSound`, `getEatingSound` is removed
    - `MilkBucketItem` class is removed
    - `OminousBottleItem` class is removed
    - `SuspiciousStewItem` class is removed
- `net.minecraft.world.item.alchemy.PotionContents` now implements `ConsumableListener`
    - The constructor takes in an optional string representing the translation key suffix of the custom name
    - `applyToLivingEntity` - Applies all effects to the provided entity.
    - `getName` - Gets the name component by appending the custom name to the end of the provided contents string.
- `net.minecraft.world.item.component`
    - `Consumable` - A data component that defines when an item can be consumed.
    - `ConsumableListener` - An interface applied to data components that can be consumed, executes once consumption is finished.
    - `SuspiciousStewEffects` now implements `ConsumableListener`
    - `UseCooldown` - A data component that defines how the cooldown for a stack should be applied.
    - `UseRemainder` - A data component that defines how the item should be replaced once used up.
    - `DeathProtection` - A data component that contains a list of `ConsumeEffect`s on what to do when using the item to survive death.
- `net.minecraft.world.item.consume_effects.ConsumeEffect` - An effect to apply after the item has finished being consumed.

## Registry Objcet Id, in the Properties?

When providing the `BlockBehaviour$Properties` to the `Block` or the `Item$Properties` to the `Item`, it must set the `ResourceKey` in the block directly by calling `#setId`. An error will be thrown if this is not set before passing in.

```java
new Block(BlockBehaviour.Properties.of()
    .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("examplemod", "example_block"))));

new BlockItem(exampleBlock, new Item.Properties()
    .useBlockDescriptionPrefix() // Makes the description id for a block item
    .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("examplemod", "example_item"))));

new Item(new Item.Properties()
    .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("examplemod", "example_item"))));
```

- `net.minecraft.world.item.Item$Properties`
    - `setId` - Sets the resource key of the item to get the default description and model from. This property must be set.
    - `useBlockDescriptionPrefix` - Creates the description id using the `block.` prefix.
    - `useItemDescriptionPrefix` - Creates the description id using the `item.` prefix.
- `net.minecraft.world.level.block.state.BlockBehaviour$Properties#setId` - Sets the resource key of the block to get the default drops and description from. This property must be set.

## Properties Changes

`DirectionProperty` has been removed, and must now be called and referenced via `EnumProperty#create` with a `Direction` generic. Additionally, all property classes have been made final and must be constructed through one of the exposed `create` methods.

- `net.minecraft.world.level.block.state.properties`
    - `BooleanProperty` is now final
    - `DirectionProperty` class is removed
    - `EnumProperty` is now final
        - `create` now takes in a `List` instead of a `Collection`
    - `IntegerProperty` is now final
    - `Property#getPossibleValues` now returns a `List` instead of a `Collection`

## Recipes, now in Registry format

Recipes have been upgraded to a data pack registry, similar to how loot tables are handled. They are still queried in the same fashion, it just simply using a pseudo-registry-backed instance. Some of the more common changes is that `RecipeHolder` may be replaced by `RecipeDisplayId`, `RecipeDisplay`, or `RecipeDisplayEntry` if the holder itself is not needed. With this, there are a few changes to how recipe books are handled.

### Recipe Books

`RecipeBookComponent`s have been modified somewhat to hold a generic instance of the menu to render. As such, the component no longer implements `PlacedRecipe` and instead takes in a generic representing the `RecipeBookMenu`. The menu is passed into the component via its constructor instead of through the `init` method. This also menas that `RecipeBookMenu` does not have any associated generics. To create a component, the class needs to be extended.

```java
// Assume some MyRecipeMenu extends AbstractContainerMenu
public class MyRecipeBookComponent extends RecipeBookComponent<MyRecipeMenu> {

    public MyRecipeBookComponent(MyRecipeMenu menu, List<RecipeBookComponent.TabInfo> tabInfos) {
        super(menu, tabInfos);
        // ...
    }

    @Override
    protected void initFilterButtonTextures() {
        // ...
    }

    @Override
    protected boolean isCraftingSlot(Slot slot) {
        // ...
    }

    @Override
    protected void selectMatchingRecipes(RecipeCollection collection, StackedItemContents contents) {
        // ...
    }

    @Override
    protected Component getRecipeFilterName() {
        // ...
    }

    @Override
    protected void fillGhostRecipe(GhostSlots slots, RecipeDisplay display, ContextMap ctx) {

    }
}

public class MyContainerScreen extends AbstractContainerScreen<MyRecipeMenu> implements RecipeUpdateListener {

    public MyContainerScreen(MyRecipeMenu menu, List<RecipeBookComponent.TabInfo> tabInfos, ...) {
        super(menu, ...);
        this.recipeBookComponent = new MyRecipeBookComponent(menu, tabInfos);
    }
    

    // See AbstractFurnaceScreen for a full implementation
}
```

### Recipe Displays

However, how does a recipe understand what should be displayed in a recipe book? This falls under two new static registries: the `RecipeDisplay` and the `SlotDisplay`.

The `SlotDisplay` represents what displays in a single slot within a recipe. The display only has one method (ignoring types): `resolve`. `resolve` takes in the `ContextMap` holding the data and the `DisplayContentsFactory` which accepts the stacks and remainders that will be displayed in this slot. `SlotDisplay` also has a lot of helper implementations, such as `$Composite` that takes in a list of displays or `$ItemStackSlotDisplay` that takes in the stack to display. The display is registered by its `$Type`, which takes in the map codec and stream codec.

The slot also has methods to get for the associated stacks that can be displayed via `resolveForStacks` and `resolveForFirstStack`.

```java
public static record MySlotDisplay() implements SlotDisplay {

    @Override
    public <T> Stream<T> resolve(ContextMap ctx, DisplayContentsFactory<T> output) {
        // Call output.forStack(...) or addRemainder(..., ...) using instanceof to display items
        if (output instanceof ForStacks<T> stacks) {
            stacks.forStack(...);
        } else if (output instanceof ForRemainders<T> remainders) {
            remainders.addRemainder(..., ...);
        }
    }

    @Override
    public SlotDisplay.Type<? extends SlotDisplay> type() {
        // Return the registered object here registered to Registries#SLOT_DISPLAY
    }
}
```

`RecipeDisplay` represents how a recipe is displayed. As an implementation detail, the `RecipeDisplay` only needs to be aware of the result (via `result` slot display) and the place the recipe is being used (via `craftingStation` slot display) as those are the only two details the recipe book cares about. However, it is recommended to also have slot displays for the ingredients and then have those consumed by your `RecipeBookComponent`. The display is registered by its `$Type`, which takes in the map codec and stream codec.


```java
public record MyRecipeDisplay(SlotDisplay result, SlotDisplay craftingStation, ...) implements RecipeDisplay {

    @Override
    public RecipeDisplay.Type<? extends RecipeDisplay> type() {
        // Return the registered object here registered to Registries#RECIPE_DISPLAY
    }
}
```

### Recipe Placements

Recipe ingredients and placements within the recipe book are now handled through `Recipe#placementInfo`. A `PlacementInfo` is basically a definition of items the recipe contains and where they should be placed within the menu if supported. If the recipe cannot be placed, such as if it is not an `Item` or uses stack information, then it should return `PlacementInfo#NOT_PLACEABLE`.

A `PlacementInfo` can be created either from an `Ingredient`, a `List<Ingredient>`, or a `List<Optional<Ingredient>>` using `create` or `createFromOptionals`, respectively.

```java
public class MyRecipe implements Recipe<RecipeInput> {

    private PlacementInfo info;

    public MyRecipe(Ingredient input) {
        // ...
    }

    // ...

    @Override
    public PlacementInfo placementInfo() {
        // This delegate is done as the HolderSet backing the ingredient may not be fully populated in the constructor
        if (this.info == null) {
            this.info = PlacementInfo.create(input);
        }

        return this.info;
    }
}
```

If an `Optional<Ingredient>` is used, they can be tested via `Ingredient#testOptionalIngredient`.

- `net.minecraft.world.item.crafting`
    - `Ingredient#display` - Returns the `SlotDisplay` that shows this ingredient.
    - `PlacementInfo` - Defines all ingredients necessary to construct the result of a recipe.
    - `Recipe`
        - `getToastSymbol` -> `getCategoryIconItem`
        - `getIngredients`, `isIncomplete` -> `placementInfo`
            - `getIngredients` -> `PlacementInfo#stackedRecipeContents`,
            - `isIncomplete` -> `PlacementInfo#isImpossibleToPlace`
    - `RecipeManager#getSynchronizedRecipes` - Returns all recipes that can be placed and sends them to the client. No other recipes are synced.
    - `ShapedRecipePattern` now takes in a `List<Optional<Ingredient>>` instead of a `NonNullList<Ingredient>`
    - `ShapelessRecipe` now takes in a `List<Ingredient>` instead of a `NonNullList<Ingredient>`
    - `SmithingTransformRecipe`, `SmithingTrimRecipe` now takes in `Optional<Ingredient>`s instead of `Ingredient`s
    - `SuspiciousStewRecipe` class is removed

### Recipe Changes

There have been a few changes within the recipe class itself, which mirror all of the above changes. First, `canCraftInDimensions` is removed and now hardcoded into the match function. `getResultItem` and `getCategoryIconItem` has been replaced by `RecipeDisplay` via `display`. `getRemainingItems` has moved to `CraftingRecipe`. Finally, all recipes now return their `RecipeBookCategory` via `recipeBookCategory`.

```java
public class MyRecipe implements Recipe<RecipeInput> {

    @Override
    public String group() {
        // Return here what `getGroup` was
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(
            // Some recipe display instance
            // RecipeDisplay#result should return `getResultItem`
            // RecipeDisplay#craftingStation should return `getCategoryIconItem`
        )
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        // Functions similar to the book category passed into the recipe builders during data generation
        return RecipeBookCategories.CRAFTING_MISC;
    }
}
```

### Creating Recipe Book Categories

Recipe book categories are unified by `ExtendedRecipeBookCategory` and split into two sections: `RecipeBookCategory` for actual categories, and `SearchRecipeBookCategory` for aggregate categories. While `SearchRecipeBookCategory`s are enums, `RecipeBookCategory` is like any other static registry object. This is done by creating a new `RecipeBookCategory`.

```java
// Using the standard vanilla registry method
public static final RecipeBookCategory EXAMPLE_CATEGORY = Registry.register(
    BuiltInRegistries.RECIPE_BOOK_CATEGORY,
    // The registry object name
    ResourceLocation.fromNamespaceAndPath("examplemod", "example_category"),
    // This creates a new recipe book category. It functions as a marker object.
    new RecipeBookCategory()
);
```

### Technical Changes

- `net.minecraft.advancements.AdvancementRewards` now takes in a list of `ResourceKey`s instead of `ResourceLocation`s for the recipe
    - `$Builder#recipe`, `addRecipe` now takes in a `ResourceKey`
- `net.minecraft.advancements.critereon`
    - `PlayerPredicate` now takes in a `ResourceKey` for the recipe map
        - `$Builder#addRecipe` now takes in a `ResourceKey`
    - `RecipeCraftedTrigger`
        - `trigger` now takes in a `ResourceKey`
        - `$TriggerInstance` now takes in a `ResourceKey`
        - `$TriggerInstance#craftedItem`, `crafterCraftedItem` now takes in a `ResourceKey`
    - `RecipeUnlockedTrigger`
        - `unlocked` now takes in a `ResourceKey`
        - `$TriggerInstance` now takes in a `ResourceKey`
- `net.minecraft.client`
    - `ClientRecipeBook`
        - `setupCollections` -> `rebuildCollections`, not one-to-one
        - `getCollection(RecipeBookCategories)` -> `getCollection(ExtendedRecipeBookCategory)`
        - `add`, `remove` - Handles adding/removing a recipe entry to display within the recipe book.
        - `addHighLight`, `removeHighlight`, `hasHighlight` - Handles if the entry is highlighted when filtered or selected by the player.
        - `clear` - Clears the known and highlighted recipes.
    - `RecipeBookCategories#*_MISC` -> `SearchRecipeBookCategory#*`
        - This can also be replaced within methods by `RecipeBookComponent$TabInfo`, `ExtendedRecipeBookCategory`, or `RecipeBookCategory`
- `net.minecraft.client.gui.components.toasts`
    - `RecipeToast(RecipeHolder)` -> `RecipeToast()`, now private
    - `addOrUpdate` now takes in a `RecipeDisplay` instead of a `RecipeHolder`
- `net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen`
    - `recipeBookComponent` is now private
    - `AbstractFurnaceScreen(T, AbstractFurnaceRecipeBookComponent, Inventory, Component, ResourceLocation, ResourceLocation, ResourceLocation)` - `AbstractFurnaceRecipeBookComponent` has been replaced with a `Component` as the recipe book is not constructed internally and now takes in a list of `RecipeBookComponent$TabInfo`
- `net.minecraft.client.gui.screens.recipebook`
    - `AbstractFurnaceReipceBookComponent`, `BlastingFurnaceReipceBookComponent`, `SmeltingFurnaceReipceBookComponent`, `SmokingFurnaceReipceBookComponent` -> `FurnaceReipceBookComponent`
    - `GhostRecipe` -> `GhostSlots` not one-to-one, as the recipe itself is stored as a private field in `RecipeBookComponent` as a `RecipeHolder`
        - `addResult` -> `setResult`, not one-to-one
        - `addIngredient` -> `setIngredient`, not one-to-one
        - `setSlot`, `setInput`, `setResult` now take in a `ContextMap`
    - `OverlayRecipeComponent()` -> `OverlayRecipeComponent(SlotSelectTime, boolean)`
        - `init` takes in a `ContextMap` containing registry data to display within the components and a `boolean` representing whether the recipe book is filtering instead of computing it from the `Minecraft` instance
        - `getLastRecipeClicked` now returns a `RecipeDisplayId`
        - `$OverlayRecipeButton` is now an abstract package-private class, taking in the `ContextMap`
        - `$Pos` is now a record
    - `RecipeBookComponent` no longer implements `RecipeShownListener`
        - The constructor takes in a list of `$TabInfo`s containing the tabs shown in the book
        - `init` no longer takes in a `RecipeBookMenu`
        - `initVisuals` is now private
        - `initFilterButtonTextures` is now abstract
        - `updateCollections` now takes in another boolean representing if the book is filtering
        - `renderTooltip` now takes in a nullable `Slot` instead of an `int` representing the slot index
        - `renderGhostRecipe` no longer takes in a float representing the delay time
        - `setupGhostRecipe` -> `fillGhostRecipe`, no longer takes in the `List<Slot>` to place, that is stored within the component itself
        - `selectMatchingRecipes` no longer takes in the `RecipeBook`
        - `recipesShown` now takes in a `RecipeDisplayId`
        - `setupGhostRecipeSlots` -> `fillGhostRecipe`, taking in the `ContextMap`
        - `$TabInfo` - A record that denotes the icons and categories of recipe to display within a recipe book page.
    - `RecipeBookPage()` -> `RecipeBookPage(RecipeBookComponent, SlotSelectTime, boolean)`
        - `updateCollections` now takes in a boolean representing if the book is filtering
        - `getMinecraft` is removed
        - `addListener` is removed
        - `getLastRecipeClicked` now returns a `RecipeDisplayId`
        - `recipesShown` now takes in a `RecipeDisplayId`
        - `getRecipeBook` now returns a `ClientRecipeBook`
    - `RecipeBookTabButton` now takes in a `RecipeBookComponent$TabInfo`
        - `startAnimation(Minecraft)` -> `startAnimation(ClientRecipeBook, boolean)`
        - `getCategory` now returns a `ExtendedRecipeBookCategory`
    - `RecipeButton()` -> `RecipeButton(SlotSelectTime)`
        - `init` now takes in a `boolean` representing if the book is filtering and a `ContextMap` holding the registry data
        - `getRecipe` -> `getCurrentRecipe`, not one-to-one
        - `getDisplayStack` - Returns the result stack of the recipe.
        - `getTooltipText` now takes in the `ItemStack`
    - `RecipeCollection(RegistryAccess, List<RecipeHolder>)` -> `RecipeCollection(List<RecipeDisplayEntry>)`
        - `canCraft` ->  `selectRecipes`
        - `getRecipes`, `getDisplayRecipes` -> `getSelectedRecipes`
        - `registryAccess`, `hasKnownRecipes`, `updateKnownRecipes` is removed
        - `isCraftable` now takes in a `RecipeDisplayId`
        - `hasFitting` -> `hasAnySelected`
        - `getRecipes` now returns a list of `RecipeDisplayEntry`s
    - `RecipeShownListener` class is removed
    - `RecipeUpdateListener`
        - `getRecipeBookComponent` is removed
        - `fillGhostRecipe` -> Fills the ghost recipe given the `RecipeDisplay`
    - `SearchRecipeBookCategory` - An enum which holds the recipe book categories for aggregate types.
    - `SlotSelectTime` - Represents the current index of the slot selected by the player.
- `net.minecraft.client.multiplayer`
    - `ClientPacketListener#getRecipeManager` -> `recipes`, returns `RecipeAccess`
    - `ClientRecipeContainer` - A client side implementation of the `RecipeAccess` when synced from the server.
    - `MultiPlayerGameMode#handlePlaceRecipe` now takes in a `RecipeDisplayId`
    - `SessionSearchTrees#updateRecipes` now takes in a `Level` instead of the `RegistryAccess$Frozen`
- `net.minecraft.client.player.LocalPlayer#removeRecipeHightlight` now takes in a `RecipeDisplayId`
- `net.minecraft.commands.SharedSuggestionProvider#getRecipeNames` is removed as it can be queried from the registry access
- `net.minecraft.commands.arguments.ResourceLocationArgument`
    - `getRecipe` -> `ResourceKeyArgument#getRecipe`
    - `getAdvancement` -> `ResourceKeyArgument#getAdvancement`
- `net.minecraft.commands.synchronization.SuggestionProviders#ALL_RECIPES` is removed
- `net.minecraft.core.component.DataComponents#RECIPES` now takes in a list of `ResourceKey`s
- `net.minecraft.data.recipes`
    - `RecipeBuilder#save` now takes in a `ResourceKey` instead of a `ResourceLocation`
    - `RecipeOutput#accept` now takes in a `ResourceKey` instead of a `ResourceLocation`
    - `RecipeProvider#trimSmithing` now takes in a `ResourceKey` instead of a `ResourceLocation`
- `net.minecraft.network.protocol.game`
    - `ClientboundPlaceGhostRecipePacket` - A packet that contains the container id and the `RecipeDisplay`
    - `ClientboundRecipeBookAddPacket` - A packet that adds entries to the recipe book
    - `ClientboundRecipeBookRemovePacket` - A packet that removes entries to the recipe book
    - `ClientboundRecipeBookSettingsPacket` - A packet that specifies the settings of the recipe book
    - `ClientboundRecipePacket` class is removed
    - `ClientboundUpdateRecipesPacket` is now a record, taking in the property sets of the recipes and the stonecutter recipes
        - `getRecipes` is removed
    - `ServerboundPlaceRecipePacket` is now a record
    - `ServerboundRecipeBookSeenRecipePacket` is now a record
- `net.minecraft.recipebook`
    - `PlaceRecipe` -> `PlaceRecipeHelper`
        - `addItemToSlot` -> `$Output#addItemToSlot`
        - `placeRecipe` now takes in a `Recipe` instead of the `RecipeHolder`
            - There is an overload that takes in two more ints that represent the pattern height and width for a `ShapedRecipe`, or just the first two integers repeated
    - `RecipeBook`
        - `add`, `contains`, `remove` -> `ServerRecipeBook#add`, `contains`, `remove`
        - `addHighlight`, `removeHighlight`, `willHighlight` -> `ServerRecipeBook#addHighlight`, `removeHighlight`, `ClientRecipeBook#hasHighlight`
        - `bookSettings` is now protected
    - `RecipeBookSettings#read`, `write` is now private
    - `ServerPlaceRecipe` is not directly accessible anymore, instead it is accessed and returned as a `RecipeBookMenu$PostPlaceAction` via `#placeRecipe`
        - `$CraftingMenuAccess` - Defines how the placable recipe menu can be interacted with.
    - `ServerRecipeBook`
        - `fromNbt` now takes in a predicate of a `ResourceKey` instead of the `RecipeManager`
        - `copyOverData` - Reads the data from another recipe book.
        - `$DisplayResolver` - Resoluves the recipes to display by passing in a `RecipeDisplayEntry`
- `net.minecraft.stats.RecipeBook#isFiltering(RecipeBookMenu)` is removed
- `net.minecraft.world.entity.player`
    - `Player#awardRecipesByKey` now takes in a list of `ResourceKey`s
    - `StackedItemContents#canCraft` overloads that take in a list of ingredient infos
- `net.minecraft.world.inventory`
    - `AbstractCraftingMenu` - A menu for a crafting interface.
    - `AbstractFurnaceMenu` now takes in the `RecipePropertySet` key
    - `CraftingMenu#slotChangedCraftingGrid` now takes in a `ServerLevel` instead of a `Level`
    - `ItemCombinerMenu` now takes in an `ItemCombinerMenuSlotDefinition`
        - `mayPickup` now defaults to `true`
    - `ItemCombinerMenuSlotDefinition#hasSlot`, `getInputSlotIndexes` is removed
    - `RecipeBookMenu` no longer takes in any generics
        - `handlePlacement` is now abstract and returns a `$PostPlaceAction`, taking in an additional `ServerLevel`
            - This remove all basic placement recipes calls, as that would be handled internally by the `ServerPlaceRecipe`
    - `RecipeCraftingHolder#setRecipeUser` no longer takes in a `Level`
    - `SmithingMenu#hasRecipeError` - Returns whether the recipe had an error when placing items in the inventory.
- `net.minecraft.world.item.crafting`
    - `AbstractCookingRecipe` now implements `SingleItemRecipe`
        - The constructor no longer takes in the `RecipeType`, making the user override the `getType` method
        - `getExperience` -> `experience`
        - `getCookingTime` -> `cookingTime`
        - `furnaceIcon` - Returns the icon of the furnace.
        - `$Serializer` - A convenience implementation for the cooking recipe serializer instance.
    - `CookingBookCategory` now has an integer id
    - `CraftingRecipe#defaultCrafingRemainder` - Gets the stacks that should remain behind in the crafting recipe.
    - `CustomRecipe$Serializer` - A convenience implementation for the custom recipe serializer instance.
    - `ExtendedRecipeBookCategory` - A unifying interface that denotes a category within the recipe book.
    - `Ingredient#optionalIngredientToDisplay` - Converts an optional ingredient to a `SlotDisplay`.
    - `Recipe#getRemainingItems` -> `CraftingRecipe#getRemainingItems`
    - `RecipeAccess` - An accessor that returns the property sets that contain the inputs of available recipes.
    - `RecipeBookCategory` - An object that represents a single category within the recipe book.
    - `RecipeCache#get` now takes in a `ServerLevel` instead of a `Level`
    - `RecipeHolder` now takes in a `ResourceKey`
    - `RecipeManager` now extends `SimplePreparableReloadLsitener<RecipeMap>` and implements `RecipeAccess`
        - `prepare` - Creates the recipe map from the recipe registry
        - `logImpossibleRecipes`, `hasErrorsLoading` is removed
        - `getRecipeFor` now takes in a `ResourceKey` where there was a `ResourceLocation` repviously
        - `getRecipesFor`, `getAllRecipesFor` -> `RecipeMap#getRecipesFor`
        - `byType` is removed
        - `getRemainingItemsFor` is Removed
        - `byKey`.`byKeyTyped` now takes in a `ResourceKey`
        - `getOrderedRecipes` is revmoed
        - `getSynchronizedRecipes` -> `getSynchronizedItemProperties`, `getSynchronizedStonecutterRecipes`; not one-to-one
        - `getRecipeIds` is removed
        - `getRecipeFromDisplay` - Gets the recipe display info given its id.
        - `listDisplaysForRecipe` - Accepts a list of display entries of the recipes to display.
        - `replaceRecipes` is removed
        - `$CachedCheck#getRecipeFor` now takes in a `ServerLevel` instead of a `Level`
        - `$IngredientCollector` - A recipe consumer that extracts the ingredient from a recipe and adds it to a `RecipePropertySet`
        - `$IngredientExtractor` - A method that gets the ingredients of a recipe when present.
        - `$ServerDisplayInfo` - A record that links a display entry to its recipe holder.
    - `RecipeMap` - A class which maps recipe holders by their recipe type and resource key.
    - `RecipePropertySet` - A set of ingredients that can be used as input to a given recipe slot. Used to only allow specific inputs to slots on screens.
    - `SelectableRecipe` - A record that holds the slot display and its associated recipe. Currently only used for the stonecutting menu.
    - `SimpleCookingSerializer` -> `AbstractCookingRecipe$Serializer`
    - `SingleItemRecipe` no longer takes in the `RecipeType` or `RecipeSerializer`
        - `ingredient`, `result`, `group` is now private
        - `input`, `result` - The slots of the recipe.
- `net.minecraft.world.item.crafting.display`
    - `DisplayContentsFactory` - A factory for accepting contents of a recipe. Its subtypes accepts the stacks of the recipe and the remainder.
    - `RecipeDisplay` - A display handler to show the contents of a recipe.
    - `RecipeDisplayEntry` - A record that links the recipe display to its identifier, category, and crafting requirements.
    - `RecipeDisplayId` - An identifier for the recipe display.
    - `SlotDisplay` - A display handler to show the contents of a slot within a recipe.
    - `SlotDisplayContext` - Context keys used by slot displays.
- `net.minecraft.world.level.Level#getRecipeManager` -> `recipeAccess`, returns `RecipeAccess` on level but `RecipeManager` on `ServerLevel`
- `net.minecraft.world.level.block.CrafterBlock#getPotentialResults` now takes in a `ServerLevel` instead of a `Level`
- `net.minecraft.world.level.block.entity.CampfireBlockEntity`
    - `getCookableRecipe` is removed
    - `placeFood` now takes in a `ServerLevel` instead of a `Level`

## Minor Migrations

The following is a list of useful or interesting additions, changes, and removals that do not deserve their own section in the primer.

### Language File Removals and Renames

All removals and renames to translations keys within `assets/minecraft/lang` are now shown in a `deprecated.json`.

### Critereons, Supplied with HolderGetters

All critereon builders during construction now take in a `HolderGetter`. While this may not be used, this is used instead of a direct call to the static registry to grab associated `Holder`s and `HolderSet`s.

- `net.minecraft.advancement.critereon`
    - `BlockPredicate$Builder#of`
    - `ConsumeItemTrigger$TriggerInstance#usedItem`
    - `EntityEquipmentPredicate#captainPredicate`
    - `EntityPredicate$Builder#of`
    - `EntityTypePredicate#of`
    - `ItemPredicate$Builder#of`
    - `PlayerTrigger$TriggerInstance#walkOnBlockWithEquipment`
    - `ShotCrossbowTrigger$TriggerInstance#shotCrossbow`
    - `UsedTotemTrigger$TriggerInstance#usedToItem`

### MacosUtil#IS_MACOS

`com.mojang.blaze3d.platform.MacosUtil#IS_MACOS` has been added to replace specifying a boolean during the render process.

- `com.mojang.blaze3d.pipeline`
    - `RenderTarget#clear(boolean)` -> `clear()`
    - `TextureTarget(int, int, boolean, boolean)` -> `TextureTarget(int, int, boolean)`
- `com.mojang.blaze3d.platform.GlStateManager#_clear(boolean)` -> `_clear()`
- `com.mojang.blaze3d.systems.RenderSystem#clear(int, boolean)` -> `clear(int)`

### Fog Parameters

Fog methods for individual values have been replaced with a `FogParameters` data object.

- `com.mojang.blaze3d.systems.RenderSystem`
    - `setShaderFogStart`, `setShaderFogEnd`, `setShaderFogColor`, `setShaderFogShape` -> `setShaderFog`
    - `getShaderFogStart`, `getShaderFogEnd`, `getShaderFogColor`, `getShaderFogShape` -> `getShaderFog`
- `net.minecraft.client.renderer.FogRenderer`
    - `setupColor` -> `computeFogColor`, returns a `Vector4f`
    - `setupNoFog` -> `FogParameters#NO_FOG`
    - `setupFog` now takes in a `Vector4f` for the color and returns the `FogParameters`
    - `levelFogColor` is removed

### New Tags

- `minecraft:banner_pattern`
    - `bordure_indented`
    - `field_masoned`
- `minecraft:block`
    - `bats_spawnable_on`
    - `pale_oak_logs`
- `minecraft:damage_type`
    - `mace_smash`
- `minecraft:item`
    - `diamond_tool_materials`
    - `furnace_minecart_fuel`
    - `gold_tool_materials`
    - `iron_tool_materials`
    - `netherite_tool_materials`
    - `villager_picks_up`
    - `wooden_tool_materials`
    - `piglin_safe_armor`
    - `repairs_leather_armor`
    - `repairs_chain_armor`
    - `repairs_iron_armor`
    - `repairs_gold_armor`
    - `repairs_diamond_armor`
    - `repairs_netherite_armor`
    - `repairs_turtle_helmet`
    - `repairs_wolf_armor`
    - `duplicates_allays`
    - `brewing_fuel`
    - `panda_eats_from_ground`
    - `shulker_boxes`
    - `bundles`
    - `map_invisibility_equipment`
    - `pale_oak_logs`
    - `gaze_disguise_equipment`
- `minecraft:entity_type`
    - `boat`

### Smarter Framerate Limiting

Instead of simply limiting the framerate when the player is not in a level or when in a screen or overlay, there is different behavior depending on different actions. This is done using the `InactivityFpsLimit` via the `FramerateLimitTracker`. This adds two additional checks. If the window is minimized, the game runs at 10 fps. If the user provides no input for a minute, then the game runs at 30 fps. 10 fps after ten minutes of no input.

- `com.mojang.blaze3d.platform.FramerateLimitTracker` - A tracker that limits the framerate based on the set value.
- `com.mojang.blaze3d.platform#Window#setFramerateLimit`, `getFramerateLimit` is removed
- `net.minecraft.client`
    - `InactivityFpsLimit` - An enum that defines how the FPS should be limited when the window is minimzed or the player is away from keyboard.
    - `Minecraft#getFramerateLimitTracker` - Returns the framerate limiter.

### Fuel Values

`FuelValues` has replaced the static map within `AbstractFurnaceBlockEntity`. This functions the same as that map, except the fuel values are stored on the `MinecraftServer` itself and made available to individual `Level` instances. The map can be obtained with access to the `MinecraftServer` or `Level` and calling the `fuelValues` method.

- `net.minecraft.client.multiplayer.ClientPacketListener#fuelValues` - Returns the burn times for fuel.
- `net.minecraft.server.MinecraftServer#fuelValues` - Returns the burn times for fuel.
- `net.minecraft.server.level.Level#fuelValues` - Returns the burn times for fuel.
- `net.minecraft.world.level.block.entity`
    - `AbstractFurnaceBlockEntity`
        - `invalidateCache`, `getFuel` -> `Level#fuelValues`
        - `getBurnDuration` now takes in the `FuelValues`
        - `isFuel` -> `FuelValues#isFuel`
    - `FuelValues` - A class which holds the list of fuel items and their associated burn times

### Light Emissions

Light emission data is now baked into the quad and can be added to a face using the `light_emission tag`.

- `net.minecraft.client.renderer.block.model`
    - `BakedQuad` now takes in an `int` representing the light emission
        - `getLightEmission` - Returns the light emission of a quad.
    - `BlockElement` now takes in an `int` representing the light emission
    - `FaceBakery#bakeQuad` now takes in an `int` representing the light emission

### Map Textures

Map textures are now handled through the `MapTextureManager`, which handles the dynamic texture, and the `MapRenderer`, which handles the map rendering. Map decorations are still loaded through the `map_decorations` sprite folder.

- `net.minecraft.client`
    - `Minecraft`
        - `getMapRenderer` - Gets the renderer for maps.
        - `getMapTextureManager` - Gets the texture manager for maps.
- `net.minecraft.client.resources#MapTextureManager` - Handles creating the dynamic texture for the map.
- `net.minecraft.client.gui.MapRenderer` -> `net.minecraft.client.renderer.MapRenderer`
- `net.minecraft.client.renderer#GameRenderer#getMapRenderer` -> `Minecraft#getMapRenderer`

### Orientations

With the edition of the redstone wire experiments comes a new class provided by the neighbor changes: `Orientation`. `Orientation` is effectively a combination of two directions and a side bias. `Orientation` is used as a way to propogate updates relative to the connected directions and biases of the context. Currently, this means nothing for people not using the new redstone wire system as all other calls to neighbor methods set this to `null`. However, it does provide a simple way to propogate behavior in a stepwise manner.


- `net.minecraft.client.renderer.debug.RedstoneWireOrientationsRenderer` - A debug renderer for redstone wires being oriented.
- `net.minecraft.world.level.Level`
    - `updateNeighborsAt` - Updates the neighbor at the given position with the specified `Orientation`.
    - `updateNeighborsAtExceptFromFacing`, `neighborChanged` now takes in an `Orientation`
- `net.minecraft.world.level.block.RedStoneWireBlock`
    - `getBlockSignal` - Returns the strength of the block signal.
- `net.minecraft.world.level.block.state.BlockBehaviour`
    - `neighborChanged`, `$BlockStateBase#handleNeighborChanged` now takes in an `Orientation` instead of the neighbor `BlockPos`
    - `updateShape` now takes in the `LevelReader`, `ScheduledTickAccess`, and a `RandomSource` instead of the `LevelAccessor`; the `Direction` and `BlockState` parameters are reordered
    - `$BlockStateBase#updateShape` now takes in the `LevelReader`, `ScheduledTickAccess`, and a `RandomSource` instead of the `LevelAccessor`; the `Direction` and `BlockState` parameters are reordered
- `net.minecraft.world.level.redstone`
    - `CollectingNeighborUpdater$ShapeUpdate#state` -> `neighborState`
    - `NeighborUpdater`
        - `neighborChanged`, `updateNeighborsAtExceptFromFacing`, `executeUpdate` now takes in an `Orientation` instead of the neighbor `BlockPos`
        - `executeShapeUpdate` switches the order of the `BlockState` and neighbor `BlockPos`
    - `Orientation` - A group of connected `Directions` on a block along with a bias towards either the front or the up side.
    - `RedstoneWireEvaluator` - A strength evaluator for incoming and outgoing signals.

### Minecart Behavior

Minecarts now have a `MinecartBehavior` class that handles how the entity should be moved and rendered.

- `net.minecraft.core.dispenser.MinecartDispenseItemBehavior` - Defines how a minecart should behave when dispensed from a dispenser.
- `net.minecraft.world.entity.vehicle`
    - `AbstractMinecart`
        - `getMinecartBehavior` - Returns the behavior of the minecart.
        - `exits` is now public
        - `isFirstTick` - Returns whether this is the first tick the entity is alive.
        - `getCurrentBlockPosOrRailBelow` - Gets the current position of the minecart or the rail beneath.
        - `moveAlongTrack` -> `makeStepAlongTrack`
        - `setOnRails` - Sets whether the minecart is on rails.
        - `isFlipped`, `setFlipped` - Returns whetherh the minecart is upside down.
        - `getRedstoneDirection` - Returns the direction the redstone is powering to.
        - `isRedstoneConductor` is now public
        - `applyNaturalSlowdown` now returns the vector to slowdown by.
        - `getPosOffs` -> `MinecartBehavior#getPos`
        - `setInitialPos` - Sets the initial position of the minecart.
        - `createMinecart` is now abstract in its creation, meaning it can be used to create any minecart given the provided parameters
        - `getMinecartType` is removed
        - `getPickResult` is now abstract
        - `$Type` and `getMinecartType` is replaced by `isRideable` and `isFurnace`, which is not one-to-one.
    - `AbstractMinecartContainer(EntityType, double, double, double, Level)` is removed
    - `MinecartBehavior` - holds how the entity should be rendered and positions during movement.
    - `MinecartFurnace#xPush`, `zPush` -> `push`
- `net.minecraft.world.level.block.state.properties.RailShape#isAscending` -> `isSlope`
- `net.minecraft.world.phys.shapes.MinecartCollisionContext` - An entity collision context that handles the collision of a minecart with some other collision object.

### EXPLOOOOSSSION!

`Explosion` is now an interface that defines the metadata of the explosion. It does not contain any method to actually explode itself. However, `ServerExplosion` is still used internally to handle level explosions and the like.

- `net.minecraft.world.level`
    - `Explosion` -> `ServerExplosion`
    - `Explosion` - An interface that defines how an explosion should occur.
        - `getDefaultDamageSource` - Returns the default damage source of the explosion instance.
        - `shouldAffectBlocklikeEntities` - Returns whether block entites should be affected by the explosion.
        - `level` - Gets the `ServerLevel`
    - `ExplosionDamageCalculator#getEntityDamageAmount` now takes in an additional `float` representing the seen percent
    - `Level#explode` no longer returns anything
- `net.minecraft.world.level.block.Block#wasExploded` now takes in a `ServerLevel` instead of a `Level`
- `net.minecraft.world.level.block.state.BlockBehaviour#onExplosionHit`, `$BlockStateBase#onExplosionHit` now takes in a `ServerLevel` instead of a `Level`


### The Removal of the Carving Generation Step

`GenerationStep$Carving` has been removed, meaning that all `ConfiguredWorldCarver`s are provided as part of a single `HolderSet`.

```json
// In some BiomeGenerationSettings JSON
{
    "carvers": [
        // Carvers here
    ]
}
```

- `net.minecraft.world.level.biome.BiomeGenerationSettings`
    - `getCarvers` no longer takes in a `GenerationStep$Carving`
    - `$Builder#addCarver` no longer takes in a `GenerationStep$Carving`
    - `$PlainBuilder#addCarver` no longer takes in a `GenerationStep$Carving`
- `net.minecraft.world.level.chunk`
    - `ChunkGenerator#applyCarvers` no longer takes in a `GenerationStep$Carving`
    - `ProtoChunk#getCarvingMask`, `getOrCreateCarvingMask`, `setCarvingMask` no longer takes in a `GenerationStep$Carving`
- `net.minecraft.world.level.levelgen.placement`
    - `CarvingMaskPlacement` class is removed
    - `PlacementContext#getCarvingMask` no longer takes in a `GenerationStep$Carving`

### Codecable Json Reload Listener

The `SimpleJsonResourceReloadListener` has been rewritten to use codecs instead of pure `Gson`.

```java
public class MyJsonListener extends SimpleJsonResourceReloadListener<MyJsonObject> {

    // If you do not need registry access, the HolderLookup$Provider parameter can be removed
    public MyJsonListener(HolderLookup.Provider registries, Codec<T> codec, String directory) {
        super(registries, codec, directory);
    }
}
```

- `net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener` now takes in a generic representing the data object of the JSON
    - The constructor is now protected, taking in the codec of the data object, the string of the directory, and an optional `HolderLookup$Provider` to construct the `RegistryOps` serialization context as necessary
    - `prepare` now returns a map of names to objects
    - `scanDirectory` now takes in the `DynamicOps` and `Codec`

### Consecutive Executors

`ProcessorMailbox` and `ProcessorHandle` have been replaced with `AbstractConsecutiveExecutor` and `TaskScheduler`, respectively. These are effectively the same in their usage, just with potentially different method names.

- `net.minecraft.util.thread`
    - `ProcessorMailbox` -> `AbstractConsecutiveExecutor`, not one-to-one
        - `ConsecutiveExecutor` would be the equivalent implementation
    - `PriorityConsecutiveExecutor` - An executor that specifies the priority of the task to run when scheduling.
    - `BlockableEventLoop#wrapRunnable` -> `AbstractConsecutiveExecutor#wrapRunnable`
    - `ProcessorHandle` -> `TaskScheduler`, where the generic is a subtype of `Runnable`
        - `tell` -> `schedule`
        - `ask`, `askEither` -> `scheduleWithResult`, not one-to-one
        - `of` -> `wrapExecutor`
    - `StrictQueue` no longer takes in an `F` generic and makes `T` a subtype of `Runnable`
        - `pop` now returns a `Runnable`
        - `$IntRunnable` -> `$RunnableWithPriority`


### Mob Conversions

Mobs, converted via `#convertTo`, have their logic handled by `ConversionType`, `ConversionParams`. `ConversionType` is an enum that dictates the logic to apply when copying the information from one mob to another via `#convert`. The common properties are handled via `#convertCommon`, which is called within the `#convert` method. There are currently two types: `SINGLE`, where the entity is converted one-to-one to another entity; and `SPLIT_ON_DEATH`, where the `Mob#convertTo` method is called mutiple times such as when a slime dies. `ConversionParams` contains the metadata about the conversion process: the type, whether the entity can keep its equipment or pick up loot, and what team the entity is on. `Mob#convertTo` also takes in a mob consumer to apply any finalization settings to the entity itself.

```java
// For some Mob exampleMob
exampleMob.convertTo(
    EntityType.SHEEP, // The entity to convert to
    new ConversionParams(
        ConversionType.SINGLE, // One-to-one
        true, // Keep equipment
        false // Do not preserve pick up loot
    ),
    EntitySpawnReason.CONVERSION, // Reason entity spawned
    sheep -> {
        // Perform any other settings to set on the newly converted entity
    },
)
```

- `net.minecraft.world.entity`
    - `ConversionParams` - A record containing the settings of what happens when a mob is converted to another entity
    - `ConversionType` - An enum that defines how one mob is transformed to another. Currently either `SINGLE` for one-to-one, or `SPLIT_ON_DEATH` for one-to-many (only used for slimes)
    - `Mob#convertTo` now takes in the `ConversionParams`, an optional `EntitySpawnReason` of the entity (default `CONVERSION`), and a mob consumer to set any other information after conversion

### Ender Pearl Chunk Loading

Ender pearls now load the chunks they cross through by adding a ticket to the chunk source and storing the entity on the player.

- `net.minecraft.server.level.ServerPlayer`
    - `registerEnderPearl`, `deregisterEnderPearl`, `getEnderPearls` - Handles the ender pearls thrown by the player.
    - `registerAndUpdateEnderPearlTicket`, `placeEnderPearlTicket` - Handles the region tickets for the thrown ender pearls.

### Profilers and the Tracy Client

Profilers have been separated from the minecraft instance, now obtained through `Profiler#get`. A new profiler instance can be added via a try-resource block on `Profiler#use`. In addition, the profiler addds a new library called Tracy, made to track the current stack frame along with capturing images on the screen, if the associated `--tracy` argument is passed in. These sections can be split into 'zones' to more granularly distinguish what is happening.

```java
Profiler.get().push("section");
// Do code here
Profiler.get().pop();
```

- `com.mojang.blaze3d.systems.RenderSystem#flipFrame` now takes in a `TracyFrameCapture`, or `null`
- `net.minecraft.client.Minecraft#getProfiler` -> `Profiler#get`
- `net.minecraft.client.main.GameConfig$GameData` now takes in a boolean on whether to capture the screen via the tracy client.
- `net.minecraft.client.multiplayer.ClientLevel` no longer takes in the `ProfilerFiller`
- `net.minecraft.server.MinecraftServer#getProfiler` -> `Profiler#get`
- `net.minecraft.server.packs.resources.PreparableReloadListener#reload` no longer takes in the `ProfilerFiller`s
- `net.minecraft.util.profiling`
    - `Profiler` - A static handler for managing the currently active `ProfilerFiller`.
    - `ProfilerFiller`
        - `addZoneText` - Adds text to label when profiling the current frame.
        - `addZoneValue` - Adds the value of the zone when profiling the current frame.
        - `setZoneColor` - Sets the color of the zone when profiling the current frame.
        - `zone` - Adds a profiler section while creating a new zone to call the above methods for.
        - `tee` -> `combine`
        - `$CombinedProfileFiller` - A profiler that writes to multiple profilers.
    - `TracyZoneFiller` - A profiler used by the tracy client to keep track of the currently profiling zones.
    - `Zone` - A section that is current being profiled and interpreted by Tracy.
- `net.minecraft.world.entity.ai.goal.GoalSelector` no longer takes in the supplied `ProfilerFiller`
- `net.minecraft.world.level`
    - `Level` no longer takes in the `ProfilerFiller`
        - `getProfiler`, `getProfilerSupplier` -> `Profiler#get`
    - `PathNavigationRegion#getProfiler` -> `Profiler#get`
- `net.minecraft.world.ticks.LevelTicks` no longer takes in the `ProfilerFiller`

### Tick Throttler

To prevent the player from spamming certain actions, `TickThrottler` was added. The throttler takes in the threshold and the increment to add to the count. If the count is less than the threshold, the action can occur. The count is reduced every tick.

- `net.minecraft.util.TickThrottler` - A utility for throttling certain actions from happening too often.

### Context Keys

Loot context parameters have been replaced with Context keys, which is simply a more general naming scheme for the previous classes. This also caused the context keys to be used in other contexts that may have arbitrary data.

For a brief description, the context key system is effectively a general typed dictionary, where each `ContextKey` holds the value type, which is then stored in a backed-map within a `ContextMap`. To enforce required and optional parameters, a `ContextMap` is built with a `ContextKeySet`, which defines the keys of the dictionary map.

- `net.minecraft.advancements.critereon.CriterionValidator#validate` now takes in a `ContextKeySet` instead of a `LootContextParamSet`
- `net.minecraft.data.loot.LootTableProvider$SubProviderEntry#paramSet` now takes in a `ContextKeySet` instead of a `LootContextParamSet`
- `net.minecraft.util.context`
    - `ContextKey` - A key that represents an object. It can be thought of a dictionary key that specifies the value type.
    - `ContextKeySet` - A key set which indicates what keys the backing dictionary must have, along with optional keys that can be specified.
    - `ContextMap` - A map of context keys to their typed objects.
- `net.minecraft.world.item.enchantment`
    - `ConditionalEffect#codec` now takes in a `ContextKeySet` instead of a`LootContextParamSet`
    - `TargetedConditionalEffect#codec` now takes in a `ContextKeySet` instead of a`LootContextParamSet`
- `net.minecraft.world.level.storage.loot`
    - `LootContext`
        - `hasParam` -> `hasParameter`
        - `getParam` -> `getParameter`
        - `getParamOrNull` - `getOptionalParameter`
        - `$EntityTraget#getParam` now returns a `ContextKey` instead of a `LootContextParam`
    - `LootContextUser#getReferencedContextParams` now takes in a set of `ContextKey`s rather than a set of `LootContextParam`s
    - `LootParams` now takes in a `ContextMap` instead of a map of params to objects
        - `hasParam`, `getParameter`, `getOptionalParameter`, `getParamOrNull` are accessible through the `ContextMap` under different names
        - `$Builder#withParameter`, `withOptionalParameter`, `getParameter`, `getOptionalParameter` now takes in a `ContextKey` instead of a `LootContextParam`
        - `$Builder#create` now takes in a `ContextKeySet` instead of a `LootContextParamSet`
    - `LootTable`
        - `getParameSet` now returns a `ContextKeySet` instead of a `LootContextParamSet`
        - `$Builder#setParamSet` now takes in a `ContextKeySet` instead of a `LootContextParamSet`
    - `ValidationContext` now takes in a `ContextKeySet` instead of a `LootContextParamSet`
        - `validateUser` -> `validateContextUsage`
        - `setParams` - `setContextKeySet`
- `net.minecraft.world.level.storage.loot.functions`
    - `CopyComponentsFunction$Source#getReferencedContextParams` now takes in a set of `ContextKey`s rather than a set of `LootContextParam`s
- `net.minecraft.world.level.storage.loot.parameters`
    - `LootContextParam` -> `net.minecraft.util.context.ContextKey`
    - `LootContextParamSet` -> `net.minecraft.util.context.ContextKeySet`
- `net.minecraft.world.level.storage.loot.providers.nbt`
    - `ContextNbtProvider$Getter#getReferencedContextParams` now takes in a set of `ContextKey`s rather than a set of `LootContextParam`s
    - `NbtProvider#getReferencedContextParams` now takes in a set of `ContextKey`s rather than a set of `LootContextParam`s
- `net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProvider#getReferencedContextParams` now takes in a set of `ContextKey`s rather than a set of `LootContextParam`s

### List of Additions

- `com.mojang.blaze3d.framegraph`
    - `FrameGraphBuilder` - A builder that constructs the frame graph that define the resources used and the frame passes to render.
    - `FramePass` - An interface that defines how to read/write resources and execute them for rendering within the frame graph.
- `com.mojang.blaze3d.platform`
    - `ClientShutdownWatchdog` - A watchdog created for what happens when the client is shutdown.
    - `NativeImage#getPixelsABGR` - Gets the pixels of the image in ABGR format.
    - `Window`
        - `isIconified` - Returns whether the window is currently iconified (usually minimized onto the taskbar).
        - `setWindowCloseCallback` - Sets the callback to run when the window is closed.
- `com.mojang.blaze3d.resource`
    - `CrossFrameResourcePool` - Handles resources that should be rendered across multiple frames
    - `GraphicsResourceAllocator` - Handles resources to be rendered and removed.
    - `RenderTargetDescriptor` - Defines a render target to be allocated and freed.
    - `ResourceDescriptor` - Defines a resource and how it is allocated and freed.
    - `ResourceHandle` - Defines a pointer to an individual resource.
- `com.mojang.blaze3d.systems.RenderSystem#overlayBlendFunc` - Sets the default overlay blend function between layers with transparency.
- `com.mojang.blaze3d.vertex`
    - `PoseStack#translate(Vec3)` - Translates the top pose using a vector
    - `VertexConsumer#setNormal(PoseStack$Pose, Vec3)` - Sets the normal of a vertex using a vector
- `net.minecraft`
    - `Optionull#orElse` - If the first object is null, return the second object.
    - `TracingExecutor` - An executor that traces the stack frames of the class references executing.
    - `Util`
        - `allOf` - ANDs all predicates or a list of predicates provided. If there are no supplied predicates, the method will default to `true`.
        - `anyOf` - ORs all predicates or a list of predicates provided. If there are no supplied predicates, the method will default to `false`.
        - `makeEnumMap` - Creates an enum map given the enum class and a function to convert the enum to a value.
- `net.minecraft.advancements.critereon`
    - `InputPredicate` - A predicate that matches the input the player is making.
    - `SheepPredicate` - A predicate for when the entity is a sheep.
- `net.minecraft.client`
    - `Minecraft`
        - `saveReport` - Saves a crash report to the given file.
        - `triggerResourcePackRecovery` - A function that attempts to save the game when a compilation exception occurs, currently used by shaders when loading.
    - `Options#highContrastBlockOutline` - When enabled, provides a greater contrast when hovering over a block in range.
    - `ScrollWheelHandler` - A handler for storing information when a mouse wheel is scrolled.
- `ItemSlotMouseAction` - An interface that defines how the mouse interacts with a slot when hovering over.
- `net.minecraft.client.gui.components`
    - `AbstractSelectionList#setSelectedIndex` - Sets the selected entry based on its index.
    - `AbstractWidget#playButtonClickSound` - Plays the button click sound.
    - `DebugScreenOverlay#getProfilerPieChart` - Gets the pie chart profiler renderer.
- `net.minecraft.client.gui.components.debugchart.AbstractDebugChart#getFullHeight` - Returns the height of the rendered chart.
- `net.minecraft.client.gui.components.toasts`
    - `Toast`
        - `getWantedVisbility` - Returns the visbility of the toast to render.
        - `update` - Updates the data within the toast.
    - `TutorialToast` has a constructor that takes in an `int` to represent the time to display in milliseconds.
- `net.minecraft.client.gui.font.glyphs.BakedGlyph`
    - `renderChar` - Renders a character in the specified color.
    - `$GlyphInstance` - An instance of a glyph with the metadata of its screen location.
- `net.minecraft.client.gui.screens`
    - `BackupConfirmScreen` has a constructor that takes in another `Component` that represents the prompt for erasing the cache.
    - `Screen`
        - `getFont` - Returns the current font used for rendering the screen.
        - `showsActiveEffects` - When true, shows the mob effects currently applied to the player, assuming that such functionality is added to the screen in question.
- `net.minecraft.client.gui.screens.inventory`
    - `AbstractContainerScreen`
        - `BACKGROUND_TEXTURE_WIDTH`, `BACKGROUND_TEXTURE_HEIGHT` - Both set to 256.
        - `addItemSlotMouseAction` - Adds a mouse action when hovering over a slot.
        - `renderSlots` - Renders all active slots within the menu.
    - `AbstractRecipeBookScreen` - A screen that has a renderable and interactable `RecipeBookComponent` supplied from the constructor.
- `net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent#showTooltipWithItemInHand`- Returns whether the tooltip should be rendered when the item is in the player's hand.
- `net.minecraft.client.gui.screens.worldselection`
    - `CreateWorldCallback` - An interface that creates the world given the current screen, registries, level data, and path directory.
    - `CreateWorldScreen#testWorld` - Tries to open the world create screen with the provided generation settings context.
    - `InitialWorldCreationOptions` - Contains the options set when creating the world to generate.
    - `WorldCreationContextMapper` - An interface that creates the world context from the available resource reloaders and registries.
- `net.minecraft.client.multiplayer`
    - `ClientChunkCache`
        - `getLoadedEmptySections` - Returns the sections that have been loaded by the game, but has no data.
    - `ClientLevel`
        - `isTickingEntity` - Returns whether the entity is ticking in the level.
        - `setSectionRangeDirty`- Marks an area as dirty to update during persistence and network calls.
        - `onSectionBecomingNonEmpty` - Updates the section when it has data.
    - `PlayerInfo#setTabListOrder`, `getTabListOrder` - Handles the order of players to cycle through in the player tab.
- `net.minecraft.client.multiplayer.chat.report.ReportReason#getIncompatibleCategories` - Gets all reasons that cannot be reported for the given type.
- `net.minecraft.client.particle.TrailParticle` - A particle to trail from its current position to the target position.
- `net.minecraft.client.player.LocalPlayer#getDropSpamThrottler` - Returns a throttler that determines when the player can drop the next item.
- `net.minecract.client.renderer`
    - `CloudRenderer` - Handles the rendering and loading of the cloud texture data.
    - `DimensionSpecialEffects#isSunriseOrSunset` - Returns whether the dimension time represents sunrise or sunset in game.
    - `LevelEventHandler` - Handles the events sent by the `Level#levelEvent` method.
    - `LevelRenderer`
        - `getCapturedFrustrum` - Returns the frustrum box of the renderer.
        - `getCloudRenderer` - Returns the renderer for the clouds in the skybox.
        - `onSectionBecomingNonEmpty` - Updates the section when it has data.
    - `LevelTargetBundle` - Holds the resource handles and render targets for the rendering stages.
    - `LightTexture`
        - `getBrightness` - Returns the brightness of the given ambient and sky light.
        - `lightCoordsWithEmission` - Returns the packed light coordinates.
    - `RenderType`
        - `entitySolidZOffsetForward` - Gets a solid entity render type where the z is offset from the individual render objects.
        - `flatClouds` - Gets the render type for flat clouds.
        - `debugTriangleFan` - Gets the render type for debugging triangles.
        - `vignette` - Gets the vignette type.
        - `crosshair` - Gets the render type for the player crosshair.
        - `mojangLogo` - Gets the render type for the mojang logo
    - `Octree` - A traversal implementation for defining the order sections should render in the frustum.
    - `ShapeRenderer` - Utility for rendering basic shapes in the Minecraft level.
    - `SkyRenderer` - Renders the sky.
    - `WeatherEffectRenderer` - Renders weather effects.
    - `WorldBorderRenderer` - Renders the world border.
- `net.minecraft.client.renderer`
    - `SectionOcclusionGraph#getOctree` - Returns the octree to handle traversal of the render sections.
    - `ViewArea#getCameraSectionPos` - Gets the section position of the camera.
- `net.minecraft.client.renderer.culling.Frustum`
    - `getFrustumPoints` - Returns the frustum matrix as an array of `Vector4f`s.
    - `getCamX`, `getCamY`, `getCamZ` - Returns the frustum camera coordinates.
- `net.minecraft.client.renderer.chunk.CompileTaskDynamicQueue` - A syncrhonized queue dealing with the compile task of a chunk render section.
- `net.minecraft.client.renderer.debug`
    - `ChunkCullingDebugRenderer` - A debug renderer for when a chunk is culled.
    - `DebugRenderer`
        - `renderAfterTranslucents` - Renders the chunk culling renderer after translucents have been rendered.
        - `renderVoxelShape` - Renders the outline of a voxel shape.
        - `toggleRenderOctree` - Toggles whether `OctreeDebugRenderer` is rendered.
    - `OctreeDebugRenderer` - Renders the order of the section nodes.
- `net.minecraft.client.renderer.texture.AbstractTexture#defaultBlur`, `getDefaultBlur` - Returns whether the blur being applied is the default blur.
- `net.minecraft.client.resources.DefaultPlayerSkin#getDefaultSkin` - Returns the default `PlayerSkin`.
- `net.minecraft.commands.CommandBuildContext#enabledFeatures` - Returns the feature flags 
- `net.minecraft.commands.arguments.selector.SelectorPattern` - A record that defines an `EntitySelector` resolved from some pattern.
- `net.minecraft.core`
    - `BlockPos#betweenClosed` - Returns an iterable of all positions within the bounding box.
    - `Direction`
        - `getYRot` - Returns the Y rotation of a given direction.
        - `getNearest` - Returns the nearest direction given some XYZ coordinate, or the fallback direction if no direction is nearer.
        - `getUnitVec3` - Returns the normal unit vector.
        - `$Axis#getPositive`, `getNegative`, `getDirections` - Gets the directions along the axis.
    - `GlobalPos#isCloseEnough` - Returns whether the distance from this position to another block position in a dimension is within the given radius.
    - `HolderLookup$Provider`
        - `listRegistries` - Returns the registry lookups for every registry.
        - `allRegistriesLifecycle` - Returns the lifecycle of all registries combined.
    - `HolderSet#isBound` - Returns whether the set is bound to some value.
    - `Registry$PendingTags#size` - Gets the number of tags to load.
    - `Vec3i#distChessboard` - Gets the maximum absolute distance between the vector components.
- `net.minecraft.core.component`
    - `DataComponentHolder#getAllOfType` - Returns all data components that are of the specific class type.
    - `DataComponentPredicate`
        - `someOf` - Constructs a data component predicate where the provided map contains the provided component types.
        - `$Builder#expect` - Adds that we should expect the data component has some value.
    - `PatchedDataComponentMap#clearPatch` - Clears all patches to the data components on the object.
- `net.minecraft.core.particles.TargetColorParticleOption` - A particle option that specifies a target location and a color of the particle.
- `net.minecraft.data.DataProvider`
    - `saveAll` - Writes all values in a resource location to value map to the `PathProvider` using the provided codec.
    - `saveStable` - Writes a value to the provided path given the codec.
- `net.minecraft.data.loot#BlockLootSubProvider`
    - `createMossyCarpetBlockDrops` - Creates a loot table for a mossy carpet block.
    - `createShearsOrSlikTouchOnlyDrop` - Creates a loot table that can only drop its item when mined with shears or an item with the silk touch enchantment.
- `net.minecraft.data.worldgen.Pools#createKey` - Creates a `ResourceKey` for a template pool.
- `net.minecraft.data.models.EquipmentModelProvider` - A model provider for equipment models, only includes vanilla bootstrap.
- `net.minecraft.data.info.DatapackStructureReport` - A provider that returns the structure of the datapack.
- `net.minecraft.gametest.framework`
    - `GameTestHelper`
        - `absoluteAABB`, `relativeAABB` - Moves the bounding box between absolute coordinates and relative coordinates to the test location
        - `assertEntityData` - Asserts that the entity at the provided block position matches the predicate.
        - `hurt` - Hurts the entity the specified amount from a source.
        - `kill` - Kills the entity.
    - `GameTestInfo#getTestOrigin` - Gets the origin of the spawn structure for the test.
    - `StructureUtils#getStartCorner` - Gets the starting position of the test to run.
- `net.minecraft.network`
    - `FriendlyByteBuf`
        - `readVec3`, `writeVec3` - Static methods to read and write vectors.
        - `readContainerId`, `writeContainerId` - Methods to read and write menu identifiers.
        - `readChunkPos`, `writeChunkPos` - Methods to read and write the chunk position.
    - `StreamCodec#composite` - A composite method that takes in seven/eight parameters.
- `net.minecraft.network.codec.ByteBufCodecs`
    - `CONTAINER_ID` - A stream codec to handle menu identifiers.
    - `ROTATION_BYTE` - A packed rotation into a byte.
    - `LONG` - A stream codec for a long, or 64 bytes.
    - `OPTIONAL_VAR_INT` - A stream codec for an optional integer, serializing `0` when not present, or one above the stored value.
        - `-1` cannot be sent properly using this stream codec.
- `net.minecraft.network.protocol.game`
    - `ClientboundEntityPositionSyncPacket` - A packet that syncs the entity's position.
    - `ClientboundPlayerRotationPacket` - A packet that contains the player's rotation.
- `net.minecraft.server`
    - `MinecraftServer`
        - `tickConnection` - Ticks the connection for handling packets.
        - `reportPacketHandlingException` - Reports a thrown exception when attempting to handle a packet
        - `pauseWhileEmptySeconds` - Determines how many ticks the server should be paused for when no players are on.
    - `SuppressedExceptionCollector` - A handler for exceptions that were supressed by the server.
- `net.minecraft.server.commands.LookAt` - An interface that defines what should happen to an entity when the command is run, typically moving it to look at another.
- `net.minecraft.server.level`
    - `ChunkHolder#hasChangesToBroadcast` - Returns whether there is any updates within the chunk to send to the clients.
    - `ChunkTaskDispatcher` - A task scheduler for chunks.
    - `DistanceManager`
        - `getSpawnCandidateChunks` - Returns all chunks that the player can spawn within.
        - `getTickingChunks` - Returns all chunks that are currently ticking.
    - `ServerChunkCache#onChunkReadyToSend` - Adds a chunk holder to broadcast to a queue.
    - `ServerEntityGetter` - An entity getter interface implementation that operates upon the `ServerLevel`.
        - Replcaes the missing methods from `EntityGetter`
    - `ServerPlayer`
        - `getTabListOrder` - Handles the order of players to cycle through in the player tab.
        - `getLastClientInput`, `setLastClientInput`, `getLastClientMoveIntent` - Handles how the server player interprets the client impulse.
        - `commandSource` - Returns the player's source of commands.
        - `createCommandSourceStack` - Creates the source stack of the player issuing the command.
    - `ThrottlingChunkTaskDispatcher` - A chunk task dispatcher that sets a maximum number of chunks that can be executing at once.
    - `TickingTracker#getTickingChunks` - Returns all chunks that are currently ticking.
- `net.minecraft.server.packs.repository.PackRepository#isAbleToClearAnyPack` - Rebuilds the selected packs and returns whether it is different from the currently selected packs.
- `net.minecraft.resources.DependantName` - A reference object that maps some registry object `ResourceKey` to a value. Acts similarly to `Holder` except as a functional interface.
- `net.minecraft.tags.TagKey#streamCodec` - Constructs a stream codec for the tag key.
- `net.minecraft.util`
    - `ARGB#vector3fFromRGB24` - Creates a `Vector3f` containing the RGB components using the low 24 bits of an integer.
    - `BinaryAnimator` - A basic animator that animates between two states using an easing function.
    - `ExtraCodecs`
        - `NON_NEGATIVE_FLOAT` - A float codec that validates the value cannot be negative.
        - `RGB_COLOR_CODEC` - An integer, float, or three vector float codec representing the RGB color.
        - `nonEmptyMap` - A map codec that validates the map is not empty.
    - `Mth`
        - `wrapDegrees` - Sets the degrees to a value within (-180, 180].
        - `lerp` - Linear interpolation between two vectors using their components.
        - `length` - Gets the length of a 2D point in space.
        - `easeInOutSine` - A cosine function that starts at (0,0) and alternates between 1 and 0 every pi.
        - `packDegrees`, `unpackDegrees` - Stores and reads a degree in `float` form to a `byte`.
    - `RandomSource#triangle` - Returns a random `float` between the two `floats` (inclusive, exclusive) using a trangle distribution.
    - `StringRepresentable$EnumCodec#byName` - Gets the enum by its string name or the provided supplier value if null.
    - `TriState` - An enum that represents three possible states: true, false, or default.
- `net.minecraft.util.datafix.ExtraDataFixUtils`
    - `patchSubType` - Rewrites the second type to the third type within the first type.
    - `blockState` - Returns a dynamic instance of the block state
    - `fixStringField` - Modifies the string field within a dynamic.
- `net.minecraft.util.thread.BlockableEventLookup`
    - `BLOCK_TIME_NANOS` - Returns the amount of time in nanoseconds that an event will block the thread.
    - `isNonRecoverable` - Returns whether the exception can be recovered from.
- `net.minecraft.world.damagesource.DamageSources`
    - `enderPearl` - Returns a damage source from when an ender pearl is hit.
    - `mace` - Returns a damage source where a direct entity hits another with a mace.
- `net.minecraft.world.entity`
    - `Entity`
        - `applyEffectsFromBlocks` - Applies any effects from blocks via `Block#entityInside` or hardcoded checks like snow or rain.
        - `isAffectedByBlocks` - Returns whether the entity is affect by the blocks when inside.
        - `checkInsideBlocks` - Gets all blocks that teh player has traversed and checks whether the entity is inside one and adds them to a set when present.
        - `oldPosition`, `setOldPosAndrot`, `setOldPos`, `setOldRot` - Helpers for updating the last position and rotation of the entity.
        - `getXRot`, `getYRot` - Returns the linearly interpolated rotation of the entity given the partial tick.
        - `isAlliedTo(Entity)` - Returns whether the entity is allied to this entity.
        - `teleportSetPosition` - Sets the position and rotation data of the entity being teleported via a `DimensionTransition`
        - `getLootTable` - Returns the `ResourceKey` of the loot table the entity should use, if present.
        - `isControlledByOrIsLocalPlayer` - Return whether the entity is the local player or is controlled by a local player.
        - `shouldPlayLavaHurtSound` - When `true`, plays the lava hurt sound when the entity is hurt by lava.
        - `onRemoval` - A method that gets called when the entity is removed.
        - `cancelLerp` - Stops any lerped movement.
        - `forceSetRotation` - Sets the rotation of the entity.
        - `isControlledByClient` - Returns whether the entity is controlled by client inputs.
    - `EntityType`
        - `getDefaultLootTable` now returns an `Optional` in case the loot table is not present
        - `$Builder#noLootTable` - Sets the entity type to have no loot spawn on death.
        - `$Builder#build` now takes in the resouce key of the entity type
    - `EntitySelector#CAN_BE_PICKED` - Returns a selector that gets all pickable entities not in spectator.
    - `LivingEntity`
        - `dropFromShearingLootTable` - Resolves a loot table with a shearing context.
        - `getItemHeldByArm` - Returns the stack held by the specific arm.
        - `getEffectiveGravity` - Returns the gravity applied to the entity.
        - `canContinueToGlide` - Returns whether the entity can stil glide in the sky.
        - `getItemBlockingWith` - Returns the stack the player is currently blocking with.
        - `canPickUpLoot` - Returns whether the entity can pick up items.
        - `dropFromGiftLootTable` - Resolves a loot table with a gift context.
        - `handleExtraItemsCreatedOnUse` - Handles when a living entity gets a new item as a result of using another item.
        - `isLookingAtMe` - Checks whether the provided entity is looking at this entity.
    - `PositionMoveRotation` - A helper for handling the position and rotation of the entity in context.
    - `WalkAnimationState#stop` - Stops the walking animation of the entity.
- `net.minecraft.world.entity.ai.attributes`
    - `AttributeInstance`
        - `getPermanentModifiers` - Returns all permanent modifiers applied to the entity.
        - `addPermanentModifiers` - Adds a collection of permanent modifiers to apply.
    - `AttributeMap#assignPermanentModifiers` - Copies the permanent modifiers from another map.
- `net.minecraft.world.entity.ai.control.Control#rotateTowards` - Returns a float that rotates to some final rotation by the provided difference within a clamped value.
- `net.minecraft.world.entity.ai.goal.Goal#getServerLevel` - Gets the server level given the entity or a level.
- `net.minecraft.world.entity.ai.navigation.PathNavigation`
    - `updatePathfinderMaxVisitedNodes` - Updates the maximum number of nodes the entity can visit.
    - `setRequiredPathLength` - Sets the minimum length of the path the entity must take.
    - `getMaxPathLength` - Returns the maximum length of the path the entity can take.
- `net.minecraft.world.entity.ai.sensing`
    - `PlayerSensor#getFollowDistance` - Returns the following distance of this entity.
    - `Sensor#wasEntityAttackableLastNTicks` - Returns a predicate that checks whether the entity is attackable within the specified number of ticks.
- `net.minecraft.world.entity.ai.village.poi.PoiRecord#pack`, `PoiSection#pack` - Packs the necessary point of interest information. This only removes the dirty runnable.
- `net.minecraft.world.entity.animal`
    - `AgeableWaterCreature` - A water creature that has an age state.
    - `Animal`
        - `createAnimalAttributes` - Creates the attribute supplier for animals.
        - `playEatingSound` - Plays the sound an animal makes while eating.
    - `Bee#isNightOrRaining` - Returns whether the current level has sky light and is either at night or raining.
    - `Cat#isLyingOnTopOfSleepingPlayer` - Returns whether the cat is on top of a sleeping player.
    - `Salmon#getSalmonScale` - Returns the scale factor to apply to the entity's bounding box.
    - `Wolf#DEFAULT_TAIL_ANGLE` - Returns the default tail angle of the wolf.
- `net.minecraft.world.entity.boss.enderdragon.DragonFlightHistory` - Holds the y and rotation of the dragon when flying through the sky. Used for animating better motion of the dragon's parts.
- `net.minecraft.world.entity.monster.Zombie#canSpawnInLiquids` - When true, the zombie can spawn in a liquid.
- `net.minecraft.world.entity.player`
    - `Inventory`
        - `isUsableForCrafting` - Returns whether the state can be used in a crafting recipe.
        - `createInventoryUpdatePacket` - Creates the packet to update an item in the inventory.
    - `Player`
        - `handleCreativeModeItemDrop` - Handles what to do when a player drops an item from creative mode.
        - `shouldRotateWithMinecart` - Returns whether the player should also rotate with the minecart.
        - `canDropItems` - When `true`, the player can drop items from the menu.
        - `getPermissionLevel`, `hasPermissions` - Returns the permissions of the player.
    - `StackedContents` - Holds a list of contents along with their associated size.
        - `$Output` - An interface that defines how the contents are accepted when picked.
- `net.minecraft.world.entity.projectile.Projectile`
    - `spawnProjectileFromRotation` - Spawns a projectile and shoots from the given rotation.
    - `spawnProjectileUsingShoot` - Spawns a projectile and sets the initial impulse via `#shoot`.
    - `spawnProjectile` - Spawns a projectile.
    - `applyOnProjectileSpawned` - Applies any additional configurations from the given level and `ItemStack`.
    - `onItemBreak` - Handles what happens when the item that shot the projectile breaks.
    - `shouldBounceOnWorldBorder` - Returns whether the projectile should bounce off the world border.
    - `setOwnerThroughUUID` - Set the owner of the projectile by querying it through its UUID.
    - `$ProjectileFactory` - Defines how a projectile is spawned from some `ItemStack` by an entity.
- `net.minecraft.world.entity.vehicle`
    - `AbstractBoat` - An entity that represents a boat.
    - `AbstractChestBoat` - An entity that represent a boat with some sort of inventory.
    - `ChestRaft` - An entity that represents a raft with some sort of inventory.
    - `Raft` - An entity that represents a raft.
- `net.minecraft.world.inventory.AbstractContainerMenu`
    - `addInventoryHotbarSlots` - Adds the hotbar slots for the given container at the x and y positions.
    - `addInventoryExtendedSlots` - Adds the player inventory slots for the given container at the x and y positions.
    - `addStandardInventorySlots` - Adds the hotbar and player inventory slots at their normal location for the given container at the x and y positions.
    - `setSelectedBundleItemIndex` - Toggles the selected bundle in a slot.
- `net.minecraft.world.item`
    - `BundleItem`
        - `getOpenBundleModelFrontLocation`, `getOpenBundleModelBackLocation` - Returns the model locations of the bundle.
        - `toggleSelectedItem`, `hasSelectedItem`, `getSelectedItem`, `getSelectedItemStack` - Handles item selection within a bundle.
        - `getNumberOfItemsToShow` - Determines the number of items in the bundle to show at once.
        - `getByColor` - Handles the available links from bundle to dyed bundles.
        - `getAllBundleItemColors` - Returns a stream of all dyed bundles.
    - `ItemStack`
        - `clearComponents` - Clears the patches made to the stack, not the item components.
        - `isBroken` - Returns wheter the stack has been broken.
        - `hurtWithoutBreaking` - Damages the stack without breaking the stack.
        - `getStyledHoverName` - Gets the stylized name component of the stack.
- `net.minecraft.world.item.component.BundleContents`
    - `canItemBeInBundle` - Whether the item can be put into the bundle.
    - `getNumberOfItemsToShow` - Determines the number of items in the bundle to show at once.
    - `hasSelectedItem`, `getSelectedItem` - Handles item selection within a bundle.
- `net.minecraft.world.item.enchantment.EnchantmentHelper`
    - `createBook` - Creates an enchanted book stack.
    - `doPostAttackEffectsWithItemSourceOnBreak` - Applies the enchantments after attack when the item breaks.
- `net.minecraft.world.level`
    - `BlockCollisions` has a constructor to take in a `CollisionContext`
    - `BlockGetter#boxTraverseBlocks` - Returns an iterable of the positions traversed along the vector in a given bounding box.
    - `CollisionGetter`
        - `noCollision` - Returns whether there is no collision between the entity and blocks, entities, and liquids if the `boolean` provided is `true`.
        - `getBlockAndLiquidCollisions` - Returns the block and liquid collisions of the entity within the bounding box.
        - `clipIncludingBorder` - Gets the block hit result for the specified clip context, clamped by the world border if necessary.
    - `EmptyBlockAndTintGetter` - A dummy `BlockAndTintGetter` instance.
    - `GameType#isValidId` - Checks whether the id matches an existing game type.
    - `LevelHeightAccessor#isInsideBuildHeight` - Returns whether the specified Y coordinate is within the bounds of the level.
- `net.minecraft.world.level.block`
    - `Block#UPDATE_SKIP_SHAPE_UPDATE_ON_WIRE` - A block flag that, when enabled, does not update the shape of a redstone wire.
    - `BonemealableFeaturePlacerBlock` - A block that places a configured feature and can be bonemealed.
- `net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerData#resetStatistics` - Resets the data of the spawn to an empty setting, but does not clear the current mobs or the next spawning entity.
- `net.minecraft.world.level.block.piston.PistonMovingBlockEntity#getPushDirection` - Returns the push direction of the moving piston.
- `net.minecraft.world.level.block.state`
    - `BlockBehaviour`
        - `getEntityInsideCollisionShape`, `$BlockStateBase#getEntityInsideCollisionShape` - Determines the voxel shape of the block when the entity is within it.
        - `$Properties#overrideDescription` - Sets the translation key of the block name.
    - `StateHolder`
        - `getValueOrElse` - Returns the value of the property, else the provided default.
        - `getNullableValue` - Returns the value of the property, or null if it does not exist.
- `net.minecraft.world.level.block.state.properties.Property#getInternalIndex` - Converts the provided boolean to a 0 when true, or 1 otherwise.
- `net.minecraft.world.level.border.WorldBorder#clampVec3ToBound` - Clamps the vector to within the world border.
- `net.minecraft.world.level.chunk`
    - `ChunkAccess#canBeSerialized` - Returns true, allows the chunk to be written to disk.
    - `ChunkSource#onSectionEmptinessChanged` - Updates the section when it has data.
    - `LevelChunkSection`
        - `copy` - Makes a shallow copy of the chunk section.
        - `setUnsavedListener` - Adds a listener which takes in the chunk position whenever the chunk is marked dirty.
        - `$UnsavedListener` - A consumer of a chunk position called when the chunk is marked dirty.
    - `PalettedContainerRO#copy` - Creates a shallow copy of the `PalettedContainer`.
    - `UpgradeData#copy` - Creates a deep copy of `UpgradeData`.
- `net.minecraft.world.level.chunk.storage.IOWorker#store` - Stores the writes of the chunk to the worker.
- `net.minecraft.world.level.levelgen`
    - `SurfaceRules$Context#getSeaLevel`, `SurfaceSystem#getSeaLevel` - Gets the sea level of the generator settings.
    - `WorldOptions#testWorldWithRandomSeed` - Creates a test world with a randomly generated seed.
- `net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator$Context#checkBlock` - Checks if the block at the given position matches the predicate.
- `net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate`
    - `getJigsaws` - Returns the jigsaw blocks that are at the provided position with the given rotation.
    - `getJointType` - Returns the joint type of the jigsaw block.
    - `$JigsawBlockInfo` - A record which contains the block info for a jigsaw block.
        - Most methods that involve jigsaws have replaced the `$StructureBlockInfo` with a `$JigsawBlockInfo`.
- `net.minecraft.world.level.lighting.LayerLightSectionStorage#lightOnInColumn` - Returns whether there is light in the zero node section position.
- `net.minecraft.world.level.pathfinder.PathFinder#setMaxVisitedNodes` - Sets the maximum number of nodes that can be visited.
- `net.minecraft.world.level.portal`
    - `DimensionTransition#withRotation` - Updates the entity's spawn rotation.
    - `PortalShape#findAnyShape` - Finds a `PortalShape` that can be located at the given block position facing the specific direction.
- `net.minecraft.world.phys`
    - `AABB`
        - `clip` - Clips the vector inside the given bounding box, or returns an empty optional if there is no intersection.
        - `collidedAlongVector` - Returns whether this box collided with one of the bounding boxes provided in the list along the provided movement vector.
        - `getBottomCenter` - Gets the bottom center of the bounding box as a vector.
    - `Vec3`
        - `add`, `subtract` - Translates the vector and returns a new object.
        - `horizontal` - Returns the horizontal components of the vector.
        - `projectedOn` - Gets the unit vector representing this vector projected onto another vector.
- `net.minecraft.world.phys.shapes`
    - `CollisionContext`
        - `of(Entity, boolean)` - Creates a new entity collision context, where the `boolean` determines whether the entity can always stand on the provided fluid state.
        - `getCollisionShape` - Returns the collision shape collided with.
    - `VoxelShape#move(Vec3)` - Offsets the voxel shape by the provided vector.
- `net.minecraft.world.ticks.ScheduledTick#toSavedTick` - Converts a scheduled tick to a saved tick.

### List of Changes

- `F3 + F` now toggles fog rendering
- `com.mojang.blaze3d.platform`
    - `NativeImage`
        - `getPixelRGBA`, `setPixelRGBA` are now private. These are replaced by `getPixel` and `setPixel`, respectively
        - `getPixelsRGBA` -> `getPixels`
    - `Window#updateDisplay` now takes in a `TraceyFrrameCapture`, or `null`
- `net.minecraft.Util`
    - `backgroundExecutor`, `ioPool`, and `nonCriticalIoPool` now return a `TracingExecutor` instead of an `ExecutorService`
    - `wrapThreadWithTaskName` -> `runNamed` with its parameters flipped and no return value
- `net.minecraft.advancements.critereon`
    - `KilledByCrossbowTrigger` -> `KilledByArrowTrigger`, not one-to-one, takes in the stack in question
    - `PlayerPredicate` can now match the player's input
- `net.minecraft.client`
    - `Minecraft`
        - `debugFpsMeterKeyPress` -> `ProfilerPieChart#profilerPieChartKeyPress` obtained via `Minecraft#getDebugOverlay` and then `DebugScreenOverlay#getProfilerPieChart`
        - `getTimer` -> `getDeltaTracker`
        - `getToasts` -> `getToastManager`
    - `Options#setModelPart` is now public, replaces `toggleModelPart` but without broadcasting the change
    - `ParticleStatus` -> `net.minecraft.server.level.ParticleStatus`
- `net.minecraft.client.animation.KeyframeAnimations#animate` now takes in a `Model` instead of a `HierarchicalModel`
- `net.minecraft.client.gui.Font`
    - `drawInBatch(String, float, float, int, boolean, Matrix4f, MultiBufferSource, Font.DisplayMode, int, int, boolean)` is removed and should use the `Component` replacement
        - There is also a delegate that sets the inverse depth boolean to true by default for the `Component` `drawInBatch` method
    - `$StringRenderOutput` now takes in the `Font`, an optional background color, and a boolean representing if inverse depth should be use when drawing the text
    - `$StringRenderOutput#finish` is now package private
- `net.minecraft.client.gui.components`
    - `AbstractSelectionList`
        - `replaceEntries` is now public
        - `getRowTop`, `getRowBottom` is now public
    - `PlayerFaceRenderer#draw(GuiGraphics, ResourceLocation, int, int, int, int)` takes in a `PlayerSkin` instead of a `ResourceLocation`
- `net.minecraft.client.gui.components.toasts`
    - `Toast`
        - `Toast$Visibility render(GuiGraphics, ToastComponent, long)` -> `void render(GuiGraphics, Font, long)`
        - `slotCount` - `occupiedSlotCount`
    - `ToastComponent` -> `ToastManager`
- `net.minecraft.client.gui.font.glyphs.BakedGlyph`
    - `render` now takes in a single integer representing the color instead of four floats and is private
        - `renderChar` is the public replacement, taking in the `$GlyphInstance`, the `Matrix4f`, `VertexConsumer`, and color integer
    - `$Effect` is a record, now taking in a single integer representing the color instead of four floats
- `net.minecraft.client.gui.screens`
    - `LoadingOverlay#MOJANG_STUDIOS_LOGO_LOCATION` is now public
    - `Screen`
        - `renderBlurredBackground(float)` -> `renderBlurredBackground()`
        - `wrapScreenError` -> `fillCrashDetails`, not one to one as it only adds the relevant crash information and not actually throw the error
- `net.minecraft.client.gui.screens.inventory`
    - `AbstractContainerScreen#renderSlotHighlight` -> `renderSlotHighlightBack`, `renderSlotHighlightFront`, now private
    - `BookEditScreen` now takes in the `WritableBookContent`
    - `AbstractSignEditScreen`
        - `sign` is now protected
        - `renderSignBackground` no longer takes in the `BlockState`
    - `EffectRenderingInventoryScreen` -> `Screen#hasActiveEffects`, `EffectsInInventory`. Not one-to-one as `EffectsInInventory` now acts as a helper class to a screen to render its effects at the specified location.
- `net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent`
    - `getHeight()` -> `getHeight(Font)`
    - `renderImage` now takes in the `int` width and height of the rendering tooltip
- `net.minecraft.client.gui.screens.recipebook`
    - `GhostSlots#render` no longer takes in an x and y offset.
    - `RecipeBookComponent` no longer takes in an x and y offset.
- `net.minecraft.client.gui.screens.reporting.ReportReasonSelectionScreen` now takes in a `ReportType`
- `net.minecraft.client.gui.screens.worldselection`
    - `CreateWorldScreen`
        - `$DataPackReloadCookie` -> `DataPackReloadCookie`
        - `openFresh` now has an overload that takes in the `CreateWorldCallback`
    - `WorldCreationContext` now takes in the `InitialWorldCreationOptions`
    - `WorldOpenFlows#createFreshLevel` takes in a `Function<HolderLookup.Provider, WorldDimensions>` instead of `Function<RegistryAccess, WorldDimensions>`
- `net.minecraft.client.gui.spectator.SpectatorMenuItem#renderIcon` now takes in a `float` instead of an `int` to represent the alpha value
- `net.minecraft.client.multiplayer`
    - `ClientLevel` now takes in an `int` representing the sea level
        - `getSkyColor` now returns a single `int` instead of a `Vec3`
        - `getCloudColor` now returns a single `int` instead of a `Vec3`
        - `setGameTime`, `setDayTime` -> `setTimeFromServer`
    - `TagCollector` -> `RegistryDataCollector$TagCollector`, now package-private
- `net.minecraft.client.player`
    - `AbstractClientPlayer#getFieldOfViewModifier` now takes in a boolean representing whether the camera is in first person and a float representing the partial tick
    - `Input` -> `ClientInput` and `net.minecraft.world.entity.player.Input`
    - `KeyboardInput` now extends `ClientInput`
    - `LocalPlayer#input` is now `ClientInput`
- `net.minecraft.client.renderer`
    - `DimensionSpecialEffects#getSunriseColor` -> `getSunriseOrSunsetColor`
    - `GameRenderer`
        - `processBlurEffect` no longer takes in the partial tick `float`
        - `getFov` returns a `float` instead of a `double`
        - `getProjectionMatrix` now takes in a `float` instead of a `double`
    - `ItemModelShaper`
        - `shapes` is now private
        - `getItemModel(Item)` is removed
        - `getItemModel(ResourceLocation)` - Gets the baked model associated with the provided `ResourceLocation`.
        - `register` is removed
        - `getModelManager` is removed
        - `invalidateCache` - Clears the model map.
    - `LevelRenderer`
        - `renderSnowAndRain` -> `WeatherEffectRenderer`
        - `tickRain` -> `tickParticles`
        - `renderLevel` now takes in a `GraphicsResourceAllocator`
        - `renderClouds` -> `CloudRenderer`
        - `addParticle` is now public
        - `globalLevelEvent` -> `LevelEventHandler`
        - `entityTarget` -> `entityOutlineTarget`
        - `$TransparencyShaderException` no longer takes in the throwable cause
    - `SectionOcclusionGraph`
        - `onSectionCompiled` -> `schedulePropagationFrom`
        - `update` now takes in a `LongOpenHashSet` that holds the currently loaded section nodes
        - `$GraphState` is now package-private
        - `addSectionsInFrustum` now takes in a list to add the render sections to
    - `ShapeRenderer#renderShape` now takes in a single integer for the color instead of four floats
    - `ViewArea`
        - `repositionCamera` now takes in the `SectionPos` instead of two `double`s
        - `getRenderSectionAt` -> `getRenderSection`
- `net.minecraft.client.renderer.blockentity`
    - `BannerRenderer#renderPatterns` now takes in a `boolean` determining the glint render type to use
    - `*Renderer` classes that constructed `LayerDefinition`s have now been moved to their associated `*Model` class
    - `SignRenderer$SignModel` -> `SignModel`
- `net.minecraft.client.renderer.chunk.SectionRenderDispatcher` now takes in a `TracingExecutor` rather than just a `Executor`
    - `$CompiledSection#hasNoRenderableLayers` -> `hasRenderableLayers`
    - `$RenderSection` now takes in a compiled `long` of the section node
        - `setOrigin` -> `setSectionNode`
        - `getRelativeOrigin` -> `getNeighborSectionNode`
        - `cancelTasks` now returns nothing
        - `pointOfView` - A reference to the location of where the translucent render type is rendered from.
        - `resortTransparency` no longer takes in the `RenderType` and returns nothing
        - `hasTranslucentGeometry` - Returns whether the compiled blocks have a translucent render type.
        - `transparencyResortingScheduled` - Returns whether the last task was scheduled but not completed.
        - `isAxisAlignedWith` -> `$TranslucencyPointOfView#isAxisAligned`
    - `$CompileTask` is now public
        - No longer `Comparable`
        - The constructor no longer takes in the distance at creation
        - `isHighPriority` -> `isRecompile`
    - `$TranslucencyPointOfView` - Returns the coordinate representing the view point of the tranlucent render type in this section.
- `net.minecraft.client.renderer.culling.Frustum#cubeInFrustum` now returns an `int` representing the index of the first plane that culled the box
- `net.minecraft.client.renderer.DebugRenderer#render` now takes in the `Frustum`
- `net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations#loadPaletteEntryFromImage` is now private
- `net.minecraft.client.tutorial`
    - `Tutorial`
        - `addTimedToast`, `#removeTimedToast`, `$TimedToast` -> `TutorialToast` parameter
        - `onInput` takes in a `ClientInput` instead of an `Input`
    - `TutorialStepInstance`
        - `onInput` takes in a `ClientInput` instead of an `Input`
- `net.minecraft.core`
    - `Direction`
        - `getNearest` -> `getApproximateNearest`
        - `getNormal` -> `getUnitVec3i`
    - `HolderGetter$Provider#get` no longer takes in the registry key, instead reading it from the `ResourceKey`
    - `HolderLookup$Provider` now implements `HolderGetter$Provider`
        - `asGetterLookup` is removed as the interface is a `HolderGetter$Provider`
        - `listRegistries` -> `listRegistryKeys`
    - `Registry` now implements `HolderLookup$RegistryLookup`
        - `getTags` only returns a stream of named holder sets
        - `asTagAddingLookup` -> `prepareTagReload`
        - `bindTags` -> `WritabelRegistry#bindTag`
        - `get` -> `getValue`
        - `getOrThrow` -> `getValueOrThrow`
        - `getHolder` -> `get`
        - `getHolderOrThrow` -> `getOrThrow`
        - `holders` -> `listElements`
        - `getTag` -> `get`
        - `holderOwner`, `asLookup` is removed as `Registry` is an instance of them
    - `RegistryAccess`
        - `registry` -> `lookup`
        - `registryOrThrow` -> `lookupOrThrow`
    - `RegistrySynchronization#NETWORKABLE_REGISTRIES` -> `isNetworkable`
- `net.minecraft.core.cauldron.CauldronInteraction`
    - `FILL_WATER` -> `fillWaterInteraction`, now private
    - `FILL_LAVA` -> `fillLavaInteraction`, now private
    - `FILL_POWDER_SNOW` -> `fillPowderSnowInteraction`, now private
    - `SHULKER_BOX` -> `shulkerBoxInteraction`, now private
    - `BANNER` -> `bannerInteraction`, now private
    - `DYED_ITEM` -> `dyedItemIteration`, now private
- `net.minecraft.core.dispenser.BoatDispenseItemBehavior` now takes in the `EntityType` to spawn rather that the variant and chest boat boolean
- `net.minecraft.core.particles.DustColorTransitionOptions`, `DustParticleOptions` now takes in integers representing an RGB value instead of `Vector3f`s.
- `net.minecraft.data.loot`
    - `BlockLootSubProvider`
        - `HAS_SHEARS` -> `hasShears`
        - `createShearsOnlyDrop` is now an instance method
    - `EntityLootSubProvider`
        - `killedByFrog`, `killedByFrogVariant` now take in the getter for the `EntityType` registry
        - `createSheepTable` -> `createSheepDispatchPool`, not one-to-one as the table was replaced with a pool builder given a map of dye colors to loot tables
- `net.minecraft.gametest.framework`
    - `GameTestHelper#assertEntityPresent`, `assertEntityNotPresent` takes in a bounding box instead of two vectors
    - `GameTestInfo#getOrCalculateNorthwestCorner` is now public
- `net.minecraft.network.chat.Component#score` now takes in a `SelectorPattern`
- `net.minecraft.network.chat.contents.ScoreContents`, `SelectorContents` is now a record
- `net.minecraft.network.protocol.login.ClientboundGameProfilePacket` -> `ClientboundLoginFinishedPacket`
- `net.minecraft.network.protocol.game`
    - `ClientboundMoveEntityPacket#getyRot`, `getxRot` now returns a `float` of the degrees
    - `ClientboundPlayerPositionPacket` is now a record, taking in a `PositionMoverotation` representing the change
        - `relativeArguments` -> `relatives`
        - `yRot`, `xRot` -> `ClientboundPalyerRotationPacket`
    - `ClientboundSetTimePacket` is now a record
    - `ClientboundRotateHeadPacket#getYHeadRot` now returns a `float` of the degrees
    - `ClientboundTeleportEntityPacket` is now a record, where the necessary parameters are passed into the packet instead of the entity
    - `ServerboundPlayerInputPacket` is now a record, taking in an `Input`
- `net.minecraft.resources.RegistryDataLoader$Loader#loadFromNetwork` now takes in a `$NetworkedRegistryData`, which contains the packed registry entries
- `net.minecraft.server`
    - `MinecraftServer` no longer implements `AutoCloseable`
        - `tickChildren` is now protected
        - `wrapRunnable` is now public
    - `ReloadableServerRegistries#reload` now takes in a list of pending tags and returns a `$LoadResult` instead of a layered registry access
    - `ReloadableServerResources`
        - `loadResources` now takes in a list of pending tags and the server `Executor`
        - `updateRegistryTags` -> `updateStaticRegistryTags`
    - `ServerFunctionLibrary#getTag`, `ServerFunctionManager#getTag` returns a list of command functions
- `net.minecraft.server.level`
    - `ChunkHolder`
        - `blockChanged`, `sectionLightChanged` now returns `boolean` if the information has changed
        - `addSaveDependency` is now protected, a method within `GenerationChunkHolder`
    - `ChunkTaskPriorityQueue` no longer takes in a generic
        - The constructor no longer takes in the maximum number of tasks to do
        - `submit` now takes in a `Runnable` rather than an `Optional`
        - `pop` returns a `$TasksForChunk` instead of a raw `Stream`
    - `ChunkTaskPriorityQueueSorter` -> `ChunkTaskDispatcher`
    - `ServerPlayer`
        - `teleportTo` takes in a `boolean` that determines whether the camera should be set
        - `INTERACTION_DISTANCE_VERIFICATION_BUFFER` -> `BLOCK_INTERACTION_DISTANCE_VERIFICATION_BUFFER`
            - Also splits into `ENTITY_INTERACTION_DISTANCE_VERIFICATION_BUFFER` set to 3.0
        - `findRespawnPositionAndUseSpawnBlock` now deals with `TeleportTransition`
    - `TextFilterClient` -> `ServerTextFilter`
    - `ThreadedLevelLightEngine` now takes in a `ConsecutiveExecutor` and `ChunkTaskDispatcher` instead of a `ProcessorMailbox` and a `ProcessorHandle`, respectively
- `net.minecraft.server.packs.resources.ProfiledReloadInstance$State` is now a record
- `net.minecraft.sounds.SoundEvent` is now a record
- `net.minecraft.tags`
    - `TagEntry$Lookup#element` now takes in a `boolean` representing if the element is required
    - `TagLoader` now takes in an `$ElementLookup`, which functions the same as its previous function parameter
        - `build` now returns a value of lists
        - `loadAndBuild` -> `loadTagsFromNetwork`, `loadTagsForExistingRegistries`, `loadTagsForRegistry`, `buildUpdatedLookups`
    - `TagNetworkSerialization$NetworkPayload`
        - `size` -> `isEmpty`
        - `applyToRegistry` -> `resolve`
- `net.minecraft.util`
    - `FastColor` -> `ARGB`
        - `scaleRGB` overload with an alpha integer and three floats.
    - `Mth#color` -> `ARGB#color`
- `net.minecraft.util.profiling.metrics.MetricCategory#MAIL_BOXES` -> `CONSECUTIVE_EXECUTORS`
- `net.minecraft.util.thread`
    - `BlockableEventLoop#waitForTasks` is now protected
    - `ProcessorMailbox` no longer implements `AutoCloseable`
- `net.minecraft.util.worldupdate.WorldUpgrader` implements `AutoCloseable`
- `net.minecraft.world.LockCode` now takes in an `ItemPredicate` instead of a `String` representing the item name
    - `addToTag`, `fromTag` now takes in a `HolderLookup$Provider`
- `net.minecraft.world.effect`
    - `MobEffect#applyEffectTick`, `applyInstantenousEffect`, `onMobRemoved`, `onMobHurt` now takes in the `ServerLevel`
    - `MobEffectInstance#onMobRemoved`, `onMobHurt` now takes in the `ServerLevel`
- `net.minecraft.world.entity`
    - `AgeableMob$AgeableMobGroupData` now has a public constructor
    - `AnimationState#getAccumulatedTime` -> `getTimeInMillis`
    - `Entity` no longer implements `CommandSource`
        - `setOnGroundWithMovement` now takes in an additional `boolean` representing whether there is any horizontal collision.
        - `getInputVector` is now protected
        - `isAlliedTo(Entity)` -> `considersEntityAsAlly`
        - `teleportTo` now takes in an additional `boolean` that determines whether the camera should be set
        - `checkInsideBlocks()` -> `recordMovementThroughBlocks`, not one-to-one as it takes in the movement vectors
        - `checkInsideBlocks(Set<BlockState>)` -> `collectBlockCollidedWith`, now private
        - `kill` now takes in the `ServerLevel`
        - `hurt` has been marked as deprecated, to be replaced by `hurtServer` and `hurtClient`
            - `hurtOrSimulate` acts as a helper to determine which to call, also marked as deprecated
        - `spawnAtLocation` now takes in a `ServerLevel`
        - `isInvulnerableTo` -> `isInvulnerableToBase`, now protected and final
            - `isInvulnerableTo` is moved to `LivingEntity#isInvulnerableTo`
        - `teleportSetPosition` now public and takes in a `PositionMoveRotation` and `Relative` set instead of the `DimensionTransition`
        - `createCommandSourceStack` -> `createCommandSourceStackForNameResolution`, not one to one as it takes in the `ServerLevel`
        - `mayInteract` now takes in the `ServerLevel` instead of just the `Level`
        - `setOldRot` is now public
        - `changeDimension` -> `teleport`, returns `ServerPlayer` given `TeleportTransition`
        - `canChangeDimensions` -> `canTeleport`
    - `EntitySpawnReason#SPAWN_EGG` -> `SPAWN_ITEM_USE`, not one-to-one as this indicates the entity can be spawned from any item
    - `EntityType`
        - `create`, `loadEntityRecursive`, `loadEntitiesRecursive`, `loadStaticEntity` now takes in an `EntitySpawnReason`
        - `*StackConfig` now takes in a `Level` instead of a `ServerLevel`
    - `EquipmentTable` now has a constructor that takes in a single float representing the slot drop chance for all equipment slots
    - `MobSpawnType` -> `EntitySpawnReason`
    - `Leashable#tickLeash` now takes in the `ServerLevel`
    - `LivingEntity`
        - `getScale` is now final
        - `onAttributeUpdated` is now protected
        - `activeLocationDependentEnchantments` now takes in an `EquipmentSlot`
        - `handleRelativeFrictionAndCalculateMovement` is now private
        - `updateFallFlying` is now protected
        - `onEffectRemoved` -> `onEffectsRemoved`
        - `spawnItemParticles` is now public
        - `getLootTable` -> `Entity#getLootTable`, wrapped in optional
        - `getBaseExperienceReward` now takes in the `ServerLevel`
        - `triggerOnDeathMobEffects` now takes in the `ServerLevel`
        - `canAttack` is removed
        - `dropEquipment` now takes in the `ServerLevel`
        - `dropExperience` now takes in the `ServerLevel`
        - `dropFromLootTable` now takes in the `ServerLevel`
        - `actuallyHurt`, `doHurtTarget` now takes in the `ServerLevel`
        - `hasLineOfSight` overload with clip contexts and a eye y supplier
        - `makePoofParticles` is now public
    - `Mob`
        - `pickUpItem`, `wantsToPickUp` now takes in the `ServerLevel`
        - `equipItemIfPossible` now takes in the `ServerLevel`
        - `customServerAiStep` now takes in the `ServerLevel`
        - `dropPreservedEquipment` now takes in the `ServerLevel`
    - `NeutralMob`
        - `isAngryAt`, `isAngryAtAllPlayers` now takes in the `ServerLevel`
        - `playerDied` now takes in the `ServerLevel`
    - `PortalProcessor#getPortalDestination` now returns a `TeleportTransition`
    - `PositionMoveRotation`
        - `of(ClientboundPlayerPositionPacket)` -> `ofEntityUsingLerpTarget(Entity)`
        - `of(DimensionTransition)` -> `of(TeleportTransition)`
    - `Shearable#shear` now takes in the `ServerLevel` and `ItemStack` that is shearing the entity
    - `RelativeMovement` -> `Relative`, expanded to contain delta movement
    - `WalkAnimationState#update` now takes in an additional `float` representing the position scale when moving.
- `net.minecraft.world.entity.ai.behavior`
    - `StartAttacking` now takes in a `$TargetFinder` and additionally a `$StartAttackingCondition`
        - Both are functional interfaces that replace the previous functions/predicates, though with an extra `ServerLevel` parameter
    - `StopAttackingIfTargetInvalid` now takes in a `$TargetErasedCallback` and/or a `$StopAttackCondition`
        - Both are functional interfaces that replace the previous consumers/predicates, though with an extra `ServerLevel` parameter
    - `MeleeAttack#create` can now take in a predicate to test the mob for
    - `Swim` now takes in a generic representing the mob
- `net.minecraft.world.entity.ai.control.LookControl#rotateTowards` -> `Control#rotateTowards`
- `net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal` now takes in a `$Selector`
     - It is a functional interface that replaces the previous predicate, though with an extra `ServerLevel` parameter
- `net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities` now takes in a `ServerLevel`
- `net.minecraft.world.entity.ai.sensing`
    - `NearestLivingEntitySensor`
        - `radiusXZ`, `radiusY` -> `Attributes#FOLLOW_RANGE`
        - `isMatchingEntity` now takes in a `ServerLevel`
    - `Sensor`
        - `TARGETING_RANGE` is now private
        - `isEntityTargetable`, `isEntityAttackable`, `isEntityAttackableIgnoringLineOfSight` now take in a `ServerLevel`
        - `wasEntityAttackableLastNTicks`, `rememberPositives` now delas with `BiPredicate`s instead of `Predicate`s
- `net.minecraft.world.entity.ai.targeting.TargetingConditions`
    - `selector` now takes in a `$Selector`
        - It is a functional interface that replaces the previous predicate, though with an extra `ServerLevel` parameter
    - `test` now takes in a `ServerLevel`
- `net.minecraft.world.entity.ai.village.poi.PoiRecord#codec`, `PoiSection#codec` -> `$Packed#CODEC`
- `net.minecraft.world.entity.animal`
    - `Fox$Type` -> `$Variant`
    - `MushroomCow$MushroomType` -> `$Variant`
        - `$Variant` no longer takes in the loot table
    - `Salmon` now has a variant for its size
    - `Wolf`
        - `getBodyRollAngle` -> `#getShakeAnim`, not one-to-one as the angle is calculated within the render state
        - `hasArmor` is removed
- `net.minecraft.world.entity.animal.horse.AbstractHorse#followMommy` now takes in a `ServerLevel`
- `net.minecraft.world.entity.boss.enderdragon.EnderDragon#onCrystalDestroyed` now takes in a `ServerLevel`
- `net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance#doServerTick` now takes in a `ServerLevel`
- `net.minecraft.world.entity.boss.wither.WitherBoss#getHead*Rot` -> `getHead*Rots`, returns all rotations rather than just the provided index
- `net.minecraft.world.entity.decoration`
    - `ArmorStand` default rotations are now public
        - `isShowArms` -> `showArms`
        - `isNoBasePlate` -> `showBasePlate`
    - `PaintingVariant` now takes in a title and author `Component`
- `net.minecraft.world.entity.item.ItemEntity#getSpin` is now static
- `net.minecraft.world.entity.monster.Monster#isPreventingPlayerRest` now takes in a `ServerLevel`
- `net.minecraft.world.entity.monster.breeze.Breeze#getSnoutYPosition` -> `getFiringYPosition`
- `net.minecraft.world.entity.monster.hoglin.HoglinBase#hurtAndThrowTarget` now takes in a `ServerLevel`
- `net.minecraft.world.entity.monster.piglin.PiglinAi#isWearingGold` -> `#isWearingSafeArmor`
- `net.minecraft.world.entity.npc.InventoryCarrier#pickUpItem` now takes in a `ServerLevel`
- `net.minecraft.world.entity.player`
    - `Player#disableShield` now takes in the stack to apply the cooldown to
    - `Inventory`
        - `findSlotMatchingUnusedItem` -> `findSlotMatchingCraftingIngredient`
        - `swapPaint` -> `setSelectedHotbarSlot`
        - `StackedContents` -> `StackedItemContents`
- `net.minecraft.world.entity.projectile`
    - `AbstractArrow#inGround` -> `IN_GROUND`, now an `EntityDataAccessor`
        - Protected accessible via `isInGround` and `setInGround`
    - `ThrowableItemProjectile` can now take in an `ItemStack` of the item thrown
- `net.minecraft.world.entity.raid.Raid#getLeaderBannerInstance` -> `getOminousBannerInstance`
- `net.minecraft.world.entity.vehicle`
    - `Boat$Type` now takes in the supplied boat item and the translation key for the item, but no longer take in the planks they are made from
    - `ContainerEntity`
        - `*LootTable*` -> `ContainerLootTable`
        - `chestVehicleDestroyed` now takes in a `ServerLevel`
    - `VehicleEntity`
        - `destroy` now takes in a `seerverLevel`
        - `getDropItem` is now protected
- `net.minecraft.world.item`
    - `BoatItem` now takes in an `EntityType` instead of the variant and chest boolean
    - `ItemStack#hurtEnemy`, `postHurtEnemy` now take in a `LivingEntity` instead of a `Player`
    - `SmithingTemplateItem` now takes in the `Item.Properties` instead of hardcoding it, also true for static initializers
    - `UseAnim` -> `ItemUseAnimation`
- `net.minecraft.world.item.crafting.ShulkerBoxColoring` -> `TransmuteRecipe`, expanded to copy any data stored on the item to the result item
- `net.minecraft.world.item.enchantment.EnchantmentHelper`
    - `onProjectileSpawned` now takes in a `Projectile` instead of an `AbstractArrow`
- `net.minecraft.world.item.enchantment.effects.DamageItem` -> `ChangeItemDamage`
- `net.minecraft.world.level`
    - `GameRules` takes in a `FeatureFlagSet` during any kind of construction
        - `$IntegerValue#create` takes in a `FeatureFlagSet`
        - `$Type` takes in a `FeatureFlagSet`
    - `Level`
        - `setSpawnSettings` no longer takes in a `boolean` to determine whether to spawn friendlies
        - `getGameRules` -> `ServerLevel#getGameRules`
    - `LevelAccessor` now implements `ScheduledTickAccess`, an interface that now contains the tick scheduling methods that were originally on `LevelAccessor`
        - `neighborShapeChanged` switches the order of the `BlockState` and neighbor `BlockPos` parameters
    - `LevelHeightAccessor`
        - `getMinBuildHeight` -> `getMinY`
        - `getMaxBuildHeight` -> `getMaxY`, this value is one less than the previous version
        - `getMinSection` -> `getMinSectionY`
        - `getMaxSection` -> `getMaxSectionY`, this value is one less than the previous version
    - `NaturalSpawner#spawnForChunk` has been split into two methods: `getFilteredSpawningCategories`, and `spawnForChunk`
- `net.minecraft.world.level.biome#Biome#getPrecipitationAt`, `coldEnoughToSnow`, `warmEnoughToRain`, `shouldMeltFrozenOceanIcebergSlightly` now takes in an `int` representing the the base height of the biome
- `net.minecraft.world.level.block`
    - `Block`
        - `shouldRenderFace` takes in the relative state for the face being checked, no longer passing in the `BlockGetter` or `BlockPos`s.
        - `updateEntityAfterFallOn` -> `updateEntityMovementAfterFallOn`
        - `$BlockStatePairKey` -> `FlowingFluid$BlockStatePairKey`, now package private
        - `getDescriptionId` -> `BlockBehaviour#getDescriptionId`, also a protected field `descriptionId`
    - `ChestBlock` constructor switched its parameter order
    - `Portal#getPortalDestination` now returns `TeleportTransition`
- `net.minecraft.world.level.block.entity`
    - `AbstractFurnaceBlockEntity#serverTick` now takes in a `ServerLevel` instead of a `Level`
    - `BrushableBlockEntity`
        - `brush` now takes in the level and stack performing the brushing behavior
        - `unpackLootTable` is now private
        - `checkReset` now takes in the server level
- `net.minecraft.world.level.block.state`
    - `BlockBehaviour`
        - `getOcclusionShape`, `getLightBlock`, `propagatesSkylightDown` only takes in the `BlockState`, not the `BlockGetter` or `BlockPos`
        - `getLootTable` now returns an `Optional`, also a protected field `drops`
        - `$BlockStateBase#getOcclusionShape`, `getLightBlock`, `getFaceOcclusionShape`, `propagatesSkylightDown`, `isSolidRender` no longer takes in the `BlockGetter` or `BlockPos`
        - `$BlockStateBase#getOffset` no longer takes in the `BlockGetter`
        - `$OffsetFunction#evaluate` no longer takes in the `BlockGetter`
        - `$Properties#dropsLike` -> `overrideLootTable`
    - `StateHolder#findNextInCollection` now takes in a `List` instead of a `Collection`
- `net.minecraft.world.level.chunk`
    - `ChunkAccess`
        - `addPackedPostProcess` now takes in a `ShortList` instead of a single `short`
        - `getTicksForSerialization` now takes in a `long` of the game time
        - `unsaved` is now private
        - `setUnsaved` -> `markUnsaved`, `tryMarkSaved`
        - `$TicksToSave` -> `$PackedTicks`
    - `ChunkSource#setSpawnSettings` no longer takes in a `boolean` to determine whether to spawn friendlies
    - `LevelChunk#postProcessGeneration` now takes in a `ServerLevel`
    - `Palette#copy` now takes in a `PaletteResize`
- `net.minecraft.world.level.chunk.status.WorldGenContext` now takes in an `Executor` or the main thread rather than a processor handle mail box
    - The construtor also takes in a `LevelChunk$UnsavedListener` for when a chunk is marked as dirty
- `net.minecraft.world.level.chunk.storage`
    - `ChunkSerializer` -> `SerializableChunkData`
    - `ChunkStorage#write` now takes in a supplied `CompoundTag` instead of the instance itself
    - `SectionStorage` now takes in a second generic representing the packed form of the storage data
        - The constructor now takes in the packed codec, a function to convert the storage to a packed format, and a function to convert the packed and dirty runnable back into the storage.
- `net.minecraft.world.level.levelgen`
    - `Aquifer$FluidStatus` is now a record
    - `WorldDimensions#withOverworld` now takes in a `HolderLookup` instead of the `Registry` itself
    - `BlendingData` now has a packed and unpacked state for serializing the interal data as a simple object
- `net.minecraft.world.level.levelgen.material.MaterialRuleList` now takes in an array instead of a list
- `net.minecraft.world.level.levelgen.placement.PlacementContext#getMinBuildHeight` -> `getMinY`
- `net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement#getShuffledJigsawBlocks` now returns a `StructureTemplate$JigsawBlockInfo`
- `net.minecraft.world.level.lighting`
    - `LevelLightEngine#lightOnInSection` -> `lightOnInColumn`
    - `LightEngine`
        - `hasDifferentLightProperties`, `getOcclusionShape` no longer takes in the `BlockGetter` or `BlockPos`
        - `getOpacity` no longer takes in the `BlockPos`
        - `shapeOccludes` no longer takes in the two `longs` representing the packed positions
- `net.minecraft.world.level.material`
    - `FlowingFluid`
        - `spread` now takes in the `BlockState` at the current position
        - `getSlopeDistance` previous parameters have been merged into a `$SpreadContext` object
        - `spread`, `getNewLiquid`, `canConvertToSource`, `getSpread` now takes in a `ServerLevel`
    - `Fluid`
        - `tick` now takes in the `BlockState` at the current position
        - `tick` and `randomTick` now take in the `ServerLevel`
    - `FluidState`
        - `tick` now takes in the `BlockState` at the current position
        - `tick` and `randomTick` now take in the `ServerLevel`
    - `MapColor#calculateRGBColor` -> `calculateARGBColor`
- `net.minecraft.world.level.portal`
    - `DimensionTransition` -> `TeleportTransition`
        - `pos` -> `position`
        - `speed` -> `deltaMovement`
        - The constructor can now take in a set of `Relatives` to indicate in what motions should the positions be moved relative to another
    - `PortalShape#createPortalBlocks` now takes in a `LevelAccessor`
- `net.minecraft.world.level.saveddata.SavedData#save(File, HolderLookup$Provider)` now returns `CompoundTag`, not writing the data to file in the method
- `net.minecraft.world.level.storage`
    - `DimensionDataStorage` now implements `AutoCloseable`
        - The constructor takes in a `Path` instead of a `File`
        - `save` -> `scheduleSave` and `saveAndJoin`
    - `LevelData#getGameRules` -> `ServerLevelData#getGameRules`
- `net.minecraft.world.phys.BlockHitResult` now takes in a boolean representing if the world border was hit
    - Adds in two helpers `hitBorder`, `isWorldBorderHit`
- `net.minecraft.world.ticks`
    - `ProtoChunkTicks#load` now takes in a list of saved ticks
    - `SavedTick#loadTickList` now returns a list of saved ticks, rather than consuming them
    - `SerializableTickContainer#save` -> `pack`

### List of Removals

- `com.mojang.blaze3d.Blaze3D`
    - `process`
    - `render`
- `com.mojang.blaze3d.pipeline.RenderPipeline`
    - Replaced by `com.mojang.blaze3d.framegraph.*` and `com.mojang.blaze3d.resources.*`
- `com.mojang.blaze3d.platform.NativeImage`
    - `setPixelLuminance`
    - `getRedOrLuminance`, `getGreenOrLuminance`, `getBlueOrLuminance`
    - `blendPixel`
    - `asByteArray`
- `com.mojang.blaze3d.systems.RenderSystem`
    - `glGenBuffers`
    - `glGenVertexArrays`
    - `_setShaderTexture`
    - `applyModelViewMatrix`
- `net.minecraft.Util#wrapThreadWithTaskName(String, Supplier)`
- `net.minecraft.advancements.critereon.EntitySubPredicates#BOAT`
- `net.minecraft.client.Options#setKey`
- `net.minecraft.client.gui.screens.inventory.EnchantmentScreen#time`
- `net.minecraft.client.multiplayer`
    - `ClientCommonPacketListenerImpl#strictErrorHandling`
    - `ClientLevel#isLightUpdateQueueEmpty`
    - `CommonListenerCookie#strictErrorHandling`
- `net.minecraft.client.particle.ParticleRenderType#PARTICLE_SHEET_LIT`
- `net.minecraft.client.renderer`
    - `GameRenderer#resetProjectionMatrix`
    - `LevelRenderer`
        - `playJukeboxSong`
        - `clear`
    - `PostChain`
        - `getTempTarget`, `addTempTarget`
    - `PostPass`
        - `setOrthoMatrix`
        - `getFilterMode`
- `net.minecraft.client.renderer.block.model.BlockModel#fromString`
- `net.minecraft.client.renderer.texture`
    - `AbstractTexture#blur`, `mipmap`
    - `TextureManager#bindForSetup`
- `net.minecraft.commands.arguments.coordinates.WorldCoordinates#current`
- `net.minecraft.core`
    - `Direction#fromDelta`
    - `Registry#getOrCreateTag`, `getTagNames`, `resetTags`
- `net.minecraft.server.MinecraftServer`
    - `isSpawningAnimals`
    - `areNpcsEnabled`
- `net.minecraft.server.level`
    - `GenerationChunkHolder#getGenerationRefCount`
    - `ServerPlayer`
        - `setPlayerInput`
        - `teleportTo(ServerLevel, double, double, double, float, float, boolean)`
- `net.minecraft.tags`
    - `TagManager`
    - `TagManagerSerialization$TagOutput`
- `net.minecraft.world.entity`
    - `AnimationState#updateTime`
    - `Entity`
        - `walkDist0`, `walkDist`
        - `wasOnFire`
        - `tryCheckInsideBlocks`
    - `EntitySelector$MobCanWearArmorEntitySelector`
- `net.minecraft.world.entity.ai.sensing`
    - `BreezeAttackEntitySensor#BREEZE_SENSOR_RADIUS`
    - `TemptingSensor#TEMPTATION_RANGE`
- `net.minecraft.world.entity.animal`
    - `Cat#getTextureId`
    - `Squid#setMovementVector`
    - `Wolf#isWet`
- `net.minecraft.world.entity.boss.dragon.EnderDragon`
    - `getLatencyPos`
    - `getHeadPartYOffset`
- `net.minecraft.world.entity.monster.Zombie#supportsBreakDoorGoal`
- `net.minecraft.world.entity.npc.Villager#setChasing`, `isChasing`
- `net.minecraft.world.entity.projectile`
    - `AbstractArrow#shotFromCrossbow`
    - `ThrowableProjectile(EntityType, LivingEntity, Level)`
- `net.minecraft.world.item`
    - `BannerPatternItem#getDisplayName`
    - `ItemStack#LIST_STREAM_CODEC`
- `net.minecraft.world.level.BlockGetter#getMaxLightLevel`
- `net.minecraft.world.level.block.entity.JigsawBlockEntity$JointType#byName`
- `net.minecraft.world.level.block.state.BlockBehaviour#isOcclusionShapeFullBlock`
- `net.minecraft.world.level.chunk.ChunkAccess#setBlendingData`
- `net.minecraft.world.level.storage.loot.LootDataType#deserialize`
- `net.minecraft.world.phys.AABB#getBottomCenter`
- `net.minecraft.world.phys.shapes.Shapes#getFaceShape`
- `net.minecraft.world.ticks.SavedTick#saveTick`



# Minecraft 1.21 -> 1.21.1 Mod Migration Primer

This is a high level, non-exhaustive overview on how to migrate your mod from 1.21 to 1.21.1. This does not look at any specific mod loader, just the changes to the vanilla classes.

This primer is licensed under the [Creative Commons Attribution 4.0 International](http://creativecommons.org/licenses/by/4.0/), so feel free to use it as a reference and leave a link so that other readers can consume the primer.

If there's any incorrect or missing information, please file an issue on this repository or ping @ChampionAsh5357 in the Neoforged Discord server.

## Minor Migrations

The following is a list of useful or interesting additions, changes, and removals that do not deserve their own section in the primer.

### Additions

- `net.minecraft.commands.arguments.selector.EntitySelectorParser#allowSelectors` - Returns whether the the available selector providers have at least creative mode permissions.
- `net.minecraft.world.level.block.entity.BlockEntity#isValidBlockState` - Returns whether the block state can have the current block entity.

### Removed

- `net.minecraft.commands.arguments.selector.EntitySelectorParser(StringReader)`

# Minecraft 1.20.5/6 -> 1.21 Mod Migration Primer

This is a high level, non-exhaustive overview on how to migrate your mod from 1.20.5/6 to 1.21. This does not look at any specific mod loader, just the changes to the vanilla classes.

This primer is licensed under the [Creative Commons Attribution 4.0 International](http://creativecommons.org/licenses/by/4.0/), so feel free to use it as a reference and leave a link so that other readers can consume the primer.

If there's any incorrect or missing information, please file an issue on this repository or ping @ChampionAsh5357 in the Neoforged Discord server.

## Pack Changes

There are a number of user-facing changes that are part of vanilla which are not discussed below that may be relevant to modders. You can find a list of them on [Misode's version changelog](https://misode.github.io/versions/?id=1.21&tab=changelog).

## Moving Experimental Features

All experimental features which were disabled with the `update_1_21` flag are now moved to their proper locations and implementations. Removed features flags can be seen within `net.minecraft.world.level.storage.WorldData#getRemovedFeatureFlags`.

## ResourceLocation, now Private

The `ResourceLocation` is final and its constructor is private. There are alternatives depending on your usecase:

- `new ResourceLocation(String, String)` -> `fromNamespaceAndPath(String, String)`
- `new ResourceLocation(String)` -> `parse(String)`
- `new ResourceLocation("minecraft", String)` -> `withDefaultNamespace(String)`
- `of` -> `bySeparator`
- `isValidResourceLocation` is removed

## Depluralizing Registry and Tag Folders

Plural references to the block, entity type, fluid, game event, and item tags have been removed. They should now use their exact registry name. The same goes for registry folders.

- `tags/blocks` -> `tags/block`
- `tags/entity_types` -> `tags/entity_type`
- `tags/fluids` -> `tags/fluid`
- `tags/game_events` -> `tags/game_event`
- `tags/items` -> `tags/item`
- `advancements` -> `advancement`
- `recipes` -> `recipe`
- `structures` -> `structure`'
- `loot_tables` -> `loot_table`

## Oh Rendering, why must you change so?

There have been a number of rendering changes. While this will not be an in-depth overview, it will cover most of the surface-level changes.

### Vertex System

The vertex system has received a major overhaul, almost being completely rewritten. However, most of the codebase has some analogue to its previous version in different locations.

First, a `VertexConsumer` is obtained using one of two methods: from a `MultiBufferSource` or the `Tesselator`. Both essentially take a `ByteBufferBuilder`, which handles the direct allocation of the vertex information, and wrap it in a `BufferBuilder`, which writes the data to the `ByteBufferBuilder` while also keeping track of other settings needed to properly write the data, such as the `VertexFormat`.

The `MultiBufferSource` constructs a `VertexConsumer` via `#getBuffer` while `Tesselator` does so via `Tesselator#begin`.

```java
// For some MultiBufferSource bufferSource
VertexConsumer buffer = bufferSource.getBuffer(RenderType.translucent());

// Note the different return types when using
// We will need the BufferBuilder subclass in the future
BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
```

Next, vertices are added to the `VertexConsumer` using the associated methods. `#addVertex` must always be called first, followed by the settings specified in the `VertexFormat`. `#endVertex` no longer exists and is called automatically when calling `#addVertex` or when uploading the buffer.

```java
// For some VertexConsumer buffer
buffer.addVertex(0.0f, 0.0f, 2000.0f).setUv(0, 0).setColor(-1);
buffer.addVertex(0.0f, 1.0f, 2000.0f).setUv(0, 0).setColor(-1);
buffer.addVertex(1.0f, 1.0f, 2000.0f).setUv(0, 0).setColor(-1);
buffer.addVertex(1.0f, 0.0f, 2000.0f).setUv(0, 0).setColor(-1);
```

Once the vertices are added, those using `MultiBufferSource` are done. `MultiBufferSource` will batch the buffers together for each `RenderType` and call the `BufferUploader` within the pipeline. If the `RenderType` sorts the vertices on upload, then it will do so when `endBatch` is called, right before `RenderType#draw` is called to setup the render state and draw the data.

The `Tesselator`, on the other hand, does not handle this logic as you are creating the `BufferBuilder` instance to manage. In this case, after writing all ther vertices, `BufferUploader#drawWithShader` should be called. The `MeshData` provided, which contains the vertex and index buffer along with the draw state, can be built via `BufferBuilder#buildOrThrow`. This replaces `BufferBuilder#end`.

```java
// For some BufferBuilder buffer
BufferUploader.drawWithShader(buffer.buildOrThrow());
```

#### Changes

- `com.mojang.blaze3d.vertex.DefaultVertexFormat`'s `VertexFormatElement`s have been moved to `VertexFormatElement`.
- `com.mojang.blaze3d.vertex.BufferBuilder$RenderedBuffer` -> `MeshData`
- `com.mojang.blaze3d.vertex.VertexConsumer`
    - `vertex` -> `addVertex`
        - Overload with `PoseStack$Pose, Vector3f`
    - `color` -> `setColor`
    - `uv` -> `setUv`
    - `overlayCoords` -> `setUv1`, `setOverlay`
    - `uv2` -> `setUv2`, `setLight`
    - `normal` -> `setNormal`
    - `endVertex` is removed
    - `defaultColor`, `color` -> `setColor`, `setWhiteAlpha`
- `net.minecraft.client.model.Model#renderToBuffer` now takes in an integer representing the ARGB tint instead of four floats
    - There is also a final method which passes in no tint
- `net.minecraft.client.model.geom.ModelPart#render`, `$Cube#compile` now takes in an integer representing the ARGB tint instead of four floats
    - There is also an overloaded method which passes in no tint
- `net.minecraft.client.particle.ParticleRenderType#begin(Tesselator, TextureManager)`, `end` -> `begin(BufferBuilder, TextureManager)`
    - This method returns the `BufferBuilder` rather than void
    - When `null`, no rendering will occur
- Shader removals and replacements
    - `minecraft:position_color_tex` -> `minecraft:position_tex_color`
    - `minecraft:rendertype_armor_glint` -> `minecraft:rendertype_armor_entity_glint`
    - `minecraft:rendertype_glint_direct` -> `minecraft:rendertype_glint`
- `net.minecraft.client.renderer.MultiBufferSource`
    - All `BufferBuilder`s have been replaced with `ByteBufferBuilder`
    - `immediateWithBuffers` now takes in a `SequencedMap`
- `net.minecraft.client.renderer.RenderType`
    - `end` -> `draw`
    - `sortOnUpload` - When true, sorts the quads according to the `VertexSorting` method for the `RenderType`
- `net.minecraft.client.renderer.SectionBufferBuilderPack#builder` -> `#buffer`
- `net.minecraft.client.renderer.ShaderInstance` no longer can change the blend mode, only `EffectInstance` can, which is applied for `PostPass`
    - `setDefaultUniforms` - Sets the default uniforms accessible to all shader instances
- `net.minecraft.client.renderer.entity.ItemRenderer`
    - `getArmorFoilBuffer` no longer takes in a boolean to change the render type
    - `getCompassFoilBufferDirect` is removed
- `net.minecraft.client.renderer.entity.layers.RenderLayer#coloredCutoutModelCopyLayerRender`, `renderColoredCutoutModel` takes in an integer representing the color rather than three floats
- `com.mojang.blaze3d.vertex.BufferVertexConsumer` is removed
- `com.mojang.blaze3d.vertex.DefaultedVertexConsumer` is removed

### Chunk Regions

- `net.minecraft.client.renderer.chunk.RenderRegionCache#createRegion` only takes in the `SectionPos` now instead of computing the section from the `BlockPos`
- `net.minecraft.client.renderer.chunk.SectionCompiler` - Renders the given chunk region via `#compile`. Returns the results of the rendering compilation.
    - This is used within `SectionRenderDispatcher` now to pass around the stored results and upload them

## The Enchantment Datapack Object

`Enchantment`s are now a datapack registry object. Querying them requires access to a `HolderLookup.Provider` or one of its subclasses.

All references to `Enchantment`s are now wrapped with `Holder`s. Some helpers can be found within `EnchantmentHelper`.

```json5
{
    // A component containing the description of the enchantment
    // Typically will be a component with translatable contents
    "description": {
        "translate": "enchantment.minecraft.example"
    },
    // An item tag that holds all items this enchantment can be applied to
    "supported_items": "#minecraft:enchantable/weapon",
    // An item tag that holds a subset of supported_items that this enchantment can be applied to in an enchanting table
    "primary_items": "#minecraft:enchantable/sharp_weapon",
    // A non-negative integer that provides a weight to be added to a pool when trying to get a random enchantment
    "weight": 3,
    // A non-negative integer that indicates the maximum level of the enchantment
    "max_level": 4,
    // The minimum cost necessary to apply this enchantment to an item
    "min_cost": {
        // The base cost of the enchantment at level 1
        "base": 10,
        // The amount to increase the cost with each added level
        "per_level_above_first": 20
    },
    // The maxmimum cost required to apply this enchantment to an item
    "max_cost": {
        "base": 60,
        "per_level_above_first": 20
    }
    // A non-negative integer that determines the cost to add this enchantment via the anvil
    "anvil_cost": 5,
    // The equipment slot groups this enchantment is applied to
    // Can be 'any', 'hand' ('mainhand' and 'offhand'), 'armor' ('head', 'chest', 'legs', and 'feet'), or 'body'
    "slots": [
        "hand"
    ],
    // An enchantment tag that contains tags that this enchantment cannot be on the same item with
    "exclusive_set": "#minecraft:exclusive_set/damage",
    // An encoded data component map which contains the effects to apply
    "effects": {
        // Read below
    },
}
```

### EnchantmentEffectComponents

`EnchantmentEffectComponents` essentially apply how enchantments should react in a given context when on an item. Each effect component is encoded as an object with its component id as the key and the object value within the effect list, usually wrapped as a list. Most of these components are wrapped in a `ConditionalEffect`, so that will be the focus of this primer.

#### ConditionalEFfect

`ConditionalEffect`s are basically a pair of the effect to apply and a list of loot conditions to determine when to execute the enchantment. The codec provided contains the effect object code and the loot context param sets to apply for the conditions (commonly one of the `ENCHANTED_*` sets).

#### The Effect Objects

Each effect object has its own combination of codecable objects and abstract logic which may refer to other registered types, similiar to loot conditions, functions, number providers, etc. For enchantments currently, this means enchantments which are applied directly to the entity, scaled and calulated for some numerical distribution, applied to the blocks around the entity, or calculating a number for some information.

For example, all protection enchantments use almost the exact same effect objects (`minecraft:attributes` and `minecraft:damage_protection`) but use the conditions to differentiate when those values should be applied.

To apply these enchantments, `EnchantmentHelper` has a bunch of different methods to help with the application. Typically, most of these are funneled through one of the `runIterationOn*` methods. These method take in the current context (e.g., stack, slot, entity) and a visitor which holds the current enchantment, level, and optionally the stack context if the item is in use. The visitor takes in a mutable object which holds the final value to return. The modifications to apply to the given item(s) are within `Enchantment#modify*`. If the conditions match, then the value from the mutable object is passed in, processed, and set back to the object.

### Enchantment Providers

`EnchantmentProvider`s are a provider of enchantments to items for entity inventory items or for custom loot on death. Each provider is some number of enchantments that is then randomly selected from and applied to the specified stacks via `EnchantmentHelper#enchantItemFromProvider` which takes in the stack to enchant, the registry access, the provider key, the difficulty instance, and the random instance.

### Minor Changes

- `net.minecraft.world.entity.LivingEntity#activeLocationDependentEnchantments` - Returns the enchantments which depend on the current location of the entity
- `net.minecraft.world.entity.Entity#doEnchantDamageEffects` has been removed
- `net.minecraft.world.entity.npc.VillagerTrades$ItemListing` implementations with `Enchantment`s now take in keys or tags of the corresponding object
- `net.minecraft.world.entity.player.Player#getEnchantedDamage` - Gets the dmaage of a source modified by the current enchantments
- `net.minecraft.world.entity.projectile.AbstractArrow#hitBlockEnchantmentEffects` - Applies the enchantment modifiers when a block is hit
- `net.minecraft.world.entity.projectile.AbstractArrow#setEnchantmentEffectsFromEntity` has been removed
- `net.minecraft.world.item.ItemStack#enchant` takes in a `Holder<Enchantment>` instead of the direct object
- `net.minecraft.world.entity.Mob#populateDefaultEquipmentEnchantments`, `enchantSpawnedWeapon`, `enchantSpawnedArmor` now take in a `ServerLevelAccessor` and `DifficultyInstance`

## The Painting Variant Datapack Object

`PaintingVariant`s are now a datapack registry object. Querying them requires access to a `HolderLookup.Provider` or one of its subclasses.

```json5
{
    // A relative location pointing to 'assets/<namespace>/textures/painting/<path>.png
    "asset_id": "minecraft:courbet",
    // A value between 1-16 representing the number of blocks this variant takes up
    // e.g. a width of 2 means it takes up 2 blocks, and has an image size of 32px
    "width": 2,
    // A value between 1-16 representing the number of blocks this variant takes up
    // e.g. a height of 1 means it takes up 1 blocks, and has an image size of 16px
    "height": 1
}
```

## Attribute Modifiers, now with ResourceLocations

`AttributeModifier`s no longer take in a `String` representing its UUID. Instead, a `ResourceLocation` is provided to uniquely identity the modifier to apply. Attribute modifiers can be compared for `ResourceLocation`s using `#is`.

- `net.minecraft.world.effect.MobEffect`
    - `addAttributeModifier`, `$AttributeTemplate` takes in a `ResourceLocation` instead of a `String`
- `net.minecraft.world.entity.ai.attributes.AttributeInstance`
    - `getModifiers` returns a `Map<ResourceLocation, AttributeModifier>`
    - `getModifier`, `hasModifier`, `removeModifier` now takes in a `ResourceLocation`
    - `removeModifier` now returns a boolean
    - `removePermanentModifier` is removed
    - `addOrReplacePermanentModifier` - Adds or replaces the provided modifier
- `net.minecraft.world.entity.ai.attributes.AttributeMap`
    - `getModifierValue`, `hasModifier`, now takes in a `ResourceLocation`
- `net.minecraft.world.entity.ai.attributes.AttributeSupplier`
    - `getModifierValue`, `hasModifier`, now takes in a `ResourceLocation`

## RecipeInput

`Recipe`s now take in a `net.minecraft.world.item.crafting.RecipeInput` instead of a `Container`. A `RecipeInput` is basically a minimal view into a list of available stacks. It contains three methods:

- `getItem` - Returns the stack in the specified index
- `size` - The size of the backing list
- `isEmpty` - true if all stacks in the list are empty

As such, all implementations which previous took a `Container` now take in a `RecipeInput`. You can think of it as replacing all the `C` generics with a `T` generic representing the `RecipeInput`. This also includes `RecipeHolder`, `RecipeManager`, and the others below.

### CraftingInput

`CraftingInput` is an implementation of `RecipeInput` which takes in width and height of the grid along with a list of `ItemStack`s. One can be created using `CraftingInput#of`. These are used in crafting recipes.

### SingleRecipeInput

`SingleRecipeInput` is an implementation of `RecipeInput` that only has one item. One can be created using the constructor.

## Changing Dimensions

How entities change dimensions have been slightly reworked in the logic provided. Instead of providing a `PortalInfo` to be constructed, instead everything returns a `DimensionTransition`. `DimensionTransition` contains the level to change to, the entity's position, speed, and rotation, and whether a respawn block should be checked. `DimensionTransition` replaces `PortalInfo` in all scenarios.

Entities have two methods for determining whether they can teleport. `Entity#canUsePortal` returns true if the entity can use a `Portal` to change dimensions, where the boolean supplies allows entities who are passengers to teleport. To actually change dimensions, `Entity#canChangeDimensions` must return true, where the current and teleporting to level is provided. Normally, only `canUsePortal` is changed while `canChangeDimensions` is always true.

> `canChangeDimensions` functioned as `canUsePortal` in 1.20.5/6. 

To change dimensions via a portal, a `Portal` should be supplied to `Entity#setAsInsidePortal`. A `Portal` contains how long it takes for the portal to transition, the `DimensionTransition` destination, and the transition effect to apply to the user. This is all wrapped in a `PortalProcessor` and stored on the `Entity`. Once the player is teleported a `DimensionTransition$PostDimensionTransition` is executed, for example playing a sound.

`Portal`s are generally implmented on the `Block` doing the teleporting.

- `net.minecraft.world.entity.Entity`
    - `changeDimension(ServerLevel)` -> `changeDimension(DimensionTransition)`
    - `findDimensionEntryPoint` -> `Portal#getPortalDestination`
    - `getPortalWaitTime` -> `Portal#getPortalTransitionTime`
    - `handleInsidePortal` -> `setAsInsidePortal`
    - `handleNetherPortal` -> `handlePortal`
    - `teleportToWithTicket` is removed
    - `getRelativePortalPosition` is now public
- `net.minecraft.world.level.portal.PortalShape`
    - `createPortalInfo` is removed
    - `findCollisionFreePosition` is now public
- `net.minecraft.client.player.LocalPlayer#getActivePortalLocalTransition` - Defines the transition to apply to the entity when the player is within a portal
- `net.minecraft.world.level.portal.PortalForce#findPortalAround` -> `findClosestPortalPosition`

### Minor Changes

- `net.minecraft.recipebook.ServerPlaceRecipe` now has a generic taking in the `RecipeInput` and the `Recipe` rather than the `Container`
- `net.minecraft.world.inventory.CraftingContainer#asCraftInput` - Converts a crafting container to an input to supply
- `net.minecraft.world.inventory.CraftingContainer#asPositionedCraftInput` - Converts a crafting container to an input positioned at some location within a grid
- `net.minecraft.world.inventory.CraftingMenu#slotChangedCraftingGrid` now takes in a `RecipeHolder`
- `net.minecraft.world.inventory.RecipeBookMenu` now has a generic taking in the `RecipeInput` and the `Recipe` rather than the `Container`
  - `beginPlacingRecipe` - When the recipe is about to be placed in the corresponding location
  - `finishPlacingRecipe` - AFter the recipe has been placed in the corresponding location
- `net.minecraft.client.gui.screens.recipebook.*RecipeComponent#addItemToSlot` still exists but is no longer overridded from its subclass

## Minor Migrations

The following is a list of useful or interesting additions, changes, and removals that do not deserve their own section in the primer.

### Options Screens Movement

The options screens within `net.minecraft.client.gui.screens` and `net.minecraft.client.gui.screens.controls` have been moved to `net.minecraft.client.gui.screens.options` and `net.minecraft.client.gui.screens.options.controls`, respectively.

### The HolderLookup$Provider in LootTableProvider

`net.minecraft.data.loot.LootTableProvider$SubProviderEntry` takes in a function which provides the `HolderLookup$Provider` and returns the `LootTableSubProvider`. `LootTableSubProvider#generate` no longer takes in a `HolderLookup$Provider` as its first argument.

### DecoratedPotPattern Object

`Registries#DECORATED_POT_PATTERNS` takes in a `DecoratedPotPattern` instead of the raw string for the asset id. This change has been reflected in all subsequent classes (e.g. `Sheets`).

### Jukebox Playable

`RecordItem`s has been removed in favor of a new data component `JukeboxPlayable`. This is added via the item properties. A jukebox song is played via `JukeboxSongPlayer`. `JukeboxBlockEntity` handles an implementation of `JukeboxSongPlayer`, replacing all of the logic handling within the class itself.

- `net.minecraft.world.item.Item$Properties#jukeboxPlayable` - Sets the song to play in a jukebox when this is added
- `net.minecraft.world.item.JukeboxSong` - A `SoundEvent`, description, length, and comparator output record indicating what this item would do when placed in a jukebox
    - This is a datapack registry
- `net.minecraft.world.item.JukeboxPlayable` - The data component for a `JukeboxSong`
- `net.minecraft.world.item.JukeboxSongPlayer` - A player which handles the logic of how a jukebox song is played
- `net.minecraft.world.level.block.entity.JukeboxBlockEntity#getComparatorOutput` - Gets the comparator output of the song playing in the jukebox

### Chunk Generation Reorganization

Chunk generation has been reorganized again, where generation logic is moved into separate maps, task, and holder classes to be executed and handled asyncronously in most instances. Missing methods can be found in `ChunkGenerationTask`, `GeneratingChunkMap`, or `GenerationChunkHolder`. Additional chunk generation handling is within `net.minecraft.world.level.chunk.status.*`.

This also means some region methods which only contain generation-based information have been replaced with one of these three classes instead of their constructed counterparts. The steps are now listed in a `ChunkPyramid`, which determines the steps takes on how a protochunk is transformed into a full chunk. Each step can have a `ChunkDependencies`, forcing the order of which they execute. `ChunkPyramid#GENERATION_PYRAMID` handles chunk generation during worldgen while `LOADING_PYRAMID` handles loading a chunk that has already been generated. The `WorldGenContext` holds a reference to the main thread box when necessary.

### Delta Tracker

`net.minecraft.client.DeltaTracker` is an interface mean to keep track of the delta ticks, both ingame and real time. This also holds the current partial tick. This replaces most instances passing around the delta time or current partial tick.

- `net.minecraft.client.Minecraft#getFrameTime`, `getDeltaFrameTime` -> `getTimer`
- `net.minecraft.client.Timer` -> `DeltaTracker$Timer`
- `net.minecraft.client.gui.Gui#render` now takes in a `DeltaTracker`

### Additions

- `com.mojang.realmsclient.dto.RealmsServer#isMinigameActive` - Returns whether a minigame is active.
- `net.minecraft.SystemReport#sizeInMiB` - Converts a long representing the number of bytes into a float representing Mebibyte (Power 2 Megabyte)
- `net.minecraft.Util#isSymmetrical` - Checks whether a list, or some subset, is symmetrical
- `net.minecraft.advancements.critereon.MovementPredicate` - A predicate which indicates how the entity is currently moving
- `net.minecraft.client.gui.components.AbstractSelectionList#clampScrollAmount` - Clamps the current scroll amount
- `net.minecraft.client.gui.screens.AccessibilityOnboardingScreen#updateNarratorButton` - Updates the narrator button
- `net.minecraft.client.gui.screens.worldselection.WorldCreationContext#validate` - Validates the generators of all datapack dimensions
- `net.minecraft.client.multiplayer.ClientPacketListener`
  - `updateSearchTrees` - Rebuilds the search trees for creative tabs and recipe collections
  - `searchTrees` - Gets the current search trees for creative tabs and recipe collections
- `net.minecraft.client.multiplayer.SessionSearchTrees` - The data contained for creative tabs and recipe collections on the client
- `net.minecraft.commands.arguments.item.ItemParser$Visitor#visitRemovedComponent` - Executed when a component is to be removed as part of an item input
- `net.minecraft.core.Registry#getAny` - Gets an element from the registry, usually the first one registered
- `net.minecraft.core.component.DataComponentMap`
  - `makeCodec` - Creates a component map codec from a component type codec
  - `makeCodecFromMap` - Creates a component map codec from a component type to object map codec
- `net.minecraft.util.ProblemReporter#getReport` - Returns a report of the problem
- `net.minecraft.util.Unit#CODEC`
- `net.minecraft.world.damagesource.DamageSources#campfire` - Returns a source where the damage was from a campfire
- `net.minecraft.world.damagesource.DamageType#CODEC`
- `net.minecraft.world.entity.LivingEntity`
  - `hasLandedInLiquid` - Returns whether the entity is moving faster than -0.00001 in the y direction while in a liquid
  - `getKnockback` - Gets the knockback to apply to the entity
- `net.minecraft.world.entity.Mob#playAttackSound` - Executes when the mob should play a sound when attacking
- `net.minecraft.world.entity.ai.attributes.Attribute#CODEC`
- `net.minecraft.world.entity.ai.attributes.AttibuteMap`
  - `addTransientAttributeModifiers` - Adds all modifiers from the provided map
  - `removeAttributeModifiers` - Removes all modifiers in the provided map
- `net.minecraft.world.entity.projectile.AbstractArrow`
  - `getWeaponItem` - The weapon the arrow was fired from
  - `setBaseDamageFromMob` - Sets the base damage this arrow can apply
- `net.minecraft.world.entity.projectile.Projectile#calculateHorizontalHurtKnockbackDirection` - Returns the horizontal vector of the knockback direction
- `net.minecraft.world.inventory.ArmorSlot` - A slot for armor
- `net.minecraft.world.item.CrossbowItem#getChargingSounds` - Sets the sounds to play when pulling back the crossbow
- `net.minecraft.world.item.Item#postHurtEntity` - Gets called after the enemy has been hurt by this item
- `net.minecraft.world.level.Explosion#canTriggerBlocks` - Returns whether the explosion cna trigger a block
- `net.minecraft.world.level.block.LeverBlock#playSound` - Plays the lever click sound
- `net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate`
  - `unobstructed` - Returns a predicate which indicates the current vector is not obstructed by another entity
- `net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction` - A function which grows the loot based on the corresponding enchantment level
- `net.minecraft.world.level.storage.loot.predicates.EnchantmentActiveCheck` - Checks whether a given enchantment is applied
- `net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider` - Determines the enchantment level to provide
- `net.minecraft.world.phys.AABB#move` - An overload of `#move` which takes in a `Vector3f`
- `net.minecraft.core.dispenser.DefaultDispenseItemBehavior#consumeWithRemainder` - Shrinks the first item stack. If empty, then the second item stack is returned. Otherwise, the second item stack is added to an inventory or dispensed, and the first item stack is returned.
- `net.minecraft.server.MinecraftServer`
    - `throwIfFatalException` - Throws an exception if its variable isn't null
    - `setFatalException` - Sets the fatal exception to throw
- `net.minecraft.util.StaticCache2D` - A two-dimensional read-only array which constructs all objects on initialization
- `net.minecraft.world.entity.Entity`
    - `fudgePositionAfterSizeChange` - Returns whether the entity's position can be moved to an appropriate free position after its dimension's change
    - `getKnownMovement` - Returns the current movement of the entity, or the last known client movement of the player
- `net.minecraft.world.entity.ai.attributes.Attribute`
    - `setSentiment` - Sets whether the attribute provides a positive, neutral or negative benefit when the value is positive
    - `getStyle` - Gets the chat formatting of the text based on the sentiment and whether the attribite value is positive
- `net.minecraft.world.entity.ai.attributes.AttributeMap#getAttributestoSync` - Returns the attributes to sync to the client
- `net.minecraft.world.level.storage.loot.LootContext$Builder#withOptionalRandomSource` - Sets the random source to use for the loot context during generation
    - This is passed by `LootTable#getRandomItems(LootParams, RandomSource)`
- `net.minecraft.client.Options#genericValueOrOffLabel` - Gets the generic value label, or an on/off component if the integer provided is 0
- `net.minecraft.client.gui.GuiGraphics#drawStringWithBackdrop` - Draws a string with a rectangle behind it
- `net.minecraft.gametest.framework.GameTestHelper`
    - `assertBlockEntityData` - Asserts that the block entity has the given information
    - `assertEntityPosition` - Asserts that the enity position is within a bounding box
- `net.minecraft.server.level.ServerPlayer#copyRespawnPosition` - Copies the respawn position from another player
- `net.minecraft.server.players.PlayerList#sendActiveEffects`, `sendActivePlayerEffects` - Sends the current entity's effects to the client using the provided packet listener
- `net.minecraft.util.Mth#hsvToArgb` - Converts an HSV value with an alpha integer to ARGB
- `net.minecraft.world.entity.Entity#absRotateTo` - Rotates the y and x of the entity clamped between its maximum values
- `net.minecraft.world.entity.ai.attributes.AttributeMap#assignBaseValues` - Sets the base value for each attribute in the map
- `net.minecraft.world.item.ItemStack#forEachModifier` - An overload which applies the modifier for each equipment slot in the group
    - Applies to `net.minecraft.world.item.component.ItemAttributeModifiers#forEach` as well
- `net.minecraft.world.level.levelgen.PositionalRandomFactory#fromSeed` - Constructs a random instance from a seed
- `net.minecraft.world.level.levelgen.structure.pools.DimensionPadding` - The padding to apply above and below a structure when attempting to generate
- `net.minecraft.ReportType` - An object that represents a header plus a list of nuggets to display after the header text
- `net.minecraft.advancements.critereon.GameTypePredicate` - A predicate that checks the game type of the player (e.g. creative, survival, etc.)
- `net.minecraft.advancements.critereon.ItemJukeboxPlayablePredicate` - A predicate that checks if the jukebox is playing a song
- `net.minecraft.core.BlockPos#clampLocationWithin` - Clamps the vector within the block
- `net.minecraft.core.registries.Registries`
    - `elementsDirPath` - Gets the path of the registry key
    - `tagsDirPath` - Gets the tags path of the registry key (replaces `TagManager#getTagDir`
- `net.minecraft.network.DisconnectionDetails` - Information as to why the user was disconnected from the current world
- `net.minecraft.server.MinecraftServer#serverLinks` - A list of entries indicating what the link coming from the server is
    - Only used for bug reports
- `net.minecraft.util.FastColor`
    - `$ABGR32#fromArgb32` - Reformats an ARGB32 color into a ABGR32 color
    - `$ARGB32#average` - Averages two colors together by each component
- `net.minecraft.util.Mth#lengthSquared` - Returns the squared distance of three floats
- `net.minecraft.world.entity.LivingEntity#triggerOnDeathMobEffects` - Trigers the mob effects when an entity is killed
- `net.minecraft.world.entity.TamableAnimal`
    - `tryToTeleportToOwner` - Attempts to teleport to the entity owner
    - `shouldTryTeleportToOwner` - Returns true if the entity owner is more than 12 blocks away
    - `unableToMoveToOwner` - Returns true if the animal cannot move to the entity owner
    - `canFlyToOwner` - Returns true if the animal can fly to the owner
- `net.minecraft.world.entity.projectile.Projectile#disown` - Removes the owner of the projectile
- `net.minecraft.world.item.EitherHolder` - A holder which either contains the holder instance or a resource key
- `net.minecraft.world.level.chunk.ChunkAccess#isSectionEmpty` - Returns whether the section only has air
- `net.minecraft.FileUtil#sanitizeName` - Replaces all illegal file charcters with underscores
- `net.minecraft.Util#parseAndValidateUntrustedUri` - Returns a URI after validating that the protocol scheme is supported
- `net.minecraft.client.gui.screens.ConfirmLinkScreen` now has two overloads to take in a `URI`
    - `confirmLinkNow` and `confirmLink` also has overloads for a `URI` parameter
- `net.minecraft.client.renderer.LevelRenderer#renderFace` - Renders a quad given two points and a color
- `net.minecraft.client.renderer.entity.EntityRenderer#renderLeash` - A **private** method that renders the leash for an entity
- `net.minecraft.client.resources.model.BlockStateModelLoader` - Loads the blockstate definitions for every block in the registry
    - Anything missing from `ModelBakery` is most likely here
- `net.minecraft.client.resources.model.ModelBakery$TextureGetter` - Gets the `TextureAtlasSprite` given the model location and the material provided
- `net.minecraft.core.BlockPos#getBottomCenter` - Gets the `Vec3` representing the bottom center of the position
- `net.minecraft.gametest.framework.GameTestBatchFactory#fromGameTestInfo(int)` - Batches the game tests into the specified partitions
- `net.minecraft.gametest.framework.GameTestHelper#getTestRotation` - Gets the rotation of the structure from the test info
- `net.minecraft.gametest.framework.GameTestRunner$StructureSpawner#onBatchStart` - Executes when the batch is going to start running within the level
- `net.minecraft.network.ProtocolInfo$Unbound`
    - `id` - The id of the protocol
    - `flow` - The direction the packet should be sent
    - `listPackets` - Provides a visitor to all packets that can be sent on this protocol
- `net.minecraft.network.chat.Component#translationArg` - Creates a component for a `URI`
- `net.minecraft.network.protocol.game.VecDeltaCodec#getBase` - Returns the base vector before encoding
- `net.minecraft.server.level.ServerEntity`
    - `getPositionBase` - Returns the current position of the entity
    - `getLastSentMovement` - Gets the vector representing the last velocity of the entity sent to the client
    - `getLastSentXRot` - Gets the last x rotation of the entity sent to the client
    - `getLastSentYRot` - Gets the last y rotation of the entity sent to the client
    - `getLastSentYHeadRot` - Gets the last y head rotation of the entity sent to the client
- `net.minecraft.world.damagesource.DamageSource#getWeaponItem` - Returns the itemstack the direct entity had
- `net.minecraft.world.entity.Entity`
    - `adjustSpawnLocation` - Returns the block pos representing the spawn location of the entity. By default, returns the entity spawn location
    - `moveTo` has an overload taking in a `Vec3`
    - `placePortalTicket` - Adds a region ticket that there is a portal at the `BlockPos`
    - `getPreciseBodyRotation` - Lerps between the previous and current body rotation of the entity
    - `getWeaponItem` - The item the entity is holding as a weapon
- `net.minecraft.world.entity.Leashable` - Indicates that an entity can be leashed
- `net.minecraft.world.entity.Mob`
    - `dropPreservedEquipment` - Drops the equipment the entity is wearing if it doesn't match the provided predicate or if the equipment succeeds on the drop chance check
- `net.minecraft.world.entity.player.Player`
    - `setIgnoreFallDamageFromCurrentImpulse` - Ignores the fall damage from the current impulses for 40 ticks when true
    - `tryResetCurrentImpulseContext` - Resets the impulse if the grace time reaches 0
- `net.minecraft.world.item.ItemStack#hurtAndConvertOnBreak` - Hurts the stack and when the item breaks, sets the item to another item
- `net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter` - Handles error reporting for chunk IO failures
- `net.minecraft.world.level.chunk.sotrage.ChunkStorage`, `IOWorker`, `RegionFileStorage`, `SimpleRegionStorage#storageInfo` - Returns the `RegionStorageInfo` used to store the chunk
- `net.minecraft.world.level.levelgen.structure.Structure$StructureSettings` now has a constructor with default values given the biome tag
    - `$Builder` - Builds the settings for the structure
- `net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings` - The settings to apply for blocks spawning with liquids within them
- `net.minecraft.world.level.storage.loot.ValidationContext` now has an constructor overload taking in an empty `HolderGetter$Provider`
    - `allowsReferences` - Checks whether the resolver is present
- `net.minecraft.client.Options#onboardingAccessibilityFinished` - Disables the onboarding accessibility feature
- `net.minecraft.client.gui.screens.reporting.AbstractReportScreen`
    - `createHeader` - Creates the report header
    - `addContent` - Adds the report content
    - `createFooter` - Creates the report footer
    - `onReportChanged` - Updates the report information, or sets a `CannotBuildReason` if not possible to change
- `net.minecraft.client.multiplayer.chat.report.Report#attested` - Sets whether the user has read the report and wants to send it
- `net.minecraft.world.level.levelgen.feature.EndPlatformFeature` - A feature that generates the end platform
- `net.minecraft.world.level.levelgen.placement.FixedPlacement` - A placement that places a feature in one of the provided positions
- `net.minecraft.world.phys.AABB`
    - `getBottomCenter` - Gets the `Vec3` representing the bottom center of the box
    - `getMinPosition` - Gets the `Vec3` representing the smallest coordinate of the box
    - `getMaxPosition` - Gets the `Vec3` representing the largest coordinate of the box
- `net.minecraft.world.entity.player.Player#isIgnoringFallDamageFromCurrentImpulse` - Returns whether the player should ignore fall damage
- `net.minecraft.world.item.LeadItem#leashableInArea` - Returns a list of Leashable entities within a 7 block radius of the provided block position

### Changes

- `net.minecraft.advancements.critereon.EnchantmentPredicate` now takes in a `HolderSet<Enchantment`
  - The previous constructor still exists as an overload
- `net.minecraft.advancements.critereon.EntityFlagsPredicate` now takes in a boolean for if the entity is on the ground or flying
- `net.minecraft.advancements.critereon.EntityPredicate` now takes in a movement predicate and periodic tick which indicates when the predicate can return true
- `net.minecraft.advancements.critereon.LocationPredicate` now takes in a fluid predicate
- `net.minecraft.client.gui.components.AbstractSelectionList#setScrollAmount` -> `setClampedScrollAmount`
  - `setScrollAmount` now delegates to this method
- `net.minecraft.client.gui.components.OptionsList` now longer takes in the unused integer
- `net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen` now takes in a `LocalPlayer` instead of a `Player`
- `net.minecraft.client.resources.language.LanguageManager` now takes in a `Consumer<ClientLanguage>` which acts as a callback when resources are reloaded
- `net.minecraft.client.searchtree.PlainTextSearchTree` -> `SearchTree#plainText`
- `net.minecraft.client.searchtree.RefreshableSearchTree` -> `SearchTree`
- `net.minecraft.commands.arguments.item.ItemInput` now takes in a `DataComponentPatch` instead of a `DataComponentMap`
- `net.minecraft.core.component.TypedDataComponent#createUnchecked` is now public
- `net.minecraft.data.loot.BlockLootSubProvider` now takes in a `HolderLookup$Provider`
  - `HAS_SILK_TOUCH` -> `hasSilkTouch` 
  - `HAS_NO_SILK_TOUCH` -> `doesNotHaveSilkTouch`
  - `HAS_SHEARS_OR_SILK_TOUCH` -> `hasShearsOrSilkTouch`
  - `HAS_NO_SHEARS_OR_SILK_TOUCH` -> `doesNotHaveShearsOrSilkTouch`
  - Most protected static methods were turned into instance methods
- `net.minecraft.data.loot.EntityLootSubProvider` now takes in a `HolderLookup$Provider`
- `net.minecraft.data.recipes.RecipeProvider#trapdoorBuilder` is now protected
- `net.minecraft.recipebook.ServerPlaceRecipe`
  - `addItemToSlot` now takes in an `Integer` instead of a `Iterator<Integer>`
  - `moveItemToGrid` now takes in an integer representing the minimum count and returns the number of items remaining that can be moved with the minimum count in place
- `net.minecraft.resources.RegistryDataLoader`
  - `load` is now private
  - `$RegistryData` now takes in a boolean indicating whether the data must have an element
- `net.minecraft.world.damagesource.CombatRules#getDamageAfterAbsorb` now takes in a `LivingEntity`
- `net.minecraft.world.entity.Entity#igniteForSeconds` now takes in a float
- `net.minecraft.world.entity.LivingEntity`
  - `onChangedBlock` now takes in a `ServerLevel`
  - `getExperienceReward` -> `getBaseExperienceReward`
    - `getExperienceReward` is now final and takes in the `ServerLevel` and `Entity`
  - `getRandom` -> `Entity#getRandom` (usage has not changed)
  - `dropExperience` now takes in a nullable `Entity`
  - `dropCustomDeathLoot` no longer takes in an integer
  - `jumpFromGround` is now public for testing
- `net.minecraft.world.entity.monster.AbstractSkeleton#getArrow` now keeps track of the weapon it was fired from
- `net.minecraft.world.entity.player.Player$startAutoSpinAttack` now takes in a float representing the damage and a stack representing the item being held
- `net.minecraft.world.entity.projectile.AbstractArrow` now keeps track of the weapon it was fired from
  - `setPierceLevel` is now private
- `net.minecraft.world.entity.projectile.ProjectileUtil#getMobArrow` now keeps track of the weapon it was fired from
- `net.minecraft.world.item.CrossbowItem#getChargeDuration` now takes in an `ItemStack` and `LivingEntity`
- `net.minecraft.world.item.Item`
  - `getAttackDamageBonus` now takes in an `Entity` and `DamageSource` instead of just the `Player`
  - `getUseDuration` now takes in a `LivingEntity`
- `net.minecraft.world.item.ItemStack`
  - `hurtAndBreak` now takes in a `ServerLevel` instead of a `RandomSource`
  - `hurtEnemy` now returns a boolean if the entity was succesfully hurt
- `net.minecraft.world.item.MaceItem#canSmashAttack` takes in a `LivingEntity` instead of a `Player`
- `net.minecraft.world.item.ProjectileWeaponItem#shoot` takes in a `ServerLevel` instead of a `Level`
- `net.minecraft.world.item.component.CustomData`
  - `update` now takes in a `DynamicOps<Tag>`
  - `read` now has an overload that takes in a `DynamicOps<Tag>`
- `net.minecraft.world.level.Level$ExplosionInteraction` now implements `StringRepresentable`
- `net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge$WindChargeDamageCalculator` -> `net.minecraft.world.level.SimpleExplosionDamageCalculator`
- `net.minecraft.world.level.block.ButtonBlock#press` now takes in a nullable `Player`
- `net.minecraft.world.level.block.LeverBlock#pull` now takes in a nullable `Player` and does not return anything
- `net.minecraft.world.level.storage.loot.LootContext$EntityTarget`
  - `KILLER` -> `ATTACKER`
  - `DIRECT_KILLER` -> `DIRECT_ATTACKER`
  - `KILLER_PLAYER` -> `ATTACKING_PLAYER`
- `net.minecraft.world.level.storage.loot.functions.CopyNameFunction$NameSource`
  - `KILLER` -> `ATTACKING_ENTITY`
  - `KILLER_PLAYER` -> `LAST_DAMAGE_PLAYER`
- `net.minecraft.world.level.storage.loot.parameters.LootContextParams`
  - `KILLER_ENTITY` -> `ATTACKING_ENTITY`
  - `DIRECT_KILLER_ENTITY` -> `DIRECT_ATTACKING_ENTITY`
- `net.minecraft.world.level.storage.loot.predicates.LootItemConditions#TYPED_CODEC`, `DIRECT_CODEC`, `CODEC` have been moved to `LootItemCondition`
- `net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition` -> `LootItemRandomChanceWithEnchantedBonusCondition`
- `net.minecraft.world.phys.shapes.VoxelShape#getCoords` is now public
- `net.minecraft.advancements.critereon.DamageSourcePredicate` now takes in a boolean of if the damage source is direct
- `net.minecraft.client.gui.components.SubtitleOverlay$Subtitle` is now package private
- `net.minecraft.network.protocol.game.ClientboundProjectilePowerPacket` takes in an `accelerationPower` double instead of power for each of the coordinate components
- `net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket#get*a` now provides the double representing the actual acceleration vector of the coordinate instead of the shifted integer
- `net.minecraft.world.damagesource.DamageSource#isIndirect` has been replaced with `isDirect`, which does the opposite check
- `net.minecraft.world.entity.Entity#push` now contains an overload that takes in a `Vec3`
- `net.minecraft.world.entity.LivingEntity#eat` is now final
    - A separate overload taking in the `FoodProperties` can now be overridden
- `net.minecraft.world.entity.ai.attributes.AttributeMap#getDirtyAttributes` -> `getAttributestoUpdate`
- `net.minecraft.world.entity.animal.frog.Tadpole#HITBOX_WIDTH`, `HITBOX_HEIGHT` is now final
- `net.minecraft.world.entity.npc.AbstractVillager#getOffers` now throws an error if called on the logical client
- `net.minecraft.world.entity.projectile.AbstractHurtingProjectile` constructors now take in `Vec3` to assign the directional movement of the acceleration power rather than the coordinate powers
    - `ATTACK_DEFLECTION_SCALE` -> `INITIAL_ACCELERATION_POWER`
    - `BOUNCE_DEFELECTION_SCALE` -> `DEFLECTION_SCALE`
    - `xPower`, `yPower`, `zPower` -> `accelerationPower`
- `net.minecraft.world.food.FoodData#eat(ItemStack)` -> `eat(FoodProperties)`
- `net.minecraft.world.food.FoodProperties` now takes in a stack representing what the food turns into once eaten
- `net.minecraft.world.item.CrossbowItem#getChargeDuration` no longer takes in an `ItemStack`
- `net.minecraft.world.item.ItemStack`
    - `transmuteCopy` now has an overload which takes in the current count
    - `transmuteCopyIgnoreEmpty` is now private
- `net.minecraft.world.level.ChunkPos#getChessboardDistance` now has an overload to take in the x and z coordinates without being wrapped in a `ChunkPos`
- `net.minecraft.world.level.block.LecternBlock#tryPlaceBook` now takes in a `LivingEntity` instead of an `Entity`
- `net.minecraft.world.level.block.entity.CampfireBlockEntity#placeFood` now takes in a `LivingEntity` instead of an `Entity`
- `net.minecraft.world.level.chunk.ChunkAccess#getStatus` -> `getPersistedStatus`
- `net.minecraft.world.level.chunk.ChunkGenerator#createBiomes`, `fillFromNoise` no longer takes in an `Executor`
- `net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement#addPieces` now takes in a `DimensionPadding` when determining the starting generation point
- `com.mojang.blaze3d.systems.RenderSystem`
    - `glBindBuffer(int, IntSupplier)` -> `glBindBuffer(int, int)`
    - `glBindVertexArray(Supplier<Integer>)` -> `glBindVertexArray(int)`
    - `setupOverlayColor(IntSupplier, int)` -> `setupOverlayColor(int, int)`
- `net.minecraft.advancements.critereon.EntityPredicate#location`, `steppingOnLocation` has been wrapped into a `$LocationWrapper` subclass
    - This does not affect currently generated entity predicates
- `net.minecraft.client.Options#menuBackgroundBlurriness`, `getMenuBackgroundBlurriness` now returns an integer
- `net.minecraft.client.renderer.GameRenderer#MAX_BLUR_RADIUS` is now public and an integer
- `net.minecraft.gametest.framework.GameTestHelper#getBlockEntity` now throws an exception if the block entity is missing
- `net.minecraft.network.protocol.game.ServerboundUseItemPacket` now takes in the y and x rotation of the item
- `net.minecraft.server.level.ServerLevel#addDuringCommandTeleport`, `#addDuringPortalTeleport` have been combined into `addDuringTeleport` by checking whether the entity is a `ServerPlayer`
- `net.minecraft.server.level.ServerPlayer#seenCredits` is now public
- `net.minecraft.server.players.PlayerList#respawn` now takes in an `Entity$RemovalReason`
- `net.minecraft.world.effect.OozingMobEffect#numberOfSlimesToSpawn(int, int, int)` -> `numberOfSlimesToSpawn(int, NearbySlimes, int)`
- `net.minecraft.world.entity.Entity`
    - `setOnGroundWithKnownMovement` -> `setOnGroundWithMovement`
    - `getBlockPosBelowThatAffectsMyMovement` is now public
- `net.minecraft.world.entity.EquipmentSlot` now takes in an integer represent the max count the slot can have, or 0 if unrestricted
    - Can be applied via `limit` which splits the current item stack
- `net.minecraft.world.entity.LivingEntity`
    - `dropAllDeathLoot`, `dropCustomDeathLoot` now takes in a `ServerLevel`
    - `broadcastBreakEvent` -> `onEquippedItemBroken`
    - `getEquipmentSlotForItem` is now an instance method
- `net.minecraft.world.entity.ai.attributes.AttributeMap#assignValues` -> `assignAllValues`
- `net.minecraft.world.entity.raid.Raider#applyRaidBuffs` now takes in a `ServerLevel`
- `net.minecraft.world.item.ItemStack#hurtAndBreak` now takes in a `Consumer` of the broken `Item` instead of a `Runnable`
- `net.minecraft.CrashReport`
    - `getFriendlyReport` and `saveToFile` now takes in a `ReportType` and a list of header strings
        - There is also an overload that just takes in the `ReportType`
    - `getSaveFile` now returns a `Path`
- `net.minecraft.client.gui.screens.DisconnectedScreeen` can now take in a record containing the disconnection details
    - `DisConnectionDetails` provide an optional path to the report and an optional string to the bug report link
- `net.minecraft.client.renderer.LevelRenderer#playStreamingMusic` -> `playJukeboxSong`
    - `stopJukeboxSongAndNotifyNearby` stops playing the current song
- `net.minecraft.client.resources.sounds.SimpleSoundInstance#forRecord` -> `forJukeboxSong`
- `net.minecraft.client.resources.sounds.Sound` now takes in a `ResourceLocation` instead of a `String`
- `net.minecraft.network.PacketListener`
    - `onDisconnect` now takes in `DisconnectionDetails` instead of a `Component`
    - `fillListenerSpecificCrashDetails` now takes in a `CrashReport`
- `net.minecraft.server.MinecraftServer`
    - `getServerDirectory`, `getFile` now returns a `Path`
    - `isNetherEnabled` -> `isLevelEnabled`
- `net.minecraft.world.entity.ai.behavior.AnimalPanic` now takes in a `Function<PathfinderMob, TagKey<DamageType>>` instead of a simple `Predicate`
- `net.minecraft.world.entity.ai.goal.FollowOwnerGoal` no longer takes in a boolean checking whether the entity can fly
    - These methods have been moved to `TamableAnimal`
- `net.minecraft.world.entity.ai.goal.PanicGoal` now takes in a `Function<PathfinderMob, TagKey<DamageType>>` instead of a simple `Predicate`
- `net.minecraft.world.entity.projectile.Projectile#deflect` now returns a boolean indicating whether the deflection was successful
- `net.minecraft.world.item.CreativeModeTabe#getBackgroundSuffix` -> `getBackgroundTexture`
- `net.minecraft.world.item.DyeColor#getTextureDiffuseColors` -> `getTextureDiffuseColor`
- `net.minecraft.world.level.block.entity.BeaconBlockEntity$BeaconBeamSection#getColor` now returns an integer
- `net.minecraft.world.level.storage.loot.LootDataType` no longer takes in a directory, instead it grabs that from the location of the `ResourceKey`
- `net.minecraft.client.gui.screens.Scnree#narrationEnabled` -> `updateNarratorStatus(boolean)`
- `net.minecraft.client.gui.screens.inventory.HorseInventoryScreen` takes in an integer representing the number of inventory columns to display
- `net.minecraft.client.renderer.block.model.BlockElementFace` is now a record
- `net.minecraft.client.resources.model.ModelBakery#getModel` is now package-private
- `net.minecraft.client.resources.model.ModelResourceLocation` is now a record
    - `inventory` method for items
- `net.minecraft.client.resources.model.UnbakedModel#bakeQuad` no longer takes in a `ResourceLocation`
- `net.minecraft.core.BlockMath#getUVLockTransform` no longer takes in a `Supplier<String>`
- `net.minecraft.gametest.framework.GameTestBatchFactory#toGameTestBatch` is now public
- `net.minecraft.gametest.framework.GameTestRunner` now takes in a boolean indicating whether to halt on error
- `net.minecraft.network.protocol.ProtocolInfoBuilder`
    - `serverboundProtocolUnbound` -> `serverboundProtocol`
    - `clientboundProtocolUnbound` -> `clientboundProtocol`
- `net.minecraft.network.protocol.game.ClientboundAddEntityPacket` now takes in the `ServerEntity` for the first two packet constructors
- `net.minecraft.server.MinecraftServer` now implements `ChunkIOErrorReporter`
- `net.minecraft.util.ExtraCodecs#QUATERNIONF_COMPONENTS` now normalizes the quaternion on encode
- `net.minecraft.world.entity.Entity#getAddEntityPacket` now takes in a `ServerEntity`
- `net.minecraft.world.entity.Mob` leash methods have been moved to `Leashable`
- `net.minecraft.world.entity.Saddleable#equipSaddle` now takes in the `ItemStack` being equipped
- `net.minecraft.world.entity.ai.village.poi.PoiManager` now takes in a `ChunkIOErrorReporter`
- `net.minecraft.world.level.chunk.storage.ChunkSerializer#read` now takes in a `RegionStorageInfo`
- `net.minecraft.world.level.chunk.storage.SectionStorage` now takes in a `ChunkIOErrorReporter`
- `net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece` now takes in `LiquidSettings`
- `net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement#addPieces` now takes in `LiquidSettings`
- `net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement` now takes in an `Optional<LiquidSettings>`
    - `getSettings` now takes in `LiquidSettings`
- `net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement` now takes in an `LiquidSettings`
    - `single` also has overloads that takes in `LiquidSettings`
- `net.minecraft.world.level.levelgen.structure.structures.JigsawStructure` now takes in `LiquidSettings`
- `net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings#shouldKeepLiquids` -> `shouldApplyWaterlogging`
- `net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager#getPathToGeneratedStructure`, `createPathToStructure` -> `createAndValidatePathToGeneratedStructure`
- `net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition` now takes in a float representing the chance to unenchant the item
- `net.minecraft.world.phys.shapes.CubePointRange`'s constructor is now public
- `net.minecraft.world.phys.shapes.VoxelShape`'s constructor is now protected
- `net.minecraft.client.gui.components.Checkbox` now takes in a max width which can be passed in through the builder via `Checkbox$Builder#maxWidth`
- `net.minecraft.client.gui.components.MultiLineLabel`
    - `create` methods with `FormattedText` has been removed
    - `create` methods with `List`s now take in a component varargs
    - `createFixed` -> `create`
    - `renderCentered`, `renderLeftAligned` no longer return anything
    - `renderBackgroundCentered` is removed
    - `TextAndWidth` is now a record
- `net.minecraft.commands.arguments.selector.EntitySelector#predicate` -> `contextFreePredicate`
    - Takes in a `List<Predicate<Entity>>` now
- `net.minecraft.world.level.border.WorldBorder`
    - `isWithinBounds` now has overloads for `Vec3` and four doubles representing two xz coordinates
    - `clampToBounds` now has overloads for `BlockPos` and `Vec3`
    - `clampToBounds(AABB)` is removed
- `net.minecraft.server.level.ServerPlayer#onInsideBlock` is now public, only for this overload

### Removed

- `net.minecraft.client.Minecraft`
    - `getSearchTree`, `populateSearchTree`
    - `renderOnThread`
- `net.minecraft.client.searchtree.SearchRegistry`
- `net.minecraft.entity.LivingEntity#canSpawnSoulSpeedParticle`, `spawnSoulSpeedParticle`
- `net.minecraft.world.entity.projectile.AbstractArrow`
  - `setKnockback`, `getKnockback`
  - `setShotFromCrossbow`
- `net.minecraft.world.item.ProjectileWeaponItem#hasInfiniteArrows`
- `com.mojang.blaze3d.systems.RenderSystem`
    - `initGameThread`, `isOnGameThread`
    - `assertInInitPhase`, `isInInitPhase`
    - `assertOnGameThreadOrInit`, `assertOnGameThread`
- `net.minecraft.client.gui.screens.Screen#advancePanoramaTime`
- `net.minecraft.world.entity.EquipmentSlot#byTypeAndIndex`
- `net.minecraft.world.entity.Mob#canWearBodyArmor`
    - Use `canUseSlot(EquipmentSlot.BODY)` instead
- `com.mojang.blaze3d.platform.MemoryTracker`
- `net.minecraft.server.level.ServerLevel#makeObsidianPlatform`
- `net.minecraft.world.entity.Entity#getPassengerClosestTo`
- `net.minecraft.client.resources.model.ModelBakery$ModelGroupKey`
- `net.minecraft.data.worldgen.Structures#structure`


# Minecraft 1.20.5 -> 1.20.6 Mod Mini Primer

This is a high level, non-exhaustive overview on the minor changes from 1.20.5 to 1.20.6. This does not look at any specific mod loader, just the changes to the vanilla classes.


This primer is licensed under the [Creative Commons Attribution 4.0 International](http://creativecommons.org/licenses/by/4.0/), so feel free to use it as a reference and leave a link so that other readers can consume the primer.

If there's any incorrect or missing information, please file an issue on this repository or ping @ChampionAsh5357 in the Neoforged Discord server.

## Pack Changes

There are a number of user-facing changes that are part of vanilla which are not discussed below that may be relevant to modders. You can find a list of them on [Misode's version changelog](https://misode.github.io/versions/?id=1.20.6&tab=changelog).

## Additions

- `net.minecraft.world.level.block.entity.BlockEntity#parseCustomNameSafe` - Attempts to serialize a string into a `Component`. On failure, `null` is returned


# Minecraft 1.20.4 -> 1.20.5 Mod Migration Primer

This is a high level, non-exhaustive overview on how to migrate your mod from 1.20.4 to 1.20.5. This does not look at any specific mod loader, just the changes to the vanilla classes.

This primer is licensed under the [Creative Commons Attribution 4.0 International](http://creativecommons.org/licenses/by/4.0/), so feel free to use it as a reference and leave a link so that other readers can consume the primer.

If there's any incorrect or missing information, please file an issue on this repository or ping @ChampionAsh5357 in the Neoforged Discord server.

## Pack Changes

There are a number of user-facing changes that are part of vanilla which are not discussed below that may be relevant to modders. You can find a list of them on [Misode's version changelog](https://misode.github.io/versions/?id=1.20.5&tab=changelog).

## Java 21

Minecraft now uses Java 21. You can download a copy of the JDK used here: https://learn.microsoft.com/en-us/java/openjdk/download#openjdk-21

Windows machines can install this using `winget` in PowerShell:

```pwsh
winget install Microsoft.OpenJDK.21
```

## Data Components

Data components (held within a `DataComponentMap`) are a replacement to the `CompoundTag` within an `ItemStack`. Each data component represents a key-value pair where the key is a string and the value is an object holding the data. Data components can be written to disk or synchronized across the network using codecs. All of vanilla's data components are stored within `DataComponents`, where arbitrary data that hasn't been converted to a data component is held within the `minecraft:custom_data` key.

The storage of data components has an analog to Maps, where `DataComponentMap` represents a read-only map and its implementation `PatchedDataComponentMap` represents an identity map. The data component values themselves should always be treated as **immutable** objects as both the hash code and equality are determined based on both the keys and values.

To hold data components, an object must implement `DataComponentHolder`. Currently this is only implement by `ItemStack`. The holder itself does not have any write based methods. `ItemStack`, on the other hand, does have methods to `set`, `update`, and `remove` attached components. Any operations that wish to modify a component's value should call `set` or `update`, making sure that the component value is **immutable** or at least not shallowly referenced somewhere else. Each method takes in a `DataComponentType` which represents the key of the value and the type of object being stored.

```java
// For some ItemStack 'stack' that wants a custom name
stack.set(DataComponents.CUSTOM_NAME, Component.literal("Hello world!"));

// To get the custom name
Component name = stack.get(DataComponents.CUSTOM_NAME);

// To update the stored value of the component
stack.update(DataComponents.CUSTOM_NAME, Component.literal("Default if not set"), component ->
  component.withStyle(ChatFormatting.BLUE)
);

// To remove the component
stack.remove(DataComponents.CUSTOM_NAME);
```

To create a custom data component, you must register a `DataComponentType`. These can be constructed via `DataComponentType$Builder`. The builder has two methods: `persistent(Codec)` for if you want the data component to be written to disk, and `networkSynchronized(StreamCodec)` for if you want the data component to be synchronized to the client. `StreamCodec`s are explained in more detail in the next section. The component type can also cache the currently encoded value if you are writing to disk the same value often via `cacheEncoding`. 

As an additional utility, `Item`s can have a default set of data components via `Item$Properties#component`. These values can then be updated for each individual stack.

Any methods regarding items that took in a `CompoundTag` are will now take in the direct object type. The list of available data component objects can be found within `net.minecraft.world.item.component`.

## Stream Codecs

When communicating across a network, previously, all logic needed to be manually written and read to a `FriendlyByteBuf`. However, most of this logic has now been replaced with `StreamCodec`s. A `StreamCodec` is a type of codec (which doesn't implement `Codec`) that holds a function to apply stream-based operations to encode and decode a given object. A `StreamCodec` is made up of two parts: the `StreamEncoder` and `StreamDecoder`.

The `StreamEncoder` takes in two generics, `O` representing the output to write data to, and `T` representing the data object. The `StreamDecoder` also takes in two generics, `I` representing the input to read data from, and `T` representing the constructed data object.

To create a `StreamCodec`, you will usually use one of the `composite` functions. The `composite` function takes in up to 6 pairs of `StreamCodec` containing the type to serialize and `Function`s representing the getter for the data field. The final parameter represents the constructor for the object. There are a set of default stream codecs located in `ByteBufCodecs`.

```java
// A basic stream codec for a record ExampleObject(int, Optional<String>, Holder<Item>)

// Using RegistryFriendlyByteBuf as a registry object is requested
// Otherwise, FriendlyByteBuf should be used
// If no special methods from FriendlyByteBuf are needed, use ByteBuf
public static final StreamCodec<RegistryFriendlyByteBuf, ExampleObject> STREAM_CODEC = StreamCodec.composite(
  ByteBufCodecs.INT, ExampleObject::intField,
  StreamCodecs.optional(ByteBufCodecs.STRING_UTF8), ExampleObject::optionalStringField,
  ByteBufCodecs.holderRegistry(Registries.ITEM), ExampleObject::holderItemField,
  ExampleObject::new
);
```

As many packets have not been updated to use a codec-based approached, they can use `Packet#codec` to turn a read/write implementation into a codec. This method takes in a `StreamMemberEncoder` instead of a `StreamEncoder` as write methods are usually instance methods on the packet class.

```java
// For some ExamplePacket with ExamplePacket(RegistryFriendlyByteBuf) and ExamplePacket#write(RegistryFriendlyByteBuf)

public static final StreamCodec<RegistryFriendlyByteBuf, ExamplePacket> STREAM_CODEC = Packet.codec(
  ExamplePacket::write, ExamplePacket::new
);
```

If read and write methods have suddently disppeared, or the network handling has been moved, it is most likely handled by a `StreamCodec` directly within the object class or in a package within `net.minecraft.network.codec`. Packets themselves do not contain the `StreamCodec` and instead are directly registered via `ProtocolInfoBuilder#addPacket`. `Packet`s now contain a `PacketType` with the name of the packet and the direction the packet is sent.

Here are some of the classes that uses stream codecs for their implementation logic:

- `net.minecraft.core.particles.ParticleType#getDeserializer` -> `streamCodec`
- `net.minecraft.network.syncher.EntityDataSerializer`
- `net.minecraft.world.item.crafting.RecipeSerializer#fromNetwork`, `toNetwork` -> `streamCodec`
- `net.minecraft.world.level.gameevent.PositionSourceType#read`, `write` -> `streamCodec`

## Sub Predicate Types

Entities and items now have sub predicates that can be applied for specific entities and items. Entities store this within the `type_specific` json object in the `EntityPredicate`. Items store this within the `predicates` json object in the `ItemPredicate`. Each of these are registered to their own built in registry, requiring some codec object.

- `net.minecraft.advancements.critereon.EntityVariantPredicate` -> `EntitySubPredicates$EntityVariantPredicateType`
  - All variant predicates are within `EntitySubPredicates`

## Don't you like Holders?

Most methods now take in a `Holder` of a registry object rather than the registry object directly. As such, direct references should not be used.

- `net.minecraft.client.renderer.FogRenderer$MobEffectFogFunction#getMobEffect` returns a `Holder<MobEffect>`
- `net.minecraft.gametest.framework.GameTestHelper` now takes in a `Holder<MobEffect>`
- `net.minecraft.network.chat.ChatType$Bound` now takes in a `Holder<ChatType>`
- `net.minecraft.world.effect.MobEffect#addAttributeModifier` now takes in a `Holder<Attribute>`
- `net.minecraft.world.effect.MobEffectInstance` now takes in a `Holder<MobEffect>`
- `net.minecraft.world.entity.Entity#gameEvent` now takes in a `Holder<GameEvent>`
- `net.minecraft.world.item.ArmorItem` now takes in a `Holder<ArmorMaterial>`
- `net.minecraft.world.level.LevelAccessor#gameEvent` now takes in a `Holder<GameEvent>`
- `net.minecraft.world.level.gameevent.GameEventListener#handleGameEvent` now takes in a `Holder<GameEvent>`
- `net.minecraft.world.level.gameevent.GameEventListenerRegistry#visitInRangeListeners` now takes in a `Holder<GameEvent>`

## ExtraCodecs to DataFixerUpper

Many of the methods that were commonly used in `ExtraCodecs` has been migrated or combined in some other appropriate place within `Codec` or `DataResult`. Most of the names are synonymous, so they just need to be replaced with the correct class.

- `net.minecraft.Util#getOrThrow`, `getPartialOrThrow` -> `com.mojang.serialization.DataResult#getOrThrow`, `getPartialOrThrow`
- `net.minecraft.util.ExtraCodecs
  - `withAlternative` -> `com.mojang.serialization.Codec#withAlternative`
  - `validate` -> `com.mojang.serialization.Codec#validate`
  - `strictOptionalField` -> `com.mojang.serialization.Codec#optionalFieldOf` as the method is now strict by default
    - `optionalFieldOf` previously has now been replaced by `lenientOptionalFieldOf`
  - `xor` -> `Codec#xor`
  - `either` -> `Codec#either`
  - `stringResolverCodec` -> `Codec#stringResolver`
  - `recursive` -> `Codec#recursive`
  - `lazyInitializedCodec` -> `Codec#lazyInitialized`
  - `sizeLimitedString` -> `Codec#sizeLimitedString`

## Codec Replacements

A lot of read/write methods have been replaced with Codec equivalents.

- `net.minecraft.nbt.NbtUtils#readGameProfile`, `#writeGameProfile` -> `ExtraCodecs#GAME_PROFILE`
- `net.minecraft.network.FriendlyByteBuf`
  - `writeId`, `readById` -> `Registry#getId`, `byId` and `FriendlyByteBuf#writeVarInt`
  - `readCollection`, `writeCollection`, `readList`, `readMap`, `writeMap`, `writeOptional`, `readOptional`, `writeNullable`, `readNullable` take in decoders and encoders for streams
  - `writeEither`, `readEither` -> `Codec#either`
  - `readComponent`, `readComponentTrusted`, `writeComponent` -> `ComponentSerialization#STREAM_CODEC`, `TRUSTED_STREAM_CODEC`
  - `writeItem`, `readItem` -> `ItemStack#OPTIONAL_STREAM_CODEC`
  - `readGameProfile`, `writeGameProfile` ->  `ExtraCodecs#GAME_PROFILE`
  - `readGameProfileProperties`, `writeGameProfileProperties` -> `ExtraCodecs#PROPERTY_MAP`''
  - `readProperty`, `writeProperty` -> `ExtraCodecs#PROPERTY`

## Removed Redundant PoseStacks

Most methods that passed around the raw `PoseStack` has had the parameter removed. Now, no `PoseStack`, or only the relevant `Pose` or `Matrix4f`, is passed.

- `net.minecraft.client.renderer.GameRenderer#renderLevel` no longer takes in a `PoseStack`
- `net.minecraft.client.renderer.LevelRenderer`
  - `prepareCullFrustum` no longer takes in a `PoseStack` and only takes in the `Matrix4f` that is being operated on
  - `renderLevel` no longer takes in a `PoseStack`
  - `renderSectionLayer` no longer takes in a `PoseStack`
  - `renderSky` no longer takes in a `PoseStack` and only takes in the `Matrix4f` that is being operated on

### No more Matrix4f in Lighting Setup

`Matrix4f` is no longer passed into the shader uniforms when setting up level lighting.

- `com.mojang.blaze3d.platform.Lighting#setupForEntityInInventory` - Setup light direction uniforms for entity displayed in inventory
- `net.minecraft.client.particle.ParticleEngine#render(PoseStack, MultiBufferSource$BufferSource, LightTexture, Camera, float)` -> `render(LightTexture, Camera, float)`

## ItemInteractionResult

There is now a separation when it comes to interacting with an item compared interacting with anything else. When interacting where an item is supposed to be called, methods will now return an `ItemInteractionResult`. An `ItemInteractionResult` can be mapped to an `InteractionResult`. The methods below now return an `ItemInteractionResult`.

- `net.minecraft.core.cauldron.CauldronInteraction`
  - `interact`
  - `fillBucket`
  - `emptyBucket`

## Enchantments, now with Definitions

`Enchantment`s have been overhauled to now hold a single record known as an `EnchantmentDefinition`. This record contains tags for the supported items this enchantment can be applied to, the items it would be considered a primary enchantment, the weight of obtaining the enchantment, the max level the enchantment can be, the minimum and maximum cost of the enchantment, the cost to repair in an anvil, the feature flags, and the slots the enchantment can be applied to.

These values can be created using `Enchantment#definition`, while the cost can be computed either via `constantCost` for every level, or `dynamicCost` for an increasing cost per level. Enchantments still need to be registered like any other built-in registry.

For the tags, there are `minecraft:enchantable/*` tags which refer to a list of items that can be applied with that enchant. For example, `minecraft:enchantable/sword` contains all swords. If one of these tags do not match your criteria, you can create a separate enchantable tag for specifically your enchantment. It is recommended to add other items via other enchantable tag groups first. This replaces `EnchantmentCategory`.

`EnchantmentHelper` methods have been replaced to use the `ItemEnchantment`s data component object instead of a raw `CompoundTag`.

- `getRarity` -> `getWeight`
- `getAnvilCost` - The minimum cost needed to repair in an anvil
- `getDamageBonus(int, MobType)` -> `getDamageBonus(int, EntityType<?>)`
- `doPostItemStackHurt` - Executes when an item with this enchantment damages an entity

## Blocks: From public to protected

Many of the methods within `Block` are now protected, to prevent direct access except through the `BlockState`, or has had some changes to its logic. The following methods are now protected:

- `updateIndirectNeighbourShapes`
- `isPathfindable`
- `updateShape`
- `skipRendering`
- `neighborChanged`
- `onPlace`
- `onRemove`
- `onExplosionHit`
- `useWithoutItem`
- `useItemOn`
- `triggerEvent`
- `getRenderShape`
- `useShapeForLightOcclusion`
- `isSignalSource`
- `getFluidState`
- `hasAnalogOutputSignal`
- `getMaxHorizontalOffset`
- `getMaxVerticalOffset`
- `rotate`
- `mirror`
- `canBeReplaced`
- `getDrops`
- `getSeed`
- `getOcclusionShape`
- `getBlockSupportShape`
- `getInteractionShape`
- `getLightBlock`
- `getMenuProvider`
- `canSurvive`
- `getShadeBrightness`
- `getAnalogOutputSignal`
- `getShape`
- `getCollisionShape`
- `isCollisionShapeFullBlock`
- `isOcclusionShapeFullBlock`
- `getVisualShape`
- `randomTick`
- `tick`
- `getDestroyProgress`
- `spawnAfterBreak`
- `attack`
- `getSignal`
- `entityInside`
- `getDirectSignal`
- `onProjectileHit`
- `propagatesSkylightDown`
- `isRandomlyTicking`
- `getSoundType`

Here are some other changes:

- `isPathfindable(BlockState, BlockGetter, BlockPos, PathComputationType)` -> `isPathfindable(BlockState, PathComputationType)`
- `use` -> `useWithoutItem`
- `BlockStateBase$neighborChanged` -> `handleNeighborChanged`
- `net.minecraft.world.level.block.Block`
  - `isRandomlyTicking` -> `BlockBehaviour#isRandomlyTicking`
  - `propagatesSkylightDown` -> `BlockBehaviour#propagatesSkylightDown`
  - `getSoundType` -> `BlockBehaviour#getSoundType`

## ItemStack Max Stack Size 99

The hard limit has been raised from 64 to 99 for `ItemStack`s.

## No more magicalSpecialHackyFocus

`net.minecraft.client.gui.components.events.ContainerEventHandler#magicalSpecialHackyFocus` has been removed. It did nothing but set the focused element.

## Bootstap? No! It's Bootstrap!

`net.minecraft.data.worldgen.BootstapContext` has finally been renamed to `BootstrapContext`!

## Minor Migrations

The following is a list of useful or interesting additions, changes, and removals that do not deserve their own section in the primer.

### StringUtil

Most string transformation utility methods have been moved to `net.minecraft.util.StringUtil` with the same name

- `net.minecraft.Util#isBlank`, `isWhitespace`
- `net.minecraft.SharedConstants#isAllowedChatCharacter`, `filterText`

### Advancement Network Codecs

All network methods for advancements (`read`, `write`) have been made private or removed. They are replaced by `StreamCodec`s taking in a `RegistryFriendlyByteBuf`.

### Collection Criteria Predicates

The following predicates have been added for handling criteria triggers:

- `CollectionContentsPredicate` -> Returns true if all contents of a collection match the given predicate(s)
- `CollectionCountsPredicate` -> Returns true if the number of contents that match the given predicate(s) is within the specified bounds
- `CollectionPredicate` -> Returns true depending on the above predicates and whether the size of the collection matches the size specified

### Entity Slot Criteria

A new field called `slots` on the `EntityPredicate` can now check the entity's inventory for a match.

### Recipe Book Categories throw on default

Recipe book methods that attempt to get a `RecipeBookCategories` will now throw a `MatchException` if no corresponding category exists.

- `net.minecraft.client.ClientRecipeBook#getCategory`
- `net.minecraft.client.RecipeBookCategories#getCategories`

### Gui Breakup

Most logic within the `Gui` class has been separated to private methods. Additionally, some fields have been removed in favor of using their `GuiGraphics` counterparts.

- `renderEffects`, `renderJumpMeter`, `renderExperienceBar`, `renderSelectedItemName`, `renderDemoOverlay` is now private
- `renderSavingIndicator` is now public and takes in a float representing the tick rate
- `screenWidth`, `screenHeight` -> `GuiGraphics#guiWidth`, `GuiGraphics#guiHeight`
- `itemRenderer` has been completely removed

### Map Decoration Textures

Map decoration textures are now managed by an atlas that is stiched together. Because of this, the map id is now stored within a record `MapId` holding the current id of the map to render as a texture. `MapRenderer` uses this `MapId` instead of a integer.

To access the texture manager, call `Minecraft#getMapDecorationTextures`.

- `net.minecraft.client.multiplayer.ClientLevel`
  - `getMapData(String)` -> `getMapData(MapId)`
  - `overrideMapData(String, MapItemSavedData)` -> `overrideMapData(MapId, MapItemSavedData)`
  - `getAllMapData` returns a `Map<MapId, MapItemSavedData>`
  - `addMapData(Map<String, MapItemSavedData>)` -> `addMapData(Map<MapId, MapItemSavedData>)`
- `net.minecraft.world.level.Level`
  - `setMapData(String, MapItemSavedData)` -> `setMapData(MapId, MapItemSavedData)`
  - `getFreeMapId` returns a `MapId`

### Font Options

Font providers now have variant filters. There are currently two available: uniform and japanese variants. These filters determine whether the variant should be used. Most font cosntruction methods allows a filter or a font option to be passed in.

- `com.mojang.blaze3d.font.GlyphProvider$Conditional` - Filters whether a glyph provider should be added to the list of available providers when loading a font option
- `net.minecraft.client.Options#japaneseGlyphVariants` - A new option that, when enabled, will use different variants of Japanese glyphs
- `net.minecraft.client.gui.font.FontManager#updateOptions` - Reloads the available font options to choose from
- `net.minecraft.client.Minecraft#selectMainFont` -> `updateFontOptions`
  - This is not one-to-one as the fonts are now options in the menu
- `net.minecraft.client.gui.font.FontSet#reload` takes in a conditional holding the filters for a glyph provider and the set of font options to reload

### Screen Backgrounds

`Screen#renderBackground` has been broken into three steps: `renderPanorama` if the level is null, `renderBlurredBackground` which processes the blur effect to apply to the background, and `renderMenuBackground` which rends the background texture to the screen. There is also an additional method for rendering a transparent background called `renderTransparentBackground` which is called when rendering the background in `AbstractContainerScreen`.

`renderDirtBackground` -> `renderMenuBackground`

### Holder Lookups in Data Providers

Some methods in data providers now take in a `HolderLookup$Provider` to get any relevant registry objects that are not explicitly accessible or built in.

- `net.minecraft.data.recipes.RecipeProvider` now takes in a `CompletableFuture<HolderLookup.Provider>`
  - `buildAdvancement` now takes in a `HolderLookup$Provider`

### ResourceKey References

Many references to `ResourceLocation`s now take in an associated `ResourceKey` instead with the generic tied to the registry object in question.

- `net.minecraft.data.loot.BlockLootSubProvider` now takes in a `Map<ResourceKey<LootTable>, LootTable.Builder>` instead of a `Map<ResourceLocation, LootTable.Builder>`
- `net.minecraft.data.loot.LootTableProvider(PackOutput, Set<ResourceLocation>, List<LootTableProvider.SubProviderEntry>)` -> `LootTableProvider(PackOutput, Set<ResourceKey<LootTable>>, List<LootTableProvider.SubProviderEntry>, CompletableFuture<HolderLookup.Provider>)
- `net.minecraft.data.loot.LootTableSubProvider#generate(BiConsumer<ResourceLocation, LootTable.Builder>)` -> `generate(HolderLookup.Provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder>)`

### Static methods in FriendlyByteBuf

Static methods for writing and reading data have been added to `FriendlyByteBuf` which takes in the same parameters of the instance method, along with the `ByteBuf` to write to.

### Loot Registries

Loot data are now handled through dynamic registries, specifically ones that can be reloaded by the `/reload` command. This means that any previous references, such as `LootDataManager`, no longer exist. They can be queried via `reloadableRegistries` in `MinecraftServer` or `Level`

### PackLocationInfo

Pack information stored within resources are now stored within a `PackLocationInfo` object. Parameters such as `name` or `isBuiltIn` is stored directly on this object (e.g. `id` and `knownPackInfo`, respectively). All classes within `net.minecraft.server.packs` have been updated to accomdate this change.

A `KnownPack` is simply a synced key and version indicating where the resource orginated from. As such, both the server and client must have the same contents as the `KnownPack` is used to avoid syncing the entire pack when unnecessary.

### DispatchCodecs now MapCodecs

`Codec#dispatch` now takes in a `MapCodec` instead of a `Codec`. All other codecs used for dispatch have been updated to a `MapCodec`:

- `net.minecraft.core.particles.ParticleType#codec`
- `net.minecraft.client.renderer.texture.atlas.SpriteSources#register`
- `net.minecraft.client.renderer.texture.atlas.SpriteSourceType`
- `net.minecraft.util.valueproviders.FloatProviderType#codec`
- `net.minecraft.util.valueproviders.IntProviderType#codec`
- `net.minecraft.world.item.crafting.RecipeSerializer#codec`
- `net.minecraft.world.level.biome.BiomeSource#codec`
- `net.minecraft.world.level.chunk.ChunkGenerator#codec`
- `net.minecraft.world.level.gameevent.PositionSourceType#codec`
- `net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType#codec`
- `net.minecraft.world.level.levelgen.carver.WorldCarver#configuredCodec`
- `net.minecraft.world.level.levelgen.feature.Feature#configuredCodec`
- `net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType#codec`
- `net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType#codec`
- `net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType#codec`
- `net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType#codec`
- `net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType#codec`
- `net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType#codec`
- `net.minecraft.world.level.levelgen.heightproviders.HeightProviderType#codec`
- `net.minecraft.world.level.levelgen.placement.PlacementModifierType#codec`
- `net.minecraft.world.level.levelgen.structure.Structure#simpleCodec`
- `net.minecraft.world.level.levelgen.structure.StructureType#codec`
- `net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType#codec`
- `net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType#codec`
- `net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding#codec`
- `net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType#codec`
- `net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType#codec`
- `net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType#codec`
- `net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType#codec`
- `net.minecraft.world.level.storage.loot.entries.CompositeEntryBase#createCodec`
- `net.minecraft.world.level.storage.loot.entries.LootPoolEntryType`
- `net.minecraft.world.level.storage.loot.functions.LootItemFunctionType`
- `net.minecraft.world.level.storage.loot.predicates.LootItemConditionType`
- `net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType`
- `net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType`
- `net.minecraft.world.level.storage.loot.providers.score.LootScoreProviderType`

### Packrat Parser

The [pakrat parser](https://en.wikipedia.org/wiki/Packrat_parser) has been added to `net.minecraft.util.parsing.packrat` to read and parse strings from commands. However, this implementation can be used generically by creating your own grammar.

### Entity Attachments

An `EntityAttachment` is a definition of where a given point lies the entity. For example, a name tag could be directly on top of an entity or underneath it. Each `EntityAttachment` contains a fallback of where the point should go if there is none set. Multiple points can be registered for a given `EntityAttachment`.

`EntityAttachment`s are added from the `EntityType$Builder` using one of the `*Attachments`, `*Offset`, or `attach` methods. They can then be accessed from the entity using `#getAttachments`. To get an attachment vector value, specify the attachment, index, and y rotation, calling `EntityAttachments#getNullable` if you want null to be returned, or `get` if you want an error to be thrown.

### Dyeables

Dyable armor is now a setting on the `ArmorMaterial$Layer` and an item tag `minecraft:dyeable` rather than a completely separate class. When true, this will tint the armor texture provided. The `ItemStack` must have a `DYED_COLOR` data component to read the tint color from. The tag allows this component to be added through the armor dyeing crafting table recipe.

### Potion Brewing

`PotionBrewing` is now an instance class on the `MinecraftServer` that is passed through the `Level`. This means all static methods are now instance methods.

### TooltipProvider

Tooltips for a particular object stored on an item (usually for `DataComponent`s) are now implemented via `TooltipProvider`. This interface has one method which takes in the context of the item tooltip, a consumer to supply the tooltip contents to, and the currently set tooltip flags. Calling this is usualy within `Item#appendHoverText`:

```java
@Override
public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tooltips, TooltipFlag flags) {
  // Get some TooltipProvider implementation provider
  provider.addToTooltip(ctx, tooltips::add, flags);
}
```

### New Criteria Triggers

- `BAD_OMEN` -> `RAID_OMEN`
- `DEFAULT_BLOCK_USE` (`minecraft:default_block_use`)
- `ANY_BLOCK_USE` (`minecraft:any_block_use`)
- `CRAFTER_RECIPE_CRAFTED` (`minecraft:crafter_recipe_crafted`)
- `FALL_AFTER_EXPLOSION` (`minecraft:fall_after_explosion`)

### New Loot Context Parameters

- `EQUIPMENT` -> `ORIGIN`, `THIS_ENTITY`
- `VAULT` -> `ORIGIN`, `THIS_ENTITY`?
- `BLOCK_USE` -> `ORIGIN`, `THIS_ENTITY`, `BLOCK_STATE`
- `SHEARING` -> `ORIGIN`, `THIS_ENTITY`?

### Additions

- `com.mojang.blaze3d.vertex.PoseStack$Pose`
  - `transformNormal` - Applies a transform to the normal within the given pose
  - `copy` - Makes a deep copy of the current pose. Does not add to stack
- `com.mojang.math.MatrixUtil`
  - `isPureTranslation` - Returns true if the matrix has only been translated from the identity
  - `isOrthonormal` - Returns true if the upper left 3x3 submatrix is orthogonal
- `net.minecraft.Util`
  - `toMutableList` - Creates a collector for a mutable list
  - `getRegisteredName` - Gets the registry name of an object, or `[unregistered]` if not registered
  - `allOf` - Combines all predicates into a single predicate using ANDs
  - `copyAndAdd` - Creates a new immuatble list with the added element
  - `copyAndPut` - Creates a new immutable map with the added element
- `net.minecraft.client.GuiMessage#icon` - Returns the icon for the message tag when present, otherwise null
- `net.minecraft.client.Minecraft#disconnect(Screen, boolean)` where the boolean, when true, will not clear downloaded resource packs
- `net.minecraft.client.MouseHandler#handleAccumulatedMovement` - Handles the movement of the mouse when running the tick
- `net.minecraft.client.OptionInstance#createButton(Options)` - Creates a button at (0,0) with a width of 150
- `net.minecraft.client.Options`
  - `menubackgroundBlurriness` - A new option to determine how blurry the background of a menu should be
- `net.minecraft.client.gui.GuiGraphics`
  - `containsPointInScissor` - Returns true if the given point is within the top entry of the scissor stack
  - `fillRenderType` - Creates a filled quad for the given `RenderType`
- `net.minecraft.client.LayeredDraw` - A class that renders objects on top of each other. The distance between each layer is translated 200 units in the Z direction to allow proper stacking
- `net.minecraft.client.gui.components.AbstractSelectionList`
  - `updateSize` - Updates the size of the list to display
  - `updateSizeAndPosition` - Updates the size and position of the list to display
  - `getDefaultScrollbarPosition` - Gets the x position where the scrollbar would normally render
  - `getListOutlinePadding` - Gets the padding to offset the x position of the scrollbar
  - `getRealRowLeft` - Gets the actual left position of the list
  - `getRealRowRight` - Gets the actual right position of the list
- `net.minecraft.client.gui.components.ChatComponent`
  - `storeState` - Returns the current state of the chat messages stored in the component
  - `restoreState` - Sets the current state of the chat messages to a previous setup
- `net.minecraft.client.gui.components.CycleButton#create(Component, CycleButton$OnValueChange)` - Constructs a new button at (0,0) of size (150,20)
- `net.minecraft.client.gui.components.DebugScreenOverlay
  - `showFpsCharts` - When true, renders the FPS chart
  - `logRemoteSample` - Logs a full sample for the specified sample type
- `net.minecraft.client.gui.components.FocusableTextWidget#containWithin` - Sets the max width to contain the specified size excluding padding
- `net.minecraft.client.gui.components.TabButton$renderMenuBackground` - Renders the background of the screen
- `net.minecraft.client.gui.components.debugchart.AbstractDebugChart`
  - `drawDimensions` - Draws the sample and additional information
  - `drawMainDimension` - Draws the sample to the display
  - `drawAdditionalDimensions` - Draws additional information about the sample
  - `getValueForAggregation` - Reads the data within the sample storage
- `net.minecraft.client.gui.components.toasts.SystemToast`
  - `onLowDiskSpace` - Creates a system toast that notifies the disk space needed to write the chunk is low
  - `onChunkLoadFailure` - Creates a system toast that notifies the chunk has failed to load
  - `onChunkSaveFailure` - Creates a system toast that notifies the chunk has failed to save
- `net.minecraft.client.gui.font.FontSet#name` - the name of the font set
- `net.minecraft.client.gui.font.providers.FreeTypeUtil` - A utility for interactions the Freetype Font API
- `net.minecraft.client.gui.layouts.HeaderAndFooterLayout
  - `getContentHeight` - Gets the y position of the content
  - `addTitleHeader` - Adds a string widget to the header
- `net.minecraft.client.gui.navigation.ScreenRectangle#containsPoint` - Checks whether the point is within the current rectangle
- `net.minecraft.client.gui.screens.Screen#setInitialFocus` - This method should be overridden to set the initial focused widget on screen
- `net.minecraft.client.multiplayer.ClientPacketListener`
  - `scoreboard` - Gets the sent scoreboard from the server
  - `potionBrewing` - Gets the sent brewing information from the server
- `net.minecraft.client.multiplayer.KnownPacksManager` - A class that holds the known packs on the client from the server
- `net.minecraft.client.multiplayer.RegistryDataCollector` - A collector which holds the contents and tags of the available registries
- `net.minecraft.client.multiplayer.TagCollector` - A collector which holds the tags of the available registries
- `net.minecraft.client.multiplayer.ServerData`
  - `state` - Gets the current state of the server data
  - `setState` - Sets the current state of the server data
- `net.minecraft.client.particle.Particle$LifetimeAlpha` - A particle utility for handing animated alpha over the particle's lifetime using linear interpolation
- `net.minecraft.client.renderer.GameRenderer`
  - `loadBlurEffect` - Loads the effect of the blur post processor (private)
  - `processBlurEffect` - Processes the blur effect, applies the 'Radius' uniform using the menu blackground blurriness option
  - `getRendertypeCloudsShader` - Gets the shader for the clouds render type
- `net.minecraft.client.renderer.entity.EntityRenderer#getShadowRadius` - Returns the radius of the entity's shadow
- `net.minecraft.client.renderer.entity.layers.WolfArmorLayer` - Render layer for wolf armor
- `net.minecraft.client.sounds.ChunkedSampleByteBuf` - A byte buffer chunked into multiple byte buffers
- `net.minecraft.commands.arguments.ResourceOrIdArgument` - An argument that operates on a resource or registry object identifier
- `net.minecraft.commands.arguments.SlotsArgument` - An argument that operates on a specific slot within a slot range
- `net.minecraft.commands.functions.CommandFunction#checkCommandLineLength` - Commands with over 2 million characters will throw an error
- `net.minecraft.core.BlockBox` - A rectangular prism as defined by two block positions
- `net.minecraft.core.BlockPos`
  - `min` - Takes the minimum coordinates between two block positions and creates a new one
  - `max` - Takes the maximum coordinates between two block positions and createa a new one
- `net.minecraft.core.Direction#getNearest` - Gets the direction closest to the normalized vector
- `net.minecraft.core.Direction$Plane#length` - Gets the number of faces in a given plane
- `net.minecraft.core.Holder`
  - `is(Holder)` - Checks if two holders are equivalent, this method is deprecated
  - `getRegisteredName` - Gets the registry object's identifer
- `net.minecraft.core.HolderGetter$Provider#get` - Gets a refence holder to a registry object from the registry key and resource key
- `net.minecraft.core.HolderLookup#createSerializationContext` - Creates a registry ops for the current registries provider
- `net.minecraft.core.HolderSet#empty` - Gets an empty holder set
- `net.minecraft.core.IdMap#getIdOrThrow` - Gets the id of a registry object or throws an error if not present
- `net.minecraft.core.RegistrationInfo` - Holds information related to a specific registry object in a registry (the pack it came from and the lifecycle of the object)
- `net.minecraft.core.Registry`
  - `getHolder(ResourceLocation)` - Gets the object holder from its registry id
  - `getRandomElementOf` - Gets a random element from the registry filtered by a tag
- `net.minecraft.core.RegistrySynchronization$PackedRegistryEntry` - Holds the data for a given registry entry
- `net.minecraft.data.tags.TagsProvider$TagAppender#addAll` - Adds a list of resource keys to the tag
- `net.minecraft.gametest.framework.GameTest`
  - `skyAccess` - When false, when preparing a test structure, this will spawn barrier blocks on top of the structure
  - `manualOnly` - When true, the test can only be manually triggered and not run as part of all available tests
- `net.minecraft.gametest.framework.GameTestHelper`
  - `spawnItem(Item, Vec3)` - Spawns an item at the specified relative vector
  - `findOneEntity` - Finds the first entity that matches the type
  - `findClosestEntity` - Finds the closest entity to the specifid (x,y,z) within a given radius
  - `findEntities` - Finds all entities within the given radius centered at (x,y,z)
  - `moveTo` - Moves a mob to the specified position using the `Mob#moveTo` method
  - `getEntities` - Get all entities of a given type within the test bounds
  - `assertValueEqual` - Asserts that two objects are equivalent
  - `assertEntityNotPresent` - Asserts that an entity is not present between the given vectors
- `net.minecraft.gametest.framework.GameTestListener#testAddedForRerun` - A listener method that is called if the test needs to be rerun
- `net.minecraft.gametest.framework.GameTestRunner$Builder` - A builder for constructing a runner to run the game tests
- `net.minecraft.network.RegistryFriendlyByteBuf` - A byte buffer which holds a `RegistryAccess`
- `net.minecraft.resources.RegistryDataLoader#load` - Methods have been added to add a `LoadingFunction` which handles how the data in the registry is loaded
- `net.minecraft.resources.RegistryOps`
  - `injectRegistryContext` - Wraps a `Dynamic` with some ops into a `Dynamic` with a `RegistryOps`
  - `withParent` - Creates a `RegistryOps` with the passed in `DynamicOps` as a delegate
- `net.minecraft.resources.ResouceLocation#readNonEmpty` - Reads a `ResourceLocation`, throwing an error if the reader buffer is empty
- `net.minecraft.server.MinecraftServer`
  - `reloadableRegistries` - Holds an accessor to the dynamic registries on the server
  - `subscribeToDebugSample` - Registers a player to receive debug samples
  - `acceptsTransfers` - Determines whether transfer packets can be sent to the server
  - `reportChunkLoadFailure` - Reports a chunk has failed to load
  - `reportChunkSaveFailure` - Reports a chunk has failed to save
- `net.minecraft.server.ReloadableServerRegistries` - A class which holds registries that can be reloaded while in game (e.g., loot tables)
- `net.minecraft.server.level.ServerLevel#getPathTypeCache` - Gets the cache of `PathType`s when computed by an entity
- `net.minecraft.server.level.ServerPlayer
  - `setSpawnExtraParticlesOnFall` - Sets a boolean to spawn more particles when landing from a fall
  - `setRaidOmenPosition`, `clearRaidOmenPosition`, `getRaidOmenPosition` - Handles creating a raid from the given position
- `net.minecraft.util.ListAndDeque` - An interface which defines an object as a list and deque that can be randomly accessed
  - `ArrayListDeque` now implements this interface
- `net.minecraft.util.ExtraCodecs`
  - `sizeLimitedMap` - Makes sure the map is no larger than the specified size
  - `optionalEmptyMap` - Returns an optional map
- `net.minecraft.util.FastColor`
  - `as8BitChannel` - Gets an 8-bit color value from a float between 0-1
  - `color(int,int,int)` - Creates an opaque 32-bit color value from RGB
  - `opaque` -> Sets the alpha of a color to 255
  - `color(int,int)` - Creates a color with the R channel separated from the GB
  - `colorFromFloat` - Creates a 32-bit color value from floats between 0-1
- `net.minecraft.util.Mth#mulAndTruncate` - Multiplies a fraction by some value. Since integer division is used, the resulting division is floored
- `net.minecraft.util.NullOps` - An intermediary that only represents a null value
- `net.minecraft.util.ParticleUtils`
  - `spawnParticleInBlock` - Spawns particles within a block
  - `spawnParticles` - Spawns particles at the given position with some offset
- `net.minecraft.util.profiling.jfr.JvmProfiler#onRegionFile(Read|Write)` - Handles logic when the region file for a given world has been read from or written to
- `net.minecraft.world.Container#getMaxStackSize(ItemStack)` - Gets the max stack size for an `ItemStack` by getting the minimum of the container max stack size and the stack's max stack size
- `net.minecraft.world.InteractionResult#SUCCESS_NO_ITEM_USED` - The interaction was successful but the context entity did not use an item
- `net.minecraft.world.effect.MobEffect`
  - `onEffectAdded` - Called when the effect is first added to the entity
  - `onMobRemoved` - Called when the entity has been removed from the level when the effect is applied
  - `onMobHurt` - Called when the entity is hurt when the effect is applied
  - `createParticleOptions` - Creates the particle options to spawn around the entity
  - `withSoundOnAdded` - Sets the sound when the effect is added to an entity
- `net.minecraft.world.effect.MobEffectInstance`
  - `is` - Returns true if the effect holders are equal
  - `skipBlending` - Tells the effect not to blend with other environmental renderers (e.g. fog)
- `net.minecraft.world.entity.AnimationState#fastForward` - Speeds up the animation based upon the animation duration and a scale amount between 0-1
- `net.minecraft.world.entity.Crackiness` - Handles the state of how much armor has been damaged, or 'cracked'
- `net.minecraft.world.entity.Entity`
  - `getDefaultGravity` - Gets the default gravity value to apply to the entity
  - `getGravity` - Gets the gravity to apply to the entity, or if there is no gravity 0
  - `applyGravity` - Applies gravity to the delta movement of the entity.
  - `getNearestViewDirection` - Gets the direction nearest to the current view vector
  - `getDefaultPassengerAttachmentPoint` - Determines the default attachment point based upon the entity's current dimensions
  - `deflection` - Determines how an entity interacts with this projectile
  - `getPassengerClosestTo` - Gets the passenger closest to the given vector
  - `onExplosionHit` - Handles when the entity is hit with an explosion
  - `registryAccess` - Gets the current registry access
- `net.minecraft.world.entity.EntityType$Builder#spawnDimensionsScale` - Sets the scale factor to apply to the entity's dimensions when attempting to spawn
- `net.minecraft.world.entity.EquipmentSlot#BODY`
- `net.minecraft.world.entity.EquipmentSlotGroup` - Indicates a grouping of `EquipmentSlot`s
- `net.minecraft.world.entity.EquipmentTable` - Indicates the drop chances of a given `EquipmentSlot`
- `net.minecraft.world.entity.EquipmentUser` - Indicates that the entity can wear equipment
  - All `Mob`s and `ArmorStand`s are equipment users
- `net.minecraft.world.entity.LivingEntity`
  - `getComfortableFallDistance` - Increases the fall distance that the entity can survive without taking damage by three blocks
  - `doHurtEquipment` - Handles when a piece of equipment should be damaged
  - `canUseSlot` - Whether a slot can be used on an entity
  - `getJumpPower(float)` - Gets the power of a jump from the attribute, scaling by a float and the block jump factor and adding the jump boost power
  - `getDefaultDimensions` - Gets the base dimensions for a given pose
  - `getSlotForHand` - Gets the `EquipmentSlot` for a given `InteractionHand`
  - `hasInfiniteMaterials` - Whether the entity has an infinite amount of materials in its inventory. This is currently only used by the `Player`
- `net.minecraft.world.entity.Mob`
  - `getTargetFromBrain` - Gets the attack target of the entity, or null if none is available
  - `stopInPlace` - Stops all navigation and entity movement
  - `clampHeadRotationToBody` - Keeps the head movement within the bounds of the body
  - `getBodyArmorItem`, `canWearBodyArmor`, `isWearingBodyArmor`, `isBodyArmorItem`, `setBodyArmorItem` - Logic for handling armor in the `BODY` slot
  - `mayBeLeashed` - If the entity can be leashed
- `net.minecraft.world.entity.SlotAccess#of` - Creates a `SlotAccess` using a supplier, consumer setup
- `net.minecraft.world.entity.SpawnPlacements#isSpawnPositionOk` - If the entity can spawn in this position
- `net.minecraft.world.entity.ai.attributes.AttributeInstance#addOrUpdateTransientModifier` - Updates a modifier value to the current one if it is already present, otherwise adds it
- `net.minecraft.world.entity.ai.behavior.Swim#shouldSwim` - Checks whether the entity can swim
- `net.minecraft.world.entity.ai.navigation.PathNavigation#moveTo` - A `moveTo` method which takes in a close enough distance
- `net.minecraft.world.entity.monster.AbstractSkeleton`
  - `getHardAttackInterval` - After how many ticks the entity will attack when in the hard difficulty
  - `getAttackInterval` - After how many ticks the entity will attack when not in the hard difficulty
- `net.minecraft.world.entity.player.Inventory#contains(Predicate)` - Whether a stack in the inventory matches the predicate
- `net.minecraft.world.entity.player.Player#canInteractWithEntity` - Whether the player can interact with another entity
- `net.minecraft.world.entity.projectile.AbstractArrow`
  - `getDefaultPickupItem`, `setPickupItemStack` - Gets the default pickup item if the actual item stack cannot be obtained or is empty
  - `getSlot` - Gets the slot access of the pickupable stack
- `net.minecraft.world.entity.projectile.Projectile`
  - `getMovementToShoot` - Gets the movement to apply to the entity when shooting
  - `hitTargetOrDeflectSelf` - Gets the deflection status of the projectile
  - `deflect` - Deflects the entity
  - `onDeflection` - What to do when this projectile is deflected
- `net.minecraft.world.entity.projectile.ProjectileDeflection` - The status of how a projectile can be deflected, typically on punch
- `net.minecraft.world.entity.raid.Raider`
  - `isCaptain` - Whether the entity is a captain of a raid
  - `hasRaid` - If the entity has a raid to do
- `net.minecraft.world.entity.vehicle.ContainerEntity#getBoundingBox` - Gets the bounding box of the entity
- `net.minecraft.world.flag.FeatureFlagSet`
  - `isEmpty` - Whether there are no feature flags enabled
  - `intersects` - Whether two feature flag sets contain at least one similar feature flag
  - `subtract` - Returns a feature flag set without the flags of the parameter
- `net.minecraft.world.food.FoodConstants#saturationByModifier` - Takes in the number of hearts to heal and how the saturation should be multiplied by
- `net.minecraft.world.inventory.SlotRange` - Defines a range of slot indexes that can be referenced from some string prefix plus the index (all available `SlotRange`s are in `SlotRanges`
- `net.minecraft.world.item.ArmorItem$Type` is now `StringRepresentable`
  - `hasTrims` - Whether the armor type supports trims
- `net.minecraft.world.item.ArmorMaterial#layers` - Stores information about the texture location of the armor type
- `net.minecraft.world.item.DiggerItem#createAttributes` - Creates the attribute modifiers given for a tier, attack damage, and attack speed
- `net.minecraft.world.item.Item`
  - `components` - Gets the data component map
  - `getDefaultMaxStackSize` - Reads the max stack size of the component, or defaults to 1
  - `getAttackDamageBonus` - Applies a bonus to the attack damage when in the main hand
  - `getBreakingSound` - The sound even to play when the item breaks
  - `$Properties#component` - Adds a data component to the item
  - `$Properties#attributes` - Sets the attribute modifiers for the item
  - `$TooltipContext` - Holds access to the available registries, tick rate, and `MapItemSavedData` when coming from a `Level`
- `net.minecraft.world.item.ItemStack`
  - `getPrototype` - Gets the component map from the item
  - `getComponentsPatch` - Gets the patches to the component map on the current `ItemStack`
  - `validateComponents` - Validates whether certain components can be on the component map
  - `parse` - Parses the stack from a tag
  - `parseOptional` - Parses the stack from a tag, or returns an empty stack when not present
  - `save` - Saves the stack to a tag, or returns an empty tag
  - `transmuteCopy` - Creates a new copy, providing the component patch
  - `transmuteCopyIgnoreEmpty` - Creates a new copy without checking whether the stack is empty or not
  - `listMatches` - Returns whether two lists of stacks match each other 1-to-1
  - `lenientOptionalFieldOf` - Creates a map codec with an optional stack
  - `hashItemAndComponents` - Computes the hash code for a given stack
  - `hashStackList` - Computes the hash code for a list of stacks
  - `set` - Sets a data component value
  - `update` - Updates a data component value
  - `applyComponentsAndValidate` - Applies a component patch and validates the components
  - `applyComponents` - Applies a patch to the component map and validates
  - `getEnchantments` - Gets the item enchantments from the data component
  - `forEachModifier` - Applies a consumer for every modifier in the attribute modifiers
  - `limitSize` - Sets the count of the stack if the current count is larger than the limit
  - `consume` - Consumes a single item
- `net.minecraft.world.item.ProjectileItem` - Defines an item that can function like a projectile. Contains logic for constructing the projectile and shooting it either from the entity or dispenser
  - `ProjectileWeaponItem` contains protected implementations of these methods to be set when implementing `ProjectileItem`
- `net.minecraft.world.item.SwordItem#createAttributes` - Creates the attribute modifiers given for a tier, attack damage, and attack speed
- `net.minecraft.world.item.Tier`
  - `getIncorrectBlocksForDrops` - A negative restraint preventing certain blocks from receiving a boost from this tier
  - `createToolProperties` - Creates a `Tool` given the tag containing the blocks to speed up mining for
- `net.minecraft.world.level.ExplosionDamageCalculator#getKnockbackMultiplier` - Returns a scalar on how much knockback to apply to the entity
- `net.minecraft.world.level.SpawnData#isValidPosition` - Whether the entity can spawn within the specified block and sky light
- `net.minecraft.world.level.block.BonemealableBlock`
  - `getParticlePos` - Gets the position the particle should spawn
  - `getType` - Gets the type of effect the bonemeal has on particle spawning
- `net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity#invalidateCache` - Clears the fuel map
- `net.minecraft.world.level.block.entity.BannerBlockEntity#getPatterns` - Gets the stored patterns on the banner
- `net.minecraft.world.level.block.entity.BlockEntity`
  - `applyImplicitComponents` - Reads any data stored on the component input for the block entity
  - `collectImplicitComponents` - Writes any data to be stored on the stack
  - `removeComponentsFromTag`- Removes any information that is stored doubly within the `BlockEntityTag` that is handled by a data component
  - `components`, `setComponents` - Getters and setters for the stored data component map
- `net.minecraft.world.level.block.entity.Hopper#isGridAligned` - Returns true if the hopper is always aligned to the block grid
- `net.minecraft.world.level.block.entity.trialspawner.PlayerDetector$EntitySelector` - Determines how to select an entity from a given context
- `net.minecraft.world.level.chunk.storage.RegionFile#getPath` - Gets the path of the region file
- `net.minecraft.world.level.chunk.storage.RegionFileInfo` - A record which contains the current level name, dimension, and the type of the region (e.g. chunk)
- `net.minecraft.world.level.levelgen.structure.Structure#getMeanFirstOccupiedHeight` - Gets the average height of the four corners of the structure
- `net.minecraft.world.level.levelgen.structure.BoundingBox#inflatedBy(int, int, int)` - Inflates the size of the box by the specified (x,y,z) in both directions
- `net.minecraft.world.level.levelgen.structure.placement.StructurePlacement`
  - `applyAdditionalChunkRestrictions` - Returns whether the structure should generate given some level of frequency reduction
  - `applyInteractionsWithOtherStructures` - Returns whether the structure should generate based on the exclusion zones
- `net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate#updateShapeAtEdge(LevelAccessor, int, DiscreteVoxelShape, BlockPos)` - Calls `BlockState#updateShape` on all blocks on the edges of the structure
- `net.minecraft.world.level.pathfinder.NodeEvaluator#getPathType(Mob, BlockPos)` - Gets the path type by calling `getPathType(PathfindingContext, int, int, int)` where the `PathfindingContext` is constructed from the mob and the three integers by unwrapping the `BlockPos`
- `net.minecraft.world.level.pathfinder.PathfindingContext` - A context object which holds information related to the current world, cache, and mob position
- `net.minecraft.world.level.storage.LevelStorageSource$LevelStorageAccess`
  - `estimateDiskSpace` - Returns how much disk space is left to write to
  - `checkForLowDiskSpace` - Checks if the amount of disk space remaining is less than 64MiB
- `net.minecraft.world.level.storage.LevelSummary#canUpload` - Returns whether the current level summary can be uploaded in a realm
- `net.minecraft.world.level.storage.loot.ContainerComponentManipulator` - A helper for setting container contents for items in their data components
  - A list of available manipulators can be found in `ContainerComponentManipulators`
- `net.minecraft.world.level.storage.loot.functions.ListOperation` - Defines an operation that can be performed on a list
- `net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction#getType` - Gets the item function type that can be conditionally loaded
- `net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape#isInterior` - Checks whether the position is surrounded by fully encompassing shapes on all sides
- `net.minecraft.client.gui.font.providers.FreeTypeUtil#checkError` - Returns true if an error was found
- `net.minecraft.client.gui.screens.Screen`
  - `advancePanoramaTime` - Updates the panorama by a set interval every frame
    - Does not use the delta time frame provided in `renderPanorama`
  - `clearTooltipForNextRenderPass` - Clears the tooltip rendering data 
- `net.minecraft.nbt.CompoundTag#shallowCopy` - Creates a shallow copy of the tag
- `net.minecraft.network.protocol.PacketUtils#makeReportedException` - Fills the crash report before returning the reported exception to throw
- `net.minecraft.server.packs.repository.PackRepository#displayPackList` - Collects all packs into a single string by their id
- `net.minecraft.world.item.crafting.RecipeManager#getOrderedRecipes` - Gets all recipes ordered by their `RecipeType`
- `net.minecraft.world.level.chunk.ChunkGenerator#validate` - Resolves the feature step data to generate

### Changes

- `com.mojang.blaze3d.font.SheetGlyphInfo`
  - `getBearingX` -> `getBearingLeft`
  - `getBearingY` -> `getBearingTop`
  - `getUp` -> `getTop`
  - `getDown` -> `getBottom`
- `com.mojang.blaze3d.font.TrueTypeGlyphProvider` now uses `org.lwjgl.util.freetype` over `org.lwjgl.stb` for font data storage
- `com.mojang.blaze3d.platform.NativeImage#copyFromFont` -> `(FT_Face, int)`
  - This method also returns a boolean, indicating whether the operation was successful
- `com.mojang.blaze3d.systems.RenderSystem#getModelViewStack` now returns a `Matrix4fStack`
- `com.mojang.blaze3d.vertex.PoseStack#mulPoseMatrix` -> `#mulPose`
- `com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator` now takes in a `PoseStack$Pose`
- `com.mojang.blaze3d.vertex.VertexConsumer#putBulkData` now takes in the alpha parameter for the color
- `net.minecraft.Util#LINEAR_LOOKUP_THRESHOLD` is now public
- `net.minecraft.advancements.Advancement#validate(ProblemReporter, LootDataResolver)` -> `validate(ProblemReporter, HolderGetter$Provider)`
- `net.minecraft.advancements.AdvancementRewards$Builder#loot`, `addLootTable` now take in a `ResourceKey<LootTable>`
- `net.minecraft.client.MouseHandler#turnPlayer` is now private
- `net.minecraft.client.Options$FieldAccess#process(String, OptionInstance)` -> `Options$OptionAccess$process`
- `net.minecraft.client.gui.components.AbstractSelectionList#renderList` -> `renderListItems`
- `net.minecraft.client.gui.components.AbstractWidget#setTooltipDelay(int)` -> `setTooltipDelay(Duration)`
- `net.minecraft.client.gui.components.ChatComponent
  - `render` now takes in if the chat is focused as a boolean parameter
  - `isChatFocused` is now public
- `net.minecraft.client.gui.components.Checkbox#getBoxSize` is now public
- `net.minecraft.client.gui.components.FocusableTextWidget` now takes in an integer representing the padding of the widget
- `net.minecraft.client.gui.components.ObjectSelectionList$Entry#mouseClicked` returns true by default
- `net.minecraft.client.gui.components.OptionList`
  - The constructor now takes in a `OptionsSubScreen` containing the content height and header height
  - `addSmall` can now take in a varargs of `OptionInstance`s, a list of widgets, or two widgets
  - `getMouseOver` now holds an optional of `GuiEventListener`
  - `$Entry` -> `$OptionEntry`, `$Entry` is now an abstracted form which takes in the widgets directly
- `net.minecraft.client.gui.components.SpriteIconButton` now takes in a `Button$CreateNarration` object
  - This has been updated for all subclasses and provided a builder option called `narration`
- `net.minecraft.client.gui.components.Tooltip#setDelay`, `refreshTooltipForNextRenderPass`, `createTooltipPositioner` have been moved to `WidgetTooltipHolder`
  - The holder is stored on an `AbstractWidget` and is final. The tooltip within is mutable
- `net.minecraft.client.gui.components.debugchart.AbstractDebugChart` now takes in a `SampleStorage`, which is an interface to the old `SampleLogger` (now renamed `LocalSampleLogger`)
- `net.minecraft.client.gui.screens.ChatScreen#handleChatInput` no longer returns anything
- `net.minecraft.client.gui.screens.ConnectScreen#startConnecting` takes in the current `TransferState`
- `net.minecraft.client.gui.screens.GenericDirtMessageScreen` -> `GenericMessageScreen`
- `net.minecraft.client.gui.screens.OptionsSubScreen` now holds a `HeaderAndFooterLayout` to initialize widgets and reposition them
  - `addTitle`, `addFooter` have been added, replacing specific `createTitle` and `createFooter` methods in other implementations
- `net.minecraft.client.gui.screens.Screen#setTooltipForNextRenderPass` is now public
- `net.minecraft.client.gui.screens.advancements.AdvancementsScreen` can hold the last screen the user has seen
- `net.minecraft.client.gui.screens.inventory.InventoryScreen#renderEntityInInventory` now takes in a float for the scale parameter
- `net.minecraft.client.gui.screens.worldselection.WorldCreationUiState`
  - `setAllowCheats` -> `setAllowCommands`
  - `isAllowCheats` -> `isAllowCommands`
- `net.minecraft.client.gui.screens.worldselection.WorldOpenFlows`
  - `checkForBackupAndLoad(String, Runnable)` -> `openWorld`
  - `checkForBackupAndLoad(LevelStorageAccess, Runnable)` -> `openWorldLoadLevelData`
  - `loadLevel` -> `openWorldLoadLevelStem`
- `net.minecraft.client.multiplayer.ServerStatusPinger#pingServer` now takes in a runnable that runs on response from the pong packet
- `net.minecraft.client.particle.FireworkParticles$SparkParticle#setFlicker` -> `setTwinkle`
- `net.minecraft.client.player.inventory.Hotbar` holds a list of `Dynamic`s instead of the raw itemstack list
- `net.minecraft.client.renderer.EffectInstance` now takes in a `ResourceProvider` instead of a `ResourceManager`
  - `ResourceManager` implements `ResourceProvider`, so there is no major change in implementation
- `net.minecraft.client.renderer.LevelRenderer#renderClouds` takes in a `Matrix4f` containing the pose to transform the clouds to the correct location
- `net.minecraft.client.renderer.PanoramaRenderer#render(float, float)` -> `render(GuiGraphics, int, int, float, float)`
- `net.minecraft.client.renderer.PostChain` now takes in a `ResourceProvider` instead of a `ResourceManager`
  - `ResourceManager` implements `ResourceProvider`, so there is no major change in implementation
- `net.minecraft.client.renderer.PostChain#addPass` now takes in a boolean which determines whether to use `GL_LINEAR` when true or `GL_NEAREST` when false
- `net.minecraft.client.renderer.PostPass` now takes in a `ResourceProvider` instead of a `ResourceManager`
  - `ResourceManager` implements `ResourceProvider`, so there is no major change in implementation
- `net.minecraft.client.renderer.PostPass#getFilterMode`
  - Either `GL_LINEAR` or `GL_NEAREST`
- `net.minecraft.client.renderer.blockentity.SkullBlockRenderer#getRenderType(SkullBlock$Type, GameProfile)` -> `getRenderType(SkullBlock$Type, ResolvableProfile)`
- `net.minecraft.client.renderer.entity.LivingEntityRenderer#setupRotations` now takes in a scale representing the float parameter
- `net.minecraft.client.renderer.entity.ArrowRenderer#vertex` combines the `Matrix4f` and `Matrix3f` into a `PoseStack$Pose`
- `net.minecraft.client.renderer.entity.EntityRenderer#renderNameTag` now takes in a float representing the partial tick
- `net.minecraft.client.renderer.entity.layers.StrayClothingLayer` -> `SkeletonClothingLater`
- `net.minecraft.client.renderer.item.ItemProperties#getProperty(Item, ResourceLocation)` -> `getProperty(ItemStack, ResourceLocation)`
- `com.mojang.blaze3d.audio.OggAudioStream` replaced by `net.minecraft.client.sounds.FiniteAudioStream`, `FloatSampleSource`, `JOrbisAudioStream`
- `net.minecraft.commands.CommandBuildContext` now implements `HolderLookup$Provider`, replacing `holderLookup` and `configurable`
- `net.minecraft.commands.arguments.ComponentArgument` now takes in a `HolderLookup$Provider` when constructing a text component
- `net.minecraft.commands.arguments.ParticleArgument#readParticle` takes in a `HolderLookup$Provider` instead of a `HolderLookup` specifically for particle types
- `net.minecraft.commands.arguments.ResourceLocationArguments`
  - `getPredicate` -> `ResourceOrIdArgument#getLootPredicate`
  - `getItemModifier` -> `ResourceOrIdArgument#getLootModifier`
- `net.minecraft.commands.arguments.StyleArgument` now takes in a `HolderLookup$Provider` when constructing a style, or `CommandBuildContext` when calling `style`
- `net.minecraft.commands.arguments.item.ItemInput` no longer implements `Predicate<ItemStack>`
- `net.minecraft.commands.functions.CommandFunction#instantiate` no longer takes in the generic object
  - The generic object is now stored as an entry directly within the function itself (e.g. `MacroFunction`)
- `net.minecraft.ccommands.functions.FunctionBuilder#addMacro` takes in the generic object
- `net.minecraft.core.GlobalPos` is now a record, though there is no change in usage
- `net.minecraft.core.HolderLookup` the below logic is now applied specifically for `$RegistryLookup`
  - `filterElements` -> `HolderLookup$RegistryLookup#filterElements)
  - `$Delegate` -> `HolderLookup$RegistryLookup$Delegate`
- `net.minecraft.core.Registry#lifecycle(T)` -> `registrationInfo(ResourceKey<T>)`
- `net.minecraft.core.RegistrySetBuilder#lookupFromMap` now takes in a `HolderOwner`
- `net.minecraft.core.RegistrySetBuilder$CompositeOwner` -> `$UniversalOwner`
  - This is not one to one, it is simply a replacement
- `net.minecraft.core.WritableRegistry#register(ResourceKey, T, Lifecycle)` -> `register(ResourceKey, T, RegistrationInfo)`
- `net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior` -> `ProjectileDispenseBehavior`
- `net.minecraft.data.DataProvider#saveStable` also takes in a `HolderLookup$Provider` to get the `RegistryOps` lookup context
- Game test batches have been reimplemented. The logic is now split between `net.minecraft.gametest.framework.GameTestBatch`, `GameTestBatchFactory`, and `GameTestBatchListener`
- `net.minecraft.gametest.framework.GameTestHelper#makeMockSurvivalPlayer`, `makeMockPlayer()` -> `makeMockPlayer(GameType)`
- `net.minecraft.gametest.framework.GameTestListener#testPassed`, `testFailed` now take in a `GameTestRunner`
- `net.minecraft.nbt.NbtUtils`
  - `readBlockPos` now takes in a key for the int array within the `CompoundTag`
  - `writeBlockPos` now returns an `IntArrayTag`
- `net.minecraft.network.chat.ChatType#CODEC` -> `#DIRECT_CODEC`
- `net.minecraft.network.chat.Component$Serializer` methods also take in a `HolderLookup$Provider`
- `net.minecraft.network.syncher.EntityDataAccessor` is now a record
- `net.minecraft.server.MinecraftServer`
  - `logTickTime` replaced by `getTickTimeLogger`, `isTickTimeLoggingEnabled`
  - `endMetricsRecordingTick` is now public
- `net.minecraft.server.ReloadableServerResources#getLootData` replaced by `fullRegistries`
- `net.minecraft.server.level` chunk-querying methods now return a `ChunkResult` instead of an `Either<ChunkAccess, ChunkHolder$ChunkLoadingFailure>`
- `net.minecraft.server.players.PlayerList`
  - `setAllowCheatsForAllPlayers` -> `setAllowCommandsForAllPlayers`
  - `isAllowCheatsForAllPlayers` -> `isAllowCommandsForAllPlayers`
- `net.minecraft.tags.TagNetworkSerialization#deserializeTagsFromNetwork` is now package-private
- `net.minecraft.util.SampleLogger` -> `net.minecraft.util.debugchart.LocalSampleLogger`
  - This is now an implementation of `SampleStorage`
- `net.minecraft.util.profiling.jfr.JvmProfiler#onPacketReceived` `onPacketSent` now take in the `PacketType` instead of an integer.
- `net.minecraft.util.profiling.jfr.stats.NetworkPacketSummary` -> `IoSummary`
- `net.minecraft.util.random.WeightedEntry$Wrapper` is now a record
- `net.minecraft.world.Container#stillValidBlockEntity` now takes in a float representing the radius past the block interaction distance to check whether the block entity can be interacted with
- `net.minecraft.world.ContainerHelper#saveAllItems`, `loadAllItems` now take in a `HolderLookup$Provider`
- `net.minecraft.world.InteractionResult#shouldAwardStats` -> `indicateItemUse`
- `net.minecraft.world.LockCode` is now a record
- `net.minecraft.world.RandomizableContainer#getLootTable`, `setLootTable`, `setBlockEntityLootTable` take in a `ResourceKey<LootTable>` instead of a `ResourceLocation`
- `net.minecraft.world.SimpleContainer#fromTag`, `createTag` now take in a `HolderLookup$Provider`
- `net.minecraft.world.damagesource.CombatRules#getDamageAfterAbsorb` now takes in a `DamageSource` to calculate armor breach
- `net.minecraft.world.effect.MobEffect`
  - `applyEffectTick` now returns a boolean that, when false, will remove the effect if `shouldApplyEffectTickThisTick` returns true
  - `createFactorData`, `setFactorDataFactory` has been replaced by `getBlendDurationTicks`, `setBlendDuration`
  - `getAttributeModifiers` is replaced by `createModifiers`
- `net.minecraft.world.effect.MobEffectInstance$FactorData` has been replaced by `$BlendState` along with the logic to apply
- `net.minecraft.world.entity.AreaEffectCloud#setPotion` -> `setPotionContents`
- `net.minecraft.world.entity.Entity` now implement `SyncedDataHolder` to handle network communication for entity properties
  - `defineSynchedData` now takes in a `SynchedEntityData$Builder`
  - `setSecondsOnFire` -> `igniteForSeconds`
    - There is also an `igniteForTicks` variant
  - `calculateViewVector` is now public
  - `getMyRidingOffset`, `ridingOffset` -> `getVehicleAttachmentPoint`
  - `getPassengerAttachmentPoint` now returns a `Vec3`
  - `getHandSlots`, `getArmorSlots`, `getAllSlots`, `setItemSlot` are no longer on `Entity`, but still on `LivingEntity`
  - `getEyeHeight` is now final and reads from the entity's dimensions
    - Eye height is set through `EntityType$Builder`
  - `getNameTagOffsetY` is now replaced by an `EntityAttachment`
  - `getFeetBlockState` -> `getInBlockState`
- `net.minecraft.world.entity.EntityDimensions` is now a record
- `net.minecraft.world.entity.EntityType`
  - `spawn`, `create` now takes in a `Consumer` of the entity to spawn rather than a `CompoundTag`
  - `updateCusutomEntityTag` now takes in a `CustomData` object instead of a `CompoundTag`
  - `getDefaultLootTable` now returns a `ResourceKey<LootTable>`
  - `getAABB` -> `getSpawnAABB`
- `net.minecraft.world.entity.LivingEntity`
  - `getScale` -> `getAgeScale`
    - `getScale` still exists and handles scaling based on an attribute property
- `net.minecraft.world.entity.Mob#finalizeSpawn` no longer takes in a `CompoundTag`
- `net.minecraft.world.entity.SpawnPlacements$Type` -> `SpawnPlacementsType`
  - All `SpawnPlacementType`s are in `SpawnPlacementTypes`
- `net.minecraft.world.entity.TamableAnimal`
  - `setTame` now takes in a second boolean that, when true, applies any side effects from taming
  - `reassessTameGoals` -> `applyTamingSideEffects`
- `net.minecraft.world.entity.ai.attributes.AttributeInstance`
  - `getModifiers` is now package-private
  - `removeModifier` is now public
- `net.minecraft.world.entity.ai.attributes.AttributeModifier` is now a record
  - `$Operation`
    - `ADDITION` -> `ADD_VALUE`
    - `MULTIPLY_BASE` -> `ADD_MULTIPLIED_BASE`
    - `MULTIPLY_TOTAL` -> `ADD_MULTIPLIED_TOTAL`
- `net.minecraft.world.entity.ai.behavior.BehaviorUtils#lockGazeAndWalkToEachOther` now takes in an integer representing the close enough distance
- `net.minecraft.world.entity.ai.village.poi.PoiManager` now takes in a `RegionStorageInfo`
- `net.minecraft.world.entity.animal.Animal#isFood` is now abstract
- `net.minecraft.world.entity.player.Player`
  - `disableShield` no longer takes in a boolean
  - `getPickRange` -> `blockInteractionRange`, `entityInteractionRange`
- `net.minecraft.world.entity.projectile.AbstractArrow#deflect` -> `Entity#deflection`
- `net.minecraft.world.entity.raid.Raid`
  - `getMaxBadOmenLevel` -> `getMaxRaidOmenLevel`
  - `getBadOmenLevel` -> `getRaidOmenLevel`
  - `setBadOmenLevel` -> `setRaidOmenLevel`
  - `absorbBadOmen` -> `absorbRaidOmen` and returns whether the entity has the effect
  - `getLeaderBannerInstance` now takes in a `HolderGetter<BannerPattern>`
- `net.minecraft.world.entity.raid.Raids#createOrExtendRaid` now takes in a `BlockPos` the raid is centered around
- `net.minecraft.world.food.FoodData#eat` no longer takes in an `Item`
- `net.minecraft.world.food.FoodProperties` is now a record
  - `fastFood` has been replaced by a `eatSeconds` float representing time
  - `Builder$saturationMod` -> `saturationModifier`
  - `Builder$alwaysEat` -> `alwaysEdible`
  - `Builder$meat` has been replaced with item tags for the given entity (e.g., `minecraft:wolf_food`
- `net.minecraft.world.inventory.tooltip.BundleTooltip` stores its information within `BundleContents` via `#contents`
- `net.minecraft.world.item.AdventureModeCheck` -> `AdventureModePredicate`
- `net.minecraft.world.item.ArmorMaterial` is now a record
  - `getDurabilityForType` -> `ArmorItem$Type#getDurability`
  - `getDefenseForType` -> `defense`
- `net.minecraft.world.item.AxeItem` is now public
- `net.minecraft.world.item.CrossbowItem#performShooting` is now an instance method
- `net.minecraft.world.item.HoeItem` is now public
- `net.minecraft.world.item.Item`
  - `verifyTagAfterLoad` -> `verifyComponentsAfterLoad`
  - `getMaxStackSize` -> `ItemStack#getMaxStackSize`
  - `getMaxDamage` -> `ItemStack#getMaxDamage`
  - `canBeDepleted` -> `ItemStack#isDamageableItem`
  - `isCorrectToolForDrops` now takes in an `ItemStack`
  - `appendHoverText` holds an `Item$TooltipContext` instead of a `Level`
  - `getDefaultAttributeModifiers` now returns an `ItemAttributeModifiers`
  -  `canBeHurtBy` -> `ItemStack#canBeHurtBy`
- `net.minecraft.world.item.ItemStack` now implements `DataComponentHolder`
  - The constructor takes in a `DataComponentPatch` or `PatchedDataComponentMap` instead of a `CompoundTag`
  - `save(CompoundTag)` -> `save(HolderLookup$Provider, Tag)`
  - `hurt` -> `hurtAndBreak`
    - A `Runnable` is the last parameter which determines what to do when a stack exceeds the max damage
  - `hurtAndBreak(int, T, Consumer<T>)` -> `hurtAndBreak(int, LivingEntity, EquipmentSlot)`
  - `isSameItemSameTags` -> `isSameItemSameComponents`
  - `getTooltipLines(Player, TooltipFlag)` -> `getTooltipLines(Item$TooltipContext, Player, TooltipFlag)`
  - `hasAdventureModePlaceTagForBlock` -> `canPlaceOnBlockInAdventureMode`
  - `hasAdventureModeBreakTagForBlock` -> `canBreakBlockInAdventureMode`
- `net.minecraft.world.item.ItemStackLinkedSet#createTypeAndTagSet` -> `createTypeAndComponentsSet`
- `net.minecraft.world.item.ItemUtils#onContainerDestroyed` now takes a `Iterable<ItemStack>` instead of a `Stream<ItemStack>`
- `net.minecraft.world.item.SmithingTemplateItem#createArmorTrimTemplate` takes in a varargs of `FeatureFlag`s
- `net.minecraft.world.item.SpawnEggItem#spawnsEntity`, `getType` now takes in an `ItemStack` instead of a `CompoundTag`
- `net.minecraft.world.item.alchemy.Potion#getName` now takes in an `Optional<Holder<Potion>>`
- `net.minecraft.world.item.alchemny.PotionUtils` -> `PotionContents`
  - Method names are roughly equivalent, ignorning all instances where a tag is wanted
- `net.minecraft.world.item.armortrim.ArmorTrim` now takes in a boolean indicating whether the tooltip component will show up
  - This tooltip can be toggled via `withTooltip`
- `net.minecraft.world.item.crafting.Recipe`
  - `assemble(C, RegistryAccess)` -> `assemble(C, HolderLookup.Provider)`
  - `getResultItem(RegistryAccess)` -> `getResultItem(HolderLookup.Provider)`
- `net.minecraft.world.item.crafting.RecipeCache#get` now returns a `Optional<RecipeHolder<CraftingRecipe>>`
- `net.minecraft.world.item.crafting.RecipeManager`
  - `getRecipeFor(RecipeType<T>, C, Level, ResourceLocation)` now returns an `Optional<RecipeHolder<T>>`
    - The `RecipeHolder` contains the `ResourceLocation`
  - `byType` now returns a `Collection<RecipeHolder` instead of a `Map<ResourceLocation, RecipeHolder<T>>`
- `net.minecraft.world.item.traiding.MerchantOffer` now takes in `ItemCost`s for cost stack instead of `ItemStack`s
- `net.minecraft.world.level.BaseCommandBlock#setName` -> `setCustomName`
- `net.minecraft.world.level.Level`
  - `getMapData`, `setMapData`, `getFreeMapId` now take in a `MapId` instead of an integer
  - `createFireworks` now takes in a `List<FireworkExplosion>` instead of a `CompoundTag`
- `net.minecraft.world.level.SpawnData` now takes in an `EquipmentTable`, which contains the armor an entity should spawn with
- `net.minecraft.world.level.StructureManager`
  - `getStructureWithPiecesAt` now takes in some set of `Sturcture`s
  - `checkStructurePresence` now takes in a `StructurePlacement`
- `net.minecraft.world.level.block.Block#appendHoverText` now takes in an `Item$TooltipContext` instead of a `BlockGetter`
- `net.minecraft.world.level.block.EnchantmentTableBlock` -> `EnchantingTableBlock`
- `net.minecraft.world.level.block.FlowerBlock` now takes in a `SuspiciousStewEffects`
- `net.minecraft.world.level.block.entity.BeehiveBlockEntity`
  - `addOccupantWithPresetTicks` -> `addOccupant`
  - `storeBee(CompoundTag, int, boolean)` -> `storeBee(BeehiveBlockEntity$Occupant)`
- `net.minecraft.world.level.block.entity.BlockEntity`
  - `load` -> `loadAdditional`
  - `saveAdditional`, `saveWithFullMetadata`, `saveWithId`, `saveWithoutMetadata`, `saveToItem`, `loadStatic`, `getUpdateTag` now takes in a `HolderLookup$Provider`
- `net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity`
  - `getItems` -> `BaseContainerBlockEntity#getItems`
  - `setItems` -> `BaseContainerBlockEntity#setItems`
- `net.minecraft.world.level.block.entity.trialspawner.PlayerDetector#detect(ServerLevel, BlockPos, int)` -> `detect(ServerLevel, PlayerDetector$EntitySelector, BlockPos, double, boolean)`
- `net.minecraft.world.level.block.state.StateHolder#getValues` now returns a regular `Map` as it stores an `Reference2ObjectArrayMap`
- `net.minecraft.world.level.chunk.ChunkAccess#getBlockEntityNbtForSaving` now takes in a `HolderLookup$Provider`
- `net.minecraft.world.level.chunk.ChunkStatus` -> `net.minecraft.world.level.chunk.status.ChunkStatus`
  - Some other classes that were inner classes have also been moved to the `net.minecraft.world.level.chunk.status` package (e.g. `ChunkType`)
- `net.minecraft.world.level.gameevent.GameEvent` is now a record`
- `net.minecraft.world.level.gameevent.GameEventListener$Holder` -> `GameEventListener$Provider`
- `net.minecraft.world.level.levelgen.WorldDimensions(Map<ResourceKey<LevelStem>, LevelStem>)` is the new base constructor, the previous constructor with the `Registry` still exists
- `net.minecraft.world.level.levegen.presets.WorldPreset#createRegistry` -> `dimensionsInOrder`
- `net.minecraft.world.level.levelgen.structure.StructureCheck#checkStart` now takes in a `StructurePlacement`
- `net.minecraft.world.level.levelgen.structure.StructurePiece#createChest` now takes in a `ResourceKey<LootTable>` instead of a `ResourceLocation`
- `net.minecraft.world.level.levelgen.structure.StructurePiece#createDispenser` now takes in a `ResourceKey<LootTable>` instead of a `ResourceLocation`
- `net.minecraft.world.level.pathfinder.BlockPathTypes` -> `PathType`
- `net.minecraft.world.level.pathfinder.NodeEvaluator`
  - `getGoal` -> `getTarget`
  - `getTargetFromNode` -> `getTargetNodeAt`
    - Gets the node based on a position rather than supplying the node itself
  - `BlockPathTypes getBlockPathType(BlockGetter, int, int, int, Mob)` -> `PathType getPathTypeOfMob(PathfindingContext, int, int, int, Mob)`
  - `BlockPathTypes getBlockPathType(BlockGetter, int, int, int)` -> `PathType getPathType(PathfindingContext, int, int, int)`
- `net.minecraft.world.level.pathfinder.SwimNodeEvaluator`
  - `isDiagonalNodeValid` -> `hasMalus`
  - `getCachedBlockType` returns `PathType`
- `net.minecraft.world.level.pathfinder.WalkNodeEvaluator`
  - `isDiagonalValid` removes the accepted node value, only returning whether the diagonal can be made
    - Checking the accepted node has been moved to a separate `isDiagonalValid(Node)` method
  - `getBlockPathTypes`, `evaluateBlockPathType` -> `getPathTypeWithinMobBB`
  - `getCachedBlockType` -> `getCachedPathType`
  - `getBlockPathTypeStatic` -> `getPathTypeStatic`
  - `checkNeighbourBlocks(BlockGetter, BlockPos$MutableBlockPos, BlockPathTypes)` -> `checkNeighbourBlocks(PathfindingContext, int, int, int, PathType)`
  - `getBlockPathTypeRaw` -> `getPathTypeFromState`
  - `isBurningBlock` -> `NodeEvaluator#isBurningBlock`
- `net.minecraft.world.level.saveddata.SavedData`
  - `save(CompoundTag)` -> `save(CompoundTag, HolderLookup.Provider)`
  - `save(File)` -> `save(File, HolderLookup.Provider)`
  - `$Factory(Supplier<T>, Function<CompoundTag, T>, DataFixTypes)` -> `$Factory(Supplier<T>, BiFunction<CompoundTag, HolderLookup.Provider, T>, DataFixTypes)`
- `net.minecraft.world.level.saveddata.maps.MapBanner` is now a record
- `net.minecraft.world.levle.saveddata.maps.MapDecoration$Type` -> `MapDecorationType`
  - `MapDecorationType` is now a built-in registry object
- `net.minecraft.world.level.storage.LevelData#getXSpawn`, `getYSpawn`, `getZSpawn` -> `getSpawnPos`
- `net.minecraft.world.level.storage.LevelSummary#hasCheats` -> `hasCommands`
- `net.minecraft.world.level.storage.ServerLevelData#getAllowCommands` -> `isAllowCommands`
- `net.minecraft.world.level.storage.WorldData#getAllowCommands` -> `isAllowCommands`
- `net.minecraft.world.level.storage.WritableLevelData#setXSpawn`, `setYSpawn`, `setZSpawn`, `setSpawnAngle` -> `setSpawn`
- `net.minecraft.world.level.storage.loot.LootContext` now takes in a `HolderGetter$Provider`
  - `getResolver` now returns the `HolderGetter$Provider`
- `net.minecraft.world.level.storage.loot.LootDataType` is now a record
- `net.minecraft.world.level.storage.loot.entries.LootTableReference` -> `NestedLootTable`
- `net.minecraft.world.ticks.ContainerSingleItem#getContainerBlockEntity` -> `$BlockContainerSingleItem#getContainerBlockEntity`
- `net.minecraft.client.Minecraft#setLevel(ClientLevel)` -> `setLevel(ClientLevel, ReceivingLevelScreen$Reason)`
- `net.minecraft.client.OptionInstance$IntRange` now takes in a boolean that applies the option change immediately instead of after 0.6 seconds. The value is not considered saved at that moment when applied immediately
- `net.minecraft.client.gui.font.providers.FreeTypeUtil#checkError` -> `assertError`
  - `checkError` is now a boolean-returning method that doesn't throw an error
- `net.minecraft.core.particles.DustParticleOptionsBase` -> `ScalableParticleOptionsBase`
- `net.minecraft.nbt.CompoundTag#entries` -> `entrySet`
  - This is a logic replacement, the returns are similar to those of a `Map`
- `net.minecraft.network.PacketListener#shouldPropagateHandlingExceptions` -> `onPacketError`
  - This is a logic replacement as the new method simply decides how to handle the error

### Removals

- `com.mojang.blaze3d.systems.RenderSystem#inverseViewRotationMatrix` along with subsequent getters and setters
- `net.minecraft.client.Minecraft#is64Bit`
- `net.minecraft.client.gui.components.AbstractSelectionList#setRenderBackground`
- `net.minecraft.client.gui.components.DebugScreenOverlay#logTickDuration`
- `net.minecraft.client.gui.font.FontManager#setRenames`, `getActualId`
- `net.minecraft.client.gui.screens.MenuScreen#create` no longer checks nullability of `MenuType`
- `net.minecraft.client.gui.screens.Screen#hideWidgets`
- `net.minecraft.client.gui.screens.achievement.StatsUpdateListener`
- `net.minecraft.client.gui.screens.worldselection.WorldOpenFlows#loadBundledResourcePack`
- `net.minecraft.client.multiplayer.ClientLevel#setScoreboard`
- `net.minecraft.client.multiplater.MultiPlayerGameMode`
  - `getPickRange`
  - `hasFarPickRange`
- `net.minecraft.client.multiplayer.ServerData`
  - `setEnforcesSecureChat`
  - `enforcesSecureChat`
- `net.minecraft.client.renderer.GameRenderer`
  - `cycleEffect`
  - `getPositionTexColorNormalShader`
  - `getPositionTexLightmapColorShader`
- `net.minecraft.commands.arguments.ArgumentSignatures#get`
- `net.minecraft.core.RegistryCodecs`
  - `withNameAndId`
  - `networkCodec`
  - `fullCodec`
  - `$RegistryEntry`
- `net.minecraft.core.dispenser.DispenseItemBehavior#getEntityPokingOutOfBlockPos`
- `net.minecraft.server.level.ChunkHolder#getFullChunk`
- `net.minecraft.util.JavaOps`
- `net.minecraft.world.effect.AttributeModifierTemplate`
- `net.minecraft.world.entity.Entity#setMaxUpStep`
- `net.minecraft.world.entity.LivingEntity`
  - `getMobType`
  - `getEyeHeight`, `getStandingEyeHeight`
- `net.minecraft.world.entity.MobType`
- `net.minecraft.world.entity.ai.goal.GoalSelector`
  - `getRunningGoals`
  - `setNewGoalRate`
- `net.minecraft.world.entity.player.Player#isValidUsername`
- `net.minecraft.world.entity.projectile.ThrowableItemProjectile#getItemRaw`
- `net.minecraft.world.item.BlockItem#getBlockEntityData`
- `net.minecraft.world.item.Vanishable`
- `net.minecraft.world.item.DyeableArmorItem`
- `net.minecraft.world.item.DyeableHorseArmorItem`
- `net.minecraft.world.item.DyeableLeatherItem`
- `net.minecraft.world.item.EnchantedGoldenAppleItem`
- `net.minecraft.world.item.FireworkStarItem#appendHoverText(CompoundTag, List<Component>)`
- `net.minecraft.world.item.HorseArmorItem`
- `net.minecraft.world.item.Item`
  - `shouldOverrideMultiplayerNbt`
  - `getRarity`
  - `isEdible`
  - `getFoodProperties`
  - `isFireResistant`
- `net.minecraft.world.item.ItemStack`
  - `of`
  - `hasTag`
  - `getTag`
  - `getOrCreateTag`
  - `getOrCreateTagElement`
  - `getTagElement`
  - `removeTagKey`
  - `getEnchantmentTags`
  - `setTag`
  - `setHoverName`
  - `hasCustomHoverName`
  - `addTagElement`
  - `getBaseRepairCost`
  - `setRepairCost`
  - `getAttributeModifiers`
  - `isEdible`
  - `$TooltipPart`
- `net.minecraft.world.item.SuspiciousStewItem`
  - `saveMobEffects`
  - `appendMobEffects`
  - `listPotionEffects`
- `net.minecraft.world.item.armortrim.ArmorTrim`
  - `setTrim`
  - `getTrim`
  - `appendUpgradeHoverText`
- `net.minecraft.world.item.crafting.Ingredient`
  - `toNetwork`
  - `fromNetwork`
- `net.minecraft.world.level.Level#dimensionTypeId`
- `net.minecraft.world.level.NaturalSpawner#isSpawnPositionOk`
- `net.minecraft.world.level.block.entity.BaseContainerBlockEntity#setCustomName`
- `net.minecraft.world.level.block.entity.BeehiveBlockEntity#addOccupant`
- `net.minecraft.world.level.storage.loot.LootDataId`
  - Basically a `ResourceKey`
- `net.minecraft.world.level.storage.loot.LootDataManager`
  - Basically a `HolderGetter$Provider`
- `net.minecraft.world.level.storage.loot.LootDataResolver`

# Minecraft 1.19.3 -> 1.19.4 Mod Migration Primer

This is a high level, non-exhaustive overview on how to migrate your mod from 1.19.3 to 1.19.4. This does not look at any specific mod loader, just the changes to the vanilla classes.

This primer is licensed under the [Creative Commons Attribution 4.0 International](http://creativecommons.org/licenses/by/4.0/), so feel free to use it as a reference and leave a link so that other readers can consume the primer.

If there's any incorrect or missing information, please file an issue on this repository or ping @ChampionAsh5357 in the Neoforged Discord server.

## Pack Changes

There are a number of user-facing changes that are part of vanilla which are not discussed below that may be relevant to modders. You can find a list of them on [Misode's version changelog](https://misode.github.io/versions/?id=1.19.4&tab=changelog).

## DamgeType and DamageSources

Damage sources have been rewritten to define common data which should be static between different sources. As such, each `DamageSource` now takes in a `DamageType` which defines this information. `DamageType`s are created in json like any other datapack registry.

The main `DamageType` record constructor takes in the following arguments:

Parameter        | Type               | Decsription
:---:            | :---:              | :---
msgId            | `String`           | A string used within a localization key (typically preceded by `death.attack.`).
scaling          | `DamageScaling`    | When the damage caused by the type should scale according to difficulty.
exhaustion       | `float`            | How much food exhaustion should occur when the damage source is applied to the player.
effects          | `DamageEffects`    | The sound to be played when the type is applied to an entity.
deathMessageType | `DeathMessageType` | How the death message should be structured for the type. Should almost always be `DEFAULT`.

A `DamageSource` is now treated as final class (it's not actually final, so it can still be extended if needed) which takes in some combination of the following parameters:

Parameter            | Type                 | Decsription
:---:                | :---:                | :---
type                 | `Holder<DamageType>` | The damage type, typically obtained from the `RegistryAccess`.
causingEntity        | `Entity`             | The entity who caused the damage directly or through the `#directEntity`.
directEntity         | `Entity`             | The entity who directly inflicted the damage.
damageSourcePosition | `Vec3`               | The position the damage source took place.

The damage sources are now constructed in `DamageSources` which can be obtained from the `Level` via `#damageSources`. Every `Entity` also has a redirect method to the level via `#damageSources`.

### DamageSourcePredicate

The `DamageSourcePredicate` for criteria triggers has been rewritten to take in a list of `TagPredicate`s for the `DamageType` applied along with the direct and causing (source) entity.

> `TagPredicate`s check if the specific object is either within or not within a tag (triggered by the `expected` boolean flag).

## GUI Changes

There have been a number of GUI changes which expands upon existing methods, restructures components, and reorganizes their locations.

### GuiComponent

All `GuiComponent` methods are now static. Additionally, new methods were added to draw information to the screen: `#renderOutline` and `#blitNineSliced`. `#setBlitOffset` and `#getBlitOffset` have been removed and are now performed using the `PoseStack` by translating the z-axis.

### ScreenRectangle

A new class called `ScreenRectangle` to specify a rectangle. This is typically used for scissors and layouts; however, it could also be used in standard rendering.

### ComponentPath

Logic relating to how components are focused and executed are now handled through `ComponentPath`s which now handle the responsibility of a for loop on a list.

### More PoseStack additions

The `PoseStack` has been added to a number of methods to properly transform the drawn information onto the screen space.

### Widget Restructing

A number of widgets have been restructured or moved into different classes to handle some common logic.

The following components have been added:
* `net.minecraft.client.gui.components.TabButton` and by extension `net.minecraft.client.gui.components.tabs.TabNavigationBar`
* `net.minecraft.client.gui.components.AbstractStringWidget` and by extension `net.minecraft.client.gui.components.MultiLineTextWidget`
* `net.minecraft.client.gui.components.ImageWidget`
* `net.minecraft.client.gui.components.TextAndImageButton`

In addition, the following components were moved, renamed, or modified:
* `net.minecraft.client.gui.components.CenteredStringWidget` -> `net.minecraft.client.gui.components.StringWidget`
* `net.minecraft.client.gui.components.AbstractWidget#renderButton` -> `#renderWidget`
* `net.minecraft.client.gui.components.AbstractWidget#getYImage` reworked into `AbstractButton#getTextureY` where it returns the texture coordinate rather than the texture index

#### Layout Components

Layout components, such as the `FrameWidget`, within `net.minecraft.client.gui.components` have been moved to `net.minecraft.client.gui.layouts`. Additionally, they have been restructure to consume widgets to properly move them to where they need to be displayed rather than having each layout be its own widget. As such, `AbstractContainerWidget` was removed.

### Font DisplayMode

`Font#drawInBatch` and any subsequent delegates (e.g. `FontRenderer#drawInBatch`) now take in a `Font$DisplayMode` instead of a boolean to determine how the font should be displayed. 

## Entity Interfaces

Entity logic has been abstracted even more into three new interfaces: `Attackable`, `Targeting`, and `TraceableEntity`. The `Attackable` interface indicates the entity can be attacked and stores the last attacker via `#getLastAttacker`. The `Targeting` interface indicates the entity can target another entity and stores the value via `#getTarget`. Finally, the `TraceableEntity` interface means that the entity's creation and initial action can be traced back to another entity, which can be obtained via `#getOwner`.

> `TraceableEntity` should not be confused with `OwnableEntity`. Traceables are typically used for projectiles or entities fired from or triggered by another entity while ownables are typically for tamed animals.

## Blocks and Sounds

Blocks have a few changes regarding their internal implementation and data structures.

The `BlockBehaviour$OffsetType` passed into `BlockBehavior$Properties#offsetType` now redirects to `BlockBehavior$OffsetFunction` which takes in a `BlockState`, `BlockGetter`, and `BlockPos` and returns the amount to offset the model by. This is currently not exposed to the end user.

A new record has been made to store common properties associated with a type, aptly named `BlockSetType`. This takes in the `SoundType` for the block in addition to sound events for flicking on or off a door, trapdoor, pressure plate, and button. Following this trend, `WoodType`s now take in the `BlockSetType` in addition to `SoundType`s for the wood and hanging sign along with `SoundEvent`s for the fence gate. `WoodType`s are also registered via the `#register` method instead of `#create`:

```java
public static final WoodType TEST_WOOD_TYPE = WoodType.register(new WoodType(new ResourceLocation(MODID, "test").toString(), BlockSetType.ACACIA));
```

> `BlockSetType`s can be created and registered using the constructor and `BlockSetType#register`, repsectively, after the `SoundEvent` registry event has fired but before the `Block` registry event.

## Creative Tabs

Creative Tabs have slightly changed as now when populating the generator, the method provides `CreativeModeTab$ItemDisplayParameters` and an `CreativeModeTab$Output`. The parameters holds the list of enabled feature, whether the player has permission, and a lookup provider on all the registries.

## Removal of `com.mojang.bridge.*`

Mojang's `javabridge` library has been removed as a dependency from Minecraft. As such, all of the redirected counterparts to the bridge (such as `com.mojang.bridge.game.PackType` and `com.mojang.bridge.game.GameVersion`) are now handled by their references or implementations (`net.minecraft.server.packs.PackType` and `net.minecraft.WorldVersion` respectively).

## Minor Additions, Changes, and Removals

The following is a list of useful or interesting additions, changes, and removals that do not deserve their own section in the primer.

### New Registries

In addition to `DamageType`s, a static registry for decorated pot patterns and datapack registries for trim materials, trim patterns, and `MultiNoiseBiomeSourceParameterList` have been added.

### New Codecs

A number of new codecs have been added or changed:
  * `com.mojang.math.Transformation#CODEC`
  * `net.minecraft.util.ExtraCodecs#QUATERNIONF_COMPONENTS`
  * `net.minecraft.util.ExtraCodecs#AXISANGLE4F`
  * `net.minecraft.util.ExtraCodecs#MATRIX4F`

### Sprite Registration Refactoring

First, `net.minecraft.client.particle.ParticleProvider$Sprite` is added, which is a functional interface used to create a `TextureSheetParticle` with only one texture. This is used as a wrapper around a `ParticleEngine$SpriteParticleRegistration` when the particle does not need access to the `SpriteSet`.

### Additions

* `GlintAlpha` - A new GLSL shader uniform which changes the glint strength based on the accessibility option of a similar name.
* `net.minecraft.advancements.Advancement#getRoot` - Gets the root of an advancement.
* `net.minecraft.client.model.geom.builders.CubeListBuilder#addBox` can now take in a set of directions which indicates the visible faces to render of a given box. This is baked into the `net.minecraft.client.model.geom.ModelPart$Cube` when adding the polygons.
* `net.minecraft.client.model.AgeableHierarchicalModel` - A parallel to `net.minecraft.client.modelAgeableListModel`s for `HierarchialModel`s.
* `net.minecraft.client.model.HumanoidArmorModel` - An extension of `net.minecraft.client.model.HumanoidModel` for armor models.
* `com.mojang.blaze3d.vertex.PoseStack#rotateAround` - Rotates a quaternion around a point.
* `net.minecraft.commands.arguments.HeightmapTypeArgument` - A common argument for specifying which heightmap to use.
* `net.minecraft.gametest.framework.GameTestHelper#continuouslyUse` - A helper test method to force a player to use an item on a certain block position every tick.
* `net.minecraft.util.ParticleUtils#spawnParticleBelow` - Spawns a particle half a block below the specified position
* `net.minecraft.world.inventory.Slot#setByPlayer` - A method indicating that a player changed the slot in some capacity.
* `net.minecraft.world.effect.MobEffectInstance#endsWithin` - Returns whether the mob effect will expire in x ticks.
* `net.minecraft.world.entity.Entity#getControlledVehicle` - Returns the vehicle if it is being controlled by this entity.
* `net.minecraft.world.phys.AABB#distanceToSqr` - Gets the squared distance to the provided vector.
* `net.minecraft.world.phys.Vec3#atLowerCornerWithOffset` - Offsets the vector by the specified x, y, z coordinates.
* `net.minecraft.world.phys.Vec3#atCenterOf` - Offsets the vector by 0.5 in all directions.
* `net.minecraft.data.advancements.AdvancementSubProvider#createPlaceholder` - Creates a dummy advancement to use as a parent for another advancement.
* `net.minecraft.network.FriendlyByteBuf#readJsonWithCodec` and #writeJsonWithCodec` - Writes a encoded object to json into a buffer.
    * `Tag` implementations `#readWithCodec` and `#writeWithCodec` are currently deprecated.
* `net.minecraft.client.renderer.texture.Dumpable` - Dumps the contents of the object to a file (`DynamicTexture`, `TextureAtlas`).
* `net.minecraft.world.entity.LivingEntity#remove` - Removes the entity from the level for the specified reason.
* `net.minecraft.world.entity.ai.Brain#clearMemories` - Clears all the memory values of an entity's brain.
* `com.mojang.math.MatrixUtil` contains some additional [jacobi matrix](https://en.wikipedia.org/wiki/Jacobian_matrix_and_determinant) methods.
* `net.minecraft.world.level.GrassColor#getDefaultColor` - Gets the default color of grass.
* `net.minecraft.network.syncher.SynchedEntityData#set(EntityDataAccessor, T, boolean)` - Boolean parameter added to force a sync of the entity data.

### Changes

* `net.minecraft.world.level.biome.Biome#isHumid` -> `#hasPrecipitation`
* `net.minecraft.world.level.biome.Biome#getPrecipitation()` -> `#getPrecipitationAt(BlockPos)`
* `net.minecraft.world.entity.LivingEntity#animationSpeedOld`, `#animationSpeed`, and `#animationPosition` has been bundled into one public object on the entity known as `WalkAnimationState`.
* `net.minecraft.server.packs.repository.PackRepository#addPack` and `#removePack` now return a boolean on whether the action was successful.
* `net.minecraft.client.gui.screens.worldselection.WorldGenSettingsComponent` -> `WorldCreationUiState`
* `net.minecraft.client.renderer.block.model.ItemTransforms$TransformType` -> `net.minecraft.world.item.ItemDisplayContext`
* `net.minecraft.client.resources.metadata.language.LanguageMetadataSectionSerializer` -> `LanguageMetadataSection#CODEC`
* `net.minecraft.world.item.crafting.UpgradeRecipe` -> `LegacyUpgradeRecipe`
    * Deprecated for removal; replaced by `SmithingRecipe`, `SmithingTransformRecipe`, and `SmithingTrimRecipe`
* `net.minecraft.data.recipes.UpgradeRecipeBuilder` -> `LegacyUpgradeRecipeBuilder`
    * Deprecated for removal; replaced by `SmithingTransformRecipeBuilder` and `SmithingTrimRecipeBuilder`
* `net.minecraft.data.worldgen.biome.Biomes` -> `BiomeData`
* `net.minecraft.world.item.Wearable` -> `Equipable`
* `net.minecraft.world.item.crafting.Recipe#assemble(C)` and `#getResultItem()` -> `#assemble(C, RegistryAccess)` and `#getResultItem#(RegistryAccess)`
* `net.minecraft.world.entity.Entity#rideableUnderWater` -> `#dismountsUnderwater`
* `net.minecraft.world.entity.PlayerRideableJumping#canJump(Player)` -> `#canJump()`
* `net.minecraft.data.tags.TagsProvider` now can take in a `CompletableFuture<TagsProvider.TagLookup<T>>` if tags need to be accessed from other `TagProvider`s. A lookup can be obtained via `TagsProvider#contentsGetter`.
* `net.minecraft.world.item.ArmorItem` now take in a delegate to the `EquipmentSlot` called `ArmorItem$Type` within their constructors and associated references.
* `net.minecraft.core.BlockPos` constructors that take in doubles should migrate to using `BlockPos#containing`.
* `net.minecraft.world.level.biome.MobSpawnSettings$SpawnerData#minCount` and `#maxCount` must be positive values.

### Removals

* `net.minecraft.world.level.biome.Biome#hasDownfall` as it was only used by the `#isHumid` check, now `#hasPrecipitation`.
* `com.mojang.blaze3d.systems.RenderSystem#enableTexture` and `#disableTexture` as they have not been doing anything other than adding extra cycles to the logic execution.


# Minecraft 1.19.3 -> 1.19.4 Forge Mod Migration Primer

This is a high level, non-exhaustive overview on how to migrate your mod from 1.19.3 to 1.19.4 using Forge.

This primer is licensed under the [Creative Commons Attribution 4.0 International](http://creativecommons.org/licenses/by/4.0/), so feel free to use it as a reference and leave a link so that other readers can consume the primer.

If there's any incorrect or missing information, please file an issue on this repository or ping @ChampionAsh5357 in the Neoforged Discord server.

## Vanilla Changes

Vanilla changes are listed [here](./index.md).

## Creative Tabs

Custom creative tabs from the previous primer are now slightly modified to take in the two parameters: 

```java
// Registered on the MOD event bus
// Assume we have RegistryObject<Item> and RegistryObject<Block> called ITEM and BLOCK
@SubscribeEvent
public void buildContents(CreativeModeTabEvent.Register event) {
  event.registerCreativeModeTab(new ResourceLocation(MOD_ID, "example"), builder ->
    // Set name of tab to display
    builder.title(Component.translatable("item_group." + MOD_ID + ".example"))
    // Set icon of creative tab
    .icon(() -> new ItemStack(ITEM.get()))
    // Add default items to tab
    .displayItems((params, output) -> {
      output.accept(ITEM.get());
      output.accept(BLOCK.get());
    })
  );
}
```

Additionally, `net.minecraftforge.event.CreativeModeTabEvent$BuildContents` can access the parameters via `#getParameters`. The other methods now delegate to the parameters for better compatibility when updating from 1.19.3.

## Spawn Events Refactor

As of 45.0.23, spawn events have been completely refactored. For starters, `LivingSpawnEvent` has been renamed to `MobSpawnEvent`. Even further `CheckSpawn` and `SpecialSpawn` hae been merged into a single event: `FinalizeSpawn`. `FinalizeSpawn` can be canceled to prevent `Mob#finalizeSpawn` from being called while the entity itself can be prevent using` FinalizeSpawn#setSpawnCancelled`.

If you want to learn more about this event and the technical changes, see the [blog post](https://blog.minecraftforge.net/breaking/spawnevents/).

## New Registries

Forge has added a new static registry for `ItemDisplayContext`s aptly named `forge:display_contexts` for registering perspectives an item may be rendered within, replacing custom `TransformType`s. There can only be at most 256 display contexts.

## Sprite Registration Refactoring

As of 45.0.25, all `net.minecraftforge.client.event.RegisterParticleProvidersEvent#register` methods have been deprecated for removal, opting to switch to method names which better specify their usecase:

* `#register(ParticleType, ParticleProvider)` -> `#registerSpecial`
* `#register(ParticleType, ParticleProvider$Sprite)` -> `#registerSprite`
* `#register(ParticleType, ParticleEngine$SpriteParticleRegistration)` -> `#registerSpriteSet`

## Minor Changes

* `net.minecraftforge.fml.CrashReportCallables` can now be supplied a callable which will append to the system report when the boolean supplier returns `true`.

# Minecraft 1.19.2 -> 1.19.3 Mod Migration Primer

This is a high level, non-exhaustive overview on how to migrate your mod from 1.19.2 to 1.19.3 using. This does not look at any specific mod loader, just the changes to the vanilla classes.

This primer is licensed under the [Creative Commons Attribution 4.0 International](http://creativecommons.org/licenses/by/4.0/), so feel free to use it as a reference and leave a link so that other readers can consume the primer.

If there's any incorrect or missing information, please file an issue on this repository or ping @ChampionAsh5357 in the Neoforged Discord server.

## Feature Flags

Feature Flags are a behavior/logic toggle for blocks and items. They determine whether certain actions or generations related to the object can trigger (e.g. bamboo boats can only be placed when the `minecraft:update_1_20` flag is enabled).

Vanilla currently provides three flags as of this version (via `FeatureFlags`):
- `minecraft:vanilla`: For everything in vanilla minecraft
- `minecraft:bundle`: For the bundle feature pack and in the creative tab
- `minecraft:update_1_20`: For objects related to the upcoming 1.20 release

You can force an item or block to require one of these features by adding it as a property via `#requiredFeatures`. This will affect all behavior and logic associated with the object. 

```java
// In some supplied instance
new Block(BlockBehaviour.Properties.of(/*...*/).requiredFeatures(/*features here*/));

new Item(new Item.Properties().requiredFeatures(/*features here*/));
```

If you only want to affect some behavior or logic associated with the object, then you will need to check `FeatureFlagSet#contains` yourself on whatever system you are using.

> Do not add your own `FeatureFlag`s. Currently, they are limited to only 6 and are hardcoded to vanilla's highly-specific implementation.

## Registries

Registries have received a significant overhaul, resulting in better stability.

### No In-Code Entries in Dynamic Registries

Dynamic registries such as dimension types or features can no longer be declared in-code. Instead, a JSON of each must now be provided.

### Key and Registry Locations

The locations of the registry keys and the registries themselves have moved. Registry keys are now in `net.minecraft.core.registries.Registries`. Static registries are now in `net.minecraft.core.registries.BuiltInRegistries`.

Dynamic registries can only be obtained from the `RegistryAccess` from the `MinecraftServer`. There is no more static access to it, such as using `RegistryAccess#builtinCopy`.

### Registry Order

The registry order has changed as well due to a bunch of stability fixes. This should not matter in most cases if you want to support registry replacement as a `Supplier` of the value is necessary, but this will be noted down regardless. This is the current order in which registries load:

- `minecraft:sound_event`
- `minecraft:fluid`
- `minecraft:block`
- `minecraft:attribute`
- `minecraft:mob_effect`
- `minecraft:particle_type`
- `minecraft:item`
- `minecraft:entity_type`
- `minecraft:sensor_type`
- `minecraft:memory_module_type`
- `minecraft:potion`
- `minecraft:game_event`
- `minecraft:enchantment`
- `minecraft:block_entity_type`
- `minecraft:painting_variant`
- `minecraft:stat_type`
- `minecraft:custom_stat`
- `minecraft:chunk_status`
- `minecraft:rule_test`
- `minecraft:pos_rule_test`
- `minecraft:menu`
- `minecraft:recipe_type`
- `minecraft:recipe_serializer`
- `minecraft:position_source_type`
- `minecraft:command_argument_type`
- `minecraft:villager_type`
- `minecraft:villager_profession`
- `minecraft:point_of_interest_type`
- `minecraft:schedule`
- `minecraft:activity`
- `minecraft:loot_pool_entry_type`
- `minecraft:loot_function_type`
- `minecraft:loot_condition_type`
- `minecraft:loot_number_provider_type`
- `minecraft:loot_nbt_provider_type`
- `minecraft:loot_score_provider_type`
- `minecraft:float_provider_type`
- `minecraft:int_provider_type`
- `minecraft:height_provider_type`
- `minecraft:block_predicate_type`
- `minecraft:worldgen/carver`
- `minecraft:worldgen/feature`
- `minecraft:worldgen/structure_processor`
- `minecraft:worldgen/structure_placement`
- `minecraft:worldgen/structure_piece`
- `minecraft:worldgen/structure_type`
- `minecraft:worldgen/placement_modifier_type`
- `minecraft:worldgen/block_state_provider_type`
- `minecraft:worldgen/foliage_placer_type`
- `minecraft:worldgen/trunk_placer_type`
- `minecraft:worldgen/root_placer_type`
- `minecraft:worldgen/tree_decorator_type`
- `minecraft:worldgen/feature_size_type`
- `minecraft:worldgen/biome_source`
- `minecraft:worldgen/chunk_generator`
- `minecraft:worldgen/material_condition`
- `minecraft:worldgen/material_rule`
- `minecraft:worldgen/density_function_type`
- `minecraft:worldgen/structure_pool_element`
- `minecraft:cat_variant`
- `minecraft:frog_variant`
- `minecraft:banner_pattern`
- `minecraft:instrument`
- `minecraft:worldgen/biome`

## Existing Creative Tabs

`CreativeModeTab`s are no longer set through a property on the item; they are harcoded onto the creative tab itself.

## The JOML Library

Mojang has migrated from using their own math classes for rendering to using [JOML](https://github.com/JOML-CI/JOML), a open source math library for OpenGL. You can fix most of these issues by changing the package of the vector, matrix, etc. to `org.joml`; however it is not a 1 to 1 translation. Some changes are functional: `Matrix*` methods modify a mutable instance instead of creating a new one, or `Quaternionf` using `AxisAngle*` instead of `Vector3f`.

## SoundEvent

`SoundEvent`s have slightly changed in this version. Specifically, they are constructed through a static method constructor which introduces a new system: fixed range sound. The standard `SoundEvent` from previous versions can be constructed using `SoundEvent#createVariableRangeEvent`, whose range changes depending on the volume of the sound with a minimum of 16 blocks. Sounds constructed from `SoundEvent#createFixedRangeEvent` can be heard from the range specified, regardless of the volume.

```java
// In some supplied instance

// Will change depending on volume, minimum 16 blocks
SoundEvent.createVariableRangeEvent("sound.example_mod.variable_example");

// Will only be heard within specified range (e.g. 5 blocks)
SoundEvent.createFixedRangeEvent("sound.example_mod.fixed_example", 5f);
```

## Packs and PackResources

`Pack`s and `PackResources` have changed slightly between the two versions.

First `PackResources#hasResource` no longer exists. Instead `#getResource` should be checked for nullability instead before getting the `InputStream` from the `IoSupplier`.

```java
// Given some PackResources resources
var io = resources.getResource(PackType.SERVER_DATA, new ResourceLocation(/**/));

if (io != null) {
  InputStream input = io.get();
  // Same as before
}
```

Additionally, `#getResources` has been replaced by `#listResources` with the `ResourceLocation` predicate changing to a `PackResources$ResourceOutput` which acts as a consumer taking in the resource and the `IoSupplier` the resource would be located in.

Finally, `Pack` has become a private constructor, only constructed through one of the static constructors `#readMetaAndCreate`, which acts similarly to the 1.19.2 `#create`, or `#create`.

## Data Generators

Data Generators have changed quite a bit from how providers are added to the providers themselves.

Starting off, all providers now take in a `PackOutput` compared to the `DataGenerator` itself. The `PackOutput` simply specifies the directory where the pack will be generated. Because of this change, the `#addProvider` method now takes in a function that takes in a `PackOutput` and constructs a `DataProvider`.

### Data Providers and CompleteableFutures

All `DataProvider`s now return a `CompletableFuture` on `#run`, which is used to write the data to its apropriate file.

### RecipeProvider and RecipeCategory

`RecipeProvider`s now construct recipes in `#buildRecipes`. Additionally, each recipe builder, besides dynamic recipes, must specify a `RecipeCategory` which determines the subdirectory on where the recipe will be generated.

```java
// In RecipeProvider#buildRecipes(writer)
ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
  .pattern("a a") // Create recipe pattern
  .define('a', item) // Define what the symbol represents
  .unlockedBy("criteria", criteria) // How the recipe is unlocked
  .save(writer); // Add data to builder
```

### TagsProvider and IntrinsicHolderTagsProvider

`TagsProvider`s can only add objects through their `ResourceKey`. To add objects directly, an `IntrinsicHolderTagsProvider` should be used instead. This takes in a function which extracts a key from the object itself. Vanilla creates these for `Item`s, `EntityType`s, `Fluid`s, and `GameEvent`s. Any others need to specify the function.

```java
// Subtype of `IntrinsicHolderTagsProvider`
public AttributeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
  super(
    output,
    Registries.ATTRIBUTE,
    registries,
    attribute -> BuiltInRegistries.ATTRIBUTE.getResourceKey(attribute).get()
  );
}
```

### LootTableProvider

`LootTableProvider` has received a massive overhaul, no longer needing to subtype the class and instead just supply arguments to the constructor. In addition to the `PackOutput`, it takes in a set of table names to validate whether they have been created and a list of `SubProviderEntry`s, which are used to generate the tables for each `LootContextParamSet`.

A `LootTableSubProvider` is used to generate the loot tables in the `SubProviderEntry`, essentially functioning the same as a `Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>`.

```java
public class ExampleSubProvider implements LootTableSubProvider {

  // Used to create a factory method for the wrapping Supplier
  public ExampleSubProvider() {}

  // The method used to generate the loot tables
  @Override
  public void generate(BiConsumer<ResourceLocation, LootTable.Builder> writer) {
    // Generate loot tables here by calling writer#accept
  }
}

// In the list passed into the LootTableProvider constructor
new LootTableProvider.SubProviderEntry(
  ExampleSubProvider::new,
  // Loot table generator for the 'empty' param set
  LootContextParamSets.EMPTY
)
```

The overrides for blocks and entities still exist being called `BlockLootSubProvider` and `EntityLootSubProvider` respectively. They both take in the feature flags, with the block sub provider taking in a set of items to which are resistant to explosions. The method used to generate the tables have been changed from `#addTables` to `#generate`. You still need to override the `#getKnown*` methods for validation.

```java
// In some BlockLootSubProvider subclass
public MyBlockLootSubProvider() {
  super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags());
}

@Override
public void generate() {
  // Add tables here
}
```

### AdvancementProvider

`AdvancementProvider` has also received a massive overhaul, no longer needing to subtype the class and instead just supply arguments to the constructor. In addition to the `PackOutput`, it takes in the holder lookup for registries and the `AdvancementSubProvider`s, which are used to generate the advancements.

## Rendering Changes

Rendering has changed a massive amount in this update. The most common being those explicitly mentioned by minecraft such as item and block textures only being in `models/item` and `models/block` respectively or the asynchronous loading and writing of assets and data. Textures for items and blocks are also required to be in `textures/item` and `textures/block` respectively as textures are loaded before model are processed. These texture directories are specified in JSON in the `atlases` directory.

This section will try to cover all the updates in 1.19.3 as these issues get fixed.

## Behaviors by DataFixerUpper

Behaviors are being migrated to use a new system built around the abstractions and overengineering provided by DataFixerUpper (DFU). As these systems tend to be complex and convoluted, this will only provide a brief overview of the changes and implementations.

### BehaviorControl

The `Behavior` class is no longer the base for behavior logic. That has been relegated to `BehaviorControl`: an interface which `Behavior` implements. `BehaviorControl` checks whether the logic can start (`#tryStart`), its ticking and stop check (`#tickOrStop`), and finally the stop logic (`#doStop`).

Some classes affected by this are `DoNothing` and `GateBehavior` which implements `BehaviorControl`.

### OneShot

`OneShot` is a behavior control which executes once via a `Trigger` statement. If the `Trigger#trigger` returns true, the behavior control is executed; though it only sets the state to running for a single tick.

### BehaviorBuilder and TriggerGate

BehaviorBuilder and TriggerGate are essentially the DFU-implemented classes which creates `OneShot` triggers such as waking up or strolling to a point of interest.

For a basic understanding, a `BehaviorBuilder` acts similarly to a `RecordCodecBuilder`: it creates the behavior instance, takes in the necessary `MemoryModuleType`s and whether it is registered, present, or absent, and then applies to construct a `Trigger`. The `BehaviorBuilder` also contains methods to make triggers act sequentially or if a given predicate is met.

`TriggerGate` is an holder of state methods for `GateBehavior`s. It either triggers a single instance at random (`#triggerOneShuffled`) or executed based on the ordering and run policy selected (`#triggerGate`).

## Minor Changes

This is a list of minor changes which probably won't affect the modding experience but is convenient to know anyways.

### Entity#getAddEntityPacket no longer abstract

`Entity#getAddEntityPacket` is no longer abstract, instead defaulting to the `ClientboundAddEntityPacket`.

### AbstractContainerMenu#stillValid Static Method

`AbstractContainerMenu` has now migrated from using the `Container` to check whether the menu could still be kept open to using a static method called `#stillValid` in the instance of the same name. To use this method, you need to provide a `ContainerLevelAccess`, the player, and the `Block` the menu is attached to.

```java
// Client menu constructor
public MyMenuAccess(int containerId, Inventory playerInventory) {
  this(containerId, playerInventory, ContainerLevelAccess.NULL);
}

// Server menu constructor
public MyMenuAccess(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
  // ...
}

// Assume this menu is attached to RegistryObject<Block> MY_BLOCK
@Override
public boolean stillValid(Player player) {
  return AbstractContainerMenu.stillValid(this.access, player, MY_BLOCK.get());
}
```

### BlockStateProvider#simpleBlockWithItem

The `BlockStateProvider` now contains a new method to generate a simple block state for a single block model along with an associated item model.

```java
// In some BlockStateProvider#registerStatesAndModels
// Assume there is a RegistryObject<Block> BLOCK
this.simpleBlockWithItem(BLOCK.get(), this.cubeAll(BLOCK.get()));
```

### Removal of Context-Sensitive Tree Growers

The context-sensitive methods within `AbstractMegaTreeGrower` and `AbstractTreeGrower` have been removed and as such no longer have access to the world or position as the methods now return the keys of the `ConfiguredFeature` rather than the feature itself.

### Music, now with Holders

The `Music` class, used to provide a background track during different situations, now takes in a `Holder<SoundEvent>` rather than the `SoundEvent` itself.

### KeyboardHandler#sendRepeatsToGui removed

Minecraft has removed `KeyboardHandler#sendRepeatsToGui` when a key was held down such that it repeated within a GUI. There is no replacement, so the handler will now always send repeats to the GUI.

## Renames and Refactors

The following classes were renamed or refactored within Minecraft:

- `net.minecraft.client.gui.components.Widget` -> `net.minecraft.client.gui.components.Renderable`
- `net.minecraft.data.loot.BlockLoot` -> `net.minecraft.data.loot.BlockLootSubProvider`
- `net.minecraft.data.loot.EntityLoot` -> `net.minecraft.data.loot.EntityLootSubProvider`


# Minecraft 1.19.2 -> 1.19.3 Forge Mod Migration Primer

This is a high level, non-exhaustive overview on how to migrate your mod from 1.19.2 to 1.19.3 using Forge.

This primer is licensed under the [Creative Commons Attribution 4.0 International](http://creativecommons.org/licenses/by/4.0/), so feel free to use it as a reference and leave a link so that other readers can consume the primer.

If there's any incorrect or missing information, please file an issue on this repository or ping @ChampionAsh5357 in the Neoforged Discord server.

## Vanilla Changes

Vanilla changes are listed [here](./index.md).

## Registry Order

This is the current order in which registries are loaded:

- `minecraft:instrument` (Everything above is the same as vanilla)
- `forge:biome_modifier_serializers`
- `forge:entity_data_serializers`
- `forge:fluid_type`
- `forge:global_loot_modifier_serializers`
- `forge:holder_set_type`
- `forge:structure_modifier_serializers`
- `minecraft:worldgen/biome`

## Existing Creative Tabs

`CreativeModeTab`s are no longer set through a property on the item; they are harcoded onto the creative tab itself. To get around this, the `CreativeModeTabEvent` was added, allowing a modder to create a creative tab or add items to an existing creative tab. All events are on the **mod** event bus.

### Adding Items

Items can be added to a creative tab via `CreativeModeTabEvent$BuildContents`. The event contains the tab to add contents to, the set feature flags, and whether the user as OP permissions. An item can be added to the creative tab by calling `#accept` or `#acceptAll`. If you want to inject between an item already in the creative tab, you can call `MutableHashedLinkedMap#putBefore` or `MutableHashedLinkedMap#putAfter` within the provided entry list (`#getEntries`).

```java
// Registered on the MOD event bus
// Assume we have RegistryObject<Item> and RegistryObject<Block> called ITEM and BLOCK
@SubscribeEvent
public void buildContents(CreativeModeTabEvent.BuildContents event) {
  // Add to ingredients tab
  if (event.getTab() == CreativeModeTabs.INGREDIENTS) {
    event.accept(ITEM);
    event.accept(BLOCK); // Takes in an ItemLike, assumes block has registered item
  }
}
```

### Custom Creative Tab

A custom `CreativeModeTab` can be created via `CreativeModeTabEvent$Register#registerCreativeModeTab`. This takes in the name of the tab along with a consumer of the builder. An additional overload is specified to determine between which tabs this tab should be located.

```java
// Registered on the MOD event bus
// Assume we have RegistryObject<Item> and RegistryObject<Block> called ITEM and BLOCK
@SubscribeEvent
public void buildContents(CreativeModeTabEvent.Register event) {
  event.registerCreativeModeTab(new ResourceLocation(MOD_ID, "example"), builder ->
    // Set name of tab to display
    builder.title(Component.translatable("item_group." + MOD_ID + ".example"))
    // Set icon of creative tab
    .icon(() -> new ItemStack(ITEM.get()))
    // Add default items to tab
    .displayItems((enabledFlags, populator, hasPermissions) -> {
      populator.accept(ITEM.get());
      populator.accept(BLOCK.get());
    })
  );
}
```

## Packs and PackResources

`Pack` has become a private constructor, only constructed through one of the static constructors `#readMetaAndCreate`, which acts similarly to the 1.19.2 `#create`, or `#create`. A common case that this will encounter is for the `AddPackFindersEvent`. Here are the list of changes:

- `Supplier<PackResources>` has turned into `Pack$ResourcesSupplier` which takes in the pack id.
- `PackType` is now a parameter to check whether the pack is compatible for the current version.
- The description, feature flags, and hidden check is stored on the `Pack$Info`, which is either obtained from the metadata file or constructed from the record.

## Data Generators

Data Generators have changed quite a bit from how providers are added to the providers themselves.

Starting off, all providers now take in a `PackOutput` compared to the `DataGenerator` itself. The `PackOutput` simply specifies the directory where the pack will be generated. Because of this change, the `#addProvider` method now takes in either the provider instance or a function that takes in a `PackOutput` and constructs a `DataProvider`.

```java
// On the mod event bus
@SubscribeEvent
public void gatherData(GatherDataEvent event) {
  event.addProvider(true, output -> /*create provider here*/);
}
```

### The Lookup Provider

`GatherDataEvent` now provides a `CompletableFuture` containing a `HolderLookup$Provider`, which is used to get registries and their objects. This can be obtained via `#getLookupProvider`.

### TagsProvider and IntrinsicHolderTagsProvider

Forge adds a tag provider for `Block`s. Any others need to specify the function.

```java
// Subtype of `IntrinsicHolderTagsProvider`
public AttributeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper fileHelper) {
  super(
    output,
    ForgeRegistries.Keys.ATTRIBUTES,
    registries,
    attribute -> ForgeRegistries.ATTRIBUTES.getResourceKey(attribute).get(),
    MOD_ID,
    fileHelper
  );
}
```

### AdvancementProvider

 For ease of access to the `ExistingFileHelper`, Forge has added an extension on the provider aptly named `ForgeAdvancementProvider` which takes in `ForgeAdvancementProvider$AdvancementGenerator`s which contain the `ExistingFileHelper` as a parameter.

```java
// On the MOD event bus
@SubscribeEvent
public void gatherData(GatherDataEvent event) {
  event.getGenerator().addProvider(
    // Tell generator to run only when server data are generating
    event.includeServer(),
    output -> new ForgeAdvancementProvider(
      output,
      event.getLookupProvider(),
      event.getExistingFileHelper(),
      // Sub providers which generate the advancements
      List.of(subProvider1, subProvider2, /*...*/)
    )
  );
}
```

The `ForgeAdvancementProvider$AdvancementGenerator` is responsible for generating advancements. To be able to effectively generate advancements the `ExistingFileHelper` should be passed in such that the advancement can be built using the `Advancement$Builder#save` method which takes it in.

```java
// In some ForgeAdvancementProvider$AdvancementGenerator or as a lambda reference

@Override
public void generate(HolderLookup.Provider registries, Consumer<Advancement> writer, ExistingFileHelper existingFileHelper) {
  // Build advancements here
}
```

### LootTableProvider

`LootTableProvider` has received a massive overhaul, no longer needing to subtype the class and instead just supply arguments to the constructor. In addition to the `PackOutput`, it takes in a set of table names to validate whether they have been created and a list of `SubProviderEntry`s, which are used to generate the tables for each `LootContextParamSet`.

```java
// On the MOD event bus
@SubscribeEvent
public void gatherData(GatherDataEvent event) {
  event.getGenerator().addProvider(
    // Tell generator to run only when server data are generating
    event.includeServer(),
    output -> new MyLootTableProvider(
      output,
      // Specify registry names of tables that are required to generate, or can leave empty
      Collections.emptySet(),
      // Sub providers which generate the loot
      List.of(subProvider1, subProvider2, /*...*/)
    )
  );
}
```

### DatapackBuiltinEntriesProvider and the removal of JsonCodecProvider#forDatapackRegistry

`JsonCodecProvider#forDatapackRegistry` was removed in favor of using the vanilla `RegistriesDatapackGenerator` for generating dynamic registry objects. To expand upon this provider, Forge introduced the `DatapackBuiltinEntriesProvider`, which can take in a `RegistrySetBuilder` to generate the specific dynamic objects to use, and a set of mod ids to determine which mod's dynamic registry objects to generate.

```java
// On the MOD event bus
@SubscribeEvent
public void gatherData(GatherDataEvent event) {
  event.getGenerator().addProvider(
    // Tell generator to run only when server data are generating
    event.includeServer(),
    output -> new DatapackBuiltinEntriesProvider(
      output,
      event.getLookupProvider(),
      // The objects to generate
      new RegistrySetBuilder()
        .add(Registries.NOISE_SETTINGS, context -> {
          // Generate noise generator settings
          context.register(
            ResourceKey.create(Registries.NOISE_SETTINGS, new ResourceLocation(MOD_ID, "example_settings")),
            NoiseGeneratorSettings.floatingIslands(context)
          );
        }),
      // Generate dynamic registry objects for this mod
      Set.of(MOD_ID)
    )
  );
}
```

## Resolving nested models

To resolve models that are nested within other models, `IUnbakedGeometry#resolveParents` was added. This is a defaulted method, so no issues should occur in custom loaders; however, those who are resolving models within models will need to pivot to use this new method.

## ModelEvent$ModifyBakingResult

`ModelEvent$ModifyBakingResult` is an event fired on the **mod** event bus used to handle the caching of state -> model map done previously in `ModelEvent$BakingCompleted`. The usecases typically stem from users who need to make adjustments to models due to cases where custom loaders are not possible or that the context provided is insufficient.

Due to the event being fired on a worker thread, you should only access those objects provided by the event itself as it is otherwise unsafe.

## Entity#getAddEntityPacket no longer abstract

`Entity#getAddEntityPacket` is no longer abstract, instead defaulting to the `ClientboundAddEntityPacket`. If you need to send additional data on entity creation, you should still use `NetworkHooks#getEntitySpawningPacket`.

## Minor Refactors

- `net.minecraft.data.tags.BlockTagsProvider` -> `net.minecraftforge.common.data.BlockTagsProvider`




