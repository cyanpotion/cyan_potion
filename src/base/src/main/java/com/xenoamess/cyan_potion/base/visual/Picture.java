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
import com.xenoamess.cyan_potion.base.commons.areas.AbstractArea;
import com.xenoamess.cyan_potion.base.commons.areas.AbstractMutableArea;
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
public class Picture implements AbstractMutableArea {
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
     * @param gameWindow gameWindow
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
     * @param scene scene
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
     * @param bindable bindable
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

    //--- getters and setters ---

    /**
     * <p>Getter for the field <code>bindable</code>.</p>
     *
     * @return return
     */
    public Bindable getBindable() {
        return bindable;
    }

    /**
     * <p>Setter for the field <code>bindable</code>.</p>
     *
     * @param bindable bindable
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
    @Override
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
    @Override
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
     * <p>Setter for the field <code>height</code>.</p>
     *
     * @param height a float.
     */
    @Override
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * <p>Getter for the field <code>colorScale</code>.</p>
     *
     * @return return
     */
    public Vector4f getColorScale() {
        return colorScale;
    }

    /**
     * <p>Setter for the field <code>colorScale</code>.</p>
     *
     * @param colorScale colorScale
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
