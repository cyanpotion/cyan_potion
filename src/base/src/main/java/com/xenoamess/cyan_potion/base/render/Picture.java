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

import com.xenoamess.cyan_potion.base.AbstractScene;
import com.xenoamess.cyan_potion.base.Area;
import com.xenoamess.cyan_potion.base.GameWindow;
import org.joml.Vector4f;

/**
 * @author XenoAmess
 */
public class Picture implements Area {
    private Bindable bindable;
    private float centerPosX;
    private float centerPosY;
    private float width;
    private float height;
    private Vector4f colorScale = new Vector4f(1, 1, 1, 1);
    private float rotateRadius = 0f;

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

    public void scaleWidth(float ratio) {
        this.setWidth(this.getWidth() * ratio);
    }

    public void scaleHeight(float ratio) {
        this.setHeight(this.getHeight() * ratio);
    }

    public void scaleSize(float ratio) {
        this.scaleWidth(ratio);
        this.scaleHeight(ratio);
    }

    public void rotate(float newRotateRadius) {
        this.setRotateRadius(this.getRotateRadius() + newRotateRadius);
    }

    public void rotateTo(float newRotateRadius) {
        this.setRotateRadius(newRotateRadius);
    }


    public void setSize(float width, float height) {
        this.setWidth(width);
        this.setHeight(height);
    }

    public void setCenterPos(float centerPosX, float centerPosY) {
        this.setCenterPosX(centerPosX);
        this.setCenterPosY(centerPosY);
    }

    public void setCenter(Area area) {
        this.setCenterPosX(area.getCenterPosX());
        this.setCenterPosY(area.getCenterPosY());
    }

    public void setSize(Area area) {
        this.setWidth(area.getWidth());
        this.setHeight(area.getHeight());
    }

    public void cover(Area area) {
        this.setCenter(area);
        this.setSize(area);
    }

    public void move(float centerMovementX, float centerMovementY) {
        this.moveX(centerMovementX);
        this.moveY(centerMovementY);
    }

    public void moveX(float centerMovementX) {
        this.setCenterPosX(this.getCenterPosX() + centerMovementX);
    }

    public void moveY(float centerMovementY) {
        this.setCenterPosY(this.getCenterPosY() + centerMovementY);
    }


    //--- getters and setters ---

    public Bindable getBindable() {
        return bindable;
    }

    public void setBindable(Bindable bindable) {
        this.bindable = bindable;
    }

    @Override
    public float getCenterPosX() {
        return centerPosX;
    }

    public void setCenterPosX(float centerPosX) {
        this.centerPosX = centerPosX;
    }

    @Override
    public float getCenterPosY() {
        return centerPosY;
    }

    public void setCenterPosY(float centerPosY) {
        this.centerPosY = centerPosY;
    }

    @Override
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    @Override
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
