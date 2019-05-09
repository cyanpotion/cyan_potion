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
 * @author XenoAmess
 */
public class HorizontalRectangle extends AbstractShape {
    public HorizontalRectangle(AbstractEntity entity, Vector3f centerPos,
                               Vector3f size) {
        super(entity, centerPos, size);
    }

    public HorizontalRectangle(HorizontalRectangle horizontalRectangle) {
        super(horizontalRectangle);
    }

    @Override
    public boolean ifIn(Vector3f point) {
        if (point.x < this.minX()) {
            return false;
        }
        if (point.x > this.maxX()) {
            return false;
        }
        if (point.y < this.minY()) {
            return false;
        }
        return point.y <= this.maxY();
    }

    public ShapeRelation relation(HorizontalRectangle target, boolean rough) {
        if (this.getCenterPos().z != target.getCenterPos().z) {
            return ShapeRelation.RELATION_NO_COLLIDE;
        }

        float thisMinX = this.minX();
        float thisMaxX = this.maxX();
        float thisMinY = this.minY();
        float thisMaxY = this.maxY();

        float thatMinX = target.minX();
        float thatMaxX = target.maxX();
        float thatMinY = target.minY();
        float thatMaxY = target.maxY();

        if ((thisMaxX < thatMinX) || (thisMaxY < thatMinY) || (thatMaxX < thisMinX) || (thatMaxY < thisMinY)) {
            return ShapeRelation.RELATION_NO_COLLIDE;
        } else {
            if (rough) {
                return ShapeRelation.RELATION_COLLIDE;
            }
            if (this.getCenterPos().equals(target.getCenterPos()) && this.getSize().equals(target.getSize())) {
                return ShapeRelation.RELATION_EQUAL;
            } else if ((thisMaxX <= thatMaxX) && (thisMaxY <= thatMaxY) && (thisMinX >= thatMinX) && (thisMinY >= thatMinY)) {
                return ShapeRelation.RELATION_INNER;
            } else if ((thatMaxX <= thisMaxX) && (thatMaxY <= thisMaxY) && (thatMinX >= thisMinX) && (thatMinY >= thisMinY)) {
                return ShapeRelation.RELATION_OUTER;
            }
            return ShapeRelation.RELATION_COLLIDE;
        }
    }

    public ShapeRelation relation(Circle target, boolean rough) {
        if (this.getCenterPos().z != target.getCenterPos().z) {
            return ShapeRelation.RELATION_NO_COLLIDE;
        }

        float thisMinX = this.minX();
        float thisMaxX = this.maxX();
        float thisMinY = this.minY();
        float thisMaxY = this.maxY();

        float tmpx = target.getCenterPos().x;
        float tmpy = target.getCenterPos().y;
        if (tmpx < thisMinX) {
            tmpx = thisMinX;
        } else if (tmpx > thisMaxX) {
            tmpx = thisMaxX;
        }

        if (tmpy < thisMinY) {
            tmpy = thisMinY;
        } else if (tmpy > thisMaxY) {
            tmpy = thisMaxY;
        }

        if (target.ifIn(new Vector3f(tmpx, tmpy, this.getCenterPos().z))) {
            return ShapeRelation.RELATION_NO_COLLIDE;
        } else {
            if (rough) {
                return ShapeRelation.RELATION_COLLIDE;
            }

            float thatMinX = target.minX();
            float thatMaxX = target.maxX();
            float thatMinY = target.minY();
            float thatMaxY = target.maxY();
            if (this.getCenterPos().equals(target.getCenterPos()) && this.getSize().equals(target.getSize())) {
                return ShapeRelation.RELATION_EQUAL;
            } else if ((thatMaxX <= thisMaxX) && (thatMaxY <= thisMaxY) && (thatMinX >= thisMinX) && (thatMinY >= thisMinY)) {
                return ShapeRelation.RELATION_OUTER;
            } else if (target.ifIn(new Vector3f(thisMinX, thisMinY,
                    this.getCenterPos().z)) && target.ifIn(new Vector3f(thisMaxX, thisMinY, this.getCenterPos().z)) && target.ifIn(new Vector3f(thisMinX, thisMaxY, this.getCenterPos().z)) && target.ifIn(new Vector3f(thisMaxX, thisMaxY, this.getCenterPos().z))) {
                return ShapeRelation.RELATION_INNER;
            }
            return ShapeRelation.RELATION_COLLIDE;
        }
    }
}
