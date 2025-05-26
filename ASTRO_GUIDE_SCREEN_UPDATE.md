# AstroGuideScreen Update

## Changes Made

The AstroGuideScreen has been updated to work with the newer Minecraft rendering system in 1.20.1. The primary changes involved migrating from the older PoseStack-based rendering to the newer GuiGraphics API, as well as updating the Button implementation to use the new Builder pattern.

### Specific Changes:

1. **Import Changes**:
   - Removed: `import com.mojang.blaze3d.vertex.PoseStack;`
   - Added: `import net.minecraft.client.gui.GuiGraphics;`
   - Added: `import net.minecraft.client.gui.components.Button;`
   - Changed: `import org.jetbrains.annotations.NotNull;` to `import javax.annotation.Nonnull;`

2. **Render Method Changes**:
   - Updated the render method signature:
     ```java
     // Old
     public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick)
     
     // New
     public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
     ```

3. **Drawing Method Updates**:
   - Changed `this.renderBackground(poseStack)` to `this.renderBackground(guiGraphics)`
   - Changed `this.blit(poseStack, ...)` to `guiGraphics.blit(TEXTURE, ...)`
   - Changed `font.draw(poseStack, ...)` to `guiGraphics.drawString(font, ...)`

4. **GuidePage Class Updates**:
   - Updated the GuidePage.render method to use GuiGraphics instead of PoseStack
   - Updated all drawing code in the GuidePage class to use GuiGraphics methods

5. **Button Implementation Updates**:
   - Changed from direct Button constructor to Builder pattern:
     ```java
     // Old
     this.addRenderableWidget(new BookNavigationButton(
         leftPos + 20, 
         topPos + 150, 
         20, 
         20, 
         Component.literal("<"), 
         button -> previousPage()
     ));
     
     // New
     this.addRenderableWidget(Button.builder(
         Component.literal("<"), 
         button -> previousPage()
     )
     .pos(leftPos + 20, topPos + 150)
     .size(20, 20)
     .build());
     ```
   - Removed the custom BookNavigationButton class as it's no longer needed

### Why These Changes Were Needed:

In Minecraft 1.20.1 (with Forge 47.2.0), the rendering system was updated with several API changes:

1. PoseStack was replaced with GuiGraphics, which provides a more streamlined approach to rendering
2. Button constructors were replaced with a fluent builder pattern
3. Drawing methods were moved from various classes into the GuiGraphics class

### Related Files:

This update only affected:
- `src/main/java/com/astrolabs/astroexpansion/client/gui/screens/AstroGuideScreen.java`

### Future Considerations:

- When creating new GUI screens, use the GuiGraphics API pattern instead of the older PoseStack approach
- For buttons and other UI widgets, use the Builder pattern instead of direct constructors
- The rendering methods like blit and drawString are now part of GuiGraphics rather than being separate methods
