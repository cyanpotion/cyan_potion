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

import com.xenoamess.cyan_potion.base.commons.areas.Area;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.coordinate.AbstractEntityScene;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;
import org.joml.Vector3f;

import java.util.Objects;

/**
 * @author XenoAmess
 */
public abstract class AbstractEntity implements Area {
    private final AbstractEntityScene scene;
    private Vector3f centerPos;
    private Vector3f size;
    private AbstractShape shape;
    private Picture picture = new Picture();


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
        this.getPicture().setBindable(bindable);
        this.getPicture().cover(this);
    }

    public Picture getPicture() {
        return this.picture;
    }

    public void draw(AbstractEntityScene scene) {
        this.getPicture().draw(scene);
    }

    public void register() {
        if (this.getShape() != null) {
            this.getShape().register();
        }
    }


    public AbstractEntityScene getScene() {
        return scene;
    }

    public Vector3f getCenterPos() {
        return centerPos;
    }

    public void setCenterPos(Vector3f centerPos) {
        this.centerPos = centerPos;
    }

    public Vector3f getSize() {
        return size;
    }

    public void setSize(Vector3f size) {
        this.size = size;
    }

    public AbstractShape getShape() {
        return shape;
    }

    public void setShape(AbstractShape shape) {
        this.shape = shape;
    }

    @Override
    public float getCenterPosX() {
        return this.getCenterPos().x();
    }

    @Override
    public float getCenterPosY() {
        return this.getCenterPos().y();
    }

    @Override
    public float getWidth() {
        return this.getSize().x();
    }

    @Override
    public float getHeight() {
        return this.getSize().y();
    }

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
