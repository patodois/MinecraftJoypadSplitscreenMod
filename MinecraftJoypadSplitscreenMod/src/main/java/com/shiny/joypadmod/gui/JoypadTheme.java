package com.shiny.joypadmod.gui;
import net.minecraft.client.gui.Gui;
public final class JoypadTheme {
    public static final GlyphArt.Painter PAINTER=new GlyphArt.Painter() {
        public void rect(int x,int y,int width,int height,int argb) { Gui.drawRect(x,y,x+width,y+height,argb); }
    };
    private JoypadTheme() {}
    public static void plate(int x,int y,int width,int height,boolean hover,boolean enabled) {
        PixelTheme.plate(PAINTER,x,y,width,height,hover,enabled);
    }
}
