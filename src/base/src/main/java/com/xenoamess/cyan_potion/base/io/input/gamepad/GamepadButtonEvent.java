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

package com.xenoamess.cyan_potion.base.io.input.gamepad;

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.io.input.key.Key;
import net.jcip.annotations.GuardedBy;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * <p>GamepadButtonEvent class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class GamepadButtonEvent implements Event {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(GamepadButtonEvent.class);

    private final long window;
    private final int key;
    /**
     * action of the GamepadButtonEvent
     * The action is one of
     * {@link org.lwjgl.glfw.GLFW#GLFW_PRESS},
     * {@link org.lwjgl.glfw.GLFW#GLFW_REPEAT},
     * {@link org.lwjgl.glfw.GLFW#GLFW_RELEASE}
     * <p>
     * Notice that this is not included in original GLFW.
     * I just use JXInput to deal with it, and I manage to force it to follow
     * such rules.
     *
     * @see com.xenoamess.cyan_potion.base.io.input.key.Keymap
     * @see org.lwjgl.glfw.GLFW
     * @see <a href="https://www.glfw.org/docs/latest/input_guide.html#input_key">GLFW documents</a>
     */
    private final int action;
    private final AbstractGamepadDevice gamepadDevice;

    /**
     * <p>Constructor for GamepadButtonEvent.</p>
     *
     * @param window        a long.
     * @param key           a int.
     * @param action        a int.
     * @param gamepadDevice a {@link com.xenoamess.cyan_potion.base.io.input.gamepad.AbstractGamepadDevice} object.
     */
    public GamepadButtonEvent(long window, int key, int action,
                              AbstractGamepadDevice gamepadDevice) {
        super();
        this.window = window;
        this.key = key;
        this.action = action;
        this.gamepadDevice = gamepadDevice;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GuardedBy("gameManager.keyMap")
    public Set<Event> apply(GameManager gameManager) {
        if (getAction() != GLFW.GLFW_REPEAT) {
            LOGGER.debug("GamepadButtonEvent : {} {} {}", getKey(),
                    getAction(), getGamepadDevice());
        }
        switch (getAction()) {
            case GLFW.GLFW_RELEASE:
            case GLFW.GLFW_PRESS:
                gameManager.getKeymap().keyFlipRaw(new Key(Key.TYPE_GAMEPAD, getKey()));
                break;
            case GLFW.GLFW_REPEAT:
                break;
            default:
        }
        return gameManager.getGameWindowComponentTree().process(this);
    }

    /**
     * <p>Getter for the field <code>window</code>.</p>
     *
     * @return a long.
     */
    public long getWindow() {
        return window;
    }

    /**
     * <p>Getter for the field <code>key</code>.</p>
     *
     * @return a int.
     */
    public int getKey() {
        return key;
    }

    /**
     * <p>Getter for the field <code>action</code>.</p>
     *
     * @return action of the GamepadButtonEvent
     * The action is one of
     * {@link org.lwjgl.glfw.GLFW#GLFW_PRESS},
     * {@link org.lwjgl.glfw.GLFW#GLFW_REPEAT},
     * {@link org.lwjgl.glfw.GLFW#GLFW_RELEASE}
     * <p>
     * Notice that this is not included in original GLFW.
     * I just use JXInput to deal with it, and I manage to force it to follow
     * such rules.
     * @see com.xenoamess.cyan_potion.base.io.input.key.Keymap
     * @see GLFW
     * @see <a href="https://www.glfw.org/docs/latest/input_guide.html#input_key">GLFW documents</a>
     */
    public int getAction() {
        return action;
    }

    /**
     * <p>Getter for the field <code>gamepadDevice</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.io.input.gamepad.AbstractGamepadDevice} object.
     */
    public AbstractGamepadDevice getGamepadDevice() {
        return gamepadDevice;
    }
}