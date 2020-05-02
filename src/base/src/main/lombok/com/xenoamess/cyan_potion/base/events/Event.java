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

package com.xenoamess.cyan_potion.base.events;

import com.xenoamess.cyan_potion.base.GameManager;

import java.util.Set;
import java.util.function.Function;

/**
 * Event is an Event that notify GameManager of what happened.
 * <p>
 * Notice that events are not solved strictly follow time.
 * <p>
 * But we make sure that event appear in a frame will always be solved
 * (for the first time) before next frame start.
 *
 * @author XenoAmess
 * @version 0.162.3-SNAPSHOT
 * @see com.xenoamess.cyan_potion.base.GameManager#eventListAdd(Event)
 * @see com.xenoamess.cyan_potion.base.GameManager#solveEvents()
 * @see com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTree#process(Event)
 * @see com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent#process(Event)
 */
@FunctionalInterface
public interface Event extends Function<GameManager, Set<Event>> {
    /**
     * use this instead of null for safety.
     *
     * @see EmptyEvent
     */
    @SuppressWarnings("unused")
    Event EMPTY = new EmptyEvent() {
        //do nothing
    };

    /**
     * {@inheritDoc}
     * <p>
     * apply means this Event invoke itself onto your GameManager.
     * Usually we will just put it onto your {@link GameManager#getGameWindowComponentTree()},
     * And let the tree pass it to all
     * {@link com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent}, and let them
     * process it.
     * of course you can do something before you put it to tree, or just do never put it to tree.
     * for example see {@link com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent#apply(GameManager)}
     * <p>
     * after the process is over, we return a {@link Set} of Event as a result.
     * that means this event generate a set of new events while processing.
     * the Set can be empty, but shall never be null.
     */
    @Override
    Set<Event> apply(GameManager gameManager);
}
