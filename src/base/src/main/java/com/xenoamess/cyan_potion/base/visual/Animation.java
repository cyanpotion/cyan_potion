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

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Animation class.</p>
 *
 * @author XenoAmess
 * @version 0.159.0-SNAPSHOT
 */
public class Animation extends AbstractPicture {
    private int texturePointer;

    private long lastTime = System.currentTimeMillis();
    private float fps;

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


    /**
     * <p>Getter for the field <code>texturePointer</code>.</p>
     *
     * @return a int.
     */
    public int getTexturePointer() {
        return texturePointer;
    }

    /**
     * <p>Setter for the field <code>texturePointer</code>.</p>
     *
     * @param texturePointer a int.
     */
    public void setTexturePointer(int texturePointer) {
        this.texturePointer = texturePointer;
    }

    /**
     * <p>Getter for the field <code>lastTime</code>.</p>
     *
     * @return a long.
     */
    public long getLastTime() {
        return lastTime;
    }

    /**
     * <p>Setter for the field <code>lastTime</code>.</p>
     *
     * @param lastTime a long.
     */
    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    /**
     * <p>Getter for the field <code>fps</code>.</p>
     *
     * @return a float.
     */
    public float getFps() {
        return fps;
    }

    /**
     * <p>Setter for the field <code>fps</code>.</p>
     *
     * @param fps a float.
     */
    public void setFps(float fps) {
        this.fps = fps;
    }

    /**
     * <p>Getter for the field <code>frames</code>.</p>
     *
     * @return return
     */
    public List<AbstractPictureInterface> getFrames() {
        return frames;
    }
}
