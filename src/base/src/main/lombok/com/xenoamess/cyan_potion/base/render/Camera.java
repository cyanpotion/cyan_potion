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

import com.xenoamess.cyan_potion.base.areas.AbstractMutablePoint;

/**
 * <p>Camera class.</p>
 *
 * @author XenoAmess
 * @version 0.160.0-SNAPSHOT
 */
public class Camera implements AbstractMutablePoint {
    private float posX;
    private float posY;

    /**
     * <p>Constructor for Camera.</p>
     *
     * @param initX a int.
     * @param initY a int.
     */
    public Camera(float initX, float initY) {
        this.setPosX(initX);
        this.setPosY(initY);
    }

    /**
     * <p>Getter for the field <code>posX</code>.</p>
     *
     * @return a float.
     */
    public float getPosX() {
        return posX;
    }

    /**
     * {@inheritDoc}
     */
    public void setPosX(float posX) {
        this.posX = posX;
    }

    /**
     * <p>Getter for the field <code>posY</code>.</p>
     *
     * @return a float.
     */
    public float getPosY() {
        return posY;
    }

    /**
     * {@inheritDoc}
     */
    public void setPosY(float posY) {
        this.posY = posY;
    }
}