package com.xenoamess.cyan_potion.base.gameWindowComponents;

//import com.xenoamess.gearbar.GameEngineObject;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.events.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * @author XenoAmess
 */
public abstract class AbstractGameWindowComponent implements AutoCloseable {
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

    public AbstractGameWindowComponent init(float leftTopPosX,
                                            float leftTopPosY, float width,
                                            float height) {
        this.setLeftTopPosX(leftTopPosX);
        this.setLeftTopPosY(leftTopPosY);
        this.setWidth(width);
        this.setHeight(height);
        return this;
    }

    public AtomicBoolean getAlive() {
        return alive;
    }

    public void kill() {
        this.getAlive().set(false);
    }

    public abstract void update();

    public abstract void draw();

    @Override
    public void close() {
        //TODO
    }

    private Map<String, Function<Event, Event>> classNameToProcessorMap =
            new HashMap<>();

    public abstract void initProcessors();

    public void registerProcessor(String eventType,
                                  Function<Event, Event> processor) {
        this.getClassNameToProcessorMap().put(eventType, processor);
    }

    public Function<Event, Event> getProcessor(String eventType) {
        return this.getClassNameToProcessorMap().get(eventType);
    }

    public Event process(Event event) {
        Function<Event, Event> processor =
                this.getProcessor(event.getClass().getCanonicalName());
        if (processor != null) {
            return processor.apply(event);
        }
        return event;
    }

    public void addToGameWindowComponentTree(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        if (gameWindowComponentTreeNode != null) {
            gameWindowComponentTreeNode.newNode(this);
//            this.leftTopPosX = gameWindowComponentTreeNode
//            .gameWindowComponent.leftTopPosX;
//            this.leftTopPosY = gameWindowComponentTreeNode
//            .gameWindowComponent.leftTopPosY;
//            this.width = gameWindowComponentTreeNode.gameWindowComponent
//            .width;
//            this.height = gameWindowComponentTreeNode.gameWindowComponent
//            .height;
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

    public void setAlive(boolean alive) {
        this.getAlive().set(alive);
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

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Map<String, Function<Event, Event>> getClassNameToProcessorMap() {
        return classNameToProcessorMap;
    }

    public void setClassNameToProcessorMap(Map<String,
            Function<Event, Event>> classNameToProcessorMap) {
        this.classNameToProcessorMap = classNameToProcessorMap;
    }
}
