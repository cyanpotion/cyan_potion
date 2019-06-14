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

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.io.input.keyboard.CharEvent;
import org.joml.Vector3f;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext;

/**
 * <p>AudioManager class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class AudioManager implements AutoCloseable {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(CharEvent.class);
    /**
     * Constant <code>INITIAL_TEMP_SOURCES_NUM=128</code>
     */
    public static final int INITIAL_TEMP_SOURCES_NUM = 128;

    private final GameManager gameManager;

    private final Map<String, Source> specialSources = new ConcurrentHashMap<>();

    private final Set<Source> unusedSources = new HashSet<>();
    private final Set<Source> usedSources = new HashSet<>();


    private long openalDevice = -1;
    private long openalContext = -1;

    private Vector3f listenerPosition = null;
    private Vector3f listenerVelocity = null;

    /**
     * <p>Constructor for AudioManager.</p>
     *
     * @param gameManager a {@link com.xenoamess.cyan_potion.base.GameManager} object.
     */
    public AudioManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * <p>init.</p>
     */
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
        this.setOpenalContext(ALC11.alcCreateContext(this.getOpenalDevice(),
                (IntBuffer) null));
        alcSetThreadContext(this.getOpenalContext());
        AL.createCapabilities(deviceCaps);
        this.setListenerPosition(new Vector3f(0, 0, 0));
        this.setListenerVelocity(new Vector3f(0, 0, 0));

        for (int i = 0; i < INITIAL_TEMP_SOURCES_NUM; i++) {
            getUnusedSources().add(new Source());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * <p>gc.</p>
     */
    public void gc() {
        ArrayList<Source> deletedSource = new ArrayList<>();
        for (Source au : getUsedSources()) {
            if (au.isStopped()) {
                deletedSource.add(au);
            } else {
                au.getCurrentWaveData().setLastUsedFrameIndex(this.getGameManager().getNowFrameIndex());
            }
        }
        for (Source au : deletedSource) {
            getUnusedSources().add(au);
            getUsedSources().remove(au);
        }
    }

    /**
     * <p>getUnusedSource.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.audio.Source} object.
     */
    protected Source getUnusedSource() {
        if (getUnusedSources().isEmpty()) {
            this.gc();
        }
        if (getUnusedSources().isEmpty()) {
            getUnusedSources().add(new Source());
            LOGGER.debug("Audio source exhausted! Current num : {}",
                    this.getUsedSources().size());
        }
        return getUnusedSources().iterator().next();
    }

    /**
     * <p>playNew.</p>
     *
     * @param waveData a {@link com.xenoamess.cyan_potion.base.audio.WaveData} object.
     */
    public void playNew(WaveData waveData) {
        Source audioSource = this.getUnusedSource();
        getUnusedSources().remove(audioSource);
        getUsedSources().add(audioSource);
        audioSource.play(waveData);
    }

    /**
     * <p>resume.</p>
     *
     * @param waveData a {@link com.xenoamess.cyan_potion.base.audio.WaveData} object.
     */
    public void resume(WaveData waveData) {
        for (Source au : getUsedSources()) {
            if (au.getCurrentWaveData() == waveData && au.isPaused()) {
                au.play();
            }
        }
    }

    /**
     * <p>pause.</p>
     *
     * @param waveData a {@link com.xenoamess.cyan_potion.base.audio.WaveData} object.
     */
    public void pause(WaveData waveData) {
        for (Source au : getUsedSources()) {
            if (au.getCurrentWaveData() == waveData && au.isPlaying()) {
                au.pause();
            }
        }
    }

    /**
     * <p>stop.</p>
     *
     * @param waveData a {@link com.xenoamess.cyan_potion.base.audio.WaveData} object.
     */
    public void stop(WaveData waveData) {
        for (Source au : getUsedSources()) {
            if (au.getCurrentWaveData() == waveData && (au.isPlaying() || au.isPaused())) {
                au.stop();
            }
        }
    }

    /**
     * <p>Setter for the field <code>listenerPosition</code>.</p>
     *
     * @param listenerPosition a {@link org.joml.Vector3f} object.
     */
    public void setListenerPosition(Vector3f listenerPosition) {
        this.listenerPosition = new Vector3f(listenerPosition);
        AL10.alListener3f(AL10.AL_POSITION, this.listenerPosition.x,
                this.listenerPosition.y, this.listenerPosition.z);
    }

    /**
     * <p>Setter for the field <code>listenerVelocity</code>.</p>
     *
     * @param listenerVelocity a {@link org.joml.Vector3f} object.
     */
    public void setListenerVelocity(Vector3f listenerVelocity) {
        this.listenerVelocity = new Vector3f(listenerVelocity);
        AL10.alListener3f(AL10.AL_VELOCITY, this.listenerVelocity.x,
                this.listenerVelocity.y, this.listenerVelocity.z);
    }

    /**
     * <p>Getter for the field <code>gameManager</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.GameManager} object.
     */
    public GameManager getGameManager() {
        return gameManager;
    }

    /**
     * <p>Getter for the field <code>specialSources</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, Source> getSpecialSources() {
        return specialSources;
    }

    /**
     * <p>Getter for the field <code>unusedSources</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Source> getUnusedSources() {
        return unusedSources;
    }

    /**
     * <p>Getter for the field <code>usedSources</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Source> getUsedSources() {
        return usedSources;
    }

    /**
     * <p>Getter for the field <code>openalDevice</code>.</p>
     *
     * @return a long.
     */
    public long getOpenalDevice() {
        return openalDevice;
    }

    /**
     * <p>Setter for the field <code>openalDevice</code>.</p>
     *
     * @param openalDevice a long.
     */
    public void setOpenalDevice(long openalDevice) {
        this.openalDevice = openalDevice;
    }

    /**
     * <p>Getter for the field <code>openalContext</code>.</p>
     *
     * @return a long.
     */
    public long getOpenalContext() {
        return openalContext;
    }

    /**
     * <p>Setter for the field <code>openalContext</code>.</p>
     *
     * @param openalContext a long.
     */
    public void setOpenalContext(long openalContext) {
        this.openalContext = openalContext;
    }

    /**
     * <p>Getter for the field <code>listenerPosition</code>.</p>
     *
     * @return a {@link org.joml.Vector3f} object.
     */
    public Vector3f getListenerPosition() {
        return listenerPosition;
    }

    /**
     * <p>Getter for the field <code>listenerVelocity</code>.</p>
     *
     * @return a {@link org.joml.Vector3f} object.
     */
    public Vector3f getListenerVelocity() {
        return listenerVelocity;
    }
}
