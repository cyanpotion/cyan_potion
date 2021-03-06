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

package com.xenoamess.cyan_potion.base.areas;

import lombok.Data;

/**
 * a simple mutable area class
 * must create it from Areas.
 *
 * @author XenoAmess
 * @version 0.162.3
 * @see Areas#generateMutableAreaFromArea(AbstractArea)
 * @see Areas#generateMutableAreaFromLeftTop(float, float, float, float)
 * @see Areas#generateMutableAreaFromArea(float, float, float, float)
 */
@Data
public final class SimpleMutableArea implements AbstractMutableArea {
    private float leftTopPosX;
    private float leftTopPosY;
    private float width;
    private float height;

    /**
     * <p>Constructor for MutableArea.</p>
     *
     * @param abstractArea area
     */
    public SimpleMutableArea(AbstractArea abstractArea) {
        this.cover(abstractArea);
    }

    /**
     * <p>Constructor for MutableArea.</p>
     *
     * @param leftTopPosX a float.
     * @param leftTopPosY a float.
     * @param width       a float.
     * @param height      a float.
     */
    public SimpleMutableArea(float leftTopPosX, float leftTopPosY, float width, float height) {
        this.leftTopPosX = leftTopPosX;
        this.leftTopPosY = leftTopPosY;
        this.width = width;
        this.height = height;
    }

}
