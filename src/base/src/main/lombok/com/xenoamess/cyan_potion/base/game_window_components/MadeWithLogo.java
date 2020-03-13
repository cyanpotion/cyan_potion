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
import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.audio.WaveData;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardEvent;
import com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent;
import com.xenoamess.cyan_potion.base.memory.ResourceInfo;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Font;
import com.xenoamess.cyan_potion.base.visual.Picture;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.xenoamess.cyan_potion.base.audio.WaveData.STRING_MUSIC;
import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PICTURE;

/**
 * <p>MadeWithLogo class.</p>
 *
 * @author XenoAmess
 * @version 0.162.0
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public final class MadeWithLogo extends AbstractGameWindowComponent {
    private final Texture logoTexture =
            this.getGameWindow().getGameManager().getResourceManager().
                    fetchResource(
                            Texture.class,
                            STRING_PICTURE,
                            "resources/www/img/pictures/madewith.png"
                    );

    private final Picture logoPicture = new Picture(this.logoTexture);

    @Getter
    private final long lifeTime;

    @Getter
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

    private static final long stayTime = 1500L;
    private static final long fadeTime = 750L;

    /**
     * <p>Constructor for MadeWithLogo.</p>
     *
     * @param gameWindow gameWindow
     */
    public MadeWithLogo(GameWindow gameWindow) {
        this(gameWindow, stayTime + fadeTime);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("Duplicates")
    @Override
    protected void initProcessors() {
        this.registerProcessor(
                KeyboardEvent.class,
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
                });

        this.registerProcessor(
                MouseButtonEvent.class,
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
    public boolean update() {
        if (System.currentTimeMillis() > this.getDieTimeStamp()) {
            this.willClose.set(true);
        }

        if (DataCenter.ifMainThread() && willClose.get() && Font.getDefaultFont().isInMemory()) {
            if (initNext.compareAndSet(false, true)) {
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
                title.addToGameWindowComponentTree(this.getGameManager().getGameWindowComponentTree().getRoot());
                title.enlargeAsFullWindow();
                this.close();
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean draw() {
        if (!this.isAlive()) {
            return false;
        }

        long t = this.getLifeTime() - this.getDieTimeStamp() + System.currentTimeMillis();
        float colorScale;

        if (t < stayTime) {
            colorScale = 1;
        } else {
            colorScale = 1 + ((float) (t - stayTime)) / fadeTime * 400;
        }

        this.logoPicture.setColorScale(1, colorScale, colorScale, 1);
        this.logoPicture.draw(getGameWindow());
        return true;
    }
}
