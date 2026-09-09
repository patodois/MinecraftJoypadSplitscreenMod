package com.shiny.joypadmod.devices;
import com.ivan.xinput.XInputDevice;
import com.ivan.xinput.XInputDevice14;
/** Windows backend; listener-independent polling also covers player zero. */
public class XInputLibrary extends SnapshotInputLibrary {
    private boolean created;
    protected Boolean xInput14 = false;
    private final XInputDeviceWrapper[] devices = new XInputDeviceWrapper[4];
    @Override public void create() throws Exception {
        if (!System.getProperty("os.name", "").startsWith("Windows")) throw new Exception("XInput requires Windows");
        xInput14 = XInputDevice14.isAvailable();
        if (!xInput14 && !XInputDevice.isAvailable()) throw new Exception("XInput is unavailable");
        created = true;
        getController(0);
    }
    @Override public Boolean isCreated() { return created; }
    @Override public int getControllerCount() { return 4; }
    @Override public InputDevice getController(int index) {
        if (index < 0 || index >= devices.length) throw new IllegalArgumentException("XInput slot: " + index);
        if (devices[index] == null) {
            devices[index] = new XInputDeviceWrapper(index);
            devices[index].setIndex(index, xInput14);
        }
        selectDevice(devices[index]);
        return theDevice;
    }
    @Override public void poll() {
        if (theDevice == null) return;
        XInputDeviceWrapper device = (XInputDeviceWrapper)theDevice;
        if (device.theDevice == null) return;
        device.theDevice.poll();
        sampleState();
    }
}
