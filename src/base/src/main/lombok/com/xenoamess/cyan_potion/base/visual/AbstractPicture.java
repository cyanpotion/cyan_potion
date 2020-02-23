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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joml.Vector4f;
import org.joml.Vector4fc;

/**
 * Abstract Picture
 *
 * @author XenoAmess
 * @version 0.161.2-SNAPSHOT
 */
@EqualsAndHashCode
@ToString
public abstract class AbstractPicture implements AbstractPictureInterface {

    @Getter
    @Setter
    private float leftTopPosX = Float.NaN;

    @Getter
    @Setter
    private float leftTopPosY = Float.NaN;

    @Getter
    @Setter
    private float width = Float.NaN;

    @Getter
    @Setter
    private float height = Float.NaN;

    @Getter
    @Setter
    private final Vector4f colorScale = new Vector4f(1, 1, 1, 1);

    @Getter
    @Setter
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
}
