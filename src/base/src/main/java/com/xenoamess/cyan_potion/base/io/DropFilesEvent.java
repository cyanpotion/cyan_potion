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
import lombok.Data;
import org.lwjgl.PointerBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.lwjgl.system.MemoryUtil.memByteBufferNT1;
import static org.lwjgl.system.MemoryUtil.memPointerBuffer;
import static org.lwjgl.system.MemoryUtil.memUTF8;

/**
 * <p>KeyboardEvent class.</p>
 *
 * @author XenoAmess
 * @version 0.162.3
 */
@Data
public class DropFilesEvent implements Event {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(DropFilesEvent.class);

    private final long window;
    private final int count;
    private final long names;

    private static class EmptyDropFilesEvent extends DropFilesEvent implements EmptyEvent {
        EmptyDropFilesEvent() {
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

    @SuppressWarnings("unused")
    public List<String> getFileNames() {
        ArrayList<String> fileNames = new ArrayList<>();
        PointerBuffer nameBuffer = memPointerBuffer(getNames(), getCount());
        for (int i = 0; i < getCount(); i++) {
            fileNames.add(memUTF8(memByteBufferNT1(nameBuffer.get(i))));
        }
        return fileNames;
    }
}
