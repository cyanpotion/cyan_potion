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

package com.xenoamess.cyan_potion.coordinate.physic.shapes;

import com.xenoamess.cyan_potion.coordinate.entity.AbstractEntity;
import com.xenoamess.cyan_potion.coordinate.physic.ShapeRelation;
import org.joml.Vector3f;

/**
 * A circle.
 * Not an Oval. Do not use this class as an Oval.
 * although this.size.y is never used, please make sure it is equals this
 * .size.x logically.
 *
 * @author XenoAmess
 */
public class Circle extends AbstractShape {
    public Circle(AbstractEntity entity, Vector3f centerPos, Vector3f size) {
        super(entity, centerPos, size);
    }

    public Circle(Circle circle) {
        super(circle);
    }

    @Override
    public boolean ifIn(Vector3f point) {
        if (point.z != this.getCenterPos().z) {
            return false;
        }
        float tmpx = this.getCenterPos().x - point.x;
        float tmpy = this.getCenterPos().y - point.y;

        return !(tmpx * tmpx + tmpy * tmpy > this.getSize().x * this.getSize().x / 4);
    }

    public ShapeRelation relation(Circle target, boolean rough) {
        if (this.getCenterPos().z != target.getCenterPos().z) {
            return ShapeRelation.RELATION_NO_COLLIDE;
        }

        float tmpx = this.getCenterPos().x - target.getCenterPos().x;
        float tmpy = this.getCenterPos().y - target.getCenterPos().y;
        float tmpr = this.getSize().x / 2 + target.getSize().x / 2;
        if (tmpx * tmpx + tmpy * tmpy > tmpr * tmpr) {
            return ShapeRelation.RELATION_NO_COLLIDE;
        } else {
            if (rough) {
                return ShapeRelation.RELATION_COLLIDE;
            }

            if (this.getCenterPos().equals(target.getCenterPos()) && this.getSize().equals(target.getSize())) {
                return ShapeRelation.RELATION_EQUAL;
            } else {
                float thisMinX = this.minX();
                float thisMaxX = this.maxX();
                float thisMinY = this.minY();
                float thisMaxY = this.maxY();

                float thatMinX = target.minX();
                float thatMaxX = target.maxX();
                float thatMinY = target.minY();
                float thatMaxY = target.maxY();

                if ((thisMaxX <= thatMaxX) && (thisMaxY <= thatMaxY) && (thisMinX >= thatMinX) && (thisMinY >= thatMinY)) {
                    return ShapeRelation.RELATION_INNER;
                } else if ((thatMaxX <= thisMaxX) && (thatMaxY <= thisMaxY) && (thatMinX >= thisMinX) && (thatMinY >= thisMinY)) {
                    return ShapeRelation.RELATION_OUTER;
                }
            }
            return ShapeRelation.RELATION_COLLIDE;
        }
    }
}
