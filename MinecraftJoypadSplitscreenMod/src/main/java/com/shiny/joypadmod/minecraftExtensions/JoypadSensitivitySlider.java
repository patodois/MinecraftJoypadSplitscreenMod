package com.shiny.joypadmod.minecraftExtensions;

import net.minecraft.client.Minecraft;
import com.shiny.joypadmod.gui.*;
import org.lwjgl.input.Mouse;

public class JoypadSensitivitySlider extends GuiSlider {
    public JoypadSensitivitySlider(int id, int x, int y, int width, int height, String text, float value) {
        super(id, x, y, width, height, text, value);
    }
    @Override public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!visible) return;
        if (!Mouse.isButtonDown(0)) dragging = false;
        if (dragging) {
            setValue((float)(mouseX - xPosition - 4) / (width - 8));
            updateText();
        }
        JoypadTheme.plate(xPosition,yPosition,width,height,dragging,true);
        drawRect(xPosition, yPosition + height - 3, xPosition + (int)(width * getValue()), yPosition + height, PixelTheme.POSITIVE);
        drawCenteredString(mc.fontRendererObj, mc.fontRendererObj.trimStringToWidth(displayString, width - 8),
            xPosition + width / 2, yPosition + 5, PixelTheme.TEXT);
    }
}
