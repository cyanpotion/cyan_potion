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
import com.xenoamess.cyan_potion.coordinate.AbstractEntityScene;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;
import org.joml.Vector3f;

import java.util.Objects;

/**
 * @author XenoAmess
 */
public abstract class AbstractDynamicEntity extends AbstractEntity {

    public AbstractDynamicEntity(AbstractEntityScene scene, Vector3f centerPos,
                                 Vector3f size, Bindable bindable,
                                 AbstractShape shape) {
        super(scene, centerPos, size, bindable, shape);
    }

    public abstract void update();

    public void forceMove(Vector3f direction) {
        this.setCenterPos(this.getCenterPos().add(direction));
        if (this.getShape() != null) {
            this.getShape().forceMove(direction);
        }
        this.getPicture().cover(this);
    }

    public boolean tryMove(Vector3f direction) {
        if (this.canMove(direction)) {
            this.forceMove(direction);
            return true;
        } else {
            return false;
        }
    }

    public boolean canMove(Vector3f direction) {
        if (this.getShape() == null) {
            return true;
        }
        return this.getShape().canMove(direction);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractDynamicEntity)) {
            return false;
        }
        AbstractDynamicEntity that = (AbstractDynamicEntity) o;
        return Objects.equals(getScene(), that.getScene()) &&
                Objects.equals(getCenterPos(), that.getCenterPos()) &&
                Objects.equals(getSize(), that.getSize()) &&
                Objects.equals(getShape(), that.getShape()) &&
                Objects.equals(getPicture(), that.getPicture());
    }

}
