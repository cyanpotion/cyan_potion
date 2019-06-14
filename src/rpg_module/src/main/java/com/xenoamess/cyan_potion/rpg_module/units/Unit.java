/*
 * MIT License
 *
 * Copyright (c) 2019 XenoAmess
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

import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.coordinate.AbstractEntityScene;
import com.xenoamess.cyan_potion.coordinate.entity.AbstractDynamicEntity;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.HorizontalRectangle;
import com.xenoamess.cyan_potion.rpg_module.render.WalkingAnimation4Dirs;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Objects;

/**
 * <p>Unit class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class Unit extends AbstractDynamicEntity {
    /**
     * Constant <code>DEFAULT_UNIT_LAYER=100</code>
     */
    public static final int DEFAULT_UNIT_LAYER = 100;

    private boolean moving = false;
    private Vector2f movement = new Vector2f();
    private float moveSpeed = 3f;
    private int faceDir = 180;
    private boolean canMove = true;

    /**
     * <p>Constructor for Unit.</p>
     *
     * @param scene     a {@link com.xenoamess.cyan_potion.coordinate.AbstractEntityScene} object.
     * @param centerPos centerPos
     * @param size      a {@link org.joml.Vector3f} object.
     * @param bindable  a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     * @param shape     a {@link com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape} object.
     */
    public Unit(AbstractEntityScene scene, Vector3f centerPos, Vector3f size,
                Bindable bindable, AbstractShape shape) {
        super(scene, centerPos, size, bindable, shape);
    }

    /**
     * <p>Constructor for Unit.</p>
     *
     * @param scene     a {@link com.xenoamess.cyan_potion.coordinate.AbstractEntityScene} object.
     * @param centerPos centerPos
     * @param size      a {@link org.joml.Vector3f} object.
     * @param bindable  a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     */
    public Unit(AbstractEntityScene scene, Vector3f centerPos, Vector3f size,
                Bindable bindable) {
        this(scene, centerPos, size, bindable, new HorizontalRectangle(null,
                centerPos, size));
    }

    /**
     * <p>Constructor for Unit.</p>
     *
     * @param scene                    a {@link com.xenoamess.cyan_potion.coordinate.AbstractEntityScene} object.
     * @param centerPos                a {@link org.joml.Vector3f} object.
     * @param size                     a {@link org.joml.Vector3f} object.
     * @param walkingAnimation4DirsURI walkingAnimation4DirsURI
     * @param resourceManager          a {@link com.xenoamess.cyan_potion.base.memory.ResourceManager} object.
     */
    public Unit(AbstractEntityScene scene, Vector3f centerPos, Vector3f size,
                String walkingAnimation4DirsURI,
                ResourceManager resourceManager) {
        this(scene, centerPos, size, null);
        this.loadWalkingAnimations(walkingAnimation4DirsURI, resourceManager);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Unit)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Unit unit = (Unit) o;
        return isMoving() == unit.isMoving() &&
                Float.compare(unit.getMoveSpeed(), getMoveSpeed()) == 0 &&
                getFaceDir() == unit.getFaceDir() &&
                isCanMove() == unit.isCanMove() &&
                Objects.equals(getMovement(), unit.getMovement());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        if (!this.isCanMove()) {
            this.getMovement().set(0, 0);
        }
        if (getMovement().x != 0 || getMovement().y != 0) {
            setMoving(true);
            setFaceDir(getFaceDir(getMovement().x, getMovement().y));

            if (this.getPicture().getBindable() instanceof WalkingAnimation4Dirs) {
                ((WalkingAnimation4Dirs) this.getPicture().getBindable()).setFaceDir(getFaceDir());
            }

            if (getMovement().length() > getMoveSpeed()) {
                this.tryMove(new Vector3f(
                                getMovement().mul(getMoveSpeed() / getMovement().length(), new Vector2f()),
                                0
                        )
                );
            } else {
                this.tryMove(new Vector3f(getMovement(), 0));
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
     * @param walkingAnimation4DirsURI walkingAnimation4DirsURI
     * @param resourceManager          a {@link com.xenoamess.cyan_potion.base.memory.ResourceManager} object.
     */
    public void loadWalkingAnimations(String walkingAnimation4DirsURI,
                                      ResourceManager resourceManager) {
        this.getPicture().setBindable(new WalkingAnimation4Dirs(4, this,
                walkingAnimation4DirsURI, resourceManager));
    }


    /**
     * <p>isMoving.</p>
     *
     * @return a boolean.
     */
    public boolean isMoving() {
        return moving;
    }

    /**
     * <p>Setter for the field <code>moving</code>.</p>
     *
     * @param moving a boolean.
     */
    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    /**
     * <p>Getter for the field <code>movement</code>.</p>
     *
     * @return return
     */
    public Vector2f getMovement() {
        return movement;
    }

    /**
     * <p>Setter for the field <code>movement</code>.</p>
     *
     * @param movement movement
     */
    public void setMovement(Vector2f movement) {
        this.movement = movement;
    }

    /**
     * <p>Getter for the field <code>moveSpeed</code>.</p>
     *
     * @return a float.
     */
    public float getMoveSpeed() {
        return moveSpeed;
    }

    /**
     * <p>Setter for the field <code>moveSpeed</code>.</p>
     *
     * @param moveSpeed a int.
     */
    public void setMoveSpeed(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    /**
     * <p>Getter for the field <code>faceDir</code>.</p>
     *
     * @return a int.
     */
    public int getFaceDir() {
        return faceDir;
    }

    /**
     * <p>Setter for the field <code>faceDir</code>.</p>
     *
     * @param faceDir a int.
     */
    public void setFaceDir(int faceDir) {
        this.faceDir = faceDir;
    }

    /**
     * <p>isCanMove.</p>
     *
     * @return a boolean.
     */
    public boolean isCanMove() {
        return canMove;
    }

    /**
     * <p>Setter for the field <code>canMove</code>.</p>
     *
     * @param canMove a boolean.
     */
    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }
}

