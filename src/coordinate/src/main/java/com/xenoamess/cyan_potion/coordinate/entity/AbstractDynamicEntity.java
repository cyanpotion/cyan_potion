package com.xenoamess.cyan_potion.coordinate.entity;

import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.coordinate.AbstractScene;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;
import org.joml.Vector3f;

/**
 * @author XenoAmess
 */
public abstract class AbstractDynamicEntity extends AbstractEntity {

    public AbstractDynamicEntity(AbstractScene scene, Vector3f centerPos, Vector3f size, Bindable bindable, AbstractShape shape) {
        super(scene, centerPos, size, bindable, shape);
    }

    public abstract void update();

    public void forceMove(Vector3f direction) {
        this.setCenterPos(this.getCenterPos().add(direction));
        if (this.getShape() != null) {
            this.getShape().forceMove(direction);
        }
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
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object);
    }

}
