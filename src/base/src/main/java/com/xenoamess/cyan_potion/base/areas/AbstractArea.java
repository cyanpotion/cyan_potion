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
 * AbstractArea means a rectangle(or maybe not rectangle, but something else like circle)
 * who have a position,and width, and height.
 * by default(of this engine), the area must be totally contained in the described rectangle.
 * for example, if the shape is a circle, then the area must be the circumscribed rectangle of the circle.
 * <p>
 * you can see implementations of this class for more details.
 *
 * @author XenoAmess
 * @version 0.159.0
 */
public interface AbstractArea {
    /**
     * see if this area is mutable.
     *
     * @return true if mutable
     */
    boolean ifMutable();

    /**
     * get left top posX of this area. return {@link java.lang.Float#NaN} if no leftTopPosX.
     *
     * @return leftTopPosX
     */
    float getLeftTopPosX();

    /**
     * get left top posY of this area. return {@link java.lang.Float#NaN} if no leftTopPosY.
     *
     * @return leftTopPosY
     */
    float getLeftTopPosY();

    /**
     * get center posX of this area. return {@link java.lang.Float#NaN} if no centerPosX.
     * <p>
     * notice that this value is calculated using {@link #getLeftTopPosX()} and {@link #getWidth()},
     * so make sure they have value before you call this, or it will return {@link java.lang.Float#NaN} as well.
     *
     * @return centerPosX
     */
    default float getCenterPosX() {
        return this.getLeftTopPosX() + this.getWidth() / 2F;
    }

    /**
     * get center posY of this area. return {@link java.lang.Float#NaN} if no centerPosY.
     * <p>
     * notice that this value is calculated using {@link #getLeftTopPosY()} and {@link #getHeight()},
     * so make sure they have value before you call this, or it will return {@link java.lang.Float#NaN} as well.
     *
     * @return centerPosY
     */
    default float getCenterPosY() {
        return this.getLeftTopPosY() + this.getHeight() / 2F;
    }


    //----------

    /**
     * get left posX of this area
     *
     * @return LeftPosX
     */
    default float getLeftPosX() {
        return this.getLeftTopPosX();
    }

    /**
     * get right posX of this area
     * <p>
     * notice that this value is calculated using {@link #getLeftTopPosX()} and {@link #getWidth()},
     * so make sure they have value before you call this, or it will return {@link java.lang.Float#NaN} as well.
     *
     * @return RightPosX
     */
    default float getRightPosX() {
        return this.getLeftPosX() + this.getWidth();
    }

    /**
     * get top posY of this area
     *
     * @return TopPosY
     */
    default float getTopPosY() {
        return this.getLeftTopPosY();
    }

    /**
     * get bottom posY of this area
     * <p>
     * notice that this value is calculated using {@link #getLeftTopPosY()} and {@link #getHeight()},
     * so make sure they have value before you call this, or it will return {@link java.lang.Float#NaN} as well.
     *
     * @return BottomPosY
     */
    default float getBottomPosY() {
        return this.getTopPosY() + this.getHeight();
    }

    //----------

    /**
     * get left bottom posX of this area
     *
     * @return LeftBottomPosX
     */
    default float getLeftBottomPosX() {
        return this.getLeftPosX();
    }

    /**
     * get left bottom posY of this area
     *
     * @return LeftBottomPosY
     */
    default float getLeftBottomPosY() {
        return this.getBottomPosY();
    }

    /**
     * get right top posX of this area
     *
     * @return RightTopPosX
     */
    default float getRightTopPosX() {
        return this.getRightPosX();
    }

    /**
     * get right top posY of this area
     *
     * @return RightTopPosY
     */
    default float getRightTopPosY() {
        return this.getTopPosY();
    }

    /**
     * get right bottom posX of this area
     *
     * @return RightBottomPosX
     */
    default float getRightBottomPosX() {
        return this.getRightPosX();
    }

    /**
     * get right bottom posY of this area
     *
     * @return RightBottomPosY
     */
    default float getRightBottomPosY() {
        return this.getBottomPosY();
    }

    /**
     * get left center posX of this area
     *
     * @return LeftCenterPosX
     */
    default float getLeftCenterPosX() {
        return this.getLeftPosX();
    }

    /**
     * get left center posY of this area
     *
     * @return LeftCenterPosY
     */
    default float getLeftCenterPosY() {
        return this.getCenterPosY();
    }

    /**
     * get right center posX of this area
     *
     * @return RightCenterPosX
     */
    default float getRightCenterPosX() {
        return this.getRightPosX();
    }

    /**
     * get right center posY of this area
     *
     * @return RightCenterPosY
     */
    default float getRightCenterPosY() {
        return this.getCenterPosY();
    }

    /**
     * get center top posX of this area
     *
     * @return CenterTopPosX
     */
    default float getCenterTopPosX() {
        return this.getCenterPosX();
    }

    /**
     * get center top posY of this area
     *
     * @return CenterTopPosY
     */
    default float getCenterTopPosY() {
        return this.getTopPosY();
    }

    /**
     * get center bottom posX of this area
     *
     * @return CenterBottomPosX
     */
    default float getCenterBottomPosX() {
        return this.getCenterPosX();
    }

    /**
     * get center bottom posY of this area
     *
     * @return CenterBottomPosY
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
     * @return SimpleImmutableArea built from this area.
     */
    default SimpleImmutableArea copyArea() {
        return new SimpleImmutableArea(this);
    }

    /**
     * detect if a PosX in this Area
     *
     * @param posX posX
     * @return whether posX in this area.
     */
    default boolean ifPosXInArea(float posX) {
        return posX >= this.getLeftPosX() && posX <= this.getRightPosX();
    }

    /**
     * detect if a PosY in this Area
     *
     * @param posY posY
     * @return whether posY in this area.
     */
    default boolean ifPosYInArea(float posY) {
        return posY >= this.getTopPosY() && posY <= this.getBottomPosY();
    }

    /**
     * detect if a Pos in this Area
     * <p>
     * if width or height be NaN, then return false.
     *
     * @param posX posX
     * @param posY posY
     * @return whether point (posX,posY) in this area.
     */
    default boolean ifPosInArea(float posX, float posY) {
        return this.ifPosXInArea(posX) && this.ifPosYInArea(posY);
    }

    /**
     * detect if a point in this Area
     * <p>
     * if width or height be NaN, then return false.
     *
     * @param point point
     * @return whether point (posX,posY) in this area.
     */
    default boolean ifPointInArea(AbstractPoint point) {
        return this.ifPosXInArea(point.getPosX()) && this.ifPosYInArea(point.getPosY());
    }

}
