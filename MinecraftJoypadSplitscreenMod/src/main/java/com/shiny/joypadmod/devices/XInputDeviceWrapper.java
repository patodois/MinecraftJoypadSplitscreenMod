package com.shiny.joypadmod.devices;

import com.ivan.xinput.XInputBatteryInformation;
import com.ivan.xinput.XInputButtons;
import com.ivan.xinput.XInputDevice;
import com.ivan.xinput.XInputDevice14;
import com.ivan.xinput.enums.XInputAxis;
import com.ivan.xinput.enums.XInputBatteryDeviceType;
import com.ivan.xinput.enums.XInputButton;
import com.ivan.xinput.exceptions.XInputNotLoadedException;
import com.shiny.joypadmod.helpers.LogHelper;

public class XInputDeviceWrapper extends InputDevice implements StandardGamepadDevice {

	public XInputDevice theDevice;
	public Boolean xInput14 = false;
	
	float[] deadZones = new float[] { 0.15f,0.15f,0.15f,0.15f,0.15f,0.15f };
	
	public XInputDeviceWrapper(int index) {
		super(index);

	}

	@Override
	public String getName() {
		String name = "XInput Device";
		return name;		
	}

	@Override
	public int getButtonCount() {
		return 15; // UNKNOWN is a sentinel, not a physical button.
	}

	@Override
	public int getAxisCount() {
		return 6;			
	}

	@Override
	public float getAxisValue(int axisIndex) {
		if (theDevice == null || !theDevice.isConnected() || axisIndex < 0 || axisIndex >= deadZones.length) return 0;
		float value = theDevice.getComponents().getAxes().get(XInputAxis.values()[axisIndex]); 		

		if (Math.abs(value) > deadZones[axisIndex])
		{
			XInputAxis axis = XInputAxis.values()[axisIndex];
			if (axis == XInputAxis.LEFT_THUMBSTICK_Y || axis == XInputAxis.RIGHT_THUMBSTICK_Y)
					value *= -1;
					
			return value;
		}
		
		return 0;	
	}

	@Override
	public String getAxisName(int index) {

		String name = XInputAxis.values()[index].toString();
		String ret;
		if (name.length() > 11)
		{
			ret = String.format("%s %s", name.substring(0, 9), name.charAt(name.length()-1));			
		}
		else
			ret = name;
		return ret;
	}

	@Override
	public float getDeadZone(int index) {
		return deadZones[index];
	}

	@Override
	public String getButtonName(int index) {
		return XInputButton.values()[index].toString();
	}

	@Override
	public Boolean isButtonPressed(int index) {
		
		return theDevice != null && theDevice.isConnected() && index >= 0 && index < getButtonCount() && isPressed(XInputButton.values()[index], theDevice.getComponents().getButtons());
	}

	@Override
	public Float getPovX() {
		
		if (isButtonPressed(XInputButton.DPAD_LEFT.ordinal()))
			return -1.0f;
		if (isButtonPressed(XInputButton.DPAD_RIGHT.ordinal()))
			return 1.0f;
		return 0f;
	}

	@Override
	public Float getPovY() {
		if (isButtonPressed(XInputButton.DPAD_UP.ordinal()))
			return -1.0f;
		if (isButtonPressed(XInputButton.DPAD_DOWN.ordinal()))
			return 1.0f;
		return 0f;
	}

	@Override
	public void setDeadZone(int axisIndex, float value) {
		if (axisIndex >= 0 && axisIndex < deadZones.length)
			deadZones[axisIndex] = Float.isNaN(value) ? 0.15f : Math.max(0f, Math.min(0.95f, value));
	}
	
	protected void setIndex(int index, Boolean useXInput14)
	{
		try {
						
			if (useXInput14)
			{
				xInput14 = true;
				theDevice = XInputDevice14.getDeviceFor(index);
			}
			else 
				theDevice = XInputDevice.getDeviceFor(index);
			myIndex = index;
			theDevice.poll();
		} catch (XInputNotLoadedException e) { 
			LogHelper.Fatal("Failed calling setIndex on XInputDevice: " + e.toString());
		}
	}

	@Override
	public Boolean isConnected() {
		return theDevice != null && theDevice.isConnected();
	}
	
	@Override
	public int getBatteryLevel()
	{
		try
		{
			XInputBatteryInformation gamepadBattInfo = ((XInputDevice14)theDevice).getBatteryInformation(XInputBatteryDeviceType.GAMEPAD);
		
			return gamepadBattInfo.getLevel().ordinal();
		}
		catch (Exception ex)
		{
			return -1;
		}
	}

	
    public Boolean isPressed(XInputButton buttonToCheck, XInputButtons buttons)
    {
    	switch(buttonToCheck)
    	{
    	case A:
    		return buttons.a;
    	case B:
    		return buttons.b;
    	case X:
    		return buttons.x;
    	case Y:
    		return buttons.y;
    	case BACK:
    		return buttons.back;
    	case START:
    		return buttons.start;
    	case LEFT_SHOULDER:
    		return buttons.lShoulder;
    	case RIGHT_SHOULDER:
    		return buttons.rShoulder;
    	case LEFT_THUMBSTICK:
    		return buttons.lThumb;
    	case RIGHT_THUMBSTICK:
    		return buttons.rThumb;
    	case DPAD_UP:
    		return buttons.up;
    	case DPAD_DOWN:
    		return buttons.down;
    	case DPAD_LEFT:
    		return buttons.left; 
    	case DPAD_RIGHT:
    		return buttons.right;
    	case GUIDE_BUTTON:
    		return buttons.guide;
    		
		default:
			return false;
    	
    	}
    }
}
