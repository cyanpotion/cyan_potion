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

package com.xenoamess.cyan_potion.base.io.input.key;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author XenoAmess
 */
public enum KeyModEnum {
    /**
     * If this bit is set one or more Shift keys were held down.
     */
    GLFW_MOD_NORMAL(0),

    /**
     * If this bit is set one or more Shift keys were held down.
     */
    GLFW_MOD_SHIFT(0x1),

    /**
     * If this bit is set one or more Control keys were held down.
     */
    GLFW_MOD_CONTROL(0x2),

    /**
     * If this bit is set one or more Alt keys were held down.
     */
    GLFW_MOD_ALT(0x4),

    /**
     * If this bit is set one or more Super keys were held down.
     */
    GLFW_MOD_SUPER(0x8),

    /**
     * If this bit is set the Caps Lock key is enabled and the {@link org.lwjgl.glfw.GLFW#GLFW_LOCK_KEY_MODS
     * LOCK_KEY_MODS} input mode
     * is set.
     */
    GLFW_MOD_CAPS_LOCK(0x10),

    /**
     * If this bit is set the Num Lock key is enabled and the {@link org.lwjgl.glfw.GLFW#GLFW_LOCK_KEY_MODS
     * LOCK_KEY_MODS} input mode is
     * set.
     */
    GLFW_MOD_NUM_LOCK(0x20);

    public final int value;
    public static final KeyModEnum[] values = generateValues();

    KeyModEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public static KeyModEnum getByValue(int value) {
        if (value >= 0 && value < values.length) {
            return values[value];
        }
        return GLFW_MOD_NORMAL;
    }

    public static Collection<KeyModEnum> getModEnumsByValue(int value) {
        Collection<KeyModEnum> res = new ArrayList<>();
        for (KeyModEnum au : KeyModEnum.values()) {
            if ((au.value & value) != 0) {
                res.add(au);
            }
        }
        return res;
    }

    public static String getStringByValue(int value) {
        return getByValue(value).name();
    }

    private static final int maxValue() {
        KeyModEnum[] rawValues = values();
        int maxValue = rawValues[0].value;
        for (KeyModEnum au : rawValues) {
            if (maxValue < au.value) {
                maxValue = au.value;
            }
        }
        return maxValue;
    }

    private static final KeyModEnum[] generateValues() {
        KeyModEnum[] res = new KeyModEnum[maxValue() + 1];
        Arrays.fill(res, GLFW_MOD_NORMAL);

        for (KeyModEnum au : values()) {
            if (au.value >= 0) {
                res[au.value] = au;
            }
        }
        return res;
    }


}