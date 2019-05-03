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

package com.xenoamess.cyan_potion.base.events;

import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.io.input.key.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author XenoAmess
 */
public class KeyEvent implements Event {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(KeyEvent.class);

    private final long window;
    private final int key;
    private final int scancode;
    /**
     * action of the KeyEvent
     * The action is one of
     * {@link org.lwjgl.glfw.GLFW#GLFW_PRESS},
     * {@link org.lwjgl.glfw.GLFW#GLFW_REPEAT},
     * {@link org.lwjgl.glfw.GLFW#GLFW_RELEASE}
     *
     * @see org.lwjgl.glfw.GLFW
     * @see <a href="https://www.glfw.org/docs/latest/input_guide.html#input_key">GLFW documents</a>
     */
    private final int action;
    /**
     * mods of the KeyEvent.
     * notice that this shall be checked for the bit you use sometimes, and
     * not always the whole value.
     * <p>
     * #define 	GLFW_MOD_SHIFT   0x0001
     * If this bit is set one or more Shift keys were held down.
     * <p>
     * #define 	GLFW_MOD_CONTROL   0x0002
     * If this bit is set one or more Control keys were held down.
     * <p>
     * #define 	GLFW_MOD_ALT   0x0004
     * If this bit is set one or more Alt keys were held down.
     * <p>
     * #define 	GLFW_MOD_SUPER   0x0008
     * If this bit is set one or more Super keys were held down.
     * <p>
     * #define 	GLFW_MOD_CAPS_LOCK   0x0010
     * If this bit is set the Caps Lock key is enabled.
     * <p>
     * #define 	GLFW_MOD_NUM_LOCK   0x0020
     * If this bit is set the Num Lock key is enabled.
     *
     * @see org.lwjgl.glfw.GLFW
     * @see <a href="http://www.glfw.org/docs/latest/group__mods.html">GLFW documents</a>
     */
    private final int mods;

    public KeyEvent(long window, int key, int scancode, int action, int mods) {
        super();
        this.window = window;
        this.key = key;
        this.scancode = scancode;
        this.action = action;
        this.mods = mods;
    }

    @Override
    public Set<Event> apply(Object object) {
        LOGGER.debug("KeyEvent : {} {} {} {}", getKey(), getScancode(),
                getAction(), getMods());
        //        GameManager gameManager = DataCenter.currentGameManager;
        GameWindow gameWindow = DataCenter.getGameWindow(getWindow());
        switch (getAction()) {
            case GLFW_RELEASE:
                gameWindow.getGameManager().getKeymap().keyReleaseRaw(new Key(Key.TYPE_KEY, getKey()));
                break;
            case GLFW_PRESS:
                gameWindow.getGameManager().getKeymap().keyPressRaw(new Key(Key.TYPE_KEY, getKey()));
                break;
            case GLFW_REPEAT:
                break;
            default:

        }
        return gameWindow.getGameManager().getGameWindowComponentTree().process(this);
    }

    public Key getKeyRaw() {
        return new Key(Key.TYPE_KEY, this.getKey());
    }

    public Key getKeyTranslated() {
//        return new Key(Key.TYPE_KEY, this.key);
        GameWindow gameWindow = DataCenter.getGameWindow(getWindow());
        return gameWindow.getGameManager().getKeymap().get(this.getKeyRaw());
    }

    public boolean checkMods(final int glfwModArgument) {
        return (this.getMods() & glfwModArgument) != 0;
    }

    /**
     * This function is deprecated.
     * you shall just use CharEvent.
     * <p>
     * This function is a product of my lack of knowledge about GLFW.
     * If you need to catch a text, please just use text input in GLFW, not
     * key input.
     *
     * @return the string that the key refers to
     * @see
     * <a href="http://www.glfw.org/docs/latest/group__input.html#ga556239421c6a5a243c66fca28da9f742">GLFW documents</a>
     */
    @Deprecated
    public String translate() {
        switch (this.getKeyRaw().getKey()) {
            case GLFW_KEY_KP_0:
                return "0";
            case GLFW_KEY_KP_1:
                return "1";
            case GLFW_KEY_KP_2:
                return "2";
            case GLFW_KEY_KP_3:
                return "3";
            case GLFW_KEY_KP_4:
                return "4";
            case GLFW_KEY_KP_5:
                return "5";
            case GLFW_KEY_KP_6:
                return "6";
            case GLFW_KEY_KP_7:
                return "7";
            case GLFW_KEY_KP_8:
                return "8";
            case GLFW_KEY_KP_9:
                return "9";
            case GLFW_KEY_SPACE:
                return " ";
            case GLFW_KEY_ENTER:
                return "\n";
            case GLFW_KEY_TAB:
                return "  ";
            default:
                break;
        }

        if (this.checkMods(GLFW_MOD_SHIFT) || this.checkMods(GLFW_MOD_CAPS_LOCK)) {
            switch (this.getKeyRaw().getKey()) {
                case GLFW_KEY_APOSTROPHE:
                    return "\"";
                case GLFW_KEY_COMMA:
                    return "<";
                case GLFW_KEY_MINUS:
                    return "_";
                case GLFW_KEY_PERIOD:
                    return ">";
                case GLFW_KEY_SLASH:
                    return "?";
                case GLFW_KEY_0:
                    return ")";
                case GLFW_KEY_1:
                    return "!";
                case GLFW_KEY_2:
                    return "@";
                case GLFW_KEY_3:
                    return "#";
                case GLFW_KEY_4:
                    return "$";
                case GLFW_KEY_5:
                    return "%";
                case GLFW_KEY_6:
                    return "^";
                case GLFW_KEY_7:
                    return "&";
                case GLFW_KEY_8:
                    return "*";
                case GLFW_KEY_9:
                    return "(";
                case GLFW_KEY_SEMICOLON:
                    return ":";
                case GLFW_KEY_EQUAL:
                    return "+";
                case GLFW_KEY_LEFT_BRACKET:
                    return "{";
                case GLFW_KEY_BACKSLASH:
                    return "|";
                case GLFW_KEY_RIGHT_BRACKET:
                    return "}";
                case GLFW_KEY_GRAVE_ACCENT:
                    return "~";
                case GLFW_KEY_A:
                    return "A";
                case GLFW_KEY_B:
                    return "B";
                case GLFW_KEY_C:
                    return "C";
                case GLFW_KEY_D:
                    return "D";
                case GLFW_KEY_E:
                    return "E";
                case GLFW_KEY_F:
                    return "F";
                case GLFW_KEY_G:
                    return "G";
                case GLFW_KEY_H:
                    return "H";
                case GLFW_KEY_I:
                    return "I";
                case GLFW_KEY_J:
                    return "J";
                case GLFW_KEY_K:
                    return "K";
                case GLFW_KEY_L:
                    return "L";
                case GLFW_KEY_M:
                    return "M";
                case GLFW_KEY_N:
                    return "N";
                case GLFW_KEY_O:
                    return "O";
                case GLFW_KEY_P:
                    return "P";
                case GLFW_KEY_Q:
                    return "Q";
                case GLFW_KEY_R:
                    return "R";
                case GLFW_KEY_S:
                    return "S";
                case GLFW_KEY_T:
                    return "T";
                case GLFW_KEY_U:
                    return "U";
                case GLFW_KEY_V:
                    return "V";
                case GLFW_KEY_W:
                    return "W";
                case GLFW_KEY_X:
                    return "X";
                case GLFW_KEY_Y:
                    return "Y";
                case GLFW_KEY_Z:
                    return "Z";
                default:
                    return "";
            }
        } else {
            switch (this.getKeyRaw().getKey()) {
                case GLFW_KEY_APOSTROPHE:
                    return "'";
                case GLFW_KEY_COMMA:
                    return ",";
                case GLFW_KEY_MINUS:
                    return "-";
                case GLFW_KEY_PERIOD:
                    return ".";
                case GLFW_KEY_SLASH:
                    return "/";
                case GLFW_KEY_0:
                    return "0";
                case GLFW_KEY_1:
                    return "1";
                case GLFW_KEY_2:
                    return "2";
                case GLFW_KEY_3:
                    return "3";
                case GLFW_KEY_4:
                    return "4";
                case GLFW_KEY_5:
                    return "5";
                case GLFW_KEY_6:
                    return "6";
                case GLFW_KEY_7:
                    return "7";
                case GLFW_KEY_8:
                    return "8";
                case GLFW_KEY_9:
                    return "9";
                case GLFW_KEY_SEMICOLON:
                    return ";";
                case GLFW_KEY_EQUAL:
                    return "=";
                case GLFW_KEY_LEFT_BRACKET:
                    return "[";
                case GLFW_KEY_BACKSLASH:
                    return "\\";
                case GLFW_KEY_RIGHT_BRACKET:
                    return "]";
                case GLFW_KEY_GRAVE_ACCENT:
                    return "`";
                case GLFW_KEY_A:
                    return "a";
                case GLFW_KEY_B:
                    return "b";
                case GLFW_KEY_C:
                    return "c";
                case GLFW_KEY_D:
                    return "d";
                case GLFW_KEY_E:
                    return "e";
                case GLFW_KEY_F:
                    return "f";
                case GLFW_KEY_G:
                    return "g";
                case GLFW_KEY_H:
                    return "h";
                case GLFW_KEY_I:
                    return "i";
                case GLFW_KEY_J:
                    return "j";
                case GLFW_KEY_K:
                    return "k";
                case GLFW_KEY_L:
                    return "l";
                case GLFW_KEY_M:
                    return "m";
                case GLFW_KEY_N:
                    return "n";
                case GLFW_KEY_O:
                    return "o";
                case GLFW_KEY_P:
                    return "p";
                case GLFW_KEY_Q:
                    return "q";
                case GLFW_KEY_R:
                    return "r";
                case GLFW_KEY_S:
                    return "s";
                case GLFW_KEY_T:
                    return "t";
                case GLFW_KEY_U:
                    return "u";
                case GLFW_KEY_V:
                    return "v";
                case GLFW_KEY_W:
                    return "w";
                case GLFW_KEY_X:
                    return "x";
                case GLFW_KEY_Y:
                    return "y";
                case GLFW_KEY_Z:
                    return "z";
                default:
                    return "";
            }
        }
    }

    public long getWindow() {
        return window;
    }

    public int getKey() {
        return key;
    }

    public int getScancode() {
        return scancode;
    }

    /**
     * @return action of the KeyEvent
     * The action is one of
     * {@link org.lwjgl.glfw.GLFW#GLFW_PRESS},
     * {@link org.lwjgl.glfw.GLFW#GLFW_REPEAT},
     * {@link org.lwjgl.glfw.GLFW#GLFW_RELEASE}
     * @see org.lwjgl.glfw.GLFW
     * @see <a href="https://www.glfw.org/docs/latest/input_guide.html#input_key">GLFW documents</a>
     */
    public int getAction() {
        return action;
    }

    /**
     * @return mods of the KeyEvent.
     * notice that this shall be checked for the bit you use sometimes, and
     * not always the whole value.
     * <p>
     * #define 	GLFW_MOD_SHIFT   0x0001
     * If this bit is set one or more Shift keys were held down.
     * <p>
     * #define 	GLFW_MOD_CONTROL   0x0002
     * If this bit is set one or more Control keys were held down.
     * <p>
     * #define 	GLFW_MOD_ALT   0x0004
     * If this bit is set one or more Alt keys were held down.
     * <p>
     * #define 	GLFW_MOD_SUPER   0x0008
     * If this bit is set one or more Super keys were held down.
     * <p>
     * #define 	GLFW_MOD_CAPS_LOCK   0x0010
     * If this bit is set the Caps Lock key is enabled.
     * <p>
     * #define 	GLFW_MOD_NUM_LOCK   0x0020
     * If this bit is set the Num Lock key is enabled.
     * @see org.lwjgl.glfw.GLFW
     * @see <a href="http://www.glfw.org/docs/latest/group__mods.html">GLFW documents</a>
     */
    public int getMods() {
        return mods;
    }
}
