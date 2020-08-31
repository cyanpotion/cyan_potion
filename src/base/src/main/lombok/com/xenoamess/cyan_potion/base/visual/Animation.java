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

package com.xenoamess.cyan_potion.base.visual;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.game_window_components.AbstractScene;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Animation class.</p>
 *
 * @author XenoAmess
 * @version 0.162.3
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class Animation extends AbstractPicture {

    @Getter
    @Setter
    private int texturePointer;

    @Getter
    @Setter
    private long lastTime = System.currentTimeMillis();

    @Getter
    @Setter
    private float fps;

    @Getter
    private final List<AbstractPictureInterface> frames = new ArrayList<>();


    /**
     * <p>Constructor for Animation.</p>
     *
     * @param fps a int.
     */
    public Animation(int fps) {
        this.setTexturePointer(0);
        this.setFps(fps);
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unused")
    @Override
    public void draw(GameWindow gameWindow) {
        AbstractPictureInterface pictureInterface = this.getCurrentPicture();
        pictureInterface.cover(this);
        pictureInterface.draw(gameWindow);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(AbstractScene scene) {
        AbstractPictureInterface pictureInterface = this.getCurrentPicture();
        pictureInterface.cover(this);
        pictureInterface.draw(scene);
    }

    /**
     * <p>getCurrentPicture.</p>
     *
     * @return return
     */
    public AbstractPictureInterface getCurrentPicture() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - getLastTime();
        int texturePointer = getTexturePointer();
        int textureAddNum = (int) Math.floor(elapsedTime / 1000.0 * getFps());
        texturePointer += textureAddNum;
        texturePointer %= getFrames().size();
        setTexturePointer(texturePointer);
        this.setLastTime(getLastTime() + (long) (textureAddNum * 1000.0 / getFps()));

        return getFrames().get(getTexturePointer());
    }
}
