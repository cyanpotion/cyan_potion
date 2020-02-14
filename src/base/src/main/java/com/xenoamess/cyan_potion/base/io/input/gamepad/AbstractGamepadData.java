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

import com.xenoamess.cyan_potion.base.GameWindow;

/**
 * <p>Abstract AbstractGamepadData class.</p>
 *
 * @author XenoAmess
 * @version 0.158.1
 */
public abstract class AbstractGamepadData {

    private final AbstractGamepadDevice gamepadDevice;

    /**
     * <p>Constructor for AbstractGamepadData.</p>
     *
     * @param gamepadDevice gamepadDevice
     */
    public AbstractGamepadData(AbstractGamepadDevice gamepadDevice) {
        this.gamepadDevice = gamepadDevice;
    }

    /**
     * <p>Getter for the field <code>gamepadDevice</code>.</p>
     *
     * @return return
     */
    public AbstractGamepadDevice getGamepadDevice() {
        return this.gamepadDevice;
    }

    /**
     * <p>updateGamepadStatus.</p>
     *
     * @param gameWindow gameWindow
     */
    public abstract void updateGamepadStatus(GameWindow gameWindow);

    /**
     * <p>reset.</p>
     */
    public abstract void reset();

    /**
     * <p>update.</p>
     *
     * @param gameWindow gameWindow
     */
    public void update(GameWindow gameWindow) {
        AbstractGamepadDevice gamepadDeviceLocal = this.getGamepadDevice();
        if (gamepadDeviceLocal != null) {
            gamepadDeviceLocal.update();
        }

        if (gamepadDeviceLocal == null || !gamepadDeviceLocal.isConnected()) {
            this.reset();
        } else {
            updateGamepadStatus(gameWindow);
        }
    }


}
