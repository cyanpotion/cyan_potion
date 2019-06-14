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
import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.HorizontalRectangle;
import org.joml.Vector3f;

/**
 * <p>Player class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class Player extends Unit {
    /**
     * <p>Constructor for Player.</p>
     *
     * @param scene     a {@link com.xenoamess.cyan_potion.coordinate.AbstractEntityScene} object.
     * @param centerPos a {@link org.joml.Vector3f} object.
     * @param size      a {@link org.joml.Vector3f} object.
     * @param bindable  a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     * @param shape     a {@link com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape} object.
     */
    public Player(AbstractEntityScene scene, Vector3f centerPos, Vector3f size,
                  Bindable bindable, AbstractShape shape) {
        super(scene, centerPos, size, bindable, shape);
    }

    /**
     * <p>Constructor for Player.</p>
     *
     * @param scene     a {@link com.xenoamess.cyan_potion.coordinate.AbstractEntityScene} object.
     * @param centerPos a {@link org.joml.Vector3f} object.
     * @param size      a {@link org.joml.Vector3f} object.
     * @param bindable  a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     */
    public Player(AbstractEntityScene scene, Vector3f centerPos, Vector3f size,
                  Bindable bindable) {
        super(scene, centerPos, size, bindable, new HorizontalRectangle(null,
                centerPos, size));
    }

    /**
     * <p>Constructor for Player.</p>
     *
     * @param scene                    a {@link com.xenoamess.cyan_potion.coordinate.AbstractEntityScene} object.
     * @param centerPos                a {@link org.joml.Vector3f} object.
     * @param size                     a {@link org.joml.Vector3f} object.
     * @param walkingAnimation4DirsURI a {@link java.lang.String} object.
     * @param resourceManager          a {@link com.xenoamess.cyan_potion.base.memory.ResourceManager} object.
     */
    public Player(AbstractEntityScene scene, Vector3f centerPos, Vector3f size,
                  String walkingAnimation4DirsURI,
                  ResourceManager resourceManager) {
        super(scene, centerPos, size, walkingAnimation4DirsURI,
                resourceManager);
    }
}
