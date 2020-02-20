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
import net.jcip.annotations.GuardedBy;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Text Event
 *
 * @author XenoAmess
 * @version 0.144.4
 */
public class TextEvent implements Event {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(TextEvent.class);

    private static class EmptyTextEvent extends TextEvent implements EmptyEvent {
        public EmptyTextEvent() {
            super(0, StringUtils.EMPTY);
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
    public static final TextEvent EMPTY = new EmptyTextEvent();

    private final long window;
    private final String contentString;

    /**
     * <p>Constructor for CharEvent.</p>
     *
     * @param window        window.
     * @param contentString contentString.
     */
    public TextEvent(long window, String contentString) {
        super();
        this.window = window;
        this.contentString = contentString;
    }

    /**
     * <p>Constructor for CharEvent.</p>
     *
     * @param window     window.
     * @param charEvents charEvents.
     */
    public TextEvent(long window, List<CharEvent> charEvents) {
        this(window, getStringFromCharEvents(charEvents));
    }

    /**
     * get String From a list of CharEvents.
     * will reorder CharEvents according to id (which means generate order)
     * then join them and return.
     *
     * @param charEvents a {@link java.util.List} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getStringFromCharEvents(List<CharEvent> charEvents) {
        StringBuilder stringBuilder = new StringBuilder();
        for (CharEvent event : charEvents) {
            stringBuilder.append((char) event.getCodepoint());
        }
        return stringBuilder.toString();
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

    /**
     * <p>Getter for the field <code>window</code>.</p>
     *
     * @return a long.
     */
    public long getWindow() {
        return window;
    }

    /**
     * <p>Getter for the field <code>contentString</code>.</p>
     *
     * @return a String.
     */
    public String getContentString() {
        return contentString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("TextEvent:{codepoint:");
        stringBuilder.append(this.getContentString());
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
