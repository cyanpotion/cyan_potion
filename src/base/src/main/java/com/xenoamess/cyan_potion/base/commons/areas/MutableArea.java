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
 * <p>MutableArea class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class MutableArea implements Area {
    private float centerPosX;
    private float centerPosY;
    private float width;
    private float height;

    /**
     * <p>Constructor for MutableArea.</p>
     *
     * @param area area
     */
    public MutableArea(Area area) {
        this.setCenterPosX(area.getCenterPosX());
        this.setCenterPosY(area.getCenterPosY());
        this.setWidth(area.getWidth());
        this.setHeight(area.getHeight());
    }

    /**
     * <p>Constructor for MutableArea.</p>
     *
     * @param centerPosX a float.
     * @param centerPosY a float.
     * @param width      a float.
     * @param height     a float.
     */
    public MutableArea(float centerPosX, float centerPosY, float width, float height) {
        this.centerPosX = centerPosX;
        this.centerPosY = centerPosY;
        this.width = width;
        this.height = height;
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
    public static MutableArea generateMutableArea(
            float centerPosX, float centerPosY, float width, float height) {
        return new MutableArea(centerPosX, centerPosY, width, height);
    }

    /**
     * <p>generateMutableArea.</p>
     *
     * @param area area
     * @return return
     */
    public static MutableArea generateMutableArea(
            Area area) {
        return new MutableArea(area);
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
    public static MutableArea generateMutableAreaFromLeftTop(
            float leftTopPosX, float leftTopPosY, float width, float height) {
        return new MutableArea(leftTopPosX + width / 2F, leftTopPosY + height / 2F, width, height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getCenterPosX() {
        return this.centerPosX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getCenterPosY() {
        return this.centerPosY;
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

    /**
     * <p>Setter for the field <code>centerPosX</code>.</p>
     *
     * @param centerPosX a float.
     */
    public void setCenterPosX(float centerPosX) {
        this.centerPosX = centerPosX;
    }

    /**
     * <p>Setter for the field <code>centerPosY</code>.</p>
     *
     * @param centerPosY a float.
     */
    public void setCenterPosY(float centerPosY) {
        this.centerPosY = centerPosY;
    }

    /**
     * <p>Setter for the field <code>width</code>.</p>
     *
     * @param width a float.
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * <p>Setter for the field <code>height</code>.</p>
     *
     * @param height a float.
     */
    public void setHeight(float height) {
        this.height = height;
    }
}
