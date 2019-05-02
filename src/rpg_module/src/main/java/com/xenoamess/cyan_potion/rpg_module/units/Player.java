package com.xenoamess.cyan_potion.rpg_module.units;

import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.coordinate.AbstractScene;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.HorizontalRectangle;
import org.joml.Vector3f;

/**
 * @author XenoAmess
 */
public class Player extends Unit {
    public Player(AbstractScene scene, Vector3f centerPos, Vector3f size,
                  Bindable bindable, AbstractShape shape) {
        super(scene, centerPos, size, bindable, shape);
    }

    public Player(AbstractScene scene, Vector3f centerPos, Vector3f size,
                  Bindable bindable) {
        super(scene, centerPos, size, bindable, new HorizontalRectangle(null,
                centerPos, size));
    }

    public Player(AbstractScene scene, Vector3f centerPos, Vector3f size,
                  String walkingAnimation4DirsURI,
                  ResourceManager resourceManager) {
        super(scene, centerPos, size, walkingAnimation4DirsURI,
                resourceManager);
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
