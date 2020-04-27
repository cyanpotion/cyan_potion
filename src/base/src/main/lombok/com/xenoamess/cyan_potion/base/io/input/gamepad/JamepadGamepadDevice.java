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
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>JamepadGamepadDevice class.</p>
 *
 * @author XenoAmess
 * @version 0.162.2-SNAPSHOT
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class JamepadGamepadDevice extends AbstractGamepadDevice {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(JamepadGamepadDevice.class);
    @Getter
    @Setter
    private int jamepadGamepadDeviceIndex;
    @Getter
    @Setter
    private ControllerManager jamepadControllerManager;
    @Getter
    @Setter
    private ControllerState jamepadControllerState;

    /**
     * <p>Constructor for JamepadGamepadDevice.</p>
     *
     * @param jamepadControllerManager  a {@link com.studiohartman.jamepad.ControllerManager} object.
     * @param jamepadGamepadDeviceIndex a int.
     */
    public JamepadGamepadDevice(ControllerManager jamepadControllerManager, int jamepadGamepadDeviceIndex) {
        this.setJamepadControllerManager(jamepadControllerManager);
        this.setJamepadGamepadDeviceIndex(jamepadGamepadDeviceIndex);
    }


    /**
     * {@inheritDoc}
     * <p>
     * update of this will be performed in GasmepadInputManager.
     *
     * @see GamepadInputManager#update
     */
    @Override
    public void update() {
        this.setJamepadControllerState(this.getJamepadControllerManager().getState(this.getJamepadGamepadDeviceIndex()));
    }

    /**
     * {@inheritDoc}
     *
     * <p>isConnected.</p>
     */
    @Override
    public boolean isConnected() {
        return this.getJamepadControllerState().isConnected;
    }

    /**
     * {@inheritDoc}
     *
     * <p>setVibration.</p>
     */
    @Override
    public void setVibration(int leftVibration, int rightVibration) {
        this.setVibration(leftVibration, rightVibration, Integer.MAX_VALUE);
    }

    /**
     * <p>setVibration.</p>
     *
     * @param leftVibration  a int.
     * @param rightVibration a int.
     * @param duration_ms    a int.
     */
    public void setVibration(int leftVibration, int rightVibration, int duration_ms) {
        this.getJamepadControllerManager().doVibration(
                this.getJamepadGamepadDeviceIndex(),
                leftVibration / 65535.0F,
                rightVibration / 65535.0F,
                duration_ms
        );
    }

}
