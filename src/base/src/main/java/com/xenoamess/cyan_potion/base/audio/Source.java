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

package com.xenoamess.cyan_potion.base.audio;

import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

/**
 * @author XenoAmess
 */
public class Source implements AutoCloseable {
    private int alSourceInt = -1;
    private WaveData currentWaveData = null;

    public Source() {
        this.setAlSourceInt(AL10.alGenSources());
        this.setVolume(1);
        this.setPitch(1);
        this.setPosition(new Vector3f(0, 0, 0));
        this.setVelocity(new Vector3f(0, 0, 0));
    }

    public void setCurrentWaveData(WaveData waveData) {
        this.currentWaveData = waveData;
        this.currentWaveData.load();
        AL10.alSourcei(this.getAlSourceInt(), AL10.AL_BUFFER,
                waveData.getAlBufferInt());
    }

    public void setVolume(float volume) {
        AL10.alSourcef(this.getAlSourceInt(), AL10.AL_GAIN, volume);
    }

    public void setPitch(float pitch) {
        AL10.alSourcef(this.getAlSourceInt(), AL10.AL_PITCH, pitch);
    }

    public void setLooping(boolean looping) {
        AL10.alSourcei(this.getAlSourceInt(), AL10.AL_LOOPING, looping ?
                AL10.AL_TRUE : AL10.AL_FALSE);
    }

    public void setPosition(Vector3f position) {
        AL10.alSource3f(this.getAlSourceInt(), AL10.AL_POSITION, position.x,
                position.y, position.z);
    }

    public void setVelocity(Vector3f velocity) {
        AL10.alSource3f(this.getAlSourceInt(), AL10.AL_POSITION, velocity.x,
                velocity.y, velocity.z);
    }

    public int getState() {
        return AL10.alGetSourcei(this.getAlSourceInt(), AL10.AL_SOURCE_STATE);
    }

    public boolean isInitial() {
        return this.getState() == AL10.AL_INITIAL;
    }

    public boolean isPlaying() {
        return this.getState() == AL10.AL_PLAYING;
    }

    public boolean isPaused() {
        return this.getState() == AL10.AL_PAUSED;
    }

    public boolean isStopped() {
        return this.getState() == AL10.AL_STOPPED;
    }

    public void play(WaveData waveData) {
        this.setCurrentWaveData(waveData);
        this.play();
    }

    protected void play() {
        this.setCurrentWaveData(this.getCurrentWaveData());
        AL10.alSourcePlay(this.getAlSourceInt());
    }

    public void pause() {
        AL10.alSourcePause(this.getAlSourceInt());
    }

    public void stop() {
        AL10.alSourceStop(this.getAlSourceInt());
    }

    @Override
    public void close() {
        if (this.getAlSourceInt() != -1) {
            this.stop();
            AL10.alDeleteSources(getAlSourceInt());
            this.setAlSourceInt(-1);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!object.getClass().equals(this.getClass())) {
            return false;
        }
        final Source source = (Source) object;
        return this.getAlSourceInt() == source.getAlSourceInt() && this.getCurrentWaveData() == source.getCurrentWaveData();
    }

    @Override
    public int hashCode() {
        return this.getAlSourceInt();
    }


    public int getAlSourceInt() {
        return alSourceInt;
    }

    public void setAlSourceInt(int alSourceInt) {
        this.alSourceInt = alSourceInt;
    }

    public WaveData getCurrentWaveData() {
        return currentWaveData;
    }
}
