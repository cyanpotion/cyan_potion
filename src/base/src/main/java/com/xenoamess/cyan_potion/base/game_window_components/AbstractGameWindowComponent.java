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


import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.commons.areas.AbstractArea;
import com.xenoamess.cyan_potion.base.commons.areas.AbstractMutableArea;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AbstractGameWindowComponent class is an ancestor of all GameWindowComponent classes.
 * If you are implementing a GameWindowComponent, and wanna a easy start,
 * just implement AbstractControllableGameWindowComponent instead
 *
 * @author XenoAmess
 * @version 0.143.0
 * @see com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.AbstractControllableGameWindowComponent
 */
public abstract class AbstractGameWindowComponent implements AutoCloseable, AbstractMutableArea {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(AbstractGameWindowComponent.class);

    private final GameWindow gameWindow;
    private final AtomicBoolean alive = new AtomicBoolean(true);
    private GameWindowComponentTreeNode gameWindowComponentTreeNode;

    private float leftTopPosX = 0;
    private float leftTopPosY = 0;
    private float width = -1;
    private float height = -1;

    /**
     * map of eventClass->processors.
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
    private final Map<Class<? extends Event>, EventProcessor<? extends Event>> eventClassToProcessorMap = new ConcurrentHashMap<>();


    /**
     * <p>Constructor for AbstractGameWindowComponent.</p>
     *
     * @param gameWindow gameWindow
     */
    public AbstractGameWindowComponent(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        this.initProcessors();
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
        assert (gameWindowComponentClassName != null);
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
     * <p>Getter for the field <code>alive</code>.</p>
     *
     * @return a boolean.
     */
    public boolean getAlive() {
        return alive.get();
    }

    /**
     * <p>Setter for the field <code>alive</code>.</p>
     *
     * @param alive a boolean.
     */
    public void setAlive(boolean alive) {
        this.alive.set(alive);
    }


    /**
     * <p>update.</p>
     */
    public abstract void update();

    /**
     * <p>draw.</p>
     */
    public abstract void draw();

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        this.setAlive(false);
    }

    /**
     * <p>initProcessors.</p>
     */
    public abstract void initProcessors();

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
        EventProcessor<? super T> processor = this.getProcessor((Class<T>) event.getClass());
        if (processor != null) {
            return processor.apply(event);
        }
        return event;
    }

    /**
     * if gameWindowComponentTreeNode == null, then will create a new Node from the tree.
     * otherwise, will create a node as gameWindowComponentTreeNode's child to contain this.
     *
     * @param gameWindowComponentTreeNode gameWindowComponentTreeNode
     */
    public void addToGameWindowComponentTree(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        if (gameWindowComponentTreeNode != null) {
            gameWindowComponentTreeNode.newNode(this);
        } else {
            getGameWindow().getGameManager().getGameWindowComponentTree().newNode(this);
        }
    }

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
     * <p>Getter for the field <code>gameWindow</code>.</p>
     *
     * @return return
     */
    public GameWindow getGameWindow() {
        return gameWindow;
    }

    /**
     * <p>Getter for the field <code>gameWindowComponentTreeNode</code>.</p>
     *
     * @return return
     */
    public GameWindowComponentTreeNode getGameWindowComponentTreeNode() {
        return gameWindowComponentTreeNode;
    }

    /**
     * <p>Setter for the field <code>gameWindowComponentTreeNode</code>.</p>
     *
     * @param gameWindowComponentTreeNode gameWindowComponentTreeNode
     */
    public void setGameWindowComponentTreeNode(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        this.gameWindowComponentTreeNode = gameWindowComponentTreeNode;
    }

    //shortcuts

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

    /**
     * don't use it if not necessary.
     * just use eventClassToProcessorMapPut , eventClassToProcessorMapGet , eventClassToProcessorMapContainsKey if you can.
     *
     * @return this.eventClassToProcessorMap
     * @see AbstractGameWindowComponent#eventClassToProcessorMapPut
     * @see AbstractGameWindowComponent#eventClassToProcessorMapGet
     * @see AbstractGameWindowComponent#eventClassToProcessorMapContainsKey
     */
    protected Map<Class<? extends Event>, EventProcessor<? extends Event>> getEventClassToProcessorMap() {
        return eventClassToProcessorMap;
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

    /**
     * <p>Getter for the field <code>leftTopPosX</code>.</p>
     *
     * @return a float.
     */
    public float getLeftTopPosX() {
        return leftTopPosX;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Setter for the field <code>leftTopPosX</code>.</p>
     */
    public void setLeftTopPosX(float leftTopPosX) {
        this.leftTopPosX = leftTopPosX;
    }

    /**
     * <p>Getter for the field <code>leftTopPosY</code>.</p>
     *
     * @return a float.
     */
    public float getLeftTopPosY() {
        return leftTopPosY;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Setter for the field <code>leftTopPosY</code>.</p>
     */
    public void setLeftTopPosY(float leftTopPosY) {
        this.leftTopPosY = leftTopPosY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getWidth() {
        return width;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Setter for the field <code>width</code>.</p>
     */
    @Override
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getHeight() {
        return height;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Setter for the field <code>height</code>.</p>
     */
    @Override
    public void setHeight(float height) {
        this.height = height;
    }
}
