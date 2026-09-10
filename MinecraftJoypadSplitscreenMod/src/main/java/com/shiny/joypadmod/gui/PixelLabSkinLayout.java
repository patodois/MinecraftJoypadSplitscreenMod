package com.shiny.joypadmod.gui;
import java.util.ArrayList;
import java.util.List;
/** Nine-slice coordinates only; every displayed pixel comes from the generated texture. */
public final class PixelLabSkinLayout {
    private PixelLabSkinLayout() {}
    public static int[][] slices(int width,int height,int state) {
        if(state<0 || state>2) throw new IllegalArgumentException("Button state");
        List<int[]> patches=new ArrayList<int[]>();
        if(width<=0 || height<=0) return new int[0][];
        int border=Math.min(4,Math.min(width/2,height/2));
        int[] x={0,border,width-border,width}, y={0,border,height-border,height};
        int[] u={0,8,56,64}, v={state*32,state*32+8,state*32+24,state*32+32};
        for(int row=0;row<3;row++) for(int col=0;col<3;col++) {
            int tileW=col==1?24:Math.max(1,x[col+1]-x[col]);
            int tileH=row==1?8:Math.max(1,y[row+1]-y[row]);
            for(int dy=y[row];dy<y[row+1];dy+=tileH) for(int dx=x[col];dx<x[col+1];dx+=tileW) {
                int w=Math.min(tileW,x[col+1]-dx),h=Math.min(tileH,y[row+1]-dy);
                int sw=col==1?w*2:u[col+1]-u[col],sh=row==1?h*2:v[row+1]-v[row];
                patches.add(new int[]{dx,dy,dx+w,dy+h,u[col],v[row],u[col]+sw,v[row]+sh});
            }
        }
        return patches.toArray(new int[patches.size()][]);
    }
}
