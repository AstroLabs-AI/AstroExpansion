package com.astrolabs.astroexpansion.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * The AstroExpansion Guide Book item
 */
public class AstroGuideItem extends Item {
    public AstroGuideItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            // Send message that guide book functionality is not yet implemented
            player.displayClientMessage(Component.translatable("message.astroexpansion.guide_book_wip"), true);
        }
        
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.astroexpansion.astro_guide.tooltip")
            .withStyle(style -> style.withItalic(true).withColor(0x00AAFF)));
        
        if (flag.isAdvanced()) {
            // Show completion percentage if available
            float completion = getCompletion(stack);
            if (completion > 0) {
                tooltip.add(Component.translatable("item.astroexpansion.astro_guide.completion", 
                    String.format("%.1f%%", completion * 100))
                    .withStyle(style -> style.withColor(0xFFAA00)));
            }
        }
    }
    
    private float getCompletion(ItemStack stack) {
        // TODO: Get from player data
        return 0.0f;
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        // Make it glow when fully completed
        return getCompletion(stack) >= 1.0f;
    }
}