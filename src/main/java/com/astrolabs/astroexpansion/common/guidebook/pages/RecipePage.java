package com.astrolabs.astroexpansion.common.guidebook.pages;

import com.astrolabs.astroexpansion.api.guidebook.IGuidePage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.List;

/**
 * Page showing an interactive recipe with JEI/REI integration
 */
public class RecipePage implements IGuidePage {
    private final ResourceLocation recipeId;
    private final int pageNumber;
    private Recipe<?> cachedRecipe;
    private long animationTime = 0;
    private int hoveredSlot = -1;
    
    public RecipePage(ResourceLocation recipeId, int pageNumber) {
        this.recipeId = recipeId;
        this.pageNumber = pageNumber;
    }
    
    @Override
    public String getType() {
        return "recipe";
    }
    
    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        if (cachedRecipe == null) {
            cachedRecipe = Minecraft.getInstance().level.getRecipeManager().byKey(recipeId).orElse(null);
            if (cachedRecipe == null) return;
        }
        
        // Draw crafting grid background
        int gridX = x + width / 2 - 58;
        int gridY = y + 20;
        
        RenderSystem.enableBlend();
        graphics.blit(new ResourceLocation("textures/gui/container/crafting_table.png"), 
            gridX, gridY, 29, 16, 116, 54);
        
        // Update animation
        animationTime++;
        
        // Check mouse hover
        hoveredSlot = -1;
        int slotX = gridX + 1;
        int slotY = gridY + 1;
        
        // Render ingredients
        if (cachedRecipe instanceof ShapedRecipe shaped) {
            int recipeWidth = shaped.getWidth();
            int recipeHeight = shaped.getHeight();
            List<Ingredient> ingredients = shaped.getIngredients();
            
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    int index = row * recipeWidth + col;
                    if (row < recipeHeight && col < recipeWidth && index < ingredients.size()) {
                        Ingredient ingredient = ingredients.get(index);
                        renderIngredient(graphics, ingredient, slotX + col * 18, slotY + row * 18, mouseX, mouseY, row * 3 + col);
                    }
                }
            }
        }
        
        // Render output
        ItemStack output = cachedRecipe.getResultItem(Minecraft.getInstance().level.registryAccess());
        int outputX = gridX + 95;
        int outputY = gridY + 19;
        
        graphics.renderItem(output, outputX, outputY);
        graphics.renderItemDecorations(Minecraft.getInstance().font, output, outputX, outputY);
        
        if (mouseX >= outputX && mouseX < outputX + 16 && mouseY >= outputY && mouseY < outputY + 16) {
            hoveredSlot = 9;
        }
        
        // Render progress arrow
        graphics.blit(new ResourceLocation("textures/gui/container/crafting_table.png"),
            gridX + 61, gridY + 19, 177, 14, 22, 15);
        
        // Render recipe name
        Component name = output.getHoverName();
        int nameWidth = Minecraft.getInstance().font.width(name);
        graphics.drawString(Minecraft.getInstance().font, name, 
            x + width / 2 - nameWidth / 2, y + 5, 0x000000, false);
        
        // Render crafting time and energy if applicable
        if (cachedRecipe instanceof CraftingRecipe) {
            Component info = Component.literal("Crafting Table Recipe");
            graphics.drawString(Minecraft.getInstance().font, info, 
                gridX, gridY + 60, 0x666666, false);
        }
    }
    
    private void renderIngredient(GuiGraphics graphics, Ingredient ingredient, int x, int y, int mouseX, int mouseY, int slot) {
        ItemStack[] items = ingredient.getItems();
        if (items.length == 0) return;
        
        // Cycle through items for animation
        int index = (int) ((animationTime / 20) % items.length);
        ItemStack stack = items[index];
        
        graphics.renderItem(stack, x, y);
        
        if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
            hoveredSlot = slot;
            graphics.fill(x, y, x + 16, y + 16, 0x80FFFFFF);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hoveredSlot >= 0 && Screen.hasShiftDown()) {
            // Open in JEI/REI if available
            return true;
        }
        return false;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return false;
    }
    
    @Override
    public void onOpened(Player player) {
        animationTime = 0;
    }
    
    @Override
    public void onClosed(Player player) {
    }
    
    @Override
    public void tick() {
    }
    
    @Override
    public Component getTooltip(int mouseX, int mouseY) {
        if (hoveredSlot >= 0 && cachedRecipe != null) {
            if (hoveredSlot == 9) {
                // Output slot
                return cachedRecipe.getResultItem(Minecraft.getInstance().level.registryAccess()).getHoverName();
            } else if (cachedRecipe instanceof ShapedRecipe shaped) {
                // Input slot
                List<Ingredient> ingredients = shaped.getIngredients();
                int row = hoveredSlot / 3;
                int col = hoveredSlot % 3;
                int index = row * shaped.getWidth() + col;
                
                if (index < ingredients.size()) {
                    ItemStack[] items = ingredients.get(index).getItems();
                    if (items.length > 0) {
                        int itemIndex = (int) ((animationTime / 20) % items.length);
                        return items[itemIndex].getHoverName();
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public boolean isInteractive() {
        return true;
    }
    
    @Override
    public int getPageNumber() {
        return pageNumber;
    }
}