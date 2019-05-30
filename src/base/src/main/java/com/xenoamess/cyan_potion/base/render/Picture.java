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

package com.xenoamess.cyan_potion.base.render;

import com.xenoamess.cyan_potion.base.GameWindow;
import org.joml.Vector4f;

/**
 * @author XenoAmess
 */
public class Picture {
    private Bindable bindable;
    private float centerPosX;
    private float centerPosY;
    private float width;
    private float height;
    private Vector4f colorScale = new Vector4f(1, 1, 1, 1);
    private float rotateRadius = 0f;

    public void draw(GameWindow gameWindow) {
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

    public Picture(Bindable bindable) {
        this.setBindable(bindable);
    }

    public Picture() {

    }

    public void enlargeWidth(float newWidth) {
        this.setWidth(this.getWidth() + newWidth);
    }

    public void enlargeHeight(float newHeight) {
        this.setHeight(this.getHeight() + newHeight);
    }

    public void enlargeSize(float newWidth, float newHeight) {
        this.enlargeWidth(newWidth);
        this.enlargeHeight(newHeight);
    }

    public void rotateTo(float newRotateRadius) {
        this.setRotateRadius(newRotateRadius);
    }

    public void rotate(float newRotateRadius) {
        this.setRotateRadius(this.getRotateRadius() + newRotateRadius);
    }

    public void setSize(float width, float height) {
        this.setWidth(width);
        this.setHeight(height);
    }

    public void setCenterPos(float centerPosX, float centerPosY) {
        this.setCenterPosX(centerPosX);
        this.setCenterPosY(centerPosY);
    }

    public void setCenter(GameWindow gameWindow) {
        this.setCenterPosX(gameWindow.getLogicWindowWidth() / 2f);
        this.setCenterPosY(gameWindow.getLogicWindowHeight() / 2f);
    }

    public void setCoverFullWindow(GameWindow gameWindow) {
        this.setCenter(gameWindow);
        this.setSize(gameWindow);
    }

    public void setSize(GameWindow gameWindow) {
        this.setWidth(gameWindow.getLogicWindowWidth());
        this.setHeight(gameWindow.getLogicWindowHeight());
    }

    //--- getters and setters ---

    public Bindable getBindable() {
        return bindable;
    }

    public void setBindable(Bindable bindable) {
        this.bindable = bindable;
    }

    public float getCenterPosX() {
        return centerPosX;
    }

    public void setCenterPosX(float centerPosX) {
        this.centerPosX = centerPosX;
    }

    public float getCenterPosY() {
        return centerPosY;
    }

    public void setCenterPosY(float centerPosY) {
        this.centerPosY = centerPosY;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Vector4f getColorScale() {
        return colorScale;
    }

    public void setColorScale(Vector4f colorScale) {
        this.colorScale = colorScale;
    }

    public float getRotateRadius() {
        return rotateRadius;
    }

    public void setRotateRadius(float rotateRadius) {
        this.rotateRadius = rotateRadius;
    }
}
