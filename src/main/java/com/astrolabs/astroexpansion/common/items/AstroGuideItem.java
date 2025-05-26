package com.astrolabs.astroexpansion.common.items;

import com.astrolabs.astroexpansion.AstroExpansion;
//import com.astrolabs.astroexpansion.client.gui.guide.AstroGuideScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * The AstroExpansion Guide Book item
 * Opens the custom AstroExpansion guide system
 */
public class AstroGuideItem extends Item {
    public AstroGuideItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            // TODO: Open our custom guide system (temporarily disabled)
            AstroExpansion.LOGGER.info("AstroGuide: Custom guide system temporarily disabled during development");
            /*
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft.getInstance().setScreen(new AstroGuideScreen());
            });
            */
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
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
    
    private float getCompletion(@Nonnull ItemStack stack) {
        // TODO: Get from player data
        return 0.0f;
    }
    
    @Override
    public boolean isFoil(@Nonnull ItemStack stack) {
        // Make it glow when fully completed
        return getCompletion(stack) >= 1.0f;
    }
}