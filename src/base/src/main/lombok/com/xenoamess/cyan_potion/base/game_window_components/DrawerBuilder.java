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
import lombok.ToString;

/**
 * <p>Abstract DrawerBuilder class.</p>
 *
 * @author XenoAmess
 * @version 0.161.2-SNAPSHOT
 */
@EqualsAndHashCode
@ToString
public abstract class DrawerBuilder<T> {
    /**
     * <p>build.</p>
     *
     * @param superUpdater a {@link com.xenoamess.cyan_potion.base.game_window_components.DrawerInterface} object.
     * @return a {@link com.xenoamess.cyan_potion.base.game_window_components.DrawerInterface} object.
     */
    public abstract DrawerInterface<T> build(DrawerInterface<? super T> superUpdater);
}
