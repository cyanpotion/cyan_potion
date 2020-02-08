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

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.audio.WaveData;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardEvent;
import com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent;
import com.xenoamess.cyan_potion.base.memory.ResourceInfo;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Picture;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.xenoamess.cyan_potion.base.audio.WaveData.STRING_MUSIC;
import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PICTURE;
import static org.lwjgl.opengl.GL11.*;

/**
 * <p>Logo class.</p>
 *
 * @author XenoAmess
 * @version 0.155.3
 */
public class Logo extends AbstractGameWindowComponent {
    private final Texture logoTexture =
            this.getGameWindow().getGameManager().getResourceManager().
                    fetchResource(
                            Texture.class,
                            STRING_PICTURE,
                            "resources/www/img/pictures/logo.png"
                    );

    private final Picture logoPicture;

    private final long lifeTime;
    private final long dieTimeStamp;


    /**
     * <p>Constructor for Logo.</p>
     *
     * @param gameWindow gameWindow
     * @param lifeTime   a long.
     */
    public Logo(GameWindow gameWindow, long lifeTime) {
        super(gameWindow);

        this.lifeTime = lifeTime;
        this.dieTimeStamp = System.currentTimeMillis() + this.getLifeTime();
        this.getGameWindow().getGameManager().getAudioManager().playWaveData(
                this.getGameWindow().getGameManager().getResourceManager().fetchResource(
                        WaveData.class,
                        new ResourceInfo<>(
                                WaveData.class,
                                STRING_MUSIC,
                                "resources/www/audio/se/logo.ogg"
                        )
                )
        );
        this.logoPicture = new Picture(this.logoTexture);
        this.logoPicture.setCenter(this.getGameWindow());
        this.logoPicture.moveY(-50 * 2);
    }

    /**
     * <p>Constructor for Logo.</p>
     *
     * @param gameWindow gameWindow
     */
    public Logo(GameWindow gameWindow) {
        this(gameWindow, 500L + 2000L + 1000L);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("Duplicates")
    @Override
    public void initProcessors() {
        this.registerProcessor(KeyboardEvent.class,
                (KeyboardEvent keyboardEvent) -> {
                    switch (keyboardEvent.getKeyTranslated(this.getGameWindow().getGameManager().getKeymap()).getKey()) {
                        case Keymap.XENOAMESS_KEY_ESCAPE:
                        case Keymap.XENOAMESS_KEY_ENTER:
                        case Keymap.XENOAMESS_KEY_SPACE:
                            this.willClose.set(true);
                            break;
                        default:
                            return keyboardEvent;
                    }
                    return null;
                }
        );

        this.registerProcessor(MouseButtonEvent.class,
                (MouseButtonEvent event) -> {
                    this.willClose.set(true);
                    return null;
                }
        );
    }

    private final AtomicBoolean willClose = new AtomicBoolean(false);
    private final AtomicBoolean initNext = new AtomicBoolean(false);

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        if (System.currentTimeMillis() > this.getDieTimeStamp()) {
            willClose.set(true);
        }

        if (willClose.get()) {
            if (initNext.compareAndSet(false, true)) {
                MadeWithLogo madeWithLogo = new MadeWithLogo(this.getGameWindow());
                madeWithLogo.addToGameWindowComponentTree(this.getGameManager().getGameWindowComponentTree().getRoot());
                madeWithLogo.enlargeAsFullWindow();
                this.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw() {
        if (!this.getAlive()) {
            return;
        }

        glClearColor(1, 1, 1, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        float t =
                (float) (this.getLifeTime() - this.getDieTimeStamp() + System.currentTimeMillis());
        float pScale;
        float dynamicTime = 500f;
        float stayTime = 2000f;
        float fadeTime = 1000f;
        float seg = 0.6f;
        if (t < seg * dynamicTime) {
            pScale = t / seg / dynamicTime;
        } else if (t < (1 - (1 - seg) / 2) * dynamicTime) {
            pScale = t / seg / dynamicTime;
        } else if (t < dynamicTime) {
            pScale =
                    ((1 - (1 - seg) / 2) * 2 * dynamicTime - t) / seg / dynamicTime;
        } else {
            pScale = 1;
        }

        float width = getGameWindow().getWidth();
        float height = getGameWindow().getHeight();
        if (width * 1024f < height * 1280f) {
            height = width * (1024f / 1280f);
        } else {
            width = height * (1280f / 1024f);
        }

        if (t < dynamicTime + stayTime) {
            this.logoPicture.setWidth(480 * (pScale + 1) / 1280f * width);
            this.logoPicture.setHeight(60 * (pScale + 1) / 1024f * height);
            this.logoPicture.setCenter(this.getGameWindow());
            this.logoPicture.moveY(-50 * 2);
            this.logoPicture.setColorScale(1, 1, 1, pScale);
        } else {
            pScale = (1 - (t - dynamicTime - stayTime) / fadeTime);
            if (pScale < 0) {
                pScale = 0;
            }
            this.logoPicture.setColorScale(1, 1, 1, pScale);
        }
        this.logoPicture.draw(getGameWindow());
    }


    /**
     * <p>Getter for the field <code>lifeTime</code>.</p>
     *
     * @return a long.
     */
    public long getLifeTime() {
        return lifeTime;
    }

    /**
     * <p>Getter for the field <code>dieTimeStamp</code>.</p>
     *
     * @return a long.
     */
    public long getDieTimeStamp() {
        return dieTimeStamp;
    }
}
