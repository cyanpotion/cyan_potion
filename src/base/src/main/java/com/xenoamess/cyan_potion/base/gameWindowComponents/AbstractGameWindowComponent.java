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

package com.xenoamess.cyan_potion.base.gameWindowComponents;


import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.commons.areas.Area;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.events.EventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author XenoAmess
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

    public AbstractGameWindowComponent(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        this.initProcessors();
    }

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


    public AbstractGameWindowComponent init(float leftTopPosX,
                                            float leftTopPosY, float width,
                                            float height) {
        this.setLeftTopPosX(leftTopPosX);
        this.setLeftTopPosY(leftTopPosY);
        this.setWidth(width);
        this.setHeight(height);
        return this;
    }

    public boolean getAlive() {
        return alive.get();
    }

    public void setAlive(boolean alive) {
        this.alive.set(alive);
    }


    public abstract void update();

    public abstract void draw();

    @Override
    public void close() {
        this.setAlive(false);
        //TODO
    }

    private final Map<String, EventProcessor> classNameToProcessorMap = new ConcurrentHashMap<>();

    public abstract void initProcessors();

    public void registerProcessor(String eventType,
                                  EventProcessor processor) {
        this.getClassNameToProcessorMap().put(eventType, processor);
    }

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

    public void addToGameWindowComponentTree(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        if (gameWindowComponentTreeNode != null) {
            gameWindowComponentTreeNode.newNode(this);
        } else {
            getGameWindow().getGameManager().getGameWindowComponentTree().newNode(this);
        }
    }

    public void enlargeAsFullWindow() {
        this.setLeftTopPosX(0);
        this.setLeftTopPosY(0);
        this.setWidth(this.getGameWindow().getLogicWindowWidth());
        this.setHeight(this.getGameWindow().getLogicWindowHeight());
    }

    public void center() {
        this.setLeftTopPosX((this.getGameWindow().getLogicWindowWidth() - this.getWidth()) / 2);
        this.setLeftTopPosY((this.getGameWindow().getLogicWindowHeight() - this.getHeight()) / 2);
    }


    public GameWindow getGameWindow() {
        return gameWindow;
    }

    public GameWindowComponentTreeNode getGameWindowComponentTreeNode() {
        return gameWindowComponentTreeNode;
    }

    public void setGameWindowComponentTreeNode(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        this.gameWindowComponentTreeNode = gameWindowComponentTreeNode;
    }

    public float getLeftTopPosX() {
        return leftTopPosX;
    }

    public void setLeftTopPosX(float leftTopPosX) {
        this.leftTopPosX = leftTopPosX;
    }

    public float getLeftTopPosY() {
        return leftTopPosY;
    }

    public void setLeftTopPosY(float leftTopPosY) {
        this.leftTopPosY = leftTopPosY;
    }

    @Override
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getCenterPosX() {
        return this.getLeftTopPosX() + this.getWidth() / 2F;
    }

    @Override
    public float getCenterPosY() {
        return this.getLeftTopPosY() + this.getHeight() / 2F;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Map<String, EventProcessor> getClassNameToProcessorMap() {
        return classNameToProcessorMap;
    }
}
