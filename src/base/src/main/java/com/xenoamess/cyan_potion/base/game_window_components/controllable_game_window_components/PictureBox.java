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
import com.xenoamess.cyan_potion.base.game_window_components.AbstractScene;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.visual.AbstractPictureInterface;
import com.xenoamess.cyan_potion.base.visual.Picture;
import org.joml.Vector4fc;

/**
 * PictureComponent
 * PictureComponent is a component class that contains only a picture.
 * It is used to replace Picture when sometimes we need a Component but Picture is not a Component.
 *
 * @author xenoa
 * @version 0.155.3-SNAPSHOT
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

    /**
     * <p>Constructor for PictureBox.</p>
     *
     * @param gameWindow a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     * @param picture    a {@link com.xenoamess.cyan_potion.base.visual.AbstractPictureInterface} object.
     */
    public PictureBox(GameWindow gameWindow, AbstractPictureInterface picture) {
        super(gameWindow);
        if (picture != null) {
            this.picture = picture;
        } else {
            this.picture = new Picture(null);
        }
    }

    /**
     * <p>Constructor for PictureBox.</p>
     *
     * @param gameWindow a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     * @param bindable   a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     */
    public PictureBox(GameWindow gameWindow, Bindable bindable) {
        this(gameWindow, (AbstractPictureInterface) new Picture(bindable));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        super.update();
        this.picture.cover(this);
    }

    /**
     * {@inheritDoc}
     *
     * <p>draw.</p>
     *
     * @param gameWindow gameWindow
     */
    public void draw(GameWindow gameWindow) {
        this.picture.draw(gameWindow);
    }

    /**
     * {@inheritDoc}
     *
     * <p>draw.</p>
     */
    public void draw(AbstractScene abstractScene) {
        this.picture.draw(abstractScene);
    }

    /**
     * <p>Getter for the field <code>picture</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.visual.AbstractPictureInterface} object.
     */
    public AbstractPictureInterface getPicture() {
        return picture;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rotate(float newRotateRadius) {
        this.getPicture().rotate(newRotateRadius);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rotateTo(float newRotateRadius) {
        this.getPicture().rotateTo(newRotateRadius);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector4fc getColorScale() {
        return this.getPicture().getColorScale();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColorScale(Vector4fc colorScale) {
        this.getPicture().setColorScale(colorScale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColorScale(float x, float y, float z, float w) {
        this.getPicture().setColorScale(x, y, z, w);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getRotateRadius() {
        return this.getPicture().getRotateRadius();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRotateRadius(float rotateRadius) {
        this.getPicture().setRotateRadius(rotateRadius);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ifVisibleThenDraw() {
        this.draw(this.getGameWindow());
    }
}
