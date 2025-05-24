package com.astrolabs.astroexpansion.common.blocks;

import com.astrolabs.astroexpansion.common.blockentities.ComponentAssemblerBlockEntity;
import com.astrolabs.astroexpansion.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.util.RandomSource;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class ComponentAssemblerBlock extends BaseEntityBlock {
    public ComponentAssemblerBlock(Properties properties) {
        super(properties);
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ComponentAssemblerBlockEntity(pos, state);
    }
    
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ComponentAssemblerBlockEntity assembler) {
            // Check if machine is processing (simplified check)
            boolean isActive = assembler.getEnergyStorage().getEnergyStored() > 0;
            
            if (isActive) {
                // Crafting particles
                com.astrolabs.astroexpansion.client.particle.MachineParticles.spawnCraftingParticles(level, pos, random);
                
                // Mechanical sound
                if (random.nextDouble() < 0.15) {
                    level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        net.minecraft.sounds.SoundEvents.PISTON_EXTEND, net.minecraft.sounds.SoundSource.BLOCKS,
                        0.3F, 1.0F + random.nextFloat() * 0.1F, false);
                }
            }
        }
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof ComponentAssemblerBlockEntity) {
                NetworkHooks.openScreen(((ServerPlayer) player), (ComponentAssemblerBlockEntity) entity, pos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }
        
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
    
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ComponentAssemblerBlockEntity) {
                ((ComponentAssemblerBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide()) {
            return createTickerHelper(type, ModBlockEntities.COMPONENT_ASSEMBLER.get(), ComponentAssemblerBlockEntity::serverTick);
        }
        return null;
    }
}