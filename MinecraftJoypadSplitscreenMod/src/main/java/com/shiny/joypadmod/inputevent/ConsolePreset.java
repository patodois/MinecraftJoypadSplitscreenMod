package com.shiny.joypadmod.inputevent;

import com.shiny.joypadmod.devices.DefaultButtonMappings;
import com.shiny.joypadmod.devices.InputDevice;
import com.shiny.joypadmod.devices.StandardGamepadDevice;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.lwjgl.input.Keyboard;

/** Uses physical controls from Controllable's defaults, translated to our backend IDs. */
public final class ConsolePreset {
    private ConsolePreset() {}
    public static ControllerInputEvent dpad(InputDevice device,int direction) {
        if(device instanceof StandardGamepadDevice) return new ButtonInputEvent(device.getIndex(),10+direction,1);
        return new PovInputEvent(device.getIndex(),direction<2?1:0,(direction==0 || direction==2)?-.9f:.9f);
    }
    private static boolean button(ControllerBinding b,int index) {
        return b!=null && b.inputEvent!=null && b.inputEvent.getEventType()==ControllerInputEvent.EventType.BUTTON
            && b.inputEvent.getEventIndex()==index;
    }
    /** Called once per profile. Only recognizable old defaults are migrated. */
    public static List<String> migrate(Map<String,ControllerBinding> bindings,InputDevice device,
            DefaultButtonMappings buttons,int inventoryKey) {
        List<String> changed=new ArrayList<String>();
        ControllerBinding sneak=bindings.get("joy.sneak"), sprint=bindings.get("joy.sprint");
        if(button(sneak,buttons.LS()) && button(sprint,buttons.RS())) {
            sneak.inputEvent=new ButtonInputEvent(device.getIndex(),buttons.RS(),1);
            sprint.inputEvent=new ButtonInputEvent(device.getIndex(),buttons.LS(),1);
            changed.add("joy.sneak"); changed.add("joy.sprint");
        }
        ControllerBinding drop=bindings.get("joy.drop");
        if(button(drop,buttons.Back())) {
            drop.inputEvent=dpad(device,1);
            drop.bindingOptions.remove(ControllerBinding.BindingOptions.REPEAT_IF_HELD);
            changed.add("joy.drop");
        }
        ControllerBinding back=bindings.get("joy.closeInventory");
        if(button(back,buttons.Y()) && back.keyCodes!=null && back.keyCodes.length==1 && back.keyCodes[0]==inventoryKey) {
            back.keyCodes=new int[]{Keyboard.KEY_ESCAPE};
            back.menuString="Back / close menu";
            changed.add("joy.closeInventory");
        }
        return changed;
    }
}
