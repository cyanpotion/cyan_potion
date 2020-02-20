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

import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.coordinate.AbstractEntityScene;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;

import java.util.Objects;

/**
 * <p>Abstract AbstractDynamicEntity class.</p>
 *
 * @author XenoAmess
 * @version 0.160.0-SNAPSHOT
 */
public abstract class AbstractDynamicEntity extends AbstractEntity {

    /**
     * <p>Constructor for AbstractDynamicEntity.</p>
     *
     * @param scene      a {@link com.xenoamess.cyan_potion.coordinate.AbstractEntityScene} object.
     * @param centerPosX a float.
     * @param centerPosY a float.
     * @param width      a float.
     * @param height     a float.
     * @param layer      a int.
     * @param bindable   a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     * @param shape      a {@link com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape} object.
     */
    public AbstractDynamicEntity(
            AbstractEntityScene scene,
            float centerPosX, float centerPosY,
            float width, float height,
            int layer,
            Bindable bindable,
            AbstractShape shape) {
        super(scene,
                centerPosX, centerPosY,
                width, height,
                layer,
                bindable,
                shape
        );
    }

    /**
     * <p>update.</p>
     */
    public abstract void update();

    /**
     * <p>forceMove.</p>
     *
     * @param movementX a float.
     * @param movementY a float.
     */
    public void forceMove(float movementX, float movementY) {
        this.move(movementX, movementY);
        if (this.getShape() != null) {
            this.getShape().forceMove(movementX, movementY);
        }
        this.getPicture().cover(this);
    }

    /**
     * <p>tryMove.</p>
     *
     * @param movementX a float.
     * @param movementY a float.
     * @return a boolean.
     */
    public boolean tryMove(float movementX, float movementY) {
        if (this.canMove(movementX, movementY)) {
            this.forceMove(movementX, movementY);
            return true;
        } else {
            return false;
        }
    }

    /**
     * <p>canMove.</p>
     *
     * @param movementX a float.
     * @param movementY a float.
     * @return a boolean.
     */
    public boolean canMove(float movementX, float movementY) {
        if (this.getShape() == null) {
            return true;
        }
        return this.getShape().canMove(movementX, movementY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractDynamicEntity that = (AbstractDynamicEntity) o;
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
}
