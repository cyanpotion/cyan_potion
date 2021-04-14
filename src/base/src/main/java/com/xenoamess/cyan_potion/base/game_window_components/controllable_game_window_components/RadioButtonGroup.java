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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RadioButtonGroup
 * <p>
 * a manager of a group of radio buttons, and operations / management.
 *
 * @author XenoAmess
 * @version 0.162.3
 * @see RadioButton
 */
@EqualsAndHashCode
@ToString
public class RadioButtonGroup implements Closeable {
    /**
     * the max buttons that can be selected.
     * notice that this value shall never be less than 1.
     * otherwise strange things might happen.
     * <p>
     * usually just use 1 is enough.
     */
    private final AtomicInteger selectLimit = new AtomicInteger();
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    private final ConcurrentLinkedDeque<RadioButton> selectedRadioButtons = new ConcurrentLinkedDeque<>();

    /**
     * <p>Constructor for RadioButtonGroup.</p>
     */
    public RadioButtonGroup() {
        this(1);
    }

    /**
     * <p>Constructor for RadioButtonGroup.</p>
     *
     * @param selectLimit this.selectLimit
     */
    public RadioButtonGroup(int selectLimit) {
        this.setSelectLimit(selectLimit);
    }

    /**
     * select a RadioButton.
     *
     * @param radioButton a
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
     *     {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.RadioButton}
     *                    object.
     * @return select succeed.
     */
    @SuppressWarnings("UnusedReturnValue")
    public synchronized boolean select(RadioButton radioButton) {
        assert (radioButton.getRadioButtonGroup() == this);
        if (!this.selectedRadioButtons.contains(radioButton)) {
            radioButton.select();
            this.selectedRadioButtons.remove(radioButton);
            this.selectedRadioButtons.addLast(radioButton);
            this.ensureSelectedRatioButtonsToSelectLimit();
            return true;
        } else {
            this.selectedRadioButtons.remove(radioButton);
            this.selectedRadioButtons.addLast(radioButton);
            this.ensureSelectedRatioButtonsToSelectLimit();
            return false;
        }
    }

    /**
     * deselect a RadioButton.
     *
     * @param radioButton a
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
     *     {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.RadioButton}
     *                    object.
     * @return deselect succeed.
     */
    @SuppressWarnings("UnusedReturnValue")
    public synchronized boolean deselect(RadioButton radioButton) {
        assert (radioButton.getRadioButtonGroup() == this);
        if (this.selectedRadioButtons.contains(radioButton)) {
            radioButton.deselect();
            this.selectedRadioButtons.remove(radioButton);
            return true;
        } else {
            return false;
        }
    }

    /**
     * <p>Getter for the field <code>selectLimit</code>.</p>
     *
     * @return a int.
     */
    public int getSelectLimit() {
        return selectLimit.get();
    }

    /**
     * <p>Setter for the field <code>selectLimit</code>.</p>
     *
     * @param selectLimit a int.
     */
    public void setSelectLimit(int selectLimit) {
        this.selectLimit.set(selectLimit);
    }

    /**
     * pop more Buttons than selectLimit, and deselect them.
     * will pop them one by one according to select timeline.
     */
    public synchronized void ensureSelectedRatioButtonsToSelectLimit() {
        while (selectedRadioButtons.size() > this.getSelectLimit()) {
            selectedRadioButtons.removeFirst().deselect();
        }
    }


    /**
     * delete all RadioButtons
     */
    public synchronized void clear() {
        while (!selectedRadioButtons.isEmpty()) {
            selectedRadioButtons.removeFirst().deselect();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        this.clear();
    }

    /**
     * get a copy of this.selectedRadioButtons
     *
     * @return a {@link java.util.List} object.
     */
    public List<RadioButton> getSelectedRadioButtons() {
        return new ArrayList<>(selectedRadioButtons);
    }
}
