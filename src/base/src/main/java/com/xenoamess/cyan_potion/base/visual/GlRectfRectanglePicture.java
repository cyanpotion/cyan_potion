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

import com.xenoamess.commons.main_thread_only.MainThreadOnly;
import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.game_window_components.AbstractScene;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import static org.lwjgl.opengl.GL11.*;

/**
 * GlRectfRectanglePicture
 * <p>
 * can draw a horizontal rectangle.(rotate radios will be ignored)
 * <p>
 * it is written here only be used for a demo for how can yuu implement AbstractPicture,
 * (especially for showing how can you implement a AbstractPicture but not AbstractBindablePicture class.)
 * <p>
 * I already found a new (and better) way to do this.
 * And I suggest you use pure color Texture instead.
 *
 * @author xenoa
 * @version 0.155.3
 * @see com.xenoamess.cyan_potion.base.render.Texture#loadAsPureColorTexture(com.xenoamess.cyan_potion.base.render.Texture)
 * @deprecated
 */
@Deprecated
public class GlRectfRectanglePicture extends AbstractPicture {
    private final Vector4f color = new Vector4f(0, 0, 0, 0);

    @Override
    public void draw(GameWindow gameWindow) {
        Vector4fc drawColor = this.getColor().mul(this.getColorScale(), new Vector4f());
        GlRectfRectanglePicture.drawRectangleLeftTop(
                this.getLeftTopPosX(),
                this.getLeftTopPosY(),
                this.getWidth(),
                this.getHeight(),
                drawColor
        );
    }

    @Override
    public void draw(AbstractScene scene) {
        Vector4fc drawColor = this.getColor().mul(this.getColorScale(), new Vector4f());
        GlRectfRectanglePicture.drawRectangleLeftTop(
                scene.relativePosToAbsoluteX(this.getLeftTopPosX()),
                scene.relativePosToAbsoluteY(this.getLeftTopPosY()),
                this.getWidth(),
                this.getHeight(),
                drawColor
        );
    }

    @MainThreadOnly
    public static void drawRectangleLeftTop(
            float leftTopPosX,
            float leftTopPosY,
            float width,
            float height,
            Vector4fc color
    ) {
        glDisable(GL_TEXTURE_2D);
        glColor4f(color.x(), color.y(), color.z(), color.w());
        glRectf(leftTopPosX, leftTopPosY, leftTopPosX + width, leftTopPosX + height);
    }

    public Vector4fc getColor() {
        return new Vector4f(color);
    }

    public void setColor(Vector4fc color) {
        this.color.set(color);
    }
}
