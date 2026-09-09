package com.shiny.joypadmod.devices;

public final class MacGamepadDevice extends InputDevice implements StandardGamepadDevice {
    private final float[] state = new float[23];
    private final float[] deadzones = {.15f, .15f, .15f, .15f, .10f, .10f};
    private String name;
    private static final String[] BUTTONS = {"A", "B", "X", "Y", "Back", "Start", "LB", "RB", "LS", "RS", "D-pad Up", "D-pad Down", "D-pad Left", "D-pad Right", "Guide"};
    private static final String[] AXES = {"LS X", "LS Y", "RS X", "RS Y", "LT", "RT"};
    public MacGamepadDevice(int index) { super(index); name = "Controller " + (index + 1); }
    void poll() {
        MacGamepadNative.read(myIndex, state);
        String detected = MacGamepadNative.name(myIndex);
        if (detected != null && !detected.isEmpty()) name = detected;
    }
    @Override public String getName() { return "macOS - " + name; }
    public String getHardwareName() { return name; }
    @Override public int getButtonCount() { return 15; }
    @Override public int getAxisCount() { return 6; }
    @Override public String getButtonName(int i) { return BUTTONS[i]; }
    @Override public String getAxisName(int i) { return AXES[i]; }
    @Override public Boolean isConnected() { return state[0] != 0f; }
    @Override public Boolean isButtonPressed(int i) { return isConnected() && i >= 0 && i < 15 && state[1 + i] > .5f; }
    @Override public float getAxisValue(int i) {
        if (!isConnected() || i < 0 || i >= 6) return 0;
        float value = state[16 + i];
        return Math.abs(value) > deadzones[i] ? value : 0;
    }
    @Override public float getDeadZone(int i) { return deadzones[i]; }
    @Override public void setDeadZone(int i, float v) { deadzones[i] = Float.isNaN(v) ? .15f : Math.max(0f, Math.min(.95f, v)); }
    @Override public Float getPovX() { return (isButtonPressed(13) ? 1f : 0f) - (isButtonPressed(12) ? 1f : 0f); }
    @Override public Float getPovY() { return (isButtonPressed(11) ? 1f : 0f) - (isButtonPressed(10) ? 1f : 0f); }
    @Override public int getBatteryLevel() { return (int)state[22]; }
}
