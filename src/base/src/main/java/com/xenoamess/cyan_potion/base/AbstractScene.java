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

package com.xenoamess.cyan_potion.base;

import com.xenoamess.cyan_potion.base.gameWindowComponents.AbstractGameWindowComponent;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.render.Camera;
import com.xenoamess.cyan_potion.base.render.Model;
import org.joml.Vector4f;

/**
 * @author XenoAmess
 */
public abstract class AbstractScene extends AbstractGameWindowComponent {
    private Camera camera = new Camera(0, 0);
    private float scale;

    public AbstractScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    public void drawBindableAbsolute(Camera camera,
                                     float scale,
                                     Bindable bindable,
                                     float posx,
                                     float posy,
                                     float size
    ) {
        this.drawBindableAbsolute(camera, scale, bindable, posx, posy, size, size);
    }

    public void drawBindableAbsolute(Camera camera,
                                     float scale,
                                     Bindable bindable,
                                     float posx,
                                     float posy,
                                     float size,
                                     Vector4f colorScale) {
        this.drawBindableAbsolute(camera, scale, bindable, posx, posy, size, size, colorScale);
    }

    public void drawBindableAbsolute(Camera camera,
                                     float scale,
                                     Bindable bindable,
                                     float posx,
                                     float posy,
                                     float width,
                                     float height) {
        this.drawBindableAbsolute(camera, scale, bindable, posx, posy, width, height, new Vector4f(1, 1, 1, 1));
    }

    public void drawBindableAbsolute(Camera camera,
                                     float scale,
                                     Bindable bindable,
                                     float posx,
                                     float posy,
                                     float width,
                                     float height,
                                     Vector4f colorScale) {
        this.drawBindableAbsolute(
                camera,
                scale,
                bindable,
                posx,
                posy,
                width,
                height,
                Model.COMMON_MODEL,
                colorScale
        );
    }

    public void drawBindableAbsolute(Camera camera,
                                     float scale,
                                     Bindable bindable,
                                     float posx,
                                     float posy,
                                     float width,
                                     float height,
                                     Model model,
                                     Vector4f colorScale) {
        this.drawBindableAbsolute(
                camera,
                scale,
                bindable,
                posx,
                posy,
                width,
                height,
                Model.COMMON_MODEL,
                colorScale,
                0
        );
    }

    public void drawBindableAbsolute(Camera camera,
                                     float scale,
                                     Bindable bindable,
                                     float posx,
                                     float posy,
                                     float width,
                                     float height,
                                     Model model,
                                     Vector4f colorScale,
                                     float rotateRadius) {
        this.getGameWindow().drawBindableRelative(
                bindable,
                (posx - camera.getPosition().x) * scale + this.getGameWindow().getLogicWindowWidth() / 2F,
                (posy - camera.getPosition().y) * scale + this.getGameWindow().getLogicWindowHeight() / 2F,
                width * scale,
                height * scale,
                model,
                colorScale,
                rotateRadius
        );
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
