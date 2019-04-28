package com.xenoamess.cyan_potion.base.gameWindowComponents.ControlableGameWindowComponents;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.gameWindowComponents.AbstractGameWindowComponent;
import com.xenoamess.cyan_potion.base.render.Texture;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XenoAmess
 */
public class Panel extends AbstractControlableGameWindowComponent {
    private List<AbstractGameWindowComponent> contents = new ArrayList<AbstractGameWindowComponent>();
    private Texture backgroundTexture;

    public Panel(GameWindow gameWindow) {
        super(gameWindow);
    }

    public Panel(GameWindow gameWindow, Texture backgroundTexture) {
        super(gameWindow);
        this.setBackgroundTexture(backgroundTexture);
    }

    public void addContent(AbstractGameWindowComponent gameWindowComponent) {
        this.getContents().add(gameWindowComponent);
    }

    @Override
    public void update() {
        for (AbstractGameWindowComponent gameWindowComponent : getContents()) {
            gameWindowComponent.update();
        }
    }

    @Override
    public void draw() {
        if (getBackgroundTexture() != null) {
            getGameWindow().drawBindableRelativeLeftTop(getBackgroundTexture(), this.getLeftTopPosX(), this.getLeftTopPosY(), this.getWidth(), this.getHeight());
        }
        for (AbstractGameWindowComponent gameWindowComponent : getContents()) {
            gameWindowComponent.draw();
        }
    }

    @Override
    public Event process(Event event) {
        for (AbstractGameWindowComponent gameWindowComponent : getContents()) {
            event = gameWindowComponent.process(event);
            if (event == null) {
                return event;
            }
        }
        return event;
    }

    public List<AbstractGameWindowComponent> getContents() {
        return contents;
    }

    public void setContents(List<AbstractGameWindowComponent> contents) {
        this.contents = contents;
    }

    public Texture getBackgroundTexture() {
        return backgroundTexture;
    }

    public void setBackgroundTexture(Texture backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
    }
}
