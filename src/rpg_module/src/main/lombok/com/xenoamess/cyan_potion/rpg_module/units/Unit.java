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

package com.xenoamess.cyan_potion.rpg_module.units;

import com.xenoamess.cyan_potion.base.math.FrameFloat;
import com.xenoamess.cyan_potion.base.memory.ResourceInfo;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.coordinate.AbstractEntityScene;
import com.xenoamess.cyan_potion.coordinate.entity.AbstractDynamicEntity;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.HorizontalRectangle;
import com.xenoamess.cyan_potion.rpg_module.render.WalkingAnimation4Dirs;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>Unit class.</p>
 *
 * @author XenoAmess
 * @version 0.161.0
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class Unit extends AbstractDynamicEntity {
    /**
     * Constant <code>DEFAULT_UNIT_LAYER=100</code>
     */
    public static final int DEFAULT_UNIT_LAYER = 100;
    private static final float DEFAULT_UNIT_SPEED = 100F;

    private final AtomicBoolean moving = new AtomicBoolean(false);

    private final AtomicBoolean canMove = new AtomicBoolean(true);

    @Getter
    @Setter
    private float movementX;

    @Getter
    @Setter
    private float movementY;

    @Getter
    private final FrameFloat moveSpeed;

    @Getter
    @Setter
    private int faceDir = 180;

    /**
     * <p>Constructor for Unit.</p>
     *
     * @param scene      a {@link com.xenoamess.cyan_potion.coordinate.AbstractEntityScene} object.
     * @param bindable   a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     * @param shape      a {@link com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape} object.
     * @param centerPosX a float.
     * @param centerPosY a float.
     * @param width      a float.
     * @param height     a float.
     * @param layer      a int.
     */
    public Unit(
            AbstractEntityScene scene,
            float centerPosX, float centerPosY,
            float width, float height,
            int layer,
            Bindable bindable,
            AbstractShape shape) {
        super(
                scene,
                centerPosX, centerPosY,
                width, height,
                layer,
                bindable, shape
        );
        this.moveSpeed = new FrameFloat(scene.getGameWindow().getGameManager(), DEFAULT_UNIT_SPEED);
    }

    /**
     * <p>Constructor for Unit.</p>
     *
     * @param scene      a {@link com.xenoamess.cyan_potion.coordinate.AbstractEntityScene} object.
     * @param bindable   a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     * @param centerPosX a float.
     * @param centerPosY a float.
     * @param width      a float.
     * @param height     a float.
     * @param layer      a int.
     */
    public Unit(
            AbstractEntityScene scene,
            float centerPosX, float centerPosY,
            float width, float height,
            int layer,
            Bindable bindable) {
        this(
                scene,
                centerPosX, centerPosY,
                width, height,
                layer,
                bindable,
                new HorizontalRectangle(
                        null,
                        new Vector3f(centerPosX, centerPosY, layer),
                        new Vector3f(width, height, 0)
                )
        );
    }

    /**
     * <p>Constructor for Unit.</p>
     *
     * @param scene                             a {@link com.xenoamess.cyan_potion.coordinate.AbstractEntityScene} object.
     * @param walkingAnimation4DirsResourceInfo walkingAnimation4DirsResourceInfo
     * @param resourceManager                   a {@link com.xenoamess.cyan_potion.base.memory.ResourceManager} object.
     * @param centerPosX                        a float.
     * @param centerPosY                        a float.
     * @param width                             a float.
     * @param height                            a float.
     * @param layer                             a int.
     */
    public Unit(
            AbstractEntityScene scene,
            float centerPosX, float centerPosY,
            float width, float height,
            int layer,
            ResourceInfo walkingAnimation4DirsResourceInfo,
            ResourceManager resourceManager) {
        this(scene,
                centerPosX, centerPosY,
                width, height,
                layer,
                null);
        this.loadWalkingAnimations(walkingAnimation4DirsResourceInfo, resourceManager);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        if (!this.isCanMove()) {
            this.setMovement(0, 0);
        }
        if (getMovementX() != 0 || getMovementY() != 0) {
            setMoving(true);
            setFaceDir(getFaceDir(getMovementX(), getMovementY()));

            if (this.getPicture() instanceof WalkingAnimation4Dirs) {
                ((WalkingAnimation4Dirs) this.getPicture()).setFaceDir(getFaceDir());
            }
            float moveLength = new Vector2f(getMovementX(), getMovementY()).length();
            if (moveLength > getMoveSpeed().getValue()) {
                this.tryMove(
                        getMovementX() * getMoveSpeed().getValue() / moveLength,
                        getMovementY() * getMoveSpeed().getValue() / moveLength
                );
            } else {
                this.tryMove(getMovementX(), getMovementY());
            }
        } else {
            setMoving(false);
        }
    }

    static int getFaceDir(double x, double y) {
        double res = -Math.atan2(x, y) / Math.PI / 2 * 360 + 180;
        return (int) res;
    }

    /**
     * <p>loadWalkingAnimations.</p>
     *
     * @param walkingAnimation4DirsResourceInfo walkingAnimation4DirsResourceInfo
     * @param resourceManager                   resourceManager
     */
    public void loadWalkingAnimations(ResourceInfo walkingAnimation4DirsResourceInfo,
                                      ResourceManager resourceManager) {
        this.setPicture(new WalkingAnimation4Dirs(4, this,
                walkingAnimation4DirsResourceInfo, resourceManager));
    }


    /**
     * <p>setMovement.</p>
     *
     * @param movementX a float.
     * @param movementY a float.
     */
    public void setMovement(float movementX, float movementY) {
        this.setMovementX(movementX);
        this.setMovementY(movementY);
    }

    /**
     * <p>isMoving.</p>
     *
     * @return a boolean.
     */
    public boolean isMoving() {
        return moving.get();
    }

    /**
     * <p>Setter for the field <code>moving</code>.</p>
     *
     * @param moving a boolean.
     */
    public void setMoving(boolean moving) {
        this.moving.set(moving);
    }

    /**
     * <p>isCanMove.</p>
     *
     * @return a boolean.
     */
    public boolean isCanMove() {
        return canMove.get();
    }

    /**
     * <p>Setter for the field <code>canMove</code>.</p>
     *
     * @param canMove a boolean.
     */
    public void setCanMove(boolean canMove) {
        this.canMove.set(canMove);
    }
}

