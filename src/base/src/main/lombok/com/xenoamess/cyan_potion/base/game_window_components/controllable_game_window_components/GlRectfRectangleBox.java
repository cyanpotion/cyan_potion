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
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import static org.lwjgl.opengl.GL11.*;

/**
 * GlRectfRectangleBox
 * <p>
 * can draw a horizontal rectangle.(rotate radios will be ignored)
 * <p>
 * it is written here only be used for a demo for how can yuu implement AbstractControllableGameWindowComponent,
 * (especially for drawing new
 * <p>
 * I already found a new (and better) way to do this.
 * And I suggest you use pure color Texture (and PictureBox) instead.
 *
 * @author XenoAmess
 * @version 0.162.2-SNAPSHOT
 * @see com.xenoamess.cyan_potion.base.render.Texture#loadAsPureColorTexture(com.xenoamess.cyan_potion.base.render.Texture)
 * @deprecated
 */
@EqualsAndHashCode(callSuper = true)
@ToString
@Deprecated
public class GlRectfRectangleBox extends AbstractControllableGameWindowComponent {
    private final Vector4f color = new Vector4f(1, 1, 1, 1);

    /**
     * <p>Constructor for AbstractControllableGameWindowComponent.</p>
     *
     * @param gameWindow gameWindow
     */
    public GlRectfRectangleBox(GameWindow gameWindow) {
        super(gameWindow);
    }

    /**
     * <p>Constructor for AbstractControllableGameWindowComponent.</p>
     *
     * @param gameWindow gameWindow
     * @param color      a {@link org.joml.Vector4f} object.
     */
    public GlRectfRectangleBox(GameWindow gameWindow, Vector4fc color) {
        this(gameWindow);
        this.setColor(color);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean ifVisibleThenDraw() {
        glDisable(GL_TEXTURE_2D);
        glColor4f(color.x, color.y, color.z, color.w);
        glRectf(getLeftPosX(), getLeftTopPosY(), getRightBottomPosX(), getRightBottomPosY());
        return true;
    }

    /**
     * <p>Getter for the field <code>color</code>.</p>
     *
     * @return a {@link org.joml.Vector4f} object.
     */
    public Vector4fc getColor() {
        return new Vector4f(color);
    }

    /**
     * <p>Setter for the field <code>color</code>.</p>
     *
     * @param color a {@link org.joml.Vector4f} object.
     */
    public void setColor(Vector4fc color) {
        this.color.set(color);
    }

    /**
     * <p>Setter for the field <code>color</code>.</p>
     *
     * @param x a int.
     * @param y a int.
     * @param z a int.
     * @param w a int.
     */
    public void setColor(int x, int y, int z, int w) {
        this.color.set(x, y, z, w);
    }
}
