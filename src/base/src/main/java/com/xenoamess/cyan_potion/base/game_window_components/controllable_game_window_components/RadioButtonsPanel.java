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
import com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.render.Texture;

import java.util.ArrayList;
import java.util.List;

public class RadioButtonsPanel extends Panel {
    private final RadioButtonSet radioButtonSet = new RadioButtonSet();

    public RadioButtonsPanel(GameWindow gameWindow) {
        super(gameWindow);
    }

    public RadioButtonsPanel(GameWindow gameWindow, Bindable backgroundBindable) {
        super(gameWindow, backgroundBindable);
    }

    public RadioButtonSet getRadioButtonSet() {
        return radioButtonSet;
    }

    public RadioButton createNewRadioButton(String buttonText, Texture bindableSelected, Texture bindableDeselected) {
        RadioButton radioButton = new RadioButton(this.getGameWindow(), this.getRadioButtonSet(), buttonText, bindableSelected, bindableDeselected);
        this.addContent(radioButton);
        return radioButton;
    }

    public List<RadioButton> getSelectedRadioButtons() {
        return this.getRadioButtonSet().getSelectedRadioButtons();
    }

    public List<RadioButton> getRadioButtons() {
        ArrayList<RadioButton> radioButtons = new ArrayList<>();
        for (AbstractGameWindowComponent abstractGameWindowComponent : this.getContents()) {
            radioButtons.add((RadioButton) abstractGameWindowComponent);
        }
        return radioButtons;
    }

    public boolean removeRadioButton(RadioButton radioButton) {
        boolean result = this.removeContent(radioButton);
        radioButton.close();
        return result;
    }

    public void removeAllRadioButtons() {
        for (RadioButton radioButton : this.getRadioButtons()) {
            this.removeRadioButton(radioButton);
        }
    }
}
