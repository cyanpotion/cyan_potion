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
import com.xenoamess.cyan_potion.base.game_window_components.Updater;
import com.xenoamess.cyan_potion.base.game_window_components.UpdaterBuilder;
import com.xenoamess.cyan_potion.base.game_window_components.UpdaterInterface;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Picture;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * RadioButton
 * <p>
 * A RadioButton is one type of selection indicator in a list of options.
 * If an option is selected,
 * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.RadioButton#isSelected()} is true.
 * If the option is not selected,
 * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.RadioButton#isSelected()} is false.
 * When one RadioButton invoke
 * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.RadioButton#select()},
 * it will invoke
 * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.RadioButtonGroup#select(RadioButton)},
 * and then if the selected RadioButton s' number
 * ({@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.RadioButtonGroup#getSelectedRadioButtons()}'s size) in {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.RadioButtonGroup} is larger than {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.RadioButtonGroup#getSelectLimit()},
 * it will deselect the overdose RadioButton one by one according to the timeline.
 *
 * @author XenoAmess
 * @version 0.162.3
 * @see RadioButtonGroup
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class RadioButton extends Button {
    private final AtomicBoolean selected = new AtomicBoolean(false);
    @Getter
    private final RadioButtonGroup radioButtonGroup;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    @Setter
    private Bindable bindableSelected;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    @Setter
    private Bindable bindableDeselected;

    private final Vector4f textColorSelected = new Vector4f(this.getTextColor());

    private final Vector4f textColorDeselected = new Vector4f(this.getTextColor()).mul(0.5F, 0.5F, 0.5F, 1);


    /**
     * UpdaterBuilder for
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.AbstractControllableGameWindowComponent}
     */
    public static final UpdaterBuilder<RadioButton> UPDATER_BUILDER_RADIOBUTTON = new UpdaterBuilder<RadioButton>() {
        @Override
        public UpdaterInterface<RadioButton> build(UpdaterInterface<? super RadioButton> superUpdater) {
            return new Updater<RadioButton>(superUpdater) {
                @Override
                public boolean thisUpdate(RadioButton radioButton) {
                    radioButton.updatePictureBindable();
                    radioButton.updateTextColor();
                    return true;
                }
            };
        }
    };


    /**
     * default Updater for
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.AbstractControllableGameWindowComponent}
     */
    @SuppressWarnings("unused")
    public static final UpdaterInterface<RadioButton> DEFAULT_UPDATER_RADIO_BUTTON =
            UPDATER_BUILDER_RADIOBUTTON.build(AbstractControllableGameWindowComponent.DEFAULT_UPDATER_ABSTRACT_CONTROLLABLE_GAME_WINDOW_COMPONENT);


    /**
     * <p>Constructor for RadioButton.</p>
     *
     * @param gameWindow       a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     * @param radioButtonGroup a
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.RadioButtonGroup} object.
     */
    public RadioButton(GameWindow gameWindow, RadioButtonGroup radioButtonGroup) {
        this(gameWindow, radioButtonGroup, null);
    }

    /**
     * <p>Constructor for RadioButton.</p>
     *
     * @param gameWindow       a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     * @param radioButtonGroup a
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.RadioButtonGroup} object.
     * @param bindableSelected a {@link com.xenoamess.cyan_potion.base.render.Texture} object.
     */
    public RadioButton(GameWindow gameWindow, RadioButtonGroup radioButtonGroup, Texture bindableSelected) {
        this(gameWindow, radioButtonGroup, bindableSelected, null);

    }

    /**
     * <p>Constructor for RadioButton.</p>
     *
     * @param gameWindow       a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     * @param radioButtonGroup a
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.RadioButtonGroup} object.
     * @param buttonText       a {@link java.lang.String} object.
     * @param bindableSelected a {@link com.xenoamess.cyan_potion.base.render.Texture} object.
     */
    public RadioButton(
            GameWindow gameWindow,
            RadioButtonGroup radioButtonGroup,
            String buttonText,
            Texture bindableSelected
    ) {
        this(gameWindow, radioButtonGroup, buttonText, bindableSelected, null);
    }

    /**
     * <p>Constructor for RadioButton.</p>
     *
     * @param gameWindow         a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     * @param radioButtonGroup   a
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.RadioButtonGroup} object.
     * @param bindableSelected   a {@link com.xenoamess.cyan_potion.base.render.Texture} object.
     * @param bindableDeselected a {@link com.xenoamess.cyan_potion.base.render.Texture} object.
     */
    public RadioButton(
            GameWindow gameWindow,
            RadioButtonGroup radioButtonGroup,
            Texture bindableSelected,
            Texture bindableDeselected
    ) {
        this(gameWindow, radioButtonGroup, null, bindableSelected, bindableDeselected);
    }

    /**
     * <p>Constructor for RadioButton.</p>
     *
     * @param gameWindow         a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     * @param radioButtonGroup   a
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.RadioButtonGroup} object.
     * @param buttonText         a {@link java.lang.String} object.
     * @param bindableSelected   a {@link com.xenoamess.cyan_potion.base.render.Texture} object.
     * @param bindableDeselected a {@link com.xenoamess.cyan_potion.base.render.Texture} object.
     */
    public RadioButton(
            GameWindow gameWindow,
            RadioButtonGroup radioButtonGroup,
            String buttonText,
            Texture bindableSelected,
            Texture bindableDeselected
    ) {
        super(gameWindow, bindableSelected, buttonText);
        this.radioButtonGroup = radioButtonGroup;
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
        this.setUpdater(
                UPDATER_BUILDER_RADIOBUTTON.build(this.getUpdater())
        );
    }

    /**
     * make this RadioButton selected
     *
     * @return select succeed.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean select() {
        synchronized (this) {
            boolean res = this.selected.compareAndSet(false, true);
            if (res) {
                this.getRadioButtonGroup().select(this);
            }
            return res;
        }
    }

    /**
     * make this RadioButton not selected
     *
     * @return deselect succeed.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean deselect() {
        synchronized (this) {
            boolean res = this.selected.compareAndSet(true, false);
            if (res) {
                this.getRadioButtonGroup().deselect(this);
            }
            return res;
        }
    }


    /**
     * update Picture's Bindable according to whether being selected
     */
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

    /**
     * update TextColor according to whether being selected
     */
    protected void updateTextColor() {
        if (this.isSelected()) {
            this.setTextColor(this.getTextColorSelected());
        } else {
            this.setTextColor(this.getTextColorDeselected());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        super.close();
        this.deselect();
    }

    /**
     * <p>isSelected.</p>
     *
     * @return a boolean.
     */
    public boolean isSelected() {
        return this.selected.get();
    }

    /**
     * <p>Getter for the field <code>textColorSelected</code>.</p>
     *
     * @return a {@link org.joml.Vector4fc} object.
     */
    public Vector4fc getTextColorSelected() {
        return new Vector4f(textColorSelected);
    }

    /**
     * <p>Setter for the field <code>textColorSelected</code>.</p>
     *
     * @param textColorSelected a {@link org.joml.Vector4fc} object.
     */
    @SuppressWarnings("unused")
    public void setTextColorSelected(Vector4fc textColorSelected) {
        this.textColorSelected.set(textColorSelected);
    }

    /**
     * <p>Getter for the field <code>textColorDeselected</code>.</p>
     *
     * @return a {@link org.joml.Vector4fc} object.
     */
    public Vector4fc getTextColorDeselected() {
        return new Vector4f(textColorDeselected);
    }

    /**
     * <p>Setter for the field <code>textColorDeselected</code>.</p>
     *
     * @param textColorDeselected a {@link org.joml.Vector4fc} object.
     */
    @SuppressWarnings("unused")
    public void setTextColorDeselected(Vector4fc textColorDeselected) {
        this.textColorDeselected.set(textColorDeselected);
    }
}
