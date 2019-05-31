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
 * @author XenoAmess
 */
public class Player extends Unit {
    public Player(AbstractEntityScene scene, Vector3f centerPos, Vector3f size,
                  Bindable bindable, AbstractShape shape) {
        super(scene, centerPos, size, bindable, shape);
    }

    public Player(AbstractEntityScene scene, Vector3f centerPos, Vector3f size,
                  Bindable bindable) {
        super(scene, centerPos, size, bindable, new HorizontalRectangle(null,
                centerPos, size));
    }

    public Player(AbstractEntityScene scene, Vector3f centerPos, Vector3f size,
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
