package com.shiny.joypadmod.minecraftExtensions;

import net.minecraft.client.Minecraft;
import com.shiny.joypadmod.gui.*;
import net.minecraft.client.gui.GuiButton;

/** Small texture-free widget that scales with Minecraft's GUI scale. */
public class JoypadFlatButton extends GuiButton {
    private GlyphArt.Icon icon;
    public JoypadFlatButton withIcon(GlyphArt.Icon glyph) { icon=glyph; return this; }
    public JoypadFlatButton(int id, int x, int y, int width, int height, String label) {
        super(id, x, y, width, height, label);
    }

    @Override public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!visible) return;
        boolean hover = mouseX >= xPosition && mouseX < xPosition + width && mouseY >= yPosition && mouseY < yPosition + height;
        JoypadTheme.plate(xPosition,yPosition,width,height,hover,enabled);
        if(icon!=null) {
            ControllerGlyphs.draw(icon,xPosition+(width-16)/2,yPosition+(height-16)/2,16);
            return;
        }
        drawCenteredString(mc.fontRendererObj, mc.fontRendererObj.trimStringToWidth(displayString, width - 4),
            xPosition + width / 2, yPosition + (height - 8) / 2, !enabled ? 0x8A8D77 : hover ? 0xF2EAA9 : PixelTheme.TEXT);
    }
}
