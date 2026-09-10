package com.shiny.joypadmod.devices;

import com.shiny.joypadmod.ControllerSettings;
import com.shiny.joypadmod.gui.ControllerGlyphs;
import com.shiny.joypadmod.gui.GlyphArt.Icon;
import com.shiny.joypadmod.inputevent.*;
import com.shiny.joypadmod.inputevent.ControllerBinding.BindingOptions;
import java.util.*;
import org.lwjgl.input.Keyboard;

public class ProfileAndGlyphTest {
    private static int checks;
    static void check(boolean ok,String name) { if(!ok)throw new AssertionError(name);checks++; }
    static ControllerBinding binding(String name,int button,int key) {
        return new ControllerBinding(name,name,new ButtonInputEvent(0,button,1),new int[]{key},0,
            EnumSet.of(BindingOptions.GAME_BINDING,BindingOptions.REPEAT_IF_HELD));
    }
    public static void main(String[] args) {
        ControllerSettings.loggingLevel=0;
        InputRegressionTest.FakeDevice device=new InputRegressionTest.FakeDevice();
        ControllerSettings.JoypadModInputLibrary=new InputRegressionTest.FakeLibrary(device);
        Icon[] expected={Icon.A,Icon.B,Icon.X,Icon.Y,Icon.VIEW,Icon.MENU,Icon.LB,Icon.RB,Icon.LS,Icon.RS,
            Icon.DPAD_UP,Icon.DPAD_DOWN,Icon.DPAD_LEFT,Icon.DPAD_RIGHT,Icon.GUIDE};
        for(int i=0;i<expected.length;i++)
            check(ControllerGlyphs.resolve(device,new ButtonInputEvent(0,i,1))==expected[i],"physical button glyph "+i);
        check(ControllerGlyphs.resolve(device,new AxisInputEvent(0,4,.7f,.1f))==Icon.LT,"LT glyph");
        check(ControllerGlyphs.resolve(device,new AxisInputEvent(0,5,.7f,.1f))==Icon.RT,"RT glyph");
        check(ControllerGlyphs.resolve(device,new AxisInputEvent(0,0,.7f,.1f))==Icon.LS_RIGHT,"LS right glyph");
        check(ControllerGlyphs.resolve(device,new AxisInputEvent(0,1,-.7f,.1f))==Icon.LS_UP,"LS up glyph");
        check(ControllerGlyphs.resolve(device,new AxisInputEvent(0,2,-.7f,.1f))==Icon.RS_LEFT,"RS left glyph");
        check(ControllerGlyphs.resolve(device,new ButtonInputEvent(0,-1,1))==Icon.UNBOUND,"unbound icon");
        check(ControllerGlyphs.resolve(null,null)==Icon.UNBOUND,"missing device is safe");
        ControllerBinding remapped=binding("joy.jump",3,Keyboard.KEY_SPACE);
        check(ControllerGlyphs.resolve(device,remapped.inputEvent)==Icon.Y,"hint follows custom jump mapping");
        remapped.inputEvent=new ButtonInputEvent(0,2,1);
        check(ControllerGlyphs.resolve(device,remapped.inputEvent)==Icon.X,"hint updates after another remap");
        for(int i=0;i<4;i++) check(ControllerGlyphs.resolve(device,ConsolePreset.dpad(device,i))==expected[10+i],"D-pad preset "+i);

        DefaultButtonMappings buttons=new DefaultButtonMappings();
        Map<String,ControllerBinding> map=new HashMap<String,ControllerBinding>();
        map.put("joy.sneak",binding("joy.sneak",8,Keyboard.KEY_LSHIFT));
        map.put("joy.sprint",binding("joy.sprint",9,Keyboard.KEY_LCONTROL));
        map.put("joy.drop",binding("joy.drop",4,Keyboard.KEY_Q));
        map.put("joy.closeInventory",binding("joy.closeInventory",3,Keyboard.KEY_E));
        List<String> changed=ConsolePreset.migrate(map,device,buttons,Keyboard.KEY_E);
        check(changed.size()==4,"all four old default bindings migrated");
        check(map.get("joy.sneak").inputEvent.getEventIndex()==9,"RS crouches like reference mod");
        check(map.get("joy.sprint").inputEvent.getEventIndex()==8,"LS sprints like reference mod");
        check(map.get("joy.drop").inputEvent.getEventIndex()==11,"D-pad down drops items");
        check(!map.get("joy.drop").bindingOptions.contains(BindingOptions.REPEAT_IF_HELD),"drop does not repeat while held");
        check(map.get("joy.closeInventory").inputEvent.getEventIndex()==3 && map.get("joy.closeInventory").keyCodes[0]==Keyboard.KEY_ESCAPE,
            "Y goes back via Escape in menus as well as containers");
        check(ConsolePreset.migrate(map,device,buttons,Keyboard.KEY_E).isEmpty(),"migration is idempotent");

        map.put("joy.sneak",binding("joy.sneak",1,Keyboard.KEY_LSHIFT));
        map.put("joy.sprint",binding("joy.sprint",9,Keyboard.KEY_LCONTROL));
        map.put("joy.drop",binding("joy.drop",2,Keyboard.KEY_Q));
        map.put("joy.closeInventory",binding("joy.closeInventory",3,Keyboard.KEY_TAB));
        check(ConsolePreset.migrate(map,device,buttons,Keyboard.KEY_E).isEmpty(),"custom input/output mappings preserved");
        check(map.get("joy.sneak").inputEvent.getEventIndex()==1,"custom B crouch retained");
        check(map.get("joy.sprint").inputEvent.getEventIndex()==9,"custom movement pair is not partly swapped");
        System.out.println("PASS: "+checks+" profile migration and glyph assertions.");
    }
}
