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

package com.xenoamess.cyan_potion.base.io;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.events.EmptyEvent;
import com.xenoamess.cyan_potion.base.events.Event;
import org.lwjgl.PointerBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.lwjgl.system.MemoryUtil.*;

/**
 * <p>KeyboardEvent class.</p>
 *
 * @author XenoAmess
 * @version 0.156.0
 */
public class DropFilesEvent implements Event {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(DropFilesEvent.class);

    private final long window;
    private final int count;
    private final long names;

    private static class EmptyDropFilesEvent extends DropFilesEvent implements EmptyEvent {
        public EmptyDropFilesEvent() {
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
    public static final DropFilesEvent EMPTY = new DropFilesEvent.EmptyDropFilesEvent();

    /**
     * <p>Constructor for DropFilesEvent.</p>
     *
     * @param window a long.
     * @param count  a int.
     * @param names  a int.
     */
    public DropFilesEvent(long window, int count, long names) {
        super();
        this.window = window;
        this.count = count;
        this.names = names;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Event> apply(GameManager gameManager) {
        if (gameManager.getDataCenter().getGameSettings().isDebug()) {
            LOGGER.debug("{}", this);
        }
        return gameManager.getGameWindowComponentTree().process(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        //notice that scancode is ignored by this engine(at this version.)
        //because we want to make it multi-platform.

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DropFilesEvent:{");
        stringBuilder.append("count:");
        stringBuilder.append(this.getCount());
        stringBuilder.append(",names:");
        stringBuilder.append(this.getNames());
        stringBuilder.append(",names:");
        PointerBuffer nameBuffer = memPointerBuffer(getNames(), getCount());
        stringBuilder.append('[');
        for (int i = 0; i < getCount(); i++) {
            stringBuilder.append(i);
            stringBuilder.append(":");
            stringBuilder.append(memUTF8(memByteBufferNT1(nameBuffer.get(i))));
            stringBuilder.append(',');
        }
        stringBuilder.append(']');
        stringBuilder.append("}");
        return stringBuilder.toString();
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
     * <p>Getter for the field <code>count</code>.</p>
     *
     * @return a int.
     */
    public int getCount() {
        return count;
    }

    /**
     * <p>Getter for the field <code>names</code>.</p>
     *
     * @return a long.
     */
    public long getNames() {
        return names;
    }
}
