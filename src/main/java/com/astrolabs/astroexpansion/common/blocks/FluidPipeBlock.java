package com.astrolabs.astroexpansion.common.blocks;

import com.astrolabs.astroexpansion.common.blockentities.FluidPipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nullable;

public class FluidPipeBlock extends Block implements EntityBlock {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    
    protected static final VoxelShape CORE_SHAPE = Block.box(5, 5, 5, 11, 11, 11);
    protected static final VoxelShape NORTH_SHAPE = Block.box(5, 5, 0, 11, 11, 5);
    protected static final VoxelShape SOUTH_SHAPE = Block.box(5, 5, 11, 11, 11, 16);
    protected static final VoxelShape EAST_SHAPE = Block.box(11, 5, 5, 16, 11, 11);
    protected static final VoxelShape WEST_SHAPE = Block.box(0, 5, 5, 5, 11, 11);
    protected static final VoxelShape UP_SHAPE = Block.box(5, 11, 5, 11, 16, 11);
    protected static final VoxelShape DOWN_SHAPE = Block.box(5, 0, 5, 11, 5, 11);
    
    public FluidPipeBlock() {
        super(Block.Properties.of()
            .mapColor(MapColor.METAL)
            .strength(2.0F)
            .sound(SoundType.METAL)
            .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(NORTH, false)
            .setValue(EAST, false)
            .setValue(SOUTH, false)
            .setValue(WEST, false)
            .setValue(UP, false)
            .setValue(DOWN, false));
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CORE_SHAPE;
        if (state.getValue(NORTH)) shape = Shapes.or(shape, NORTH_SHAPE);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SOUTH_SHAPE);
        if (state.getValue(EAST)) shape = Shapes.or(shape, EAST_SHAPE);
        if (state.getValue(WEST)) shape = Shapes.or(shape, WEST_SHAPE);
        if (state.getValue(UP)) shape = Shapes.or(shape, UP_SHAPE);
        if (state.getValue(DOWN)) shape = Shapes.or(shape, DOWN_SHAPE);
        return shape;
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return getConnectionState(context.getLevel(), context.getClickedPos());
    }
    
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return getConnectionState(level, pos);
    }
    
    private BlockState getConnectionState(LevelAccessor level, BlockPos pos) {
        return this.defaultBlockState()
            .setValue(NORTH, canConnect(level, pos, Direction.NORTH))
            .setValue(EAST, canConnect(level, pos, Direction.EAST))
            .setValue(SOUTH, canConnect(level, pos, Direction.SOUTH))
            .setValue(WEST, canConnect(level, pos, Direction.WEST))
            .setValue(UP, canConnect(level, pos, Direction.UP))
            .setValue(DOWN, canConnect(level, pos, Direction.DOWN));
    }
    
    private boolean canConnect(LevelAccessor level, BlockPos pos, Direction direction) {
        BlockPos neighborPos = pos.relative(direction);
        BlockState neighborState = level.getBlockState(neighborPos);
        
        if (neighborState.getBlock() instanceof FluidPipeBlock) {
            return true;
        }
        
        BlockEntity be = level.getBlockEntity(neighborPos);
        if (be != null) {
            return be.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).isPresent();
        }
        
        return false;
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidPipeBlockEntity(pos, state);
    }
    
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}