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

package com.xenoamess.cyan_potion.base.audio;

import com.xenoamess.commons.main_thread_only.MainThreadOnly;
import com.xenoamess.cyan_potion.base.events.Event;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

import java.io.Closeable;

import static org.lwjgl.openal.AL10.*;

/**
 * <p>Source class.</p>
 *
 * @author XenoAmess
 * @version 0.161.3
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Source implements Closeable {

    @EqualsAndHashCode.Include
    @Getter
    @Setter
    private int alSourceInt = -1;

    @Getter
    @Setter
    private WaveData currentWaveData = null;

    /**
     * an event that will be inserted into the eventList (by source)
     * when this WaveData is played over.
     */
    @Getter
    @Setter
    private Event playOverEvent = null;

    @Getter
    @Setter
    private float volume;

    @Getter
    @Setter
    private float pitch;

    @Getter
    @Setter
    private Vector3f position;

    @Getter
    @Setter
    private Vector3f velocity;

    @Getter
    @Setter
    private boolean relative;

    @Getter
    @Setter
    private float rollOffFactor;

    @Getter
    @Setter
    private boolean looping;

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
    @MainThreadOnly
    public void setCurrentWaveData(WaveData waveData) {
        this.currentWaveData = waveData;
        this.currentWaveData.load();
        AL10.alSourcei(this.getAlSourceInt(), AL10.AL_BUFFER,
                waveData.getAlBufferInt());
    }

    /**
     * <p>Setter for the field <code>relative</code>.</p>
     *
     * @param relative a boolean.
     */
    @MainThreadOnly
    public void setRelative(boolean relative) {
        this.relative = relative;
        alSourcei(this.getAlSourceInt(), AL10.AL_SOURCE_RELATIVE, relative ? AL_TRUE : AL_FALSE);
    }

    /**
     * <p>Setter for the field <code>rollOffFactor</code>.</p>
     *
     * @param rollOffFactor a float.
     */
    @MainThreadOnly
    public void setRollOffFactor(float rollOffFactor) {
        this.rollOffFactor = rollOffFactor;
        alSourcef(this.getAlSourceInt(), AL10.AL_ROLLOFF_FACTOR, rollOffFactor);
    }

    /**
     * <p>setVolume.</p>
     *
     * @param volume a float.
     */
    @MainThreadOnly
    public void setVolume(float volume) {
        this.volume = volume;
        AL10.alSourcef(this.getAlSourceInt(), AL10.AL_GAIN, this.volume);
    }

    /**
     * <p>setPitch.</p>
     *
     * @param pitch a float.
     */
    @MainThreadOnly
    public void setPitch(float pitch) {
        this.pitch = pitch;
        AL10.alSourcef(this.getAlSourceInt(), AL10.AL_PITCH, this.pitch);
    }

    /**
     * <p>setLooping.</p>
     *
     * @param looping a boolean.
     */
    @MainThreadOnly
    public void setLooping(boolean looping) {
        this.looping = looping;
        AL10.alSourcei(this.getAlSourceInt(), AL10.AL_LOOPING,
                this.looping ? AL_TRUE : AL10.AL_FALSE);
    }

    /**
     * <p>setPosition.</p>
     *
     * @param position position
     */
    @MainThreadOnly
    public void setPosition(Vector3f position) {
        this.position = position;
        AL10.alSource3f(this.getAlSourceInt(), AL10.AL_POSITION,
                this.position.x, this.position.y, this.position.z);
    }

    /**
     * <p>setVelocity.</p>
     *
     * @param velocity velocity
     */
    @MainThreadOnly
    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
        AL10.alSource3f(this.getAlSourceInt(), AL10.AL_POSITION, velocity.x,
                velocity.y, velocity.z);
    }

    /**
     * <p>getState.</p>
     *
     * @return a int.
     */
    @MainThreadOnly
    public int getState() {
        return AL10.alGetSourcei(this.getAlSourceInt(), AL10.AL_SOURCE_STATE);
    }

    /**
     * <p>isInitial.</p>
     *
     * @return a boolean.
     */
    @MainThreadOnly
    public boolean isInitial() {
        return this.getState() == AL10.AL_INITIAL;
    }

    /**
     * <p>isPlaying.</p>
     *
     * @return a boolean.
     */
    @MainThreadOnly
    public boolean isPlaying() {
        return this.getState() == AL10.AL_PLAYING;
    }

    /**
     * <p>isPaused.</p>
     *
     * @return a boolean.
     */
    @MainThreadOnly
    public boolean isPaused() {
        return this.getState() == AL10.AL_PAUSED;
    }

    /**
     * <p>isStopped.</p>
     *
     * @return a boolean.
     */
    @MainThreadOnly
    public boolean isStopped() {
        return this.getState() == AL10.AL_STOPPED;
    }

    /**
     * <p>play.</p>
     *
     * @param waveData waveData
     */
    @MainThreadOnly
    public void play(WaveData waveData) {
        this.setCurrentWaveData(waveData);
        this.play();
    }

    /**
     * <p>play.</p>
     */
    @MainThreadOnly
    public void play() {
        this.setCurrentWaveData(this.getCurrentWaveData());
        AL10.alSourcePlay(this.getAlSourceInt());
    }

    /**
     * <p>pause.</p>
     */
    @MainThreadOnly
    public void pause() {
        AL10.alSourcePause(this.getAlSourceInt());
    }

    /**
     * <p>stop.</p>
     */
    @MainThreadOnly
    public void stop() {
        AL10.alSourceStop(this.getAlSourceInt());
    }

    /**
     * {@inheritDoc}
     */
    @MainThreadOnly
    @Override
    public void close() {
        if (this.getAlSourceInt() != -1) {
            this.stop();
            AL10.alDeleteSources(getAlSourceInt());
            this.setAlSourceInt(-1);
        }
    }
}
