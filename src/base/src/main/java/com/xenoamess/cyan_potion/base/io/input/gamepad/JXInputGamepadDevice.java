/*
 * MIT License
 *
 * Copyright (c) 2019 XenoAmess
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.xenoamess.cyan_potion.base.io.input.gamepad;

import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author XenoAmess
 */
public class JXInputGamepadDevice extends AbstractGamepadDevice {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(JXInputGamepadDevice.class);

    private int JXInputGamepadDeviceIndex;
    private XInputDevice rawXInputDevice;

    public JXInputGamepadDevice(int JXInputGamepadDeviceIndex) {
        this.setJXInputGamepadDeviceIndex(JXInputGamepadDeviceIndex);
        try {
            this.setRawXInputDevice(XInputDevice.getDeviceFor(this.getJXInputGamepadDeviceIndex()));
        } catch (XInputNotLoadedException e) {
            LOGGER.error("this.setRawXInputDevice(XInputDevice.getDeviceFor(this.getJXInputGamepadDeviceIndex()));", e);
        }
    }

    @Override
    public void update() {
        try {
            this.setRawXInputDevice(XInputDevice.getDeviceFor(this.getJXInputGamepadDeviceIndex()));
        } catch (XInputNotLoadedException e) {
            LOGGER.error("this.setRawXInputDevice(XInputDevice.getDeviceFor(this.getJXInputGamepadDeviceIndex()));", e);
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
