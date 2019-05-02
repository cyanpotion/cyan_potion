package com.xenoamess.cyan_potion.coordinate;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.gameWindowComponents.AbstractGameWindowComponent;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.render.Camera;
import com.xenoamess.cyan_potion.coordinate.entity.AbstractDynamicEntity;
import com.xenoamess.cyan_potion.coordinate.entity.StaticEntity;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author XenoAmess
 */
public abstract class AbstractScene extends AbstractGameWindowComponent {
    public static final int BOX_SIZE = 128;

    private Camera camera = new Camera(0, 0);
    private float scale;

    private Set<StaticEntity> staticEntitySet = new HashSet<>();
    private Set<AbstractDynamicEntity> dynamicEntitySet = new HashSet<>();

    private Map<ImmutablePair<Integer, Integer>, Set<AbstractShape>> boxToShapeMap = new HashMap<>();

    private Map<AbstractShape, Set<AbstractShape>> shapeCollisionSet =
            new HashMap<>();

    public AbstractScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    public void drawBindableAbsolute(Camera camera, float scale,
                                     Bindable bindable, float posx,
                                     float posy, float size) {
        this.drawBindableAbsolute(camera, scale, bindable, posx, posy, size,
                size);
    }

    public void drawBindableAbsolute(Camera camera, float scale,
                                     Bindable bindable, float posx,
                                     float posy, float size, Vector4f
            colorScale) {
        this.drawBindableAbsolute(camera, scale, bindable, posx, posy, size,
                size, colorScale);
    }

    public void drawBindableAbsolute(Camera camera, float scale,
                                     Bindable bindable, float posx,
                                     float posy, float width,
                                     float height) {
        this.drawBindableAbsolute(camera, scale, bindable, posx, posy, width,
                height, new Vector4f(1, 1, 1, 1));
    }

    public void drawBindableAbsolute(Camera camera, float scale,
                                     Bindable bindable, float posx,
                                     float posy, float width,
                                     float height, Vector4f colorScale) {
        this.getGameWindow().drawBindableRelative(bindable,
                (posx - camera.getPosition().x) * scale + this.getGameWindow().getLogicWindowWidth() / 2, (posy - camera.getPosition().y) * scale + this.getGameWindow().getLogicWindowHeight() / 2, width * scale, height * scale, colorScale);
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Set<StaticEntity> getStaticEntitySet() {
        return staticEntitySet;
    }

    public void setStaticEntitySet(Set<StaticEntity> staticEntitySet) {
        this.staticEntitySet = staticEntitySet;
    }

    public Set<AbstractDynamicEntity> getDynamicEntitySet() {
        return dynamicEntitySet;
    }

    public void setDynamicEntitySet(Set<AbstractDynamicEntity> dynamicEntitySet) {
        this.dynamicEntitySet = dynamicEntitySet;
    }

    public Map<ImmutablePair<Integer, Integer>, Set<AbstractShape>> getBoxToShapeMap() {
        return boxToShapeMap;
    }

    public void setBoxToShapeMap(Map<ImmutablePair<Integer, Integer>,
            Set<AbstractShape>> boxToShapeMap) {
        this.boxToShapeMap = boxToShapeMap;
    }

    public Map<AbstractShape, Set<AbstractShape>> getShapeCollisionSet() {
        return shapeCollisionSet;
    }

    public void setShapeCollisionSet(Map<AbstractShape, Set<AbstractShape>> shapeCollisionSet) {
        this.shapeCollisionSet = shapeCollisionSet;
    }
}
