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

package com.xenoamess.cyan_potion.base.game_window_components;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.commonx.java.lang.IllegalArgumentExceptionUtilsx;
import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.areas.AbstractArea;
import com.xenoamess.cyan_potion.base.areas.AbstractMutableArea;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.events.RemoteCallEvent;
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AbstractGameWindowComponent class is an ancestor of all GameWindowComponent classes.
 * If you are implementing a GameWindowComponent, and wanna a easy start,
 * just implement AbstractControllableGameWindowComponent instead
 * <p>
 * if you want to override {@link #update()} / {@link #draw()} method,
 * Then you shall better go use {@link #setUpdater(UpdaterInterface)} / {@link #setDrawer(DrawerInterface)} instead.
 * to make life easier, we follow such a principle here.
 * 1. if you override the  {@link #update()} / {@link #draw()} method directly in a class, then this class MUST be a
 * final class.
 * 2. if the class is not a final class, then you should not override {@link #update()} / {@link #draw()} method.
 *
 * @author XenoAmess
 * @version 0.162.0
 * @see com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.AbstractControllableGameWindowComponent
 */
@EqualsAndHashCode
@ToString
public abstract class AbstractGameWindowComponent implements Closeable, AbstractMutableArea {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(AbstractGameWindowComponent.class);
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    private final GameWindow gameWindow;

    private final AtomicBoolean alive = new AtomicBoolean(true);

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    @Setter
    private GameWindowComponentTreeNode gameWindowComponentTreeNode;

    @Getter
    @Setter
    private float leftTopPosX = Float.NaN;

    @Getter
    @Setter
    private float leftTopPosY = Float.NaN;

    @Getter
    @Setter
    private float width = Float.NaN;

    @Getter
    @Setter
    private float height = Float.NaN;

    @Getter
    @Setter
    private UpdaterInterface updater = DEFAULT_UPDATER_ABSTRACTGAMEWINDOWCOMPONENT;

    @Getter
    @Setter
    private DrawerInterface drawer = DEFAULT_DRAWER_ABSTRACTGAMEWINDOWCOMPONENT;

    /**
     * UpdaterBuilder for {@link com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent}
     */
    public static final UpdaterBuilder<AbstractGameWindowComponent> UPDATER_BUILDER_ABSTRACTGAMEWINDOWCOMPONENT = new UpdaterBuilder<AbstractGameWindowComponent>() {
        @Override
        public UpdaterInterface<AbstractGameWindowComponent> build(UpdaterInterface<? super AbstractGameWindowComponent> superUpdater) {
            return new Updater<AbstractGameWindowComponent>() {
                @Override
                public boolean thisUpdate(AbstractGameWindowComponent abstractGameWindowComponent) {
                    return true;
                }
            };
        }
    };

    /**
     * DrawerBuilder for {@link com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent}
     */
    public static final DrawerBuilder<AbstractGameWindowComponent> DRAWER_BUILDER_ABSTRACTGAMEWINDOWCOMPONENT = new DrawerBuilder<AbstractGameWindowComponent>() {
        @Override
        public DrawerInterface<AbstractGameWindowComponent> build(DrawerInterface<? super AbstractGameWindowComponent> superUpdater) {
            return new Drawer<AbstractGameWindowComponent>() {
                @Override
                public boolean thisDraw(AbstractGameWindowComponent abstractGameWindowComponent) {
                    return true;
                }
            };
        }
    };


    /**
     * default Updater for {@link com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent}
     */
    public static final UpdaterInterface<AbstractGameWindowComponent> DEFAULT_UPDATER_ABSTRACTGAMEWINDOWCOMPONENT = UPDATER_BUILDER_ABSTRACTGAMEWINDOWCOMPONENT.build(null);

    /**
     * default Drawer for {@link com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent}
     */
    public static final DrawerInterface<AbstractGameWindowComponent> DEFAULT_DRAWER_ABSTRACTGAMEWINDOWCOMPONENT = DRAWER_BUILDER_ABSTRACTGAMEWINDOWCOMPONENT.build(null);


    /**
     * map of eventClass to processors.
     * EventProcess must be able to process the event Class.
     * notice that this eventClassToProcessorMap not works automatically,
     * and you must get the processor and invoke it by yourself.
     * you can see examples of this map/these functions about it.
     *
     * @see AbstractGameWindowComponent#getProcessor(String)
     * @see AbstractGameWindowComponent#getProcessor(Class)
     * @see AbstractGameWindowComponent#registerProcessor(String, EventProcessor)
     * @see AbstractGameWindowComponent#registerProcessor(Class, EventProcessor)
     */
    @Getter(AccessLevel.PROTECTED)
    private final Map<Class<? extends Event>, EventProcessor<? extends Event>> eventClassToProcessorMap = new ConcurrentHashMap<>();


    /**
     * <p>Constructor for AbstractGameWindowComponent.</p>
     *
     * @param gameWindow gameWindow
     */
    public AbstractGameWindowComponent(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        this.initRemoteCallEventProcessor();
        this.initProcessors();
    }

    /**
     * register a processor to deal with RemoteCallEvent
     *
     * @see com.xenoamess.cyan_potion.base.events.RemoteCallEvent
     */
    protected void initRemoteCallEventProcessor() {
        this.registerProcessor(
                RemoteCallEvent.class,
                remoteCallEvent -> {
                    if (remoteCallEvent.getGameWindowComponent() != AbstractGameWindowComponent.this) {
                        //if remoteCallEvent is not for this gameWindowComponent, then return event.
                        return remoteCallEvent;
                    }
                    return (Event) remoteCallEvent.getFunction().apply(AbstractGameWindowComponent.this);
                }
        );
    }

    /**
     * create an instance of gameWindowComponentClassName class.
     * this class must be subclass of AbstractGameWindowComponent
     * and must have a public constructor of [AbstractGameWindowComponent(GameWindow)].
     * otherwise the engine will exit directly.
     *
     * @param gameWindow                   a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     * @param gameWindowComponentClassName name of the class.
     * @return return
     */
    public static AbstractGameWindowComponent createGameWindowComponentFromClassName(GameWindow gameWindow,
                                                                                     String gameWindowComponentClassName) {
        IllegalArgumentExceptionUtilsx.isAnyNullInParamsThenThrowIllegalArgumentException(gameWindowComponentClassName);
        AbstractGameWindowComponent gameWindowComponent = null;
        try {
            gameWindowComponent =
                    (AbstractGameWindowComponent) DataCenter.class.getClassLoader().loadClass(gameWindowComponentClassName).getConstructor(GameWindow.class).newInstance(gameWindow);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            LOGGER.error("AbstractGameWindowComponent.createGameWindowComponentFromClassName(GameWindow gameWindow, " +
                    "String gameWindowComponentClassName) fails:{},{}", gameWindow, gameWindowComponentClassName, e);
            System.exit(-1);
        }
        return gameWindowComponent;
    }


    /**
     * set leftTopPosX, leftTopPosY, width, height one by one.
     * and then return this.
     *
     * @param leftTopPosX a float.
     * @param leftTopPosY a float.
     * @param width       a float.
     * @param height      a float.
     * @return return
     */
    public AbstractGameWindowComponent init(float leftTopPosX,
                                            float leftTopPosY, float width,
                                            float height) {
        this.setLeftTopPosX(leftTopPosX);
        this.setLeftTopPosY(leftTopPosY);
        this.setWidth(width);
        this.setHeight(height);
        return this;
    }


    /**
     * <p>update.</p>
     *
     * @return if update succeed
     */
    public boolean update() {
        return this.getUpdater().update(this);
    }

    /**
     * <p>draw.</p>
     *
     * @return if draw succeed
     */
    public boolean draw() {
        return this.getDrawer().draw(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (!this.alive.compareAndSet(true, false)) {
            return;
        }
        GameWindowComponentTreeNode gameWindowComponentTreeNodeLocal = this.getGameWindowComponentTreeNode();
        if (gameWindowComponentTreeNodeLocal != null) {
            gameWindowComponentTreeNodeLocal.close();
        }
    }

    /**
     * <p>initProcessors.</p>
     */
    protected abstract void initProcessors();

    /**
     * <p>registerProcessor.</p>
     *
     * @param eventClassString eventClassString
     * @param processor        event processor
     * @param <P>              event Class
     */
    public <P extends Event> void registerProcessor(String eventClassString, EventProcessor<P> processor) {
        try {
            this.eventClassToProcessorMapPut((Class<? extends P>) Class.forName(eventClassString), processor);
        } catch (ClassNotFoundException e) {
            LOGGER.error("cannot get class : {}", eventClassString, e);
        }
    }

    /**
     * <p>registerProcessor.</p>
     *
     * @param eventClass event class
     * @param processor  event processor
     * @param <T>        event class
     */
    public <T extends Event> void registerProcessor(Class<T> eventClass,
                                                    EventProcessor<? super T> processor) {
        this.eventClassToProcessorMapPut(eventClass, processor);
    }

    /**
     * <p>getProcessor.</p>
     *
     * @param eventClassString event Class String
     * @param <P>              event Class
     * @return EventProcessor
     */
    public <P extends Event> EventProcessor<P> getProcessor(String eventClassString) {
        EventProcessor<P> res = null;
        try {
            res = (EventProcessor<P>) this.eventClassToProcessorMapGet((Class<? extends P>) Class.forName(eventClassString));
        } catch (ClassNotFoundException e) {
            LOGGER.error("cannot get class : {}", eventClassString, e);
        }
        return res;
    }

    /**
     * get processor of class T.
     * if class T have no processor registered here,
     * then will try to get from T's super class.
     * then super super class.
     * till Event.class.
     *
     * @param eventClass event class
     * @param <T>        event class
     * @return EventProcessor
     */
    public <T extends Event> EventProcessor<? super T> getProcessor(Class<T> eventClass) {
        Class<? super T> nowClass = eventClass;
        while (nowClass != null && nowClass != Event.class && !this.getEventClassToProcessorMap().containsKey(nowClass)) {
            nowClass = nowClass.getSuperclass();
        }
        if (eventClass == null) {
            return null;
        }
        return this.eventClassToProcessorMapGet(eventClass);
    }


    /**
     * 1. If the GameWindowComponent can not/shall not solve the event
     * (like, the event is totally not relevant to the component),
     * it will just return the event.
     * <p>
     * 2. If the event is totally solved by the component,
     * then it shall return null
     * <p>
     * 3. If the event is solved by the component, but caused another event arise,
     * then it shall return the new event.
     *
     * @param event the old event that is being processed by this Component now.
     * @param <T>   event class
     * @return the new Event that generated during the processing of the old event.
     * @see Event#apply(GameManager)
     */
    public <T extends Event> Event process(T event) {
        synchronized (this) {
            EventProcessor<? super T> processor = this.getProcessor((Class<T>) event.getClass());
            if (processor != null) {
                return processor.apply(event);
            }
            return event;
        }
    }

    /**
     * if gameWindowComponentTreeNode == null, then will create a new Node from the tree.
     * otherwise, will create a node as gameWindowComponentTreeNode's child to contain this.
     *
     * @param gameWindowComponentTreeNode gameWindowComponentTreeNode
     */
    public void addToGameWindowComponentTree(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        IllegalArgumentExceptionUtilsx.isAnyNullInParamsThenThrowIllegalArgumentException(gameWindowComponentTreeNode);
        gameWindowComponentTreeNode.newNode(this);
    }

    /**
     * <p>eventClassToProcessorMapPut.</p>
     *
     * @param eventClass     event class
     * @param eventProcessor event processor
     * @param <T>            event class
     * @return a {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor} object.
     */
    protected <T extends Event> EventProcessor<? super T> eventClassToProcessorMapPut(Class<T> eventClass, EventProcessor<? super T> eventProcessor) {
        return (EventProcessor) this.getEventClassToProcessorMap().put(eventClass, eventProcessor);
    }

    /**
     * <p>eventClassToProcessorMapGet.</p>
     *
     * @param eventClass event class
     * @param <T>        event class
     * @return a {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor} object.
     */
    protected <T extends Event> EventProcessor<? super T> eventClassToProcessorMapGet(Class<T> eventClass) {
        return (EventProcessor) this.getEventClassToProcessorMap().get(eventClass);
    }

    /**
     * <p>eventClassToProcessorMapContainsKey.</p>
     *
     * @param eventClass event class
     * @param <T>        event class
     * @return a boolean.
     */
    protected <T extends Event> boolean eventClassToProcessorMapContainsKey(Class<T> eventClass) {
        return this.getEventClassToProcessorMap().containsKey(eventClass);
    }


//-----shortcuts starts-----

    /**
     * enlarge to full window.
     * <p>
     * I'm thinking about deleting this function
     * because I think this function be meaningless.
     * I asked myself: why not call this.cover(this.getGameWindow()); direcly?
     */
    public void enlargeAsFullWindow() {
        this.cover(this.getGameWindow());
    }

    /**
     * move to center to the gameWindow
     * <p>
     * I'm thinking about deleting this function
     * because I think this function be meaningless.
     * I asked myself: why not call this.setCenter(this.getGameWindow()); direcly?
     *
     * @see #setCenter(AbstractArea)
     */
    public void moveToCenterOfFullWindow() {
        this.setCenter(this.getGameWindow());
    }

    /**
     * <p>getGameManager.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.GameManager} object.
     */
    public GameManager getGameManager() {
        return this.getGameWindow().getGameManager();
    }

    /**
     * <p>getResourceManager.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.memory.ResourceManager} object.
     */
    public ResourceManager getResourceManager() {
        return this.getGameWindow().getGameManager().getResourceManager();
    }

    /**
     * <p>getDataCenter.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.DataCenter} object.
     */
    public DataCenter getDataCenter() {
        return this.getGameWindow().getGameManager().getDataCenter();
    }

//-----shortcuts ends-----

    /**
     * <p>Getter for the field <code>alive</code>.</p>
     *
     * @return alive
     */
    public boolean isAlive() {
        return alive.get();
    }

    /**
     * <p>Setter for the field <code>alive</code>.</p>
     *
     * @param alive alive
     */
    public void setAlive(boolean alive) {
        this.alive.set(alive);
    }
}
