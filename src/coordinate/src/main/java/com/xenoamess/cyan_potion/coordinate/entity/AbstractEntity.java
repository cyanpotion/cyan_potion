package com.xenoamess.cyan_potion.coordinate.entity;

import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.coordinate.AbstractScene;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;
import org.joml.Vector3f;

/**
 * @author XenoAmess
 */
public abstract class AbstractEntity {
    private final AbstractScene scene;
    private Vector3f centerPos;
    private Vector3f size;
    private AbstractShape shape;

    public AbstractEntity(AbstractScene scene, Vector3f centerPos,
                          Vector3f size, Bindable bindable,
                          AbstractShape shape) {
        this.scene = scene;
        this.setCenterPos(new Vector3f(centerPos));
        this.setSize(new Vector3f(size));
        this.setBindable(bindable);
        this.setShape(shape);
        if (this.getShape() != null) {
            this.getShape().setEntity(this);
        }
    }

    public abstract Bindable getBindable();

    public abstract void setBindable(Bindable bindable);

    public void draw(AbstractScene scene) {
        scene.drawBindableAbsolute(scene.getCamera(), scene.getScale(),
                this.getBindable(), getCenterPos().x, getCenterPos().y,
                this.getSize().x, this.getSize().y);
    }

    public void register() {
        if (this.getShape() != null) {
            this.getShape().register();
        }
    }

    public AbstractScene getScene() {
        return scene;
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

    public AbstractShape getShape() {
        return shape;
    }

    public void setShape(AbstractShape shape) {
        this.shape = shape;
    }
}
