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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joml.Vector4f;
import org.joml.Vector4fc;

/**
 * <p>Abstract AbstractScene class.</p>
 *
 * @author XenoAmess
 * @version 0.160.0-SNAPSHOT
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public abstract class AbstractScene extends AbstractGameWindowComponent {
    @Getter
    @Setter
    private Camera camera = new Camera(0, 0);
    @Getter
    @Setter
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
                                     Vector4fc colorScale) {
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
                                     Vector4fc colorScale) {
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
                                     Vector4fc colorScale) {
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
                                     Vector4fc colorScale,
                                     float rotateRadius) {
        this.getGameWindow().drawBindableRelativeCenter(
                bindable,
                this.absolutePosToRelativeX(camera, posX, scale),
                this.absolutePosToRelativeY(camera, posY, scale),
                width * scale,
                height * scale,
                model,
                colorScale,
                rotateRadius
        );
    }

    /**
     * translate a absolute pos(for this scene) to a relative pos(for window)
     *
     * @param absolutePosX absolutePosX
     * @return relativePosX
     */
    public float absolutePosToRelativeX(float absolutePosX) {
        return this.absolutePosToRelativeX(absolutePosX, this.scale);
    }

    /**
     * translate a absolute pos(for this scene) to a relative pos(for window)
     *
     * @param absolutePosX absolutePosX
     * @param scale        scale
     * @return relativePosX
     */
    public float absolutePosToRelativeX(float absolutePosX, float scale) {
        return this.absolutePosToRelativeX(this.getCamera(), absolutePosX, scale);
    }

    /**
     * translate a absolute pos(for this scene) to a relative pos(for window)
     *
     * @param camera       a {@link com.xenoamess.cyan_potion.base.render.Camera} object.
     * @param absolutePosX absolutePosX
     * @param scale        scale
     * @return relativePosX
     */
    public float absolutePosToRelativeX(Camera camera, float absolutePosX, float scale) {
        return (absolutePosX - camera.getPosX()) * scale + this.getGameWindow().getLogicWindowWidth() / 2F;
    }

    /**
     * translate a absolute pos(for this scene) to a relative pos(for window)
     *
     * @param absolutePosY absolutePosY
     * @return relativePosY
     */
    public float absolutePosToRelativeY(float absolutePosY) {
        return this.absolutePosToRelativeY(absolutePosY, this.scale);
    }

    /**
     * translate a absolute pos(for this scene) to a relative pos(for window)
     *
     * @param absolutePosX absolutePosX
     * @param scale        scale
     * @return relativePosY
     */
    public float absolutePosToRelativeY(float absolutePosX, float scale) {
        return this.absolutePosToRelativeY(this.getCamera(), absolutePosX, scale);
    }

    /**
     * translate a absolute pos(for this scene) to a relative pos(for window)
     *
     * @param camera       a {@link com.xenoamess.cyan_potion.base.render.Camera} object.
     * @param absolutePosX absolutePosX
     * @param scale        scale
     * @return relativePosY
     */
    public float absolutePosToRelativeY(Camera camera, float absolutePosX, float scale) {
        return (absolutePosX - camera.getPosY()) * scale + this.getGameWindow().getLogicWindowHeight() / 2F;
    }

}
