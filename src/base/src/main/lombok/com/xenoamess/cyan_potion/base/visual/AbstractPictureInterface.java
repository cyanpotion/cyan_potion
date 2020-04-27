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
import com.xenoamess.cyan_potion.base.areas.AbstractMutableArea;
import com.xenoamess.cyan_potion.base.game_window_components.AbstractScene;
import org.joml.Vector4f;
import org.joml.Vector4fc;

/**
 * AbstractPictureInterface
 * AbstractPictureInterface mean
 *
 * @author XenoAmess
 * @version 0.162.2-SNAPSHOT
 */
public interface AbstractPictureInterface extends AbstractMutableArea {
    /**
     * draw this picture to {@link com.xenoamess.cyan_potion.base.GameWindow}.
     *
     * @param gameWindow gameWindow
     */
    void draw(GameWindow gameWindow);

    /**
     * draw this picture to {@link com.xenoamess.cyan_potion.base.game_window_components.AbstractScene}
     * notice that this function will use Scene's "absolutePos" for all positions, and draw on the scene absolutely
     * if you want to draw relative to the window, then you shall get GameWindow from the Scene, and
     *
     * @param scene scene
     */
    void draw(AbstractScene scene);

    /**
     * <p>rotate.</p>
     *
     * @param newRotateRadius a float.
     */
    void rotate(float newRotateRadius);

    /**
     * <p>rotateTo.</p>
     *
     * @param newRotateRadius a float.
     */
    void rotateTo(float newRotateRadius);

    /**
     * <p>getColorScale.</p>
     *
     * @return a {@link org.joml.Vector4f} object.
     */
    Vector4fc getColorScale();

    /**
     * <p>setColorScale.</p>
     *
     * @param colorScale a {@link org.joml.Vector4f} object.
     */
    void setColorScale(Vector4fc colorScale);

    /**
     * <p>setColorScale.</p>
     *
     * @param x a float.
     * @param y a float.
     * @param z a float.
     * @param w a float.
     */
    default void setColorScale(float x, float y, float z, float w) {
        this.setColorScale(new Vector4f(x, y, z, w));
    }

    /**
     * <p>getRotateRadius.</p>
     *
     * @return a float.
     */
    float getRotateRadius();

    /**
     * <p>setRotateRadius.</p>
     *
     * @param rotateRadius a float.
     */
    void setRotateRadius(float rotateRadius);

}
