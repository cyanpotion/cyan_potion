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

package com.xenoamess.cyan_potion.base.io.input.mouse;

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.io.input.key.Key;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import net.jcip.annotations.GuardedBy;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author XenoAmess
 */
public class MouseButtonEvent implements Event {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MouseButtonEvent.class);

    private final long window;
    private final int key;
    /**
     * action of the MouseButtonEvent
     * The action is one of
     * {@link org.lwjgl.glfw.GLFW#GLFW_PRESS},
     * {@link org.lwjgl.glfw.GLFW#GLFW_RELEASE}
     * <p>
     * notice that mouseButtonEvent's action can NEVER be
     * {@link org.lwjgl.glfw.GLFW#GLFW_REPEAT},
     *
     * @see org.lwjgl.glfw.GLFW
     * @see <a href="https://www.glfw.org/docs/latest/input_guide.html#input_mouse_button">GLFW documents</a>
     */
    private final int action;
    /**
     * mods of the KeyboardEvent.
     * notice that this shall be checked for the bit you use, and not the
     * whole value.
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

    public MouseButtonEvent(long window, int button, int action, int mods) {
        super();
        this.window = window;
        this.key = button;
        this.action = action;
        this.mods = mods;
    }

    @Override
    @GuardedBy("gameManager.keyMap")
    public Set<Event> apply(GameManager gameManager) {
        LOGGER.debug("MouseButtonEvent : {} {} {}", getKey(), getAction(),
                getMods());
        switch (getAction()) {
            case GLFW.GLFW_RELEASE:
                gameManager.getKeymap().keyFlipRaw(new Key(Key.TYPE_MOUSE, getKey()));
                break;
            case GLFW.GLFW_PRESS:
                gameManager.getKeymap().keyFlipRaw(new Key(Key.TYPE_MOUSE, getKey()));
                break;
            default:
        }
        return gameManager.getGameWindowComponentTree().process(this);
    }

    public Key getKeyRaw() {
        return new Key(Key.TYPE_MOUSE, this.getKey());
    }

    public Key getKeyTranslated(Keymap keymap) {
        return keymap.get(this.getKeyRaw());
    }

    public long getWindow() {
        return window;
    }

    public int getKey() {
        return key;
    }

    /**
     * @return action of the MouseButtonEvent
     * The action is one of
     * {@link GLFW#GLFW_PRESS},
     * {@link GLFW#GLFW_RELEASE}
     * <p>
     * notice that mouseButtonEvent's action can NEVER be
     * {@link GLFW#GLFW_REPEAT},
     * @see GLFW
     * @see <a href="https://www.glfw.org/docs/latest/input_guide.html#input_mouse_button">GLFW documents</a>
     */
    public int getAction() {
        return action;
    }

    /**
     * @return mods of the KeyboardEvent.
     * notice that this shall be checked for the bit you use, and not the
     * whole value.
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
     * @see GLFW
     * @see <a href="http://www.glfw.org/docs/latest/group__mods.html">GLFW documents</a>
     */
    public int getMods() {
        return mods;
    }
}