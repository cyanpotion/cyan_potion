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
