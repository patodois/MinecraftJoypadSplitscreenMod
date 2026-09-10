package com.shiny.joypadmod;

import com.shiny.joypadmod.devices.InputDevice;
import com.shiny.joypadmod.gui.ControllerGlyphs;
import com.shiny.joypadmod.gui.PixelTheme;
import com.shiny.joypadmod.helpers.McObfuscationHelper;
import com.shiny.joypadmod.helpers.ModVersionHelper;
import com.shiny.joypadmod.inputevent.ControllerBinding;
import com.shiny.joypadmod.minecraftExtensions.JoypadConfigMenu;
import com.shiny.joypadmod.minecraftExtensions.JoypadAdvancedMenu;
import com.shiny.joypadmod.minecraftExtensions.JoypadCalibrationMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;

/** Contextual hints resolve the actual binding every frame, including custom remaps. */
public class ButtonScreenTips extends Gui {
    private static final String[][] GAME_LEFT={{"joy.inventory",null},{"joy.jump",null}};
    private static final String[][] GAME_RIGHT={{"joy.attack",null},{"joy.use",null}};
    private static final String[][] CONTAINER_LEFT={{"joy.closeInventory","improved.back"},{"joy.shiftClick","menuHint.quickmove"}};
    private static final String[][] CONTAINER_RIGHT={{"joy.guiLeftClick","menuHint.takeall","menuHint.placeall"},{"joy.guiRightClick","menuHint.takehalf","menuHint.placeone"}};
    private static final String[][] MENU_LEFT={{"joy.guiLeftClick","improved.select"}};
    private static final String[][] MENU_RIGHT={{"joy.closeInventory","improved.back"}};

    // Preserved for callers compiled against the original mod; no stale label cache remains.
    public static void UpdateHintString() {}

    public ButtonScreenTips() {
        if(ControllerSettings.isSuspended() || !ControllerSettings.isInputEnabled()
                || !ControllerSettings.displayHints || ControllerSettings.joyNo<0) return;
        Minecraft mc=Minecraft.getMinecraft();
        if(mc.currentScreen instanceof JoypadConfigMenu || mc.currentScreen instanceof JoypadAdvancedMenu
                || mc.currentScreen instanceof JoypadCalibrationMenu) return;
        boolean game=mc.currentScreen==null && mc.theWorld!=null && mc.thePlayer!=null;
        boolean container=mc.currentScreen instanceof GuiContainer;
        if(!game && mc.currentScreen==null) return;
        InputDevice device=ControllerSettings.JoypadModInputLibrary.getController(ControllerSettings.joyNo);
        if(!device.isConnected()) return;
        ScaledResolution scaled=ModVersionHelper.GetScaledResolution();
        int width=scaled.getScaledWidth(), height=scaled.getScaledHeight();
        boolean holding=container && mc.thePlayer!=null && mc.thePlayer.inventory.getItemStack()!=null;
        if(container && width<600) {
            String[][] items={CONTAINER_LEFT[0],CONTAINER_LEFT[1],CONTAINER_RIGHT[0],CONTAINER_RIGHT[1]};
            int cell=(width-16)/4;
            for(int i=0;i<items.length;i++) drawColumn(mc.fontRendererObj,device,new String[][]{items[i]},holding,
                8+i*cell,height-21,cell-4);
            return;
        }
        if(!game && !container) {
            int edge=Math.max(26,Math.min(100,(width-200)/2-16));
            drawColumn(mc.fontRendererObj,device,MENU_LEFT,false,8,height-22,edge);
            drawColumn(mc.fontRendererObj,device,MENU_RIGHT,false,width-8-edge,height-22,edge);
            return;
        }
        boolean sides=width>=600;
        int right=sides?width/2+106:width/2+4;
        int maxWidth=sides?width/2-114:width/2-12;
        int top=height-(game&&!sides?86:45);
        drawColumn(mc.fontRendererObj,device,game?GAME_LEFT:container?CONTAINER_LEFT:MENU_LEFT,holding,8,top,maxWidth);
        drawColumn(mc.fontRendererObj,device,game?GAME_RIGHT:container?CONTAINER_RIGHT:MENU_RIGHT,holding,right,top,maxWidth);
    }
    private void drawColumn(FontRenderer font,InputDevice device,String[][] hints,boolean holding,int x,int y,int maxWidth) {
        for(String[] hint:hints) {
            ControllerBinding binding=ControllerSettings.get(hint[0]);
            if(binding==null || binding.inputEvent==null || !binding.inputEvent.isValid()) continue;
            String key=holding && hint.length>2?hint[2]:hint[1];
            String label=key==null?binding.getMenuItemName():McObfuscationHelper.lookupString(key);
            label=font.trimStringToWidth(label,Math.max(0,maxWidth-24));
            int width=Math.min(maxWidth,font.getStringWidth(label)+25);
            drawRect(x,y,x+width,y+19,0xBC1C2017);
            drawRect(x,y+18,x+width,y+19,0xC87C7150);
            ControllerGlyphs.draw(ControllerGlyphs.resolve(device,binding.inputEvent),x+1,y+1,16);
            font.drawStringWithShadow(label,x+21,y+5,PixelTheme.TEXT);
            y+=21;
        }
    }
}
