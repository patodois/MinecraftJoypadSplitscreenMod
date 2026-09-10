package com.shiny.joypadmod.gui;

/** Stone, wood, grass and warm text shared by runtime UI and preview rendering. */
public final class PixelTheme {
    public static final int PANEL=0xED25271F, ROW=0xFF303229, ROW_ALT=0xFF37382F;
    public static final int TEXT=0xF1EFDC, MUTED=0xB9B7A5, ACCENT=0xA1BC69;
    public static final int TRACK=0xFF171B13, POSITIVE=0xFF829F49, NEGATIVE=0xFFC4A468;
    private PixelTheme() {}
    public static void plate(GlyphArt.Painter p,int x,int y,int width,int height,boolean hover,boolean enabled) {
        p.rect(x,y,width,height,0xFF171911);
        p.rect(x+1,y+1,width-2,height-2,!enabled?0xFF34372C:hover?0xFF5E6D40:0xFF57594C);
        p.rect(x+1,y+1,width-2,1,!enabled?0xFF62644F:hover?0xFFC1D28E:0xFFA5A591);
        p.rect(x+1,y+2,1,height-3,!enabled?0xFF62644F:0xFF888C72);
        p.rect(x+2,y+height-2,width-3,1,0xFF303426);
        p.rect(x+width-2,y+2,1,height-3,0xFF303426);
    }
}
