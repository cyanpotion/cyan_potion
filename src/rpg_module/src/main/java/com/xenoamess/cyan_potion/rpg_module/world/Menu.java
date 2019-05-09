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

package com.xenoamess.cyan_potion.rpg_module.world;

import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.events.KeyEvent;
import com.xenoamess.cyan_potion.base.gameWindowComponents.AbstractGameWindowComponent;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.render.Texture;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author XenoAmess
 */
public class Menu extends AbstractGameWindowComponent {
    private AtomicBoolean show = new AtomicBoolean(false);
    private Texture menuBackGroundTexture;
    private World world;

    public Menu(World world) {
        super(world.getGameWindow());
        this.setWorld(world);
        this.setMenuBackGroundTexture(this.getGameWindow().getGameManager().getResourceManager().fetchResourceWithShortenURI(Texture.class, "/www/img/pictures/menuBackGround.png:picture"));
    }

    @Override
    public void initProcessors() {
        this.registerProcessor(KeyEvent.class.getCanonicalName(), event -> {
            KeyEvent keyEvent = (KeyEvent) event;
            switch (keyEvent.getKeyTranslated(this.getGameWindow().getGameManager().getKeymap()).getKey()) {
                case Keymap.XENOAMESS_KEY_ESCAPE:
                    if (keyEvent.getAction() == GLFW.GLFW_PRESS && keyEvent.getMods() == 0) {
                        this.getShow().set(false);
                    }
                    return null;
                case Keymap.XENOAMESS_KEY_UP:
                case Keymap.XENOAMESS_KEY_DOWN:
                case Keymap.XENOAMESS_KEY_LEFT:
                case Keymap.XENOAMESS_KEY_RIGHT:
                    return null;
                default:
                    return event;
            }
        });
    }

    @Override
    public void update() {

    }

    @Override
    public void draw() {
        if (!getShow().get()) {
            return;
        }
        this.getGameWindow().drawBindableRelativeCenter(getMenuBackGroundTexture(),
                this.getGameWindow().getLogicWindowWidth(), this.getGameWindow().getLogicWindowHeight());
    }

    @Override
    public Event process(Event event) {
        if (!getShow().get()) {
            return event;
        }
        return super.process(event);
    }

    public AtomicBoolean getShow() {
        return show;
    }

    public void setShow(AtomicBoolean show) {
        this.show = show;
    }

    public Texture getMenuBackGroundTexture() {
        return menuBackGroundTexture;
    }

    public void setMenuBackGroundTexture(Texture menuBackGroundTexture) {
        this.menuBackGroundTexture = menuBackGroundTexture;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
