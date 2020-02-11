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
 * Mutable Area
 *
 * @author XenoAmess
 * @version 0.157.1-SNAPSHOT
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
     * set left top posX.
     *
     * @param leftTopPosX leftTopPosX
     */
    void setLeftTopPosX(float leftTopPosX);

    /**
     * set left top posY.
     *
     * @param leftTopPosY leftTopPosY
     */
    void setLeftTopPosY(float leftTopPosY);

    /**
     * set center posX.
     * <p>
     * notice that this function will calculate center posX from centerPosX and {@link #getWidth()}.
     * so make sure your {@link #getWidth()} is correct before you set this.
     *
     * @param centerPosX centerPosX
     */
    default void setCenterPosX(float centerPosX) {
        this.setLeftTopPosX(centerPosX - this.getWidth() / 2F);
    }

    /**
     * set center posY.
     * <p>
     * notice that this function will calculate center posY from leftTop posY and {@link #getHeight()}.
     * so make sure your {@link #getHeight()} is correct before you set this.
     *
     * @param centerPosY centerPosY
     */
    default void setCenterPosY(float centerPosY) {
        this.setLeftTopPosY(centerPosY - this.getHeight() / 2F);
    }

    //----------

    /**
     * set left PosX.
     *
     * @param leftPosX leftPosX
     */
    default void setLeftPosX(float leftPosX) {
        this.setLeftTopPosX(leftPosX);
    }

    /**
     * set right posX.
     * <p>
     * notice that this function will calculate left top posX from right PosX and {@link #getWidth()}.
     * so make sure your {@link #getWidth()} is correct before you set this.
     *
     * @param rightPosX rightPosX
     */
    default void setRightPosX(float rightPosX) {
        this.setLeftTopPosX(rightPosX - this.getWidth());
    }

    /**
     * set top posY.
     *
     * @param topPosY topPosY
     */
    default void setTopPosY(float topPosY) {
        this.setLeftTopPosY(topPosY);
    }

    /**
     * set bottom posY.
     * <p>
     * notice that this function will calculate left top posY from bottomPosY and {@link #getHeight()}.
     * so make sure your {@link #getHeight()} is correct before you set this.
     *
     * @param bottomPosY bottomPosY
     */
    default void setBottomPosY(float bottomPosY) {
        this.setLeftTopPosY(bottomPosY - this.getHeight());
    }

    //----------

    /**
     * <p>setLeftBottomPosX.</p>
     *
     * @param leftBottomPosX leftBottomPosX
     * @see #setLeftPosX(float)
     */
    default void setLeftBottomPosX(float leftBottomPosX) {
        this.setLeftPosX(leftBottomPosX);
    }

    /**
     * <p>setLeftBottomPosY.</p>
     *
     * @param leftBottomPosY leftBottomPosY
     * @see #setBottomPosY(float)
     */
    default void setLeftBottomPosY(float leftBottomPosY) {
        this.setBottomPosY(leftBottomPosY);
    }

    /**
     * <p>setRightTopPosX.</p>
     *
     * @param rightTopPosX rightTopPosX
     * @see #setRightPosX(float)
     */
    default void setRightTopPosX(float rightTopPosX) {
        this.setRightPosX(rightTopPosX);
    }

    /**
     * <p>setRightTopPosY.</p>
     *
     * @param rightTopPosY rightTopPosY
     * @see #setTopPosY(float)
     */
    default void setRightTopPosY(float rightTopPosY) {
        this.setTopPosY(rightTopPosY);
    }

    /**
     * <p>setRightBottomPosX.</p>
     *
     * @param rightBottomPosX rightBottomPosX
     * @see #setRightBottomPosX(float)
     */
    default void setRightBottomPosX(float rightBottomPosX) {
        this.setRightPosX(rightBottomPosX);
    }

    /**
     * <p>setRightBottomPosY.</p>
     *
     * @param rightBottomPosY rightBottomPosY
     * @see #setBottomPosY(float)
     */
    default void setRightBottomPosY(float rightBottomPosY) {
        this.setBottomPosY(rightBottomPosY);
    }

    //----------

    /**
     * <p>setLeftCenterPosX.</p>
     *
     * @param leftCenterPosX leftCenterPosX
     * @see #setLeftPosX(float)
     */
    default void setLeftCenterPosX(float leftCenterPosX) {
        this.setLeftPosX(leftCenterPosX);
    }

    /**
     * <p>setLeftCenterPosY.</p>
     *
     * @param leftCenterPosY leftCenterPosY
     * @see #setCenterPosY(float)
     */
    default void setLeftCenterPosY(float leftCenterPosY) {
        this.setCenterPosY(leftCenterPosY);
    }

    /**
     * <p>setRightCenterPosX.</p>
     *
     * @param rightCenterPosX rightCenterPosX
     * @see #setRightPosX(float)
     */
    default void setRightCenterPosX(float rightCenterPosX) {
        this.setRightPosX(rightCenterPosX);
    }

    /**
     * <p>setRightCenterPosY.</p>
     *
     * @param rightCenterPosY rightCenterPosY
     * @see #setCenterPosY(float)
     */
    default void setRightCenterPosY(float rightCenterPosY) {
        this.setCenterPosY(rightCenterPosY);
    }

    /**
     * <p>setCenterTopPosX.</p>
     *
     * @param centerTopPosX centerTopPosX
     * @see #setCenterPosX(float)
     */
    default void setCenterTopPosX(float centerTopPosX) {
        this.setCenterPosX(centerTopPosX);
    }

    /**
     * <p>setCenterTopPosY.</p>
     *
     * @param centerTopPosY centerTopPosY
     * @see #setTopPosY(float)
     */
    default void setCenterTopPosY(float centerTopPosY) {
        this.setTopPosY(centerTopPosY);
    }

    /**
     * <p>setCenterBottomPosX.</p>
     *
     * @param centerBottomPosX centerBottomPosX
     * @see #setCenterPosX(float)
     */
    default void setCenterBottomPosX(float centerBottomPosX) {
        this.setCenterPosX(centerBottomPosX);
    }

    /**
     * <p>setCenterBottomPosY.</p>
     *
     * @param centerBottomPosY centerBottomPosY
     * @see #setBottomPosY(float)
     */
    default void setCenterBottomPosY(float centerBottomPosY) {
        this.setBottomPosY(centerBottomPosY);
    }

    //----------

    /**
     * set width and height.
     *
     * @param width  a float.
     * @param height a float.
     * @see #setHeight(float)
     * @see #setWidth(float)
     */
    default void setSize(float width, float height) {
        this.setWidth(width);
        this.setHeight(height);
    }

    /**
     * set width and height to a same value.
     *
     * @param widthAndHeight widthAndHeight == width == height
     * @see #setHeight(float)
     * @see #setWidth(float)
     */
    default void setSize(float widthAndHeight) {
        setSize(widthAndHeight, widthAndHeight);
    }

    /**
     * set leftTopPosX and leftTopPoxY
     *
     * @param leftTopPosX leftTopPosX
     * @param leftTopPoxY leftTopPoxY
     * @see #setLeftTopPosX(float)
     * @see #setLeftTopPosY(float)
     */
    default void setLeftTopPos(float leftTopPosX, float leftTopPoxY) {
        this.setLeftTopPosX(leftTopPosX);
        this.setLeftTopPosY(leftTopPoxY);
    }

    /**
     * set centerPosX and centerPosY
     *
     * @param centerPosX centerPosX
     * @param centerPosY centerPosY
     * @see #setCenterPosX(float)
     * @see #setCenterPosY(float)
     */
    default void setCenterPos(float centerPosX, float centerPosY) {
        this.setCenterPosX(centerPosX);
        this.setCenterPosY(centerPosY);
    }

    /**
     * set rightTopPosX and rightTopPoxY
     *
     * @param rightTopPosX rightTopPosX
     * @param rightTopPoxY rightTopPosX
     * @see #setRightTopPosX(float)
     * @see #setRightTopPosY(float)
     */
    default void setRightTopPos(float rightTopPosX, float rightTopPoxY) {
        this.setRightTopPosX(rightTopPosX);
        this.setRightTopPosY(rightTopPoxY);
    }

    /**
     * set rightTopPosX and rightTopPoxY
     *
     * @param leftBottomPosX leftBottomPoxY
     * @param leftBottomPoxY leftBottomPoxY
     * @see #setLeftBottomPosX(float)
     * @see #setLeftBottomPosY(float)
     */
    default void setLeftBottomPos(float leftBottomPosX, float leftBottomPoxY) {
        this.setLeftBottomPosX(leftBottomPosX);
        this.setLeftBottomPosY(leftBottomPoxY);
    }

    /**
     * set rightBottomPosX and rightBottomPoxY
     *
     * @param rightBottomPosX rightBottomPosX
     * @param rightBottomPoxY rightBottomPoxY
     * @see #setRightBottomPosX(float)
     * @see #setRightBottomPosY(float)
     */
    default void setRightBottomPos(float rightBottomPosX, float rightBottomPoxY) {
        this.setRightBottomPosX(rightBottomPosX);
        this.setRightBottomPosY(rightBottomPoxY);
    }

    /**
     * set leftCenterPosX and leftCenterPoxY
     *
     * @param leftCenterPosX leftCenterPosX
     * @param leftCenterPoxY leftCenterPoxY
     * @see #setLeftCenterPosX(float)
     * @see #setLeftCenterPosY(float)
     */
    default void setLeftCenterPos(float leftCenterPosX, float leftCenterPoxY) {
        this.setLeftCenterPosX(leftCenterPosX);
        this.setLeftCenterPosY(leftCenterPoxY);
    }

    /**
     * set rightCenterPosX and rightCenterPoxY
     *
     * @param rightCenterPosX rightCenterPosX
     * @param rightCenterPoxY rightCenterPoxY
     * @see #setRightCenterPosX(float)
     * @see #setRightCenterPosY(float)
     */
    default void setRightCenterPos(float rightCenterPosX, float rightCenterPoxY) {
        this.setRightCenterPosX(rightCenterPosX);
        this.setRightCenterPosY(rightCenterPoxY);
    }

    /**
     * set centerTopPosX and centerTopPosY
     *
     * @param centerTopPosX centerTopPosX
     * @param centerTopPosY centerTopPosY
     * @see #setCenterTopPosX(float)
     * @see #setCenterTopPosY(float)
     */
    default void setCenterTopPos(float centerTopPosX, float centerTopPosY) {
        this.setCenterTopPosX(centerTopPosX);
        this.setCenterTopPosY(centerTopPosY);
    }

    /**
     * set centerBottomPosX and centerBottomPosY
     *
     * @param centerBottomPosX centerBottomPosX
     * @param centerBottomPosY centerBottomPosY
     * @see #setCenterBottomPosX(float)
     * @see #setCenterBottomPosY(float)
     */
    default void setCenterBottomPos(float centerBottomPosX, float centerBottomPosY) {
        this.setCenterBottomPosX(centerBottomPosX);
        this.setCenterBottomPosY(centerBottomPosY);
    }


    /**
     * set this.center to abstractArea.center
     *
     * @param abstractArea abstractArea
     * @see #setCenterPosX(float)
     * @see #setCenterPosY(float)
     */
    default void setCenter(AbstractArea abstractArea) {
        this.setCenterPosX(abstractArea.getCenterPosX());
        this.setCenterPosY(abstractArea.getCenterPosY());
    }

    /**
     * set this.size to abstractArea.size
     *
     * @param abstractArea area
     */
    default void setSize(AbstractArea abstractArea) {
        this.setWidth(abstractArea.getWidth());
        this.setHeight(abstractArea.getHeight());
    }

    /**
     * make this be same size and same position to a area
     * set this.center to abstractArea.center
     * set this.size to abstractArea.size
     *
     * @param abstractArea area
     */
    default void cover(AbstractArea abstractArea) {
        this.setSize(abstractArea);
        this.setCenter(abstractArea);
    }

    /**
     * move
     *
     * @param movementX movementX
     * @param movementY movementY
     */
    default void move(float movementX, float movementY) {
        this.moveX(movementX);
        this.moveY(movementY);
    }

    /**
     * move posX
     *
     * @param movementX movementX
     */
    default void moveX(float movementX) {
        this.setLeftTopPosX(this.getLeftTopPosX() + movementX);
    }

    /**
     * move posY
     *
     * @param movementY movementY
     */
    default void moveY(float movementY) {
        this.setLeftTopPosY(this.getLeftTopPosY() + movementY);
    }

    //----------

    /**
     * <p>moveToLeftTopOf.</p>
     *
     * @param abstractArea a {@link AbstractArea} object.
     */
    default void moveToLeftTopOf(AbstractArea abstractArea) {
        this.setLeftTopPos(abstractArea.getLeftTopPosX(), abstractArea.getLeftTopPosY());
    }

    /**
     * <p>moveToRightTopOf.</p>
     *
     * @param abstractArea a {@link AbstractArea} object.
     */
    default void moveToRightTopOf(AbstractArea abstractArea) {
        this.setRightTopPos(abstractArea.getRightTopPosX(), abstractArea.getRightTopPosY());
    }

    /**
     * <p>moveToLeftBottomOf.</p>
     *
     * @param abstractArea a {@link AbstractArea} object.
     */
    default void moveToLeftBottomOf(AbstractArea abstractArea) {
        this.setLeftBottomPos(abstractArea.getLeftBottomPosX(), abstractArea.getLeftBottomPosY());
    }

    /**
     * <p>moveToRightBottomOf.</p>
     *
     * @param abstractArea a {@link AbstractArea} object.
     */
    default void moveToRightBottomOf(AbstractArea abstractArea) {
        this.setRightBottomPos(abstractArea.getRightBottomPosX(), abstractArea.getRightBottomPosY());
    }

    /**
     * <p>moveToLeftCenterOf.</p>
     *
     * @param abstractArea a {@link AbstractArea} object.
     */
    default void moveToLeftCenterOf(AbstractArea abstractArea) {
        this.setLeftCenterPos(abstractArea.getLeftCenterPosX(), abstractArea.getLeftCenterPosY());
    }

    /**
     * <p>moveToRightCenterOf.</p>
     *
     * @param abstractArea a {@link AbstractArea} object.
     */
    default void moveToRightCenterOf(AbstractArea abstractArea) {
        this.setRightCenterPos(abstractArea.getRightCenterPosX(), abstractArea.getRightCenterPosY());
    }

    /**
     * <p>moveToCenterTopOf.</p>
     *
     * @param abstractArea a {@link AbstractArea} object.
     */
    default void moveToCenterTopOf(AbstractArea abstractArea) {
        this.setCenterTopPos(abstractArea.getCenterTopPosX(), abstractArea.getCenterTopPosY());
    }

    /**
     * <p>moveToCenterBottomOf.</p>
     *
     * @param abstractArea a {@link AbstractArea} object.
     */
    default void moveToCenterBottomOf(AbstractArea abstractArea) {
        this.setCenterBottomPos(abstractArea.getCenterBottomPosX(), abstractArea.getCenterBottomPosY());
    }

    /**
     * <p>moveToCenterCentorOf.</p>
     *
     * @param abstractArea a {@link AbstractArea} object.
     */
    default void moveToCenterCentorOf(AbstractArea abstractArea) {
        this.setCenter(abstractArea);
    }

    //----------

    /**
     * <p>Setter for the field <code>width</code>.</p>
     *
     * @param width width
     */
    void setWidth(float width);

    /**
     * <p>Setter for the field <code>height</code>.</p>
     *
     * @param height height
     */
    void setHeight(float height);

    /**
     * this.width += enlargedWidth
     *
     * @param enlargedWidth enlargedWidth
     */
    default void enlargeWidth(float enlargedWidth) {
        this.setWidth(this.getWidth() + enlargedWidth);
    }

    /**
     * this.height += enlargedHeight
     *
     * @param enlargedHeight enlargedHeight
     */
    default void enlargeHeight(float enlargedHeight) {
        this.setHeight(this.getHeight() + enlargedHeight);
    }

    /**
     * enlarge both width and height
     *
     * @param enlargedWidth  a float.
     * @param enlargedHeight a float.
     */
    default void enlargeSize(float enlargedWidth, float enlargedHeight) {
        this.enlargeWidth(enlargedWidth);
        this.enlargeHeight(enlargedHeight);
    }

    /**
     * scale width
     *
     * @param ratio ratio
     */
    default void scaleWidth(float ratio) {
        this.setWidth(this.getWidth() * ratio);
    }

    /**
     * scale height
     *
     * @param ratio ratio
     */
    default void scaleHeight(float ratio) {
        this.setHeight(this.getHeight() * ratio);
    }

    /**
     * scale size
     *
     * @param ratio ratio
     */
    default void scaleSize(float ratio) {
        this.scaleWidth(ratio);
        this.scaleHeight(ratio);
    }
}
