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

import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.coordinate.AbstractScene;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;
import org.joml.Vector3f;

/**
 * @author XenoAmess
 */
public abstract class AbstractEntity {
    private final AbstractScene scene;
    private Vector3f centerPos;
    private Vector3f size;
    private AbstractShape shape;

    public AbstractEntity(AbstractScene scene, Vector3f centerPos,
                          Vector3f size, Bindable bindable,
                          AbstractShape shape) {
        this.scene = scene;
        this.setCenterPos(new Vector3f(centerPos));
        this.setSize(new Vector3f(size));
        this.setBindable(bindable);
        this.setShape(shape);
        if (this.getShape() != null) {
            this.getShape().setEntity(this);
        }
    }

    public abstract Bindable getBindable();

    public abstract void setBindable(Bindable bindable);

    public void draw(AbstractScene scene) {
        scene.drawBindableAbsolute(scene.getCamera(), scene.getScale(),
                this.getBindable(), getCenterPos().x, getCenterPos().y,
                this.getSize().x, this.getSize().y);
    }

    public void register() {
        if (this.getShape() != null) {
            this.getShape().register();
        }
    }

    public AbstractScene getScene() {
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
}
