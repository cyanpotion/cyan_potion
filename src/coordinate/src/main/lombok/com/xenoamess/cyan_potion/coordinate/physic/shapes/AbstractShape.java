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


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.cyan_potion.base.areas.AbstractMutableArea;
import com.xenoamess.cyan_potion.coordinate.AbstractEntityScene;
import com.xenoamess.cyan_potion.coordinate.entity.AbstractEntity;
import com.xenoamess.cyan_potion.coordinate.physic.ShapeRelation;
import com.xenoamess.cyan_potion.coordinate.physic.shape_relation_judges.ShapeRelationJudge;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.xenoamess.cyan_potion.coordinate.physic.ShapeRelation.*;

/**
 * Shape is a shape of something.
 * shape is used for calculating collide or something.
 * every etity have a shape (if you want to use collide for it)
 *
 * @author XenoAmess
 * @version 0.162.0-SNAPSHOT
 */
@EqualsAndHashCode
@ToString
public abstract class AbstractShape implements AbstractMutableArea {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(AbstractShape.class);

    /**
     * Constant <code>STRING_RELATION="relation"</code>
     */
    public static final String STRING_RELATION = "relation";

    @Getter
    private static final Map<ImmutablePair<Class, Class>, ShapeRelationJudge>
            shapeRelationJudges = new ConcurrentHashMap<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    @Setter
    private AbstractEntity entity;

    @Getter
    @Setter
    private Vector3f centerPos;

    @Getter
    @Setter
    private Vector3f size;

    /**
     * <p>Constructor for AbstractShape.</p>
     *
     * @param entity    a {@link com.xenoamess.cyan_potion.coordinate.entity.AbstractEntity} object.
     * @param centerPos centerPos
     * @param size      a {@link org.joml.Vector3f} object.
     */
    public AbstractShape(AbstractEntity entity, Vector3f centerPos,
                         Vector3f size) {
        this.setEntity(entity);
        this.setCenterPos(new Vector3f(centerPos));
        this.setSize(new Vector3f(size));
    }

    /**
     * <p>Constructor for AbstractShape.</p>
     *
     * @param shape shape
     */
    public AbstractShape(AbstractShape shape) {
        this(shape.getEntity(), shape.getCenterPos(), shape.getSize());
    }

    /**
     * <p>relation.</p>
     *
     * @param k     shape.
     * @param v     another shape.
     * @param rough if rough.
     * @param <K>   shape class.
     * @param <V>   another shape class.
     * @return return
     */
    public static <K extends AbstractShape, V extends AbstractShape> ShapeRelation relation(K k, V v, boolean rough) {
        ShapeRelation res = RELATION_UNDEFINED;

        Method excludeMethod = null;
        try {
            excludeMethod = AbstractShape.class.getMethod(
                    STRING_RELATION, AbstractShape.class, boolean.class);
        } catch (NoSuchMethodException e) {
            //do nothing
        }

        Method method = null;
        try {
            method = k.getClass().getMethod(
                    STRING_RELATION, v.getClass(), boolean.class);
            if (method.equals(excludeMethod)) {
                method = null;
            }
        } catch (NoSuchMethodException e) {
            //do nothing
        }

        if (method != null) {
            try {
                res = (ShapeRelation) method.invoke(k, v, rough);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.error("AbstractShape.relation(K k, V v, boolean rough) fails:", e);
            }
        }
        if (res != RELATION_UNDEFINED) {
            return res;
        }

        try {
            method = v.getClass().getMethod(
                    STRING_RELATION, k.getClass(), boolean.class);
            if (method.equals(excludeMethod)) {
                method = null;
            }
        } catch (NoSuchMethodException e) {
            //do nothing
        }

        if (method != null) {
            try {
                res = (ShapeRelation) method.invoke(v, k, rough);
                if (res == RELATION_INNER) {
                    res = RELATION_OUTER;
                } else if (res == RELATION_OUTER) {
                    res = RELATION_INNER;
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.error("AbstractShape.relation(K k, V v, boolean rough) fails:", e);
            }
        }
        if (res != RELATION_UNDEFINED) {
            return res;
        }

        //if cannot find the relation function in it,then goto

        ShapeRelationJudge shapeComparator =
                getShapeRelationJudges().get(
                        new ImmutablePair(k.getClass(), v.getClass()));
        if (shapeComparator == null) {
            shapeComparator =
                    getShapeRelationJudges().get(
                            new ImmutablePair(v.getClass(), k.getClass()));
            if (shapeComparator == null) {
                return RELATION_UNDEFINED;
            } else {
                res = shapeComparator.relation(v, k, rough);
                if (res == RELATION_INNER) {
                    res = RELATION_OUTER;
                } else if (res == RELATION_OUTER) {
                    res = RELATION_INNER;
                }
                return res;
            }
        } else {
            return shapeComparator.relation(k, v, rough);
        }
    }

    /**
     * <p>relation.</p>
     *
     * @param shape: the other shape
     * @param rough: if true, then only return RELATION_UNDEFINED = -1,
     *               RELATION_NO_COLLIDE = 0,or RELATION_COLLIDE = 1;
     *               if false, then can return all the 6 status.
     * @return return the relationship between the two shapes.
     */
    public ShapeRelation relation(AbstractShape shape, boolean rough) {
        return relation(this, shape, rough);
    }

    /**
     * <p>minX.</p>
     *
     * @return a float.
     */
    public float minX() {
        return this.getCenterPos().x - this.getSize().x / 2;
    }

    /**
     * <p>maxX.</p>
     *
     * @return a float.
     */
    public float maxX() {
        return this.getCenterPos().x + this.getSize().x / 2;
    }

    /**
     * <p>minY.</p>
     *
     * @return a float.
     */
    public float minY() {
        return this.getCenterPos().y - this.getSize().y / 2;
    }

    /**
     * <p>maxY.</p>
     *
     * @return a float.
     */
    public float maxY() {
        return this.getCenterPos().y + this.getSize().y / 2;
    }

    /**
     * detect if a point is in the shape.
     *
     * @param point the point
     * @return true/false
     */
    public abstract boolean ifIn(Vector3f point);

    /**
     * <p>getBoxes.</p>
     *
     * @return boxes that the shape is in.
     * box size is 128*128.
     */
    public Set<ImmutablePair<Integer, Integer>> getBoxes() {
        Set<ImmutablePair<Integer, Integer>> res = new HashSet<>();
        int minBoxX = (int) Math.ceil(this.minX() / AbstractEntityScene.BOX_SIZE);
        int maxBoxX = (int) Math.ceil(this.maxX() / AbstractEntityScene.BOX_SIZE);
        int minBoxY = (int) Math.ceil(this.minY() / AbstractEntityScene.BOX_SIZE);
        int maxBoxY = (int) Math.ceil(this.maxY() / AbstractEntityScene.BOX_SIZE);
        for (int i = minBoxY; i <= maxBoxY; i++) {
            for (int j = minBoxX; j <= maxBoxX; j++) {
                res.add(new ImmutablePair<>(j, i));
            }
        }
        return res;
    }

    /**
     * <p>copy.</p>
     *
     * @param source source shape.
     * @param <T>    shape class.
     * @return copied shape
     */
    public static <T extends AbstractShape> T copy(T source) {
        T res;
        try {
            Constructor<? extends AbstractShape> constructor =
                    source.getClass().getDeclaredConstructor(source.getClass());
            res = (T) constructor.newInstance(source);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new NotImplementedException(e.getMessage());
        }
        return res;
    }

    /**
     * <p>copy.</p>
     *
     * @return return
     */
    public AbstractShape copy() {
        return AbstractShape.copy(this);
    }

    /**
     * register the shape into the scene;
     *
     * @return if succeed with no collisions then return true;
     * else (it collide with exist shape then) return false, but it will
     * still register.
     */
    public boolean register() {
        boolean res = true;
        Set<ImmutablePair<Integer, Integer>> newBoxes = this.getBoxes();
        for (ImmutablePair<Integer, Integer> au : newBoxes) {
            Set<AbstractShape> shapes =
                    this.getEntity().getScene().getBoxToShapeMap().get(au);
            if (shapes != null) {
                for (AbstractShape au2 : shapes) {
                    if (au2 == this) {
                        continue;
                    }
                    if (this.relation(au2, true) == RELATION_COLLIDE) {
                        res = false;
                    }
                }
            } else {
                shapes = new HashSet<>();
                this.getEntity().getScene().getBoxToShapeMap().put(au, shapes);
            }
            shapes.add(this);
        }
        return res;
    }

    /**
     * <p>canMove.</p>
     *
     * @param movementX a float.
     * @param movementY a float.
     * @return true: can move. false: cannot move.
     */
    public boolean canMove(float movementX, float movementY) {
        AbstractShape tmpCopy = this.copy();
        tmpCopy.forceMove(movementX, movementY);

        Set<AbstractShape> newCollisionSet = new HashSet<>();
        Set<AbstractShape> oldCollisionSet =
                this.getEntity().getScene().getShapeCollisionSet().get(this);
        if (oldCollisionSet == null) {
            oldCollisionSet = new HashSet<>();
        }

        Set<ImmutablePair<Integer, Integer>> newBoxes = tmpCopy.getBoxes();

        for (ImmutablePair<Integer, Integer> au : newBoxes) {
            Collection<AbstractShape> shapes =
                    this.getEntity().getScene().getBoxToShapeMap().get(au);
            if (shapes != null) {
                for (AbstractShape au2 : shapes) {
                    if (au2 == this) {
                        continue;
                    }
                    if (tmpCopy.relation(au2, true) == RELATION_COLLIDE) {
                        if (!oldCollisionSet.contains(au2)) {
                            return false;
                        }
                        oldCollisionSet.remove(au2);
                        newCollisionSet.add(au2);
                    }
                }
            }
        }

        this.getEntity().getScene().getShapeCollisionSet().put(this,
                newCollisionSet);
        for (AbstractShape shape : oldCollisionSet) {
            Set<AbstractShape> shapes =
                    this.getEntity().getScene().getShapeCollisionSet().get(shape);
            if (shapes != null) {
                shapes.remove(this);
            }
        }

        Set<ImmutablePair<Integer, Integer>> oldBoxes = this.getBoxes();
        for (ImmutablePair<Integer, Integer> au : newBoxes) {
            if (oldBoxes.contains(au)) {
                oldBoxes.remove(au);
            } else {
                Set<AbstractShape> shapes =
                        this.getEntity().getScene().getBoxToShapeMap().computeIfAbsent(au, k -> new HashSet<>());
                shapes.add(this);
            }
        }

        for (ImmutablePair<Integer, Integer> au : oldBoxes) {
            Set<AbstractShape> shapes =
                    this.getEntity().getScene().getBoxToShapeMap().get(au);
            if (shapes != null) {
                shapes.remove(this);
                if (shapes.isEmpty()) {
                    this.getEntity().getScene().getBoxToShapeMap().remove(au);
                }
            }
        }
        return true;
    }

    /**
     * <p>forceMove.</p>
     *
     * @param movementX a float.
     * @param movementY a float.
     */
    public void forceMove(float movementX, float movementY) {
        this.move(movementX, movementY);
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
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractShape)) {
            return false;
        }
        AbstractShape that = (AbstractShape) o;
        return getEntity() == that.getEntity() &&
                Objects.equals(getCenterPos(), that.getCenterPos()) &&
                Objects.equals(getSize(), that.getSize());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getCenterPosX() {
        return this.getCenterPos().x();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getCenterPosY() {
        return this.getCenterPos().y();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCenterPosX(float centerPosX) {
        this.getCenterPos().x = centerPosX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCenterPosY(float centerPosY) {
        this.getCenterPos().y = centerPosY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLeftTopPosX() {
        return this.getCenterPosX() - getWidth() / 2F;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLeftTopPosY() {
        return this.getCenterPosY() - getHeight() / 2F;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLeftTopPosX(float newLeftTopPosX) {
        this.setCenterPosX(newLeftTopPosX + getWidth() / 2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLeftTopPosY(float newLeftTopPosY) {
        this.setCenterPosY(newLeftTopPosY + getHeight() / 2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getWidth() {
        return this.getSize().x();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getHeight() {
        return this.getSize().y();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWidth(float newWidth) {
        this.getSize().x = newWidth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHeight(float newHeight) {
        this.getSize().y = newHeight;
    }

}
