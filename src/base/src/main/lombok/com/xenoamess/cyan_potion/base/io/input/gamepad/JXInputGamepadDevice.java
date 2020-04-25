/*
 * MIT License
 *
 * Copyright (c) 2020 XenoAmess
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;
import com.xenoamess.cyan_potion.base.DataCenter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * notice that this JXInput is deprecated
 * since it can only work on windows.
 * we mainly use Jamepad now,
 * which is thought to have equal facility to JXInput,
 * but it can work on windows/linux/mac
 * <p>
 * If you really want to use it,
 * then you shall call DataCenter.setUsingJXInput(true) in code plugin rightBeforeGamepadInputInit.
 *
 * @author XenoAmess
 * @version 0.162.1
 * @see DataCenter#isUsingJXInput()
 * @deprecated
 */
@EqualsAndHashCode(callSuper = true)
@ToString
@Deprecated
public class JXInputGamepadDevice extends AbstractGamepadDevice {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(JXInputGamepadDevice.class);
    @Getter
    @Setter
    private int jXInputGamepadDeviceIndex;
    @Getter
    @Setter
    private XInputDevice rawXInputDevice;

    /**
     * <p>Constructor for JXInputGamepadDevice.</p>
     *
     * @param jXInputGamepadDeviceIndex a int.
     */
    public JXInputGamepadDevice(int jXInputGamepadDeviceIndex) {
        this.setJXInputGamepadDeviceIndex(jXInputGamepadDeviceIndex);
        try {
            this.setRawXInputDevice(XInputDevice.getDeviceFor(this.getJXInputGamepadDeviceIndex()));
        } catch (XInputNotLoadedException e) {
            LOGGER.error("this.setRawXInputDevice(XInputDevice.getDeviceFor(this.getJXInputGamepadDeviceIndex()));", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        try {
            this.setRawXInputDevice(XInputDevice.getDeviceFor(this.getJXInputGamepadDeviceIndex()));
        } catch (XInputNotLoadedException e) {
            LOGGER.error("this.setRawXInputDevice(XInputDevice.getDeviceFor(this.getJXInputGamepadDeviceIndex()));", e);
        }
        this.getRawXInputDevice().poll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return this.getRawXInputDevice().isConnected();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVibration(int leftVibration, int rightVibration) {
        this.getRawXInputDevice().setVibration(fixVibrationPower(leftVibration), fixVibrationPower(rightVibration));
    }

}
