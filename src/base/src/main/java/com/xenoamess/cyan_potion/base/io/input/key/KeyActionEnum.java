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

package com.xenoamess.cyan_potion.base.io.input.key;

import java.util.Arrays;

/**
 * <p>KeyActionEnum class.</p>
 *
 * @author XenoAmess
 * @version 0.155.1-SNAPSHOT
 */
public enum KeyActionEnum {
    GLFW_RELEASE(0),
    GLFW_PRESS(1),
    GLFW_REPEAT(2);

    /**
     * Constant <code>values</code>
     */
    public final int value;
    private static final KeyActionEnum[] values = generateValues();

    KeyActionEnum(int value) {
        this.value = value;
    }

    /**
     * <p>Getter for the field <code>values</code>.</p>
     *
     * @return an array of {@link com.xenoamess.cyan_potion.base.io.input.key.KeyActionEnum} objects.
     */
    public static KeyActionEnum[] getValues() {
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
     * @return a {@link com.xenoamess.cyan_potion.base.io.input.key.KeyActionEnum} object.
     */
    public static KeyActionEnum getByValue(int value) {
        if (value >= 0 && value < getValues().length) {
            return getValues()[value];
        }
        return GLFW_REPEAT;
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
        KeyActionEnum[] rawValues = values();
        int maxValue = rawValues[0].value;
        for (KeyActionEnum au : rawValues) {
            if (maxValue < au.value) {
                maxValue = au.value;
            }
        }
        return maxValue;
    }

    private static KeyActionEnum[] generateValues() {
        KeyActionEnum[] res = new KeyActionEnum[maxValue() + 1];
        Arrays.fill(res, GLFW_REPEAT);

        for (KeyActionEnum au : values()) {
            if (au.value >= 0) {
                res[au.value] = au;
            }
        }
        return res;
    }


}
