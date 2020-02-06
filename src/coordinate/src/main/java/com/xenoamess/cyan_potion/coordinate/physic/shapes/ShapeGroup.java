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

package com.xenoamess.cyan_potion.coordinate.physic.shapes;

import com.xenoamess.cyan_potion.coordinate.entity.AbstractEntity;
import com.xenoamess.cyan_potion.coordinate.physic.ShapeRelation;
import org.apache.commons.collections4.CollectionUtils;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ShapeGroup is used as an AbstractEntity whose shape cannot be represented
 * as simple AbstractShape,thus as a group of shapes.
 *
 * @author XenoAmess
 * @version 0.155.3-SNAPSHOT
 */
public class ShapeGroup extends AbstractShape {
    private final List<AbstractShape> shapes = new ArrayList<>();

    /**
     * <p>Constructor for ShapeGroup.</p>
     *
     * @param entity    a {@link com.xenoamess.cyan_potion.coordinate.entity.AbstractEntity} object.
     * @param centerPos centerPos
     * @param size      a {@link org.joml.Vector3f} object.
     * @param shapes    a {@link java.util.Collection} object.
     */
    public ShapeGroup(AbstractEntity entity, Vector3f centerPos,
                      Vector3f size, Collection shapes) {
        super(entity, centerPos, size);
        this.getShapes().addAll(shapes);
    }

    /**
     * <p>Constructor for ShapeGroup.</p>
     *
     * @param shapeGroup shapeGroup
     */
    public ShapeGroup(ShapeGroup shapeGroup) {
        super(shapeGroup);
        for (AbstractShape au : shapeGroup.getShapes()) {
            this.getShapes().add(au.copy());
        }
    }


    /**
     * {@inheritDoc}
     * <p>
     * ShapeGroup can only accept rough as true.
     * for more information about rough, please go and see relation function
     * in class AbstractShape
     */
    @Override
    public ShapeRelation relation(AbstractShape shape, boolean rough) {
        rough = true;
        ShapeRelation res;
        for (AbstractShape au : getShapes()) {
            res = au.relation(shape, rough);
            if (res == ShapeRelation.RELATION_COLLIDE) {
                return ShapeRelation.RELATION_COLLIDE;
            }
        }
        return ShapeRelation.RELATION_NO_COLLIDE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float minX() {
        float minX = Float.MAX_VALUE;
        for (AbstractShape au : getShapes()) {
            minX = Math.min(minX, au.minX());
        }
        return minX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float maxX() {
        float maxX = Float.MIN_VALUE;
        for (AbstractShape au : getShapes()) {
            maxX = Math.max(maxX, au.maxX());
        }
        return maxX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float minY() {
        float minY = Float.MAX_VALUE;
        for (AbstractShape au : getShapes()) {
            minY = Math.min(minY, au.minY());
        }
        return minY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float maxY() {
        float maxY = Float.MIN_VALUE;
        for (AbstractShape au : getShapes()) {
            maxY = Math.max(maxY, au.maxY());
        }
        return maxY;
    }

    /**
     * {@inheritDoc}
     * <p>
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

    /**
     * <p>Getter for the field <code>shapes</code>.</p>
     *
     * @return return
     */
    public List<AbstractShape> getShapes() {
        return shapes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.getShapes().hashCode() + 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof ShapeGroup)) {
            return false;
        }
        return CollectionUtils.isEqualCollection(this.getShapes(), ((ShapeGroup) object).getShapes());
    }
}
