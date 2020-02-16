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

/**
 * TextPicture
 * TextPicture is a class that allow you to pack up some text into an {@link com.xenoamess.cyan_potion.base.visual.AbstractPicture}, and use it as an {@link com.xenoamess.cyan_potion.base.visual.AbstractPicture}.
 *
 * @author XenoAmess
 * @version 0.159.0-SNAPSHOT
 */
public class TextPicture extends AbstractPicture {
    private float centerPosX = Float.NaN;
    private float centerPosY = Float.NaN;
    private Font font = Font.getCurrentFont();
    private float characterSpace = Float.NaN;
    private final Vector4f color = new Vector4f(Colors.BLACK);
    private String text = "";

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(AbstractScene scene) {
        TextPicture.draw(scene.getGameWindow(),
                scene.absolutePosToRelativeX(this.getLeftTopPosX()),
                scene.absolutePosToRelativeY(this.getLeftTopPosY()),
                scene.absolutePosToRelativeX(this.getCenterPosX()),
                scene.absolutePosToRelativeY(this.getCenterPosY()),
                this.getWidth() * scene.getScale(),
                this.getHeight() * scene.getScale(),
                this.getCharacterSpace() * scene.getScale(),
                this.getFont(),
                this.getColor().mul(this.getColorScale(), new Vector4f()),
                this.getText());
    }

    /**
     * <p>draw.</p>
     *
     * @param gameWindow     a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     * @param leftTopPosX    a float.
     * @param leftTopPosY    a float.
     * @param centerPosX     a float.
     * @param centerPosY     a float.
     * @param width          a float.
     * @param height         a float.
     * @param characterSpace a float.
     * @param font           a {@link com.xenoamess.cyan_potion.base.visual.Font} object.
     * @param color          a {@link org.joml.Vector4fc} object.
     * @param text           a {@link java.lang.String} object.
     */
    public static void draw(GameWindow gameWindow, float leftTopPosX, float leftTopPosY, float centerPosX, float centerPosY, float width, float height, float characterSpace, Font font, Vector4fc color, String text) {
        leftTopPosX = leftTopPosX / (float) gameWindow.getLogicWindowWidth() * (float) gameWindow.getRealWindowWidth();
        leftTopPosY = leftTopPosY / (float) gameWindow.getLogicWindowHeight() * (float) gameWindow.getRealWindowHeight();
        centerPosX = centerPosX / (float) gameWindow.getLogicWindowWidth() * (float) gameWindow.getRealWindowWidth();
        centerPosY = centerPosY / (float) gameWindow.getLogicWindowHeight() * (float) gameWindow.getRealWindowHeight();

        width = width / (float) gameWindow.getLogicWindowHeight() * (float) gameWindow.getRealWindowHeight();
        height = height / (float) gameWindow.getLogicWindowHeight() * (float) gameWindow.getRealWindowHeight();

        characterSpace = characterSpace / (float) gameWindow.getLogicWindowHeight() * (float) gameWindow.getRealWindowHeight();

        DrawTextStruct drawTextStruct = new DrawTextStruct();
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


    /**
     * {@inheritDoc}
     */
    @Override
    public float getCenterPosX() {
        return centerPosX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCenterPosX(float centerPosX) {
        this.centerPosX = centerPosX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getCenterPosY() {
        return centerPosY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCenterPosY(float centerPosY) {
        this.centerPosY = centerPosY;
    }

    /**
     * <p>Getter for the field <code>font</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.visual.Font} object.
     */
    public Font getFont() {
        return font;
    }

    /**
     * <p>Setter for the field <code>font</code>.</p>
     *
     * @param font a {@link com.xenoamess.cyan_potion.base.visual.Font} object.
     */
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * <p>Getter for the field <code>characterSpace</code>.</p>
     *
     * @return a float.
     */
    public float getCharacterSpace() {
        return characterSpace;
    }

    /**
     * <p>Setter for the field <code>characterSpace</code>.</p>
     *
     * @param characterSpace a float.
     */
    public void setCharacterSpace(float characterSpace) {
        this.characterSpace = characterSpace;
    }

    /**
     * <p>Getter for the field <code>color</code>.</p>
     *
     * @return a {@link org.joml.Vector4f} object.
     */
    public Vector4f getColor() {
        return new Vector4f(color);
    }

    /**
     * <p>Setter for the field <code>color</code>.</p>
     *
     * @param color a {@link org.joml.Vector4f} object.
     */
    public void setColor(Vector4f color) {
        this.color.set(color);
    }

    /**
     * <p>Getter for the field <code>text</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getText() {
        return text;
    }

    /**
     * <p>Setter for the field <code>text</code>.</p>
     *
     * @param text a {@link java.lang.String} object.
     */
    public void setText(String text) {
        this.text = text;
    }
}
