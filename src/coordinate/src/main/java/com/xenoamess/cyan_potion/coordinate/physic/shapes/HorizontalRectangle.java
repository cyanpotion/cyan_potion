package com.xenoamess.cyan_potion.coordinate.physic.shapes;

import com.xenoamess.cyan_potion.coordinate.entity.AbstractEntity;
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
        if (point.y > this.maxY()) {
            return false;
        }
        return true;
    }

    public int relation(HorizontalRectangle target, boolean rough) {
        if (this.getCenterPos().z != target.getCenterPos().z) {
            return AbstractShape.RELATION_NO_COLLIDE;
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
            return AbstractShape.RELATION_NO_COLLIDE;
        } else {
            if (rough) {
                return AbstractShape.RELATION_COLLIDE;
            }
            if (this.getCenterPos().equals(target.getCenterPos()) && this.getSize().equals(target.getSize())) {
                return AbstractShape.RELATION_EQUAL;
            } else if ((thisMaxX <= thatMaxX) && (thisMaxY <= thatMaxY) && (thisMinX >= thatMinX) && (thisMinY >= thatMinY)) {
                return AbstractShape.RELATION_INNER;
            } else if ((thatMaxX <= thisMaxX) && (thatMaxY <= thisMaxY) && (thatMinX >= thisMinX) && (thatMinY >= thisMinY)) {
                return AbstractShape.RELATION_OUTER;
            }
            return AbstractShape.RELATION_COLLIDE;
        }
    }

    public int relation(Circle target, boolean rough) {
        if (this.getCenterPos().z != target.getCenterPos().z) {
            return AbstractShape.RELATION_NO_COLLIDE;
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

//        tmpx -= target.centerPos.x;
//        tmpy -= target.centerPos.y;

        if (target.ifIn(new Vector3f(tmpx, tmpy, this.getCenterPos().z))) {
            return AbstractShape.RELATION_NO_COLLIDE;
        } else {
            if (rough) {
                return AbstractShape.RELATION_COLLIDE;
            }

            float thatMinX = target.minX();
            float thatMaxX = target.maxX();
            float thatMinY = target.minY();
            float thatMaxY = target.maxY();
            if (this.getCenterPos().equals(target.getCenterPos()) && this.getSize().equals(target.getSize())) {
                return AbstractShape.RELATION_EQUAL;
            } else if ((thatMaxX <= thisMaxX) && (thatMaxY <= thisMaxY) && (thatMinX >= thisMinX) && (thatMinY >= thisMinY)) {
                return AbstractShape.RELATION_OUTER;
            } else if (target.ifIn(new Vector3f(thisMinX, thisMinY,
                    this.getCenterPos().z)) && target.ifIn(new Vector3f(thisMaxX, thisMinY, this.getCenterPos().z)) && target.ifIn(new Vector3f(thisMinX, thisMaxY, this.getCenterPos().z)) && target.ifIn(new Vector3f(thisMaxX, thisMaxY, this.getCenterPos().z))) {
                return AbstractShape.RELATION_INNER;
            }
            return AbstractShape.RELATION_COLLIDE;
        }
    }
}
