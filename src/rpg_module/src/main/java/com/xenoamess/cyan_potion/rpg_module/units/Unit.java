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

import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.coordinate.AbstractScene;
import com.xenoamess.cyan_potion.coordinate.entity.AbstractDynamicEntity;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.HorizontalRectangle;
import com.xenoamess.cyan_potion.rpg_module.render.WalkingAnimation4Dirs;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * @author XenoAmess
 */
public class Unit extends AbstractDynamicEntity {
    public static final int DEFAULT_UNIT_LAYER = 100;

    private boolean moving = false;
    private Vector2f movement = new Vector2f();
    private int moveSpeed = 256;
    private int faceDir = 180;
    private boolean canMove = true;

    private Bindable bindable;

    public Unit(AbstractScene scene, Vector3f centerPos, Vector3f size,
                Bindable bindable, AbstractShape shape) {
        super(scene, centerPos, size, bindable, shape);
    }

    public Unit(AbstractScene scene, Vector3f centerPos, Vector3f size,
                Bindable bindable) {
        this(scene, centerPos, size, bindable, new HorizontalRectangle(null,
                centerPos, size));
    }

    public Unit(AbstractScene scene, Vector3f centerPos, Vector3f size,
                String walkingAnimation4DirsURI,
                ResourceManager resourceManager) {
        this(scene, centerPos, size, null);
        this.loadWalkingAnimations(walkingAnimation4DirsURI, resourceManager);
    }

    @Override
    public Bindable getBindable() {
        return bindable;
    }

    @Override
    public void setBindable(Bindable bindable) {
        this.bindable = bindable;
    }

    @Override
    public void update() {
        if (!this.isCanMove()) {
            this.getMovement().set(0, 0);
        }
        if (getMovement().x != 0 || getMovement().y != 0) {
            setMoving(true);
            setFaceDir(getFaceDir(getMovement().x, getMovement().y));

            if (this.getBindable() instanceof WalkingAnimation4Dirs) {
                ((WalkingAnimation4Dirs) this.getBindable()).setFaceDir(getFaceDir());
            }

            if (getMovement().length() > getMoveSpeed() * DataCenter.FRAME_CAP_F) {
                this.tryMove(new Vector3f(getMovement().mul(getMoveSpeed() * DataCenter.FRAME_CAP_F / getMovement().length(), new Vector2f()), 0));
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


    public void loadWalkingAnimations(String walkingAnimation4DirsURI,
                                      ResourceManager resourceManager) {
        this.setBindable(new WalkingAnimation4Dirs(4, this,
                walkingAnimation4DirsURI, resourceManager));
    }


    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public Vector2f getMovement() {
        return movement;
    }

    public void setMovement(Vector2f movement) {
        this.movement = movement;
    }

    public int getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public int getFaceDir() {
        return faceDir;
    }

    public void setFaceDir(int faceDir) {
        this.faceDir = faceDir;
    }

    public boolean isCanMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }
}

