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
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.visual.Picture;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


/**
 * <p>Panel class.</p>
 *
 * @author XenoAmess
 * @version 0.155.2
 */
public class Panel extends AbstractControllableGameWindowComponent {
    private final List<AbstractGameWindowComponent> contents = new Vector<>();
    private final Picture backgroundPicture = new Picture();

    /**
     * <p>Constructor for Panel.</p>
     *
     * @param gameWindow gameWindow
     */
    public Panel(GameWindow gameWindow) {
        super(gameWindow);
    }

    /**
     * <p>Constructor for Panel.</p>
     *
     * @param gameWindow         a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     * @param backgroundBindable backgroundBindable
     */
    public Panel(GameWindow gameWindow, Bindable backgroundBindable) {
        super(gameWindow);
        this.backgroundPicture.setBindable(backgroundBindable);
    }

    /**
     * return this.contents.add(gameWindowComponent);
     *
     * @param gameWindowComponent gameWindowComponent
     * @return this.contents.add(gameWindowComponent)
     * @see #contents
     * @see List#add(Object)
     */
    public boolean addContent(AbstractGameWindowComponent gameWindowComponent) {
        synchronized (this.contents) {
            return this.contents.add(gameWindowComponent);
        }
    }

    /**
     * return this.contents.remove(index);
     *
     * @param index a int.
     * @return this.contents.remove(index);
     * @see #contents
     * @see List#remove(int)
     */
    public AbstractGameWindowComponent removeContent(int index) {
        synchronized (this.contents) {
            return this.contents.remove(index);
        }
    }

    /**
     * return this.contents.remove(gameWindowComponent);
     *
     * @param gameWindowComponent a {@link com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent} object.
     * @return this.contents.remove(gameWindowComponent);
     * @see #contents
     * @see List#remove(Object)
     */
    public boolean removeContent(AbstractGameWindowComponent gameWindowComponent) {
        synchronized (this.contents) {
            return this.contents.remove(gameWindowComponent);
        }
    }

    /**
     * return this.contents.clear();
     *
     * @see #contents
     * @see List#clear()
     */
    public void clearContents() {
        synchronized (this.contents) {
            this.contents.clear();
        }
    }

    /**
     * return new ArrayList(this.contents);
     *
     * @return new ArrayList(this.contents);
     * @see #contents
     * @see List#clear()
     */
    public List<AbstractGameWindowComponent> copyContents() {
        synchronized (this.contents) {
            return new ArrayList<>(this.contents);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        super.update();
        synchronized (this.contents) {
            for (AbstractGameWindowComponent gameWindowComponent : this.contents) {
                gameWindowComponent.update();
            }
        }
        backgroundPicture.cover(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw() {
        this.backgroundPicture.draw(this.getGameWindow());
        synchronized (this.contents) {
            for (AbstractGameWindowComponent gameWindowComponent : this.contents) {
                gameWindowComponent.draw();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Event process(Event event) {
        synchronized (this.contents) {
            for (AbstractGameWindowComponent gameWindowComponent : this.contents) {
                Event newEvent = gameWindowComponent.process(event);
                if (newEvent != event) {
                    return newEvent;
                }
            }
            return super.process(event);
        }
    }

}
