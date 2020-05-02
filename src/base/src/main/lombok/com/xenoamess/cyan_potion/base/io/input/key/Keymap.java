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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.cyan_potion.base.exceptions.KeyShallBeXenoAmessKeyButItIsNotException;
import com.xenoamess.cyan_potion.base.io.input.gamepad.JXInputGamepadData;
import com.xenoamess.cyan_potion.base.io.input.gamepad.JXInputGamepadKeyEnum;
import com.xenoamess.cyan_potion.base.io.input.gamepad.JamepadGamepadKeyEnum;
import com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardKeyEnum;
import com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonKeyEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.xenoamess.commonx.java.util.Arraysx.fillNewSelf;
import static com.xenoamess.commonx.java.util.concurrent.atomic.AtomicBooleanUtilsx.flip;
import static org.lwjgl.glfw.GLFW.*;

/**
 * <p>Keymap class.</p>
 *
 * @author XenoAmess
 * @version 0.162.3-SNAPSHOT
 */
@EqualsAndHashCode
@ToString
public class Keymap {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(Keymap.class);

    /**
     * Constant <code>XENOAMESS_KEY_ESCAPE=GLFW_KEY_ESCAPE</code>
     */
    public static final int XENOAMESS_KEY_ESCAPE = GLFW_KEY_ESCAPE;
    /**
     * Constant <code>XENOAMESS_KEY_ENTER=GLFW_KEY_ENTER</code>
     */
    public static final int XENOAMESS_KEY_ENTER = GLFW_KEY_ENTER;
    /**
     * Constant <code>XENOAMESS_KEY_SPACE=GLFW_KEY_SPACE</code>
     */
    public static final int XENOAMESS_KEY_SPACE = GLFW_KEY_SPACE;
    /**
     * Constant <code>XENOAMESS_KEY_LEFT_SHIFT=GLFW_KEY_LEFT_SHIFT</code>
     */
    @SuppressWarnings("unused")
    public static final int XENOAMESS_KEY_LEFT_SHIFT = GLFW_KEY_LEFT_SHIFT;
    /**
     * Constant <code>XENOAMESS_KEY_RIGHT_SHIFT=GLFW_KEY_RIGHT_SHIFT</code>
     */
    @SuppressWarnings("unused")
    public static final int XENOAMESS_KEY_RIGHT_SHIFT = GLFW_KEY_RIGHT_SHIFT;

    /**
     * Constant <code>XENOAMESS_KEY_UP=GLFW_KEY_UP</code>
     */
    public static final int XENOAMESS_KEY_UP = GLFW_KEY_UP;
    /**
     * Constant <code>XENOAMESS_KEY_LEFT=GLFW_KEY_LEFT</code>
     */
    public static final int XENOAMESS_KEY_LEFT = GLFW_KEY_LEFT;
    /**
     * Constant <code>XENOAMESS_KEY_DOWN=GLFW_KEY_DOWN</code>
     */
    public static final int XENOAMESS_KEY_DOWN = GLFW_KEY_DOWN;
    /**
     * Constant <code>XENOAMESS_KEY_RIGHT=GLFW_KEY_RIGHT</code>
     */
    public static final int XENOAMESS_KEY_RIGHT = GLFW_KEY_RIGHT;
    /**
     * Constant <code>XENOAMESS_MOUSE_BUTTON_LEFT=GLFW_KEY_LAST + 1 + GLFW_MOUSE_BUTTON_LEFT</code>
     */
    public static final int XENOAMESS_MOUSE_BUTTON_LEFT =
            GLFW_KEY_LAST + 1 + GLFW_MOUSE_BUTTON_LEFT;
    /**
     * Constant <code>XENOAMESS_MOUSE_BUTTON_RIGHT=GLFW_KEY_LAST + 1 + GLFW_MOUSE_BUTTON_RIGHT</code>
     */
    public static final int XENOAMESS_MOUSE_BUTTON_RIGHT =
            GLFW_KEY_LAST + 1 + GLFW_MOUSE_BUTTON_RIGHT;
    /**
     * Constant <code>XENOAMESS_MOUSE_BUTTON_MIDDLE=GLFW_KEY_LAST + 1 + GLFW_MOUSE_BUTTON_MIDDLE</code>
     */
    public static final int XENOAMESS_MOUSE_BUTTON_MIDDLE =
            GLFW_KEY_LAST + 1 + GLFW_MOUSE_BUTTON_MIDDLE;

    /**
     * the map to convert raw-key-type to my-key-type
     */
    @Getter
    private final Map<Key, Key> keymap = new ConcurrentHashMap<>();

    /**
     * the map to convert my-key-type to raw-key-type
     */
    @Getter
    private final Map<Key, List<Key>> keymapReverse = new ConcurrentHashMap<>();

    @Getter
    private final AtomicBoolean[][] rawKeys =
            new AtomicBoolean[][]{
                    fillNewSelf(new AtomicBoolean[GLFW_KEY_LAST + 1]),
                    fillNewSelf(new AtomicBoolean[GLFW_MOUSE_BUTTON_LAST + 1]),
                    fillNewSelf(new AtomicBoolean[GLFW_JOYSTICK_LAST + 1]),
                    fillNewSelf(new AtomicBoolean[JXInputGamepadData.JXINPUT_KEY_LAST + 1])};

    @Getter
    private final AtomicBoolean[] myKeys = fillNewSelf(new AtomicBoolean[2000]);

    /**
     * <p>get.</p>
     *
     * @param rawKey rawKey
     * @return return
     */
    public Key get(Key rawKey) {
        Key res = getKeymap().get(rawKey);
        if (res == null) {
            res = Key.NULL;
        }
        return res;
    }

    /**
     * <p>put.</p>
     *
     * @param rawInput rawInput
     * @param myInput  a {@link java.lang.String} object.
     * @return return
     */
    @SuppressWarnings("UnusedReturnValue")
    public Key put(String rawInput, String myInput) {
        if (rawInput == null || myInput == null) {
            return null;
        }
        Integer rawInputI = null;
        Integer myInputI = null;
        int type = -1;

        try {
            rawInputI = KeyboardKeyEnum.valueOf(rawInput).value;
            type = Key.TYPE_KEY;
        } catch (Exception e) {
            LOGGER.debug("rawInput {} is not a Keyboard key.", rawInputI);
        }

        if (rawInputI == null) {
            try {
                switch (rawInput) {
                    case "GLFW_MOUSE_BUTTON_LEFT":
                        rawInput = "GLFW_MOUSE_BUTTON_1";
                        break;
                    case "GLFW_MOUSE_BUTTON_RIGHT":
                        rawInput = "GLFW_MOUSE_BUTTON_2";
                        break;
                    case "GLFW_MOUSE_BUTTON_MIDDLE":
                        rawInput = "GLFW_MOUSE_BUTTON_3";
                        break;
                    default:
                        //do nothing
                }

                rawInputI = MouseButtonKeyEnum.valueOf(rawInput).value;
                type = Key.TYPE_MOUSE;
            } catch (Exception e) {
                LOGGER.debug("rawInput {} is not a MouseButton key.", rawInputI);
            }
        }
        if (rawInputI == null) {
            try {
                rawInputI = JXInputGamepadKeyEnum.valueOf(rawInput).value;
                type = Key.TYPE_GAMEPAD;
            } catch (Exception e) {
                LOGGER.debug("rawInput {} is not a JXInputGamepad key.", rawInputI);
            }
        }
        if (rawInputI == null) {
            try {
                rawInputI = JamepadGamepadKeyEnum.valueOf(rawInput).value;
                type = Key.TYPE_GAMEPAD;
            } catch (Exception e) {
                LOGGER.debug("rawInput {} is not a JXInputGamepad key.", rawInputI);
            }
        }
        if (rawInputI == null) {
            return null;
        }


        try {
            Field field = Keymap.class.getField(myInput);
            myInputI = (Integer) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.debug("myInputI {} not in class Keymap.", myInputI);
        }


        if (myInputI == null) {
            return null;
        }

        return put(new Key(type, rawInputI), new Key(Key.TYPE_XENOAMESS_KEY,
                myInputI));
    }


    /**
     * <p>put.</p>
     *
     * @param rawKey rawKey
     * @param myKey  a {@link com.xenoamess.cyan_potion.base.io.input.key.Key} object.
     * @return return
     */
    public Key put(Key rawKey, Key myKey) {
        Key res = getKeymap().put(rawKey, myKey);
        List<Key> rawInputKeys = getKeymapReverse().computeIfAbsent(myKey, k -> new ArrayList<>());
        rawInputKeys.add(rawKey);

        return res;
    }

    /**
     * <p>keyFlipRaw.</p>
     *
     * @param rawKey rawKey
     */
    public void keyFlipRaw(Key rawKey) {
        keyFlip(getKeymap().get(rawKey));
        flip(getRawKeys()[rawKey.getType()][rawKey.getKey()]);
    }

    /**
     * <p>keyFlip.</p>
     *
     * @param myKey myKey
     */
    public void keyFlip(Key myKey) {
        if (myKey == null) {
            return;
        }
        if (myKey.getType() != Key.TYPE_XENOAMESS_KEY) {
            throw new KeyShallBeXenoAmessKeyButItIsNotException(myKey.toString());
        }
        flip(getMyKeys()[myKey.getKey()]);
    }

    /**
     * <p>isKeyDown.</p>
     *
     * @param myKey myKey
     * @return a boolean.
     */
    public boolean isKeyDown(Key myKey) {
        if (myKey.getType() != Key.TYPE_XENOAMESS_KEY) {
            throw new KeyShallBeXenoAmessKeyButItIsNotException(myKey.toString());
        }
        return getMyKeys()[myKey.getKey()].get();
    }

    /**
     * <p>isKeyDownRaw.</p>
     *
     * @param rawKey rawKey
     * @return a boolean.
     */
    @SuppressWarnings("unused")
    public boolean isKeyDownRaw(Key rawKey) {
        return getRawKeys()[rawKey.getType()][rawKey.getKey()].get();
    }
}
