package com.shiny.joypadmod.devices;
import java.util.ArrayDeque;
import java.util.Arrays;
/** Shared state-to-event pipeline for standardized desktop gamepads. */
public abstract class SnapshotInputLibrary extends InputLibrary {
    protected InputDevice theDevice;
    public Boolean recentlyDisconnected = false;
    public Boolean recentlyConnected = false;
    private boolean connected;
    private final boolean[] buttons = new boolean[15];
    private final float[] axes = new float[6];
    private float povX, povY;
    private final ArrayDeque<Integer> events = new ArrayDeque<Integer>();
    private final boolean[] queued = new boolean[23];
    private int lastEvent = -1;
    private int lastEventControlIndex = -1;

    protected void selectDevice(InputDevice device) {
        if (theDevice != device) {
            theDevice = device;
            resetSnapshot();
            connected = device.isConnected();
            recentlyConnected = recentlyDisconnected = false;
        }
    }
    protected void resetSnapshot() {
        clearEvents();
        Arrays.fill(buttons, false);
        Arrays.fill(axes, 0f);
        povX = povY = 0f;
    }

    private void enqueue(int code) {
        // A stationary held axis used to add thousands of duplicate events.
        if (!queued[code]) { events.addLast(code); queued[code] = true; }
    }

    @Override public void clearEvents() {
        events.clear();
        Arrays.fill(queued, false);
        lastEvent = lastEventControlIndex = -1;
    }

    // Separate from native polling so the real event pipeline is testable with a fake device.
    void sampleState() {
        boolean nowConnected = theDevice.isConnected();
        if (connected != nowConnected) {
            recentlyConnected = nowConnected;
            recentlyDisconnected = !nowConnected;
            resetSnapshot();
            connected = nowConnected;
        }
        if (!connected) return;
        for (int i = 0; i < buttons.length; i++) {
            boolean value = theDevice.isButtonPressed(i);
            if (value != buttons[i]) enqueue(i);
            buttons[i] = value;
        }
        for (int i = 0; i < axes.length; i++) {
            float value = theDevice.getAxisValue(i);
            // Keep active-axis notifications for binding capture at any threshold.
            // ControllerInputEvent gates action presses to the activation edge.
            if (value != 0f || value != axes[i]) enqueue(15 + i);
            axes[i] = value;
        }
        float x = theDevice.getPovX(), y = theDevice.getPovY();
        if (x != povX) enqueue(21);
        if (y != povY) enqueue(22);
        povX = x;
        povY = y;
    }

    @Override public Boolean next() {
        if (events.isEmpty()) return false;
        int code = events.removeFirst();
        queued[code] = false;
        lastEvent = code < 15 ? 0 : code < 21 ? 1 : code == 21 ? 2 : 3;
        lastEventControlIndex = code < 15 ? code : code < 21 ? code - 15 : code - 21;
        return true;
    }

    @Override public InputDevice getEventSource() { return theDevice; }
    @Override public InputDevice getCurrentController() { return theDevice; }
    @Override public int getEventControlIndex() { return lastEventControlIndex; }
    @Override public Boolean isEventButton() { return lastEvent == 0; }
    @Override public Boolean isEventAxis() { return lastEvent == 1; }
    @Override public Boolean isEventPovX() { return lastEvent == 2; }
    @Override public Boolean isEventPovY() { return lastEvent == 3; }
    @Override public Boolean wasDisconnected() { boolean value = recentlyDisconnected; recentlyDisconnected = false; return value; }
    @Override public Boolean wasConnected() { boolean value = recentlyConnected; recentlyConnected = false; return value; }
}
