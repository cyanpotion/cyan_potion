package com.xenoamess.cyan_potion.base.audio;

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.events.CharEvent;
import org.joml.Vector3f;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext;

/**
 * @author XenoAmess
 */
public class AudioManager implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharEvent.class);
    public static final int INITIAL_TEMP_SOURCES_NUM = 128;

    private final GameManager gameManager;

    private final Map<String, Source> specialSources = new HashMap<>();

    private final Set<Source> unusedSources = new HashSet<>();
    private final Set<Source> usedSources = new HashSet<>();


    private long openalDevice = -1;
    private long openalContext = -1;

    private Vector3f listenerPosition = null;
    private Vector3f listenerVelocity = null;

    public AudioManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void init() {
        this.close();

        ALC.create();
        this.setOpenalDevice(alcOpenDevice((ByteBuffer) null));
        if (this.getOpenalDevice() == MemoryUtil.NULL) {
            throw new IllegalStateException("Failed to open the default device.");
        }
        ALCCapabilities deviceCaps = ALC.createCapabilities(this.getOpenalDevice());
// Query for Effect Extension
//        if (!deviceCaps.ALC_EXT_EFX) {
//            alcCloseDevice(device);
//            throw new Exception("No EXTEfx supported by driver.");
//        }
//        System.out.println("EXTEfx found.");
        this.setOpenalContext(ALC11.alcCreateContext(this.getOpenalDevice(), (IntBuffer) null));
        alcSetThreadContext(this.getOpenalContext());
        AL.createCapabilities(deviceCaps);
        this.setListenerPosition(new Vector3f(0, 0, 0));
        this.setListenerVelocity(new Vector3f(0, 0, 0));

        for (int i = 0; i < INITIAL_TEMP_SOURCES_NUM; i++) {
            getUnusedSources().add(new Source());
        }
    }

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

    protected Source getUnusedSource() {
        if (getUnusedSources().isEmpty()) {
            this.gc();
        }
        if (getUnusedSources().isEmpty()) {
            getUnusedSources().add(new Source());
            LOGGER.debug("Audio source exhausted! Current num : {}", this.getUsedSources().size());
        }
        Source res = getUnusedSources().iterator().next();
        return res;
    }

    public void playNew(WaveData waveData) {
//        for (Source au : usedSources) {
//            if (au.currentWaveData == waveData && au.isPaused()) {
//                au.play();
////                return;
//            }
//        }
        Source audioSource = this.getUnusedSource();
        getUnusedSources().remove(audioSource);
        getUsedSources().add(audioSource);
        audioSource.play(waveData);
    }

    public void resume(WaveData waveData) {
        for (Source au : getUsedSources()) {
            if (au.getCurrentWaveData() == waveData && au.isPaused()) {
                au.play();
//                return;
            }
        }
    }

    public void pause(WaveData waveData) {
        for (Source au : getUsedSources()) {
            if (au.getCurrentWaveData() == waveData && au.isPlaying()) {
                au.pause();
//                return;
            }
        }
    }

    public void stop(WaveData waveData) {
        for (Source au : getUsedSources()) {
            if (au.getCurrentWaveData() == waveData) {
                if (au.isPlaying() || au.isPaused()) {
                    au.stop();
                }
            }
        }
    }

    public void setListenerPosition(Vector3f listenerPosition) {
        this.listenerPosition = new Vector3f(listenerPosition);
        AL10.alListener3f(AL10.AL_POSITION, this.listenerPosition.x, this.listenerPosition.y, this.listenerPosition.z);
    }

    public void setListenerVelocity(Vector3f listenerVelocity) {
        this.listenerVelocity = new Vector3f(listenerVelocity);
        AL10.alListener3f(AL10.AL_VELOCITY, this.listenerVelocity.x, this.listenerVelocity.y, this.listenerVelocity.z);
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public Map<String, Source> getSpecialSources() {
        return specialSources;
    }

    public Set<Source> getUnusedSources() {
        return unusedSources;
    }

    public Set<Source> getUsedSources() {
        return usedSources;
    }

    public long getOpenalDevice() {
        return openalDevice;
    }

    public void setOpenalDevice(long openalDevice) {
        this.openalDevice = openalDevice;
    }

    public long getOpenalContext() {
        return openalContext;
    }

    public void setOpenalContext(long openalContext) {
        this.openalContext = openalContext;
    }

    public Vector3f getListenerPosition() {
        return listenerPosition;
    }

    public Vector3f getListenerVelocity() {
        return listenerVelocity;
    }
}
