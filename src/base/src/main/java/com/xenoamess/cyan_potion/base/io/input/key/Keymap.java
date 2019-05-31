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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

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


    // Filling

    /**
     * Wrapper of the same name function in java.util.Arrays,
     * but returns the array itself after filling.
     *
     * @see Arrays#fill(long[] a, long val)
     */
    public static long[] fill(long[] a, long val) {
        Arrays.fill(a, val);
        return a;
    }

    /**
     * Wrapper of the same name function in java.util.Arrays,
     * but returns the array itself after filling.
     *
     * @see Arrays#fill(long[] a, int fromIndex, int toIndex, long val)
     */
    public static long[] fill(long[] a, int fromIndex, int toIndex, long val) {
        Arrays.fill(a, fromIndex, toIndex, val);
        return a;
    }

    /**
     * Wrapper of the same name function in java.util.Arrays,
     * but returns the array itself after filling.
     *
     * @see Arrays#fill(int[] a, int val)
     */
    public static int[] fill(int[] a, int val) {
        Arrays.fill(a, val);
        return a;
    }

    /**
     * Wrapper of the same name function in java.util.Arrays,
     * but returns the array itself after filling.
     *
     * @see Arrays#fill(int[] a, int fromIndex, int toIndex, int val)
     */
    public static int[] fill(int[] a, int fromIndex, int toIndex, int val) {
        Arrays.fill(a, fromIndex, toIndex, val);
        return a;
    }

    /**
     * Wrapper of the same name function in java.util.Arrays,
     * but returns the array itself after filling.
     *
     * @see Arrays#fill(short[] a, short val)
     */
    public static short[] fill(short[] a, short val) {
        Arrays.fill(a, val);
        return a;
    }

    /**
     * Wrapper of the same name function in java.util.Arrays,
     * but returns the array itself after filling.
     *
     * @see Arrays#fill(short[] a, int fromIndex, int toIndex, short val)
     */
    public static short[] fill(short[] a, int fromIndex, int toIndex, short val) {
        Arrays.fill(a, fromIndex, toIndex, val);
        return a;
    }

    /**
     * Wrapper of the same name function in java.util.Arrays,
     * but returns the array itself after filling.
     *
     * @see Arrays#fill(char[] a, char val)
     */
    public static char[] fill(char[] a, char val) {
        Arrays.fill(a, val);
        return a;
    }

    /**
     * Wrapper of the same name function in java.util.Arrays,
     * but returns the array itself after filling.
     *
     * @see Arrays#fill(char[] a, int fromIndex, int toIndex, char val)
     */
    public static char[] fill(char[] a, int fromIndex, int toIndex, char val) {
        Arrays.fill(a, fromIndex, toIndex, val);
        return a;
    }

    /**
     * Wrapper of the same name function in java.util.Arrays,
     * but returns the array itself after filling.
     *
     * @see Arrays#fill(byte[] a, byte val)
     */
    public static byte[] fill(byte[] a, byte val) {
        Arrays.fill(a, val);
        return a;
    }

    /**
     * Wrapper of the same name function in java.util.Arrays,
     * but returns the array itself after filling.
     *
     * @see Arrays#fill(byte[] a, int fromIndex, int toIndex, byte val)
     */
    public static byte[] fill(byte[] a, int fromIndex, int toIndex, byte val) {
        Arrays.fill(a, fromIndex, toIndex, val);
        return a;
    }

    /**
     * Wrapper of the same name function in java.util.Arrays,
     * but returns the array itself after filling.
     *
     * @see Arrays#fill(boolean[] a, boolean val)
     */
    public static boolean[] fill(boolean[] a, boolean val) {
        Arrays.fill(a, val);
        return a;
    }

    /**
     * Wrapper of the same name function in java.util.Arrays,
     * but returns the array itself after filling.
     *
     * @see Arrays#fill(boolean[] a, int fromIndex, int toIndex, boolean val)
     */
    public static boolean[] fill(boolean[] a, int fromIndex, int toIndex,
                                 boolean val) {
        Arrays.fill(a, fromIndex, toIndex, val);
        return a;
    }

    /**
     * Wrapper of the same name function in java.util.Arrays,
     * but returns the array itself after filling.
     *
     * @see Arrays#fill(double[] a, double val)
     */
    public static double[] fill(double[] a, double val) {
        Arrays.fill(a, val);
        return a;
    }

    /**
     * Wrapper of the same name function in java.util.Arrays,
     * but returns the array itself after filling.
     *
     * @see Arrays#fill(double[] a, int fromIndex, int toIndex, double val)
     */
    public static double[] fill(double[] a, int fromIndex, int toIndex, double val) {
        Arrays.fill(a, fromIndex, toIndex, val);
        return a;
    }

    /**
     * Wrapper of the same name function in java.util.Arrays,
     * but returns the array itself after filling.
     *
     * @see Arrays#fill(float[] a, float val)
     */
    public static float[] fill(float[] a, float val) {
        Arrays.fill(a, val);
        return a;
    }

    /**
     * Wrapper of the same name function in java.util.Arrays,
     * but returns the array itself after filling.
     *
     * @see Arrays#fill(float[] a, int fromIndex, int toIndex, float val)
     */
    public static float[] fill(float[] a, int fromIndex, int toIndex, float val) {
        Arrays.fill(a, fromIndex, toIndex, val);
        return a;
    }

    /**
     * Wrapper of the same name function in java.util.Arrays,
     * but returns the array itself after filling.
     *
     * @see Arrays#fill(Object[] a, Object val)
     */
    public static <T> T[] fill(T[] a, T val) {
        Arrays.fill(a, val);
        return a;
    }

    /**
     * Wrapper of the same name function in java.util.Arrays,
     * but returns the array itself after filling.
     *
     * @see Arrays#fill(Object[] a, int fromIndex, int toIndex, Object val)
     * @since 3.10
     */
    public static <T> T[] fill(T[] a, int fromIndex, int toIndex, T val) {
        Arrays.fill(a, fromIndex, toIndex, val);
        return a;
    }

    /**
     * <p>Fill a array with creating new instances of the component class
     * using constructor that accept 0 arguments.
     *
     * <pre>
     *     private final AtomicBoolean[][] rawKeys =
     *             new AtomicBoolean[][]{
     *                     fillNew(new AtomicBoolean[1000]),
     *                     fillNew(new AtomicBoolean[1000]),
     *                     fillNew(new AtomicBoolean[1000]),
     *                     fillNew(new AtomicBoolean[1000])};
     * </pre>
     *
     * @param <T>   the component type of the array
     * @param array the array to be filled
     * @return the same array
     * @throws IllegalArgumentException if array is null,
     *                                  or if T have no such constructor,
     *                                  or the constructor is not accessible,
     *                                  or the class cannot be instantiated.
     * @since 3.10
     */
    public static <T> T[] fillNew(T[] array) {
        if (array == null) {
            throw new IllegalArgumentException("The input array must not be null.");
        }
        Class arrayClass = array.getClass();
        Class componentClass = arrayClass.getComponentType();
        try {
            Constructor defaultConstructor = componentClass.getConstructor();
            for (int i = 0, len = array.length; i < len; i++) {
                array[i] = (T) defaultConstructor.newInstance();
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "The class must have an constructor that accept 0 arguments, but not : "
                            + componentClass.getCanonicalName()
            );
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(
                    "The class's constructor that accept 0 arguments must be accessible by this class, but not : "
                            + componentClass.getCanonicalName()
            );
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(
                    "The class must be able to be instantiated, but not : "
                            + componentClass.getCanonicalName()
            );
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(
                    "The class's constructor that accept 0 arguments must really can accept 0 arguments, but not : "
                            + componentClass.getCanonicalName()
            );
        }
        return array;
    }

    /**
     * Flip the atomicBoolean.
     *
     * @param atomicBoolean atomicBoolean
     * @return new boolean value of atomicBoolean
     * @see
     * <a href="https://stackoverflow.com/questions/1255617/does-atomicboolean-not-have-a-negate-method">question about this</a>
     */
    public static boolean flip(AtomicBoolean atomicBoolean) {
        boolean currentBooleanValue;
        do {
            currentBooleanValue = atomicBoolean.get();
        } while (!atomicBoolean.compareAndSet(currentBooleanValue, !currentBooleanValue));
        return !currentBooleanValue;
    }

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
