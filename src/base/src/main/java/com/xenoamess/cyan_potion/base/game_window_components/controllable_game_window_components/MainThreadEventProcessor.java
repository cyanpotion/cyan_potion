/*
 * MIT License
 *
 * Copyright (c) 2019 XenoAmess
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

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent;

/**
 * <p>MainThreadEventProcessor class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class MainThreadEventProcessor implements EventProcessor {
    public GameManager gameManager;
    public EventProcessor processor;

    /**
     * <p>Constructor for MainThreadEventProcessor.</p>
     *
     * @param gameManager gameManager
     * @param processor   a
     *                    <p>
     *                    <p>
     *                    <p>
     *                    <p>
     *                    <p>
     *                    <p>
     *                    <p>
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor} object.
     */
    public MainThreadEventProcessor(GameManager gameManager, EventProcessor processor) {
        this.gameManager = gameManager;
        this.processor = processor;
    }

    /**
     * <p>Constructor for MainThreadEventProcessor.</p>
     *
     * @param gameWindowComponent a
     *                            <p>
     *                            <p>
     *
     *
     *
     *                         {@link com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent}
     *                            object.
     * @param processor           a
     *                            <p>
     *                            <p>
     *                            <p>
     *                            <p>
     *                            <p>
     *                            <p>
     *                            <p>
     *                            <p>
     *                            <p>
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor} object.
     */
    public MainThreadEventProcessor(AbstractGameWindowComponent gameWindowComponent, EventProcessor processor) {
        this.gameManager = gameWindowComponent.getGameWindow().getGameManager();
        this.processor = processor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Event apply(Event event) {
        if (Thread.currentThread().getId() != 1) {
            this.gameManager.delayMainThreadEventProcess(this, event);
            return null;
        }
        return this.processor.apply(event);
    }
}
