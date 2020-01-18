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

package com.xenoamess.cyan_potion.base.events;

import com.xenoamess.commons.main_thread_only.MainThreadOnly;
import com.xenoamess.cyan_potion.base.GameManager;

import java.util.Set;

/**
 * Event that must be applied by main thread due to some reasons,
 * for Example, some native invoke for glfw or opengl by lwjgl.
 * <p>
 * Notice that due to reason of performance, we decide to solve  all non-MainThreadEvent first,
 * then we solve the MainThreadEvent s.
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public interface MainThreadEvent extends Event {
    /**
     * {@inheritDoc}
     */
    @Override
    @MainThreadOnly
    Set<Event> apply(GameManager gameManager);
}
