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

package com.xenoamess.cyan_potion.base.visual;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.commons.areas.Area;
import com.xenoamess.cyan_potion.base.game_window_components.AbstractScene;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.render.Model;
import org.joml.Vector4f;

import java.util.Objects;

/**
 * <p>Picture class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class Picture implements Area {
    private Bindable bindable;
    private float centerPosX;
    private float centerPosY;
    private float width;
    private float height;
    private Vector4f colorScale = new Vector4f(1, 1, 1, 1);
    private float rotateRadius = 0f;

    /**
     * <p>draw.</p>
     *
     * @param gameWindow a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     */
    public void draw(GameWindow gameWindow) {
        if (bindable == null) {
            return;
        }
        gameWindow.drawBindableRelative(
                getBindable(),
                getCenterPosX(),
                getCenterPosY(),
                getWidth(),
                getHeight(),
                Model.COMMON_MODEL,
                getColorScale(),
                getRotateRadius()
        );
    }

    /**
     * <p>draw.</p>
     *
     * @param scene a {@link com.xenoamess.cyan_potion.base.game_window_components.AbstractScene} object.
     */
    public void draw(AbstractScene scene) {
        if (bindable == null) {
            return;
        }

        scene.drawBindableAbsolute(
                scene.getCamera(),
                scene.getScale(),
                getBindable(),
                getCenterPosX(),
                getCenterPosY(),
                getWidth(),
                getHeight(),
                Model.COMMON_MODEL,
                getColorScale(),
                getRotateRadius()
        );
    }

    /**
     * <p>Constructor for Picture.</p>
     *
     * @param bindable a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     */
    public Picture(Bindable bindable) {
        this.setBindable(bindable);
    }

    /**
     * <p>Constructor for Picture.</p>
     */
    public Picture() {

    }

    /**
     * <p>enlargeWidth.</p>
     *
     * @param newWidth a float.
     */
    public void enlargeWidth(float newWidth) {
        this.setWidth(this.getWidth() + newWidth);
    }

    /**
     * <p>enlargeHeight.</p>
     *
     * @param newHeight a float.
     */
    public void enlargeHeight(float newHeight) {
        this.setHeight(this.getHeight() + newHeight);
    }

    /**
     * <p>enlargeSize.</p>
     *
     * @param newWidth  a float.
     * @param newHeight a float.
     */
    public void enlargeSize(float newWidth, float newHeight) {
        this.enlargeWidth(newWidth);
        this.enlargeHeight(newHeight);
    }

    /**
     * <p>scaleWidth.</p>
     *
     * @param ratio a float.
     */
    public void scaleWidth(float ratio) {
        this.setWidth(this.getWidth() * ratio);
    }

    /**
     * <p>scaleHeight.</p>
     *
     * @param ratio a float.
     */
    public void scaleHeight(float ratio) {
        this.setHeight(this.getHeight() * ratio);
    }

    /**
     * <p>scaleSize.</p>
     *
     * @param ratio a float.
     */
    public void scaleSize(float ratio) {
        this.scaleWidth(ratio);
        this.scaleHeight(ratio);
    }

    /**
     * <p>rotate.</p>
     *
     * @param newRotateRadius a float.
     */
    public void rotate(float newRotateRadius) {
        this.setRotateRadius(this.getRotateRadius() + newRotateRadius);
    }

    /**
     * <p>rotateTo.</p>
     *
     * @param newRotateRadius a float.
     */
    public void rotateTo(float newRotateRadius) {
        this.setRotateRadius(newRotateRadius);
    }


    /**
     * <p>setSize.</p>
     *
     * @param width  a float.
     * @param height a float.
     */
    public void setSize(float width, float height) {
        this.setWidth(width);
        this.setHeight(height);
    }

    /**
     * <p>setCenterPos.</p>
     *
     * @param centerPosX a float.
     * @param centerPosY a float.
     */
    public void setCenterPos(float centerPosX, float centerPosY) {
        this.setCenterPosX(centerPosX);
        this.setCenterPosY(centerPosY);
    }

    /**
     * <p>setCenter.</p>
     *
     * @param area a {@link com.xenoamess.cyan_potion.base.commons.areas.Area} object.
     */
    public void setCenter(Area area) {
        this.setCenterPosX(area.getCenterPosX());
        this.setCenterPosY(area.getCenterPosY());
    }

    /**
     * <p>setSize.</p>
     *
     * @param area a {@link com.xenoamess.cyan_potion.base.commons.areas.Area} object.
     */
    public void setSize(Area area) {
        this.setWidth(area.getWidth());
        this.setHeight(area.getHeight());
    }

    /**
     * <p>cover.</p>
     *
     * @param area a {@link com.xenoamess.cyan_potion.base.commons.areas.Area} object.
     */
    public void cover(Area area) {
        this.setCenter(area);
        this.setSize(area);
    }

    /**
     * <p>move.</p>
     *
     * @param centerMovementX a float.
     * @param centerMovementY a float.
     */
    public void move(float centerMovementX, float centerMovementY) {
        this.moveX(centerMovementX);
        this.moveY(centerMovementY);
    }

    /**
     * <p>moveX.</p>
     *
     * @param centerMovementX a float.
     */
    public void moveX(float centerMovementX) {
        this.setCenterPosX(this.getCenterPosX() + centerMovementX);
    }

    /**
     * <p>moveY.</p>
     *
     * @param centerMovementY a float.
     */
    public void moveY(float centerMovementY) {
        this.setCenterPosY(this.getCenterPosY() + centerMovementY);
    }

    /**
     * <p>moveTo.</p>
     *
     * @param newCenterPosX a float.
     * @param newCenterPosY a float.
     */
    public void moveTo(float newCenterPosX, float newCenterPosY) {
        this.setCenterPos(newCenterPosX, newCenterPosY);
    }

    /**
     * <p>moveToLeftTop.</p>
     *
     * @param newLeftTopPosX a float.
     * @param newLeftTopPosY a float.
     */
    public void moveToLeftTop(float newLeftTopPosX, float newLeftTopPosY) {
        this.setCenterPos(newLeftTopPosX + this.getWidth() / 2F, newLeftTopPosY + this.getHeight() / 2F);
    }


    //--- getters and setters ---

    /**
     * <p>Getter for the field <code>bindable</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     */
    public Bindable getBindable() {
        return bindable;
    }

    /**
     * <p>Setter for the field <code>bindable</code>.</p>
     *
     * @param bindable a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     */
    public void setBindable(Bindable bindable) {
        this.bindable = bindable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getCenterPosX() {
        return centerPosX;
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
     * {@inheritDoc}
     */
    @Override
    public float getCenterPosY() {
        return centerPosY;
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
     * {@inheritDoc}
     */
    @Override
    public float getWidth() {
        return width;
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
     * {@inheritDoc}
     */
    @Override
    public float getHeight() {
        return height;
    }

    /**
     * <p>Setter for the field <code>height</code>.</p>
     *
     * @param height a float.
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * <p>Getter for the field <code>colorScale</code>.</p>
     *
     * @return a {@link org.joml.Vector4f} object.
     */
    public Vector4f getColorScale() {
        return colorScale;
    }

    /**
     * <p>Setter for the field <code>colorScale</code>.</p>
     *
     * @param colorScale a {@link org.joml.Vector4f} object.
     */
    public void setColorScale(Vector4f colorScale) {
        this.colorScale = colorScale;
    }

    /**
     * <p>Getter for the field <code>rotateRadius</code>.</p>
     *
     * @return a float.
     */
    public float getRotateRadius() {
        return rotateRadius;
    }

    /**
     * <p>Setter for the field <code>rotateRadius</code>.</p>
     *
     * @param rotateRadius a float.
     */
    public void setRotateRadius(float rotateRadius) {
        this.rotateRadius = rotateRadius;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Picture)) {
            return false;
        }
        Picture picture = (Picture) o;
        return Float.compare(picture.getCenterPosX(), getCenterPosX()) == 0 &&
                Float.compare(picture.getCenterPosY(), getCenterPosY()) == 0 &&
                Float.compare(picture.getWidth(), getWidth()) == 0 &&
                Float.compare(picture.getHeight(), getHeight()) == 0 &&
                Float.compare(picture.getRotateRadius(), getRotateRadius()) == 0 &&
                Objects.equals(getBindable(), picture.getBindable()) &&
                Objects.equals(getColorScale(), picture.getColorScale());
    }
}
