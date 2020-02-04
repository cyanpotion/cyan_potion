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
 * AbstractArea means a rectangle(or maybe not rectangle, but something else like circle)
 * who have a position,and width, and height.
 * by default(of this engine), the area must be totally contained in the described rectangle.
 * for example, if the shape is a circle, then the area must be the circumscribed rectangle of the circle.
 * you can see implement of this class for more details.
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
     * get left top posX of this area
     *
     * @return a float.
     */
    float getLeftTopPosX();

    /**
     * get left top posY of this area
     *
     * @return a float.
     */
    float getLeftTopPosY();

    /**
     * get center posX of this area
     *
     * @return a float.
     */
    default float getCenterPosX() {
        return this.getLeftTopPosX() + this.getWidth() / 2F;
    }

    /**
     * get center posY of this area
     *
     * @return a float.
     */
    default float getCenterPosY() {
        return this.getLeftTopPosY() + this.getHeight() / 2F;
    }


    //----------

    /**
     * get left posX of this area
     *
     * @return a float.
     */
    default float getLeftPosX() {
        return this.getLeftTopPosX();
    }

    /**
     * get right posX of this area
     *
     * @return a float.
     */
    default float getRightPosX() {
        return this.getLeftPosX() + this.getWidth();
    }

    /**
     * get top posY of this area
     *
     * @return a float.
     */
    default float getTopPosY() {
        return this.getLeftTopPosY();
    }

    /**
     * get bottom posY of this area
     *
     * @return a float.
     */
    default float getBottomPosY() {
        return this.getTopPosY() + this.getHeight();
    }

    //----------

    /**
     * get left bottom posX of this area
     *
     * @return a float.
     */
    default float getLeftBottomPosX() {
        return this.getLeftPosX();
    }

    /**
     * get left bottom posY of this area
     *
     * @return a float.
     */
    default float getLeftBottomPosY() {
        return this.getBottomPosY();
    }

    /**
     * get right top posX of this area
     *
     * @return a float.
     */
    default float getRightTopPosX() {
        return this.getRightPosX();
    }

    /**
     * get right top posY of this area
     *
     * @return a float.
     */
    default float getRightTopPosY() {
        return this.getTopPosY();
    }

    /**
     * get right bottom posX of this area
     *
     * @return a float.
     */
    default float getRightBottomPosX() {
        return this.getRightPosX();
    }

    /**
     * get right bottom posY of this area
     *
     * @return a float.
     */
    default float getRightBottomPosY() {
        return this.getBottomPosY();
    }

    /**
     * get left center posX of this area
     *
     * @return a float.
     */
    default float getLeftCenterPosX() {
        return this.getLeftPosX();
    }

    /**
     * get left center posY of this area
     *
     * @return a float.
     */
    default float getLeftCenterPosY() {
        return this.getCenterPosY();
    }

    /**
     * get right center posX of this area
     *
     * @return a float.
     */
    default float getRightCenterPosX() {
        return this.getRightPosX();
    }

    /**
     * get right center posY of this area
     *
     * @return a float.
     */
    default float getRightCenterPosY() {
        return this.getCenterPosY();
    }

    /**
     * get center top posX of this area
     *
     * @return a float.
     */
    default float getCenterTopPosX() {
        return this.getCenterPosX();
    }

    /**
     * get center top posY of this area
     *
     * @return a float.
     */
    default float getCenterTopPosY() {
        return this.getTopPosY();
    }

    /**
     * get center bottom posX of this area
     *
     * @return a float.
     */
    default float getCenterBottomPosX() {
        return this.getCenterPosX();
    }

    /**
     * get center bottom posY of this area
     *
     * @return a float.
     */
    default float getCenterBottomPosY() {
        return this.getBottomPosY();
    }


    /**
     * get width of this area
     *
     * @return width of this area.
     */
    float getWidth();

    /**
     * get height of this area
     *
     * @return height of this area.
     */
    float getHeight();

    /**
     * get a SimpleImmutableArea build from this area.
     *
     * @return return
     */
    default SimpleImmutableArea copyArea() {
        return new SimpleImmutableArea(this);
    }
}
