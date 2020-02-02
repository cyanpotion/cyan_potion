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

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.commons.areas.AbstractMutableArea;
import com.xenoamess.cyan_potion.base.game_window_components.AbstractScene;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.render.Model;
import org.joml.Vector4f;

/**
 * Abstract Picture
 *
 * @author XenoAmess
 * @version 0.148.8
 */
public abstract class AbstractPicture implements AbstractMutableArea, Bindable {
    private float leftTopPosX;
    private float leftTopPosY;
    private float width;
    private float height;
    private final Vector4f colorScale = new Vector4f(1, 1, 1, 1);
    private float rotateRadius = 0f;

    /**
     * {@inheritDoc}
     */
    @Override
    public void bind(int sampler) {
        Bindable bindable = this.getCurrentBindable();
        if (bindable != null) {
            bindable.bind(sampler);
        }
    }

    /**
     * <p>draw.</p>
     *
     * @param gameWindow gameWindow
     */
    public void draw(GameWindow gameWindow) {
        if (this.getCurrentBindable() == null) {
            return;
        }
        gameWindow.drawBindableRelativeCenter(
                this.getCurrentBindable(),
                this.getCenterPosX(),
                this.getCenterPosY(),
                this.getWidth(),
                this.getHeight(),
                Model.COMMON_MODEL,
                this.getColorScale(),
                this.getRotateRadius()
        );
    }

    /**
     * <p>draw.</p>
     *
     * @param scene scene
     */
    public void draw(AbstractScene scene) {
        if (this.getCurrentBindable() == null) {
            return;
        }
        scene.drawBindableAbsolute(
                scene.getCamera(),
                scene.getScale(),
                this.getCurrentBindable(),
                this.getCenterPosX(),
                this.getCenterPosY(),
                this.getWidth(),
                this.getHeight(),
                Model.COMMON_MODEL,
                this.getColorScale(),
                this.getRotateRadius()
        );
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
     * get current bindable of this picture.
     *
     * @return the bindable for draw
     */
    public abstract Bindable getCurrentBindable();

    //--- getters and setters ---

    /**
     * <p>Getter for the field <code>leftTopPosX</code>.</p>
     *
     * @return a float.
     */
    public float getLeftTopPosX() {
        return leftTopPosX;
    }

    /**
     * {@inheritDoc}
     */
    public void setLeftTopPosX(float leftTopPosX) {
        this.leftTopPosX = leftTopPosX;
    }

    /**
     * {@inheritDoc}
     *
     * @return a float.
     */
    public float getLeftTopPosY() {
        return leftTopPosY;
    }

    /**
     * {@inheritDoc}
     */
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
     * <p>Getter for the field <code>colorScale</code>.</p>
     *
     * @return return
     */
    public Vector4f getColorScale() {
        return colorScale;
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
}
