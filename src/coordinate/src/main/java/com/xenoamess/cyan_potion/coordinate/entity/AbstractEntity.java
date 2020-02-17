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

package com.xenoamess.cyan_potion.coordinate.entity;

import com.xenoamess.cyan_potion.base.areas.AbstractMutableArea;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.visual.AbstractPictureInterface;
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.coordinate.AbstractEntityScene;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;

import java.util.Objects;

/**
 * Entity means some thing in your game, alive or not.
 * it can be a tree, a person, a player, or something.
 * it can have size, have shape, have picture.
 * But you can also make it dont't have them.
 * Entity just mean a basic Object in cyan_potion.coordinate
 * If a thing wanna use functions in cyan_potion.coordinate like collided detect, then it is common to make it an Entity.
 *
 * @author XenoAmess
 * @version 0.159.1-SNAPSHOT
 */
public abstract class AbstractEntity implements AbstractMutableArea {
    private final AbstractEntityScene scene;
    private float leftTopPosX;
    private float leftTopPosY;

    private int layer;

    private float width;
    private float height;

    private AbstractShape shape;
    private AbstractPictureInterface picture = new Picture();


    /**
     * <p>Constructor for AbstractEntity.</p>
     *
     * @param scene      a {@link com.xenoamess.cyan_potion.coordinate.AbstractEntityScene} object.
     * @param bindable   a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     * @param shape      a {@link com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape} object.
     * @param centerPosX a float.
     * @param centerPosY a float.
     * @param width      a float.
     * @param height     a float.
     * @param layer      a int.
     */
    public AbstractEntity(
            AbstractEntityScene scene,
            float centerPosX, float centerPosY,
            float width, float height,
            int layer,
            Bindable bindable,
            AbstractShape shape
    ) {
        this.scene = scene;
        this.width = width;
        this.height = height;
        this.setCenterPos(centerPosX, centerPosY);
        this.layer = layer;
        this.setShape(shape);
        if (this.getShape() != null) {
            this.getShape().setEntity(this);
        }
        this.setPicture(new Picture(bindable));
        this.getPicture().cover(this);
    }

    /**
     * <p>Getter for the field <code>picture</code>.</p>
     *
     * @param picture a {@link com.xenoamess.cyan_potion.base.visual.AbstractPicture} object.
     */
    public void setPicture(AbstractPictureInterface picture) {
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
    public AbstractPictureInterface getPicture() {
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
     * register this.shape into the scene if shape exist
     */
    public void registerShape() {
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

//    /**
//     * <p>Getter for the field <code>centerPos</code>.</p>
//     *
//     * @return return
//     */
//    public Vector3fc getCenterPos() {
//        return centerPos;
//    }
//
//    /**
//     * <p>Setter for the field <code>centerPos</code>.</p>
//     *
//     * @param centerPos centerPos
//     */
//    public void setCenterPos(Vector3f centerPos) {
//        this.centerPos.set(centerPos);
//    }
//
//    /**
//     * <p>Getter for the field <code>size</code>.</p>
//     *
//     * @return return
//     */
//    public Vector3fc getSize() {
//        return size;
//    }
//
//    /**
//     * <p>Setter for the field <code>size</code>.</p>
//     *
//     * @param size size
//     */
//    public void setSize(Vector3f size) {
//        this.size.set(size);
//    }

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
    public float getLeftTopPosX() {
        return this.leftTopPosX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLeftTopPosY() {
        return this.leftTopPosY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLeftTopPosX(float newLeftTopPosX) {
        this.leftTopPosX = newLeftTopPosX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLeftTopPosY(float newLeftTopPosY) {
        this.leftTopPosY = newLeftTopPosY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getWidth() {
        return this.width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getHeight() {
        return this.height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWidth(float newWidth) {
        this.width = newWidth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHeight(float newHeight) {
        this.height = newHeight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractEntity that = (AbstractEntity) o;
        return Float.compare(that.getLeftTopPosX(), getLeftTopPosX()) == 0 &&
                Float.compare(that.getLeftTopPosY(), getLeftTopPosY()) == 0 &&
                Float.compare(that.getWidth(), getWidth()) == 0 &&
                Float.compare(that.getHeight(), getHeight()) == 0 &&
                Objects.equals(getScene(), that.getScene()) &&
                Objects.equals(getShape(), that.getShape()) &&
                Objects.equals(getPicture(), that.getPicture());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getScene(), getLeftTopPosX(), getLeftTopPosY(), getWidth(), getHeight(), getShape(), getPicture());
    }

    /**
     * <p>Getter for the field <code>layer</code>.</p>
     *
     * @return a int.
     */
    public int getLayer() {
        return layer;
    }

    /**
     * <p>Setter for the field <code>layer</code>.</p>
     *
     * @param layer a int.
     */
    public void setLayer(int layer) {
        this.layer = layer;
    }
}
