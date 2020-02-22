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

package com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components;

import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * MainThreadEventProcessor class is not a processor class for MainThreadEvent,
 * but a processor to make sure a normal(non-mainthread-only) event be handled in main thread.
 *
 * @author XenoAmess
 * @version 0.161.0-SNAPSHOT
 */

@EqualsAndHashCode
@ToString
public class MainThreadEventProcessor<T extends Event> implements EventProcessor<T> {
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    private final GameManager gameManager;
    @Getter
    @Setter
    private EventProcessor<? super T> processor;

    /**
     * <p>Constructor for MainThreadEventProcessor.</p>
     *
     * @param gameManager gameManager
     * @param processor   processor
     */
    public MainThreadEventProcessor(GameManager gameManager, EventProcessor<? super T> processor) {
        this.gameManager = gameManager;
        this.setProcessor(processor);
    }

    /**
     * <p>Constructor for MainThreadEventProcessor.</p>
     *
     * @param gameWindowComponent gameWindowComponent
     * @param processor           processor
     */
    public MainThreadEventProcessor(AbstractGameWindowComponent gameWindowComponent, EventProcessor<? super T> processor) {
        this(gameWindowComponent.getGameWindow().getGameManager(), processor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Event apply(T event) {
        if (!DataCenter.ifMainThread()) {
            this.getGameManager().delayMainThreadEventProcess(this, event);
            return null;
        }
        return this.getProcessor().apply(event);
    }
}
