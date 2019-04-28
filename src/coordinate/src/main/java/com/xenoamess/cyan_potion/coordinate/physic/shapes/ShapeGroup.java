package com.xenoamess.cyan_potion.coordinate.physic.shapes;

import com.xenoamess.cyan_potion.coordinate.entity.AbstractEntity;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ShapeGroup is used as an AbstractEntity whose shape cannot be represented as simple AbstractShape,thus as a group of shapes.
 *
 * @author XenoAmess
 */
public class ShapeGroup extends AbstractShape {
    private final List<AbstractShape> shapes = new ArrayList<>();

    public ShapeGroup(AbstractEntity entity, Vector3f centerPos, Vector3f size, Collection shapes) {
        super(entity, centerPos, size);
        shapes.addAll(shapes);
    }

    public ShapeGroup(ShapeGroup shapeGroup) {
        super(shapeGroup);
        for (AbstractShape au : shapeGroup.getShapes()) {
            this.getShapes().add(au.copy());
        }
    }


    /**
     * ShapeGroup can only accept rough as true.
     * for more information about rough, please go and see relation function in class AbstractShape
     */
    @Override
    public int relation(AbstractShape shape, boolean rough) {
        rough = true;
        int res;
        for (AbstractShape au : getShapes()) {
            res = au.relation(shape, rough);
            if (res == AbstractShape.RELATION_COLLIDE) {
                return AbstractShape.RELATION_COLLIDE;
            }
        }
        return AbstractShape.RELATION_NO_COLLIDE;
    }

    @Override
    public float minX() {
        float minX = Float.MAX_VALUE;
        for (AbstractShape au : getShapes()) {
            minX = Math.min(minX, au.minX());
        }
        return minX;
    }

    @Override
    public float maxX() {
        float maxX = Float.MIN_VALUE;
        for (AbstractShape au : getShapes()) {
            maxX = Math.max(maxX, au.maxX());
        }
        return maxX;
    }

    @Override
    public float minY() {
        float minY = Float.MAX_VALUE;
        for (AbstractShape au : getShapes()) {
            minY = Math.min(minY, au.minY());
        }
        return minY;
    }

    @Override
    public float maxY() {
        float maxY = Float.MIN_VALUE;
        for (AbstractShape au : getShapes()) {
            maxY = Math.max(maxY, au.maxY());
        }
        return maxY;
    }

    /**
     * detect if a point is in the shape.
     */
    @Override
    public boolean ifIn(Vector3f point) {
        for (AbstractShape au : getShapes()) {
            if (au.ifIn(point)) {
                return true;
            }
        }
        return false;
    }

    public List<AbstractShape> getShapes() {
        return shapes;
    }
}
