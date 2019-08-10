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
 * Notice that this class is a duplicate of JXInputGamepadKeyEnum.
 * But for convenience, we just split them.
 *
 * @author XenoAmess
 * @see JXInputGamepadKeyEnum
 */
public enum JamepadGamepadKeyEnum {
    JAMEPAD_KEY_A(0),
    JAMEPAD_KEY_B(1),
    JAMEPAD_KEY_X(2),
    JAMEPAD_KEY_Y(3),
    JAMEPAD_KEY_BACK(4),
    JAMEPAD_KEY_START(5),
    JAMEPAD_KEY_LB(6),
    JAMEPAD_KEY_RB(7),
    JAMEPAD_KEY_L(8),
    JAMEPAD_KEY_R(9),
    JAMEPAD_KEY_UP(10),
    JAMEPAD_KEY_DOWN(11),
    JAMEPAD_KEY_LEFT(12),
    JAMEPAD_KEY_RIGHT(13),
    JAMEPAD_KEY_GUIDE(14),
    JAMEPAD_KEY_UNKNOWN(15),
    JAMEPAD_KEY_LT(16),
    JAMEPAD_KEY_RT(17);

    public final int value;
    public static final JamepadGamepadKeyEnum[] values = generateValues();

    JamepadGamepadKeyEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public static JamepadGamepadKeyEnum getByValue(int value) {
        if (value >= 0 && value < values.length) {
            return values[value];
        }
        return JAMEPAD_KEY_UNKNOWN;
    }

    public static String getStringByValue(int value) {
        return getByValue(value).name();
    }

    private static final int maxValue() {
        JamepadGamepadKeyEnum[] rawValues = values();
        int maxValue = rawValues[0].value;
        for (JamepadGamepadKeyEnum au : rawValues) {
            if (maxValue < au.value) {
                maxValue = au.value;
            }
        }
        return maxValue;
    }

    private static final JamepadGamepadKeyEnum[] generateValues() {
        JamepadGamepadKeyEnum[] res = new JamepadGamepadKeyEnum[maxValue() + 1];
        Arrays.fill(res, JAMEPAD_KEY_UNKNOWN);

        for (JamepadGamepadKeyEnum au : values()) {
            if (au.value >= 0) {
                res[au.value] = au;
            }
        }
        return res;
    }

}
