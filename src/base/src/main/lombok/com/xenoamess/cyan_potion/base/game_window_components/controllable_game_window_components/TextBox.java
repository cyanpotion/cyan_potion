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
import com.xenoamess.cyan_potion.base.visual.Font;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import static com.xenoamess.cyan_potion.base.visual.Font.EACH_CHAR_NUM;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;


/**
 * <p>TextBox class.</p>
 *
 * @author XenoAmess
 * @version 0.160.0
 */

@EqualsAndHashCode(callSuper = true)
@ToString
public class TextBox extends AbstractControllableGameWindowComponent {
    @Getter
    @Setter
    private boolean wordWrap;
    @Getter
    @Setter
    private String contentString = "";
    @Getter
    @Setter
    private float charHeight = 20.0f;
    @Getter
    @Setter
    private final Vector4f textColor = new Vector4f(1, 1, 1, 1);

    /**
     * <p>Constructor for TextBox.</p>
     *
     * @param gameWindow gameWindow
     */
    public TextBox(GameWindow gameWindow) {
        super(gameWindow);
        this.setWordWrap(this.getGameWindow().getGameManager().getDataCenter().getTextStructure().ifLanguageNeedWordWrap(this.getGameWindow().getGameManager().getDataCenter().getTextStructure().getCurrentLanguage()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean ifVisibleThenDraw() {
        this.drawText();
        return true;
    }


    /**
     * <p>drawText.</p>
     */
    public void drawText() {
        float realLeftTopPosX =
                this.getLeftTopPosX() / this.getGameWindow().getLogicWindowWidth() * this.getGameWindow().getRealWindowWidth();
        float realLeftTopPosY =
                this.getLeftTopPosY() / this.getGameWindow().getLogicWindowHeight() * this.getGameWindow().getRealWindowHeight();
        float realWidth =
                this.getWidth() / this.getGameWindow().getLogicWindowWidth() * this.getGameWindow().getRealWindowWidth();
        float realHeight =
                this.getHeight() / this.getGameWindow().getLogicWindowHeight() * this.getGameWindow().getRealWindowHeight();
        float realCharHeight =
                this.getCharHeight() / this.getGameWindow().getLogicWindowHeight() * this.getGameWindow().getRealWindowHeight();

        Font font = Font.getCurrentFont();

        float scaleY = font.getScale(realCharHeight);
        float scaleX = scaleY;

        font.bind();

        float lineStartPosX = realLeftTopPosX;
        float lineStartPosY = realLeftTopPosY;


        font.getXb().put(0, lineStartPosX);
        font.getYb().put(0, lineStartPosY);

        glEnable(GL_TEXTURE_2D);

        glBegin(GL_QUADS);

        float lastXReal = lineStartPosX;
        float lastYReal = lineStartPosY;
        float lastXShould = lineStartPosX;
        float lastYShould = lineStartPosY;

        for (int i = 0; i < this.getContentString().length(); i++) {
            if (this.getTextColor() != null) {
                glColor4f(
                        this.getTextColor().x(),
                        this.getTextColor().y(),
                        this.getTextColor().z(),
                        this.getTextColor().w()
                );
            }

            if (this.getContentString().charAt(i) == '\n') {
                lineStartPosX = realLeftTopPosX;
                lineStartPosY += realCharHeight;
                lastXReal = lineStartPosX;
                lastYReal = lineStartPosY;
                lastXShould = lineStartPosX;
                lastYShould = lineStartPosY;
                font.getXb().put(0, lineStartPosX);
                font.getYb().put(0, lineStartPosY);
                continue;
            }
            if (this.getContentString().charAt(i) < 32) {
                continue;
            }
            if (Character.isWhitespace(this.getContentString().charAt(i))) {
                glEnd();
                glBindTexture(GL_TEXTURE_2D,
                        font.getFontTextures().getPrimitive(this.getContentString().charAt(i) / EACH_CHAR_NUM));
                glBegin(GL_QUADS);
                stbtt_GetPackedQuad(
                        font.getCharDatas().get(this.getContentString().charAt(i) / EACH_CHAR_NUM), Font.BITMAP_W,
                        Font.BITMAP_H,
                        this.getContentString().charAt(i) % EACH_CHAR_NUM,
                        font.getXb(), font.getYb(), font.getQ(), false);

                float charWidthShould = font.getQ().x1() - font.getQ().x0();
                float charHeightShould = font.getQ().y1() - font.getQ().y0();
                float spaceLeftToCharShould = font.getQ().x0() - lastXShould;
                float spaceUpToCharShould = font.getQ().y0() - lastYShould;
                float nowX0 = lastXReal + spaceLeftToCharShould * scaleX;
                float nowY0 = lineStartPosY + spaceUpToCharShould * scaleY;

                Font.drawBoxTC(
                        nowX0, nowY0 + realCharHeight * 0.8f,
                        nowX0 + charWidthShould * scaleX,
                        nowY0 + charHeightShould * scaleY + realCharHeight * 0.8f,
                        font.getQ().s0(), font.getQ().t0(), font.getQ().s1(),
                        font.getQ().t1()
                );

                lastXReal = nowX0 + charWidthShould * scaleX;
                lastYReal = lineStartPosY;
                lastXShould = font.getQ().x1();
                lastYShould = lineStartPosY;
                continue;
            }

            float lastXRealBak = lastXReal;
            float lastYRealBak = lastYReal;
            float lastXShouldBak = lastXShould;
            float lastYShouldBak = lastYShould;
            float xbBak = font.getXb().get(0);
            float ybBak = font.getYb().get(0);
            int ti;

            for (ti = i; ti < this.getContentString().length() && (isWordWrap() || ti < i + 1); ti++) {
                char nowChar = this.getContentString().charAt(ti);
                if (Character.isWhitespace(nowChar)) {
                    break;
                }
                if (nowChar < 32) {
                    continue;
                }
                glEnd();
                glBindTexture(GL_TEXTURE_2D,
                        font.getFontTextures().getPrimitive(nowChar / EACH_CHAR_NUM));
                glBegin(GL_QUADS);
                stbtt_GetPackedQuad(
                        font.getCharDatas().get(nowChar / EACH_CHAR_NUM), Font.BITMAP_W, Font.BITMAP_H,
                        nowChar % EACH_CHAR_NUM,
                        font.getXb(), font.getYb(), font.getQ(), false);
                float charWidthShould = font.getQ().x1() - font.getQ().x0();
                float charHeightShould = font.getQ().y1() - font.getQ().y0();
                float spaceLeftToCharShould = font.getQ().x0() - lastXShould;
                float spaceUpToCharShould = font.getQ().y0() - lastYShould;
                float nowX0 = lastXReal + spaceLeftToCharShould * scaleX;
                float nowY0 = lineStartPosY + spaceUpToCharShould * scaleY;

                lastXReal = nowX0 + charWidthShould * scaleX;
                lastYReal = lineStartPosY;
                lastXShould = font.getQ().x1();
                lastYShould = lineStartPosY;
            }

            boolean ifChangeLine = lastXReal > lineStartPosX + realWidth;

            lastXReal = lastXRealBak;
            lastYReal = lastYRealBak;
            lastXShould = lastXShouldBak;
            lastYShould = lastYShouldBak;
            font.getXb().put(0, xbBak);
            font.getYb().put(0, ybBak);

            if (ifChangeLine) {
                lineStartPosX = realLeftTopPosX;
                lineStartPosY += realCharHeight;
                lastXReal = lineStartPosX;
                lastYReal = lineStartPosY;
                lastXShould = lineStartPosX;
                lastYShould = lineStartPosY;
                font.getXb().put(0, lineStartPosX);
                font.getYb().put(0, lineStartPosY);
            }

            for (ti = i; ti < this.getContentString().length() && (isWordWrap() || ti < i + 1); ti++) {
                char nowChar = this.getContentString().charAt(ti);
                if (Character.isWhitespace(nowChar)) {
                    break;
                }
                if (nowChar < 32) {
                    continue;
                }
                glEnd();
                glBindTexture(GL_TEXTURE_2D,
                        font.getFontTextures().getPrimitive(nowChar / EACH_CHAR_NUM));
                glBegin(GL_QUADS);
                stbtt_GetPackedQuad(
                        font.getCharDatas().get(nowChar / EACH_CHAR_NUM), Font.BITMAP_W, Font.BITMAP_H,
                        nowChar % EACH_CHAR_NUM,
                        font.getXb(), font.getYb(), font.getQ(), false);
                float charWidthShould = font.getQ().x1() - font.getQ().x0();
                float charHeightShould = font.getQ().y1() - font.getQ().y0();
                float spaceLeftToCharShould = font.getQ().x0() - lastXShould;
                float spaceUpToCharShould = font.getQ().y0() - lastYShould;
                float nowX0 = lastXReal + spaceLeftToCharShould * scaleX;
                float nowY0 = lineStartPosY + spaceUpToCharShould * scaleY;

                Font.drawBoxTC(
                        nowX0, nowY0 + realCharHeight * 0.8f,
                        nowX0 + charWidthShould * scaleX,
                        nowY0 + charHeightShould * scaleY + realCharHeight * 0.8f,
                        font.getQ().s0(), font.getQ().t0(), font.getQ().s1(),
                        font.getQ().t1()
                );

                lastXReal = nowX0 + charWidthShould * scaleX;
                lastYReal = lineStartPosY;
                lastXShould = font.getQ().x1();
                lastYShould = lineStartPosY;
            }

            i = ti - 1;
        }
        glEnd();
    }

    /**
     * <p>Getter for the field <code>textColor</code>.</p>
     *
     * @return return
     */
    public Vector4fc getTextColor() {
        return new Vector4f(textColor);
    }

    /**
     * <p>Setter for the field <code>textColor</code>.</p>
     *
     * @param textColor textColor
     */
    public void setTextColor(Vector4fc textColor) {
        this.textColor.set(textColor);
    }

    /**
     * <p>Setter for the field <code>textColor</code>.</p>
     *
     * @param x textColor.x
     * @param y textColor.y
     * @param z textColor.z
     * @param w textColor.w
     */
    public void setTextColor(float x, float y, float z, float w) {
        this.textColor.set(x, y, z, w);
    }
}

