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

package com.xenoamess.cyan_potion.base.commons.areas;

/**
 * <p>Area interface.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public interface AbstractArea {
    /**
     * see if this area is mutable.
     *
     * @return true if mutable
     */
    boolean ifMutable();

    /**
     * <p>getCenterPosX.</p>
     *
     * @return a float.
     */
    float getCenterPosX();

    /**
     * <p>getCenterPosY.</p>
     *
     * @return a float.
     */
    float getCenterPosY();

    /**
     * <p>getWidth.</p>
     *
     * @return a float.
     */
    float getWidth();

    /**
     * <p>getHeight.</p>
     *
     * @return a float.
     */
    float getHeight();

    /**
     * <p>copyArea.</p>
     *
     * @return return
     */
    default SimpleImmutableArea copyArea() {
        return new SimpleImmutableArea(this);
    }

    /**
     * <p>generateImmutableArea.</p>
     *
     * @param centerPosX a float.
     * @param centerPosY a float.
     * @param width      a float.
     * @param height     a float.
     * @return return
     */
    static SimpleImmutableArea generateImmutableArea(
            float centerPosX, float centerPosY, float width, float height) {
        return new SimpleImmutableArea(centerPosX, centerPosY, width, height);
    }

    /**
     * <p>generateImmutableArea.</p>
     *
     * @param abstractArea area
     * @return return
     */
    static SimpleImmutableArea generateImmutableArea(
            AbstractArea abstractArea) {
        return new SimpleImmutableArea(abstractArea);
    }

    /**
     * <p>generateImmutableAreaFromLeftTop.</p>
     *
     * @param leftTopPosX a float.
     * @param leftTopPosY a float.
     * @param width       a float.
     * @param height      a float.
     * @return return
     */
    static SimpleImmutableArea generateImmutableAreaFromLeftTop(
            float leftTopPosX, float leftTopPosY, float width, float height) {
        return new SimpleImmutableArea(leftTopPosX + width / 2F, leftTopPosY + height / 2F, width, height);
    }

    /**
     * <p>generateMutableArea.</p>
     *
     * @param centerPosX a float.
     * @param centerPosY a float.
     * @param width      a float.
     * @param height     a float.
     * @return return
     */
    static SimpleMutableArea generateMutableArea(
            float centerPosX, float centerPosY, float width, float height) {
        return new SimpleMutableArea(centerPosX, centerPosY, width, height);
    }

    /**
     * <p>generateMutableArea.</p>
     *
     * @param abstractArea area
     * @return return
     */
    static SimpleMutableArea generateMutableArea(
            AbstractArea abstractArea) {
        return new SimpleMutableArea(abstractArea);
    }

    /**
     * <p>generateMutableAreaFromLeftTop.</p>
     *
     * @param leftTopPosX a float.
     * @param leftTopPosY a float.
     * @param width       a float.
     * @param height      a float.
     * @return return
     */
    static SimpleMutableArea generateMutableAreaFromLeftTop(
            float leftTopPosX, float leftTopPosY, float width, float height) {
        return new SimpleMutableArea(leftTopPosX + width / 2F, leftTopPosY + height / 2F, width, height);
    }
}
