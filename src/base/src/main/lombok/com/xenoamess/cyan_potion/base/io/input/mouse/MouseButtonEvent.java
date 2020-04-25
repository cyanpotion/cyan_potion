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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.areas.AbstractPoint;
import com.xenoamess.cyan_potion.base.areas.SimpleImmutablePoint;
import com.xenoamess.cyan_potion.base.events.EmptyEvent;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.io.input.key.Key;
import com.xenoamess.cyan_potion.base.io.input.key.KeyModEnum;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import lombok.Data;
import net.jcip.annotations.GuardedBy;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;

/**
 * <p>MouseButtonEvent class.</p>
 *
 * @author XenoAmess
 * @version 0.162.1
 */
@Data
public class MouseButtonEvent implements Event {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(MouseButtonEvent.class);

    private static class EmptyMouseButtonEvent extends MouseButtonEvent implements EmptyEvent {
        public EmptyMouseButtonEvent(float mousePosX, float mousePosY) {
            super(0, 0, 0, 0, mousePosX, mousePosY);
        }

        @Override
        public Set<Event> apply(GameManager gameManager) {
            return null;
        }
    }

    /**
     * use this instead of null for safety.
     * notice that due to the need of mousePosX / Y, you have to generate the empty event from your posX / Y.
     *
     * @param mousePosX mousePosX
     * @param mousePosY mousePosX
     * @return a {@link com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent} object.
     * @see EmptyEvent
     */
    public static MouseButtonEvent generateEmptyMouseButtonEvent(float mousePosX, float mousePosY) {
        return new EmptyMouseButtonEvent(mousePosX, mousePosY);
    }

    /**
     * use this instead of null for safety.
     * notice that due to the need of mousePosX / Y, you have to generate the empty event from your posX / Y.
     *
     * @param gameWindow a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     * @return a {@link com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent} object.
     * @see EmptyEvent
     */
    public static MouseButtonEvent generateEmptyMouseButtonEvent(GameWindow gameWindow) {
        return new EmptyMouseButtonEvent(gameWindow.getMousePosX(), gameWindow.getMousePosY());
    }

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

    private final float mousePosX;

    private final float mousePosY;

    /**
     * <p>Constructor for MouseButtonEvent.</p>
     *
     * @param window    window
     * @param button    button
     * @param action    action
     * @param mods      mods
     * @param mousePosX mousePosX
     * @param mousePosY mousePosY
     */
    public MouseButtonEvent(long window, int button, int action, int mods, float mousePosX, float mousePosY) {
        super();
        this.window = window;
        this.key = button;
        this.action = action;
        this.mods = mods;
        this.mousePosX = mousePosX;
        this.mousePosY = mousePosY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GuardedBy("gameManager.keyMap")
    public Set<Event> apply(GameManager gameManager) {
        if (gameManager.getDataCenter().getGameSettings().isDebug()) {
            LOGGER.debug("{}", this);
        }
        switch (getAction()) {
            case GLFW.GLFW_RELEASE:
            case GLFW.GLFW_PRESS:
                gameManager.getKeymap().keyFlipRaw(new Key(Key.TYPE_MOUSE, getKey()));
                break;
            default:
        }
        return gameManager.getGameWindowComponentTree().process(this);
    }

    /**
     * <p>getKeyRaw.</p>
     *
     * @return return
     */
    public Key getKeyRaw() {
        return new Key(Key.TYPE_MOUSE, this.getKey());
    }

    /**
     * <p>getKeyTranslated.</p>
     *
     * @param keymap keymap
     * @return return
     */
    public Key getKeyTranslated(Keymap keymap) {
        return keymap.get(this.getKeyRaw());
    }

    /**
     * <p>getMousePoint.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.areas.AbstractPoint} object.
     */
    public AbstractPoint getMousePoint() {
        return new SimpleImmutablePoint(this.getMousePosX(), this.getMousePosY());
    }

    /**
     * <p>getModEnums.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<KeyModEnum> getModEnums() {
        return KeyModEnum.getModEnumsByValue(this.getMods());
    }
}
