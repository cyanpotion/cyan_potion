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

package com.xenoamess.cyan_potion.base.game_window_components;

/**
 * <p>Abstract Updater class.</p>
 *
 * @author XenoAmess
 * @version 0.160.0-SNAPSHOT
 */
public abstract class Updater<T> implements UpdaterInterface<T> {
    private final UpdaterInterface<? super T> parentUpdater;

    /**
     * <p>Constructor for Updater.</p>
     */
    public Updater() {
        this(null);
    }

    /**
     * <p>Constructor for Updater.</p>
     *
     * @param parentUpdater a {@link com.xenoamess.cyan_potion.base.game_window_components.UpdaterInterface} object.
     */
    public Updater(UpdaterInterface<? super T> parentUpdater) {
        this.parentUpdater = parentUpdater;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(T t) {
        if (getParentUpdater() == null || getParentUpdater().update(t)) {
            return this.thisUpdate(t);
        } else {
            return false;
        }
    }

    /**
     * this updater update.
     * this will invoke after {@link #getParentUpdater()} update.
     *
     * @param t the AbstractGameWindowComponent to update
     * @return true means this update runs to its end normally.
     * false means something happens that this update is abort.
     * Usually when we implement a subclass of this,
     * if update returns false,
     * then we shall consider whether should we also stop the subclass's updateer as well.
     * @see #update(Object)
     */
    public abstract boolean thisUpdate(T t);

    /**
     * <p>Getter for the field <code>parentUpdater</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.game_window_components.UpdaterInterface} object.
     */
    public UpdaterInterface<? super T> getParentUpdater() {
        return parentUpdater;
    }
}