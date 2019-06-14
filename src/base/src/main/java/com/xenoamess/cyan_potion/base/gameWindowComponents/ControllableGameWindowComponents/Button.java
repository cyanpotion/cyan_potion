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
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Font;
import com.xenoamess.cyan_potion.base.visual.Picture;
import org.joml.Vector4f;


/**
 * @author XenoAmess
 */
public class Button extends AbstractControllableGameWindowComponent {
    private final Picture buttonPicture = new Picture();
    private String buttonText;

    public Button(GameWindow gameWindow, Texture buttonTexture) {
        this(gameWindow, buttonTexture, null);
    }

    public Button(GameWindow gameWindow, Texture buttonTexture,
                  String buttonText) {
        super(gameWindow);
        this.buttonPicture.setBindable(buttonTexture);
        this.setButtonText(buttonText);
    }

    @Override
    public void update() {
        super.update();
        this.buttonPicture.cover(this);
    }

    @Override
    public void ifVisibleThenDraw() {
        this.buttonPicture.draw(this.getGameWindow());
        if (this.getButtonText() != null) {
            this.getGameWindow().drawTextFillArea(Font.getCurrentFont(),
                    this.getLeftTopPosX() + this.getWidth() / 2,
                    this.getLeftTopPosY() + this.getHeight() / 2,
                    this.getWidth() / 6 * 4, this.getHeight() / 6 * 4,
                    0, new Vector4f(1, 1, 0, 1), this.getButtonText());
        }
    }


    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

}
