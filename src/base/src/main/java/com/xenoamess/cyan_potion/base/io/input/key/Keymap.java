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

import com.xenoamess.cyan_potion.base.exceptions.KeyShallBeXenoAmessKeyButItIsNotException;
import com.xenoamess.cyan_potion.base.io.input.Gamepad.JXInputGamepadData;
import org.apache.commons.lang3.NotImplementedException;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.xenoamess.commonx.java.util.Arraysx.fillNew;
import static com.xenoamess.commonx.java.util.concurrent.atomic.AtomicBooleanUtilsx.flip;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author XenoAmess
 */
public class Keymap {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(Keymap.class);

    public static final int XENOAMESS_KEY_ESCAPE = GLFW_KEY_ESCAPE;
    public static final int XENOAMESS_KEY_ENTER = GLFW_KEY_ENTER;
    public static final int XENOAMESS_KEY_SPACE = GLFW_KEY_SPACE;
    public static final int XENOAMESS_KEY_LEFT_SHIFT = GLFW_KEY_LEFT_SHIFT;
    public static final int XENOAMESS_KEY_RIGHT_SHIFT = GLFW_KEY_RIGHT_SHIFT;

    public static final int XENOAMESS_KEY_UP = GLFW_KEY_UP;
    public static final int XENOAMESS_KEY_LEFT = GLFW_KEY_LEFT;
    public static final int XENOAMESS_KEY_DOWN = GLFW_KEY_DOWN;
    public static final int XENOAMESS_KEY_RIGHT = GLFW_KEY_RIGHT;
    public static final int XENOAMESS_MOUSE_BUTTON_LEFT =
            GLFW_KEY_LAST + 1 + GLFW_MOUSE_BUTTON_LEFT;
    public static final int XENOAMESS_MOUSE_BUTTON_RIGHT =
            GLFW_KEY_LAST + 1 + GLFW_MOUSE_BUTTON_RIGHT;
    public static final int XENOAMESS_MOUSE_BUTTON_MIDDLE =
            GLFW_KEY_LAST + 1 + GLFW_MOUSE_BUTTON_MIDDLE;

    private final Map<Key, Key> keymap = new ConcurrentHashMap<>();

    private final Map<Key, ArrayList> keymapReverse = new ConcurrentHashMap<>();

    private final AtomicBoolean[][] rawKeys =
            new AtomicBoolean[][]{
                    fillNew(new AtomicBoolean[GLFW_KEY_LAST + 1]),
                    fillNew(new AtomicBoolean[GLFW_MOUSE_BUTTON_LAST + 1]),
                    fillNew(new AtomicBoolean[GLFW_JOYSTICK_LAST + 1]),
                    fillNew(new AtomicBoolean[JXInputGamepadData.JXINPUT_KEY_LAST + 1])};

    private final AtomicBoolean[] myKeys = fillNew(new AtomicBoolean[2000]);

    public Key get(Key rawKey) {
        Key res = getKeymap().get(rawKey);
        if (res == null) {
            res = Key.NULL;
        }
        return res;
    }

    public Key put(String rawInput, String myInput) {
        if (rawInput == null || myInput == null) {
            return null;
        }
        Integer rawInputI = null;
        Integer myInputI = null;
        try {
            Field field = GLFW.class.getField(rawInput);
            field.setAccessible(true);
            rawInputI = (Integer) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.debug("Keymap.put(String rawInput, String myInput) fail", rawInput, myInput, e);
        }
        if (rawInputI == null) {
            return null;
        }
        try {
            Field field = Keymap.class.getField(myInput);
            myInputI = (Integer) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.debug("Keymap.put(String rawInput, String myInput) fail", rawInput, myInput, e);
        }

        if (myInputI == null) {
            return null;
        }

        int type;

        if (rawInput.startsWith("GLFW_KEY")) {
            type = Key.TYPE_KEY;
        } else if (rawInput.startsWith("GLFW_MOUSE_BUTTON")) {
            type = Key.TYPE_MOUSE;
        } else {
            throw new NotImplementedException("if you want to implement Joystick you should add" +
                    " it here");
        }
        return put(new Key(type, rawInputI), new Key(Key.TYPE_XENOAMESS_KEY,
                myInputI));
    }


    public Key put(Key rawKey, Key myKey) {
        Key res = getKeymap().put(rawKey, myKey);
        ArrayList rawInputKeys = getKeymapReverse().get(myKey);
        if (rawInputKeys == null) {
            rawInputKeys = new ArrayList<Integer>();
            getKeymapReverse().put(myKey, rawInputKeys);
        }
        rawInputKeys.add(rawKey);

        return res;
    }

    public void keyFlipRaw(Key rawKey) {
        keyFlip(getKeymap().get(rawKey));
        flip(getRawKeys()[rawKey.getType()][rawKey.getKey()]);
    }

    public void keyFlip(Key myKey) {
        if (myKey == null) {
            return;
        }
        if (myKey.getType() != Key.TYPE_XENOAMESS_KEY) {
            throw new KeyShallBeXenoAmessKeyButItIsNotException(myKey.toString());
        }
        flip(getMyKeys()[myKey.getKey()]);
    }

    public boolean isKeyDown(Key myKey) {
        if (myKey.getType() != Key.TYPE_XENOAMESS_KEY) {
            throw new KeyShallBeXenoAmessKeyButItIsNotException(myKey.toString());
        }
        return getMyKeys()[myKey.getKey()].get();
    }

    public boolean isKeyDownRaw(Key rawKey) {
        return getRawKeys()[rawKey.getType()][rawKey.getKey()].get();
    }

    /**
     * @return the map to convert raw-key-type to my-key-type
     */
    public Map<Key, Key> getKeymap() {
        return keymap;
    }

    /**
     * @return the map to convert my-key-type to raw-key-type
     */
    public Map<Key, ArrayList> getKeymapReverse() {
        return keymapReverse;
    }

    public AtomicBoolean[][] getRawKeys() {
        return rawKeys;
    }

    public AtomicBoolean[] getMyKeys() {
        return myKeys;
    }

}
