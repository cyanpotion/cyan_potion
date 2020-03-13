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
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * SingleContentPanel
 * A {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.Panel} who
 * allow at most one {@link com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent} as
 * content.
 * When adding a new {@link com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent}, the
 * exist ones will be closed automatically.
 * <p>
 * This class is used as a display panel or something.
 *
 * @author XenoAmess
 * @version 0.162.0
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class SingleContentPanel extends Panel {

    /**
     * <p>Constructor for SingleContentPanel.</p>
     *
     * @param gameWindow a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     */
    public SingleContentPanel(GameWindow gameWindow) {
        super(gameWindow);
    }

    /**
     * <p>Constructor for SingleContentPanel.</p>
     *
     * @param gameWindow         a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     * @param backgroundBindable a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     */
    public SingleContentPanel(GameWindow gameWindow, Bindable backgroundBindable) {
        super(gameWindow, backgroundBindable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addContent(AbstractGameWindowComponent gameWindowComponent) {
        this.clearContents();
        return super.addContent(gameWindowComponent);
    }
}
