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

package com.xenoamess.cyan_potion.base.io.input.mouse;

import java.util.Arrays;

/**
 * <p>MouseButtonKeyEnum class.</p>
 *
 * @author XenoAmess
 * @version 0.155.3-SNAPSHOT
 */
public enum MouseButtonKeyEnum {
    /**
     * GLFW_MOUSE_BUTTON_LEFT   = GLFW_MOUSE_BUTTON_1,
     */
    GLFW_MOUSE_BUTTON_1(0),
    /**
     * GLFW_MOUSE_BUTTON_RIGHT  =GLFW_MOUSE_BUTTON_2,
     */
    GLFW_MOUSE_BUTTON_2(1),
    /**
     * GLFW_MOUSE_BUTTON_MIDDLE =GLFW_MOUSE_BUTTON_3;
     */
    GLFW_MOUSE_BUTTON_3(2),

    GLFW_MOUSE_BUTTON_4(3),

    GLFW_MOUSE_BUTTON_5(4),

    GLFW_MOUSE_BUTTON_6(5),

    GLFW_MOUSE_BUTTON_7(6),

    GLFW_MOUSE_BUTTON_8(7);


    /**
     * Constant <code>values</code>
     */
    public final int value;
    private static final MouseButtonKeyEnum[] values = generateValues();

    MouseButtonKeyEnum(int value) {
        this.value = value;
    }

    /**
     * <p>Getter for the field <code>values</code>.</p>
     *
     * @return an array of {@link com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonKeyEnum} objects.
     */
    public static MouseButtonKeyEnum[] getValues() {
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
     * @return a {@link com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonKeyEnum} object.
     */
    public static MouseButtonKeyEnum getByValue(int value) {
        if (value >= 0 && value < getValues().length) {
            return getValues()[value];
        }
        return GLFW_MOUSE_BUTTON_1;
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
        MouseButtonKeyEnum[] rawValues = values();
        int maxValue = rawValues[0].value;
        for (MouseButtonKeyEnum au : rawValues) {
            if (maxValue < au.value) {
                maxValue = au.value;
            }
        }
        return maxValue;
    }

    private static MouseButtonKeyEnum[] generateValues() {
        MouseButtonKeyEnum[] res = new MouseButtonKeyEnum[maxValue() + 1];
        Arrays.fill(res, GLFW_MOUSE_BUTTON_1);

        for (MouseButtonKeyEnum au : values()) {
            if (au.value >= 0) {
                res[au.value] = au;
            }
        }
        return res;
    }


}
