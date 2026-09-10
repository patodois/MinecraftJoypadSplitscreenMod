package com.shiny.joypadmod.gui;

/** Original pixel artwork. The build tool rasterizes this into a small texture atlas. */
public final class GlyphArt {
    public enum Icon {
        A, B, X, Y, VIEW, MENU, LB, RB, LS, RS,
        DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT, GUIDE, LT, RT,
        LS_UP, LS_DOWN, LS_LEFT, LS_RIGHT, RS_UP, RS_DOWN, RS_LEFT, RS_RIGHT,
        UNBOUND, BUTTON
    }
    public interface Painter { void rect(int x, int y, int width, int height, int argb); }
    public static final int SIZE = 16, COLUMNS = 8, WIDTH = 128, HEIGHT = 64;
    private static final int EDGE=0xFF171911, LIGHT=0xFFE0DDC9, FACE=0xFF676C57, INK=0xFFF8F5E5;
    private GlyphArt() {}
    private static void box(Painter p,int x,int y,int w,int h,int color) { p.rect(x,y,w,h,color); }
    private static void round(Painter p,int x,int y,int color) {
        box(p,x+4,y,8,16,EDGE); box(p,x+2,y+2,12,12,EDGE); box(p,x,y+4,16,8,EDGE);
        box(p,x+4,y+1,8,14,color); box(p,x+2,y+3,12,10,color); box(p,x+1,y+5,14,6,color);
        box(p,x+5,y+2,6,1,LIGHT); box(p,x+3,y+3,2,1,LIGHT);
        box(p,x+5,y+13,6,1,0xFF353525);
    }
    private static void text(Painter p,String text,int x,int y,int color) {
        for (char c:text.toCharArray()) {
            String bits;
            switch(c) {
                case 'A': bits="010101111101101"; break;
                case 'B': bits="110101110101110"; break;
                case 'X': bits="101101010101101"; break;
                case 'Y': bits="101101010010010"; break;
                case 'L': bits="100100100100111"; break;
                case 'R': bits="110101110101101"; break;
                case 'S': bits="011100010001110"; break;
                case 'T': bits="111010010010010"; break;
                default: bits="110001010000010";
            }
            for(int i=0;i<15;i++) if(bits.charAt(i)=='1') box(p,x+i%3,y+i/3,1,1,color);
            x+=4;
        }
    }
    public static void paint(Painter p,Icon icon,int x,int y) {
        int id=icon.ordinal();
        if(id<4) {
            int[] colors={0xFF56883C,0xFFAA4439,0xFF426994,0xFFB99734};
            round(p,x,y,colors[id]); text(p,icon.name(),x+6,y+5,INK); return;
        }
        if(icon==Icon.LB || icon==Icon.RB || icon==Icon.LT || icon==Icon.RT) {
            int top=(icon==Icon.LT || icon==Icon.RT)?1:3;
            box(p,x+2,y+top,12,14-top,EDGE); box(p,x+1,y+top+2,14,10-top,EDGE);
            box(p,x+2,y+top+1,12,11-top,0xFF737663); box(p,x+3,y+top+1,10,1,LIGHT);
            text(p,icon.name(),x+4,y+6,INK); return;
        }
        if(id>=10 && id<=13) {
            box(p,x+5,y+1,6,14,EDGE); box(p,x+1,y+5,14,6,EDGE);
            box(p,x+6,y+2,4,12,FACE); box(p,x+2,y+6,12,4,FACE);
            int dx=icon==Icon.DPAD_LEFT?-4:icon==Icon.DPAD_RIGHT?4:0;
            int dy=icon==Icon.DPAD_UP?-4:icon==Icon.DPAD_DOWN?4:0;
            box(p,x+6+dx,y+6+dy,4,4,0xFFA2C36C); box(p,x+7,y+7,2,2,EDGE); return;
        }
        if(icon==Icon.VIEW || icon==Icon.MENU) {
            box(p,x+1,y+3,14,10,EDGE); box(p,x+2,y+4,12,8,FACE);
            if(icon==Icon.MENU) for(int i=0;i<3;i++) box(p,x+4,y+5+i*2,8,1,INK);
            else {
                box(p,x+3,y+5,6,4,INK); box(p,x+4,y+6,4,2,FACE);
                box(p,x+7,y+7,6,4,INK); box(p,x+8,y+8,4,2,FACE);
            }
            return;
        }
        round(p,x,y,FACE);
        if(icon==Icon.LS || icon==Icon.RS || (id>=17 && id<=24)) {
            boolean left=icon==Icon.LS || (id>=17 && id<=20);
            text(p,left?"LS":"RS",x+4,y+5,INK);
            if(id>=17 && id<=24) {
                int direction=(id-17)%4;
                if(direction==0) {box(p,x+6,y,4,2,0xFFCAB77F);}
                if(direction==1) {box(p,x+6,y+14,4,2,0xFFCAB77F);}
                if(direction==2) {box(p,x,y+6,2,4,0xFFCAB77F);}
                if(direction==3) {box(p,x+14,y+6,2,4,0xFFCAB77F);}
            }
        } else if(icon==Icon.GUIDE) text(p,"X",x+6,y+5,INK);
        else if(icon==Icon.UNBOUND) box(p,x+4,y+7,8,2,0xFFBCAD91);
        else box(p,x+6,y+6,4,4,INK);
    }
}
