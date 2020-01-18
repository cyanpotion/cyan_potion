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
import com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardEvent;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Picture;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>Menu class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class Menu extends AbstractGameWindowComponent {
    private final AtomicBoolean show = new AtomicBoolean(false);
    private final Texture menuBackGroundTexture =
            this.getGameWindow().getGameManager().getResourceManager().
                    fetchResource(
                            Texture.class,
                            "picture",
                            "/www/img/pictures/menuBackGround.png"
                    );

    private final Picture menuBackGroundPicture = new Picture(menuBackGroundTexture);

    private World world;

    /**
     * <p>Constructor for Menu.</p>
     *
     * @param world world
     */
    public Menu(World world) {
        super(world.getGameWindow());
        this.setWorld(world);
        menuBackGroundPicture.cover(this.getGameWindow());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initProcessors() {
        this.registerProcessor(KeyboardEvent.class.getCanonicalName(), event -> {
            KeyboardEvent keyboardEvent = (KeyboardEvent) event;
            switch (keyboardEvent.getKeyTranslated(this.getGameWindow().getGameManager().getKeymap()).getKey()) {
                case Keymap.XENOAMESS_KEY_ESCAPE:
                    if (keyboardEvent.getAction() == GLFW.GLFW_PRESS && keyboardEvent.getMods() == 0) {
                        this.setShow(false);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        //TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw() {
        if (!getShow()) {
            return;
        }
        this.menuBackGroundPicture.draw(this.getGameWindow());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Event process(Event event) {
        if (!getShow()) {
            return event;
        }
        return super.process(event);
    }

    /**
     * <p>Getter for the field <code>show</code>.</p>
     *
     * @return a boolean.
     */
    public boolean getShow() {
        return show.get();
    }

    /**
     * <p>Setter for the field <code>show</code>.</p>
     *
     * @param show a boolean.
     */
    public void setShow(boolean show) {
        this.show.set(show);
    }

    /**
     * <p>Getter for the field <code>menuBackGroundTexture</code>.</p>
     *
     * @return return
     */
    public Texture getMenuBackGroundTexture() {
        return menuBackGroundTexture;
    }

    /**
     * <p>Getter for the field <code>world</code>.</p>
     *
     * @return return
     */
    public World getWorld() {
        return world;
    }

    /**
     * <p>Setter for the field <code>world</code>.</p>
     *
     * @param world world
     */
    public void setWorld(World world) {
        this.world = world;
    }
}
