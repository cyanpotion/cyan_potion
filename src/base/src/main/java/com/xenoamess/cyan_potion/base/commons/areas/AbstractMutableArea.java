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
 * Mutable Area
 *
 * @author XenoAmess
 * @version 0.148.8
 */
public interface AbstractMutableArea extends AbstractArea {

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean ifMutable() {
        return true;
    }

    /**
     * <p>setLeftTopPosX.</p>
     *
     * @param leftTopPosX a float.
     */
    void setLeftTopPosX(float leftTopPosX);

    /**
     * <p>setLeftTopPosY.</p>
     *
     * @param leftTopPosY a float.
     */
    void setLeftTopPosY(float leftTopPosY);

    /**
     * set center posX.
     * notice that this function will calculate center posX from leftTop posX and width.
     * so make sure your this.getWidth() is correct before you set this.
     *
     * @param newCenterPosX a float.
     */
    default void setCenterPosX(float newCenterPosX) {
        this.setLeftTopPosX(newCenterPosX - this.getWidth() / 2F);
    }

    /**
     * set center posY.
     * notice that this function will calculate center posY from leftTop posY and height.
     * so make sure your this.getHeight() is correct before you set this.
     *
     * @param newCenterPosY a float.
     */
    default void setCenterPosY(float newCenterPosY) {
        this.setLeftTopPosY(newCenterPosY - this.getHeight() / 2F);
    }

    /**
     * <p>setSize.</p>
     *
     * @param width  a float.
     * @param height a float.
     */
    default void setSize(float width, float height) {
        this.setWidth(width);
        this.setHeight(height);
    }

    /**
     * <p>setCenterPos.</p>
     *
     * @param leftTopPosX a float.
     * @param leftTopPoxY a float.
     */
    default void setLeftTopPos(float leftTopPosX, float leftTopPoxY) {
        this.setLeftTopPosX(leftTopPosX);
        this.setLeftTopPosY(leftTopPoxY);
    }

    /**
     * <p>setCenterPos.</p>
     *
     * @param centerPosX a float.
     * @param centerPosY a float.
     */
    default void setCenterPos(float centerPosX, float centerPosY) {
        this.setCenterPosX(centerPosX);
        this.setCenterPosY(centerPosY);
    }

    /**
     * <p>setCenter.</p>
     *
     * @param abstractArea area
     */
    default void setCenter(AbstractArea abstractArea) {
        this.setCenterPosX(abstractArea.getCenterPosX());
        this.setCenterPosY(abstractArea.getCenterPosY());
    }

    /**
     * <p>setSize.</p>
     *
     * @param abstractArea area
     */
    default void setSize(AbstractArea abstractArea) {
        this.setWidth(abstractArea.getWidth());
        this.setHeight(abstractArea.getHeight());
    }

    /**
     * <p>cover.</p>
     *
     * @param abstractArea area
     */
    default void cover(AbstractArea abstractArea) {
        this.setSize(abstractArea);
        this.setCenter(abstractArea);
    }

    /**
     * <p>move.</p>
     *
     * @param centerMovementX a float.
     * @param centerMovementY a float.
     */
    default void move(float centerMovementX, float centerMovementY) {
        this.moveX(centerMovementX);
        this.moveY(centerMovementY);
    }

    /**
     * <p>moveX.</p>
     *
     * @param centerMovementX a float.
     */
    default void moveX(float centerMovementX) {
        this.setCenterPosX(this.getCenterPosX() + centerMovementX);
    }

    /**
     * <p>moveY.</p>
     *
     * @param centerMovementY a float.
     */
    default void moveY(float centerMovementY) {
        this.setCenterPosY(this.getCenterPosY() + centerMovementY);
    }

    /**
     * <p>Setter for the field <code>width</code>.</p>
     *
     * @param width a float.
     */
    void setWidth(float width);

    /**
     * <p>Setter for the field <code>height</code>.</p>
     *
     * @param height a float.
     */
    void setHeight(float height);

    /**
     * <p>enlargeWidth.</p>
     *
     * @param newWidth a float.
     */
    default void enlargeWidth(float newWidth) {
        this.setWidth(this.getWidth() + newWidth);
    }

    /**
     * <p>enlargeHeight.</p>
     *
     * @param newHeight a float.
     */
    default void enlargeHeight(float newHeight) {
        this.setHeight(this.getHeight() + newHeight);
    }

    /**
     * <p>enlargeSize.</p>
     *
     * @param newWidth  a float.
     * @param newHeight a float.
     */
    default void enlargeSize(float newWidth, float newHeight) {
        this.enlargeWidth(newWidth);
        this.enlargeHeight(newHeight);
    }

    /**
     * <p>scaleWidth.</p>
     *
     * @param ratio a float.
     */
    default void scaleWidth(float ratio) {
        this.setWidth(this.getWidth() * ratio);
    }

    /**
     * <p>scaleHeight.</p>
     *
     * @param ratio a float.
     */
    default void scaleHeight(float ratio) {
        this.setHeight(this.getHeight() * ratio);
    }

    /**
     * <p>scaleSize.</p>
     *
     * @param ratio a float.
     */
    default void scaleSize(float ratio) {
        this.scaleWidth(ratio);
        this.scaleHeight(ratio);
    }
}
