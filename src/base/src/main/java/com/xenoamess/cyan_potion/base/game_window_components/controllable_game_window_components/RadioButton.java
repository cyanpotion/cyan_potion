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
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Picture;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import java.util.concurrent.atomic.AtomicBoolean;

public class RadioButton extends Button {
    private final AtomicBoolean selected = new AtomicBoolean(false);
    private final RadioButtonSet radioButtonSet;

    private Bindable bindableSelected;
    private Bindable bindableDeselected;

    private final Vector4f textColorSelected = new Vector4f(this.getTextColor());

    private final Vector4f textColorDeselected = new Vector4f(this.getTextColor()).mul(0.5F, 0.5F, 0.5F, 1);

    public RadioButton(GameWindow gameWindow, RadioButtonSet radioButtonSet) {
        this(gameWindow, radioButtonSet, null);
    }

    public RadioButton(GameWindow gameWindow, RadioButtonSet radioButtonSet, Texture bindableSelected) {
        this(gameWindow, radioButtonSet, bindableSelected, null);

    }

    public RadioButton(GameWindow gameWindow, RadioButtonSet radioButtonSet, String buttonText, Texture bindableSelected) {
        this(gameWindow, radioButtonSet, buttonText, bindableSelected, null);
    }

    public RadioButton(GameWindow gameWindow, RadioButtonSet radioButtonSet, Texture bindableSelected, Texture bindableDeselected) {
        this(gameWindow, radioButtonSet, null, bindableSelected, null);
    }

    public RadioButton(GameWindow gameWindow, RadioButtonSet radioButtonSet, String buttonText, Texture bindableSelected, Texture bindableDeselected) {
        super(gameWindow, bindableSelected, buttonText);
        this.radioButtonSet = radioButtonSet;
        this.setBindableSelected(bindableSelected);
        this.setBindableDeselected(bindableDeselected);
        this.registerOnMouseButtonLeftDownCallback(
                event -> {
                    if (this.isSelected()) {
                        this.deselect();
                    } else {
                        this.select();
                    }
                    return null;
                }
        );
    }

    public boolean select() {
        synchronized (this) {
            boolean res = this.selected.compareAndSet(false, true);
            if (res) {
                this.getRadioButtonSet().select(this);
            }
            return res;
        }
    }

    public boolean deselect() {
        synchronized (this) {
            boolean res = this.selected.compareAndSet(true, false);
            if (res) {
                this.getRadioButtonSet().deselect(this);
            }
            return res;
        }
    }

    @Override
    public void update() {
        super.update();
        this.updatePictureBindable();
        this.updateTextColor();
    }

    protected void updatePictureBindable() {
        Picture buttonPicture = this.getButtonPicture();
        if (this.getBindableDeselected() != null) {
            if (this.isSelected()) {
                buttonPicture.setBindable(this.getBindableSelected());
            } else {
                buttonPicture.setBindable(this.getBindableDeselected());
            }
        } else {
            if (this.isSelected()) {
                buttonPicture.setBindable(this.getBindableSelected());
                buttonPicture.setColorScale(new Vector4f(1, 1, 1, 1));
            } else {
                buttonPicture.setBindable(this.getBindableSelected());
                Vector4fc oldColorScale = buttonPicture.getColorScale();
                Vector4f newColorScale = new Vector4f(0.5F, 0.5F, 0.5F, 1).mul(oldColorScale);
                buttonPicture.setColorScale(newColorScale);
            }
        }
    }

    protected void updateTextColor() {
        if (this.isSelected()) {
            this.setTextColor(this.getTextColorSelected());
        } else {
            this.setTextColor(this.getTextColorDeselected());
        }
    }

    @Override
    public void close() {
        super.close();
        this.deselect();
    }

    //-----getters and setters-----

    public RadioButtonSet getRadioButtonSet() {
        return radioButtonSet;
    }

    public Bindable getBindableSelected() {
        return bindableSelected;
    }

    public void setBindableSelected(Texture bindableSelected) {
        this.bindableSelected = bindableSelected;
    }

    public Bindable getBindableDeselected() {
        return bindableDeselected;
    }

    public void setBindableDeselected(Texture bindableDeselected) {
        this.bindableDeselected = bindableDeselected;
    }

    public boolean isSelected() {
        return this.selected.get();
    }

    public Vector4fc getTextColorSelected() {
        return new Vector4f(textColorSelected);
    }

    public void setTextColorSelected(Vector4fc textColorSelected) {
        this.textColorSelected.set(textColorSelected);
    }

    public Vector4fc getTextColorDeselected() {
        return new Vector4f(textColorDeselected);
    }

    public void setTextColorDeselected(Vector4fc textColorDeselected) {
        this.textColorDeselected.set(textColorDeselected);
    }
}
