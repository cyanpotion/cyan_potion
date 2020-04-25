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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * <p>Abstract Drawer class.</p>
 *
 * @author XenoAmess
 * @version 0.162.1
 */

@EqualsAndHashCode
@ToString
public abstract class Drawer<T> implements DrawerInterface<T> {
    @Getter
    private final DrawerInterface<? super T> parentDrawer;

    /**
     * <p>Constructor for Drawer.</p>
     */
    public Drawer() {
        this(null);
    }

    /**
     * <p>Constructor for Drawer.</p>
     *
     * @param parentDrawer a {@link com.xenoamess.cyan_potion.base.game_window_components.DrawerInterface} object.
     */
    public Drawer(DrawerInterface<? super T> parentDrawer) {
        this.parentDrawer = parentDrawer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean draw(T t) {
        if (getParentDrawer() == null || getParentDrawer().draw(t)) {
            return this.thisDraw(t);
        } else {
            return false;
        }
    }

    /**
     * this drawer draw.
     * this will invoke after {@link #getParentDrawer()} draw.
     *
     * @param t the AbstractGameWindowComponent to draw
     * @return true means this draw runs to its end normally.
     * false means something happens that this draw is abort.
     * Usually when we implement a subclass of this,
     * if draw returns false,
     * then we shall consider whether should we also stop the subclass's drawer as well.
     * @see #draw(Object)
     */
    public abstract boolean thisDraw(T t);

}
