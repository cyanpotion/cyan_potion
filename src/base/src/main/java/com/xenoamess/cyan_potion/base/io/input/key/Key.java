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

/**
 * @author XenoAmess
 */
public class Key {
    public static final int TYPE_XENOAMESS_KEY = -1;
    public static final int TYPE_KEY = 0;
    public static final int TYPE_MOUSE = 1;
    /**
     * not implemented yet
     */
    public static final int TYPE_JOYSTICK = 2;
    public static final int TYPE_GAMEPAD = 3;

    public static final Key NULL = new Key(-666, -666);

    private final int type;
    private final int key;

    public Key(int key) {
        this(TYPE_XENOAMESS_KEY, key);
    }

    public Key(int type, int key) {
        this.type = type;
        this.key = key;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!object.getClass().equals(this.getClass())) {
            return false;
        }
        final Key key = (Key) (object);
        return (this.getType() == key.getType() && this.getKey() == key.getKey());
    }

    @Override
    public int hashCode() {
        return (this.getType() << 10) | this.getKey();
    }

    @Override
    public String toString() {
        return "type : " + this.type + ", key : " + key;
    }

    public int getType() {
        return type;
    }

    public int getKey() {
        return key;
    }
}
