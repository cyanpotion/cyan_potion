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
import org.joml.Vector4f;
import org.joml.Vector4fc;

public class TextPicture extends AbstractPicture {
    private float centerPosX = Float.NaN;
    private float centerPosY = Float.NaN;
    private Font font = Font.getCurrentFont();
    private float characterSpace = Float.NaN;
    private final Vector4f color = new Vector4f(Colors.BLACK);
    private String text = "";

    @Override
    public void draw(GameWindow gameWindow) {
        TextPicture.draw(gameWindow,
                this.getLeftTopPosX(),
                this.getLeftTopPosY(),
                this.getCenterPosX(),
                this.getCenterPosY(),
                this.getWidth(),
                this.getHeight(),
                this.getCharacterSpace(),
                this.getFont(),
                this.getColor().mul(this.getColorScale(), new Vector4f()),
                this.getText());
    }

    @Override
    public void draw(AbstractScene scene) {
        TextPicture.draw(scene.getGameWindow(),
                scene.relativePosToAbsoluteX(this.getLeftTopPosX()),
                scene.relativePosToAbsoluteY(this.getLeftTopPosY()),
                scene.relativePosToAbsoluteX(this.getCenterPosX()),
                scene.relativePosToAbsoluteY(this.getCenterPosY()),
                this.getWidth() * scene.getScale(),
                this.getHeight() * scene.getScale(),
                this.getCharacterSpace() * scene.getScale(),
                this.getFont(),
                this.getColor().mul(this.getColorScale(), new Vector4f()),
                this.getText());
    }

    public static void draw(GameWindow gameWindow, float leftTopPosX, float leftTopPosY, float centerPosX, float centerPosY, float width, float height, float characterSpace, Font font, Vector4fc color, String text) {
        leftTopPosX = leftTopPosX / (float) gameWindow.getLogicWindowWidth() * (float) gameWindow.getRealWindowWidth();
        leftTopPosY = leftTopPosY / (float) gameWindow.getLogicWindowHeight() * (float) gameWindow.getRealWindowHeight();
        centerPosX = centerPosX / (float) gameWindow.getLogicWindowWidth() * (float) gameWindow.getRealWindowWidth();
        centerPosY = centerPosY / (float) gameWindow.getLogicWindowHeight() * (float) gameWindow.getRealWindowHeight();

        width = width / (float) gameWindow.getLogicWindowHeight() * (float) gameWindow.getRealWindowHeight();
        height = height / (float) gameWindow.getLogicWindowHeight() * (float) gameWindow.getRealWindowHeight();

        characterSpace = characterSpace / (float) gameWindow.getLogicWindowHeight() * (float) gameWindow.getRealWindowHeight();

        Font.DrawTextStruct drawTextStruct = new Font.DrawTextStruct();
        drawTextStruct.setFont(font);
        drawTextStruct.setLeftTopPosXY(leftTopPosX, leftTopPosY);
        drawTextStruct.setCenterPosXY(centerPosX, centerPosY);
        drawTextStruct.setWidth(width);
        drawTextStruct.setHeight(height);
        drawTextStruct.setCharacterSpace(characterSpace);
        drawTextStruct.setColor(color);
        drawTextStruct.setText(text);
        drawTextStruct.draw();
    }


    @Override
    public float getCenterPosX() {
        return centerPosX;
    }

    @Override
    public void setCenterPosX(float centerPosX) {
        this.centerPosX = centerPosX;
    }

    @Override
    public float getCenterPosY() {
        return centerPosY;
    }

    @Override
    public void setCenterPosY(float centerPosY) {
        this.centerPosY = centerPosY;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public float getCharacterSpace() {
        return characterSpace;
    }

    public void setCharacterSpace(float characterSpace) {
        this.characterSpace = characterSpace;
    }

    public Vector4f getColor() {
        return new Vector4f(color);
    }

    public void setColor(Vector4f color) {
        this.color.set(color);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
