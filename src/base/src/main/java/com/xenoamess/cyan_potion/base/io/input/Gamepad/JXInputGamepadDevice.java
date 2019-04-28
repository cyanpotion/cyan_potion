package com.xenoamess.cyan_potion.base.io.input.Gamepad;

import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;

/**
 * @author XenoAmess
 */
public class JXInputGamepadDevice extends AbstractGamepadDevice {
    private int JXInputGamepadDeviceIndex;
    private XInputDevice rawXInputDevice;

    public JXInputGamepadDevice(int JXInputGamepadDeviceIndex) {
        this.setJXInputGamepadDeviceIndex(JXInputGamepadDeviceIndex);
        try {
            this.setRawXInputDevice(XInputDevice.getDeviceFor(this.getJXInputGamepadDeviceIndex()));
        } catch (XInputNotLoadedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        try {
            this.setRawXInputDevice(XInputDevice.getDeviceFor(this.getJXInputGamepadDeviceIndex()));
        } catch (XInputNotLoadedException e) {
            e.printStackTrace();
        }
        this.getRawXInputDevice().poll();
    }

    @Override
    public boolean isConnected() {
        return this.getRawXInputDevice().isConnected();
    }

    @Override
    public void setVibration(int leftVibration, int rightVibration) {
        this.getRawXInputDevice().setVibration(fixVibrationPower(leftVibration), fixVibrationPower(rightVibration));
    }

    public XInputDevice getRawXInputDevice() {
        return rawXInputDevice;
    }

    public int getJXInputGamepadDeviceIndex() {
        return JXInputGamepadDeviceIndex;
    }

    public void setJXInputGamepadDeviceIndex(int JXInputGamepadDeviceIndex) {
        this.JXInputGamepadDeviceIndex = JXInputGamepadDeviceIndex;
    }

    public void setRawXInputDevice(XInputDevice rawXInputDevice) {
        this.rawXInputDevice = rawXInputDevice;
    }
}
