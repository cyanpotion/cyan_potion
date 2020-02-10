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
import com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent;

import java.util.Set;
import java.util.function.Function;

/**
 * RemoteCallEvent
 * RemoteCallEvent is a type of Event that designed "delay" a function to a gameWindowComponent.
 * When this function be solved, it invoke field function on field gameWindowComponent.
 * You can see how it works exactly in AbstractGameWindowComponent.initRemoteCallEventProcessor()
 *
 * @author XenoAmess
 * @version 0.156.1-SNAPSHOT
 */
public class RemoteCallEvent<T extends AbstractGameWindowComponent> implements Event {
    /**
     * the AbstractGameWindowComponent that will be invoked with this function.
     */
    private final T gameWindowComponent;
    /**
     * the function to invoke.
     */
    private final Function<T, Event> function;

    /**
     * <p>Constructor for RemoteCallEvent.</p>
     *
     * @param gameWindowComponent a T object.
     * @param function            a {@link java.util.function.Function} object.
     */
    public RemoteCallEvent(T gameWindowComponent, Function<T, Event> function) {
        this.gameWindowComponent = gameWindowComponent;
        this.function = function;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Event> apply(GameManager gameManager) {
        return gameManager.getGameWindowComponentTree().process(this);
    }

    /**
     * <p>Getter for the field <code>gameWindowComponent</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent} object.
     */
    public AbstractGameWindowComponent getGameWindowComponent() {
        return gameWindowComponent;
    }

    /**
     * <p>Getter for the field <code>function</code>.</p>
     *
     * @return a {@link java.util.function.Function} object.
     */
    public Function<T, Event> getFunction() {
        return function;
    }
}
