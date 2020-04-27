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

package com.xenoamess.cyan_potion.base.render;

import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.glBindTexture;

/**
 * Bindable is something who can bind.
 * It is usually a kind of resource, a picture or something else that once bind, it can draw to something.
 *
 * @author XenoAmess
 * @version 0.162.2-SNAPSHOT
 */
public interface Bindable {

    /**
     * <p>bind.</p>
     *
     * @param sampler a int.
     */
    void bind(int sampler);

    /**
     * <p>unbind.</p>
     */
    default void unbind() {
        glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    /**
     * <p>bind.</p>
     */
    default void bind() {
        bind(0);
    }
}
