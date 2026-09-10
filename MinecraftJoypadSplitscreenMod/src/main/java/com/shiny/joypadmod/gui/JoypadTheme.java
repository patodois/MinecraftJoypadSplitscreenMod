package com.shiny.joypadmod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/** PixelLab button/slider skins. Labels remain native Minecraft text for localization. */
public final class JoypadTheme {
    private static final ResourceLocation SKINS=new ResourceLocation("joypadmod:textures/gui/pixellab_widgets.png");
    private JoypadTheme() {}
    public static void plate(int x,int y,int width,int height,boolean hover,boolean enabled) {
        int[][] patches=PixelLabSkinLayout.slices(width,height,!enabled?2:hover?1:0);
        if(patches.length==0) return;
        boolean blend=begin();
        Tessellator t=Tessellator.instance;t.startDrawingQuads();
        for(int[] p:patches) quad(t,x+p[0],y+p[1],x+p[2],y+p[3],p[4],p[5],p[6],p[7]);
        t.draw();end(blend);
    }
    public static void thumb(int x,int y,int width,int height,boolean hover,boolean enabled) {
        if(width<=0 || height<=0)return;
        int v=(!enabled?2:hover?1:0)*32;
        boolean blend=begin();Tessellator t=Tessellator.instance;t.startDrawingQuads();
        quad(t,x,y,x+width,y+height,64,v,80,v+32);t.draw();end(blend);
    }
    private static boolean begin() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(SKINS);
        GL11.glColor4f(1,1,1,1);
        boolean was=GL11.glIsEnabled(GL11.GL_BLEND);GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);return was;
    }
    private static void end(boolean blend) { if(!blend)GL11.glDisable(GL11.GL_BLEND); }
    private static void quad(Tessellator t,int x1,int y1,int x2,int y2,int u1,int v1,int u2,int v2) {
        t.addVertexWithUV(x1,y2,0,u1/128.0,v2/128.0);t.addVertexWithUV(x2,y2,0,u2/128.0,v2/128.0);
        t.addVertexWithUV(x2,y1,0,u2/128.0,v1/128.0);t.addVertexWithUV(x1,y1,0,u1/128.0,v1/128.0);
    }
}
