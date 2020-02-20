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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * EventsEvent.
 * EventsEvent is a Event which contains set of Events.
 * that is done
 *
 * @author XenoAmess
 * @version 0.160.0-SNAPSHOT
 */
public class EventsEvent implements Event {
    private final Set<Event> events;

    /**
     * <p>Constructor for EventsEvent.</p>
     */
    public EventsEvent() {
        this.events = new HashSet<>();
    }

    /**
     * <p>Constructor for EventsEvent.</p>
     *
     * @param events a {@link java.util.Set} object.
     */
    public EventsEvent(Set<Event> events) {
        this.events = new HashSet<>(events);
    }

    /**
     * <p>addEvent.</p>
     *
     * @param event a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
    public void addEvent(Event event) {
        this.events.add(event);
    }

    /**
     * <p>addEvents.</p>
     *
     * @param events a {@link java.util.Collection} object.
     */
    public void addEvents(Collection<Event> events) {
        this.events.addAll(events);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Event> apply(GameManager gameManager) {
        return events;
    }

    /**
     * <p>Getter for the field <code>events</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Event> getEvents() {
        return new HashSet<>(this.events);
    }
}
