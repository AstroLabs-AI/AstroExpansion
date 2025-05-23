package com.astrolabs.astroexpansion.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class MultiblockMatcher {
    
    public static IMultiblockMatcher block(Block block) {
        return (level, pos, state) -> state.is(block);
    }
    
    public static IMultiblockMatcher tag(TagKey<Block> tag) {
        return (level, pos, state) -> state.is(tag);
    }
    
    public static IMultiblockMatcher predicate(Predicate<BlockState> predicate) {
        return (level, pos, state) -> predicate.test(state);
    }
    
    public static IMultiblockMatcher any(IMultiblockMatcher... matchers) {
        return (level, pos, state) -> {
            for (IMultiblockMatcher matcher : matchers) {
                if (matcher.matches(level, pos, state)) {
                    return true;
                }
            }
            return false;
        };
    }
}