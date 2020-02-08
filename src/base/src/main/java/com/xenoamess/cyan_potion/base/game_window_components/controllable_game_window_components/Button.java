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
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.visual.Font;
import com.xenoamess.cyan_potion.base.visual.Picture;
import org.joml.Vector4f;
import org.joml.Vector4fc;


/**
 * <p>Button class.</p>
 *
 * @author XenoAmess
 * @version 0.155.4-SNAPSHOT
 */
public class Button extends AbstractControllableGameWindowComponent {
    private final Picture buttonPicture = new Picture();
    private String buttonText;
    /**
     * color of the buttonText drawn.
     */
    private final Vector4f textColor = new Vector4f(1, 1, 1, 1);

    /**
     * <p>Constructor for Button.</p>
     *
     * @param gameWindow gameWindow
     */
    public Button(GameWindow gameWindow) {
        this(gameWindow, null);
    }

    /**
     * <p>Constructor for Button.</p>
     *
     * @param gameWindow     gameWindow
     * @param buttonBindable picture of the button
     */
    public Button(GameWindow gameWindow, Bindable buttonBindable) {
        this(gameWindow, buttonBindable, null);
    }

    /**
     * <p>Constructor for Button.</p>
     *
     * @param gameWindow     gameWindow
     * @param buttonBindable picture of the button
     * @param buttonText     text of the button
     */
    public Button(GameWindow gameWindow, Bindable buttonBindable,
                  String buttonText) {
        super(gameWindow);
        this.getButtonPicture().setBindable(buttonBindable);
        this.setButtonText(buttonText);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        super.update();
        this.getButtonPicture().cover(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ifVisibleThenDraw() {
        this.getButtonPicture().draw(this.getGameWindow());
        if (this.getButtonText() != null) {
            this.getGameWindow().drawTextFillAreaCenter(Font.getCurrentFont(),
                    this.getLeftTopPosX() + this.getWidth() / 2,
                    this.getLeftTopPosY() + this.getHeight() / 2,
                    this.getWidth() / 6 * 4, this.getHeight() / 6 * 4,
                    0, this.getTextColor(), this.getButtonText());
        }
    }


    /**
     * <p>Getter for the field <code>buttonText</code>.</p>
     *
     * @return return
     */
    public String getButtonText() {
        return buttonText;
    }

    /**
     * <p>Setter for the field <code>buttonText</code>.</p>
     *
     * @param buttonText buttonText
     */
    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    /**
     * <p>Getter for the field <code>buttonPicture</code>.</p>
     *
     * @return this.buttonPicture
     */
    public Picture getButtonPicture() {
        return buttonPicture;
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

    public void setTextColor(float x, float y, float z, float w) {
        this.textColor.set(x, y, z, w);
    }
}
