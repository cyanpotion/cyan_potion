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
import com.xenoamess.cyan_potion.base.events.EmptyEvent;
import com.xenoamess.cyan_potion.base.events.Event;
import net.jcip.annotations.GuardedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * <p>MouseScrollEvent class.</p>
 *
 * @author XenoAmess
 * @version 0.158.0
 */
public class MouseScrollEvent implements Event {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(MouseScrollEvent.class);

    private static class EmptyMouseScrollEvent extends MouseScrollEvent implements EmptyEvent {
        public EmptyMouseScrollEvent() {
            super(0, 0, 0);
        }

        @Override
        public Set<Event> apply(GameManager gameManager) {
            return null;
        }
    }

    /**
     * use this instead of null for safety.
     *
     * @see EmptyEvent
     */
    public static final MouseScrollEvent EMPTY = new EmptyMouseScrollEvent();

    private final long window;
    private final double xoffset;
    private final double yoffset;

    /**
     * <p>Constructor for MouseScrollEvent.</p>
     *
     * @param window  a long.
     * @param xoffset a double.
     * @param yoffset a double.
     */
    public MouseScrollEvent(long window, double xoffset, double yoffset) {
        super();
        this.window = window;
        this.xoffset = xoffset;
        this.yoffset = yoffset;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GuardedBy("gameManager")
    public Set<Event> apply(GameManager gameManager) {
        LOGGER.debug("MouseScrollEvent : {} {} {}",
                getWindow(), getXoffset(), getYoffset());
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
     * <p>Getter for the field <code>xoffset</code>.</p>
     *
     * @return a double.
     */
    public double getXoffset() {
        return xoffset;
    }

    /**
     * <p>Getter for the field <code>yoffset</code>.</p>
     *
     * @return a double.
     */
    public double getYoffset() {
        return yoffset;
    }
}
