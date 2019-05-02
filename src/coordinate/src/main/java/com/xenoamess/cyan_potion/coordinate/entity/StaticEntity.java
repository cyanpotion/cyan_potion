package com.xenoamess.cyan_potion.coordinate.entity;

import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.coordinate.AbstractScene;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;
import org.joml.Vector3f;

/**
 * @author XenoAmess
 */
public class StaticEntity extends AbstractEntity {
    private Bindable bindable;

    public StaticEntity(AbstractScene scene, Vector3f centerPos,
                        Vector3f size, Bindable bindable, AbstractShape shape) {
        super(scene, centerPos, size, bindable, shape);
    }

    @Override
    public Bindable getBindable() {
        return this.bindable;
    }

    @Override
    public void setBindable(Bindable bindable) {
        this.bindable = bindable;
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
