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
import com.xenoamess.cyan_potion.base.visual.Font;
import com.xenoamess.cyan_potion.base.visual.Picture;

import static com.xenoamess.cyan_potion.base.audio.WaveData.STRING_MUSIC;
import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PICTURE;
import static org.lwjgl.opengl.GL11.*;

/**
 * <p>MadeWithLogo class.</p>
 *
 * @author XenoAmess
 * @version 0.155.2
 */
public class MadeWithLogo extends AbstractGameWindowComponent {
    private final Texture logoTexture =
            this.getGameWindow().getGameManager().getResourceManager().
                    fetchResource(
                            Texture.class,
                            STRING_PICTURE,
                            "resources/www/img/pictures/madewith.png"
                    );

    private final Picture logoPicture = new Picture(this.logoTexture);

    private final long lifeTime;
    private final long dieTimeStamp;


    /**
     * <p>Constructor for MadeWithLogo.</p>
     *
     * @param gameWindow gameWindow
     * @param lifeTime   a long.
     */
    public MadeWithLogo(GameWindow gameWindow, long lifeTime) {
        super(gameWindow);
        this.logoPicture.cover(this.getGameWindow());
        this.lifeTime = lifeTime;
        this.dieTimeStamp = System.currentTimeMillis() + this.getLifeTime();
        this.getGameWindow().getGameManager().getAudioManager().playWaveData(
                this.getGameWindow().getGameManager().getResourceManager().fetchResource(
                        WaveData.class,
                        new ResourceInfo<>(
                                WaveData.class, STRING_MUSIC,
                                "resources/www/audio/se/madewith.ogg"
                        )
                )
        );
    }

    /**
     * <p>Constructor for MadeWithLogo.</p>
     *
     * @param gameWindow gameWindow
     */
    public MadeWithLogo(GameWindow gameWindow) {
        this(gameWindow, 5000L + 5000L);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("Duplicates")
    @Override
    public void initProcessors() {
        this.registerProcessor(
                KeyboardEvent.class,
                (KeyboardEvent keyboardEvent) -> {
                    switch (keyboardEvent.getKeyTranslated(this.getGameWindow().getGameManager().getKeymap()).getKey()) {
                        case Keymap.XENOAMESS_KEY_ESCAPE:
                        case Keymap.XENOAMESS_KEY_ENTER:
                        case Keymap.XENOAMESS_KEY_SPACE:
                            this.setAlive(false);
                            break;
                        default:
                            return keyboardEvent;
                    }
                    return null;
                });

        this.registerProcessor(
                MouseButtonEvent.class,
                (MouseButtonEvent event) -> {
                    this.setAlive(false);
                    return null;
                }
        );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        if (System.currentTimeMillis() > this.getDieTimeStamp()) {
            this.setAlive(false);
        }

        if (!this.getAlive() && Font.getDefaultFont().isInMemory()) {
            this.getGameWindowComponentTreeNode().close();
            {
                Font.getDefaultFont().init(this.getGameWindow());
                AbstractGameWindowComponent title =
                        AbstractGameWindowComponent.createGameWindowComponentFromClassName(
                                this.getGameWindow(),
                                this.getGameWindow()
                                        .getGameManager()
                                        .getDataCenter()
                                        .getGameSettings()
                                        .getTitleClassName()
                        );

                title.addToGameWindowComponentTree(null);
                title.enlargeAsFullWindow();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw() {
        if (!this.getAlive()) {
            glClearColor(1, 1, 1, 1);
            glClear(GL_COLOR_BUFFER_BIT);
            return;
        }

        long t = this.getLifeTime() - this.getDieTimeStamp() + System.currentTimeMillis();
        float colorScale;

        long stayTime = 5000L;
        long fadeTime = 5000L;
        if (t < stayTime) {
            colorScale = 1;
        } else {
            colorScale = 1 + ((float) (t - stayTime)) / fadeTime * 400;
        }

        this.logoPicture.setColorScale(1, colorScale, colorScale, 1);
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
