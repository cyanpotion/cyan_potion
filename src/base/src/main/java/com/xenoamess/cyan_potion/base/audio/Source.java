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

import com.xenoamess.cyan_potion.base.events.Event;
import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

import java.util.Collection;

import static org.lwjgl.openal.AL10.*;

/**
 * <p>Source class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class Source implements AutoCloseable {
    private int alSourceInt = -1;
    private WaveData currentWaveData = null;
    private Event playOverEvent = null;

    /**
     * <p>Constructor for Source.</p>
     */
    public Source() {
        this.setAlSourceInt(AL10.alGenSources());
        this.clean();
    }


    /**
     * <p>Constructor for Source.</p>
     */
    public void clean() {
        this.stop();
        this.setVolume(1);
        this.setPitch(1);
        this.setPosition(new Vector3f(0, 0, 0));
        this.setVelocity(new Vector3f(0, 0, 0));
        this.setRelative(true);
        this.setRollOffFactor(0.0f);
        this.setLooping(false);
        this.setPlayOverEvent(null);
    }

    /**
     * <p>Setter for the field <code>currentWaveData</code>.</p>
     *
     * @param waveData waveData
     */
    public void setCurrentWaveData(WaveData waveData) {
        this.currentWaveData = waveData;
        this.currentWaveData.load();
        AL10.alSourcei(this.getAlSourceInt(), AL10.AL_BUFFER,
                waveData.getAlBufferInt());
    }

    public void setRelative(boolean ifRelative) {
        alSourcei(this.getAlSourceInt(), AL10.AL_SOURCE_RELATIVE, ifRelative ? AL_TRUE : AL_FALSE);
    }

    public void setRollOffFactor(float rollOffFactor) {
        alSourcef(this.getAlSourceInt(), AL10.AL_ROLLOFF_FACTOR, rollOffFactor);
    }

    /**
     * <p>setVolume.</p>
     *
     * @param volume a float.
     */
    public void setVolume(float volume) {
        AL10.alSourcef(this.getAlSourceInt(), AL10.AL_GAIN, volume);
    }

    /**
     * <p>setPitch.</p>
     *
     * @param pitch a float.
     */
    public void setPitch(float pitch) {
        AL10.alSourcef(this.getAlSourceInt(), AL10.AL_PITCH, pitch);
    }

    /**
     * <p>setLooping.</p>
     *
     * @param looping a boolean.
     */
    public void setLooping(boolean looping) {
        AL10.alSourcei(this.getAlSourceInt(), AL10.AL_LOOPING, looping ?
                AL_TRUE : AL10.AL_FALSE);
    }

    /**
     * <p>setPosition.</p>
     *
     * @param position position
     */
    public void setPosition(Vector3f position) {
        AL10.alSource3f(this.getAlSourceInt(), AL10.AL_POSITION, position.x,
                position.y, position.z);
    }

    /**
     * <p>setVelocity.</p>
     *
     * @param velocity velocity
     */
    public void setVelocity(Vector3f velocity) {
        AL10.alSource3f(this.getAlSourceInt(), AL10.AL_POSITION, velocity.x,
                velocity.y, velocity.z);
    }

    /**
     * <p>getState.</p>
     *
     * @return a int.
     */
    public int getState() {
        return AL10.alGetSourcei(this.getAlSourceInt(), AL10.AL_SOURCE_STATE);
    }

    /**
     * <p>isInitial.</p>
     *
     * @return a boolean.
     */
    public boolean isInitial() {
        return this.getState() == AL10.AL_INITIAL;
    }

    /**
     * <p>isPlaying.</p>
     *
     * @return a boolean.
     */
    public boolean isPlaying() {
        return this.getState() == AL10.AL_PLAYING;
    }

    /**
     * <p>isPaused.</p>
     *
     * @return a boolean.
     */
    public boolean isPaused() {
        return this.getState() == AL10.AL_PAUSED;
    }

    /**
     * <p>isStopped.</p>
     *
     * @return a boolean.
     */
    public boolean isStopped() {
        return this.getState() == AL10.AL_STOPPED;
    }

    /**
     * <p>play.</p>
     *
     * @param waveData waveData
     */
    public void play(WaveData waveData) {
        this.setCurrentWaveData(waveData);
        this.play();
    }

    public Event getPlayOverEvent() {
        return playOverEvent;
    }

    public void setPlayOverEvent(Event playOverEvent) {
        this.playOverEvent = playOverEvent;
    }

    static class MultiWaveDataPlayThread implements Runnable {
        private final Source source;
        private final Collection<WaveData> waveDatas;

        public MultiWaveDataPlayThread(Source source, Collection<WaveData> waveDatas) {
            this.source = source;
            this.waveDatas = waveDatas;
        }

        @Override
        public void run() {
            for (WaveData waveData : waveDatas) {
                this.source.clean();
                this.source.play(waveData);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (this.source.isPlaying()) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            this.source.clean();
        }
    }

    /**
     * <p>play.</p>
     */
    public void play() {
        this.setCurrentWaveData(this.getCurrentWaveData());
        AL10.alSourcePlay(this.getAlSourceInt());
    }

    /**
     * <p>pause.</p>
     */
    public void pause() {
        AL10.alSourcePause(this.getAlSourceInt());
    }

    /**
     * <p>stop.</p>
     */
    public void stop() {
        AL10.alSourceStop(this.getAlSourceInt());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (this.getAlSourceInt() != -1) {
            this.stop();
            AL10.alDeleteSources(getAlSourceInt());
            this.setAlSourceInt(-1);
        }
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.getAlSourceInt();
    }

    /**
     * <p>Getter for the field <code>alSourceInt</code>.</p>
     *
     * @return a int.
     */
    public int getAlSourceInt() {
        return alSourceInt;
    }

    /**
     * <p>Setter for the field <code>alSourceInt</code>.</p>
     *
     * @param alSourceInt a int.
     */
    public void setAlSourceInt(int alSourceInt) {
        this.alSourceInt = alSourceInt;
    }


    /**
     * <p>Getter for the field <code>currentWaveData</code>.</p>
     *
     * @return return
     */
    public WaveData getCurrentWaveData() {
        return currentWaveData;
    }


}
