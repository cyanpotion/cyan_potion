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

import com.xenoamess.cyan_potion.base.render.Bindable;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Animation class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class Animation extends AbstractPicture {
    private int texturePointer;

    private long lastTime = System.currentTimeMillis();
    private float fps;

    private final List<AbstractPicture> frames = new ArrayList<>();


    /**
     * <p>Constructor for Animation.</p>
     *
     * @param fps a int.
     */
    public Animation(int fps) {
        this.setTexturePointer(0);
        this.setFps(1f / fps);
    }

    /**
     * <p>getCurrentPicture.</p>
     *
     * @return return
     */
    public AbstractPicture getCurrentPicture() {
        long currentTime = System.currentTimeMillis();
        float elapsedTime = currentTime - getLastTime();
        elapsedTime /= 1000;

        if (elapsedTime >= getFps()) {
            setTexturePointer(getTexturePointer() + 1);
            this.setLastTime(currentTime);
        }

        if (getTexturePointer() >= getFrames().size()) {
            setTexturePointer(0);
        }

        return getFrames().get(getTexturePointer());
    }

    /**
     * <p>getCurrentBindable.</p>
     *
     * @return return
     */
    @Override
    public Bindable getCurrentBindable() {
        return this.getCurrentPicture().getCurrentBindable();
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
    public List<AbstractPicture> getFrames() {
        return frames;
    }
}
