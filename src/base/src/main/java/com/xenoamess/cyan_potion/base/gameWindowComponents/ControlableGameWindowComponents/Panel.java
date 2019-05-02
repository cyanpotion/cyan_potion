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

package com.xenoamess.cyan_potion.base.gameWindowComponents.Co

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.gameWindowComponents.AbstractGameWindowComponent;
import com.xenoamess.cyan_potion.base.render.Texture;

import java.util.ArrayList;
import java.util.List;

ntrolableGameWindowComponents;

/**
 * @author XenoAmess
 */
public class Panel extends AbstractControlableGameWindowComponent {
    private List<AbstractGameWindowComponent> contents =
            new ArrayList<AbstractGameWindowComponent>();
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
            getGameWindow().drawBindableRelativeLeftTop(getBackgroundTexture(), this.getLeftTopPosX(),
                    this.getLeftTopPosY(), this.getWidth(), this.getHeight());
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
