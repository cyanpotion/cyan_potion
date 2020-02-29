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

package com.xenoamess.cyan_potion.coordinate.entity;

import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.coordinate.AbstractEntityScene;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * <p>StaticEntity class.</p>
 *
 * @author XenoAmess
 * @version 0.161.3
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class StaticEntity extends AbstractEntity {

    /**
     * <p>Constructor for StaticEntity.</p>
     *
     * @param scene       a {@link com.xenoamess.cyan_potion.coordinate.AbstractEntityScene} object.
     * @param bindable    a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     * @param shape       a {@link com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape} object.
     * @param leftTopPosX a float.
     * @param leftTopPosY a float.
     * @param width       a float.
     * @param height      a float.
     * @param layer       a int.
     */
    public StaticEntity(
            AbstractEntityScene scene,
            float leftTopPosX, float leftTopPosY,
            float width, float height,
            int layer,
            Bindable bindable,
            AbstractShape shape
    ) {
        super(
                scene,
                leftTopPosX, leftTopPosY,
                width, height,
                layer,
                bindable,
                shape
        );
    }
}
