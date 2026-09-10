package com.shiny.joypadmod.gui;

import com.shiny.joypadmod.devices.InputDevice;
import com.shiny.joypadmod.devices.StandardGamepadDevice;
import com.shiny.joypadmod.inputevent.ControllerInputEvent;
import com.shiny.joypadmod.inputevent.ControllerInputEvent.EventType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public final class ControllerGlyphs {
    private static final ResourceLocation ATLAS=new ResourceLocation("joypadmod:textures/gui/controller_glyphs.png");
    private ControllerGlyphs() {}
    public static GlyphArt.Icon button(InputDevice device,int index) {
        return device instanceof StandardGamepadDevice && index>=0 && index<15
            ? GlyphArt.Icon.values()[index] : GlyphArt.Icon.BUTTON;
    }
    public static GlyphArt.Icon resolve(InputDevice device,ControllerInputEvent event) {
        if(device==null || event==null || !event.isValid()) return GlyphArt.Icon.UNBOUND;
        int index=event.getEventIndex();
        if(event.getEventType()==EventType.POV) return index==0
            ? (event.getThreshold()>0?GlyphArt.Icon.DPAD_RIGHT:GlyphArt.Icon.DPAD_LEFT)
            : (event.getThreshold()>0?GlyphArt.Icon.DPAD_DOWN:GlyphArt.Icon.DPAD_UP);
        if(device instanceof StandardGamepadDevice) {
            if(event.getEventType()==EventType.BUTTON && index>=0 && index<15) return button(device,index);
            if(event.getEventType()==EventType.AXIS) {
                if(index==4) return GlyphArt.Icon.LT;
                if(index==5) return GlyphArt.Icon.RT;
                if(index>=0 && index<4) {
                    int base=index<2?17:21;
                    int direction=index%2==0?(event.getThreshold()>0?3:2):(event.getThreshold()>0?1:0);
                    return GlyphArt.Icon.values()[base+direction];
                }
            }
        }
        // Unknown layouts get a neutral icon, never a misleading Xbox button.
        return GlyphArt.Icon.BUTTON;
    }
    public static void draw(GlyphArt.Icon icon,int x,int y,int size) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(ATLAS);
        GL11.glColor4f(1,1,1,1);
        boolean blend=GL11.glIsEnabled(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
        int id=icon.ordinal();
        Gui.drawScaledCustomSizeModalRect(x,y,(id%GlyphArt.COLUMNS)*16f,(id/GlyphArt.COLUMNS)*16f,
            16,16,size,size,(float)GlyphArt.WIDTH,(float)GlyphArt.HEIGHT);
        if(!blend) GL11.glDisable(GL11.GL_BLEND);
    }
}
