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

package com.xenoamess.cyan_potion.base.visual;

import org.joml.Vector4f;
import org.joml.Vector4fc;

/**
 * Abstract Picture
 *
 * @author XenoAmess
 * @version 0.156.1-SNAPSHOT
 */
public abstract class AbstractPicture implements AbstractPictureInterface {
    private float leftTopPosX = Float.NaN;
    private float leftTopPosY = Float.NaN;
    private float width = Float.NaN;
    private float height = Float.NaN;
    private final Vector4f colorScale = new Vector4f(1, 1, 1, 1);
    private float rotateRadius = 0f;

    /**
     * {@inheritDoc}
     *
     * <p>rotate.</p>
     */
    @Override
    public void rotate(float newRotateRadius) {
        this.setRotateRadius(this.getRotateRadius() + newRotateRadius);
    }

    /**
     * {@inheritDoc}
     *
     * <p>rotateTo.</p>
     */
    @Override
    public void rotateTo(float newRotateRadius) {
        this.setRotateRadius(newRotateRadius);
    }


    //--- getters and setters ---

    /**
     * {@inheritDoc}
     *
     * <p>Getter for the field <code>leftTopPosX</code>.</p>
     */
    @Override
    public float getLeftTopPosX() {
        return leftTopPosX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLeftTopPosX(float leftTopPosX) {
        this.leftTopPosX = leftTopPosX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLeftTopPosY() {
        return leftTopPosY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLeftTopPosY(float leftTopPosY) {
        this.leftTopPosY = leftTopPosY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getWidth() {
        return width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getHeight() {
        return height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Getter for the field <code>colorScale</code>.</p>
     */
    @Override
    public Vector4fc getColorScale() {
        return new Vector4f(colorScale);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Setter for the field <code>colorScale</code>.</p>
     */
    @Override
    public void setColorScale(Vector4fc colorScale) {
        this.colorScale.set(colorScale);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Getter for the field <code>rotateRadius</code>.</p>
     */
    @Override
    public float getRotateRadius() {
        return rotateRadius;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Setter for the field <code>rotateRadius</code>.</p>
     */
    @Override
    public void setRotateRadius(float rotateRadius) {
        this.rotateRadius = rotateRadius;
    }
}
