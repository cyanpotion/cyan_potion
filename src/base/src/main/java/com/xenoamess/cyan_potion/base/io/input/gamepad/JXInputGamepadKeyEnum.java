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

import java.util.Arrays;

/**
 * <p>JXInputGamepadKeyEnum class.</p>
 *
 * @author XenoAmess
 * @version 0.155.3
 * @see JXInputGamepadDevice
 * @deprecated
 */
@Deprecated
public enum JXInputGamepadKeyEnum {
    JXINPUT_KEY_A(0),
    JXINPUT_KEY_B(1),
    JXINPUT_KEY_X(2),
    JXINPUT_KEY_Y(3),
    JXINPUT_KEY_BACK(4),
    JXINPUT_KEY_START(5),
    JXINPUT_KEY_LB(6),
    JXINPUT_KEY_RB(7),
    JXINPUT_KEY_L(8),
    JXINPUT_KEY_R(9),
    JXINPUT_KEY_UP(10),
    JXINPUT_KEY_DOWN(11),
    JXINPUT_KEY_LEFT(12),
    JXINPUT_KEY_RIGHT(13),
    JXINPUT_KEY_GUIDE(14),
    JXINPUT_KEY_UNKNOWN(15),
    JXINPUT_KEY_LT(16),
    JXINPUT_KEY_RT(17);

    /**
     * Constant <code>values</code>
     */
    public final int value;
    private static final JXInputGamepadKeyEnum[] values = generateValues();

    JXInputGamepadKeyEnum(int value) {
        this.value = value;
    }

    /**
     * <p>Getter for the field <code>values</code>.</p>
     *
     * @return an array of {@link com.xenoamess.cyan_potion.base.io.input.gamepad.JXInputGamepadKeyEnum} objects.
     */
    public static JXInputGamepadKeyEnum[] getValues() {
        return values;
    }

    /**
     * <p>Getter for the field <code>value</code>.</p>
     *
     * @return a int.
     */
    public int getValue() {
        return value;
    }


    /**
     * <p>getByValue.</p>
     *
     * @param value a int.
     * @return a {@link com.xenoamess.cyan_potion.base.io.input.gamepad.JXInputGamepadKeyEnum} object.
     */
    public static JXInputGamepadKeyEnum getByValue(int value) {
        if (value >= 0 && value < getValues().length) {
            return getValues()[value];
        }
        return JXINPUT_KEY_UNKNOWN;
    }

    /**
     * <p>getStringByValue.</p>
     *
     * @param value a int.
     * @return a {@link java.lang.String} object.
     */
    public static String getStringByValue(int value) {
        return getByValue(value).name();
    }

    private static int maxValue() {
        JXInputGamepadKeyEnum[] rawValues = values();
        int maxValue = rawValues[0].value;
        for (JXInputGamepadKeyEnum au : rawValues) {
            if (maxValue < au.value) {
                maxValue = au.value;
            }
        }
        return maxValue;
    }

    private static JXInputGamepadKeyEnum[] generateValues() {
        JXInputGamepadKeyEnum[] res = new JXInputGamepadKeyEnum[maxValue() + 1];
        Arrays.fill(res, JXINPUT_KEY_UNKNOWN);

        for (JXInputGamepadKeyEnum au : values()) {
            if (au.value >= 0) {
                res[au.value] = au;
            }
        }
        return res;
    }

}
