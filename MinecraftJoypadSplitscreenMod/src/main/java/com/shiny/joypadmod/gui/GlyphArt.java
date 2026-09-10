package com.shiny.joypadmod.gui;

/** Stable IDs for the PixelLab-generated controller texture atlas. */
public final class GlyphArt {
    public enum Icon {
        A, B, X, Y, VIEW, MENU, LB, RB, LS, RS,
        DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT, GUIDE, LT, RT,
        LS_UP, LS_DOWN, LS_LEFT, LS_RIGHT, RS_UP, RS_DOWN, RS_LEFT, RS_RIGHT,
        UNBOUND, BUTTON
    }
    public static final int SIZE=32, COLUMNS=8, WIDTH=256, HEIGHT=128;
    private GlyphArt() {}
}
