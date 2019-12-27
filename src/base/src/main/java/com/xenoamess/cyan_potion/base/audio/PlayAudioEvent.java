package com.xenoamess.cyan_potion.base.audio;

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.events.MainThreadEvent;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author XenoAmess
 */
public class PlayAudioEvent implements MainThreadEvent {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MainThreadEvent.class);

    private AudioManager audioManager;

    private WaveData waveData;

    private float volume;
    private float pitch;
    private Vector3f position;
    private Vector3f velocity;
    private boolean relative;
    private float rollOffFactor;
    private boolean looping;
    private Event playOverEvent;

    /**
     * <p>Constructor for Source.</p>
     */
    public void init() {
        this.setWaveData(null);
        this.setVolume(1);
        this.setPitch(1);
        this.setPosition(new Vector3f(0, 0, 0));
        this.setVelocity(new Vector3f(0, 0, 0));
        this.setRelative(true);
        this.setRollOffFactor(0.0f);
        this.setLooping(false);
        this.setPlayOverEvent(null);
    }

    public PlayAudioEvent(AudioManager audioManager, WaveData waveData) {
        this.init();
        this.setAudioManager(audioManager);
        this.setWaveData(waveData);
    }

    public PlayAudioEvent(AudioManager audioManager, WaveData waveData, Event playOverEvent) {
        this(audioManager, waveData);
        this.setPlayOverEvent(playOverEvent);
    }

    /**
     * {@inheritDoc}
     *
     * @param gameManager
     */
    @Override
    public Set<Event> apply(GameManager gameManager) {
        if (gameManager.getDataCenter().isDebug()) {
            LOGGER.debug("{}", this);
        }
        Source source = this.getAudioManager().useSource();
        source.setCurrentWaveData(this.getWaveData());
        source.setVolume(this.getVolume());
        source.setPitch(this.getPitch());
        source.setPosition(this.getPosition());
        source.setVelocity(this.getVelocity());
        source.setRelative(this.isRelative());
        source.setRollOffFactor(this.getRollOffFactor());
        source.setLooping(this.isLooping());
        source.setPlayOverEvent(this.getPlayOverEvent());
        source.play();
        return null;
    }

    public WaveData getWaveData() {
        return waveData;
    }

    public void setWaveData(WaveData waveData) {
        this.waveData = waveData;
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
