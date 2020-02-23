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

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * <p>Abstract AbstractGamepadDevice class.</p>
 *
 * @author XenoAmess
 * @version 0.161.3-SNAPSHOT
 */
@EqualsAndHashCode
@ToString
public abstract class AbstractGamepadDevice {
    /**
     * Constant <code>MIN_VIBRATION_POWER=0</code>
     */
    public static final int MIN_VIBRATION_POWER = 0;
    /**
     * Constant <code>MAX_VIBRATION_POWER=65535</code>
     */
    public static final int MAX_VIBRATION_POWER = 65535;

    /**
     * <p>update.</p>
     */
    public abstract void update();

    /**
     * <p>isConnected.</p>
     *
     * @return a boolean.
     */
    public abstract boolean isConnected();

    /**
     * <p>setVibration.</p>
     *
     * @param leftVibration  a int.
     * @param rightVibration a int.
     */
    public abstract void setVibration(int leftVibration, int rightVibration);

    /**
     * <p>fixVibrationPower.</p>
     *
     * @param originalVibrationPower a int.
     * @return a int.
     */
    public static int fixVibrationPower(int originalVibrationPower) {
        if (originalVibrationPower < MIN_VIBRATION_POWER) {
            originalVibrationPower = MIN_VIBRATION_POWER;
        }
        if (originalVibrationPower > MAX_VIBRATION_POWER) {
            originalVibrationPower = MAX_VIBRATION_POWER;
        }
        return originalVibrationPower;
    }
}
