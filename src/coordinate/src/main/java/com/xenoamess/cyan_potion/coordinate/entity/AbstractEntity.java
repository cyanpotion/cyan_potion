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

package com.xenoamess.cyan_potion.coordinate.entity;

import com.xenoamess.cyan_potion.base.commons.areas.AbstractMutableArea;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.visual.AbstractPicture;
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.coordinate.AbstractEntityScene;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;
import org.joml.Vector3f;

import java.util.Objects;

/**
 * <p>Abstract AbstractEntity class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public abstract class AbstractEntity implements AbstractMutableArea {
    private final AbstractEntityScene scene;
    private Vector3f centerPos;
    private Vector3f size;
    private AbstractShape shape;
    private AbstractPicture picture = new Picture();


    /**
     * <p>Constructor for AbstractEntity.</p>
     *
     * @param scene     a {@link com.xenoamess.cyan_potion.coordinate.AbstractEntityScene} object.
     * @param centerPos centerPos
     * @param size      a {@link org.joml.Vector3f} object.
     * @param bindable  a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     * @param shape     a {@link com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape} object.
     */
    public AbstractEntity(AbstractEntityScene scene, Vector3f centerPos,
                          Vector3f size, Bindable bindable,
                          AbstractShape shape) {
        this.scene = scene;
        this.setCenterPos(new Vector3f(centerPos));
        this.setSize(new Vector3f(size));
        this.setShape(shape);
        if (this.getShape() != null) {
            this.getShape().setEntity(this);
        }
        this.setPicture(new Picture(bindable));
        this.getPicture().cover(this);
    }

    /**
     * <p>Getter for the field <code>picture</code>.</p>
     */
    public void setPicture(AbstractPicture picture) {
        this.picture = picture;
        if (this.picture != null) {
            this.picture.cover(this);
        }
    }

    /**
     * <p>Getter for the field <code>picture</code>.</p>
     *
     * @return return
     */
    public AbstractPicture getPicture() {
        return this.picture;
    }

    /**
     * <p>draw.</p>
     *
     * @param scene scene
     */
    public void draw(AbstractEntityScene scene) {
        this.getPicture().draw(scene);
    }

    /**
     * <p>register.</p>
     */
    public void register() {
        if (this.getShape() != null) {
            this.getShape().register();
        }
    }


    /**
     * <p>Getter for the field <code>scene</code>.</p>
     *
     * @return return
     */
    public AbstractEntityScene getScene() {
        return scene;
    }

    /**
     * <p>Getter for the field <code>centerPos</code>.</p>
     *
     * @return return
     */
    public Vector3f getCenterPos() {
        return centerPos;
    }

    /**
     * <p>Setter for the field <code>centerPos</code>.</p>
     *
     * @param centerPos centerPos
     */
    public void setCenterPos(Vector3f centerPos) {
        this.centerPos = centerPos;
    }

    /**
     * <p>Getter for the field <code>size</code>.</p>
     *
     * @return return
     */
    public Vector3f getSize() {
        return size;
    }

    /**
     * <p>Setter for the field <code>size</code>.</p>
     *
     * @param size size
     */
    public void setSize(Vector3f size) {
        this.size = size;
    }

    /**
     * <p>Getter for the field <code>shape</code>.</p>
     *
     * @return return
     */
    public AbstractShape getShape() {
        return shape;
    }

    /**
     * <p>Setter for the field <code>shape</code>.</p>
     *
     * @param shape shape
     */
    public void setShape(AbstractShape shape) {
        this.shape = shape;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getCenterPosX() {
        return this.getCenterPos().x();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getCenterPosY() {
        return this.getCenterPos().y();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCenterPosX(float newCenterPosX) {
        this.getCenterPos().x = newCenterPosX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCenterPosY(float newCenterPosY) {
        this.getCenterPos().y = newCenterPosY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getWidth() {
        return this.getSize().x();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getHeight() {
        return this.getSize().y();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWidth(float newWidth) {
        this.getSize().x = newWidth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHeight(float newHeight) {
        this.getSize().y = newHeight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractEntity)) {
            return false;
        }
        AbstractEntity that = (AbstractEntity) o;
        return Objects.equals(getScene(), that.getScene()) &&
                Objects.equals(getCenterPos(), that.getCenterPos()) &&
                Objects.equals(getSize(), that.getSize()) &&
                Objects.equals(getShape(), that.getShape()) &&
                Objects.equals(getPicture(), that.getPicture());
    }
}
