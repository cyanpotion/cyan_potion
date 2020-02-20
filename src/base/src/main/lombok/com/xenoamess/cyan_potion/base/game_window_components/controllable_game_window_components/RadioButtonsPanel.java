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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.render.Texture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * RadioButtonsPanel
 * <p>
 * a panel which can contains a set of radio buttons.
 * <p>
 * notice that if you want to use this class,
 * you might must have some knowledge about {@link com.xenoamess.cyan_potion.base.game_window_components.Drawer} and {@link com.xenoamess.cyan_potion.base.game_window_components.Updater} first.
 *
 * @author XenoAmess
 * @version 0.160.0-SNAPSHOT
 */
public class RadioButtonsPanel extends Panel {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(RadioButtonsPanel.class);

    private final RadioButtonGroup radioButtonGroup = new RadioButtonGroup();

    /**
     * <p>Constructor for RadioButtonsPanel.</p>
     *
     * @param gameWindow a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     */
    public RadioButtonsPanel(GameWindow gameWindow) {
        super(gameWindow);
    }

    /**
     * <p>Constructor for RadioButtonsPanel.</p>
     *
     * @param gameWindow         a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     * @param backgroundBindable a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     */
    public RadioButtonsPanel(GameWindow gameWindow, Bindable backgroundBindable) {
        super(gameWindow, backgroundBindable);
    }

    /**
     * <p>Getter for the field <code>radioButtonSet</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.RadioButtonGroup} object.
     */
    public RadioButtonGroup getRadioButtonGroup() {
        return radioButtonGroup;
    }

    /**
     * <p>createNewRadioButton.</p>
     *
     * @param buttonText         a {@link java.lang.String} object.
     * @param bindableSelected   a {@link com.xenoamess.cyan_potion.base.render.Texture} object.
     * @param bindableDeselected a {@link com.xenoamess.cyan_potion.base.render.Texture} object.
     * @return a {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.RadioButton} object.
     */
    public RadioButton createNewRadioButton(String buttonText, Texture bindableSelected, Texture bindableDeselected) {
        RadioButton radioButton = new RadioButton(this.getGameWindow(), this.getRadioButtonGroup(), buttonText, bindableSelected, bindableDeselected);
        this.addContent(radioButton);
        return radioButton;
    }

    /**
     * <p>createNewRadioButton.</p>
     *
     * @param <T>                class of the returned RadioButton.
     * @param tClass             class of the returned RadioButton.
     * @param buttonText         a {@link java.lang.String} object.
     * @param bindableSelected   a {@link com.xenoamess.cyan_potion.base.render.Texture} object.
     * @param bindableDeselected a {@link com.xenoamess.cyan_potion.base.render.Texture} object.
     * @return a {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.RadioButton} object.
     */
    public <T extends RadioButton> T createNewRadioButton(Class<T> tClass, String buttonText, Texture bindableSelected, Texture bindableDeselected) {
        T radioButton = null;
        try {
            Constructor<T> constructor = tClass.getConstructor(GameWindow.class, RadioButtonGroup.class, String.class, Texture.class, Texture.class);
            radioButton = constructor.newInstance(this.getGameWindow(), this.getRadioButtonGroup(), buttonText, bindableSelected, bindableDeselected);
            this.addContent(radioButton);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            LOGGER.error("cannot create RadioButton from (GameWindow,RadioButtonGroup,String,Texture,Texture), class:{}", tClass, e);
        }
        return radioButton;
    }

    /**
     * <p>getSelectedRadioButtons.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<RadioButton> getSelectedRadioButtons() {
        return this.getRadioButtonGroup().getSelectedRadioButtons();
    }

    /**
     * <p>getRadioButtons.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<RadioButton> getRadioButtons() {
        ArrayList<RadioButton> radioButtons = new ArrayList<>();
        for (AbstractGameWindowComponent abstractGameWindowComponent : this.getContents()) {
            radioButtons.add((RadioButton) abstractGameWindowComponent);
        }
        return radioButtons;
    }

    /**
     * <p>removeRadioButton.</p>
     *
     * @param radioButton a {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.RadioButton} object.
     * @return a boolean.
     */
    public boolean removeRadioButton(RadioButton radioButton) {
        boolean result = this.removeContent(radioButton);
        radioButton.close();
        return result;
    }

    /**
     * <p>removeAllRadioButtons.</p>
     */
    public void removeAllRadioButtons() {
        for (RadioButton radioButton : this.getRadioButtons()) {
            this.removeRadioButton(radioButton);
        }
    }
}
