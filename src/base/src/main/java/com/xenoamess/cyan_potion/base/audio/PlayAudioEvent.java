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

import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * @version 0.155.2
 */
public class PlayAudioEvent implements MainThreadEvent {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(PlayAudioEvent.class);

    private static class EmptyPlayAudioEvent extends PlayAudioEvent implements EmptyEvent {
        public EmptyPlayAudioEvent() {
            super(null, null, null, null);
        }

        @Override
        public Set<Event> apply(GameManager gameManager) {
            return null;
        }
    }

    /**
     * use this instead of null for safety.
     *
     * @see EmptyEvent
     */
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
            this.setRelative(source.isRelative());
            this.setRollOffFactor(source.getRollOffFactor());
            this.setLooping(source.isLooping());
        }
    }


    /**
     * <p>Constructor for PlayAudioEvent.</p>
     *
     * @param audioManager a {@link com.xenoamess.cyan_potion.base.audio.AudioManager} object.
     * @param waveData     a {@link com.xenoamess.cyan_potion.base.audio.WaveData} object.
     */
    public PlayAudioEvent(AudioManager audioManager, WaveData waveData) {
        this(audioManager, waveData, null);
    }

    /**
     * <p>Constructor for PlayAudioEvent.</p>
     *
     * @param audioManager  a {@link com.xenoamess.cyan_potion.base.audio.AudioManager} object.
     * @param waveData      a {@link com.xenoamess.cyan_potion.base.audio.WaveData} object.
     * @param playOverEvent a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
    public PlayAudioEvent(AudioManager audioManager, WaveData waveData, Event playOverEvent) {
        this(audioManager, null, waveData, null);
    }

    /**
     * <p>Constructor for PlayAudioEvent.</p>
     *
     * @param audioManager  a {@link com.xenoamess.cyan_potion.base.audio.AudioManager} object.
     * @param source        a {@link com.xenoamess.cyan_potion.base.audio.Source} object.
     * @param playOverEvent a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
    public PlayAudioEvent(AudioManager audioManager, Source source, Event playOverEvent) {
        this(audioManager, source, null, null);
    }

    /**
     * <p>Constructor for PlayAudioEvent.</p>
     *
     * @param audioManager  a {@link com.xenoamess.cyan_potion.base.audio.AudioManager} object.
     * @param source        a {@link com.xenoamess.cyan_potion.base.audio.Source} object.
     * @param waveData      a {@link com.xenoamess.cyan_potion.base.audio.WaveData} object.
     * @param playOverEvent a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
    public PlayAudioEvent(AudioManager audioManager, Source source, WaveData waveData, Event playOverEvent) {
        this.setAudioManager(audioManager);
        this.source = source;
        this.waveData = waveData;
        this.setPlayOverEvent(playOverEvent);
        this.init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @MainThreadOnly
    public Set<Event> apply(GameManager gameManager) {
        if (gameManager.getDataCenter().getGameSettings().isDebug()) {
            LOGGER.debug("{}", this);
        }
        Source usedSource;
        if (this.getSource() == null) {
            usedSource = this.getAudioManager().useSource();
        } else {
            usedSource = this.getSource();
        }

        usedSource.setVolume(this.getVolume());
        usedSource.setPitch(this.getPitch());
        usedSource.setPosition(this.getPosition());
        usedSource.setVelocity(this.getVelocity());
        usedSource.setRelative(this.isRelative());
        usedSource.setRollOffFactor(this.getRollOffFactor());
        usedSource.setLooping(this.isLooping());

        if (this.waveData != null) {
            usedSource.setCurrentWaveData(this.waveData);
        }
        usedSource.setPlayOverEvent(this.getPlayOverEvent());
        usedSource.play();
        return null;
    }

    /**
     * <p>Getter for the field <code>source</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.audio.Source} object.
     */
    public Source getSource() {
        return source;
    }

    /**
     * <p>Getter for the field <code>waveData</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.audio.WaveData} object.
     */
    public WaveData getWaveData() {
        return waveData;
    }

    /**
     * <p>Getter for the field <code>volume</code>.</p>
     *
     * @return a float.
     */
    public float getVolume() {
        return volume;
    }

    /**
     * <p>Setter for the field <code>volume</code>.</p>
     *
     * @param volume a float.
     */
    public void setVolume(float volume) {
        this.volume = volume;
    }

    /**
     * <p>Getter for the field <code>pitch</code>.</p>
     *
     * @return a float.
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * <p>Setter for the field <code>pitch</code>.</p>
     *
     * @param pitch a float.
     */
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    /**
     * <p>Getter for the field <code>position</code>.</p>
     *
     * @return a {@link org.joml.Vector3f} object.
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * <p>Setter for the field <code>position</code>.</p>
     *
     * @param position a {@link org.joml.Vector3f} object.
     */
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    /**
     * <p>Getter for the field <code>velocity</code>.</p>
     *
     * @return a {@link org.joml.Vector3f} object.
     */
    public Vector3f getVelocity() {
        return velocity;
    }

    /**
     * <p>Setter for the field <code>velocity</code>.</p>
     *
     * @param velocity a {@link org.joml.Vector3f} object.
     */
    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    /**
     * <p>isRelative.</p>
     *
     * @return a boolean.
     */
    public boolean isRelative() {
        return relative;
    }

    /**
     * <p>Setter for the field <code>relative</code>.</p>
     *
     * @param relative a boolean.
     */
    public void setRelative(boolean relative) {
        this.relative = relative;
    }

    /**
     * <p>Getter for the field <code>rollOffFactor</code>.</p>
     *
     * @return a float.
     */
    public float getRollOffFactor() {
        return rollOffFactor;
    }

    /**
     * <p>Setter for the field <code>rollOffFactor</code>.</p>
     *
     * @param rollOffFactor a float.
     */
    public void setRollOffFactor(float rollOffFactor) {
        this.rollOffFactor = rollOffFactor;
    }

    /**
     * <p>isLooping.</p>
     *
     * @return a boolean.
     */
    public boolean isLooping() {
        return looping;
    }

    /**
     * <p>Setter for the field <code>looping</code>.</p>
     *
     * @param looping a boolean.
     */
    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    /**
     * <p>Getter for the field <code>playOverEvent</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
    public Event getPlayOverEvent() {
        return playOverEvent;
    }

    /**
     * <p>Setter for the field <code>playOverEvent</code>.</p>
     *
     * @param playOverEvent a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
    public void setPlayOverEvent(Event playOverEvent) {
        this.playOverEvent = playOverEvent;
    }

    /**
     * <p>Getter for the field <code>audioManager</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.audio.AudioManager} object.
     */
    public AudioManager getAudioManager() {
        return audioManager;
    }

    /**
     * <p>Setter for the field <code>audioManager</code>.</p>
     *
     * @param audioManager a {@link com.xenoamess.cyan_potion.base.audio.AudioManager} object.
     */
    public void setAudioManager(AudioManager audioManager) {
        this.audioManager = audioManager;
    }
}
