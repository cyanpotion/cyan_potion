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
import com.xenoamess.commonx.java.util.Arraysx;
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.SubManager;
import com.xenoamess.cyan_potion.base.events.Event;
import lombok.*;
import org.joml.Vector3f;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext;

/**
 * AudioManager class.
 * Audios Manager is manager class of some SE audio.
 *
 * @author XenoAmess
 * @version 0.161.2
 * @see #useSource()
 * @see #useSource(WaveData)
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class AudioManager extends SubManager {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(AudioManager.class);
    /**
     * Initial used Source s' num.
     */
    public static final int INITIAL_TEMP_SOURCES_NUM = 128;

    /**
     * Well sometimes we just want to map some sources to some name and get them by name and reuse them.
     * For example we can cache a "BGM" and a specific Source object into this map,
     * Then use "BGM" to get this Source.
     * It is really quite boring and can be done in a more elegant way.
     * So I'm just wondering if we shall delete it?
     */
    @Getter(AccessLevel.PRIVATE)
    private final Map<String, Source> specialSources = new ConcurrentHashMap<>();

    /**
     * Unused sources.
     */
    @Getter(AccessLevel.PRIVATE)
    private final Set<Source> unusedSources = ConcurrentHashMap.newKeySet();
    /**
     * Used sources.
     */
    @Getter(AccessLevel.PRIVATE)
    private final Set<Source> usedSources = ConcurrentHashMap.newKeySet();

    @Getter
    @Setter
    private long openalDevice = -1L;
    @Getter
    @Setter
    private long openalContext = -1L;

    /**
     * Position of the listener.
     */
    @Getter
    private Vector3f listenerPosition = null;


    /**
     * Velocity of the listener.
     */
    @Getter
    private Vector3f listenerVelocity = null;

    /**
     * <p>Constructor for AudioManager.</p>
     *
     * @param gameManager gameManager
     */
    public AudioManager(GameManager gameManager) {
        super(gameManager);
    }

    /**
     * <p>init.</p>
     */
    @MainThreadOnly
    public void init() {
        this.close();

        ALC.create();
        this.setOpenalDevice(alcOpenDevice((ByteBuffer) null));
        if (this.getOpenalDevice() == MemoryUtil.NULL) {
            throw new IllegalStateException("Failed to open the default " +
                    "device.");
        }
        ALCCapabilities deviceCaps =
                ALC.createCapabilities(this.getOpenalDevice());
        this.setOpenalContext(ALC10.alcCreateContext(this.getOpenalDevice(),
                (IntBuffer) null));
        alcSetThreadContext(this.getOpenalContext());
        AL.createCapabilities(deviceCaps);
        this.setListenerPosition(new Vector3f(0, 0, 0));
        this.setListenerVelocity(new Vector3f(0, 0, 0));

        this.getUnusedSources().addAll(Arrays.asList(Arraysx.fillNewSelf(new Source[INITIAL_TEMP_SOURCES_NUM])));
    }

    /**
     * <p>update.</p>
     */
    public void update() {
        this.gc();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @MainThreadOnly
    public void close() {
        getUnusedSources().clear();
        getUsedSources().clear();

        for (Map.Entry<String, Source> entry : getSpecialSources().entrySet()) {
            entry.getValue().close();
        }
        getSpecialSources().clear();
        if (getOpenalContext() != -1) {
            alcDestroyContext(getOpenalContext());
            setOpenalContext(-1);
        }
        if (getOpenalDevice() != -1) {
            alcCloseDevice(getOpenalDevice());
            setOpenalDevice(-1);
        }
        ALC.destroy();
    }

    /**
     * since gc must not happened in solveEvents(), so it is safe to do eventListAdd().
     * (now we can even do eventListAdd() in solveEvents() so this tip can be just ignored)
     */
    public synchronized void gc() {
        ArrayList<Source> deletedSource = new ArrayList<>();
        for (Source au : getUsedSources()) {
            if (au.isStopped()) {
                deletedSource.add(au);
            } else {
                au.getCurrentWaveData().setLastUsedFrameIndex(this.getGameManager().getNowFrameIndex());
            }
        }
        for (Source au : deletedSource) {
            this.getGameManager().eventListAdd(au.getPlayOverEvent());
            getUnusedSources().add(au);
            getUsedSources().remove(au);
        }
    }

    /**
     * Get an unused source, and then delete it from unused sources,
     * then add it to used sources, clean it, then return it.
     * <p>
     * Notice that Sources in AudioManager are auto-managed, and will be re-used when it stop playing,
     * so please never try to hold any reference to the returned Source.
     * <p>
     * Notice that please play the Source get from the function immediately,(before the next gc()),
     * or it might be reused.
     * <p>
     * When you want to run some sound effect(short, not looping), then use this.
     * When you want a full control of the source, then you shall create Source by your own.
     *
     * @return the source that we will use.
     */
    @MainThreadOnly
    public synchronized Source useSource() {
        if (getUnusedSources().isEmpty()) {
            this.gc();
        }
        if (getUnusedSources().isEmpty()) {
            getUnusedSources().add(new Source());
            LOGGER.debug("Audio source exhausted! Current num : {}",
                    this.getUsedSources().size());
        }
        Iterator<Source> sourceIterator = getUnusedSources().iterator();
        Source source = sourceIterator.next();
        sourceIterator.remove();
        getUsedSources().add(source);
        source.clean();
        return source;
    }


    /**
     * Get an unused source, and then delete it from unused sources,
     * then add it to used sources, clean it, then return it.
     * <p>
     * Notice that Sources in AudioManager are auto-managed, and will be re-used when it stop playing,
     * so please never try to hold any reference to the returned Source.
     * <p>
     * Notice that please play the Source get from the function immediately,(before the next gc()),
     * or it might be reused.
     * <p>
     * When you want to run some sound effect(short, not looping), then use this.
     * When you want a full control of the source, then you shall create Source by your own.
     *
     * @param waveData the WaveData you wanna play in this source.
     * @return the source that we will use.
     */
    @MainThreadOnly
    public synchronized Source useSource(WaveData waveData) {
        Source source = this.useSource();
        source.setCurrentWaveData(waveData);
        return source;
    }

    /**
     * please just use playWaveData(WaveData waveData) instead.
     * ----------
     * Get an unused source, and then delete it from unused sources,
     * then add it to used sources, clean it, then play it, then return it.
     * <p>
     * Notice that Sources in AudioManager are auto-managed, and will be re-used when it stop playing,
     * so please never try to hold any reference to the returned Source.
     * <p>
     * Notice that please play the Source get from the function immediately,(before the next gc()),
     * or it might be reused.
     * <p>
     * When you want to run some sound effect(short, not looping), then use this.
     * When you want a full control of the source, then you shall create Source by your own.
     *
     * @param waveData the WaveData you wanna play.
     * @return the source that we will use.
     * @deprecated
     */
    @MainThreadOnly
    @Deprecated
    public synchronized Source playSource(WaveData waveData) {
        return this.playSource(waveData, null);
    }

    /**
     * please just use playWaveData(WaveData waveData, Event playOverEvent) instead.
     * ----------
     * Get an unused source, and then delete it from unused sources,
     * then add it to used sources, clean it, then play it, then return it.
     * <p>
     * Notice that Sources in AudioManager are auto-managed, and will be re-used when it stop playing,
     * so please never try to hold any reference to the returned Source.
     * <p>
     * Notice that please play the Source get from the function immediately,(before the next gc()),
     * or it might be reused.
     * <p>
     * When you want to run some sound effect(short, not looping), then use this.
     * When you want a full control of the source, then you shall create Source by your own.
     *
     * @param waveData      the WaveData you wanna play.
     * @param playOverEvent the event that will invoke after the waveData played.
     * @return the source that we will use.
     * @deprecated
     */
    @MainThreadOnly
    @Deprecated
    public synchronized Source playSource(WaveData waveData, Event playOverEvent) {
        Source source = this.useSource(waveData);
        source.setPlayOverEvent(playOverEvent);
        source.play();
        return source;
    }

    /**
     * <p>playWaveData.</p>
     *
     * @param waveData the WaveData you wanna play.
     */
    public void playWaveData(WaveData waveData) {
        this.playWaveData(waveData, null);
    }

    /**
     * <p>playWaveData.</p>
     *
     * @param waveData      the WaveData you wanna play.
     * @param playOverEvent the event that will invoke after the waveData played.
     */
    public void playWaveData(WaveData waveData, Event playOverEvent) {
        this.play(this.generatePlayAudioEvent(waveData, playOverEvent));
    }

    /**
     * <p>generatePlayAudioEvent.</p>
     *
     * @param waveData the WaveData you wanna play.
     * @return the generated {@link com.xenoamess.cyan_potion.base.audio.PlayAudioEvent} object.
     */
    public PlayAudioEvent generatePlayAudioEvent(WaveData waveData) {
        return this.generatePlayAudioEvent(waveData, null);
    }

    /**
     * <p>generatePlayAudioEvent.</p>
     *
     * @param waveData      the WaveData you wanna play.
     * @param playOverEvent the event that will invoke after the waveData played.
     * @return the generated {@link com.xenoamess.cyan_potion.base.audio.PlayAudioEvent} object.
     */
    public PlayAudioEvent generatePlayAudioEvent(WaveData waveData, Event playOverEvent) {
        return new PlayAudioEvent(this, waveData, playOverEvent);
    }

    /**
     * <p>generatePlayAudioEvent.</p>
     *
     * @param waveDatas the WaveData List you wanna play, one by one
     * @return the generated {@link com.xenoamess.cyan_potion.base.audio.PlayAudioEvent} object.
     */
    public PlayAudioEvent generatePlayAudioEvent(List<WaveData> waveDatas) {
        if (waveDatas == null || waveDatas.isEmpty()) {
            return null;
        }
        WaveData[] waveDatasArray = new WaveData[waveDatas.size()];
        waveDatas.toArray(waveDatasArray);
        PlayAudioEvent playAudioEvent = null;
        for (int i = waveDatasArray.length - 1; i >= 0; i--) {
            playAudioEvent = new PlayAudioEvent(this, waveDatasArray[i], playAudioEvent);
        }
        return playAudioEvent;
    }

    /**
     * add the playAudioEvent to eventList.
     * (It will play itself when apply().
     *
     * @param playAudioEvent a {@link com.xenoamess.cyan_potion.base.audio.PlayAudioEvent} object.
     */
    public void play(PlayAudioEvent playAudioEvent) {
        this.getGameManager().eventListAdd(playAudioEvent);
    }


    /**
     * pause all playing sources.
     */
    @MainThreadOnly
    public void pauseAll() {
        for (Source au : getUsedSources()) {
            if (au.isPlaying()) {
                au.pause();
            }
        }
    }

    /**
     * resume all paused sources.
     */
    @MainThreadOnly
    public void resumeAll() {
        for (Source au : getUsedSources()) {
            if (au.isPaused()) {
                au.play();
            }
        }
    }

    /**
     * stop all sources.
     */
    @MainThreadOnly
    public void stopAll() {
        for (Source au : getUsedSources()) {
            au.stop();
        }
    }

    /**
     * <p>Setter for the field <code>listenerPosition</code>.</p>
     *
     * @param listenerPosition listenerPosition
     */
    @MainThreadOnly
    public void setListenerPosition(Vector3f listenerPosition) {
        this.listenerPosition = new Vector3f(listenerPosition);
        AL10.alListener3f(AL10.AL_POSITION, this.listenerPosition.x,
                this.listenerPosition.y, this.listenerPosition.z);
    }

    /**
     * <p>Setter for the field <code>listenerVelocity</code>.</p>
     *
     * @param listenerVelocity listenerVelocity
     */
    @MainThreadOnly
    public void setListenerVelocity(Vector3f listenerVelocity) {
        this.listenerVelocity = new Vector3f(listenerVelocity);
        AL10.alListener3f(AL10.AL_VELOCITY, this.listenerVelocity.x,
                this.listenerVelocity.y, this.listenerVelocity.z);
    }
}
