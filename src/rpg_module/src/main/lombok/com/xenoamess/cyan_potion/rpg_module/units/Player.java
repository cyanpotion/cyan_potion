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

import com.xenoamess.cyan_potion.base.memory.ResourceInfo;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.coordinate.AbstractEntityScene;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;

/**
 * <p>Player class.</p>
 *
 * @author XenoAmess
 * @version 0.161.3-SNAPSHOT
 */
public class Player extends Unit {
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
    public Player(
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
    public Player(
            AbstractEntityScene scene,
            float centerPosX, float centerPosY,
            float width, float height,
            int layer,
            Bindable bindable) {
        super(
                scene,
                centerPosX, centerPosY,
                width, height,
                layer,
                bindable
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
    public Player(
            AbstractEntityScene scene,
            float centerPosX, float centerPosY,
            float width, float height,
            int layer,
            ResourceInfo walkingAnimation4DirsResourceInfo,
            ResourceManager resourceManager) {
        super(
                scene,
                centerPosX, centerPosY,
                width, height,
                layer,
                walkingAnimation4DirsResourceInfo,
                resourceManager
        );
    }

}
