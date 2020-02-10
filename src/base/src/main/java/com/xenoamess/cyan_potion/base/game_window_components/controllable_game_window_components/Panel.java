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
import com.xenoamess.cyan_potion.base.events.EventsEvent;
import com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent;
import com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTree;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.visual.Picture;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;


/**
 * <p>Panel class.</p>
 *
 * @author XenoAmess
 * @version 0.156.1-SNAPSHOT
 */
public class Panel extends AbstractControllableGameWindowComponent {
    private final List<AbstractGameWindowComponent> contents = new Vector<>();
    private final Picture backgroundPicture = new Picture();
    private final GameWindowComponentTree subGameWindowComponentTree = new GameWindowComponentTree(this.getGameManager(), new AbstractGameWindowComponent(this.getGameWindow()) {
        @Override
        public void update() {
        }

        @Override
        public void draw() {
        }

        @Override
        public void initProcessors() {
        }
    });

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
        this.getBackgroundPicture().setBindable(backgroundBindable);
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
            boolean result = this.contents.add(gameWindowComponent);
            if (subGameWindowComponentTree.getRoot() != null) {
                gameWindowComponent.addToGameWindowComponentTree(subGameWindowComponentTree.getRoot());
            }
            return result;
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
            AbstractGameWindowComponent result = this.contents.remove(index);
            result.close();
            return result;
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
            boolean result = this.contents.remove(gameWindowComponent);
            gameWindowComponent.close();
            return result;
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
            for (AbstractGameWindowComponent abstractGameWindowComponent : this.contents) {
                abstractGameWindowComponent.close();
            }
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
    public List<AbstractGameWindowComponent> getContents() {
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
        for (AbstractGameWindowComponent abstractGameWindowComponent : this.getContents()) {
            if (abstractGameWindowComponent.getGameWindowComponentTreeNode() == null) {
                abstractGameWindowComponent.addToGameWindowComponentTree(subGameWindowComponentTree.getRoot());
            }
        }
        getBackgroundPicture().cover(this);
        this.subGameWindowComponentTree.update();
//        synchronized (this.contents) {
//            for (AbstractGameWindowComponent gameWindowComponent : this.contents) {
//                gameWindowComponent.update();
//            }
//        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean ifVisibleThenDraw() {
        this.getBackgroundPicture().draw(this.getGameWindow());
        this.subGameWindowComponentTree.draw();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Event process(Event event) {
        synchronized (this) {
            Set<Event> res = this.subGameWindowComponentTree.process(event);
            if (!res.isEmpty()) {
                return new EventsEvent(res);
            } else {
                return super.process(event);
            }
        }

    }

    public void close() {
        subGameWindowComponentTree.getRoot().close();
        this.clearContents();
        super.close();
    }

    public Picture getBackgroundPicture() {
        return backgroundPicture;
    }
}
