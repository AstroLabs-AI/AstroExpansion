package com.astrolabs.astroexpansion.common.blocks;

import com.astrolabs.astroexpansion.common.blockentities.TieredOreWasherBlockEntity;
import com.astrolabs.astroexpansion.common.blocks.machines.base.MachineTier;
import com.astrolabs.astroexpansion.common.blocks.machines.base.TieredMachineBlock;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class TieredOreWasherBlock extends TieredMachineBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    
    public TieredOreWasherBlock(Properties properties, MachineTier tier) {
        super(properties, tier);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(LIT, false));
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
    
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }
    
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }
    
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            // Water particle effects
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.7;
            double z = pos.getZ() + 0.5;
            
            // Spawn water drips
            if (random.nextFloat() < 0.5f) {
                level.addParticle(net.minecraft.core.particles.ParticleTypes.DRIPPING_WATER,
                    x + (random.nextDouble() - 0.5) * 0.5,
                    y,
                    z + (random.nextDouble() - 0.5) * 0.5,
                    0.0, 0.0, 0.0);
            }
            
            // More particles for higher tiers
            if (tier != MachineTier.BASIC && random.nextFloat() < 0.3f) {
                level.addParticle(net.minecraft.core.particles.ParticleTypes.SPLASH,
                    x + (random.nextDouble() - 0.5) * 0.3,
                    y,
                    z + (random.nextDouble() - 0.5) * 0.3,
                    0.0, 0.1, 0.0);
            }
            
            // Water sounds
            if (random.nextDouble() < 0.1) {
                level.playLocalSound(x, y, z,
                    net.minecraft.sounds.SoundEvents.WATER_AMBIENT, net.minecraft.sounds.SoundSource.BLOCKS,
                    0.3F + random.nextFloat() * 0.1F, 0.8F + random.nextFloat() * 0.2F, false);
            }
        }
    }
    
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TieredOreWasherBlockEntity) {
                ((TieredOreWasherBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof TieredOreWasherBlockEntity) {
                NetworkHooks.openScreen(((ServerPlayer) player), (TieredOreWasherBlockEntity) entity, pos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }
        
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TieredOreWasherBlockEntity(pos, state, tier);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        BlockEntityType<?> expectedType = switch (tier) {
            case BASIC -> ModBlockEntities.ORE_WASHER_BASIC.get();
            case ADVANCED -> ModBlockEntities.ORE_WASHER_ADVANCED.get();
            case ELITE -> ModBlockEntities.ORE_WASHER_ELITE.get();
        };
        
        return createTickerHelper(type, expectedType, TieredOreWasherBlockEntity::tick);
    }
    
    @SuppressWarnings("unchecked")
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> serverType, BlockEntityType<?> clientType, BlockEntityTicker<? super E> ticker) {
        return clientType == serverType ? (BlockEntityTicker<A>) ticker : null;
    }
}