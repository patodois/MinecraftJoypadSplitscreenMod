package com.shiny.joypadmod.devices;

import com.shiny.joypadmod.ControllerSettings;
import com.shiny.joypadmod.inputevent.AxisInputEvent;
import com.shiny.joypadmod.inputevent.ButtonInputEvent;
import com.shiny.joypadmod.inputevent.ControllerInputEvent;
import com.shiny.joypadmod.inputevent.ControllerUtils;
import com.ivan.xinput.enums.XInputButton;

/** Real patched classes, simulated controller state; no native DLL or game window. */
public class InputRegressionTest {
    private static int checks;
    static class FakeDevice extends XInputDeviceWrapper {
        boolean connected = true;
        boolean[] held = new boolean[15];
        float[] values = new float[6];
        FakeDevice() { super(0); }
        @Override public Boolean isConnected() { return connected; }
        @Override public Boolean isButtonPressed(int i) { return connected && held[i]; }
        @Override public float getAxisValue(int i) { return connected ? values[i] : 0f; }
    }
    static class FakeLibrary extends XInputLibrary {
        FakeLibrary(FakeDevice d) { theDevice = d; }
        @Override public InputDevice getController(int i) { return theDevice; }
    }
    static void check(boolean ok, String name) {
        if (!ok) throw new AssertionError(name);
        checks++;
    }
    static boolean pressed(FakeLibrary lib, ControllerInputEvent binding) {
        boolean pressed = false;
        while (lib.next()) pressed |= binding.wasPressed();
        return pressed;
    }
    public static void main(String[] args) {
        ControllerSettings.loggingLevel = 0;
        FakeDevice d = new FakeDevice();
        FakeLibrary lib = new FakeLibrary(d);
        ControllerSettings.JoypadModInputLibrary = lib;
        lib.sampleState();
        check(lib.wasConnected(), "initial connection");
        check(!lib.wasConnected(), "connection notification consumed once");
        for (int i = 0; i < 15; i++) {
            ButtonInputEvent b = new ButtonInputEvent(0, i, 1);
            d.held[i] = true; lib.sampleState();
            check(pressed(lib, b), "slot zero button " + i);
            check(b.isPressed(), "held button " + i);
            lib.sampleState(); check(!pressed(lib, b), "no repeated press " + i);
            d.held[i] = false; lib.sampleState();
            check(!b.isPressed() && b.wasReleased(), "released button " + i);
            check(!pressed(lib, b), "release is not a press " + i);
        }
        d.held[XInputButton.DPAD_UP.ordinal()] = true;
        d.held[XInputButton.DPAD_RIGHT.ordinal()] = true;
        lib.sampleState(); lib.clearEvents();
        check(d.getPovY() == -1f && d.getPovX() == 1f, "D-pad diagonal orientation");
        for (int i = 0; i < 100; i++) lib.sampleState();
        check(d.getPovY() == -1f && d.getPovX() == 1f, "D-pad stays held without deltas");
        d.held[XInputButton.DPAD_UP.ordinal()] = false;
        d.held[XInputButton.DPAD_RIGHT.ordinal()] = false;
        lib.sampleState(); lib.clearEvents();
        check(d.getPovY() == 0f && d.getPovX() == 0f, "D-pad neutral");

        AxisInputEvent rt = new AxisInputEvent(0, 5, .7f, .2f);
        d.values[5] = .8f; lib.sampleState();
        check(pressed(lib, rt), "positive RT activates attack");
        for (int i = 0; i < 10000; i++) lib.sampleState();
        int pending = 0;
        while (lib.next()) { pending++; check(!rt.wasPressed(), "held RT does not retrigger"); }
        check(pending == 1, "axis queue bounded across 10000 polls");
        check(rt.isPressed(), "held RT remains active for continuous mining");
        d.values[5] = 0f; lib.sampleState();
        check(!rt.isPressed() && rt.wasReleased(), "RT release");
        check(!pressed(lib, rt), "RT release does not activate");
        d.values[5] = .95f; lib.sampleState();
        check(pressed(lib, rt), "RT can be pressed again");
        d.values[4] = .85f;
        AxisInputEvent lt = new AxisInputEvent(0, 4, .7f, .2f);
        lib.sampleState();
        check(pressed(lib, lt) && rt.isPressed(), "LT and RT independent and simultaneous");
        d.values[4] = 0f;
        check(ControllerUtils.getAxisValue(d, 4) == 0f, "XInput resting trigger is not remapped to 0.5");
        d.values[0] = d.values[1] = -1f;
        check(!new ControllerUtils().isDeadlocked(d), "fully tilted XInput stick is not initialization deadlock");
        d.setDeadZone(0, 1.2f); check(d.getDeadZone(0) == .95f, "deadzone upper bound");
        d.setDeadZone(0, -1f); check(d.getDeadZone(0) == 0f, "deadzone lower bound");
        d.setDeadZone(0, Float.NaN); check(d.getDeadZone(0) == .15f, "NaN deadzone repair");
        rt.resetState(); check(!rt.isActive() && !rt.isPressed(), "reset releases active binding");
        d.connected = false; lib.sampleState();
        check(lib.wasDisconnected() && !lib.wasDisconnected(), "Bluetooth disconnect reported once");
        check(!lib.next(), "disconnect clears stale events");
        check(d.getPovX() == 0f && d.getPovY() == 0f, "disconnect neutral state");
        d.connected = true; lib.sampleState();
        check(lib.wasConnected(), "Bluetooth reconnect detected");
        check(pressed(lib, rt), "input resumes on reconnect");
        XInputDeviceWrapper absent = new XInputDeviceWrapper(0);
        check(!absent.isConnected() && absent.getAxisValue(5) == 0f && !absent.isButtonPressed(0), "uninitialized device safe");
        check(absent.getButtonCount() == 15, "UNKNOWN enum sentinel excluded");
        System.out.println("PASS: " + checks + " input regression assertions (simulated hardware).");
    }
}
