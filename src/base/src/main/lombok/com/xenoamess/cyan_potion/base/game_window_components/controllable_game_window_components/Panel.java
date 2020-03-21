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
import com.xenoamess.cyan_potion.base.game_window_components.*;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.visual.Picture;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Set;
import java.util.Vector;


/**
 * <p>Panel class.</p>
 *
 * @author XenoAmess
 * @version 0.162.1-SNAPSHOT
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class Panel extends AbstractControllableGameWindowComponent {

    @Getter
    private final List<AbstractGameWindowComponent> contents = new Vector<>();

    @Getter
    private final Picture backgroundPicture = new Picture();

    @Getter(AccessLevel.PROTECTED)
    private final GameWindowComponentTree subGameWindowComponentTree = new GameWindowComponentTree(this.getGameManager(), new AbstractGameWindowComponent(this.getGameWindow()) {
        @Override
        protected void initProcessors() {
            //do nothing
        }
    });

    /**
     * UpdaterBuilder for {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.AbstractControllableGameWindowComponent}
     */
    public static final UpdaterBuilder<Panel> UPDATER_BUILDER_PANEL = new UpdaterBuilder<Panel>() {
        @Override
        public UpdaterInterface<Panel> build(UpdaterInterface<? super Panel> superUpdater) {
            return new Updater<Panel>(superUpdater) {
                @Override
                public boolean thisUpdate(Panel panel) {
                    for (AbstractGameWindowComponent abstractGameWindowComponent : panel.getContents()) {
                        if (abstractGameWindowComponent.getGameWindowComponentTreeNode() == null) {
                            abstractGameWindowComponent.addToGameWindowComponentTree(panel.getSubGameWindowComponentTree().getRoot());
                        }
                    }
                    panel.getBackgroundPicture().cover(panel);
                    panel.getSubGameWindowComponentTree().update();
                    return true;
                }
            };
        }
    };


    /**
     * default Updater for {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.AbstractControllableGameWindowComponent}
     */
    public static final UpdaterInterface<Panel> DEFAULT_UPDATER_PANEL = UPDATER_BUILDER_PANEL.build(AbstractControllableGameWindowComponent.DEFAULT_UPDATER_ABSTRACTCONTROLLABLEGAMEWINDOWCOMPONENT);


    /**
     * <p>Constructor for Panel.</p>
     *
     * @param gameWindow gameWindow
     */
    public Panel(GameWindow gameWindow) {
        this(gameWindow, null);
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
        this.setUpdater(
                UPDATER_BUILDER_PANEL.build(super.getUpdater())
        );
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
            if (getSubGameWindowComponentTree().getRoot() != null) {
                gameWindowComponent.addToGameWindowComponentTree(getSubGameWindowComponentTree().getRoot());
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
     * {@inheritDoc}
     */
    @Override
    public boolean ifVisibleThenDraw() {
        this.getBackgroundPicture().draw(this.getGameWindow());
        this.getSubGameWindowComponentTree().draw();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Event process(Event event) {
        synchronized (this) {
            Set<Event> res = this.getSubGameWindowComponentTree().process(event);
            if (!res.isEmpty()) {
                return new EventsEvent(res);
            } else {
                return super.process(event);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        getSubGameWindowComponentTree().getRoot().close();
        this.clearContents();
        super.close();
    }
}
