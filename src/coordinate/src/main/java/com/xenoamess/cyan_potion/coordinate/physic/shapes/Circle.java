package com.xenoamess.cyan_potion.coordinate.physic.shapes;

import com.xenoamess.cyan_potion.coordinate.entity.AbstractEntity;
import org.joml.Vector3f;

/**
 * A circle.
 * Not an Oval. Do not use this class as an Oval.
 * although this.size.y is never used, please make sure it is equals this.size.x logically.
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

        if (tmpx * tmpx + tmpy * tmpy > this.getSize().x * this.getSize().x / 4) {
            return false;
        } else {
            return true;
        }
    }

    public int relation(Circle target, boolean rough) {
        if (this.getCenterPos().z != target.getCenterPos().z) {
            return AbstractShape.RELATION_NO_COLLIDE;
        }

        float tmpx = this.getCenterPos().x - target.getCenterPos().x;
        float tmpy = this.getCenterPos().y - target.getCenterPos().y;
        float tmpr = this.getSize().x / 2 + target.getSize().x / 2;
        if (tmpx * tmpx + tmpy * tmpy > tmpr * tmpr) {
            return AbstractShape.RELATION_NO_COLLIDE;
        } else {
            if (rough) {
                return AbstractShape.RELATION_COLLIDE;
            }

            if (this.getCenterPos().equals(target.getCenterPos()) && this.getSize().equals(target.getSize())) {
                return AbstractShape.RELATION_EQUAL;
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
                    return AbstractShape.RELATION_INNER;
                } else if ((thatMaxX <= thisMaxX) && (thatMaxY <= thisMaxY) && (thatMinX >= thisMinX) && (thatMinY >= thisMinY)) {
                    return AbstractShape.RELATION_OUTER;
                }
            }
            return AbstractShape.RELATION_COLLIDE;
        }
    }
}
