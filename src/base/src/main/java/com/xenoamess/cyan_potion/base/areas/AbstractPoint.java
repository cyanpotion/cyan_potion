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

/**
 * <p>AbstractPoint interface.</p>
 *
 * @author XenoAmess
 * @version 0.162.3
 */
public interface AbstractPoint {
    /**
     * see if this point is mutable.
     *
     * @return true if mutable
     */
    @SuppressWarnings("unused")
    boolean ifMutable();

    /**
     * <p>getPosX.</p>
     *
     * @return a float.
     */
    float getPosX();

    /**
     * <p>getPosY.</p>
     *
     * @return a float.
     */
    float getPosY();

    /**
     * detect if a point in this Area
     * <p>
     * if width or height be NaN, then return false.
     *
     * @param area area
     * @return whether point (posX,posY) in this area.
     */
    @SuppressWarnings("unused")
    default boolean ifPointInArea(AbstractArea area) {
        return area.ifPosXInArea(this.getPosX()) && area.ifPosYInArea(this.getPosY());
    }
}
