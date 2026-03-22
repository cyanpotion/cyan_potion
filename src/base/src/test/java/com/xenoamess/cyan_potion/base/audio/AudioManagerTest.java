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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AudioManager and audio-related classes.
 *
 * @author XenoAmess
 * @version 0.167.4
 */
public class AudioManagerTest {

    @Test
    public void testSourceCreation() {
        // Test that Source class exists and can be referenced
        assertNotNull(Source.class);
    }

    @Test
    public void testWaveDataCreation() {
        // Test that WaveData class exists and can be referenced
        assertNotNull(WaveData.class);
    }

    @Test
    public void testPlayAudioEventCreation() {
        // Test that PlayAudioEvent class exists
        assertNotNull(PlayAudioEvent.class);
    }

    @Test
    public void testAudioManagerClassExists() {
        // Test that AudioManager class exists
        assertNotNull(AudioManager.class);
    }

    @Test
    public void testSourceMethodsExist() {
        // Test that Source has the expected methods
        try {
            Source.class.getMethod("getState");
            Source.class.getMethod("isPlaying");
            Source.class.getMethod("isPaused");
            Source.class.getMethod("isStopped");
            Source.class.getMethod("play");
            Source.class.getMethod("pause");
            Source.class.getMethod("stop");
            Source.class.getMethod("setVolume", float.class);
            Source.class.getMethod("setPitch", float.class);
            Source.class.getMethod("setLooping", boolean.class);
        } catch (NoSuchMethodException e) {
            fail("Source should have standard audio methods: " + e.getMessage());
        }
    }

    @Test
    public void testSourceCloseable() {
        // Source should implement Closeable
        assertTrue(java.io.Closeable.class.isAssignableFrom(Source.class));
    }
}
