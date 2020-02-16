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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.stb.STBTruetype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.opengl.GL11.*;

public class DrawTextStruct {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(DrawTextStruct.class);

    private Font font = Font.getCurrentFont();
    private float leftTopPosX = Float.NaN;
    private float leftTopPosY = Float.NaN;
    private float centerPosX = Float.NaN;
    private float centerPosY = Float.NaN;
    private float width = Float.NaN;
    private float height = Float.NaN;
    private float scaleX = Float.NaN;
    private float scaleY = Float.NaN;
    private float characterSpace = 0;

    private static final Vector4fc DEFAULT_TEXT_COLOR = Colors.BLACK;
    private final Vector4f color = new Vector4f(1, 1, 1, 1);
    private String text = "";

    public DrawTextStruct() {

    }

    public DrawTextStruct(DrawTextStruct drawTextStruct) {
        this.setFont(drawTextStruct.getFont());
        this.setLeftTopPosX(drawTextStruct.getLeftTopPosX());
        this.setLeftTopPosY(drawTextStruct.getLeftTopPosY());
        this.setCenterPosX(drawTextStruct.getCenterPosX());
        this.setCenterPosY(drawTextStruct.getCenterPosY());
        this.setWidth(drawTextStruct.getWidth());
        this.setHeight(drawTextStruct.getHeight());
        this.setScaleX(drawTextStruct.getScaleX());
        this.setScaleY(drawTextStruct.getScaleY());
        this.setCharacterSpace(drawTextStruct.getCharacterSpace());
        if (drawTextStruct.color != null) {
            this.color.set(drawTextStruct.color);
        } else {
            this.color.set(DEFAULT_TEXT_COLOR);
        }
        this.setText(drawTextStruct.getText());
    }

    public void setLeftTopPosXY(float leftTopPosX, float leftTopPosY) {
        this.setLeftTopPosX(leftTopPosX);
        this.setLeftTopPosY(leftTopPosY);
    }

    public void setCenterPosXY(float centerPosX, float centerPosY) {
        this.setCenterPosX(centerPosX);
        this.setCenterPosY(centerPosY);
    }

    public void setWidthHeight(float width, float height) {
        this.setWidth(width);
        this.setHeight(height);
    }

    public void setScaleXY(float scaleX, float scaleY) {
        this.setScaleX(scaleX);
        this.setScaleY(scaleY);
    }

    public void bakePosXY() {
        assert (!Float.isNaN(this.getWidth()));
        assert (!Float.isNaN(this.getHeight()));

        if (!Float.isNaN(getLeftTopPosX()) && !Float.isNaN(getLeftTopPosY())) {
            setCenterPosX(getLeftTopPosX() + getWidth() / 2f);
            setCenterPosY(getLeftTopPosY() + getHeight() / 2f);
        } else if (!Float.isNaN(getCenterPosX()) && !Float.isNaN(getCenterPosY())) {
            setLeftTopPosX(getCenterPosX() - getWidth() / 2f);
            setLeftTopPosY(getCenterPosY() - getHeight() / 2f);
        } else {
            LOGGER.info("all pos be NaN : {}", this);
        }
    }

    public void calculateScaleXYFromWidthHeight() {
        assert (!Float.isNaN(this.getWidth()) || !Float.isNaN(this.getHeight()));
        assert (this.getText() != null);

        this.getFont().bind();
        getFont().getXb().put(0, 0);
        getFont().getYb().put(0, 0);

        glEnable(GL_TEXTURE_2D);

        float lastXReal = 0;
        float lastYReal = 0;
        float lastXShould = 0;
        float lastYShould = 0;

        float x3 = Float.MIN_VALUE;
        float y3 = Float.MIN_VALUE;

        for (int i = 0; i < this.getText().length(); i++) {
            if (this.getText().charAt(i) < 32) {
                continue;
            }
            glBindTexture(GL_TEXTURE_2D, getFont().getFontTextures().getPrimitive(this.getText().charAt(i) / Font.EACH_CHAR_NUM));
            glBegin(GL_QUADS);
            STBTruetype.stbtt_GetPackedQuad(getFont().getCharDatas().get(this.getText().charAt(i) / Font.EACH_CHAR_NUM), Font.BITMAP_W, Font.BITMAP_H,
                    this.getText().charAt(i) % Font.EACH_CHAR_NUM, getFont().getXb(), getFont().getYb(), getFont().getQ(), false);
//            LOGGER.debug("x0:" + q.x0() + " x1:" + q.x1() + " y0:" +
//            q.y0() + " y1:" + q.y1());
            float charWidthShould = getFont().getQ().x1() - getFont().getQ().x0();
            float charHeightShould = getFont().getQ().y1() - getFont().getQ().y0();
            float spaceLeftToCharShould = getFont().getQ().x0() - lastXShould;
            float spaceUpToCharShould = getFont().getQ().y0() - lastYShould;
            float nowX0 = lastXReal + spaceLeftToCharShould;
            float nowY0 = 0;
//            LOGGER.debug(charWidthShould + " " + charHeightShould + "
//            " + spaceLeftToCharShould + " " + spaceUpToCharShould + " " +
//            nowX0 + " " + nowY0);
//            drawBoxTC(
//                    nowX0, nowY0, nowX0 + charWidthShould * 1, nowY0 +
//                    charHeightShould * 1,
//                    q.s0(), q.t0(), q.s1(), q.t1()
//            );
            x3 = Math.max(x3, nowX0 + charWidthShould);
            y3 = Math.max(y3, nowY0 + charHeightShould);
            lastXReal = nowX0 + charWidthShould * 1;
            lastYReal = 0;
            lastXShould = getFont().getQ().x1();
            lastYShould = 0;
            glEnd();
        }
        assert (!Float.isNaN(this.getWidth()) || !Float.isNaN(this.getHeight()));
        float calculatedScaleX = Float.isNaN(this.getWidth()) ? Float.NaN : this.getWidth() / (x3 - 0);
        float calculatedScaleY = Float.isNaN(this.getHeight()) ? Float.NaN : this.getHeight() / (y3 - 0);
        if (Float.isNaN(calculatedScaleX)) {
            calculatedScaleX = calculatedScaleY;
        } else if (Float.isNaN(calculatedScaleY)) {
            calculatedScaleY = calculatedScaleX;
        }
        this.setScaleXY(calculatedScaleX, calculatedScaleY);
        if (Float.isNaN(this.getHeight())) {
            this.setHeight(this.getWidth() / x3 * y3);
        }
        if (Float.isNaN(this.getWidth())) {
            this.setWidth(this.getHeight() / y3 * x3);
        }
    }

    public void bake() {
        if (!Float.isNaN(this.getScaleX()) && !Float.isNaN(this.getScaleY()) && !Float.isNaN(this.getHeight())) {
            //do nothing
        } else if (!Float.isNaN(this.getScaleX()) && !Float.isNaN(this.getScaleY())) {
            DrawTextStruct drawTextStruct = new DrawTextStruct(this);
            drawTextStruct.setColor(new Vector4f(0, 0, 0, 0));
            drawTextStruct.setLeftTopPosX(0);
            drawTextStruct.setLeftTopPosY(0);
            getFont().drawTextLeftTop(drawTextStruct);
            this.setWidth(drawTextStruct.getWidth());
            this.setHeight(drawTextStruct.getHeight());
        } else if (!Float.isNaN(this.getWidth()) || !Float.isNaN(this.getHeight())) {
            this.calculateScaleXYFromWidthHeight();
        }
        this.bakePosXY();
    }

    public void draw() {
        if (Float.isNaN(getLeftTopPosX()) || Float.isNaN(getLeftTopPosY()) || Float.isNaN(getCenterPosX()) || Float.isNaN(getCenterPosY()) || Float.isNaN(getScaleX()) || Float.isNaN(getScaleY()) || Float.isNaN(getCharacterSpace()) || getText() == null || Float.isNaN(getHeight())) {
            this.bake();
            if (Float.isNaN(getLeftTopPosX()) || Float.isNaN(getLeftTopPosY()) || Float.isNaN(getCenterPosX()) || Float.isNaN(getCenterPosY()) || Float.isNaN(getScaleX()) || Float.isNaN(getScaleY()) || Float.isNaN(getCharacterSpace()) || getText() == null || Float.isNaN(getHeight())) {
                LOGGER.info("This DrawTextStruct still cannot draw after bake, thus we will not draw it : {}" + this.toString());
            } else {
                this.getFont().drawTextLeftTop(this);
            }
        }
    }

    public float getLeftTopPosX() {
        return leftTopPosX;
    }

    public void setLeftTopPosX(float leftTopPosX) {
        this.leftTopPosX = leftTopPosX;
    }

    public float getLeftTopPosY() {
        return leftTopPosY;
    }

    public void setLeftTopPosY(float leftTopPosY) {
        this.leftTopPosY = leftTopPosY;
    }

    public float getCenterPosX() {
        return centerPosX;
    }

    public void setCenterPosX(float centerPosX) {
        this.centerPosX = centerPosX;
    }

    public float getCenterPosY() {
        return centerPosY;
    }

    public void setCenterPosY(float centerPosY) {
        this.centerPosY = centerPosY;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public Vector4fc getColor() {
        return new Vector4f(color);
    }

    public void setColor(Vector4fc color) {
        if (color == null) {
            color = DEFAULT_TEXT_COLOR;
        }
        this.color.set(color);
    }

    public float getCharacterSpace() {
        return characterSpace;
    }

    public void setCharacterSpace(float characterSpace) {
        this.characterSpace = characterSpace;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        if (font == null) {
            font = Font.getCurrentFont();
        }
        this.font = font;
    }

    @Override
    public String toString() {
        return "DrawTextStruct{" +
                "font=" + getFont() +
                ", leftTopPosX=" + getLeftTopPosX() +
                ", leftTopPosY=" + getLeftTopPosY() +
                ", centerPosX=" + getCenterPosX() +
                ", centerPosY=" + getCenterPosY() +
                ", width=" + getWidth() +
                ", height=" + getHeight() +
                ", scaleX=" + getScaleX() +
                ", scaleY=" + getScaleY() +
                ", characterSpace=" + getCharacterSpace() +
                ", color=" + color +
                ", text='" + getText() + '\'' +
                '}';
    }

}
