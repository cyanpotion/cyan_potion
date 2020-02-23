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

package com.xenoamess.cyan_potion.base.io.input.keyboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.events.EmptyEvent;
import com.xenoamess.cyan_potion.base.events.Event;
import lombok.Data;
import net.jcip.annotations.GuardedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Char Event.
 * Event to deal with input characters in glfw.
 * Notice that in most cases your GameWindowComponent does not need to handle this type of Event directly
 * (although you still can.)
 * I have made a wrapper Event class named TextEvent. You shall handle that Event.
 * That will always make things easier for both of us.
 *
 * @author XenoAmess
 * @version 0.161.1
 */
@Data
public class CharEvent implements Event {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(CharEvent.class);

    private static class EmptyCharEvent extends CharEvent implements EmptyEvent {
        public EmptyCharEvent() {
            super(0, 0);
        }

        @Override
        public Set<Event> apply(GameManager gameManager) {
            return null;
        }
    }

    private static final AtomicLong currentId = new AtomicLong(0L);

    /**
     * use this instead of null for safety.
     *
     * @see EmptyEvent
     */
    public static final CharEvent EMPTY = new CharEvent.EmptyCharEvent();

    private final long window;
    private final int codepoint;

    /**
     * id of this CharEvent.
     * id of each CharEvent shall be unique, so can be used to distinct CharEvent
     */
    private final long id;

    /**
     * <p>Constructor for CharEvent.</p>
     *
     * @param window    a long.
     * @param codepoint a int.
     */
    public CharEvent(long window, int codepoint) {
        super();
        this.window = window;
        this.codepoint = codepoint;
        synchronized (currentId) {
            this.id = currentId.getAndAdd(1L);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GuardedBy("gameManager")
    public Set<Event> apply(GameManager gameManager) {
        if (gameManager.getDataCenter().getGameSettings().isDebug()) {
            LOGGER.debug("{}", this);
        }
        return gameManager.getGameWindowComponentTree().process(this);
    }
}
