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

package com.xenoamess.cyan_potion.base.gameWindowComponents.ControllableGameWindowComponents;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.visual.Font;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;


/**
 * @author XenoAmess
 */
public class TextBox extends AbstractControllableGameWindowComponent {
    private boolean wordWrap;
    private String contentString = "";
    private float charHeight = 20.0f;
    private Vector4f textColor = new Vector4f(1, 1, 1, 1);

    public TextBox(GameWindow gameWindow) {
        super(gameWindow);
        this.setWordWrap(this.getGameWindow().getGameManager().getDataCenter().getTextStructure().ifLanguageNeedWordWrap(this.getGameWindow().getGameManager().getDataCenter().getTextStructure().getCurrentLanguage()));
    }

    @Override
    public void ifVisibleThenDraw() {
        this.drawText();
    }


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

        float scaley = font.getScale(realCharHeight);
        float scalex = scaley;

        font.bind();

        float lineStartPosX = realLeftTopPosX;
        float lineStartPosY = realLeftTopPosY;


        font.getXb().put(0, lineStartPosX);
        font.getYb().put(0, lineStartPosY);

        font.getChardata().position(0);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, font.getFontTexture());

        glBegin(GL_QUADS);

        float lastxReal = lineStartPosX;
        float lastyReal = lineStartPosY;
        float lastxShould = lineStartPosX;
        float lastyShould = lineStartPosY;

        for (int i = 0; i < this.getContentString().length(); i++) {
            if (this.getTextColor() != null) {
                glColor4f(this.getTextColor().x, this.getTextColor().y,
                        this.getTextColor().z, this.getTextColor().w);
            }

            if (this.getContentString().charAt(i) == '\n') {
                lineStartPosX = realLeftTopPosX;
                lineStartPosY += realCharHeight;
                lastxReal = lineStartPosX;
                lastyReal = lineStartPosY;
                lastxShould = lineStartPosX;
                lastyShould = lineStartPosY;
                font.getXb().put(0, lineStartPosX);
                font.getYb().put(0, lineStartPosY);
                continue;
            }

            if (Character.isWhitespace(this.getContentString().charAt(i))) {
                stbtt_GetPackedQuad(
                        font.getChardata(), Font.BITMAP_W, Font.BITMAP_H,
                        this.getContentString().charAt(i),
                        font.getXb(), font.getYb(), font.getQ(), false);

                float charWidthShould = font.getQ().x1() - font.getQ().x0();
                float charHeightShould = font.getQ().y1() - font.getQ().y0();
                float spaceLeftToCharShould = font.getQ().x0() - lastxShould;
                float spaceUpToCharShould = font.getQ().y0() - lastyShould;
                float nowx0 = lastxReal + spaceLeftToCharShould * scalex;
                float nowy0 = lineStartPosY + spaceUpToCharShould * scaley;

                Font.drawBoxTC(
                        nowx0, nowy0 + realCharHeight * 0.8f,
                        nowx0 + charWidthShould * scalex,
                        nowy0 + charHeightShould * scaley + realCharHeight * 0.8f,
                        font.getQ().s0(), font.getQ().t0(), font.getQ().s1(),
                        font.getQ().t1()
                );

                lastxReal = nowx0 + charWidthShould * scalex;
                lastyReal = lineStartPosY;
                lastxShould = font.getQ().x1();
                lastyShould = lineStartPosY;
                continue;
            }

            float lastxRealBak = lastxReal;
            float lastyRealBak = lastyReal;
            float lastxShouldBak = lastxShould;
            float lastyShouldBak = lastyShould;
            float xbBak = font.getXb().get(0);
            float ybBak = font.getYb().get(0);
            int ti;

            for (ti = i; ti < this.getContentString().length() && (isWordWrap() || ti < i + 1); ti++) {
                char nowChar = this.getContentString().charAt(ti);
                if (Character.isWhitespace(nowChar)) {
                    break;
                }
                stbtt_GetPackedQuad(
                        font.getChardata(), Font.BITMAP_W, Font.BITMAP_H,
                        nowChar,
                        font.getXb(), font.getYb(), font.getQ(), false);
                float charWidthShould = font.getQ().x1() - font.getQ().x0();
                float charHeightShould = font.getQ().y1() - font.getQ().y0();
                float spaceLeftToCharShould = font.getQ().x0() - lastxShould;
                float spaceUpToCharShould = font.getQ().y0() - lastyShould;
                float nowx0 = lastxReal + spaceLeftToCharShould * scalex;
                float nowy0 = lineStartPosY + spaceUpToCharShould * scaley;

                lastxReal = nowx0 + charWidthShould * scalex;
                lastyReal = lineStartPosY;
                lastxShould = font.getQ().x1();
                lastyShould = lineStartPosY;
            }

            boolean ifChangeLine = lastxReal > lineStartPosX + realWidth;

            lastxReal = lastxRealBak;
            lastyReal = lastyRealBak;
            lastxShould = lastxShouldBak;
            lastyShould = lastyShouldBak;
            font.getXb().put(0, xbBak);
            font.getYb().put(0, ybBak);

            if (ifChangeLine) {
                lineStartPosX = realLeftTopPosX;
                lineStartPosY += realCharHeight;
                lastxReal = lineStartPosX;
                lastyReal = lineStartPosY;
                lastxShould = lineStartPosX;
                lastyShould = lineStartPosY;
                font.getXb().put(0, lineStartPosX);
                font.getYb().put(0, lineStartPosY);
            }

            for (ti = i; ti < this.getContentString().length() && (isWordWrap() || ti < i + 1); ti++) {
                char nowChar = this.getContentString().charAt(ti);
                if (Character.isWhitespace(nowChar)) {
                    break;
                }
                stbtt_GetPackedQuad(
                        font.getChardata(), Font.BITMAP_W, Font.BITMAP_H,
                        nowChar,
                        font.getXb(), font.getYb(), font.getQ(), false);
                float charWidthShould = font.getQ().x1() - font.getQ().x0();
                float charHeightShould = font.getQ().y1() - font.getQ().y0();
                float spaceLeftToCharShould = font.getQ().x0() - lastxShould;
                float spaceUpToCharShould = font.getQ().y0() - lastyShould;
                float nowx0 = lastxReal + spaceLeftToCharShould * scalex;
                float nowy0 = lineStartPosY + spaceUpToCharShould * scaley;

                Font.drawBoxTC(
                        nowx0, nowy0 + realCharHeight * 0.8f,
                        nowx0 + charWidthShould * scalex,
                        nowy0 + charHeightShould * scaley + realCharHeight * 0.8f,
                        font.getQ().s0(), font.getQ().t0(), font.getQ().s1(),
                        font.getQ().t1()
                );

                lastxReal = nowx0 + charWidthShould * scalex;
                lastyReal = lineStartPosY;
                lastxShould = font.getQ().x1();
                lastyShould = lineStartPosY;
            }

            i = ti - 1;
        }
        glEnd();
    }

    public boolean isWordWrap() {
        return wordWrap;
    }

    public void setWordWrap(boolean wordWrap) {
        this.wordWrap = wordWrap;
    }

    public String getContentString() {
        return contentString;
    }

    public void setContentString(String contentString) {
        this.contentString = contentString;
    }

    public float getCharHeight() {
        return charHeight;
    }

    public void setCharHeight(float charHeight) {
        this.charHeight = charHeight;
    }

    public Vector4f getTextColor() {
        return textColor;
    }

    public void setTextColor(Vector4f textColor) {
        this.textColor = textColor;
    }
}

