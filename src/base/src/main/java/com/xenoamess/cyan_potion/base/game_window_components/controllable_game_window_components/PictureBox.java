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

package com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.visual.AbstractPictureInterface;
import com.xenoamess.cyan_potion.base.visual.Picture;
import org.joml.Vector4f;

/**
 * PictureComponent
 * PictureComponent is a component class that contains only a picture.
 * It is used to replace Picture when sometimes we need a Component but Picture is not a Component.
 */
public class PictureBox extends AbstractControllableGameWindowComponent implements AbstractPictureInterface {

    /**
     * make sure PictureComponent.picture shall never be null.
     */
    private final AbstractPictureInterface picture;

    /**
     * <p>Constructor for AbstractControllableGameWindowComponent.</p>
     *
     * @param gameWindow gameWindow
     */
    public PictureBox(GameWindow gameWindow) {
        this(gameWindow, (Bindable) null);
    }

    public PictureBox(GameWindow gameWindow, AbstractPictureInterface picture) {
        super(gameWindow);
        if (picture != null) {
            this.picture = picture;
        } else {
            this.picture = new Picture(null);
        }
    }

    public PictureBox(GameWindow gameWindow, Bindable bindable) {
        this(gameWindow, new Picture(bindable));
    }

    @Override
    public void update() {
        super.update();
        this.picture.cover(this);
    }

    /**
     * <p>draw.</p>
     *
     * @param gameWindow gameWindow
     */
    public void draw(GameWindow gameWindow) {
        this.picture.draw(gameWindow);
    }


    public AbstractPictureInterface getPicture() {
        return picture;
    }

    @Override
    public void rotate(float newRotateRadius) {
        this.getPicture().rotate(newRotateRadius);
    }

    @Override
    public void rotateTo(float newRotateRadius) {
        this.getPicture().rotateTo(newRotateRadius);
    }

    public Bindable getCurrentBindable() {
        return this.getPicture().getCurrentBindable();
    }

    @Override
    public Vector4f getColorScale() {
        return this.getPicture().getColorScale();
    }

    @Override
    public void setColorScale(Vector4f colorScale) {
        this.getPicture().setColorScale(colorScale);
    }

    @Override
    public void setColorScale(float x, float y, float z, float w) {
        this.getPicture().getColorScale().set(x, y, z, w);
    }

    @Override
    public float getRotateRadius() {
        return this.getPicture().getRotateRadius();
    }

    @Override
    public void setRotateRadius(float rotateRadius) {
        this.getPicture().setRotateRadius(rotateRadius);
    }


    @Override
    public void bind(int sampler) {
        this.getPicture().bind(sampler);
    }

    @Override
    public void ifVisibleThenDraw() {
        this.draw(this.getGameWindow());
    }
}
