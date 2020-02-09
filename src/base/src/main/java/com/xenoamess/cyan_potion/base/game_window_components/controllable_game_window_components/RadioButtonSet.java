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

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class RadioButtonSet implements Closeable {
    /**
     * the max buttons that can be selected.
     * notice that this value shall never be less than 1.
     * otherwise strange things might happen.
     * <p>
     * usually just use 1 is enough.
     */
    private final AtomicInteger selectLimit = new AtomicInteger();
    private final ConcurrentLinkedDeque<RadioButton> selectedRadioButtons = new ConcurrentLinkedDeque<>();

    public RadioButtonSet() {
        this(1);
    }

    public RadioButtonSet(int selectLimit) {
        this.setSelectLimit(selectLimit);
    }

    public synchronized boolean select(RadioButton radioButton) {
        if (!this.selectedRadioButtons.contains(radioButton)) {
            if (radioButton.select()) {
                this.selectedRadioButtons.addLast(radioButton);
                this.ensureSelectedRatioButtonsToSelectLimit();
            }
            return true;
        } else {
            this.selectedRadioButtons.remove(radioButton);
            this.selectedRadioButtons.addLast(radioButton);
            this.ensureSelectedRatioButtonsToSelectLimit();
            return false;
        }
    }

    public synchronized boolean deselect(RadioButton radioButton) {
        if (this.selectedRadioButtons.contains(radioButton)) {
            if (radioButton.deselect()) {
                this.selectedRadioButtons.remove(radioButton);
            }
            return true;
        } else {
            return false;
        }
    }

    public int getSelectLimit() {
        return selectLimit.get();
    }

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


    public synchronized void clear() {
        while (selectedRadioButtons.size() > 0) {
            selectedRadioButtons.removeFirst().deselect();
        }
    }

    @Override
    public void close() {
        this.clear();
    }

    public List<RadioButton> getSelectedRadioButtons() {
        return new ArrayList<>(selectedRadioButtons);
    }
}
