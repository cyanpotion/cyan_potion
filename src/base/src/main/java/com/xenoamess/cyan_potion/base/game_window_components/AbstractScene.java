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

package com.xenoamess.cyan_potion.base.game_window_components;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.render.Camera;
import com.xenoamess.cyan_potion.base.render.Model;
import org.joml.Vector4f;

/**
 * <p>Abstract AbstractScene class.</p>
 *
 * @author XenoAmess
 * @version 0.155.1-SNAPSHOT
 */
public abstract class AbstractScene extends AbstractGameWindowComponent {
    private Camera camera = new Camera(0, 0);
    private float scale;

    /**
     * <p>Constructor for AbstractScene.</p>
     *
     * @param gameWindow gameWindow
     */
    public AbstractScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    /**
     * <p>drawBindableAbsolute.</p>
     *
     * @param camera   a {@link com.xenoamess.cyan_potion.base.render.Camera} object.
     * @param scale    a float.
     * @param bindable bindable
     * @param posX     a float.
     * @param posY     a float.
     * @param size     a float.
     */
    public void drawBindableAbsolute(Camera camera,
                                     float scale,
                                     Bindable bindable,
                                     float posX,
                                     float posY,
                                     float size
    ) {
        this.drawBindableAbsolute(camera, scale, bindable, posX, posY, size, size);
    }

    /**
     * <p>drawBindableAbsolute.</p>
     *
     * @param camera     a {@link com.xenoamess.cyan_potion.base.render.Camera} object.
     * @param scale      a float.
     * @param bindable   a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     * @param posX       a float.
     * @param posY       a float.
     * @param size       a float.
     * @param colorScale colorScale
     */
    public void drawBindableAbsolute(Camera camera,
                                     float scale,
                                     Bindable bindable,
                                     float posX,
                                     float posY,
                                     float size,
                                     Vector4f colorScale) {
        this.drawBindableAbsolute(camera, scale, bindable, posX, posY, size, size, colorScale);
    }

    /**
     * <p>drawBindableAbsolute.</p>
     *
     * @param camera   a {@link com.xenoamess.cyan_potion.base.render.Camera} object.
     * @param scale    a float.
     * @param bindable bindable
     * @param posX     a float.
     * @param posY     a float.
     * @param width    a float.
     * @param height   a float.
     */
    public void drawBindableAbsolute(Camera camera,
                                     float scale,
                                     Bindable bindable,
                                     float posX,
                                     float posY,
                                     float width,
                                     float height) {
        this.drawBindableAbsolute(camera, scale, bindable, posX, posY, width, height, new Vector4f(1, 1, 1, 1));
    }

    /**
     * <p>drawBindableAbsolute.</p>
     *
     * @param camera     a {@link com.xenoamess.cyan_potion.base.render.Camera} object.
     * @param scale      a float.
     * @param bindable   a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     * @param posX       a float.
     * @param posY       a float.
     * @param width      a float.
     * @param height     a float.
     * @param colorScale colorScale
     */
    public void drawBindableAbsolute(Camera camera,
                                     float scale,
                                     Bindable bindable,
                                     float posX,
                                     float posY,
                                     float width,
                                     float height,
                                     Vector4f colorScale) {
        this.drawBindableAbsolute(
                camera,
                scale,
                bindable,
                posX,
                posY,
                width,
                height,
                Model.COMMON_MODEL,
                colorScale
        );
    }

    /**
     * <p>drawBindableAbsolute.</p>
     *
     * @param camera     a {@link com.xenoamess.cyan_potion.base.render.Camera} object.
     * @param scale      a float.
     * @param bindable   a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     * @param posX       a float.
     * @param posY       a float.
     * @param width      a float.
     * @param height     a float.
     * @param model      a {@link com.xenoamess.cyan_potion.base.render.Model} object.
     * @param colorScale colorScale
     */
    public void drawBindableAbsolute(Camera camera,
                                     float scale,
                                     Bindable bindable,
                                     float posX,
                                     float posY,
                                     float width,
                                     float height,
                                     Model model,
                                     Vector4f colorScale) {
        this.drawBindableAbsolute(
                camera,
                scale,
                bindable,
                posX,
                posY,
                width,
                height,
                Model.COMMON_MODEL,
                colorScale,
                0
        );
    }

    /**
     * <p>drawBindableAbsolute.</p>
     *
     * @param camera       a {@link com.xenoamess.cyan_potion.base.render.Camera} object.
     * @param scale        a float.
     * @param bindable     a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     * @param posX         a float.
     * @param posY         a float.
     * @param width        a float.
     * @param height       a float.
     * @param model        a {@link com.xenoamess.cyan_potion.base.render.Model} object.
     * @param colorScale   a {@link org.joml.Vector4f} object.
     * @param rotateRadius a float.
     */
    public void drawBindableAbsolute(Camera camera,
                                     float scale,
                                     Bindable bindable,
                                     float posX,
                                     float posY,
                                     float width,
                                     float height,
                                     Model model,
                                     Vector4f colorScale,
                                     float rotateRadius) {
        this.getGameWindow().drawBindableRelativeCenter(
                bindable,
                (posX - camera.getPosition().x) * scale + this.getGameWindow().getLogicWindowWidth() / 2F,
                (posY - camera.getPosition().y) * scale + this.getGameWindow().getLogicWindowHeight() / 2F,
                width * scale,
                height * scale,
                model,
                colorScale,
                rotateRadius
        );
    }

    /**
     * <p>Getter for the field <code>camera</code>.</p>
     *
     * @return return
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * <p>Setter for the field <code>camera</code>.</p>
     *
     * @param camera camera
     */
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    /**
     * <p>Getter for the field <code>scale</code>.</p>
     *
     * @return a float.
     */
    public float getScale() {
        return scale;
    }

    /**
     * <p>Setter for the field <code>scale</code>.</p>
     *
     * @param scale a float.
     */
    public void setScale(float scale) {
        this.scale = scale;
    }
}
