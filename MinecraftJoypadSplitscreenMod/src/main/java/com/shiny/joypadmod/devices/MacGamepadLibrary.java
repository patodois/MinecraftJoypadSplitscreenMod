package com.shiny.joypadmod.devices;

public final class MacGamepadLibrary extends SnapshotInputLibrary {
    private boolean created;
    private final MacGamepadDevice[] devices = new MacGamepadDevice[4];
    @Override public void create() throws Exception {
        MacGamepadNative.load();
        created = true;
        getController(0);
    }
    @Override public Boolean isCreated() { return created; }
    @Override public int getControllerCount() { return Math.max(1, MacGamepadNative.slotCount()); }
    @Override public InputDevice getController(int slot) {
        if (slot < 0 || slot >= 4) throw new IllegalArgumentException("Gamepad slot: " + slot);
        if (devices[slot] == null) {
            devices[slot] = new MacGamepadDevice(slot);
            devices[slot].poll();
        }
        selectDevice(devices[slot]);
        return theDevice;
    }
    @Override public void poll() {
        if (theDevice == null) return;
        ((MacGamepadDevice)theDevice).poll();
        sampleState();
    }
}
