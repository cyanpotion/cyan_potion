/*
 * MIT License
 *
 * Copyright (c) 2019 XenoAmess
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
import com.xenoamess.cyan_potion.base.commons.areas.Area;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>Abstract AbstractGameWindowComponent class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public abstract class AbstractGameWindowComponent implements AutoCloseable, Area {
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
     * <p>Constructor for AbstractGameWindowComponent.</p>
     *
     * @param gameWindow a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     */
    public AbstractGameWindowComponent(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        this.initProcessors();
    }

    /**
     * <p>createGameWindowComponentFromClassName.</p>
     *
     * @param gameWindow                   a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     * @param gameWindowComponentClassName a {@link java.lang.String} object.
     * @return a {@link com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent} object.
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
     * <p>init.</p>
     *
     * @param leftTopPosX a float.
     * @param leftTopPosY a float.
     * @param width       a float.
     * @param height      a float.
     * @return a {@link com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent} object.
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
        //TODO
    }

    private final Map<String, EventProcessor> classNameToProcessorMap = new ConcurrentHashMap<>();

    /**
     * <p>initProcessors.</p>
     */
    public abstract void initProcessors();

    /**
     * <p>registerProcessor.</p>
     *
     * @param eventType a {@link java.lang.String} object.
     * @param processor a
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor} object.
     */
    public void registerProcessor(String eventType,
                                  EventProcessor processor) {
        this.getClassNameToProcessorMap().put(eventType, processor);
    }

    /**
     * <p>getProcessor.</p>
     *
     * @param eventType a {@link java.lang.String} object.
     * @return a {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor} object.
     */
    public EventProcessor getProcessor(String eventType) {
        return this.getClassNameToProcessorMap().get(eventType);
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
     * @return the new Event that generated during the processing of the old event.
     * @see Event#apply(GameManager)
     */
    public Event process(Event event) {
        EventProcessor processor =
                this.getProcessor(event.getClass().getCanonicalName());
        if (processor != null) {
            return processor.apply(event);
        }
        return event;
    }

    /**
     * <p>addToGameWindowComponentTree.</p>
     *
     * @param gameWindowComponentTreeNode a
     * {@link com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode} object.
     */
    public void addToGameWindowComponentTree(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        if (gameWindowComponentTreeNode != null) {
            gameWindowComponentTreeNode.newNode(this);
        } else {
            getGameWindow().getGameManager().getGameWindowComponentTree().newNode(this);
        }
    }

    /**
     * <p>enlargeAsFullWindow.</p>
     */
    public void enlargeAsFullWindow() {
        this.setLeftTopPosX(0);
        this.setLeftTopPosY(0);
        this.setWidth(this.getGameWindow().getLogicWindowWidth());
        this.setHeight(this.getGameWindow().getLogicWindowHeight());
    }

    /**
     * <p>center.</p>
     */
    public void center() {
        this.setLeftTopPosX((this.getGameWindow().getLogicWindowWidth() - this.getWidth()) / 2);
        this.setLeftTopPosY((this.getGameWindow().getLogicWindowHeight() - this.getHeight()) / 2);
    }


    /**
     * <p>Getter for the field <code>gameWindow</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     */
    public GameWindow getGameWindow() {
        return gameWindow;
    }

    /**
     * <p>Getter for the field <code>gameWindowComponentTreeNode</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode} object.
     */
    public GameWindowComponentTreeNode getGameWindowComponentTreeNode() {
        return gameWindowComponentTreeNode;
    }

    /**
     * <p>Setter for the field <code>gameWindowComponentTreeNode</code>.</p>
     *
     * @param gameWindowComponentTreeNode a
     * {@link com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode} object.
     */
    public void setGameWindowComponentTreeNode(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        this.gameWindowComponentTreeNode = gameWindowComponentTreeNode;
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
     * <p>Setter for the field <code>leftTopPosX</code>.</p>
     *
     * @param leftTopPosX a float.
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
     * <p>Setter for the field <code>leftTopPosY</code>.</p>
     *
     * @param leftTopPosY a float.
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
     * <p>Setter for the field <code>width</code>.</p>
     *
     * @param width a float.
     */
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
     */
    @Override
    public float getCenterPosX() {
        return this.getLeftTopPosX() + this.getWidth() / 2F;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getCenterPosY() {
        return this.getLeftTopPosY() + this.getHeight() / 2F;
    }

    /**
     * <p>Setter for the field <code>height</code>.</p>
     *
     * @param height a float.
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * <p>Getter for the field <code>classNameToProcessorMap</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, EventProcessor> getClassNameToProcessorMap() {
        return classNameToProcessorMap;
    }
}
