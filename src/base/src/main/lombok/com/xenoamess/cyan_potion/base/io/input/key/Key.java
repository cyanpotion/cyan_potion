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

import lombok.Data;

/**
 * <p>Key class.</p>
 *
 * @author XenoAmess
 * @version 0.161.1
 */
@Data
public class Key {
    /**
     * Constant <code>TYPE_XENOAMESS_KEY=-1</code>
     */
    public static final int TYPE_XENOAMESS_KEY = -1;
    /**
     * Constant <code>TYPE_KEY=0</code>
     */
    public static final int TYPE_KEY = 0;
    /**
     * Constant <code>TYPE_MOUSE=1</code>
     */
    public static final int TYPE_MOUSE = 1;
    /**
     * not implemented yet
     */
    public static final int TYPE_JOYSTICK = 2;
    /**
     * Constant <code>TYPE_GAMEPAD=3</code>
     */
    public static final int TYPE_GAMEPAD = 3;

    /**
     * Constant <code>NULL</code>
     */
    public static final Key NULL = new Key(-666, -666);

    private final int type;
    private final int key;

    /**
     * <p>Constructor for Key.</p>
     *
     * @param key a int.
     */
    public Key(int key) {
        this(TYPE_XENOAMESS_KEY, key);
    }

    /**
     * <p>Constructor for Key.</p>
     *
     * @param type a int.
     * @param key  a int.
     */
    public Key(int type, int key) {
        this.type = type;
        this.key = key;
    }
}
