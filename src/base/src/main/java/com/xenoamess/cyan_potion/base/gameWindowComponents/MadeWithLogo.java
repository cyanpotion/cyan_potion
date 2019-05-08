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
import com.xenoamess.cyan_potion.base.events.KeyEvent;
import com.xenoamess.cyan_potion.base.events.MouseButtonEvent;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Font;
import org.joml.Vector4f;

import static com.xenoamess.cyan_potion.base.GameManagerConfig.getString;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author XenoAmess
 */
public class MadeWithLogo extends AbstractGameWindowComponent {
    private final Texture logoTexture;
    private final long lifeTime;
    private final long dieTimeStamp;


    public MadeWithLogo(GameWindow gameWindow, long lifeTime) {
        super(gameWindow);
        this.logoTexture =
                this.getGameWindow().getGameManager().getResourceManager().fetchResourceWithShortenURI(Texture.class,
                        "/www/img/pictures/madewith.png:picture");
        this.lifeTime = lifeTime;
        this.dieTimeStamp = System.currentTimeMillis() + this.getLifeTime();
        this.getGameWindow().getGameManager().getAudioManager().playNew(this.getGameWindow().getGameManager().getResourceManager().fetchResourceWithShortenURI(WaveData.class, "/www/audio/se/madewith.ogg:music"));

    }

    public MadeWithLogo(GameWindow gameWindow) {
        this(gameWindow, 5000L + 5000L);
    }

    @Override
    public void initProcessors() {
        this.registerProcessor(KeyEvent.class.getCanonicalName(), event -> {
            KeyEvent keyEvent = (KeyEvent) event;
            switch (keyEvent.getKeyTranslated(this.getGameWindow().getGameManager().getKeymap()).getKey()) {
                case Keymap.XENOAMESS_KEY_ESCAPE:
                case Keymap.XENOAMESS_KEY_ENTER:
                case Keymap.XENOAMESS_KEY_SPACE:
                    this.setAlive(false);
                    break;
                default:
                    return event;
            }
            return null;
        });

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

        if (!this.getAlive() && Font.getDefaultFont() != null) {
            this.getGameWindowComponentTreeNode().close();
            {
                Font.getDefaultFont().init(this.getGameWindow());
                AbstractGameWindowComponent title =
                        AbstractGameWindowComponent.createGameWindowComponentFromClassName(this.getGameWindow(),
                                getString(this.getGameWindow().getGameManager().getDataCenter().getCommonSettings(),
                                        "titleClassName",
                                        "com.xenoamess.cyan_potion.base.gameWindowComponents.TitleExample"));

                title.addToGameWindowComponentTree(null);
                title.enlargeAsFullWindow();
            }
        }
    }

    @Override
    public void draw() {
        if (!this.getAlive()) {
            glClearColor(1, 1, 1, 1);
            glClear(GL_COLOR_BUFFER_BIT);
            return;
        }
//        glClearColor(1, 1, 1, 1);
//        glClear(GL_COLOR_BUFFER_BIT);

        long t =
                this.getLifeTime() - this.getDieTimeStamp() + System.currentTimeMillis();
        float cscale;

        long stayTime = 5000L;
        long fadeTime = 5000L;
        if (t < stayTime) {
            cscale = 1;
        } else {
            cscale = 1 + ((float) (t - stayTime)) / fadeTime * 400;
        }
//        cscale = 1.1f;


        this.getGameWindow().drawBindableRelativeCenter(this.getLogoTexture()
                , this.getGameWindow().getLogicWindowWidth(),
                this.getGameWindow().getLogicWindowHeight(), new Vector4f(1,
                        cscale, cscale, 1));
    }

    public Texture getLogoTexture() {
        return logoTexture;
    }

    public long getLifeTime() {
        return lifeTime;
    }

    public long getDieTimeStamp() {
        return dieTimeStamp;
    }
}
