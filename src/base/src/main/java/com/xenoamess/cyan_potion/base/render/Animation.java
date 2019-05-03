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

package com.xenoamess.cyan_potion.base.render;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XenoAmess
 */
public class Animation implements Bindable {
    private int texturePointer;

    private long lastTime = System.currentTimeMillis();
    private float fps;

    private final List<Bindable> frames = new ArrayList<>();


    public Animation(int fps) {
        this.setTexturePointer(0);
        this.setFps(1f / fps);
    }

    public Bindable getCurrentBindable() {
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

    @Override
    public void bind(int sampler) {
        this.getCurrentBindable().bind(sampler);
    }

    @Override
    public void unbind() {
        this.getCurrentBindable().unbind();
    }

    public int getTexturePointer() {
        return texturePointer;
    }

    public void setTexturePointer(int texturePointer) {
        this.texturePointer = texturePointer;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public float getFps() {
        return fps;
    }

    public void setFps(float fps) {
        this.fps = fps;
    }

    public List<Bindable> getFrames() {
        return frames;
    }
}
