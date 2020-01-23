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
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.events.EmptyEvent;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.events.MainThreadEvent;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * it is a little complex.
 * if this.source==null, then will get a source from AudioManager.
 * if this.waveData==null, then will use source.getCurrentWaveData() as waveData
 *
 * @author XenoAmess
 */
public class PlayAudioEvent implements MainThreadEvent {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MainThreadEvent.class);

    private static class EmptyPlayAudioEvent extends PlayAudioEvent implements EmptyEvent {
        public EmptyPlayAudioEvent() {
            super(null, null, null, null);
        }

        @Override
        public Set<Event> apply(GameManager gameManager) {
            return null;
        }
    }

    public static final PlayAudioEvent EMPTY = new PlayAudioEvent.EmptyPlayAudioEvent();

    private AudioManager audioManager;
    /**
     * the WaveData that will be played.
     * if this.waveData==null then will play this.source.waveData instead.
     */
    private final WaveData waveData;
    /**
     * source.
     * if null then will invoke this.getAudioManager().useSource() and grab an unused source and use it.
     */
    private final Source source;

    private float volume;
    private float pitch;
    private Vector3f position;
    private Vector3f velocity;
    private boolean relative;
    private float rollOffFactor;
    private boolean looping;

    /**
     * an event that will be inserted into the eventList (by source)
     * when this WaveData is played over.
     *
     * @see Source#getPlayOverEvent()
     */
    private Event playOverEvent;

    /**
     * <p>Constructor for Source.</p>
     */
    public void init() {
        if (getSource() == null) {
            this.setVolume(1);
            this.setPitch(1);
            this.setPosition(new Vector3f(0, 0, 0));
            this.setVelocity(new Vector3f(0, 0, 0));
            this.setRelative(true);
            this.setRollOffFactor(0.0f);
            this.setLooping(false);
        } else {
            this.setVolume(source.getVolume());
            this.setPitch(source.getPitch());
            this.setPosition(source.getPosition());
            this.setVelocity(source.getVelocity());
            this.setRelative(source.getRelative());
            this.setRollOffFactor(source.getRollOffFactor());
            this.setLooping(source.getLooping());
        }
    }


    public PlayAudioEvent(AudioManager audioManager, WaveData waveData) {
        this(audioManager, waveData, null);
    }

    public PlayAudioEvent(AudioManager audioManager, WaveData waveData, Event playOverEvent) {
        this(audioManager, null, waveData, null);
    }

    public PlayAudioEvent(AudioManager audioManager, Source source, Event playOverEvent) {
        this(audioManager, source, null, null);
    }

    public PlayAudioEvent(AudioManager audioManager, Source source, WaveData waveData, Event playOverEvent) {
        this.setAudioManager(audioManager);
        this.source = source;
        this.waveData = waveData;
        this.setPlayOverEvent(playOverEvent);
        this.init();
    }

    /**
     * {@inheritDoc}
     *
     * @param gameManager
     */
    @Override
    @MainThreadOnly
    public Set<Event> apply(GameManager gameManager) {
        if (gameManager.getDataCenter().isDebug()) {
            LOGGER.debug("{}", this);
        }
        Source source;
        if (this.getSource() == null) {
            source = this.getAudioManager().useSource();
        } else {
            source = this.getSource();
        }

        source.setVolume(this.getVolume());
        source.setPitch(this.getPitch());
        source.setPosition(this.getPosition());
        source.setVelocity(this.getVelocity());
        source.setRelative(this.isRelative());
        source.setRollOffFactor(this.getRollOffFactor());
        source.setLooping(this.isLooping());

        if (this.waveData != null) {
            source.setCurrentWaveData(this.waveData);
        }
        source.setPlayOverEvent(this.getPlayOverEvent());
        source.play();
        return null;
    }

    public Source getSource() {
        return source;
    }

    public WaveData getWaveData() {
        return waveData;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public boolean isRelative() {
        return relative;
    }

    public void setRelative(boolean relative) {
        this.relative = relative;
    }

    public float getRollOffFactor() {
        return rollOffFactor;
    }

    public void setRollOffFactor(float rollOffFactor) {
        this.rollOffFactor = rollOffFactor;
    }

    public boolean isLooping() {
        return looping;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    public Event getPlayOverEvent() {
        return playOverEvent;
    }

    public void setPlayOverEvent(Event playOverEvent) {
        this.playOverEvent = playOverEvent;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public void setAudioManager(AudioManager audioManager) {
        this.audioManager = audioManager;
    }
}
