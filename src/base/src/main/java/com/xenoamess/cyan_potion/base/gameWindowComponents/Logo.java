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

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.audio.WaveData;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardEvent;
import com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Picture;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author XenoAmess
 */
public class Logo extends AbstractGameWindowComponent {
    private final Texture logoTexture =
            this.getGameWindow().getGameManager().getResourceManager().
                    fetchResourceWithShortenURI(
                            Texture.class,
                            "/www/img/pictures/logo.png:picture"
                    );

    private final Picture logoPicture = new Picture(this.logoTexture);

    {
        this.logoPicture.setCenter(this.getGameWindow());
        this.logoPicture.moveY(-50 * 2);
    }

    private final long lifeTime;
    private final long dieTimeStamp;


    public Logo(GameWindow gameWindow, long lifeTime) {
        super(gameWindow);

        this.lifeTime = lifeTime;
        this.dieTimeStamp = System.currentTimeMillis() + this.getLifeTime();
        this.getGameWindow().getGameManager().getAudioManager().playNew(this.getGameWindow().getGameManager().getResourceManager().fetchResourceWithShortenURI(WaveData.class, "/www/audio/se/logo.ogg:music"));
    }

    public Logo(GameWindow gameWindow) {
        this(gameWindow, 500L + 2000L + 1000L);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void initProcessors() {
        this.registerProcessor(KeyboardEvent.class.getCanonicalName(),
                event -> {
                    KeyboardEvent keyboardEvent = (KeyboardEvent) event;
                    switch (keyboardEvent.getKeyTranslated(this.getGameWindow().getGameManager().getKeymap()).getKey()) {
                        case Keymap.XENOAMESS_KEY_ESCAPE:
                        case Keymap.XENOAMESS_KEY_ENTER:
                        case Keymap.XENOAMESS_KEY_SPACE:
                            this.setAlive(false);
                            break;
                        default:
                            return event;
                    }
                    return null;
                }
        );

        this.registerProcessor(MouseButtonEvent.class.getCanonicalName(),
                event -> {
                    this.setAlive(false);
                    return null;
                }
        );
    }

    @Override
    public void update() {
        if (System.currentTimeMillis() > this.getDieTimeStamp()) {
            this.setAlive(false);
        }

        if (!this.getAlive()) {
            this.getGameWindowComponentTreeNode().close();
            MadeWithLogo madeWithLogo = new MadeWithLogo(this.getGameWindow());
            madeWithLogo.addToGameWindowComponentTree(null);
            madeWithLogo.enlargeAsFullWindow();
        }
    }

    @Override
    public void draw() {
        if (!this.getAlive()) {
            glClearColor(1, 1, 1, 1);
            glClear(GL_COLOR_BUFFER_BIT);
            return;
        }
        glClearColor(1, 1, 1, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        float t =
                (float) (this.getLifeTime() - this.getDieTimeStamp() + System.currentTimeMillis());
        float pscale;
        float dynamicTime = 500f;
        float stayTime = 2000f;
        float fadeTime = 1000f;
        float seg = 0.6f;
        if (t < seg * dynamicTime) {
            pscale = t / seg / dynamicTime;
        } else if (t < (1 - (1 - seg) / 2) * dynamicTime) {
            pscale = t / seg / dynamicTime;
        } else if (t < dynamicTime) {
            pscale =
                    ((1 - (1 - seg) / 2) * 2 * dynamicTime - t) / seg / dynamicTime;
        } else {
            pscale = 1;
        }

        if (t < dynamicTime + stayTime) {
            this.logoPicture.setWidth(480 * (pscale + 1));
            this.logoPicture.setHeight(60 * (pscale + 1));
            this.logoPicture.setColorScale(new Vector4f(1, 1, 1, pscale));
        } else {
            pscale = (1 - (t - dynamicTime - stayTime) / fadeTime);
            if (pscale < 0) {
                pscale = 0;
            }
            this.logoPicture.setColorScale(new Vector4f(1, 1, 1, pscale));
        }
        this.logoPicture.draw(getGameWindow());
    }


    public long getLifeTime() {
        return lifeTime;
    }

    public long getDieTimeStamp() {
        return dieTimeStamp;
    }
}
