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

import java.util.Arrays;

/**
 * @author XenoAmess
 */
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

    public final int value;
    public static final JXInputGamepadKeyEnum[] values = generateValues();

    JXInputGamepadKeyEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public static JXInputGamepadKeyEnum getByValue(int value) {
        if (value >= 0 && value < values.length) {
            return values[value];
        }
        return JXINPUT_KEY_UNKNOWN;
    }

    public static String getStringByValue(int value) {
        return getByValue(value).name();
    }

    private static final int maxValue() {
        JXInputGamepadKeyEnum[] rawValues = values();
        int maxValue = rawValues[0].value;
        for (JXInputGamepadKeyEnum au : rawValues) {
            if (maxValue < au.value) {
                maxValue = au.value;
            }
        }
        return maxValue;
    }

    private static final JXInputGamepadKeyEnum[] generateValues() {
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