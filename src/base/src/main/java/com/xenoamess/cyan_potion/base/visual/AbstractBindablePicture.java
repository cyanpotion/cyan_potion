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

package com.xenoamess.cyan_potion.base.visual;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.game_window_components.AbstractScene;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.render.Model;

/**
 * <p>AbstractBindablePicture class.</p>
 *
 * @author XenoAmess
 * @version 0.159.0
 */
public abstract class AbstractBindablePicture extends AbstractPicture implements Bindable {
    /**
     * {@inheritDoc}
     *
     * <p>draw.</p>
     */
    @Override
    public void draw(GameWindow gameWindow) {
        if (this.getCurrentBindable() == null) {
            return;
        }
        gameWindow.drawBindableRelativeCenter(
                this.getCurrentBindable(),
                this.getCenterPosX(),
                this.getCenterPosY(),
                this.getWidth(),
                this.getHeight(),
                Model.COMMON_MODEL,
                this.getColorScale(),
                this.getRotateRadius()
        );
    }

    /**
     * {@inheritDoc}
     *
     * <p>draw.</p>
     */
    @Override
    public void draw(AbstractScene scene) {
        if (this.getCurrentBindable() == null) {
            return;
        }
        scene.drawBindableAbsolute(
                scene.getCamera(),
                scene.getScale(),
                this.getCurrentBindable(),
                this.getCenterPosX(),
                this.getCenterPosY(),
                this.getWidth(),
                this.getHeight(),
                Model.COMMON_MODEL,
                this.getColorScale(),
                this.getRotateRadius()
        );
    }

    /**
     * get current bindable of this picture.
     *
     * @return the bindable for draw
     */
    protected abstract Bindable getCurrentBindable();

    /**
     * {@inheritDoc}
     */
    @Override
    public void bind(int sampler) {
        Bindable bindable = this.getCurrentBindable();
        if (bindable != null) {
            bindable.bind(sampler);
        }
    }
}
