package com.shiny.joypadmod.minecraftExtensions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

/** Small texture-free widget that scales with Minecraft's GUI scale. */
public class JoypadFlatButton extends GuiButton {
    public JoypadFlatButton(int id, int x, int y, int width, int height, String label) {
        super(id, x, y, width, height, label);
    }

    @Override public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!visible) return;
        boolean hover = mouseX >= xPosition && mouseX < xPosition + width && mouseY >= yPosition && mouseY < yPosition + height;
        drawRect(xPosition, yPosition, xPosition + width, yPosition + height,
            !enabled ? 0xFF1B293A : hover ? 0xFF38536A : 0xFF293B50);
        if (enabled && hover) drawRect(xPosition, yPosition + height - 2, xPosition + width, yPosition + height, 0xFF6EE7C2);
        drawCenteredString(mc.fontRendererObj, mc.fontRendererObj.trimStringToWidth(displayString, width - 4),
            xPosition + width / 2, yPosition + (height - 8) / 2, !enabled ? 0x73849B : hover ? 0x6EE7C2 : 0xE6EDF7);
    }
}
