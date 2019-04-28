package com.xenoamess.cyan_potion.coordinate.physic.shapes;

import com.xenoamess.cyan_potion.coordinate.AbstractScene;
import com.xenoamess.cyan_potion.coordinate.entity.AbstractEntity;
import com.xenoamess.cyan_potion.coordinate.physic.shapeComparators.ShapeRelationJudger;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.joml.Vector3f;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author XenoAmess
 */
public abstract class AbstractShape {
    public static final int RELATION_UNDEFINED = -1;
    public static final int RELATION_NO_COLLIDE = 0;
    public static final int RELATION_COLLIDE = 1;
    public static final int RELATION_EQUAL = 2;
    public static final int RELATION_INNER = 3;
    public static final int RELATION_OUTER = 4;

    private AbstractEntity entity;
    private Vector3f centerPos;
    private Vector3f size;


    private static Map<ImmutablePair<Class, Class>, ShapeRelationJudger> ShapeRelationJudgers = new HashMap<>();

    public AbstractShape(AbstractEntity entity, Vector3f centerPos, Vector3f size) {
        this.setEntity(entity);
        this.setCenterPos(new Vector3f(centerPos));
        this.setSize(new Vector3f(size));
    }

    public AbstractShape(AbstractShape shape) {
        this(shape.getEntity(), shape.getCenterPos(), shape.getSize());
    }

    public static <K extends AbstractShape, V extends AbstractShape> int relation(K k, V v, boolean rough) {
        int res = RELATION_UNDEFINED;
        Method method = null;
        Method excludeMethod = null;
        try {
            excludeMethod = AbstractShape.class.getMethod("relation", AbstractShape.class, boolean.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        try {
            method = k.getClass().getMethod("relation", v.getClass(), boolean.class);
            if (method.equals(excludeMethod)) {
                method = null;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (method != null) {
            try {
                res = (int) method.invoke(k, v, rough);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (res != RELATION_UNDEFINED) {
            return res;
        }

        try {
            method = v.getClass().getMethod("relation", k.getClass(), boolean.class);
            if (method.equals(excludeMethod)) {
                method = null;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (method != null) {
            try {
                res = (int) method.invoke(v, k, rough);
                if (res == AbstractShape.RELATION_INNER) {
                    res = AbstractShape.RELATION_OUTER;
                } else if (res == AbstractShape.RELATION_OUTER) {
                    res = RELATION_INNER;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (res != RELATION_UNDEFINED) {
            return res;
        }

        //if cannot find the relation function in it,then goto

        ShapeRelationJudger shapeComparator = getShapeRelationJudgers().get(new ImmutablePair(k.getClass(), v.getClass()));
        if (shapeComparator == null) {
            shapeComparator = getShapeRelationJudgers().get(new ImmutablePair(v.getClass(), k.getClass()));
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
     * @param shape: the other shape
     * @param rough: if true, then only return RELATION_UNDEFINED = -1,RELATION_NO_COLLIDE = 0,or RELATION_COLLIDE = 1;
     *               if false, then can return all the 6 status.
     * @return return the relationship between the two shapes.
     */
    public int relation(AbstractShape shape, boolean rough) {
        return relation(this, shape, rough);
    }

    public float minX() {
        return this.getCenterPos().x - this.getSize().x / 2;
    }

    public float maxX() {
        return this.getCenterPos().x + this.getSize().x / 2;
    }

    public float minY() {
        return this.getCenterPos().y - this.getSize().y / 2;
    }

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
     * @return boxes that the shape is in.
     * box size is 128*128.
     */
    public Set<ImmutablePair<Integer, Integer>> getBoxes() {
        Set<ImmutablePair<Integer, Integer>> res = new HashSet<>();
        int minBoxX = (int) Math.ceil(this.minX() / AbstractScene.BOX_SIZE);
        int maxBoxX = (int) Math.ceil(this.maxX() / AbstractScene.BOX_SIZE);
        int minBoxY = (int) Math.ceil(this.minY() / AbstractScene.BOX_SIZE);
        int maxBoxY = (int) Math.ceil(this.maxY() / AbstractScene.BOX_SIZE);
        for (int i = minBoxY; i <= maxBoxY; i++) {
            for (int j = minBoxX; j <= maxBoxX; j++) {
                res.add(new ImmutablePair<>(j, i));
            }
        }
        return res;
    }

    public static <T extends AbstractShape> T copy(T source) {
        T res = null;
        try {
            Constructor<? extends AbstractShape> constructor = source.getClass().getDeclaredConstructor(source.getClass());
            res = (T) constructor.newInstance(source);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return res;
    }

    public AbstractShape copy() {
        return AbstractShape.copy(this);
    }

    /**
     * register the shape into the scene;
     *
     * @return if succeed with no collisions then return true;
     * else (it collide with exist shape then) return false, but it will still register.
     */
    public boolean register() {
        boolean res = true;
        Set<ImmutablePair<Integer, Integer>> newBoxes = this.getBoxes();
        for (ImmutablePair<Integer, Integer> au : newBoxes) {
            Set<AbstractShape> shapes = this.getEntity().getScene().getBoxToShapeMap().get(au);
            if (shapes != null) {
                for (AbstractShape au2 : shapes) {
                    if (au2 == this) {
                        continue;
                    }
                    if (this.relation(au2, true) == AbstractShape.RELATION_COLLIDE) {
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
     * @param direction the direction the moves to
     * @return true: can move. false: cannot move.
     */
    public boolean canMove(Vector3f direction) {
        AbstractShape tmpCopy = this.copy();
        tmpCopy.forceMove(direction);

        Set<AbstractShape> newCollisionSet = new HashSet<>();
        Set<AbstractShape> oldCollisionSet = this.getEntity().getScene().getShapeCollisionSet().get(this);
        if (oldCollisionSet == null) {
            oldCollisionSet = new HashSet<>();
        }

        Set<ImmutablePair<Integer, Integer>> newBoxes = tmpCopy.getBoxes();

        for (ImmutablePair<Integer, Integer> au : newBoxes) {
            Collection<AbstractShape> shapes = this.getEntity().getScene().getBoxToShapeMap().get(au);
            if (shapes != null) {
                for (AbstractShape au2 : shapes) {
                    if (au2 == this) {
                        continue;
                    }
                    if (tmpCopy.relation(au2, true) == AbstractShape.RELATION_COLLIDE) {
                        if (!oldCollisionSet.contains(au2)) {
                            return false;
                        }
                        oldCollisionSet.remove(au2);
                        newCollisionSet.add(au2);
                    }
                }
            }
        }

        this.getEntity().getScene().getShapeCollisionSet().put(this, newCollisionSet);
        for (AbstractShape shape : oldCollisionSet) {
            Set<AbstractShape> shapes = this.getEntity().getScene().getShapeCollisionSet().get(shape);
            if (shapes != null) {
                shapes.remove(this);
            }
        }

        Set<ImmutablePair<Integer, Integer>> oldBoxes = this.getBoxes();
        for (ImmutablePair<Integer, Integer> au : newBoxes) {
            if (oldBoxes.contains(au)) {
                oldBoxes.remove(au);
            } else {
                Set<AbstractShape> shapes = this.getEntity().getScene().getBoxToShapeMap().get(au);
                if (shapes == null) {
                    shapes = new HashSet<>();
                    this.getEntity().getScene().getBoxToShapeMap().put(au, shapes);
                }
                shapes.add(this);
            }
        }

        for (ImmutablePair<Integer, Integer> au : oldBoxes) {
            Set<AbstractShape> shapes = this.getEntity().getScene().getBoxToShapeMap().get(au);
            if (shapes != null) {
                shapes.remove(this);
                if (shapes.isEmpty()) {
                    this.getEntity().getScene().getBoxToShapeMap().remove(au);
                }
            }
        }
        return true;
    }

    public void forceMove(Vector3f direction) {
        this.setCenterPos(this.getCenterPos().add(direction));
    }

    public boolean tryMove(Vector3f direction) {
        if (this.canMove(direction)) {
            this.forceMove(direction);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object);
    }

    public AbstractEntity getEntity() {
        return entity;
    }

    public void setEntity(AbstractEntity entity) {
        this.entity = entity;
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

    public static Map<ImmutablePair<Class, Class>, ShapeRelationJudger> getShapeRelationJudgers() {
        return ShapeRelationJudgers;
    }

    public static void setShapeRelationJudgers(Map<ImmutablePair<Class, Class>, ShapeRelationJudger> shapeRelationJudgers) {
        ShapeRelationJudgers = shapeRelationJudgers;
    }
}
