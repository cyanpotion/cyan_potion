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
     * <p>getLeftTopPosX.</p>
     *
     * @return a float.
     */
    float getLeftTopPosX();

    /**
     * <p>getLeftTopPosY.</p>
     *
     * @return a float.
     */
    float getLeftTopPosY();

    default float getCenterPosX() {
        return this.getLeftTopPosX() + this.getWidth() / 2F;
    }

    default float getCenterPosY() {
        return this.getLeftTopPosY() + this.getHeight() / 2F;
    }


    default float getLeftPosX() {
        return this.getLeftTopPosX();
    }

    default float getRightPosX() {
        return this.getLeftPosX() + this.getWidth();
    }

    default float getTopPosY() {
        return this.getLeftTopPosY();
    }

    default float getBottomPosY() {
        return this.getTopPosY() + this.getHeight();
    }


    default float getLeftBottomPosX() {
        return this.getLeftPosX();
    }

    default float getLeftBottomPosY() {
        return this.getBottomPosY();
    }

    default float getRightTopPosX() {
        return this.getRightPosX();
    }

    default float getRightTopPosY() {
        return this.getTopPosY();
    }

    default float getRightBottomPosX() {
        return this.getRightPosX();
    }

    default float getRightBottomPosY() {
        return this.getBottomPosY();
    }

    default float getLeftCenterPosX() {
        return this.getLeftPosX();
    }

    default float getLeftCenterPosY() {
        return this.getCenterPosY();
    }

    default float getCenterTopPosX() {
        return this.getCenterPosX();
    }

    default float getCenterTopPosY() {
        return this.getTopPosY();
    }

    default float getCenterBottomPosX() {
        return this.getCenterPosX();
    }

    default float getCenterBottomPosY() {
        return this.getBottomPosY();
    }


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
}
