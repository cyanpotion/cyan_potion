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

package com.xenoamess.cyan_potion.base.commons.areas;

/**
 * a simple immutable area class
 * must create it from Areas.
 *
 * @author XenoAmess
 * @version 0.155.4-SNAPSHOT
 * @see Areas#generateImmutableAreaFromArea(AbstractArea)
 * @see Areas#generateImmutableAreaFromLeftTop(float, float, float, float)
 * @see Areas#generateImmutableAreaFromArea(float, float, float, float)
 */
public final class SimpleImmutableArea implements AbstractImmutableArea {
    private final float leftTopPosX;
    private final float leftTopPosY;
    private final float width;
    private final float height;

    /**
     * <p>Constructor for ImmutableArea.</p>
     *
     * @param abstractArea area
     */
    SimpleImmutableArea(AbstractArea abstractArea) {
        this.leftTopPosX = abstractArea.getLeftTopPosX();
        this.leftTopPosY = abstractArea.getLeftTopPosY();
        this.width = abstractArea.getWidth();
        this.height = abstractArea.getHeight();
    }

    /**
     * <p>Constructor for ImmutableArea.</p>
     *
     * @param leftTopPosX a float.
     * @param leftTopPosY a float.
     * @param width       a float.
     * @param height      a float.
     */
    SimpleImmutableArea(float leftTopPosX, float leftTopPosY, float width, float height) {
        this.leftTopPosX = leftTopPosX;
        this.leftTopPosY = leftTopPosY;
        this.width = width;
        this.height = height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLeftTopPosX() {
        return this.leftTopPosX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLeftTopPosY() {
        return this.leftTopPosY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getWidth() {
        return this.width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getHeight() {
        return this.height;
    }
}
