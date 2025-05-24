package com.astrolabs.astroexpansion.client.gui.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

/**
 * Custom search bar with enhanced visuals
 */
public class GuideSearchBar extends EditBox {
    public GuideSearchBar(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
        setMaxLength(50);
        setBordered(true);
        setTextColor(0xFFFFFF);
        setTextColorUneditable(0x707070);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean clicked = super.mouseClicked(mouseX, mouseY, button);
        if (clicked && getValue().equals(getMessage().getString())) {
            setValue("");
        }
        return clicked;
    }
    
    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused && getValue().isEmpty()) {
            setValue(getMessage().getString());
        }
    }
}