package com.shiny.joypadmod.minecraftExtensions;

import net.minecraft.client.Minecraft;
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
        drawRect(xPosition, yPosition, xPosition + width, yPosition + height, 0xFF263449);
        drawRect(xPosition, yPosition + height - 3, xPosition + (int)(width * getValue()), yPosition + height, 0xFF6EE7C2);
        drawCenteredString(mc.fontRendererObj, mc.fontRendererObj.trimStringToWidth(displayString, width - 8),
            xPosition + width / 2, yPosition + 5, 0xE6EDF7);
    }
}
