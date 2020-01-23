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

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import com.xenoamess.cyan_potion.base.DataCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author XenoAmess
 */
public class JamepadGamepadDevice extends AbstractGamepadDevice {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(JamepadGamepadDevice.class);

    private int jamepadGamepadDeviceIndex;
    private ControllerManager jamepadControllerManager;
    private ControllerState jamepadControllerState;

    public JamepadGamepadDevice(ControllerManager jamepadControllerManager, int jamepadGamepadDeviceIndex) {
        this.setJamepadControllerManager(jamepadControllerManager);
        this.setJamepadGamepadDeviceIndex(jamepadGamepadDeviceIndex);
    }


    /**
     * update of this will be performed in GasmepadInputManager.
     *
     * @see GamepadInputManager#update
     */
    @Override
    public void update() {
        this.setJamepadControllerState(this.getJamepadControllerManager().getState(this.getJamepadGamepadDeviceIndex()));
    }

    /**
     * <p>isConnected.</p>
     *
     * @return a boolean.
     */
    @Override
    public boolean isConnected() {
        return this.getJamepadControllerState().isConnected;
    }

    /**
     * <p>setVibration.</p>
     *
     * @param leftVibration  a int.
     * @param rightVibration a int.
     */
    @Override
    public void setVibration(int leftVibration, int rightVibration) {
        this.getJamepadControllerManager().doVibration(
                this.getJamepadGamepadDeviceIndex(),
                leftVibration / 65535.0F,
                rightVibration / 65535.0F,
                (int) DataCenter.FRAME_CAP * 1000
        );
    }

    public int getJamepadGamepadDeviceIndex() {
        return jamepadGamepadDeviceIndex;
    }

    public void setJamepadGamepadDeviceIndex(int jamepadGamepadDeviceIndex) {
        this.jamepadGamepadDeviceIndex = jamepadGamepadDeviceIndex;
    }

    public ControllerManager getJamepadControllerManager() {
        return jamepadControllerManager;
    }

    public void setJamepadControllerManager(ControllerManager jamepadControllerManager) {
        this.jamepadControllerManager = jamepadControllerManager;
    }

    public ControllerState getJamepadControllerState() {
        return jamepadControllerState;
    }

    public void setJamepadControllerState(ControllerState jamepadControllerState) {
        this.jamepadControllerState = jamepadControllerState;
    }
}
