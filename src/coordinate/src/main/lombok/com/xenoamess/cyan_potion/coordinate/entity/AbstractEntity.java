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

import com.xenoamess.cyan_potion.base.areas.AbstractMutableArea;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.visual.AbstractPictureInterface;
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.coordinate.AbstractEntityScene;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity means some thing in your game, alive or not.
 * it can be a tree, a person, a player, or something.
 * it can have size, have shape, have picture.
 * But you can also make it dont't have them.
 * Entity just mean a basic Object in cyan_potion.coordinate
 * If a thing wanna use functions in cyan_potion.coordinate like collided detect, then it is common to make it an
 * Entity.
 *
 * @author XenoAmess
 * @version 0.162.2
 */
@EqualsAndHashCode
@ToString
public abstract class AbstractEntity implements AbstractMutableArea {
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    private final AbstractEntityScene scene;

    @Getter
    @Setter
    private float leftTopPosX;

    @Getter
    @Setter
    private float leftTopPosY;

    @Getter
    @Setter
    private int layer;

    @Getter
    @Setter
    private float width;

    @Getter
    @Setter
    private float height;

    @Getter
    @Setter
    private AbstractShape shape;

    @Getter
    @Setter
    private AbstractPictureInterface picture = new Picture();


    /**
     * <p>Constructor for AbstractEntity.</p>
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
    public AbstractEntity(
            AbstractEntityScene scene,
            float centerPosX, float centerPosY,
            float width, float height,
            int layer,
            Bindable bindable,
            AbstractShape shape
    ) {
        this.scene = scene;
        this.width = width;
        this.height = height;
        this.setCenterPos(centerPosX, centerPosY);
        this.layer = layer;
        this.setShape(shape);
        if (this.getShape() != null) {
            this.getShape().setEntity(this);
        }
        this.setPicture(new Picture(bindable));
        this.getPicture().cover(this);
    }


    /**
     * <p>Getter for the field <code>picture</code>.</p>
     *
     * @param picture a {@link com.xenoamess.cyan_potion.base.visual.AbstractPicture} object.
     */
    public void setPicture(AbstractPictureInterface picture) {
        this.picture = picture;
        if (this.picture != null) {
            this.picture.cover(this);
        }
    }

    /**
     * <p>draw.</p>
     *
     * @param scene scene
     */
    public void draw(AbstractEntityScene scene) {
        this.getPicture().draw(scene);
    }

    /**
     * register this.shape into the scene if shape exist
     */
    public void registerShape() {
        if (this.getShape() != null) {
            this.getShape().register();
        }
    }
}
